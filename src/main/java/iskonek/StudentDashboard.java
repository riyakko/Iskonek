package iskonek;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
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

        // Main panel 
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(0, 0, new Color(102, 126, 234), 
                                                         0, getHeight(), new Color(118, 75, 162));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        setResizable(false);

        JLabel welcomeLabel = new JLabel("Welcome, ");
        welcomeLabel.setFont(new Font("Inter", Font.BOLD, 48));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(34, 10, 300, 80);
        contentPane.add(welcomeLabel);

        JLabel nameLabel = new JLabel(fullName + "!");
        nameLabel.setFont(new Font("Inter", Font.BOLD, 48));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(280, 10, 800, 80);
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

        // Profile Button
        RoundedButton btnProfile = new RoundedButton("");
        btnProfile.setIcon(profileIcon);
        styleIconOnlyButton(btnProfile);
        btnProfile.setBounds(34, 36, 204, 186); // Extra padding
        btnProfile.setToolTipText("My Profile");
        btnProfile.setFont(new Font("Inter", Font.BOLD, 16));
        btnProfile.setBackground(new Color(102, 126, 234));
        btnProfile.setForeground(Color.BLACK);
        btnProfile.addActionListener(event -> {
            StudentInformation infoWindow = new StudentInformation(studentId);
            infoWindow.setVisible(true);
            this.dispose();
        });
        panel.add(btnProfile);

        // Schedule Button
        RoundedButton btnSchedule = new RoundedButton("");
        btnSchedule.setIcon(scheduleIcon);
        styleIconTextButton(btnSchedule);
        btnSchedule.setBounds(322, 36, 204, 186);
        btnSchedule.setFont(new Font("Inter", Font.BOLD, 16));
        btnSchedule.setBackground(new Color(102, 126, 234));
        btnSchedule.setForeground(Color.BLACK);
        btnSchedule.addActionListener(e -> {
            ScheduleViewer scheduleViewer = new ScheduleViewer(studentId);
            scheduleViewer.setVisible(true);
            this.dispose();
        });
        panel.add(btnSchedule);

        // Student Ledger Button 
        RoundedButton btnLedger = new RoundedButton("");
        btnLedger.setIcon(ledgerIcon);
        styleIconTextButton(btnLedger);
        btnLedger.setBounds(596, 36, 204, 186);
        btnLedger.setFont(new Font("Inter", Font.BOLD, 16));
        btnLedger.setBackground(new Color(102, 126, 234));
        btnLedger.setForeground(Color.BLACK);
        btnLedger.addActionListener(e -> {
            String course = getStudentCourse(studentId);
            new Ledger(studentId, fullName, course, this).setVisible(true);
            this.dispose();
        });
        panel.add(btnLedger);

        // Log Out Button 
        RoundedButton btnLogout = new RoundedButton("Log Out");
        btnLogout.setFont(new Font("Inter", Font.BOLD, 16));
        btnLogout.setBackground(new Color(102, 126, 234));
        btnLogout.setForeground(Color.BLACK);
        btnLogout.setBounds(1080, 30, 120, 40);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            new IskonekLogin().setVisible(true);
            dispose();
        });
        contentPane.add(btnLogout);
    }

    private void initializeDatabase() {
        try {
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

    private String getStudentCourse(String studentId) {
        String course = "";
        try {
            String query = "SELECT student_course FROM students WHERE student_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                course = rs.getString("student_course");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return course;
    }

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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String studentId = "2025-1000";

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
        button.setFont(new Font("Inter", Font.BOLD, 16));
        button.setForeground(Color.BLACK);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        
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
            setFont(new Font("Inter", Font.BOLD, 16));
            setBackground(new Color(102, 126, 234));
            setForeground(Color.BLACK);
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