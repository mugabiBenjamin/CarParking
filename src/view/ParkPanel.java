package view;

import controller.ParkingController;
import util.IconUtil;
import util.MessageBox;
import util.Validator;

import javax.swing.*;
import javax.swing.border.Border; // Added import
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.*;

public class ParkPanel extends JPanel {
    private final ParkingController controller;
    private JTextField plateInput;
    private JLabel validationIcon;
    private JButton parkButton;
    private static final Color PLACEHOLDER_COLOR = Color.GRAY;
    private static final Border ERROR_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10));

    public ParkPanel(ParkingController controller) {
        this.controller = controller;
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Park a Car",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)));
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        initComponents();
    }

    private void initComponents() {
        validationIcon = new JLabel();
        validationIcon.setPreferredSize(new Dimension(20, 20));
        validationIcon.setToolTipText("License plate validation status");

        plateInput = new JTextField(15);
        plateInput.setBorder(new RoundedBorder(8, 1));
        plateInput.setPreferredSize(new Dimension(200, 30));
        plateInput.setText("Enter A123B");
        plateInput.setForeground(PLACEHOLDER_COLOR);
        plateInput.setToolTipText("Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
        setupInputField();

        parkButton = new JButton("Park", IconUtil.createCarIcon(16, 16));
        configureButton();
        parkButton.addActionListener(e -> handleParkAction());

        add(new JLabel("License Plate:"));
        add(plateInput);
        add(validationIcon);
        add(parkButton);
    }

    private void configureButton() {
        parkButton.setBorder(new RoundedBorder(8, 1));
        parkButton.setFocusPainted(false);
        parkButton.setContentAreaFilled(true);
        parkButton.setMargin(new Insets(5, 10, 5, 10));
        parkButton.setIconTextGap(4);
        parkButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        parkButton.setToolTipText("Park a car in an available slot (Enter in input field)");
        parkButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                parkButton.setBorder(new RoundedBorder(8, 2));
                parkButton.setBackground(parkButton.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                parkButton.setBorder(new RoundedBorder(8, 1));
                parkButton.setBackground(UIManager.getColor("Button.background"));
            }
        });
    }

    private void setupInputField() {
        plateInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (plateInput.getText().equals("Enter A123B")) {
                    plateInput.setText("");
                    plateInput.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (plateInput.getText().isEmpty()) {
                    plateInput.setText("Enter A123B");
                    plateInput.setForeground(PLACEHOLDER_COLOR);
                    plateInput.setBorder(new RoundedBorder(8, 1));
                    plateInput.setToolTipText(
                            "Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
                }
            }
        });

        plateInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = plateInput.getText().trim();
                String errorMessage = getValidationErrorMessage(text);
                if (text.equals("Enter A123B") || text.isEmpty()) {
                    validationIcon.setIcon(null);
                    validationIcon.setToolTipText("License plate validation status");
                    plateInput.setBorder(new RoundedBorder(8, 1));
                    plateInput.setToolTipText(
                            "Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
                } else if (Validator.isValidPlate(text)) {
                    validationIcon.setIcon(IconUtil.createCheckIcon(16, 16, "validation"));
                    validationIcon.setToolTipText(errorMessage);
                    plateInput.setBorder(new RoundedBorder(8, 1));
                    plateInput.setToolTipText(errorMessage);
                } else {
                    validationIcon.setIcon(IconUtil.createXIcon(16, 16));
                    validationIcon.setToolTipText(errorMessage);
                    plateInput.setBorder(ERROR_BORDER);
                    plateInput.setToolTipText(errorMessage);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleParkAction();
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

    private void handleParkAction() {
        String plateText = plateInput.getText().equals("Enter A123B") ? "" : plateInput.getText().trim();
        if (!validateInput(plateText)) {
            return;
        }
        controller.parkCar(plateText);
        resetInputField();
    }

    private boolean validateInput(String text) {
        if (text.isEmpty()) {
            MessageBox.showError("Parking failed: No license plate entered.",
                    "Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123) in the input field.",
                    "Ensure the plate follows one of the allowed formats.");
            plateInput.setBorder(new RoundedBorder(8, 1));
            validationIcon.setIcon(null);
            validationIcon.setToolTipText("License plate validation status");
            plateInput.setToolTipText(
                    "Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
            plateInput.requestFocusInWindow();
            return false;
        }
        if (!Validator.isValidPlate(text)) {
            MessageBox.showError("Parking failed for license plate " + text + ": Invalid format.",
                    getValidationErrorMessage(text),
                    "Examples: UYZ 123B, UG 456C, ABC123. Ensure the format is correct.");
            plateInput.requestFocusInWindow();
            return false;
        }
        return true;
    }

    private void resetInputField() {
        plateInput.setText("Enter A123B");
        plateInput.setForeground(PLACEHOLDER_COLOR);
        validationIcon.setIcon(null);
        plateInput.setBorder(new RoundedBorder(8, 1));
        validationIcon.setToolTipText("License plate validation status");
        plateInput.setToolTipText("Enter a valid license plate (e.g., UAA 123B, UG 123B, or personalized like ABC123)");
    }

    // For testing
    public JTextField getPlateInput() {
        return plateInput;
    }

    public JLabel getValidationIcon() {
        return validationIcon;
    }

    public JButton getParkButton() {
        return parkButton;
    }
}