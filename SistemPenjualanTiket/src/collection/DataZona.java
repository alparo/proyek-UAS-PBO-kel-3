package collection;

import java.util.ArrayList;
import java.util.List;
import model.Zona;

public class DataZona extends DataKoleksi<Zona> {
    private static final DataZona INSTANCE = new DataZona();
    private DataZona() { }
    public static DataZona getInstance() { return INSTANCE; }

    @Override protected int idDari(Zona z) { return z.getId(); }

    public List<Zona> byEvent(int eventId) {
        List<Zona> hasil = new ArrayList<>();
        for (Zona z : daftar) if (z.getEventId() == eventId) hasil.add(z);
        return hasil;
    }
}
