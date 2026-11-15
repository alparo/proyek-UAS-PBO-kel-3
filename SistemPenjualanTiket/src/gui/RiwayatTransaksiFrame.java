package gui;

import collection.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.*;
import model.Event;
import service.AuthService;
import service.TransaksiService;

/**
 * Satu kelas, tiga tampilan:
 *  - TIKET_SAYA   : kartu e-ticket milik pembeli (hanya yang BERHASIL)
 *  - RIWAYAT_USER : tabel riwayat transaksi pembeli
 *  - ADMIN        : tabel semua transaksi + tombol batalkan
 */
public class RiwayatTransaksiFrame extends JPanel implements Refreshable {
    public enum Mode { TIKET_SAYA, RIWAYAT_USER, ADMIN }

    private final Mode mode;
    private final TransaksiService service = new TransaksiService();
    private final JPanel isi = new JPanel(new BorderLayout());
    private DefaultTableModel model;
    private JTable tabel;

    public RiwayatTransaksiFrame(Mode mode) {
        super(new BorderLayout());
        this.mode = mode;
        setBackground(Tema.BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));
        isi.setOpaque(false);
        add(isi, BorderLayout.CENTER);
    }

    /** Baris tabel untuk admin / ringkasan: Kode, Pembeli, Event, Tanggal, Qty, Total, Status. */
    static Object[] barisAdmin(Transaksi t) {
        User u = DataUser.getInstance().cari(t.getUserId());
        Event e = t.getDetail().isEmpty() ? null : DataEvent.getInstance().cari(t.getDetail().get(0).getEventId());
        return new Object[]{t.getKode(), u == null ? "-" : u.getNama(), e == null ? "-" : e.getNama(),
                Tema.tanggal(t.getTanggal()), t.getJumlahTiket(), Tema.rupiah(t.getTotal()), t.getStatus()};
    }

    private static String namaKategori(Transaksi t) {
        if (t.getDetail().isEmpty()) return "-";
        Tiket tk = DataTiket.getInstance().cari(t.getDetail().get(0).getTiketId());
        return tk == null ? "-" : tk.getKategori();
    }

    private static String daftarKursi(Transaksi t) {
        StringBuilder sb = new StringBuilder();
        for (DetailTransaksi d : t.getDetail())
            for (int id : d.getSeatIds()) {
                Seat s = DataSeat.getInstance().cari(id);
                if (s != null) sb.append(sb.length() > 0 ? ", " : "").append(s.getNomor());
            }
        return sb.length() == 0 ? "Free seating" : sb.toString();
    }

    @Override public void refresh() {
        isi.removeAll();
        switch (mode) {
            case TIKET_SAYA: bangunTiketSaya(); break;
            case RIWAYAT_USER: bangunTabel(false); break;
            default: bangunTabel(true);
        }
        isi.revalidate();
        isi.repaint();
    }

    private void bangunTiketSaya() {
        User u = AuthService.getAktif();
        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setOpaque(false);
        int n = 0;
        List<Transaksi> milik = service.milikUser(u.getId());
        for (int i = milik.size() - 1; i >= 0; i--) {
            Transaksi t = milik.get(i);
            if (t.getStatus() != Transaksi.Status.BERHASIL) continue;
            Event e = DataEvent.getInstance().cari(t.getDetail().get(0).getEventId());
            Tema.Kartu k = new Tema.Kartu(new GridLayout(0, 1, 0, 5));
            k.add(Tema.label("E-TICKET · BERHASIL", new Font("SansSerif", Font.BOLD, 11), Tema.TEAL));
            k.add(Tema.label(e == null ? "-" : e.getNama(), new Font("SansSerif", Font.BOLD, 15), Tema.INK));
            k.add(Tema.muted(e == null ? "-" : Tema.tanggal(e.getTanggal())));
            k.add(Tema.muted(e == null ? "-" : "⌖ " + e.getLokasi()));
            k.add(Tema.label(namaKategori(t) + " · " + t.getJumlahTiket() + " tiket", Tema.FONT_BOLD, Tema.INK));
            k.add(Tema.label("Kursi: " + daftarKursi(t), Tema.FONT, Tema.INK));
            k.add(Tema.label(Tema.rupiah(t.getTotal()), Tema.FONT_H2, Tema.PINK));
            k.add(Tema.muted("Kode tiket: " + t.getKode()));
            grid.add(k);
            n++;
        }
        if (n == 0) {
            JPanel kosong = new Tema.Kartu(new BorderLayout());
            kosong.add(Tema.muted("Kamu belum memiliki tiket. Yuk pilih event di menu Dashboard Event!"), BorderLayout.CENTER);
            isi.add(kosong, BorderLayout.NORTH);
        } else {
            JPanel pembungkus = new JPanel(new BorderLayout());
            pembungkus.setOpaque(false);
            pembungkus.add(grid, BorderLayout.NORTH);
            isi.add(Tema.scroll(pembungkus), BorderLayout.CENTER);
        }
    }

    private void bangunTabel(boolean admin) {
        model = admin
                ? Tema.modelTabel("Kode", "Pembeli", "Event", "Tanggal", "Qty", "Total", "Status")
                : Tema.modelTabel("Kode Transaksi", "Tanggal", "Event", "Kategori", "Qty", "Total", "Status");
        tabel = Tema.tabel(model);

        List<Transaksi> data = admin ? service.semua() : service.milikUser(AuthService.getAktif().getId());
        for (int i = data.size() - 1; i >= 0; i--) {          // terbaru di atas
            Transaksi t = data.get(i);
            if (admin) {
                model.addRow(barisAdmin(t));
            } else {
                Event e = DataEvent.getInstance().cari(t.getDetail().get(0).getEventId());
                model.addRow(new Object[]{t.getKode(), Tema.tanggal(t.getTanggal()), e == null ? "-" : e.getNama(),
                        namaKategori(t), t.getJumlahTiket(), Tema.rupiah(t.getTotal()), t.getStatus()});
            }
        }

        Tema.Kartu k = new Tema.Kartu(new BorderLayout(0, 10));
        k.add(Tema.scrollTabel(tabel), BorderLayout.CENTER);
        if (admin) {
            JButton btnBatal = Tema.bahaya("Batalkan Transaksi Terpilih");
            btnBatal.addActionListener(e -> batalkanTerpilih());
            JPanel aksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            aksi.setOpaque(false);
            aksi.add(btnBatal);
            k.add(aksi, BorderLayout.SOUTH);
        }
        isi.add(k, BorderLayout.CENTER);
    }

    private void batalkanTerpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) { Tema.info(this, "Pilih transaksi pada tabel terlebih dahulu."); return; }
        String kode = (String) model.getValueAt(tabel.convertRowIndexToModel(baris), 0);
        Transaksi target = null;
        for (Transaksi t : service.semua()) if (t.getKode().equals(kode)) target = t;
        if (target == null) return;
        if (target.getStatus() != Transaksi.Status.BERHASIL) { Tema.info(this, "Transaksi ini sudah dibatalkan."); return; }
        if (!Tema.konfirmasi(this, "Batalkan transaksi " + kode + "? Stok & kursi akan dikembalikan.")) return;
        try {
            service.batalkan(target);
            refresh();
        } catch (RuntimeException ex) {
            Tema.error(this, ex.getMessage());
        }
    }
}
