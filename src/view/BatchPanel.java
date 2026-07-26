package view;

import controller.ParkingController;
import util.IconUtil;
import util.Logger;
import util.MessageBox;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public final class BatchPanel extends JPanel {
    private static final int ICON_SIZE = 16;
    private static final String PANEL_TITLE = "Batch Operations";
    private static final String BATCH_UNPARK_TEXT = "Batch Unpark";
    private static final String GENERATE_REPORT_TEXT = "Generate Report";

    private final ParkingController controller;
    private final SlotPanel slotPanel;
    private final JLabel statusBar;

    private JButton batchUnparkButton;
    private JButton generateReportButton;

    public BatchPanel(ParkingController controller, SlotPanel slotPanel, JLabel statusBar) {
        if (controller == null) {
            throw new IllegalArgumentException("Parking controller cannot be null");
        }

        if (slotPanel == null) {
            throw new IllegalArgumentException("Slot panel cannot be null");
        }

        if (statusBar == null) {
            throw new IllegalArgumentException("Status bar cannot be null");
        }

        this.controller = controller;
        this.slotPanel = slotPanel;
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
        batchUnparkButton = createButton(
                BATCH_UNPARK_TEXT,
                IconUtil.createUnparkIcon(ICON_SIZE, ICON_SIZE),
                "Unpark all selected occupied slots"
        );

        generateReportButton = createButton(
                GENERATE_REPORT_TEXT,
                IconUtil.createReportIcon(ICON_SIZE, ICON_SIZE),
                "Generate a CSV report of all parking slots"
        );

        batchUnparkButton.addActionListener(event -> handleBatchUnparkAction());
        generateReportButton.addActionListener(event -> handleGenerateReportAction());

        add(batchUnparkButton);
        add(generateReportButton);
    }

    private JButton createButton(String text, javax.swing.Icon icon, String tooltip) {
        JButton button = new JButton(text, icon);
        button.setBorder(new RoundedBorder(8, 1));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setMargin(new Insets(5, 10, 5, 10));
        button.setIconTextGap(4);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setToolTipText(tooltip);
        button.addMouseListener(createButtonHoverEffect(button));
        return button;
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

    private void handleBatchUnparkAction() {
        try {
            List<Integer> selectedSlots = slotPanel.getSelectedSlots();

            if (selectedSlots.isEmpty()) {
                MessageBox.showError(
                        "No slots selected for batch unpark.",
                        "Select at least one occupied slot using the checkboxes.",
                        "Click the checkboxes next to occupied slots and try again."
                );

                statusBar.setText("Batch unpark failed: No slots selected");
                return;
            }

            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to unpark " + selectedSlots.size() + " car(s)? This action is irreversible.",
                    "Confirm Batch Unpark",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmation != JOptionPane.YES_OPTION) {
                slotPanel.clearSelection();
                statusBar.setText("Batch unpark cancelled");
                Logger.info("Batch unpark cancelled. Cleared " + selectedSlots.size() + " selected slots");
                return;
            }

            controller.batchUnpark(selectedSlots);
            slotPanel.clearSelection();
        } catch (Exception exception) {
            Logger.error("Failed to process batch unpark action", exception);
            statusBar.setText("Batch unpark failed: Unexpected error");

            MessageBox.showError(
                    "Batch unpark failed because an unexpected error occurred.",
                    "Refresh the parking slots and try again.",
                    "Restart the application if the problem continues."
            );
        }
    }

    private void handleGenerateReportAction() {
        try {
            controller.generateReport();
        } catch (Exception exception) {
            Logger.error("Failed to generate parking report", exception);
            statusBar.setText("Report generation failed");

            MessageBox.showError(
                    "Report generation failed.",
                    "Check that the application has permission to write report files.",
                    "Close any open report file and try again."
            );
        }
    }

    public JButton getBatchUnparkButton() {
        return batchUnparkButton;
    }

    public JButton getGenerateReportButton() {
        return generateReportButton;
    }
}