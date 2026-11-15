package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Zona;

public class ZonaDAO {
    public List<Zona> semua() {
        List<Zona> hasil = new ArrayList<>();
        if (!Koneksi.isTersedia()) return hasil;
        String sql = "SELECT id, event_id, nama, tipe FROM zona ORDER BY id";
        try (Statement st = Koneksi.getKoneksi().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                hasil.add(new Zona(rs.getInt("id"), rs.getInt("event_id"), rs.getString("nama"),
                        Zona.Tipe.valueOf(rs.getString("tipe"))));
        } catch (SQLException e) {
            throw new DatabaseException("Gagal memuat data zona", e);
        }
        return hasil;
    }

    public void tambah(Zona z) {
        if (!Koneksi.isTersedia()) return;
        String sql = "INSERT INTO zona (id, event_id, nama, tipe) VALUES (?,?,?,?)";
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql)) {
            ps.setInt(1, z.getId());
            ps.setInt(2, z.getEventId());
            ps.setString(3, z.getNama());
            ps.setString(4, z.getTipe().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menyimpan zona", e);
        }
    }

    public void hapus(int id) {
        if (!Koneksi.isTersedia()) return;
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement("DELETE FROM zona WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menghapus zona", e);
        }
    }
}
