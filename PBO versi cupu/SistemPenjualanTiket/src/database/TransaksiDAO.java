package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DetailTransaksi;
import model.Transaksi;

public class TransaksiDAO {

    /** Memuat transaksi + detail + kursi dengan 3 query SELECT lalu dirakit di memori. */
    public List<Transaksi> semua() {
        List<Transaksi> hasil = new ArrayList<>();
        if (!Koneksi.isTersedia()) return hasil;
        Map<Integer, Transaksi> petaTrx = new HashMap<>();
        Map<Integer, DetailTransaksi> petaDetail = new HashMap<>();
        try {
            Connection c = Koneksi.getKoneksi();
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, user_id, tanggal, status FROM transaksi ORDER BY id")) {
                while (rs.next()) {
                    Transaksi t = new Transaksi(rs.getInt("id"), rs.getInt("user_id"),
                            rs.getTimestamp("tanggal").toLocalDateTime(), Transaksi.Status.valueOf(rs.getString("status")));
                    hasil.add(t);
                    petaTrx.put(t.getId(), t);
                }
            }
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, transaksi_id, tiket_id, event_id, qty, harga_satuan FROM detail_transaksi ORDER BY id")) {
                while (rs.next()) {
                    DetailTransaksi d = new DetailTransaksi(rs.getInt("id"), rs.getInt("transaksi_id"),
                            rs.getInt("tiket_id"), rs.getInt("event_id"), rs.getInt("qty"), rs.getDouble("harga_satuan"));
                    petaDetail.put(d.getId(), d);
                    Transaksi t = petaTrx.get(d.getTransaksiId());
                    if (t != null) t.getDetail().add(d);
                }
            }
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT detail_id, seat_id FROM detail_seat")) {
                while (rs.next()) {
                    DetailTransaksi d = petaDetail.get(rs.getInt("detail_id"));
                    if (d != null) d.getSeatIds().add(rs.getInt("seat_id"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal memuat data transaksi", e);
        }
        return hasil;
    }

    /**
     * Menyimpan pembelian secara ATOMIK dalam satu transaksi JDBC:
     * insert transaksi + detail + kursi, kurangi stok tiket, tandai kursi SOLD.
     * Kalau salah satu langkah gagal -> rollback semuanya.
     */
    public void simpanPembelian(Transaksi t, int stokBaru) {
        if (!Koneksi.isTersedia()) return;
        Connection c = null;
        try {
            c = Koneksi.getKoneksi();
            c.setAutoCommit(false);                                   // mulai transaksi
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO transaksi (id, user_id, tanggal, total, status) VALUES (?,?,?,?,?)")) {
                ps.setInt(1, t.getId());
                ps.setInt(2, t.getUserId());
                ps.setTimestamp(3, Timestamp.valueOf(t.getTanggal()));
                ps.setDouble(4, t.getTotal());
                ps.setString(5, t.getStatus().name());
                ps.executeUpdate();
            }
            for (DetailTransaksi d : t.getDetail()) {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO detail_transaksi (id, transaksi_id, tiket_id, event_id, qty, harga_satuan) VALUES (?,?,?,?,?,?)")) {
                    ps.setInt(1, d.getId());
                    ps.setInt(2, t.getId());
                    ps.setInt(3, d.getTiketId());
                    ps.setInt(4, d.getEventId());
                    ps.setInt(5, d.getQty());
                    ps.setDouble(6, d.getHargaSatuan());
                    ps.executeUpdate();
                }
                for (int seatId : d.getSeatIds()) {
                    try (PreparedStatement ps = c.prepareStatement("INSERT INTO detail_seat (detail_id, seat_id) VALUES (?,?)")) {
                        ps.setInt(1, d.getId());
                        ps.setInt(2, seatId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = c.prepareStatement("UPDATE seat SET status='SOLD' WHERE id=?")) {
                        ps.setInt(1, seatId);
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE tiket SET stok=? WHERE id=?")) {
                    ps.setInt(1, stokBaru);
                    ps.setInt(2, d.getTiketId());
                    ps.executeUpdate();
                }
            }
            c.commit();                                                // semua berhasil
        } catch (SQLException e) {
            try { if (c != null) c.rollback(); } catch (SQLException ignore) { }
            throw new DatabaseException("Transaksi gagal disimpan (di-rollback)", e);
        } finally {
            try { if (c != null) c.setAutoCommit(true); } catch (SQLException ignore) { }
        }
    }

    /** Pembatalan: status DIBATALKAN, stok dikembalikan, kursi AVAILABLE lagi (atomik). */
    public void batalkan(Transaksi t, int stokBaru) {
        if (!Koneksi.isTersedia()) return;
        Connection c = null;
        try {
            c = Koneksi.getKoneksi();
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement("UPDATE transaksi SET status='DIBATALKAN' WHERE id=?")) {
                ps.setInt(1, t.getId());
                ps.executeUpdate();
            }
            for (DetailTransaksi d : t.getDetail()) {
                for (int seatId : d.getSeatIds()) {
                    try (PreparedStatement ps = c.prepareStatement("UPDATE seat SET status='AVAILABLE' WHERE id=?")) {
                        ps.setInt(1, seatId);
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE tiket SET stok=? WHERE id=?")) {
                    ps.setInt(1, stokBaru);
                    ps.setInt(2, d.getTiketId());
                    ps.executeUpdate();
                }
            }
            c.commit();
        } catch (SQLException e) {
            try { if (c != null) c.rollback(); } catch (SQLException ignore) { }
            throw new DatabaseException("Pembatalan gagal (di-rollback)", e);
        } finally {
            try { if (c != null) c.setAutoCommit(true); } catch (SQLException ignore) { }
        }
    }
}
