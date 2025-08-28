import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BookService } from '../../services/book.service';
import { UserService } from '../../services/user.service';
import { BorrowService } from '../../services/borrow.service';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardStats, BorrowRecord } from '../../models';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = {
    totalBooks: 0,
    availableBooks: 0,
    totalUsers: 0,
    activeUsers: 0,
    currentlyBorrowed: 0,
    overdueBooks: 0
  };

  overdueRecords: BorrowRecord[] = [];
  booksDueToday: BorrowRecord[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(
    private bookService: BookService,
    private userService: UserService,
    private borrowService: BorrowService,
    private dashboardService: DashboardService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    this.error = null;

    this.dashboardService.getDashboardStats().subscribe({
      next: (dashboardStats) => {
        this.stats = dashboardStats;
        this.loadAdditionalData();
      },
      error: (error) => {
        console.warn('Dashboard endpoint failed:', error);
        this.loadDataManually();
      }
    });
  }

  private loadAdditionalData(): void {
    forkJoin({
      overdueRecords: this.borrowService.getOverdueRecords(),
      booksDueToday: this.borrowService.getBooksDueToday()
    }).subscribe({
      next: (data) => {
        this.overdueRecords = data.overdueRecords;
        this.booksDueToday = data.booksDueToday;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading additional data:', error);
        this.overdueRecords = [];
        this.booksDueToday = [];
        this.isLoading = false;
      }
    });
  }

  private loadDataManually(): void {
    forkJoin({
      allBooks: this.bookService.getAllBooks(),
      availableBooks: this.bookService.getAvailableBooks(),
      allUsers: this.userService.getAllUsers(),
      activeUsers: this.userService.getActiveUsers(),
      borrowRecords: this.borrowService.getAllBorrowRecords(),
      overdueRecords: this.borrowService.getOverdueRecords(),
      booksDueToday: this.borrowService.getBooksDueToday()
    }).subscribe({
      next: (data) => {
        this.stats = {
          totalBooks: data.allBooks.length,
          availableBooks: data.availableBooks.length,
          totalUsers: data.allUsers.length,
          activeUsers: data.activeUsers.length,
          currentlyBorrowed: data.borrowRecords.filter(r => r.status === 'BORROWED').length,
          overdueBooks: data.overdueRecords.length
        };
        this.overdueRecords = data.overdueRecords;
        this.booksDueToday = data.booksDueToday;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading dashboard data:', error);
        this.error = 'Failed to load dashboard data';
        this.isLoading = false;
        this.snackBar.open('Failed to load dashboard data', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  getBooksAvailabilityPercentage(): number {
    return this.stats.totalBooks > 0
      ? Math.round((this.stats.availableBooks / this.stats.totalBooks) * 100)
      : 0;
  }

  getUsersActivePercentage(): number {
    return this.stats.totalUsers > 0
      ? Math.round((this.stats.activeUsers / this.stats.totalUsers) * 100)
      : 0;
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }
}
