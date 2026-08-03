package presentation.swing.panels;

import presentation.swing.ParkingViewController;
import presentation.swing.components.RoundedBorder;
import presentation.swing.resources.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public final class BatchPanel extends JPanel {
    private static final int ICON_SIZE = 16;

    private final ParkingViewController controller;
    private final SlotPanel slotPanel;
    private final IconUtil iconUtil;

    private JButton batchUnparkButton;
    private JButton reportButton;

    public BatchPanel(ParkingViewController controller, SlotPanel slotPanel, IconUtil iconUtil) {
        if (controller == null) {
            throw new IllegalArgumentException("Parking view controller cannot be null");
        }

        if (slotPanel == null) {
            throw new IllegalArgumentException("Slot panel cannot be null");
        }

        if (iconUtil == null) {
            throw new IllegalArgumentException("Icon utility cannot be null");
        }

        this.controller = controller;
        this.slotPanel = slotPanel;
        this.iconUtil = iconUtil;

        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Batch Operations",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)
        ));

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        initializeComponents();
    }

    private void initializeComponents() {
        batchUnparkButton = new JButton("Batch Unpark", iconUtil.createUnparkIcon(ICON_SIZE, ICON_SIZE));
        configureButton(batchUnparkButton, "Unpark selected occupied slots");
        batchUnparkButton.addActionListener(event -> handleBatchUnpark());

        reportButton = new JButton("Generate Report", iconUtil.createReportIcon(ICON_SIZE, ICON_SIZE));
        configureButton(reportButton, "Generate CSV parking report");
        reportButton.addActionListener(event -> controller.generateReport());

        add(batchUnparkButton);
        add(reportButton);
    }

    private void configureButton(JButton button, String tooltip) {
        button.setBorder(new RoundedBorder(8, 1));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setMargin(new Insets(5, 10, 5, 10));
        button.setIconTextGap(4);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setToolTipText(tooltip);
        button.addMouseListener(createHoverEffect(button));
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

    private void handleBatchUnpark() {
        List<Integer> selectedSlots = slotPanel.getSelectedSlots();

        if (selectedSlots.isEmpty()) {
            controller.showValidationError("No slots selected", "Select at least one occupied slot and try again.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Unpark " + selectedSlots.size() + " selected car(s)?",
                "Confirm Batch Unpark",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            controller.batchUnpark(selectedSlots);
            slotPanel.clearSelection();
        }
    }
}