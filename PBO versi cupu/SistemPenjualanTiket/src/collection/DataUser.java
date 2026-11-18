package collection;

import java.util.HashMap;
import model.User;

public class DataUser extends DataKoleksi<User> {
    private static final DataUser INSTANCE = new DataUser();
    private final HashMap<String, User> perEmail = new HashMap<>();   // login O(1) via email
    private DataUser() { }
    public static DataUser getInstance() { return INSTANCE; }

    @Override protected int idDari(User u) { return u.getId(); }

    @Override public void tambah(User u) {
        super.tambah(u);
        perEmail.put(u.getEmail().toLowerCase(), u);
    }

    @Override public void kosongkan() {
        super.kosongkan();
        perEmail.clear();
    }

    public User cariEmail(String email) { return perEmail.get(email.toLowerCase()); }
}
