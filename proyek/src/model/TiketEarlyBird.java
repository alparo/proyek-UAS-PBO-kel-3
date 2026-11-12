package proyek.src.model;

public class TiketEarlyBird extends Tiket {
    public TiketEarlyBird() {
        super("Early Bird", 150000, 75);
    }

    @Override
    public String getFasilitas() {
        return "Harga khusus pembelian lebih awal";
    }

    @Override
    public int hitungHarga() {
        return getHarga();
    }
}
