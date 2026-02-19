<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.beans.Admin" %>
<%@page import="com.election.dao.StudentDAO" %>
<%@page import="com.election.dao.ElectionDAO" %>
<%@page import="com.election.dao.CandidateDAO" %>
<%@page import="com.election.beans.Election" %>
<%@page import="com.election.beans.Candidate" %>
<%@page import="java.util.*" %>
<%

Admin admin = (Admin) session.getAttribute("admin");
if (admin == null) {
    response.sendRedirect("../login.jsp");
    return;
}

StudentDAO studentDAO = new StudentDAO();
ElectionDAO electionDAO = new ElectionDAO();
CandidateDAO candidateDAO = new CandidateDAO();


int totalStudents = studentDAO.getTotalStudents();
List allElections = electionDAO.getAllElections();



int ongoing = 0, upcoming = 0, ended = 0;
Election ongoingElection = null;

for (int i = 0; i < allElections.size(); i++) {
    Election e = (Election) allElections.get(i);
    String status = e.getStatus();
    if ("ONGOING".equals(status)) {
        ongoing++;
        ongoingElection = e;
    } else if ("UPCOMING".equals(status)) {
        upcoming++;
    } else if ("ENDED".equals(status)) {
        ended++;
    }
}



List candidates = new ArrayList();
int totalVotes = 0;

