package com.wipro.librarymanagementsystem.repository;

import com.wipro.librarymanagementsystem.entity.BorrowRecord;
import com.wipro.librarymanagementsystem.entity.User;
import com.wipro.librarymanagementsystem.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    
    // Find borrow records by user
    List<BorrowRecord> findByUser(User user);
    
    // Find borrow records by book
    List<BorrowRecord> findByBook(Book book);
    
    
    // Find borrow records by status
    List<BorrowRecord> findByStatus(BorrowRecord.BorrowStatus status);
    
    // Find currently borrowed books by user
    List<BorrowRecord> findByUserAndStatus(User user, BorrowRecord.BorrowStatus status);
    
    //to fetch records with user and book data
    @Query("SELECT br FROM BorrowRecord br JOIN FETCH br.user JOIN FETCH br.book")
    List<BorrowRecord> findAllWithUserAndBook();
    
    // Find overdue books
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND br.dueDate < CURRENT_TIMESTAMP")
    List<BorrowRecord> findOverdueRecords();
    
    // Find books due today
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND DATE(br.dueDate) = CURRENT_DATE")
    List<BorrowRecord> findBooksDueToday();
    
    // Find active borrow record for specific user and book
    Optional<BorrowRecord> findByUserAndBookAndStatus(User user, Book book, BorrowRecord.BorrowStatus status);
    
    // Count borrowed books by user
    long countByUserAndStatus(User user, BorrowRecord.BorrowStatus status);
    
    // Find borrow history for a user (all statuses)
    @Query("SELECT br FROM BorrowRecord br WHERE br.user = :user ORDER BY br.borrowDate DESC")
    List<BorrowRecord> findBorrowHistoryByUser(@Param("user") User user);
    
    // Find borrow history for a book
    @Query("SELECT br FROM BorrowRecord br WHERE br.book = :book ORDER BY br.borrowDate DESC")
    List<BorrowRecord> findBorrowHistoryByBook(@Param("book") Book book);
    
    // Find records between dates
    List<BorrowRecord> findByBorrowDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Get borrowing statistics
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.status = 'BORROWED'")
    long countCurrentlyBorrowedBooks();
}