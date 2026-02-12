<%-- 
    Document   : thankyou
    Created on : Jan 10, 2026, 12:40:11 PM
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
    <title>Thank You - UITM Election</title>
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
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 20px;
        }
        
        .thankyou-container {
            background-color: white;
            border-radius: 15px;
            padding: 40px;
            text-align: center;
            max-width: 600px;
            width: 100%;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }
        
        .checkmark {
            width: 80px;
            height: 80px;
            background-color: #e1f7e1;
            border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
            margin: 0 auto 30px;
            font-size: 40px;
            color: #2e7d32;
        }
        
        .thankyou-title {
            color: #112D4E;
            font-size: 28px;
            margin-bottom: 15px;
        }
        
        .thankyou-message {
            color: #3F72AF;
            font-size: 18px;
            line-height: 1.6;
            margin-bottom: 30px;
        }
        
        .student-name {
            color: #112D4E;
            font-weight: bold;
            font-size: 20px;
            margin-bottom: 10px;
        }
        
        .election-info {
            background-color: #F9F7F7;
            padding: 20px;
            border-radius: 8px;
            margin: 25px 0;
            text-align: left;
        }
        
        .info-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            color: #112D4E;
        }
        
        .info-label {
            font-weight: bold;
        }
        
        .button-group {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 30px;
        }
        
        .dashboard-btn {
            padding: 12px 30px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        
        .dashboard-btn:hover {
            background-color: #112D4E;
        }
        
        .logout-btn {
            padding: 12px 30px;
            background-color: #DBE2EF;
            color: #112D4E;
            border: 1px solid #3F72AF;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        
        .logout-btn:hover {
            background-color: #3F72AF;
            color: white;
        }
        
        .note-box {
            background-color: #fff3e0;
            border: 1px solid #ef6c00;
            padding: 15px;
            border-radius: 8px;
            margin-top: 25px;
            text-align: left;
        }
        
        .note-title {
            color: #ef6c00;
            font-weight: bold;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <div class="thankyou-container">
        <div class="checkmark">✓</div>
        
        <h1 class="thankyou-title">Thank You for Voting!</h1>
        
        <p class="thankyou-message">
            Your vote has been successfully recorded and counted.
        </p>
        
        <div class="student-name"><%= student.getName() %></div>
        <p style="color: #3F72AF; margin-bottom: 25px;">Student ID: <%= student.getStudentId() %></p>
        
        <div class="election-info">
            <div class="info-item">
                <span class="info-label">Election:</span>
                <span>Student Council Election 2024</span>
            </div>
            <div class="info-item">
                <span class="info-label">Voted For:</span>
                <span>Ahmad bin Ali</span>
            </div>
            <div class="info-item">
                <span class="info-label">Vote Time:</span>
                <span>10 December 2024, 14:30</span>
            </div>
            <div class="info-item">
                <span class="info-label">Transaction ID:</span>
                <span>VT20241210-<%= student.getStudentId() %></span>
            </div>
        </div>
        
        <div class="note-box">
            <div class="note-title">Important Information:</div>
            <p style="color: #112D4E; line-height: 1.6; font-size: 14px;">
                1. Your vote is now final and cannot be changed.<br>
                2. Election results will be announced on 16 December 2024.<br>
                3. You can view results from your dashboard when available.<br>
                4. Keep this transaction ID for any future reference.
            </p>
        </div>
        
        <div class="button-group">
            <a href="studentdashboard.jsp" class="dashboard-btn">Back to Dashboard</a>
            <a href="<%= request.getContextPath() %>/LogoutServlet" class="logout-btn">Logout</a>
        </div>
        
        <p style="color: #3F72AF; margin-top: 30px; font-size: 14px;">
            UITM Election System • Secure Online Voting Platform
        </p>
    </div>
</body>
</html>