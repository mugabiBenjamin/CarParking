package presentation.swing.components;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class HoverButtonFactory {
    private static final int DEFAULT_RADIUS = 8;
    private static final int NORMAL_BORDER_THICKNESS = 1;
    private static final int HOVER_BORDER_THICKNESS = 2;

    private HoverButtonFactory() {
        throw new UnsupportedOperationException("HoverButtonFactory class cannot be instantiated");
    }

    public static JButton createButton(String text, Icon icon, String tooltip, Runnable action) {
        JButton button = new JButton(normalize(text, "Button"), icon);

        button.setBorder(new RoundedBorder(DEFAULT_RADIUS, NORMAL_BORDER_THICKNESS));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setMargin(new Insets(5, 10, 5, 10));
        button.setIconTextGap(4);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setToolTipText(normalize(tooltip, ""));
        button.addMouseListener(createHoverEffect(button));

        if (action != null) {
            button.addActionListener(event -> action.run());
        }

        return button;
    }

    private static MouseAdapter createHoverEffect(JButton button) {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                button.setBorder(new RoundedBorder(DEFAULT_RADIUS, HOVER_BORDER_THICKNESS));

                if (button.getBackground() != null) {
                    button.setBackground(button.getBackground().darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                button.setBorder(new RoundedBorder(DEFAULT_RADIUS, NORMAL_BORDER_THICKNESS));
                button.setBackground(UIManager.getColor("Button.background"));
            }
        };
    }

    private static String normalize(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }
}