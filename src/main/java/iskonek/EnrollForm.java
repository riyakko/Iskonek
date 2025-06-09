package iskonek;
import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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
    private JList<String> scheduleList;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private String generatedStudentId;
    private String generatedStudentEmail;

    public EnrollForm() {
        initializeDatabase();
        initializeUI();
    }

    private void initializeDatabase() {
        try {
            // Use the same database path as login form
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

        // Main panel with gradient background
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
        // Create white rounded background panel
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
        
        // Title with shadow effect (matching login form style)
        JLabel titleLabel = new JLabel("ISKOnek Enrollment") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(getText(), x + 2, y + 2);
                
                // Main text
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

        // Add placeholder behavior for DOB field
        addPlaceholderBehavior(dobField, "YYYY-MM-DD");
        
        // Set consistent field size
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

        // Form fields with labels
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
    

        // Create a list for multi-selection
        scheduleList = new JList<>();

        // Put list in scroll pane
        scheduleList.setFont(new Font("Inter", Font.PLAIN, 14));
        scheduleList.setSelectionBackground(new Color(102, 126, 234));
        scheduleList.setSelectionForeground(Color.WHITE);
        scheduleList.setLayoutOrientation(JList.VERTICAL);
        scheduleList.setFixedCellHeight(25);
        scheduleList.setFixedCellWidth(280); 
        scheduleList.setBackground(new Color(230, 230, 230)); 
        scheduleList.setForeground(Color.BLACK);
        scheduleList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
        scheduleList.setFont(new Font("Inter", Font.PLAIN, 14));
        scheduleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scheduleList.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        scheduleList.setFocusable(false); 
        scheduleList.setLayoutOrientation(JList.VERTICAL_WRAP); 
        scheduleList.setVisibleRowCount(13); 
        scheduleList.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); 


        JScrollPane scrollPane = new JScrollPane(scheduleList);
        scrollPane.setPreferredSize(new Dimension(300, 70)); 
        whitePanel.add(scrollPane, gbc);

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


        // Add button below the schedule list
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

        // Create panel for password field and toggle button
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

        // Create panel for confirm password field and toggle button
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

        // Button panel for better layout
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
        DefaultListModel<String> model = new DefaultListModel<>();
        
        if (selectedCourse != null) {
            switch (selectedCourse) {
                case "BS Computer Science":

                    model.addElement("COM241 - CCDATRCL: DATA STRUCTURES AND ALGORITHMS | TUE 12:00PM - 04:00PM ROOM 505, FRI 01:00PM - 03:40PM ROOM 431");
                    model.addElement("COM242  - CCDATRCL: DATA STRUCTURES AND ALGORITHMS | MON 12:00PM - 04:00PM ROOM 505, THU 01:00PM - 03:40PM ROOM 431");
                    model.addElement("COM241 - CCPLTFRL: PLATFORM TECHNOLOGIES | MON 07:00AM - 09:40AM ROOM 431, THU 07:00AM - 11:00AM ROOM 505");
                    model.addElement("COM242  - CCPLTFRL: PLATFORM TECHNOLOGIES | MON 01:00PM - 03:40PM ROOM 431, THU 12:00PM - 04:00PM ROOM 505");
                    model.addElement("COM241 - GEFID01X: WIKA AT PANITIKAN SA PAGPAPATIBAY NG PILIPINONG IDENTIDAD | TUE 09:00AM - 11:00AM VR408A, FRI 09:00AM - 11:00AM ROOM 407");
                    model.addElement("COM242  - GEFID01X: WIKA AT PANITIKAN SA PAGPAPATIBAY NG PILIPINONG IDENTIDAD | TUE 03:00PM - 05:00PM VR408C, FRI 03:00PM - 05:00PM ROOM 407");
                    model.addElement("COM241 - GEITE01X: LIVING IN THE I.T. ERA | TUE 05:00PM - 07:00PM VR408A, FRI 05:00PM - 07:00PM VR408A");
                    model.addElement("COM242  - GEITE01X: LIVING IN THE I.T. ERA | TUE 09:00AM - 11:00AM VR408B, FRI 09:00AM - 11:00AM VR408B");
                    model.addElement("COM241 - GERIZ01X: LIFE AND WORKS OF RIZAL | TUE 03:00PM - 05:00PM VR408A, FRI 03:00PM - 05:00PM VR408A");
                    model.addElement("COM242  - GERIZ01X: LIFE AND WORKS OF RIZAL | TUE 01:00PM - 03:00PM VR408C, FRI 01:00PM - 03:00PM VR408B");
                    model.addElement("COM241 - MCFIT04X: PATHFit 4 | MON 04:00PM - 06:40PM Gymnasium2");
                    model.addElement("COM242  - MCFIT04X: PATHFit 4 | THU 04:00PM - 06:40PM Gymnasium2");

                    break;
                case "BS Information Technology":

                    model.addElement("INF241 - CCDATRCL: DATA STRUCTURES AND ALGORITHMS | TUE 12:00PM - 04:00PM ROOM 505, FRI 01:00PM - 03:40PM ROOM 431");
                    model.addElement("INF242 - CCDATRCL: DATA STRUCTURES AND ALGORITHMS | MON 12:00PM - 04:00PM ROOM 505, THU 01:00PM - 03:40PM ROOM 431");
                    model.addElement("INF241 - CCPLTFRL: PLATFORM TECHNOLOGIES | MON 07:00AM - 09:40AM ROOM 431, THU 07:00AM - 11:00AM ROOM 505");
                    model.addElement("INF242 - CCPLTFRL: PLATFORM TECHNOLOGIES | MON 01:00PM - 03:40PM ROOM 431, THU 12:00PM - 04:00PM ROOM 505");
                    model.addElement("INF241 - GEFID01X: WIKA AT PANITIKAN SA PAGPAPATIBAY NG PILIPINONG IDENTIDAD | TUE 09:00AM - 11:00AM VR408A, FRI 09:00AM - 11:00AM ROOM 407");
                    model.addElement("INF242 - GEFID01X: WIKA AT PANITIKAN SA PAGPAPATIBAY NG PILIPINONG IDENTIDAD | TUE 03:00PM - 05:00PM VR408C, FRI 03:00PM - 05:00PM ROOM 407");
                    model.addElement("INF241 - GEITE01X: LIVING IN THE I.T. ERA | TUE 05:00PM - 07:00PM VR408A, FRI 05:00PM - 07:00PM VR408A");
                    model.addElement("INF242 - GEITE01X: LIVING IN THE I.T. ERA | TUE 09:00AM - 11:00AM VR408B, FRI 09:00AM - 11:00AM VR408B");
                    model.addElement("INF241 - GERIZ01X: LIFE AND WORKS OF RIZAL | TUE 03:00PM - 05:00PM VR408A, FRI 03:00PM - 05:00PM VR408A");
                    model.addElement("INF242 - GERIZ01X: LIFE AND WORKS OF RIZAL | TUE 01:00PM - 03:00PM VR408C, FRI 01:00PM - 03:00PM VR408B");
                    model.addElement("INF241 - MCFIT04X: PATHFit 4 | MON 04:00PM - 06:40PM Gymnasium2");
                    model.addElement("INF242 - MCFIT04X: PATHFit 4 | THU 04:00PM - 06:40PM Gymnasium2");

                    break;
                case "BS Computer Engineering":

                    model.addElement("BSCE241  - CEEDRP1D: ENGINEERING DRAWING AND PLANS | MON 07:00AM - 11:00AM Room 521");
                    model.addElement("BSCE242   - CEEDRP1D: ENGINEERING DRAWING AND PLANS | THU 07:00AM - 11:00AM Room 521");
                    model.addElement("BSCE241  - CEORTN20: CIVIL ENGINEERING ORIENTATION | MON 03:00PM - 05:40PM VR407A");
                    model.addElement("BSCE242   - CEORTN20: CIVIL ENGINEERING ORIENTATION | THU 12:20PM - 03:00PM VR405J");
                    model.addElement("BSCE241  - ENITV12D: INTERVENTION FOR CALCULUS - DRAFTING | MON 01:00PM - 03:00PM ROOM 420, WED 01:00PM - 05:00PM Room 517, THU 01:00PM - 03:00PM ROOM 420");
                    model.addElement("BSCE241    - ENITV21D: INTERVENTION FOR CHEMISTRY - DRAFTING | THU 07:00AM - 11:00AM Room 514");
                    model.addElement("BSCE241  - GEETH01X: ETHICS | TUE 03:00PM - 05:00PM VR405C, FRI 03:00PM - 05:00PM VR405C");
                    model.addElement("BSCE241    - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | TUE 01:00PM - 03:00PM ROOM 419, FRI 01:00PM - 03:00PM ROOM 419");
                    model.addElement("BSCE241  - GENAT01R: Nationalian Course | TUE 09:00AM - 11:00AM VR405B, FRI 09:00AM - 11:00AM VR405B");
                    model.addElement("BSCE241    - GESTS01X: SCIENCE, TECHNOLOGY AND SOCIETY | TUE 07:00AM - 09:00AM VR405C, FRI 07:00AM - 09:00AM VR405B");

                    break;
                case "BS Architecture":

                    model.addElement("ARCH241 - AALGTRIG: COLLEGE ALGEBRA AND PLANE TRIGONOMETRY | TUE 11:00AM - 01:00PM ROOM 432, FRI 11:00AM - 01:00PM ROOM 432");
                    model.addElement("ARCH241 - AGRAPN1S: ARCHITECTURAL VISUAL COMM 1: GRAPHICS 1 | MON 07:00AM - 11:40AM Room 524, THU 07:00AM - 11:40AM Room 524");
                    model.addElement("ARCH242 - AGRAPN1S: ARCHITECTURAL VISUAL COMM 1: GRAPHICS 1 | MON 01:00PM - 05:20PM Room 524, THU 01:00PM - 05:20PM Room 524");
                    model.addElement("ARCH241 - ATHEORY1: THEORY OF ARCHITECTURE 1 | TUE 07:00AM - 09:40AM ROOM 409");
                    model.addElement("ARCH241 - AVSTEN1S: ARCHITECTURAL VISUAL COMM 2: VISUAL TECHNIQUES 1 | MON 12:20PM - 05:40PM Room 541");
                    model.addElement("ARCH242 - AVSTEN1S: ARCHITECTURAL VISUAL COMM 2: VISUAL TECHNIQUES 1 | MON 07:00AM - 12:20PM Room 542");
                    model.addElement("ARCH241 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | TUE 01:00PM - 03:00PM ROOM 520, FRI 01:00PM - 03:00PM ROOM 520");
                    model.addElement("ARCH241 - GENAT01R: Nationalian Course | TUE 03:00PM - 05:00PM VR407R, FRI 03:00PM - 05:00PM VR405B");
                    model.addElement("ARCH241 - MNSTP01X: Civic Welfare Training Service 1 | WED 11:00AM - 03:00PM VR405B");

                    break;
                case "BS Civil Engineering":

                    model.addElement("CPE241 - CPORTN10: COMPUTER ENGINEERING ORIENTATION | MON 01:00PM - 02:20PM VR407M");
                    model.addElement("CPE241 - CPPROG2L: PROGRAMMING LOGIC AND DESIGN - LAB | TUE 11:00AM - 03:00PM ROOM 507, FRI 11:00AM - 03:00PM ROOM 507");
                    model.addElement("CPE241 - ENITV12D: INTERVENTION FOR CALCULUS - DRAFTING | TUE 07:00AM - 11:00AM ROOM 425, FRI 07:00AM - 11:00AM ROOM 425");
                    model.addElement("CPE241 - ENITV21D: INTERVENTION FOR CHEMISTRY - DRAFTING | SAT 07:00AM - 11:00AM Room 514");
                    model.addElement("CPE241 - ENITV31D: INTERVENTION FOR PHYSICS - DRAFTING | TUE 03:00PM - 07:00PM Room 517");
                    model.addElement("CPE241 - GEART01X: ART APPRECIATION | MON 09:00AM - 11:00AM VR410S, THU 09:00AM - 11:00AM VR407A");
                    model.addElement("CPE241 - GEETH01X: ETHICS | MON 07:00AM - 09:00AM VR410S, THU 07:00AM - 09:00AM VR407A");
                    model.addElement("CPE241 - GEMMW01X: MATHEMATICS IN THE MODERN WORLD | WED 07:00AM - 11:00AM ROOM 406");
                    model.addElement("SECA-CPE241 - MCWTS01X: NATIONAL SERVICE TRAINING PROGRAM 1 | MON 03:00PM - 05:00PM VR405L, THU 03:00PM - 05:00PM VR407Z");

                    break;
            }
        }
        
        if (model.size() == 0) {
            model.addElement("Select a course first");
        }
        
        scheduleList.setModel(model);
    }

    // Method to show full schedule list
    private void showFullScheduleList() {
        if (fullListDialog == null) {
            createFullListDialog();
        }
        
        // Update the full list with current data
        JList<String> fullList = (JList<String>)((JScrollPane)fullListDialog.getContentPane().getComponent(0)).getViewport().getView();
        fullList.setModel(scheduleList.getModel());
        fullList.setSelectedIndices(getSelectedIndices(scheduleList));
        
        fullListDialog.setVisible(true);
    }

    // Create the full list dialog (only once)
    private void createFullListDialog() {
        fullListDialog = new JDialog();
        fullListDialog.setTitle("Full Schedule List");
        fullListDialog.setModal(false); // Non-modal so user can interact with main window
        fullListDialog.setSize(600, 500); // Larger size for better viewing
        fullListDialog.setLayout(new BorderLayout());
        fullListDialog.setLocationRelativeTo(null); // Center on screen

        // Create a list for the dialog with all schedules
        JList<String> fullList = new JList<>();
        
        // Apply consistent styling with your original list
        fullList.setFont(new Font("Inter", Font.PLAIN, 14));
        fullList.setSelectionBackground(new Color(102, 126, 234));
        fullList.setSelectionForeground(Color.WHITE);
        fullList.setLayoutOrientation(JList.VERTICAL);
        fullList.setFixedCellHeight(25);
        fullList.setBackground(new Color(230, 230, 230));
        fullList.setForeground(Color.BLACK);
        fullList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        fullList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fullList.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fullList.setFocusable(true); // Allow focus in dialog
        fullList.setVisibleRowCount(-1); // Show all items
        
        // Add to scroll pane with nice border
        JScrollPane fullScrollPane = new JScrollPane(fullList);
        fullScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));
        fullListDialog.add(fullScrollPane, BorderLayout.CENTER);
        
        // Add button panel with "Apply" and "Close"
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        
        JButton applyButton = new JButton("Apply Selections");
        applyButton.setFont(new Font("Inter", Font.PLAIN, 12));
        applyButton.setBackground(new Color(102, 126, 234));
        applyButton.setForeground(Color.WHITE);
        applyButton.addActionListener(e -> {
            scheduleList.setSelectedIndices(fullList.getSelectedIndices());
            fullListDialog.setVisible(false);
        });
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Inter", Font.PLAIN, 12));
        closeButton.addActionListener(e -> fullListDialog.setVisible(false));
        
        buttonPanel.add(applyButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeButton);
        
        fullListDialog.add(buttonPanel, BorderLayout.SOUTH);
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

    // To get selected schedules later:
    public List<String> getSelectedSchedules() {
        return scheduleList.getSelectedValuesList();
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
        // Check required fields
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

        return true;
    }

    
    private void insertStudent() {
    try {
        generatedStudentId = generateStudentId();
        generatedStudentEmail = generateStudentEmail();
        
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
        stmt.setString(16, String.join(", ", getSelectedSchedules()));

        System.out.println(courseBox.getSelectedItem());

        int result = stmt.executeUpdate();
        stmt.close();
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
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Custom rounded components (matching login form style)
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