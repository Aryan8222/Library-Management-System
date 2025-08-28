import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BorrowService } from '../../services/borrow.service';
import { UserService } from '../../services/user.service';
import { BookService } from '../../services/book.service';
import { BorrowRecord, User, Book } from '../../models';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-borrow-records',
  templateUrl: './borrow-records.component.html',
  styleUrls: ['./borrow-records.component.css']
})
export class BorrowRecordsComponent implements OnInit {
  displayedColumns: string[] = ['user', 'book', 'borrowDate', 'dueDate', 'returnDate', 'status', 'fineAmount', 'actions'];
  dataSource = new MatTableDataSource<BorrowRecord>();
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  borrowRecords: BorrowRecord[] = [];
  users: User[] = [];
  availableBooks: Book[] = [];
  
  isLoading = true;
  showBorrowForm = false;
  borrowForm: FormGroup;

  selectedStatus = '';
  selectedUser = '';
  
  statusOptions = [
    { value: 'BORROWED', label: 'Currently Borrowed' },
    { value: 'RETURNED', label: 'Returned' },
    { value: 'OVERDUE', label: 'Overdue' }
  ];
 

  constructor(
    private borrowService: BorrowService,
    private userService: UserService,
    private bookService: BookService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder
  ) {
    this.borrowForm = this.createBorrowForm();
  }

