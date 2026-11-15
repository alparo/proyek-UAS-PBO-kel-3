package gui;

import collection.DataEvent;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Event;
import model.Seat;
import model.Zona;
import service.ZonaSeatService;

/** Halaman admin: zona (NUMBERED / FREE_SEATING) + denah kursi. */
public class ZonaSeatFrame extends JPanel implements Refreshable {
    private final ZonaSeatService service = new ZonaSeatService();
    private final JComboBox<Event> cmbEvent = new JComboBox<>();
    private final JTextField txtNama = Tema.input();
    private final JComboBox<Zona.Tipe> cmbTipe = new JComboBox<>(Zona.Tipe.values());
    private final JSpinner spnJumlah = new JSpinner(new SpinnerNumberModel(24, 0, 500, 1));
    private final DefaultTableModel model = Tema.modelTabel("ID", "Event", "Zona", "Tipe", "Kursi", "Tersedia", "Terjual");
    private final JTable tabel = Tema.tabel(model);
    private final JPanel denah = new JPanel(new GridLayout(0, 12, 6, 6));
    private final JLabel lblDenah = Tema.label("Pilih zona pada tabel untuk melihat denah kursi.", Tema.FONT_H2, Tema.INK);

    public ZonaSeatFrame() {
        super(new BorderLayout(0, 16));
        setBackground(Tema.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        Tema.Kartu form = new Tema.Kartu(new BorderLayout(0, 8));
        form.add(Tema.label("Tambah Zona", Tema.FONT_H2, Tema.INK), BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        cmbEvent.setFont(Tema.FONT);
        cmbTipe.setFont(Tema.FONT);
        Tema.field(grid, 0, 0, 1, "Event", cmbEvent);
        Tema.field(grid, 1, 0, 1, "Nama zona (VIP / Festival / Tribune)", txtNama);
        Tema.field(grid, 0, 1, 1, "Tipe zona", cmbTipe);
        Tema.field(grid, 1, 1, 1, "Jumlah kursi (khusus NUMBERED)", spnJumlah);
        form.add(grid, BorderLayout.CENTER);
        JButton btnSimpan = Tema.utama("+ Simpan Zona");
        JButton btnHapus = Tema.bahaya("Hapus Zona Terpilih");
        JPanel aksi = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        aksi.setOpaque(false);
        aksi.add(btnSimpan);
        aksi.add(btnHapus);
        form.add(aksi, BorderLayout.SOUTH);
        add(form, BorderLayout.NORTH);

        Tema.Kartu daftar = new Tema.Kartu(new BorderLayout(0, 10));
        daftar.add(Tema.label("Zona dan Kursi", Tema.FONT_H2, Tema.INK), BorderLayout.NORTH);
        daftar.add(Tema.scrollTabel(tabel), BorderLayout.CENTER);
        add(daftar, BorderLayout.CENTER);

        Tema.Kartu kartuDenah = new Tema.Kartu(new BorderLayout(0, 8));
        kartuDenah.add(lblDenah, BorderLayout.NORTH);
        denah.setOpaque(false);
        JScrollPane sp = new JScrollPane(denah);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setPreferredSize(new Dimension(100, 130));
        kartuDenah.add(sp, BorderLayout.CENTER);
        add(kartuDenah, BorderLayout.SOUTH);

        // ===== EVENT HANDLING =====
        cmbTipe.addActionListener(e -> spnJumlah.setEnabled(cmbTipe.getSelectedItem() == Zona.Tipe.NUMBERED));

        btnSimpan.addActionListener(e -> {
            try {
                Event ev = (Event) cmbEvent.getSelectedItem();
                service.tambahZona(ev == null ? null : ev.getId(), txtNama.getText(),
                        (Zona.Tipe) cmbTipe.getSelectedItem(), (Integer) spnJumlah.getValue());
                txtNama.setText("");
                refresh();
            } catch (RuntimeException ex) { Tema.error(this, ex.getMessage()); }
        });

        btnHapus.addActionListener(e -> {
            Zona z = zonaTerpilih();
            if (z == null) { Tema.info(this, "Pilih zona pada tabel terlebih dahulu."); return; }
            if (!Tema.konfirmasi(this, "Hapus zona \"" + z.getNama() + "\"?")) return;
            try { service.hapusZona(z); refresh(); } catch (RuntimeException ex) { Tema.error(this, ex.getMessage()); }
        });

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) tampilkanDenah(zonaTerpilih());
        });
    }

    private Zona zonaTerpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) return null;
        return collection.DataZona.getInstance().cari((int) model.getValueAt(tabel.convertRowIndexToModel(baris), 0));
    }

    private void tampilkanDenah(Zona z) {
        denah.removeAll();
        if (z == null) {
            lblDenah.setText("Pilih zona pada tabel untuk melihat denah kursi.");
        } else if (!z.isNumbered()) {
            lblDenah.setText("Zona " + z.getNama() + " memakai free seating (tanpa nomor kursi).");
        } else {
            List<Seat> kursi = service.kursiZona(z.getId());
            lblDenah.setText("Denah kursi zona " + z.getNama() + " — " + kursi.size() + " kursi");
            for (Seat s : kursi) {
                JLabel l = new JLabel(s.getNomor(), SwingConstants.CENTER);
                l.setOpaque(true);
                l.setFont(new Font("SansSerif", Font.PLAIN, 11));
                l.setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
                l.setBackground(s.isTersedia() ? Tema.TEAL_SOFT : new Color(0xEDF0F5));
                l.setForeground(s.isTersedia() ? new Color(0x078575) : new Color(0xA3AABA));
                denah.add(l);
            }
        }
        denah.revalidate();
        denah.repaint();
    }

    @Override public void refresh() {
        Object pilihan = cmbEvent.getSelectedItem();
        cmbEvent.removeAllItems();
        for (Event e : DataEvent.getInstance().semua()) cmbEvent.addItem(e);
        if (pilihan != null) cmbEvent.setSelectedItem(pilihan);
        model.setRowCount(0);
        for (Zona z : service.semua()) {
            Event e = DataEvent.getInstance().cari(z.getEventId());
            int tersedia = service.tersedia(z.getId()), terjual = service.terjual(z.getId());
            model.addRow(new Object[]{z.getId(), e == null ? "-" : e.getNama(), z.getNama(), z.getTipe().getLabel(),
                    tersedia + terjual, tersedia, terjual});
        }
        tampilkanDenah(null);
    }
}
