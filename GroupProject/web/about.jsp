<%-- 
    Document   : about
    Created on : Jan 3, 2026, 10:35:35 PM
    Author     :Habll
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>About - UITM Election System</title>
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
        

        .about-container {
            max-width: 900px;
            margin: 40px auto;
            padding: 30px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        
        .about-title {
            color: #112D4E;
            text-align: center;
            margin-bottom: 30px;
            border-bottom: 2px solid #3F72AF;
            padding-bottom: 10px;
        }
        
        .section {
            margin-bottom: 30px;
        }
        
        .section h3 {
            color: #3F72AF;
            margin-bottom: 15px;
        }
        
        .section p {
            line-height: 1.6;
            color: #112D4E;
            margin-bottom: 15px;
        }
        
        .features {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
            margin-top: 20px;
        }
        
        .feature-box {
            background-color: #DBE2EF;
            padding: 20px;
            border-radius: 8px;
            border-left: 4px solid #3F72AF;
        }
        
        .feature-box h4 {
            color: #112D4E;
            margin-top: 0;
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
        
        .back-button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #3F72AF;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            margin-top: 20px;
        }
        
        .back-button:hover {
            background-color: #112D4E;
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
    

    <div class="about-container">
        <h2 class="about-title">About UITM Election System</h2>
        
        <div class="section">
            <h3>Our Mission</h3>
            <p>The UITM Election System is a secure online platform designed to facilitate transparent and efficient campus elections. Our mission is to provide a reliable digital voting solution that ensures fairness, security, and accessibility for all UITM students.</p>
        </div>
        
        <div class="section">
            <h3>System Features</h3>
            <div class="features">
                <div class="feature-box">
                    <h4>Secure Voting</h4>
                    <p>Encrypted voting process with student authentication to ensure vote integrity.</p>
                </div>
                
                <div class="feature-box">
                    <h4>Real-time Results</h4>
                    <p>Instant vote counting and result display after election closure.</p>
                </div>
                
                <div class="feature-box">
                    <h4>Easy Registration</h4>
                    <p>Simple student registration using valid UITM student credentials.</p>
                </div>
                
                <div class="feature-box">
                    <h4>Admin Management</h4>
                    <p>Comprehensive tools for election officers to manage candidates and voting.</p>
                </div>
            </div>
        </div>
        
        <div class="section">
            <h3>How It Works</h3>
            <p>1. Students register using their UITM student ID and details</p>
            <p>2. During election period, students login and cast their votes</p>
            <p>3. Votes are securely recorded and counted in real-time</p>
            <p>4. Results are displayed after election ends</p>
        </div>
        
        <div class="section">
            <h3>For Students</h3>
            <p>All active UITM students are eligible to participate in campus elections through this system. Ensure your student account is active and your details are up-to-date.</p>
        </div>
        
        <div style="text-align: center; margin-top: 30px;">
            <a href="login.jsp" class="back-button">Back to Home</a>
        </div>
    </div>
    

    <div class="footer">
        <div class="footer-content">
            <h3>UITM Election System</h3>
            <p>University Technology MARA</p>
            <p>Transparent Campus Elections</p>
            
            <div class="copyright">
                <p>&copy; 2024 UITM Election System. CSC584 Group Project.</p>
            </div>
        </div>
    </div>
</body>
</html>