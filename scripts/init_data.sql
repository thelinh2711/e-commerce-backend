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
INSERT INTO Brands (name, description, logo) VALUES
('Nike', 'Thương hiệu thể thao hàng đầu thế giới.', 'nike_logo.png'),
('Adidas', 'Thương hiệu thể thao nổi tiếng từ Đức.', 'adidas_logo.png'),
('Puma', 'Thương hiệu thể thao với logo báo đốm.', 'puma_logo.png'),
('Under Armour', 'Thương hiệu chuyên về quần áo và phụ kiện thể thao hiệu suất cao.', 'under_armour_logo.png'),
('ASICS', 'Thương hiệu nổi tiếng về giày chạy bộ từ Nhật Bản.', 'asics_logo.png'),
('New Balance', 'Thương hiệu giày thể thao thoải mái và thời trang.', 'new_balance_logo.png'),
('Reebok', 'Thương hiệu con của Adidas, tập trung vào CrossFit và training.', 'reebok_logo.png'),
('The North Face', 'Chuyên về đồ dã ngoại, leo núi và thời tiết lạnh.', 'the_north_face_logo.png'),
('Columbia', 'Thương hiệu đồ outdoor và dã ngoại.', 'columbia_logo.png'),
('Decathlon (Quechua)', 'Thương hiệu bán lẻ thể thao giá cả phải chăng.', 'decathlon_logo.png');

-- Categories (10)
INSERT INTO Categories (name, description, slug) VALUES
('Giày Chạy Bộ', 'Giày chuyên dụng cho chạy bộ đường trường và đường mòn.', 'giay-chay-bo'),
('Giày Đá Banh', 'Giày đinh cho sân cỏ tự nhiên và nhân tạo.', 'giay-da-banh'),
('Áo Thun & Áo Ba Lỗ', 'Áo thun, áo tank top cho tập luyện và mặc hàng ngày.', 'ao-thun-ba-lo'),
('Quần Short', 'Quần short cho mọi hoạt động thể thao.', 'quan-short'),
('Quần Dài & Legging', 'Quần jogger, quần legging, quần nỉ.', 'quan-dai-legging'),
('Áo Khoác', 'Áo khoác gió, áo khoác nỉ, áo khoác chống nước.', 'ao-khoac'),
('Đồ Tập Gym', 'Quần áo chuyên dụng cho tập gym và fitness.', 'do-tap-gym'),
('Phụ Kiện', 'Tất, mũ, găng tay, băng đô.', 'phu-kien'),
('Balo & Túi', 'Balo, túi trống, túi đeo chéo thể thao.', 'balo-tui'),
('Dụng Cụ Thể Thao', 'Bóng, thảm tập yoga, dây nhảy.', 'dung-cu-the-thao');

