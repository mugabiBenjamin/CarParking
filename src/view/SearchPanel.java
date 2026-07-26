package view;

import controller.ParkingController;
import util.IconUtil;
import util.Logger;
import util.MessageBox;
import util.Validator;
import util.Validator.PlateValidationMessage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public final class SearchPanel extends JPanel {
    private static final String PANEL_TITLE = "Search for a Car";
    private static final String INPUT_LABEL = "License Plate:";
    private static final String PLACEHOLDER_TEXT = "Enter plate";
    private static final String DEFAULT_TOOLTIP = "Enter a valid Uganda license plate";
    private static final String VALID_TOOLTIP = "Valid license plate";
    private static final String INVALID_STATUS_MESSAGE = "Search failed: Invalid input";
    private static final String EMPTY_STATUS_MESSAGE = "Search failed: Enter a valid license plate";
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

    private final ParkingController controller;
    private final JLabel statusBar;

    private JTextField searchInput;
    private JLabel searchValidationIcon;
    private JButton searchButton;

    public SearchPanel(ParkingController controller, SlotPanel slotPanel, JLabel statusBar) {
        if (controller == null) {
            throw new IllegalArgumentException("Parking controller cannot be null");
        }

        if (statusBar == null) {
            throw new IllegalArgumentException("Status bar cannot be null");
        }

        this.controller = controller;
        this.statusBar = statusBar;

        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                PANEL_TITLE,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)
        ));

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        initComponents();
    }

    private void initComponents() {
        searchValidationIcon = createValidationIcon();
        searchInput = createSearchInput();
        searchButton = createSearchButton();

        add(new JLabel(INPUT_LABEL));
        add(searchInput);
        add(searchValidationIcon);
        add(searchButton);
    }

    private JLabel createValidationIcon() {
        JLabel icon = new JLabel();
        icon.setPreferredSize(new Dimension(20, 20));
        icon.setToolTipText(DEFAULT_TOOLTIP);
        return icon;
    }

    private JTextField createSearchInput() {
        JTextField input = new JTextField(INPUT_COLUMNS);
        input.setBorder(new RoundedBorder(8, 1));
        input.setPreferredSize(new Dimension(INPUT_WIDTH, INPUT_HEIGHT));
        input.setMargin(new Insets(5, 10, 5, 10));
        input.setText(PLACEHOLDER_TEXT);
        input.setForeground(PLACEHOLDER_COLOR);
        input.setToolTipText(DEFAULT_TOOLTIP);
        input.setEnabled(true);
        input.setFocusable(true);
        input.setRequestFocusEnabled(true);

        input.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                Logger.info("SearchPanel input clicked");
                searchInput.requestFocusInWindow();
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                searchInput.requestFocus();
            }
        });

        input.addFocusListener(createInputFocusListener());
        input.addKeyListener(createInputKeyListener());

        return input;
    }

    private JButton createSearchButton() {
        JButton button = new JButton("Search", IconUtil.createSearchIcon(ICON_SIZE, ICON_SIZE));
        button.setBorder(new RoundedBorder(8, 1));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setMargin(new Insets(5, 10, 5, 10));
        button.setIconTextGap(4);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setToolTipText("Search for a parked car by license plate");
        button.addMouseListener(createButtonHoverEffect(button));
        button.addActionListener(event -> handleSearchAction());
        return button;
    }

    private FocusAdapter createInputFocusListener() {
        return new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                if (isPlaceholderVisible()) {
                    searchInput.setText("");
                    searchInput.setForeground(INPUT_TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent event) {
                String input = getInputValue();

                if (input.isEmpty()) {
                    resetInputToPlaceholder();
                    return;
                }

                updateValidationState(input);
            }
        };
    }

    private KeyAdapter createInputKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                String input = getInputValue();

                if (input.isEmpty()) {
                    resetValidationState();
                    return;
                }

                updateValidationState(input);
            }

            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSearchAction();
                }
            }
        };
    }

    private MouseAdapter createButtonHoverEffect(JButton button) {
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

    private void handleSearchAction() {
        try {
            String plateText = getInputValue();

            if (!validateInputBeforeSubmit(plateText)) {
                return;
            }

            String normalizedPlate = Validator.normalizePlate(plateText);
            controller.findCarByPlate(normalizedPlate);
            resetInputToPlaceholder();
        } catch (Exception exception) {
            Logger.error("Failed to process search action", exception);
            statusBar.setText("Search failed: Unexpected error");

            MessageBox.showError(
                    "Search failed because an unexpected error occurred.",
                    "Check the license plate and try again.",
                    "Restart the application if the problem continues."
            );
        }
    }

    private boolean validateInputBeforeSubmit(String input) {
        PlateValidationMessage validationMessage = Validator.getPlateValidationMessage(input);

        if (input.isEmpty()) {
            MessageBox.showError(
                    "Search failed: No license plate entered.",
                    "Enter a valid Uganda license plate.",
                    "Examples: UA 001AA, UG 32 00042, CD 01 02 U, UMA 001AA, UAA 123B."
            );

            statusBar.setText(EMPTY_STATUS_MESSAGE);
            resetValidationState();
            searchInput.requestFocusInWindow();
            return false;
        }

        if (!validationMessage.isValid()) {
            MessageBox.showError(
                    "Search failed for license plate " + input + ": Invalid format.",
                    validationMessage.getMessage(),
                    "Use a supported Uganda plate format and try again."
            );

            statusBar.setText(INVALID_STATUS_MESSAGE);
            applyInvalidState(validationMessage.getMessage());
            searchInput.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private void updateValidationState(String input) {
        PlateValidationMessage validationMessage = Validator.getPlateValidationMessage(input);

        if (validationMessage.isValid()) {
            applyValidState(validationMessage.getMessage());
        } else {
            applyInvalidState(validationMessage.getMessage());
        }
    }

    private void applyValidState(String message) {
        searchInput.setBorder(new RoundedBorder(8, 1));
        searchInput.setToolTipText(message == null || message.isBlank() ? VALID_TOOLTIP : message);
        searchValidationIcon.setIcon(IconUtil.createCheckIcon(ICON_SIZE, ICON_SIZE, "validation"));
        searchValidationIcon.setToolTipText(VALID_TOOLTIP);
    }

    private void applyInvalidState(String message) {
        String safeMessage = message == null || message.isBlank() ? "Invalid license plate" : message;

        searchInput.setBorder(ERROR_BORDER);
        searchInput.setToolTipText(safeMessage);
        searchValidationIcon.setIcon(IconUtil.createXIcon(ICON_SIZE, ICON_SIZE));
        searchValidationIcon.setToolTipText(safeMessage);
    }

    private void resetValidationState() {
        searchInput.setBorder(new RoundedBorder(8, 1));
        searchInput.setToolTipText(DEFAULT_TOOLTIP);
        searchValidationIcon.setIcon(null);
        searchValidationIcon.setToolTipText(DEFAULT_TOOLTIP);
    }

    private void resetInputToPlaceholder() {
        searchInput.setText(PLACEHOLDER_TEXT);
        searchInput.setForeground(PLACEHOLDER_COLOR);
        resetValidationState();
    }

    private String getInputValue() {
        if (searchInput == null || searchInput.getText() == null) {
            return "";
        }

        String value = searchInput.getText().trim();

        if (PLACEHOLDER_TEXT.equals(value)) {
            return "";
        }

        return value;
    }

    private boolean isPlaceholderVisible() {
        return PLACEHOLDER_TEXT.equals(searchInput.getText()) && PLACEHOLDER_COLOR.equals(searchInput.getForeground());
    }

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