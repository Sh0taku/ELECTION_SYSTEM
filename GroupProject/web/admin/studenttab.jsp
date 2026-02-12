<%--
Document: studenttab
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.beans.Admin" %>
<%@page import="com.election.beans.Student" %>
<%@page import="java.util.*" %>
<%@page import="java.sql.*" %>
<%@page import="com.election.dao.DBConnection" %>
<%
// Check admin session
Admin admin = (Admin) session.getAttribute("admin");
if (admin == null) {
    response.sendRedirect("../login.jsp");
    return;
}

String message = "";
String messageType = "";

// Handle CRUD operations
String action = request.getParameter("action");
String studentId = request.getParameter("studentId");

Connection conn = null;
PreparedStatement pstmt = null;
Statement stmt = null;
ResultSet rs = null;

if (action != null) {
    try {
        conn = DBConnection.getConnection();
        
        if ("delete".equals(action) && studentId != null) {
            // DELETE STUDENT (check if candidate first)
            String checkCandidate = "SELECT CANDIDATE_STATUS FROM STUDENTS WHERE STUDENT_ID = ?";
            pstmt = conn.prepareStatement(checkCandidate);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String candidateStatus = rs.getString("CANDIDATE_STATUS");
                if ("CANDIDATE".equals(candidateStatus)) {
                    message = "Cannot delete a candidate! Remove candidate status first.";
                    messageType = "error";
                } else {
                    String sql = "DELETE FROM STUDENTS WHERE STUDENT_ID = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, studentId);
                    
                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        message = "Student deleted successfully!";
                        messageType = "success";
                    }
                }
            }
            
        } else if ("add".equals(action)) {
            // ADD NEW STUDENT
            String newStudentId = request.getParameter("studentId");
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String faculty = request.getParameter("faculty");
            String candidateStatus = request.getParameter("candidateStatus");
            
            // Check if student already exists
            String checkSql = "SELECT STUDENT_ID FROM STUDENTS WHERE STUDENT_ID = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, newStudentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                message = "Student ID already exists!";
                messageType = "error";
            } else {
                String insertSql = "INSERT INTO STUDENTS (STUDENT_ID, NAME, EMAIL, PASSWORD, FACULTY, HAS_VOTED, CANDIDATE_STATUS) VALUES (?, ?, ?, ?, ?, 0, ?)";
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setString(1, newStudentId);
                pstmt.setString(2, name);
                pstmt.setString(3, email);
                pstmt.setString(4, password);
                pstmt.setString(5, faculty);
                pstmt.setString(6, candidateStatus);
                
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    message = "Student added successfully!";
                    messageType = "success";
                }
            }
            
        } else if ("update".equals(action)) {
            // UPDATE STUDENT
            String updateStudentId = request.getParameter("studentId");
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String faculty = request.getParameter("faculty");
            String candidateStatus = request.getParameter("candidateStatus");
            
            String updateSql = "UPDATE STUDENTS SET NAME = ?, EMAIL = ?, FACULTY = ?, CANDIDATE_STATUS = ? WHERE STUDENT_ID = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, faculty);
            pstmt.setString(4, candidateStatus);
            pstmt.setString(5, updateStudentId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Student updated successfully!";
                messageType = "success";
            }
        } else if ("resetPassword".equals(action) && studentId != null) {
            // RESET PASSWORD
            String newPassword = request.getParameter("newPassword");
            
            String updateSql = "UPDATE STUDENTS SET PASSWORD = ? WHERE STUDENT_ID = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, newPassword);
            pstmt.setString(2, studentId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Password reset successfully!";
                messageType = "success";
            }
        }
        
    } catch (SQLException e) {
        message = "Database error: " + e.getMessage();
        messageType = "error";
        e.printStackTrace();
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
}

// Fetch students for display
List<Student> students = new ArrayList<Student>();

try {
    conn = DBConnection.getConnection();
    stmt = conn.createStatement();
    
    // Get all students
    String studentSql = "SELECT * FROM STUDENTS ORDER BY STUDENT_ID";
    rs = stmt.executeQuery(studentSql);
    
    while (rs.next()) {
        Student student = new Student();
        student.setStudentId(rs.getString("STUDENT_ID"));
        student.setName(rs.getString("NAME"));
        student.setEmail(rs.getString("EMAIL"));
        student.setFaculty(rs.getString("FACULTY"));
        student.setHasVoted(rs.getBoolean("HAS_VOTED"));
        student.setCandidateStatus(rs.getString("CANDIDATE_STATUS"));
        students.add(student);
    }
    
} catch (SQLException e) {
    message = "Error fetching data: " + e.getMessage();
    messageType = "error";
    e.printStackTrace();
} finally {
    try { if (rs != null) rs.close(); } catch (Exception e) {}
    try { if (stmt != null) stmt.close(); } catch (Exception e) {}
    try { if (conn != null) conn.close(); } catch (Exception e) {}
}
%>

