package proyek.src.model;

public abstract class Tiket {
    private String kategori;
    private int harga;
    private int stok;

    public Tiket(String kategori, int harga, int stok) {
        this.kategori = kategori;
        this.harga = harga;
        this.stok = stok;
    }

    public String getKategori() {
        return kategori;
    }

    public int getHarga() {
        return harga;
    }

    public int getStok() {
        return stok;
    }

    public void kurangiStok(int jumlah) {
        if (jumlah > 0 && jumlah <= stok) {
            stok -= jumlah;
        }
    }

    public abstract String getFasilitas();

    public abstract int hitungHarga();
}