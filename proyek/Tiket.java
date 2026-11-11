package proyek;
public class Tiket {
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
        stok -= jumlah;
    }
}