package presentation.swing.components;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.Border;

public final class RoundedBorder implements Border {
    private static final int DEFAULT_THICKNESS = 1;
    private static final int DEFAULT_HORIZONTAL_PADDING = 10;
    private static final int DEFAULT_VERTICAL_PADDING = 5;

    private final int radius;
    private final int thickness;
    private final Insets insets;

    public RoundedBorder(int radius) {
        this(radius, DEFAULT_THICKNESS);
    }

    public RoundedBorder(int radius, int thickness) {
        this.radius = normalizePositiveValue(radius, 8);
        this.thickness = normalizePositiveValue(thickness, DEFAULT_THICKNESS);
        this.insets = new Insets(
                DEFAULT_VERTICAL_PADDING,
                DEFAULT_HORIZONTAL_PADDING,
                DEFAULT_VERTICAL_PADDING,
                DEFAULT_HORIZONTAL_PADDING
        );
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
        if (component == null || graphics == null || width <= 0 || height <= 0) {
            return;
        }

        Graphics2D graphics2D = (Graphics2D) graphics.create();

        try {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (component.getBackground() != null) {
                graphics2D.setColor(component.getBackground().darker());
            }

            graphics2D.setStroke(new BasicStroke(thickness));
            graphics2D.draw(new RoundRectangle2D.Double(
                    x,
                    y,
                    width - thickness,
                    height - thickness,
                    radius,
                    radius
            ));
        } finally {
            graphics2D.dispose();
        }
    }

    @Override
    public Insets getBorderInsets(Component component) {
        return new Insets(insets.top, insets.left, insets.bottom, insets.right);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    private int normalizePositiveValue(int value, int fallback) {
        return value <= 0 ? fallback : value;
    }
}