package model;

public class TiketVIP extends Tiket {
    public TiketVIP(int id, int zonaId, String kategori, double harga, int stok) {
        super(id, zonaId, kategori, harga, stok);
    }
    @Override public String getJenis() { return "VIP"; }
    @Override public String getBenefit() { return "Area prioritas, akses lounge"; }
    @Override public int getMaksPerTransaksi() { return 6; }
}
