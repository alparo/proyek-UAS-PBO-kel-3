package gui;

import collection.DataEvent;
import collection.DataZona;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Tiket;
import model.Zona;
import service.TiketService;

/** Halaman admin: kategori tiket (harga & stok per zona). */
public class TiketFrame extends JPanel implements Refreshable {
    private static final String[] JENIS = {"VIP", "FESTIVAL", "EARLY_BIRD"};

    /** Pembungkus supaya JComboBox menampilkan "Event — Zona" tetapi menyimpan objek Zona. */
    private static class ItemZona {
        final Zona zona;
        ItemZona(Zona z) { zona = z; }
        @Override public String toString() {
            var e = DataEvent.getInstance().cari(zona.getEventId());
            return (e == null ? "-" : e.getNama()) + " — " + zona.getNama();
        }
    }

    private final TiketService service = new TiketService();
    private final JComboBox<ItemZona> cmbZona = new JComboBox<>();
    private final JTextField txtKategori = Tema.input();
    private final JComboBox<String> cmbJenis = new JComboBox<>(JENIS);
    private final JTextField txtHarga = Tema.input();
    private final JTextField txtStok = Tema.input();
    private final DefaultTableModel model = Tema.modelTabel("ID", "Event / Zona", "Kategori", "Jenis", "Harga", "Stok", "Benefit");
    private final JTable tabel = Tema.tabel(model);
    private Tiket dipilih;

    private final JButton btnSimpan = Tema.utama("+ Simpan Kategori");
    private final JButton btnUbah = Tema.teal("Simpan Perubahan");
    private final JButton btnReset = Tema.sekunder("Reset Form");
    private final JButton btnHapus = Tema.bahaya("Hapus");

    public TiketFrame() {
        super(new BorderLayout(0, 16));
        setBackground(Tema.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        Tema.Kartu form = new Tema.Kartu(new BorderLayout(0, 8));
        form.add(Tema.label("Form Kategori Tiket", Tema.FONT_H2, Tema.INK), BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        cmbZona.setFont(Tema.FONT);
        cmbJenis.setFont(Tema.FONT);
        Tema.field(grid, 0, 0, 1, "Zona", cmbZona);
        Tema.field(grid, 1, 0, 1, "Nama kategori (mis. VIP / Festival)", txtKategori);
        Tema.field(grid, 0, 1, 1, "Jenis tiket", cmbJenis);
        Tema.field(grid, 1, 1, 1, "Harga (Rp)", txtHarga);
        Tema.field(grid, 0, 2, 1, "Stok", txtStok);
        form.add(grid, BorderLayout.CENTER);
        JPanel aksi = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        aksi.setOpaque(false);
        aksi.add(btnSimpan);
        aksi.add(btnUbah);
        aksi.add(btnReset);
        form.add(aksi, BorderLayout.SOUTH);
        add(form, BorderLayout.NORTH);

        Tema.Kartu daftar = new Tema.Kartu(new BorderLayout(0, 10));
        daftar.add(Tema.label("Daftar Kategori  (klik baris untuk mengedit)", Tema.FONT_H2, Tema.INK), BorderLayout.NORTH);
        daftar.add(Tema.scrollTabel(tabel), BorderLayout.CENTER);
        JPanel aksi2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        aksi2.setOpaque(false);
        aksi2.add(btnHapus);
        daftar.add(aksi2, BorderLayout.SOUTH);
        add(daftar, BorderLayout.CENTER);

        pasangEvent();
        btnUbah.setEnabled(false);
    }

    private void pasangEvent() {
        btnSimpan.addActionListener(e -> {
            try {
                ItemZona z = (ItemZona) cmbZona.getSelectedItem();
                service.tambah(z == null ? null : z.zona.getId(), txtKategori.getText(),
                        (String) cmbJenis.getSelectedItem(), angka(txtHarga, "Harga"), (int) angka(txtStok, "Stok"));
                kosongkan();
                refresh();
            } catch (RuntimeException ex) { Tema.error(this, ex.getMessage()); }
        });

        btnUbah.addActionListener(e -> {
            if (dipilih == null) return;
            try {
                ItemZona z = (ItemZona) cmbZona.getSelectedItem();
                service.ubah(dipilih, z == null ? null : z.zona.getId(), txtKategori.getText(),
                        (String) cmbJenis.getSelectedItem(), angka(txtHarga, "Harga"), (int) angka(txtStok, "Stok"));
                kosongkan();
                refresh();
            } catch (RuntimeException ex) { Tema.error(this, ex.getMessage()); }
        });

        btnReset.addActionListener(e -> kosongkan());

        btnHapus.addActionListener(e -> {
            Tiket t = tiketTerpilihTabel();
            if (t == null) { Tema.info(this, "Pilih kategori pada tabel terlebih dahulu."); return; }
            if (!Tema.konfirmasi(this, "Hapus kategori \"" + t.getKategori() + "\"?")) return;
            try { service.hapus(t); kosongkan(); refresh(); } catch (RuntimeException ex) { Tema.error(this, ex.getMessage()); }
        });

        tabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                Tiket t = tiketTerpilihTabel();
                if (t == null) return;
                dipilih = t;
                for (int i = 0; i < cmbZona.getItemCount(); i++)
                    if (cmbZona.getItemAt(i).zona.getId() == t.getZonaId()) cmbZona.setSelectedIndex(i);
                txtKategori.setText(t.getKategori());
                cmbJenis.setSelectedItem(t.getJenis());
                txtHarga.setText(String.valueOf((long) t.getHarga()));
                txtStok.setText(String.valueOf(t.getStok()));
                btnSimpan.setEnabled(false);
                btnUbah.setEnabled(true);
            }
        });
    }

    private double angka(JTextField f, String nama) {
        try { return Double.parseDouble(f.getText().trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException(nama + " harus berupa angka."); }
    }

    private Tiket tiketTerpilihTabel() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) return null;
        return collection.DataTiket.getInstance().cari((int) model.getValueAt(tabel.convertRowIndexToModel(baris), 0));
    }

    private void kosongkan() {
        dipilih = null;
        txtKategori.setText("");
        txtHarga.setText("");
        txtStok.setText("");
        cmbJenis.setSelectedIndex(0);
        tabel.clearSelection();
        btnSimpan.setEnabled(true);
        btnUbah.setEnabled(false);
    }

    @Override public void refresh() {
        cmbZona.removeAllItems();
        for (Zona z : DataZona.getInstance().semua()) cmbZona.addItem(new ItemZona(z));
        model.setRowCount(0);
        for (Tiket t : service.semua()) {
            Zona z = DataZona.getInstance().cari(t.getZonaId());
            var e = z == null ? null : DataEvent.getInstance().cari(z.getEventId());
            model.addRow(new Object[]{t.getId(), (e == null ? "-" : e.getNama()) + " / " + (z == null ? "-" : z.getNama()),
                    t.getKategori(), t.getJenis(), Tema.rupiah(t.getHarga()), t.getStok(), t.getBenefit()});
        }
    }
}
