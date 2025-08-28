package com.wipro.librarymanagementsystem.repository;

import com.wipro.librarymanagementsystem.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Find book by ISBN
    Optional<Book> findByIsbn(String isbn);
    
    // Find books by author
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    // Find books by title
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    // Find books by genre
    List<Book> findByGenreIgnoreCase(String genre);
    
    // Find available books (available copies > 0)
    List<Book> findByAvailableCopiesGreaterThan(Integer availableCopies);
    
    // Find books by publication year
    List<Book> findByPublicationYear(Integer publicationYear);
    
    // Search books by title or author
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchByTitleOrAuthor(@Param("keyword") String keyword);
    
    // Check if ISBN already exists
    boolean existsByIsbn(String isbn);
    
    // Get all available books
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    List<Book> findAllAvailableBooks();
    
    // Get books by genre and available
    @Query("SELECT b FROM Book b WHERE LOWER(b.genre) = LOWER(:genre) AND b.availableCopies > 0")
    List<Book> findAvailableBooksByGenre(@Param("genre") String genre);
    
    // Get most borrowed books
    @Query("SELECT b FROM Book b ORDER BY (b.totalCopies - b.availableCopies) DESC")
    List<Book> findMostBorrowedBooks();
}