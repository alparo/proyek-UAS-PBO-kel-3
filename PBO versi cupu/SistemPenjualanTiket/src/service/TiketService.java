package service;

import collection.*;
import database.TiketDAO;
import java.util.List;
import model.Tiket;

public class TiketService {
    private final DataTiket dataTiket = DataTiket.getInstance();
    private final DataZona dataZona = DataZona.getInstance();
    private final DataTransaksi dataTrx = DataTransaksi.getInstance();
    private final TiketDAO dao = new TiketDAO();

    private void validasi(Integer zonaId, String kategori, double harga, int stok) {
        if (zonaId == null || dataZona.cari(zonaId) == null) throw new IllegalArgumentException("Pilih zona terlebih dahulu.");
        if (kategori == null || kategori.trim().isEmpty()) throw new IllegalArgumentException("Nama kategori wajib diisi.");
        if (harga < 0) throw new IllegalArgumentException("Harga tidak boleh negatif.");
        if (stok < 0) throw new IllegalArgumentException("Stok tidak boleh negatif.");
    }

    public Tiket tambah(Integer zonaId, String kategori, String jenis, double harga, int stok) {
        validasi(zonaId, kategori, harga, stok);
        Tiket t = Tiket.buat(jenis, dataTiket.idBerikutnya(), zonaId, kategori.trim(), harga, stok);
        dao.tambah(t);
        dataTiket.tambah(t);
        return t;
    }

    /** Karena jenis tiket = kelas berbeda, perubahan dilakukan dengan mengganti objek (id tetap). */
    public Tiket ubah(Tiket lama, Integer zonaId, String kategori, String jenis, double harga, int stok) {
        validasi(zonaId, kategori, harga, stok);
        Tiket baru = Tiket.buat(jenis, lama.getId(), zonaId, kategori.trim(), harga, stok);
        dao.ubah(baru);
        dataTiket.ganti(lama, baru);
        return baru;
    }

    public void hapus(Tiket t) {
        if (dataTrx.adaTransaksiTiket(t.getId()))
            throw new IllegalStateException("Kategori sudah pernah terjual, tidak dapat dihapus.");
        dao.hapus(t.getId());
        dataTiket.hapus(t);
    }

    public List<Tiket> semua() { return dataTiket.semua(); }
    public List<Tiket> byEvent(int eventId) { return dataTiket.byEvent(eventId); }

    /** Harga termurah pada sebuah event (untuk label "Mulai Rp ..."). */
    public double hargaMulai(int eventId) {
        double min = Double.MAX_VALUE;
        for (Tiket t : byEvent(eventId)) min = Math.min(min, t.getHarga());
        return min == Double.MAX_VALUE ? 0 : min;
    }
}
