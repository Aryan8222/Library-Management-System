package com.wipro.librarymanagementsystem.controller;

import com.wipro.librarymanagementsystem.repository.BookRepository;
import com.wipro.librarymanagementsystem.repository.UserRepository;
import com.wipro.librarymanagementsystem.repository.BorrowRecordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public DashboardController(BookRepository bookRepository,
                               UserRepository userRepository,
                               BorrowRecordRepository borrowRecordRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @GetMapping
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", bookRepository.count());
        stats.put("availableBooks", bookRepository.findAllAvailableBooks().size());
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countByIsActiveTrue());
        stats.put("currentlyBorrowed", borrowRecordRepository.findByStatus(
                com.wipro.librarymanagementsystem.entity.BorrowRecord.BorrowStatus.BORROWED
        ).size());
        stats.put("overdueBooks", borrowRecordRepository.findOverdueRecords().size());
        return stats;
    }
}