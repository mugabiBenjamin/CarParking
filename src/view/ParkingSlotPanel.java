package view;

import model.ParkingSlot;

import javax.swing.*;
import java.awt.*;

public class ParkingSlotPanel extends JPanel {
    private JButton button;
    private int slotNumber;

    // Define custom colors
    private static final Color EMPTY_SLOT_COLOR = new Color(144, 238, 144); // Light Green
    private static final Color OCCUPIED_SLOT_COLOR = new Color(255, 182, 193); // Light Red
    private static final Color TEXT_COLOR = Color.BLACK; // Black text

    public ParkingSlotPanel(ParkingSlot slot) {
        this.slotNumber = slot.getNumber();
        this.setLayout(new BorderLayout());

        button = new JButton(slot.isOccupied() ? slot.getCar().getPlateNumber() : "Empty");
        button.setBackground(slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setToolTipText("Slot " + slotNumber);
        button.setFocusable(false);

        this.add(button, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public JButton getButton() {
        return button;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void update(ParkingSlot slot) {
        button.setText(slot.isOccupied() ? slot.getCar().getPlateNumber() : "Empty");
        button.setBackground(slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR);
    }
}