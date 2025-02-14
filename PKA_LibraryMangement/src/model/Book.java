package model;

import java.io.Serializable;

public class Book implements Serializable {
    public String isbn;
    public String title;
    public String author;
    public String publisher;
    public int year;
    public int quantity;

    public Book() {}

    public Book(String isbn, String title, String author, String publisher, int year, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.quantity = quantity;
    }

    public String getAuthor() {
        return author;
    }
    public String getIsbn() {
        return isbn;
    }
    public String getPublisher() {
        return publisher;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getTitle() {
        return title;
    }
    public int getYear() {
        return year;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setYear(int year) {
        this.year = year;
    }
}
