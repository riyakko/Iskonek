package iskonek;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class EnrollForm extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:iskonek.db"; 
    Connection conn = SQLiteConnector.gConnection();
    ResultSet rs = null;
    PreparedStatement pst = null;

    private JTextField firstNameField;
    private JTextField middleNameField;
    private JTextField lastNameField;
    private JTextField dobField;
    private JComboBox<String> genderBox;
    private JComboBox<String> civilStatusBox;
    private JTextField nationalityField;
    private JTextField contactField;
    private JTextField addressField;
    private JTextField guardianField;
    private JTextField guardianContactField;
    private JComboBox<String> courseBox;
    private JButton showFullListButton;
    private JDialog fullListDialog;
    private JPanel schedulePanel;
    private Map<String, JCheckBox> courseCheckBoxes = new HashMap<>();
    private JScrollPane scheduleScrollPane;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private String generatedStudentId;
    private String generatedStudentEmail;
    private JList<String> fullList;

    public EnrollForm() {
        initializeDatabase();
        initializeUI();
    }

    private void initializeDatabase() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            createTables(conn);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private static void createTables(Connection connection) {
        try {
            Statement stmt = connection.createStatement();
            String createStudentsTable =
                "CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id TEXT UNIQUE NOT NULL," +
                "first_name TEXT NOT NULL," +
                "middle_name TEXT," +
                "last_name TEXT NOT NULL," +
                "date_of_birth DATE NOT NULL," +
                "gender TEXT NOT NULL," +
                "civil_status TEXT NOT NULL," +
                "nationality TEXT NOT NULL," +
                "contact_number TEXT NOT NULL," +
                "address TEXT NOT NULL," +
                "guardian_name TEXT," +
                "guardian_contact TEXT," +
                "password TEXT NOT NULL," +
                "enrollment_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "student_email TEXT UNIQUE NOT NULL," +
                "student_course TEXT NOT NULL," +
                "student_schedule TEXT NOT NULL" +
                ")";
            stmt.execute(createStudentsTable);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
private void initializeUI() {
        setTitle("ISKOnek - Student Enrollment");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1260, 780);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel 
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background (matching login form style)
                GradientPaint gradient = new GradientPaint(0, 0, new Color(102, 126, 234), 
                                                         0, getHeight(), new Color(118, 75, 162));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Scroll pane for the form
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
        
        setContentPane(mainPanel);
        revalidate();
        repaint();

        System.out.println("UI initialized and painted");
    }

    private JPanel createFormPanel() {
        JPanel whitePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 245)); // Match login form transparency
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40); // Match login form radius
            }
        };
        whitePanel.setOpaque(false);
        whitePanel.setLayout(new GridBagLayout());
        whitePanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Title 
        JLabel titleLabel = new JLabel("ISKOnek Enrollment") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(getText(), x + 2, y + 2);
                
                g2d.setColor(new Color(102, 126, 234)); // Match login form color
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        titleLabel.setFont(new Font("Inter", Font.BOLD, 48)); // Match login form font
        titleLabel.setOpaque(false);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 30, 10);
        whitePanel.add(titleLabel, gbc);

        // Initialize form fields
        firstNameField = new RoundedTextField();
        middleNameField = new RoundedTextField();
        lastNameField = new RoundedTextField();
        dobField = new RoundedTextField();
        dobField.setText("YYYY-MM-DD");
        dobField.setForeground(Color.GRAY);
        
        genderBox = new RoundedComboBox(new String[]{"Male", "Female", "Other"});
        civilStatusBox = new RoundedComboBox(new String[]{"Single", "Married", "Widowed", "Separated", "Divorced"});
        
        nationalityField = new RoundedTextField();
        contactField = new RoundedTextField();
        addressField = new RoundedTextField();
        guardianField = new RoundedTextField();
        guardianContactField = new RoundedTextField();
        passwordField = new RoundedPasswordField();
        confirmPasswordField = new RoundedPasswordField();

        addPlaceholderBehavior(dobField, "YYYY-MM-DD");
        
        Dimension fieldSize = new Dimension(300, 35);
        firstNameField.setPreferredSize(fieldSize);
        middleNameField.setPreferredSize(fieldSize);
        lastNameField.setPreferredSize(fieldSize);
        dobField.setPreferredSize(fieldSize);
        genderBox.setPreferredSize(fieldSize);
        civilStatusBox.setPreferredSize(fieldSize);
        nationalityField.setPreferredSize(fieldSize);
        contactField.setPreferredSize(fieldSize);
        addressField.setPreferredSize(fieldSize);
        guardianField.setPreferredSize(fieldSize);
        guardianContactField.setPreferredSize(fieldSize);
        passwordField.setPreferredSize(fieldSize);
        confirmPasswordField.setPreferredSize(fieldSize);

        // Reset insets and grid settings for form fields
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.EAST;

        int row = 1;
        
        // First Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(firstNameField, gbc);

        // Middle Name
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel middleNameLabel = new JLabel("Middle Name:");
        middleNameLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(middleNameLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(middleNameField, gbc);

        // Last Name
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(lastNameField, gbc);

        // Date of Birth
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD):");
        dobLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(dobLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(dobField, gbc);

        // Gender
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(genderLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(genderBox, gbc);

        // Nationality
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel nationalityLabel = new JLabel("Nationality:");
        nationalityLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(nationalityLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(nationalityField, gbc);

        // Civil Status
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel civilStatusLabel = new JLabel("Civil Status:");
        civilStatusLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(civilStatusLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(civilStatusBox, gbc);

        // Address
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel addressFieldLabel = new JLabel("Address:");
        addressFieldLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(addressFieldLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(addressField, gbc);

        // Contact No.
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel contactLabel = new JLabel("Contact No.:");
        contactLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(contactLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(contactField, gbc);

        // Guardian Name
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel guardianNameLabel = new JLabel("Guardian Name:");
        guardianNameLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(guardianNameLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(guardianField, gbc);

        // Guardian Contact
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel guardianContactLabel = new JLabel("Guardian Contact:");
        guardianContactLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(guardianContactLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(guardianContactField, gbc);

        // Course Selection
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(courseLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        String[] courses = {"BS Computer Science", "BS Information Technology", "BS Computer Engineering", "BS Architecture", "BS Civil Engineering"};
        courseBox = new RoundedComboBox(courses);
        courseBox.setPreferredSize(new Dimension(300, 35));
        whitePanel.add(courseBox, gbc);

        // Schedule Selection
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel scheduleLabel = new JLabel("Schedule Selection:");
        scheduleLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(scheduleLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
    
        schedulePanel = new JPanel();
        schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
        schedulePanel.setBackground(new Color(230, 230, 230));
        schedulePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        scheduleScrollPane = new JScrollPane(schedulePanel);
        scheduleScrollPane.setPreferredSize(new Dimension(300, 200));
        scheduleScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(scheduleScrollPane, gbc);

        courseBox.addActionListener(e -> updateSchedules());
        updateSchedules();

        // Create "Show Full List" button
        showFullListButton = new RoundedButton("Show Full List");
        showFullListButton.setFont(new Font("Inter", Font.PLAIN, 12));
        showFullListButton.setBackground(new Color(102, 126, 234));
        showFullListButton.setForeground(Color.WHITE);
        showFullListButton.setFocusPainted(false);
        showFullListButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        showFullListButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showFullListButton.addActionListener(e -> showFullScheduleList());

        row++;
        gbc.gridy = row;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 0, 0, 0); 
        whitePanel.add(showFullListButton, gbc);

        // Password with toggle button
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(passwordLabel, gbc);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        // Create toggle button for password
        JButton passwordToggle = new JButton("👁");
        passwordToggle.setPreferredSize(new Dimension(30, passwordField.getPreferredSize().height));
        passwordToggle.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordToggle.setMargin(new Insets(0, 0, 0, 0));
        passwordToggle.addActionListener(e -> {
            if (passwordField.getEchoChar() == 0) {
                passwordField.setEchoChar('•');
                passwordToggle.setText("👁");
            } else {
                passwordField.setEchoChar((char) 0);
                passwordToggle.setText("🙈");
            }
        });
        passwordPanel.add(passwordToggle, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(passwordPanel, gbc);

        // Confirm Password with toggle button
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        whitePanel.add(confirmPasswordLabel, gbc);

        JPanel confirmPasswordPanel = new JPanel(new BorderLayout());
        confirmPasswordPanel.add(confirmPasswordField, BorderLayout.CENTER);

        // Create toggle button for confirm password
        JButton confirmPasswordToggle = new JButton("👁");
        confirmPasswordToggle.setPreferredSize(new Dimension(30, confirmPasswordField.getPreferredSize().height));
        confirmPasswordToggle.setFont(new Font("Arial", Font.PLAIN, 12));
        confirmPasswordToggle.setMargin(new Insets(0, 0, 0, 0));
        confirmPasswordToggle.addActionListener(e -> {
            if (confirmPasswordField.getEchoChar() == 0) {
                confirmPasswordField.setEchoChar('•');
                confirmPasswordToggle.setText("👁");
            } else {
                confirmPasswordField.setEchoChar((char) 0);
                confirmPasswordToggle.setText("🙈");
            }
        });
        confirmPasswordPanel.add(confirmPasswordToggle, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        whitePanel.add(confirmPasswordPanel, gbc);

        // Button panel 
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        
        // Enroll button
        JButton enrollButton = new RoundedButton("Enroll");
        enrollButton.setPreferredSize(new Dimension(120, 40));
        enrollButton.addActionListener(new EnrollActionListener());

        
        // Back to Login button
        JButton backButton = new RoundedButton("Back to Login");
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> {
            this.dispose();
            if (IskonekLogin.class != null) {
                try {
                    IskonekLogin loginWindow = (IskonekLogin) IskonekLogin.class.getDeclaredConstructor().newInstance();
                    loginWindow.setVisible(true);
                    loginWindow.refreshDatabaseConnection();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    new IskonekLogin().setVisible(true);
                }
            } else {
                new IskonekLogin().setVisible(true);
            }
        });
        
        buttonPanel.add(enrollButton);
        buttonPanel.add(backButton);
        whitePanel.add(buttonPanel, gbc);

        // Create outer panel to center the white panel
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setOpaque(false);
        outerPanel.add(whitePanel);

        return outerPanel;
    }

    private void updateSchedules() {
        String selectedCourse = (String) courseBox.getSelectedItem();
        if (!courseCheckBoxes.isEmpty()) {
        schedulePanel.removeAll();
        courseCheckBoxes.clear();
        }
        if (selectedCourse != null) {
            schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
            switch (selectedCourse) {
                case "BS Computer Science":
                    addCourseCheckBox("COM241 - CCDATRCL: DATA STRUCTURES AND ALGORITHMS | TUE 12:00PM - 04:00PM ROOM 505, FRI 01:00PM - 03:40PM ROOM 431");
                    addCourseCheckBox("COM242  - CCDATRCL: DATA STRUCTURES AND ALGORITHMS | MON 12:00PM - 04:00PM ROOM 505, THU 01:00PM - 03:40PM ROOM 431");
                    addCourseCheckBox("COM241 - CCPLTFRL: PLATFORM TECHNOLOGIES | MON 07:00AM - 09:40AM ROOM 431, THU 07:00AM - 11:00AM ROOM 505");
                    addCourseCheckBox("COM242  - CCPLTFRL: PLATFORM TECHNOLOGIES | MON 01:00PM - 03:40PM ROOM 431, THU 12:00PM - 04:00PM ROOM 505");
                    addCourseCheckBox("COM241 - GEFID01X: WIKA AT PANITIKAN SA PAGPAPATIBAY NG PILIPINONG IDENTIDAD | TUE 09:00AM - 11:00AM VR408A, FRI 09:00AM - 11:00AM ROOM 407");
                    addCourseCheckBox("COM242  - GEFID01X: WIKA AT PANITIKAN SA PAGPAPATIBAY NG PILIPINONG IDENTIDAD | TUE 03:00PM - 05:00PM VR408C, FRI 03:00PM - 05:00PM ROOM 407");
                    addCourseCheckBox("COM241 - GEITE01X: LIVING IN THE I.T. ERA | TUE 05:00PM - 07:00PM VR408A, FRI 05:00PM - 07:00PM VR408A");
                    addCourseCheckBox("COM242  - GEITE01X: LIVING IN THE I.T. ERA | TUE 09:00AM - 11:00AM VR408B, FRI 09:00AM - 11:00AM VR408B");
                    addCourseCheckBox("COM241 - GERIZ01X: LIFE AND WORKS OF RIZAL | TUE 03:00PM - 05:00PM VR408A, FRI 03:00PM - 05:00PM VR408A");
                    addCourseCheckBox("COM242  - GERIZ01X: LIFE AND WORKS OF RIZAL | TUE 01:00PM - 03:00PM VR408C, FRI 01:00PM - 03:00PM VR408B");
                    addCourseCheckBox("COM241 - MCFIT04X: PATHFit 4 | MON 04:00PM - 06:40PM Gymnasium2");
                    addCourseCheckBox("COM242  - MCFIT04X: PATHFit 4 | THU 04:00PM - 06:40PM Gymnasium2");
                    break;
                case "BS Information Technology":
                    addCourseCheckBox("INF241 - CCDATRCL: DATA STRUCTURES AND ALGORITHMS | TUE 12:00PM - 04:00PM ROOM 505, FRI 01:00PM - 03:40PM ROOM 431");
                    addCourseCheckBox("INF242 - CCDATRCL: DATA STRUCTURES AND ALGORITHMS | MON 12:00PM - 04:00PM ROOM 505, THU 01:00PM - 03:40PM ROOM 431");
                    addCourseCheckBox("INF241 - CCPLTFRL: PLATFORM TECHNOLOGIES | MON 07:00AM - 09:40AM ROOM 431, THU 07:00AM - 11:00AM ROOM 505");
                    addCourseCheckBox("INF242 - CCPLTFRL: PLATFORM TECHNOLOGIES | MON 01:00PM - 03:40PM ROOM 431, THU 12:00PM - 04:00PM ROOM 505");
                    addCourseCheckBox("INF241 - GEFID01X: WIKA AT PANITIKAN SA PAGPAPATIBAY NG PILIPINONG IDENTIDAD | TUE 09:00AM - 11:00AM VR408A, FRI 09:00AM - 11:00AM ROOM 407");
                    addCourseCheckBox("INF242 - GEFID01X: WIKA AT PANITIKAN SA PAGPAPATIBAY NG PILIPINONG IDENTIDAD | TUE 03:00PM - 05:00PM VR408C, FRI 03:00PM - 05:00PM ROOM 407");
                    addCourseCheckBox("INF241 - GEITE01X: LIVING IN THE I.T. ERA | TUE 05:00PM - 07:00PM VR408A, FRI 05:00PM - 07:00PM VR408A");
                    addCourseCheckBox("INF242 - GEITE01X: LIVING IN THE I.T. ERA | TUE 09:00AM - 11:00AM VR408B, FRI 09:00AM - 11:00AM VR408B");
                    addCourseCheckBox("INF241 - GERIZ01X: LIFE AND WORKS OF RIZAL | TUE 03:00PM - 05:00PM VR408A, FRI 03:00PM - 05:00PM VR408A");
                    addCourseCheckBox("INF242 - GERIZ01X: LIFE AND WORKS OF RIZAL | TUE 01:00PM - 03:00PM VR408C, FRI 01:00PM - 03:00PM VR408B");
                    addCourseCheckBox("INF241 - MCFIT04X: PATHFit 4 | MON 04:00PM - 06:40PM Gymnasium2");
                    addCourseCheckBox("INF242 - MCFIT04X: PATHFit 4 | THU 04:00PM - 06:40PM Gymnasium2");
                    break;
                case "BS Computer Engineering":
                    addCourseCheckBox("CPE241 - CPORTN10: COMPUTER ENGINEERING ORIENTATION | MON 01:00PM - 02:20PM VR407M");
                    addCourseCheckBox("CPE242 - CPORTN10: COMPUTER ENGINEERING ORIENTATION | TUE 01:00PM - 02:20PM VR407M");
                    addCourseCheckBox("CPE241 - CPPROG2L: PROGRAMMING LOGIC AND DESIGN - LAB | TUE 11:00AM - 03:00PM ROOM 507, FRI 11:00AM - 03:00PM ROOM 507");
                    addCourseCheckBox("CPE242 - CPPROG2L: PROGRAMMING LOGIC AND DESIGN - LAB | WED 11:00AM - 03:00PM ROOM 507, SAT 11:00AM - 03:00PM ROOM 507");
                    addCourseCheckBox("CPE241 - ENITV12D: INTERVENTION FOR CALCULUS - DRAFTING | TUE 07:00AM - 11:00AM ROOM 425, FRI 07:00AM - 11:00AM ROOM 425");
                    addCourseCheckBox("CPE242 - ENITV12D: INTERVENTION FOR CALCULUS - DRAFTING | WED 07:00AM - 11:00AM ROOM 425, SAT 07:00AM - 11:00AM ROOM 425");
                    addCourseCheckBox("CPE241 - ENITV21D: INTERVENTION FOR CHEMISTRY - DRAFTING | SAT 07:00AM - 11:00AM Room 514");
                    addCourseCheckBox("CPE242 - ENITV21D: INTERVENTION FOR CHEMISTRY - DRAFTING | MON 07:00AM - 11:00AM Room 514");
                    addCourseCheckBox("CPE241 - ENITV31D: INTERVENTION FOR PHYSICS - DRAFTING | TUE 03:00PM - 07:00PM Room 517");
                    addCourseCheckBox("CPE242 - ENITV31D: INTERVENTION FOR PHYSICS - DRAFTING | WED 03:00PM - 07:00PM Room 517");
                    addCourseCheckBox("CPE241 - GEART01X: ART APPRECIATION | MON 09:00AM - 11:00AM VR410S, THU 09:00AM - 11:00AM VR407A");
                    addCourseCheckBox("CPE242 - GEART01X: ART APPRECIATION | TUE 09:00AM - 11:00AM VR410S, FRI 09:00AM - 11:00AM VR407A");
                    addCourseCheckBox("CPE241 - GEETH01X: ETHICS | MON 07:00AM - 09:00AM VR410S, THU 07:00AM - 09:00AM VR407A");
                    addCourseCheckBox("CPE242 - GEETH01X: ETHICS | TUE 07:00AM - 09:00AM VR410S, FRI 07:00AM - 09:00AM VR407A");
                    addCourseCheckBox("CPE241 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | WED 07:00AM - 11:00AM ROOM 406");
                    addCourseCheckBox("CPE242 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | THU 07:00AM - 11:00AM ROOM 406");
                    addCourseCheckBox("SECA-CPE241 - MCWTS01X: NATIONAL SERVICE TRAINING PROGRAM 1 | MON 03:00PM - 05:00PM VR405L, THU 03:00PM - 05:00PM VR407Z");
                    addCourseCheckBox("SECA-CPE242 - MCWTS01X: NATIONAL SERVICE TRAINING PROGRAM 1 | TUE 03:00PM - 05:00PM VR405L, FRI 03:00PM - 05:00PM VR407Z");
                    break;
                case "BS Architecture":
                    addCourseCheckBox("ARCH241 - AALGTRIG: COLLEGE ALGEBRA AND PLANE TRIGONOMETRY | TUE 11:00AM - 01:00PM ROOM 432, FRI 11:00AM - 01:00PM ROOM 432");
                    addCourseCheckBox("ARCH242 - AALGTRIG: COLLEGE ALGEBRA AND PLANE TRIGONOMETRY | WED 11:00AM - 01:00PM ROOM 432, SAT 11:00AM - 01:00PM ROOM 432");
                    addCourseCheckBox("ARCH241 - AGRAPN1S: ARCHITECTURAL VISUAL COMM 1: GRAPHICS 1 | MON 07:00AM - 11:40AM Room 524, THU 07:00AM - 11:40AM Room 524");
                    addCourseCheckBox("ARCH242 - AGRAPN1S: ARCHITECTURAL VISUAL COMM 1: GRAPHICS 1 | TUE 01:00PM - 05:20PM Room 524, FRI 01:00PM - 05:20PM Room 524");
                    addCourseCheckBox("ARCH241 - ATHEORY1: THEORY OF ARCHITECTURE 1 | TUE 07:00AM - 09:40AM ROOM 409");
                    addCourseCheckBox("ARCH242 - ATHEORY1: THEORY OF ARCHITECTURE 1 | WED 07:00AM - 09:40AM ROOM 409");
                    addCourseCheckBox("ARCH241 - AVSTEN1S: ARCHITECTURAL VISUAL COMM 2: VISUAL TECHNIQUES 1 | MON 12:20PM - 05:40PM Room 541");
                    addCourseCheckBox("ARCH242 - AVSTEN1S: ARCHITECTURAL VISUAL COMM 2: VISUAL TECHNIQUES 1 | TUE 12:20PM - 05:40PM Room 542");
                    addCourseCheckBox("ARCH241 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | TUE 01:00PM - 03:00PM ROOM 520, FRI 01:00PM - 03:00PM ROOM 520");
                    addCourseCheckBox("ARCH242 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | WED 01:00PM - 03:00PM ROOM 520, SAT 01:00PM - 03:00PM ROOM 520");
                    addCourseCheckBox("ARCH241 - GENAT01R: Nationalian Course | TUE 03:00PM - 05:00PM VR407R, FRI 03:00PM - 05:00PM VR405B");
                    addCourseCheckBox("ARCH242 - GENAT01R: Nationalian Course | WED 03:00PM - 05:00PM VR407R, SAT 03:00PM - 05:00PM VR405B");
                    addCourseCheckBox("ARCH241 - MNSTP01X: Civic Welfare Training Service 1 | WED 11:00AM - 03:00PM VR405B");
                    addCourseCheckBox("ARCH242 - MNSTP01X: Civic Welfare Training Service 1 | SAT 11:00AM - 03:00PM VR405B");
                    break;
                case "BS Civil Engineering":
                    addCourseCheckBox("BSCE241 - CEEDRP1D: ENGINEERING DRAWING AND PLANS | MON 07:00AM - 11:00AM Room 521");
                    addCourseCheckBox("BSCE242 - CEEDRP1D: ENGINEERING DRAWING AND PLANS | THU 07:00AM - 11:00AM Room 521");
                    addCourseCheckBox("BSCE241 - CEORTN20: CIVIL ENGINEERING ORIENTATION | MON 03:00PM - 05:40PM VR407A");
                    addCourseCheckBox("BSCE242 - CEORTN20: CIVIL ENGINEERING ORIENTATION | THU 12:20PM - 03:00PM VR405J");
                    addCourseCheckBox("BSCE241 - ENITV12D: INTERVENTION FOR CALCULUS - DRAFTING | MON 01:00PM - 03:00PM ROOM 420, WED 01:00PM - 05:00PM Room 517, THU 01:00PM - 03:00PM ROOM 420");
                    addCourseCheckBox("BSCE242 - ENITV12D: INTERVENTION FOR CALCULUS - DRAFTING | TUE 01:00PM - 03:00PM ROOM 420, FRI 01:00PM - 05:00PM Room 517, THU 03:00PM - 05:00PM ROOM 420");
                    addCourseCheckBox("BSCE241 - ENITV21D: INTERVENTION FOR CHEMISTRY - DRAFTING | THU 07:00AM - 11:00AM Room 514");
                    addCourseCheckBox("BSCE242 - ENITV21D: INTERVENTION FOR CHEMISTRY - DRAFTING | FRI 07:00AM - 11:00AM Room 514");
                    addCourseCheckBox("BSCE241 - GEETH01X: ETHICS | TUE 03:00PM - 05:00PM VR405C, FRI 03:00PM - 05:00PM VR405C");
                    addCourseCheckBox("BSCE242 - GEETH01X: ETHICS | WED 03:00PM - 05:00PM VR405C, SAT 03:00PM - 05:00PM VR405C");
                    addCourseCheckBox("BSCE241 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | TUE 01:00PM - 03:00PM ROOM 419, FRI 01:00PM - 03:00PM ROOM 419");
                    addCourseCheckBox("BSCE242 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | WED 01:00PM - 03:00PM ROOM 419, SAT 01:00PM - 03:00PM ROOM 419");
                    addCourseCheckBox("BSCE241 - GENAT01R: Nationalian Course | TUE 09:00AM - 11:00AM VR405B, FRI 09:00AM - 11:00AM VR405B");
                    addCourseCheckBox("BSCE242 - GENAT01R: Nationalian Course | WED 09:00AM - 11:00AM VR405B, SAT 09:00AM - 11:00AM VR405B");
                    addCourseCheckBox("BSCE241 - GESTS01X: SCIENCE, TECHNOLOGY AND SOCIETY | TUE 07:00AM - 09:00AM VR405C, FRI 07:00AM - 09:00AM VR405B");
                    addCourseCheckBox("BSCE242 - GESTS01X: SCIENCE, TECHNOLOGY AND SOCIETY | WED 07:00AM - 09:00AM VR405C, SAT 07:00AM - 09:00AM VR405B");
                    break;
            }
        } else {
            JLabel noCourseLabel = new JLabel("Select a course first");
            noCourseLabel.setFont(new Font("Inter", Font.PLAIN, 14));
            schedulePanel.add(noCourseLabel);
        }
        schedulePanel.revalidate();
        schedulePanel.repaint();
    }

    private boolean isDuplicateCourse(String newCourse) {
        try {
            String newCourseSubject = newCourse.split(" - ")[1].split(":")[0].trim();
            
            for (JCheckBox checkBox : courseCheckBoxes.values()) {
                if (checkBox.isSelected()) {
                    String existingCourse = checkBox.getText();
                    String existingSubject = existingCourse.split(" - ")[1].split(":")[0].trim();
                    
                    if (existingSubject.equals(newCourseSubject)) {
                        return true;
                    }
                }
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("Error checking for duplicate courses: " + e.getMessage());
            return false;
        }
    }

    private boolean hasOverlappingSchedules(String newSchedule, String existingSchedules) {
        try {
            if (existingSchedules.isEmpty()) return false;
            
            // Split the new schedule into individual time slots
            String[] newTimeSlots = newSchedule.split(", ");
            
            // Split existing schedules into individual entries
            String[] existingEntries = existingSchedules.split(", ");
            
            for (String newSlot : newTimeSlots) {
                for (String existingSlot : existingEntries) {
                    // Extract days and times
                    String[] newParts = newSlot.split(" ");
                    String[] existingParts = existingSlot.split(" ");
                    
                    if (newParts.length >= 3 && existingParts.length >= 3) {
                        String newDay = newParts[0];
                        String existingDay = existingParts[0];
                        
                        // Check if days overlap
                        if (newDay.equals(existingDay)) {
                            // Extract times
                            String newTime = newParts[1] + " " + newParts[2];
                            String existingTime = existingParts[1] + " " + existingParts[2];
                            
                            // Simple time overlap check 
                            if (newTime.equals(existingTime)) {
                                return true;
                            }
                        }
                    }
                }
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("Error checking for schedule overlaps: " + e.getMessage());
            return false;
        }
    }

    private void addCourseCheckBox(String course) {
        String courseCode = course.split(" - ")[0].trim();
        String courseSubject = course.split(" - ")[1].split(":")[0].trim();
        String schedule = course.split("\\|")[1].trim();
        
        JCheckBox checkBox = new JCheckBox(course);
        checkBox.setFont(new Font("Inter", Font.PLAIN, 14));
        checkBox.setBackground(new Color(230, 230, 230));
        checkBox.setFocusPainted(false);
        
        // Add action listener to handle schedule conflicts only
        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()) {
                // Check for schedule conflicts only
                for (Map.Entry<String, JCheckBox> entry : courseCheckBoxes.entrySet()) {
                    if (entry.getValue().isSelected() && !entry.getValue().equals(checkBox)) {
                        String existingCourse = entry.getValue().getText();
                        String existingSchedule = existingCourse.split("\\|")[1].trim();
                        String[] newSlots = schedule.split(", ");
                        String[] existingSlots = existingSchedule.split(", ");
                        
                        for (String newSlot : newSlots) {
                            for (String existingSlot : existingSlots) {
                                String[] newParts = newSlot.split(" ");
                                String[] existingParts = existingSlot.split(" ");
                                if (newParts.length >= 3 && existingParts.length >= 3) {
                                    if (newParts[0].equals(existingParts[0])) {
                                        String newTime = newParts[1] + " " + newParts[2];
                                        String existingTime = existingParts[1] + " " + existingParts[2];
                                        if (newTime.equals(existingTime)) {
                                            checkBox.setSelected(false);
                                            showError("Schedule conflict detected. This course overlaps with another selected course.");
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        
        // Store the checkbox with a unique key that includes both course code and subject
        String uniqueKey = courseCode + "_" + courseSubject;
        courseCheckBoxes.put(uniqueKey, checkBox);
        schedulePanel.add(checkBox);
    }

    // Method to show full schedule list
    private void showFullScheduleList() {
        if (fullListDialog == null) {
            createFullListDialog();
        }
        
        DefaultListModel<String> model = new DefaultListModel<>();
        
        String selectedProgram = (String) courseBox.getSelectedItem();
        if (selectedProgram == null) {
            model.addElement("Please select a program first");
        } else {
            model.addElement(selectedProgram + " Courses:");
            
            // Get all courses for the selected program
            List<String> allCourses = new ArrayList<>();
            switch (selectedProgram) {
                case "BS Computer Science":
                    allCourses.add("COM241 - CCDATRCL: DATA STRUCTURES AND ALGORITHMS | TUE 12:00PM - 04:00PM ROOM 505, FRI 01:00PM - 03:40PM ROOM 431");
                    allCourses.add("COM242 - CCDATRCL: DATA STRUCTURES AND ALGORITHMS | MON 12:00PM - 04:00PM ROOM 505, THU 01:00PM - 03:40PM ROOM 431");
                    allCourses.add("COM241 - CCPLTFRL: PLATFORM TECHNOLOGIES | MON 07:00AM - 09:40AM ROOM 431, THU 07:00AM - 11:00AM ROOM 505");
                    allCourses.add("COM242 - CCPLTFRL: PLATFORM TECHNOLOGIES | MON 01:00PM - 03:40PM ROOM 431, THU 12:00PM - 04:00PM ROOM 505");
                    allCourses.add("COM241 - GEFID01X: WIKA AT PANITIKAN SA PAGPAPATIBAY NG PILIPINONG IDENTIDAD | TUE 09:00AM - 11:00AM VR408A, FRI 09:00AM - 11:00AM ROOM 407");
                    allCourses.add("COM242 - GEFID01X: WIKA AT PANITIKAN SA PAGPAPATIBAY NG PILIPINONG IDENTIDAD | TUE 03:00PM - 05:00PM VR408C, FRI 03:00PM - 05:00PM ROOM 407");
                    allCourses.add("COM241 - GEITE01X: LIVING IN THE I.T. ERA | TUE 05:00PM - 07:00PM VR408A, FRI 05:00PM - 07:00PM VR408A");
                    allCourses.add("COM242 - GEITE01X: LIVING IN THE I.T. ERA | TUE 09:00AM - 11:00AM VR408B, FRI 09:00AM - 11:00AM VR408B");
                    allCourses.add("COM241 - GERIZ01X: LIFE AND WORKS OF RIZAL | TUE 03:00PM - 05:00PM VR408A, FRI 03:00PM - 05:00PM VR408A");
                    allCourses.add("COM242 - GERIZ01X: LIFE AND WORKS OF RIZAL | TUE 01:00PM - 03:00PM VR408C, FRI 01:00PM - 03:00PM VR408B");
                    allCourses.add("COM241 - MCFIT04X: PATHFit 4 | MON 04:00PM - 06:40PM Gymnasium2");
                    allCourses.add("COM242 - MCFIT04X: PATHFit 4 | THU 04:00PM - 06:40PM Gymnasium2");
                    break;
                case "BS Information Technology":
                    allCourses.add("INF241 - CCDATRCL: DATA STRUCTURES AND ALGORITHMS | TUE 12:00PM - 04:00PM ROOM 505, FRI 01:00PM - 03:40PM ROOM 431");
                    allCourses.add("INF242 - CCDATRCL: DATA STRUCTURES AND ALGORITHMS | MON 12:00PM - 04:00PM ROOM 505, THU 01:00PM - 03:40PM ROOM 431");
                    allCourses.add("INF241 - CCPLTFRL: PLATFORM TECHNOLOGIES | MON 07:00AM - 09:40AM ROOM 431, THU 07:00AM - 11:00AM ROOM 505");
                    allCourses.add("INF242 - CCPLTFRL: PLATFORM TECHNOLOGIES | MON 01:00PM - 03:40PM ROOM 431, THU 12:00PM - 04:00PM ROOM 505");
                    allCourses.add("INF241 - GEFID01X: WIKA AT PANITIKAN SA PAGPAPATIBAY NG PILIPINONG IDENTIDAD | TUE 09:00AM - 11:00AM VR408A, FRI 09:00AM - 11:00AM ROOM 407");
                    allCourses.add("INF242 - GEFID01X: WIKA AT PANITIKAN SA PAGPAPATIBAY NG PILIPINONG IDENTIDAD | TUE 03:00PM - 05:00PM VR408C, FRI 03:00PM - 05:00PM ROOM 407");
                    allCourses.add("INF241 - GEITE01X: LIVING IN THE I.T. ERA | TUE 05:00PM - 07:00PM VR408A, FRI 05:00PM - 07:00PM VR408A");
                    allCourses.add("INF242 - GEITE01X: LIVING IN THE I.T. ERA | TUE 09:00AM - 11:00AM VR408B, FRI 09:00AM - 11:00AM VR408B");
                    allCourses.add("INF241 - GERIZ01X: LIFE AND WORKS OF RIZAL | TUE 03:00PM - 05:00PM VR408A, FRI 03:00PM - 05:00PM VR408A");
                    allCourses.add("INF242 - GERIZ01X: LIFE AND WORKS OF RIZAL | TUE 01:00PM - 03:00PM VR408C, FRI 01:00PM - 03:00PM VR408B");
                    allCourses.add("INF241 - MCFIT04X: PATHFit 4 | MON 04:00PM - 06:40PM Gymnasium2");
                    allCourses.add("INF242 - MCFIT04X: PATHFit 4 | THU 04:00PM - 06:40PM Gymnasium2");
                    break;
                case "BS Computer Engineering":
                    allCourses.add("CPE241 - CPORTN10: COMPUTER ENGINEERING ORIENTATION | MON 01:00PM - 02:20PM VR407M");
                    allCourses.add("CPE242 - CPORTN10: COMPUTER ENGINEERING ORIENTATION | TUE 01:00PM - 02:20PM VR407M");
                    allCourses.add("CPE241 - CPPROG2L: PROGRAMMING LOGIC AND DESIGN - LAB | TUE 11:00AM - 03:00PM ROOM 507, FRI 11:00AM - 03:00PM ROOM 507");
                    allCourses.add("CPE242 - CPPROG2L: PROGRAMMING LOGIC AND DESIGN - LAB | WED 11:00AM - 03:00PM ROOM 507, SAT 11:00AM - 03:00PM ROOM 507");
                    allCourses.add("CPE241 - ENITV12D: INTERVENTION FOR CALCULUS - DRAFTING | TUE 07:00AM - 11:00AM ROOM 425, FRI 07:00AM - 11:00AM ROOM 425");
                    allCourses.add("CPE242 - ENITV12D: INTERVENTION FOR CALCULUS - DRAFTING | WED 07:00AM - 11:00AM ROOM 425, SAT 07:00AM - 11:00AM ROOM 425");
                    allCourses.add("CPE241 - ENITV21D: INTERVENTION FOR CHEMISTRY - DRAFTING | SAT 07:00AM - 11:00AM Room 514");
                    allCourses.add("CPE242 - ENITV21D: INTERVENTION FOR CHEMISTRY - DRAFTING | MON 07:00AM - 11:00AM Room 514");
                    allCourses.add("CPE241 - ENITV31D: INTERVENTION FOR PHYSICS - DRAFTING | TUE 03:00PM - 07:00PM Room 517");
                    allCourses.add("CPE242 - ENITV31D: INTERVENTION FOR PHYSICS - DRAFTING | WED 03:00PM - 07:00PM Room 517");
                    allCourses.add("CPE241 - GEART01X: ART APPRECIATION | MON 09:00AM - 11:00AM VR410S, THU 09:00AM - 11:00AM VR407A");
                    allCourses.add("CPE242 - GEART01X: ART APPRECIATION | TUE 09:00AM - 11:00AM VR410S, FRI 09:00AM - 11:00AM VR407A");
                    allCourses.add("CPE241 - GEETH01X: ETHICS | MON 07:00AM - 09:00AM VR410S, THU 07:00AM - 09:00AM VR407A");
                    allCourses.add("CPE242 - GEETH01X: ETHICS | TUE 07:00AM - 09:00AM VR410S, FRI 07:00AM - 09:00AM VR407A");
                    allCourses.add("CPE241 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | WED 07:00AM - 11:00AM ROOM 406");
                    allCourses.add("CPE242 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | THU 07:00AM - 11:00AM ROOM 406");
                    allCourses.add("SECA-CPE241 - MCWTS01X: NATIONAL SERVICE TRAINING PROGRAM 1 | MON 03:00PM - 05:00PM VR405L, THU 03:00PM - 05:00PM VR407Z");
                    allCourses.add("SECA-CPE242 - MCWTS01X: NATIONAL SERVICE TRAINING PROGRAM 1 | TUE 03:00PM - 05:00PM VR405L, FRI 03:00PM - 05:00PM VR407Z");
                    break;
                case "BS Architecture":
                    allCourses.add("ARCH241 - AALGTRIG: COLLEGE ALGEBRA AND PLANE TRIGONOMETRY | TUE 11:00AM - 01:00PM ROOM 432, FRI 11:00AM - 01:00PM ROOM 432");
                    allCourses.add("ARCH242 - AALGTRIG: COLLEGE ALGEBRA AND PLANE TRIGONOMETRY | WED 11:00AM - 01:00PM ROOM 432, SAT 11:00AM - 01:00PM ROOM 432");
                    allCourses.add("ARCH241 - AGRAPN1S: ARCHITECTURAL VISUAL COMM 1: GRAPHICS 1 | MON 07:00AM - 11:40AM Room 524, THU 07:00AM - 11:40AM Room 524");
                    allCourses.add("ARCH242 - AGRAPN1S: ARCHITECTURAL VISUAL COMM 1: GRAPHICS 1 | TUE 01:00PM - 05:20PM Room 524, FRI 01:00PM - 05:20PM Room 524");
                    allCourses.add("ARCH241 - ATHEORY1: THEORY OF ARCHITECTURE 1 | TUE 07:00AM - 09:40AM ROOM 409");
                    allCourses.add("ARCH242 - ATHEORY1: THEORY OF ARCHITECTURE 1 | WED 07:00AM - 09:40AM ROOM 409");
                    allCourses.add("ARCH241 - AVSTEN1S: ARCHITECTURAL VISUAL COMM 2: VISUAL TECHNIQUES 1 | MON 12:20PM - 05:40PM Room 541");
                    allCourses.add("ARCH242 - AVSTEN1S: ARCHITECTURAL VISUAL COMM 2: VISUAL TECHNIQUES 1 | TUE 12:20PM - 05:40PM Room 542");
                    allCourses.add("ARCH241 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | TUE 01:00PM - 03:00PM ROOM 520, FRI 01:00PM - 03:00PM ROOM 520");
                    allCourses.add("ARCH242 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | WED 01:00PM - 03:00PM ROOM 520, SAT 01:00PM - 03:00PM ROOM 520");
                    allCourses.add("ARCH241 - GENAT01R: Nationalian Course | TUE 03:00PM - 05:00PM VR407R, FRI 03:00PM - 05:00PM VR405B");
                    allCourses.add("ARCH242 - GENAT01R: Nationalian Course | WED 03:00PM - 05:00PM VR407R, SAT 03:00PM - 05:00PM VR405B");
                    allCourses.add("ARCH241 - MNSTP01X: Civic Welfare Training Service 1 | WED 11:00AM - 03:00PM VR405B");
                    allCourses.add("ARCH242 - MNSTP01X: Civic Welfare Training Service 1 | SAT 11:00AM - 03:00PM VR405B");
                    break;
                case "BS Civil Engineering":
                    allCourses.add("BSCE241 - CEEDRP1D: ENGINEERING DRAWING AND PLANS | MON 07:00AM - 11:00AM Room 521");
                    allCourses.add("BSCE242 - CEEDRP1D: ENGINEERING DRAWING AND PLANS | THU 07:00AM - 11:00AM Room 521");
                    allCourses.add("BSCE241 - CEORTN20: CIVIL ENGINEERING ORIENTATION | MON 03:00PM - 05:40PM VR407A");
                    allCourses.add("BSCE242 - CEORTN20: CIVIL ENGINEERING ORIENTATION | THU 12:20PM - 03:00PM VR405J");
                    allCourses.add("BSCE241 - ENITV12D: INTERVENTION FOR CALCULUS - DRAFTING | MON 01:00PM - 03:00PM ROOM 420, WED 01:00PM - 05:00PM Room 517, THU 01:00PM - 03:00PM ROOM 420");
                    allCourses.add("BSCE242 - ENITV12D: INTERVENTION FOR CALCULUS - DRAFTING | TUE 01:00PM - 03:00PM ROOM 420, FRI 01:00PM - 05:00PM Room 517, THU 03:00PM - 05:00PM ROOM 420");
                    allCourses.add("BSCE241 - ENITV21D: INTERVENTION FOR CHEMISTRY - DRAFTING | THU 07:00AM - 11:00AM Room 514");
                    allCourses.add("BSCE242 - ENITV21D: INTERVENTION FOR CHEMISTRY - DRAFTING | FRI 07:00AM - 11:00AM Room 514");
                    allCourses.add("BSCE241 - GEETH01X: ETHICS | TUE 03:00PM - 05:00PM VR405C, FRI 03:00PM - 05:00PM VR405C");
                    allCourses.add("BSCE242 - GEETH01X: ETHICS | WED 03:00PM - 05:00PM VR405C, SAT 03:00PM - 05:00PM VR405C");
                    allCourses.add("BSCE241 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | TUE 01:00PM - 03:00PM ROOM 419, FRI 01:00PM - 03:00PM ROOM 419");
                    allCourses.add("BSCE242 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | WED 01:00PM - 03:00PM ROOM 419, SAT 01:00PM - 03:00PM ROOM 419");
                    allCourses.add("BSCE241 - GENAT01R: Nationalian Course | TUE 09:00AM - 11:00AM VR405B, FRI 09:00AM - 11:00AM VR405B");
                    allCourses.add("BSCE242 - GENAT01R: Nationalian Course | WED 09:00AM - 11:00AM VR405B, SAT 09:00AM - 11:00AM VR405B");
                    allCourses.add("BSCE241 - GESTS01X: SCIENCE, TECHNOLOGY AND SOCIETY | TUE 07:00AM - 09:00AM VR405C, FRI 07:00AM - 09:00AM VR405B");
                    allCourses.add("BSCE242 - GESTS01X: SCIENCE, TECHNOLOGY AND SOCIETY | WED 07:00AM - 09:00AM VR405C, SAT 07:00AM - 09:00AM VR405B");
                    break;
            }
            
            // Add all courses to the model
            for (String course : allCourses) {
                model.addElement(course);
            }
        }
        
        fullList.setModel(model);
        
        // Update selected items based on checked boxes
        List<Integer> selectedIndices = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            String course = model.getElementAt(i);
            if (!course.endsWith("Courses:")) {
                String courseCode = course.split(" - ")[0].trim();
                if (courseCheckBoxes.containsKey(courseCode) && courseCheckBoxes.get(courseCode).isSelected()) {
                    selectedIndices.add(i);
                }
            }
        }
        fullList.setSelectedIndices(selectedIndices.stream().mapToInt(i -> i).toArray());
        
        fullListDialog.setVisible(true);
    }

    // Create the full list dialog (only once)
    private void createFullListDialog() {
        fullListDialog = new JDialog();
        fullListDialog.setTitle("Full Schedule List");
        fullListDialog.setModal(false);
        fullListDialog.setSize(900, 600);
        fullListDialog.setLayout(new BorderLayout());
        fullListDialog.setLocationRelativeTo(null);

        // Create header panel with column labels
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        headerPanel.setBackground(new Color(102, 126, 234));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel courseLabel = new JLabel("Course Code & Subject");
        JLabel scheduleLabel = new JLabel("Schedule");
        JLabel roomLabel = new JLabel("Room");

        // Style the headers
        Font headerFont = new Font("Inter", Font.BOLD, 14);
        courseLabel.setFont(headerFont);
        scheduleLabel.setFont(headerFont);
        roomLabel.setFont(headerFont);
        courseLabel.setForeground(Color.WHITE);
        scheduleLabel.setForeground(Color.WHITE);
        roomLabel.setForeground(Color.WHITE);

        headerPanel.add(courseLabel);
        headerPanel.add(scheduleLabel);
        headerPanel.add(roomLabel);

        fullListDialog.add(headerPanel, BorderLayout.NORTH);

        // Create the list with custom renderer
        fullList = new JList<>();
        fullList.setFont(new Font("Inter", Font.PLAIN, 14));
        fullList.setSelectionBackground(new Color(102, 126, 234));
        fullList.setSelectionForeground(Color.WHITE);
        fullList.setLayoutOrientation(JList.VERTICAL);
        fullList.setFixedCellHeight(50);
        fullList.setBackground(new Color(240, 240, 240));
        fullList.setForeground(Color.BLACK);
        fullList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        fullList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fullList.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Custom cell renderer
        fullList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
                panel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                if (value instanceof String) {
                    String text = (String) value;
                    if (text.endsWith("Courses:")) {
                        // Header style
                        JLabel headerLabel = new JLabel(text);
                        headerLabel.setFont(new Font("Inter", Font.BOLD, 16));
                        headerLabel.setForeground(new Color(102, 126, 234));
                        panel.setLayout(new BorderLayout());
                        panel.add(headerLabel, BorderLayout.CENTER);
                        panel.setBackground(list.getBackground());
                    } else {
                        // Course item style
                        String[] parts = text.split("\\|");
                        if (parts.length >= 2) {
                            String courseInfo = parts[0].trim();
                            String scheduleInfo = parts[1].trim();

                            // Split schedule info
                            String[] scheduleParts = scheduleInfo.split("ROOM");
                            String time = scheduleParts[0].trim();
                            String room = scheduleParts.length > 1 ? "ROOM " + scheduleParts[1].trim() : "";

                            JLabel courseLabel = new JLabel(courseInfo);
                            JLabel timeLabel = new JLabel(time);
                            JLabel roomLabel = new JLabel(room);

                            courseLabel.setFont(new Font("Inter", Font.PLAIN, 14));
                            timeLabel.setFont(new Font("Inter", Font.PLAIN, 14));
                            roomLabel.setFont(new Font("Inter", Font.PLAIN, 14));

                            courseLabel.setForeground(isSelected ? Color.WHITE : Color.BLACK);
                            timeLabel.setForeground(isSelected ? Color.WHITE : Color.BLACK);
                            roomLabel.setForeground(isSelected ? Color.WHITE : Color.BLACK);

                            panel.add(courseLabel);
                            panel.add(timeLabel);
                            panel.add(roomLabel);
                        }
                    }
                }

                return panel;
            }
        });

        JScrollPane fullScrollPane = new JScrollPane(fullList);
        fullScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));
        fullListDialog.add(fullScrollPane, BorderLayout.CENTER);
        
        // Add close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Inter", Font.PLAIN, 12));
        closeButton.addActionListener(e -> fullListDialog.setVisible(false));
        
        buttonPanel.add(closeButton);
        fullListDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add selection listener
        fullList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                for (int i = 0; i < fullList.getModel().getSize(); i++) {
                    String course = fullList.getModel().getElementAt(i);
                    if (!course.endsWith("Courses:")) {
                        String courseCode = course.split(" - ")[0].trim();
                        if (courseCheckBoxes.containsKey(courseCode)) {
                            courseCheckBoxes.get(courseCode).setSelected(fullList.isSelectedIndex(i));
                        }
                    }
                }
            }
        });
    }

    // Helper to get selected indices
    private int[] getSelectedIndices(JList<String> list) {
        ListModel<String> model = list.getModel();
        List<String> selectedValues = list.getSelectedValuesList();
        List<Integer> indices = new ArrayList<>();
        
        for (int i = 0; i < model.getSize(); i++) {
            if (selectedValues.contains(model.getElementAt(i))) {
                indices.add(i);
            }
        }
        
        return indices.stream().mapToInt(i -> i).toArray();
    }

    // Modify getSelectedSchedules to handle multiple selections
    public List<String> getSelectedSchedules() {
        List<String> selectedSchedules = new ArrayList<>();
        for (JCheckBox checkBox : courseCheckBoxes.values()) {
            if (checkBox.isSelected()) {
                selectedSchedules.add(checkBox.getText());
                System.out.println("Selected: " + checkBox.getText()); // Debug output
            }
        }
        return selectedSchedules;
    }

    private void addPlaceholderBehavior(JTextField field, String placeholder) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private String generateStudentId() {
        try {
            int currentYear = LocalDate.now().getYear();
            String yearPrefix = String.valueOf(currentYear);
            
            // Get the highest student number for the current year
            String sql = "SELECT MAX(CAST(SUBSTR(student_id, 6) AS INTEGER)) FROM students WHERE student_id LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, yearPrefix + "-%");
            
            ResultSet rs = stmt.executeQuery();
            int nextNumber = 1000; // Starting number
            
            if (rs.next()) {
                Integer maxNumber = rs.getInt(1);
                if (maxNumber > 0) {
                    nextNumber = maxNumber + 1;
                }
            }
            
            rs.close();
            stmt.close();
            
            return yearPrefix + "-" + String.format("%04d", nextNumber);
        } catch (SQLException e) {
            System.err.println("Error generating student ID: " + e.getMessage());
            return LocalDate.now().getYear() + "-1000";
        }
    }

    private String generateStudentEmail() {

        String firstName = firstNameField.getText().trim().toLowerCase();
        String middleName = middleNameField.getText().trim().toLowerCase();
        String firstInitial = firstName.isEmpty() ? "" : firstName.substring(0, 1);
        String middleInitial = middleName.isEmpty() ? "" : middleName.substring(0, 1);
        String lastName = lastNameField.getText().trim().toLowerCase();

        String email = lastName + firstInitial + middleInitial + "@students.iskonek.edu.ph";
        return email.replaceAll("\\s+", ""); 
        }

    private void showScheduleSelection(String studentId, String studentEmail) {
        // Create a simple success dialog with options
        JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel successLabel = new JLabel("<html><center><h2>Enrollment Successful!</h2><br>" +
                                       "Your Student ID: <b>" + studentId + "</b><br><br>" + 
                                       "Your Student Email: <b>" + studentEmail + "</b><br><br>" +
                                       "Please remember your Student ID and Password for login.</center></html>");
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messagePanel.add(successLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());

        
        JButton loginButton = new JButton("Go to Login");
        loginButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(loginButton).dispose();
            this.dispose();
            if (IskonekLogin.class != null) {
                try {
                    IskonekLogin loginWindow = (IskonekLogin) IskonekLogin.class.getDeclaredConstructor().newInstance();
                    loginWindow.setVisible(true);
                    loginWindow.refreshDatabaseConnection();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    new IskonekLogin().setVisible(true);
                }
            } else {
                new IskonekLogin().setVisible(true);
            }
        });
        
        buttonPanel.add(loginButton);
        messagePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JOptionPane.showMessageDialog(this, messagePanel, "Success", JOptionPane.PLAIN_MESSAGE);
    }

    private class EnrollActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (validateForm()) {
                insertStudent();
            }
        }
    }

    private boolean validateForm() {

        if (firstNameField.getText().trim().isEmpty()) {
            showError("First Name is required");
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            showError("Last Name is required");
            return false;
        }
        if (dobField.getText().equals("YYYY-MM-DD") || dobField.getText().trim().isEmpty()) {
            showError("Date of Birth is required");
            return false;
        }
        if (nationalityField.getText().trim().isEmpty()) {
            showError("Nationality is required");
            return false;
        }
        if (contactField.getText().trim().isEmpty()) {
            showError("Contact Number is required");
            return false;
        }
        if (addressField.getText().trim().isEmpty()) {
            showError("Address is required");
            return false;
        }
        if (passwordField.getPassword().length == 0) {
            showError("Password is required");
            return false;
        }
        if (confirmPasswordField.getPassword().length == 0) {
            showError("Confirm Password is required");
            return false;
        }

        // Validate date format
        try {
            LocalDate.parse(dobField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Please use YYYY-MM-DD");
            return false;
        }

        // Check password match
        if (!new String(passwordField.getPassword()).equals(new String(confirmPasswordField.getPassword()))) {
            showError("Passwords do not match");
            return false;
        }

        // Validate password strength
        String password = new String(passwordField.getPassword());
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            return false;
        }

        // Check for schedule conflicts only
        List<String> selectedSchedules = getSelectedSchedules();
        if (selectedSchedules.isEmpty()) {
            showError("Please select at least one course schedule");
                    return false;
                }
                
        // Check for schedule conflicts
        for (int i = 0; i < selectedSchedules.size(); i++) {
            for (int j = i + 1; j < selectedSchedules.size(); j++) {
                String schedule1 = selectedSchedules.get(i).split("\\|")[1].trim();
                String schedule2 = selectedSchedules.get(j).split("\\|")[1].trim();
                
                String[] slots1 = schedule1.split(", ");
                String[] slots2 = schedule2.split(", ");
                
                for (String slot1 : slots1) {
                    for (String slot2 : slots2) {
                        String[] parts1 = slot1.split(" ");
                        String[] parts2 = slot2.split(" ");
                        if (parts1.length >= 3 && parts2.length >= 3) {
                            if (parts1[0].equals(parts2[0])) {
                                String time1 = parts1[1] + " " + parts1[2];
                                String time2 = parts2[1] + " " + parts2[2];
                                if (time1.equals(time2)) {
                                    showError("Schedule conflict detected between selected courses.");
                    return false;
                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    
    private void insertStudent() {
        try {
            generatedStudentId = generateStudentId();
            generatedStudentEmail = generateStudentEmail();
            
            List<String> selectedSchedules = getSelectedSchedules();
            if (selectedSchedules.isEmpty()) {
                showError("Please select at least one course schedule");
                return;
            }
            
            String schedulesString = String.join("|||", selectedSchedules);
            System.out.println("DEBUG: schedulesString to save: " + schedulesString);
            
            String sql = "INSERT INTO students (student_id, first_name, middle_name, last_name, date_of_birth, gender, " +
                       "civil_status, nationality, contact_number, address, guardian_name, " +
                       "guardian_contact, password, student_email, student_course, student_schedule) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, generatedStudentId);
            stmt.setString(2, firstNameField.getText().trim());
            stmt.setString(3, middleNameField.getText().trim());
            stmt.setString(4, lastNameField.getText().trim());
            stmt.setString(5, dobField.getText().trim());
            stmt.setString(6, (String) genderBox.getSelectedItem());
            stmt.setString(7, (String) civilStatusBox.getSelectedItem());
            stmt.setString(8, nationalityField.getText().trim());
            stmt.setString(9, contactField.getText().trim());
            stmt.setString(10, addressField.getText().trim());
            stmt.setString(11, guardianField.getText().trim());
            stmt.setString(12, guardianContactField.getText().trim());
            stmt.setString(13, new String(passwordField.getPassword()));
            stmt.setString(14, generatedStudentEmail);
            stmt.setString(15, (String) courseBox.getSelectedItem());
            stmt.setString(16, schedulesString);

            int result = stmt.executeUpdate();
            if (result > 0) {
                showScheduleSelection(generatedStudentId, generatedStudentEmail);
                clearForm();
            } else {
                showError("Failed to enroll student");
            }
            
            stmt.close();
        } catch (SQLException ex) {
            if (ex.getMessage().contains("UNIQUE constraint failed")) {
                showError("Student ID already exists. Please try again.");
            } else {
                showError("Database error: " + ex.getMessage());
                ex.printStackTrace(); // Add this for debugging
            }
        }
    }

    private void clearForm() {
        firstNameField.setText("");
        middleNameField.setText("");
        lastNameField.setText("");
        dobField.setText("YYYY-MM-DD");
        dobField.setForeground(Color.GRAY);
        genderBox.setSelectedIndex(0);
        civilStatusBox.setSelectedIndex(0);
        nationalityField.setText("");
        contactField.setText("");
        addressField.setText("");
        guardianField.setText("");
        guardianContactField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        courseBox.setSelectedIndex(0);
        for (JCheckBox checkBox : courseCheckBoxes.values()) {
            checkBox.setSelected(false);
        }
        courseCheckBoxes.clear();
        updateSchedules();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Custom rounded components
    static class RoundedTextField extends JTextField {
        public RoundedTextField() {
            setOpaque(false);
            setFont(new Font("Inter", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            setBackground(new Color(230, 230, 230));
            setForeground(Color.BLACK);
            setCaretColor(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Background
            g2.setColor(new Color(230, 230, 230));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            
            super.paintComponent(g);
            g2.dispose();
        }
    }

    static class RoundedPasswordField extends JPasswordField {
        public RoundedPasswordField() {
            setOpaque(false);
            setFont(new Font("Inter", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            setBackground(new Color(230, 230, 230));
            setForeground(Color.BLACK);
            setCaretColor(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Background
            g2.setColor(new Color(230, 230, 230));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            
            super.paintComponent(g);
            g2.dispose();
        }
    }

    static class RoundedComboBox extends JComboBox<String> {
        public RoundedComboBox(String[] items) {
            super(items);
            setOpaque(false);
            setFont(new Font("Inter", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Background
            g2.setColor(new Color(230, 230, 230));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            
            super.paintComponent(g);
            g2.dispose();
        }
    }

    static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setForeground(Color.WHITE);
            setFont(new Font("Inter", Font.BOLD, 14));
            setBorder(null);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2.setColor(new Color(85, 105, 200));
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(120, 140, 255));
            } else {
                g2.setColor(new Color(102, 126, 234));
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    @Override
    public void dispose() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
        super.dispose();
    }


    private void enrollInCourse(String studentId, String newCourse) {
        try {
            String getQuery = "SELECT student_schedule FROM students WHERE student_id = ?";
            PreparedStatement getStmt = conn.prepareStatement(getQuery);
            getStmt.setString(1, studentId);
            ResultSet rs = getStmt.executeQuery();
            
            String existingSchedule = "";
            if (rs.next()) {
                String temp = rs.getString("student_schedule");
                existingSchedule = (temp != null) ? temp : "";
            }
            rs.close();
            getStmt.close();
            
            String updatedSchedule;
            if (existingSchedule.isEmpty()) {
                updatedSchedule = newCourse;
            } else {
                updatedSchedule = existingSchedule + "|||" + newCourse;
            }
            
            String updateQuery = "UPDATE students SET student_schedule = ? WHERE student_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, updatedSchedule);
            updateStmt.setString(2, studentId);
            updateStmt.executeUpdate();
            updateStmt.close();
            
        } catch (SQLException e) {
            System.err.println("Error enrolling in course: " + e.getMessage());
            showError("Failed to enroll in course: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new EnrollForm().setVisible(true);
        });
    }
}