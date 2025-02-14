## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).


-- Bảng Book
CREATE TABLE book (
    isbn VARCHAR(20) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    publisher VARCHAR(255),
    year INT,
    quantity INT
);

-- Bảng User
CREATE TABLE user (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL -- "admin" hoặc "user"
);

-- Bảng Category
CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

-- Bảng Loan
CREATE TABLE loan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_isbn VARCHAR(20) NOT NULL,
    user_username VARCHAR(50) NOT NULL,
    borrow_date DATE,
    return_date DATE,
    status VARCHAR(20) DEFAULT 'pending', -- "pending", "approved", "rejected", "returned"
    FOREIGN KEY (book_isbn) REFERENCES book(isbn),
    FOREIGN KEY (user_username) REFERENCES user(username)
);

-- Bảng trung gian Book_Category (nếu một cuốn sách thuộc nhiều danh mục)
CREATE TABLE book_category (
    book_isbn VARCHAR(20),
    category_id INT,
    PRIMARY KEY (book_isbn, category_id),
    FOREIGN KEY (book_isbn) REFERENCES book(isbn),
    FOREIGN KEY (category_id) REFERENCES category(id)
);