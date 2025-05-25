package view;

import controller.ParkingController;
import util.IconUtil;
import util.MessageBox;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BatchPanel extends JPanel {
    private final ParkingController controller;
    private final SlotPanel slotPanel;
    private final JLabel statusBar;
    private JButton batchUnparkButton;
    private JButton generateReportButton;

    public BatchPanel(ParkingController controller, SlotPanel slotPanel, JLabel statusBar) {
        this.controller = controller;
        this.slotPanel = slotPanel;
        this.statusBar = statusBar;
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Batch Operations",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)));
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        initComponents();
    }

    private void initComponents() {
        batchUnparkButton = new JButton("Batch Unpark", IconUtil.createUnparkIcon(16, 16));
        configureButton(batchUnparkButton);
        batchUnparkButton.addActionListener(e -> handleBatchUnparkAction());
        batchUnparkButton.setToolTipText("Unpark all selected occupied slots");

        generateReportButton = new JButton("Generate Report", IconUtil.createReportIcon(16, 16));
        configureButton(generateReportButton);
        generateReportButton.addActionListener(e -> handleGenerateReportAction());
        generateReportButton.setToolTipText("Generate a CSV report of all parking slots");

        add(batchUnparkButton);
        add(generateReportButton);
    }

    private void configureButton(JButton button) {
        button.setBorder(new RoundedBorder(8, 1));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setMargin(new Insets(5, 10, 5, 10));
        button.setIconTextGap(4);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBorder(new RoundedBorder(8, 2));
                button.setBackground(button.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(new RoundedBorder(8, 1));
                button.setBackground(UIManager.getColor("Button.background"));
            }
        });
    }

    private void handleBatchUnparkAction() {
        List<Integer> selectedSlots = slotPanel.getSelectedSlots();
        if (selectedSlots.isEmpty()) {
            MessageBox.showError("No slots selected for batch unpark.",
                    "Select at least one occupied slot using the checkboxes.",
                    "Click the checkboxes next to occupied slots and try again.");
            statusBar.setText("Batch unpark failed: No slots selected");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to unpark " + selectedSlots.size() + " car(s)? This action is irreversible.",
                "Confirm Batch Unpark",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            statusBar.setText("Batch unpark cancelled");
            return;
        }

        controller.batchUnpark(selectedSlots);
        slotPanel.clearSelection();
    }

    private void handleGenerateReportAction() {
        controller.generateReport();
    }

    // For testing
    public JButton getBatchUnparkButton() {
        return batchUnparkButton;
    }

    public JButton getGenerateReportButton() {
        return generateReportButton;
    }
}