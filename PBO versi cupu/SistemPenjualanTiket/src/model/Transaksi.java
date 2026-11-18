package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Header transaksi pembelian; isinya berupa daftar DetailTransaksi. */
public class Transaksi {
    public enum Status { BERHASIL, DIBATALKAN }

    private int id;
    private int userId;
    private LocalDateTime tanggal;
    private Status status;
    private final List<DetailTransaksi> detail = new ArrayList<>();

    public Transaksi(int id, int userId, LocalDateTime tanggal, Status status) {
        this.id = id; this.userId = userId; this.tanggal = tanggal; this.status = status;
    }

    public double getTotal() {
        double t = 0;
        for (DetailTransaksi d : detail) t += d.getSubtotal();
        return t;
    }
    public int getJumlahTiket() {
        int n = 0;
        for (DetailTransaksi d : detail) n += d.getQty();
        return n;
    }
    public String getKode() { return String.format("KK-%06d", id); }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public LocalDateTime getTanggal() { return tanggal; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public List<DetailTransaksi> getDetail() { return detail; }
}
