package proyek.src;
import javax.swing.*;

import proyek.src.model.Tiket;

import java.awt.*;

public class SistemTiket extends JFrame {

    private ManajemenTiket manajemen = new ManajemenTiket();

    private JComboBox<String> pilihTiket;
    private JTextField jumlah;
    private JTextArea output;

    public SistemTiket() {

        setTitle("Sistem Penjualan Tiket Konser");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel judul = new JLabel(
            "SISTEM PENJUALAN TIKET KONSER",
            JLabel.CENTER
        );

        judul.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(judul, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 15));

        inputPanel.add(new JLabel("Kategori Tiket:"));

        pilihTiket = new JComboBox<>(
            new String[]{"VIP", "Festival", "Early Bird"}
        );

        inputPanel.add(pilihTiket);

        inputPanel.add(new JLabel("Jumlah Tiket:"));

        jumlah = new JTextField();
        inputPanel.add(jumlah);

        JButton beli = new JButton("Beli Tiket");
        JButton tampil = new JButton("Tampilkan Stok");

        inputPanel.add(beli);
        inputPanel.add(tampil);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 15));
        contentPanel.add(inputPanel, BorderLayout.NORTH);

        output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font("Monospace", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(output);
        contentPanel.add(scroll, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        add(panel);

        beli.addActionListener(e -> beliTiket());
        tampil.addActionListener(e -> tampilkanStok());

        tampilkanStok();
    }

    private void beliTiket() {
        String kategori = pilihTiket.getSelectedItem().toString();

        try {
            int jumlahBeli = Integer.parseInt(jumlah.getText());

            if (jumlahBeli <= 0) {
                JOptionPane.showMessageDialog(
                    this, "Jumlah harus lebih dari 0!"
                );
                return;
            }

            Tiket tiket = manajemen.getTiket(kategori);

            if (manajemen.beliTiket(kategori, jumlahBeli)) {
                int total = manajemen.hitungTotal(kategori, jumlahBeli);

                output.setText(
                    "Pembelian Berhasil!\n\n" +
                    "Kategori: " + kategori + "\n" +
                    "Jumlah: " + jumlahBeli + "\n" +
                    "Harga: Rp " + tiket.getHarga() + "\n" +
                    "Fasilitas: " + tiket.getFasilitas() + "\n" +
                    "Total: Rp " + total + "\n" +
                    "Sisa Stok: " + tiket.getStok()
                );

            } else {
                JOptionPane.showMessageDialog(
                    this, "Stok tiket tidak mencukupi!"
                );
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this, "Masukkan jumlah tiket berupa angka!"
            );
        }
    }

    private void tampilkanStok() {
        output.setText("=== DAFTAR STOK TIKET ===\n\n");

        for (Tiket tiket : manajemen.getDaftarTiket().values()) {

            output.append(
                "Kategori: " + tiket.getKategori() + "\n" +
                "Harga: Rp " + tiket.getHarga() + "\n" +
                "Fasilitas: " + tiket.getFasilitas() + "\n" +
                "Stok: " + tiket.getStok() + "\n\n"
            );
        }
    }
}
