package presentation.swing.panels;

import presentation.swing.ParkingViewController;
import presentation.swing.components.PlateInputPanel;
import presentation.swing.resources.IconUtil;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;

public final class ParkPanel extends JPanel {
    private final PlateInputPanel plateInputPanel;

    public ParkPanel(ParkingViewController controller, IconUtil iconUtil) {
        if (controller == null) {
            throw new IllegalArgumentException("Parking view controller cannot be null");
        }

        if (iconUtil == null) {
            throw new IllegalArgumentException("Icon utility cannot be null");
        }

        setLayout(new BorderLayout());

        this.plateInputPanel = new PlateInputPanel(
                "Park a Car",
                "Park",
                iconUtil.createCarIcon(16, 16),
                "Park a car in an available slot",
                controller,
                iconUtil,
                controller::parkCar
        );

        add(plateInputPanel, BorderLayout.CENTER);
    }

    public JTextField getPlateInput() {
        return plateInputPanel.getInputField();
    }
}