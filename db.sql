DROP SCHEMA IF EXISTS datn_shopping;

CREATE SCHEMA datn_shopping;

USE datn_shopping;

CREATE TABLE categories(
	id	 				INT AUTO_INCREMENT PRIMARY KEY,
    `name`				VARCHAR(100) NOT NULL,
    `description` 		TEXT
);

CREATE TABLE brands(
	id	 				INT AUTO_INCREMENT PRIMARY KEY,
    `name`				VARCHAR(100) NOT NULL,
    logo 				VARCHAR(255)
);

CREATE TABLE genders(
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
    gender_id			INT NOT NULL,
    
	created_at 			TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at 			TIMESTAMP DEFAULT CURRENT_TIMESTAMP
						ON UPDATE CURRENT_TIMESTAMP,

    deleted_at 			TIMESTAMP NULL,
    
    CONSTRAINT FK_PC FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT FK_PB FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT FK_PG FOREIGN KEY (gender_id) REFERENCES genders(id)
);

CREATE TABLE product_images(
	id					INT AUTO_INCREMENT PRIMARY KEY,
    product_id 			INT NOT NULL,
    image_url			VARCHAR(255) NOT NULL,
    
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
)