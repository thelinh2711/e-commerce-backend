/*
=====================================================
SỬ DỤNG DATABASE
=====================================================
*/
USE shopbackend_db;

/*
=====================================================
BẢNG CƠ SỞ (TIER 1) - KHÔNG PHỤ THUỘC
=====================================================
*/

-- Brands (10)
-- Thêm: is_active để khớp với model Brand
INSERT INTO brands (name, description, is_active) VALUES
('Nike', 'Thương hiệu thể thao hàng đầu thế giới.', TRUE),
('Adidas', 'Thương hiệu thể thao nổi tiếng từ Đức.', TRUE),
('Puma', 'Thương hiệu thể thao với logo báo đốm.', TRUE),
('Under Armour', 'Thương hiệu chuyên về quần áo và phụ kiện thể thao hiệu suất cao.', TRUE),
('ASICS', 'Thương hiệu nổi tiếng về giày chạy bộ từ Nhật Bản.', TRUE),
('New Balance', 'Thương hiệu giày thể thao thoải mái và thời trang.', TRUE),
('Reebok', 'Thương hiệu con của Adidas, tập trung vào CrossFit và training.', TRUE),
('The North Face', 'Chuyên về đồ dã ngoại, leo núi và thời tiết lạnh.', TRUE),
('Columbia', 'Thương hiệu đồ outdoor và dã ngoại.', TRUE),
('Decathlon (Quechua)', 'Thương hiệu bán lẻ thể thao giá cả phải chăng.', TRUE);

-- Categories (10)
-- Đã bỏ: slug (không có trong model Category)
-- Thêm: image (bắt buộc trong model Category)
INSERT INTO categories (name, description, image) VALUES
('Giày Chạy Bộ', 'Giày chuyên dụng cho chạy bộ đường trường và đường mòn.', 'category_running_shoes.jpg'),
('Giày Đá Banh', 'Giày đinh cho sân cỏ tự nhiên và nhân tạo.', 'category_football_shoes.jpg'),
('Áo Thun & Áo Ba Lỗ', 'Áo thun, áo tank top cho tập luyện và mặc hàng ngày.', 'category_tshirts.jpg'),
('Quần Short', 'Quần short cho mọi hoạt động thể thao.', 'category_shorts.jpg'),
('Quần Dài & Legging', 'Quần jogger, quần legging, quần nỉ.', 'category_pants.jpg'),
('Áo Khoác', 'Áo khoác gió, áo khoác nỉ, áo khoác chống nước.', 'category_jackets.jpg'),
('Đồ Tập Gym', 'Quần áo chuyên dụng cho tập gym và fitness.', 'category_gym.jpg'),
('Phụ Kiện', 'Tất, mũ, găng tay, băng đô.', 'category_accessories.jpg'),
('Balo & Túi', 'Balo, túi trống, túi đeo chéo thể thao.', 'category_bags.jpg'),
('Dụng Cụ Thể Thao', 'Bóng, thảm tập yoga, dây nhảy.', 'category_equipment.jpg');

-- Colors (10)
INSERT INTO colors (name, hex_code) VALUES
('Đen', '#000000'),
('Trắng', '#FFFFFF'),
('Đỏ', '#FF0000'),
('Xanh Dương', '#0000FF'),
('Xám', '#808080'),
('Xanh Lá', '#008000'),
('Cam', '#FFA500'),
('Vàng', '#FFFF00'),
('Hồng', '#FFC0CB'),
('Tím', '#800080');

-- Sizes (10)
INSERT INTO sizes (name) VALUES
('S'),
('M'),
('L'),
('XL'),
('XXL'),
('39'),
('40'),
('41'),
('42'),
('43');

-- Labels (10)
INSERT INTO labels (name, color) VALUES
('Hàng Mới', 'blue'),
('Bán Chạy', 'green'),
('Giảm Giá', 'red'),
('Độc Quyền', 'purple'),
('Thân Thiện Môi Trường', 'darkgreen'),
('Sắp Hết Hàng', 'orange'),
('Chống Nước', 'blue'),
('Công Nghệ Mới', 'cyan'),
('Online Only', 'black'),
('Flash Sale', 'red');