  ngOnInit(): void {
    console.log('BorrowRecordsComponent initialized');
    this.loadData();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  private createBorrowForm(): FormGroup {
    return this.formBuilder.group({
      userId: ['', Validators.required],
      bookId: ['', Validators.required]
    });
  }

  loadData(): void {
    this.isLoading = true;
    console.log('Loading borrow records data...');
    
    forkJoin({
      borrowRecords: this.borrowService.getAllBorrowRecords(),
      users: this.userService.getActiveUsers(),
      availableBooks: this.bookService.getAvailableBooks()
    }).subscribe({
      next: (data) => {
        console.log('Data loaded successfully:', data);
        
        // Safely assign data with fallbacks
        this.borrowRecords = data.borrowRecords || [];
        this.users = data.users || [];
        this.availableBooks = data.availableBooks || [];
        
        // Update data source
        this.dataSource.data = this.borrowRecords;
        this.isLoading = false;

        console.log(`Loaded ${this.borrowRecords.length} borrow records`);
        console.log(`Loaded ${this.users.length} users`);
        console.log(`Loaded ${this.availableBooks.length} available books`);
      },
      error: (error) => {
        console.error('Error loading borrow records data:', error);
        this.snackBar.open('Failed to load borrow records data', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        this.isLoading = false;
        
        // Initialize empty arrays to prevent template errors
        this.borrowRecords = [];
        this.users = [];
        this.availableBooks = [];
        this.dataSource.data = [];
      }
    });
  }

  onStatusFilter(): void {
    this.filterRecords();
  }

  onUserFilter(): void {
    this.filterRecords();
  }

  private filterRecords(): void {
    let filteredRecords = [...this.borrowRecords];

    if (this.selectedStatus) {
      filteredRecords = filteredRecords.filter(record => record.status === this.selectedStatus);
    }

    if (this.selectedUser) {
      filteredRecords = filteredRecords.filter(record => 
        record.user && record.user.id?.toString() === this.selectedUser
      );
    }

    this.dataSource.data = filteredRecords;
  }

  clearFilters(): void {
    this.selectedStatus = '';
    this.selectedUser = '';
    this.dataSource.data = this.borrowRecords;
  }

  getStatusChip(status: string): { text: string; color: string; icon: string } {
    switch (status) {
      case 'BORROWED':
        return { text: 'Borrowed', color: 'primary', icon: 'book' };
      case 'RETURNED':
        return { text: 'Returned', color: 'accent', icon: 'check_circle' };
      case 'OVERDUE':
        return { text: 'Overdue', color: 'warn', icon: 'warning' };
      default:
        return { text: status || 'Unknown', color: '', icon: 'help' };
    }
  }

  toggleBorrowForm(): void {
    this.showBorrowForm = !this.showBorrowForm;
    if (this.showBorrowForm) {
      this.borrowForm.reset();
    }
  }

  onBorrowBook(): void {
    if (this.borrowForm.valid) {
      const { userId, bookId } = this.borrowForm.value;
      
      console.log('Borrowing book:', { userId, bookId });
      
      this.borrowService.borrowBook(userId, bookId).subscribe({
        next: (borrowRecord) => {
          console.log('Book borrowed successfully:', borrowRecord);
          this.snackBar.open('Book borrowed successfully!', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.loadData(); // Reload data to show new record
          this.showBorrowForm = false;
        },
        error: (error) => {
          console.error('Error borrowing book:', error);
          let errorMessage = 'Failed to borrow book.';
          
          if (error.status === 400) {
            errorMessage += ' Check if user or book is available.';
          } else if (error.status === 404) {
            errorMessage += ' User or book not found.';
          }
          
          this.snackBar.open(errorMessage, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    } else {
      this.snackBar.open('Please select both user and book', 'Close', {
        duration: 3000,
        panelClass: ['warning-snackbar']
      });
    }
  }

  returnBook(record: BorrowRecord): void {
    if (!record || !record.id) {
      this.snackBar.open('Invalid borrow record', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    const bookTitle = record.book?.title || 'this book';
    const userName = record.user ? `${record.user.firstName} ${record.user.lastName}` : 'the user';
    
    if (confirm(`Return "${bookTitle}" borrowed by ${userName}?`)) {
      this.borrowService.returnBook(record.id).subscribe({
        next: (returnedRecord) => {
          console.log('Book returned successfully:', returnedRecord);
          this.snackBar.open('Book returned successfully!', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          
          if (returnedRecord.fineAmount && returnedRecord.fineAmount > 0) {
            this.snackBar.open(`Fine amount: ${returnedRecord.fineAmount}`, 'Close', {
              duration: 5000,
              panelClass: ['warning-snackbar']
            });
          }
          
          this.loadData(); // Reload data to show updated record
        },
        error: (error) => {
          console.error('Error returning book:', error);
          this.snackBar.open('Failed to return book', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }

  extendDueDate(record: BorrowRecord): void {
    if (!record || !record.id) {
      this.snackBar.open('Invalid borrow record', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    const additionalDays = prompt('How many additional days would you like to extend?', '7');
    
    if (additionalDays && !isNaN(Number(additionalDays))) {
      const days = parseInt(additionalDays);
      if (days > 0 && days <= 30) {
        this.borrowService.extendDueDate(record.id, days).subscribe({
          next: () => {
            console.log(`Due date extended by ${days} days`);
            this.snackBar.open(`Due date extended by ${days} days`, 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
            this.loadData(); // Reload data to show updated due date
          },
          error: (error) => {
            console.error('Error extending due date:', error);
            this.snackBar.open('Failed to extend due date', 'Close', {
              duration: 5000,
              panelClass: ['error-snackbar']
            });
          }
        });
      } else {
        this.snackBar.open('Please enter a valid number of days (1-30)', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    }
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    } catch (error) {
      console.error('Error formatting date:', error);
      return 'Invalid Date';
    }
  }

  isOverdue(record: BorrowRecord): boolean {
    if (!record || record.status !== 'BORROWED' || !record.dueDate) {
      return false;
    }
    
    try {
      return new Date(record.dueDate) < new Date();
    } catch (error) {
      console.error('Error checking overdue status:', error);
      return false;
    }
  }

  getDaysOverdue(record: BorrowRecord): number {
    if (!this.isOverdue(record) || !record.dueDate) return 0;
    
    try {
      const today = new Date();
      const dueDate = new Date(record.dueDate);
      const diffTime = today.getTime() - dueDate.getTime();
      return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    } catch (error) {
      console.error('Error calculating overdue days:', error);
      return 0;
    }
  }

  // Helper method to get user display name safely
  getUserDisplayName(user: User | undefined): string {
    if (!user) return 'N/A';
    return `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.username || 'Unknown User';
  }

  // Helper method to get book display name safely
  getBookDisplayName(book: Book | undefined): string {
    if (!book) return 'N/A';
    return book.title || 'Unknown Book';
  }
}