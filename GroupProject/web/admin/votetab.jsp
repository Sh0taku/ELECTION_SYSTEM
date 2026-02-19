<%--
Document: votetab
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.beans.Admin" %>
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
String voteIdStr = request.getParameter("voteId");

Connection conn = null;
PreparedStatement pstmt = null;
Statement stmt = null;
ResultSet rs = null;

if (action != null) {
    try {
        conn = DBConnection.getConnection();
        
        if ("delete".equals(action) && voteIdStr != null) {
            int voteId = Integer.parseInt(voteIdStr);
            
 
            String getVoteSql = "SELECT STUDENT_ID, ELECTION_ID, CANDIDATE_ID FROM VOTES WHERE VOTE_ID = ?";
            pstmt = conn.prepareStatement(getVoteSql);
            pstmt.setInt(1, voteId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String studentId = rs.getString("STUDENT_ID");
                int electionId = rs.getInt("ELECTION_ID");
                int candidateId = rs.getInt("CANDIDATE_ID");

                conn.setAutoCommit(false);
                
                try {
                    String deleteSql = "DELETE FROM VOTES WHERE VOTE_ID = ?";
                    pstmt = conn.prepareStatement(deleteSql);
                    pstmt.setInt(1, voteId);
                    pstmt.executeUpdate();

                    String updateCandidateSql = "UPDATE CANDIDATES SET VOTE_COUNT = VOTE_COUNT - 1 WHERE CANDIDATE_ID = ?";
                    pstmt = conn.prepareStatement(updateCandidateSql);
                    pstmt.setInt(1, candidateId);
                    pstmt.executeUpdate();
    
                    String updateStudentSql = "UPDATE STUDENTS SET HAS_VOTED = 0 WHERE STUDENT_ID = ?";
                    pstmt = conn.prepareStatement(updateStudentSql);
                    pstmt.setString(1, studentId);
                    pstmt.executeUpdate();
                    
                    conn.commit();
                    message = "Vote deleted successfully!";
                    messageType = "success";
                    
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                } finally {
                    conn.setAutoCommit(true);
                }
            }
        }
        
    } catch (SQLException e) {
        message = "Database error: " + e.getMessage();
        messageType = "error";
        e.printStackTrace();
    } catch (NumberFormatException e) {
        message = "Invalid vote ID!";
        messageType = "error";
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
}

List<Map<String, Object>> votes = new ArrayList<Map<String, Object>>();
SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");

try {
    conn = DBConnection.getConnection();
    stmt = conn.createStatement();

    String voteSql = "SELECT " +
                     "V.VOTE_ID, V.STUDENT_ID, V.ELECTION_ID, V.CANDIDATE_ID, V.VOTE_TIME, " +
                     "S.NAME AS STUDENT_NAME, " +
                     "E.TITLE AS ELECTION_TITLE, " +
                     "C.CANDIDATE_NAME, C.POSITION " +
                     "FROM VOTES V " +
                     "JOIN STUDENTS S ON V.STUDENT_ID = S.STUDENT_ID " +
                     "JOIN ELECTIONS E ON V.ELECTION_ID = E.ELECTION_ID " +
                     "JOIN CANDIDATES C ON V.CANDIDATE_ID = C.CANDIDATE_ID " +
                     "ORDER BY V.VOTE_TIME DESC";
    
    rs = stmt.executeQuery(voteSql);
    
    while (rs.next()) {
        Map<String, Object> vote = new HashMap<String, Object>();
        vote.put("voteId", rs.getInt("VOTE_ID"));
        vote.put("studentId", rs.getString("STUDENT_ID"));
        vote.put("studentName", rs.getString("STUDENT_NAME"));
        vote.put("electionId", rs.getInt("ELECTION_ID"));
        vote.put("electionTitle", rs.getString("ELECTION_TITLE"));
        vote.put("candidateId", rs.getInt("CANDIDATE_ID"));
        vote.put("candidateName", rs.getString("CANDIDATE_NAME"));
        vote.put("position", rs.getString("POSITION"));
        vote.put("voteTime", rs.getTimestamp("VOTE_TIME"));
        
        votes.add(vote);
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
    <title>Manage Votes - UITM Election System</title>
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

        .stats-container {
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
        }

        .stat-card {
            background-color: #3F72AF;
            color: white;
            padding: 15px 25px;
            border-radius: 8px;
            text-align: center;
            min-width: 150px;
        }

        .stat-number {
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .stat-label {
            font-size: 14px;
            opacity: 0.9;
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
            gap: 5px;
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

        .view-btn {
            padding: 5px 10px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 11px;
        }

        .view-btn:hover {
            background-color: #112D4E;
        }

 
        .vote-info {
            font-size: 12px;
            color: #666;
        }

        .candidate-info {
            color: #3F72AF;
            font-weight: bold;
        }

        .student-info {
            color: #2e7d32;
            font-weight: bold;
        }

        .time-info {
            color: #666;
            font-size: 11px;
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

        .vote-details {
            margin-bottom: 20px;
        }

        .detail-row {
            display: flex;
            margin-bottom: 10px;
            padding-bottom: 10px;
            border-bottom: 1px solid #F9F7F7;
        }

        .detail-label {
            flex: 1;
            font-weight: bold;
            color: #112D4E;
        }

        .detail-value {
            flex: 2;
            color: #3F72AF;
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
            <a href="candidatetab.jsp" class="nav-link">Manage Candidates</a>
            <a href="electiontab.jsp" class="nav-link">Manage Elections</a>
            <a href="votetab.jsp" class="nav-link active">Manage Votes</a>
            <a href="admintab.jsp" class="nav-link">Manage Admins</a>
            <a href="resulttab.jsp" class="nav-link">Current Results</a>
            <a href="../LogoutServlet" class="nav-link" onclick="return confirm('Logout?')">Logout</a>
        </div>
    </div>


    <div class="main-content">

        <div class="top-header">
            <div class="page-title">Manage Votes</div>
            <button class="logout-btn" onclick="if(confirm('Logout?')) window.location.href='../LogoutServlet'">Logout</button>
        </div>


        <div class="management-content">
            <% if (!message.isEmpty()) { %>
            <div class="message-alert <%= messageType %>">
                <%= message %>
            </div>
            <% } %>

    
            <div class="action-bar">
                <div class="stats-container">
                    <div class="stat-card">
                        <div class="stat-number"><%= votes.size() %></div>
                        <div class="stat-label">Total Votes</div>
                    </div>
                    <% 

                    int todayVotes = 0;
                    java.util.Date today = new java.util.Date();
                    SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String todayStr = dateOnlyFormat.format(today);
                    
                    for (Map<String, Object> vote : votes) {
                        Timestamp voteTime = (Timestamp) vote.get("voteTime");
                        String voteDateStr = dateOnlyFormat.format(voteTime);
                        if (voteDateStr.equals(todayStr)) {
                            todayVotes++;
                        }
                    }
                    %>
                    <div class="stat-card" style="background-color: #2e7d32;">
                        <div class="stat-number"><%= todayVotes %></div>
                        <div class="stat-label">Today's Votes</div>
                    </div>
                </div>
            </div>


            <div class="data-table">
                <div class="table-header">
                    <div class="table-title">Voting Records (<%= votes.size() %> votes)</div>
                </div>
                
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                            <tr>
                                <th>Vote ID</th>
                                <th>Student</th>
                                <th>Election</th>
                                <th>Candidate</th>
                                <th>Position</th>
                                <th>Time</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="voteTableBody">
                            <% 
                            for (Map<String, Object> vote : votes) { 
                                String voteTime = dateFormat.format(vote.get("voteTime"));
                            %>
                            <tr>
                                <td><strong>#<%= vote.get("voteId") %></strong></td>
                                <td>
                                    <div class="student-info"><%= vote.get("studentName") %></div>
                                    <div class="vote-info">ID: <%= vote.get("studentId") %></div>
                                </td>
                                <td>
                                    <div><%= vote.get("electionTitle") %></div>
                                    <div class="vote-info">Election ID: <%= vote.get("electionId") %></div>
                                </td>
                                <td>
                                    <div class="candidate-info"><%= vote.get("candidateName") %></div>
                                    <div class="vote-info">Candidate ID: <%= vote.get("candidateId") %></div>
                                </td>
                                <td><%= vote.get("position") %></td>
                                <td>
                                    <div><%= voteTime %></div>
                                    <div class="time-info">Cast on <%= voteTime %></div>
                                </td>
                                <td class="actions-cell">
                                    <button class="view-btn" onclick="viewVoteDetails(
                                        <%= vote.get("voteId") %>, 
                                        '<%= vote.get("studentName").toString().replace("'", "\\'") %>',
                                        '<%= vote.get("studentId").toString().replace("'", "\\'") %>',
                                        '<%= vote.get("electionTitle").toString().replace("'", "\\'") %>',
                                        '<%= vote.get("candidateName").toString().replace("'", "\\'") %>',
                                        '<%= vote.get("position").toString().replace("'", "\\'") %>',
                                        '<%= voteTime.replace("'", "\\'") %>'
                                    )">View</button>
                                    <button class="delete-btn" onclick="deleteVote(
                                        <%= vote.get("voteId") %>, 
                                        '<%= vote.get("studentName").toString().replace("'", "\\'") %>',
                                        '<%= vote.get("candidateName").toString().replace("'", "\\'") %>'
                                    )">Delete</button>
                                </td>
                            </tr>
                            <% } %>
                            
                            <% if (votes.isEmpty()) { %>
                            <tr>
                                <td colspan="7" class="no-data">
                                    No voting records found. Votes will appear here when students cast their votes.
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>


    <div id="viewModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title">Vote Details</h2>
            
            <div class="vote-details">
                <div class="detail-row">
                    <div class="detail-label">Vote ID:</div>
                    <div class="detail-value" id="detailVoteId"></div>
                </div>
                <div class="detail-row">
                    <div class="detail-label">Student:</div>
                    <div class="detail-value">
                        <div id="detailStudentName"></div>
                        <div style="font-size: 12px; color: #666;" id="detailStudentId"></div>
                    </div>
                </div>
                <div class="detail-row">
                    <div class="detail-label">Election:</div>
                    <div class="detail-value" id="detailElectionTitle"></div>
                </div>
                <div class="detail-row">
                    <div class="detail-label">Candidate:</div>
                    <div class="detail-value">
                        <div id="detailCandidateName"></div>
                        <div style="font-size: 12px; color: #666;">Position: <span id="detailPosition"></span></div>
                    </div>
                </div>
                <div class="detail-row">
                    <div class="detail-label">Time Cast:</div>
                    <div class="detail-value" id="detailVoteTime"></div>
                </div>
            </div>
            
            <div class="modal-buttons">
                <button type="button" class="cancel-btn" onclick="closeViewModal()">Close</button>
            </div>
        </div>
    </div>


                        
    <div id="deleteModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title">Confirm Delete Vote</h2>
            <p style="color: #112D4E; margin-bottom: 20px;" id="deleteMessage">
                Are you sure you want to delete this vote?
            </p>
            <p style="color: #c62828; font-size: 14px; margin-bottom: 20px; padding: 10px; background-color: #ffebee; border-radius: 5px;">
                ⚠️ Warning: Deleting a vote will:<br>
                1. Remove the voting record<br>
                2. Decrease candidate's vote count<br>
                3. Allow student to vote again<br>
                This action cannot be undone!
            </p>
            <form id="deleteForm" method="POST" action="votetab.jsp">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" id="deleteVoteId" name="voteId">
                
                <div class="modal-buttons">
                    <button type="submit" class="save-btn" style="background-color: #c62828;">Delete Vote</button>
                    <button type="button" class="cancel-btn" onclick="closeDeleteModal()">Cancel</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function viewVoteDetails(voteId, studentName, studentId, electionTitle, candidateName, position, voteTime) {
            document.getElementById('detailVoteId').textContent = '#' + voteId;
            document.getElementById('detailStudentName').textContent = studentName;
            document.getElementById('detailStudentId').textContent = 'ID: ' + studentId;
            document.getElementById('detailElectionTitle').textContent = electionTitle;
            document.getElementById('detailCandidateName').textContent = candidateName;
            document.getElementById('detailPosition').textContent = position;
            document.getElementById('detailVoteTime').textContent = voteTime;
            
            document.getElementById('viewModal').style.display = 'flex';
        }

        function closeViewModal() {
            document.getElementById('viewModal').style.display = 'none';
        }

        function deleteVote(voteId, studentName, candidateName) {
            document.getElementById('deleteMessage').innerHTML = 
                'Are you sure you want to delete vote record?<br><br>' +
                '<strong>Student:</strong> ' + studentName + '<br>' +
                '<strong>Candidate:</strong> ' + candidateName + '<br>' +
                '<strong>Vote ID:</strong> ' + voteId;
            document.getElementById('deleteVoteId').value = voteId;
            document.getElementById('deleteModal').style.display = 'flex';
        }

        function closeDeleteModal() {
            document.getElementById('deleteModal').style.display = 'none';
        }



        function searchVotes() {
            var searchTerm = document.getElementById('searchInput').value.toLowerCase();
            var rows = document.getElementById('voteTableBody').getElementsByTagName('tr');
            
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



        window.onclick = function(event) {
            var viewModal = document.getElementById('viewModal');
            var deleteModal = document.getElementById('deleteModal');
            
            if (event.target === viewModal) {
                closeViewModal();
            }
            if (event.target === deleteModal) {
                closeDeleteModal();
            }
        }



        setTimeout(function() {
            window.location.reload();
        }, 30000);
    </script>
</body>
</html>