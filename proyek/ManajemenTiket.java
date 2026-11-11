package proyek;
import java.util.HashMap;

public class ManajemenTiket {

    private HashMap<String, Tiket> daftarTiket = new HashMap<>();

    public ManajemenTiket() {
        daftarTiket.put("VIP", new Tiket("VIP", 500000, 50));
        daftarTiket.put("Festival", new Tiket("Festival", 250000, 100));
        daftarTiket.put("Early Bird", new Tiket("Early Bird", 150000, 75));
    }

    public Tiket getTiket(String kategori) {
        return daftarTiket.get(kategori);
    }

    public boolean beliTiket(String kategori, int jumlah) {
        Tiket tiket = daftarTiket.get(kategori);

        if (tiket != null && jumlah > 0 && jumlah <= tiket.getStok()) {
            tiket.kurangiStok(jumlah);
            return true;
        }

        return false;
    }

    public int hitungTotal(String kategori, int jumlah) {
        return daftarTiket.get(kategori).getHarga() * jumlah;
    }

    public HashMap<String, Tiket> getDaftarTiket() {
        return daftarTiket;
    }
}
