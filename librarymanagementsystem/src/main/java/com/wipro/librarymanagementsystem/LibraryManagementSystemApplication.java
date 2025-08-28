package com.wipro.librarymanagementsystem;

import com.wipro.librarymanagementsystem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;

@SpringBootApplication
public class LibraryManagementSystemApplication implements CommandLineRunner {

    @Autowired
    private AdminService adminService;

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementSystemApplication.class, args);
    }

    @Override
    @Order(1) // Run first, before data seeding
    public void run(String... args) throws Exception {
        // Initialize default admin on application startup
        adminService.initializeDefaultAdmin();
    }
}