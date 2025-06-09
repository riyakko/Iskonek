package iskonek;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;


public class StudentDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private String fullName;
    private Connection connection;
    private String studentId;

    public StudentDashboard(String studentId) {
    try {
        this.studentId = studentId;
        initializeDatabase();
        this.fullName = getStudentFullName(studentId);
        initializeUI();
        setVisible(true); 
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading dashboard: " + e.getMessage());
    }
}
    // Initialize the UI components
    private void initializeUI() {
        setTitle("Student Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1260, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(100, 104, 158));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        setResizable(false);

        JLabel welcomeLabel = new JLabel("Welcome, ");
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Merich-YqW6q.otf")).deriveFont(Font.PLAIN, 52f);
            welcomeLabel.setFont(customFont);
        } catch (Exception e) {
            welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        }

        URL location = getClass().getResource("/Merich-YqW6q.otf");
        if (location == null) {
            System.out.println("Resource NOT found!");
        } else {
            System.out.println("Resource found at: " + location.toExternalForm());
        }

        welcomeLabel.setBounds(34, 10, 300, 80);
        contentPane.add(welcomeLabel);

        JLabel nameLabel = new JLabel(fullName + "!");
        nameLabel.setBackground(new Color(221, 160, 221));
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 52));
        nameLabel.setBounds(320, 10, 800, 80);
        contentPane.add(nameLabel);

        RoundedPanel panel = new RoundedPanel();
        panel.setBackground(new Color(230, 230, 250));
        panel.setBounds(34, 120, 1173, 500);
        panel.setLayout(null);
        contentPane.add(panel);

        // Load and scale icons safely
        ImageIcon profileIcon = new ImageIcon(getClass().getResource("/profile.png"));
        Image profileImg = profileIcon.getImage().getScaledInstance(204, 186, Image.SCALE_SMOOTH);
        profileIcon = new ImageIcon(profileImg);

        ImageIcon scheduleIcon = new ImageIcon(getClass().getResource("/sched.png"));
        Image scheduleImg = scheduleIcon.getImage().getScaledInstance(204, 186, Image.SCALE_SMOOTH);
        scheduleIcon = new ImageIcon(scheduleImg);

        ImageIcon ledgerIcon = new ImageIcon(getClass().getResource("/ledger.png"));
        Image ledgerImg = ledgerIcon.getImage().getScaledInstance(204, 186, Image.SCALE_SMOOTH);
        ledgerIcon = new ImageIcon(ledgerImg);

        // Profile Button (icon only)
        RoundedButton btnProfile = new RoundedButton("");
        btnProfile.setIcon(profileIcon);
        styleIconOnlyButton(btnProfile);
        btnProfile.setBounds(34, 36, 204, 186); // Extra padding
        btnProfile.setToolTipText("My Profile");
        btnProfile.addActionListener(event -> {
            StudentInformation infoWindow = new StudentInformation(studentId);
            infoWindow.setVisible(true);
            // dispose();
        });
        panel.add(btnProfile);

        // Schedule Button (icon + text)
        RoundedButton btnSchedule = new RoundedButton("");
        btnSchedule.setIcon(scheduleIcon);
        styleIconTextButton(btnSchedule);
        btnSchedule.setBounds(322, 36, 204, 186);
        btnSchedule.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Schedule feature is not implemented yet.");
        });
        panel.add(btnSchedule);

        // Student Ledger Button (icon + text)
        RoundedButton btnLedger = new RoundedButton("");
        btnLedger.setIcon(ledgerIcon);
        styleIconTextButton(btnLedger);
        btnLedger.setBounds(596, 36, 204, 186);
        btnLedger.addActionListener(e -> {
            // new Ledger(studentName).setVisible(true);
            // dispose();
        });
        panel.add(btnLedger);
    }

    // Initialize database connection and create table if needed
    private void initializeDatabase() {
        try {
            // Assuming SQLite database - adjust connection string as needed
            connection = DriverManager.getConnection("jdbc:sqlite:iskonek.db");
        
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    private String getStudentFullName(String studentId) {
        String query = "SELECT first_name, last_name FROM students WHERE student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                fullName = firstName + " " + lastName;
            } else {
                System.err.println("Student not found with ID: " + studentId);
            }
            
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving student data: " + e.getMessage());
        }
        
        return fullName;
    }

    // Clean up database connection when window is closed
    @Override
    public void dispose() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.dispose();
    }

    // Main method for testing/running the dashboard independently
    public static void main(String[] args) {
        // Set look and feel to system default for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String studentId = "2025-1000";

        // Run on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                StudentDashboard dashboard = new StudentDashboard(studentId);
                dashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exits app on close
                dashboard.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting Student Dashboard: " + e.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }


    // Style for icon-only buttons
    private void styleIconOnlyButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBorderPainted(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBorderPainted(false);
            }
        });
    }

    // Style for buttons with icon + text
    private void styleIconTextButton(JButton button) {
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBorderPainted(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBorderPainted(false);
            }
        });
    }

    // Rounded Panel class
    class RoundedPanel extends JPanel {
        public RoundedPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
        }
    }

    // Rounded Button class
    class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw rounded background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

            // Draw button contents (icon and text)
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            if (isBorderPainted()) {
                g.setColor(getForeground());
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            }
        }
    }
}