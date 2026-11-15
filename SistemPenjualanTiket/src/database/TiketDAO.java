package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Tiket;

public class TiketDAO {
    public List<Tiket> semua() {
        List<Tiket> hasil = new ArrayList<>();
        if (!Koneksi.isTersedia()) return hasil;
        String sql = "SELECT id, zona_id, kategori, jenis, harga, stok FROM tiket ORDER BY id";
        try (Statement st = Koneksi.getKoneksi().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                hasil.add(Tiket.buat(rs.getString("jenis"), rs.getInt("id"), rs.getInt("zona_id"),
                        rs.getString("kategori"), rs.getDouble("harga"), rs.getInt("stok")));
        } catch (SQLException e) {
            throw new DatabaseException("Gagal memuat data tiket", e);
        }
        return hasil;
    }

    public void tambah(Tiket t) {
        if (!Koneksi.isTersedia()) return;
        String sql = "INSERT INTO tiket (id, zona_id, kategori, jenis, harga, stok) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql)) {
            ps.setInt(1, t.getId());
            ps.setInt(2, t.getZonaId());
            ps.setString(3, t.getKategori());
            ps.setString(4, t.getJenis());
            ps.setDouble(5, t.getHarga());
            ps.setInt(6, t.getStok());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menyimpan tiket", e);
        }
    }

    public void ubah(Tiket t) {
        if (!Koneksi.isTersedia()) return;
        String sql = "UPDATE tiket SET zona_id=?, kategori=?, jenis=?, harga=?, stok=? WHERE id=?";
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql)) {
            ps.setInt(1, t.getZonaId());
            ps.setString(2, t.getKategori());
            ps.setString(3, t.getJenis());
            ps.setDouble(4, t.getHarga());
            ps.setInt(5, t.getStok());
            ps.setInt(6, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengubah tiket", e);
        }
    }

    public void hapus(int id) {
        if (!Koneksi.isTersedia()) return;
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement("DELETE FROM tiket WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menghapus tiket", e);
        }
    }
}
