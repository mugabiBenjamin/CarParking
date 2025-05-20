package view;

import model.ParkingSlot;

import javax.swing.*;
import java.awt.*;

public class ParkingSlotPanel extends JPanel {
    private JButton button;
    private int slotNumber;

    public ParkingSlotPanel(ParkingSlot slot) {
        this.slotNumber = slot.getNumber();
        this.setLayout(new BorderLayout());

        button = new JButton(slot.isOccupied() ? slot.getCar().getPlateNumber() : "Empty");
        button.setBackground(slot.isOccupied() ? Color.RED : Color.GREEN);
        button.setForeground(Color.WHITE);
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
        button.setBackground(slot.isOccupied() ? Color.RED : Color.GREEN);
    }
}
