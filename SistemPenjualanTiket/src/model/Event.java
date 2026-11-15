package model;

import java.time.LocalDateTime;

/** Entitas event/konser. */
public class Event {
    public enum Status { AKTIF, NONAKTIF }

    private int id;
    private String nama;
    private LocalDateTime tanggal;
    private String lokasi;
    private String deskripsi;
    private Status status;

    public Event(int id, String nama, LocalDateTime tanggal, String lokasi, String deskripsi, Status status) {
        this.id = id; this.nama = nama; this.tanggal = tanggal;
        this.lokasi = lokasi; this.deskripsi = deskripsi; this.status = status;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public LocalDateTime getTanggal() { return tanggal; }
    public String getLokasi() { return lokasi; }
    public String getDeskripsi() { return deskripsi; }
    public Status getStatus() { return status; }
    public boolean isAktif() { return status == Status.AKTIF; }

    public void setNama(String nama) { this.nama = nama; }
    public void setTanggal(LocalDateTime tanggal) { this.tanggal = tanggal; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setStatus(Status status) { this.status = status; }

    @Override public String toString() { return nama; }
}
