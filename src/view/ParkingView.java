package view;

import controller.ParkingController;
import model.ParkingLot;
import model.ParkingSlot;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

// Custom rounded border class
class RoundedBorder implements Border {
    private int radius;
    private int thickness;

    RoundedBorder(int radius, int thickness) {
        this.radius = radius;
        this.thickness = thickness;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c.getBackground().darker());
        g2.setStroke(new BasicStroke(thickness));
        g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}

public class ParkingView extends JFrame {
    private ParkingController controller;
    private JTextField plateInput;
    private JPanel slotPanel;
    private ParkingLot lot;

    // Define custom colors
    private final Color EMPTY_SLOT_COLOR = new Color(144, 238, 144); // Light Green
    private final Color OCCUPIED_SLOT_COLOR = new Color(255, 182, 193); // Light Red
    private final Color TEXT_COLOR = Color.BLACK; // Black text

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

    // Custom rounded border for input fields and buttons
    private Border createRoundedBorder() {
        return BorderFactory.createCompoundBorder(
                new RoundedBorder(8, 1), // 8 pixel radius, 1px thickness
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding inside components
        );
    }

    // Custom rounded border for hover state
    private Border createHoverBorder() {
        return BorderFactory.createCompoundBorder(
                new RoundedBorder(8, 2), // 8 pixel radius, 2px thickness for hover
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Same padding
        );
    }

    private void initUI() {
        // Top panel for parking cars
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Top and bottom padding

        // Create and style plate input field
        plateInput = new JTextField(15); // Increased from 10 to 15 for wider input
        plateInput.setBorder(createRoundedBorder());
        plateInput.setPreferredSize(new Dimension(200, 30)); // Set explicit width

        // Create and style park button
        JButton parkBtn = new JButton("Park");
        parkBtn.setBorder(createRoundedBorder());
        parkBtn.setFocusPainted(false); // Prevent focus rectangle

        topPanel.add(new JLabel("Plate Number:"));
        topPanel.add(plateInput);
        topPanel.add(parkBtn);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());

        // Create and style search input field
        JTextField searchInput = new JTextField(15); // Increased from 10 to 15 for wider input
        searchInput.setBorder(createRoundedBorder());
        searchInput.setPreferredSize(new Dimension(200, 30)); // Set explicit width

        // Create and style search button
        JButton searchBtn = new JButton("Search");
        searchBtn.setBorder(createRoundedBorder());
        searchBtn.setFocusPainted(false); // Prevent focus rectangle

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

        // Status bar with padding and centered text
        JLabel statusBar = new JLabel("Ready", SwingConstants.CENTER);
        statusBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Top and bottom padding
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
                        "<br><br><small>(Click to remove)</small></center></html>");
                btn.setBackground(OCCUPIED_SLOT_COLOR);
            } else {
                btn.setText("Empty");
                btn.setBackground(EMPTY_SLOT_COLOR);
            }

            btn.setForeground(TEXT_COLOR);
            btn.setToolTipText("Slot " + slot.getNumber());

            // Apply rounded border to slot buttons
            btn.setBorder(createRoundedBorder());
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(true);

            // Add hover effect
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    Color baseColor = slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR;
                    btn.setBorder(createHoverBorder());
                    btn.setBackground(baseColor.darker()); // Slightly darker color
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBorder(createRoundedBorder());
                    btn.setBackground(slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR);
                }
            });

            int slotNumber = slot.getNumber();
            btn.addActionListener(e -> {
                if (slot.isOccupied()) {
                    int confirm = JOptionPane.showConfirmDialog(ParkingView.this,
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
                btn.setForeground(Color.WHITE); // White text for better contrast on blue
                Timer timer = new Timer(2000, e -> updateSlots()); // Reset after 2 seconds
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
}