package proyek;

public class TiketFestival extends Tiket {
    public TiketFestival() {
        super("Festival", 250000, 100);
    }

    @Override
    public String getFasilitas() {
        return "Akses area festival dan standing zone";
    }

    @Override
    public int hitungHarga() {
        return getHarga();
    }
}
