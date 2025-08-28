import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { BorrowRecord, BorrowStats } from '../models';
import { AuthService } from './auth.service';


@Injectable({
  providedIn: 'root'
})
export class BorrowService {
  private readonly API_URL = 'http://localhost:8083/api/borrow';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  // Get all borrow records
  getAllBorrowRecords(): Observable<BorrowRecord[]> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<BorrowRecord[]>(this.API_URL, { headers })
      .pipe(catchError(this.handleError));
  }
  // Get borrow record by ID
  getBorrowRecordById(id: number): Observable<BorrowRecord> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<BorrowRecord>(`${this.API_URL}/${id}`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Borrow a book
  borrowBook(userId: number, bookId: number): Observable<BorrowRecord> {
    const headers = this.authService.getAuthHeader();
    const params = new HttpParams()
      .set('userId', userId.toString())
      .set('bookId', bookId.toString());
    
    return this.http.post<BorrowRecord>(`${this.API_URL}/borrow`, null, { params, headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Return a book
  returnBook(borrowRecordId: number): Observable<BorrowRecord> {
    const headers = this.authService.getAuthHeader();
    return this.http.put<BorrowRecord>(`${this.API_URL}/return/${borrowRecordId}`, null, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get borrow history by user
  getBorrowHistoryByUser(userId: number): Observable<BorrowRecord[]> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<BorrowRecord[]>(`${this.API_URL}/user/${userId}/history`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get currently borrowed books by user
  getCurrentlyBorrowedBooksByUser(userId: number): Observable<BorrowRecord[]> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<BorrowRecord[]>(`${this.API_URL}/user/${userId}/current`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get borrow history by book
  getBorrowHistoryByBook(bookId: number): Observable<BorrowRecord[]> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<BorrowRecord[]>(`${this.API_URL}/book/${bookId}/history`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get overdue records
  getOverdueRecords(): Observable<BorrowRecord[]> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<BorrowRecord[]>(`${this.API_URL}/overdue`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get books due today
  getBooksDueToday(): Observable<BorrowRecord[]> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<BorrowRecord[]>(`${this.API_URL}/due-today`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Extend due date
  extendDueDate(borrowRecordId: number, additionalDays: number): Observable<BorrowRecord> {
    const headers = this.authService.getAuthHeader();
    const params = new HttpParams().set('additionalDays', additionalDays.toString());
    
    return this.http.put<BorrowRecord>(`${this.API_URL}/${borrowRecordId}/extend`, null, { params, headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Update overdue status
  updateOverdueStatus(): Observable<string> {
    const headers = this.authService.getAuthHeader();
    return this.http.post(`${this.API_URL}/update-overdue-status`, null, { 
      headers, 
      responseType: 'text' 
    })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get borrowing statistics
  getBorrowingStats(): Observable<BorrowStats> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<BorrowStats>(`${this.API_URL}/stats`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Error handling
  private handleError = (error: any): Observable<never> => {
    console.error('BorrowService error:', error);
    
    // Check for authentication errors
    if (error.status === 401 || error.status === 403) {
      this.authService.handleAuthError();
    }
    
    throw error;
  }
}
