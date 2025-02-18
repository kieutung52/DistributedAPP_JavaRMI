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

public class AdminPanel extends JPanel {
    private Client client;
    @SuppressWarnings("unused")
    private User currentUser;
    private BookService bookService;
    private DefaultTableModel bookTableModel;
    private JTable bookTable;
    private JTextField isbnField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField publisherField;
    private JTextField yearField;
    private JTextField quantityField;
    private JDialog bookDialog;

    private DefaultTableModel loanTableModel;
    private JTable loanTable;

    public AdminPanel(Client client) {
        this.client = client;
        this.bookService = client.getBookService();
        setLayout(new BorderLayout());

        JPanel bookPanel = new JPanel(new BorderLayout());
        bookTableModel = new DefaultTableModel(new Object[]{"ISBN", "Tiêu đề", "Tác giả", "Nhà xuất bản", "Năm", "Số lượng"}, 0);
        bookTable = new JTable(bookTableModel);
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        bookPanel.add(bookScrollPane, BorderLayout.CENTER);

        JPanel bookButtonPanel = new JPanel(new FlowLayout());
        JButton addBookButton = new JButton("Thêm sách");
        JButton updateBookButton = new JButton("Cập nhật sách");
        JButton deleteBookButton = new JButton("Xóa sách");
        JButton viewAllBooksButton = new JButton("Xem tất cả sách");

        bookButtonPanel.add(addBookButton);
        bookButtonPanel.add(updateBookButton);
        bookButtonPanel.add(deleteBookButton);
        bookButtonPanel.add(viewAllBooksButton);
        bookPanel.add(bookButtonPanel, BorderLayout.NORTH);

        add(bookPanel, BorderLayout.CENTER);

        JPanel loanPanel = new JPanel(new BorderLayout());
        loanTableModel = new DefaultTableModel(new Object[]{"ID", "Sách", "Người mượn", "Ngày mượn", "Trạng thái"}, 0);
        loanTable = new JTable(loanTableModel);
        JScrollPane loanScrollPane = new JScrollPane(loanTable);
        loanPanel.add(loanScrollPane, BorderLayout.CENTER);

        JPanel loanButtonPanel = new JPanel(new FlowLayout());
        JButton approveButton = new JButton("Duyệt");
        JButton rejectButton = new JButton("Từ chối");
        JButton viewAllRequestsButton = new JButton("Xem tất cả yêu cầu");

        loanButtonPanel.add(approveButton);
        loanButtonPanel.add(rejectButton);
        loanButtonPanel.add(viewAllRequestsButton);
        loanPanel.add(loanButtonPanel, BorderLayout.NORTH);

        add(loanPanel, BorderLayout.SOUTH);

        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBookDialog(null);
            }
        });

        updateBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookTable.getSelectedRow();
                if (selectedRow != -1) {
                    Book book = getSelectedBook(selectedRow);
                    showBookDialog(book);
                } else {
                    JOptionPane.showMessageDialog(AdminPanel.this, "Vui lòng chọn sách để cập nhật.");
                }
            }
        });

        deleteBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookTable.getSelectedRow();
                if (selectedRow != -1) {
                    String isbn = (String) bookTableModel.getValueAt(selectedRow, 0);
                    deleteBook(isbn);
                } else {
                    JOptionPane.showMessageDialog(AdminPanel.this, "Vui lòng chọn sách để xóa.");
                }
            }
        });

        viewAllBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAllBooks();
            }
        });

        approveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = loanTable.getSelectedRow();
                if (selectedRow != -1) {
                    int loanId = (int) loanTableModel.getValueAt(selectedRow, 0);
                    approveRequest(loanId);
                } else {
                    JOptionPane.showMessageDialog(AdminPanel.this, "Vui lòng chọn yêu cầu để duyệt.");
                }
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = loanTable.getSelectedRow();
                if (selectedRow != -1) {
                    int loanId = (int) loanTableModel.getValueAt(selectedRow, 0);
                    rejectRequest(loanId);
                } else {
                    JOptionPane.showMessageDialog(AdminPanel.this, "Vui lòng chọn yêu cầu để từ chối.");
                }
            }
        });

        viewAllRequestsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAllRequests();
            }
        });

        setVisible(true);
        viewAllBooks();
        viewAllRequests();
    }

    private void showBookDialog(Book book) {
        bookDialog = new JDialog(client, book == null ? "Thêm sách" : "Cập nhật sách", true);
        bookDialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        JLabel isbnLabel = new JLabel("ISBN:");
        isbnField = new JTextField();
        isbnField.setEditable(book == null);
        JLabel titleLabel = new JLabel("Tiêu đề:");
        titleField = new JTextField();
        JLabel authorLabel = new JLabel("Tác giả:");
        authorField = new JTextField();
        JLabel publisherLabel = new JLabel("Nhà xuất bản:");
        publisherField = new JTextField();
        JLabel yearLabel = new JLabel("Năm xuất bản:");
        yearField = new JTextField();
        JLabel quantityLabel = new JLabel("Số lượng:");
        quantityField = new JTextField();

        if (book != null) {
            isbnField.setText(book.getIsbn());
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            publisherField.setText(book.getPublisher());
            yearField.setText(String.valueOf(book.getYear()));
            quantityField.setText(String.valueOf(book.getQuantity()));
        }

        inputPanel.add(isbnLabel);
        inputPanel.add(isbnField);
        inputPanel.add(titleLabel);
        inputPanel.add(titleField);
        inputPanel.add(authorLabel);
        inputPanel.add(authorField);
        inputPanel.add(publisherLabel);
        inputPanel.add(publisherField);
        inputPanel.add(yearLabel);
        inputPanel.add(yearField);
        inputPanel.add(quantityLabel);
        inputPanel.add(quantityField);

        bookDialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String isbn = isbnField.getText();
                String title = titleField.getText();
                String author = authorField.getText();
                String publisher = publisherField.getText();
                int year = 0;
                int quantity = 0;

                try {
                    year = Integer.parseInt(yearField.getText());
                    quantity = Integer.parseInt(quantityField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(bookDialog, "Năm và số lượng phải là số nguyên.");
                    return;
                }

                if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || publisher.isEmpty()) {
                    JOptionPane.showMessageDialog(bookDialog, "Vui lòng nhập đầy đủ thông tin.");
                    return;
                }

                try {
                    Book newBook = new Book(isbn, title, author, publisher, year, quantity);
                    if (book == null) {
                        bookService.addBook(newBook);
                        JOptionPane.showMessageDialog(bookDialog, "Thêm sách thành công.");
                    } else {
                        bookService.updateBook(newBook);
                        JOptionPane.showMessageDialog(bookDialog, "Cập nhật sách thành công.");
                    }
                    bookDialog.dispose();
                    viewAllBooks();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(bookDialog, "Lỗi: " + ex.getMessage());
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookDialog.dispose();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        bookDialog.add(buttonPanel, BorderLayout.SOUTH);

        bookDialog.pack();
        bookDialog.setLocationRelativeTo(client);
        bookDialog.setVisible(true);
    }

    private Book getSelectedBook(int selectedRow) {
        String isbn = (String) bookTableModel.getValueAt(selectedRow, 0);
        String title = (String) bookTableModel.getValueAt(selectedRow, 1);
        String author = (String) bookTableModel.getValueAt(selectedRow, 2);
        String publisher = (String) bookTableModel.getValueAt(selectedRow, 3);
        int year = (int) bookTableModel.getValueAt(selectedRow, 4);
        int quantity = (int) bookTableModel.getValueAt(selectedRow, 5);
        return new Book(isbn, title, author, publisher, year, quantity);
    }

    private void deleteBook(String isbn) {
        int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sách này không?", "Xóa sách", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                bookService.deleteBook(isbn);
                JOptionPane.showMessageDialog(this, "Xóa sách thành công.");
                viewAllBooks();
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xóa sách: " + ex.getMessage());
            }
        }
    }

    public void viewAllBooks() {
        try {
            List<Book> books = bookService.viewAllBooks();
            updateBookTable(books);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi lấy danh sách sách: " + ex.getMessage());
        }
    }

    private void updateBookTable(List<Book> books) {
        bookTableModel.setRowCount(0);
        for (Book book : books) {
            bookTableModel.addRow(new Object[]{book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getYear(), book.getQuantity()});
        }
    }

    public void viewAllRequests() {
        try {
            List<Loan> loans = bookService.getBorrowRequests();
            updateLoanTable(loans);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi lấy danh sách yêu cầu mượn: " + ex.getMessage());
        }
    }

    private void updateLoanTable(List<Loan> loans) {
        loanTableModel.setRowCount(0);
        for (Loan loan : loans) {
            loanTableModel.addRow(new Object[]{loan.getId(), loan.getBook().getTitle(), loan.getBorrower().getUsername(), loan.getBorrowDate(), loan.getStatus()});
        }
    }

    private void approveRequest(int loanId) {
        try {
            bookService.approveBorrowRequest(loanId);
            JOptionPane.showMessageDialog(this, "Yêu cầu mượn đã được duyệt.");
            viewAllRequests();
            viewAllBooks();
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi duyệt yêu cầu: " + ex.getMessage());
        }
    }

    private void rejectRequest(int loanId) {
        try {
            bookService.rejectBorrowRequest(loanId);
            JOptionPane.showMessageDialog(this, "Yêu cầu mượn đã bị từ chối.");
            viewAllRequests();
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi từ chối yêu cầu: " + ex.getMessage());
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;

        if (user == null) {
            client.showPanel("authentication");
        }
    }
}