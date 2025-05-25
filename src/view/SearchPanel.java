package view;

import controller.ParkingController;
import model.ParkingSlot;
import util.IconUtil;
import util.Logger;
import util.MessageBox;
import util.Validator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.*;
import java.util.Optional;

public class SearchPanel extends JPanel {
    private final ParkingController controller;
    private JTextField searchInput;
    private JLabel searchValidationIcon;
    private JButton searchButton;
    private static final Color PLACEHOLDER_COLOR = Color.GRAY;
    private static final Border ERROR_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10));
    private final SlotPanel slotPanel;
    private final JLabel statusBar;

    public SearchPanel(ParkingController controller, SlotPanel slotPanel, JLabel statusBar) {
        this.controller = controller;
        this.slotPanel = slotPanel;
        this.statusBar = statusBar;
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Search for a Car",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)));
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        initComponents();
    }

    private void initComponents() {
        searchValidationIcon = new JLabel();
        searchValidationIcon.setPreferredSize(new Dimension(20, 20));
        searchValidationIcon.setToolTipText("Search license plate validation status");

        searchInput = new JTextField(15);
        searchInput.setBorder(new RoundedBorder(8, 1));
        searchInput.setPreferredSize(new Dimension(200, 30));
        searchInput.setMargin(new Insets(5, 10, 5, 10));
        searchInput.setText("Enter A123B");
        searchInput.setForeground(PLACEHOLDER_COLOR);
        searchInput.setToolTipText("Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
        searchInput.setEnabled(true);
        searchInput.setFocusable(true);
        searchInput.setRequestFocusEnabled(true);
        // Debug: Log click events
        searchInput.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Logger.log("SearchPanel: searchInput clicked at " + e.getPoint());
                searchInput.requestFocusInWindow();
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                searchInput.requestFocus();
            }
        });
        setupInputField();

        searchButton = new JButton("Search", IconUtil.createSearchIcon(16, 16));
        configureButton();
        searchButton.addActionListener(e -> handleSearchAction());

        add(new JLabel("License Plate:"));
        add(searchInput);
        add(searchValidationIcon);
        add(searchButton);
    }

    private void configureButton() {
        searchButton.setBorder(new RoundedBorder(8, 1));
        searchButton.setFocusPainted(false);
        searchButton.setContentAreaFilled(true);
        searchButton.setMargin(new Insets(5, 10, 5, 10));
        searchButton.setIconTextGap(4);
        searchButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        searchButton.setToolTipText("Search for a car; found slot highlights blue for 2 seconds (Enter in input field)");
        searchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                searchButton.setBorder(new RoundedBorder(8, 2));
                searchButton.setBackground(searchButton.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                searchButton.setBorder(new RoundedBorder(8, 1));
                searchButton.setBackground(UIManager.getColor("Button.background"));
            }
        });
    }

    private void setupInputField() {
        searchInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                Logger.log("SearchPanel: searchInput gained focus");
                if (searchInput.getText().equals("Enter A123B")) {
                    searchInput.setText("");
                    searchInput.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                Logger.log("SearchPanel: searchInput lost focus");
                String text = searchInput.getText().trim();
                if (text.isEmpty()) {
                    searchInput.setText("Enter A123B");
                    searchInput.setForeground(PLACEHOLDER_COLOR);
                    searchInput.setBorder(new RoundedBorder(8, 1));
                    searchValidationIcon.setIcon(null);
                    searchValidationIcon.setToolTipText("Search license plate validation status");
                } else if (!Validator.isValidPlate(text)) {
                    searchInput.setBorder(ERROR_BORDER);
                    searchValidationIcon.setIcon(IconUtil.createXIcon(16, 16));
                    searchValidationIcon.setToolTipText(getValidationErrorMessage(text));
                } else {
                    searchInput.setBorder(new RoundedBorder(8, 1));
                    searchValidationIcon.setIcon(IconUtil.createCheckIcon(16, 16, "validation"));
                    searchValidationIcon.setToolTipText("Valid license plate");
                }
            }
        });

        searchInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchInput.getText().trim();
                String errorMessage = getValidationErrorMessage(text);
                if (text.isEmpty() || text.equals("Enter A123B")) {
                    searchInput.setBorder(new RoundedBorder(8, 1));
                    searchValidationIcon.setIcon(null);
                    searchValidationIcon.setToolTipText("Search license plate validation status");
                    searchInput.setToolTipText("Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
                } else if (Validator.isValidPlate(text)) {
                    searchInput.setBorder(new RoundedBorder(8, 1));
                    searchValidationIcon.setIcon(IconUtil.createCheckIcon(16, 16, "validation"));
                    searchValidationIcon.setToolTipText("Valid license plate");
                    searchInput.setToolTipText("Valid license plate");
                } else {
                    searchInput.setBorder(ERROR_BORDER);
                    searchValidationIcon.setIcon(IconUtil.createXIcon(16, 16));
                    searchValidationIcon.setToolTipText(errorMessage);
                    searchInput.setToolTipText(errorMessage);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSearchAction();
                }
            }
        });
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

    private void handleSearchAction() {
        String searchPlate = searchInput.getText().equals("Enter A123B") ? "" : searchInput.getText().trim();
        if (!validateInput(searchPlate)) {
            statusBar.setText("Search failed: Invalid input");
            return;
        }
        Optional<ParkingSlot> foundSlot = controller.findCarByPlate(searchPlate);
        if (foundSlot.isPresent()) {
            int slotNumber = foundSlot.get().getNumber();
            slotPanel.highlightSlot(slotNumber);
            MessageBox.showInfo("Car with license plate " + searchPlate + " found in slot " + slotNumber);
        } else {
            MessageBox.showInfo("Search failed: No car with license plate " + searchPlate + " is currently parked.");
            statusBar.setText("Car with license plate " + searchPlate + " not found");
            searchInput.requestFocusInWindow();
        }
        resetInputField();
    }

    private boolean validateInput(String text) {
        if (text.isEmpty()) {
            MessageBox.showError("Search failed: No license plate entered.",
                    "Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123) in the input field.",
                    "Ensure the plate follows one of the allowed formats.");
            statusBar.setText("Search failed: Enter a valid license plate");
            searchInput.setBorder(new RoundedBorder(8, 1));
            searchValidationIcon.setIcon(null);
            searchValidationIcon.setToolTipText("Search license plate validation status");
            searchInput.setToolTipText("Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
            searchInput.requestFocusInWindow();
            return false;
        }
        if (!Validator.isValidPlate(text)) {
            MessageBox.showError("Search failed for license plate " + text + ": Invalid format.",
                    getValidationErrorMessage(text),
                    "Examples: UYZ 123B, UG 456C, ABC123. Ensure the format is correct.");
            statusBar.setText("Search failed: Invalid license plate format");
            searchInput.requestFocusInWindow();
            return false;
        }
        return true;
    }

    private void resetInputField() {
        searchInput.setText("Enter A123B");
        searchInput.setForeground(PLACEHOLDER_COLOR);
        searchInput.setBorder(new RoundedBorder(8, 1));
        searchValidationIcon.setIcon(null);
        searchValidationIcon.setToolTipText("Search license plate validation status");
        searchInput.setToolTipText("Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
    }

    // For testing
    public JTextField getSearchInput() {
        return searchInput;
    }

    public JLabel getSearchValidationIcon() {
        return searchValidationIcon;
    }

    public JButton getSearchButton() {
        return searchButton;
    }
}