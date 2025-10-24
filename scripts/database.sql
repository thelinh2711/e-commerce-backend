-- =====================================================
-- DATABASE CREATION
-- =====================================================
DROP DATABASE IF EXISTS shopbackend_db;
CREATE DATABASE shopbackend_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shopbackend_db;

-- =====================================================
-- USERS & AUTHENTICATION
-- =====================================================
CREATE TABLE Users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fullName VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER', 'CUSTOMER') DEFAULT 'CUSTOMER',
    status ENUM('ACTIVE', 'INACTIVE', 'BLOCKED') DEFAULT 'ACTIVE',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE UserProviders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL,
    providerName VARCHAR(50),
    providerUserId VARCHAR(255),
    FOREIGN KEY (userId) REFERENCES Users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_provider (userId, providerName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- ADDRESSES & CONTACTS
-- =====================================================
CREATE TABLE Addresses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL,
    street VARCHAR(255) NOT NULL,
    ward VARCHAR(100),
    district VARCHAR(100),
    city VARCHAR(100),
    province VARCHAR(100),
    postalCode VARCHAR(20),
    country VARCHAR(100),
    isDefault BOOLEAN DEFAULT FALSE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES Users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- CATEGORIES, BRANDS, COLORS, SIZES, LABELS (Base tables)
-- =====================================================
CREATE TABLE Categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    image VARCHAR(255),
    slug VARCHAR(100) UNIQUE,
    isActive BOOLEAN DEFAULT TRUE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Brands (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    logo VARCHAR(255),
    description TEXT,
    isActive BOOLEAN DEFAULT TRUE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Colors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    hexCode VARCHAR(7),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Sizes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Labels (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    color VARCHAR(20),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PRODUCTS (Phụ thuộc: Brands)
-- =====================================================
CREATE TABLE Products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    costPrice DECIMAL(10, 2),
    discountPrice DECIMAL(10, 2),
    discountPercent INT DEFAULT 0,
    brandId INT,
    sku VARCHAR(100) UNIQUE,
    stock INT DEFAULT 0,
    sold INT DEFAULT 0,
    rating DECIMAL(3, 2) DEFAULT 0,
    view INT DEFAULT 0,
    status ENUM('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK') DEFAULT 'ACTIVE',
    slug VARCHAR(255) UNIQUE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (brandId) REFERENCES Brands(id) ON DELETE SET NULL,
    INDEX idx_name (name),
    INDEX idx_price (price),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PRODUCT RELATIONSHIPS (Phụ thuộc: Products, Categories, Labels)
-- =====================================================
CREATE TABLE ProductCategories (
    productId INT NOT NULL,
    categoryId INT NOT NULL,
    PRIMARY KEY (productId, categoryId),
    FOREIGN KEY (productId) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (categoryId) REFERENCES Categories(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ProductLabels (
    id INT PRIMARY KEY AUTO_INCREMENT,
    productId INT NOT NULL,
    labelId INT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (productId) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (labelId) REFERENCES Labels(id) ON DELETE CASCADE,
    UNIQUE KEY unique_product_label (productId, labelId),
    INDEX idx_productId (productId),
    INDEX idx_labelId (labelId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PRODUCT VARIANTS (Phụ thuộc: Products, Colors, Sizes)
-- =====================================================
CREATE TABLE ProductVariants (
    id INT PRIMARY KEY AUTO_INCREMENT,
    productId INT NOT NULL,
    colorId INT,
    sizeId INT,
    sku VARCHAR(100) UNIQUE,
    stock INT DEFAULT 0,
    price DECIMAL(10, 2),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (productId) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (colorId) REFERENCES Colors(id) ON DELETE SET NULL,
    FOREIGN KEY (sizeId) REFERENCES Sizes(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PRODUCT IMAGES (Phụ thuộc: Products, ProductVariants)
-- =====================================================
CREATE TABLE ProductImages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    productId INT NOT NULL,
    imageUrl VARCHAR(255) NOT NULL,
    altText VARCHAR(255),
    isThumbnail BOOLEAN DEFAULT FALSE,
    displayOrder INT DEFAULT 0,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (productId) REFERENCES Products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ProductVariantImages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    productVariantId INT NOT NULL,
    imageUrl VARCHAR(255) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (productVariantId) REFERENCES ProductVariants(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- CART (Phụ thuộc: Users, Products)
-- =====================================================
CREATE TABLE Carts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL UNIQUE,
    totalItems INT DEFAULT 0,
    totalPrice DECIMAL(12, 2) DEFAULT 0,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES Users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE CartItems (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cartId INT NOT NULL,
    productId INT NOT NULL,
    productVariantId INT,
    quantity INT NOT NULL DEFAULT 1,
    unitPrice DECIMAL(10, 2) NOT NULL,
    totalPrice DECIMAL(12, 2) NOT NULL,
    addedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cartId) REFERENCES Carts(id) ON DELETE CASCADE,
    FOREIGN KEY (productId) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (productVariantId) REFERENCES ProductVariants(id) ON DELETE SET NULL,
    UNIQUE KEY unique_cart_item (cartId, productId, productVariantId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- WISHLIST (Phụ thuộc: Users, Products, ProductVariants)
-- =====================================================
CREATE TABLE Wishlists (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL UNIQUE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES Users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE WishlistItems (
    id INT PRIMARY KEY AUTO_INCREMENT,
    wishlistId INT NOT NULL,
    productId INT NOT NULL,
    productVariantId INT,
    addedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wishlistId) REFERENCES Wishlists(id) ON DELETE CASCADE,
    FOREIGN KEY (productId) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (productVariantId) REFERENCES ProductVariants(id) ON DELETE SET NULL,
    UNIQUE KEY unique_wishlist_item (wishlistId, productId, productVariantId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- VOUCHERS (Không phụ thuộc bảng khác)
-- =====================================================
CREATE TABLE Vouchers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    discountType ENUM('PERCENTAGE', 'FIXED_AMOUNT') DEFAULT 'PERCENTAGE',
    discountValue DECIMAL(10, 2) NOT NULL,
    maxUsageCount INT,
    usageCount INT DEFAULT 0,
    startDate DATETIME,
    endDate DATETIME,
    isActive BOOLEAN DEFAULT TRUE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- ORDERS (Phụ thuộc: Users)
-- =====================================================
CREATE TABLE Orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL,
    orderNumber VARCHAR(50) UNIQUE NOT NULL,
    totalAmount DECIMAL(12, 2) NOT NULL,
    discountAmount DECIMAL(10, 2) DEFAULT 0,
    shippingFee DECIMAL(10, 2) DEFAULT 0,
    finalAmount DECIMAL(12, 2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    paymentStatus ENUM('UNPAID', 'PAID', 'REFUNDED') DEFAULT 'UNPAID',
    deliveryAddress VARCHAR(255),
    notes TEXT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES Users(id) ON DELETE CASCADE,
    INDEX idx_orderNumber (orderNumber),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- ORDER ITEMS (Phụ thuộc: Orders, Products)
-- ✅ FIX: ON DELETE RESTRICT để bảo vệ dữ liệu đơn hàng
-- =====================================================
CREATE TABLE OrderItems (
    id INT PRIMARY KEY AUTO_INCREMENT,
    orderId INT NOT NULL,
    productId INT NOT NULL,
    quantity INT NOT NULL,
    unitPrice DECIMAL(10, 2) NOT NULL,
    totalPrice DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (orderId) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (productId) REFERENCES Products(id) ON DELETE RESTRICT,
    INDEX idx_orderId (orderId),
    INDEX idx_productId (productId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- ORDER VOUCHERS & USER VOUCHERS (Phụ thuộc: Orders, Users, Vouchers)
-- =====================================================
CREATE TABLE OrderVouchers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    orderId INT NOT NULL,
    voucherId INT NOT NULL,
    discountAmount DECIMAL(10, 2),
    FOREIGN KEY (orderId) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (voucherId) REFERENCES Vouchers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE UserVouchers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL,
    voucherId INT NOT NULL,
    usedAt TIMESTAMP NULL,
    FOREIGN KEY (userId) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (voucherId) REFERENCES Vouchers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PAYMENTS (Phụ thuộc: Orders)
-- =====================================================
CREATE TABLE Payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    orderId INT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    paymentMethod ENUM('CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'COD') NOT NULL,
    transactionId VARCHAR(100) UNIQUE,
    status ENUM('PENDING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (orderId) REFERENCES Orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- FLASH SALES (Phụ thuộc: Products)
-- =====================================================
CREATE TABLE FlashSales (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    startDate DATETIME NOT NULL,
    endDate DATETIME NOT NULL,
    isActive BOOLEAN DEFAULT TRUE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE FlashSaleProducts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    flashSaleId INT NOT NULL,
    productId INT NOT NULL,
    flashSalePrice DECIMAL(10, 2),
    quantity INT DEFAULT 0,
    sold INT DEFAULT 0,
    FOREIGN KEY (flashSaleId) REFERENCES FlashSales(id) ON DELETE CASCADE,
    FOREIGN KEY (productId) REFERENCES Products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- REVIEWS (Phụ thuộc: Users, Products)
-- =====================================================
CREATE TABLE Reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL,
    productId INT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(255),
    content TEXT,
    isVerified BOOLEAN DEFAULT FALSE,
    helpful INT DEFAULT 0,
    unhelpful INT DEFAULT 0,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (productId) REFERENCES Products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- CHAT & MESSAGES
-- =====================================================
CREATE TABLE Messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    content TEXT,
    senderUsername VARCHAR(100),
    senderRole ENUM('ADMIN', 'CLIENT') DEFAULT 'CLIENT',
    senderFullName VARCHAR(100),
    receiverUsername VARCHAR(100),
    roomId VARCHAR(100),
    messageType ENUM('TEXT', 'IMAGE', 'FILE') DEFAULT 'TEXT',
    attachmentName VARCHAR(255),
    attachmentType VARCHAR(50),
    attachmentUrl VARCHAR(500),
    isRead BOOLEAN DEFAULT FALSE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_roomId (roomId),
    INDEX idx_sender (senderUsername),
    INDEX idx_createdAt (createdAt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Conversations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT,
    subject VARCHAR(255),
    status ENUM('OPEN', 'IN_PROGRESS', 'CLOSED') DEFAULT 'OPEN',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES Users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE MessageAttachments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    messageId INT NOT NULL,
    fileUrl VARCHAR(500),
    fileName VARCHAR(255),
    fileSize INT,
    fileType VARCHAR(50),
    FOREIGN KEY (messageId) REFERENCES Messages(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- NEWS & BANNERS
-- =====================================================
CREATE TABLE News (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE,
    content TEXT,
    excerpt VARCHAR(500),
    image VARCHAR(255),
    author VARCHAR(100),
    status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') DEFAULT 'DRAFT',
    publishedAt DATETIME,
    viewCount INT DEFAULT 0,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_publishedAt (publishedAt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Banners (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    image VARCHAR(255) NOT NULL,
    link VARCHAR(500),
    position ENUM('TOP', 'MIDDLE', 'BOTTOM') DEFAULT 'TOP',
    displayOrder INT DEFAULT 0,
    isActive BOOLEAN DEFAULT TRUE,
    startDate DATETIME,
    endDate DATETIME,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================
CREATE INDEX idx_users_email ON Users(email);
CREATE INDEX idx_users_role ON Users(role);
CREATE INDEX idx_products_brand ON Products(brandId);
CREATE INDEX idx_orders_user ON Orders(userId);
CREATE INDEX idx_orders_date ON Orders(createdAt);
CREATE INDEX idx_reviews_product ON Reviews(productId);
CREATE INDEX idx_reviews_user ON Reviews(userId);
CREATE INDEX idx_carts_user ON Carts(userId);
CREATE INDEX idx_cartitems_cart ON CartItems(cartId);
CREATE INDEX idx_wishlists_user ON Wishlists(userId);
