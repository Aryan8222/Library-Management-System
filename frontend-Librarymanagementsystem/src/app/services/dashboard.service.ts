// src/app/services/dashboard.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardStats } from '../models';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly API_URL = 'http://localhost:8083/api/dashboard';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  getDashboardStats(): Observable<DashboardStats> {
    const headers = this.authService.getAuthHeader();
    return this.http.get<DashboardStats>(this.API_URL, { headers });
  }
}