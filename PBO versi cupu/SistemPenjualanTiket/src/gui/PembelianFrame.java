package gui;

import collection.DataSeat;
import collection.DataZona;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.*;
import model.Event;
import service.AuthService;
import service.EventService;
import service.TiketService;
import service.TransaksiService;

/**
 * Halaman pembeli: daftar event -> detail & pilih tiket -> pilih kursi (jika NUMBERED) -> checkout.
 * Setiap langkah adalah panel baru yang dipasang ke "konten" (BorderLayout CENTER).
 */
public class PembelianFrame extends JPanel implements Refreshable {
    private final EventService eventService = new EventService();
    private final TiketService tiketService = new TiketService();
    private final TransaksiService trxService = new TransaksiService();
    private final Runnable keTiketSaya;                       // callback: pindah ke menu "Tiket Saya"

    private final JPanel konten = new JPanel(new BorderLayout());
    private JPanel gridEvent;
    private JTextField txtCari;

    // state alur pembelian
    private Event eventDipilih;
    private Tiket tiketDipilih;
    private int qty = 1;
    private final Set<Integer> kursiDipilih = new LinkedHashSet<>();   // Set: tidak boleh ada kursi ganda, urutan klik terjaga

    public PembelianFrame(Runnable keTiketSaya) {
        super(new BorderLayout());
        this.keTiketSaya = keTiketSaya;
        setBackground(Tema.BG);
        konten.setBackground(Tema.BG);
        add(konten, BorderLayout.CENTER);
    }

    @Override public void refresh() {
        eventDipilih = null;
        tiketDipilih = null;
        kursiDipilih.clear();
        tampilDaftar();
    }

    private void ganti(JComponent c) {
        konten.removeAll();
        konten.add(c, BorderLayout.CENTER);
        konten.revalidate();
        konten.repaint();
    }

