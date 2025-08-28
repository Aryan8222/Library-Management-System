package com.wipro.librarymanagementsystem.controller;

import com.wipro.librarymanagementsystem.entity.BorrowRecord;
import com.wipro.librarymanagementsystem.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin(origins = "*")
public class BorrowController {
    
    @Autowired
    private BorrowRecordService borrowRecordService;
    
    // Get all borrow records
    @GetMapping
    public ResponseEntity<List<BorrowRecord>> getAllBorrowRecords() {
        try {
            List<BorrowRecord> records = borrowRecordService.getAllBorrowRecordsWithUserAndBook();
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get borrow record by ID
    @GetMapping("/{id}")
    public ResponseEntity<BorrowRecord> getBorrowRecordById(@PathVariable Long id) {
        try {
            Optional<BorrowRecord> record = borrowRecordService.getBorrowRecordById(id);
            return record.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Borrow a book
    @PostMapping("/borrow")
    public ResponseEntity<BorrowRecord> borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        try {
            BorrowRecord borrowRecord = borrowRecordService.borrowBook(userId, bookId);
            return ResponseEntity.status(HttpStatus.CREATED).body(borrowRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Return a book
    @PutMapping("/return/{borrowRecordId}")
    public ResponseEntity<BorrowRecord> returnBook(@PathVariable Long borrowRecordId) {
        try {
            BorrowRecord borrowRecord = borrowRecordService.returnBook(borrowRecordId);
            return ResponseEntity.ok(borrowRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get borrow history by user
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<BorrowRecord>> getBorrowHistoryByUser(@PathVariable Long userId) {
        try {
            List<BorrowRecord> records = borrowRecordService.getBorrowHistoryByUser(userId);
            return ResponseEntity.ok(records);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get currently borrowed books by user
    @GetMapping("/user/{userId}/current")
    public ResponseEntity<List<BorrowRecord>> getCurrentlyBorrowedBooksByUser(@PathVariable Long userId) {
        try {
            List<BorrowRecord> records = borrowRecordService.getCurrentlyBorrowedBooksByUser(userId);
            return ResponseEntity.ok(records);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get borrow history by book
    @GetMapping("/book/{bookId}/history")
    public ResponseEntity<List<BorrowRecord>> getBorrowHistoryByBook(@PathVariable Long bookId) {
        try {
            List<BorrowRecord> records = borrowRecordService.getBorrowHistoryByBook(bookId);
            return ResponseEntity.ok(records);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get overdue records
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowRecord>> getOverdueRecords() {
        try {
            List<BorrowRecord> records = borrowRecordService.getOverdueRecords();
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get books due today
    @GetMapping("/due-today")
    public ResponseEntity<List<BorrowRecord>> getBooksDueToday() {
        try {
            List<BorrowRecord> records = borrowRecordService.getBooksDueToday();
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Extend due date
    @PutMapping("/{borrowRecordId}/extend")
    public ResponseEntity<BorrowRecord> extendDueDate(@PathVariable Long borrowRecordId, @RequestParam int additionalDays) {
        try {
            if (additionalDays <= 0 || additionalDays > 30) {
                return ResponseEntity.badRequest().build();
            }
            
            BorrowRecord borrowRecord = borrowRecordService.extendDueDate(borrowRecordId, additionalDays);
            return ResponseEntity.ok(borrowRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Update overdue status for all records
    @PostMapping("/update-overdue-status")
    public ResponseEntity<String> updateOverdueStatus() {
        try {
            borrowRecordService.updateOverdueStatus();
            return ResponseEntity.ok("Overdue status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to update overdue status");
        }
    }
    
    // Get borrowing statistics
    @GetMapping("/stats")
    public ResponseEntity<BorrowStats> getBorrowingStats() {
        try {
            long currentlyBorrowed = borrowRecordService.getCurrentlyBorrowedBooksCount();
            long totalRecords = borrowRecordService.getTotalBorrowRecordsCount();
            List<BorrowRecord> overdueRecords = borrowRecordService.getOverdueRecords();
            long overdueCount = overdueRecords.size();
            
            BorrowStats stats = new BorrowStats(totalRecords, currentlyBorrowed, overdueCount);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Inner class for borrowing statistics
    public static class BorrowStats {
        private long totalRecords;
        private long currentlyBorrowed;
        private long overdueBooks;
        
        public BorrowStats(long totalRecords, long currentlyBorrowed, long overdueBooks) {
            this.totalRecords = totalRecords;
            this.currentlyBorrowed = currentlyBorrowed;
            this.overdueBooks = overdueBooks;
        }
        
        // Getters
        public long getTotalRecords() { return totalRecords; }
        public long getCurrentlyBorrowed() { return currentlyBorrowed; }
        public long getOverdueBooks() { return overdueBooks; }
        public long getReturnedBooks() { return totalRecords - currentlyBorrowed; }
    }
}