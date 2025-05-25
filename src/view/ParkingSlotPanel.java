package view;

import controller.ParkingController;
import model.ParkingSlot;
import util.IconUtil;
import util.MessageBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ParkingSlotPanel extends JPanel {
    private final ParkingSlot slot;
    private final ParkingController controller;
    private final JLabel statusBar;
    private JLabel slotLabel;
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
        slotLabel = new JLabel("", SwingConstants.CENTER);
        slotLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        slotLabel.setBorder(BorderFactory.createEmptyBorder());

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
        add(slotLabel, BorderLayout.CENTER);
    }

    public void updateSlot() {
        if (slot.isOccupied()) {
            setBackground(OCCUPIED_COLOR);
            slotLabel.setIcon(IconUtil.createCarIcon(20, 12));
            slotLabel.setText(slot.getCar().getPlateNumber());
            unparkButton.setEnabled(true);
            selectCheckBox.setEnabled(true);
        } else {
            setBackground(EMPTY_COLOR);
            slotLabel.setIcon(IconUtil.createCheckIcon(20, 12, "slot"));
            slotLabel.setText("Slot " + slot.getNumber());
            unparkButton.setEnabled(false);
            selectCheckBox.setEnabled(false);
            selectCheckBox.setSelected(false);
        }
        revalidate();
        repaint();
    }

    private void handleUnparkAction() {
        if (slot.isOccupied()) {
            String licensePlate = slot.getCar().getPlateNumber();
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
            } else {
                statusBar.setText("Unpark cancelled for car " + licensePlate);
            }
        }
    }

    public boolean isSelected() {
        return selectCheckBox.isSelected() && slot.isOccupied();
    }

    public void clearSelection() {
        selectCheckBox.setSelected(false);
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    // For testing
    public JLabel getSlotLabel() {
        return slotLabel;
    }

    public JButton getUnparkButton() {
        return unparkButton;
    }

    public JCheckBox getSelectCheckBox() {
        return selectCheckBox;
    }
}