    private JPanel kembali(Runnable aksi) {
        JButton b = Tema.sekunder("← Kembali");
        b.addActionListener(e -> aksi.run());
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));   // FlowLayout: tombol tidak melebar
        p.setOpaque(false);
        p.add(b);
        return p;
    }

    // ===================================================================
    //  LANGKAH 1: daftar event
    // ===================================================================
    private void tampilDaftar() {
        JPanel hal = Tema.halaman();
        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);
        atas.add(Tema.judulHalaman("Event pilihan untukmu", "Cari pengalaman seru berikutnya."), BorderLayout.WEST);
        txtCari = Tema.input();
        txtCari.setPreferredSize(new Dimension(260, 38));
        txtCari.setToolTipText("Cari nama event...");
        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 10));
        kanan.setOpaque(false);
        kanan.add(Tema.muted("Cari:"));
        kanan.add(txtCari);
        atas.add(kanan, BorderLayout.EAST);
        hal.add(atas, BorderLayout.NORTH);

        gridEvent = new JPanel(new GridLayout(0, 3, 16, 16));
        gridEvent.setOpaque(false);
        JPanel pembungkus = new JPanel(new BorderLayout());
        pembungkus.setOpaque(false);
        pembungkus.add(gridEvent, BorderLayout.NORTH);
        hal.add(Tema.scroll(pembungkus), BorderLayout.CENTER);

        // KeyAdapter: filter daftar setiap kali pengguna mengetik
        txtCari.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { isiDaftar(txtCari.getText()); }
        });
        isiDaftar("");
        ganti(hal);
    }

    private void isiDaftar(String kata) {
        gridEvent.removeAll();
        String q = kata.trim().toLowerCase();
        int i = 0;
        for (Event e : eventService.aktif()) {
            if (!q.isEmpty() && !e.getNama().toLowerCase().contains(q)) continue;
            gridEvent.add(kartuEvent(e, i++));
        }
        if (i == 0) gridEvent.add(Tema.muted("Event tidak ditemukan."));
        gridEvent.revalidate();
        gridEvent.repaint();
    }

    private JPanel kartuEvent(Event e, int indeks) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Color.WHITE);
        c.setBorder(new LineBorder(Tema.LINE));
        c.add(new Tema.Poster(indeks, e.getNama(), Tema.tanggal(e.getTanggal()), 135), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(0, 1, 0, 4));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(12, 14, 14, 14));
        body.add(Tema.label(e.getNama(), new Font("SansSerif", Font.BOLD, 15), Tema.INK));
        body.add(Tema.muted("⌖ " + e.getLokasi()));
        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setOpaque(false);
        bawah.add(Tema.label("Mulai " + Tema.rupiah(tiketService.hargaMulai(e.getId())), Tema.FONT_H2, Tema.PINK), BorderLayout.WEST);
        JButton btn = Tema.utama("Lihat Tiket");
        btn.addActionListener(ev -> { eventDipilih = e; tiketDipilih = null; tampilDetail(); });
        bawah.add(btn, BorderLayout.EAST);
        body.add(bawah);
        c.add(body, BorderLayout.CENTER);
        return c;
    }

    // ===================================================================
    //  LANGKAH 2: detail event + pilih kategori & jumlah
    // ===================================================================
    private void tampilDetail() {
        JPanel hal = Tema.halaman();
        hal.add(kembali(this::tampilDaftar), BorderLayout.NORTH);

        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));

        Tema.Kartu info = new Tema.Kartu(new BorderLayout(0, 10));
        info.add(new Tema.Poster(eventService.semua().indexOf(eventDipilih), eventDipilih.getNama(),
                Tema.tanggal(eventDipilih.getTanggal()) + " · " + eventDipilih.getLokasi(), 150), BorderLayout.NORTH);
        info.add(Tema.label("<html><div style='width:600px'><b style='font-size:15px'>" + eventDipilih.getNama() + "</b><br>"
                + eventDipilih.getDeskripsi() + "<br><span style='color:#788196'>Tanggal: " + Tema.tanggal(eventDipilih.getTanggal())
                + "<br>Lokasi: " + eventDipilih.getLokasi() + "</span></div></html>", Tema.FONT, Tema.INK), BorderLayout.CENTER);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(info);
        isi.add(Box.createVerticalStrut(14));

        Tema.Kartu pilih = new Tema.Kartu(new BorderLayout(0, 10));
        pilih.add(Tema.label("Pilih kategori tiket", Tema.FONT_H2, Tema.INK), BorderLayout.NORTH);

        JSpinner spn = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        JLabel lblTotal = Tema.label("Total sementara: -", Tema.FONT_H2, Tema.INK);
        JButton btnLanjut = Tema.utama("Lanjutkan →");

        JPanel daftar = new JPanel(new GridLayout(0, 1, 0, 8));
        daftar.setOpaque(false);
        ButtonGroup grup = new ButtonGroup();
        List<Tiket> tikets = tiketService.byEvent(eventDipilih.getId());
        for (Tiket t : tikets) {
            Zona z = DataZona.getInstance().cari(t.getZonaId());
            JPanel baris = new JPanel(new BorderLayout(10, 0));
            baris.setBackground(Color.WHITE);
            baris.setBorder(new javax.swing.border.CompoundBorder(new LineBorder(Tema.LINE, 1, true), new EmptyBorder(10, 12, 10, 12)));
            JRadioButton rb = new JRadioButton("<html><b>" + t.getKategori() + "</b> &nbsp;<span style='color:#788196'>"
                    + (z.isNumbered() ? "Nomor kursi" : "Free seating") + " · " + t.getBenefit() + " · Stok " + t.getStok() + "</span></html>");
            rb.setOpaque(false);
            rb.setFont(Tema.FONT);
            rb.setEnabled(t.getStok() > 0);
            grup.add(rb);
            baris.add(rb, BorderLayout.CENTER);
            baris.add(Tema.label(Tema.rupiah(t.getHarga()), Tema.FONT_H2, Tema.PINK), BorderLayout.EAST);
            daftar.add(baris);

            // ActionListener radio: simpan pilihan, atur batas jumlah tiket, hitung total
            rb.addActionListener(e -> {
                tiketDipilih = t;
                int maks = Math.max(1, Math.min(t.getStok(), t.getMaksPerTransaksi()));
                spn.setModel(new SpinnerNumberModel(1, 1, maks, 1));
                lblTotal.setText("Total sementara: " + Tema.rupiah(t.hitungTotal(1)));
            });
        }
        if (tikets.isEmpty()) daftar.add(Tema.muted("Belum ada kategori tiket untuk event ini."));
        pilih.add(daftar, BorderLayout.CENTER);

        // ChangeListener spinner: hitung ulang total saat jumlah berubah
        spn.addChangeListener(e -> {
            if (tiketDipilih != null)
                lblTotal.setText("Total sementara: " + Tema.rupiah(tiketDipilih.hitungTotal((Integer) spn.getValue())));
        });

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        bawah.setOpaque(false);
        bawah.add(Tema.label("Jumlah tiket:", Tema.FONT_BOLD, Tema.INK));
        spn.setPreferredSize(new Dimension(70, 34));
        bawah.add(spn);
        bawah.add(lblTotal);
        bawah.add(btnLanjut);
        pilih.add(bawah, BorderLayout.SOUTH);
        pilih.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(pilih);

        btnLanjut.addActionListener(e -> {
            if (tiketDipilih == null) { Tema.info(this, "Pilih kategori tiket dahulu."); return; }
            qty = (Integer) spn.getValue();
            kursiDipilih.clear();
            Zona z = DataZona.getInstance().cari(tiketDipilih.getZonaId());
            if (z.isNumbered()) tampilKursi(); else tampilCheckout();
        });

        hal.add(Tema.scroll(isi), BorderLayout.CENTER);
        ganti(hal);
    }

    // ===================================================================
    //  LANGKAH 3: pilih kursi (hanya zona NUMBERED)
    // ===================================================================
    private static class KursiTombol extends JButton {
        private final boolean terjual;
        private boolean dipilih;

        KursiTombol(Seat s) {
            super(s.getNomor());
            terjual = !s.isTersedia();
            setFont(new Font("SansSerif", Font.BOLD, 11));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setEnabled(!terjual);
            setCursor(Cursor.getPredefinedCursor(terjual ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
        }
        void setDipilih(boolean b) { dipilih = b; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color bg = terjual ? new Color(0xEDF0F5) : dipilih ? Tema.PINK : Tema.TEAL_SOFT;
            Color fg = terjual ? new Color(0xA3AABA) : dipilih ? Color.WHITE : new Color(0x078575);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.setColor(fg);
            FontMetrics fm = g2.getFontMetrics(getFont());
            g2.setFont(getFont());
            g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }

    private void tampilKursi() {
        JPanel hal = Tema.halaman();
        hal.add(kembali(this::tampilDetail), BorderLayout.NORTH);

        Tema.Kartu k = new Tema.Kartu(new BorderLayout(0, 12));
        k.add(Tema.label("Pilih " + qty + " kursi — " + tiketDipilih.getKategori(), Tema.FONT_H2, Tema.INK), BorderLayout.NORTH);

        JLabel lblTerpilih = Tema.label(" ", Tema.FONT_BOLD, Tema.INK);
        JButton btnLanjut = Tema.utama("Lanjut Checkout →");
        btnLanjut.setEnabled(false);

        List<Seat> kursi = DataSeat.getInstance().byZona(tiketDipilih.getZonaId());
        int kolom = 8;
        JPanel grid = new JPanel(new GridLayout(0, kolom, 8, 8));
        grid.setOpaque(false);
        int baris = (kursi.size() + kolom - 1) / kolom;
        grid.setPreferredSize(new Dimension(kolom * 62, Math.max(1, baris) * 42));

        Runnable perbarui = () -> {
            StringBuilder sb = new StringBuilder();
            for (int id : kursiDipilih) sb.append(sb.length() > 0 ? ", " : "").append(DataSeat.getInstance().cari(id).getNomor());
            lblTerpilih.setText("Terpilih " + kursiDipilih.size() + "/" + qty + (sb.length() > 0 ? " : " + sb : ""));
            btnLanjut.setEnabled(kursiDipilih.size() == qty);
        };

        for (Seat s : kursi) {
            KursiTombol kt = new KursiTombol(s);
            kt.addActionListener(e -> {                        // klik kursi: toggle pilih / batal pilih
                if (kursiDipilih.contains(s.getId())) kursiDipilih.remove(s.getId());
                else if (kursiDipilih.size() < qty) kursiDipilih.add(s.getId());
                else { Tema.info(this, "Jumlah kursi sudah sesuai jumlah tiket (" + qty + ")."); return; }
                kt.setDipilih(kursiDipilih.contains(s.getId()));
                perbarui.run();
            });
            grid.add(kt);
        }

        JPanel tengah = new JPanel(new BorderLayout(0, 10));
        tengah.setOpaque(false);
        JLabel panggung = Tema.label("P A N G G U N G", Tema.FONT_BOLD, Color.WHITE);
        panggung.setHorizontalAlignment(SwingConstants.CENTER);
        panggung.setOpaque(true);
        panggung.setBackground(Tema.NAVY);
        panggung.setBorder(new EmptyBorder(8, 0, 8, 0));
        tengah.add(panggung, BorderLayout.NORTH);
        JPanel wrapGrid = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapGrid.setOpaque(false);
        wrapGrid.add(grid);
        tengah.add(Tema.scroll(wrapGrid), BorderLayout.CENTER);
        tengah.add(Tema.muted("Hijau = tersedia · Abu-abu = terjual · Pink = pilihanmu"), BorderLayout.SOUTH);
        k.add(tengah, BorderLayout.CENTER);

        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setOpaque(false);
        bawah.add(lblTerpilih, BorderLayout.WEST);
        bawah.add(btnLanjut, BorderLayout.EAST);
        k.add(bawah, BorderLayout.SOUTH);

        btnLanjut.addActionListener(e -> tampilCheckout());
        perbarui.run();
        hal.add(k, BorderLayout.CENTER);
        ganti(hal);
    }

    // ===================================================================
    //  LANGKAH 4: checkout
    // ===================================================================
    private void tampilCheckout() {
        boolean bernomor = !kursiDipilih.isEmpty();
        JPanel hal = Tema.halaman();
        hal.add(kembali(bernomor ? this::tampilKursi : this::tampilDetail), BorderLayout.NORTH);

        Tema.Kartu k = new Tema.Kartu(new BorderLayout(0, 14));
        k.add(Tema.label("Ringkasan Pesanan", Tema.FONT_H2, Tema.INK), BorderLayout.NORTH);

        StringBuilder kursi = new StringBuilder();
        for (int id : kursiDipilih) kursi.append(kursi.length() > 0 ? ", " : "").append(DataSeat.getInstance().cari(id).getNomor());

        JPanel ringkas = new JPanel(new GridLayout(0, 2, 10, 10));
        ringkas.setOpaque(false);
        baris(ringkas, "Event", eventDipilih.getNama());
        baris(ringkas, "Kategori", tiketDipilih.getKategori() + " (" + tiketDipilih.getJenis() + ")");
        baris(ringkas, "Jumlah", qty + " tiket");
        baris(ringkas, "Kursi", bernomor ? kursi.toString() : "Free seating");
        baris(ringkas, "Harga satuan", Tema.rupiah(tiketDipilih.getHarga()));
        baris(ringkas, "Total pembayaran", Tema.rupiah(tiketDipilih.hitungTotal(qty)));
        k.add(ringkas, BorderLayout.CENTER);

        JPanel bawah = new JPanel(new BorderLayout(0, 10));
        bawah.setOpaque(false);
        JLabel hint = Tema.label("<html>Pembayaran pada prototype ini disimulasikan. Saat dikonfirmasi, transaksi langsung berstatus BERHASIL.</html>",
                new Font("SansSerif", Font.PLAIN, 12), new Color(0x657086));
        hint.setOpaque(true);
        hint.setBackground(Tema.BG);
        hint.setBorder(new EmptyBorder(10, 12, 10, 12));
        bawah.add(hint, BorderLayout.NORTH);
        JButton btnKonfirmasi = Tema.teal("Konfirmasi Pembelian");
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tombol.setOpaque(false);
        tombol.add(btnKonfirmasi);
        bawah.add(tombol, BorderLayout.SOUTH);
        k.add(bawah, BorderLayout.SOUTH);

        btnKonfirmasi.addActionListener(e -> prosesPembelian());

        JPanel pembungkus = new JPanel(new BorderLayout());
        pembungkus.setOpaque(false);
        pembungkus.add(k, BorderLayout.NORTH);
        hal.add(pembungkus, BorderLayout.CENTER);
        ganti(hal);
    }

    private void baris(JPanel p, String label, String nilai) {
        p.add(Tema.muted(label));
        p.add(Tema.label(nilai, Tema.FONT_BOLD, Tema.INK));
    }

    /** Alur: panggil service (validasi + simpan DB + update memori) -> notifikasi -> pindah ke Tiket Saya. */
    private void prosesPembelian() {
        try {
            Transaksi trx = trxService.beli(AuthService.getAktif(), tiketDipilih, qty, kursiDipilih);
            Tema.info(this, "Pembelian berhasil!\nKode transaksi: " + trx.getKode() + "\nTiket sudah masuk ke menu Tiket Saya.");
            kursiDipilih.clear();
            tiketDipilih = null;
            keTiketSaya.run();
        } catch (RuntimeException ex) {
            Tema.error(this, ex.getMessage());
            kursiDipilih.clear();
            tampilDetail();            // muat ulang stok & kursi terbaru
        }
    }
}
