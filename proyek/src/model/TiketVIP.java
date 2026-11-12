package proyek.src.model;

public class TiketVIP extends Tiket {
    public TiketVIP() {
        super("VIP", 500000, 50);
    }
    
    // Polimorfism
    @Override
    public String getFasilitas() {
        return "Akses area VIP dan prioritas masuk";
    }

    @Override
    public int hitungHarga() {
        return getHarga();
    }
}
