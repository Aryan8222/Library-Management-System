import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../../services/user.service';
import { User } from '../../models';

export interface UserFormData {
  mode: 'create' | 'edit' | 'view';
  user?: User;
}

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css']
})
export class UserFormComponent implements OnInit {
  userForm: FormGroup;
  isLoading = false;
  
  get isCreateMode(): boolean { return this.data.mode === 'create'; }
  get isEditMode(): boolean { return this.data.mode === 'edit'; }
  get isViewMode(): boolean { return this.data.mode === 'view'; }
  get title(): string {
    switch (this.data.mode) {
      case 'create': return 'Add New User';
      case 'edit': return 'Edit User';
      case 'view': return 'User Details';
      default: return 'User';
    }
  }

  membershipTypes = [
    { value: 'REGULAR', label: 'Regular', description: 'Standard library membership' },
    { value: 'PREMIUM', label: 'Premium', description: 'Extended borrowing periods and priority' },
    { value: 'STUDENT', label: 'Student', description: 'Student discount with extended periods' }
  ];

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<UserFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserFormData
  ) {
    this.userForm = this.createForm();
  }

  ngOnInit(): void {
    if (this.data.user) {
      this.userForm.patchValue(this.data.user);
    }

    if (this.isViewMode) {
      this.userForm.disable();
    }
  }

  private createForm(): FormGroup {
    return this.formBuilder.group({
      username: ['', [
        Validators.required, 
        Validators.minLength(3), 
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z0-9_]+$/)
      ]],
      email: ['', [
        Validators.required, 
        Validators.email,
        Validators.maxLength(100)
      ]],
      firstName: ['', [
        Validators.required, 
        Validators.minLength(2), 
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z\s-']+$/)
      ]],
      lastName: ['', [
        Validators.required, 
        Validators.minLength(2), 
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z\s-']+$/)
      ]],
      phone: ['', [
        Validators.pattern(/^[\+]?[1-9][\d]{0,15}$/)
      ]],
      address: ['', [
        Validators.maxLength(500)
      ]],
      membershipType: ['REGULAR', Validators.required],
      isActive: [true]
    });
  }

  onSubmit(): void {
    if (this.userForm.valid && !this.isViewMode) {
      this.isLoading = true;
      const userData: User = this.userForm.value;

      if (this.isCreateMode) {
        this.createUser(userData);
      } else if (this.isEditMode) {
        this.updateUser(userData);
      }
    } else {
      this.markFormGroupTouched();
    }
  }

  private createUser(userData: User): void {
    this.userService.createUser(userData).subscribe({
      next: (user) => {
        this.isLoading = false;
        this.dialogRef.close(user);
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error creating user:', error);
        this.snackBar.open('Failed to create user. Username or email may already exist.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  private updateUser(userData: User): void {
    const userId = this.data.user!.id!;
    this.userService.updateUser(userId, userData).subscribe({
      next: (user) => {
        this.isLoading = false;
        this.dialogRef.close(user);
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error updating user:', error);
        this.snackBar.open('Failed to update user. Username or email may already exist.', 'Close', {
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
    Object.keys(this.userForm.controls).forEach(key => {
      const control = this.userForm.get(key);
      control?.markAsTouched();
    });
  }

  // Helper methods for validation
  isFieldInvalid(fieldName: string): boolean {
    const field = this.userForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  getFieldError(fieldName: string): string {
    const field = this.userForm.get(fieldName);
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
      if (errors['email']) {
        return 'Please enter a valid email address';
      }
      if (errors['pattern']) {
        return this.getPatternError(fieldName);
      }
    }
    return '';
  }

  private getFieldDisplayName(fieldName: string): string {
    const displayNames: {[key: string]: string} = {
      'username': 'Username',
      'email': 'Email',
      'firstName': 'First Name',
      'lastName': 'Last Name',
      'phone': 'Phone',
      'address': 'Address',
      'membershipType': 'Membership Type'
    };
    return displayNames[fieldName] || fieldName;
  }

  private getPatternError(fieldName: string): string {
    switch (fieldName) {
      case 'username':
        return 'Username can only contain letters, numbers, and underscores';
      case 'firstName':
      case 'lastName':
        return 'Name can only contain letters, spaces, hyphens, and apostrophes';
      case 'phone':
        return 'Please enter a valid phone number';
      default:
        return `Please enter a valid ${fieldName.toLowerCase()}`;
    }
  }

  getMembershipDescription(type: string): string {
    const membership = this.membershipTypes.find(m => m.value === type);
    return membership ? membership.description : '';
  }
}