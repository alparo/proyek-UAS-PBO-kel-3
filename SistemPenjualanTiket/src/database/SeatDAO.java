package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Seat;

public class SeatDAO {
    public List<Seat> semua() {
        List<Seat> hasil = new ArrayList<>();
        if (!Koneksi.isTersedia()) return hasil;
        String sql = "SELECT id, zona_id, nomor, status FROM seat ORDER BY id";
        try (Statement st = Koneksi.getKoneksi().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                hasil.add(new Seat(rs.getInt("id"), rs.getInt("zona_id"), rs.getString("nomor"),
                        Seat.Status.valueOf(rs.getString("status"))));
        } catch (SQLException e) {
            throw new DatabaseException("Gagal memuat data kursi", e);
        }
        return hasil;
    }

    /** Batch insert banyak kursi sekaligus dalam satu transaksi JDBC. */
    public void tambahBanyak(List<Seat> daftar) {
        if (!Koneksi.isTersedia() || daftar.isEmpty()) return;
        String sql = "INSERT INTO seat (id, zona_id, nomor, status) VALUES (?,?,?,?)";
        Connection c = null;
        try {
            c = Koneksi.getKoneksi();
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                for (Seat s : daftar) {
                    ps.setInt(1, s.getId());
                    ps.setInt(2, s.getZonaId());
                    ps.setString(3, s.getNomor());
                    ps.setString(4, s.getStatus().name());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            c.commit();
        } catch (SQLException e) {
            try { if (c != null) c.rollback(); } catch (SQLException ignore) { }
            throw new DatabaseException("Gagal menyimpan kursi", e);
        } finally {
            try { if (c != null) c.setAutoCommit(true); } catch (SQLException ignore) { }
        }
    }
}
