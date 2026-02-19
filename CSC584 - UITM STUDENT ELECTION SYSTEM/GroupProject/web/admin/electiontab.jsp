<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.beans.Admin" %>
<%@page import="com.election.beans.Election" %>
<%@page import="java.util.*" %>
<%@page import="java.sql.*" %>
<%@page import="com.election.dao.DBConnection" %>
<%@page import="java.text.SimpleDateFormat" %>
<%

Admin admin = (Admin) session.getAttribute("admin");
if (admin == null) {
    response.sendRedirect("../login.jsp");
    return;
}

String message = "";
String messageType = "";


String action = request.getParameter("action");
String electionIdStr = request.getParameter("electionId");

Connection conn = null;
PreparedStatement pstmt = null;
Statement stmt = null;
ResultSet rs = null;

if (action != null) {
    try {
        conn = DBConnection.getConnection();
        
        if ("delete".equals(action) && electionIdStr != null) {

            int electionId = Integer.parseInt(electionIdStr);
            

            String checkCandidates = "SELECT COUNT(*) FROM CANDIDATES WHERE ELECTION_ID = ?";
            pstmt = conn.prepareStatement(checkCandidates);
            pstmt.setInt(1, electionId);
            rs = pstmt.executeQuery();
            rs.next();
            int candidateCount = rs.getInt(1);
            
            if (candidateCount > 0) {
                message = "Cannot delete election with candidates! Remove candidates first.";
                messageType = "error";
            } else {
                String sql = "DELETE FROM ELECTIONS WHERE ELECTION_ID = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, electionId);
                
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    message = "Election deleted successfully!";
                    messageType = "success";
                }
            }
            
        } else if ("add".equals(action)) {

            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String status = request.getParameter("status");
            
            String insertSql = "INSERT INTO ELECTIONS (TITLE, DESCRIPTION, START_DATE, END_DATE, STATUS) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setDate(3, java.sql.Date.valueOf(startDate));
            pstmt.setDate(4, java.sql.Date.valueOf(endDate));
            pstmt.setString(5, status);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Election added successfully!";
                messageType = "success";
            }
            
        } else if ("update".equals(action)) {

            String electionId = request.getParameter("electionId");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String status = request.getParameter("status");
            
            String updateSql = "UPDATE ELECTIONS SET TITLE = ?, DESCRIPTION = ?, START_DATE = ?, END_DATE = ?, STATUS = ? WHERE ELECTION_ID = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setDate(3, java.sql.Date.valueOf(startDate));
            pstmt.setDate(4, java.sql.Date.valueOf(endDate));
            pstmt.setString(5, status);
            pstmt.setInt(6, Integer.parseInt(electionId));
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Election updated successfully!";
                messageType = "success";
            }
        } else if ("changeStatus".equals(action) && electionIdStr != null) {

            int electionId = Integer.parseInt(electionIdStr);
            String newStatus = request.getParameter("status");
            
            String updateSql = "UPDATE ELECTIONS SET STATUS = ? WHERE ELECTION_ID = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, electionId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Election status updated to " + newStatus + "!";
                messageType = "success";
            }
        }
        
    } catch (SQLException e) {
        message = "Database error: " + e.getMessage();
        messageType = "error";
        e.printStackTrace();
    } catch (Exception e) {
        message = "Error: " + e.getMessage();
        messageType = "error";
        e.printStackTrace();
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
}


List<Election> elections = new ArrayList<Election>();
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

