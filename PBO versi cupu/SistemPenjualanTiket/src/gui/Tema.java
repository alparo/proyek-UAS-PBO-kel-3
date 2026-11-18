package gui;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/** Warna, font, dan fabrik komponen supaya tampilan seragam dengan versi web KonserKu. */
public class Tema {
    public static final Color PINK = new Color(0xE73569);
    public static final Color PINK_SOFT = new Color(0xFFF0F4);
    public static final Color TEAL = new Color(0x08A995);
    public static final Color TEAL_SOFT = new Color(0xEAF7F3);
    public static final Color INK = new Color(0x20283A);
    public static final Color MUTED = new Color(0x788196);
    public static final Color BG = new Color(0xF5F7FB);
    public static final Color LINE = new Color(0xE8EBF2);
    public static final Color NAVY = new Color(0x17243C);
    public static final Color DANGER = new Color(0xD52D55);
    public static final Color DANGER_SOFT = new Color(0xFFF0F2);
    public static final Color GRAY_BTN = new Color(0xF1F3F8);

    public static final Font FONT = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_H1 = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_H2 = new Font("SansSerif", Font.BOLD, 17);

    private static final Locale ID = Locale.forLanguageTag("id-ID");
    private static final DateTimeFormatter FMT_TANGGAL = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", ID);

    public static String rupiah(double n) { return String.format(ID, "Rp %,.0f", n); }
    public static String tanggal(LocalDateTime t) { return t == null ? "-" : FMT_TANGGAL.format(t); }

    // ---------- label ----------
    public static JLabel label(String teks, Font font, Color warna) {
        JLabel l = new JLabel(teks);
        l.setFont(font);
        l.setForeground(warna);
        return l;
    }
    public static JLabel muted(String teks) { return label(teks, FONT, MUTED); }
    public static JLabel judul(String teks) { return label(teks, FONT_H1, INK); }

    // ---------- tombol ----------
    public static class Tombol extends JButton {
        private final Color warna;
        private final Color teks;

        public Tombol(String label, Color warna, Color teks) {
            super(label);
            this.warna = warna;
            this.teks = teks;
            setForeground(teks);
            setFont(FONT_BOLD);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 16, 9, 16));
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = warna;
            if (!isEnabled()) c = new Color(0xD9DDE6);
            else if (getModel().isPressed()) c = warna.darker();
            else if (getModel().isRollover()) c = warna.brighter();
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JButton utama(String t) { return new Tombol(t, PINK, Color.WHITE); }
    public static JButton teal(String t) { return new Tombol(t, TEAL, Color.WHITE); }
    public static JButton sekunder(String t) { return new Tombol(t, GRAY_BTN, new Color(0x3B455A)); }
    public static JButton bahaya(String t) { return new Tombol(t, DANGER_SOFT, DANGER); }

    // ---------- kartu ----------
    public static class Kartu extends JPanel {
        public Kartu(LayoutManager lm) {
            super(lm);
            setOpaque(false);
            setBorder(new EmptyBorder(18, 20, 18, 20));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.setColor(LINE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Kartu statistik: label kecil, angka besar, keterangan. */
    public static JPanel stat(String label, String nilai, String ket) {
        Kartu k = new Kartu(new GridLayout(3, 1, 0, 2));
        k.add(muted(label));
        k.add(label(nilai, new Font("SansSerif", Font.BOLD, 22), INK));
        k.add(label(ket, new Font("SansSerif", Font.PLAIN, 11), MUTED));
        return k;
    }

    /** Poster bergradasi untuk kartu event. */
    public static class Poster extends JPanel {
        private static final Color[][] WARNA = {
            {new Color(0x171D38), new Color(0xA72557)},
            {new Color(0x092C58), new Color(0x168BBA)},
            {new Color(0x4B1B54), new Color(0xD34C70)}
        };
        private final Color a, b;

        public Poster(int indeks, String nama, String tanggal, int tinggi) {
            super(new BorderLayout());
            a = WARNA[indeks % WARNA.length][0];
            b = WARNA[indeks % WARNA.length][1];
            setBorder(new EmptyBorder(14, 16, 14, 16));
            setPreferredSize(new Dimension(100, tinggi));
            JLabel atas = label("KONSERKU PRESENTS", new Font("SansSerif", Font.BOLD, 10), new Color(0xFFD9E4));
            JLabel tengah = label("<html><div style='width:230px'>" + nama + "</div></html>",
                    new Font("SansSerif", Font.BOLD, 18), Color.WHITE);
            JLabel bawah = label(tanggal, new Font("SansSerif", Font.PLAIN, 11), new Color(0xFFD9E4));
            add(atas, BorderLayout.NORTH);
            add(tengah, BorderLayout.CENTER);
            add(bawah, BorderLayout.SOUTH);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, a, getWidth(), getHeight(), b));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    // ---------- input & form ----------
    public static JTextField input() {
        JTextField f = new JTextField();
        styleInput(f);
        return f;
    }

    public static void styleInput(JComponent c) {
        c.setFont(FONT);
        c.setBorder(new CompoundBorder(new LineBorder(new Color(0xDFE4ED), 1, true), new EmptyBorder(7, 10, 7, 10)));
    }

    /** Menaruh label + komponen pada GridBagLayout (kolom, baris, lebar kolom). */
    public static void field(JPanel form, int kol, int baris, int span, String teks, JComponent komponen) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(label(teks, new Font("SansSerif", Font.BOLD, 11), new Color(0x687389)), BorderLayout.NORTH);
        p.add(komponen, BorderLayout.CENTER);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = kol; gc.gridy = baris; gc.gridwidth = span;
        gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 6, 6, 6);
        form.add(p, gc);
    }

    public static JPanel halaman() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));
        return p;
    }

    public static JPanel judulHalaman(String judul, String sub) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setOpaque(false);
        p.add(judul(judul));
        p.add(muted(sub));
        return p;
    }

    public static JScrollPane scroll(JComponent isi) {
        JScrollPane sp = new JScrollPane(isi);
        sp.setBorder(null);
        sp.getViewport().setBackground(BG);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        return sp;
    }

    // ---------- tabel ----------
    public static DefaultTableModel modelTabel(String... kolom) {
        return new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    public static JTable tabel(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(FONT);
        t.setRowHeight(34);
        t.setShowVerticalLines(false);
        t.setGridColor(LINE);
        t.setSelectionBackground(PINK_SOFT);
        t.setSelectionForeground(INK);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setAutoCreateRowSorter(true);
        t.setFillsViewportHeight(true);
        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(100, 36));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, false, false, r, c);
                l.setBackground(BG);
                l.setForeground(MUTED);
                l.setFont(new Font("SansSerif", Font.BOLD, 11));
                l.setBorder(new EmptyBorder(0, 10, 0, 10));
                return l;
            }
        });
        return t;
    }

    public static JScrollPane scrollTabel(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(new LineBorder(LINE));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    // ---------- dialog ----------
    public static void info(Component p, String pesan) { JOptionPane.showMessageDialog(p, pesan, "KonserKu", JOptionPane.INFORMATION_MESSAGE); }
    public static void error(Component p, String pesan) { JOptionPane.showMessageDialog(p, pesan, "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE); }
    public static boolean konfirmasi(Component p, String pesan) {
        return JOptionPane.showConfirmDialog(p, pesan, "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
