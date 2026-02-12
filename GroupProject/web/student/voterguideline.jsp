<%-- 
    Document   : voterguideline
    Created on : Jan 10, 2026, 12:35:36 PM
    Author     : Emir
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.beans.Student" %>
<%
    Student student = (Student) session.getAttribute("student");
    if (student == null) {
        response.sendRedirect("../login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Voter Guideline - UITM Election</title>
    <style>
        /* Same base styles as dashboard */
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
        
        .student-info {
            padding-bottom: 20px;
            border-bottom: 1px solid #3F72AF;
            margin-bottom: 20px;
        }
        
        .student-name {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .student-id {
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
        
        /* Guidelines Content */
        .guidelines-content {
            padding: 30px;
            overflow-y: auto;
            flex: 1;
        }
        
        .guidelines-container {
            max-width: 800px;
            margin: 0 auto;
        }
        
        .section-title {
            color: #112D4E;
            margin: 25px 0 15px 0;
            padding-bottom: 10px;
            border-bottom: 2px solid #3F72AF;
        }
        
        .rule-item {
            background-color: white;
            padding: 20px;
            margin-bottom: 15px;
            border-radius: 8px;
            border-left: 4px solid #3F72AF;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        
        .rule-number {
            display: inline-block;
            background-color: #3F72AF;
            color: white;
            width: 30px;
            height: 30px;
            text-align: center;
            line-height: 30px;
            border-radius: 50%;
            margin-right: 10px;
            font-weight: bold;
        }
        
        .rule-text {
            color: #112D4E;
            line-height: 1.6;
            display: inline;
        }
        
        .warning-box {
            background-color: #fff3e0;
            border: 2px solid #ef6c00;
            padding: 20px;
            border-radius: 8px;
            margin-top: 30px;
        }
        
        .warning-title {
            color: #ef6c00;
            font-weight: bold;
            margin-bottom: 10px;
        }
        
        .important-note {
            background-color: #e1f7e1;
            border: 2px solid #2e7d32;
            padding: 20px;
            border-radius: 8px;
            margin-top: 20px;
        }
        
        .note-title {
            color: #2e7d32;
            font-weight: bold;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <!-- Left Navigation Panel -->
    <div class="nav-panel">
        <div class="student-info">
            <div class="student-name"><%= student.getName() %></div>
            <div class="student-id"><%= student.getStudentId() %></div>
        </div>
        
        <div class="nav-links">
            <a href="studentdashboard.jsp" class="nav-link">Dashboard</a>
            <a href="vote.jsp" class="nav-link">Vote</a>
            <a href="voterguideline.jsp" class="nav-link active">Voter Guideline</a>
        </div>
    </div>
    
    <!-- Main Content -->
    <div class="main-content">
        <!-- Top Header -->
        <div class="top-header">
            <div class="page-title">Voter Guidelines</div>
            <button class="logout-btn" onclick="window.location.href='<%= request.getContextPath() %>/LogoutServlet'">
                Logout
            </button>
        </div>
        
        <!-- Guidelines Content -->
        <div class="guidelines-content">
            <div class="guidelines-container">
                <h1 style="color: #112D4E; margin-bottom: 20px;">UITM Election Guidelines</h1>
                <p style="color: #3F72AF; margin-bottom: 30px; font-size: 16px;">
                    Please read and follow these guidelines for a fair and transparent election process.
                </p>
                
                <h2 class="section-title">General Rules</h2>
                
                <div class="rule-item">
                    <span class="rule-number">1</span>
                    <span class="rule-text">Only registered UITM students are eligible to vote.</span>
                </div>
                
                <div class="rule-item">
                    <span class="rule-number">2</span>
                    <span class="rule-text">Each student can vote only ONCE per election.</span>
                </div>
                
                <div class="rule-item">
                    <span class="rule-number">3</span>
                    <span class="rule-text">Voting is confidential and cannot be changed once submitted.</span>
                </div>
                
                <div class="rule-item">
                    <span class="rule-number">4</span>
                    <span class="rule-text">Do not share your login credentials with anyone.</span>
                </div>
                
                <div class="rule-item">
                    <span class="rule-number">5</span>
                    <span class="rule-text">Voting period: 8:00 AM to 6:00 PM on election day.</span>
                </div>
                
                <h2 class="section-title">Voting Process</h2>
                
                <div class="rule-item">
                    <span class="rule-number">1</span>
                    <span class="rule-text">Login to the election system using your student credentials.</span>
                </div>
                
                <div class="rule-item">
                    <span class="rule-number">2</span>
                    <span class="rule-text">Select the election you wish to vote in.</span>
                </div>
                
                <div class="rule-item">
                    <span class="rule-number">3</span>
                    <span class="rule-text">Review all candidate information before voting.</span>
                </div>
                
                <div class="rule-item">
                    <span class="rule-number">4</span>
                    <span class="rule-text">Select your preferred candidate and confirm your vote.</span>
                </div>
                
                <div class="rule-item">
                    <span class="rule-number">5</span>
                    <span class="rule-text">Wait for confirmation message before closing the window.</span>
                </div>
                
                <h2 class="section-title">Prohibited Actions</h2>
                
                <div class="rule-item">
                    <span class="rule-number">1</span>
                    <span class="rule-text">Do not attempt to vote multiple times.</span>
                </div>
                
                <div class="rule-item">
                    <span class="rule-number">2</span>
                    <span class="rule-text">Do not coerce or influence other voters.</span>
                </div>
                
                <div class="rule-item">
                    <span class="rule-number">3</span>
                    <span class="rule-text">Do not use someone else's account to vote.</span>
                </div>
                
                <div class="rule-item">
                    <span class="rule-number">4</span>
                    <span class="rule-text">Do not share your voting choice publicly.</span>
                </div>
                
                <div class="warning-box">
                    <div class="warning-title">⚠️ IMPORTANT WARNING</div>
                    <p style="color: #112D4E; line-height: 1.6;">
                        Any violation of these guidelines will result in:<br>
                        • Immediate disqualification of your vote<br>
                        • Disciplinary action by the university<br>
                        • Possible suspension of voting privileges
                    </p>
                </div>
                
                <div class="important-note">
                    <div class="note-title">📝 IMPORTANT NOTE</div>
                    <p style="color: #112D4E; line-height: 1.6;">
                        If you encounter any technical issues during voting:<br>
                        1. Contact Election Technical Support immediately<br>
                        2. Do NOT attempt to vote from another device<br>
                        3. Report the issue with your student ID and time of error
                    </p>
                </div>
                
                <div style="text-align: center; margin-top: 40px; padding-top: 20px; border-top: 1px solid #DBE2EF;">
                    <p style="color: #112D4E; font-size: 14px;">
                        Last updated: December 2024<br>
                        UITM Election Committee
                    </p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>