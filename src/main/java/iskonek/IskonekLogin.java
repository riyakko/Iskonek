package iskonek;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import iskonek.EnrollForm;
import iskonek.StudentDashboard;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;


public class IskonekLogin extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:iskonek.db"; 
    Connection conn = SQLiteConnector.gConnection();
    ResultSet rs = null;
    PreparedStatement pst = null;

    
    private RoundedTextField studentIdField;
    private RoundedPasswordField passwordField;
    private JTextArea ocrResultArea;
    private JLabel statusLabel;
    private RoundedButton loginButton, enrollButton;
    private JPanel uploadPanel;
    private JScrollPane ocrScrollPane;
    
    // Custom rounded panel class
    static class RoundedPanel extends JPanel {
        private int radius;
        private Color backgroundColor;
        
        public RoundedPanel(int radius, Color backgroundColor) {
            this.radius = radius;
            this.backgroundColor = backgroundColor;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Custom rounded button class
    static class RoundedButton extends JButton {
        private int radius;
        private Color bgColor;
        private Color hoverColor;
        private boolean isHovered = false;
        
        public RoundedButton(String text, int radius, Color bgColor, Color hoverColor) {
            super(text);
            this.radius = radius;
            this.bgColor = bgColor;
            this.hoverColor = hoverColor;
            
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Inter", Font.BOLD, 16));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color currentColor = isHovered ? hoverColor : bgColor;
            if (!isEnabled()) {
                currentColor = new Color(150, 150, 150);
            }
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Custom rounded text field class
    static class RoundedTextField extends JTextField {
        private int radius;
        private String placeholder;
        private boolean showingPlaceholder = true;
        
        public RoundedTextField(int radius, String placeholder) {
            this.radius = radius;
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(new EmptyBorder(15, 20, 15, 20));
            setFont(new Font("Inter", Font.PLAIN, 14));
            setForeground(new Color(100, 100, 100));
            setText(placeholder);
            
            addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (showingPlaceholder) {
                        setText("");
                        setForeground(Color.BLACK);
                        showingPlaceholder = false;
                    }
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (getText().isEmpty()) {
                        setForeground(new Color(100, 100, 100));
                        setText(placeholder);
                        showingPlaceholder = true;
                    }
                }
            });
        }
        
        @Override
        public String getText() {
            return showingPlaceholder ? "" : super.getText();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(new Color(230, 230, 230));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Custom rounded password field class
    static class RoundedPasswordField extends JPasswordField {
        private int radius;
        private String placeholder;
        private boolean showingPlaceholder = true;
        
        public RoundedPasswordField(int radius, String placeholder) {
            this.radius = radius;
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(new EmptyBorder(15, 20, 15, 20));
            setFont(new Font("Inter", Font.PLAIN, 14));
            setForeground(new Color(100, 100, 100));
            setText(placeholder);
            setEchoChar((char) 0);
            
            addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (showingPlaceholder) {
                        setText("");
                        setEchoChar('•');
                        setForeground(Color.BLACK);
                        showingPlaceholder = false;
                    }
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (getPassword().length == 0) {
                        setForeground(new Color(100, 100, 100));
                        setText(placeholder);
                        setEchoChar((char) 0);
                        showingPlaceholder = true;
                    }
                }
            });
        }
        
        @Override
        public char[] getPassword() {
            return showingPlaceholder ? new char[0] : super.getPassword();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(new Color(230, 230, 230));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public IskonekLogin() {

        conn = SQLiteConnector.gConnection();
        initializeComponents();
    }
    
    private void initializeComponents() {
            setTitle("ISKonek Login");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1260, 780);
            setLocationRelativeTo(null);
            setResizable(false);
            
            // Create gradient background panel
            JPanel backgroundPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(102, 126, 234),
                        getWidth(), getHeight(), new Color(118, 75, 162)
                    );
                    g2.setPaint(gradient);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            };
            backgroundPanel.setLayout(new GridBagLayout());
            
            // Main container with rounded corners
            RoundedPanel mainPanel = new RoundedPanel(40, new Color(255, 255, 255, 245));
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(new EmptyBorder(50, 40, 50, 40));
            mainPanel.setPreferredSize(new Dimension(597, 640));
            
            // Title
            JLabel titleLabel = new JLabel("ISKOnek");
            titleLabel.setFont(new Font("Inter", Font.BOLD, 64));
            titleLabel.setForeground(new Color(102, 126, 234));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Subtitle
            JLabel subtitleLabel = new JLabel("Mabilis. Sigurado. Iskonekado.");
            subtitleLabel.setFont(new Font("Inter", Font.ITALIC, 16));
            subtitleLabel.setForeground(new Color(120, 120, 120));
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Student ID field
            studentIdField = new RoundedTextField(25, "Student ID");
            studentIdField.setMaximumSize(new Dimension(443, 68));
            studentIdField.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Password field
            passwordField = new RoundedPasswordField(25, "Password");
            passwordField.setMaximumSize(new Dimension(443, 68));
            passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Login button
            loginButton = new RoundedButton("Login", 25, 
                new Color(102, 126, 234), new Color(85, 105, 200));
            loginButton.setMaximumSize(new Dimension(443, 68));
            loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            loginButton.addActionListener(e -> handleLogin());
            

            // Enroll Now button
            enrollButton = new RoundedButton("Enroll Now", 25, 
                new Color(34, 197, 94), new Color(22, 163, 74));
            enrollButton.setMaximumSize(new Dimension(443, 68));
            enrollButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            enrollButton.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    new EnrollForm().setVisible(true);
                });
                System.out.println("Enroll button clicked!");
                this.dispose(); // Close login window
            });
            
            // Upload ID section
            uploadPanel = new JPanel();
            uploadPanel.setOpaque(false);
            uploadPanel.setLayout(new BoxLayout(uploadPanel, BoxLayout.Y_AXIS));
            
            JPanel idIcon = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(100, 150, 120));
                    g2.fillRoundRect(10, 5, 40, 30, 5, 5);
                    g2.setColor(new Color(80, 130, 100));
                    g2.drawRoundRect(10, 5, 40, 30, 5, 5);
                    // Draw lines to represent text
                    g2.setColor(Color.WHITE);
                    g2.fillRect(15, 12, 20, 2);
                    g2.fillRect(15, 18, 15, 2);
                    g2.fillRect(15, 24, 25, 2);
                    g2.dispose();
                }
            };
            idIcon.setPreferredSize(new Dimension(60, 40));
            idIcon.setMaximumSize(new Dimension(60, 40));
            idIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            idIcon.setOpaque(false);
            
            JLabel uploadLabel = new JLabel("Upload your ID");
            uploadLabel.setFont(new Font("Inter", Font.BOLD, 14));
            uploadLabel.setForeground(new Color(100, 100, 100));
            uploadLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            uploadPanel.add(idIcon);
            uploadPanel.add(Box.createVerticalStrut(10));
            uploadPanel.add(uploadLabel);
            
            // Add click listener to upload panel
            uploadPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleFileUpload();
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    uploadPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            });
            
            // OCR Results Area
            ocrResultArea = new JTextArea(3, 30);
            ocrResultArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
            ocrResultArea.setEditable(false);
            ocrResultArea.setBackground(new Color(243, 244, 246));
            ocrResultArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            
            ocrScrollPane = new JScrollPane(ocrResultArea);
            ocrScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            ocrScrollPane.setBorder(BorderFactory.createTitledBorder("Extracted Information"));
            ocrScrollPane.setVisible(false);
            
            // Status Label
            statusLabel = new JLabel(" ");
            statusLabel.setFont(new Font("Inter", Font.PLAIN, 12));
            statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            statusLabel.setOpaque(true);
            statusLabel.setBorder(new EmptyBorder(8, 12, 8, 12));
            
            // Add components to main panel
            mainPanel.add(titleLabel);
            mainPanel.add(Box.createVerticalStrut(10));
            mainPanel.add(subtitleLabel);
            mainPanel.add(Box.createVerticalStrut(40));
            mainPanel.add(studentIdField);
            mainPanel.add(Box.createVerticalStrut(20));
            mainPanel.add(passwordField);
            mainPanel.add(Box.createVerticalStrut(30));
            mainPanel.add(loginButton);
            mainPanel.add(Box.createVerticalStrut(20));
            mainPanel.add(enrollButton);
            mainPanel.add(Box.createVerticalStrut(30));
            mainPanel.add(uploadPanel);
            mainPanel.add(Box.createVerticalStrut(15));
            mainPanel.add(ocrScrollPane);
            mainPanel.add(Box.createVerticalStrut(10));
            mainPanel.add(statusLabel);
            
            backgroundPanel.add(mainPanel);
            add(backgroundPanel);
        }
        
        private void handleLogin() {
            String studentId = studentIdField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (studentId.isEmpty() || password.isEmpty()) {
                showStatus("Please fill in all fields.", Color.RED);
                return;
            }
            
            // Disable login button during processing
            loginButton.setEnabled(false);
            loginButton.setText("Logging in...");
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                private String loggedInStudentId = studentId;
                
                @Override
                protected Boolean doInBackground() throws Exception {
                    Thread.sleep(1000); // Simulate network delay
                    return authenticateUser(loggedInStudentId, password);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            String userName = getUserName(loggedInStudentId);
                            showStatus("Welcome, " + userName + "! Login successful.", new Color(34, 197, 94));
                            System.out.println("Logging in student ID: " + loggedInStudentId);
                            openDashboard(loggedInStudentId);
                        } else {
                            showStatus("Invalid Student ID or Password.", Color.RED);
                        }
                    } catch (Exception e) {
                        showStatus("Login failed: " + e.getMessage(), Color.RED);
                        e.printStackTrace();
                    }

                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            };
            worker.execute();
        }

        private void openDashboard(String studentId) {
            System.out.println("Opening dashboard with student ID: " + studentId); 
            SwingUtilities.invokeLater(() -> {
                try {
                    new StudentDashboard(studentId).setVisible(true);
                    this.dispose(); 
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
        
        private boolean authenticateUser(String studentId, String password) {
            try {
                String query = "SELECT COUNT(*) FROM students WHERE student_id = ? AND password = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, studentId);
                pstmt.setString(2, password);
                
                ResultSet rs = pstmt.executeQuery();
                boolean authenticated = rs.next() && rs.getInt(1) > 0;
                
                rs.close();
                pstmt.close();
                
                System.out.println("Authentication attempt for: " + studentId + " - " + (authenticated ? "SUCCESS" : "FAILED"));
                return authenticated;
            } catch (SQLException e) {
                showError("Authentication error: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        private String getUserName(String studentId) {
            try {
                String query = "SELECT first_name, last_name FROM students WHERE student_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, studentId);
                
                ResultSet rs = pstmt.executeQuery();
                String name = "Student";
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    name = firstName + " " + lastName;
                }
                
                rs.close();
                pstmt.close();
                
                return name;
            } catch (SQLException e) {
                System.err.println("Error getting user name: " + e.getMessage());
                return "Student";
            }
        }
        
        private void handleFileUpload() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "bmp"));
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                processImage(selectedFile);
            }
        }
        
        private void processImage(File imageFile) {
            showStatus("Processing ID... Please wait.", new Color(59, 130, 246));
            
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return performOCR(imageFile);
                }
                
                @Override
                protected void done() {
                    try {
                        String ocrResult = get();
                        ocrResultArea.setText(ocrResult);
                        ocrScrollPane.setVisible(true);
                        
                        String extractedId = extractStudentId(ocrResult);
                        if (extractedId != null) {
                            studentIdField.setText("");
                            studentIdField.setForeground(Color.BLACK);
                            studentIdField.setText(extractedId);
                            showStatus("ID scanned successfully! Student ID extracted: " + extractedId, new Color(34, 197, 94));
                        } else {
                            showStatus("Could not extract Student ID from image.", new Color(255, 140, 0));
                        }
                        
                    } catch (Exception e) {
                        showStatus("Error processing image: " + e.getMessage(), Color.RED);
                    }
                }
            };
            worker.execute();
        }
        
        private String performOCR(File imageFile) {
            try {
                Tesseract tesseract = new Tesseract();
                tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata"); 
                tesseract.setLanguage("eng");
                
                BufferedImage image = ImageIO.read(imageFile);
                String result = tesseract.doOCR(image);
                return result;
            } catch (TesseractException e) {
                return generateMockOCRResult(imageFile.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read image file", e);
            }
        }
        
        private String generateMockOCRResult(String fileName) {
            // Mock OCR result 
            return "UNIVERSITY OF THE PHILIPPINES\n" +
                "STUDENT IDENTIFICATION CARD\n" +
                "STUDENT ID: 2024-1000\n" +
                "NAME: JUAN DELA CRUZ\n" +
                "PROGRAM: COMPUTER SCIENCE\n" +
                "COLLEGE: COLLEGE OF ENGINEERING\n" +
                "VALID UNTIL: 2028\n" +
                "\n(Note: This is mock data as Tesseract may not be properly configured)";
        }
        
        private String extractStudentId(String ocrText) {
            // Try multiple patterns to extract student ID
            String[] patterns = {
                "STUDENT ID[:\\s]*(\\d{4}-\\d+)",
                "ID[:\\s]*(\\d{4}-\\d+)",
                "STUDENT NO[:\\s]*(\\d{4}-\\d+)",
                "(\\d{4}-\\d{3,6})" // General pattern for year-number format
            };
            
            for (String patternStr : patterns) {
                Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(ocrText);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
            return null;
        }
        
        private void showStatus(String message, Color color) {
            statusLabel.setText(message);
            statusLabel.setForeground(color);
            statusLabel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            
            Timer timer = new Timer(5000, e -> statusLabel.setText(" "));
            timer.setRepeats(false);
            timer.start();
        }
        
        private void showError(String message) {
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        public void refreshDatabaseConnection() {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
                conn = DriverManager.getConnection(DB_URL);
                System.out.println("Database connection refreshed");
            } catch (SQLException e) {
                showError("Failed to refresh database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        @Override
        public void dispose() {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("Database connection closed");
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
            
            SwingUtilities.invokeLater(() -> {
                new IskonekLogin().setVisible(true);
            });
        }
    }