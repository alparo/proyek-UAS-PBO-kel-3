package service;

import collection.*;
import database.EventDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Event;
import model.Zona;

public class EventService {
    private final DataEvent dataEvent = DataEvent.getInstance();
    private final DataZona dataZona = DataZona.getInstance();
    private final DataTiket dataTiket = DataTiket.getInstance();
    private final DataSeat dataSeat = DataSeat.getInstance();
    private final DataTransaksi dataTrx = DataTransaksi.getInstance();
    private final EventDAO dao = new EventDAO();

    private void validasi(String nama, LocalDateTime tanggal, String lokasi) {
        if (nama == null || nama.trim().isEmpty()) throw new IllegalArgumentException("Nama event wajib diisi.");
        if (tanggal == null) throw new IllegalArgumentException("Tanggal event wajib diisi.");
        if (lokasi == null || lokasi.trim().isEmpty()) throw new IllegalArgumentException("Lokasi wajib diisi.");
    }

    public Event tambah(String nama, LocalDateTime tanggal, String lokasi, String deskripsi, Event.Status status) {
        validasi(nama, tanggal, lokasi);
        Event e = new Event(dataEvent.idBerikutnya(), nama.trim(), tanggal, lokasi.trim(), deskripsi.trim(), status);
        dao.tambah(e);          // simpan ke database dulu; kalau gagal, memori tidak berubah
        dataEvent.tambah(e);
        return e;
    }

    public void ubah(Event e, String nama, LocalDateTime tanggal, String lokasi, String deskripsi, Event.Status status) {
        validasi(nama, tanggal, lokasi);
        Event salinan = new Event(e.getId(), nama.trim(), tanggal, lokasi.trim(), deskripsi.trim(), status);
        dao.ubah(salinan);
        e.setNama(salinan.getNama());
        e.setTanggal(tanggal);
        e.setLokasi(salinan.getLokasi());
        e.setDeskripsi(salinan.getDeskripsi());
        e.setStatus(status);
    }

    public void toggleStatus(Event e) {
        Event.Status baru = e.isAktif() ? Event.Status.NONAKTIF : Event.Status.AKTIF;
        Event salinan = new Event(e.getId(), e.getNama(), e.getTanggal(), e.getLokasi(), e.getDeskripsi(), baru);
        dao.ubah(salinan);
        e.setStatus(baru);
    }

    public void hapus(Event e) {
        if (dataTrx.adaTransaksiEvent(e.getId()))
            throw new IllegalStateException("Event sudah memiliki transaksi. Nonaktifkan saja agar riwayat tetap aman.");
        dao.hapus(e.getId());
        // cascade di memori: zona -> tiket & kursi
        for (Zona z : new ArrayList<>(dataZona.byEvent(e.getId()))) {
            for (var t : new ArrayList<>(dataTiket.byZona(z.getId()))) dataTiket.hapus(t);
            dataSeat.hapusByZona(z.getId());
            dataZona.hapus(z);
        }
        dataEvent.hapus(e);
    }

    public List<Event> semua() { return dataEvent.semua(); }
    public List<Event> aktif() { return dataEvent.aktif(); }
}
