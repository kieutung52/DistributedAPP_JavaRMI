package model;

import java.io.Serializable;
import java.util.Date;

public class Loan implements Serializable {
    private int id;
    private Book book;
    private User borrower;
    private Date borrowDate;
    private Date returnDate;
    private String status; // "pending", "approved", "rejected", "returned"

    public Loan() {}

    public Loan(Book book, User borrower, Date borrowDate, String status) {
        this.book = book;
        this.borrower = borrower;
        this.borrowDate = borrowDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}