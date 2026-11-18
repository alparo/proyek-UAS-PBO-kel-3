package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Event;
import service.EventService;

/**
 * Halaman admin: kelola event (CRUD).
 * Komponen: JTextField, JTextArea, JComboBox, JButton, JTable. Layout: BorderLayout + GridBagLayout.
 */
public class EventFrame extends JPanel implements Refreshable {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final EventService service = new EventService();
    private final JTextField txtNama = Tema.input();
    private final JTextField txtTanggal = Tema.input();
    private final JTextField txtLokasi = Tema.input();
    private final JComboBox<Event.Status> cmbStatus = new JComboBox<>(Event.Status.values());
    private final JTextArea txtDeskripsi = new JTextArea(3, 20);
    private final DefaultTableModel model = Tema.modelTabel("ID", "Event", "Tanggal", "Lokasi", "Status");
    private final JTable tabel = Tema.tabel(model);
    private Event dipilih;      // event yang sedang diedit (null = mode tambah)

    private final JButton btnSimpan = Tema.utama("+ Simpan Event");
    private final JButton btnUbah = Tema.teal("Simpan Perubahan");
    private final JButton btnReset = Tema.sekunder("Reset Form");
    private final JButton btnToggle = Tema.sekunder("Aktifkan / Nonaktifkan");
    private final JButton btnHapus = Tema.bahaya("Hapus");

    public EventFrame() {
        super(new BorderLayout(0, 16));
        setBackground(Tema.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        add(buatForm(), BorderLayout.NORTH);
        add(buatTabel(), BorderLayout.CENTER);
        pasangEvent();
        modeTambah();
    }

    private JPanel buatForm() {
        Tema.Kartu k = new Tema.Kartu(new BorderLayout(0, 8));
        k.add(Tema.label("Form Event", Tema.FONT_H2, Tema.INK), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        Tema.styleInput(txtDeskripsi);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtTanggal.setToolTipText("Format: yyyy-MM-dd HH:mm, contoh 2026-12-20 19:00");
        cmbStatus.setFont(Tema.FONT);

        Tema.field(form, 0, 0, 1, "Nama Event", txtNama);
        Tema.field(form, 1, 0, 1, "Tanggal & waktu (yyyy-MM-dd HH:mm)", txtTanggal);
        Tema.field(form, 0, 1, 1, "Lokasi", txtLokasi);
        Tema.field(form, 1, 1, 1, "Status", cmbStatus);
        Tema.field(form, 0, 2, 2, "Deskripsi", txtDeskripsi);
        k.add(form, BorderLayout.CENTER);

        JPanel aksi = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        aksi.setOpaque(false);
        aksi.add(btnSimpan);
        aksi.add(btnUbah);
        aksi.add(btnReset);
        k.add(aksi, BorderLayout.SOUTH);
        return k;
    }

    private JPanel buatTabel() {
        Tema.Kartu k = new Tema.Kartu(new BorderLayout(0, 10));
        k.add(Tema.label("Daftar Event  (klik baris untuk mengedit)", Tema.FONT_H2, Tema.INK), BorderLayout.NORTH);
        k.add(Tema.scrollTabel(tabel), BorderLayout.CENTER);
        JPanel aksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        aksi.setOpaque(false);
        aksi.add(btnToggle);
        aksi.add(btnHapus);
        k.add(aksi, BorderLayout.SOUTH);
        return k;
    }

    // ================= EVENT HANDLING =================
    private void pasangEvent() {
        // 1) ActionListener: tombol Simpan -> ambil input -> panggil service -> update tabel
        btnSimpan.addActionListener(e -> {
            try {
                service.tambah(txtNama.getText(), parseTanggal(), txtLokasi.getText(),
                        txtDeskripsi.getText(), (Event.Status) cmbStatus.getSelectedItem());
                Tema.info(this, "Event berhasil ditambahkan.");
                kosongkanForm();
                refresh();
            } catch (RuntimeException ex) {
                Tema.error(this, ex.getMessage());
            }
        });

        btnUbah.addActionListener(e -> {
            if (dipilih == null) return;
            try {
                service.ubah(dipilih, txtNama.getText(), parseTanggal(), txtLokasi.getText(),
                        txtDeskripsi.getText(), (Event.Status) cmbStatus.getSelectedItem());
                kosongkanForm();
                refresh();
            } catch (RuntimeException ex) {
                Tema.error(this, ex.getMessage());
            }
        });

        btnReset.addActionListener(e -> kosongkanForm());

        btnToggle.addActionListener(e -> {
            Event ev = eventTerpilihTabel();
            if (ev == null) { Tema.info(this, "Pilih event pada tabel terlebih dahulu."); return; }
            try { service.toggleStatus(ev); refresh(); } catch (RuntimeException ex) { Tema.error(this, ex.getMessage()); }
        });

        btnHapus.addActionListener(e -> {
            Event ev = eventTerpilihTabel();
            if (ev == null) { Tema.info(this, "Pilih event pada tabel terlebih dahulu."); return; }
            if (!Tema.konfirmasi(this, "Hapus event \"" + ev.getNama() + "\"?")) return;
            try { service.hapus(ev); kosongkanForm(); refresh(); } catch (RuntimeException ex) { Tema.error(this, ex.getMessage()); }
        });

        // 2) MouseListener: klik baris tabel -> isi form dengan data event tsb
        tabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                Event ev = eventTerpilihTabel();
                if (ev == null) return;
                dipilih = ev;
                txtNama.setText(ev.getNama());
                txtTanggal.setText(FORMAT.format(ev.getTanggal()));
                txtLokasi.setText(ev.getLokasi());
                txtDeskripsi.setText(ev.getDeskripsi());
                cmbStatus.setSelectedItem(ev.getStatus());
                modeEdit();
            }
        });
    }

    private LocalDateTime parseTanggal() {
        try {
            return LocalDateTime.parse(txtTanggal.getText().trim(), FORMAT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Format tanggal harus yyyy-MM-dd HH:mm, contoh 2026-12-20 19:00");
        }
    }

    private Event eventTerpilihTabel() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) return null;
        int id = (int) model.getValueAt(tabel.convertRowIndexToModel(baris), 0);
        return collection.DataEvent.getInstance().cari(id);
    }

    private void kosongkanForm() {
        dipilih = null;
        txtNama.setText("");
        txtTanggal.setText("");
        txtLokasi.setText("");
        txtDeskripsi.setText("");
        cmbStatus.setSelectedIndex(0);
        tabel.clearSelection();
        modeTambah();
    }

    private void modeTambah() { btnSimpan.setEnabled(true); btnUbah.setEnabled(false); }
    private void modeEdit() { btnSimpan.setEnabled(false); btnUbah.setEnabled(true); }

    @Override public void refresh() {
        model.setRowCount(0);
        for (Event e : service.semua())
            model.addRow(new Object[]{e.getId(), e.getNama(), Tema.tanggal(e.getTanggal()), e.getLokasi(), e.getStatus()});
    }
}
