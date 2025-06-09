package iskonek;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

// Nag palagay ako sa AI ng commnets incase need mo i edit sa hard code, goodluck bang!!
public class StudentInformation extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:iskonek.db"; 
    Connection conn = SQLiteConnector.gConnection();
    ResultSet rs = null;
    PreparedStatement pst = null;
    private String studentId;

    // UI components
    private JPanel contentPanel;

    private JTextField[] fields;
    private JLabel nameValue, courseValue, idValue, deptValue, emailValue, dateValue, genderValue, civilValue, 
                   nationalityValue, contactValue, addressValue, guardianNameValue, guardianContactValue;
    private JTextField fullNameField, courseField, idNumberField, departmentField, emailField, dobField, 
                       genderField, civilStatusField, nationalityField, contactNumberField, addressField, 
                       guardianNameField, guardianContactField;

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

    public void getStudentData(String studentId) {
        String query = "SELECT * FROM students WHERE student_id = ?";

        try (Connection conn = SQLiteConnector.gConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // First store all data in variables
                String fullName = rs.getString("first_name") + " " + 
                                rs.getString("middle_name") + " " + 
                                rs.getString("last_name");
                String course = rs.getString("student_course");
                String email = rs.getString("student_email");
                String dob = rs.getString("date_of_birth");
                String gender = rs.getString("gender");
                String civilStatus = rs.getString("civil_status");
                String nationality = rs.getString("nationality");
                String contact = rs.getString("contact_number");
                String address = rs.getString("address");
                String guardianName = rs.getString("guardian_name");
                String guardianContact = rs.getString("guardian_contact");

                // Update UI components on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    if (nameValue != null) nameValue.setText(fullName);
                    if (courseValue != null) courseValue.setText(course);
                    if (idValue != null) idValue.setText(studentId);
                    if (emailValue != null) emailValue.setText(email);
                    if (dateValue != null) dateValue.setText(dob);
                    if (genderValue != null) genderValue.setText(gender);
                    if (civilValue != null) civilValue.setText(civilStatus);
                    if (nationalityValue != null) nationalityValue.setText(nationality);
                    if (contactValue != null) contactValue.setText(contact);
                    if (addressValue != null) addressValue.setText(address);
                    if (guardianNameValue != null) guardianNameValue.setText(guardianName);
                    if (guardianContactValue != null) guardianContactValue.setText(guardianContact);

                    populateFormFields();
                });
            } else {
                System.out.println("No student found with ID: " + studentId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Consider showing an error message to the user
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, 
                    "Error loading student data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            });
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
            
            System.out.println("Students table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    public StudentInformation(String studentId) {
        this.studentId = studentId;
        initializeDatabase();
        InitializeUI();
        createGeneralInfoPanel();
        getStudentData(studentId);
    }

        private void InitializeUI() {
        // Basic JFrame setup
        setTitle("Student Information");
        setSize(1260, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(false);

        nameValue = new JLabel();
        courseValue = new JLabel();
        idValue = new JLabel();
        deptValue = new JLabel("School of Engineering, Computing, and Architecture"); 
        emailValue = new JLabel();
        dateValue = new JLabel();
        genderValue = new JLabel();
        civilValue = new JLabel();
        nationalityValue = new JLabel();
        contactValue = new JLabel();
        addressValue = new JLabel();
        guardianNameValue = new JLabel();
        guardianContactValue = new JLabel();

        fields = new JTextField[13];

        // Fonts used throughout the UI
        Font contentFont = new Font("SansSerif", Font.PLAIN, 14);
        Font headerFont;

        // Attempt to load a custom font from a file, fallback if not found
        try {
            java.net.URL fontUrl = getClass().getResource("/Merich-YqW6q.otf");
            if (fontUrl != null) {
                File fontFile = new File(fontUrl.getFile());
                headerFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(14f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(headerFont);
            } else {
                throw new IOException("Font file not found.");
            }
        } catch (IOException | FontFormatException e) {
            System.err.println("Custom font not found or invalid. Using default font.");
            headerFont = new Font("SansSerif", Font.BOLD, 14);
        }

        // Main panel holds everything with some padding and background color
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(100, 104, 158)); // dark blue background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Content panel with rounded corners and light background
        contentPanel = new RoundedPanel(0);
        contentPanel.setLayout(null); // Absolute positioning for flexibility
        contentPanel.setBackground(new Color(235, 235, 235)); // light gray
        contentPanel.setPreferredSize(new Dimension(1200, 1300)); // large height for scrolling

        // Header label at top-left of content panel
        JLabel tabLabel = new JLabel("STUDENTS DATA");
        tabLabel.setOpaque(true);
        tabLabel.setBackground(new Color(235, 235, 235));
        tabLabel.setForeground(Color.BLACK);
        tabLabel.setFont(headerFont.deriveFont(Font.ITALIC, 15f));
        tabLabel.setBounds(20, 5, 200, 25);
        contentPanel.add(tabLabel);

        // Load and display profile picture if exists
        File pfpFile = new File(getClass().getResource("/pfp.png").getFile());
        if (pfpFile.exists()) {
            ImageIcon icon = new ImageIcon(pfpFile.getAbsolutePath());
            Image scaledImage = icon.getImage().getScaledInstance(195, 195, Image.SCALE_SMOOTH);
            JLabel userIcon = new JLabel(new ImageIcon(scaledImage));
            userIcon.setBounds(30, 60, 195, 195);
            contentPanel.add(userIcon);
        }

        // Load and add ID verification icon
        File idFile = new File(getClass().getResource("/idver.png").getFile());
        if (idFile.exists()) {
            ImageIcon icon = new ImageIcon(idFile.getAbsolutePath());
            Image scaledImage = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            JLabel userIcon = new JLabel(new ImageIcon(scaledImage));
            userIcon.setBounds(160, 90, 180, 180);
            contentPanel.add(userIcon);
        }

        // Load and add school icon
        File slFile = new File(getClass().getResource("/school.png").getFile());
        if (slFile.exists()) {
            ImageIcon icon = new ImageIcon(slFile.getAbsolutePath());
            Image scaledImage = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            JLabel userIcon = new JLabel(new ImageIcon(scaledImage));
            userIcon.setBounds(160, 125, 180, 180);
            contentPanel.add(userIcon);
        }

        // Load and add mail icon
        File mFile = new File(getClass().getResource("/mail.png").getFile());
        if (mFile.exists()) {
            ImageIcon icon = new ImageIcon(mFile.getAbsolutePath());
            Image scaledImage = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            JLabel userIcon = new JLabel(new ImageIcon(scaledImage));
            userIcon.setBounds(160, 160, 180, 180);
            contentPanel.add(userIcon);
        }

        // Title label for student info section
        addBLabel(contentPanel, "Students Information", 240, 60, headerFont);

        // Coordinates and spacing setup for labels and values
        int baseX = 260, baseY = 100, lineHeight = 35;

        // Display student name prominently
        nameValue = addBValue(contentPanel, "Student Name", baseX - 20, baseY - 5, contentFont);

        // Add static labels and corresponding values (course, ID, dept, email)
        addLabel(contentPanel, "Course:", baseX - 20, baseY += lineHeight, headerFont, SwingConstants.LEFT);
        
        
        String courseText = (courseValue != null) ? courseValue.getText().trim() : ""; 
        JLabel cLabel = new JLabel(courseText);
        cLabel.setFont(contentFont.deriveFont(14f));
        cLabel.setBounds(baseX + 100, baseY - 2, 600, 25);
        cLabel.setHorizontalAlignment(SwingConstants.LEFT);
        courseValue = cLabel; 
        contentPanel.add(cLabel);

        addLabel(contentPanel, "ID Number:", baseX + 10, baseY += lineHeight, headerFont, SwingConstants.LEFT);


        String idText = (idValue != null) ? idValue.getText().trim() : "";
        JLabel idLabel = new JLabel(idText);
        idLabel.setFont(contentFont.deriveFont(14f));
        idLabel.setBounds(baseX + 110, baseY - 2, 600, 25);
        idLabel.setHorizontalAlignment(SwingConstants.LEFT);
        idValue = idLabel;
        contentPanel.add(idLabel);
        
        addLabel(contentPanel, "Department:", baseX + 10, baseY += lineHeight, headerFont, SwingConstants.LEFT);
        deptValue = addValue(contentPanel, "School of Engineering, Computing, and Architecture", baseX + 110, baseY - 2, contentFont);

        addLabel(contentPanel, "Issued Email:", baseX + 10, baseY += lineHeight, headerFont, SwingConstants.LEFT);
        
        String emailText = (emailValue != null) ? emailValue.getText().trim() : "";
        JLabel emailLabel = new JLabel(emailText);
        emailLabel.setFont(contentFont.deriveFont(14f));
        emailLabel.setBounds(baseX + 110, baseY - 2, 600, 25);
        emailLabel.setHorizontalAlignment(SwingConstants.LEFT);
        emailValue = emailLabel;
        contentPanel.add(emailLabel);

        // Create tabbed pane to organize information tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(20, 280, 1146, 750);

        // Create panel for general information
        RoundedPanel generalInfoPanel = createGeneralInfoPanel();

        // Add the panel as a tab
        tabbedPane.addTab("General Info", generalInfoPanel);
        contentPanel.add(tabbedPane);

        // Scroll pane setup
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);

        // Add to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

        private RoundedPanel createGeneralInfoPanel() {
        RoundedPanel panel = new RoundedPanel(20);
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(950, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Create individual field variables instead of array
        fullNameField = new JTextField();
        courseField = new JTextField();
        idNumberField = new JTextField();
        departmentField = new JTextField();
        emailField = new JTextField();
        dobField = new JTextField();
        genderField = new JTextField();
        civilStatusField = new JTextField();
        nationalityField = new JTextField();
        contactNumberField = new JTextField();
        addressField = new JTextField();
        guardianNameField = new JTextField();
        guardianContactField = new JTextField();

        // Helper method to add label and field
        addLabelAndField(panel, gbc, "Full Name:", 0);
        addLabelAndField(panel, gbc, "Course:", 1);
        addLabelAndField(panel, gbc, "ID Number:", 2);
        addLabelAndField(panel, gbc, "Department:", 3);
        addLabelAndField(panel, gbc, "Email:", 4);
        addLabelAndField(panel, gbc, "Date of Birth:", 5);
        addLabelAndField(panel, gbc, "Gender:", 6);
        addLabelAndField(panel, gbc, "Civil Status:", 7);
        addLabelAndField(panel, gbc, "Nationality:", 8);
        addLabelAndField(panel, gbc, "Contact Number:", 9);
        addLabelAndField(panel, gbc, "Address:", 10);
        addLabelAndField(panel, gbc, "Guardian Name:", 11);
        addLabelAndField(panel, gbc, "Guardian Contact No.:", 12);

        // Add update button at the end
        gbc.gridx = 1;
        gbc.gridy = 13;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        
        JButton updateBtn = createUpdateButton();
        panel.add(updateBtn, gbc);

        return panel;
    }

    private void populateFormFields() {
        System.out.println("--- Starting populateFormFields ---");
        System.out.println("nameValue text: " + nameValue.getText());
        System.out.println("fullNameField exists: " + (fullNameField != null));

        if (fullNameField == null) return; 
    
        SwingUtilities.invokeLater(() -> {
            System.out.println("Setting fullNameField to: " + nameValue.getText());
            fullNameField.setText(nameValue.getText());
            System.out.println("After setting, fullNameField contains: " + fullNameField.getText());
            
            fullNameField.setText(nameValue.getText());
            courseField.setText(courseValue.getText());
            idNumberField.setText(idValue.getText());
            departmentField.setText(deptValue.getText());
            emailField.setText(emailValue.getText());
            dobField.setText(dateValue.getText());
            genderField.setText(genderValue.getText());
            civilStatusField.setText(civilValue.getText());
            nationalityField.setText(nationalityValue.getText());
            contactNumberField.setText(contactValue.getText());
            addressField.setText(addressValue.getText());
            guardianNameField.setText(guardianNameValue.getText());
            guardianContactField.setText(guardianContactValue.getText());

            Component parent = fullNameField.getParent();
            if (parent != null) {
                parent.revalidate();
                parent.repaint();
            }


            System.out.println("Name from DB: " + nameValue.getText());
            System.out.println("Name in field: " + fullNameField.getText());
        });
    }

    // Helper method to add label and field to panel
    private void addLabelAndField(RoundedPanel panel, GridBagConstraints gbc, String labelText, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(150, 30));
        panel.add(label, gbc);

        // Add text field
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        fields[row] = new JTextField();
        JTextField field = fields[row];
        
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(400, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            field.getBorder(),
            BorderFactory.createEmptyBorder(3, 10, 5, 10)
        ));
        panel.add(field, gbc);
    }

    private JButton createUpdateButton() {
        JButton updateBtn = new JButton("Update Profile");
        Font contentFont = new Font("SansSerif", Font.PLAIN, 14);
        updateBtn.setFont(contentFont.deriveFont(Font.BOLD));
        updateBtn.setBorder(new RoundedBorder(10));
        updateBtn.setContentAreaFilled(true);
        updateBtn.setFocusPainted(false);
        updateBtn.setOpaque(true);
        updateBtn.setBackground(new Color(180, 200, 250));
        
        updateBtn.addActionListener(e -> handleProfileUpdate());
        
        return updateBtn;
    }

    // Handles the profile update button click
    private void handleProfileUpdate() {
        if (!validateFormFields()) {
            return;
        }
        
        try {
            updateUILabels();
            updateStudentInDatabase();
            
            JOptionPane.showMessageDialog(this, 
                "Profile updated successfully",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error updating profile: " + ex.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Validates the form fields before updating
    private boolean validateFormFields() {
        if (fields == null || fields.length < 13) {
            JOptionPane.showMessageDialog(this, 
                "Form fields not initialized",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Example validations - customize as needed
        if (fields[0].getText().trim().isEmpty()) {
            showValidationError("Full name cannot be empty");
            return false;
        }
        
        if (!fields[4].getText().trim().matches("^.+@.+\\..+$")) {
            showValidationError("Please enter a valid email address");
            return false;
        }
        
        return true;
    }

    // Updates the UI labels with current field values
    private void updateUILabels() {
        nameValue.setText(fullNameField.getText().trim());
        courseValue.setText(courseField.getText().trim());
        idValue.setText(idNumberField.getText().trim());
        deptValue.setText(departmentField.getText().trim());
        emailValue.setText(emailField.getText().trim());
        dateValue.setText(dobField.getText().trim());
        genderValue.setText(genderField.getText().trim());
        civilValue.setText(civilStatusField.getText().trim());
        nationalityValue.setText(nationalityField.getText().trim());
        contactValue.setText(contactNumberField.getText().trim());
        addressValue.setText(addressField.getText().trim());
        guardianNameValue.setText(guardianNameField.getText().trim());
        guardianContactValue.setText(guardianContactField.getText().trim());
    }

    /**
     * Shows a validation error message
     */
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Validation Error",
            JOptionPane.WARNING_MESSAGE);
    }

    private void updateStudentInDatabase() throws SQLException {
        String updateQuery = "UPDATE students SET " +
            "first_name = ?, middle_name = ?, last_name = ?, " +
            "student_course = ?, student_email = ?, date_of_birth = ?, " +
            "gender = ?, civil_status = ?, nationality = ?, " +
            "contact_number = ?, address = ?, guardian_name = ?, " +
            "guardian_contact = ? WHERE student_id = ?";

        try (Connection conn = SQLiteConnector.gConnection();
            PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            // Parse full name into components
            String[] nameParts = fields[0].getText().trim().split(" ");
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String middleName = nameParts.length > 2 ? nameParts[1] : "";
            String lastName = nameParts.length > 1 ? 
                            nameParts[nameParts.length-1] : "";

            // Set parameters
            pstmt.setString(1, firstName);
            pstmt.setString(2, middleName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, fields[1].getText().trim()); // course
            pstmt.setString(5, fields[4].getText().trim()); // email
            pstmt.setString(6, fields[5].getText().trim()); // dob
            pstmt.setString(7, fields[6].getText().trim()); // gender
            pstmt.setString(8, fields[7].getText().trim()); // civil status
            pstmt.setString(9, fields[8].getText().trim()); // nationality
            pstmt.setString(10, fields[9].getText().trim()); // contact
            pstmt.setString(11, fields[10].getText().trim()); // address
            pstmt.setString(12, fields[11].getText().trim()); // guardian name
            pstmt.setString(13, fields[12].getText().trim()); // guardian contact
            pstmt.setString(14, studentId); // WHERE condition

            // Execute update
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("No student found with ID: " + studentId);
            }
        }
    }

    // Helper method to add a left-aligned label at specific position with font
    private void addLabel(JPanel panel, String text, int x, int y, Font font, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(font.deriveFont(14f));
        label.setBounds(x, y, 120, 25);
        panel.add(label);
    }

    // Helper method to add a bold label at specific position with font
    private void addBLabel(JPanel panel, String text, int x, int y, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font.deriveFont(Font.BOLD, 14f));
        label.setBounds(x, y, 300, 25);
        panel.add(label);
    }

    // Helper method to add a standard value label at specific position
    private JLabel addValue(JPanel panel, String text, int x, int y, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font.deriveFont(14f));
        label.setBounds(x, y, 600, 25);
        panel.add(label);
        return label;
    }

    // Helper method to add a large bold value label (for name display)
    private JLabel addBValue(JPanel panel, String text, int x, int y, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font.deriveFont(32f));
        label.setBounds(x, y, 600, 35);
        panel.add(label);
        return label;
    }

       

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Provide a sample or test student ID here
            new StudentInformation("2025-1000").setVisible(true);
        });
    }
}


// === Custom rounded border for buttons ===
class RoundedBorder implements javax.swing.border.Border {
    private int radius;

    RoundedBorder(int radius) {
        this.radius = radius;
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(radius + 1, radius + 1, radius + 2, radius);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.GRAY);
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
}

// === Custom JPanel with rounded corners for nicer UI panels ===
class RoundedPanel extends JPanel {
    private int cornerRadius;

    public RoundedPanel(int radius) {
        super();
        this.cornerRadius = radius;
        setOpaque(false); // let paintComponent handle background drawing
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        // Enable anti-aliasing for smooth corners
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw background rounded rectangle
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
        // Draw border around the panel
        graphics.setColor(Color.GRAY);
        graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
    }
}