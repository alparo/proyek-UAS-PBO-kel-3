package gui;

import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Event;
import model.Transaksi;
import service.LaporanService;

/** Halaman admin: laporan pendapatan dari transaksi BERHASIL. */
public class LaporanFrame extends JPanel implements Refreshable {
    private final LaporanService service = new LaporanService();

    public LaporanFrame() {
        super(new BorderLayout(0, 16));
        setBackground(Tema.BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));
    }

    @Override public void refresh() {
        removeAll();
        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stats.add(Tema.stat("Pendapatan Total", Tema.rupiah(service.totalPendapatan()), "Hanya transaksi berhasil"));
        stats.add(Tema.stat("Transaksi Berhasil", String.valueOf(service.jumlahTransaksiBerhasil()), "Jumlah pesanan"));
        stats.add(Tema.stat("Tiket Terjual", String.valueOf(service.totalTiketTerjual()), "Total unit tiket"));
        stats.add(Tema.stat("Rata-rata Transaksi", Tema.rupiah(service.rataRataTransaksi()), "Nilai per transaksi"));
        add(stats, BorderLayout.NORTH);

        JPanel tengah = new JPanel(new GridLayout(1, 2, 16, 0));
        tengah.setOpaque(false);
        tengah.add(panelPerEvent());
        tengah.add(panelRincian());
        add(tengah, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel panelPerEvent() {
        Tema.Kartu k = new Tema.Kartu(new BorderLayout(0, 10));
        k.add(Tema.label("Pendapatan per Event", Tema.FONT_H2, Tema.INK), BorderLayout.NORTH);
        JPanel daftar = new JPanel();
        daftar.setOpaque(false);
        daftar.setLayout(new BoxLayout(daftar, BoxLayout.Y_AXIS));
        double total = service.totalPendapatan();
        for (Map.Entry<Event, Double> en : service.pendapatanPerEvent().entrySet()) {
            JPanel baris = new JPanel(new BorderLayout(0, 4));
            baris.setOpaque(false);
            baris.setBorder(new EmptyBorder(8, 0, 8, 0));
            JPanel judul = new JPanel(new BorderLayout());
            judul.setOpaque(false);
            judul.add(Tema.label(en.getKey().getNama(), Tema.FONT_BOLD, Tema.INK), BorderLayout.WEST);
            judul.add(Tema.label(Tema.rupiah(en.getValue()), Tema.FONT_BOLD, Tema.INK), BorderLayout.EAST);
            JProgressBar bar = new JProgressBar(0, 1000);
            bar.setValue(total == 0 ? 0 : (int) (en.getValue() / total * 1000));
            bar.setForeground(Tema.TEAL);
            bar.setBackground(new Color(0xEDF0F5));
            bar.setBorderPainted(false);
            bar.setPreferredSize(new Dimension(100, 9));
            baris.add(judul, BorderLayout.NORTH);
            baris.add(bar, BorderLayout.CENTER);
            baris.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
            daftar.add(baris);
        }
        k.add(Tema.scroll(daftar), BorderLayout.CENTER);
        return k;
    }

    private JPanel panelRincian() {
        Tema.Kartu k = new Tema.Kartu(new BorderLayout(0, 10));
        k.add(Tema.label("Rincian Transaksi Berhasil", Tema.FONT_H2, Tema.INK), BorderLayout.NORTH);
        DefaultTableModel m = Tema.modelTabel("Kode", "Pembeli", "Event", "Tanggal", "Qty", "Total", "Status");
        for (Transaksi t : service.transaksiBerhasil()) m.addRow(RiwayatTransaksiFrame.barisAdmin(t));
        k.add(Tema.scrollTabel(Tema.tabel(m)), BorderLayout.CENTER);
        return k;
    }
}
