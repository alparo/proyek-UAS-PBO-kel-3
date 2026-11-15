package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.User;

public class UserDAO {
    public List<User> semua() {
        List<User> hasil = new ArrayList<>();
        if (!Koneksi.isTersedia()) return hasil;
        String sql = "SELECT id, nama, email, password_hash, role FROM users ORDER BY id";
        try (Statement st = Koneksi.getKoneksi().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                hasil.add(new User(rs.getInt("id"), rs.getString("nama"), rs.getString("email"),
                        rs.getString("password_hash"), User.Role.valueOf(rs.getString("role"))));
        } catch (SQLException e) {
            throw new DatabaseException("Gagal memuat data user", e);
        }
        return hasil;
    }

    public void tambah(User u) {
        if (!Koneksi.isTersedia()) return;
        String sql = "INSERT INTO users (id, nama, email, password_hash, role) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql)) {
            ps.setInt(1, u.getId());
            ps.setString(2, u.getNama());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPasswordHash());
            ps.setString(5, u.getRole().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menyimpan user", e);
        }
    }
}
