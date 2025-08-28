package com.wipro.librarymanagementsystem.service;

import com.wipro.librarymanagementsystem.entity.Book;
import com.wipro.librarymanagementsystem.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    // Get all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    // Get book by ID
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    // Get book by ISBN
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    // Save book
    public Book saveBook(Book book) {
        // Check if ISBN already exists
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuntimeException("Book with ISBN '" + book.getIsbn() + "' already exists");
        }
        return bookRepository.save(book);
    }
    
    // Update book
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        // Check if ISBN is being changed and if new ISBN exists
        if (!book.getIsbn().equals(bookDetails.getIsbn()) && 
            bookRepository.existsByIsbn(bookDetails.getIsbn())) {
            throw new RuntimeException("Book with ISBN '" + bookDetails.getIsbn() + "' already exists");
        }
        
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setGenre(bookDetails.getGenre());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setTotalCopies(bookDetails.getTotalCopies());
        book.setAvailableCopies(bookDetails.getAvailableCopies());
        
        return bookRepository.save(book);
    }
    
    // Delete book
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        bookRepository.delete(book);
    }
    
    // Search books
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchByTitleOrAuthor(keyword);
    }
    
    // Get books by author
    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }
    
    // Get books by genre
    public List<Book> getBooksByGenre(String genre) {
        return bookRepository.findByGenreIgnoreCase(genre);
    }
    
    // Get available books
    public List<Book> getAvailableBooks() {
        return bookRepository.findAllAvailableBooks();
    }
    
    // Get available books by genre
    public List<Book> getAvailableBooksByGenre(String genre) {
        return bookRepository.findAvailableBooksByGenre(genre);
    }
    
    // Get most borrowed books
    public List<Book> getMostBorrowedBooks() {
        return bookRepository.findMostBorrowedBooks();
    }
    
    // Check if book is available for borrowing
    public boolean isBookAvailable(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        return book.isAvailable();
    }
    
    // Decrease available copies (when book is borrowed)
    public Book decreaseAvailableCopies(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No available copies of this book");
        }
        
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return bookRepository.save(book);
    }
    
    // Increase available copies (when book is returned)
    public Book increaseAvailableCopies(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new RuntimeException("All copies are already available");
        }
        
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        return bookRepository.save(book);
    }
    
    // Get book statistics
    public long getTotalBooksCount() {
        return bookRepository.count();
    }
    
    public long getAvailableBooksCount() {
        return bookRepository.findAllAvailableBooks().size();
    }
}
