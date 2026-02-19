<%--
Document: candidatetab
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.beans.Admin" %>
<%@page import="com.election.beans.Candidate" %>
<%@page import="com.election.beans.Election" %>
<%@page import="java.util.*" %>
<%@page import="java.sql.*" %>
<%@page import="com.election.dao.DBConnection" %>
<%

Admin admin = (Admin) session.getAttribute("admin");
if (admin == null) {
    response.sendRedirect("../login.jsp");
    return;
}

String message = "";
String messageType = "";


String action = request.getParameter("action");
String candidateIdStr = request.getParameter("candidateId");

Connection conn = null;
PreparedStatement pstmt = null;
Statement stmt = null;
ResultSet rs = null;

if (action != null) {
    try {
        conn = DBConnection.getConnection();
        
        if ("delete".equals(action) && candidateIdStr != null) {
            int candidateId = Integer.parseInt(candidateIdStr);
            String sql = "DELETE FROM CANDIDATES WHERE CANDIDATE_ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, candidateId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Candidate deleted successfully!";
                messageType = "success";
            } else {
                message = "Failed to delete candidate!";
                messageType = "error";
            }
            
            // addnew candidatee
        } else if ("add".equals(action)) {
            String studentId = request.getParameter("studentId");
            String electionIdStr = request.getParameter("electionId");
            String candidateName = request.getParameter("candidateName");
            String position = request.getParameter("position");
            String manifesto = request.getParameter("manifesto");
            
            //cehcking students exitance
            String checkStudentSql = "SELECT STUDENT_ID FROM STUDENTS WHERE STUDENT_ID = ?";
            pstmt = conn.prepareStatement(checkStudentSql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                message = "Student ID does not exist!";
                messageType = "error";
            } else {
                String insertSql = "INSERT INTO CANDIDATES (STUDENT_ID, ELECTION_ID, CANDIDATE_NAME, POSITION, MANIFESTO, VOTE_COUNT) VALUES (?, ?, ?, ?, ?, 0)";
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setString(1, studentId);
                pstmt.setInt(2, Integer.parseInt(electionIdStr));
                pstmt.setString(3, candidateName);
                pstmt.setString(4, position);
                pstmt.setString(5, manifesto);
                
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    message = "Candidate added successfully!";
                    messageType = "success";
                    
                    String updateStatusSql = "UPDATE STUDENTS SET CANDIDATE_STATUS = 'CANDIDATE' WHERE STUDENT_ID = ?";
                    pstmt = conn.prepareStatement(updateStatusSql);
                    pstmt.setString(1, studentId);
                    pstmt.executeUpdate();
                }
            }
            
        } else if ("update".equals(action)) {
            String candidateId = request.getParameter("candidateId");
            String candidateName = request.getParameter("candidateName");
            String position = request.getParameter("position");
            String manifesto = request.getParameter("manifesto");
            
            String updateSql = "UPDATE CANDIDATES SET CANDIDATE_NAME = ?, POSITION = ?, MANIFESTO = ? WHERE CANDIDATE_ID = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, candidateName);
            pstmt.setString(2, position);
            pstmt.setString(3, manifesto);
            pstmt.setInt(4, Integer.parseInt(candidateId));
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Candidate updated successfully!";
                messageType = "success";
            }
        }
        
    } catch (SQLException e) {
        message = "Database error: " + e.getMessage();
        messageType = "error";
        e.printStackTrace();
    } catch (NumberFormatException e) {
        message = "Invalid number format!";
        messageType = "error";
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
}



List<Candidate> candidates = new ArrayList<Candidate>();
List<Election> elections = new ArrayList<Election>();

