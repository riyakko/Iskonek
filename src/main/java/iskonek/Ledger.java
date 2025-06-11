package iskonek;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class Ledger extends JFrame {
    private JLabel balanceAmount;
   
    private JFrame dashboard;

    public Ledger(String studentId, String studentName, String course, JFrame dashboard) {
        this.dashboard = dashboard;
        setTitle("Student Ledger");
        setSize(1260, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(102, 126, 234), 0, getHeight(), new Color(118, 75, 162));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        JLabel headerLabel = new JLabel("Student Ledger");
        headerLabel.setFont(new Font("Inter", Font.BOLD, 64));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBounds(44, 30, 600, 60);
        headerLabel.setBorder(null);
       
        backgroundPanel.add(headerLabel);

        // Back Button
        RoundedButton backButtonTop = new RoundedButton("BACK", 44);
        backButtonTop.setFont(new Font("Inter", Font.BOLD, 16));
        backButtonTop.setBackground(Color.WHITE);
        backButtonTop.setForeground(new Color(80, 80, 80));
        backButtonTop.setFocusPainted(false);
        backButtonTop.setBounds(1060, 30, 140, 44);
        backButtonTop.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButtonTop.addActionListener(e -> {
            dashboard.setVisible(true);
            dispose();
        });
        backgroundPanel.add(backButtonTop);

        // Main rounded panel
        DropShadowRoundedPanel mainPanel = new DropShadowRoundedPanel(30, new Color(240, 240, 240), 10, new Color(80, 80, 80, 60));
        mainPanel.setLayout(null);
        mainPanel.setBounds(20, 145, 1200, 580);
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        backgroundPanel.add(mainPanel);

        // ASSESSMENT tab 
        DropShadowRoundedTab assessmentTab = new DropShadowRoundedTab("ASSESSMENT");
        assessmentTab.setBounds(30, 113, 180, 48);
        backgroundPanel.add(assessmentTab);

        // Student Info
        JLabel nameLabel = new JLabel("Student Name: " + studentName);
        nameLabel.setFont(new Font("Inter", Font.BOLD, 32));
        nameLabel.setForeground(new Color(60, 60, 60));
        nameLabel.setBounds(60, 60, 700, 36);
        mainPanel.add(nameLabel);

        JLabel idLabel = new JLabel("Student ID: " + studentId);
        idLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        idLabel.setForeground(new Color(90, 90, 90));
        idLabel.setBounds(60, 120, 400, 28);
        mainPanel.add(idLabel);

        JLabel courseLabel = new JLabel("Course: " + course);
        courseLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        courseLabel.setForeground(new Color(90, 90, 90));
        courseLabel.setBounds(60, 170, 600, 28);
        mainPanel.add(courseLabel);

        // Assessment label
        JLabel assessmentLabel = new JLabel("Assessment:");
        assessmentLabel.setFont(new Font("Inter", Font.PLAIN, 18));
        assessmentLabel.setForeground(new Color(90, 90, 90));
        assessmentLabel.setBounds(60, 330, 200, 28);
        mainPanel.add(assessmentLabel);

        // Balance box
        DropShadowRoundedPanel balancePanel = new DropShadowRoundedPanel(30, Color.WHITE, 12, new Color(80, 80, 80, 60));
        balancePanel.setLayout(null);
        balancePanel.setBounds(50, 360, 1080, 170);
        mainPanel.add(balancePanel);

        JLabel balanceTitle = new JLabel("Current Balance:");
        balanceTitle.setFont(new Font("Inter", Font.PLAIN, 22));
        balanceTitle.setForeground(new Color(90, 90, 90));
        balanceTitle.setBounds(40, 18, 300, 32);
        balancePanel.add(balanceTitle);

        balanceAmount = new JLabel();
        balanceAmount.setFont(new Font("Inter", Font.BOLD, 72));
        balanceAmount.setForeground(new Color(102, 126, 234));
        balanceAmount.setBounds(30, 50, 1000, 80);
        balanceAmount.setHorizontalAlignment(SwingConstants.LEFT);
        balancePanel.add(balanceAmount);

        updateTuitionFee(course);

        setVisible(true);
    }

    // Drop shadow rounded panel for main and balance
    static class DropShadowRoundedPanel extends JPanel {
        private int cornerRadius;
        private Color bgColor;
        private int shadowSize;
        private Color shadowColor;
        public DropShadowRoundedPanel(int radius, Color bgColor, int shadowSize, Color shadowColor) {
            this.cornerRadius = radius;
            this.bgColor = bgColor;
            this.shadowSize = shadowSize;
            this.shadowColor = shadowColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Draw drop shadow
            g2.setColor(shadowColor);
            g2.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, cornerRadius, cornerRadius);
            // Draw panel
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - shadowSize, getHeight() - shadowSize, cornerRadius, cornerRadius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Drop shadow rounded tab for ASSESSMENT
    static class DropShadowRoundedTab extends JPanel {
        private String text;
        public DropShadowRoundedTab(String text) {
            this.text = text;
            setOpaque(false);
            setPreferredSize(new Dimension(180, 48));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(80, 80, 80, 60));
            g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 24, 24);

            g2.setColor(new Color(220, 220, 220));
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 24, 24);

            g2.setColor(new Color(120, 120, 120));
            g2.setFont(new Font("Inter", Font.BOLD, 18));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g2.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 4);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private void updateTuitionFee(String courseName) {
        String feeText;
        switch (courseName) {
            case "BS Information Technology":
                feeText = "₱31,000.00";
                break;
            case "BS Computer Science":
                feeText = "₱31,500.00";
                break;
            case "BS Computer Engineering":
                feeText = "₱32,000.00";
                break;
            case "BS Architecture":
                feeText = "₱34,600.00";
                break;
            case "BS Civil Engineering":
                feeText = "₱32,700.00";
                break;
            default:
                feeText = "N/A";
        }
        balanceAmount.setText(feeText);
    }

    private String getFullCourseName(String code) {
        switch (code) {
            case "BSIT-MWA":
                return "Bachelor of Science in Information Technology with Specialization<br>in Mobile and Web Applications";
            case "BSCS":
                return "Bachelor of Science in Computer Science";
            case "BS Accountancy":
                return "Bachelor of Science in Accountancy";
            default:
                return code;
        }
    }

    // Custom rounded button 
    static class RoundedButton extends JButton {
        private int arc;
        public RoundedButton(String text, int arc) {
            super(text);
            this.arc = arc;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Background
            if (getModel().isArmed()) {
                g2.setColor(new Color(230, 230, 230));
            } else {
                g2.setColor(getBackground());
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            // Border
            g2.setColor(new Color(180, 180, 180));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
            // Text
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(getText());
            int textHeight = fm.getAscent();
            g2.setColor(getForeground());
            g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 4);
            g2.dispose();
        }
    }
}