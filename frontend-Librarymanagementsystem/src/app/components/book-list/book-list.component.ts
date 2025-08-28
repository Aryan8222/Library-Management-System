import { Component, Input, Output, EventEmitter, ViewChild, OnChanges } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BookFormComponent } from '../book-form/book-form.component';
import { BookService } from '../../services/book.service';
import { Book } from '../../models';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css']
})
export class BookListComponent implements OnChanges {
  @Input() books: Book[] = [];
  @Output() bookUpdated = new EventEmitter<void>();
  @Output() bookDeleted = new EventEmitter<void>();

  displayedColumns: string[] = ['title', 'author', 'isbn', 'genre', 'publicationYear', 'totalCopies', 'availableCopies', 'status', 'actions'];
  dataSource = new MatTableDataSource<Book>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private bookService: BookService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnChanges(): void {
    this.dataSource.data = this.books;
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  getStatusChip(book: Book): { text: string; color: string; icon: string } {
    if (book.availableCopies > 0) {
      return { text: 'Available', color: 'primary', icon: 'check_circle' };
    } else {
      return { text: 'All Borrowed', color: 'warn', icon: 'remove_circle' };
    }
  }

  openEditDialog(book: Book): void {
    const dialogRef = this.dialog.open(BookFormComponent, {
      width: '600px',
      data: { mode: 'edit', book: { ...book } }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.bookUpdated.emit();
      }
    });
  }

  openViewDialog(book: Book): void {
    const dialogRef = this.dialog.open(BookFormComponent, {
      width: '600px',
      data: { mode: 'view', book: book }
    });
  }

  deleteBook(book: Book): void {
    if (confirm(`Are you sure you want to delete "${book.title}"?`)) {
      this.bookService.deleteBook(book.id!).subscribe({
        next: () => {
          this.bookDeleted.emit();
        },
        error: (error) => {
          console.error('Error deleting book:', error);
          this.snackBar.open('Failed to delete book', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }

  formatPublicationYear(year: number | undefined): string {
    if (!year) return 'N/A';
    if (year < 0) return `${Math.abs(year)} BC`;
    return year.toString();
  }
}