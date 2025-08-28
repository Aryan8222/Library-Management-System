import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BookService } from '../../services/book.service';
import { Book } from '../../models';

export interface BookFormData {
  mode: 'create' | 'edit' | 'view';
  book?: Book;
}

@Component({
  selector: 'app-book-form',
  templateUrl: './book-form.component.html',
  styleUrls: ['./book-form.component.css']
})
export class BookFormComponent implements OnInit {
  bookForm: FormGroup;
  isLoading = false;
  
  get isCreateMode(): boolean { return this.data.mode === 'create'; }
  get isEditMode(): boolean { return this.data.mode === 'edit'; }
  get isViewMode(): boolean { return this.data.mode === 'view'; }
  get title(): string {
    switch (this.data.mode) {
      case 'create': return 'Add New Book';
      case 'edit': return 'Edit Book';
      case 'view': return 'Book Details';
      default: return 'Book';
    }
  }

  genres = [
    'Fiction', 'Non-Fiction', 'Science Fiction', 'Fantasy', 'Mystery', 'Thriller',
    'Romance', 'Historical Fiction', 'Biography', 'History', 'Science', 'Technology',
    'Philosophy', 'Religion', 'Self-Help', 'Business', 'Education', 'Children',
    'Young Adult', 'Classic Literature', 'Poetry', 'Drama', 'Horror', 'Adventure'
  ];

  constructor(
    private formBuilder: FormBuilder,
    private bookService: BookService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<BookFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: BookFormData
  ) {
    this.bookForm = this.createForm();
  }

  ngOnInit(): void {
    if (this.data.book) {
      this.bookForm.patchValue(this.data.book);
    }

    if (this.isViewMode) {
      this.bookForm.disable();
    }
  }

  private createForm(): FormGroup {
    return this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
      author: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
      isbn: ['', [Validators.required, Validators.pattern(/^[\d\-X]{10,17}$/)]],
      genre: [''],
      publicationYear: ['', [Validators.min(-3000), Validators.max(new Date().getFullYear())]],
      totalCopies: [1, [Validators.required, Validators.min(1), Validators.max(1000)]],
      availableCopies: [1, [Validators.required, Validators.min(0)]]
    });
  }

  onSubmit(): void {
    if (this.bookForm.valid && !this.isViewMode) {
      this.isLoading = true;
      const bookData: Book = this.bookForm.value;

      // Ensure available copies don't exceed total copies
      if (bookData.availableCopies > bookData.totalCopies) {
        bookData.availableCopies = bookData.totalCopies;
      }

      if (this.isCreateMode) {
        this.createBook(bookData);
      } else if (this.isEditMode) {
        this.updateBook(bookData);
      }
    } else {
      this.markFormGroupTouched();
    }
  }

  private createBook(bookData: Book): void {
    this.bookService.createBook(bookData).subscribe({
      next: (book) => {
        this.isLoading = false;
        this.dialogRef.close(book);
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error creating book:', error);
        this.snackBar.open('Failed to create book. Please check if ISBN already exists.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  private updateBook(bookData: Book): void {
    const bookId = this.data.book!.id!;
    this.bookService.updateBook(bookId, bookData).subscribe({
      next: (book) => {
        this.isLoading = false;
        this.dialogRef.close(book);
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error updating book:', error);
        this.snackBar.open('Failed to update book. Please check if ISBN already exists.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  private markFormGroupTouched(): void {
    Object.keys(this.bookForm.controls).forEach(key => {
      const control = this.bookForm.get(key);
      control?.markAsTouched();
    });
  }

  // Helper methods for validation
  isFieldInvalid(fieldName: string): boolean {
    const field = this.bookForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  getFieldError(fieldName: string): string {
    const field = this.bookForm.get(fieldName);
    if (field?.errors) {
      const errors = field.errors;
      
      if (errors['required']) {
        return `${this.getFieldDisplayName(fieldName)} is required`;
      }
      if (errors['minlength']) {
        return `${this.getFieldDisplayName(fieldName)} must be at least ${errors['minlength'].requiredLength} characters`;
      }
      if (errors['maxlength']) {
        return `${this.getFieldDisplayName(fieldName)} cannot exceed ${errors['maxlength'].requiredLength} characters`;
      }
      if (errors['min']) {
        return `${this.getFieldDisplayName(fieldName)} must be at least ${errors['min'].min}`;
      }
      if (errors['max']) {
        return `${this.getFieldDisplayName(fieldName)} cannot exceed ${errors['max'].max}`;
      }
      if (errors['pattern']) {
        return `Please enter a valid ${fieldName.toLowerCase()}`;
      }
    }
    return '';
  }

  private getFieldDisplayName(fieldName: string): string {
    const displayNames: {[key: string]: string} = {
      'title': 'Title',
      'author': 'Author',
      'isbn': 'ISBN',
      'genre': 'Genre',
      'publicationYear': 'Publication Year',
      'totalCopies': 'Total Copies',
      'availableCopies': 'Available Copies'
    };
    return displayNames[fieldName] || fieldName;
  }

  // Sync available copies when total copies changes
  onTotalCopiesChange(): void {
    const totalCopies = this.bookForm.get('totalCopies')?.value;
    const availableCopies = this.bookForm.get('availableCopies')?.value;
    
    if (totalCopies && availableCopies > totalCopies) {
      this.bookForm.get('availableCopies')?.setValue(totalCopies);
    }
  }
}