package view;

import controller.ParkingController;
import model.ParkingSlot;
import util.IconUtil;
import util.Logger;
import util.MessageBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ParkingSlotPanel extends JPanel {
    private final ParkingSlot slot;
    private final ParkingController controller;
    private final JLabel statusBar;
    private JPanel slotContentPanel;
    private JLabel iconLabel;
    private JLabel textLabel;
    private JButton unparkButton;
    private JCheckBox selectCheckBox;
    private static final Color OCCUPIED_COLOR = new Color(255, 204, 204); // Light red
    private static final Color EMPTY_COLOR = new Color(204, 255, 204); // Light green

    public ParkingSlotPanel(ParkingSlot slot, ParkingController controller, JLabel statusBar) {
        this.slot = slot;
        this.controller = controller;
        this.statusBar = statusBar;
        setLayout(new BorderLayout(5, 5));
        setBorder(new RoundedBorder(8, 1)); // Rounded border
        initComponents();
        updateSlot();
    }

    private void initComponents() {
        // Content panel for icon and text
        slotContentPanel = new JPanel();
        slotContentPanel.setLayout(new BoxLayout(slotContentPanel, BoxLayout.Y_AXIS));
        slotContentPanel.setOpaque(false);
        Logger.log("Slot " + slot.getNumber() + ": Initialized slotContentPanel with BoxLayout.Y_AXIS");

        // Icon label
        iconLabel = new JLabel();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        // Text label
        textLabel = new JLabel("", SwingConstants.CENTER);
        textLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        // Add components with vertical spacing
        slotContentPanel.add(Box.createVerticalGlue());
        slotContentPanel.add(iconLabel);
        slotContentPanel.add(Box.createVerticalStrut(4)); // Spacing between icon and text
        slotContentPanel.add(textLabel);
        slotContentPanel.add(Box.createVerticalGlue());

        unparkButton = new JButton(IconUtil.createUnparkIcon(16, 16));
        unparkButton.setFocusPainted(false);
        unparkButton.setContentAreaFilled(false);
        unparkButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        unparkButton.setToolTipText("Unpark car from this slot");
        unparkButton.addActionListener(e -> handleUnparkAction());
        unparkButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (unparkButton.isEnabled()) {
                    unparkButton.setBackground(unparkButton.getBackground().darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                unparkButton.setBackground(UIManager.getColor("Panel.background"));
            }
        });

        selectCheckBox = new JCheckBox();
        selectCheckBox.setToolTipText("Select slot for batch unpark");
        selectCheckBox.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setOpaque(false);
        topPanel.add(selectCheckBox, BorderLayout.WEST);
        topPanel.add(unparkButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(slotContentPanel, BorderLayout.CENTER);
    }

    public void updateSlot() {
        if (slot.isOccupied()) {
            setBackground(OCCUPIED_COLOR);
            Icon carIcon = IconUtil.createCarIcon(16, 16);
            iconLabel.setIcon(carIcon);
            textLabel.setText(slot.getCar().getPlateNumber());
            unparkButton.setEnabled(true);
            selectCheckBox.setEnabled(true);
            Logger.log("Slot " + slot.getNumber() + ": Set car.png (16x16), Plate: " + slot.getCar().getPlateNumber());
        } else {
            setBackground(EMPTY_COLOR);
            Icon checkIcon = IconUtil.createCheckIcon(16, 16, "slot");
            iconLabel.setIcon(checkIcon);
            textLabel.setText("Slot " + slot.getNumber());
            unparkButton.setEnabled(false);
            selectCheckBox.setEnabled(false);
            selectCheckBox.setSelected(false);
            Logger.log("Slot " + slot.getNumber() + ": Set check.png (16x16), Text: Slot " + slot.getNumber());
        }
        if (iconLabel.getIcon() != null) {
            Logger.log("Slot " + slot.getNumber() + ": Icon size - Width: " + iconLabel.getIcon().getIconWidth()
                    + ", Height: " + iconLabel.getIcon().getIconHeight());
        }
        revalidate();
        repaint();
    }

    private void handleUnparkAction() {
        if (slot.isOccupied()) {
            String licensePlate = slot.getCar().getPlateNumber();
            Logger.log("Unpark dialog shown for plate: " + licensePlate);
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to unpark car " + licensePlate
                            + " from this spot? This action is irreversible.",
                    "Confirm Unparking",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.unparkCar(slot.getNumber());
                updateSlot();
                String message = "Car " + licensePlate + " unparked from slot " + slot.getNumber();
                MessageBox.showInfo(message);
                statusBar.setText(message);
                Logger.log("Unparked slot " + slot.getNumber());
            } else {
                statusBar.setText("Unpark cancelled for car " + licensePlate);
            }
        }
    }

    public boolean isSelected() {
        boolean selected = selectCheckBox.isSelected() && slot.isOccupied();
        Logger.log("Slot " + slot.getNumber() + " selected: " + selected);
        return selected;
    }

    public void clearSelection() {
        selectCheckBox.setSelected(false);
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    // For testing
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