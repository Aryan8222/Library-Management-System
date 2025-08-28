import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { User, UserStats } from '../models';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API_URL = 'http://localhost:8083/api/users';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  // Get all users
  getAllUsers(): Observable<User[]> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<User[]>(this.API_URL, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get user by ID
  getUserById(id: number): Observable<User> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<User>(`${this.API_URL}/${id}`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get user by username
  getUserByUsername(username: string): Observable<User> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<User>(`${this.API_URL}/username/${username}`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Search users by name
  searchUsers(name: string): Observable<User[]> {
    const headers = this.authService.getAuthHeader();
    const params = new HttpParams().set('name', name);
    return this.http.get<User[]>(`${this.API_URL}/search`, { params, headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get active users only
  getActiveUsers(): Observable<User[]> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<User[]>(`${this.API_URL}/active`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get users by membership type
  getUsersByMembershipType(type: string): Observable<User[]> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<User[]>(`${this.API_URL}/membership/${type}`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get users with overdue books
  getUsersWithOverdueBooks(): Observable<User[]> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<User[]>(`${this.API_URL}/overdue`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Create new user
  createUser(user: User): Observable<User> {
    const headers = this.authService.getAuthHeader();
    return this.http.post<User>(this.API_URL, user, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Update user
  updateUser(id: number, user: User): Observable<User> {
    const headers = this.authService.getAuthHeader();
    return this.http.put<User>(`${this.API_URL}/${id}`, user, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Deactivate user
  deactivateUser(id: number): Observable<User> {
    const headers = this.authService.getAuthHeader();
    return this.http.put<User>(`${this.API_URL}/${id}/deactivate`, {}, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Activate user
  activateUser(id: number): Observable<User> {
    const headers = this.authService.getAuthHeader();
    return this.http.put<User>(`${this.API_URL}/${id}/activate`, {}, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Delete user
  deleteUser(id: number): Observable<void> {
    const headers = this.authService.getAuthHeader();
    return this.http.delete<void>(`${this.API_URL}/${id}`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get user statistics
  getUserStats(): Observable<UserStats> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<UserStats>(`${this.API_URL}/stats`, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Error handling
  private handleError = (error: any): Observable<never> => {
    console.error('UserService error:', error);
    
    // Check for authentication errors
    if (error.status === 401 || error.status === 403) {
      this.authService.handleAuthError();
    }
    
    throw error;
  }
}