-- Users (10)
-- Mật khẩu nên được băm (hashed) ở phía ứng dụng. Ở đây dùng 'hashed_password_placeholder'
-- Thêm: avatar, reward_points, status để khớp với model User
INSERT INTO users (full_name, email, phone, password, avatar, reward_points, role, status) VALUES
('Chủ cửa hàng', 'owner@shop.com', '0900000001', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', NULL, 0, 'OWNER', 'ACTIVE'),
('Quản lý', 'admin@shop.com', '0912345678', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', NULL, 0, 'ADMIN', 'ACTIVE'),
('Lê Thị Bình', 'binh.le@yahoo.com', '0987654321', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', NULL, 0, 'CUSTOMER', 'ACTIVE'),
('Phạm Văn Cường', 'cuong.pham@outlook.com', '0911223344', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', NULL, 0, 'CUSTOMER', 'ACTIVE'),
('Võ Thị Dung', 'dung.vo@gmail.com', '0922334455', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', NULL, 0, 'CUSTOMER', 'ACTIVE'),
('Hoàng Văn Em', 'em.hoang@gmail.com', '0933445566', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', NULL, 0, 'CUSTOMER', 'ACTIVE'),
('Đặng Thị Giang', 'giang.dang@gmail.com', '0944556677', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', NULL, 0, 'CUSTOMER', 'ACTIVE'),
('Bùi Văn Hải', 'hai.bui@gmail.com', '0955667788', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', NULL, 0, 'CUSTOMER', 'ACTIVE'),
('Ngô Thị Hương', 'huong.ngo@gmail.com', '0966778899', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', NULL, 0, 'CUSTOMER', 'ACTIVE'),
('Lý Văn Kiên', 'kien.ly@gmail.com', '0977889900', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', NULL, 0, 'CUSTOMER', 'ACTIVE');

-- Vouchers (10)
-- Thêm: usage_count, is_active để khớp với model Voucher
INSERT INTO vouchers (code, description, discount_type, discount_value, max_discount_value, min_order_value, usage_limit, usage_count, status, start_date, end_date) VALUES
('SALE10', 'Giảm 10% tổng đơn hàng', 'PERCENTAGE', 10, NULL, NULL, 1000, 0, 'ACTIVE', '2025-10-01 00:00:00', '2025-10-31 23:59:59'),
('FREESHIP', 'Miễn phí vận chuyển', 'FREESHIP', 0, NULL, NULL, 500, 0, 'ACTIVE', '2025-10-01 00:00:00', '2025-10-31 23:59:59'),
('NEWUSER20', 'Giảm 20% cho người dùng mới', 'PERCENTAGE', 20, NULL, NULL, 200, 0, 'ACTIVE', '2025-01-01 00:00:00', '2025-12-31 23:59:59'),
('GIAM50K', 'Giảm 50.000 VNĐ cho đơn từ 500K', 'FIXED_AMOUNT', 50000, NULL, NULL, 100, 0, 'ACTIVE', '2025-10-20 00:00:00', '2025-10-25 23:59:59'),
('NIKEONLY', 'Giảm 15% chỉ cho sản phẩm Nike', 'PERCENTAGE', 15, NULL, NULL, 50, 0, 'ACTIVE', '2025-10-15 00:00:00', '2025-10-30 23:59:59'),
('ADIDASFLASH', 'Giảm 100K cho sản phẩm Adidas', 'FIXED_AMOUNT', 100000, NULL, NULL, 50, 0, 'ACTIVE', '2025-10-24 00:00:00', '2025-10-24 23:59:59'),
('SPORT123', 'Voucher ngẫu nhiên', 'PERCENTAGE', 12, NULL, NULL, 100, 0, 'ACTIVE', '2025-10-01 00:00:00', '2025-11-30 23:59:59'),
('HAPPYWEEKEND', 'Cuối tuần vui vẻ giảm 5%', 'PERCENTAGE', 5, NULL, NULL, 100, 0, 'ACTIVE', '2025-10-25 00:00:00', '2025-10-26 23:59:59'),
('THANKYOU', 'Tri ân khách hàng giảm 25K', 'FIXED_AMOUNT', 25000, NULL, NULL, 1000, 0, 'ACTIVE', '2025-01-01 00:00:00', '2025-12-31 23:59:59'),
('RUNFAST', 'Giảm 10% cho giày chạy bộ', 'PERCENTAGE', 10, NULL, NULL, 50, 0, 'ACTIVE', '2025-10-01 00:00:00', '2025-10-31 23:59:59');

-- FlashSales (10)
-- Thêm: is_active để khớp với model FlashSale
INSERT INTO flash_sales (name, description, is_active, start_date, end_date) VALUES
('Black Friday Sớm', 'Giảm giá sốc các mặt hàng hot', TRUE, '2025-11-20 00:00:00', '2025-11-28 23:59:59'),
('Sale 11.11', 'Siêu sale 11.11', TRUE, '2025-11-11 00:00:00', '2025-11-11 23:59:59'),
('Giáng Sinh An Lành', 'Mua sắm quà giáng sinh', TRUE, '2025-12-15 00:00:00', '2025-12-25 23:59:59'),
('Xả Hàng Cuối Năm', 'Dọn kho đón năm mới', TRUE, '2025-12-28 00:00:00', '2026-01-05 23:59:59'),
('Chào Hè Sôi Động', 'Giảm giá đồ đi biển, đồ tập hè', TRUE, '2026-04-15 00:00:00', '2026-04-30 23:59:59'),
('Back to School', 'Giảm giá balo, túi xách', TRUE, '2025-08-15 00:00:00', '2025-08-31 23:59:59'),
('Running Day', 'Ưu đãi đặc biệt cho dân chạy bộ', TRUE, '2025-10-25 00:00:00', '2025-10-27 23:59:59'),
('Football Fever', 'Giảm giá giày và áo đá banh', TRUE, '2026-06-01 00:00:00', '2026-06-15 23:59:59'),
('Gym & Fitness Week', 'Sale đồ tập gym', TRUE, '2025-10-28 00:00:00', '2025-11-05 23:59:59'),
('Outdoor Essentials', 'Giảm giá đồ dã ngoại', TRUE, '2025-10-10 00:00:00', '2025-10-20 23:59:59');

-- News (10)
-- Thêm: view_count, image để khớp với model News
INSERT INTO news (title, slug, content, excerpt, image, author, status, view_count, published_at) VALUES
('Cách chọn giày chạy bộ cho người mới bắt đầu', 'cach-chon-giay-chay-bo', 'Nội dung chi tiết về cách chọn giày...', 'Việc chọn đúng giày chạy bộ là rất quan trọng...', NULL, 'Admin', 'PUBLISHED', 0, '2025-10-01 10:00:00'),
('Top 5 bài tập tăng cơ bắp hiệu quả tại nhà', 'top-5-bai-tap-tang-co-bap', 'Nội dung chi tiết về 5 bài tập...', 'Không cần đến phòng gym, bạn vẫn có thể...', NULL, 'Admin', 'PUBLISHED', 0, '2025-10-05 11:00:00'),
('Công nghệ Dri-FIT của Nike hoạt động như thế nào?', 'cong-nghe-dri-fit-nike', 'Nội dung chi tiết về Dri-FIT...', 'Dri-FIT là công nghệ vải giúp thấm hút mồ hôi...', NULL, 'Biên Tập Viên', 'PUBLISHED', 0, '2025-10-10 14:30:00'),
('Đánh giá chi tiết Adidas Ultraboost Light', 'danh-gia-adidas-ultraboost-light', 'Nội dung đánh giá...', 'Ultraboost Light có thật sự "nhẹ"?', NULL, 'Admin', 'PUBLISHED', 0, '2025-10-15 09:00:00'),
('Xu hướng thời trang thể thao 2026', 'xu-huong-thoi-trang-the-thao-2026', 'Nội dung về xu hướng...', 'Athleisure vẫn tiếp tục thống trị...', NULL, 'Biên Tập Viên', 'PUBLISHED', 0, '2025-10-20 16:00:00'),
('Hướng dẫn bảo quản giày đá banh đúng cách', 'bao-quan-giay-da-banh', 'Nội dung hướng dẫn...', 'Để đôi giày của bạn bền hơn...', NULL, 'Admin', 'PUBLISHED', 0, '2025-10-22 10:00:00'),
('Vì sao quần legging Under Armour được yêu thích?', 'vi-sao-quan-legging-ua-duoc-yeu-thich', 'Nội dung phân tích...', 'Độ co giãn, thấm hút và thiết kế...', NULL, 'Admin', 'PUBLISHED', 0, '2025-10-23 11:00:00'),
('Lợi ích của việc tập Yoga mỗi ngày', 'loi-ich-cua-yoga', 'Nội dung chi tiết...', 'Yoga không chỉ giúp dẻo dai mà còn...', NULL, 'Biên Tập Viên', 'PUBLISHED', 0, '2025-10-24 08:00:00'),
('Sự trở lại của Puma Suede', 'su-tro-lai-puma-suede', 'Nội dung...', 'Một huyền thoại đường phố...', NULL, 'Admin', 'DRAFT', 0, '2025-10-25 10:00:00'),
('Các loại balo The North Face phổ biến', 'cac-loai-balo-the-north-face', 'Nội dung...', 'Từ đi học đến đi phượt...', NULL, 'Admin', 'PUBLISHED', 0, '2025-10-18 15:00:00');

-- Banners (10)
-- Thêm: display_order để khớp với model Banner
INSERT INTO banners (title, image, link, position, display_order, is_active, start_date, end_date) VALUES
('Bộ sưu tập Nike Mới', 'banner_nike_new.jpg', '/categories/giay-chay-bo?brand=nike', 'TOP', 0, TRUE, '2025-10-15 00:00:00', '2025-10-31 23:59:59'),
('Adidas Sale 30%', 'banner_adidas_sale.jpg', '/categories/giay-da-banh?brand=adidas', 'TOP', 0, TRUE, '2025-10-20 00:00:00', '2025-10-30 23:59:59'),
('Under Armour Training', 'banner_ua_gym.jpg', '/categories/do-tap-gym', 'MIDDLE', 0, TRUE, '2025-10-01 00:00:00', '2025-10-31 23:59:59'),
('Miễn Phí Vận Chuyển', 'banner_freeship.jpg', '/vouchers', 'BOTTOM', 0, TRUE, '2025-10-01 00:00:00', '2025-10-31 23:59:59'),
('The North Face - Chinh Phục Mùa Đông', 'banner_tnf_winter.jpg', '/categories/ao-khoac?brand=the-north-face', 'TOP', 0, TRUE, '2025-10-25 00:00:00', '2025-11-30 23:59:59'),
('ASICS - Vững Bước Chạy', 'banner_asics_run.jpg', '/categories/giay-chay-bo?brand=asics', 'MIDDLE', 0, TRUE, '2025-10-10 00:00:00', '2025-11-10 23:59:59'),
('Phụ Kiện Thể Thao', 'banner_accessories.jpg', '/categories/phu-kien', 'BOTTOM', 0, TRUE, '2025-10-01 00:00:00', '2025-12-31 23:59:59'),
('Puma Football', 'banner_puma_football.jpg', '/categories/giay-da-banh?brand=puma', 'MIDDLE', 0, TRUE, '2025-10-20 00:00:00', '2025-11-20 23:59:59'),
('New Balance 550', 'banner_nb_550.jpg', '/products/new-balance-550', 'TOP', 0, TRUE, '2025-10-22 00:00:00', '2025-11-05 23:59:59'),
('Decathlon - Giá Tốt Mỗi Ngày', 'banner_decathlon.jpg', '/brands/decathlon', 'BOTTOM', 0, TRUE, '2025-10-01 00:00:00', '2025-12-31 23:59:59');

/*
=====================================================
BẢNG TIER 2 (PHỤ THUỘC TIER 1)
=====================================================
*/

-- UserProviders (10)
INSERT INTO user_providers (user_id, provider_name, provider_user_id) VALUES
(2, 'google', 'google_user_1001'),
(3, 'facebook', 'fb_user_2002'),
(4, 'google', 'google_user_1003'),
(5, 'facebook', 'fb_user_2004'),
(6, 'google', 'google_user_1005'),
(7, 'google', 'google_user_1006'),
(8, 'facebook', 'fb_user_2007'),
(9, 'google', 'google_user_1008'),
(10, 'google', 'google_user_1009'),
(1, 'google', 'google_user_admin');

-- Addresses (10)
-- Sửa: bỏ city, country (không có trong model Address hiện tại)
INSERT INTO addresses (user_id, street, ward, district, province, is_default) VALUES
(1, '123 Đường Quản Trị', 'Phường Bến Nghé', 'Quận 1', 'Hồ Chí Minh', TRUE),
(2, '456 Đường Lê Lợi', 'Phường Hàng Bạc', 'Quận Hoàn Kiếm', 'Hà Nội', TRUE),
(3, '789 Đường Võ Văn Tần', 'Phường 6', 'Quận 3', 'Hồ Chí Minh', TRUE),
(4, '101 Đường Nguyễn Huệ', 'Phường Hải Châu 1', 'Quận Hải Châu', 'Đà Nẵng', TRUE),
(5, '202 Đường Trần Phú', 'Phường Phước Ninh', 'Quận Hải Châu', 'Đà Nẵng', TRUE),
(6, '303 Đường Hai Bà Trưng', 'Phường Tân Định', 'Quận 1', 'Hồ Chí Minh', TRUE),
(7, '404 Đường Cầu Giấy', 'Phường Dịch Vọng', 'Quận Cầu Giấy', 'Hà Nội', TRUE),
(8, '505 Đường Lạch Tray', 'Phường Lạch Tray', 'Quận Ngô Quyền', 'Hải Phòng', TRUE),
(9, '606 Đường Hùng Vương', 'Phường Thới Bình', 'Quận Ninh Kiều', 'Cần Thơ', TRUE),
(10, '707 Đường 30/4', 'Phường Hưng Lợi', 'Quận Ninh Kiều', 'Cần Thơ', TRUE);

-- Carts (10)
INSERT INTO carts (user_id) VALUES
(1), (2), (3), (4), (5), (6), (7), (8), (9), (10);

-- Orders (10)
-- Đảm bảo đúng thứ tự và trường theo model Order.java
-- Các trường: user_id, subtotal, discount_amount, shipping_fee_original, shipping_discount, shipping_fee, total_amount, address, full_name, phone, note, payment_method, status, created_at, updated_at
INSERT INTO orders (
	user_id, subtotal, discount_amount, shipping_fee_original, shipping_discount, shipping_fee, total_amount, address, full_name, phone, note, payment_method, status, created_at, updated_at
) VALUES
(2, 3200000, 0, 0, 0, 0, 3200000, '456 Đường Lê Lợi, Hàng Bạc, Hoàn Kiếm, Hà Nội', 'Trần Văn An', '0912345678', NULL, 'BANK_TRANSFER', 'DELIVERED', '2025-10-01 10:00:00', '2025-10-01 10:00:00'),
(3, 1586000, 30000, 0, 0, 0, 1556000, '789 Đường Võ Văn Tần, P6, Q3, TPHCM', 'Lê Thị Bình', '0987654321', NULL, 'COD', 'DELIVERED', '2025-10-05 11:00:00', '2025-10-05 11:00:00'),
(4, 765000, 0, 0, 0, 0, 765000, '101 Đường Nguyễn Huệ, Hải Châu 1, Hải Châu, Đà Nẵng', 'Phạm Văn Cường', '0911223344', NULL, 'BANK_TRANSFER', 'SHIPPED', '2025-10-10 14:30:00', '2025-10-10 14:30:00'),
(5, 3800000, 0, 0, 0, 0, 3800000, '202 Đường Trần Phú, Phước Ninh, Hải Châu, Đà Nẵng', 'Võ Thị Dung', '0922334455', NULL, 'BANK_TRANSFER', 'PENDING', '2025-10-15 09:00:00', '2025-10-15 09:00:00'),
(2, 1100000, 110000, 0, 0, 0, 990000, '456 Đường Lê Lợi, Hàng Bạc, Hoàn Kiếm, Hà Nội', 'Trần Văn An', '0912345678', NULL, 'COD', 'CANCELLED', '2025-10-20 16:00:00', '2025-10-20 16:00:00'),
(6, 4800000, 0, 0, 0, 0, 4800000, '303 Đường Hai Bà Trưng, Tân Định, Q1, TPHCM', 'Hoàng Văn Em', '0933445566', NULL, 'BANK_TRANSFER', 'CONFIRMED', '2025-10-22 10:00:00', '2025-10-22 10:00:00'),
(7, 600000, 0, 0, 0, 0, 600000, '404 Đường Cầu Giấy, Dịch Vọng, Cầu Giấy, Hà Nội', 'Đặng Thị Giang', '0944556677', NULL, 'BANK_TRANSFER', 'DELIVERED', '2025-10-23 11:00:00', '2025-10-23 11:00:00'),
(8, 2800000, 0, 0, 0, 0, 2800000, '505 Đường Lạch Tray, Lạch Tray, Ngô Quyền, Hải Phòng', 'Bùi Văn Hải', '0955667788', NULL, 'COD', 'SHIPPED', '2025-10-24 08:00:00', '2025-10-24 08:00:00'),
(9, 600000, 50000, 0, 0, 0, 550000, '606 Đường Hùng Vương, Thới Bình, Ninh Kiều, Cần Thơ', 'Ngô Thị Hương', '0966778899', NULL, 'COD', 'CONFIRMED', '2025-10-25 10:00:00', '2025-10-25 10:00:00'),
(10, 8230000, 0, 0, 0, 0, 8230000, '707 Đường 30/4, Hưng Lợi, Ninh Kiều, Cần Thơ', 'Lý Văn Kiên', '0977889900', NULL, 'BANK_TRANSFER', 'PENDING', '2025-10-25 15:00:00', '2025-10-25 15:00:00');

-- Conversations (10)
-- Thêm: priority để khớp với model Conversation
INSERT INTO conversations (user_id, subject, status, priority) VALUES
(2, 'Hỏi về tình trạng đơn hàng DH000001', 'CLOSED', 'MEDIUM'),
(3, 'Tư vấn chọn size giày', 'OPEN', 'MEDIUM'),
(4, 'Khiếu nại sản phẩm bị lỗi', 'IN_PROGRESS', 'HIGH'),
(5, 'Hỏi về chính sách đổi trả', 'OPEN', 'MEDIUM'),
(2, 'Hủy đơn hàng DH000005', 'CLOSED', 'HIGH'),
(6, 'Hỏi về voucher FREESHIP', 'CLOSED', 'LOW'),
(7, 'Sản phẩm giao không đúng màu', 'IN_PROGRESS', 'HIGH'),
(8, 'Hỏi về thời gian giao hàng', 'OPEN', 'MEDIUM'),
(9, 'Tư vấn mua đồ tập gym', 'OPEN', 'LOW'),
(10, 'Hỏi về chương trình Flash Sale', 'OPEN', 'MEDIUM');

/*
=====================================================
PRODUCTS (100 SẢN PHẨM)
Tạo 10 sản phẩm cho mỗi thương hiệu (10 thương hiệu)
Đã bỏ: stock, total_product, sku, slug, status, discount_price (GENERATED COLUMN)
=====================================================
*/
INSERT INTO products (name, description, price, cost_price, discount_percent, brand_id, sold, sku, created_at) VALUES
-- Nike (1-10)
('Nike Air Zoom Pegasus 40', 'Giày chạy bộ huyền thoại, êm ái và ổn định.', 3200000, NULL, 0, 1, 0, 'SP1', '2025-09-15 10:00:00'),
('Nike Mercurial Superfly 9', 'Giày đá banh tốc độ, thiết kế cho sân cỏ tự nhiên.', 4500000, NULL, 0, 1, 0, 'SP2', '2025-09-20 14:30:00'),
('Áo Thun Nike Dri-FIT Miller', 'Áo thun chạy bộ, công nghệ Dri-FIT thoáng khí.', 850000, NULL, 10, 1, 0, 'SP3', '2025-10-01 09:15:00'),
('Quần Short Nike Challenger', 'Quần short chạy bộ 2 lớp, có túi đựng điện thoại.', 1100000, NULL, 0, 1, 0, 'SP4', '2025-10-05 11:20:00'),
('Quần Legging Nike Pro', 'Quần legging tập luyện, co giãn và hỗ trợ cơ bắp.', 1300000, NULL, 0, 1, 0, 'SP5', '2025-10-10 16:45:00'),
('Áo Khoác Nike Windrunner', 'Áo khoác gió, chống nước nhẹ, thiết kế cổ điển.', 2500000, NULL, 15, 1, 0, 'SP6', '2025-10-12 13:30:00'),
('Áo Bra Nike Swoosh', 'Áo lót thể thao hỗ trợ vừa, thoải mái.', 900000, NULL, 0, 1, 0, 'SP7', '2025-10-15 10:00:00'),
('Tất Nike Everyday Plus', 'Tất thể thao, đệm êm, thấm hút mồ hôi (bộ 3 đôi).', 450000, NULL, 0, 1, 0, 'SP8', '2025-10-18 15:20:00'),
('Balo Nike Brasilia', 'Balo thể thao, dung tích lớn, nhiều ngăn.', 1200000, NULL, 0, 1, 0, 'SP9', '2025-10-20 12:00:00'),
('Bóng Rổ Nike Everyday', 'Bóng rổ size 7, chất liệu cao su bền bỉ.', 700000, NULL, 0, 1, 0, 'SP10', '2025-10-22 14:15:00'),

-- Adidas (11-20)
('Adidas Ultraboost Light', 'Giày chạy bộ với đệm Boost nhẹ nhất từ trước đến nay.', 4800000, NULL, 0, 2, 0, 'SP11', '2025-09-18 11:00:00'),
('Adidas Predator Accuracy', 'Giày đá banh kiểm soát, upper công nghệ mới.', 5200000, NULL, 0, 2, 0, 'SP12', '2025-09-25 15:30:00'),
('Áo Thun Adidas Own The Run', 'Áo thun chạy bộ, chất liệu tái chế, thoáng mát.', 950000, NULL, 12, 2, 0, 'SP13', '2025-10-02 10:45:00'),
('Quần Short Adidas 3-Stripes', 'Quần short thể thao, phong cách 3 sọc cổ điển.', 750000, NULL, 0, 2, 0, 'SP14', '2025-10-06 12:30:00'),
('Quần Dài Adidas Tiro 23', 'Quần dài thể thao, dáng ôm, khóa kéo ở cổ chân.', 1400000, NULL, 0, 2, 0, 'SP15', '2025-10-08 14:00:00'),
('Áo Khoác Nỉ Adidas Essentials', 'Áo khoác nỉ 3 sọc, giữ ấm tốt.', 1800000, NULL, 20, 2, 0, 'SP16', '2025-10-11 10:30:00'),
('Áo Bra Tập Luyện Adidas TLRD', 'Áo lót thể thao hỗ trợ cao, tùy chỉnh.', 1100000, NULL, 0, 2, 0, 'SP17', '2025-10-14 09:00:00'),
('Mũ Adidas Baseball', 'Mũ lưỡi trai, logo thêu, quai điều chỉnh.', 550000, NULL, 0, 2, 0, 'SP18', '2025-10-17 15:45:00'),
('Túi Trống Adidas Defender', 'Túi trống tập gym, size M, chống nước.', 1300000, NULL, 0, 2, 0, 'SP19', '2025-10-19 11:15:00'),
('Bóng Đá Adidas Al Rihla', 'Bóng đá thi đấu chính thức World Cup 2022.', 3500000, NULL, 0, 2, 0, 'SP20', '2025-10-21 16:20:00'),

-- Puma (21-30)
('Puma Velocity Nitro 2', 'Giày chạy bộ, đệm Nitro Foam êm ái.', 2800000, NULL, 0, 3, 0, 'SP21', '2025-09-22 10:00:00'),
('Puma Future Ultimate', 'Giày đá banh linh hoạt, không dây.', 4300000, NULL, 5, 3, 0, 'SP22', '2025-09-28 14:30:00'),
('Áo Thun Puma Essentials Logo', 'Áo thun cotton, logo Puma lớn trước ngực.', 600000, NULL, 0, 3, 0, 'SP23', '2025-10-03 09:30:00'),
('Quần Short Puma Fit Woven', 'Quần short tập luyện, vải dệt nhẹ, co giãn.', 800000, NULL, 0, 3, 0, 'SP24', '2025-10-07 13:15:00'),
('Quần Jogger Puma T7', 'Quần jogger, viền T7 cổ điển 2 bên.', 1200000, NULL, 0, 3, 0, 'SP25', '2025-10-09 11:45:00'),
('Áo Khoác Puma Iconic T7', 'Áo khoác thể thao, phong cách T7 cổ điển.', 1700000, NULL, 10, 3, 0, 'SP26', '2025-10-13 14:30:00'),
('Đồ Tập Gym Puma Fit', 'Bộ đồ tập gym nữ, chất liệu co giãn.', 1500000, NULL, 0, 3, 0, 'SP27', '2025-10-16 10:20:00'),
('Tất Puma Quarter (3 đôi)', 'Tất cổ ngắn, chất liệu cotton.', 300000, NULL, 0, 3, 0, 'SP28', '2025-10-19 16:00:00'),
('Balo Puma Phase', 'Balo đi học, đi làm, thiết kế đơn giản.', 700000, NULL, 0, 3, 0, 'SP29', '2025-10-22 12:45:00'),
('Thảm Tập Yoga Puma', 'Thảm tập yoga, chống trơn trượt.', 900000, NULL, 0, 3, 0, 'SP30', '2025-10-24 15:30:00'),

-- Under Armour (31-40)
('UA HOVR Phantom 3', 'Giày chạy bộ thông minh, kết nối app.', 3800000, NULL, 0, 4, 0, 'SP31', '2025-09-25 11:30:00'),
('Giày Đá Banh UA Clone Magnetico', 'Giày đá banh, upper ôm sát như lớp da thứ 2.', 4100000, NULL, 8, 4, 0, 'SP32', '2025-09-30 15:00:00'),
('Áo Thun UA HeatGear', 'Áo thun nén cơ, giữ mát và khô ráo.', 900000, NULL, 0, 4, 0, 'SP33', '2025-10-04 10:00:00'),
('Quần Short UA Launch Run', 'Quần short chạy bộ, siêu nhẹ, có túi.', 1000000, NULL, 0, 4, 0, 'SP34', '2025-10-08 13:45:00'),
('Quần Legging UA RUSH', 'Quần legging, công nghệ RUSH hoàn trả năng lượng.', 1800000, NULL, 15, 4, 0, 'SP35', '2025-10-11 09:30:00'),
('Áo Khoác UA Storm', 'Áo khoác chống nước, chống gió, siêu nhẹ.', 2800000, NULL, 0, 4, 0, 'SP36', '2025-10-14 14:15:00'),
('Áo Bra UA Infinity High', 'Áo lót thể thao hỗ trợ cao, đệm mút liền.', 1500000, NULL, 0, 4, 0, 'SP37', '2025-10-17 11:00:00'),
('Mũ UA Iso-Chill', 'Mũ tập luyện, công nghệ làm mát Iso-Chill.', 750000, NULL, 0, 4, 0, 'SP38', '2025-10-20 16:30:00'),
('Túi Trống UA Undeniable 5.0', 'Túi trống, công nghệ Storm chống nước.', 1400000, NULL, 0, 4, 0, 'SP39', '2025-10-23 12:20:00'),
('Găng Tay Tập Gym UA', 'Găng tay tập tạ, đệm êm, bám dính tốt.', 600000, NULL, 0, 4, 0, 'SP40', '2025-10-25 15:45:00'),

-- ASICS (41-50)
('ASICS GEL-Kayano 30', 'Giày chạy bộ ổn định, công nghệ GEL êm ái.', 4200000, NULL, 12, 5, 0, 'SP41', '2025-09-16 12:00:00'),
('ASICS GEL-Nimbus 25', 'Giày chạy bộ siêu êm, đệm dày.', 4000000, NULL, 0, 5, 0, 'SP42', '2025-09-21 15:30:00'),
('Áo Thun ASICS Ventilate', 'Áo thun chạy bộ, siêu thoáng khí.', 1000000, NULL, 0, 5, 0, 'SP43', '2025-10-01 10:45:00'),
('Quần Short ASICS Road 2-in-1', 'Quần short chạy bộ 2 lớp, có túi gel.', 1200000, NULL, 0, 5, 0, 'SP44', '2025-10-05 14:00:00'),
('Quần Legging ASICS Lite-Show', 'Quần legging chạy tối, phản quang 360 độ.', 1500000, NULL, 18, 5, 0, 'SP45', '2025-10-10 12:30:00'),
('Áo Khoác ASICS Accelerate', 'Áo khoác chạy bộ, chống gió, chống mưa nhẹ.', 2200000, NULL, 0, 5, 0, 'SP46', '2025-10-13 09:45:00'),
('Áo Bra ASICS Sakura', 'Áo lót thể thao, thiết kế hoa anh đào.', 950000, NULL, 0, 5, 0, 'SP47', '2025-10-16 11:20:00'),
('Tất Chạy Bộ ASICS (3 đôi)', 'Tất chạy bộ, mỏng nhẹ, chống phồng rộp.', 500000, NULL, 0, 5, 0, 'SP48', '2025-10-19 14:50:00'),
('Balo Chạy Bộ ASICS', 'Balo/vest chạy bộ, đựng nước và gel.', 1800000, NULL, 0, 5, 0, 'SP49', '2025-10-22 10:30:00'),
('Băng Đô ASICS', 'Băng đô thể thao, thấm hút mồ hôi.', 300000, NULL, 0, 5, 0, 'SP50', '2025-10-24 16:15:00'),

-- New Balance (51-60)
('New Balance Fresh Foam X 1080v13', 'Giày chạy bộ, đệm Fresh Foam X siêu êm.', 3900000, NULL, 0, 6, 0, 'SP51', '2025-09-19 13:00:00'),
('New Balance 550', 'Giày thể thao thời trang, phong cách bóng rổ cổ điển.', 2800000, NULL, 10, 6, 0, 'SP52', '2025-09-24 16:00:00'),
('Áo Thun NB Essentials', 'Áo thun cotton, logo NB cổ điển.', 700000, NULL, 0, 6, 0, 'SP53', '2025-10-02 11:30:00'),
('Quần Short NB Accelerate', 'Quần short chạy bộ, chất liệu nhẹ, khô nhanh.', 850000, NULL, 0, 6, 0, 'SP54', '2025-10-06 14:45:00'),
('Quần Jogger NB Athletics', 'Quần jogger nỉ, thời trang, thoải mái.', 1400000, NULL, 0, 6, 0, 'SP55', '2025-10-09 10:15:00'),
('Áo Khoác Gió NB Impact Run', 'Áo khoác chạy bộ, siêu mỏng, gấp gọn được.', 2100000, NULL, 15, 6, 0, 'SP56', '2025-10-12 15:00:00'),
('Đồ Tập Gym NB Relentless', 'Bộ đồ tập gym nữ, thiết kế thời trang.', 1600000, NULL, 0, 6, 0, 'SP57', '2025-10-15 12:30:00'),
('Mũ NB Classic', 'Mũ lưỡi trai, logo NB.', 500000, NULL, 0, 6, 0, 'SP58', '2025-10-18 09:45:00'),
('Túi Đeo Chéo NB', 'Túi đeo chéo nhỏ gọn, tiện lợi.', 650000, NULL, 0, 6, 0, 'SP59', '2025-10-21 14:20:00'),
('Giày NB 574', 'Giày thời trang, thiết kế 574 huyền thoại.', 2300000, NULL, 20, 6, 0, 'SP60', '2025-10-23 16:00:00'),

-- Reebok (61-70)
('Reebok Nano X4', 'Giày tập gym, CrossFit, đế ổn định.', 3300000, NULL, 0, 7, 0, 'SP61', '2025-09-17 10:30:00'),
('Reebok Classic Leather', 'Giày thời trang, da thật, phong cách cổ điển.', 2100000, NULL, 0, 7, 0, 'SP62', '2025-09-23 14:00:00'),
('Áo Thun Reebok CrossFit', 'Áo thun chuyên dụng cho CrossFit.', 800000, NULL, 0, 7, 0, 'SP63', '2025-10-01 11:15:00'),
('Quần Short Reebok Speedwick', 'Quần short tập luyện, thấm hút mồ hôi.', 900000, NULL, 10, 7, 0, 'SP64', '2025-10-04 15:30:00'),
('Quần Legging Reebok Lux', 'Quần legging cao cấp, mềm mại, co giãn tốt.', 1300000, NULL, 0, 7, 0, 'SP65', '2025-10-08 12:00:00'),
('Áo Hoodie Reebok Classic', 'Áo hoodie nỉ, logo vector lớn.', 1600000, NULL, 25, 7, 0, 'SP66', '2025-10-11 14:45:00'),
('Áo Bra Reebok Lux High-Support', 'Áo lót thể thao hỗ trợ cao, cho CrossFit.', 1100000, NULL, 0, 7, 0, 'SP67', '2025-10-14 10:30:00'),
('Tất Reebok Classic (3 đôi)', 'Tất cổ cao, logo Reebok.', 350000, NULL, 0, 7, 0, 'SP68', '2025-10-17 16:15:00'),
('Balo Reebok Training', 'Balo tập luyện, ngăn đựng giày riêng.', 1100000, NULL, 0, 7, 0, 'SP69', '2025-10-20 13:00:00'),
('Dây Nhảy Reebok', 'Dây nhảy tốc độ, tay cầm bọc đệm.', 400000, NULL, 0, 7, 0, 'SP70', '2025-10-22 15:45:00'),

-- The North Face (71-80)
('Giày Leo Núi TNF VECTIV', 'Giày leo núi, công nghệ VECTIV trợ lực.', 4500000, NULL, 0, 8, 0, 'SP71', '2025-09-20 12:30:00'),
('Áo Khoác TNF Resolve 2', 'Áo khoác chống nước, công nghệ DryVent 2L.', 2900000, NULL, 20, 8, 0, 'SP72', '2025-09-26 15:00:00'),
('Áo Thun TNF Simple Dome', 'Áo thun cotton, logo TNF nhỏ.', 750000, NULL, 0, 8, 0, 'SP73', '2025-10-02 09:45:00'),
('Quần Short TNF Movmynt', 'Quần short chạy trail, siêu nhẹ.', 1300000, NULL, 0, 8, 0, 'SP74', '2025-10-05 13:30:00'),
('Quần Dài TNF Paramount', 'Quần dã ngoại, tháo ống thành quần short.', 2200000, NULL, 0, 8, 0, 'SP75', '2025-10-09 11:00:00'),
('Áo Khoác Lông Vũ TNF Nuptse', 'Áo khoác lông vũ, giữ ấm, kiểu dáng retro.', 7500000, NULL, 0, 8, 0, 'SP76', '2025-10-12 14:15:00'),
('Áo Nỉ TNF Glacier', 'Áo nỉ mỏng, giữ ấm, mặc lót.', 1500000, NULL, 0, 8, 0, 'SP77', '2025-10-15 10:45:00'),
('Mũ Nồi TNF', 'Mũ len giữ ấm, logo thêu.', 600000, NULL, 0, 8, 0, 'SP78', '2025-10-18 16:30:00'),
('Balo TNF Borealis', 'Balo đa năng, đi học, đi làm, đi phượt.', 2600000, NULL, 15, 8, 0, 'SP79', '2025-10-21 12:15:00'),
('Găng Tay TNF Etip', 'Găng tay nỉ, cảm ứng điện thoại được.', 900000, NULL, 0, 8, 0, 'SP80', '2025-10-23 14:45:00'),

-- Columbia (81-90)
('Giày Lội Nước Columbia Drainmaker', 'Giày lội nước, đi biển, thoát nước nhanh.', 2400000, NULL, 0, 9, 0, 'SP81', '2025-09-21 11:00:00'),
('Áo Khoác Columbia Watertight II', 'Áo khoác chống nước, công nghệ Omni-Tech.', 2300000, NULL, 0, 9, 0, 'SP82', '2025-09-27 14:30:00'),
('Áo Thun Columbia PFG', 'Áo thun câu cá, chống tia UV.', 900000, NULL, 0, 9, 0, 'SP83', '2025-10-02 10:15:00'),
('Quần Short Columbia Silver Ridge', 'Quần short dã ngoại, vải mỏng nhẹ.', 1100000, NULL, 0, 9, 0, 'SP84', '2025-10-05 13:45:00'),
('Quần Dài Columbia Omni-Heat', 'Quần giữ nhiệt, công nghệ phản nhiệt Omni-Heat.', 1900000, NULL, 0, 9, 0, 'SP85', '2025-10-09 11:30:00'),
('Áo Sơ Mi Columbia Bahama', 'Áo sơ mi dã ngoại, câu cá, nhanh khô.', 1300000, NULL, 10, 9, 0, 'SP86', '2025-10-12 14:45:00'),
('Áo Nỉ Columbia Steens Mountain', 'Áo nỉ dày, giữ ấm tốt.', 1200000, NULL, 0, 9, 0, 'SP87', '2025-10-15 11:15:00'),
('Mũ Rộng Vành Columbia Bora Bora', 'Mũ dã ngoại, chống UV, thoáng khí.', 800000, NULL, 0, 9, 0, 'SP88', '2025-10-18 15:00:00'),
('Balo Columbia Newton Ridge', 'Balo dã ngoại, dung tích 25L.', 1700000, NULL, 0, 9, 0, 'SP89', '2025-10-21 12:45:00'),
('Khăn Đa Năng Columbia', 'Khăn ống đa năng, chống nắng.', 400000, NULL, 0, 9, 0, 'SP90', '2025-10-23 15:30:00'),

-- Decathlon (Quechua/Kipsta/Domyos) (91-100)
('Giày Chạy Bộ Kiprun KD900X', 'Giày chạy bộ thi đấu, đệm carbon.', 3000000, NULL, 0, 10, 0, 'SP91', '2025-09-18 12:15:00'),
('Giày Đá Banh Kipsta Viralto IV', 'Giày đá banh da thật, giá tốt.', 1500000, NULL, 0, 10, 0, 'SP92', '2025-09-24 15:45:00'),
('Áo Thun Domyos Fit', 'Áo thun tập gym, giá rẻ, thoáng mát.', 150000, NULL, 0, 10, 0, 'SP93', '2025-10-01 09:00:00'),
('Quần Short Domyos 2-in-1', 'Quần short tập gym, có lớp lót.', 350000, NULL, 0, 10, 0, 'SP94', '2025-10-04 12:30:00'),
('Quần Legging Domyos Nữ', 'Quần legging tập yoga, co giãn.', 250000, NULL, 0, 10, 0, 'SP95', '2025-10-07 10:45:00'),
('Áo Khoác Gió Quechua MH100', 'Áo khoác gió dã ngoại, chống nước nhẹ.', 400000, NULL, 0, 10, 0, 'SP96', '2025-10-10 14:15:00'),
('Áo Bra Domyos', 'Áo lót thể thao, hỗ trợ vừa.', 200000, NULL, 0, 10, 0, 'SP97', '2025-10-13 11:30:00'),
('Tất Chạy Bộ Kiprun (2 đôi)', 'Tất chạy bộ, chống phồng rộp.', 180000, NULL, 0, 10, 0, 'SP98', '2025-10-16 15:45:00'),
('Balo Quechua NH100 (20L)', 'Balo dã ngoại, đi học, giá rẻ.', 250000, NULL, 0, 10, 0, 'SP99', '2025-10-19 12:30:00'),
('Thảm Tập Yoga Domyos 8mm', 'Thảm tập yoga, dày 8mm, êm ái.', 500000, NULL, 0, 10, 0, 'SP100', '2025-10-22 16:00:00');


/*
=====================================================
BẢNG TIER 3 (PHỤ THUỘC SẢN PHẨM, USER...)
=====================================================
*/

-- Wishlists (10 mục yêu thích từ các user)
-- Model Wishlist đã thay đổi: giờ lưu trực tiếp user_id và product_id
-- QUAN TRỌNG: Phải insert SAU khi đã có Products
INSERT INTO wishlists (user_id, product_id) VALUES
(2, 11), -- User 2 thích SP 11 (Adidas Ultraboost)
(3, 72), -- User 3 thích SP 72 (Áo khoác TNF)
(4, 76), -- User 4 thích SP 76 (Áo lông vũ TNF)
(5, 61), -- User 5 thích SP 61 (Giày Reebok Nano)
(6, 5),  -- User 6 thích SP 5 (Quần Legging Nike Pro)
(7, 10), -- User 7 thích SP 10 (Bóng rổ Nike)
(8, 20), -- User 8 thích SP 20 (Bóng đá Adidas)
(9, 30), -- User 9 thích SP 30 (Thảm tập yoga Puma)
(10, 40), -- User 10 thích SP 40 (Găng tay UA)
(1, 50); -- User 1 (Admin) thích SP 50 (Băng đô ASICS)

-- ProductCategories (Liên kết 100 sản phẩm với 10 danh mục)
-- 10 sản phẩm đầu (1-10) là của Nike, 10 sản phẩm sau (11-20) là của Adidas...
-- 10 danh mục (1-10) là Giày chạy, Giày đá banh, Áo thun...
-- -> Sản phẩm 1, 11, 21, 31, 41, 51, 61, 71, 81, 91 là "Giày Chạy Bộ" (hoặc tương tự)
-- -> Sản phẩm 2, 12, 22, 32, 42, 52, 62, 72, 82, 92 là "Giày Đá Banh" (hoặc tương tự)
INSERT INTO product_categories (product_id, category_id, created_at) VALUES
(1, 1, NOW()), (2, 2, NOW()), (3, 3, NOW()), (4, 4, NOW()), (5, 5, NOW()), (6, 6, NOW()), (7, 7, NOW()), (8, 8, NOW()), (9, 9, NOW()), (10, 10, NOW()),
(11, 1, NOW()), (12, 2, NOW()), (13, 3, NOW()), (14, 4, NOW()), (15, 5, NOW()), (16, 6, NOW()), (17, 7, NOW()), (18, 8, NOW()), (19, 9, NOW()), (20, 10, NOW()),
(21, 1, NOW()), (22, 2, NOW()), (23, 3, NOW()), (24, 4, NOW()), (25, 5, NOW()), (26, 6, NOW()), (27, 7, NOW()), (28, 8, NOW()), (29, 9, NOW()), (30, 10, NOW()),
(31, 1, NOW()), (32, 2, NOW()), (33, 3, NOW()), (34, 4, NOW()), (35, 5, NOW()), (36, 6, NOW()), (37, 7, NOW()), (38, 8, NOW()), (39, 9, NOW()), (40, 10, NOW()),
(41, 1, NOW()), (42, 1, NOW()), (43, 3, NOW()), (44, 4, NOW()), (45, 5, NOW()), (46, 6, NOW()), (47, 7, NOW()), (48, 8, NOW()), (49, 9, NOW()), (50, 8, NOW()),
(51, 1, NOW()), (52, 1, NOW()), (53, 3, NOW()), (54, 4, NOW()), (55, 5, NOW()), (56, 6, NOW()), (57, 7, NOW()), (58, 8, NOW()), (59, 9, NOW()), (60, 1, NOW()),
(61, 7, NOW()), (62, 1, NOW()), (63, 3, NOW()), (64, 4, NOW()), (65, 5, NOW()), (66, 6, NOW()), (67, 7, NOW()), (68, 8, NOW()), (69, 9, NOW()), (70, 10, NOW()),
(71, 1, NOW()), (72, 6, NOW()), (73, 3, NOW()), (74, 4, NOW()), (75, 5, NOW()), (76, 6, NOW()), (77, 6, NOW()), (78, 8, NOW()), (79, 9, NOW()), (80, 8, NOW()),
(81, 1, NOW()), (82, 6, NOW()), (83, 3, NOW()), (84, 4, NOW()), (85, 5, NOW()), (86, 3, NOW()), (87, 6, NOW()), (88, 8, NOW()), (89, 9, NOW()), (90, 8, NOW()),
(91, 1, NOW()), (92, 2, NOW()), (93, 3, NOW()), (94, 4, NOW()), (95, 5, NOW()), (96, 6, NOW()), (97, 7, NOW()), (98, 8, NOW()), (99, 9, NOW()), (100, 10, NOW());

-- ProductLabels (Gán nhãn cho 20 sản phẩm đầu tiên)
INSERT INTO product_labels (product_id, label_id, created_at) VALUES
(1, 1, NOW()), (1, 2, NOW()), (1, 8, NOW()), (11, 1, NOW()), (11, 2, NOW()), (21, 1, NOW()), (31, 8, NOW()), (41, 2, NOW()), (51, 1, NOW()), (61, 2, NOW()), (71, 7, NOW()),
(2, 1, NOW()), (2, 3, NOW()), (12, 1, NOW()), (22, 1, NOW()), (32, 1, NOW()), (92, 3, NOW()), (10, 2, NOW()), (20, 2, NOW()), (30, 2, NOW()), (40, 2, NOW()), (50, 2, NOW()), (60, 2, NOW());

-- ProductVariants (Tạo biến thể cho 10 sản phẩm đầu: 5 áo/quần, 5 giày)
-- Áo/Quần (ID 3, 4, 5, 6, 7): Size S, M, L (ID 1, 2, 3) + Màu Đen, Trắng (ID 1, 2)
-- Giày (ID 1, 2): Size 40, 41, 42 (ID 7, 8, 9) + Màu Đen, Xám (ID 1, 5)
-- Đã bỏ: sku, price (giá lấy từ bảng products)
INSERT INTO product_variants (product_id, color_id, size_id, stock) VALUES
-- SP 1 (Giày Nike Peg40): Đen(1)+40(7), Đen(1)+41(8), Đen(1)+42(9), Xám(5)+40(7), Xám(5)+41(8), Xám(5)+42(9)
(1, 1, 7, 20), (1, 1, 8, 20), (1, 1, 9, 20),
(1, 5, 7, 15), (1, 5, 8, 15), (1, 5, 9, 15),
-- SP 2 (Giày Nike Merc9): Đỏ(3)+40(7), Đỏ(3)+41(8), Đỏ(3)+42(9), Vàng(8)+40(7), Vàng(8)+41(8), Vàng(8)+42(9)
(2, 3, 7, 10), (2, 3, 8, 10), (2, 3, 9, 10),
(2, 8, 7, 8), (2, 8, 8, 8), (2, 8, 9, 8),
-- SP 3 (Áo Nike Miller): Đen(1)+S(1), Đen(1)+M(2), Đen(1)+L(3), Trắng(2)+S(1), Trắng(2)+M(2), Trắng(2)+L(3)
(3, 1, 1, 30), (3, 1, 2, 30), (3, 1, 3, 30),
(3, 2, 1, 25), (3, 2, 2, 25), (3, 2, 3, 25),
-- SP 4 (Quần Nike Chal): Đen(1)+S(1), Đen(1)+M(2), Đen(1)+L(3), Xám(5)+S(1), Xám(5)+M(2), Xám(5)+L(3)
(4, 1, 1, 20), (4, 1, 2, 20), (4, 1, 3, 20),
(4, 5, 1, 15), (4, 5, 2, 15), (4, 5, 3, 15),
-- SP 11 (Giày Adidas Ultra): Trắng(2)+40(7), Trắng(2)+41(8), Trắng(2)+42(9), Xanh(4)+40(7), Xanh(4)+41(8), Xanh(4)+42(9)
(11, 2, 7, 20), (11, 2, 8, 20), (11, 2, 9, 20),
(11, 4, 7, 15), (11, 4, 8, 15), (11, 4, 9, 15),
-- SP 13 (Áo Adidas OTR): Đen(1)+S(1), Đen(1)+M(2), Đen(1)+L(3), Cam(7)+S(1), Cam(7)+M(2), Cam(7)+L(3)
(13, 1, 1, 30), (13, 1, 2, 30), (13, 1, 3, 30),
(13, 7, 1, 20), (13, 7, 2, 20), (13, 7, 3, 20),
-- SP 14 (Quần Adidas 3S): Đen(1)+S(1), Đen(1)+M(2), Đen(1)+L(3)
(14, 1, 1, 50), (14, 1, 2, 50), (14, 1, 3, 50),
-- SP 21 (Giày Puma Velo): Đen(1)+40(7), Đen(1)+41(8), Xanh(4)+41(8)
(21, 1, 7, 10), (21, 1, 8, 10), (21, 4, 8, 10),
-- SP 23 (Áo Puma Ess): Trắng(2)+S(1), Trắng(2)+M(2), Trắng(2)+L(3)
(23, 2, 1, 40), (23, 2, 2, 40), (23, 2, 3, 40),
-- SP 31 (Giày UA HOVR): Xám(5)+40(7), Xám(5)+41(8), Xám(5)+42(9)
(31, 5, 7, 15), (31, 5, 8, 15), (31, 5, 9, 15);


-- ProductImages (Thêm 2 ảnh cho 10 sản phẩm đầu)
INSERT INTO product_images (product_id, image_url, alt_text) VALUES
(1, 'public/product_image/nike_peg40_1.jpg', 'Nike Pegasus 40 - Ảnh 1'), (1, 'public/product_image/nike_peg40_2.jpg', 'Nike Pegasus 40 - Ảnh 2'),
(2, 'public/product_image/nike_merc9_1.jpg', 'Nike Mercurial 9 - Ảnh 1'), (2, 'public/product_image/nike_merc9_2.jpg', 'Nike Mercurial 9 - Ảnh 2'),
(3, 'public/product_image/nike_miller_1.jpg', 'Áo Nike Miller - Ảnh 1'), (3, 'public/product_image/nike_miller_2.jpg', 'Áo Nike Miller - Ảnh 2'),
(4, 'public/product_image/nike_chal_1.jpg', 'Quần Nike Challenger - Ảnh 1'), (4, 'public/product_image/nike_chal_2.jpg', 'Quần Nike Challenger - Ảnh 2'),
(5, 'public/product_image/nike_pro_1.jpg', 'Quần Legging Nike Pro - Ảnh 1'), (5, 'public/product_image/nike_pro_2.jpg', 'Quần Legging Nike Pro - Ảnh 2'),
(11, 'public/product_image/adi_ublight_1.jpg', 'Adidas Ultraboost Light - Ảnh 1'), (11, 'public/product_image/adi_ublight_2.jpg', 'Adidas Ultraboost Light - Ảnh 2'),
(12, 'public/product_image/adi_pred_1.jpg', 'Adidas Predator - Ảnh 1'), (12, 'public/product_image/adi_pred_2.jpg', 'Adidas Predator - Ảnh 2'),
(13, 'public/product_image/adi_otr_1.jpg', 'Áo Adidas OTR - Ảnh 1'), (13, 'public/product_image/adi_otr_2.jpg', 'Áo Adidas OTR - Ảnh 2'),
(21, 'public/product_image/puma_velo_1.jpg', 'Puma Velocity - Ảnh 1'), (21, 'public/product_image/puma_velo_2.jpg', 'Puma Velocity - Ảnh 2'),
(31, 'public/product_image/ua_hovr_1.jpg', 'UA HOVR Phantom 3 - Ảnh 1'), (31, 'public/product_image/ua_hovr_2.jpg', 'UA HOVR Phantom 3 - Ảnh 2');

-- FlashSaleProducts (Thêm 10 sản phẩm vào Flash Sale 7: "Running Day")
INSERT INTO flash_sale_products (flash_sale_id, product_id, flash_sale_price, quantity) VALUES
(7, 1, 2900000, 20),
(7, 11, 4200000, 20),
(7, 21, 2500000, 15),
(7, 31, 3300000, 15),
(7, 41, 3800000, 20),
(7, 51, 3500000, 10),
(7, 3, 700000, 50),
(7, 13, 800000, 50),
(7, 4, 900000, 30),
(7, 44, 1000000, 30);

-- Reviews (10 đánh giá từ 10 user cho 10 sản phẩm)
-- Thêm: is_verified, helpful, unhelpful để khớp với model Review
INSERT INTO reviews (user_id, product_id, rating, title, content, is_verified, helpful, unhelpful, status) VALUES
(2, 1, 5, 'Tuyệt vời!', 'Giày êm, chạy rất thích. Giao hàng nhanh.', FALSE, 0, 0, 'APPROVED'),
(3, 11, 5, 'Rất hài lòng', 'Đúng là Ultraboost, nhẹ và êm, đáng tiền.', FALSE, 0, 0, 'APPROVED'),
(4, 21, 4, 'Khá tốt', 'Giày đẹp, đệm êm, nhưng form hơi nhỏ.', FALSE, 0, 0, 'APPROVED'),
(5, 31, 5, 'Xuất sắc!', 'Kết nối app rất hay, giày chạy nảy.', FALSE, 0, 0, 'APPROVED'),
(6, 41, 5, 'Đỉnh cao ổn định', 'Mình bị lật cổ chân, đi Kayano rất tự tin.', FALSE, 0, 0, 'APPROVED'),
(7, 51, 4, 'Êm, thời trang', 'Giày êm, đi hàng ngày cũng đẹp. Hơi nóng.', FALSE, 0, 0, 'PENDING'),
(8, 2, 5, 'Đá sướng', 'Giày ôm chân, sút bóng cảm giác tốt.', FALSE, 0, 0, 'APPROVED'),
(9, 3, 4, 'Vải mỏng mát', 'Áo mặc tập gym thoáng, nhưng hơi đắt.', FALSE, 0, 0, 'APPROVED'),
(10, 13, 5, 'Màu đẹp', 'Áo màu cam nổi bật, vải mịn, thấm mồ hôi tốt.', FALSE, 0, 0, 'REJECTED'),
(2, 4, 3, 'Bình thường', 'Quần cũng được, túi hơi bé.', FALSE, 0, 0, 'APPROVED');

-- UserVouchers (Gán voucher cho user)
INSERT INTO user_vouchers (user_id, voucher_id) VALUES
(2, 3), (3, 3), (4, 1), (5, 2), (6, 1), (7, 4), (8, 2), (9, 5), (10, 9), (1, 1);

-- OrderItems (Chi tiết cho 10 đơn hàng)
-- Order 1 (User 2): Mua SP 1 (Giày Nike) - Variant 1 (Đen, 40)
INSERT INTO order_items (order_id, product_variant_id, quantity, unit_price, total_price) VALUES
(1, 1, 1, 3200000, 3200000);
-- Order 2 (User 3): Mua SP 13 (Áo Adidas) - Variant 31, SP 14 (Quần Adidas) - Variant 37
INSERT INTO order_items (order_id, product_variant_id, quantity, unit_price, total_price) VALUES
(2, 31, 1, 836000, 836000),
(2, 37, 1, 750000, 750000);
-- Order 3 (User 4): Mua SP 3 (Áo Nike Miller) - Variant 13 (Đen, S)
INSERT INTO order_items (order_id, product_variant_id, quantity, unit_price, total_price) VALUES
(3, 13, 1, 765000, 765000);
-- Order 4 (User 5): Mua SP 31 (Giày UA HOVR) - Variant 46 (Xám, 40)
INSERT INTO order_items (order_id, product_variant_id, quantity, unit_price, total_price) VALUES
(4, 46, 1, 3800000, 3800000);
-- Order 5 (User 2): Mua SP 4 (Quần Nike Challenger) - Variant 19 (Đen, S)
INSERT INTO order_items (order_id, product_variant_id, quantity, unit_price, total_price) VALUES
(5, 19, 1, 1100000, 1100000);
-- Order 6 (User 6): Mua SP 11 (Giày Adidas Ultraboost) - Variant 25 (Trắng, 40)
INSERT INTO order_items (order_id, product_variant_id, quantity, unit_price, total_price) VALUES
(6, 25, 1, 4800000, 4800000);
-- Order 7 (User 7): Mua SP 23 (Áo Puma Ess) - Variant 43 (Trắng, S)
INSERT INTO order_items (order_id, product_variant_id, quantity, unit_price, total_price) VALUES
(7, 43, 1, 600000, 600000);
-- Order 8 (User 8): Mua SP 21 (Giày Puma Velocity) - Variant 40 (Đen, 40)
INSERT INTO order_items (order_id, product_variant_id, quantity, unit_price, total_price) VALUES
(8, 40, 1, 2800000, 2800000);
-- Order 9 (User 9): Mua SP 23 (Áo Puma) - Variant 44 (Trắng, M)
INSERT INTO order_items (order_id, product_variant_id, quantity, unit_price, total_price) VALUES
(9, 44, 1, 600000, 600000);
-- Order 10 (User 10): Mua SP 3 (Áo Nike), SP 4 (Quần Nike), SP 2 (Giày Nike)
INSERT INTO order_items (order_id, product_variant_id, quantity, unit_price, total_price) VALUES
(10, 14, 2, 765000, 1530000),
(10, 20, 2, 1100000, 2200000),
(10, 7, 1, 4500000, 4500000);

-- OrderVouchers (Áp dụng voucher cho một số đơn hàng)
-- Model OrderVoucher không có trường discount_amount, chỉ có order_id và voucher_id
INSERT INTO order_vouchers (order_id, voucher_id) VALUES
(2, 2), -- Order 2 dùng FREESHIP
(5, 1), -- Order 5 dùng SALE10
(9, 4); -- Order 9 dùng GIAM50K

-- Payments (Thanh toán cho các đơn hàng đã 'PAID')
INSERT INTO payments (order_id, amount, payment_method, transaction_id, status) VALUES
(1, 3200000, 'BANK_TRANSFER', 'VNPAY_001', 'PAID'),
(2, 1556000, 'COD', 'COD_002', 'PAID'),
(3, 765000, 'BANK_TRANSFER', 'PAYPAL_003', 'PAID'),
(6, 4800000, 'BANK_TRANSFER', 'BANK_004', 'PAID'),
(7, 600000, 'BANK_TRANSFER', 'VNPAY_005', 'PAID'),
(8, 2800000, 'COD', 'COD_006', 'PAID'),
-- Đơn hàng 4, 5, 9, 10 chưa thanh toán hoặc đã hủy
(4, 3800000, 'BANK_TRANSFER', 'VNPAY_007', 'UNPAID'),
(5, 990000, 'COD', 'COD_008', 'UNPAID'),
(9, 550000, 'COD', 'COD_009', 'UNPAID'),
(10, 8230000, 'BANK_TRANSFER', 'BANK_010', 'UNPAID');

-- Messages (10 tin nhắn trong cuộc hội thoại 1 và 3)
-- Thêm: message_type, attachment_name, attachment_type, attachment_url, is_read để khớp với model Message
-- Cuộc hội thoại 1: User An (id=2) chat với Admin (id=1)
-- room_id = "2" (userId của customer)

-- Tin nhắn 1: User An gửi cho Admin (2 giờ trước)

INSERT INTO Messages (sender_id, receiver_id, content, room_id, message_type, is_read, created_at) 
VALUES 
(2, 1, 'Chào shop, đơn hàng DH000001 của tôi đã giao chưa?', '2', 'TEXT', TRUE, DATE_SUB(NOW(), INTERVAL 120 MINUTE));

INSERT INTO Messages (sender_id, receiver_id, content, room_id, message_type, is_read, created_at) 
VALUES 
(1, 2, 'Chào bạn, để mình kiểm tra nhé.', '2', 'TEXT', TRUE, DATE_SUB(NOW(), INTERVAL 115 MINUTE));

INSERT INTO Messages (sender_id, receiver_id, content, room_id, message_type, is_read, created_at) 
VALUES 
(1, 2, 'Đơn hàng DH000001 đã được giao thành công ngày hôm qua ạ.', '2', 'TEXT', TRUE, DATE_SUB(NOW(), INTERVAL 110 MINUTE));

INSERT INTO Messages (sender_id, receiver_id, content, room_id, message_type, is_read, created_at) 
VALUES 
(2, 1, 'OK, cảm ơn shop.', '2', 'TEXT', FALSE, DATE_SUB(NOW(), INTERVAL 105 MINUTE));

INSERT INTO Messages (sender_id, receiver_id, content, room_id, message_type, is_read, created_at) 
VALUES 
(3, 1, 'Shop ơi, tôi muốn tư vấn size giày Adidas.', '3', 'TEXT', FALSE, DATE_SUB(NOW(), INTERVAL 30 MINUTE));

INSERT INTO Messages (sender_id, receiver_id, content, room_id, message_type, is_read, created_at) 
VALUES 
(1, 3, 'Chào bạn, bạn thường đi size bao nhiêu?', '3', 'TEXT', TRUE, DATE_SUB(NOW(), INTERVAL 25 MINUTE));

INSERT INTO Messages (sender_id, receiver_id, content, room_id, message_type, is_read, created_at) 
VALUES 
(4, 1, 'Tôi mua sản phẩm Nike Mercurial, bị lỗi keo ở mũi giày.', '4', 'TEXT', FALSE, DATE_SUB(NOW(), INTERVAL 15 MINUTE));

INSERT INTO Messages (sender_id, receiver_id, content, room_id, message_type, is_read, created_at) 
VALUES 
(1, 4, 'Bạn vui lòng chụp ảnh sản phẩm gửi cho shop nhé.', '4', 'TEXT', TRUE, DATE_SUB(NOW(), INTERVAL 10 MINUTE));

INSERT INTO Messages (sender_id, receiver_id, content, room_id, message_type, is_read, created_at) 
VALUES 
(4, 1, 'Đây nhé shop, sản phẩm bị lỗi như thế này.', '4', 'TEXT', FALSE, DATE_SUB(NOW(), INTERVAL 5 MINUTE));

INSERT INTO Messages (sender_id, receiver_id, content, room_id, message_type, is_read, created_at) 
VALUES 
(1, 4, 'Shop đã nhận được. Shop sẽ xử lý khiếu nại này ngay.', '4', 'TEXT', TRUE, DATE_SUB(NOW(), INTERVAL 2 MINUTE));



/*
=====================================================
BẢNG TIER 4 (PHỤ THUỘC TIER 3)
=====================================================
*/

-- ProductVariantImages (Ảnh cho các biến thể - VD: ảnh giày màu đen, màu trắng)
-- Lấy 10 ID biến thể đầu tiên
INSERT INTO product_variant_images (product_variant_id, image_url) VALUES
(1, 'public/product_image/nike_peg40_blk.jpg'), (2, 'public/product_image/nike_peg40_blk.jpg'), (3, 'public/product_image/nike_peg40_blk.jpg'),
(4, 'public/product_image/nike_peg40_gry.jpg'), (5, 'public/product_image/nike_peg40_gry.jpg'), (6, 'public/product_image/nike_peg40_gry.jpg'),
(7, 'public/product_image/nike_merc9_red.jpg'), (8, 'public/product_image/nike_merc9_red.jpg'),
(10, 'public/product_image/nike_merc9_yel.jpg'), (11, 'public/product_image/nike_merc9_yel.jpg');

-- CartItems (Thêm sản phẩm vào giỏ hàng cho 10 user)
-- Model CartItem: cart_id, product_variant_id (bắt buộc), quantity, item_total_price
-- Lưu ý: KHÔNG có product_id, unit_price, total_price
INSERT INTO cart_items (cart_id, product_variant_id, quantity, item_total_price) VALUES
(2, 1, 1, 3200000), -- User 2, Cart 2, Variant 1 (SP1: Nike Air Zoom - Đen, 40)
(3, 25, 1, 4800000), -- User 3, Cart 3, Variant 25 (SP11: Adidas Ultraboost - Trắng, 40)
(3, 31, 2, 1672000), -- User 3, Cart 3, Variant 31 (SP13: Áo Adidas - Đen, S) - giá đã giảm 12%
(4, 19, 1, 1100000), -- User 4, Cart 4, Variant 19 (SP4: Quần Nike - Đen, S)
(5, 46, 1, 3230000), -- User 5, Cart 5, Variant 46 (SP31: UA HOVR - size variant)
(6, 13, 1, 765000), -- User 6, Cart 6, Variant 13 (SP3: Áo Nike - Đen, S) - giá đã giảm 10%
(7, 19, 1, 1100000), -- User 7, Cart 7, Variant 19 (SP4: Quần Nike - Đen, S)
(8, 40, 1, 2800000), -- User 8, Cart 8, Variant 40 (SP21: Giày Puma)
(9, 46, 1, 3230000), -- User 9, Cart 9, Variant 46 (SP31: UA HOVR)
(10, 1, 1, 3200000); -- User 10, Cart 10, Variant 1 (SP1: Nike Air Zoom - Đen, 40)

-- MessageAttachments (Đính kèm cho tin nhắn 9)
INSERT INTO message_attachments (message_id, file_url, file_name, file_size, file_type) VALUES
(9, 'uploads/attachments/loi_keo_giay.jpg', 'loi_keo_giay.jpg', 102400, 'image/jpeg'),
(1, 'uploads/attachments/screenshot.png', 'screenshot.png', 204800, 'image/png'),
(2, 'uploads/attachments/file.pdf', 'file.pdf', 512000, 'application/pdf'),
(3, 'uploads/attachments/image1.jpg', 'image1.jpg', 153600, 'image/jpeg'),
(4, 'uploads/attachments/document.docx', 'document.docx', 307200, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'),
(5, 'uploads/attachments/image2.png', 'image2.png', 256000, 'image/png'),
(6, 'uploads/attachments/file2.pdf', 'file2.pdf', 614400, 'application/pdf'),
(7, 'uploads/attachments/photo.jpg', 'photo.jpg', 184320, 'image/jpeg'),
(8, 'uploads/attachments/report.pdf', 'report.pdf', 716800, 'application/pdf'),
(10, 'uploads/attachments/final_image.png', 'final_image.png', 409600, 'image/png');