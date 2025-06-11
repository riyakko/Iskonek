package iskonek;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GradientPaint;
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

public class StudentInformation extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:iskonek.db"; 
    Connection conn = SQLiteConnector.gConnection();
    ResultSet rs = null;
    PreparedStatement pst = null;
    private String studentId;

    // UI components
    private JPanel contentPane;

    private JTextField[] fields;
    private JLabel nameValue, courseValue, idValue, deptValue, emailValue, dateValue, genderValue, civilValue, 
                   nationalityValue, contactValue, addressValue, guardianNameValue, guardianContactValue;
    private JTextField fullNameField, courseField, idNumberField, departmentField, emailField, dobField, 
                       genderField, civilStatusField, nationalityField, contactNumberField, addressField, 
                       guardianNameField, guardianContactField;
    private Font contentFont;
    private Font headerFont;

    private void initializeDatabase() {
        try {
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
        setTitle("Student Information");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1260, 780);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeFonts();

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

        // Main panel 
        JPanel mainPanel = new JPanel() {
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
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Content panel
        contentPane = new RoundedPanel(0);
        contentPane.setLayout(null); 
        contentPane.setBackground(new Color(255, 255, 255, 245)); 
        contentPane.setPreferredSize(new Dimension(1200, 1300)); 

        // Header label 
        JLabel tabLabel = new JLabel("Student Information");
        tabLabel.setFont(headerFont.deriveFont(Font.ITALIC, 20f));
        tabLabel.setBounds(15, 7, 220, 25);
        contentPane.add(tabLabel);

        // === Add Back Button ===
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        backButton.setBounds(1050, 40, 80, 30);
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(new RoundedBorder(10));
        backButton.addActionListener(e -> {
            this.dispose();
            new StudentDashboard(studentId).setVisible(true);
        });
        contentPane.add(backButton);

        // Load and display profile picture if exists
        File pfpFile = new File(getClass().getResource("/pfp.png").getFile());
        if (pfpFile.exists()) {
            ImageIcon icon = new ImageIcon(pfpFile.getAbsolutePath());
            Image scaledImage = icon.getImage().getScaledInstance(195, 195, Image.SCALE_SMOOTH);
            JLabel userIcon = new JLabel(new ImageIcon(scaledImage));
            userIcon.setBounds(30, 60, 195, 195);
            contentPane.add(userIcon);
        }

        // Load and add ID verification icon
        File idFile = new File(getClass().getResource("/idver.png").getFile());
        if (idFile.exists()) {
            ImageIcon icon = new ImageIcon(idFile.getAbsolutePath());
            Image scaledImage = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            JLabel userIcon = new JLabel(new ImageIcon(scaledImage));
            userIcon.setBounds(160, 90, 180, 180);
            contentPane.add(userIcon);
        }

        // Load and add school icon
        File slFile = new File(getClass().getResource("/school.png").getFile());
        if (slFile.exists()) {
            ImageIcon icon = new ImageIcon(slFile.getAbsolutePath());
            Image scaledImage = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            JLabel userIcon = new JLabel(new ImageIcon(scaledImage));
            userIcon.setBounds(160, 125, 180, 180);
            contentPane.add(userIcon);
        }

        // Load and add mail icon
        File mFile = new File(getClass().getResource("/mail.png").getFile());
        if (mFile.exists()) {
            ImageIcon icon = new ImageIcon(mFile.getAbsolutePath());
            Image scaledImage = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            JLabel userIcon = new JLabel(new ImageIcon(scaledImage));
            userIcon.setBounds(160, 160, 180, 180);
            contentPane.add(userIcon);
        }

        // Title label for student info section
        addBLabel(contentPane, "Students Information", 240, 60, headerFont);
        int baseX = 260, baseY = 100, lineHeight = 35;

        nameValue = addBValue(contentPane, "Student Name", baseX - 20, baseY - 5, contentFont);

        addLabel(contentPane, "Course:", baseX - 20, baseY += lineHeight, headerFont, SwingConstants.LEFT);
        String courseText = (courseValue != null) ? courseValue.getText().trim() : ""; 
        JLabel cLabel = new JLabel(courseText);
        cLabel.setFont(contentFont.deriveFont(14f));
        cLabel.setBounds(baseX + 100, baseY - 2, 600, 25);
        cLabel.setHorizontalAlignment(SwingConstants.LEFT);
        courseValue = cLabel; 
        contentPane.add(cLabel);

        addLabel(contentPane, "ID Number:", baseX + 10, baseY += lineHeight, headerFont, SwingConstants.LEFT);
        String idText = (idValue != null) ? idValue.getText().trim() : "";
        JLabel idLabel = new JLabel(idText);
        idLabel.setFont(contentFont.deriveFont(14f));
        idLabel.setBounds(baseX + 110, baseY - 2, 600, 25);
        idLabel.setHorizontalAlignment(SwingConstants.LEFT);
        idValue = idLabel;
        contentPane.add(idLabel);
        
        addLabel(contentPane, "Department:", baseX + 10, baseY += lineHeight, headerFont, SwingConstants.LEFT);
        deptValue = addValue(contentPane, "School of Engineering, Computing, and Architecture", baseX + 110, baseY - 2, contentFont);

        addLabel(contentPane, "Issued Email:", baseX + 10, baseY += lineHeight, headerFont, SwingConstants.LEFT);
        String emailText = (emailValue != null) ? emailValue.getText().trim() : "";
        JLabel emailLabel = new JLabel(emailText);
        emailLabel.setFont(contentFont.deriveFont(14f));
        emailLabel.setBounds(baseX + 110, baseY - 2, 600, 25);
        emailLabel.setHorizontalAlignment(SwingConstants.LEFT);
        emailValue = emailLabel;
        contentPane.add(emailLabel);

        // Create tabbed pane 
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(20, 280, 1146, 750);

        // Create panel 
        RoundedPanel generalInfoPanel = createGeneralInfoPanel();

        // Add the panel 
        tabbedPane.addTab("General Info", generalInfoPanel);
        contentPane.add(tabbedPane);

        // Scroll pane setup
        JScrollPane scrollPane = new JScrollPane(contentPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void initializeFonts() {
        contentFont = new Font("SansSerif", Font.PLAIN, 14);
        try {
            java.net.URL fontUrl = getClass().getResource("/Merich-YqW6q.otf");
            if (fontUrl != null) {
                File fontFile = new File(fontUrl.getFile());
                headerFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(16f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(headerFont);
            } else {
                throw new IOException("Font file not found.");
            }
        } catch (IOException | FontFormatException e) {
            System.err.println("Custom font not found or invalid. Using default font.");
            headerFont = new Font("SansSerif", Font.BOLD, 16);
        }
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

        this.fields = new JTextField[13];
        
        String[] labels = {
            "Full Name:", "Course:", "ID Number:", "Department:", 
            "Email:", "Date of Birth:", "Gender:", "Civil Status:",
            "Nationality:", "Contact Number:", "Address:", 
            "Guardian Name:", "Guardian Contact No.:"
        };

        // Get student data 
        String query = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = SQLiteConnector.gConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                for (int i = 0; i < labels.length; i++) {
    
                    gbc.gridx = 0;
                    gbc.gridy = i;
                    gbc.weightx = 0;
                    gbc.fill = GridBagConstraints.NONE;
                    
                    JLabel label = new JLabel(labels[i]);
                    label.setPreferredSize(new Dimension(150, 30));
                    panel.add(label, gbc);

                
                    gbc.gridx = 1;
                    gbc.weightx = 1;
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    
                    fields[i] = new JTextField();
                    fields[i].setFont(new Font("SansSerif", Font.PLAIN, 14));
                    fields[i].setPreferredSize(new Dimension(400, 30));
                    fields[i].setBorder(BorderFactory.createCompoundBorder(
                        fields[i].getBorder(),
                        BorderFactory.createEmptyBorder(3, 10, 5, 10)
                    ));

                    switch(i) {
                        case 0: // Full Name
                            fields[i].setText(rs.getString("first_name") + " " + 
                                            rs.getString("middle_name") + " " + 
                                            rs.getString("last_name"));
                            fields[i].setEditable(false);
                            break;
                        case 1: // Course
                            fields[i].setText(rs.getString("student_course"));
                            fields[i].setEditable(false);
                            break;
                        case 2: // ID Number
                            fields[i].setText(studentId);
                            fields[i].setEditable(false);
                            break;
                        case 3: // Department
                            fields[i].setText("School of Engineering, Computing, and Architecture");
                            fields[i].setEditable(false);
                            break;
                        case 4: // Email
                            fields[i].setText(rs.getString("student_email"));
                            fields[i].setEditable(false);
                            break;
                        case 5: // Date of Birth
                            fields[i].setText(rs.getString("date_of_birth"));
                            fields[i].setEditable(false);
                            break;
                        case 6: // Gender
                            fields[i].setText(rs.getString("gender"));
                            fields[i].setEditable(false);
                            break;
                        case 7: // Civil Status
                            fields[i].setText(rs.getString("civil_status"));
                            fields[i].setEditable(false);
                            break;
                        case 8: // Nationality
                            fields[i].setText(rs.getString("nationality"));
                            fields[i].setEditable(false);
                            break;
                        case 9: // Contact Number
                            fields[i].setText(rs.getString("contact_number"));
                            fields[i].setEditable(true);
                            fields[i].addFocusListener(new java.awt.event.FocusAdapter() {
                                public void focusLost(java.awt.event.FocusEvent evt) {
                                    saveContactFields();
                                }
                            });
                            break;
                        case 10: // Address
                            fields[i].setText(rs.getString("address"));
                            fields[i].setEditable(false);
                            break;
                        case 11: // Guardian Name
                            fields[i].setText(rs.getString("guardian_name"));
                            fields[i].setEditable(false);
                            break;
                        case 12: // Guardian Contact
                            fields[i].setText(rs.getString("guardian_contact"));
                            fields[i].setEditable(true);
                            fields[i].addFocusListener(new java.awt.event.FocusAdapter() {
                                public void focusLost(java.awt.event.FocusEvent evt) {
                                    saveContactFields();
                                }
                            });
                            break;
                    }

                    panel.add(fields[i], gbc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading student data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }

        return panel;
    }

    // Save only the contact number and guardian contact number to the database
    private void saveContactFields() {
        try (Connection conn = SQLiteConnector.gConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE students SET contact_number = ?, guardian_contact = ? WHERE student_id = ?")) {
            pstmt.setString(1, fields[9].getText().trim());
            pstmt.setString(2, fields[12].getText().trim());
            pstmt.setString(3, studentId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this,
                "Contact numbers updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error saving contact numbers: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFields() {
        if (fields == null || fields.length < 13) {
            System.err.println("Fields array not properly initialized!");
            return;
        }

        System.out.println("Populating form fields...");
        
        fields[0].setText(nameValue.getText().trim());
        fields[1].setText(courseValue.getText().trim());
        fields[2].setText(idValue.getText().trim());
        fields[3].setText(deptValue.getText().trim());
        fields[4].setText(emailValue.getText().trim());
        fields[5].setText(dateValue.getText().trim());
        fields[6].setText(genderValue.getText().trim());
        fields[7].setText(civilValue.getText().trim());
        fields[8].setText(nationalityValue.getText().trim());
        fields[9].setText(contactValue.getText().trim());
        fields[10].setText(addressValue.getText().trim());
        fields[11].setText(guardianNameValue.getText().trim());
        fields[12].setText(guardianContactValue.getText().trim());

        System.out.println("First field value: " + fields[0].getText());
        System.out.println("Second field value: " + fields[1].getText());
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
        setOpaque(false); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
        graphics.setColor(Color.GRAY);
        graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
    }
}