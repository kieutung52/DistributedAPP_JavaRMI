package view;

import model.Book;
import model.Loan;
import model.User;
import server.services.book_service.BookService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;

public class UserPanel extends JPanel {
    private Client client;
    private User currentUser;
    private BookService bookService;
    private DefaultTableModel tableModel;
    private JTable bookTable;
    private JButton borrowButton;
    private JButton returnButton;
    private DefaultTableModel requestTableModel;
    private JTable requestTable;
    private Loan selectedLoan;

    public UserPanel(Client client) {
        this.client = client;
        this.bookService = client.getBookService(); 
        setLayout(new BorderLayout());

        
        tableModel = new DefaultTableModel(new Object[]{"ISBN", "Tiêu đề", "Tác giả", "Nhà xuất bản", "Năm", "Số lượng"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        borrowButton = new JButton("Mượn sách");
        returnButton = new JButton("Trả sách");
        JButton viewAllButton = new JButton("Xem tất cả");

        buttonPanel.add(borrowButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(viewAllButton);
        add(buttonPanel, BorderLayout.NORTH);

        
        requestTableModel = new DefaultTableModel(new Object[]{"Sách", "Trạng thái"}, 0);
        requestTable = new JTable(requestTableModel);
        JScrollPane requestScrollPane = new JScrollPane(requestTable);
        add(requestScrollPane, BorderLayout.SOUTH);

        
        borrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookTable.getSelectedRow();
                if (selectedRow != -1) {
                    Book book = getSelectedBook(selectedRow);
                    borrowBook(book);
                } else {
                    JOptionPane.showMessageDialog(UserPanel.this, "Vui lòng chọn sách để mượn.");
                }
            }
        });

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = requestTable.getSelectedRow(); // Chọn từ bảng yêu cầu
                if (selectedRow != -1) {
                    selectedLoan = getSelectedLoan(selectedRow); // Lấy loan được chọn
                    if (selectedLoan.getStatus().equals("approved")) { // Kiểm tra trạng thái đã được duyệt
                        returnBook(selectedLoan);
                    } else {
                        JOptionPane.showMessageDialog(UserPanel.this, "Sách này chưa được duyệt để trả.");
                    }
                } else {
                    JOptionPane.showMessageDialog(UserPanel.this, "Vui lòng chọn sách đã được duyệt để trả.");
                }
            }
        });

        viewAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAllBooks();
                updateBorrowRequests();
            }
        });

        setVisible(true);
        viewAllBooks(); 

    }

    private Book getSelectedBook(int selectedRow) {
        String isbn = (String) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        String author = (String) tableModel.getValueAt(selectedRow, 2);
        String publisher = (String) tableModel.getValueAt(selectedRow, 3);
        int year = (int) tableModel.getValueAt(selectedRow, 4);
        int quantity = (int) tableModel.getValueAt(selectedRow, 5);
        return new Book(isbn, title, author, publisher, year, quantity);
    }

    private void borrowBook(Book book) {
        User currentUser = client.getCurrentUser();
        if (currentUser != null) {
            try {
                bookService.borrowBook(book, currentUser);
                JOptionPane.showMessageDialog(this, "Yêu cầu mượn sách đã được gửi.");
                updateBorrowRequests(); 
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi mượn sách: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Bạn cần đăng nhập để mượn sách.");
        }
    }

    private void returnBook(Book book) {
        User currentUser = client.getCurrentUser();
        if (currentUser != null) {
            try {
                bookService.returnBook(book, currentUser);
                JOptionPane.showMessageDialog(this, "Trả sách thành công.");
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi trả sách: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Bạn cần đăng nhập để trả sách.");
        }
    }

    private void viewAllBooks() {
        try {
            List<Book> books = bookService.viewAllBooks();
            updateBookTable(books);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi lấy danh sách sách: " + ex.getMessage());
        }
    }

    private void updateBookTable(List<Book> books) {
        tableModel.setRowCount(0); 
        for (Book book : books) {
            tableModel.addRow(new Object[]{book.isbn, book.title, book.author, book.publisher, book.year, book.quantity});
        }
    }

    private Loan getSelectedLoan(int selectedRow) {
        String bookTitle = (String) requestTableModel.getValueAt(selectedRow, 0);
        String status = (String) requestTableModel.getValueAt(selectedRow, 1);

        try {
            List<Loan> loans = bookService.getBorrowRequestsByUser();
            for (Loan loan : loans) {
                if (loan.getBook().getTitle().equals(bookTitle) && loan.getStatus().equals(status)) {
                    return loan;
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi lấy danh sách yêu cầu mượn: " + ex.getMessage());
        }

        return null;
    }


    private void returnBook(Loan loan) {
        User currentUser = client.getCurrentUser();
        if (currentUser != null) {
            try {
                bookService.returnBook(loan);
                JOptionPane.showMessageDialog(this, "Trả sách thành công.");
                updateBorrowRequests();
                viewAllBooks();
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi trả sách: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Bạn cần đăng nhập để trả sách.");
        }
    }

    private void updateBorrowRequests() {
        User currentUser = client.getCurrentUser();
        if (currentUser != null) {
            try {
                List<Loan> loans = bookService.getBorrowRequestsByUser();
                requestTableModel.setRowCount(0);

                for (Loan loan : loans) {
                    if (loan.getBorrower().getUsername().equals(currentUser.getUsername())) {
                        requestTableModel.addRow(new Object[]{loan.getBook().getTitle(), loan.getStatus()});
                    }
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi lấy danh sách yêu cầu mượn: " + ex.getMessage());
            }
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;

        if (user != null) {
            updateBorrowRequests();
        } else {
            client.showPanel("authentication");
        }
    }
}