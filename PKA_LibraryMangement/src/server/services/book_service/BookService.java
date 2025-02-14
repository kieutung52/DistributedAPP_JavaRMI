package server.services.book_service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import model.Book;
import model.Loan;
import model.User;

public interface BookService extends Remote {
    void addBook(Book book) throws RemoteException;
    List<Book> searchBook(String keyword) throws RemoteException;
    void updateBook(Book book) throws RemoteException;
    void deleteBook(String isbn) throws RemoteException;
    List<Book> viewAllBooks() throws RemoteException;
    List<Loan> getBorrowRequests() throws RemoteException;
    List<Loan> getBorrowRequestsByUser() throws RemoteException;
    void approveBorrowRequest(int loanId) throws RemoteException;
    void rejectBorrowRequest(int loanId) throws RemoteException;
    void borrowBook(Book book, User user) throws RemoteException;
    void returnBook(Book book, User user) throws RemoteException;
    void returnBook(Loan loan) throws RemoteException;
}
