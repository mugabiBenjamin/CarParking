package view;

import controller.ParkingController;
import model.ParkingLot;
import model.ParkingSlot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SlotPanel extends JPanel {
    private final ParkingController controller;
    private final ParkingLot lot;
    private final JLabel statusBar;
    private final List<ParkingSlotPanel> slotPanels;

    public SlotPanel(ParkingController controller, ParkingLot lot, JLabel statusBar) {
        this.controller = controller;
        this.lot = lot;
        this.statusBar = statusBar;
        this.slotPanels = new ArrayList<>();
        setLayout(new GridLayout(0, 5, 5, 5)); // Reduced gap
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Reduced padding
        initSlots();
    }

    private void initSlots() {
        for (ParkingSlot slot : lot.getSlots()) {
            ParkingSlotPanel slotPanel = new ParkingSlotPanel(slot, controller, statusBar);
            slotPanels.add(slotPanel);
            add(slotPanel);
        }
    }

    public void updateSlots() {
        for (ParkingSlotPanel slotPanel : slotPanels) {
            slotPanel.updateSlot();
        }
        revalidate();
        repaint();
    }

    public void highlightSlot(int slotNumber) {
        ParkingSlotPanel slotPanel = slotPanels.get(slotNumber - 1);
        Color originalColor = slotPanel.getBackground();
        slotPanel.setBackground(Color.BLUE);
        Timer timer = new Timer(2000, e -> {
            slotPanel.setBackground(originalColor);
            slotPanel.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public List<Integer> getSelectedSlots() {
        return slotPanels.stream()
                .filter(ParkingSlotPanel::isSelected)
                .map(panel -> panel.getSlot().getNumber())
                .collect(Collectors.toList());
    }

    public void clearSelection() {
        slotPanels.forEach(ParkingSlotPanel::clearSelection);
    }

    // For testing
    public List<ParkingSlotPanel> getSlotPanels() {
        return slotPanels;
    }
}