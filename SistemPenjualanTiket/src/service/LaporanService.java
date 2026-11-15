package service;

import collection.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.*;

/** Perhitungan laporan pendapatan (hanya transaksi BERHASIL). */
public class LaporanService {
    private final DataTransaksi dataTrx = DataTransaksi.getInstance();
    private final DataEvent dataEvent = DataEvent.getInstance();

    public double totalPendapatan() {
        double total = 0;
        for (Transaksi t : dataTrx.berhasil()) total += t.getTotal();
        return total;
    }

    public int jumlahTransaksiBerhasil() { return dataTrx.berhasil().size(); }

    public int totalTiketTerjual() {
        int n = 0;
        for (Transaksi t : dataTrx.berhasil()) n += t.getJumlahTiket();
        return n;
    }

    public double rataRataTransaksi() {
        int n = jumlahTransaksiBerhasil();
        return n == 0 ? 0 : totalPendapatan() / n;
    }

    /** LinkedHashMap menjaga urutan event sama seperti di daftar event. */
    public Map<Event, Double> pendapatanPerEvent() {
        Map<Event, Double> hasil = new LinkedHashMap<>();
        for (Event e : dataEvent.semua()) hasil.put(e, 0.0);
        for (Transaksi t : dataTrx.berhasil())
            for (DetailTransaksi d : t.getDetail()) {
                Event e = dataEvent.cari(d.getEventId());
                if (e != null) hasil.merge(e, d.getSubtotal(), Double::sum);
            }
        return hasil;
    }

    public List<Transaksi> transaksiBerhasil() { return dataTrx.berhasil(); }
}
