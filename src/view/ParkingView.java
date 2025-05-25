package view;

import controller.ParkingController;
import model.ParkingLot;
import model.ParkingSlot;
import util.Validator;
import util.MessageBox;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

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
    private JTextField searchInput;
    private JPanel slotPanel;
    private ParkingLot lot;
    private volatile boolean isDialogOpen = false;
    private Timer statusBarTimer;
    private JLabel statusBar;
    private JLabel validationIcon;
    private JLabel searchValidationIcon;
    private static final Color EMPTY_SLOT_COLOR = new Color(144, 238, 144);
    private static final Color OCCUPIED_SLOT_COLOR = new Color(255, 182, 193);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color PLACEHOLDER_COLOR = Color.GRAY;
    private static final Border ERROR_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10));

    public ParkingView() {
        setTitle("Car Parking System");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add Help Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JMenuItem visitGithubItem = new JMenuItem("Visit GitHub Repository");
        visitGithubItem.setFont(new Font("SansSerif", Font.PLAIN, 12));
        visitGithubItem.setToolTipText("Open the GitHub repository in your browser");
        visitGithubItem.addActionListener(e -> openGitHubRepository());
        helpMenu.add(visitGithubItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

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
        this.statusBarTimer = new Timer(5000, e -> {
        });
        initUI();
        setVisible(true);
    }

    private void openGitHubRepository() {
        String url = "https://github.com/mugabiBenjamin/CarParking.git";
        boolean opened = false;
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(url));
                    statusBar.setText("Opened GitHub repository");
                    clearStatusBar();
                    opened = true;
                } catch (Exception ex) {
                    System.err.println("Desktop.browse failed: " + ex.getMessage());
                }
            }
        }
        if (!opened) {
            try {
                String os = System.getProperty("os.name").toLowerCase();
                String command;
                if (os.contains("linux")) {
                    command = "xdg-open " + url;
                } else if (os.contains("mac")) {
                    command = "open " + url;
                } else if (os.contains("win")) {
                    command = "start " + url;
                } else {
                    throw new UnsupportedOperationException("Unsupported OS");
                }
                Runtime.getRuntime().exec(command);
                statusBar.setText("Opened GitHub repository");
                clearStatusBar();
                opened = true;
            } catch (Exception ex) {
                System.err.println("Command execution failed: " + ex.getMessage());
            }
        }
        if (!opened) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("<html>Could not open browser. Please visit:<br>" + url + "</html>");
            JButton copyButton = new JButton("Copy URL to Clipboard");
            copyButton.addActionListener(e2 -> {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(url), null);
                statusBar.setText("GitHub URL copied to clipboard");
                clearStatusBar();
            });
            panel.add(label, BorderLayout.CENTER);
            panel.add(copyButton, BorderLayout.SOUTH);
            JOptionPane.showMessageDialog(this, panel, "Open GitHub Manually", JOptionPane.INFORMATION_MESSAGE);
            statusBar.setText("Displayed GitHub URL");
            clearStatusBar();
        }
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

    private String getValidationErrorMessage(String text) {
        if (text.isEmpty() || text.equals("Enter A123B")) {
            return "Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)";
        }
        if (!text.matches("[A-Z].*")) {
            return "License plate must start with a letter (A-Z)";
        }
        if (text.length() > 8) {
            return "License plate cannot exceed 8 characters";
        }
        if (text.startsWith("UG")) {
            if (!text.matches("UG\\s\\d{3}[A-Z]")) {
                return "Government plate must be UG followed by space, 3 digits, and a letter (e.g., UG 123B)";
            }
        } else if (text.matches("U[A-Z]{2}\\s\\d{3}[A-Z]")) {
            return "Valid normal plate format";
        } else if (text.matches("[A-Z][A-Z0-9\\s]{1,7}")) {
            if (text.length() >= 2 && text.length() <= 8) {
                return "Valid personalized plate format";
            }
            return "Personalized plate must be 2–8 characters (letters, numbers, spaces)";
        }
        return "Invalid format: Use UAA 123B (normal), UG 123B (government), or 2–8 chars (personalized)";
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
            System.err.println("Failed to load car PNG icon: " + e.getMessage());
            BufferedImage fallback = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = fallback.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            int scale = Math.min(width / 24, height / 16);
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
            int scale = Math.min(width / 16, height / 16);
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
            int scale = Math.min(width / 16, height / 16);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12 * scale));
            g2.drawString("?", 6 * scale, 12 * scale);
            g2.dispose();
            return new ImageIcon(fallback);
        }
    }

    private ImageIcon createCheckIcon(int width, int height, String type) {
        String iconPath = type.equals("validation") ? "/resources/icons/check-green.png" : "/resources/icons/check.png";
        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResource(iconPath));
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resizedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(originalImage, 0, 0, width, height, null);
            g2.dispose();
            return new ImageIcon(resizedImage);
        } catch (IOException e) {
            System.err.println("Failed to load " + (type.equals("validation") ? "check-green" : "check") + " PNG icon: "
                    + e.getMessage());
            BufferedImage fallback = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = fallback.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GREEN);
            int scale = Math.min(width / 16, height / 16);
            g2.setStroke(new BasicStroke(2 * scale));
            g2.drawLine(4 * scale, 8 * scale, 7 * scale, 11 * scale);
            g2.drawLine(7 * scale, 11 * scale, 12 * scale, 5 * scale);
            g2.dispose();
            return new ImageIcon(fallback);
        }
    }

    private ImageIcon createXIcon(int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResource("/resources/icons/x.png"));
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resizedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(originalImage, 0, 0, width, height, null);
            g2.dispose();
            return new ImageIcon(resizedImage);
        } catch (IOException e) {
            System.err.println("Failed to load x PNG icon: " + e.getMessage());
            BufferedImage fallback = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = fallback.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.RED);
            int scale = Math.min(width / 16, height / 16);
            g2.setStroke(new BasicStroke(2 * scale));
            g2.drawLine(4 * scale, 4 * scale, 12 * scale, 12 * scale);
            g2.drawLine(4 * scale, 12 * scale, 12 * scale, 4 * scale);
            g2.dispose();
            return new ImageIcon(fallback);
        }
    }

    private void clearStatusBar() {
        if (statusBarTimer.isRunning()) {
            statusBarTimer.stop();
        }
        statusBarTimer = new Timer(5000, e -> statusBar.setText("Ready"));
        statusBarTimer.setRepeats(false);
        statusBarTimer.start();
    }

    private void initUI() {
        // Initialize validation icons
        validationIcon = new JLabel();
        validationIcon.setPreferredSize(new Dimension(20, 20));
        validationIcon.setToolTipText("License plate validation status");

        searchValidationIcon = new JLabel();
        searchValidationIcon.setPreferredSize(new Dimension(20, 20));
        searchValidationIcon.setToolTipText("Search license plate validation status");

        // Parking Section
        JPanel parkPanel = createParkPanel();
        // Search Section
        JPanel searchPanel = createSearchPanel();
        // Help Button Section
        JPanel helpPanel = createHelpPanel();

        // Control Panel Layout
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.add(parkPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(searchPanel);
        controlPanel.add(helpPanel);

        add(controlPanel, BorderLayout.NORTH);

        slotPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        updateSlots();
        add(new JScrollPane(slotPanel), BorderLayout.CENTER);

        statusBar = new JLabel("Ready", SwingConstants.CENTER);
        statusBar.setForeground(Color.BLACK);
        statusBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusBar.setToolTipText("Shows parking and search status, clears after 5 seconds");
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createParkPanel() {
        JPanel parkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        parkPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Park a Car",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)));

        plateInput = new JTextField(15);
        plateInput.setBorder(createRoundedBorder());
        plateInput.setPreferredSize(new Dimension(200, 30));
        plateInput.setText("Enter A123B");
        plateInput.setForeground(PLACEHOLDER_COLOR);
        plateInput.setToolTipText("Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
        setupInputField(plateInput, validationIcon, true);

        JButton parkBtn = new JButton("Park", createCarIcon(16, 16));
        configureButton(parkBtn, "Park a car in an available slot (Enter in input field)");
        parkBtn.addActionListener(e -> handleParkAction());

        parkPanel.add(new JLabel("License Plate:"));
        parkPanel.add(plateInput);
        parkPanel.add(validationIcon);
        parkPanel.add(parkBtn);
        return parkPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Search for a Car",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)));

        searchInput = new JTextField(15);
        searchInput.setBorder(createRoundedBorder());
        searchInput.setPreferredSize(new Dimension(200, 30));
        searchInput.setText("Enter A123B");
        searchInput.setForeground(PLACEHOLDER_COLOR);
        searchInput
                .setToolTipText("Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
        setupInputField(searchInput, searchValidationIcon, false);

        JButton searchBtn = new JButton("Search", createSearchIcon(16, 16));
        configureButton(searchBtn, "Search for a car; found slot highlights blue for 2 seconds (Enter in input field)");
        searchBtn.addActionListener(e -> handleSearchAction());

        searchPanel.add(new JLabel("License Plate:"));
        searchPanel.add(searchInput);
        searchPanel.add(searchValidationIcon);
        searchPanel.add(searchBtn);
        return searchPanel;
    }

    private JPanel createHelpPanel() {
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        helpPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JButton helpBtn = new JButton("Help", createHelpIcon(16, 16));
        configureButton(helpBtn, "Open help guide for using the parking system");
        helpBtn.addActionListener(e -> showHelpDialog());

        helpPanel.add(helpBtn);
        return helpPanel;
    }

    private void configureButton(JButton button, String tooltip) {
        button.setBorder(createRoundedBorder());
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setMargin(new Insets(5, 10, 5, 10));
        button.setIconTextGap(4);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setToolTipText(tooltip);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBorder(createHoverBorder());
                button.setBackground(button.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(createRoundedBorder());
                button.setBackground(UIManager.getColor("Button.background"));
            }
        });
    }

    private void setupInputField(JTextField inputField, JLabel validationIcon, boolean isParkField) {
        inputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputField.getText().equals("Enter A123B")) {
                    inputField.setText("");
                    inputField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputField.getText().isEmpty()) {
                    inputField.setText("Enter A123B");
                    inputField.setForeground(PLACEHOLDER_COLOR);
                    inputField.setBorder(createRoundedBorder());
                    inputField.setToolTipText(
                            "Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
                }
            }
        });

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = inputField.getText().trim();
                String errorMessage = getValidationErrorMessage(text);
                if (text.equals("Enter A123B") || text.isEmpty()) {
                    validationIcon.setIcon(null);
                    validationIcon.setToolTipText(
                            isParkField ? "License plate validation status" : "Search license plate validation status");
                    inputField.setBorder(createRoundedBorder());
                    inputField.setToolTipText(
                            "Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
                } else if (Validator.isValidPlate(text)) {
                    validationIcon.setIcon(createCheckIcon(16, 16, "validation"));
                    validationIcon.setToolTipText(errorMessage);
                    inputField.setBorder(createRoundedBorder());
                    inputField.setToolTipText(errorMessage);
                } else {
                    validationIcon.setIcon(createXIcon(16, 16));
                    validationIcon.setToolTipText(errorMessage);
                    inputField.setBorder(ERROR_BORDER);
                    inputField.setToolTipText(errorMessage);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (isParkField) {
                        handleParkAction();
                    } else {
                        handleSearchAction();
                    }
                }
            }
        });
    }

    private void handleParkAction() {
        String plateText = plateInput.getText().equals("Enter A123B") ? "" : plateInput.getText().trim();
        if (!validateInput(plateText, plateInput, validationIcon, true)) {
            return;
        }
        controller.parkCar(plateText);
        updateSlots();
        resetInputField(plateInput, validationIcon, true);
    }

    private void handleSearchAction() {
        String searchPlate = searchInput.getText().equals("Enter A123B") ? "" : searchInput.getText().trim();
        if (!validateInput(searchPlate, searchInput, searchValidationIcon, false)) {
            return;
        }
        Optional<ParkingSlot> foundSlot = controller.findCarByPlate(searchPlate);
        if (foundSlot.isPresent()) {
            int slotNumber = foundSlot.get().getNumber();
            statusBar.setText("Found car with license plate " + searchPlate + " in slot " + slotNumber);
            highlightSlot(slotNumber);
            MessageBox.showInfo("Car with license plate " + searchPlate + " found in slot " + slotNumber);
        } else {
            MessageBox.showInfo("Search failed: No car with license plate " + searchPlate + " is currently parked.");
            statusBar.setText("Car with license plate " + searchPlate + " not found");
            searchInput.requestFocusInWindow();
        }
        clearStatusBar();
        resetInputField(searchInput, searchValidationIcon, false);
    }

    private boolean validateInput(String text, JTextField inputField, JLabel validationIcon, boolean isParkField) {
        if (text.isEmpty()) {
            MessageBox.showError((isParkField ? "Parking" : "Search") + " failed: No license plate entered.",
                    "Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123) in the input field.",
                    "Ensure the plate follows one of the allowed formats.");
            statusBar.setText((isParkField ? "Parking" : "Search") + " failed: Enter a valid license plate");
            clearStatusBar();
            inputField.setBorder(createRoundedBorder());
            validationIcon.setIcon(null);
            validationIcon.setToolTipText(
                    isParkField ? "License plate validation status" : "Search license plate validation status");
            inputField.setToolTipText(
                    "Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
            inputField.requestFocusInWindow();
            return false;
        }
        if (!Validator.isValidPlate(text)) {
            MessageBox.showError(
                    (isParkField ? "Parking" : "Search") + " failed for license plate " + text + ": Invalid format.",
                    getValidationErrorMessage(text),
                    "Examples: UYZ 123B, UG 456C, ABC123. Ensure the format is correct.");
            statusBar.setText((isParkField ? "Parking" : "Search") + " failed: Invalid license plate format");
            clearStatusBar();
            inputField.requestFocusInWindow();
            return false;
        }
        return true;
    }

    private void resetInputField(JTextField inputField, JLabel validationIcon, boolean isParkField) {
        inputField.setText("Enter A123B");
        inputField.setForeground(PLACEHOLDER_COLOR);
        validationIcon.setIcon(null);
        inputField.setBorder(createRoundedBorder());
        validationIcon.setToolTipText(
                isParkField ? "License plate validation status" : "Search license plate validation status");
        inputField.setToolTipText(
                "Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
    }

    private void showHelpDialog() {
        JLabel helpLabel = new JLabel(
                "<html><div style='width: 550px; font-size: 11px; margin: 5px; line-height: 1.2;'>" +
                        "<h3 style='margin: 5px 0;'>Car Parking System Help</h3>" +
                        "<p><b>Parking a Car:</b> Enter a license plate (e.g., UAA 123B, UG 123B, ABC123) in the top field. "
                        +
                        "Use 'Park' or Enter. Green check for valid, red X with tooltip for invalid. Input preserved on errors.</p>"
                        +
                        "<p><b>Searching:</b> Enter a plate in the search field, use 'Search' or Enter. Valid plates highlight slot in blue for 2s. Input preserved if invalid or not found.</p>"
                        +
                        "<p><b>Removing a Car:</b> Click an occupied (light red) slot, confirm to unpark (irreversible).</p>"
                        +
                        "<p><b>Slot Status:</b><ul style='margin: 5px 0; padding-left: 20px;'>" +
                        "<li><font color='#90EE90'>Light green</font>: Empty (check icon, not clickable).</li>" +
                        "<li><font color='#FFB6C1'>Light red</font>: Occupied (car icon, clickable).</li>" +
                        "<li><font color='blue'>Blue</font>: Found (after search).</li></ul></p>" +
                        "<p><b>Plate Formats:</b><ul style='margin: 5px 0; padding-left: 20px;'>" +
                        "<li><b>Normal</b>: UAA 123B (U, 2 letters, space, 3 digits, letter).</li>" +
                        "<li><b>Government</b>: UG 123B (UG, space, 3 digits, letter).</li>" +
                        "<li><b>Personalized</b>: 2–8 chars, starts with letter (e.g., ABC123, X12 Y34).</li></ul></p>"
                        +
                        "<p><b>Shortcuts:</b> Enter in input fields to park or search.</p>" +
                        "<p><b>Errors:</b> Tooltips and red borders guide corrections. Input preserved for editing.</p>"
                        +
                        "<p><b>Documentation:</b> Use the Help menu to visit the GitHub repository.</p>" +
                        "<p><b>Accessibility:</b> High-contrast colors, preserved inputs, readable tooltips.</p>" +
                        "<p><b>Status Bar:</b> Shows actions, clears after 5s.</p>" +
                        "</div></html>");
        JScrollPane scrollPane = new JScrollPane(helpLabel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        JOptionPane.showMessageDialog(this, scrollPane, "Help Guide", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateSlots() {
        slotPanel.removeAll();
        ImageIcon carIcon = createCarIcon(32, 32);
        ImageIcon checkIcon = createCheckIcon(32, 32, "slot");

        for (ParkingSlot slot : lot.getSlots()) {
            JButton btn = createSlotButton(slot, carIcon, checkIcon);
            slotPanel.add(btn);
        }
        slotPanel.revalidate();
        slotPanel.repaint();
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
        btn.setBorder(createRoundedBorder());
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setMargin(new Insets(5, 10, 5, 10));
    }

    private void setupSlotButtonListeners(JButton btn, ParkingSlot slot) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBorder(createHoverBorder());
                btn.setBackground(OCCUPIED_SLOT_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBorder(createRoundedBorder());
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
                statusBar.setText("Removed car with license plate " + plateNumber + " from slot " + slotNumber);
                clearStatusBar();
                updateSlots();
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

    private void highlightSlot(int slotNumber) {
        if (slotNumber > 0 && slotNumber <= lot.getSlots().size()) {
            Component comp = slotPanel.getComponent(slotNumber - 1);
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                ParkingSlot slot = lot.getSlots().get(slotNumber - 1);
                btn.setBackground(Color.BLUE);
                btn.setForeground(Color.WHITE);
                Timer timer = new Timer(2000, e -> {
                    btn.setBackground(slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR);
                    btn.setForeground(slot.isOccupied() ? Color.BLACK : TEXT_COLOR);
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
}