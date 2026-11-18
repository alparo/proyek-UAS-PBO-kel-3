package gui;

import collection.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import model.*;
import model.Event;
import service.AuthService;
import service.LaporanService;

/**
 * Jendela utama setelah login. Layout:
 *   BorderLayout -> WEST: sidebar menu | NORTH: top bar | CENTER: CardLayout (halaman-halaman)
 * Menu menyesuaikan role: ADMIN (6 menu) atau USER/pembeli (3 menu).
 */
public class DashboardFrame extends JFrame {
    private final User user;
    private final CardLayout kartu = new CardLayout();
    private final JPanel isi = new JPanel(kartu);
    private final JPanel sidebar = new JPanel();
    private final JLabel lblJudul = Tema.label("", Tema.FONT_BOLD, Tema.INK);
    private final JLabel lblSub = Tema.muted("");
    private final Map<String, JButton> tombolMenu = new LinkedHashMap<>();
    private final Map<String, Refreshable> halaman = new LinkedHashMap<>();
    private final Map<String, String[]> judulMenu = new LinkedHashMap<>();

    public DashboardFrame(User user) {
        super("KonserKu — " + (user.isAdmin() ? "Admin Panel" : "Pembeli"));
        this.user = user;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1240, 780);
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buatSidebar(), BorderLayout.WEST);
        JPanel kanan = new JPanel(new BorderLayout());
        kanan.add(buatTopBar(), BorderLayout.NORTH);
        isi.setBackground(Tema.BG);
        kanan.add(isi, BorderLayout.CENTER);
        add(kanan, BorderLayout.CENTER);

        if (user.isAdmin()) {
            tambahMenu("ringkasan", "▦   Ringkasan", "Ringkasan Admin", "Pantau operasional penjualan tiket", new RingkasanPanel());
            tambahMenu("event", "◈   Kelola Event", "Kelola Event", "Tambah, ubah, dan nonaktifkan event", new EventFrame());
            tambahMenu("tiket", "▤   Kategori Tiket", "Kategori Tiket", "Atur kategori, harga, dan stok per zona", new TiketFrame());
            tambahMenu("zona", "▦   Zona & Kursi", "Kelola Zona & Kursi", "Tentukan zona numbered seat atau free seating", new ZonaSeatFrame());
            tambahMenu("transaksi", "⇄   Transaksi", "Kelola Transaksi", "Pantau pembelian dan ubah status transaksi", new RiwayatTransaksiFrame(RiwayatTransaksiFrame.Mode.ADMIN));
            tambahMenu("laporan", "▥   Laporan Pendapatan", "Laporan Pendapatan", "Ringkasan pendapatan dari transaksi berhasil", new LaporanFrame());
            buka("ringkasan");
        } else {
            tambahMenu("beli", "⌂   Dashboard Event", "Temukan Event", "Pilih acara favorit dan pesan tiketmu",
                    new PembelianFrame(() -> buka("tiketsaya")));
            tambahMenu("tiketsaya", "▤   Tiket Saya", "Tiket Saya", "Tiket yang sudah berhasil kamu beli",
                    new RiwayatTransaksiFrame(RiwayatTransaksiFrame.Mode.TIKET_SAYA));
            tambahMenu("riwayat", "◷   Riwayat Transaksi", "Riwayat Transaksi", "Semua aktivitas pembelian tiket",
                    new RiwayatTransaksiFrame(RiwayatTransaksiFrame.Mode.RIWAYAT_USER));
            buka("beli");
        }
    }

    private JPanel buatSidebar() {
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(new CompoundBorderKanan());
        sidebar.setPreferredSize(new Dimension(235, 100));
        sidebar.add(Box.createVerticalStrut(8));
        JLabel brand = Tema.label("<html><span style='color:#E73569'>◆</span> KonserKu</html>",
                new Font("SansSerif", Font.BOLD, 22), Tema.NAVY);
        brand.setBorder(new EmptyBorder(8, 18, 10, 10));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brand);

        JLabel role = Tema.label(user.isAdmin() ? "ADMIN PANEL" : "PEMBELI", new Font("SansSerif", Font.BOLD, 11), new Color(0xC72D58));
        role.setOpaque(true);
        role.setBackground(new Color(0xFDF0F4));
        role.setBorder(new EmptyBorder(7, 10, 7, 10));
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        wrap.setOpaque(false);
        wrap.add(role);
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        sidebar.add(wrap);
        sidebar.add(Box.createVerticalStrut(14));
        return sidebar;
    }

    private static class CompoundBorderKanan extends MatteBorder {
        CompoundBorderKanan() { super(0, 0, 0, 1, Tema.LINE); }
    }

    private JPanel buatTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(new javax.swing.border.CompoundBorder(new MatteBorder(0, 0, 1, 0, Tema.LINE), new EmptyBorder(12, 28, 12, 28)));
        JPanel kiri = new JPanel(new GridLayout(2, 1));
        kiri.setOpaque(false);
        kiri.add(lblJudul);
        kiri.add(lblSub);
        JPanel kananBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        kananBar.setOpaque(false);
        JButton keluar = Tema.sekunder("Keluar");
        keluar.addActionListener(e -> logout());
        kananBar.add(Tema.muted(user.getNama()));
        kananBar.add(keluar);
        bar.add(kiri, BorderLayout.WEST);
        bar.add(kananBar, BorderLayout.EAST);
        return bar;
    }

    private <T extends JPanel & Refreshable> void tambahMenu(String kunci, String label, String judul, String sub, T panel) {
        JButton b = new JButton(label);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(11, 18, 11, 10));
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setBackground(Color.WHITE);
        b.setForeground(new Color(0x606B80));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.addActionListener(e -> buka(kunci));                // klik menu -> ganti halaman
        sidebar.add(b);

        tombolMenu.put(kunci, b);
        halaman.put(kunci, panel);
        judulMenu.put(kunci, new String[]{judul, sub});
        isi.add(panel, kunci);
    }

    private void buka(String kunci) {
        halaman.get(kunci).refresh();                          // muat ulang data sebelum tampil
        kartu.show(isi, kunci);
        tombolMenu.forEach((k, b) -> {
            boolean aktif = k.equals(kunci);
            b.setBackground(aktif ? Tema.PINK_SOFT : Color.WHITE);
            b.setForeground(aktif ? Tema.PINK : new Color(0x606B80));
        });
        lblJudul.setText(judulMenu.get(kunci)[0]);
        lblSub.setText(judulMenu.get(kunci)[1]);
    }

    private void logout() {
        AuthService.logout();
        new LoginFrame().setVisible(true);
        dispose();
    }

    // =====================================================================
    //  Halaman Ringkasan (khusus admin)
    // =====================================================================
    private static class RingkasanPanel extends JPanel implements Refreshable {
        private final LaporanService laporan = new LaporanService();

        RingkasanPanel() {
            super(new BorderLayout(0, 18));
            setBackground(Tema.BG);
            setBorder(new EmptyBorder(24, 28, 24, 28));
        }

        @Override public void refresh() {
            removeAll();
            JPanel atas = new JPanel(new BorderLayout(0, 14));
            atas.setOpaque(false);
            atas.add(Tema.judulHalaman("Dashboard Admin",
                    "Selamat datang. Semua perubahan di panel admin akan terlihat pada sisi pembeli."), BorderLayout.NORTH);

            JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
            stats.setOpaque(false);
            stats.add(Tema.stat("Total Event", String.valueOf(DataEvent.getInstance().jumlah()), "Event terdaftar"));
            stats.add(Tema.stat("Kategori Tiket", String.valueOf(DataTiket.getInstance().jumlah()), "Kategori aktif"));
            stats.add(Tema.stat("Transaksi", String.valueOf(DataTransaksi.getInstance().jumlah()), "Semua status"));
            stats.add(Tema.stat("Pendapatan", Tema.rupiah(laporan.totalPendapatan()), "Transaksi berhasil"));
            atas.add(stats, BorderLayout.CENTER);
            add(atas, BorderLayout.NORTH);

            Tema.Kartu panel = new Tema.Kartu(new BorderLayout(0, 12));
            panel.add(Tema.label("Aktivitas terbaru", Tema.FONT_H2, Tema.INK), BorderLayout.NORTH);
            DefaultTableModel m = Tema.modelTabel("Kode", "Pembeli", "Event", "Tanggal", "Qty", "Total", "Status");
            List<Transaksi> semua = DataTransaksi.getInstance().semua();
            for (int i = semua.size() - 1; i >= 0 && i >= semua.size() - 5; i--) {
                Transaksi t = semua.get(i);
                m.addRow(RiwayatTransaksiFrame.barisAdmin(t));
            }
            panel.add(Tema.scrollTabel(Tema.tabel(m)), BorderLayout.CENTER);
            add(panel, BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }
}
