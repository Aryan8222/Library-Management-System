import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { LoginRequest, AuthResponse, Admin } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8083/api';
  private currentAdminSubject: BehaviorSubject<Admin | null>;
  public currentAdmin: Observable<Admin | null>;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    // Initialize with stored admin data if available
    const storedAdmin = localStorage.getItem('currentAdmin');
    this.currentAdminSubject = new BehaviorSubject<Admin | null>(
      storedAdmin ? JSON.parse(storedAdmin) : null
    );
    this.currentAdmin = this.currentAdminSubject.asObservable();
  }

  public get currentAdminValue(): Admin | null {
    return this.currentAdminSubject.value;
  }

  // Login method
  login(loginRequest: LoginRequest): Observable<AuthResponse> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<AuthResponse>(`${this.API_URL}/auth/login`, loginRequest, { headers })
      .pipe(
        map(response => {
          if (response.success && response.admin) {
            // Store admin details and token in local storage
            localStorage.setItem('currentAdmin', JSON.stringify(response.admin));
            if (response.token) {
              localStorage.setItem('token', response.token);
            }
            
            // Update current admin subject
            this.currentAdminSubject.next(response.admin);
          }
          return response;
        }),
        catchError(error => {
          console.error('Login error:', error);
          throw error;
        })
      );
  }

  // Logout method
  logout(): void {
    // Remove admin from local storage
    localStorage.removeItem('currentAdmin');
    localStorage.removeItem('token');
    
    // Update current admin subject
    this.currentAdminSubject.next(null);
    
    // Navigate to login page
    this.router.navigate(['/login']);
  }

  // Check if user is logged in
  isLoggedIn(): boolean {
    return this.currentAdminValue !== null;
  }

  // Get authorization header
  getAuthHeader(): HttpHeaders {
    const admin = this.currentAdminValue;
    const token = localStorage.getItem('token');
    
    if (admin && token) {
      return new HttpHeaders({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      });
    } else {
      return new HttpHeaders({
        'Content-Type': 'application/json'
      });
    }
  }

  // Get current admin profile
  getProfile(): Observable<AuthResponse> {
    const headers = this.getAuthHeader();
    return this.http.get<AuthResponse>(`${this.API_URL}/auth/profile`, { headers });
  }

  // Auto-logout when token expires or on error
  handleAuthError(): void {
    this.logout();
  }
}