<!DOCTYPE html>
<html>
<head>
    <title>Manage Students - UITM Election System</title>
    <style>
        /* Same CSS structure as other tabs */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }

        body {
            background-color: #F9F7F7;
            display: flex;
            height: 100vh;
        }

        /* Left Navigation Panel */
        .nav-panel {
            width: 250px;
            background-color: #112D4E;
            color: white;
            padding: 20px;
        }

        .admin-info {
            padding-bottom: 20px;
            border-bottom: 1px solid #3F72AF;
            margin-bottom: 20px;
        }

        .admin-name {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .admin-id {
            color: #DBE2EF;
            font-size: 14px;
        }

        .nav-links {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .nav-link {
            color: #DBE2EF;
            text-decoration: none;
            padding: 12px 15px;
            border-radius: 5px;
            transition: all 0.3s;
        }

        .nav-link:hover, .nav-link.active {
            background-color: #3F72AF;
            color: white;
        }

        /* Main Content Area */
        .main-content {
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        /* Top Header */
        .top-header {
            background-color: white;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }

        .page-title {
            color: #112D4E;
            font-size: 20px;
        }

        .logout-btn {
            padding: 8px 20px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
        }

        .logout-btn:hover {
            background-color: #112D4E;
        }

        /* Management Content */
        .management-content {
            padding: 30px;
            overflow-y: auto;
            flex: 1;
        }

        /* Message Alert */
        .message-alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            text-align: center;
        }

        .success {
            background-color: #e1f7e1;
            color: #2e7d32;
            border: 1px solid #2e7d32;
        }

        .error {
            background-color: #ffebee;
            color: #c62828;
            border: 1px solid #c62828;
        }

        /* Action Bar */
        .action-bar {
            background-color: white;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }

        .search-box {
            display: flex;
            gap: 10px;
            flex: 1;
            max-width: 400px;
        }

        .search-input {
            flex: 1;
            padding: 10px;
            border: 1px solid #DBE2EF;
            border-radius: 5px;
        }

        .search-btn {
            padding: 10px 20px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        .search-btn:hover {
            background-color: #112D4E;
        }

        .add-btn {
            padding: 10px 20px;
            background-color: #2e7d32;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
        }

        .add-btn:hover {
            background-color: #1b5e20;
        }

        /* Data Table */
        .data-table {
            width: 100%;
            background-color: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }

        .table-header {
            background-color: #112D4E;
            color: white;
            padding: 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .table-title {
            font-size: 18px;
            font-weight: bold;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background-color: #DBE2EF;
            color: #112D4E;
            padding: 15px;
            text-align: left;
            font-weight: bold;
            border-bottom: 2px solid #3F72AF;
        }

        td {
            padding: 15px;
            border-bottom: 1px solid #F9F7F7;
            color: #112D4E;
        }

        tr:hover {
            background-color: #F9F7F7;
        }

        .actions-cell {
            display: flex;
            gap: 5px;
            flex-wrap: wrap;
        }

        .edit-btn {
            padding: 5px 10px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 11px;
        }

        .edit-btn:hover {
            background-color: #112D4E;
        }

        .delete-btn {
            padding: 5px 10px;
            background-color: #c62828;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 11px;
        }

        .delete-btn:hover {
            background-color: #b71c1c;
        }

        .password-btn {
            padding: 5px 10px;
            background-color: #ff9800;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 11px;
        }

        .password-btn:hover {
            background-color: #f57c00;
        }

        /* Status badges */
        .badge {
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: bold;
            display: inline-block;
        }
        
        .badge-voted {
            background-color: #e1f7e1;
            color: #2e7d32;
            border: 1px solid #2e7d32;
        }
        
        .badge-not-voted {
            background-color: #fff3e0;
            color: #ef6c00;
            border: 1px solid #ef6c00;
        }
        
        .badge-candidate {
            background-color: #cce5ff;
            color: #004085;
            border: 1px solid #004085;
        }
        
        .badge-not-candidate {
            background-color: #e2e3e5;
            color: #383d41;
            border: 1px solid #383d41;
        }

        .no-data {
            text-align: center;
            padding: 30px;
            color: #3F72AF;
        }

        /* Modal Styles */
        .modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
            z-index: 1000;
            justify-content: center;
            align-items: center;
        }

        .modal-content {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            width: 500px;
            max-width: 90%;
        }

        .modal-title {
            color: #112D4E;
            margin-bottom: 20px;
            font-size: 20px;
            border-bottom: 2px solid #3F72AF;
            padding-bottom: 10px;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-label {
            display: block;
            margin-bottom: 5px;
            color: #112D4E;
            font-weight: bold;
        }

        .form-input, .form-select {
            width: 100%;
            padding: 10px;
            border: 1px solid #DBE2EF;
            border-radius: 5px;
            font-size: 14px;
        }

        .form-row {
            display: flex;
            gap: 15px;
        }

        .form-row .form-group {
            flex: 1;
        }

        .modal-buttons {
            display: flex;
            gap: 10px;
            margin-top: 25px;
        }

        .save-btn {
            flex: 1;
            padding: 12px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
        }

        .save-btn:hover {
            background-color: #112D4E;
        }

        .cancel-btn {
            flex: 1;
            padding: 12px;
            background-color: #DBE2EF;
            color: #112D4E;
            border: 1px solid #3F72AF;
            border-radius: 5px;
            cursor: pointer;
        }

        .cancel-btn:hover {
            background-color: #cccccc;
        }
    </style>
</head>
<body>
    <!-- Left Navigation Panel -->
    <div class="nav-panel">
        <div class="admin-info">
            <div class="admin-name"><%= admin.getUsername() %></div>
            <div class="admin-id">Admin ID: <%= admin.getAdminId() %></div>
        </div>

        <div class="nav-links">
            <a href="admindashboard.jsp" class="nav-link">Dashboard</a>
            <a href="studenttab.jsp" class="nav-link active">Manage Students</a>
            <a href="candidatetab.jsp" class="nav-link">Manage Candidates</a>
            <a href="electiontab.jsp" class="nav-link">Manage Elections</a>
            <a href="votetab.jsp" class="nav-link">Manage Votes</a>
            <a href="admintab.jsp" class="nav-link">Manage Admins</a>
            <a href="resulttab.jsp" class="nav-link">Current Results</a>
            <a href="../LogoutServlet" class="nav-link" onclick="return confirm('Logout?')">Logout</a>
        </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Top Header -->
        <div class="top-header">
            <div class="page-title">Manage Students</div>
            <button class="logout-btn" onclick="if(confirm('Logout?')) window.location.href='../LogoutServlet'">Logout</button>
        </div>

        <!-- Management Content -->
        <div class="management-content">
            <% if (!message.isEmpty()) { %>
            <div class="message-alert <%= messageType %>">
                <%= message %>
            </div>
            <% } %>

            <!-- Action Bar -->
            <div class="action-bar">
                <div class="search-box">
                    <input type="text" class="search-input" placeholder="Search students..." id="searchInput">
                    <button class="search-btn" onclick="searchStudents()">Search</button>
                </div>
                <button class="add-btn" onclick="openAddModal()">+ Add New Student</button>
            </div>

            <!-- Data Table -->
            <div class="data-table">
                <div class="table-header">
                    <div class="table-title">Students List (<%= students.size() %> students)</div>
                </div>
                
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                            <tr>
                                <th>Student ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Faculty</th>
                                <th>Voted</th>
                                <th>Candidate Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="studentTableBody">
                            <% 
                            for (Student student : students) { 
                                String votedClass = student.isHasVoted() ? "badge-voted" : "badge-not-voted";
                                String votedText = student.isHasVoted() ? "Voted" : "Not Voted";
                                
                                String candidateClass = "CANDIDATE".equals(student.getCandidateStatus()) ? "badge-candidate" : "badge-not-candidate";
                                String candidateText = student.getCandidateStatus() != null ? student.getCandidateStatus() : "NOT_CANDIDATE";
                            %>
                            <tr>
                                <td><strong><%= student.getStudentId() %></strong></td>
                                <td><%= student.getName() %></td>
                                <td><%= student.getEmail() %></td>
                                <td><%= student.getFaculty() %></td>
                                <td>
                                    <span class="badge <%= votedClass %>">
                                        <%= votedText %>
                                    </span>
                                </td>
                                <td>
                                    <span class="badge <%= candidateClass %>">
                                        <%= candidateText %>
                                    </span>
                                </td>
                                <td class="actions-cell">
                                    <button class="edit-btn" onclick="editStudent(
                                        '<%= student.getStudentId() %>', 
                                        '<%= student.getName().replace("'", "\\'") %>', 
                                        '<%= student.getEmail() != null ? student.getEmail().replace("'", "\\'") : "" %>',
                                        '<%= student.getFaculty() != null ? student.getFaculty().replace("'", "\\'") : "" %>',
                                        '<%= student.getCandidateStatus() != null ? student.getCandidateStatus().replace("'", "\\'") : "NOT_CANDIDATE" %>'
                                    )">Edit</button>
                                    <button class="password-btn" onclick="resetPassword('<%= student.getStudentId() %>', '<%= student.getName().replace("'", "\\'") %>')">Reset Password</button>
                                    <button class="delete-btn" onclick="deleteStudent('<%= student.getStudentId() %>', '<%= student.getName().replace("'", "\\'") %>')">Delete</button>
                                </td>
                            </tr>
                            <% } %>
                            
                            <% if (students.isEmpty()) { %>
                            <tr>
                                <td colspan="7" class="no-data">
                                    No students found. Add new students using the "Add New Student" button.
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Add/Edit Student Modal -->
    <div id="studentModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title" id="modalTitle">Add New Student</h2>
            <form id="studentForm" method="POST" action="studenttab.jsp">
                <input type="hidden" id="actionType" name="action" value="add">
                <input type="hidden" id="editStudentId" name="studentId">
                
                <div class="form-group">
                    <label class="form-label">Student ID:</label>
                    <input type="text" id="studentId" name="studentId" class="form-input" required 
                           placeholder="e.g., 2023123456">
                </div>
                
                <div class="form-group">
                    <label class="form-label">Full Name:</label>
                    <input type="text" id="name" name="name" class="form-input" required
                           placeholder="e.g., Ali bin Ahmad">
                </div>
                
                <div class="form-group">
                    <label class="form-label">Email:</label>
                    <input type="email" id="email" name="email" class="form-input"
                           placeholder="e.g., 2023123456@student.uitm.edu.my">
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Password:</label>
                        <input type="password" id="password" name="password" class="form-input" required
                               placeholder="Set password for student">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Faculty:</label>
                        <select id="faculty" name="faculty" class="form-select" required>
                            <option value="">Select Faculty</option>
                            <option value="FCSIT">Faculty of Computer Science & IT</option>
                            <option value="FKE">Faculty of Engineering</option>
                            <option value="FBM">Faculty of Business Management</option>
                            <option value="FSSH">Faculty of Social Sciences</option>
                            <option value="FSPU">Faculty of Sports Science</option>
                            <option value="OTHER">Other</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Candidate Status:</label>
                    <select id="candidateStatus" name="candidateStatus" class="form-select">
                        <option value="NOT_CANDIDATE">Not a Candidate</option>
                        <option value="CANDIDATE">Candidate</option>
                        <option value="PENDING">Pending</option>
                    </select>
                </div>
                
                <div class="modal-buttons">
                    <button type="submit" class="save-btn">Save Student</button>
                    <button type="button" class="cancel-btn" onclick="closeModal()">Cancel</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Reset Password Modal -->
    <div id="passwordModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title">Reset Password</h2>
            <p style="color: #112D4E; margin-bottom: 15px;" id="passwordMessage">
                Enter new password for student:
            </p>
            
            <form id="passwordForm" method="POST" action="studenttab.jsp">
                <input type="hidden" name="action" value="resetPassword">
                <input type="hidden" id="passwordStudentId" name="studentId">
                
                <div class="form-group">
                    <label class="form-label">New Password:</label>
                    <input type="password" id="newPassword" name="newPassword" class="form-input" required
                           placeholder="Enter new password">
                </div>
                
                <div class="form-group">
                    <label class="form-label">Confirm Password:</label>
                    <input type="password" id="confirmPassword" class="form-input" required
                           placeholder="Confirm new password">
                </div>
                
                <div class="modal-buttons">
                    <button type="submit" class="save-btn">Reset Password</button>
                    <button type="button" class="cancel-btn" onclick="closePasswordModal()">Cancel</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div id="deleteModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title">Confirm Delete</h2>
            <p style="color: #112D4E; margin-bottom: 20px;" id="deleteMessage">
                Are you sure you want to delete this student?
            </p>
            <form id="deleteForm" method="POST" action="studenttab.jsp">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" id="deleteStudentId" name="studentId">
                
                <div class="modal-buttons">
                    <button type="submit" class="save-btn" style="background-color: #c62828;">Delete</button>
                    <button type="button" class="cancel-btn" onclick="closeDeleteModal()">Cancel</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Modal functions
        function openAddModal() {
            document.getElementById('modalTitle').textContent = 'Add New Student';
            document.getElementById('actionType').value = 'add';
            document.getElementById('studentForm').reset();
            document.getElementById('studentModal').style.display = 'flex';
        }

        function editStudent(studentId, name, email, faculty, candidateStatus) {
            document.getElementById('modalTitle').textContent = 'Edit Student';
            document.getElementById('actionType').value = 'update';
            document.getElementById('editStudentId').value = studentId;
            document.getElementById('studentId').value = studentId;
            document.getElementById('studentId').readOnly = true;
            document.getElementById('name').value = name;
            document.getElementById('email').value = email || '';
            document.getElementById('faculty').value = faculty || '';
            document.getElementById('candidateStatus').value = candidateStatus || 'NOT_CANDIDATE';
            document.getElementById('password').removeAttribute('required');
            document.getElementById('password').placeholder = 'Leave blank to keep current password';
            
            document.getElementById('studentModal').style.display = 'flex';
        }

        function resetPassword(studentId, studentName) {
            document.getElementById('passwordMessage').innerHTML = 
                'Enter new password for student:<br><strong>' + studentName + '</strong>';
            document.getElementById('passwordStudentId').value = studentId;
            document.getElementById('passwordForm').reset();
            document.getElementById('passwordModal').style.display = 'flex';
        }

        function closeModal() {
            document.getElementById('studentModal').style.display = 'none';
        }

        function closePasswordModal() {
            document.getElementById('passwordModal').style.display = 'none';
        }

        function deleteStudent(studentId, studentName) {
            document.getElementById('deleteMessage').innerHTML = 
                'Are you sure you want to delete student:<br><br>' +
                '<strong>' + studentName + '</strong><br>' +
                'ID: ' + studentId + '<br><br>' +
                'Warning: This action cannot be undone!';
            document.getElementById('deleteStudentId').value = studentId;
            document.getElementById('deleteModal').style.display = 'flex';
        }

        function closeDeleteModal() {
            document.getElementById('deleteModal').style.display = 'none';
        }

        // Search function
        function searchStudents() {
            var searchTerm = document.getElementById('searchInput').value.toLowerCase();
            var rows = document.getElementById('studentTableBody').getElementsByTagName('tr');
            
            for (var i = 0; i < rows.length; i++) {
                var cells = rows[i].getElementsByTagName('td');
                var found = false;
                
                for (var j = 0; j < cells.length; j++) {
                    var cellText = cells[j].textContent || cells[j].innerText;
                    if (cellText.toLowerCase().indexOf(searchTerm) > -1) {
                        found = true;
                        break;
                    }
                }
                
                rows[i].style.display = found ? '' : 'none';
            }
        }

        // Form validation
        document.getElementById('studentForm').addEventListener('submit', function(e) {
            var studentId = document.getElementById('studentId').value;
            var name = document.getElementById('name').value;
            
            if (!studentId.trim() || !name.trim()) {
                e.preventDefault();
                alert('Please fill in all required fields!');
                return false;
            }
            
          
            
            return true;
        });

        document.getElementById('passwordForm').addEventListener('submit', function(e) {
            var newPassword = document.getElementById('newPassword').value;
            var confirmPassword = document.getElementById('confirmPassword').value;
            
            if (newPassword.length < 6) {
                e.preventDefault();
                alert('Password must be at least 6 characters!');
                return false;
            }
            
            if (newPassword !== confirmPassword) {
                e.preventDefault();
                alert('Passwords do not match!');
                return false;
            }
            
            return true;
        });

        // Close modals when clicking outside
        window.onclick = function(event) {
            var studentModal = document.getElementById('studentModal');
            var passwordModal = document.getElementById('passwordModal');
            var deleteModal = document.getElementById('deleteModal');
            
            if (event.target === studentModal) {
                closeModal();
            }
            if (event.target === passwordModal) {
                closePasswordModal();
            }
            if (event.target === deleteModal) {
                closeDeleteModal();
            }
        }
    </script>
</body>
</html>