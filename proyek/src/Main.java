package proyek.src;
import javax.swing.SwingUtilities;

import proyek.src.gui.PembelianFrame;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new PembelianFrame().setVisible(true);
        });

    }
}
