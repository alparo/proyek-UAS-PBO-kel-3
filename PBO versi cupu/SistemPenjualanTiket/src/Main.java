import collection.*;
import database.*;
import gui.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.*;

/** Titik masuk aplikasi: uji koneksi DB -> muat data ke koleksi -> tampilkan LoginFrame. */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignore) { }

        if (Koneksi.uji()) {
            try {
                muatDariDatabase();
            } catch (DatabaseException e) {
                // koneksi hidup tapi tabel belum ada / error query -> pakai data contoh di memori
                kosongkanSemua();
                Koneksi.nonaktifkan("Tabel belum siap (jalankan db_penjualan_tiket.sql). Mode memori aktif.");
                System.err.println(e.getMessage() + " :: " + e.getCause());
                DataAwal.isi();
            }
        } else {
            System.out.println("[Info] " + Koneksi.getPesanStatus() + " -> mode memori (data contoh).");
            DataAwal.isi();
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    /** Memuat seluruh tabel ke koleksi. Urutan penting karena ada relasi antar tabel. */
    static void muatDariDatabase() {
        for (User u : new UserDAO().semua()) DataUser.getInstance().tambah(u);
        for (Event e : new EventDAO().semua()) DataEvent.getInstance().tambah(e);
        for (Zona z : new ZonaDAO().semua()) DataZona.getInstance().tambah(z);
        for (Tiket t : new TiketDAO().semua()) DataTiket.getInstance().tambah(t);
        for (Seat s : new SeatDAO().semua()) DataSeat.getInstance().tambah(s);
        for (Transaksi t : new TransaksiDAO().semua()) DataTransaksi.getInstance().tambah(t);
    }

    static void kosongkanSemua() {
        DataUser.getInstance().kosongkan();
        DataEvent.getInstance().kosongkan();
        DataZona.getInstance().kosongkan();
        DataTiket.getInstance().kosongkan();
        DataSeat.getInstance().kosongkan();
        DataTransaksi.getInstance().kosongkan();
    }
}
