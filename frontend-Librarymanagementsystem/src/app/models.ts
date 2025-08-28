// User model
export interface User {
  id?: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  address?: string;
  membershipType: 'REGULAR' | 'PREMIUM' | 'STUDENT';
  isActive: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// Book model
export interface Book {
  id?: number;
  title: string;
  author: string;
  isbn: string;
  genre?: string;
  publicationYear?: number;
  totalCopies: number;
  availableCopies: number;
  createdAt?: string;
  updatedAt?: string;
}

// Borrow Record model
export interface BorrowRecord {
  id?: number;
  user: User;
  book: Book;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: 'BORROWED' | 'RETURNED' | 'OVERDUE';
  fineAmount: number;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

// Admin model
export interface Admin {
  id?: number;
  username: string;
  fullName: string;
  email: string;
  role: 'SUPER_ADMIN' | 'ADMIN' | 'LIBRARIAN';
  lastLogin?: string;
}
// src/app/models.ts

export interface DashboardStats {
  totalBooks: number;
  availableBooks: number;
  totalUsers: number;
  activeUsers: number;
  currentlyBorrowed: number;
  overdueBooks: number;
}


// Login Request
export interface LoginRequest {
  username: string;
  password: string;
}

// Auth Response
export interface AuthResponse {
  success: boolean;
  message: string;
  token?: string;
  admin?: Admin;
  timestamp: string;
}

// API Response wrapper
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
}

// Statistics models
export interface DashboardStats {
  totalBooks: number;
  availableBooks: number;
  totalUsers: number;
  activeUsers: number;
  currentlyBorrowed: number;
  overdueBooks: number;
}

export interface UserStats {
  totalUsers: number;
  activeUsers: number;
  inactiveUsers: number;
}

export interface BorrowStats {
  totalRecords: number;
  currentlyBorrowed: number;
  overdueBooks: number;
  returnedBooks: number;
}