package database;

/** Exception unchecked pembungkus SQLException agar mudah ditangkap di layer GUI. */
public class DatabaseException extends RuntimeException {
    public DatabaseException(String pesan, Throwable sebab) { super(pesan, sebab); }
}
