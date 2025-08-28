import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BorrowRecordsComponent } from './borrow-records.component';

describe('BorrowRecordsComponent', () => {
  let component: BorrowRecordsComponent;
  let fixture: ComponentFixture<BorrowRecordsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BorrowRecordsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BorrowRecordsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
