﻿-- Tạo database
CREATE DATABASE hotel_booking_system;
GO

USE hotel_booking_system;
GO

-- Bảng roles
CREATE TABLE roles (
    role_id INT PRIMARY KEY IDENTITY(1,1),
    role_name NVARCHAR(50) UNIQUE,
    description NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- Bảng accounts (đổi tên từ users)
CREATE TABLE accounts (
    account_id INT PRIMARY KEY IDENTITY(1,1),
    full_name NVARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    phone_number VARCHAR(20),
    role_id INT,
    is_active BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);
GO

-- Bảng employees (nhân viên)
CREATE TABLE employees (
    employee_id INT PRIMARY KEY IDENTITY(1,1),
    account_id INT UNIQUE,
    employee_code VARCHAR(20) UNIQUE,
    position NVARCHAR(100),
    department NVARCHAR(100),
    hire_date DATE DEFAULT GETDATE(),
    salary DECIMAL(10, 2),
    status NVARCHAR(20) DEFAULT N'active',
	password NVARCHAR(100),
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT chk_employee_status CHECK (status IN (N'active', N'on_leave', N'terminated')),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
GO

-- Bảng password_reset_tokens
CREATE TABLE password_reset_tokens (
    token_id INT PRIMARY KEY IDENTITY(1,1),
    account_id INT,
    token VARCHAR(255) UNIQUE,
    created_at DATETIME DEFAULT GETDATE(),
    expires_at DATETIME,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
GO

-- Bảng rooms
CREATE TABLE rooms (
    room_id INT PRIMARY KEY IDENTITY(1,1),
    room_number VARCHAR(10) UNIQUE,
    room_type NVARCHAR(50),
    price_per_night DECIMAL(10, 2),
    description NVARCHAR(MAX),
    amenities NVARCHAR(MAX),
    status NVARCHAR(20) DEFAULT N'available',
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT chk_room_status CHECK (status IN (N'available', N'occupied', N'maintenance'))
);
GO

-- Bảng services (mới thêm)
CREATE TABLE services (
    service_id INT PRIMARY KEY IDENTITY(1,1),
    service_name NVARCHAR(100),
    description NVARCHAR(MAX),
    price DECIMAL(10, 2),
    category NVARCHAR(50),
    is_available BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- Bảng bookings
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY IDENTITY(1,1),
    account_id INT,
    room_id INT,
    check_in_date DATE,
    check_out_date DATE,
    total_price DECIMAL(10, 2),
    status NVARCHAR(20) DEFAULT N'pending',
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT chk_booking_status CHECK (status IN (N'pending', N'confirmed', N'checked_in', N'checked_out', N'cancelled')),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);
GO

-- Bảng booking_services (liên kết booking với services)
CREATE TABLE booking_services (
    booking_service_id INT PRIMARY KEY IDENTITY(1,1),
    booking_id INT,
    service_id INT,
    quantity INT DEFAULT 1,
    price DECIMAL(10, 2),
    service_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    FOREIGN KEY (service_id) REFERENCES services(service_id)
);
GO

-- Bảng payments
CREATE TABLE payments (
    payment_id INT PRIMARY KEY IDENTITY(1,1),
    booking_id INT,
    amount DECIMAL(10, 2),
    payment_method VARCHAR(50),
    payment_date DATETIME DEFAULT GETDATE(),
    status NVARCHAR(20) DEFAULT N'pending',
    transaction_id VARCHAR(255),
    CONSTRAINT chk_payment_status CHECK (status IN (N'pending', N'completed', N'failed', N'refunded')),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);
GO

-- Bảng reviews
CREATE TABLE reviews (
    review_id INT PRIMARY KEY IDENTITY(1,1),
    booking_id INT,
    account_id INT,
    rating INT,
    comment NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
GO

-- Bảng notifications
CREATE TABLE notifications (
    notification_id INT PRIMARY KEY IDENTITY(1,1),
    account_id INT,
    message NVARCHAR(MAX),
    link_url VARCHAR(255),
    is_read BIT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
GO

-- Bảng reports
CREATE TABLE reports (
    report_id INT PRIMARY KEY IDENTITY(1,1),
    generated_by_account_id INT,
    report_type VARCHAR(50),
    generated_at DATETIME DEFAULT GETDATE(),
    file_path VARCHAR(255),
    parameters NVARCHAR(MAX),
    FOREIGN KEY (generated_by_account_id) REFERENCES accounts(account_id)
);
GO

-- Bảng chatbox
CREATE TABLE chatbox (
    chatbox_id INT PRIMARY KEY IDENTITY(1,1),
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- Bảng chatbox_participants
CREATE TABLE chatbox_participants (
    chatbox_id INT,
    account_id INT,
    PRIMARY KEY (chatbox_id, account_id),
    FOREIGN KEY (chatbox_id) REFERENCES chatbox(chatbox_id),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
GO

-- Bảng messages
CREATE TABLE messages (
    message_id INT PRIMARY KEY IDENTITY(1,1),
    chatbox_id INT,
    sender_id INT,
    content NVARCHAR(MAX),
    sent_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (chatbox_id) REFERENCES chatbox(chatbox_id),
    FOREIGN KEY (sender_id) REFERENCES accounts(account_id)
);
GO

-- Thêm dữ liệu mẫu cho bảng roles
SET IDENTITY_INSERT roles ON;
INSERT INTO roles (role_id, role_name, description) VALUES
(1, N'customer', N'Khách hàng đặt phòng'),
(2, N'admin', N'Quản trị viên hệ thống'),
(3, N'staff', N'Nhân viên khách sạn'),
(4, N'manager', N'Quản lý khách sạn');
SET IDENTITY_INSERT roles OFF;
GO

-- Thêm dữ liệu mẫu cho services
SET IDENTITY_INSERT services ON;
INSERT INTO services (service_id, service_name, description, price, category, is_available) VALUES
(1, N'Breakfast', N'Buffet sáng tại nhà hàng', 150000, N'Food & Beverage', 1),
(2, N'Airport Pickup', N'Đưa đón sân bay', 300000, N'Transportation', 1),
(3, N'Spa Massage', N'Massage thư giãn 60 phút', 500000, N'Spa & Wellness', 1),
(4, N'Laundry Service', N'Giặt ủi quần áo', 100000, N'Room Service', 1),
(5, N'Tour Guide', N'Hướng dẫn viên du lịch', 800000, N'Tourism', 1);
SET IDENTITY_INSERT services OFF;
GO