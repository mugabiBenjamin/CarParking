package view;

import model.ParkingSlot;

import javax.swing.*;
import java.awt.*;

public class ParkingSlotPanel extends JPanel {
    private final JButton button;
    private final int slotNumber;
    private static final Color EMPTY_SLOT_COLOR = new Color(144, 238, 144); // Light Green
    private static final Color OCCUPIED_SLOT_COLOR = new Color(255, 182, 193); // Light Red
    private static final Color TEXT_COLOR = Color.BLACK;

    public ParkingSlotPanel(ParkingSlot slot) {
        this.slotNumber = slot.getNumber();
        this.setLayout(new BorderLayout());

        button = new JButton(slot.isOccupied() ? slot.getCar().toString() : "Empty");
        button.setBackground(slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setToolTipText("Slot " + slotNumber);
        button.setFocusable(false);
        button.setFocusPainted(false);
        button.setOpaque(true); // Ensure background color is visible

        button.setPreferredSize(new Dimension(180, 50)); // Increased size for padding
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15))); // Increased padding
        button.setMargin(new Insets(10, 15, 10, 15)); // Added padding

        this.add(button, BorderLayout.CENTER);
        this.setOpaque(false);
    }

    public JButton getButton() {
        return button;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void update(ParkingSlot slot) {
        button.setText(slot.isOccupied() ? slot.getCar().toString() : "Empty");
        button.setBackground(slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR);
        button.setOpaque(true);
        button.repaint();
    }
}