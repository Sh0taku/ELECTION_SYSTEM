<%--
Document: resulttab
Created on: Jan 13, 2026
Author: Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.beans.Admin" %>
<%
// Check admin session
Admin admin = (Admin) session.getAttribute("admin");
if (admin == null) {
    response.sendRedirect("../login.jsp");
    return;
}
%>

<!DOCTYPE html>
<html>
<head>
    <title>Election Results - UITM Election System</title>
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

        /* Under Construction Content */
        .construction-content {
            padding: 30px;
            overflow-y: auto;
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .construction-container {
            text-align: center;
            max-width: 600px;
            padding: 40px;
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .construction-icon {
            font-size: 60px;
            margin-bottom: 20px;
            color: #ff9800;
        }

        .construction-title {
            color: #112D4E;
            font-size: 28px;
            margin-bottom: 15px;
        }

        .construction-message {
            color: #3F72AF;
            font-size: 16px;
            margin-bottom: 25px;
            line-height: 1.6;
        }

        .back-link {
            display: inline-block;
            padding: 10px 25px;
            background-color: #3F72AF;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            margin-top: 20px;
            font-weight: bold;
        }

        .back-link:hover {
            background-color: #112D4E;
        }

        .progress-text {
            color: #666;
            font-size: 14px;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
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
            <a href="admintab.jsp" class="nav-link">Manage Admins</a>
            <a href="resulttab.jsp" class="nav-link active">Current Results</a>
            <a href="../LogoutServlet" class="nav-link" onclick="return confirm('Logout?')">Logout</a>
        </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Top Header -->
        <div class="top-header">
            <div class="page-title">Election Results</div>
            <button class="logout-btn" onclick="if(confirm('Logout?')) window.location.href='../LogoutServlet'">Logout</button>
        </div>

        <!-- Under Construction Content -->
        <div class="construction-content">
            <div class="construction-container">
                <div class="construction-icon">🚧</div>
                <h1 class="construction-title">Results Page Under Construction</h1>
                <p class="construction-message">
                    The election results page is currently being developed.<br>
                    This feature will allow you to view detailed election results,<br>
                    generate reports, and analyze voting patterns.
                </p>
                
                <a href="admindashboard.jsp" class="back-link">Return to Dashboard</a>
                
                <div class="progress-text">
                    Expected Completion: Coming Soon<br>
                    Check back later for updates!
                </div>
            </div>
        </div>
    </div>
</body>
</html>