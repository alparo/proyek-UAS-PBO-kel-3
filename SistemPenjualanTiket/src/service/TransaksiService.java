package service;

import collection.*;
import database.TransaksiDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import model.*;

public class TransaksiService {
    private final DataTransaksi dataTrx = DataTransaksi.getInstance();
    private final DataTiket dataTiket = DataTiket.getInstance();
    private final DataSeat dataSeat = DataSeat.getInstance();
    private final DataZona dataZona = DataZona.getInstance();
    private final DataEvent dataEvent = DataEvent.getInstance();
    private final TransaksiDAO dao = new TransaksiDAO();

    /**
     * Proses pembelian: validasi -> simpan ke DB (atomik) -> baru ubah data di memori.
     * @param seatIds ID kursi terpilih (wajib sebanyak qty untuk zona NUMBERED, kosong untuk free seating)
     */
    public Transaksi beli(User user, Tiket tiket, int qty, Collection<Integer> seatIds) {
        Zona zona = dataZona.cari(tiket.getZonaId());
        Event event = zona == null ? null : dataEvent.cari(zona.getEventId());
        if (event == null || !event.isAktif()) throw new IllegalStateException("Event tidak tersedia.");
        if (qty < 1) throw new IllegalArgumentException("Jumlah tiket minimal 1.");
        if (qty > tiket.getMaksPerTransaksi())
            throw new IllegalArgumentException("Maksimal " + tiket.getMaksPerTransaksi() + " tiket " + tiket.getKategori() + " per transaksi.");
        if (!tiket.stokCukup(qty)) throw new IllegalStateException("Stok tiket sudah berubah / tidak mencukupi.");

        List<Seat> kursi = new ArrayList<>();
        if (zona.isNumbered()) {
            if (seatIds == null || seatIds.size() != qty) throw new IllegalArgumentException("Pilih kursi sesuai jumlah tiket.");
            for (int id : seatIds) {
                Seat s = dataSeat.cari(id);
                if (s == null || s.getZonaId() != zona.getId()) throw new IllegalArgumentException("Kursi tidak valid.");
                if (!s.isTersedia()) throw new IllegalStateException("Kursi " + s.getNomor() + " sudah dibeli orang lain.");
                kursi.add(s);
            }
        }

        int idTrx = dataTrx.idBerikutnya();
        Transaksi trx = new Transaksi(idTrx, user.getId(), LocalDateTime.now(), Transaksi.Status.BERHASIL);
        DetailTransaksi det = new DetailTransaksi(dataTrx.idDetailBerikutnya(), idTrx, tiket.getId(), event.getId(), qty, tiket.getHarga());
        for (Seat s : kursi) det.getSeatIds().add(s.getId());
        trx.getDetail().add(det);

        dao.simpanPembelian(trx, tiket.getStok() - qty);   // kalau gagal -> exception, memori utuh

        tiket.kurangiStok(qty);
        for (Seat s : kursi) s.setStatus(Seat.Status.SOLD);
        dataTrx.tambah(trx);
        return trx;
    }

    public void batalkan(Transaksi trx) {
        if (trx.getStatus() != Transaksi.Status.BERHASIL) throw new IllegalStateException("Transaksi sudah dibatalkan.");
        DetailTransaksi d = trx.getDetail().get(0);
        Tiket tiket = dataTiket.cari(d.getTiketId());
        int stokBaru = (tiket == null ? 0 : tiket.getStok()) + d.getQty();

        dao.batalkan(trx, stokBaru);

        trx.setStatus(Transaksi.Status.DIBATALKAN);
        if (tiket != null) tiket.tambahStok(d.getQty());
        for (int id : d.getSeatIds()) {
            Seat s = dataSeat.cari(id);
            if (s != null) s.setStatus(Seat.Status.AVAILABLE);
        }
    }

    public List<Transaksi> semua() { return dataTrx.semua(); }
    public List<Transaksi> milikUser(int userId) { return dataTrx.byUser(userId); }
}
