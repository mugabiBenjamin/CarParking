package util;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class IconUtil {
    public static ImageIcon createCarIcon(int width, int height) {
        return loadIcon("/resources/icons/car.png", width, height);
    }

    public static ImageIcon createSearchIcon(int width, int height) {
        return loadIcon("/resources/icons/search.png", width, height);
    }

    public static ImageIcon createUnparkIcon(int width, int height) {
        return loadIcon("/resources/icons/unpark.png", width, height);
    }

    public static ImageIcon createCheckIcon(int width, int height, String type) {
        if ("slot".equals(type)) {
            return loadIcon("/resources/icons/check.png", width, height);
        }
        return loadIcon("/resources/icons/check-green.png", width, height);
    }

    public static ImageIcon createXIcon(int width, int height) {
        return loadIcon("/resources/icons/x.png", width, height);
    }

    public static ImageIcon createReportIcon(int width, int height) {
        return loadIcon("/resources/icons/report.png", width, height);
    }

    public static ImageIcon createHelpIcon(int width, int height) {
        return loadIcon("/resources/icons/help.png", width, height);
    }

    private static ImageIcon loadIcon(String path, int width, int height) {
        try (InputStream stream = IconUtil.class.getResourceAsStream(path)) {
            if (stream == null) {
                Logger.error("Icon resource not found: " + path);
                return null;
            }
            ImageIcon icon = new ImageIcon(stream.readAllBytes());
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            Logger.error("Failed to load icon " + path + ": " + e.getMessage());
            return null;
        }
    }
}