<%-- 
    Document   : register
    Created on : Jan 10, 2026, 12:01:36 AM
    Author     : Emir
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.dao.*" %>
<%@page import="com.election.beans.*" %>
<%@page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register - UITM Election System</title>
    <style>
        /* Same CSS from login page for consistency */
        body {
            font-family: Arial, sans-serif;
            background-color: #F9F7F7;
            margin: 0;
            padding: 0;
        }
        
        .header {
            background-color: #112D4E;
            color: white;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .title {
            font-size: 24px;
            color: white;
        }
        
        .nav ul {
            list-style: none;
            margin: 0;
            padding: 0;
            display: flex;
            gap: 20px;
        }
        
        .nav a {
            color: white;
            text-decoration: none;
            padding: 8px 15px;
            border-radius: 4px;
            transition: background-color 0.3s;
        }
        
        .nav a:hover {
            background-color: #3F72AF;
        }
        
        .register-container {
            width: 500px;
            margin: 40px auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        
        .register-container h2 {
            color: #112D4E;
            text-align: center;
            margin-bottom: 30px;
        }
        
        .form-row {
            display: flex;
            gap: 15px;
            margin-bottom: 15px;
        }
        
        .form-group {
            flex: 1;
            margin-bottom: 15px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            color: #112D4E;
            font-weight: bold;
        }
        
        .form-group input, .form-group select {
            width: 100%;
            padding: 10px;
            border: 1px solid #DBE2EF;
            border-radius: 5px;
            font-size: 16px;
        }
        
        .register-button {
            width: 100%;
            padding: 12px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            margin-top: 10px;
        }
        
        .register-button:hover {
            background-color: #112D4E;
        }
        
        .back-link {
            display: block;
            text-align: center;
            margin-top: 15px;
            color: #3F72AF;
            text-decoration: none;
        }
        
        .back-link:hover {
            text-decoration: underline;
        }
        
        .footer {
            background-color: #112D4E;
            color: white;
            padding: 20px;
            margin-top: 50px;
        }
        
        .footer-content {
            max-width: 900px;
            margin: 0 auto;
            text-align: center;
        }
        
        .copyright {
            margin-top: 20px;
            padding-top: 20px;
            border-top: 1px solid #3F72AF;
            color: #DBE2EF;
        }
    </style>
</head>
<body>
    <!-- Header with Right-aligned Navigation -->
    <div class="header">
        <div class="title">
            <h2>UITM Election System</h2>
        </div>
        
        <div class="nav">
            <ul>
                <li><a href="login.jsp">Home</a></li>
                <li><a href="about.jsp">About</a></li>
                <li><a href="contact.jsp">Contact</a></li>
                <li><a href="register.jsp">Register</a></li>
            </ul>
        </div>
    </div>
    
    <!-- Registration Form -->
    <div class="register-container">
        <h2>Student Registration</h2>
        <p style="text-align: center; color: #3F72AF; margin-bottom: 20px;">
            Create your account to vote in UITM elections
        </p>
        
        <form action="StudentRegisterServlet" method="POST" onsubmit="return validateRegisterForm(this)">
            <div class="form-row">
                <div class="form-group">
                    <label>Student ID:</label>
                    <input type="text" name="studentId" required>
                </div>
                
                <div class="form-group">
                    <label>Full Name:</label>
                    <input type="text" name="name" required>
                </div>
            </div>
            
            <div class="form-group">
                <label>Email:</label>
                <input type="email" name="email" required>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label>Password:</label>
                    <input type="password" name="password" required>
                </div>
                
                <div class="form-group">
                    <label>Confirm Password:</label>
                    <input type="password" name="confirmPassword" required>
                </div>
            </div>
            
            <div class="form-group">
                <label>Faculty:</label>
                <select name="faculty">
                    <option value="FCSIT">Faculty of Computer Science & IT</option>
                    <option value="FKE">Faculty of Engineering</option>
                    <option value="FBM">Faculty of Business</option>
                    <option value="FSSH">Faculty of Social Sciences</option>
                    <option value="FSPU">Faculty of Sports Science</option>
                </select>
            </div>
            
            <button type="submit" class="register-button">Register Account</button>
            
            <a href="login.jsp" class="back-link">Back to Login Page</a>
        </form>
    </div>
    
    <!-- Footer -->
    <div class="footer">
        <div class="footer-content">
            <h3>UITM Election System</h3>
            <p>University Technology MARA</p>
            <p>Secure Campus Voting Platform</p>
            
            <div class="copyright">
                <p>&copy; 2024 UITM Election System. CSC584 Group Project.</p>
            </div>
        </div>
    </div>
    
<script>
    // Improved form validation
    function validateRegisterForm(form) {
        // 1. Check all required fields
        const requiredInputs = form.querySelectorAll('input[required]');
        for (let input of requiredInputs) {
            if (!input.value.trim()) {
                alert('Please fill in ' + input.previousElementSibling.textContent);
                input.style.borderColor = 'red';
                return false;
            }
        }
        
        // 2. Check password match
        const password = document.querySelector('input[name="password"]').value;
        const confirmPassword = document.querySelector('input[name="confirmPassword"]').value;
        
        if (password !== confirmPassword) {
            alert('Passwords do not match!');
            document.querySelector('input[name="confirmPassword"]').style.borderColor = 'red';
            return false;
        }
        
        // 3. Check password length (optional but good)
        if (password.length < 6) {
            alert('Password should be at least 6 characters long.');
            document.querySelector('input[name="password"]').style.borderColor = 'red';
            return false;
        }
        
        return true; // All good, submit form
    }
</script>
</body>
</html>