-- =====================================================================
--  Database  : db_penjualan_tiket  (MySQL 5.7+ / MariaDB 10.3+)
--  Aplikasi  : Sistem Penjualan Tiket Konser "KonserKu"
--  Cara pakai: import file ini lewat phpMyAdmin (tab Import) atau:
--              mysql -u root -p < db_penjualan_tiket.sql
-- =====================================================================
DROP DATABASE IF EXISTS db_penjualan_tiket;
CREATE DATABASE db_penjualan_tiket CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_penjualan_tiket;

-- ---------- TABEL ----------
CREATE TABLE users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nama          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash CHAR(64)     NOT NULL,              -- SHA-256 hex
    role          ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER'
) ENGINE=InnoDB;

CREATE TABLE event (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nama      VARCHAR(150) NOT NULL,
    tanggal   DATETIME     NOT NULL,
    lokasi    VARCHAR(150) NOT NULL,
    deskripsi TEXT,
    status    ENUM('AKTIF','NONAKTIF') NOT NULL DEFAULT 'AKTIF'
) ENGINE=InnoDB;

CREATE TABLE zona (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    nama     VARCHAR(50) NOT NULL,
    tipe     ENUM('NUMBERED','FREE_SEATING') NOT NULL,
    CONSTRAINT fk_zona_event FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE tiket (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    zona_id  INT NOT NULL,
    kategori VARCHAR(50) NOT NULL,
    jenis    ENUM('VIP','FESTIVAL','EARLY_BIRD') NOT NULL,
    harga    DECIMAL(12,2) NOT NULL CHECK (harga >= 0),
    stok     INT NOT NULL CHECK (stok >= 0),
    CONSTRAINT fk_tiket_zona FOREIGN KEY (zona_id) REFERENCES zona(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE seat (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    zona_id INT NOT NULL,
    nomor   VARCHAR(10) NOT NULL,
    status  ENUM('AVAILABLE','SOLD') NOT NULL DEFAULT 'AVAILABLE',
    UNIQUE KEY uq_seat (zona_id, nomor),
    CONSTRAINT fk_seat_zona FOREIGN KEY (zona_id) REFERENCES zona(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE transaksi (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    tanggal DATETIME NOT NULL,
    total   DECIMAL(14,2) NOT NULL,
    status  ENUM('BERHASIL','DIBATALKAN') NOT NULL DEFAULT 'BERHASIL',
    CONSTRAINT fk_trx_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE detail_transaksi (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    transaksi_id INT NOT NULL,
    tiket_id     INT NOT NULL,
    event_id     INT NOT NULL,
    qty          INT NOT NULL CHECK (qty > 0),
    harga_satuan DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_det_trx   FOREIGN KEY (transaksi_id) REFERENCES transaksi(id) ON DELETE CASCADE,
    CONSTRAINT fk_det_tiket FOREIGN KEY (tiket_id)     REFERENCES tiket(id),
    CONSTRAINT fk_det_event FOREIGN KEY (event_id)     REFERENCES event(id)
) ENGINE=InnoDB;

CREATE TABLE detail_seat (
    detail_id INT NOT NULL,
    seat_id   INT NOT NULL,
    PRIMARY KEY (detail_id, seat_id),
    CONSTRAINT fk_ds_detail FOREIGN KEY (detail_id) REFERENCES detail_transaksi(id) ON DELETE CASCADE,
    CONSTRAINT fk_ds_seat   FOREIGN KEY (seat_id)   REFERENCES seat(id)
) ENGINE=InnoDB;

-- ---------- DATA AWAL ----------
-- Akun demo: admin@konserku.id / admin123  |  demo@konserku.id / demo123
INSERT INTO users (id, nama, email, password_hash, role) VALUES
(1, 'Admin KonserKu', 'admin@konserku.id', SHA2('admin123', 256), 'ADMIN'),
(2, 'Demo Pembeli',   'demo@konserku.id',  SHA2('demo123', 256),  'USER');

INSERT INTO event (id, nama, tanggal, lokasi, deskripsi, status) VALUES
(1, 'Jakarta Music Festival 2026',        '2026-12-20 19:00:00', 'Jakarta International Stadium', 'Malam musik bersama musisi pilihan Indonesia.', 'AKTIF'),
(2, 'Soundrenaline: Sana Sini',           '2026-11-28 15:30:00', 'Bandung',                       'Festival musik lintas genre dan komunitas.',    'AKTIF'),
(3, 'Kadin Fest Bekasi 2027',             '2027-01-30 10:00:00', 'Transera Waterpark, Bekasi',    'Pameran, hiburan, dan pertunjukan.',            'AKTIF'),
(4, 'Coldplay Music of the Spheres Tour', '2027-05-25 19:00:00', 'GBK Jakarta',                   'Malam penuh cahaya dan musik bersama Coldplay.','AKTIF'),
(5, 'Dewa 19 Live in Concert',            '2027-05-31 19:30:00', 'JIS Jakarta',                   'Konser spesial bersama Dewa 19.',               'AKTIF');

INSERT INTO zona (id, event_id, nama, tipe) VALUES
(1, 1, 'VIP',      'NUMBERED'),
(2, 1, 'Festival', 'FREE_SEATING'),
(3, 2, 'Regular',  'FREE_SEATING'),
(4, 3, 'Tribune',  'NUMBERED'),
(5, 4, 'VIP',      'NUMBERED'),
(6, 4, 'Festival', 'FREE_SEATING'),
(7, 5, 'VIP',      'NUMBERED'),
(8, 5, 'Festival', 'FREE_SEATING');

INSERT INTO tiket (id, zona_id, kategori, jenis, harga, stok) VALUES
(1, 1, 'VIP',        'VIP',        750000,  24),
(2, 2, 'Festival',   'FESTIVAL',   250000,  180),
(3, 3, 'Regular',    'FESTIVAL',   150000,  220),
(4, 4, 'Tribune',    'VIP',        200000,  24),
(5, 2, 'Early Bird', 'EARLY_BIRD', 175000,  80),
(6, 5, 'VIP',        'VIP',        3000000, 24),
(7, 6, 'Festival',   'FESTIVAL',   1500000, 100),
(8, 7, 'VIP',        'VIP',        1500000, 24),
(9, 8, 'Festival',   'FESTIVAL',   500000,  150);

-- Kursi A01..A24 untuk setiap zona bertipe NUMBERED
INSERT INTO seat (zona_id, nomor, status)
SELECT z.id, CONCAT('A', LPAD(n.i, 2, '0')), 'AVAILABLE'
FROM zona z
JOIN (SELECT a.d + b.d * 10 + 1 AS i
      FROM (SELECT 0 d UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
            UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a
      CROSS JOIN (SELECT 0 d UNION SELECT 1 UNION SELECT 2) b) n
  ON n.i <= 24
WHERE z.tipe = 'NUMBERED'
ORDER BY z.id, n.i;

-- ---------- CONTOH QUERY (untuk laporan / dokumentasi) ----------
-- Pendapatan per event:
-- SELECT e.nama, SUM(d.qty * d.harga_satuan) AS pendapatan
-- FROM detail_transaksi d JOIN transaksi t ON t.id = d.transaksi_id
-- JOIN event e ON e.id = d.event_id
-- WHERE t.status = 'BERHASIL' GROUP BY e.id;
