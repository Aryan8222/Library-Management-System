package com.wipro.librarymanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



@Entity
@Table(name = "borrow_records")
public class BorrowRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"borrowRecords"})
     
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    @JsonIgnoreProperties({"borrowRecords"})
    private Book book;
    
    @Column(name = "borrow_date")
    private LocalDateTime borrowDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;
    
    @Column(name = "return_date")
    private LocalDateTime returnDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus status = BorrowStatus.BORROWED;
    
    @Column(name = "fine_amount", precision = 10, scale = 2)
    private BigDecimal fineAmount = BigDecimal.ZERO;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enum for borrow status
    public enum BorrowStatus {
        BORROWED, RETURNED, OVERDUE
    }
    
    // Constructors
    public BorrowRecord() {
        this.borrowDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Default due date: 14 days from borrow date
        this.dueDate = this.borrowDate.plusDays(14);
    }
    
    public BorrowRecord(User user, Book book) {
        this();
        this.user = user;
        this.book = book;
    }
    
    public BorrowRecord(User user, Book book, LocalDateTime dueDate) {
        this(user, book);
        this.dueDate = dueDate;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { 
        this.user = user;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Book getBook() { return book; }
    public void setBook(Book book) { 
        this.book = book;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDateTime borrowDate) { 
        this.borrowDate = borrowDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { 
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { 
        this.returnDate = returnDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public BorrowStatus getStatus() { return status; }
    public void setStatus(BorrowStatus status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal getFineAmount() { return fineAmount; }
    public void setFineAmount(BigDecimal fineAmount) { 
        this.fineAmount = fineAmount;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { 
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Helper method to check if book is overdue
    public boolean isOverdue() {
        return status == BorrowStatus.BORROWED && LocalDateTime.now().isAfter(dueDate);
    }
    
    // Helper method to calculate days overdue
    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDateTime.now());
    }
    
    @Override
    public String toString() {
        return "BorrowRecord{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", book=" + (book != null ? book.getTitle() : "null") +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status=" + status +
                ", fineAmount=" + fineAmount +
                '}';
    }
}