package com.wipro.librarymanagementsystem.config;

import com.wipro.librarymanagementsystem.entity.Book;
import com.wipro.librarymanagementsystem.entity.User;
import com.wipro.librarymanagementsystem.repository.BookRepository;
import com.wipro.librarymanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2) // Run after admin initialization
public class DataSeeder implements CommandLineRunner {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        seedBooks();
        seedUsers();
    }
    
    private void seedBooks() {
        if (bookRepository.count() == 0) {
            // Fiction Books
            Book book1 = new Book("To Kill a Mockingbird", "Harper Lee", "9780061120084");
            book1.setGenre("Fiction");
            book1.setPublicationYear(1960);
            book1.setTotalCopies(3);
            book1.setAvailableCopies(3);
            bookRepository.save(book1);
            
            Book book2 = new Book("1984", "George Orwell", "9780451524935");
            book2.setGenre("Dystopian Fiction");
            book2.setPublicationYear(1949);
            book2.setTotalCopies(5);
            book2.setAvailableCopies(4);
            bookRepository.save(book2);
            
            Book book3 = new Book("Pride and Prejudice", "Jane Austen", "9780141439518");
            book3.setGenre("Romance");
            book3.setPublicationYear(1813);
            book3.setTotalCopies(2);
            book3.setAvailableCopies(2);
            bookRepository.save(book3);
            
            Book book4 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565");
            book4.setGenre("Classic Literature");
            book4.setPublicationYear(1925);
            book4.setTotalCopies(4);
            book4.setAvailableCopies(3);
            bookRepository.save(book4);
            
            // Science Fiction
            Book book5 = new Book("Dune", "Frank Herbert", "9780441172719");
            book5.setGenre("Science Fiction");
            book5.setPublicationYear(1965);
            book5.setTotalCopies(3);
            book5.setAvailableCopies(2);
            bookRepository.save(book5);
            
            Book book6 = new Book("The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "9780345391803");
            book6.setGenre("Science Fiction");
            book6.setPublicationYear(1979);
            book6.setTotalCopies(2);
            book6.setAvailableCopies(2);
            bookRepository.save(book6);
            
            // Non-Fiction
            Book book7 = new Book("Sapiens", "Yuval Noah Harari", "9780062316097");
            book7.setGenre("History");
            book7.setPublicationYear(2014);
            book7.setTotalCopies(3);
            book7.setAvailableCopies(1);
            bookRepository.save(book7);
            
            Book book8 = new Book("The Art of War", "Sun Tzu", "9781599869773");
            book8.setGenre("Philosophy");
            book8.setPublicationYear(-500); // 5th century BC
            book8.setTotalCopies(2);
            book8.setAvailableCopies(2);
            bookRepository.save(book8);
            
            // Technical Books
            Book book9 = new Book("Clean Code", "Robert C. Martin", "9780132350884");
            book9.setGenre("Technology");
            book9.setPublicationYear(2008);
            book9.setTotalCopies(4);
            book9.setAvailableCopies(3);
            bookRepository.save(book9);
            
            Book book10 = new Book("Design Patterns", "Gang of Four", "9780201633610");
            book10.setGenre("Technology");
            book10.setPublicationYear(1994);
            book10.setTotalCopies(2);
            book10.setAvailableCopies(1);
            bookRepository.save(book10);
            
            // Mystery/Thriller
            Book book11 = new Book("The Girl with the Dragon Tattoo", "Stieg Larsson", "9780307454546");
            book11.setGenre("Mystery");
            book11.setPublicationYear(2005);
            book11.setTotalCopies(3);
            book11.setAvailableCopies(3);
            bookRepository.save(book11);
            
            Book book12 = new Book("Gone Girl", "Gillian Flynn", "9780307588371");
            book12.setGenre("Thriller");
            book12.setPublicationYear(2012);
            book12.setTotalCopies(2);
            book12.setAvailableCopies(1);
            bookRepository.save(book12);
            
            System.out.println("Sample books have been seeded into the database.");
        }
    }
    
    private void seedUsers() {
        if (userRepository.count() == 0) {
            // Regular Users
            User user1 = new User("john_doe", "john.doe@email.com", "John", "Doe");
            user1.setPhone("555-0101");
            user1.setAddress("123 Main St, City, State");
            user1.setMembershipType(User.MembershipType.REGULAR);
            userRepository.save(user1);
            
            User user2 = new User("jane_smith", "jane.smith@email.com", "Jane", "Smith");
            user2.setPhone("555-0102");
            user2.setAddress("456 Oak Ave, City, State");
            user2.setMembershipType(User.MembershipType.PREMIUM);
            userRepository.save(user2);
            
            // Student Users
            User user3 = new User("alice_johnson", "alice.johnson@student.edu", "Alice", "Johnson");
            user3.setPhone("555-0103");
            user3.setAddress("789 College Blvd, City, State");
            user3.setMembershipType(User.MembershipType.STUDENT);
            userRepository.save(user3);
            
            User user4 = new User("bob_wilson", "bob.wilson@student.edu", "Bob", "Wilson");
            user4.setPhone("555-0104");
            user4.setAddress("321 University Dr, City, State");
            user4.setMembershipType(User.MembershipType.STUDENT);
            userRepository.save(user4);
            
            // Premium Users
            User user5 = new User("carol_brown", "carol.brown@email.com", "Carol", "Brown");
            user5.setPhone("555-0105");
            user5.setAddress("654 Pine St, City, State");
            user5.setMembershipType(User.MembershipType.PREMIUM);
            userRepository.save(user5);
            
            User user6 = new User("david_davis", "david.davis@email.com", "David", "Davis");
            user6.setPhone("555-0106");
            user6.setAddress("987 Elm St, City, State");
            user6.setMembershipType(User.MembershipType.REGULAR);
            userRepository.save(user6);
            
            // More users for testing
            User user7 = new User("emma_wilson", "emma.wilson@email.com", "Emma", "Wilson");
            user7.setPhone("555-0107");
            user7.setAddress("147 Maple Ave, City, State");
            user7.setMembershipType(User.MembershipType.REGULAR);
            userRepository.save(user7);
            
            User user8 = new User("frank_miller", "frank.miller@email.com", "Frank", "Miller");
            user8.setPhone("555-0108");
            user8.setAddress("258 Cedar St, City, State");
            user8.setMembershipType(User.MembershipType.PREMIUM);
            userRepository.save(user8);
            
            // Inactive user for testing
            User user9 = new User("inactive_user", "inactive@email.com", "Inactive", "User");
            user9.setPhone("555-0109");
            user9.setAddress("369 Birch Ln, City, State");
            user9.setMembershipType(User.MembershipType.REGULAR);
            user9.setIsActive(false);
            userRepository.save(user9);
            
            System.out.println("Sample users have been seeded into the database.");
        }
    }
}