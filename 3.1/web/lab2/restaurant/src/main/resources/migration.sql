
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS statuses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    balance DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category_id INT NOT NULL,
    
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    status_id INT NOT NULL,
    
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (status_id) REFERENCES statuses(id)
);

CREATE TABLE IF NOT EXISTS order_items (
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    quantity INT NOT NULL,

    PRIMARY KEY (order_id, menu_item_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);


INSERT IGNORE INTO categories (category) VALUES ('Main Course');
INSERT IGNORE INTO categories (category) VALUES ('Starter');
INSERT IGNORE INTO categories (category) VALUES ('Dessert');
INSERT IGNORE INTO categories (category) VALUES ('Drink');

INSERT IGNORE INTO statuses (status) VALUES ('Pending');
INSERT IGNORE INTO statuses (status) VALUES ('Cooking');
INSERT IGNORE INTO statuses (status) VALUES ('Cooked');
INSERT IGNORE INTO statuses (status) VALUES ('Completed');

INSERT IGNORE INTO menu_items (name, price, category_id) VALUES ('Burger', 8.50, 1);
INSERT IGNORE INTO menu_items (name, price, category_id) VALUES ('Salad', 5.00, 2);
INSERT IGNORE INTO menu_items (name, price, category_id) VALUES ('Ice Cream', 3.50, 3);
INSERT IGNORE INTO menu_items (name, price, category_id) VALUES ('Coffee', 2.50, 4);
