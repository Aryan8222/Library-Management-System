package com.wipro.librarymanagementsystem.repository;

import com.wipro.librarymanagementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username
    Optional<User> findByUsername(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Find users by membership type
    List<User> findByMembershipType(User.MembershipType membershipType);
    
    // Find active users
    List<User> findByIsActiveTrue();
    
    // Find inactive users
    List<User> findByIsActiveFalse();
    
    // Search users by name (first name or last name)
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> searchByName(@Param("name") String name);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find users with overdue books
    @Query("SELECT DISTINCT u FROM User u JOIN u.borrowRecords br WHERE br.status = 'BORROWED' AND br.dueDate < CURRENT_TIMESTAMP")
    List<User> findUsersWithOverdueBooks();
    
    // Count active users
    long countByIsActiveTrue();
}