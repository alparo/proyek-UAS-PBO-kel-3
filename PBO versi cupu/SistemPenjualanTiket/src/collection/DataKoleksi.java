package collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Kelas dasar penyimpanan data di memori (Java Collection Framework).
 *
 *  - ArrayList<T>        : menjaga urutan data -> cocok untuk ditampilkan di JTable / daftar kartu.
 *  - HashMap<Integer,T>  : indeks berdasarkan ID -> pencarian O(1), dipakai terus saat checkout,
 *                          validasi stok, dan relasi antar objek (tiket -> zona -> event).
 */
public abstract class DataKoleksi<T> {
    protected final ArrayList<T> daftar = new ArrayList<>();
    protected final HashMap<Integer, T> indeks = new HashMap<>();

    protected abstract int idDari(T objek);

    public void tambah(T objek) {
        daftar.add(objek);
        indeks.put(idDari(objek), objek);
    }

    public void hapus(T objek) {
        daftar.remove(objek);
        indeks.remove(idDari(objek));
    }

    /** Mengganti objek lama dengan objek baru (mis. jenis tiket berubah). */
    public void ganti(T lama, T baru) {
        int pos = daftar.indexOf(lama);
        if (pos >= 0) daftar.set(pos, baru); else daftar.add(baru);
        indeks.remove(idDari(lama));
        indeks.put(idDari(baru), baru);
    }

    public T cari(int id) { return indeks.get(id); }

    public List<T> semua() { return Collections.unmodifiableList(daftar); }

    public int jumlah() { return daftar.size(); }

    public int idBerikutnya() {
        int maks = 0;
        for (int k : indeks.keySet()) maks = Math.max(maks, k);
        return maks + 1;
    }

    public void kosongkan() {
        daftar.clear();
        indeks.clear();
    }
}
