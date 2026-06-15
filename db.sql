DROP SCHEMA IF EXISTS datn_shopping;

CREATE SCHEMA datn_shopping;

USE datn_shopping;

CREATE TABLE categories(
	id	 				INT AUTO_INCREMENT PRIMARY KEY,
    `name`				VARCHAR(100) NOT NULL,
    is_accessory			BIT NOT NULL,
    `description` 		TEXT
);

CREATE TABLE brands(
	id	 				INT AUTO_INCREMENT PRIMARY KEY,
    `name`				VARCHAR(100) NOT NULL,
    logo 				VARCHAR(255)
);

CREATE TABLE target_audiences(
	id	 				INT AUTO_INCREMENT PRIMARY KEY,
    `name`				VARCHAR(50) NOT NULL		
);

CREATE TABLE products(
	id					INT AUTO_INCREMENT PRIMARY KEY,
    `name`				NVARCHAR(500) NOT NULL,
    `description` 		TEXT,
    base_price 			DECIMAL(10,2) NOT NULL,
    
    category_id 		INT NOT NULL,
    brand_id			INT NOT NULL,
    target_audiences_id	INT NOT NULL,
    
	created_at 			TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at 			TIMESTAMP DEFAULT CURRENT_TIMESTAMP
						ON UPDATE CURRENT_TIMESTAMP,

    deleted_at 			TIMESTAMP NULL,
    
    CONSTRAINT FK_PC FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT FK_PB FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT FK_PG FOREIGN KEY (target_audiences_id) REFERENCES target_audiences(id)
);

