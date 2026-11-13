
package proyek.src.service;

import java.util.HashMap;

import proyek.src.collection.DataTiket;
import proyek.src.model.Tiket;

public class TiketService {

    private DataTiket dataTiket = new DataTiket();

    public Tiket getTiket(String kategori) {
        return dataTiket.getTiket(kategori);
    }

    public boolean beliTiket(String kategori, int jumlah) {
        Tiket tiket = dataTiket.getTiket(kategori);

        if (tiket != null && jumlah > 0 && jumlah <= tiket.getStok()) {
            tiket.kurangiStok(jumlah);
            return true;
        }

        return false;
    }

    public int hitungTotal(String kategori, int jumlah) {
        Tiket tiket = dataTiket.getTiket(kategori);

        if (tiket != null && jumlah > 0) {
            return tiket.hitungHarga() * jumlah;
        }

        return 0;
    }

    public HashMap<String, Tiket> getDaftarTiket() {
        return dataTiket.getDaftarTiket();
    }
}
