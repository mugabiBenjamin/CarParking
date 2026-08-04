package presentation.swing.panels;

import presentation.swing.ParkingViewController;
import presentation.swing.components.PlateInputPanel;
import presentation.swing.resources.IconUtil;

import javax.swing.JPanel;
import java.awt.BorderLayout;

public final class SearchPanel extends JPanel {
    public SearchPanel(ParkingViewController controller, IconUtil iconUtil) {
        if (controller == null) {
            throw new IllegalArgumentException("Parking view controller cannot be null");
        }

        if (iconUtil == null) {
            throw new IllegalArgumentException("Icon utility cannot be null");
        }

        setLayout(new BorderLayout());

        PlateInputPanel plateInputPanel = new PlateInputPanel(
                "Search for a Car",
                "Search",
                iconUtil.createSearchIcon(16, 16),
                "Search for a parked car by license plate",
                controller,
                iconUtil,
                controller::findCar
        );

        add(plateInputPanel, BorderLayout.CENTER);
    }
}