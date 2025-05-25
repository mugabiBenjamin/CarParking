package util;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class IconUtil {
    public static ImageIcon createCarIcon(int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(IconUtil.class.getResource("/resources/icons/car.png"));
            return resizeImage(originalImage, width, height);
        } catch (IOException e) {
            System.err.println("Failed to load car PNG icon: " + e.getMessage());
            return createFallbackCarIcon(width, height);
        }
    }

    public static ImageIcon createSearchIcon(int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(IconUtil.class.getResource("/resources/icons/search.png"));
            return resizeImage(originalImage, width, height);
        } catch (IOException e) {
            System.err.println("Failed to load search PNG icon: " + e.getMessage());
            return createFallbackSearchIcon(width, height);
        }
    }

    public static ImageIcon createHelpIcon(int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(IconUtil.class.getResource("/resources/icons/help.png"));
            return resizeImage(originalImage, width, height);
        } catch (IOException e) {
            System.err.println("Failed to load help PNG icon: " + e.getMessage());
            return createFallbackHelpIcon(width, height);
        }
    }

    public static ImageIcon createCheckIcon(int width, int height, String type) {
        String iconPath = type.equals("validation") ? "/resources/icons/check-green.png" : "/resources/icons/check.png";
        try {
            BufferedImage originalImage = ImageIO.read(IconUtil.class.getResource(iconPath));
            return resizeImage(originalImage, width, height);
        } catch (IOException e) {
            System.err.println("Failed to load " + (type.equals("validation") ? "check-green" : "check") + " PNG icon: "
                    + e.getMessage());
            return createFallbackCheckIcon(width, height);
        }
    }

    public static ImageIcon createXIcon(int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(IconUtil.class.getResource("/resources/icons/x.png"));
            return resizeImage(originalImage, width, height);
        } catch (IOException e) {
            System.err.println("Failed to load x PNG icon: " + e.getMessage());
            return createFallbackXIcon(width, height);
        }
    }

    private static ImageIcon resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(originalImage, 0, 0, width, height, null);
        g2.dispose();
        return new ImageIcon(resizedImage);
    }

    private static ImageIcon createFallbackCarIcon(int width, int height) {
        BufferedImage fallback = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = fallback.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        int scale = Math.min(width / 24, height / 16);
        g2.fillRect(4 * scale, 4 * scale, 16 * scale, 8 * scale);
        g2.fillOval(6 * scale, 10 * scale, 4 * scale, 4 * scale);
        g2.fillOval(14 * scale, 10 * scale, 4 * scale, 4 * scale);
        g2.fillRect(8 * scale, 2 * scale, 8 * scale, 4 * scale);
        g2.dispose();
        return new ImageIcon(fallback);
    }

    private static ImageIcon createFallbackSearchIcon(int width, int height) {
        BufferedImage fallback = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = fallback.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        int scale = Math.min(width / 16, height / 16);
        g2.setStroke(new BasicStroke(2 * scale));
        g2.drawOval(4 * scale, 4 * scale, 8 * scale, 8 * scale);
        g2.drawLine(10 * scale, 10 * scale, 12 * scale, 12 * scale);
        g2.dispose();
        return new ImageIcon(fallback);
    }

    private static ImageIcon createFallbackHelpIcon(int width, int height) {
        BufferedImage fallback = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = fallback.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        int scale = Math.min(width / 16, height / 16);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12 * scale));
        g2.drawString("?", 6 * scale, 12 * scale);
        g2.dispose();
        return new ImageIcon(fallback);
    }

    private static ImageIcon createFallbackCheckIcon(int width, int height) {
        BufferedImage fallback = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = fallback.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.GREEN);
        int scale = Math.min(width / 16, height / 16);
        g2.setStroke(new BasicStroke(2 * scale));
        g2.drawLine(4 * scale, 8 * scale, 7 * scale, 11 * scale);
        g2.drawLine(7 * scale, 11 * scale, 12 * scale, 5 * scale);
        g2.dispose();
        return new ImageIcon(fallback);
    }

    private static ImageIcon createFallbackXIcon(int width, int height) {
        BufferedImage fallback = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = fallback.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.RED);
        int scale = Math.min(width / 16, height / 16);
        g2.setStroke(new BasicStroke(2 * scale));
        g2.drawLine(4 * scale, 4 * scale, 12 * scale, 12 * scale);
        g2.drawLine(4 * scale, 12 * scale, 12 * scale, 4 * scale);
        g2.dispose();
        return new ImageIcon(fallback);
    }
}