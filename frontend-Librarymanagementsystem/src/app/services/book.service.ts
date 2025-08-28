import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Book } from '../models';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private readonly API_URL = 'http://localhost:8083/api/books';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  // Get all books
  getAllBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(this.API_URL)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get book by ID
  getBookById(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.API_URL}/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Search books by keyword
  searchBooks(keyword: string): Observable<Book[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<Book[]>(`${this.API_URL}/search`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get books by author
  getBooksByAuthor(author: string): Observable<Book[]> {
    return this.http.get<Book[]>(`${this.API_URL}/author/${author}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get books by genre
  getBooksByGenre(genre: string): Observable<Book[]> {
    return this.http.get<Book[]>(`${this.API_URL}/genre/${genre}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get available books only
  getAvailableBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(`${this.API_URL}/available`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Create new book (requires authentication)
  createBook(book: Book): Observable<Book> {
    const headers = this.authService.getAuthHeader();
    return this.http.post<Book>(this.API_URL, book, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Update book (requires authentication)
  updateBook(id: number, book: Book): Observable<Book> {
    const headers = this.authService.getAuthHeader();
    return this.http.put<Book>(`${this.API_URL}/${id}`, book, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Delete book (requires authentication)
  deleteBook(id: number): Observable<void> {
    const headers = this.authService.getAuthHeader();
    return this.http.delete<void>(`${this.API_URL}/${id}`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Check if book is available
  isBookAvailable(id: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.API_URL}/${id}/availability`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get unique genres for filtering
  getGenres(): Observable<string[]> {
    return new Observable(observer => {
      this.getAllBooks().subscribe(
        books => {
          const genres = Array.from(new Set(
            books
              .filter(book => book.genre)
              .map(book => book.genre!)
          )).sort();
          observer.next(genres);
          observer.complete();
        },
        error => observer.error(error)
      );
    });
  }

  // Error handling
  private handleError = (error: any): Observable<never> => {
    console.error('BookService error:', error);
    
    // Check for authentication errors
    if (error.status === 401 || error.status === 403) {
      this.authService.handleAuthError();
    }
    
    throw error;
  }
}