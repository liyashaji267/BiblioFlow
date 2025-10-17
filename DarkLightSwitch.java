import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// This class is a reusable toggle switch with theme application
public class DarkLightSwitch {

    private boolean darkMode = false;
    private JPanel togglePanel;
    private JPanel targetPanel;  // The panel to apply theme on

    public DarkLightSwitch(JPanel targetPanel) {
        this.targetPanel = targetPanel;
        togglePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                g2.setColor(darkMode ? Color.GREEN : Color.LIGHT_GRAY);
                g2.fillRoundRect(0, 0, width, height, height, height);

                g2.setColor(Color.WHITE);
                int knobX = darkMode ? width - height : 0;
                g2.fillOval(knobX, 0, height, height);

                g2.dispose();
            }
        };

        togglePanel.setPreferredSize(new Dimension(50, 25));
        togglePanel.setOpaque(false);
        togglePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        togglePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                darkMode = !darkMode;
                togglePanel.repaint();
                applyTheme();  // directly apply theme to targetPanel
            }
        });
    }

    // Returns the JPanel containing the toggle switch
    public JPanel getTogglePanel() {
        return togglePanel;
    }

    // Apply theme to the target panel and all its children
    public void applyTheme() {
        Color bg = darkMode ? Color.DARK_GRAY : Color.WHITE;
        Color fg = darkMode ? Color.WHITE : Color.BLACK;

        targetPanel.setBackground(bg);
        applyThemeToComponent(targetPanel, bg, fg);
        targetPanel.repaint();
    }

public void applyTableTheme(JTable table, boolean darkMode) {
    Color bg = darkMode ? Color.DARK_GRAY : Color.WHITE;
    Color fg = darkMode ? Color.WHITE : Color.BLACK;
    Color grid = darkMode ? Color.LIGHT_GRAY : Color.GRAY;

    table.setBackground(bg);
    table.setForeground(fg);
    table.setGridColor(grid);
    table.setSelectionBackground(darkMode ? Color.GRAY : Color.LIGHT_GRAY);
    table.setSelectionForeground(fg);

    // Update table header
    JTableHeader header = table.getTableHeader();
    header.setBackground(darkMode ? Color.BLACK : Color.LIGHT_GRAY);
    header.setForeground(darkMode ? Color.WHITE : Color.BLACK);

    // Set a default cell renderer for all columns
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    renderer.setBackground(bg);
    renderer.setForeground(fg);

    for (int i = 0; i < table.getColumnCount(); i++) {
        table.getColumnModel().getColumn(i).setCellRenderer(renderer);
    }

    table.repaint();
}



    public void applyThemeToComponent(Component comp, Color bg, Color fg) {
        if (comp instanceof JPanel) {
            comp.setBackground(bg);
            for (Component child : ((JPanel) comp).getComponents()) {
                applyThemeToComponent(child, bg, fg);
            }
        } else if (comp instanceof JLabel || comp instanceof JButton || comp instanceof JTextField) {
            comp.setForeground(fg);
        }
        else if (comp instanceof JTable) {
        applyTableTheme((JTable) comp, darkMode);
}

    }

    // Optional: get current dark mode state
    public boolean isDarkMode() {
        return darkMode;
    }
}
