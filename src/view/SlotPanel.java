package view;

import controller.ParkingController;
import model.ParkingLot;
import model.ParkingSlot;
import util.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SlotPanel extends JPanel {
    private final ParkingController controller;
    private final ParkingLot lot;
    private final JLabel statusBar;
    private final SlotComponent[] slotComponents;
    private static final int BORDER_RADIUS = 15;

    public SlotPanel(ParkingController controller, ParkingLot lot, JLabel statusBar) {
        this.controller = controller;
        this.lot = lot;
        this.statusBar = statusBar;
        this.slotComponents = new SlotComponent[lot.getSize()];
        setLayout(new GridLayout(2, 5, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initializeSlots();
    }

    private void initializeSlots() {
        for (int i = 0; i < lot.getSize(); i++) {
            ParkingSlot slot = lot.getSlots().get(i);
            SlotComponent component = new SlotComponent(slot);
            slotComponents[i] = component;
            add(component);
        }
    }

    public void updateSlots() {
        for (int i = 0; i < lot.getSize(); i++) {
            ParkingSlot slot = lot.getSlots().get(i);
            slotComponents[i].update(slot);
        }
        revalidate();
        repaint();
    }

    public void highlightSlot(int slotNumber) {
        if (slotNumber >= 1 && slotNumber <= lot.getSize()) {
            SlotComponent component = slotComponents[slotNumber - 1];
            Color original = component.getBackground();
            component.setBackground(Color.BLUE);
            statusBar.setText("Highlighted slot " + slotNumber);
            Timer timer = new Timer(2000, e -> {
                component.setBackground(original);
                component.update(lot.getSlot(slotNumber).orElse(null));
                statusBar.setText("Ready");
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    public List<Integer> getSelectedSlots() {
        List<Integer> selected = new ArrayList<>();
        for (SlotComponent component : slotComponents) {
            if (component.isSelected()) {
                selected.add(component.getSlotNumber());
            }
        }
        if (!selected.isEmpty()) {
            statusBar.setText("Selected slots: " + selected);
        } else {
            statusBar.setText("No slots selected");
        }
        return selected;
    }

    public void clearSelection() {
        for (SlotComponent component : slotComponents) {
            component.setSelected(false);
        }
        updateSlots();
        statusBar.setText("Selection cleared");
    }

    private class SlotComponent extends JPanel {
        private final JLabel iconLabel;
        private final JLabel textLabel;
        private final JCheckBox selectBox;
        private final JButton unparkButton;
        private final int slotNumber;

        public SlotComponent(ParkingSlot slot) {
            this.slotNumber = slot.getNumber();
            setLayout(new BorderLayout(5, 5));
            setBorder(new RoundedBorder(BORDER_RADIUS));
            setOpaque(true);

            iconLabel = new JLabel();
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

            textLabel = new JLabel("", SwingConstants.CENTER);
            textLabel.setForeground(Color.BLACK);

            selectBox = new JCheckBox();
            selectBox.setToolTipText("Select for batch unpark");
            selectBox.addActionListener(e -> {
                statusBar.setText(selectBox.isSelected() ? "Slot " + slotNumber + " selected"
                        : "Slot " + slotNumber + " unselected");
            });

            unparkButton = new JButton();
            unparkButton.setToolTipText("Unpark car");
            try {
                InputStream iconStream = getClass().getResourceAsStream("/resources/icons/unpark.png");
                if (iconStream != null) {
                    BufferedImage icon = ImageIO.read(iconStream);
                    unparkButton.setIcon(new ImageIcon(icon.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
                }
            } catch (IOException e) {
                Logger.log("Failed to load unpark icon: " + e.getMessage());
                unparkButton.setText("Unpark");
            }
            unparkButton.addActionListener(e -> {
                if (lot.getSlot(slotNumber).map(ParkingSlot::isOccupied).orElse(false)) {
                    int confirm = JOptionPane.showConfirmDialog(
                            SlotPanel.this,
                            "Unpark car from slot " + slotNumber + "?",
                            "Confirm Unpark",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        controller.unparkCar(slotNumber);
                        selectBox.setSelected(false);
                        statusBar.setText("Unparked car from slot " + slotNumber);
                    }
                }
            });

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(selectBox, BorderLayout.WEST);
            topPanel.add(unparkButton, BorderLayout.EAST);
            topPanel.setOpaque(false);

            add(iconLabel, BorderLayout.CENTER);
            add(textLabel, BorderLayout.SOUTH);
            add(topPanel, BorderLayout.NORTH);

            update(slot);
        }

        public void update(ParkingSlot slot) {
            if (slot == null) {
                setBackground(Color.GRAY);
                textLabel.setText("?");
                iconLabel.setIcon(null);
                unparkButton.setVisible(false);
                return;
            }
            if (slot.isOccupied()) {
                setBackground(new Color(255, 182, 193)); // Light red
                textLabel.setText(slot.getCar().getLicensePlate());
                try {
                    InputStream iconStream = getClass().getResourceAsStream("/resources/icons/car.png");
                    if (iconStream != null) {
                        BufferedImage icon = ImageIO.read(iconStream);
                        iconLabel.setIcon(new ImageIcon(icon.getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
                    }
                } catch (IOException e) {
                    Logger.log("Failed to load car icon: " + e.getMessage());
                    textLabel.setText("Car: " + slot.getCar().getLicensePlate());
                }
                unparkButton.setVisible(true);
            } else {
                setBackground(new Color(144, 238, 144)); // Light green
                textLabel.setText("Slot " + slot.getNumber());
                try {
                    InputStream iconStream = getClass().getResourceAsStream("/resources/icons/check.png");
                    if (iconStream != null) {
                        BufferedImage icon = ImageIO.read(iconStream);
                        iconLabel.setIcon(new ImageIcon(icon.getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
                    }
                } catch (IOException e) {
                    Logger.log("Failed to load check icon: " + e.getMessage());
                    textLabel.setText("Empty");
                }
                unparkButton.setVisible(false);
            }
        }

        public boolean isSelected() {
            return selectBox.isSelected();
        }

        public void setSelected(boolean selected) {
            selectBox.setSelected(selected);
        }

        public int getSlotNumber() {
            return slotNumber;
        }
    }

    private static class RoundedBorder implements Border {
        private final int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
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
}