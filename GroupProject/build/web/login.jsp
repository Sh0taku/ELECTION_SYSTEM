<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.dao.*" %>
<%@page import="com.election.beans.*" %>
<%@page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>UITM Election System - Login</title>
    <style>
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
        
       
        .container {
            display: flex;
            width: 900px;
            margin: 50px auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
       
        .left-side {
            width: 50%;
            background-color: #DBE2EF;
            padding: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .image-box {
            text-align: center;
            color: #112D4E;
        }
        
        /* Right Side -- Login Form---------------------- */
        .right-side {
            width: 50%;
            padding: 40px;
        }
        
        .login-title {
            color: #112D4E;
            margin-bottom: 30px;
            text-align: center;
        }
        
        /* Tab---------------------- */
        .tabs {
            display: flex;
            margin-bottom: 20px;
            border-bottom: 2px solid #DBE2EF;
        }
        
        .tab {
            padding: 10px 20px;
            background: none;
            border: none;
            cursor: pointer;
            font-size: 16px;
            flex: 1;
        }
        
        .tab.active {
            background-color: #3F72AF;
            color: white;
        }
        
        /* Form----------------- */
        .form {
            display: none;
        }
        
        .form.active {
            display: block;
        }
        
        .input-group {
            margin-bottom: 20px;
        }
        
        .input-group label {
            display: block;
            margin-bottom: 5px;
            color: #112D4E;
            font-weight: bold;
        }
        
        .input-group input {
            width: 100%;
            padding: 10px;
            border: 1px solid #DBE2EF;
            border-radius: 5px;
            font-size: 16px;
        }
        
        /* Buttons------------------ */
        .login-button {
            width: 100%;
            padding: 12px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            margin-bottom: 10px;
        }
        
        .login-button:hover {
            background-color: #112D4E;
        }
        
        .register-button {
            width: 100%;
            padding: 12px;
            background-color: #DBE2EF;
            color: #112D4E;
            border: 1px solid #3F72AF;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
        }
        
        .register-button:hover {
            background-color: #3F72AF;
            color: white;
        }
        
        /* kaki */
        .footer {
            background-color: #112D4E;
            color: white;
            padding: 20px;
            margin-top: 50px;
        }
        
        .footer-content {
            display: flex;
            justify-content: space-between;
            max-width: 900px;
            margin: 0 auto;
        }
        
        .footer-section h3 {
            color: #DBE2EF;
            margin-bottom: 10px;
        }
        
        .copyright {
            text-align: center;
            margin-top: 20px;
            padding-top: 20px;
            border-top: 1px solid #3F72AF;
            color: #DBE2EF;
        }
    </style>
</head>
<body>
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
    
    <!-- Main Content -->
    <div class="container">
        <!-- Left Side - Image -->
        <div class="left-side">
            <div class="image-box">
                <h2>UITM Election System</h2>
                <p>Secure Online Voting Platform</p>
                <p style="margin-top: 20px; color: #3F72AF;">
                  
                </p>
            </div>
        </div>
        
        <!-- right - Login Form -->
        <div class="right-side">
            <h2 class="login-title">Login to Your Account</h2>
            
            <!-- Tabs for Student/Admin -->
            <div class="tabs">
                <button class="tab active" onclick="showForm('student')">Student Login</button>
                <button class="tab" onclick="showForm('admin')">Admin Login</button>
            </div>
            
            <!-- Student login form -->
            <form id="studentForm" class="form active" action="StudentLoginServlet" method="POST" onsubmit="return checkForm(this)">
                <div class="input-group">
                    <label>Student ID:</label>
                    <input type="text" name="studentId" placeholder="Enter student ID" required>
                </div>
                
                <div class="input-group">
                    <label>Password:</label>
                    <input type="password" name="password" placeholder="Enter password" required>
                </div>
                
                <button type="submit" class="login-button">Login as Student</button>
            </form>
            
            <!-- Admin login form -->
            <form id="adminForm" class="form" action="AdminLoginServlet2" method="POST" onsubmit="return checkForm(this)">
                <div class="input-group">
                    <label>Username:</label>
                    <input type="text" name="username" placeholder="Enter admin username" required>
                </div>
                
                <div class="input-group">
                    <label>Password:</label>
                    <input type="password" name="password" placeholder="Enter admin password" required>
                </div>
                
                <button type="submit" class="login-button">Login as Admin</button>
            </form>
            
            <!-- register button -->
            <button class="register-button" onclick="window.location.href='register.jsp'">
                New Student? Click to Register
            </button>
        </div>
    </div>
    
    <!-- Kaki -->
    <div class="footer">
        <div class="footer-content">
            <div class="footer-section">
                <h3>UITM Election System</h3>
                <p>Secure online voting platform for campus elections</p>
                <p>Managed by University Technology MARA</p>
            </div>
            
            <div class="footer-section">
                <h3>Contact Information</h3>
                <p>Email: election@uitm.edu.my</p>
                <p>Phone: 03-5544 2000</p>
                <p>Location: Shah Alam Campus</p>
            </div>
        </div>
        
        <div class="copyright">
            <p>&copy; 2024 UITM Election System. CSC584 Group Project.</p>
            <p>All rights reserved.</p>
        </div>
    </div>
    
    <script>
function checkForm(form) {
    const inputs = form.querySelectorAll('input[required]');
    
    for (let input of inputs) {
        if (!input.value.trim()) {
            alert('Please fill in ' + input.previousElementSibling.textContent);
            input.style.borderColor = 'red';
            return false;
        }
    }
    return true;
}


function showForm(formType) {
    document.getElementById('studentForm').classList.remove('active');
    document.getElementById('adminForm').classList.remove('active');
    
    document.querySelectorAll('.tab').forEach(tab => {
        tab.classList.remove('active');
    });
    
    if (formType === 'student') {
        document.getElementById('studentForm').classList.add('active');
        event.target.classList.add('active');
    } else {
        document.getElementById('adminForm').classList.add('active');
        event.target.classList.add('active');
    }
}
    </script>
</body>
</html>