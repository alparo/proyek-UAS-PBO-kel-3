package collection;

import java.util.ArrayList;
import java.util.List;
import model.Event;

public class DataEvent extends DataKoleksi<Event> {
    private static final DataEvent INSTANCE = new DataEvent();
    private DataEvent() { }
    public static DataEvent getInstance() { return INSTANCE; }

    @Override protected int idDari(Event e) { return e.getId(); }

    /** Event yang tampil untuk pembeli. */
    public List<Event> aktif() {
        List<Event> hasil = new ArrayList<>();
        for (Event e : daftar) if (e.isAktif()) hasil.add(e);
        return hasil;
    }
}