try {
    conn = DBConnection.getConnection();
    stmt = conn.createStatement();
    

    String electionSql = "SELECT * FROM ELECTIONS ORDER BY ELECTION_ID";
    rs = stmt.executeQuery(electionSql);
    
    while (rs.next()) {
        Election election = new Election();
        election.setElectionId(rs.getInt("ELECTION_ID"));
        election.setTitle(rs.getString("TITLE"));
        election.setDescription(rs.getString("DESCRIPTION"));
        election.setStartDate(rs.getDate("START_DATE"));
        election.setEndDate(rs.getDate("END_DATE"));
        election.setStatus(rs.getString("STATUS"));
        elections.add(election);
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
    <title>Manage Elections - UITM Election System</title>
    <style>

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


        .main-content {
            flex: 1;
            display: flex;
            flex-direction: column;
        }


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


        .management-content {
            padding: 30px;
            overflow-y: auto;
            flex: 1;
        }


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
            gap: 10px;
        }

        .edit-btn {
            padding: 5px 15px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 12px;
        }

        .edit-btn:hover {
            background-color: #112D4E;
        }

        .delete-btn {
            padding: 5px 15px;
            background-color: #c62828;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 12px;
        }

        .delete-btn:hover {
            background-color: #b71c1c;
        }

        .status-btn {
            padding: 5px 15px;
            background-color: #ff9800;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 12px;
        }

        .status-btn:hover {
            background-color: #f57c00;
        }

        
        
        
        
        .badge {
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: bold;
            display: inline-block;
        }
        
        .badge-ongoing {
            background-color: #e1f7e1;
            color: #2e7d32;
            border: 1px solid #2e7d32;
        }
        
        .badge-upcoming {
            background-color: #fff3e0;
            color: #ef6c00;
            border: 1px solid #ef6c00;
        }
        
        .badge-ended {
            background-color: #ffebee;
            color: #c62828;
            border: 1px solid #c62828;
        }

        
        .description-cell {
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .no-data {
            text-align: center;
            padding: 30px;
            color: #3F72AF;
        }


        
        
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

        .form-input, .form-select, .form-textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #DBE2EF;
            border-radius: 5px;
            font-size: 14px;
        }

        .form-textarea {
            resize: vertical;
            min-height: 80px;
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

        
        
        .status-options {
            display: flex;
            gap: 10px;
            margin: 15px 0;
        }

        .status-option {
            flex: 1;
            padding: 10px;
            text-align: center;
            border: 2px solid #DBE2EF;
            border-radius: 5px;
            cursor: pointer;
            transition: all 0.3s;
        }

        .status-option:hover {
            border-color: #3F72AF;
            background-color: #F9F7F7;
        }

        .status-option.selected {
            border-color: #3F72AF;
            background-color: #3F72AF;
            color: white;
        }
    </style>
</head>
<body>

    
    
    
    <div class="nav-panel">
        <div class="admin-info">
            <div class="admin-name"><%= admin.getUsername() %></div>
            <div class="admin-id">Admin ID: <%= admin.getAdminId() %></div>
        </div>

        <div class="nav-links">
            <a href="admindashboard.jsp" class="nav-link">Dashboard</a>
            <a href="studenttab.jsp" class="nav-link">Manage Students</a>
            <a href="candidatetab.jsp" class="nav-link">Manage Candidates</a>
            <a href="electiontab.jsp" class="nav-link active">Manage Elections</a>
            <a href="votetab.jsp" class="nav-link">Manage Votes</a>
            <a href="resulttab.jsp" class="nav-link">Current Results</a>
            <a href="../LogoutServlet" class="nav-link" onclick="return confirm('Logout?')">Logout</a>
        </div>
    </div>


        
        
    <div class="main-content">

        
        <div class="top-header">
            <div class="page-title">Manage Elections</div>
            <button class="logout-btn" onclick="if(confirm('Logout?')) window.location.href='../LogoutServlet'">Logout</button>
        </div>


        
        <div class="management-content">
            <% if (!message.isEmpty()) { %>
            <div class="message-alert <%= messageType %>">
                <%= message %>
            </div>
            <% } %>


            
            <div class="action-bar">
                <div class="search-box">
                    <input type="text" class="search-input" placeholder="Search elections..." id="searchInput">
                    <button class="search-btn" onclick="searchElections()">Search</button>
                </div>
                <button class="add-btn" onclick="openAddModal()">+ Create New Election</button>
            </div>


            
            
            <div class="data-table">
                <div class="table-header">
                    <div class="table-title">Elections List (<%= elections.size() %> elections)</div>
                </div>
                
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Title</th>
                                <th>Description</th>
                                <th>Start Date</th>
                                <th>End Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="electionTableBody">
                            <% 
                            for (Election election : elections) { 
                                String statusClass = "";
                                String status = election.getStatus();
                                if ("ONGOING".equals(status)) {
                                    statusClass = "badge-ongoing";
                                } else if ("UPCOMING".equals(status)) {
                                    statusClass = "badge-upcoming";
                                } else if ("ENDED".equals(status)) {
                                    statusClass = "badge-ended";
                                }
                                
                                String startDate = election.getStartDate() != null ? 
                                    new SimpleDateFormat("dd MMM yyyy").format(election.getStartDate()) : "Not set";
                                String endDate = election.getEndDate() != null ? 
                                    new SimpleDateFormat("dd MMM yyyy").format(election.getEndDate()) : "Not set";
                            %>
                            <tr>
                                <td><strong>#<%= election.getElectionId() %></strong></td>
                                <td><strong><%= election.getTitle() %></strong></td>
                                <td class="description-cell" title="<%= election.getDescription() != null ? election.getDescription() : "" %>">
                                    <%= election.getDescription() != null && election.getDescription().length() > 50 ? 
                                        election.getDescription().substring(0, 50) + "..." : 
                                        (election.getDescription() != null ? election.getDescription() : "No description") %>
                                </td>
                                <td><%= startDate %></td>
                                <td><%= endDate %></td>
                                <td>
                                    <span class="badge <%= statusClass %>">
                                        <%= status %>
                                    </span>
                                </td>
                                <td class="actions-cell">
                                    <button class="edit-btn" onclick="editElection(
                                        <%= election.getElectionId() %>, 
                                        '<%= election.getTitle().replace("'", "\\'") %>', 
                                        '<%= election.getDescription() != null ? election.getDescription().replace("'", "\\'") : "" %>',
                                        '<%= election.getStartDate() != null ? dateFormat.format(election.getStartDate()) : "" %>',
                                        '<%= election.getEndDate() != null ? dateFormat.format(election.getEndDate()) : "" %>',
                                        '<%= election.getStatus() %>'
                                    )">Edit</button>
                                    <button class="status-btn" onclick="changeStatus(<%= election.getElectionId() %>, '<%= election.getStatus() %>', '<%= election.getTitle().replace("'", "\\'") %>')">Change Status</button>
                                    <button class="delete-btn" onclick="deleteElection(<%= election.getElectionId() %>, '<%= election.getTitle().replace("'", "\\'") %>')">Delete</button>
                                </td>
                            </tr>
                            <% } %>
                            
                            <% if (elections.isEmpty()) { %>
                            <tr>
                                <td colspan="7" class="no-data">
                                    No elections found. Create new elections using the "Create New Election" button.
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>


                        
                        
    <div id="electionModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title" id="modalTitle">Create New Election</h2>
            <form id="electionForm" method="POST" action="electiontab.jsp">
                <input type="hidden" id="actionType" name="action" value="add">
                <input type="hidden" id="editElectionId" name="electionId">
                
                <div class="form-group">
                    <label class="form-label">Election Title:</label>
                    <input type="text" id="title" name="title" class="form-input" required 
                           placeholder="e.g., Student Council Election 2024">
                </div>
                
                <div class="form-group">
                    <label class="form-label">Description:</label>
                    <textarea id="description" name="description" class="form-textarea" 
                              placeholder="Describe the election purpose, positions available, etc."></textarea>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Start Date:</label>
                        <input type="date" id="startDate" name="startDate" class="form-input" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">End Date:</label>
                        <input type="date" id="endDate" name="endDate" class="form-input" required>
                    </div>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Initial Status:</label>
                    <select id="status" name="status" class="form-select" required>
                        <option value="UPCOMING">Upcoming</option>
                        <option value="ONGOING">Ongoing</option>
                        <option value="ENDED">Ended</option>
                    </select>
                </div>
                
                <div class="modal-buttons">
                    <button type="submit" class="save-btn">Save Election</button>
                    <button type="button" class="cancel-btn" onclick="closeModal()">Cancel</button>
                </div>
            </form>
        </div>
    </div>


    <div id="statusModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title">Change Election Status</h2>
            <p style="color: #112D4E; margin-bottom: 15px;" id="statusMessage">
                Select new status for election:
            </p>
            
            <div class="status-options">
                <div class="status-option" data-status="UPCOMING" onclick="selectStatus('UPCOMING')">
                    <div style="font-weight: bold; color: #ef6c00;">UPCOMING</div>
                    <div style="font-size: 11px; color: #666;">Election hasn't started</div>
                </div>
                <div class="status-option" data-status="ONGOING" onclick="selectStatus('ONGOING')">
                    <div style="font-weight: bold; color: #2e7d32;">ONGOING</div>
                    <div style="font-size: 11px; color: #666;">Election is active</div>
                </div>
                <div class="status-option" data-status="ENDED" onclick="selectStatus('ENDED')">
                    <div style="font-weight: bold; color: #c62828;">ENDED</div>
                    <div style="font-size: 11px; color: #666;">Election has ended</div>
                </div>
            </div>
            
            <form id="statusForm" method="POST" action="electiontab.jsp">
                <input type="hidden" name="action" value="changeStatus">
                <input type="hidden" id="statusElectionId" name="electionId">
                <input type="hidden" id="selectedStatus" name="status">
                
                <div class="modal-buttons">
                    <button type="submit" class="save-btn">Update Status</button>
                    <button type="button" class="cancel-btn" onclick="closeStatusModal()">Cancel</button>
                </div>
            </form>
        </div>
    </div>

                        
                        
    <div id="deleteModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title">Confirm Delete</h2>
            <p style="color: #112D4E; margin-bottom: 20px;" id="deleteMessage">
                Are you sure you want to delete this election?
            </p>
            <form id="deleteForm" method="POST" action="electiontab.jsp">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" id="deleteElectionId" name="electionId">
                
                <div class="modal-buttons">
                    <button type="submit" class="save-btn" style="background-color: #c62828;">Delete</button>
                    <button type="button" class="cancel-btn" onclick="closeDeleteModal()">Cancel</button>
                </div>
            </form>
        </div>
    </div>

    <script>

        function openAddModal() {
            document.getElementById('modalTitle').textContent = 'Create New Election';
            document.getElementById('actionType').value = 'add';
            document.getElementById('electionForm').reset();
            
  
            var today = new Date().toISOString().split('T')[0];
            var tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            var tomorrowStr = tomorrow.toISOString().split('T')[0];
            
            document.getElementById('startDate').value = today;
            document.getElementById('endDate').value = tomorrowStr;
            
            document.getElementById('electionModal').style.display = 'flex';
        }

        function editElection(electionId, title, description, startDate, endDate, status) {
            document.getElementById('modalTitle').textContent = 'Edit Election';
            document.getElementById('actionType').value = 'update';
            document.getElementById('editElectionId').value = electionId;
            document.getElementById('title').value = title;
            document.getElementById('description').value = description || '';
            document.getElementById('startDate').value = startDate;
            document.getElementById('endDate').value = endDate;
            document.getElementById('status').value = status;
            
            document.getElementById('electionModal').style.display = 'flex';
        }

        function changeStatus(electionId, currentStatus, electionTitle) {
            document.getElementById('statusMessage').innerHTML = 
                'Select new status for election:<br><strong>' + electionTitle + '</strong>';
            document.getElementById('statusElectionId').value = electionId;
            
  
            var options = document.querySelectorAll('.status-option');
            options.forEach(option => {
                option.classList.remove('selected');
                if (option.dataset.status === currentStatus) {
                    option.classList.add('selected');
                    document.getElementById('selectedStatus').value = currentStatus;
                }
            });
            
            document.getElementById('statusModal').style.display = 'flex';
        }

        function selectStatus(status) {
            document.getElementById('selectedStatus').value = status;
            
            var options = document.querySelectorAll('.status-option');
            options.forEach(option => {
                option.classList.remove('selected');
                if (option.dataset.status === status) {
                    option.classList.add('selected');
                }
            });
        }

        function closeModal() {
            document.getElementById('electionModal').style.display = 'none';
        }

        function closeStatusModal() {
            document.getElementById('statusModal').style.display = 'none';
        }

        function deleteElection(electionId, electionTitle) {
            document.getElementById('deleteMessage').innerHTML = 
                'Are you sure you want to delete election:<br><br>' +
                '<strong>' + electionTitle + '</strong><br>' +
                'ID: ' + electionId + '<br><br>' +
                'Warning: This action cannot be undone!';
            document.getElementById('deleteElectionId').value = electionId;
            document.getElementById('deleteModal').style.display = 'flex';
        }

        function closeDeleteModal() {
            document.getElementById('deleteModal').style.display = 'none';
        }


        function searchElections() {
            var searchTerm = document.getElementById('searchInput').value.toLowerCase();
            var rows = document.getElementById('electionTableBody').getElementsByTagName('tr');
            
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


        document.getElementById('electionForm').addEventListener('submit', function(e) {
            var title = document.getElementById('title').value;
            var startDate = document.getElementById('startDate').value;
            var endDate = document.getElementById('endDate').value;
            
            if (!title.trim()) {
                e.preventDefault();
                alert('Please enter election title!');
                return false;
            }
            
            if (!startDate || !endDate) {
                e.preventDefault();
                alert('Please select both start and end dates!');
                return false;
            }
            
            if (new Date(startDate) > new Date(endDate)) {
                e.preventDefault();
                alert('End date must be after start date!');
                return false;
            }
            
            return true;
        });


        window.onclick = function(event) {
            var electionModal = document.getElementById('electionModal');
            var statusModal = document.getElementById('statusModal');
            var deleteModal = document.getElementById('deleteModal');
            
            if (event.target === electionModal) {
                closeModal();
            }
            if (event.target === statusModal) {
                closeStatusModal();
            }
            if (event.target === deleteModal) {
                closeDeleteModal();
            }
        }
    </script>
</body>
</html>