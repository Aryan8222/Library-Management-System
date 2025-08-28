import { Component, Input, Output, EventEmitter, ViewChild, OnChanges } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserFormComponent } from '../user-form/user-form.component';
import { UserService } from '../../services/user.service';
import { User } from '../../models';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnChanges {
  @Input() users: User[] = [];
  @Output() userUpdated = new EventEmitter<void>();
  @Output() userDeleted = new EventEmitter<void>();
  @Output() userStatusChanged = new EventEmitter<void>();

  displayedColumns: string[] = ['name', 'username', 'email', 'membershipType', 'phone', 'status', 'createdAt', 'actions'];
  dataSource = new MatTableDataSource<User>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnChanges(): void {
    this.dataSource.data = this.users;
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  getStatusChip(user: User): { text: string; color: string; icon: string } {
    if (user.isActive) {
      return { text: 'Active', color: 'primary', icon: 'check_circle' };
    } else {
      return { text: 'Inactive', color: 'warn', icon: 'cancel' };
    }
  }

  getMembershipTypeChip(type: string): { text: string; color: string } {
    switch (type) {
      case 'PREMIUM':
        return { text: 'Premium', color: 'accent' };
      case 'STUDENT':
        return { text: 'Student', color: 'primary' };
      case 'REGULAR':
      default:
        return { text: 'Regular', color: '' };
    }
  }

  openEditDialog(user: User): void {
    const dialogRef = this.dialog.open(UserFormComponent, {
      width: '700px',
      data: { mode: 'edit', user: { ...user } }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.userUpdated.emit();
      }
    });
  }

  openViewDialog(user: User): void {
    const dialogRef = this.dialog.open(UserFormComponent, {
      width: '700px',
      data: { mode: 'view', user: user }
    });
  }

  toggleUserStatus(user: User): void {
    const action = user.isActive ? 'deactivate' : 'activate';
    const actionText = user.isActive ? 'deactivate' : 'activate';
    
    if (confirm(`Are you sure you want to ${actionText} user "${user.firstName} ${user.lastName}"?`)) {
      const operation = user.isActive 
        ? this.userService.deactivateUser(user.id!)
        : this.userService.activateUser(user.id!);

      operation.subscribe({
        next: () => {
          this.userStatusChanged.emit();
        },
        error: (error) => {
          console.error(`Error ${actionText}ing user:`, error);
          this.snackBar.open(`Failed to ${actionText} user`, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }

  deleteUser(user: User): void {
    if (confirm(`Are you sure you want to permanently delete user "${user.firstName} ${user.lastName}"? This action cannot be undone.`)) {
      this.userService.deleteUser(user.id!).subscribe({
        next: () => {
          this.userDeleted.emit();
        },
        error: (error) => {
          console.error('Error deleting user:', error);
          this.snackBar.open('Failed to delete user', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }

  getFullName(user: User): string {
    return `${user.firstName} ${user.lastName}`;
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }
}