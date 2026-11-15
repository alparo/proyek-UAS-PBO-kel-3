package model;

/** Satu kursi bernomor pada zona NUMBERED. */
public class Seat {
    public enum Status { AVAILABLE, SOLD }

    private int id;
    private int zonaId;
    private String nomor;
    private Status status;

    public Seat(int id, int zonaId, String nomor, Status status) {
        this.id = id; this.zonaId = zonaId; this.nomor = nomor; this.status = status;
    }

    public int getId() { return id; }
    public int getZonaId() { return zonaId; }
    public String getNomor() { return nomor; }
    public Status getStatus() { return status; }
    public boolean isTersedia() { return status == Status.AVAILABLE; }
    public void setStatus(Status status) { this.status = status; }

    @Override public String toString() { return nomor; }
}
