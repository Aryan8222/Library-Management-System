import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BookFormComponent } from '../book-form/book-form.component';
import { BookService } from '../../services/book.service';
import { Book } from '../../models';

@Component({
  selector: 'app-books',
  templateUrl: './books.component.html',
  styleUrls: ['./books.component.css']
})
export class BooksComponent implements OnInit {
  books: Book[] = [];
  filteredBooks: Book[] = [];
  searchTerm = '';
  selectedGenre = '';
  genres: string[] = [];
  isLoading = true;

  constructor(
    private bookService: BookService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadBooks();
    this.loadGenres();
  }

  loadBooks(): void {
    this.isLoading = true;
    this.bookService.getAllBooks().subscribe({
      next: (books) => {
        this.books = books;
        this.filteredBooks = books;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading books:', error);
        this.snackBar.open('Failed to load books', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        this.isLoading = false;
      }
    });
  }

  loadGenres(): void {
    this.bookService.getGenres().subscribe({
      next: (genres) => {
        this.genres = genres;
      },
      error: (error) => {
        console.error('Error loading genres:', error);
      }
    });
  }

  onSearch(): void {
    this.filterBooks();
  }

  onGenreChange(): void {
    this.filterBooks();
  }

  private filterBooks(): void {
    this.filteredBooks = this.books.filter(book => {
      const matchesSearch = !this.searchTerm || 
        book.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        book.author.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        book.isbn.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesGenre = !this.selectedGenre || book.genre === this.selectedGenre;
      
      return matchesSearch && matchesGenre;
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedGenre = '';
    this.filteredBooks = this.books;
  }

  openAddBookDialog(): void {
    const dialogRef = this.dialog.open(BookFormComponent, {
      width: '600px',
      data: { mode: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadBooks();
        this.snackBar.open('Book added successfully!', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      }
    });
  }

  onBookUpdated(): void {
    this.loadBooks();
    this.snackBar.open('Book updated successfully!', 'Close', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  onBookDeleted(): void {
    this.loadBooks();
    this.snackBar.open('Book deleted successfully!', 'Close', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }
}