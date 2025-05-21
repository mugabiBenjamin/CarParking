package view;

import model.ParkingSlot;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// Custom rounded border class (duplicate of the one in ParkingView for completeness)
class RoundedBorder implements Border {
    private int radius;

    RoundedBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c.getBackground().darker());
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

public class ParkingSlotPanel extends JPanel {
    private JButton button;
    private int slotNumber;

    // Define custom colors
    private static final Color EMPTY_SLOT_COLOR = new Color(144, 238, 144); // Light Green
    private static final Color OCCUPIED_SLOT_COLOR = new Color(255, 182, 193); // Light Red
    private static final Color TEXT_COLOR = Color.BLACK; // Black text

    public ParkingSlotPanel(ParkingSlot slot) {
        this.slotNumber = slot.getNumber();
        this.setLayout(new BorderLayout());

        button = new JButton(slot.isOccupied() ? slot.getCar().getPlateNumber() : "Empty");
        button.setBackground(slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setToolTipText("Slot " + slotNumber);
        button.setFocusable(false);
        button.setFocusPainted(false);  // Remove focus outline

        // Set preferred size to make button wider
        button.setPreferredSize(new Dimension(180, 40));

        // Apply rounded border with padding
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        this.add(button, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public JButton getButton() {
        return button;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void update(ParkingSlot slot) {
        button.setText(slot.isOccupied() ? slot.getCar().getPlateNumber() : "Empty");
        button.setBackground(slot.isOccupied() ? OCCUPIED_SLOT_COLOR : EMPTY_SLOT_COLOR);
    }
}