package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Modul koneksi JDBC ke MySQL/MariaDB.
 * Ubah URL, USER, dan PASSWORD sesuai server lokal (XAMPP default: root & password kosong).
 */
public class Koneksi {
    private static final String URL = "jdbc:mysql://localhost:3306/db_penjualan_tiket"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection koneksi;
    private static boolean tersedia = false;
    private static String pesanStatus = "Belum dicoba";

    /** Mencoba terhubung sekali saat aplikasi start. Hasilnya menentukan mode DB atau mode memori. */
    public static boolean uji() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");          // 1. muat driver
            getKoneksi();                                         // 2. buka koneksi
            tersedia = true;
            pesanStatus = "Terhubung ke MySQL (db_penjualan_tiket)";
        } catch (ClassNotFoundException e) {
            tersedia = false;
            pesanStatus = "Driver MySQL (lib/mysql-connector-j-*.jar) tidak ditemukan";
        } catch (SQLException e) {
            tersedia = false;
            pesanStatus = "Gagal terhubung ke database: " + e.getMessage();
        }
        return tersedia;
    }

    /** Mengembalikan koneksi aktif; membuat ulang jika belum ada atau sudah tertutup. */
    public static Connection getKoneksi() throws SQLException {
        if (koneksi == null || koneksi.isClosed()) {
            koneksi = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return koneksi;
    }

    /** Dipanggil jika koneksi hidup tetapi tabel belum dibuat -> aplikasi jatuh ke mode memori. */
    public static void nonaktifkan(String alasan) {
        tersedia = false;
        pesanStatus = alasan;
    }

    public static boolean isTersedia() { return tersedia; }
    public static String getPesanStatus() { return pesanStatus; }

    public static void tutup() {
        try {
            if (koneksi != null && !koneksi.isClosed()) koneksi.close();
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi: " + e.getMessage());
        }
    }
}
