package com.wipro.librarymanagementsystem.controller;

import com.wipro.librarymanagementsystem.service.BookService;
import com.wipro.librarymanagementsystem.service.UserService;
import com.wipro.librarymanagementsystem.service.BorrowRecordService;
import com.wipro.librarymanagementsystem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BorrowRecordService borrowRecordService;
    
    @Autowired
    private AdminService adminService;
    
    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Library Management System is running");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    // Database connectivity test
    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> databaseTest() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long totalBooks = bookService.getTotalBooksCount();
            long totalUsers = userService.getTotalUsersCount();
            long totalBorrowRecords = borrowRecordService.getTotalBorrowRecordsCount();
            long totalAdmins = adminService.getAllAdmins().size();
            
            response.put("status", "Connected");
            response.put("totalBooks", totalBooks);
            response.put("totalUsers", totalUsers);
            response.put("totalBorrowRecords", totalBorrowRecords);
            response.put("totalAdmins", totalAdmins);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "Error");
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // Quick stats endpoint
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            stats.put("books", Map.of(
                "total", bookService.getTotalBooksCount(),
                "available", bookService.getAvailableBooksCount()
            ));
            
            stats.put("users", Map.of(
                "total", userService.getTotalUsersCount(),
                "active", userService.getActiveUsersCount()
            ));
            
            stats.put("borrowing", Map.of(
                "totalRecords", borrowRecordService.getTotalBorrowRecordsCount(),
                "currentlyBorrowed", borrowRecordService.getCurrentlyBorrowedBooksCount(),
                "overdue", borrowRecordService.getOverdueRecords().size()
            ));
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            stats.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(stats);
        }
    }
}