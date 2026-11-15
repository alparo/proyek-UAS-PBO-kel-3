package model;

/** Zona di dalam sebuah event: bernomor kursi (NUMBERED) atau bebas (FREE_SEATING). */
public class Zona {
    public enum Tipe {
        NUMBERED("Nomor Kursi"), FREE_SEATING("Free Seating");
        private final String label;
        Tipe(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private int id;
    private int eventId;
    private String nama;
    private Tipe tipe;

    public Zona(int id, int eventId, String nama, Tipe tipe) {
        this.id = id; this.eventId = eventId; this.nama = nama; this.tipe = tipe;
    }

    public int getId() { return id; }
    public int getEventId() { return eventId; }
    public String getNama() { return nama; }
    public Tipe getTipe() { return tipe; }
    public boolean isNumbered() { return tipe == Tipe.NUMBERED; }

    @Override public String toString() { return nama; }
}
