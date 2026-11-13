
package proyek.src.collection;

import java.util.HashMap;

import proyek.src.model.Tiket;
import proyek.src.model.TiketVIP;
import proyek.src.model.TiketFestival;
import proyek.src.model.TiketEarlyBird;

public class DataTiket {

    private HashMap<String, Tiket> daftarTiket = new HashMap<>();

    public DataTiket() {
        daftarTiket.put("VIP", new TiketVIP());
        daftarTiket.put("Festival", new TiketFestival());
        daftarTiket.put("Early Bird", new TiketEarlyBird());
    }

    public Tiket getTiket(String kategori) {
        return daftarTiket.get(kategori);
    }

    public HashMap<String, Tiket> getDaftarTiket() {
        return daftarTiket;
    }
}
