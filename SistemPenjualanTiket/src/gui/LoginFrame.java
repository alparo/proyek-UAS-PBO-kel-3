package gui;

import database.Koneksi;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;
import service.AuthService;

/** Layar login + registrasi pembeli. */
public class LoginFrame extends JFrame {
    private final JTextField txtEmail = Tema.input();
    private final JPasswordField txtPass = new JPasswordField();
    private final JLabel lblError = Tema.label(" ", Tema.FONT, Tema.DANGER);

    public LoginFrame() {
        super("KonserKu — Sistem Penjualan Tiket");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 700);
        setLocationRelativeTo(null);

        JPanel latar = new JPanel(new GridBagLayout());      // GridBagLayout: kartu login selalu di tengah
        latar.setBackground(new Color(0xF7F4FB));
        latar.add(buatKartu());
        setContentPane(latar);

        // ===== EVENT HANDLING =====
        txtPass.addActionListener(e -> prosesLogin());        // tekan Enter di kolom password
    }

    private JPanel buatKartu() {
        Tema.Kartu k = new Tema.Kartu(new GridBagLayout());
        k.setBorder(new EmptyBorder(28, 30, 28, 30));
        k.setPreferredSize(new Dimension(430, 600));
        Tema.styleInput(txtPass);

        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));

        isi.add(rata(Tema.label("<html><span style='color:#E73569'>◆</span> KonserKu</html>",
                new Font("SansSerif", Font.BOLD, 24), Tema.NAVY)));
        isi.add(Box.createVerticalStrut(14));
        isi.add(rata(Tema.judul("Selamat datang!")));
        isi.add(rata(Tema.muted("Masuk untuk mengelola atau membeli tiket event.")));
        isi.add(Box.createVerticalStrut(18));

        isi.add(rata(Tema.label("Email", new Font("SansSerif", Font.BOLD, 11), Tema.MUTED)));
        isi.add(Box.createVerticalStrut(4));
        isi.add(rata(txtEmail));
        isi.add(Box.createVerticalStrut(12));
        isi.add(rata(Tema.label("Password", new Font("SansSerif", Font.BOLD, 11), Tema.MUTED)));
        isi.add(Box.createVerticalStrut(4));
        isi.add(rata(txtPass));
        isi.add(Box.createVerticalStrut(6));
        isi.add(rata(lblError));
        isi.add(Box.createVerticalStrut(8));

        JButton btnMasuk = Tema.utama("Masuk");
        JButton btnDaftar = Tema.sekunder("Belum punya akun? Daftar");
        btnMasuk.addActionListener(e -> prosesLogin());
        btnDaftar.addActionListener(e -> bukaRegistrasi());
        isi.add(rata(btnMasuk));
        isi.add(Box.createVerticalStrut(8));
        isi.add(rata(btnDaftar));
        isi.add(Box.createVerticalStrut(18));

        JLabel hint = Tema.label("<html><b>Akun demo</b><br>Admin: admin@konserku.id / admin123<br>"
                + "Pembeli: demo@konserku.id / demo123</html>", new Font("SansSerif", Font.PLAIN, 12), new Color(0x657086));
        hint.setOpaque(true);
        hint.setBackground(Tema.BG);
        hint.setBorder(new EmptyBorder(10, 12, 10, 12));
        isi.add(rata(hint));
        isi.add(Box.createVerticalStrut(10));

        boolean db = Koneksi.isTersedia();
        JLabel status = Tema.label("<html>● " + (db ? "Mode database MySQL" : "Mode memori (tanpa database)")
                + "<br><span style='color:#788196'>" + Koneksi.getPesanStatus() + "</span></html>",
                new Font("SansSerif", Font.PLAIN, 11), db ? Tema.TEAL : Tema.DANGER);
        isi.add(rata(status));

        k.add(isi, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        return k;
    }

    private JComponent rata(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height + (c instanceof JTextField ? 8 : 0)));
        return c;
    }

    /** Alur: ambil input -> AuthService.login -> buka Dashboard, atau tampilkan pesan error. */
    private void prosesLogin() {
        lblError.setText(" ");
        try {
            User u = AuthService.login(txtEmail.getText(), new String(txtPass.getPassword()));
            new DashboardFrame(u).setVisible(true);
            dispose();
        } catch (IllegalArgumentException ex) {
            lblError.setText(ex.getMessage());
        }
    }

    private void bukaRegistrasi() {
        JTextField nama = Tema.input(), email = Tema.input();
        JPasswordField pass = new JPasswordField();
        Tema.styleInput(pass);
        JPanel form = new JPanel(new GridBagLayout());
        form.setPreferredSize(new Dimension(340, 230));
        Tema.field(form, 0, 0, 1, "Nama lengkap", nama);
        Tema.field(form, 0, 1, 1, "Email", email);
        Tema.field(form, 0, 2, 1, "Password (min. 6 karakter)", pass);

        while (JOptionPane.showConfirmDialog(this, form, "Buat Akun KonserKu",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                AuthService.register(nama.getText(), email.getText(), new String(pass.getPassword()));
                txtEmail.setText(email.getText().trim());
                txtPass.setText("");
                Tema.info(this, "Registrasi berhasil. Silakan login.");
                return;
            } catch (IllegalArgumentException ex) {
                Tema.error(this, ex.getMessage());
            } catch (RuntimeException ex) {
                Tema.error(this, "Gagal menyimpan akun: " + ex.getMessage());
                return;
            }
        }
    }
}