-- Colors (10)
INSERT INTO Colors (name, hex_code) VALUES
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
INSERT INTO Sizes (name) VALUES
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
INSERT INTO Labels (name, color) VALUES
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
INSERT INTO Users (full_name, email, phone, password, role) VALUES
('Nguyễn Văn Quản Trị', 'admin@shop.com', '0900000001', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', 'ADMIN'),
('Trần Văn An', 'an.tran@gmail.com', '0912345678', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', 'CUSTOMER'),
('Lê Thị Bình', 'binh.le@yahoo.com', '0987654321', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', 'CUSTOMER'),
('Phạm Văn Cường', 'cuong.pham@outlook.com', '0911223344', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', 'CUSTOMER'),
('Võ Thị Dung', 'dung.vo@gmail.com', '0922334455', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', 'CUSTOMER'),
('Hoàng Văn Em', 'em.hoang@gmail.com', '0933445566', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', 'CUSTOMER'),
('Đặng Thị Giang', 'giang.dang@gmail.com', '0944556677', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', 'CUSTOMER'),
('Bùi Văn Hải', 'hai.bui@gmail.com', '0955667788', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', 'CUSTOMER'),
('Ngô Thị Hương', 'huong.ngo@gmail.com', '0966778899', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', 'CUSTOMER'),
('Lý Văn Kiên', 'kien.ly@gmail.com', '0977889900', '$2a$10$E.M9j8cN0.A.i.3.A.B.C.D.E.F.G.H.I.J.K.L.M', 'CUSTOMER');

-- Vouchers (10)
INSERT INTO Vouchers (code, description, discount_type, discount_value, max_usage_count, start_date, end_date) VALUES
('SALE10', 'Giảm 10% tổng đơn hàng', 'PERCENTAGE', 10, 1000, '2025-10-01 00:00:00', '2025-10-31 23:59:59'),
('FREESHIP', 'Miễn phí vận chuyển', 'FIXED_AMOUNT', 30000, 500, '2025-10-01 00:00:00', '2025-10-31 23:59:59'),
('NEWUSER20', 'Giảm 20% cho người dùng mới', 'PERCENTAGE', 20, 200, '2025-01-01 00:00:00', '2025-12-31 23:59:59'),
('GIAM50K', 'Giảm 50.000 VNĐ cho đơn từ 500K', 'FIXED_AMOUNT', 50000, 100, '2025-10-20 00:00:00', '2025-10-25 23:59:59'),
('NIKEONLY', 'Giảm 15% chỉ cho sản phẩm Nike', 'PERCENTAGE', 15, 50, '2025-10-15 00:00:00', '2025-10-30 23:59:59'),
('ADIDASFLASH', 'Giảm 100K cho sản phẩm Adidas', 'FIXED_AMOUNT', 100000, 50, '2025-10-24 00:00:00', '2025-10-24 23:59:59'),
('SPORT123', 'Voucher ngẫu nhiên', 'PERCENTAGE', 12, 100, '2025-10-01 00:00:00', '2025-11-30 23:59:59'),
('HAPPYWEEKEND', 'Cuối tuần vui vẻ giảm 5%', 'PERCENTAGE', 5, 100, '2025-10-25 00:00:00', '2025-10-26 23:59:59'),
('THANKYOU', 'Tri ân khách hàng giảm 25K', 'FIXED_AMOUNT', 25000, 1000, '2025-01-01 00:00:00', '2025-12-31 23:59:59'),
('RUNFAST', 'Giảm 10% cho giày chạy bộ', 'PERCENTAGE', 10, 50, '2025-10-01 00:00:00', '2025-10-31 23:59:59');

-- FlashSales (10)
INSERT INTO FlashSales (name, description, start_date, end_date) VALUES
('Black Friday Sớm', 'Giảm giá sốc các mặt hàng hot', '2025-11-20 00:00:00', '2025-11-28 23:59:59'),
('Sale 11.11', 'Siêu sale 11.11', '2025-11-11 00:00:00', '2025-11-11 23:59:59'),
('Giáng Sinh An Lành', 'Mua sắm quà giáng sinh', '2025-12-15 00:00:00', '2025-12-25 23:59:59'),
('Xả Hàng Cuối Năm', 'Dọn kho đón năm mới', '2025-12-28 00:00:00', '2026-01-05 23:59:59'),
('Chào Hè Sôi Động', 'Giảm giá đồ đi biển, đồ tập hè', '2026-04-15 00:00:00', '2026-04-30 23:59:59'),
('Back to School', 'Giảm giá balo, túi xách', '2025-08-15 00:00:00', '2025-08-31 23:59:59'),
('Running Day', 'Ưu đãi đặc biệt cho dân chạy bộ', '2025-10-25 00:00:00', '2025-10-27 23:59:59'),
('Football Fever', 'Giảm giá giày và áo đá banh', '2026-06-01 00:00:00', '2026-06-15 23:59:59'),
('Gym & Fitness Week', 'Sale đồ tập gym', '2025-10-28 00:00:00', '2025-11-05 23:59:59'),
('Outdoor Essentials', 'Giảm giá đồ dã ngoại', '2025-10-10 00:00:00', '2025-10-20 23:59:59');

-- News (10)
INSERT INTO News (title, slug, content, excerpt, author, status, published_at) VALUES
('Cách chọn giày chạy bộ cho người mới bắt đầu', 'cach-chon-giay-chay-bo', 'Nội dung chi tiết về cách chọn giày...', 'Việc chọn đúng giày chạy bộ là rất quan trọng...', 'Admin', 'PUBLISHED', '2025-10-01 10:00:00'),
('Top 5 bài tập tăng cơ bắp hiệu quả tại nhà', 'top-5-bai-tap-tang-co-bap', 'Nội dung chi tiết về 5 bài tập...', 'Không cần đến phòng gym, bạn vẫn có thể...', 'Admin', 'PUBLISHED', '2025-10-05 11:00:00'),
('Công nghệ Dri-FIT của Nike hoạt động như thế nào?', 'cong-nghe-dri-fit-nike', 'Nội dung chi tiết về Dri-FIT...', 'Dri-FIT là công nghệ vải giúp thấm hút mồ hôi...', 'Biên Tập Viên', 'PUBLISHED', '2025-10-10 14:30:00'),
('Đánh giá chi tiết Adidas Ultraboost Light', 'danh-gia-adidas-ultraboost-light', 'Nội dung đánh giá...', 'Ultraboost Light có thật sự "nhẹ"?', 'Admin', 'PUBLISHED', '2025-10-15 09:00:00'),
('Xu hướng thời trang thể thao 2026', 'xu-huong-thoi-trang-the-thao-2026', 'Nội dung về xu hướng...', 'Athleisure vẫn tiếp tục thống trị...', 'Biên Tập Viên', 'PUBLISHED', '2025-10-20 16:00:00'),
('Hướng dẫn bảo quản giày đá banh đúng cách', 'bao-quan-giay-da-banh', 'Nội dung hướng dẫn...', 'Để đôi giày của bạn bền hơn...', 'Admin', 'PUBLISHED', '2025-10-22 10:00:00'),
('Vì sao quần legging Under Armour được yêu thích?', 'vi-sao-quan-legging-ua-duoc-yeu-thich', 'Nội dung phân tích...', 'Độ co giãn, thấm hút và thiết kế...', 'Admin', 'PUBLISHED', '2025-10-23 11:00:00'),
('Lợi ích của việc tập Yoga mỗi ngày', 'loi-ich-cua-yoga', 'Nội dung chi tiết...', 'Yoga không chỉ giúp dẻo dai mà còn...', 'Biên Tập Viên', 'PUBLISHED', '2025-10-24 08:00:00'),
('Sự trở lại của Puma Suede', 'su-tro-lai-puma-suede', 'Nội dung...', 'Một huyền thoại đường phố...', 'Admin', 'DRAFT', '2025-10-25 10:00:00'),
('Các loại balo The North Face phổ biến', 'cac-loai-balo-the-north-face', 'Nội dung...', 'Từ đi học đến đi phượt...', 'Admin', 'PUBLISHED', '2025-10-18 15:00:00');

-- Banners (10)
INSERT INTO Banners (title, image, link, position, is_active, start_date, end_date) VALUES
('Bộ sưu tập Nike Mới', 'banner_nike_new.jpg', '/categories/giay-chay-bo?brand=nike', 'TOP', TRUE, '2025-10-15 00:00:00', '2025-10-31 23:59:59'),
('Adidas Sale 30%', 'banner_adidas_sale.jpg', '/categories/giay-da-banh?brand=adidas', 'TOP', TRUE, '2025-10-20 00:00:00', '2025-10-30 23:59:59'),
('Under Armour Training', 'banner_ua_gym.jpg', '/categories/do-tap-gym', 'MIDDLE', TRUE, '2025-10-01 00:00:00', '2025-10-31 23:59:59'),
('Miễn Phí Vận Chuyển', 'banner_freeship.jpg', '/vouchers', 'BOTTOM', TRUE, '2025-10-01 00:00:00', '2025-10-31 23:59:59'),
('The North Face - Chinh Phục Mùa Đông', 'banner_tnf_winter.jpg', '/categories/ao-khoac?brand=the-north-face', 'TOP', TRUE, '2025-10-25 00:00:00', '2025-11-30 23:59:59'),
('ASICS - Vững Bước Chạy', 'banner_asics_run.jpg', '/categories/giay-chay-bo?brand=asics', 'MIDDLE', TRUE, '2025-10-10 00:00:00', '2025-11-10 23:59:59'),
('Phụ Kiện Thể Thao', 'banner_accessories.jpg', '/categories/phu-kien', 'BOTTOM', TRUE, '2025-10-01 00:00:00', '2025-12-31 23:59:59'),
('Puma Football', 'banner_puma_football.jpg', '/categories/giay-da-banh?brand=puma', 'MIDDLE', TRUE, '2025-10-20 00:00:00', '2025-11-20 23:59:59'),
('New Balance 550', 'banner_nb_550.jpg', '/products/new-balance-550', 'TOP', TRUE, '2025-10-22 00:00:00', '2025-11-05 23:59:59'),
('Decathlon - Giá Tốt Mỗi Ngày', 'banner_decathlon.jpg', '/brands/decathlon', 'BOTTOM', TRUE, '2025-10-01 00:00:00', '2025-12-31 23:59:59');

/*
=====================================================
BẢNG TIER 2 (PHỤ THUỘC TIER 1)
=====================================================
*/

-- UserProviders (10)
INSERT INTO UserProviders (user_id, provider_name, provider_user_id) VALUES
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
INSERT INTO Addresses (user_id, street, ward, district, city, province, country, is_default) VALUES
(1, '123 Đường Quản Trị', 'Phường Bến Nghé', 'Quận 1', 'Hồ Chí Minh', 'Hồ Chí Minh', 'Vietnam', TRUE),
(2, '456 Đường Lê Lợi', 'Phường Hàng Bạc', 'Quận Hoàn Kiếm', 'Hà Nội', 'Hà Nội', 'Vietnam', TRUE),
(3, '789 Đường Võ Văn Tần', 'Phường 6', 'Quận 3', 'Hồ Chí Minh', 'Hồ Chí Minh', 'Vietnam', TRUE),
(4, '101 Đường Nguyễn Huệ', 'Phường Hải Châu 1', 'Quận Hải Châu', 'Đà Nẵng', 'Đà Nẵng', 'Vietnam', TRUE),
(5, '202 Đường Trần Phú', 'Phường Phước Ninh', 'Quận Hải Châu', 'Đà Nẵng', 'Đà Nẵng', 'Vietnam', TRUE),
(6, '303 Đường Hai Bà Trưng', 'Phường Tân Định', 'Quận 1', 'Hồ Chí Minh', 'Hồ Chí Minh', 'Vietnam', TRUE),
(7, '404 Đường Cầu Giấy', 'Phường Dịch Vọng', 'Quận Cầu Giấy', 'Hà Nội', 'Hà Nội', 'Vietnam', TRUE),
(8, '505 Đường Lạch Tray', 'Phường Lạch Tray', 'Quận Ngô Quyền', 'Hải Phòng', 'Hải Phòng', 'Vietnam', TRUE),
(9, '606 Đường Hùng Vương', 'Phường Thới Bình', 'Quận Ninh Kiều', 'Cần Thơ', 'Cần Thơ', 'Vietnam', TRUE),
(10, '707 Đường 30/4', 'Phường Hưng Lợi', 'Quận Ninh Kiều', 'Cần Thơ', 'Cần Thơ', 'Vietnam', TRUE);

-- Carts (10)
INSERT INTO Carts (user_id) VALUES
(1), (2), (3), (4), (5), (6), (7), (8), (9), (10);

-- Wishlists (10)
INSERT INTO Wishlists (user_id) VALUES
(1), (2), (3), (4), (5), (6), (7), (8), (9), (10);

-- Orders (10)
INSERT INTO Orders (user_id, order_number, total_amount, final_amount, status, payment_status, delivery_address) VALUES
(2, 'DH000001', 2500000, 2500000, 'DELIVERED', 'PAID', '456 Đường Lê Lợi, Hàng Bạc, Hoàn Kiếm, Hà Nội'),
(3, 'DH000002', 1200000, 1170000, 'DELIVERED', 'PAID', '789 Đường Võ Văn Tần, P6, Q3, TPHCM'),
(4, 'DH000003', 850000, 850000, 'SHIPPED', 'PAID', '101 Đường Nguyễn Huệ, Hải Châu 1, Hải Châu, Đà Nẵng'),
(5, 'DH000004', 3200000, 3200000, 'PENDING', 'UNPAID', '202 Đường Trần Phú, Phước Ninh, Hải Châu, Đà Nẵng'),
(2, 'DH000005', 450000, 420000, 'CANCELLED', 'UNPAID', '456 Đường Lê Lợi, Hàng Bạc, Hoàn Kiếm, Hà Nội'),
(6, 'DH000006', 1800000, 1800000, 'CONFIRMED', 'PAID', '303 Đường Hai Bà Trưng, Tân Định, Q1, TPHCM'),
(7, 'DH000007', 750000, 750000, 'DELIVERED', 'PAID', '404 Đường Cầu Giấy, Dịch Vọng, Cầu Giấy, Hà Nội'),
(8, 'DH000008', 2100000, 2100000, 'SHIPPED', 'PAID', '505 Đường Lạch Tray, Lạch Tray, Ngô Quyền, Hải Phòng'),
(9, 'DH000009', 550000, 500000, 'CONFIRMED', 'UNPAID', '606 Đường Hùng Vương, Thới Bình, Ninh Kiều, Cần Thơ'),
(10, 'DH000010', 980000, 980000, 'PENDING', 'UNPAID', '707 Đường 30/4, Hưng Lợi, Ninh Kiều, Cần Thơ');

-- Conversations (10)
INSERT INTO Conversations (user_id, subject, status) VALUES
(2, 'Hỏi về tình trạng đơn hàng DH000001', 'CLOSED'),
(3, 'Tư vấn chọn size giày', 'OPEN'),
(4, 'Khiếu nại sản phẩm bị lỗi', 'IN_PROGRESS'),
(5, 'Hỏi về chính sách đổi trả', 'OPEN'),
(2, 'Hủy đơn hàng DH000005', 'CLOSED'),
(6, 'Hỏi về voucher FREESHIP', 'CLOSED'),
(7, 'Sản phẩm giao không đúng màu', 'IN_PROGRESS'),
(8, 'Hỏi về thời gian giao hàng', 'OPEN'),
(9, 'Tư vấn mua đồ tập gym', 'OPEN'),
(10, 'Hỏi về chương trình Flash Sale', 'OPEN');

/*
=====================================================
PRODUCTS (100 SẢN PHẨM)
Tạo 10 sản phẩm cho mỗi thương hiệu (10 thương hiệu)
=====================================================
*/
INSERT INTO Products (name, description, price, brand_id, sku, stock, slug) VALUES
-- Nike (1-10)
('Nike Air Zoom Pegasus 40', 'Giày chạy bộ huyền thoại, êm ái và ổn định.', 3200000, 1, 'NIKE-PEG40', 100, 'nike-air-zoom-pegasus-40'),
('Nike Mercurial Superfly 9', 'Giày đá banh tốc độ, thiết kế cho sân cỏ tự nhiên.', 4500000, 1, 'NIKE-MERC9', 50, 'nike-mercurial-superfly-9'),
('Áo Thun Nike Dri-FIT Miller', 'Áo thun chạy bộ, công nghệ Dri-FIT thoáng khí.', 850000, 1, 'NIKE-MILLER-T', 200, 'ao-thun-nike-dri-fit-miller'),
('Quần Short Nike Challenger', 'Quần short chạy bộ 2 lớp, có túi đựng điện thoại.', 1100000, 1, 'NIKE-CHAL-SHORT', 150, 'quan-short-nike-challenger'),
('Quần Legging Nike Pro', 'Quần legging tập luyện, co giãn và hỗ trợ cơ bắp.', 1300000, 1, 'NIKE-PRO-LEG', 100, 'quan-legging-nike-pro'),
('Áo Khoác Nike Windrunner', 'Áo khoác gió, chống nước nhẹ, thiết kế cổ điển.', 2500000, 1, 'NIKE-WINDRUN', 80, 'ao-khoac-nike-windrunner'),
('Áo Bra Nike Swoosh', 'Áo lót thể thao hỗ trợ vừa, thoải mái.', 900000, 1, 'NIKE-SWOOSH-BRA', 120, 'ao-bra-nike-swoosh'),
('Tất Nike Everyday Plus', 'Tất thể thao, đệm êm, thấm hút mồ hôi (bộ 3 đôi).', 450000, 1, 'NIKE-SOCK-3P', 300, 'tat-nike-everyday-plus'),
('Balo Nike Brasilia', 'Balo thể thao, dung tích lớn, nhiều ngăn.', 1200000, 1, 'NIKE-BRASILIA-BP', 100, 'balo-nike-brasilia'),
('Bóng Rổ Nike Everyday', 'Bóng rổ size 7, chất liệu cao su bền bỉ.', 700000, 1, 'NIKE-BALL-BB', 60, 'bong-ro-nike-everyday'),

-- Adidas (11-20)
('Adidas Ultraboost Light', 'Giày chạy bộ với đệm Boost nhẹ nhất từ trước đến nay.', 4800000, 2, 'ADI-UBLIGHT', 100, 'adidas-ultraboost-light'),
('Adidas Predator Accuracy', 'Giày đá banh kiểm soát, upper công nghệ mới.', 5200000, 2, 'ADI-PRED-ACC', 50, 'adidas-predator-accuracy'),
('Áo Thun Adidas Own The Run', 'Áo thun chạy bộ, chất liệu tái chế, thoáng mát.', 950000, 2, 'ADI-OTR-TEE', 200, 'ao-thun-adidas-own-the-run'),
('Quần Short Adidas 3-Stripes', 'Quần short thể thao, phong cách 3 sọc cổ điển.', 750000, 2, 'ADI-3S-SHORT', 150, 'quan-short-adidas-3-stripes'),
('Quần Dài Adidas Tiro 23', 'Quần dài thể thao, dáng ôm, khóa kéo ở cổ chân.', 1400000, 2, 'ADI-TIRO23', 100, 'quan-dai-adidas-tiro-23'),
('Áo Khoác Nỉ Adidas Essentials', 'Áo khoác nỉ 3 sọc, giữ ấm tốt.', 1800000, 2, 'ADI-ESS-HOODIE', 80, 'ao-khoac-ni-adidas-essentials'),
('Áo Bra Tập Luyện Adidas TLRD', 'Áo lót thể thao hỗ trợ cao, tùy chỉnh.', 1100000, 2, 'ADI-TLRD-BRA', 120, 'ao-bra-tap-luyen-adidas-tlrd'),
('Mũ Adidas Baseball', 'Mũ lưỡi trai, logo thêu, quai điều chỉnh.', 550000, 2, 'ADI-CAP-BB', 300, 'mu-adidas-baseball'),
('Túi Trống Adidas Defender', 'Túi trống tập gym, size M, chống nước.', 1300000, 2, 'ADI-DEF-DUFFEL', 100, 'tui-trong-adidas-defender'),
('Bóng Đá Adidas Al Rihla', 'Bóng đá thi đấu chính thức World Cup 2022.', 3500000, 2, 'ADI-BALL-WC22', 60, 'bong-da-adidas-al-rihla'),

-- Puma (21-30)
('Puma Velocity Nitro 2', 'Giày chạy bộ, đệm Nitro Foam êm ái.', 2800000, 3, 'PUMA-VELO-N2', 100, 'puma-velocity-nitro-2'),
('Puma Future Ultimate', 'Giày đá banh linh hoạt, không dây.', 4300000, 3, 'PUMA-FUTURE-ULT', 50, 'puma-future-ultimate'),
('Áo Thun Puma Essentials Logo', 'Áo thun cotton, logo Puma lớn trước ngực.', 600000, 3, 'PUMA-ESS-TEE', 200, 'ao-thun-puma-essentials-logo'),
('Quần Short Puma Fit Woven', 'Quần short tập luyện, vải dệt nhẹ, co giãn.', 800000, 3, 'PUMA-FIT-SHORT', 150, 'quan-short-puma-fit-woven'),
('Quần Jogger Puma T7', 'Quần jogger, viền T7 cổ điển 2 bên.', 1200000, 3, 'PUMA-T7-JOGGER', 100, 'quan-jogger-puma-t7'),
('Áo Khoác Puma Iconic T7', 'Áo khoác thể thao, phong cách T7 cổ điển.', 1700000, 3, 'PUMA-T7-JACKET', 80, 'ao-khoac-puma-iconic-t7'),
('Đồ Tập Gym Puma Fit', 'Bộ đồ tập gym nữ, chất liệu co giãn.', 1500000, 3, 'PUMA-FIT-SET', 120, 'do-tap-gym-puma-fit'),
('Tất Puma Quarter (3 đôi)', 'Tất cổ ngắn, chất liệu cotton.', 300000, 3, 'PUMA-SOCK-3P', 300, 'tat-puma-quarter-3-doi'),
('Balo Puma Phase', 'Balo đi học, đi làm, thiết kế đơn giản.', 700000, 3, 'PUMA-PHASE-BP', 100, 'balo-puma-phase'),
('Thảm Tập Yoga Puma', 'Thảm tập yoga, chống trơn trượt.', 900000, 3, 'PUMA-YOGA-MAT', 60, 'tham-tap-yoga-puma'),

-- Under Armour (31-40)
('UA HOVR Phantom 3', 'Giày chạy bộ thông minh, kết nối app.', 3800000, 4, 'UA-HOVR-PH3', 100, 'ua-hovr-phantom-3'),
('Giày Đá Banh UA Clone Magnetico', 'Giày đá banh, upper ôm sát như lớp da thứ 2.', 4100000, 4, 'UA-CLONE-MAG', 50, 'giay-da-banh-ua-clone-magnetico'),
('Áo Thun UA HeatGear', 'Áo thun nén cơ, giữ mát và khô ráo.', 900000, 4, 'UA-HG-TEE', 200, 'ao-thun-ua-heatgear'),
('Quần Short UA Launch Run', 'Quần short chạy bộ, siêu nhẹ, có túi.', 1000000, 4, 'UA-LAUNCH-SHORT', 150, 'quan-short-ua-launch-run'),
('Quần Legging UA RUSH', 'Quần legging, công nghệ RUSH hoàn trả năng lượng.', 1800000, 4, 'UA-RUSH-LEG', 100, 'quan-legging-ua-rush'),
('Áo Khoác UA Storm', 'Áo khoác chống nước, chống gió, siêu nhẹ.', 2800000, 4, 'UA-STORM-JACKET', 80, 'ao-khoac-ua-storm'),
('Áo Bra UA Infinity High', 'Áo lót thể thao hỗ trợ cao, đệm mút liền.', 1500000, 4, 'UA-INF-BRA', 120, 'ao-bra-ua-infinity-high'),
('Mũ UA Iso-Chill', 'Mũ tập luyện, công nghệ làm mát Iso-Chill.', 750000, 4, 'UA-ISO-CAP', 300, 'mu-ua-iso-chill'),
('Túi Trống UA Undeniable 5.0', 'Túi trống, công nghệ Storm chống nước.', 1400000, 4, 'UA-UNDENIABLE-5', 100, 'tui-trong-ua-undeniable-5-0'),
('Găng Tay Tập Gym UA', 'Găng tay tập tạ, đệm êm, bám dính tốt.', 600000, 4, 'UA-GYM-GLOVE', 60, 'gang-tay-tap-gym-ua'),

-- ASICS (41-50)
('ASICS GEL-Kayano 30', 'Giày chạy bộ ổn định, công nghệ GEL êm ái.', 4200000, 5, 'ASICS-KAYANO30', 100, 'asics-gel-kayano-30'),
('ASICS GEL-Nimbus 25', 'Giày chạy bộ siêu êm, đệm dày.', 4000000, 5, 'ASICS-NIMBUS25', 50, 'asics-gel-nimbus-25'),
('Áo Thun ASICS Ventilate', 'Áo thun chạy bộ, siêu thoáng khí.', 1000000, 5, 'ASICS-VENT-TEE', 200, 'ao-thun-asics-ventilate'),
('Quần Short ASICS Road 2-in-1', 'Quần short chạy bộ 2 lớp, có túi gel.', 1200000, 5, 'ASICS-ROAD-SHORT', 150, 'quan-short-asics-road-2-in-1'),
('Quần Legging ASICS Lite-Show', 'Quần legging chạy tối, phản quang 360 độ.', 1500000, 5, 'ASICS-LITE-LEG', 100, 'quan-legging-asics-lite-show'),
('Áo Khoác ASICS Accelerate', 'Áo khoác chạy bộ, chống gió, chống mưa nhẹ.', 2200000, 5, 'ASICS-ACC-JACKET', 80, 'ao-khoac-asics-accelerate'),
('Áo Bra ASICS Sakura', 'Áo lót thể thao, thiết kế hoa anh đào.', 950000, 5, 'ASICS-SKR-BRA', 120, 'ao-bra-asics-sakura'),
('Tất Chạy Bộ ASICS (3 đôi)', 'Tất chạy bộ, mỏng nhẹ, chống phồng rộp.', 500000, 5, 'ASICS-RUN-SOCK', 300, 'tat-chay-bo-asics-3-doi'),
('Balo Chạy Bộ ASICS', 'Balo/vest chạy bộ, đựng nước và gel.', 1800000, 5, 'ASICS-RUN-VEST', 100, 'balo-chay-bo-asics'),
('Băng Đô ASICS', 'Băng đô thể thao, thấm hút mồ hôi.', 300000, 5, 'ASICS-HEADBAND', 60, 'bang-do-asics'),

-- New Balance (51-60)
('New Balance Fresh Foam X 1080v13', 'Giày chạy bộ, đệm Fresh Foam X siêu êm.', 3900000, 6, 'NB-1080V13', 100, 'nb-fresh-foam-x-1080v13'),
('New Balance 550', 'Giày thể thao thời trang, phong cách bóng rổ cổ điển.', 2800000, 6, 'NB-550', 50, 'nb-550'),
('Áo Thun NB Essentials', 'Áo thun cotton, logo NB cổ điển.', 700000, 6, 'NB-ESS-TEE', 200, 'ao-thun-nb-essentials'),
('Quần Short NB Accelerate', 'Quần short chạy bộ, chất liệu nhẹ, khô nhanh.', 850000, 6, 'NB-ACC-SHORT', 150, 'quan-short-nb-accelerate'),
('Quần Jogger NB Athletics', 'Quần jogger nỉ, thời trang, thoải mái.', 1400000, 6, 'NB-ATH-JOGGER', 100, 'quan-jogger-nb-athletics'),
('Áo Khoác Gió NB Impact Run', 'Áo khoác chạy bộ, siêu mỏng, gấp gọn được.', 2100000, 6, 'NB-IMP-JACKET', 80, 'ao-khoac-gio-nb-impact-run'),
('Đồ Tập Gym NB Relentless', 'Bộ đồ tập gym nữ, thiết kế thời trang.', 1600000, 6, 'NB-RELENT-SET', 120, 'do-tap-gym-nb-relentless'),
('Mũ NB Classic', 'Mũ lưỡi trai, logo NB.', 500000, 6, 'NB-CLASSIC-CAP', 300, 'mu-nb-classic'),
('Túi Đeo Chéo NB', 'Túi đeo chéo nhỏ gọn, tiện lợi.', 650000, 6, 'NB-CROSSBODY', 100, 'tui-deo-cheo-nb'),
('Giày NB 574', 'Giày thời trang, thiết kế 574 huyền thoại.', 2300000, 6, 'NB-574', 60, 'giay-nb-574'),

-- Reebok (61-70)
('Reebok Nano X4', 'Giày tập gym, CrossFit, đế ổn định.', 3300000, 7, 'RBK-NANO-X4', 100, 'reebok-nano-x4'),
('Reebok Classic Leather', 'Giày thời trang, da thật, phong cách cổ điển.', 2100000, 7, 'RBK-CLASSIC-L', 50, 'reebok-classic-leather'),
('Áo Thun Reebok CrossFit', 'Áo thun chuyên dụng cho CrossFit.', 800000, 7, 'RBK-CF-TEE', 200, 'ao-thun-reebok-crossfit'),
('Quần Short Reebok Speedwick', 'Quần short tập luyện, thấm hút mồ hôi.', 900000, 7, 'RBK-SPEED-SHORT', 150, 'quan-short-reebok-speedwick'),
('Quần Legging Reebok Lux', 'Quần legging cao cấp, mềm mại, co giãn tốt.', 1300000, 7, 'RBK-LUX-LEG', 100, 'quan-legging-reebok-lux'),
('Áo Hoodie Reebok Classic', 'Áo hoodie nỉ, logo vector lớn.', 1600000, 7, 'RBK-CLASSIC-HOOD', 80, 'ao-hoodie-reebok-classic'),
('Áo Bra Reebok Lux High-Support', 'Áo lót thể thao hỗ trợ cao, cho CrossFit.', 1100000, 7, 'RBK-LUX-BRA', 120, 'ao-bra-reebok-lux-high-support'),
('Tất Reebok Classic (3 đôi)', 'Tất cổ cao, logo Reebok.', 350000, 7, 'RBK-SOCK-3P', 300, 'tat-reebok-classic-3-doi'),
('Balo Reebok Training', 'Balo tập luyện, ngăn đựng giày riêng.', 1100000, 7, 'RBK-TRAIN-BP', 100, 'balo-reebok-training'),
('Dây Nhảy Reebok', 'Dây nhảy tốc độ, tay cầm bọc đệm.', 400000, 7, 'RBK-JUMP-ROPE', 60, 'day-nhay-reebok'),

-- The North Face (71-80)
('Giày Leo Núi TNF VECTIV', 'Giày leo núi, công nghệ VECTIV trợ lực.', 4500000, 8, 'TNF-VECTIV-HIKE', 100, 'giay-leo-nui-tnf-vectiv'),
('Áo Khoác TNF Resolve 2', 'Áo khoác chống nước, công nghệ DryVent 2L.', 2900000, 8, 'TNF-RESOLVE-2', 50, 'ao-khoac-tnf-resolve-2'),
('Áo Thun TNF Simple Dome', 'Áo thun cotton, logo TNF nhỏ.', 750000, 8, 'TNF-DOME-TEE', 200, 'ao-thun-tnf-simple-dome'),
('Quần Short TNF Movmynt', 'Quần short chạy trail, siêu nhẹ.', 1300000, 8, 'TNF-MOVMYNT-SHORT', 150, 'quan-short-tnf-movmynt'),
('Quần Dài TNF Paramount', 'Quần dã ngoại, tháo ống thành quần short.', 2200000, 8, 'TNF-PARAMOUNT-PANT', 100, 'quan-dai-tnf-paramount'),
('Áo Khoác Lông Vũ TNF Nuptse', 'Áo khoác lông vũ, giữ ấm, kiểu dáng retro.', 7500000, 8, 'TNF-NUPTSE-JCKT', 80, 'ao-khoac-long-vu-tnf-nuptse'),
('Áo Nỉ TNF Glacier', 'Áo nỉ mỏng, giữ ấm, mặc lót.', 1500000, 8, 'TNF-GLACIER-FLC', 120, 'ao-ni-tnf-glacier'),
('Mũ Nồi TNF', 'Mũ len giữ ấm, logo thêu.', 600000, 8, 'TNF-BEANIE', 300, 'mu-noi-tnf'),
('Balo TNF Borealis', 'Balo đa năng, đi học, đi làm, đi phượt.', 2600000, 8, 'TNF-BOREALIS-BP', 100, 'balo-tnf-borealis'),
('Găng Tay TNF Etip', 'Găng tay nỉ, cảm ứng điện thoại được.', 900000, 8, 'TNF-ETIP-GLOVE', 60, 'gang-tay-tnf-etip'),

-- Columbia (81-90)
('Giày Lội Nước Columbia Drainmaker', 'Giày lội nước, đi biển, thoát nước nhanh.', 2400000, 9, 'COL-DRAINMAKER', 100, 'giay-loi-nuoc-columbia-drainmaker'),
('Áo Khoác Columbia Watertight II', 'Áo khoác chống nước, công nghệ Omni-Tech.', 2300000, 9, 'COL-WATERTIGHT-2', 50, 'ao-khoac-columbia-watertight-ii'),
('Áo Thun Columbia PFG', 'Áo thun câu cá, chống tia UV.', 900000, 9, 'COL-PFG-TEE', 200, 'ao-thun-columbia-pfg'),
('Quần Short Columbia Silver Ridge', 'Quần short dã ngoại, vải mỏng nhẹ.', 1100000, 9, 'COL-SR-SHORT', 150, 'quan-short-columbia-silver-ridge'),
('Quần Dài Columbia Omni-Heat', 'Quần giữ nhiệt, công nghệ phản nhiệt Omni-Heat.', 1900000, 9, 'COL-OH-PANT', 100, 'quan-dai-columbia-omni-heat'),
('Áo Sơ Mi Columbia Bahama', 'Áo sơ mi dã ngoại, câu cá, nhanh khô.', 1300000, 9, 'COL-BAHAMA-SHIRT', 80, 'ao-so-mi-columbia-bahama'),
('Áo Nỉ Columbia Steens Mountain', 'Áo nỉ dày, giữ ấm tốt.', 1200000, 9, 'COL-STEENS-FLC', 120, 'ao-ni-columbia-steens-mountain'),
('Mũ Rộng Vành Columbia Bora Bora', 'Mũ dã ngoại, chống UV, thoáng khí.', 800000, 9, 'COL-BORA-HAT', 300, 'mu-rong-vanh-columbia-bora-bora'),
('Balo Columbia Newton Ridge', 'Balo dã ngoại, dung tích 25L.', 1700000, 9, 'COL-NEWTON-BP', 100, 'balo-columbia-newton-ridge'),
('Khăn Đa Năng Columbia', 'Khăn ống đa năng, chống nắng.', 400000, 9, 'COL-GAITER', 60, 'khan-da-nang-columbia'),

-- Decathlon (Quechua/Kipsta/Domyos) (91-100)
('Giày Chạy Bộ Kiprun KD900X', 'Giày chạy bộ thi đấu, đệm carbon.', 3000000, 10, 'DEC-KD900X', 100, 'giay-chay-bo-kiprun-kd900x'),
('Giày Đá Banh Kipsta Viralto IV', 'Giày đá banh da thật, giá tốt.', 1500000, 10, 'DEC-VIRALTO4', 50, 'giay-da-banh-kipsta-viralto-iv'),
('Áo Thun Domyos Fit', 'Áo thun tập gym, giá rẻ, thoáng mát.', 150000, 10, 'DEC-DOMYOS-TEE', 200, 'ao-thun-domyos-fit'),
('Quần Short Domyos 2-in-1', 'Quần short tập gym, có lớp lót.', 350000, 10, 'DEC-DOMYOS-SHORT', 150, 'quan-short-domyos-2-in-1'),
('Quần Legging Domyos Nữ', 'Quần legging tập yoga, co giãn.', 250000, 10, 'DEC-DOMYOS-LEG', 100, 'quan-legging-domyos-nu'),
('Áo Khoác Gió Quechua MH100', 'Áo khoác gió dã ngoại, chống nước nhẹ.', 400000, 10, 'DEC-QUE-MH100', 80, 'ao-khoac-gio-quechua-mh100'),
('Áo Bra Domyos', 'Áo lót thể thao, hỗ trợ vừa.', 200000, 10, 'DEC-DOMYOS-BRA', 120, 'ao-bra-domyos'),
('Tất Chạy Bộ Kiprun (2 đôi)', 'Tất chạy bộ, chống phồng rộp.', 180000, 10, 'DEC-KIPRUN-SOCK', 300, 'tat-chay-bo-kiprun-2-doi'),
('Balo Quechua NH100 (20L)', 'Balo dã ngoại, đi học, giá rẻ.', 250000, 10, 'DEC-QUE-NH100', 100, 'balo-quechua-nh100-20l'),
('Thảm Tập Yoga Domyos 8mm', 'Thảm tập yoga, dày 8mm, êm ái.', 500000, 10, 'DEC-YOGA-MAT8', 60, 'tham-tap-yoga-domyos-8mm');


/*
=====================================================
BẢNG TIER 3 (PHỤ THUỘC SẢN PHẨM, USER...)
=====================================================
*/

-- ProductCategories (Liên kết 100 sản phẩm với 10 danh mục)
-- 10 sản phẩm đầu (1-10) là của Nike, 10 sản phẩm sau (11-20) là của Adidas...
-- 10 danh mục (1-10) là Giày chạy, Giày đá banh, Áo thun...
-- -> Sản phẩm 1, 11, 21, 31, 41, 51, 61, 71, 81, 91 là "Giày Chạy Bộ" (hoặc tương tự)
-- -> Sản phẩm 2, 12, 22, 32, 42, 52, 62, 72, 82, 92 là "Giày Đá Banh" (hoặc tương tự)
INSERT INTO ProductCategories (product_id, category_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7), (8, 8), (9, 9), (10, 10),
(11, 1), (12, 2), (13, 3), (14, 4), (15, 5), (16, 6), (17, 7), (18, 8), (19, 9), (20, 10),
(21, 1), (22, 2), (23, 3), (24, 4), (25, 5), (26, 6), (27, 7), (28, 8), (29, 9), (30, 10),
(31, 1), (32, 2), (33, 3), (34, 4), (35, 5), (36, 6), (37, 7), (38, 8), (39, 9), (40, 10),
(41, 1), (42, 1), (43, 3), (44, 4), (45, 5), (46, 6), (47, 7), (48, 8), (49, 9), (50, 8),
(51, 1), (52, 1), (53, 3), (54, 4), (55, 5), (56, 6), (57, 7), (58, 8), (59, 9), (60, 1),
(61, 7), (62, 1), (63, 3), (64, 4), (65, 5), (66, 6), (67, 7), (68, 8), (69, 9), (70, 10),
(71, 1), (72, 6), (73, 3), (74, 4), (75, 5), (76, 6), (77, 6), (78, 8), (79, 9), (80, 8),
(81, 1), (82, 6), (83, 3), (84, 4), (85, 5), (86, 3), (87, 6), (88, 8), (89, 9), (90, 8),
(91, 1), (92, 2), (93, 3), (94, 4), (95, 5), (96, 6), (97, 7), (98, 8), (99, 9), (100, 10);

-- ProductLabels (Gán nhãn cho 20 sản phẩm đầu tiên)
INSERT INTO ProductLabels (product_id, label_id) VALUES
(1, 1), (1, 2), (1, 8), (11, 1), (11, 2), (21, 1), (31, 8), (41, 2), (51, 1), (61, 2), (71, 7),
(2, 1), (2, 3), (12, 1), (22, 1), (32, 1), (92, 3), (10, 2), (20, 2), (30, 2), (40, 2), (50, 2), (60, 2);

-- ProductVariants (Tạo biến thể cho 10 sản phẩm đầu: 5 áo/quần, 5 giày)
-- Áo/Quần (ID 3, 4, 5, 6, 7): Size S, M, L (ID 1, 2, 3) + Màu Đen, Trắng (ID 1, 2)
-- Giày (ID 1, 2): Size 40, 41, 42 (ID 7, 8, 9) + Màu Đen, Xám (ID 1, 5)
INSERT INTO ProductVariants (product_id, color_id, size_id, sku, stock, price) VALUES
-- SP 1 (Giày Nike Peg40): Đen(1)+40(7), Đen(1)+41(8), Đen(1)+42(9), Xám(5)+40(7), Xám(5)+41(8), Xám(5)+42(9)
(1, 1, 7, 'NIKE-PEG40-BLK-40', 20, 3200000), (1, 1, 8, 'NIKE-PEG40-BLK-41', 20, 3200000), (1, 1, 9, 'NIKE-PEG40-BLK-42', 20, 3200000),
(1, 5, 7, 'NIKE-PEG40-GRY-40', 15, 3200000), (1, 5, 8, 'NIKE-PEG40-GRY-41', 15, 3200000), (1, 5, 9, 'NIKE-PEG40-GRY-42', 15, 3200000),
-- SP 2 (Giày Nike Merc9): Đỏ(3)+40(7), Đỏ(3)+41(8), Đỏ(3)+42(9), Vàng(8)+40(7), Vàng(8)+41(8), Vàng(8)+42(9)
(2, 3, 7, 'NIKE-MERC9-RED-40', 10, 4500000), (2, 3, 8, 'NIKE-MERC9-RED-41', 10, 4500000), (2, 3, 9, 'NIKE-MERC9-RED-42', 10, 4500000),
(2, 8, 7, 'NIKE-MERC9-YEL-40', 8, 4500000), (2, 8, 8, 'NIKE-MERC9-YEL-41', 8, 4500000), (2, 8, 9, 'NIKE-MERC9-YEL-42', 8, 4500000),
-- SP 3 (Áo Nike Miller): Đen(1)+S(1), Đen(1)+M(2), Đen(1)+L(3), Trắng(2)+S(1), Trắng(2)+M(2), Trắng(2)+L(3)
(3, 1, 1, 'NIKE-MILLER-T-BLK-S', 30, 850000), (3, 1, 2, 'NIKE-MILLER-T-BLK-M', 30, 850000), (3, 1, 3, 'NIKE-MILLER-T-BLK-L', 30, 850000),
(3, 2, 1, 'NIKE-MILLER-T-WHT-S', 25, 850000), (3, 2, 2, 'NIKE-MILLER-T-WHT-M', 25, 850000), (3, 2, 3, 'NIKE-MILLER-T-WHT-L', 25, 850000),
-- SP 4 (Quần Nike Chal): Đen(1)+S(1), Đen(1)+M(2), Đen(1)+L(3), Xám(5)+S(1), Xám(5)+M(2), Xám(5)+L(3)
(4, 1, 1, 'NIKE-CHAL-SHORT-BLK-S', 20, 1100000), (4, 1, 2, 'NIKE-CHAL-SHORT-BLK-M', 20, 1100000), (4, 1, 3, 'NIKE-CHAL-SHORT-BLK-L', 20, 1100000),
(4, 5, 1, 'NIKE-CHAL-SHORT-GRY-S', 15, 1100000), (4, 5, 2, 'NIKE-CHAL-SHORT-GRY-M', 15, 1100000), (4, 5, 3, 'NIKE-CHAL-SHORT-GRY-L', 15, 1100000),
-- SP 11 (Giày Adidas Ultra): Trắng(2)+40(7), Trắng(2)+41(8), Trắng(2)+42(9), Xanh(4)+40(7), Xanh(4)+41(8), Xanh(4)+42(9)
(11, 2, 7, 'ADI-UBLIGHT-WHT-40', 20, 4800000), (11, 2, 8, 'ADI-UBLIGHT-WHT-41', 20, 4800000), (11, 2, 9, 'ADI-UBLIGHT-WHT-42', 20, 4800000),
(11, 4, 7, 'ADI-UBLIGHT-BLU-40', 15, 4800000), (11, 4, 8, 'ADI-UBLIGHT-BLU-41', 15, 4800000), (11, 4, 9, 'ADI-UBLIGHT-BLU-42', 15, 4800000),
-- SP 13 (Áo Adidas OTR): Đen(1)+S(1), Đen(1)+M(2), Đen(1)+L(3), Cam(7)+S(1), Cam(7)+M(2), Cam(7)+L(3)
(13, 1, 1, 'ADI-OTR-TEE-BLK-S', 30, 950000), (13, 1, 2, 'ADI-OTR-TEE-BLK-M', 30, 950000), (13, 1, 3, 'ADI-OTR-TEE-BLK-L', 30, 950000),
(13, 7, 1, 'ADI-OTR-TEE-ORN-S', 20, 950000), (13, 7, 2, 'ADI-OTR-TEE-ORN-M', 20, 950000), (13, 7, 3, 'ADI-OTR-TEE-ORN-L', 20, 950000),
-- SP 14 (Quần Adidas 3S): Đen(1)+S(1), Đen(1)+M(2), Đen(1)+L(3)
(14, 1, 1, 'ADI-3S-SHORT-BLK-S', 50, 750000), (14, 1, 2, 'ADI-3S-SHORT-BLK-M', 50, 750000), (14, 1, 3, 'ADI-3S-SHORT-BLK-L', 50, 750000),
-- SP 21 (Giày Puma Velo): Đen(1)+40(7), Đen(1)+41(8), Xanh(4)+41(8)
(21, 1, 7, 'PUMA-VELO-N2-BLK-40', 10, 2800000), (21, 1, 8, 'PUMA-VELO-N2-BLK-41', 10, 2800000), (21, 4, 8, 'PUMA-VELO-N2-BLU-41', 10, 2800000),
-- SP 23 (Áo Puma Ess): Trắng(2)+S(1), Trắng(2)+M(2), Trắng(2)+L(3)
(23, 2, 1, 'PUMA-ESS-TEE-WHT-S', 40, 600000), (23, 2, 2, 'PUMA-ESS-TEE-WHT-M', 40, 600000), (23, 2, 3, 'PUMA-ESS-TEE-WHT-L', 40, 600000),
-- SP 31 (Giày UA HOVR): Xám(5)+40(7), Xám(5)+41(8), Xám(5)+42(9)
(31, 5, 7, 'UA-HOVR-PH3-GRY-40', 15, 3800000), (31, 5, 8, 'UA-HOVR-PH3-GRY-41', 15, 3800000), (31, 5, 9, 'UA-HOVR-PH3-GRY-42', 15, 3800000);


-- ProductImages (Thêm 2 ảnh cho 10 sản phẩm đầu)
INSERT INTO ProductImages (product_id, image_url, alt_text, is_thumbnail) VALUES
(1, 'nike_peg40_1.jpg', 'Nike Pegasus 40 - Ảnh 1', TRUE), (1, 'nike_peg40_2.jpg', 'Nike Pegasus 40 - Ảnh 2', FALSE),
(2, 'nike_merc9_1.jpg', 'Nike Mercurial 9 - Ảnh 1', TRUE), (2, 'nike_merc9_2.jpg', 'Nike Mercurial 9 - Ảnh 2', FALSE),
(3, 'nike_miller_1.jpg', 'Áo Nike Miller - Ảnh 1', TRUE), (3, 'nike_miller_2.jpg', 'Áo Nike Miller - Ảnh 2', FALSE),
(4, 'nike_chal_1.jpg', 'Quần Nike Challenger - Ảnh 1', TRUE), (4, 'nike_chal_2.jpg', 'Quần Nike Challenger - Ảnh 2', FALSE),
(5, 'nike_pro_1.jpg', 'Quần Legging Nike Pro - Ảnh 1', TRUE), (5, 'nike_pro_2.jpg', 'Quần Legging Nike Pro - Ảnh 2', FALSE),
(11, 'adi_ublight_1.jpg', 'Adidas Ultraboost Light - Ảnh 1', TRUE), (11, 'adi_ublight_2.jpg', 'Adidas Ultraboost Light - Ảnh 2', FALSE),
(12, 'adi_pred_1.jpg', 'Adidas Predator - Ảnh 1', TRUE), (12, 'adi_pred_2.jpg', 'Adidas Predator - Ảnh 2', FALSE),
(13, 'adi_otr_1.jpg', 'Áo Adidas OTR - Ảnh 1', TRUE), (13, 'adi_otr_2.jpg', 'Áo Adidas OTR - Ảnh 2', FALSE),
(21, 'puma_velo_1.jpg', 'Puma Velocity - Ảnh 1', TRUE), (21, 'puma_velo_2.jpg', 'Puma Velocity - Ảnh 2', FALSE),
(31, 'ua_hovr_1.jpg', 'UA HOVR Phantom 3 - Ảnh 1', TRUE), (31, 'ua_hovr_2.jpg', 'UA HOVR Phantom 3 - Ảnh 2', FALSE);

-- FlashSaleProducts (Thêm 10 sản phẩm vào Flash Sale 7: "Running Day")
INSERT INTO FlashSaleProducts (flash_sale_id, product_id, flash_sale_price, quantity) VALUES
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
INSERT INTO Reviews (user_id, product_id, rating, title, content, status) VALUES
(2, 1, 5, 'Tuyệt vời!', 'Giày êm, chạy rất thích. Giao hàng nhanh.', 'APPROVED'),
(3, 11, 5, 'Rất hài lòng', 'Đúng là Ultraboost, nhẹ và êm, đáng tiền.', 'APPROVED'),
(4, 21, 4, 'Khá tốt', 'Giày đẹp, đệm êm, nhưng form hơi nhỏ.', 'APPROVED'),
(5, 31, 5, 'Xuất sắc!', 'Kết nối app rất hay, giày chạy nảy.', 'APPROVED'),
(6, 41, 5, 'Đỉnh cao ổn định', 'Mình bị lật cổ chân, đi Kayano rất tự tin.', 'APPROVED'),
(7, 51, 4, 'Êm, thời trang', 'Giày êm, đi hàng ngày cũng đẹp. Hơi nóng.', 'PENDING'),
(8, 2, 5, 'Đá sướng', 'Giày ôm chân, sút bóng cảm giác tốt.', 'APPROVED'),
(9, 3, 4, 'Vải mỏng mát', 'Áo mặc tập gym thoáng, nhưng hơi đắt.', 'APPROVED'),
(10, 13, 5, 'Màu đẹp', 'Áo màu cam nổi bật, vải mịn, thấm mồ hôi tốt.', 'REJECTED'),
(2, 4, 3, 'Bình thường', 'Quần cũng được, túi hơi bé.', 'APPROVED');

-- UserVouchers (Gán voucher cho user)
INSERT INTO UserVouchers (user_id, voucher_id) VALUES
(2, 3), (3, 3), (4, 1), (5, 2), (6, 1), (7, 4), (8, 2), (9, 5), (10, 9), (1, 1);

-- OrderItems (Chi tiết cho 10 đơn hàng)
-- Order 1 (User 2): Mua SP 1 (Giày Nike)
INSERT INTO OrderItems (order_id, product_id, quantity, unit_price, total_price) VALUES
(1, 1, 1, 2500000, 2500000);
-- Order 2 (User 3): Mua SP 13 (Áo Adidas), SP 14 (Quần Adidas)
INSERT INTO OrderItems (order_id, product_id, quantity, unit_price, total_price) VALUES
(2, 13, 1, 950000, 950000),
(2, 14, 1, 750000, 750000);
-- Order 3 (User 4): Mua SP 3 (Áo Nike)
INSERT INTO OrderItems (order_id, product_id, quantity, unit_price, total_price) VALUES
(3, 3, 1, 850000, 850000);
-- Order 4 (User 5): Mua SP 31 (Giày UA)
INSERT INTO OrderItems (order_id, product_id, quantity, unit_price, total_price) VALUES
(4, 31, 1, 3200000, 3200000);
-- Order 5 (User 2): Mua SP 8 (Tất Nike)
INSERT INTO OrderItems (order_id, product_id, quantity, unit_price, total_price) VALUES
(5, 8, 1, 450000, 450000);
-- Order 6 (User 6): Mua SP 51 (Giày NB)
INSERT INTO OrderItems (order_id, product_id, quantity, unit_price, total_price) VALUES
(6, 51, 1, 1800000, 1800000);
-- Order 7 (User 7): Mua SP 23 (Áo Puma)
INSERT INTO OrderItems (order_id, product_id, quantity, unit_price, total_price) VALUES
(7, 23, 1, 750000, 750000);
-- Order 8 (User 8): Mua SP 61 (Giày Reebok)
INSERT INTO OrderItems (order_id, product_id, quantity, unit_price, total_price) VALUES
(8, 61, 1, 2100000, 2100000);
-- Order 9 (User 9): Mua SP 40 (Găng tay UA)
INSERT INTO OrderItems (order_id, product_id, quantity, unit_price, total_price) VALUES
(9, 40, 1, 550000, 550000);
-- Order 10 (User 10): Mua SP 93 (Áo Domyos), SP 95 (Quần Domyos)
INSERT INTO OrderItems (order_id, product_id, quantity, unit_price, total_price) VALUES
(10, 93, 2, 150000, 300000),
(10, 95, 2, 250000, 500000),
(10, 100, 1, 180000, 180000);

-- OrderVouchers (Áp dụng voucher cho một số đơn hàng)
INSERT INTO OrderVouchers (order_id, voucher_id, discount_amount) VALUES
(2, 2, 30000), -- Order 2 dùng FREESHIP
(5, 1, 45000), -- Order 5 dùng SALE10 (10% của 450k)
(9, 4, 50000); -- Order 9 dùng GIAM50K

-- Payments (Thanh toán cho các đơn hàng đã 'PAID')
INSERT INTO Payments (order_id, amount, payment_method, transaction_id, status) VALUES
(1, 2500000, 'CREDIT_CARD', 'VNPAY_001', 'SUCCESS'),
(2, 1170000, 'COD', 'COD_002', 'SUCCESS'),
(3, 850000, 'PAYPAL', 'PAYPAL_003', 'SUCCESS'),
(6, 1800000, 'BANK_TRANSFER', 'BANK_004', 'SUCCESS'),
(7, 750000, 'CREDIT_CARD', 'VNPAY_005', 'SUCCESS'),
(8, 2100000, 'COD', 'COD_006', 'SUCCESS'),
-- Đơn hàng 4, 5, 9, 10 chưa thanh toán hoặc đã hủy
(4, 3200000, 'CREDIT_CARD', 'VNPAY_007', 'PENDING'),
(5, 420000, 'COD', 'COD_008', 'FAILED'),
(9, 500000, 'COD', 'COD_009', 'PENDING'),
(10, 980000, 'BANK_TRANSFER', 'BANK_010', 'PENDING');

-- Messages (10 tin nhắn trong cuộc hội thoại 1 và 3)
INSERT INTO Messages (content, sender_username, sender_role, sender_full_name, receiver_username, room_id) VALUES
('Chào shop, đơn hàng DH000001 của tôi đã giao chưa?', 'an.tran@gmail.com', 'CLIENT', 'Trần Văn An', 'admin@shop.com', 'CONV_1'),
('Chào bạn, để mình kiểm tra nhé.', 'admin@shop.com', 'ADMIN', 'Nguyễn Văn Quản Trị', 'an.tran@gmail.com', 'CONV_1'),
('Đơn hàng DH000001 đã được giao thành công ngày hôm qua ạ.', 'admin@shop.com', 'ADMIN', 'Nguyễn Văn Quản Trị', 'an.tran@gmail.com', 'CONV_1'),
('OK, cảm ơn shop.', 'an.tran@gmail.com', 'CLIENT', 'Trần Văn An', 'admin@shop.com', 'CONV_1'),
('Shop ơi, tôi muốn tư vấn size giày Adidas.', 'binh.le@yahoo.com', 'CLIENT', 'Lê Thị Bình', 'admin@shop.com', 'CONV_2'),
('Chào bạn, bạn thường đi size bao nhiêu?', 'admin@shop.com', 'ADMIN', 'Nguyễn Văn Quản Trị', 'binh.le@yahoo.com', 'CONV_2'),
('Tôi mua sản phẩm Nike Mercurial, bị lỗi keo ở mũi giày.', 'cuong.pham@outlook.com', 'CLIENT', 'Phạm Văn Cường', 'admin@shop.com', 'CONV_3'),
('Bạn vui lòng chụp ảnh sản phẩm gửi cho shop nhé.', 'admin@shop.com', 'ADMIN', 'Nguyễn Văn Quản Trị', 'cuong.pham@outlook.com', 'CONV_3'),
('Đây nhé shop. [hình ảnh]', 'cuong.pham@outlook.com', 'CLIENT', 'Phạm Văn Cường', 'admin@shop.com', 'CONV_3'),
('Shop đã nhận được. Shop sẽ xử lý khiếu nại này ngay.', 'admin@shop.com', 'ADMIN', 'Nguyễn Văn Quản Trị', 'cuong.pham@outlook.com', 'CONV_3');


/*
=====================================================
BẢNG TIER 4 (PHỤ THUỘC TIER 3)
=====================================================
*/

-- ProductVariantImages (Ảnh cho các biến thể - VD: ảnh giày màu đen, màu trắng)
-- Lấy 10 ID biến thể đầu tiên
INSERT INTO ProductVariantImages (product_variant_id, image_url) VALUES
(1, 'nike_peg40_blk.jpg'), (2, 'nike_peg40_blk.jpg'), (3, 'nike_peg40_blk.jpg'),
(4, 'nike_peg40_gry.jpg'), (5, 'nike_peg40_gry.jpg'), (6, 'nike_peg40_gry.jpg'),
(7, 'nike_merc9_red.jpg'), (8, 'nike_merc9_red.jpg'),
(10, 'nike_merc9_yel.jpg'), (11, 'nike_merc9_yel.jpg');

-- CartItems (Thêm sản phẩm vào giỏ hàng cho 5 user đầu)
-- (cart_id, product_id, product_variant_id, quantity, unit_price, total_price)
INSERT INTO CartItems (cart_id, product_id, product_variant_id, quantity, unit_price, total_price) VALUES
(2, 1, 1, 1, 3200000, 3200000), -- User 2, SP 1 (variant 1: Đen, 40)
(3, 11, 25, 1, 4800000, 4800000), -- User 3, SP 11 (variant 25: Trắng, 40)
(3, 13, 31, 2, 950000, 1900000), -- User 3, SP 13 (variant 31: Đen, S)
(4, 99, NULL, 1, 250000, 250000), -- User 4, SP 99 (ko có variant)
(5, 79, NULL, 1, 2600000, 2600000), -- User 5, SP 79 (Balo TNF)
(6, 3, 13, 1, 850000, 850000),
(7, 4, 19, 1, 1100000, 1100000),
(8, 21, 40, 1, 2800000, 2800000),
(9, 31, 46, 1, 3800000, 3800000),
(10, 100, NULL, 1, 500000, 500000);

-- WishlistItems (Thêm sản phẩm vào wishlist cho 5 user)
INSERT INTO WishlistItems (wishlist_id, product_id, product_variant_id) VALUES
(2, 11, 26), -- User 2, SP 11 (variant 26: Trắng, 41)
(3, 72, NULL), -- User 3, SP 72 (Áo khoác TNF)
(4, 76, NULL), -- User 4, SP 76 (Áo lông vũ TNF)
(5, 61, NULL), -- User 5, SP 61 (Giày Reebok Nano)
(6, 5, 14), -- User 6, SP 5 (variant 14)
(7, 10, NULL),
(8, 20, NULL),
(9, 30, NULL),
(10, 40, NULL),
(1, 50, NULL);

-- MessageAttachments (Đính kèm cho tin nhắn 9)
INSERT INTO MessageAttachments (message_id, file_url, file_name, file_size, file_type) VALUES
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