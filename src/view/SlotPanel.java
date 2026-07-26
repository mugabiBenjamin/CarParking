package view;

import controller.ParkingController;
import model.ParkingLot;
import model.ParkingSlot;
import util.Logger;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public final class SlotPanel extends JPanel {
    private static final int GRID_ROWS = 2;
    private static final int GRID_COLUMNS = 5;
    private static final int GRID_HORIZONTAL_GAP = 10;
    private static final int GRID_VERTICAL_GAP = 10;
    private static final int HIGHLIGHT_DURATION_MILLISECONDS = 2000;
    private static final Color FOUND_SLOT_COLOR = Color.BLUE;

    private final ParkingController controller;
    private final ParkingLot lot;
    private final JLabel statusBar;
    private final ParkingSlotPanel[] slotPanels;

    public SlotPanel(ParkingController controller, ParkingLot lot, JLabel statusBar) {
        if (controller == null) {
            throw new IllegalArgumentException("Parking controller cannot be null");
        }

        if (lot == null) {
            throw new IllegalArgumentException("Parking lot cannot be null");
        }

        if (statusBar == null) {
            throw new IllegalArgumentException("Status bar cannot be null");
        }

        this.controller = controller;
        this.lot = lot;
        this.statusBar = statusBar;
        this.slotPanels = new ParkingSlotPanel[lot.getSize()];

        setLayout(new GridLayout(GRID_ROWS, GRID_COLUMNS, GRID_HORIZONTAL_GAP, GRID_VERTICAL_GAP));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeSlots();
    }

    private void initializeSlots() {
        removeAll();

        List<ParkingSlot> slots = lot.getSlots();

        for (int index = 0; index < slots.size(); index++) {
            ParkingSlotPanel slotPanel = new ParkingSlotPanel(slots.get(index), controller, statusBar);
            slotPanels[index] = slotPanel;
            add(slotPanel);
        }

        revalidate();
        repaint();
    }

    public void updateSlots() {
        try {
            List<ParkingSlot> slots = lot.getSlots();

            for (int index = 0; index < slots.size() && index < slotPanels.length; index++) {
                ParkingSlotPanel slotPanel = slotPanels[index];

                if (slotPanel != null) {
                    slotPanel.update(slots.get(index));
                }
            }

            revalidate();
            repaint();
        } catch (Exception exception) {
            Logger.error("Failed to update parking slots", exception);
            statusBar.setText("Failed to update parking slots");
        }
    }

    public void highlightSlot(int slotNumber) {
        try {
            Optional<ParkingSlotPanel> slotPanel = getSlotPanel(slotNumber);

            if (slotPanel.isEmpty()) {
                statusBar.setText("Cannot highlight invalid slot " + slotNumber);
                return;
            }

            ParkingSlotPanel panel = slotPanel.get();
            Color originalColor = panel.getBackground();

            panel.setBackground(FOUND_SLOT_COLOR);
            statusBar.setText("Highlighted slot " + slotNumber);

            Timer timer = new Timer(HIGHLIGHT_DURATION_MILLISECONDS, event -> {
                panel.setBackground(originalColor);
                lot.getSlot(slotNumber).ifPresent(panel::update);
                statusBar.setText("Ready");
            });

            timer.setRepeats(false);
            timer.start();
        } catch (Exception exception) {
            Logger.error("Failed to highlight slot " + slotNumber, exception);
            statusBar.setText("Failed to highlight slot " + slotNumber);
        }
    }

    public List<Integer> getSelectedSlots() {
        List<Integer> selectedSlots = new ArrayList<>();

        for (ParkingSlotPanel slotPanel : slotPanels) {
            if (slotPanel != null && slotPanel.isSelected()) {
                selectedSlots.add(slotPanel.getSlotNumber());
            }
        }

        if (selectedSlots.isEmpty()) {
            statusBar.setText("No slots selected");
        } else {
            statusBar.setText("Selected slots: " + selectedSlots);
        }

        return selectedSlots;
    }

    public void clearSelection() {
        for (ParkingSlotPanel slotPanel : slotPanels) {
            if (slotPanel != null) {
                slotPanel.clearSelection();
            }
        }

        updateSlots();
        statusBar.setText("Selection cleared");
    }

    private Optional<ParkingSlotPanel> getSlotPanel(int slotNumber) {
        if (slotNumber < 1 || slotNumber > slotPanels.length) {
            return Optional.empty();
        }

        return Optional.ofNullable(slotPanels[slotNumber - 1]);
    }
}