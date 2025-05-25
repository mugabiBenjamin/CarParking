package view;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedBorder implements Border {
    private final int radius;
    private final int thickness;

    public RoundedBorder(int radius, int thickness) {
        this.radius = radius;
        this.thickness = thickness;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c.getBackground().darker());
        g2.setStroke(new BasicStroke(thickness));
        g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(5, 10, 5, 10); // Match input field and button padding
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}