package service;

import collection.DataUser;
import database.UserDAO;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import model.User;

/** Login, registrasi, dan penyimpanan sesi pengguna aktif. */
public class AuthService {
    private static final DataUser data = DataUser.getInstance();
    private static final UserDAO dao = new UserDAO();
    private static User aktif;

    public static User login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty())
            throw new IllegalArgumentException("Email dan password wajib diisi.");
        User u = data.cariEmail(email.trim());
        if (u == null || !u.getPasswordHash().equals(hash(password)))
            throw new IllegalArgumentException("Email atau password tidak cocok.");
        aktif = u;
        return u;
    }

    public static User register(String nama, String email, String password) {
        if (nama == null || nama.trim().isEmpty()) throw new IllegalArgumentException("Nama wajib diisi.");
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$"))
            throw new IllegalArgumentException("Format email tidak valid.");
        if (password == null || password.length() < 6) throw new IllegalArgumentException("Password minimal 6 karakter.");
        if (data.cariEmail(email.trim()) != null) throw new IllegalArgumentException("Email sudah terdaftar.");
        User u = new User(data.idBerikutnya(), nama.trim(), email.trim(), hash(password), User.Role.USER);
        dao.tambah(u);
        data.tambah(u);
        return u;
    }

    public static void logout() { aktif = null; }
    public static User getAktif() { return aktif; }

    /** SHA-256 -> heksadesimal huruf kecil (sama dengan SHA2(x,256) di MySQL). */
    public static String hash(String teks) {
        try {
            byte[] b = MessageDigest.getInstance("SHA-256").digest(teks.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
