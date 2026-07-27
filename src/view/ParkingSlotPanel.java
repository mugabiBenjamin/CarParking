package view;

import controller.ParkingController;
import domain.entities.ParkingSlot;
import util.IconUtil;
import util.Logger;
import util.MessageBox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public final class ParkingSlotPanel extends JPanel {
    private static final int ICON_SIZE = 32;
    private static final int UNPARK_ICON_SIZE = 16;
    private static final int SLOT_WIDTH = 110;
    private static final int SLOT_HEIGHT = 90;

    private static final Color EMPTY_SLOT_COLOR = new Color(144, 238, 144);
    private static final Color OCCUPIED_SLOT_COLOR = new Color(255, 182, 193);
    private static final Color INVALID_SLOT_COLOR = Color.GRAY;

    private final ParkingController controller;
    private final JLabel statusBar;

    private ParkingSlot slot;
    private JLabel iconLabel;
    private JLabel textLabel;
    private JCheckBox selectCheckBox;
    private JButton unparkButton;

    public ParkingSlotPanel(ParkingSlot slot, ParkingController controller, JLabel statusBar) {
        if (slot == null) {
            throw new IllegalArgumentException("Parking slot cannot be null");
        }

        if (controller == null) {
            throw new IllegalArgumentException("Parking controller cannot be null");
        }

        if (statusBar == null) {
            throw new IllegalArgumentException("Status bar cannot be null");
        }

        this.slot = slot;
        this.controller = controller;
        this.statusBar = statusBar;

        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(SLOT_WIDTH, SLOT_HEIGHT));
        setBorder(new RoundedBorder(15, 1));
        setOpaque(true);

        initComponents();
        updateSlot();
    }

    private void initComponents() {
        iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        textLabel = new JLabel("", SwingConstants.CENTER);
        textLabel.setForeground(Color.BLACK);

        selectCheckBox = new JCheckBox();
        selectCheckBox.setOpaque(false);
        selectCheckBox.setToolTipText("Select this occupied slot for batch unpark");
        selectCheckBox.addActionListener(event -> updateSelectionStatus());

        unparkButton = new JButton(IconUtil.createUnparkIcon(UNPARK_ICON_SIZE, UNPARK_ICON_SIZE));
        unparkButton.setToolTipText("Unpark car");
        unparkButton.setContentAreaFilled(false);
        unparkButton.setBorderPainted(false);
        unparkButton.setFocusPainted(false);
        unparkButton.addActionListener(event -> handleUnparkAction());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(selectCheckBox, BorderLayout.WEST);
        topPanel.add(unparkButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(iconLabel, BorderLayout.CENTER);
        add(textLabel, BorderLayout.SOUTH);
    }

    public void update(ParkingSlot updatedSlot) {
        if (updatedSlot == null) {
            setInvalidState();
            return;
        }

        this.slot = updatedSlot;
        updateSlot();
    }

    private void updateSlot() {
        if (slot == null) {
            setInvalidState();
            return;
        }

        if (slot.isOccupied()) {
            setOccupiedState();
        } else {
            setEmptyState();
        }

        revalidate();
        repaint();
    }

    private void setEmptyState() {
        setBackground(EMPTY_SLOT_COLOR);
        iconLabel.setIcon(IconUtil.createCheckIcon(ICON_SIZE, ICON_SIZE, "slot"));
        textLabel.setText("Slot " + slot.getNumber() + ": Empty");
        selectCheckBox.setSelected(false);
        selectCheckBox.setEnabled(false);
        selectCheckBox.setVisible(false);
        unparkButton.setVisible(false);
        setToolTipText("Slot " + slot.getNumber() + " is available");
    }

    private void setOccupiedState() {
        setBackground(OCCUPIED_SLOT_COLOR);
        iconLabel.setIcon(IconUtil.createCarIcon(ICON_SIZE, ICON_SIZE));
        textLabel.setText(slot.getCar().getLicensePlate());
        selectCheckBox.setEnabled(true);
        selectCheckBox.setVisible(true);
        unparkButton.setVisible(true);
        setToolTipText("Slot " + slot.getNumber() + " occupied by " + slot.getCar().getLicensePlate());
    }

    private void setInvalidState() {
        setBackground(INVALID_SLOT_COLOR);
        iconLabel.setIcon(null);
        textLabel.setText("Invalid slot");
        selectCheckBox.setSelected(false);
        selectCheckBox.setEnabled(false);
        selectCheckBox.setVisible(false);
        unparkButton.setVisible(false);
        setToolTipText("Invalid parking slot");
    }

    private void handleUnparkAction() {
        try {
            if (slot == null) {
                statusBar.setText("Unpark failed: Invalid slot");
                return;
            }

            if (!slot.isOccupied()) {
                statusBar.setText("Unpark failed: Slot already empty");
                MessageBox.showError(
                        "Slot " + slot.getNumber() + " is already empty.",
                        "Choose an occupied slot and try again."
                );
                return;
            }

            String licensePlate = slot.getCar().getLicensePlate();

            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to unpark car " + licensePlate + " from slot " + slot.getNumber()
                            + "? This action is irreversible.",
                    "Confirm Unparking",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmation != JOptionPane.YES_OPTION) {
                statusBar.setText("Unpark cancelled for car " + licensePlate);
                return;
            }

            controller.unparkCar(slot.getNumber());
            selectCheckBox.setSelected(false);
            statusBar.setText("Unparked car from slot " + slot.getNumber());
        } catch (Exception exception) {
            Logger.error("Failed to unpark slot " + getSlotNumber(), exception);
            statusBar.setText("Unpark failed: Unexpected error");

            MessageBox.showError(
                    "Unable to unpark the selected car.",
                    "Refresh the parking slots and try again.",
                    "Restart the application if the issue continues."
            );
        }
    }

    private void updateSelectionStatus() {
        if (slot == null || !slot.isOccupied()) {
            selectCheckBox.setSelected(false);
            statusBar.setText("Only occupied slots can be selected");
            return;
        }

        if (selectCheckBox.isSelected()) {
            statusBar.setText("Slot " + slot.getNumber() + " selected");
        } else {
            statusBar.setText("Slot " + slot.getNumber() + " unselected");
        }
    }

    public boolean isSelected() {
        return selectCheckBox.isSelected() && slot != null && slot.isOccupied();
    }

    public void clearSelection() {
        selectCheckBox.setSelected(false);
    }

    public void setSelected(boolean selected) {
        selectCheckBox.setSelected(selected && slot != null && slot.isOccupied());
    }

    public int getSlotNumber() {
        if (slot == null) {
            return -1;
        }

        return slot.getNumber();
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    public JLabel getSlotLabel() {
        return textLabel;
    }

    public JButton getUnparkButton() {
        return unparkButton;
    }

    public JCheckBox getSelectCheckBox() {
        return selectCheckBox;
    }
}