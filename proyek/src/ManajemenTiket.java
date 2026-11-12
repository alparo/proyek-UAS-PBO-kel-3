package proyek.src;
import java.util.HashMap;

import proyek.src.model.Tiket;
import proyek.src.model.TiketEarlyBird;
import proyek.src.model.TiketFestival;
import proyek.src.model.TiketVIP;

public class ManajemenTiket {

    private HashMap<String, Tiket> daftarTiket = new HashMap<>();

    public ManajemenTiket() {
        // buat objek dari class turunan
        daftarTiket.put("VIP", new TiketVIP());
        daftarTiket.put("Festival", new TiketFestival());
        daftarTiket.put("Early Bird", new TiketEarlyBird());
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

    // menggunakan polimorfism
    public int hitungTotal(String kategori, int jumlah) {
        Tiket tiket = daftarTiket.get(kategori);

        if (tiket != null && jumlah > 0) {
            return tiket.hitungHarga() * jumlah;
        }

        return 0;
    }

    public HashMap<String, Tiket> getDaftarTiket() {
        return daftarTiket;
    }
}