try {
    conn = DBConnection.getConnection();
    stmt = conn.createStatement();
    

    
    String candidateSql = "SELECT * FROM CANDIDATES ORDER BY CANDIDATE_ID";
    rs = stmt.executeQuery(candidateSql);
    
    while (rs.next()) {
        Candidate candidate = new Candidate();
        candidate.setCandidateId(rs.getInt("CANDIDATE_ID"));
        candidate.setStudentId(rs.getString("STUDENT_ID"));
        candidate.setElectionId(rs.getInt("ELECTION_ID"));
        candidate.setPosition(rs.getString("POSITION"));
        candidate.setManifesto(rs.getString("MANIFESTO"));
        candidate.setVoteCount(rs.getInt("VOTE_COUNT"));
        candidate.setCandidateName(rs.getString("CANDIDATE_NAME"));
        candidates.add(candidate);
    }
    

    
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
    <title>Manage Candidates - UITM Election System</title>
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

        .manifesto-cell {
            max-width: 200px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .vote-count {
            font-weight: bold;
            color: #3F72AF;
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
            min-height: 100px;
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
    
    <div class="nav-panel">
        <div class="admin-info">
            <div class="admin-name"><%= admin.getUsername() %></div>
            <div class="admin-id">Admin ID: <%= admin.getAdminId() %></div>
        </div>

        <div class="nav-links">
            <a href="admindashboard.jsp" class="nav-link">Dashboard</a>
            <a href="studenttab.jsp" class="nav-link">Manage Students</a>
            <a href="candidatetab.jsp" class="nav-link active">Manage Candidates</a>
            <a href="electiontab.jsp" class="nav-link">Manage Elections</a>
            <a href="votetab.jsp" class="nav-link">Manage Votes</a>
            <a href="resulttab.jsp" class="nav-link">Current Results</a>
            <a href="../LogoutServlet" class="nav-link" onclick="return confirm('Logout?')">Logout</a>
        </div>
    </div>

  
    <div class="main-content">
        <div class="top-header">
            <div class="page-title">Manage Candidates</div>
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
                    <input type="text" class="search-input" placeholder="Search candidates..." id="searchInput">
                    <button class="search-btn" onclick="searchCandidates()">Search</button>
                </div>
                <button class="add-btn" onclick="openAddModal()">+ Add New Candidate</button>
            </div>


            
            <div class="data-table">
                <div class="table-header">
                    <div class="table-title">Candidates List (<%= candidates.size() %> candidates)</div>
                </div>
                
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                            <tr>
                                <th>Candidate ID</th>
                                <th>Candidate Name</th>
                                <th>Student ID</th>
                                <th>Position</th>
                                <th>Election</th>
                                <th>Manifesto</th>
                                <th>Votes</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="candidateTableBody">
                            <% 
                            for (Candidate candidate : candidates) { 
                                String candidateName = candidate.getCandidateName();
                                if (candidateName == null || candidateName.isEmpty()) {
                                    candidateName = "Candidate " + candidate.getCandidateId();
                                }
                                
                                String manifesto = candidate.getManifesto();
                                if (manifesto == null || manifesto.isEmpty()) {
                                    manifesto = "No manifesto";
                                } else if (manifesto.length() > 50) {
                                    manifesto = manifesto.substring(0, 50) + "...";
                                }
                                
                                // Get election title
                                String electionTitle = "Election " + candidate.getElectionId();
                                for (Election election : elections) {
                                    if (election.getElectionId() == candidate.getElectionId()) {
                                        electionTitle = election.getTitle();
                                        break;
                                    }
                                }
                            %>
                            <tr>
                                <td><%= candidate.getCandidateId() %></td>
                                <td><%= candidateName %></td>
                                <td><%= candidate.getStudentId() %></td>
                                <td><%= candidate.getPosition() %></td>
                                <td><%= electionTitle %></td>
                                <td class="manifesto-cell" title="<%= candidate.getManifesto() != null ? candidate.getManifesto() : "" %>"><%= manifesto %></td>
                                <td class="vote-count"><%= candidate.getVoteCount() %></td>
                                <td class="actions-cell">
                                    <button class="edit-btn" onclick="editCandidate(<%= candidate.getCandidateId() %>, '<%= candidateName %>', '<%= candidate.getPosition() %>', '<%= candidate.getManifesto() != null ? candidate.getManifesto().replace("'", "\\'") : "" %>')">Edit</button>
                                    <button class="delete-btn" onclick="deleteCandidate(<%= candidate.getCandidateId() %>, '<%= candidateName.replace("'", "\\'") %>')">Delete</button>
                                </td>
                            </tr>
                            <% } %>
                            
                            <% if (candidates.isEmpty()) { %>
                            <tr>
                                <td colspan="8" class="no-data">
                                    No candidates found. Add new candidates using the "Add New Candidate" button.
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

   
    <div id="candidateModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title" id="modalTitle">Add New Candidate</h2>
            <form id="candidateForm" method="POST" action="candidatetab.jsp">
                <input type="hidden" id="actionType" name="action" value="add">
                <input type="hidden" id="editCandidateId" name="candidateId">
                
                <div class="form-group">
                    <label class="form-label">Student ID:</label>
                    <input type="text" id="studentId" name="studentId" class="form-input" required 
                           placeholder="Enter student ID">
                </div>
                
                <div class="form-group">
                    <label class="form-label">Election:</label>
                    <select id="electionId" name="electionId" class="form-select" required>
                        <option value="">Select Election</option>
                        <% for (Election election : elections) { %>
                        <option value="<%= election.getElectionId() %>"><%= election.getTitle() %></option>
                        <% } %>
                    </select>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Candidate Name:</label>
                    <input type="text" id="candidateName" name="candidateName" class="form-input" required
                           placeholder="Enter candidate's full name">
                </div>
                
                <div class="form-group">
                    <label class="form-label">Position:</label>
                    <input type="text" id="position" name="position" class="form-input" required 
                           placeholder="e.g., President, Vice President">
                </div>
                
                <div class="form-group">
                    <label class="form-label">Manifesto:</label>
                    <textarea id="manifesto" name="manifesto" class="form-textarea" 
                              placeholder="Describe the candidate's platform, goals, and vision..."></textarea>
                </div>
                
                <div class="modal-buttons">
                    <button type="submit" class="save-btn">Save</button>
                    <button type="button" class="cancel-btn" onclick="closeModal()">Cancel</button>
                </div>
            </form>
        </div>
    </div>

   
    <div id="deleteModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title">Confirm Delete</h2>
            <p style="color: #112D4E; margin-bottom: 20px;" id="deleteMessage">
                Are you sure you want to delete this candidate?
            </p>
            <form id="deleteForm" method="POST" action="candidatetab.jsp">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" id="deleteCandidateId" name="candidateId">
                
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
            document.getElementById('modalTitle').textContent = 'Add New Candidate';
            document.getElementById('actionType').value = 'add';
            document.getElementById('candidateForm').reset();
            document.getElementById('candidateModal').style.display = 'flex';
        }

        function editCandidate(candidateId, candidateName, position, manifesto) {
            document.getElementById('modalTitle').textContent = 'Edit Candidate';
            document.getElementById('actionType').value = 'update';
            document.getElementById('editCandidateId').value = candidateId;
            document.getElementById('candidateName').value = candidateName;
            document.getElementById('position').value = position;
            document.getElementById('manifesto').value = manifesto;
            
            document.getElementById('candidateModal').style.display = 'flex';
        }

        function closeModal() {
            document.getElementById('candidateModal').style.display = 'none';
        }

        function deleteCandidate(candidateId, candidateName) {
            document.getElementById('deleteMessage').textContent = 
                'Are you sure you want to delete candidate: ' + candidateName + ' (ID: ' + candidateId + ')?';
            document.getElementById('deleteCandidateId').value = candidateId;
            document.getElementById('deleteModal').style.display = 'flex';
        }

        function closeDeleteModal() {
            document.getElementById('deleteModal').style.display = 'none';
        }

        // Search function
        function searchCandidates() {
            var searchTerm = document.getElementById('searchInput').value.toLowerCase();
            var rows = document.getElementById('candidateTableBody').getElementsByTagName('tr');
            
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

        // Close modals when clicking outside
        window.onclick = function(event) {
            var candidateModal = document.getElementById('candidateModal');
            var deleteModal = document.getElementById('deleteModal');
            
            if (event.target === candidateModal) {
                closeModal();
            }
            if (event.target === deleteModal) {
                closeDeleteModal();
            }
        }

        // Form validation
        document.getElementById('candidateForm').addEventListener('submit', function(e) {
            var studentId = document.getElementById('studentId').value;
            var electionId = document.getElementById('electionId').value;
            var candidateName = document.getElementById('candidateName').value;
            var position = document.getElementById('position').value;
            
            if (!studentId.trim() || !electionId || !candidateName.trim() || !position.trim()) {
                e.preventDefault();
                alert('Please fill in all required fields!');
                return false;
            }
            
            return true;
        });
    </script>
</body>
</html>