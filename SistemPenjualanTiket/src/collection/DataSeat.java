package collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import model.Seat;

/**
 * Selain ArrayList + HashMap(id), kursi dikelompokkan lagi dengan
 * HashMap<zonaId, List<Seat>> supaya denah kursi satu zona bisa diambil langsung.
 */
public class DataSeat extends DataKoleksi<Seat> {
    private static final DataSeat INSTANCE = new DataSeat();
    private final HashMap<Integer, List<Seat>> perZona = new HashMap<>();
    private DataSeat() { }
    public static DataSeat getInstance() { return INSTANCE; }

    @Override protected int idDari(Seat s) { return s.getId(); }

    @Override public void tambah(Seat s) {
        super.tambah(s);
        perZona.computeIfAbsent(s.getZonaId(), k -> new ArrayList<>()).add(s);
    }

    @Override public void kosongkan() {
        super.kosongkan();
        perZona.clear();
    }

    public List<Seat> byZona(int zonaId) {
        return Collections.unmodifiableList(perZona.getOrDefault(zonaId, new ArrayList<>()));
    }

    public void hapusByZona(int zonaId) {
        List<Seat> list = perZona.remove(zonaId);
        if (list == null) return;
        for (Seat s : list) { daftar.remove(s); indeks.remove(s.getId()); }
    }

    public int hitung(int zonaId, Seat.Status status) {
        int n = 0;
        for (Seat s : byZona(zonaId)) if (s.getStatus() == status) n++;
        return n;
    }
}
