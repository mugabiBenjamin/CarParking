package presentation.swing.panels;

import application.dto.ParkingLotViewData;
import application.dto.SlotViewData;
import presentation.swing.ParkingViewController;
import presentation.swing.components.ParkingSlotPanel;
import presentation.swing.resources.IconUtil;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SlotPanel extends JPanel {
    private static final int GRID_COLUMNS = 5;
    private static final int HIGHLIGHT_DELAY_MILLISECONDS = 2000;

    private final ParkingViewController controller;
    private final IconUtil iconUtil;
    private final int parkingLotSize;
    private final Map<Integer, ParkingSlotPanel> slotPanels;

    public SlotPanel(ParkingViewController controller, IconUtil iconUtil, int parkingLotSize) {
        if (controller == null) {
            throw new IllegalArgumentException("Parking view controller cannot be null");
        }

        if (iconUtil == null) {
            throw new IllegalArgumentException("Icon utility cannot be null");
        }

        if (parkingLotSize <= 0) {
            throw new IllegalArgumentException("Parking lot size must be greater than zero");
        }

        this.controller = controller;
        this.iconUtil = iconUtil;
        this.parkingLotSize = parkingLotSize;
        this.slotPanels = new LinkedHashMap<>();

        initializeLayout(parkingLotSize);
        initializeEmptySlots();
    }

    public void displayParkingLot(ParkingLotViewData parkingLotViewData) {
        if (parkingLotViewData == null) {
            initializeEmptySlots();
            return;
        }

        removeAll();
        slotPanels.clear();
        initializeLayout(parkingLotViewData.getSize());

        for (SlotViewData slotViewData : parkingLotViewData.getSlots()) {
            ParkingSlotPanel panel = new ParkingSlotPanel(controller, iconUtil, slotViewData);
            slotPanels.put(slotViewData.getSlotNumber(), panel);
            add(panel);
        }

        revalidate();
        repaint();
    }

    public void highlightSlot(int slotNumber) {
        ParkingSlotPanel panel = slotPanels.get(slotNumber);

        if (panel == null) {
            return;
        }

        panel.setHighlighted(true);

        Timer timer = new Timer(HIGHLIGHT_DELAY_MILLISECONDS, event -> panel.setHighlighted(false));
        timer.setRepeats(false);
        timer.start();
    }

    public List<Integer> getSelectedSlots() {
        List<Integer> selectedSlots = new ArrayList<>();

        for (ParkingSlotPanel panel : slotPanels.values()) {
            if (panel.isSelectedForBatch()) {
                selectedSlots.add(panel.getSlotNumber());
            }
        }

        return selectedSlots;
    }

    public void clearSelection() {
        for (ParkingSlotPanel panel : slotPanels.values()) {
            panel.clearSelection();
        }
    }

    private void initializeEmptySlots() {
        removeAll();
        slotPanels.clear();

        for (int slotNumber = 1; slotNumber <= parkingLotSize; slotNumber++) {
            SlotViewData slotViewData = new SlotViewData(slotNumber, null, "");
            ParkingSlotPanel panel = new ParkingSlotPanel(controller, iconUtil, slotViewData);

            slotPanels.put(slotNumber, panel);
            add(panel);
        }

        revalidate();
        repaint();
    }

    private void initializeLayout(int size) {
        int rows = (int) Math.ceil(size / (double) GRID_COLUMNS);
        setLayout(new GridLayout(rows, GRID_COLUMNS, 10, 10));
    }
}