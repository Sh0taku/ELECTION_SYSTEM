<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Contact - UITM Election</title>
    <style>
        body {
            font-family: Arial;
            background-color: #F9F7F7;
            margin: 0;
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
            font-size: 22px;
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
            padding: 5px 10px;
        }
        
        .nav a:hover {
            background-color: #3F72AF;
        }
        

        .main {
            max-width: 800px;
            margin: 30px auto;
            padding: 20px;
            background: white;
            border-radius: 5px;
        }
        
        h2 {
            color: #112D4E;
            text-align: center;
        }
        
        .contact-box {
            background-color: #DBE2EF;
            padding: 20px;
            margin: 20px 0;
            border-radius: 5px;
        }
        
        .contact-item {
            margin: 15px 0;
        }
        
        .contact-item strong {
            color: #112D4E;
        }
        
        .footer {
            background-color: #112D4E;
            color: white;
            padding: 20px;
            text-align: center;
            margin-top: 40px;
        }
        
        .back-btn {
            display: block;
            width: 150px;
            margin: 20px auto;
            padding: 10px;
            background-color: #3F72AF;
            color: white;
            text-align: center;
            text-decoration: none;
            border-radius: 5px;
        }
        
        .back-btn:hover {
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
    

    <div class="main">
        <h2>Contact Us</h2>
        
        <div class="contact-box">
            <div class="contact-item">
                <strong>Location:</strong>
                <p>Student Affairs Department, UITM Shah Alam</p>
            </div>
            
            <div class="contact-item">
                <strong>Email:</strong>
                <p>election@uitm.edu.my</p>
            </div>
            
            <div class="contact-item">
                <strong>Phone:</strong>
                <p>03-5544 2000</p>
            </div>
            
            <div class="contact-item">
                <strong>Office Hours:</strong>
                <p>Monday-Friday: 8:30 AM - 5:00 PM</p>
            </div>
        </div>
        
        <a href="login.jsp" class="back-btn">Back to Home</a>
    </div>
    

    <div class="footer">
        <p>UITM Election System</p>
        <p>&copy; 2024 CSC584 Group Project</p>
    </div>
</body>
</html>