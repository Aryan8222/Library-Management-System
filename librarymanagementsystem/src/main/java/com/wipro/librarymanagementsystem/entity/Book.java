package com.wipro.librarymanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "books")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Column(nullable = false, length = 255)
    private String title;
    
    @NotBlank(message = "Author is required")
    @Column(nullable = false, length = 255)
    private String author;
    
    @NotBlank(message = "ISBN is required")
    @Column(unique = true, nullable = false, length = 20)
    private String isbn;
    
    @Column(length = 100)
    private String genre;
    
    @Column(name = "publication_year")
    private Integer publicationYear;
    
    @Column(name = "total_copies")
    private Integer totalCopies = 1;
    
    @Column(name = "available_copies")
    private Integer availableCopies = 1;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
   
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<BorrowRecord> borrowRecords;
    
   
    public Book() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Book(String title, String author, String isbn) {
        this();
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }
    
   
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { 
        this.author = author;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { 
        this.isbn = isbn;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { 
        this.genre = genre;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { 
        this.publicationYear = publicationYear;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { 
        this.totalCopies = totalCopies;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(Integer availableCopies) { 
        this.availableCopies = availableCopies;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isAvailable() {
        return availableCopies > 0;
    }
    
    public Integer getBorrowedCopies() {
        return totalCopies - availableCopies;
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", genre='" + genre + '\'' +
                ", publicationYear=" + publicationYear +
                ", totalCopies=" + totalCopies +
                ", availableCopies=" + availableCopies +
                '}';
    }
}