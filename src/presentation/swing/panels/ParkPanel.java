package presentation.swing.panels;

import application.dto.OperationResult;
import presentation.swing.ParkingViewController;
import presentation.swing.components.RoundedBorder;
import presentation.swing.resources.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class ParkPanel extends JPanel {
    private static final String PANEL_TITLE = "Park a Car";
    private static final String PLACEHOLDER_TEXT = "Enter plate";
    private static final String DEFAULT_TOOLTIP = "Enter a valid Uganda license plate";
    private static final String VALID_TOOLTIP = "Valid license plate";

    private static final int INPUT_COLUMNS = 15;
    private static final int INPUT_WIDTH = 200;
    private static final int INPUT_HEIGHT = 30;
    private static final int ICON_SIZE = 16;

    private static final Color PLACEHOLDER_COLOR = Color.GRAY;
    private static final Color INPUT_TEXT_COLOR = Color.BLACK;

    private static final Border ERROR_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
    );

    private final ParkingViewController controller;
    private final IconUtil iconUtil;

    private JTextField plateInput;
    private JLabel validationIcon;
    private JButton parkButton;

    public ParkPanel(ParkingViewController controller, IconUtil iconUtil) {
        if (controller == null) {
            throw new IllegalArgumentException("Parking view controller cannot be null");
        }

        if (iconUtil == null) {
            throw new IllegalArgumentException("Icon utility cannot be null");
        }

        this.controller = controller;
        this.iconUtil = iconUtil;

        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                PANEL_TITLE,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)
        ));

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        initializeComponents();
    }

    public JTextField getPlateInput() {
        return plateInput;
    }

    private void initializeComponents() {
        validationIcon = new JLabel();
        validationIcon.setPreferredSize(new Dimension(20, 20));
        validationIcon.setToolTipText(DEFAULT_TOOLTIP);

        plateInput = createPlateInput();
        parkButton = createParkButton();

        add(new JLabel("License Plate:"));
        add(plateInput);
        add(validationIcon);
        add(parkButton);
    }

    private JTextField createPlateInput() {
        JTextField input = new JTextField(INPUT_COLUMNS);
        input.setBorder(new RoundedBorder(8, 1));
        input.setPreferredSize(new Dimension(INPUT_WIDTH, INPUT_HEIGHT));
        input.setMargin(new Insets(5, 10, 5, 10));
        input.setText(PLACEHOLDER_TEXT);
        input.setForeground(PLACEHOLDER_COLOR);
        input.setToolTipText(DEFAULT_TOOLTIP);

        input.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                if (isPlaceholderVisible()) {
                    plateInput.setText("");
                    plateInput.setForeground(INPUT_TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent event) {
                if (getInputValue().isEmpty()) {
                    resetInputToPlaceholder();
                }
            }
        });

        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                validateCurrentInput();
            }

            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    submit();
                }
            }
        });

        return input;
    }

    private JButton createParkButton() {
        JButton button = new JButton("Park", iconUtil.createCarIcon(ICON_SIZE, ICON_SIZE));
        button.setBorder(new RoundedBorder(8, 1));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setMargin(new Insets(5, 10, 5, 10));
        button.setIconTextGap(4);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setToolTipText("Park a car in an available slot");
        button.addMouseListener(createHoverEffect(button));
        button.addActionListener(event -> submit());

        return button;
    }

    private MouseAdapter createHoverEffect(JButton button) {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                button.setBorder(new RoundedBorder(8, 2));

                if (button.getBackground() != null) {
                    button.setBackground(button.getBackground().darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                button.setBorder(new RoundedBorder(8, 1));
                button.setBackground(UIManager.getColor("Button.background"));
            }
        };
    }

    private void submit() {
        String input = getInputValue();

        if (input.isEmpty()) {
            applyInvalidState("License plate is required");
            controller.showValidationError("License plate is required", "Enter a valid Uganda license plate and try again.");
            plateInput.requestFocusInWindow();
            return;
        }

        OperationResult<?> validationResult = controller.validateLicensePlate(input);

        if (validationResult.isFailure()) {
            applyInvalidState(validationResult.getMessage());
            controller.showValidationError(validationResult.getMessage(), "Use a supported Uganda license plate format and try again.");
            plateInput.requestFocusInWindow();
            return;
        }

        controller.parkCar(input);
        resetInputToPlaceholder();
    }

    private void validateCurrentInput() {
        String input = getInputValue();

        if (input.isEmpty()) {
            resetValidationState();
            return;
        }

        OperationResult<?> validationResult = controller.validateLicensePlate(input);

        if (validationResult.isSuccess()) {
            applyValidState(validationResult.getMessage());
        } else {
            applyInvalidState(validationResult.getMessage());
        }
    }

    private void applyValidState(String message) {
        plateInput.setBorder(new RoundedBorder(8, 1));
        plateInput.setToolTipText(isBlank(message) ? VALID_TOOLTIP : message);
        validationIcon.setIcon(iconUtil.createCheckIcon(ICON_SIZE, ICON_SIZE, "validation"));
        validationIcon.setToolTipText(VALID_TOOLTIP);
    }

    private void applyInvalidState(String message) {
        String safeMessage = isBlank(message) ? "Invalid license plate" : message;

        plateInput.setBorder(ERROR_BORDER);
        plateInput.setToolTipText(safeMessage);
        validationIcon.setIcon(iconUtil.createXIcon(ICON_SIZE, ICON_SIZE));
        validationIcon.setToolTipText(safeMessage);
    }

    private void resetValidationState() {
        plateInput.setBorder(new RoundedBorder(8, 1));
        plateInput.setToolTipText(DEFAULT_TOOLTIP);
        validationIcon.setIcon(null);
        validationIcon.setToolTipText(DEFAULT_TOOLTIP);
    }

    private void resetInputToPlaceholder() {
        plateInput.setText(PLACEHOLDER_TEXT);
        plateInput.setForeground(PLACEHOLDER_COLOR);
        resetValidationState();
    }

    private String getInputValue() {
        if (plateInput == null || plateInput.getText() == null) {
            return "";
        }

        String value = plateInput.getText().trim();

        if (PLACEHOLDER_TEXT.equals(value)) {
            return "";
        }

        return value;
    }

    private boolean isPlaceholderVisible() {
        return PLACEHOLDER_TEXT.equals(plateInput.getText()) && PLACEHOLDER_COLOR.equals(plateInput.getForeground());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}