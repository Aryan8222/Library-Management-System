package com.wipro.librarymanagementsystem.service;

import com.wipro.librarymanagementsystem.entity.User;
import com.wipro.librarymanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // Get user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // Save user
    public User saveUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username '" + user.getUsername() + "' already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email '" + user.getEmail() + "' already exists");
        }
        
        return userRepository.save(user);
    }
    
    // Update user
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Check if username is being changed and if new username exists
        if (!user.getUsername().equals(userDetails.getUsername()) && 
            userRepository.existsByUsername(userDetails.getUsername())) {
            throw new RuntimeException("Username '" + userDetails.getUsername() + "' already exists");
        }
        
        // Check if email is being changed and if new email exists
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Email '" + userDetails.getEmail() + "' already exists");
        }
        
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        user.setMembershipType(userDetails.getMembershipType());
        user.setIsActive(userDetails.getIsActive());
        
        return userRepository.save(user);
    }
    
    // Delete user
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }
    
    // Deactivate user (soft delete)
    public User deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setIsActive(false);
        return userRepository.save(user);
    }
    
    // Activate user
    public User activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setIsActive(true);
        return userRepository.save(user);
    }
    
    // Search users by name
    public List<User> searchUsersByName(String name) {
        return userRepository.searchByName(name);
    }
    
    // Get active users
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    
    // Get users by membership type
    public List<User> getUsersByMembershipType(User.MembershipType membershipType) {
        return userRepository.findByMembershipType(membershipType);
    }
    
    // Get users with overdue books
    public List<User> getUsersWithOverdueBooks() {
        return userRepository.findUsersWithOverdueBooks();
    }
    
    // Get user statistics
    public long getActiveUsersCount() {
        return userRepository.countByIsActiveTrue();
    }
    
    public long getTotalUsersCount() {
        return userRepository.count();
    }
}