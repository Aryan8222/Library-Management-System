package com.wipro.librarymanagementsystem.repository;

import com.wipro.librarymanagementsystem.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    // Find admin by username
    Optional<Admin> findByUsername(String username);
    
    // Find admin by email
    Optional<Admin> findByEmail(String email);
    
    // Find active admins
    List<Admin> findByIsActiveTrue();
    
    // Find admins by role
    List<Admin> findByRole(Admin.AdminRole role);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find admin by username and active status
    Optional<Admin> findByUsernameAndIsActiveTrue(String username);
}