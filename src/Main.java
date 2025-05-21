import view.ParkingView;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new ParkingView(); // Launch GUI
        });
    }
}
