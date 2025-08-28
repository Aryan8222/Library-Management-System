import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserFormComponent } from '../user-form/user-form.component';
import { UserService } from '../../services/user.service';
import { User } from '../../models';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  searchTerm = '';
  selectedMembershipType = '';
  selectedStatus = '';
  isLoading = true;

  membershipTypes = [
    { value: 'REGULAR', label: 'Regular' },
    { value: 'PREMIUM', label: 'Premium' },
    { value: 'STUDENT', label: 'Student' }
  ];

  statusOptions = [
    { value: 'active', label: 'Active Only' },
    { value: 'inactive', label: 'Inactive Only' }
  ];

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.filteredUsers = users;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.snackBar.open('Failed to load users', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        this.isLoading = false;
      }
    });
  }

  onSearch(): void {
    this.filterUsers();
  }

  onMembershipTypeChange(): void {
    this.filterUsers();
  }

  onStatusChange(): void {
    this.filterUsers();
  }

  private filterUsers(): void {
    this.filteredUsers = this.users.filter(user => {
      const matchesSearch = !this.searchTerm || 
        user.username.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.firstName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.lastName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesMembershipType = !this.selectedMembershipType || 
        user.membershipType === this.selectedMembershipType;
      
      const matchesStatus = !this.selectedStatus || 
        (this.selectedStatus === 'active' && user.isActive) ||
        (this.selectedStatus === 'inactive' && !user.isActive);
      
      return matchesSearch && matchesMembershipType && matchesStatus;
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedMembershipType = '';
    this.selectedStatus = '';
    this.filteredUsers = this.users;
  }

  openAddUserDialog(): void {
    const dialogRef = this.dialog.open(UserFormComponent, {
      width: '600px',
      data: { mode: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUsers();
        this.snackBar.open('User added successfully!', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      }
    });
  }

  onUserUpdated(): void {
    this.loadUsers();
    this.snackBar.open('User updated successfully!', 'Close', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  onUserDeleted(): void {
    this.loadUsers();
    this.snackBar.open('User deleted successfully!', 'Close', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  onUserStatusChanged(): void {
    this.loadUsers();
    this.snackBar.open('User status updated successfully!', 'Close', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }
}