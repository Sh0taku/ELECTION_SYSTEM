<%-- 
    Document   : admintab
    Created on : Jan 13, 2026, 9:52:19 AM
    Author     : Emir
--%>

<%--
Document: admintab
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.beans.Admin" %>
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
String adminId = request.getParameter("adminId");

Connection conn = null;
PreparedStatement pstmt = null;
Statement stmt = null;
ResultSet rs = null;

if (action != null) {
    try {
        conn = DBConnection.getConnection();
        
        if ("delete".equals(action) && adminId != null) {
            // DELETE ADMIN (prevent deleting yourself)
            if (adminId.equals(admin.getAdminId())) {
                message = "You cannot delete your own account!";
                messageType = "error";
            } else {
                String sql = "DELETE FROM ADMIN WHERE ADMIN_ID = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, adminId);
                
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    message = "Admin deleted successfully!";
                    messageType = "success";
                }
            }
            
        } else if ("add".equals(action)) {
            // ADD NEW ADMIN
            String newAdminId = request.getParameter("adminId");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            
            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                message = "Passwords do not match!";
                messageType = "error";
            } else {
                // Check if admin already exists
                String checkSql = "SELECT ADMIN_ID FROM ADMIN WHERE ADMIN_ID = ? OR USERNAME = ?";
                pstmt = conn.prepareStatement(checkSql);
                pstmt.setString(1, newAdminId);
                pstmt.setString(2, username);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    message = "Admin ID or Username already exists!";
                    messageType = "error";
                } else {
                    String insertSql = "INSERT INTO ADMIN (ADMIN_ID, USERNAME, PASSWORD) VALUES (?, ?, ?)";
                    pstmt = conn.prepareStatement(insertSql);
                    pstmt.setString(1, newAdminId);
                    pstmt.setString(2, username);
                    pstmt.setString(3, password);
                    
                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        message = "Admin added successfully!";
                        messageType = "success";
                    }
                }
            }
            
        } else if ("update".equals(action)) {
            // UPDATE ADMIN
            String updateAdminId = request.getParameter("adminId");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            
            if (password != null && !password.trim().isEmpty()) {
                // Update with password
                String updateSql = "UPDATE ADMIN SET USERNAME = ?, PASSWORD = ? WHERE ADMIN_ID = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, updateAdminId);
            } else {
                // Update without password
                String updateSql = "UPDATE ADMIN SET USERNAME = ? WHERE ADMIN_ID = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, username);
                pstmt.setString(2, updateAdminId);
            }
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Admin updated successfully!";
                messageType = "success";
            }
        } else if ("resetPassword".equals(action) && adminId != null) {
            // RESET PASSWORD
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            
            if (!newPassword.equals(confirmPassword)) {
                message = "Passwords do not match!";
                messageType = "error";
            } else {
                String updateSql = "UPDATE ADMIN SET PASSWORD = ? WHERE ADMIN_ID = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, newPassword);
                pstmt.setString(2, adminId);
                
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    message = "Password reset successfully!";
                    messageType = "success";
                }
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

// Fetch admins for display
List<Admin> admins = new ArrayList<Admin>();

try {
    conn = DBConnection.getConnection();
    stmt = conn.createStatement();
    
    // Get all admins
    String adminSql = "SELECT * FROM ADMIN ORDER BY ADMIN_ID";
    rs = stmt.executeQuery(adminSql);
    
    while (rs.next()) {
        Admin adminObj = new Admin(
            rs.getString("ADMIN_ID"),
            rs.getString("USERNAME"),
            rs.getString("PASSWORD")
        );
        admins.add(adminObj);
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
    <title>Manage Admins - UITM Election System</title>
    <style>
        /* Same CSS structure */
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

        /* Current user indicator */
        .current-user {
            background-color: #f0f7ff;
            border-left: 4px solid #3F72AF;
        }

        .current-user:hover {
            background-color: #e3f2fd;
        }

        .current-user-badge {
            background-color: #3F72AF;
            color: white;
            padding: 2px 6px;
            border-radius: 10px;
            font-size: 10px;
            margin-left: 5px;
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

        .form-input {
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
            <a href="studenttab.jsp" class="nav-link">Manage Students</a>
            <a href="candidatetab.jsp" class="nav-link">Manage Candidates</a>
            <a href="electiontab.jsp" class="nav-link">Manage Elections</a>
            <a href="votetab.jsp" class="nav-link">Manage Votes</a>
            <a href="admintab.jsp" class="nav-link active">Manage Admins</a>
            <a href="resulttab.jsp" class="nav-link">Current Results</a>
            <a href="../LogoutServlet" class="nav-link" onclick="return confirm('Logout?')">Logout</a>
        </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Top Header -->
        <div class="top-header">
            <div class="page-title">Manage Admin Accounts</div>
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
                    <input type="text" class="search-input" placeholder="Search admins..." id="searchInput">
                    <button class="search-btn" onclick="searchAdmins()">Search</button>
                </div>
                <button class="add-btn" onclick="openAddModal()">+ Add New Admin</button>
            </div>

            <!-- Data Table -->
            <div class="data-table">
                <div class="table-header">
                    <div class="table-title">Admin Accounts (<%= admins.size() %> admins)</div>
                </div>
                
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                            <tr>
                                <th>Admin ID</th>
                                <th>Username</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="adminTableBody">
                            <% 
                            for (Admin adminObj : admins) { 
                                boolean isCurrentUser = adminObj.getAdminId().equals(admin.getAdminId());
                            %>
                            <tr class="<%= isCurrentUser ? "current-user" : "" %>">
                                <td>
                                    <strong><%= adminObj.getAdminId() %></strong>
                                    <% if (isCurrentUser) { %>
                                    <span class="current-user-badge">You</span>
                                    <% } %>
                                </td>
                                <td><%= adminObj.getUsername() %></td>
                                <td class="actions-cell">
                                    <button class="edit-btn" onclick="editAdmin(
                                        '<%= adminObj.getAdminId() %>', 
                                        '<%= adminObj.getUsername().replace("'", "\\'") %>'
                                    )">Edit</button>
                                    <button class="password-btn" onclick="resetAdminPassword(
                                        '<%= adminObj.getAdminId() %>', 
                                        '<%= adminObj.getUsername().replace("'", "\\'") %>'
                                    )">Reset Password</button>
                                    <% if (!isCurrentUser) { %>
                                    <button class="delete-btn" onclick="deleteAdmin(
                                        '<%= adminObj.getAdminId() %>', 
                                        '<%= adminObj.getUsername().replace("'", "\\'") %>'
                                    )">Delete</button>
                                    <% } else { %>
                                    <button class="delete-btn" style="background-color: #cccccc; cursor: not-allowed;" 
                                            title="Cannot delete your own account" disabled>Delete</button>
                                    <% } %>
                                </td>
                            </tr>
                            <% } %>
                            
                            <% if (admins.isEmpty()) { %>
                            <tr>
                                <td colspan="3" class="no-data">
                                    No admin accounts found. Add new admins using the "Add New Admin" button.
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Add/Edit Admin Modal -->
    <div id="adminModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title" id="modalTitle">Add New Admin</h2>
            <form id="adminForm" method="POST" action="admintab.jsp">
                <input type="hidden" id="actionType" name="action" value="add">
                <input type="hidden" id="editAdminId" name="adminId">
                
                <div class="form-group">
                    <label class="form-label">Admin ID:</label>
                    <input type="text" id="adminIdInput" name="adminId" class="form-input" required 
                           placeholder="e.g., ADM001">
                </div>
                
                <div class="form-group">
                    <label class="form-label">Username:</label>
                    <input type="text" id="username" name="username" class="form-input" required
                           placeholder="e.g., admin_user">
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Password:</label>
                        <input type="password" id="password" name="password" class="form-input" required
                               placeholder="Set password">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Confirm Password:</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-input" required
                               placeholder="Confirm password">
                    </div>
                </div>
                
                <div class="modal-buttons">
                    <button type="submit" class="save-btn">Save Admin</button>
                    <button type="button" class="cancel-btn" onclick="closeModal()">Cancel</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Reset Password Modal -->
    <div id="passwordModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title">Reset Admin Password</h2>
            <p style="color: #112D4E; margin-bottom: 15px;" id="passwordMessage">
                Enter new password for admin:
            </p>
            
            <form id="passwordForm" method="POST" action="admintab.jsp">
                <input type="hidden" name="action" value="resetPassword">
                <input type="hidden" id="passwordAdminId" name="adminId">
                
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">New Password:</label>
                        <input type="password" id="newPassword" name="newPassword" class="form-input" required
                               placeholder="Enter new password">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Confirm Password:</label>
                        <input type="password" id="confirmNewPassword" name="confirmPassword" class="form-input" required
                               placeholder="Confirm new password">
                    </div>
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
                Are you sure you want to delete this admin account?
            </p>
            <form id="deleteForm" method="POST" action="admintab.jsp">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" id="deleteAdminId" name="adminId">
                
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
            document.getElementById('modalTitle').textContent = 'Add New Admin';
            document.getElementById('actionType').value = 'add';
            document.getElementById('adminForm').reset();
            document.getElementById('adminModal').style.display = 'flex';
        }

        function editAdmin(adminId, username) {
            document.getElementById('modalTitle').textContent = 'Edit Admin';
            document.getElementById('actionType').value = 'update';
            document.getElementById('editAdminId').value = adminId;
            document.getElementById('adminIdInput').value = adminId;
            document.getElementById('adminIdInput').readOnly = true;
            document.getElementById('username').value = username;
            document.getElementById('password').removeAttribute('required');
            document.getElementById('confirmPassword').removeAttribute('required');
            document.getElementById('password').placeholder = 'Leave blank to keep current password';
            document.getElementById('confirmPassword').placeholder = 'Leave blank to keep current password';
            
            document.getElementById('adminModal').style.display = 'flex';
        }

        function resetAdminPassword(adminId, username) {
            document.getElementById('passwordMessage').innerHTML = 
                'Enter new password for admin:<br><strong>' + username + '</strong>';
            document.getElementById('passwordAdminId').value = adminId;
            document.getElementById('passwordForm').reset();
            document.getElementById('passwordModal').style.display = 'flex';
        }

        function closeModal() {
            document.getElementById('adminModal').style.display = 'none';
        }

        function closePasswordModal() {
            document.getElementById('passwordModal').style.display = 'none';
        }

        function deleteAdmin(adminId, username) {
            document.getElementById('deleteMessage').innerHTML = 
                'Are you sure you want to delete admin account:<br><br>' +
                '<strong>' + username + '</strong><br>' +
                'ID: ' + adminId + '<br><br>' +
                'Warning: This action cannot be undone!';
            document.getElementById('deleteAdminId').value = adminId;
            document.getElementById('deleteModal').style.display = 'flex';
        }

        function closeDeleteModal() {
            document.getElementById('deleteModal').style.display = 'none';
        }

        // Search function
        function searchAdmins() {
            var searchTerm = document.getElementById('searchInput').value.toLowerCase();
            var rows = document.getElementById('adminTableBody').getElementsByTagName('tr');
            
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
        document.getElementById('adminForm').addEventListener('submit', function(e) {
            var adminId = document.getElementById('adminIdInput').value;
            var username = document.getElementById('username').value;
            var password = document.getElementById('password').value;
            var confirmPassword = document.getElementById('confirmPassword').value;
            
            if (!adminId.trim() || !username.trim()) {
                e.preventDefault();
                alert('Please fill in all required fields!');
                return false;
            }
            
            // For new admin, password is required
            if (document.getElementById('actionType').value === 'add') {
                if (!password.trim() || password.length < 6) {
                    e.preventDefault();
                    alert('Password must be at least 6 characters!');
                    return false;
                }
                
                if (password !== confirmPassword) {
                    e.preventDefault();
                    alert('Passwords do not match!');
                    return false;
                }
            }
            
            return true;
        });

        document.getElementById('passwordForm').addEventListener('submit', function(e) {
            var newPassword = document.getElementById('newPassword').value;
            var confirmNewPassword = document.getElementById('confirmNewPassword').value;
            
            if (newPassword.length < 6) {
                e.preventDefault();
                alert('Password must be at least 6 characters!');
                return false;
            }
            
            if (newPassword !== confirmNewPassword) {
                e.preventDefault();
                alert('Passwords do not match!');
                return false;
            }
            
            return true;
        });

        // Close modals when clicking outside
        window.onclick = function(event) {
            var adminModal = document.getElementById('adminModal');
            var passwordModal = document.getElementById('passwordModal');
            var deleteModal = document.getElementById('deleteModal');
            
            if (event.target === adminModal) {
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