package view;

import controller.ParkingController;
import model.ParkingLot;
import model.ParkingSlot;

import javax.swing.*;
import java.awt.*;
// import java.util.Optional;

public class ParkingView extends JFrame {
    private ParkingController controller;
    private JTextField plateInput;
    private JPanel slotPanel;
    private ParkingLot lot;

    public ParkingView() {
        setTitle("Car Parking System");
        setSize(600, 500); // Increased height to accommodate search panel
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        this.lot = new ParkingLot(10); // 10 slots
        this.controller = new ParkingController(lot);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        // Top panel for parking cars
        JPanel topPanel = new JPanel(new FlowLayout());
        plateInput = new JTextField(10);
        JButton parkBtn = new JButton("Park");
        topPanel.add(new JLabel("Plate Number:"));
        topPanel.add(plateInput);
        topPanel.add(parkBtn);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchInput = new JTextField(10);
        JButton searchBtn = new JButton("Search");
        searchPanel.add(new JLabel("Search Plate:"));
        searchPanel.add(searchInput);
        searchPanel.add(searchBtn);

        // Combine top panels
        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        controlPanel.add(topPanel);
        controlPanel.add(searchPanel);
        add(controlPanel, BorderLayout.NORTH);

        // Parking slots panel
        slotPanel = new JPanel(new GridLayout(2, 5, 5, 5)); // Added gaps between slots
        updateSlots();
        add(new JScrollPane(slotPanel), BorderLayout.CENTER);

        // Status bar
        JLabel statusBar = new JLabel(" Ready");
        add(statusBar, BorderLayout.SOUTH);

        // Action listeners
        parkBtn.addActionListener(e -> {
            controller.parkCar(plateInput.getText());
            updateSlots();
            plateInput.setText("");
        });

        searchBtn.addActionListener(e -> {
            String searchPlate = searchInput.getText().trim();
            if (searchPlate.isEmpty()) {
                MessageBox.showError("Please enter a plate number to search");
                return;
            }

            boolean found = false;
            int foundSlot = -1;

            // Search for the car
            for (ParkingSlot slot : lot.getSlots()) {
                if (slot.isOccupied() && slot.getCar().getPlateNumber().equalsIgnoreCase(searchPlate)) {
                    found = true;
                    foundSlot = slot.getNumber();
                    break;
                }
            }

            if (found) {
                statusBar.setText(" Found car with plate " + searchPlate + " in slot " + foundSlot);
                highlightSlot(foundSlot);
                MessageBox.showInfo("Car found in slot " + foundSlot);
            } else {
                statusBar.setText(" Car with plate " + searchPlate + " not found");
                MessageBox.showInfo("No car with plate " + searchPlate + " is currently parked");
            }

            searchInput.setText("");
        });
    }

    private void updateSlots() {
        slotPanel.removeAll();
        for (var slot : lot.getSlots()) {
            JButton btn = new JButton();

            if (slot.isOccupied()) {
                btn.setText("<html><center>" + slot.getCar().toString() +
                        "<br><small>(Click to remove)</small></center></html>");
                btn.setBackground(Color.RED);
            } else {
                btn.setText("Empty");
                btn.setBackground(Color.GREEN);
            }

            btn.setForeground(Color.WHITE);
            btn.setToolTipText("Slot " + slot.getNumber());

            int slotNumber = slot.getNumber();
            btn.addActionListener(e -> {
                if (slot.isOccupied()) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Remove car " + slot.getCar().getPlateNumber() + " from Slot " + slotNumber + "?",
                            "Confirm Removal", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        controller.unparkCar(slotNumber);
                        updateSlots();
                    }
                }
            });

            slotPanel.add(btn);
        }
        slotPanel.revalidate();
        slotPanel.repaint();
    }

    private void highlightSlot(int slotNumber) {
        // Reset all buttons to normal state
        updateSlots();

        // Highlight the found slot
        if (slotNumber > 0 && slotNumber <= lot.getSlots().size()) {
            Component comp = slotPanel.getComponent(slotNumber - 1);
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(Color.BLUE); // Highlight color
                Timer timer = new Timer(2000, e -> updateSlots()); // Reset after 2 seconds
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
}