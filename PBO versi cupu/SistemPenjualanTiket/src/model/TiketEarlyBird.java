package model;

public class TiketEarlyBird extends Tiket {
    public TiketEarlyBird(int id, int zonaId, String kategori, double harga, int stok) {
        super(id, zonaId, kategori, harga, stok);
    }
    @Override public String getJenis() { return "EARLY_BIRD"; }
    @Override public String getBenefit() { return "Harga promo, kuota terbatas"; }
    @Override public int getMaksPerTransaksi() { return 4; }
}
