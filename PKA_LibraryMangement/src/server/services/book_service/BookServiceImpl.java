package server.services.book_service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Book;
import model.Loan;
import model.User;

public class BookServiceImpl extends UnicastRemoteObject implements BookService {
    private Connection connection;

    public BookServiceImpl() throws RemoteException, SQLException {
        String url = "jdbc:mysql://mysql-c1071a-kieuthanhtung0502-6dee.c.aivencloud.com:18485/PKA_libraryRMI?useSSL=false";
        String user = "avnadmin";
        String password = "AVNS_NZCUzPfcnm3RVyLqLtB";
        connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
    }

    @Override
    public void addBook(Book book) throws RemoteException {
        try {
            String sql = "INSERT INTO book (isbn, title, author, publisher, year, quantity) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setString(4, book.getPublisher());
            statement.setInt(5, book.getYear());
            statement.setInt(6, book.getQuantity());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi thêm sách: " + e.getMessage());
        }
    }

    @Override
    public List<Book> searchBook(String keyword) throws RemoteException {
        List<Book> books = new ArrayList<>();
        try {
            String sql = "SELECT * FROM book WHERE title LIKE ? OR author LIKE ? OR publisher LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + keyword + "%");
            statement.setString(2, "%" + keyword + "%");
            statement.setString(3, "%" + keyword + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Book book = new Book(
                    resultSet.getString("isbn"),
                    resultSet.getString("title"),
                    resultSet.getString("author"),
                    resultSet.getString("publisher"),
                    resultSet.getInt("year"),
                    resultSet.getInt("quantity")
                );
                books.add(book);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi tìm kiếm sách: " + e.getMessage());
        }
        return books;
    }

    @Override
    public void updateBook(Book book) throws RemoteException {
        try {
            String sql = "UPDATE book SET title = ?, author = ?, publisher = ?, year = ?, quantity = ? WHERE isbn = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getPublisher());
            statement.setInt(4, book.getYear());
            statement.setInt(5, book.getQuantity());
            statement.setString(6, book.getIsbn());
            int rowsUpdated = statement.executeUpdate();
            statement.close();

            if (rowsUpdated == 0) {
                throw new RemoteException("Không tìm thấy sách để cập nhật.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi cập nhật sách: " + e.getMessage());
        }
    }

    @Override
    public void deleteBook(String isbn) throws RemoteException {
        try {
            String sql = "DELETE FROM book WHERE isbn = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, isbn);
            int rowsDeleted = statement.executeUpdate();
            statement.close();

            if (rowsDeleted == 0) {
                throw new RemoteException("Không tìm thấy sách để xóa.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi xóa sách: " + e.getMessage());
        }
    }

    @Override
    public List<Book> viewAllBooks() throws RemoteException {
        List<Book> books = new ArrayList<>();
        try {
            String sql = "SELECT * FROM book";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Book book = new Book(
                    resultSet.getString("isbn"),
                    resultSet.getString("title"),
                    resultSet.getString("author"),
                    resultSet.getString("publisher"),
                    resultSet.getInt("year"),
                    resultSet.getInt("quantity")
                );
                books.add(book);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi xem danh sách sách: " + e.getMessage());
        }
        return books;
    }

    @Override
    public List<Loan> getBorrowRequests() throws RemoteException {
        List<Loan> loans = new ArrayList<>();
        try {
            String sql = "SELECT l.id, l.book_isbn, b.title as book_title, b.author as book_author, b.publisher as book_publisher, b.year as book_year, b.quantity as book_quantity, l.user_username, u.password as user_password, u.role as user_role, l.borrow_date, l.status " +
                        "FROM loan l " +
                        "JOIN book b ON l.book_isbn = b.isbn " +
                        "JOIN user u ON l.user_username = u.username " +
                        "WHERE l.status = 'pending'";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getString("book_isbn"),
                        resultSet.getString("book_title"),
                        resultSet.getString("book_author"),
                        resultSet.getString("book_publisher"),
                        resultSet.getInt("book_year"),
                        resultSet.getInt("book_quantity")
                );

                User user = new User(
                        resultSet.getString("user_username"),
                        resultSet.getString("user_password"),
                        resultSet.getString("user_role")
                );

                Loan loan = new Loan(
                        book,
                        user,
                        resultSet.getDate("borrow_date"),
                        resultSet.getString("status")
                );
                loan.setId(resultSet.getInt("id"));
                loans.add(loan);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi lấy danh sách yêu cầu mượn: " + e.getMessage());
        }
        return loans;
    }

    @Override
    public List<Loan> getBorrowRequestsByUser() throws RemoteException {
        List<Loan> loans = new ArrayList<>();
        try {
            String sql = "SELECT l.id, l.book_isbn, b.title as book_title, b.author as book_author, b.publisher as book_publisher, b.year as book_year, b.quantity as book_quantity, l.user_username, u.password as user_password, u.role as user_role, l.borrow_date, l.status " +
                        "FROM loan l " +
                        "JOIN book b ON l.book_isbn = b.isbn " +
                        "JOIN user u ON l.user_username = u.username ";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getString("book_isbn"),
                        resultSet.getString("book_title"),
                        resultSet.getString("book_author"),
                        resultSet.getString("book_publisher"),
                        resultSet.getInt("book_year"),
                        resultSet.getInt("book_quantity")
                );

                User user = new User(
                        resultSet.getString("user_username"),
                        resultSet.getString("user_password"),
                        resultSet.getString("user_role")
                );

                Loan loan = new Loan(
                        book,
                        user,
                        resultSet.getDate("borrow_date"),
                        resultSet.getString("status")
                );
                loan.setId(resultSet.getInt("id"));
                loans.add(loan);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi lấy danh sách yêu cầu mượn: " + e.getMessage());
        }
        return loans;
    }

    @Override
    public void approveBorrowRequest(int loanId) throws RemoteException {
        try {
            String sql = "UPDATE loan SET status = 'approved' WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, loanId);
            statement.executeUpdate();

            sql = "UPDATE book SET quantity = quantity - 1 WHERE isbn = (SELECT book_isbn FROM loan WHERE id = ?)";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, loanId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi duyệt yêu cầu mượn: " + e.getMessage());
        }
    }

    @Override
    public void returnBook(Loan loan) throws RemoteException {
        try {
            String updateLoanSQL = "UPDATE loan SET status = 'returned' WHERE id = ?";
            PreparedStatement updateLoanStatement = connection.prepareStatement(updateLoanSQL);
            updateLoanStatement.setInt(1, loan.getId());
            updateLoanStatement.executeUpdate();
            updateLoanStatement.close();

            String updateBookSQL = "UPDATE book SET quantity = quantity + 1 WHERE isbn = ?";
            PreparedStatement updateBookStatement = connection.prepareStatement(updateBookSQL);
            updateBookStatement.setString(1, loan.getBook().getIsbn());
            updateBookStatement.executeUpdate();
            updateBookStatement.close();

            connection.commit();
            System.out.println("Trả sách thành công (Loan ID: " + loan.getId() + ")");

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RemoteException("Lỗi trả sách: " + e.getMessage());
        }
    }

    @Override
    public void rejectBorrowRequest(int loanId) throws RemoteException {
        try {
            String sql = "UPDATE loan SET status = 'rejected' WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, loanId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi từ chối yêu cầu mượn: " + e.getMessage());
        }
    }

    @Override
    public void borrowBook(Book book, User user) throws RemoteException {
        try {
            String sql = "INSERT INTO loan (book_isbn, user_username, borrow_date, status) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, book.getIsbn());
            statement.setString(2, user.getUsername());
            statement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            statement.setString(4, "pending");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi mượn sách: " + e.getMessage());
        }
    }

    @Override
    public void returnBook(Book book, User user) throws RemoteException {
        try {
            String sql = "UPDATE loan SET status = 'returned', return_date = ? WHERE book_isbn = ? AND user_username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            statement.setString(2, book.getIsbn());
            statement.setString(3, user.getUsername());
            statement.executeUpdate();

            // Cập nhật số lượng sách
            sql = "UPDATE book SET quantity = quantity + 1 WHERE isbn = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, book.getIsbn());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi trả sách: " + e.getMessage());
        }
    }
}
