# Iskonek
ISKOnek SIS (Student Information System)
Overview
ISKOnek SIS is a streamlined desktop application designed to manage student administration, academic processes, and student services. Built in a two-week sprint, it provides a centralized platform for students to view personal information, payment transactions, class schedules, and upload IDs with text extraction using Tesseract OCR.

**Features**
- Login Page: Sign in using provided ID, enrollment page (input user information to get the email and ID provided), upload ID (Upload student ID images, extract text using Tesseract OCR, and validate against stored records).
- Enrollment Page: Input user data.
- My Profile: View and manage personal details, including name, contact info, and guardian information.
- My Student Ledger: Display payment transactions and account balance with the Accounting office.
- My Schedule Viewer: Visualize weekly class schedules (Sun-Sat, 6AM-9PM) with subject details (code & section).
  
**Tech Stack**
- Java: JDK 17+ for core application logic.
- MySQLite: Lightweight database for storing student data.
- Maven: Dependency and build management.
- Tesseract OCR (Tess4J): Text extraction from uploaded ID images.
- JavaFX (or Swing): GUI for user interface.
  
**Prerequisites**
- Java Development Kit (JDK) 17 or higher
- Maven 3.8+
- MySQLite
- Tesseract OCR (with Tess4J library for Java integration)
- Git
  
**Installation**
1. **Clone the Repository:**
```
bash
git clone https://github.com/riyakko/iskonek.git
cd iskonek
```
3. **Install Tesseract OCR:**
- Windows: Download and install Tesseract from GitHub.
- Linux/macOS: Install via package manager:
```
bash
sudo apt-get install tesseract-ocr  # Ubuntu/Debian
brew install tesseract  # macOS
```
3. **Set Up Maven Dependencies:**
- Ensure pom.xml includes:
```
xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.46.0</version>
</dependency>
<dependency>
    <groupId>net.sourceforge.tess4j</groupId>
    <artifactId>tess4j</artifactId>
    <version>5.13.0</version>
</dependency>
```

- Run:
bash
mvn clean install

4. **Configure MySQLite:**
- Initialize the database with provided SQL scripts in oop_project/lib.
- Update database path in src/main/java/SQLiteConnector.java if needed.
  
5. **Run the Application:**
bash
- mvn exec:java

**Usage**
1. **Launch the Application:**
- Run the JAR file or use the Maven command above.
- Log in using a valid Student ID (test data available in iskonek.db); enroll if ID is not available.
2. **My Profile:**
- View/edit personal details (e.g., name, contact, guardian info).
3. **My Student Ledger:**
- Check payment history and balance.
4. **My Schedule Viewer:**
- View weekly class schedule in a table.
5. **Upload ID:**
- Upload a student ID image (e.g., PNG, JPEG).
- View extracted text (e.g., Student ID, Name) via Tesseract OCR.
- Validate and save to MySQLite.

**Database Schema**
- MyProfile: Stores student details (LastName, FirstName, BirthDate, etc.).
- StudentLedger: Tracks StudentID, Name, Course, Balance.
- ScheduleViewer: Stores SubjectCode, Section, Day, Time.
- UserID: Stores StudentID, IDImage, ExtractedText, UploadStatus.
  
**Project Structure**
```text
iskonek-sif/
├── src/
│   ├── main/
│   │   ├── java/iskonek
│   │   │   ├── EnrollForm.java
│   │   │   ├── IskonekLogin.java
│   │   │   ├── Ledger.java
│   │   │   ├── ScheduleViewer.java
│   │   │   ├── SQLiteConnector.java
│   │   │   ├── StudentDashboard.java
│   │   │   ├── StudentInformation.java 
│   │   ├── resources/
│   │   │   ├── Assets (PNG & OTF)
├── pom.xml
├── iskonek.db
├── README.md
...
```

Contributing
1. Fork the repository.
2. Create a feature branch (git checkout -b feature/<feature-name>).
3. Commit changes (git commit -m "Add <feature>").
4. Push to the branch (git push origin feature/<feature-name>).
5. Open a pull request with a clear description.

Known Issues
- Tesseract OCR accuracy depends on image quality; ensure clear, high-contrast ID images.
- Limited testing due to two-week timeline; report bugs via GitHub Issues.
  
Future Enhancements
- Support mobile/web interfaces.
- Improve OCR accuracy with advanced preprocessing.

Contact
For issues or suggestions, open a GitHub Issue or contact the team at marianost@students.nu-dasma.edu.ph
