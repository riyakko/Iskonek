package iskonek;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ScheduleViewer extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Connection connection;
    private String studentId;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;

    public ScheduleViewer(String studentId) {
        this.studentId = studentId;
        initializeDatabase();
        initializeUI();
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:iskonek.db");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    private void initializeUI() {
        setTitle("Student Schedule");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1260, 780);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient background
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background (matching EnrollForm style)
                GradientPaint gradient = new GradientPaint(0, 0, new Color(102, 126, 234), 
                                                         0, getHeight(), new Color(118, 75, 162));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);  // Make header panel transparent
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("My Schedule");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);  // White text for contrast against gradient
        titleLabel.setBounds(50, 25, 600, 50); // Position above mainContentPanel
        contentPane.add(titleLabel);

        String[] columns = {"Course Code", "Subject", "Schedule", "Room"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        scheduleTable = new JTable(tableModel);
        scheduleTable.setRowHeight(30);
        scheduleTable.setFont(new Font("Inter", Font.PLAIN, 16));
        scheduleTable.setGridColor(new Color(200, 200, 200));
        scheduleTable.setShowGrid(true);
        scheduleTable.setIntercellSpacing(new Dimension(1, 1));
        scheduleTable.setForeground(Color.BLACK);
        scheduleTable.setBackground(Color.WHITE);

        // Set preferred column widths
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Course Code
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(180); // Subject
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(500); // Schedule
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(300); // Room

        // Style table header
        JTableHeader header = scheduleTable.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 18));
        header.setBackground(new Color(230, 230, 230));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setOpaque(false);  // Make scroll pane transparent
        scrollPane.getViewport().setOpaque(false);  // Make viewport transparent

        JPanel mainPanel = new RoundedPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 245));  // Semi-transparent white
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        RoundedButton backButtonTop = new RoundedButton("Back", 36);
        backButtonTop.setFont(new Font("Inter", Font.BOLD, 15));
        backButtonTop.setBackground(new Color(255, 255, 255, 220));
        backButtonTop.setForeground(new Color(102, 126, 234));
        backButtonTop.setFocusPainted(false);
        backButtonTop.setBounds(1130, 20, 100, 36);
        backButtonTop.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButtonTop.addActionListener(e -> {
            StudentDashboard dashboard = new StudentDashboard(studentId);
            dashboard.setVisible(true);
            dispose();
        });
        contentPane.add(backButtonTop);
        
        JPanel mainContentPanel = new JPanel(new BorderLayout(20, 20));  // Increased padding between components
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBounds(50, 70, 1160, 600);  // Move down to sit below the label
        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(mainContentPanel);

        scheduleTable.setSelectionBackground(new Color(102, 126, 234));
        scheduleTable.setSelectionForeground(Color.WHITE);

        loadScheduleData();
    }

    private void loadScheduleData() {
        try {
            String query = "SELECT student_schedule, student_course FROM students WHERE student_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String scheduleString = rs.getString("student_schedule");
                String studentCourse = rs.getString("student_course");
                
                System.out.println("\n=== Schedule Viewer Debug Info ===");
                System.out.println("Student ID: " + studentId);
                System.out.println("Student Course: " + studentCourse);
                System.out.println("Raw schedule string from database: [" + scheduleString + "]");
                System.out.println("Schedule string length: " + (scheduleString != null ? scheduleString.length() : 0));
                System.out.println("Schedule string contains comma: " + (scheduleString != null && scheduleString.contains(",")));
                System.out.println("Schedule string contains pipe: " + (scheduleString != null && scheduleString.contains("|")));
                
                setTitle("Student Schedule - " + studentCourse);
                
                if (scheduleString != null && !scheduleString.isEmpty()) {
                    tableModel.setRowCount(0);
                    
                    String[] courses = scheduleString.split("\\|\\|\\|");
                    System.out.println("\nNumber of courses found after split: " + courses.length);
                    System.out.println("Courses array contents:");
                    for (int i = 0; i < courses.length; i++) {
                        System.out.println("  [" + i + "] [" + courses[i] + "]");
                    }
                    
                    for (String course : courses) {
                        try {
                            System.out.println("\nProcessing course: [" + course + "]");
                            String[] parts = course.split(" \\| ");
                            System.out.println("  Parts after | split: " + parts.length);
                            for (int i = 0; i < parts.length; i++) {
                                System.out.println("    [" + i + "] [" + parts[i] + "]");
                            }
                            
                            if (parts.length >= 2) {
                                String courseInfo = parts[0].trim();
                                String scheduleInfo = parts[1].trim();

                                String[] courseParts = courseInfo.split(" - ");
                                System.out.println("  Course parts after - split: " + courseParts.length);
                                for (int i = 0; i < courseParts.length; i++) {
                                    System.out.println("    [" + i + "] [" + courseParts[i] + "]");
                                }
                                
                                if (courseParts.length >= 2) {
                                    String courseCode = courseParts[0].trim();
                                    String subject = courseParts[1].split(":")[0].trim();

                                    // Split scheduleInfo into slots (by comma)
                                    String[] slots = scheduleInfo.split(", ");
                                    List<String> scheduleList = new ArrayList<>();
                                    List<String> roomList = new ArrayList<>();
                                    for (String slot : slots) {
                                        slot = slot.trim();
                                        String slotUpper = slot.toUpperCase();
                                        int roomIdx = -1;

                                        int idxRoom = slotUpper.indexOf("ROOM");
                                        int idxVR = slotUpper.indexOf("VR");
                                        int idxGym = slotUpper.indexOf("GYMNASIUM");
  
                                        if (idxRoom != -1) roomIdx = idxRoom;
                                        if (idxVR != -1 && (roomIdx == -1 || idxVR < roomIdx)) roomIdx = idxVR;
                                        if (idxGym != -1 && (roomIdx == -1 || idxGym < roomIdx)) roomIdx = idxGym;
                                        if (roomIdx != -1) {
                                            String sched = slot.substring(0, roomIdx).trim();
                                            String room = slot.substring(roomIdx).trim();
                                            scheduleList.add(sched);
                                            roomList.add(room);
                                        } else {
                                            scheduleList.add(slot);
                                        }
                                    }
                                    String schedule = String.join(", ", scheduleList);
                                    String room = String.join(", ", roomList);

                                    System.out.println("  Adding to table:");
                                    System.out.println("    Course Code: [" + courseCode + "]");
                                    System.out.println("    Subject: [" + subject + "]");
                                    System.out.println("    Schedule: [" + schedule + "]");
                                    System.out.println("    Room: [" + room + "]");
                                    
                                    tableModel.addRow(new Object[]{courseCode, subject, schedule, room});
                                    System.out.println("  Row added successfully");
                                } else {
                                    System.out.println("  Invalid course format - missing course parts");
                                    System.out.println("  Course info: [" + courseInfo + "]");
                                }
                            } else {
                                System.out.println("  Invalid course format - missing parts after | split");
                                System.out.println("  Course string: [" + course + "]");
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing course entry: " + course);
                            e.printStackTrace();
                        }
                    }
                    
                    System.out.println("\nFinal table row count: " + tableModel.getRowCount());
                } else {
                    System.out.println("No schedule found for student");
                    JOptionPane.showMessageDialog(this, 
                        "No schedule found for this student.", 
                        "No Schedule", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                System.out.println("Student not found in database");
                JOptionPane.showMessageDialog(this, 
                    "Student not found in database.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading schedule: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
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
            if (getModel().isArmed()) {
                g2.setColor(new Color(230, 230, 230));
            } else {
                g2.setColor(getBackground());
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setColor(new Color(102, 126, 234));
            g2.setStroke(new java.awt.BasicStroke(2f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
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