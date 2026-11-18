package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Event;

public class EventDAO {
    /** SELECT semua event. */
    public List<Event> semua() {
        List<Event> hasil = new ArrayList<>();
        if (!Koneksi.isTersedia()) return hasil;
        String sql = "SELECT id, nama, tanggal, lokasi, deskripsi, status FROM event ORDER BY id";
        try (Statement st = Koneksi.getKoneksi().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                hasil.add(new Event(rs.getInt("id"), rs.getString("nama"),
                        rs.getTimestamp("tanggal").toLocalDateTime(), rs.getString("lokasi"),
                        rs.getString("deskripsi"), Event.Status.valueOf(rs.getString("status"))));
        } catch (SQLException e) {
            throw new DatabaseException("Gagal memuat data event", e);
        }
        return hasil;
    }

    /** INSERT satu event. */
    public void tambah(Event e) {
        if (!Koneksi.isTersedia()) return;
        String sql = "INSERT INTO event (id, nama, tanggal, lokasi, deskripsi, status) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql)) {
            ps.setInt(1, e.getId());
            ps.setString(2, e.getNama());
            ps.setTimestamp(3, Timestamp.valueOf(e.getTanggal()));
            ps.setString(4, e.getLokasi());
            ps.setString(5, e.getDeskripsi());
            ps.setString(6, e.getStatus().name());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("Gagal menyimpan event", ex);
        }
    }

    /** UPDATE satu event berdasarkan id. */
    public void ubah(Event e) {
        if (!Koneksi.isTersedia()) return;
        String sql = "UPDATE event SET nama=?, tanggal=?, lokasi=?, deskripsi=?, status=? WHERE id=?";
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql)) {
            ps.setString(1, e.getNama());
            ps.setTimestamp(2, Timestamp.valueOf(e.getTanggal()));
            ps.setString(3, e.getLokasi());
            ps.setString(4, e.getDeskripsi());
            ps.setString(5, e.getStatus().name());
            ps.setInt(6, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("Gagal mengubah event", ex);
        }
    }

    /** DELETE event (zona, tiket, dan kursi ikut terhapus lewat FOREIGN KEY ON DELETE CASCADE). */
    public void hapus(int id) {
        if (!Koneksi.isTersedia()) return;
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement("DELETE FROM event WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("Gagal menghapus event", ex);
        }
    }
}
