package com.wipro.librarymanagementsystem.service;

import com.wipro.librarymanagementsystem.entity.BorrowRecord;
import com.wipro.librarymanagementsystem.entity.User;
import com.wipro.librarymanagementsystem.entity.Book;
import com.wipro.librarymanagementsystem.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowRecordService {
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserService userService;
    
    // Get all borrow records
    public List<BorrowRecord> getAllBorrowRecordsWithUserAndBook() {
        return borrowRecordRepository.findAllWithUserAndBook();
        
    }
    
    // Get borrow record by ID
    public Optional<BorrowRecord> getBorrowRecordById(Long id) {
        return borrowRecordRepository.findById(id);
    }
    
    // Borrow a book
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        // Check if user is active
        if (!user.getIsActive()) {
            throw new RuntimeException("User account is inactive");
        }
        
        // Check if book is available
        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available for borrowing");
        }
        
        // Check if user already has this book
        Optional<BorrowRecord> existingRecord = borrowRecordRepository
                .findByUserAndBookAndStatus(user, book, BorrowRecord.BorrowStatus.BORROWED);
        if (existingRecord.isPresent()) {
            throw new RuntimeException("User already has this book borrowed");
        }
        
        // Create borrow record
        BorrowRecord borrowRecord = new BorrowRecord(user, book);
        
        // Set due date based on membership type (different loan periods)
        int loanDays = switch (user.getMembershipType()) {
            case PREMIUM -> 21; // 3 weeks
            case STUDENT -> 30; // 1 month
            default -> 14;      // 2 weeks for regular
        };
        borrowRecord.setDueDate(LocalDateTime.now().plusDays(loanDays));
        
        // Update book availability
        bookService.decreaseAvailableCopies(bookId);
        
        return borrowRecordRepository.save(borrowRecord);
    }
    
    // Return a book
    public BorrowRecord returnBook(Long borrowRecordId) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found with id: " + borrowRecordId));
        
        if (borrowRecord.getStatus() != BorrowRecord.BorrowStatus.BORROWED) {
            throw new RuntimeException("Book is already returned");
        }
        
        // Set return date and status
        borrowRecord.setReturnDate(LocalDateTime.now());
        borrowRecord.setStatus(BorrowRecord.BorrowStatus.RETURNED);
        
        // Calculate fine if overdue
        if (borrowRecord.isOverdue()) {
            long daysOverdue = borrowRecord.getDaysOverdue();
            BigDecimal finePerDay = new BigDecimal("1.00"); // $1 per day
            BigDecimal totalFine = finePerDay.multiply(BigDecimal.valueOf(daysOverdue));
            borrowRecord.setFineAmount(totalFine);
        }
        
        // Update book availability
        bookService.increaseAvailableCopies(borrowRecord.getBook().getId());
        
        return borrowRecordRepository.save(borrowRecord);
    }
    
    // Get borrow history by user
    public List<BorrowRecord> getBorrowHistoryByUser(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return borrowRecordRepository.findBorrowHistoryByUser(user);
    }
    
    // Get currently borrowed books by user
    public List<BorrowRecord> getCurrentlyBorrowedBooksByUser(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return borrowRecordRepository.findByUserAndStatus(user, BorrowRecord.BorrowStatus.BORROWED);
    }
    
    // Get borrow history by book
    public List<BorrowRecord> getBorrowHistoryByBook(Long bookId) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        return borrowRecordRepository.findBorrowHistoryByBook(book);
    }
    
    // Get overdue records
    public List<BorrowRecord> getOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords();
    }
    
    // Get books due today
    public List<BorrowRecord> getBooksDueToday() {
        return borrowRecordRepository.findBooksDueToday();
    }
    
    // Update overdue status for all borrowed books
    public void updateOverdueStatus() {
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords();
        for (BorrowRecord record : overdueRecords) {
            if (record.getStatus() == BorrowRecord.BorrowStatus.BORROWED) {
                record.setStatus(BorrowRecord.BorrowStatus.OVERDUE);
                borrowRecordRepository.save(record);
            }
        }
    }
    
    // Get borrowing statistics
    public long getCurrentlyBorrowedBooksCount() {
        return borrowRecordRepository.countCurrentlyBorrowedBooks();
    }
    
    public long getTotalBorrowRecordsCount() {
        return borrowRecordRepository.count();
    }
    
    // Extend due date
    public BorrowRecord extendDueDate(Long borrowRecordId, int additionalDays) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found with id: " + borrowRecordId));
        
        if (borrowRecord.getStatus() != BorrowRecord.BorrowStatus.BORROWED) {
            throw new RuntimeException("Can only extend due date for borrowed books");
        }
        
        borrowRecord.setDueDate(borrowRecord.getDueDate().plusDays(additionalDays));
        return borrowRecordRepository.save(borrowRecord);
    }
}