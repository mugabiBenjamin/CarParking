package presentation.swing.components;

import application.dto.SlotViewData;
import domain.enums.SlotStatus;
import presentation.swing.ParkingViewController;
import presentation.swing.resources.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public final class ParkingSlotPanel extends JPanel {
    private static final Color EMPTY_COLOR = new Color(144, 238, 144);
    private static final Color OCCUPIED_COLOR = new Color(255, 182, 193);
    private static final Color HIGHLIGHT_COLOR = new Color(100, 149, 237);

    private static final int ICON_SIZE = 16;

    private final ParkingViewController controller;
    private final IconUtil iconUtil;
    private final int slotNumber;

    private JLabel slotLabel;
    private JCheckBox selectCheckBox;
    private JButton unparkButton;
    private SlotViewData slotData;
    private boolean highlighted;

    public ParkingSlotPanel(ParkingViewController controller, IconUtil iconUtil, SlotViewData slotData) {
        if (controller == null) {
            throw new IllegalArgumentException("Parking view controller cannot be null");
        }

        if (iconUtil == null) {
            throw new IllegalArgumentException("Icon utility cannot be null");
        }

        if (slotData == null) {
            throw new IllegalArgumentException("Slot data cannot be null");
        }

        this.controller = controller;
        this.iconUtil = iconUtil;
        this.slotData = slotData;
        this.slotNumber = slotData.getSlotNumber();

        initializeLayout();
        initializeComponents();
        applySlotState(false);
    }

    private void initializeLayout() {
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(120, 95));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    private void initializeComponents() {
        slotLabel = new JLabel("", SwingConstants.CENTER);
        slotLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        selectCheckBox = new JCheckBox();
        selectCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        selectCheckBox.setToolTipText("Select this occupied slot for batch unpark");

        unparkButton = new JButton("Unpark", iconUtil.createUnparkIcon(ICON_SIZE, ICON_SIZE));
        unparkButton.setFocusPainted(false);
        unparkButton.setToolTipText("Unpark this car");
        unparkButton.addActionListener(event -> confirmAndUnpark());

        add(slotLabel, BorderLayout.CENTER);
        add(selectCheckBox, BorderLayout.WEST);
        add(unparkButton, BorderLayout.SOUTH);
    }

    public void updateSlot(SlotViewData slotData) {
        if (slotData == null) {
            return;
        }

        this.slotData = slotData;
        applySlotState(highlighted);
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        applySlotState(highlighted);
    }

    public boolean isSelectedForBatch() {
        return selectCheckBox != null && selectCheckBox.isSelected() && isOccupied();
    }

    public void clearSelection() {
        if (selectCheckBox != null) {
            selectCheckBox.setSelected(false);
        }
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    private boolean isOccupied() {
        return slotData.getStatus() == SlotStatus.OCCUPIED;
    }

    private void applySlotState(boolean highlighted) {
        boolean occupied = isOccupied();

        if (highlighted) {
            setBackground(HIGHLIGHT_COLOR);
        } else {
            setBackground(occupied ? OCCUPIED_COLOR : EMPTY_COLOR);
        }

        String labelText = occupied
                ? "<html><center>Slot " + slotNumber + "<br>" + escapeHtml(slotData.getLicensePlate()) + "</center></html>"
                : "<html><center>Slot " + slotNumber + "<br>EMPTY</center></html>";

        slotLabel.setText(labelText);
        slotLabel.setIcon(occupied ? iconUtil.createCarIcon(ICON_SIZE, ICON_SIZE) : iconUtil.createCheckIcon(ICON_SIZE, ICON_SIZE, "slot"));
        slotLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        slotLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        selectCheckBox.setEnabled(occupied);
        selectCheckBox.setVisible(occupied);

        if (!occupied) {
            selectCheckBox.setSelected(false);
        }

        unparkButton.setEnabled(occupied);
        unparkButton.setVisible(occupied);

        revalidate();
        repaint();
    }

    private void confirmAndUnpark() {
        if (!isOccupied()) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Unpark car " + slotData.getLicensePlate() + " from slot " + slotNumber + "?",
                "Confirm Unpark",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            controller.unparkCar(slotNumber);
        }
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}