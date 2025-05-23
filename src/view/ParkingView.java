package view;

import controller.ParkingController;
import model.ParkingLot;
import model.ParkingSlot;
import util.Validator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

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
    private volatile boolean isDialogOpen = false; // Prevents multiple unpark dialogs

    // Colors for slot displays, used consistently for empty and occupied slots
    private final Color EMPTY_SLOT_COLOR = new Color(144, 238, 144); // Light Green for empty slots
    private final Color OCCUPIED_SLOT_COLOR = new Color(255, 182, 193); // Light Red for occupied slots
    private final Color TEXT_COLOR = Color.BLACK; // Black text
    private final Color PLACEHOLDER_COLOR = Color.GRAY; // Color for placeholder text

    public ParkingView() {
        setTitle("Car Parking System");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set window icon (cross-platform, relative path)
        try {
            InputStream iconStream = getClass().getResourceAsStream("/resources/icons/car.png");
            if (iconStream != null) {
                BufferedImage icon = ImageIO.read(iconStream);
                setIconImage(icon);
            } else {
                System.err.println("Icon resource not found: /resources/icons/car.png");
            }
        } catch (IOException e) {
            System.err.println("Failed to load window icon: " + e.getMessage());
        }

        this.lot = new ParkingLot(10);
        this.controller = new ParkingController(lot);

        initUI();
        setVisible(true);
    }

    private Border createRoundedBorder() {
        return BorderFactory.createCompoundBorder(
                new RoundedBorder(8, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private Border createHoverBorder() {
        return BorderFactory.createCompoundBorder(
                new RoundedBorder(8, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private ImageIcon createCarIcon(int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResource("/resources/icons/car.png"));
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resizedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(originalImage, 0, 0, width, height, null);
            g2.dispose();
            return new ImageIcon(resizedImage);
        } catch (IOException e) {
            System.err.println("Failed to load PNG icon: " + e.getMessage());
            BufferedImage fallback = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = fallback.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            int scale = Math.min(width / 24, height / 16); // Base size: 24x16
            g2.fillRect(4 * scale, 4 * scale, 16 * scale, 8 * scale);
            g2.fillOval(6 * scale, 10 * scale, 4 * scale, 4 * scale);
            g2.fillOval(14 * scale, 10 * scale, 4 * scale, 4 * scale);
            g2.fillRect(8 * scale, 2 * scale, 8 * scale, 4 * scale);
            g2.dispose();
            return new ImageIcon(fallback);
        }
    }

    private ImageIcon createSearchIcon(int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResource("/resources/icons/search.png"));
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resizedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(originalImage, 0, 0, width, height, null);
            g2.dispose();
            return new ImageIcon(resizedImage);
        } catch (IOException e) {
            System.err.println("Failed to load search PNG icon: " + e.getMessage());
            BufferedImage fallback = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = fallback.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            int scale = Math.min(width / 16, height / 16); // Base size: 16x16
            g2.setStroke(new BasicStroke(2 * scale));
            g2.drawOval(4 * scale, 4 * scale, 8 * scale, 8 * scale);
            g2.drawLine(10 * scale, 10 * scale, 12 * scale, 12 * scale);
            g2.dispose();
            return new ImageIcon(fallback);
        }
    }

    private ImageIcon createHelpIcon(int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResource("/resources/icons/help.png"));
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resizedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(originalImage, 0, 0, width, height, null);
            g2.dispose();
            return new ImageIcon(resizedImage);
        } catch (IOException e) {
            System.err.println("Failed to load help PNG icon: " + e.getMessage());
            BufferedImage fallback = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = fallback.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            int scale = Math.min(width / 16, height / 16); // Base size: 16x16
            g2.setFont(new Font("SansSerif", Font.BOLD, 12 * scale));
            g2.drawString("?", 6 * scale, 12 * scale);
            g2.dispose();
            return new ImageIcon(fallback);
        }
    }

    private ImageIcon createCheckIcon(int width, int height) {
        // Amortized cost is O(1) for icon creation per slot
        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResource("/resources/icons/check.png"));
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resizedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(originalImage, 0, 0, width, height, null);
            g2.dispose();
            return new ImageIcon(resizedImage);
        } catch (IOException e) {
            System.err.println("Failed to load checkmark PNG icon: " + e.getMessage());
            BufferedImage fallback = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = fallback.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            int scale = Math.min(width / 16, height / 16); // Base size: 16x16
            g2.setStroke(new BasicStroke(2 * scale));
            g2.drawLine(4 * scale, 8 * scale, 7 * scale, 11 * scale);
            g2.drawLine(7 * scale, 11 * scale, 12 * scale, 5 * scale);
            g2.dispose();
            return new ImageIcon(fallback);
        }
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Input field for license plate with placeholder and tooltip
        plateInput = new JTextField(15);
        plateInput.setBorder(createRoundedBorder());
        plateInput.setPreferredSize(new Dimension(200, 30));
        plateInput.setText("Enter AAA 123B");
        plateInput.setForeground(PLACEHOLDER_COLOR);
        plateInput.setToolTipText("Enter license plate in format AAA 123B");
        plateInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (plateInput.getText().equals("Enter AAA 123B")) {
                    plateInput.setText("");
                    plateInput.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (plateInput.getText().isEmpty()) {
                    plateInput.setText("Enter AAA 123B");
                    plateInput.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });

        // Park button with car icon, uniform styling, and tooltip
        JButton parkBtn = new JButton("Park", createCarIcon(16, 16));
        parkBtn.setBorder(createRoundedBorder());
        parkBtn.setFocusPainted(false);
        parkBtn.setContentAreaFilled(true);
        parkBtn.setMargin(new Insets(5, 10, 5, 10));
        parkBtn.setIconTextGap(4);
        parkBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        parkBtn.setToolTipText("Park a car in an available slot");
        parkBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                parkBtn.setBorder(createHoverBorder());
                parkBtn.setBackground(parkBtn.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                parkBtn.setBorder(createRoundedBorder());
                parkBtn.setBackground(UIManager.getColor("Button.background"));
            }
        });

        // Help button with question mark icon, uniform styling, and tooltip
        JButton helpBtn = new JButton("Help", createHelpIcon(16, 16));
        helpBtn.setBorder(createRoundedBorder());
        helpBtn.setFocusPainted(false);
        helpBtn.setContentAreaFilled(true);
        helpBtn.setMargin(new Insets(5, 10, 5, 10));
        helpBtn.setIconTextGap(4);
        helpBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        helpBtn.setToolTipText("Open help guide for using the parking system");
        helpBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                helpBtn.setBorder(createHoverBorder());
                helpBtn.setBackground(helpBtn.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                helpBtn.setBorder(createRoundedBorder());
                helpBtn.setBackground(UIManager.getColor("Button.background"));
            }
        });
        helpBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(ParkingView.this,
                    "<html><h3>Car Parking System Help</h3>" +
                            "<p><b>Parking a Car:</b> Enter a license plate (e.g., AAA 123B) in the top input field and click 'Park' to assign the car to an available slot.</p>"
                            +
                            "<p><b>Searching for a Car:</b> Enter a license plate in the search field and click 'Search'. If found, the slot highlights blue for 2 seconds.</p>"
                            +
                            "<p><b>Slot Status:</b><br>" +
                            "- <font color='green'>Green</font>: Empty slot (checkmark icon, not clickable).<br>" +
                            "- <font color='red'>Red</font>: Occupied slot (car icon, click to remove).<br>" +
                            "- <font color='blue'>Blue</font>: Highlighted slot (after search).</p>" +
                            "<p><b>Removing a Car:</b> Click an occupied (red) slot and confirm to unpark the car.</p>"
                            +
                            "<p><b>Input Format:</b> Use AAA 123B (3 letters, space, 3 digits, letter). Placeholder text guides you.</p>"
                            +
                            "<p><b>Status Bar:</b> Shows parking and search status at the bottom.</p>" +
                            "</html>",
                    "Help Guide",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        topPanel.add(new JLabel("License Plate:"));
        topPanel.add(plateInput);
        topPanel.add(parkBtn);
        topPanel.add(helpBtn);

        JPanel searchPanel = new JPanel(new FlowLayout());
        // Search input field for license plate with placeholder and tooltip
        JTextField searchInput = new JTextField(15);
        searchInput.setBorder(createRoundedBorder());
        searchInput.setPreferredSize(new Dimension(200, 30));
        searchInput.setText("Enter AAA 123B");
        searchInput.setForeground(PLACEHOLDER_COLOR);
        searchInput.setToolTipText("Enter license plate to find a parked car");
        searchInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchInput.getText().equals("Enter AAA 123B")) {
                    searchInput.setText("");
                    searchInput.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchInput.getText().isEmpty()) {
                    searchInput.setText("Enter AAA 123B");
                    searchInput.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });

        // Search button with magnifying glass icon, uniform styling, and tooltip
        JButton searchBtn = new JButton("Search", createSearchIcon(16, 16));
        searchBtn.setBorder(createRoundedBorder());
        searchBtn.setFocusPainted(false);
        searchBtn.setContentAreaFilled(true);
        searchBtn.setMargin(new Insets(5, 10, 5, 10));
        searchBtn.setIconTextGap(4);
        searchBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        searchBtn.setToolTipText("Search for a car; found slot highlights blue for 2 seconds");
        searchBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                searchBtn.setBorder(createHoverBorder());
                searchBtn.setBackground(searchBtn.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                searchBtn.setBorder(createRoundedBorder());
                searchBtn.setBackground(UIManager.getColor("Button.background"));
            }
        });

        searchPanel.add(new JLabel("Search License Plate:"));
        searchPanel.add(searchInput);
        searchPanel.add(searchBtn);

        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        controlPanel.add(topPanel);
        controlPanel.add(searchPanel);
        add(controlPanel, BorderLayout.NORTH);

        slotPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        updateSlots();
        add(new JScrollPane(slotPanel), BorderLayout.CENTER);

        // Status bar with tooltip
        JLabel statusBar = new JLabel("Ready", SwingConstants.CENTER);
        statusBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusBar.setToolTipText("Shows parking and search status");
        add(statusBar, BorderLayout.SOUTH);

        parkBtn.addActionListener(e -> {
            // Clear placeholder text before processing
            String plateText = plateInput.getText().equals("Enter AAA 123B") ? "" : plateInput.getText();
            controller.parkCar(plateText);
            updateSlots();
            plateInput.setText("Enter AAA 123B");
            plateInput.setForeground(PLACEHOLDER_COLOR);
        });

        // Search action with standardized, actionable error messages
        searchBtn.addActionListener(e -> {
            // Clear placeholder text before processing
            String searchPlate = searchInput.getText().equals("Enter AAA 123B") ? "" : searchInput.getText().trim();
            if (searchPlate.isEmpty()) {
                MessageBox.showError("Search failed for license plate: Enter a valid license plate (e.g., AAA 123B).");
                return;
            }

            if (!Validator.isValidPlate(searchPlate)) {
                MessageBox.showError(
                        "Search failed for license plate " + searchPlate + ": Invalid format, use AAA 123B.");
                return;
            }

            boolean found = false;
            int foundSlot = -1;

            for (ParkingSlot slot : lot.getSlots()) {
                if (slot.isOccupied() && slot.getCar().getPlateNumber().equalsIgnoreCase(searchPlate)) {
                    found = true;
                    foundSlot = slot.getNumber();
                    break;
                }
            }

            if (found) {
                statusBar.setText("Found car with license plate " + searchPlate + " in slot " + foundSlot);
                highlightSlot(foundSlot);
                MessageBox.showInfo("Car with license plate " + searchPlate + " found in slot " + foundSlot);
            } else {
                statusBar.setText("Car with license plate " + searchPlate + " not found");
                MessageBox
                        .showInfo("Search failed: No car with license plate " + searchPlate + " is currently parked.");
            }

            searchInput.setText("Enter AAA 123B");
            searchInput.setForeground(PLACEHOLDER_COLOR);
        });
    }

    private void updateSlots() {
        slotPanel.removeAll();
        ImageIcon carIcon = createCarIcon(32, 32); // Resize to 32x32 pixels for occupied slots
        ImageIcon checkIcon = createCheckIcon(32, 32); // Resize to 32x32 pixels for empty slots

        // Create slot buttons with consistent car (occupied) and checkmark (empty)
        // icons
        for (var slot : lot.getSlots()) {
            JButton btn = new JButton();

            if (slot.isOccupied()) {
                btn.setText("<html><center>" + slot.getCar().toString() +
                        "<br><br><small>(Click to remove)</small></center></html>");
                btn.setIcon(carIcon);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
                btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                btn.setBackground(OCCUPIED_SLOT_COLOR);
            } else {
                btn.setText("Empty");
                btn.setIcon(checkIcon);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
                btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                btn.setBackground(EMPTY_SLOT_COLOR);
                btn.setEnabled(false); // Disable empty slots to prevent interaction
            }

            // Uniform styling for slot buttons with rounded border, hover effect, and
            // dynamic tooltip
            btn.setForeground(TEXT_COLOR);
            btn.setToolTipText("Slot " + slot.getNumber() + ": " +
                    (slot.isOccupied() ? "Occupied, click to remove (car)" : "Empty (checkmark, not clickable)"));
            btn.setBorder(createRoundedBorder());
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(true);
            btn.setMargin(new Insets(5, 10, 5, 10));

            btn.addMouseListener(new MouseAdapter() {
                // Hover effect darkens EMPTY_SLOT_COLOR or OCCUPIED_SLOT_COLOR for enabled
                // buttons
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (btn.isEnabled()) {
                        Color baseColor = slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR;
                        btn.setBorder(createHoverBorder());
                        btn.setBackground(baseColor.darker());
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (btn.isEnabled()) {
                        btn.setBorder(createRoundedBorder());
                        btn.setBackground(slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR);
                    }
                }
            });

            int slotNumber = slot.getNumber();
            btn.addActionListener(e -> {
                if (slot.isOccupied() && !isDialogOpen) {
                    isDialogOpen = true;
                    int confirm = JOptionPane.showConfirmDialog(ParkingView.this,
                            "Remove car with license plate " + slot.getCar().getPlateNumber() + " from Slot "
                                    + slotNumber + "?",
                            "Confirm Removal", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        controller.unparkCar(slotNumber);
                        updateSlots();
                    }
                    isDialogOpen = false;
                }
            });

            slotPanel.add(btn);
        }
        slotPanel.revalidate();
        slotPanel.repaint();
    }

    private void highlightSlot(int slotNumber) {
        if (slotNumber > 0 && slotNumber <= lot.getSlots().size()) {
            Component comp = slotPanel.getComponent(slotNumber - 1);
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                ParkingSlot slot = lot.getSlots().get(slotNumber - 1);
                // Temporarily highlight slot in blue, then restore consistent slot color
                btn.setBackground(Color.BLUE);
                btn.setForeground(Color.WHITE);
                Timer timer = new Timer(2000, e -> {
                    btn.setBackground(slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR);
                    btn.setForeground(TEXT_COLOR);
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
}