if (ongoingElection != null) {
    candidates = candidateDAO.getCandidatesByElection(ongoingElection.getElectionId());
    for (int i = 0; i < candidates.size(); i++) {
        Candidate c = (Candidate) candidates.get(i);
        totalVotes += c.getVoteCount();
    }
}
%>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard - UITM Election System</title>
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

 
        
        .dashboard-content {
            padding: 30px;
            overflow-y: auto;
            flex: 1;
        }


        
        .card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }

        .card h2 {
            color: #112D4E;
            margin-bottom: 15px;
            font-size: 18px;
            border-bottom: 2px solid #DBE2EF;
            padding-bottom: 10px;
        }

     
        .stats {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
            margin-bottom: 20px;
        }

        .stat-box {
            background: white;
            padding: 20px;
            text-align: center;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }

        .stat-number {
            font-size: 24px;
            color: #3F72AF;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .stat-label {
            color: #112D4E;
            font-size: 14px;
        }

   
        
        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .data-table th {
            background: #DBE2EF;
            color: #112D4E;
            text-align: left;
            padding: 10px;
            font-weight: bold;
            border-bottom: 2px solid #3F72AF;
        }

        .data-table td {
            padding: 10px;
            border-bottom: 1px solid #F9F7F7;
            color: #112D4E;
        }

        .data-table tr:hover {
            background: #F9F7F7;
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

   
        
        .btn {
            display: inline-block;
            background: #3F72AF;
            color: white;
            border: none;
            padding: 8px 15px;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            font-size: 14px;
        }
        
        .btn:hover {
            background: #112D4E;
        }
        
        .btn-small {
            padding: 5px 10px;
            font-size: 12px;
        }

    
        
        .welcome-section {
            margin-bottom: 30px;
        }

        .welcome-title {
            color: #112D4E;
            font-size: 32px;
            margin-bottom: 5px;
        }

        .welcome-subtitle {
            color: #3F72AF;
            font-size: 16px;
        }

  
        
        .info-box {
            background-color: #F9F7F7;
            padding: 15px;
            border-radius: 5px;
            margin-top: 20px;
            font-size: 14px;
            color: #666;
            border: 1px solid #DBE2EF;
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
            <a href="admindashboard.jsp" class="nav-link active">Dashboard</a>
            <a href="studenttab.jsp" class="nav-link">Manage Students</a>
            <a href="candidatetab.jsp" class="nav-link">Manage Candidates</a>
            <a href="electiontab.jsp" class="nav-link">Manage Elections</a>
            <a href="votetab.jsp" class="nav-link">Manage Votes</a>
            <a href="admintab.jsp" class="nav-link">Manage Admins</a>
            <a href="resulttab.jsp" class="nav-link">Current Results</a>
            <a href="../LogoutServlet" class="nav-link" onclick="return confirm('Logout?')">Logout</a>
        </div>
    </div>


       
    <div class="main-content">

        
        <div class="top-header">
            <div class="page-title">Admin Dashboard</div>
            <button class="logout-btn" onclick="if(confirm('Logout?')) window.location.href='../LogoutServlet'">Logout</button>
        </div>

   
        
        <div class="dashboard-content">
          
            
            <div class="welcome-section">
                <h1 class="welcome-title">Welcome, <%= admin.getUsername() %>!</h1>
                <p class="welcome-subtitle">UITM Election System Administration Panel</p>
            </div>

        
                
            <div class="stats">
                <div class="stat-box">
                    <div class="stat-number"><%= totalStudents %></div>
                    <div class="stat-label">Total Students</div>
                </div>
                <div class="stat-box">
                    <div class="stat-number"><%= allElections.size() %></div>
                    <div class="stat-label">Total Elections</div>
                </div>
                <div class="stat-box">
                    <div class="stat-number"><%= ongoing %></div>
                    <div class="stat-label">Ongoing Elections</div>
                </div>
            </div>

      
                    
            <% if (ongoingElection != null) { %>
            <div class="card">
                <h2>Current Election: <%= ongoingElection.getTitle() %></h2>
                <p style="color: #3F72AF; margin-bottom: 20px;"><%= ongoingElection.getDescription() %></p>
                
                <% if (!candidates.isEmpty()) { %>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Candidate</th>
                            <th>Position</th>
                            <th>Votes</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (int i = 0; i < candidates.size(); i++) { 
                            Candidate c = (Candidate) candidates.get(i);
                            String name = c.getCandidateName();
                            if (name == null || name.isEmpty()) {
                                name = "Candidate " + c.getCandidateId();
                            }
                        %>
                        <tr>
                            <td><strong><%= name %></strong></td>
                            <td><%= c.getPosition() %></td>
                            <td><strong style="color: #3F72AF;"><%= c.getVoteCount() %></strong></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <p style="margin-top: 15px; color: #3F72AF; text-align: center;">
                    Total Votes: <strong><%= totalVotes %></strong>
                </p>
                <% } else { %>
                <p style="color: #3F72AF; text-align: center; padding: 20px;">
                    No candidates yet for this election.
                </p>
                <% } %>
            </div>
            <% } %>

       
            
            <div class="card">
                <h2>All Elections</h2>
                <% if (!allElections.isEmpty()) { %>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Dates</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (int i = 0; i < allElections.size(); i++) { 
                            Election e = (Election) allElections.get(i);
                            String badgeClass = "";
                            if ("ONGOING".equals(e.getStatus())) badgeClass = "badge-ongoing";
                            else if ("UPCOMING".equals(e.getStatus())) badgeClass = "badge-upcoming";
                            else if ("ENDED".equals(e.getStatus())) badgeClass = "badge-ended";
                        %>
                        <tr>
                            <td><strong><%= e.getTitle() %></strong></td>
                            <td>
                                <% if (e.getStartDate() != null && e.getEndDate() != null) { %>
                                <%= e.getStartDate() %> to <%= e.getEndDate() %>
                                <% } else { %>
                                <span style="color: #666;">Dates not set</span>
                                <% } %>
                            </td>
                            <td>
                                <span class="badge <%= badgeClass %>">
                                    <%= e.getStatus() %>
                                </span>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <% } else { %>
                <p style="color: #3F72AF; text-align: center; padding: 20px;">
                    No elections created yet.
                </p>
                <% } %>
            </div>

        
            
            
            <div class="card">
                <h2>Quick Actions</h2>
                <div style="display: flex; gap: 10px; flex-wrap: wrap; margin-top: 10px;">
                    <a href="studenttab.jsp" class="btn">Manage Students</a>
                    <a href="candidatetab.jsp" class="btn">Manage Candidates</a>
                    <a href="electiontab.jsp" class="btn">Create Election</a>
                    <a href="resulttab.jsp" class="btn">View Results</a>
                </div>
            </div>

          
            <div class="info-box">
                <strong>System Information:</strong><br>
                Server Time: <%= new java.util.Date() %> | 
                Database: Apache Derby (ElectionDB) | 
                Server: GlassFish
            </div>
        </div>
    </div>
</body>
</html>