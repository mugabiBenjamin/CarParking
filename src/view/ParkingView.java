package view;

import controller.ParkingController;
import model.ParkingLot;

import javax.swing.*;
import java.awt.*;

public class ParkingView extends JFrame {
    private ParkingController controller;
    private JTextField plateInput;
    private JPanel slotPanel;

    public ParkingView() {
        setTitle("Car Parking System");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        var lot = new ParkingLot(10); // 10 slots
        this.controller = new ParkingController(lot);

        initUI(lot);
        setVisible(true);
    }

    private void initUI(ParkingLot lot) {
        JPanel topPanel = new JPanel();
        plateInput = new JTextField(10);
        JButton parkBtn = new JButton("Park");
        topPanel.add(new JLabel("Plate Number:"));
        topPanel.add(plateInput);
        topPanel.add(parkBtn);

        add(topPanel, BorderLayout.NORTH);

        slotPanel = new JPanel(new GridLayout(2, 5));
        updateSlots(lot);
        add(slotPanel, BorderLayout.CENTER);

        parkBtn.addActionListener(e -> {
            controller.parkCar(plateInput.getText());
            updateSlots(lot);
            plateInput.setText("");
        });
    }

    private void updateSlots(ParkingLot lot) {
        slotPanel.removeAll();
        for (var slot : lot.getSlots()) {
            JButton btn = new JButton(slot.isOccupied() ? slot.getCar().toString() : "Empty");
            btn.setBackground(slot.isOccupied() ? Color.RED : Color.GREEN);
            btn.setToolTipText("Slot " + slot.getNumber());

            int slotNumber = slot.getNumber();
            btn.addActionListener(e -> {
                if (slot.isOccupied()) {
                    int confirm = JOptionPane.showConfirmDialog(this, "Remove car from Slot " + slotNumber + "?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        controller.unparkCar(slotNumber);
                        updateSlots(lot);
                    }
                }
            });

            slotPanel.add(btn);
        }
        slotPanel.revalidate();
        slotPanel.repaint();
    }
}
