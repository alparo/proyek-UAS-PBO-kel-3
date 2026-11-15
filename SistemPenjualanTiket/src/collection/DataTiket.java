package collection;

import java.util.ArrayList;
import java.util.List;
import model.Tiket;
import model.Zona;

public class DataTiket extends DataKoleksi<Tiket> {
    private static final DataTiket INSTANCE = new DataTiket();
    private DataTiket() { }
    public static DataTiket getInstance() { return INSTANCE; }

    @Override protected int idDari(Tiket t) { return t.getId(); }

    public List<Tiket> byZona(int zonaId) {
        List<Tiket> hasil = new ArrayList<>();
        for (Tiket t : daftar) if (t.getZonaId() == zonaId) hasil.add(t);
        return hasil;
    }

    /** Semua kategori tiket milik satu event (tiket -> zona -> event). */
    public List<Tiket> byEvent(int eventId) {
        List<Tiket> hasil = new ArrayList<>();
        for (Tiket t : daftar) {
            Zona z = DataZona.getInstance().cari(t.getZonaId());
            if (z != null && z.getEventId() == eventId) hasil.add(t);
        }
        return hasil;
    }
}
