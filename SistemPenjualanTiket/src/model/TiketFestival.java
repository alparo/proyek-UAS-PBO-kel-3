package model;

public class TiketFestival extends Tiket {
    public TiketFestival(int id, int zonaId, String kategori, double harga, int stok) {
        super(id, zonaId, kategori, harga, stok);
    }
    @Override public String getJenis() { return "FESTIVAL"; }
    @Override public String getBenefit() { return "Akses area festival"; }
    @Override public int getMaksPerTransaksi() { return 10; }
}
