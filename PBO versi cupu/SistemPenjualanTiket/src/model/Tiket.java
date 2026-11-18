package model;

/**
 * Kelas induk (abstract) kategori tiket.
 * Subclass: TiketVIP, TiketFestival, TiketEarlyBird -> demonstrasi inheritance & polymorphism.
 */
public abstract class Tiket {
    protected int id;
    protected int zonaId;
    protected String kategori;   // nama tampilan, mis. "VIP", "Regular", "Tribune"
    protected double harga;
    protected int stok;

    protected Tiket(int id, int zonaId, String kategori, double harga, int stok) {
        this.id = id; this.zonaId = zonaId; this.kategori = kategori;
        this.harga = harga; this.stok = stok;
    }

    /** Kode jenis yang disimpan di database: VIP / FESTIVAL / EARLY_BIRD. */
    public abstract String getJenis();
    /** Keterangan benefit yang tampil di GUI. */
    public abstract String getBenefit();
    /** Batas maksimal pembelian per transaksi (aturan bisnis per jenis tiket). */
    public abstract int getMaksPerTransaksi();

    /** Polymorphism: subclass boleh mengubah cara hitung (mis. diskon/biaya layanan). */
    public double hitungTotal(int qty) { return harga * qty; }

    public boolean stokCukup(int qty) { return qty > 0 && stok >= qty; }
    public void kurangiStok(int qty) { stok -= qty; }
    public void tambahStok(int qty) { stok += qty; }

    /** Factory: membuat objek subclass yang sesuai dengan kode jenis. */
    public static Tiket buat(String jenis, int id, int zonaId, String kategori, double harga, int stok) {
        switch (jenis) {
            case "VIP":        return new TiketVIP(id, zonaId, kategori, harga, stok);
            case "EARLY_BIRD": return new TiketEarlyBird(id, zonaId, kategori, harga, stok);
            default:           return new TiketFestival(id, zonaId, kategori, harga, stok);
        }
    }

    public int getId() { return id; }
    public int getZonaId() { return zonaId; }
    public String getKategori() { return kategori; }
    public double getHarga() { return harga; }
    public int getStok() { return stok; }

    @Override public String toString() { return kategori; }
}
