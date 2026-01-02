import javax.swing.*;

public class HotelApp {
    public static void main(String[] args) {
        DataRegistry.loadFromFiles();

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}