CREATE TABLE product_images(
	id					INT AUTO_INCREMENT PRIMARY KEY,
    product_id 			INT NOT NULL,
    image_url			VARCHAR(255) NOT NULL,
    is_primary			BIT DEFAULT 0,
    
    CONSTRAINT FK_PI FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE sizes(
	id					INT AUTO_INCREMENT PRIMARY KEY,
    `name` 				varchar(20) not null
);

CREATE TABLE product_variants(
	id					INT AUTO_INCREMENT PRIMARY KEY,
    
    product_id 			INT NOT NULL,
    
    color 				VARCHAR(50),
    size_id				INT NOT NULL,
    
    stock 				INT DEFAULT 0,
    
    price 				DECIMAL(10,2) NOT NULL,
    
    sku					VARCHAR(100) UNIQUE,
    
    created_at 			TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(product_id, color, size_id),
    
    CONSTRAINT FK_PV_P FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT FK_PV_S FOREIGN KEY (size_id) REFERENCES sizes(id)
);

CREATE TABLE users(
	id					INT AUTO_INCREMENT PRIMARY KEY,
    full_name 			NVARCHAR(100) NOT NULL,
    email				VARCHAR(255) NOT NULL UNIQUE,
    `password`			VARCHAR(500) NOT NULL,
    phone				VARCHAR(15) NOT NULL,
    birth_day           TIMESTAMP NOT NULL,
     -- 'local' hoặc 'google'
    provider      		VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
 
    -- false cho đến khi kích hoạt qua email
    is_actived       	BOOLEAN NOT NULL DEFAULT FALSE,
    
    created_at			TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE addresses(
	id					INT AUTO_INCREMENT PRIMARY KEY,
    address				TEXT NOT NULL,
    user_id				INT NOT NULL,
    CONSTRAINT FK_AD_U FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE roles(
	id 					INT AUTO_INCREMENT PRIMARY KEY,
    `name`				VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles(
	user_id				INT NOT NULL,
    role_id				INT NOT NULL,
    PRIMARY KEY(user_id, role_id),
    CONSTRAINT FK_UR_U FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT FK_UR_R FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE product_reviews(

    user_id				INT NOT NULL,
    
    product_id			INT NOT NULL,
    
    rating 				INT NOT NULL,
    
    `comment`			TEXT,
    
    created_at 			TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CHECK (rating BETWEEN 1 AND 5),
    
    PRIMARY KEY(user_id, product_id),
    
    CONSTRAINT FK_PR_U FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT FK_PR_P FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE vouchers(
	id 					INT AUTO_INCREMENT PRIMARY KEY,
    
    `code`				VARCHAR(50) UNIQUE NOT NULL,
    
    `description`		TEXT,
    
    discount_type		ENUM('PERCENT', 'FIXED') NOT NULL,
    
    discount_value		DECIMAL(10,2) NOT NULL,
    
    min_order_value DECIMAL(10,2) DEFAULT 0,

    max_discount DECIMAL(10,2),

    quantity INT DEFAULT 0,

    used_count INT DEFAULT 0,

    start_date TIMESTAMP,
    end_date TIMESTAMP,

    is_active BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_methods(
	id					INT AUTO_INCREMENT PRIMARY KEY,
    `name`				VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE orders(
	id 					INT AUTO_INCREMENT PRIMARY KEY,
    user_id 			INT NOT NULL,
    total_price			DECIMAL(10,2),
	` status` 			ENUM(
							'PENDING',
							'CONFIRMED',
							'SHIPPING',
							'DELIVERED',
							'CANCELLED'
							)
						DEFAULT 'PENDING',
    
    voucher_id			INT,
    discount_amount		DECIMAL(10,2) DEFAULT 0,
    final_price			DECIMAL(10,2),
    
    shipping_address	TEXT NOT NULL,
    reciever_name       VARCHAR(100) NOT NULL,
    reviever_phone      VARCHAR(15) NOT NULL,

    payment_method_id	INT NOT NULL,
    created_at			TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at			TIMESTAMP DEFAULT CURRENT_TIMESTAMP
						ON UPDATE CURRENT_TIMESTAMP,
	
    
    
    CONSTRAINT FK_O_U FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT FK_O_V FOREIGN KEY (voucher_id) REFERENCES vouchers(id),
    CONSTRAINT FK_O_P FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id)
);

CREATE TABLE order_details(
	id					INT AUTO_INCREMENT PRIMARY KEY,
    
    order_id			INT NOT NULL,
    
    product_variant_id	INT NOT NULL,
    
    quantity 			INT NOT NULL,
    
    product_name		VARCHAR(255) NOT NULL,
	color 				VARCHAR(50),
	size_name 			VARCHAR(20),
    
    price				DECIMAL(10,2) NOT NULL,
    
    CONSTRAINT FK_OD_O FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT FK_OD_PV FOREIGN KEY (product_variant_id) REFERENCES product_variants(id)
);

CREATE TABLE carts(
	id					INT AUTO_INCREMENT PRIMARY KEY,
    
    user_id				INT NOT NULL UNIQUE,
    
    CONSTRAINT FK_C_U FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE cart_items(
	id 					INT AUTO_INCREMENT PRIMARY KEY,
    
    cart_id				INT NOT NULL,
    
    product_variant_id	INT NOT NULL,
    
    quantity			INT NOT NULL,
    
    UNIQUE(cart_id, product_variant_id),
    
    CONSTRAINT FK_CI_C FOREIGN KEY (cart_id) REFERENCES carts(id),
    CONSTRAINT FK_CI_PV FOREIGN KEY (product_variant_id) REFERENCES product_variants(id)
);

CREATE TABLE verification_tokens(
	id 					INT AUTO_INCREMENT PRIMARY KEY,
    token				VARCHAR(500) NOT NULL,
    expiry_date			TIMESTAMP NOT NULL,
    user_id             INT NOT NULL UNIQUE,
    CONSTRAINT FK_VRFT_U FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE password_reset_tokens(
    id 					INT AUTO_INCREMENT PRIMARY KEY,
    token				VARCHAR(500) NOT NULL,     
    expiry_date			TIMESTAMP NOT NULL,
    email				VARCHAR(255) NOT NULL UNIQUE,
)

INSERT INTO roles(`name`) values("USER"),("ADMIN");

INSERT INTO target_audiences(`name`) VALUES
('Men'),
('Women'),
('Kids'),
('Unisex');

INSERT INTO categories (`name`, is_accessory, `description`) VALUES
('Giày sân cỏ tự nhiên', 0, 'Giày đá bóng dành cho sân cỏ tự nhiên với đinh FG/SG'),
('Giày sân cỏ nhân tạo', 0, 'Giày đá bóng dành cho sân cỏ nhân tạo với đinh AG/TF'),
('Giày futsal', 0, 'Giày đá bóng trong nhà dành cho sân futsal'),
('Áo bóng đá', 0, 'Áo thi đấu và áo tập bóng đá'),
('Quần bóng đá', 0, 'Quần thi đấu và quần tập bóng đá'),
('Bộ quần áo bóng đá', 0, 'Set áo và quần bóng đá'),
('Tất bóng đá', 1, 'Tất và vớ chuyên dụng cho bóng đá'),
('Găng tay thủ môn', 1, 'Găng tay dành cho thủ môn'),
('Bóng đá', 1, 'Các loại bóng thi đấu và tập luyện'),
('Balo bóng đá', 1, 'Balo và túi đựng đồ thể thao bóng đá'),
('Túi đựng giày', 1, 'Túi chuyên dụng để đựng giày bóng đá'),
('Áo khoác thể thao', 0, 'Áo khoác và đồ giữ ấm thể thao'),
('Băng bảo vệ', 1, 'Băng gối, băng cổ chân và phụ kiện bảo vệ'),
('Ống đồng', 1, 'Phụ kiện bảo vệ ống chân khi thi đấu'),
('Phụ kiện bóng đá', 1, 'Các phụ kiện hỗ trợ cho bóng đá');

INSERT INTO sizes (`name`) VALUES
('38'),
('39'),
('40'),
('41'),
('42'),
('43'),
('S'),
('M'),
('L'),
('XL'),
('Free Size');

INSERT INTO brands (`name`, logo) VALUES
('Nike', 'nike.png'),
('Adidas', 'adidas.png'),
('Puma', 'puma.png'),
('Umbro', 'umbro.png'),
('Mizuno', 'mizuno.png'),
('Joma', 'joma.png'),
('Kelme', 'kelme.png'),
('Kappa', 'kappa.png'),
('Lotto', 'lotto.png'),
('Diadora', 'diadora.png'),
('New Balance', 'new-balance.png'),
('Hummel', 'hummel.png'),
('Macron', 'macron.png');


INSERT INTO products (
    `name`,
    `description`,
    base_price,
    category_id,
    brand_id,
    target_audiences_id
) VALUES

(
    'Nike Mercurial Vapor 16 Academy TF',
    'Giày đá bóng sân cỏ nhân tạo Nike Mercurial',
    2490000,
    2,
    1,
    1
),

(
    'Adidas Predator League TF',
    'Giày đá bóng Adidas Predator dành cho sân cỏ nhân tạo',
    2790000,
    2,
    2,
    1
),

(
    'Puma Future 7 Match IT',
    'Giày futsal Puma Future',
    2190000,
    3,
    3,
    1
),

(
    'Nike Tiempo Legend 10 Academy Kids TF',
    'Giày đá bóng trẻ em Nike Tiempo',
    1790000,
    2,
    1,
    3
),

(
    'Adidas X Crazyfast Women TF',
    'Giày đá bóng nữ Adidas X Crazyfast',
    2590000,
    2,
    2,
    2
),

(
    'Áo CLB Real Madrid 2025',
    'Áo đấu sân nhà Real Madrid mùa giải 2025',
    1890000,
    4,
    2,
    1
),

(
    'Áo Manchester City Women',
    'Áo đấu Manchester City dành cho nữ',
    1690000,
    4,
    3,
    2
),

(
    'Balo Nike Academy',
    'Balo thể thao bóng đá Nike Academy',
    990000,
    10,
    1,
    4
),

(
    'Bóng Adidas UCL League',
    'Bóng đá UEFA Champions League Adidas',
    1290000,
    9,
    2,
    4
),

(
    'Găng Tay Thủ Môn Puma Ultra',
    'Găng tay thủ môn Puma Ultra Grip',
    1490000,
    8,
    3,
    1
);

INSERT INTO product_images (product_id, image_url,is_primary) VALUES
(1, 'products/nike-mercurial-vapor-16-academy-tf.jpg', 1),
(2, 'products/adidas-predator-league-tf.jpg', 1),
(3, 'products/puma-future-7-match-it.jpg', 1),
(4, 'products/nike-tiempo-legend-10-kids-tf.jpg', 1),
(5, 'products/adidas-x-crazyfast-women-tf.jpg', 1),
(6, 'products/ao-real-madrid-2025.jpg', 1),
(7, 'products/ao-manchester-city-women.jpg', 1),
(8, 'products/balo-nike-academy.jpg', 1),
(9, 'products/adidas-ucl-league-ball.jpg', 1),
(10, 'products/puma-ultra-goalkeeper-gloves.jpg', 1);

INSERT INTO product_variants (
    product_id,
    color,
    size_id,
    stock,
    price,
    sku
) VALUES

-- Nike Mercurial
(1, 'Blue', 3, 10, 2490000, 'NK-MER-TF-40-BL'),
(1, 'Blue', 4, 8, 2490000, 'NK-MER-TF-41-BL'),
(1, 'Blue', 5, 6, 2490000, 'NK-MER-TF-42-BL'),

-- Adidas Predator
(2, 'White', 4, 12, 2790000, 'AD-PRE-TF-41-WH'),
(2, 'White', 5, 9, 2790000, 'AD-PRE-TF-42-WH'),

-- Puma Future
(3, 'Black', 4, 7, 2190000, 'PM-FUT-IT-41-BK'),
(3, 'Black', 5, 5, 2190000, 'PM-FUT-IT-42-BK'),

-- Nike Tiempo Kids
(4, 'Orange', 1, 10, 1790000, 'NK-KID-TF-38-OR'),
(4, 'Orange', 2, 8, 1790000, 'NK-KID-TF-39-OR'),

-- Adidas Women
(5, 'Pink', 3, 6, 2590000, 'AD-WMN-TF-40-PK'),
(5, 'Pink', 4, 5, 2590000, 'AD-WMN-TF-41-PK'),

-- Real Madrid Shirt
(6, 'White', 8, 20, 1890000, 'RM-M-WH'),
(6, 'White', 9, 15, 1890000, 'RM-L-WH'),
(6, 'White', 10, 10, 1890000, 'RM-XL-WH'),

-- MC Women Shirt
(7, 'Blue', 7, 12, 1690000, 'MC-WMN-S-BL'),
(7, 'Blue', 8, 10, 1690000, 'MC-WMN-M-BL'),

-- Nike Backpack
(8, 'Black', 11, 25, 990000, 'NK-BAG-FREE-BK'),

-- Adidas Ball
(9, 'White', 11, 30, 1290000, 'AD-BALL-UCL-WH'),

-- Goalkeeper Gloves
(10, 'Green', 8, 8, 1490000, 'PM-GK-M-GR'),
(10, 'Green', 9, 6, 1490000, 'PM-GK-L-GR');

