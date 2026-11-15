package collection;

import java.util.ArrayList;
import java.util.List;
import model.DetailTransaksi;
import model.Transaksi;

public class DataTransaksi extends DataKoleksi<Transaksi> {
    private static final DataTransaksi INSTANCE = new DataTransaksi();
    private DataTransaksi() { }
    public static DataTransaksi getInstance() { return INSTANCE; }

    @Override protected int idDari(Transaksi t) { return t.getId(); }

    public List<Transaksi> byUser(int userId) {
        List<Transaksi> hasil = new ArrayList<>();
        for (Transaksi t : daftar) if (t.getUserId() == userId) hasil.add(t);
        return hasil;
    }

    public List<Transaksi> berhasil() {
        List<Transaksi> hasil = new ArrayList<>();
        for (Transaksi t : daftar) if (t.getStatus() == Transaksi.Status.BERHASIL) hasil.add(t);
        return hasil;
    }

    public boolean adaTransaksiEvent(int eventId) {
        for (Transaksi t : daftar)
            for (DetailTransaksi d : t.getDetail()) if (d.getEventId() == eventId) return true;
        return false;
    }

    public boolean adaTransaksiTiket(int tiketId) {
        for (Transaksi t : daftar)
            for (DetailTransaksi d : t.getDetail()) if (d.getTiketId() == tiketId) return true;
        return false;
    }

    public int idDetailBerikutnya() {
        int maks = 0;
        for (Transaksi t : daftar)
            for (DetailTransaksi d : t.getDetail()) maks = Math.max(maks, d.getId());
        return maks + 1;
    }
}
