package model;

/** Akun pengguna (ADMIN atau USER/pembeli). Password disimpan sebagai hash SHA-256. */
public class User {
    public enum Role { ADMIN, USER }

    private int id;
    private String nama;
    private String email;
    private String passwordHash;
    private Role role;

    public User(int id, String nama, String email, String passwordHash, Role role) {
        this.id = id; this.nama = nama; this.email = email;
        this.passwordHash = passwordHash; this.role = role;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isAdmin() { return role == Role.ADMIN; }

    @Override public String toString() { return nama; }
}
