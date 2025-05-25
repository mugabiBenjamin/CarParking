package view;

import controller.ParkingController;
import model.ParkingLot;
import model.ParkingSlot;
import util.IconUtil;
import util.MessageBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SlotPanel extends JPanel {
    private final ParkingController controller;
    private final ParkingLot lot;
    private final JLabel statusBar;
    private volatile boolean isDialogOpen = false;
    private static final Color EMPTY_SLOT_COLOR = new Color(144, 238, 144); // Light Green
    private static final Color OCCUPIED_SLOT_COLOR = new Color(255, 182, 193); // Light Red
    private static final Color TEXT_COLOR = Color.WHITE;

    public SlotPanel(ParkingController controller, ParkingLot lot, JLabel statusBar) {
        this.controller = controller;
        this.lot = lot;
        this.statusBar = statusBar;
        setLayout(new GridLayout(2, 5, 10, 10)); // Increased spacing
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Added padding
        updateSlots();
    }

    public void updateSlots() {
        removeAll();
        ImageIcon carIcon = IconUtil.createCarIcon(32, 32);
        ImageIcon checkIcon = IconUtil.createCheckIcon(32, 32, "slot");

        for (ParkingSlot slot : lot.getSlots()) {
            JButton btn = createSlotButton(slot, carIcon, checkIcon);
            add(btn);
        }
        revalidate();
        repaint();
    }

    private JButton createSlotButton(ParkingSlot slot, ImageIcon carIcon, ImageIcon checkIcon) {
        JButton btn = new JButton();
        configureSlotButtonAppearance(btn, slot, carIcon, checkIcon);
        if (slot.isOccupied()) {
            setupSlotButtonListeners(btn, slot);
        }
        return btn;
    }

    private void configureSlotButtonAppearance(JButton btn, ParkingSlot slot, ImageIcon carIcon, ImageIcon checkIcon) {
        if (slot.isOccupied()) {
            btn.setText("<html><center>" + slot.getCar().toString() +
                    "<br><br><small>(Click to remove)</small></center></html>");
            btn.setIcon(carIcon);
            btn.setBackground(OCCUPIED_SLOT_COLOR);
            btn.setForeground(Color.BLACK);
        } else {
            btn.setText("Empty");
            btn.setIcon(checkIcon);
            btn.setBackground(EMPTY_SLOT_COLOR);
            btn.setForeground(TEXT_COLOR);
            btn.setEnabled(false);
        }
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setToolTipText("Slot " + slot.getNumber() + ": " +
                (slot.isOccupied() ? "Occupied (light red), click to remove (car icon)"
                        : "Empty (light green), not clickable (check icon)"));
        btn.setBorder(new RoundedBorder(8, 1));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setMargin(new Insets(10, 15, 10, 15)); // Added padding
        btn.setOpaque(true); // Ensure background color is visible
    }

    private void setupSlotButtonListeners(JButton btn, ParkingSlot slot) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBorder(new RoundedBorder(8, 2));
                btn.setBackground(OCCUPIED_SLOT_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBorder(new RoundedBorder(8, 1));
                btn.setBackground(OCCUPIED_SLOT_COLOR);
            }
        });

        int slotNumber = slot.getNumber();
        btn.addActionListener(e -> handleUnparkAction(slot, slotNumber));
    }

    private void handleUnparkAction(ParkingSlot slot, int slotNumber) {
        if (!slot.isOccupied() || isDialogOpen) {
            System.out.println("Unpark skipped: slot occupied=" + slot.isOccupied() + ", isDialogOpen=" + isDialogOpen);
            return;
        }
        try {
            isDialogOpen = true;
            String plateNumber = slot.getCar().getPlateNumber();
            System.out.println("Showing unpark dialog for slot " + slotNumber + ", plate: " + plateNumber);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "<html><h3>Confirm Unpark</h3>" +
                            "<p><b>Car Details:</b></p>" +
                            "<ul>" +
                            "<li><b>License Plate:</b> " + plateNumber + "</li>" +
                            "<li><b>Slot Number:</b> " + slotNumber + "</li>" +
                            "<li><b>Additional Details:</b> No additional details available</li>" +
                            "</ul>" +
                            "<p><font color='red'><b>Warning:</b> This action is irreversible. The car will be removed from the parking system.</font></p>"
                            +
                            "<p>Do you want to proceed?</p>" +
                            "</html>",
                    "Unpark Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                System.out.println("Unparking car from slot " + slotNumber);
                controller.unparkCar(slotNumber);
            }
        } catch (Exception ex) {
            System.err.println("Error during unpark: " + ex.getMessage());
            MessageBox.showError("Failed to unpark car from slot " + slotNumber + ": " + ex.getMessage(),
                    "Ensure the slot is still occupied and try again.",
                    "If the issue persists, contact system support with error details.");
            statusBar.setText("Unpark failed for slot " + slotNumber);
            clearStatusBar();
        } finally {
            isDialogOpen = false;
            System.out.println("isDialogOpen reset to false");
        }
    }

    public void highlightSlot(int slotNumber) {
        if (slotNumber > 0 && slotNumber <= lot.getSlots().size()) {
            Component comp = getComponent(slotNumber - 1);
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                ParkingSlot slot = lot.getSlots().get(slotNumber - 1);
                btn.setBackground(Color.BLUE);
                btn.setForeground(Color.WHITE);
                btn.setOpaque(true);
                btn.repaint();
                Timer timer = new Timer(2000, e -> {
                    btn.setBackground(slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR);
                    btn.setForeground(slot.isOccupied() ? Color.BLACK : TEXT_COLOR);
                    btn.setOpaque(true);
                    btn.repaint();
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private void clearStatusBar() {
        Timer statusBarTimer = new Timer(5000, e -> statusBar.setText("Ready"));
        statusBarTimer.setRepeats(false);
        statusBarTimer.start();
    }

    // For testing
    public JButton getSlotButton(int slotNumber) {
        if (slotNumber > 0 && slotNumber <= lot.getSlots().size()) {
            Component comp = getComponent(slotNumber - 1);
            if (comp instanceof JButton) {
                return (JButton) comp;
            }
        }
        return null;
    }
}