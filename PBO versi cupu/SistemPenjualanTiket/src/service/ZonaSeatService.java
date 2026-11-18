package service;

import collection.*;
import database.SeatDAO;
import database.ZonaDAO;
import java.util.ArrayList;
import java.util.List;
import model.Seat;
import model.Zona;

public class ZonaSeatService {
    private final DataZona dataZona = DataZona.getInstance();
    private final DataSeat dataSeat = DataSeat.getInstance();
    private final DataEvent dataEvent = DataEvent.getInstance();
    private final DataTiket dataTiket = DataTiket.getInstance();
    private final ZonaDAO zonaDAO = new ZonaDAO();
    private final SeatDAO seatDAO = new SeatDAO();

    /** Membuat zona baru; jika NUMBERED, otomatis dibuat kursi A01..Axx. */
    public Zona tambahZona(Integer eventId, String nama, Zona.Tipe tipe, int jumlahKursi) {
        if (eventId == null || dataEvent.cari(eventId) == null) throw new IllegalArgumentException("Pilih event terlebih dahulu.");
        if (nama == null || nama.trim().isEmpty()) throw new IllegalArgumentException("Nama zona wajib diisi.");
        if (tipe == Zona.Tipe.NUMBERED && jumlahKursi <= 0) throw new IllegalArgumentException("Jumlah kursi harus lebih dari 0.");

        Zona z = new Zona(dataZona.idBerikutnya(), eventId, nama.trim(), tipe);
        List<Seat> seatBaru = new ArrayList<>();
        if (tipe == Zona.Tipe.NUMBERED) {
            int id = dataSeat.idBerikutnya();
            for (int i = 1; i <= jumlahKursi; i++)
                seatBaru.add(new Seat(id++, z.getId(), String.format("A%02d", i), Seat.Status.AVAILABLE));
        }
        zonaDAO.tambah(z);
        seatDAO.tambahBanyak(seatBaru);
        dataZona.tambah(z);
        for (Seat s : seatBaru) dataSeat.tambah(s);
        return z;
    }

    public void hapusZona(Zona z) {
        if (!dataTiket.byZona(z.getId()).isEmpty())
            throw new IllegalStateException("Zona masih memiliki kategori tiket. Hapus kategori tiketnya dulu.");
        zonaDAO.hapus(z.getId());
        dataSeat.hapusByZona(z.getId());
        dataZona.hapus(z);
    }

    public List<Zona> semua() { return dataZona.semua(); }
    public List<Seat> kursiZona(int zonaId) { return dataSeat.byZona(zonaId); }
    public int tersedia(int zonaId) { return dataSeat.hitung(zonaId, Seat.Status.AVAILABLE); }
    public int terjual(int zonaId) { return dataSeat.hitung(zonaId, Seat.Status.SOLD); }
}
