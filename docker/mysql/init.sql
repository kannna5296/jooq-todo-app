-- ほんまはflyway使うべきだけどめんどくせーのではしょる
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL
);

CREATE TABLE todos (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  title VARCHAR(255) NOT NULL,
  done BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE todo_logs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  todo_id INT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  FOREIGN KEY (todo_id) REFERENCES todos(id)
);
