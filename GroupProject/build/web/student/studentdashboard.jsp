<%--
    Document : studentdashboard
    Created on : DEC, 2025, 9:12:16 AM
    Author : Emir
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.beans.Student" %>
<%@page import="com.election.beans.Election" %>
<%@page import="com.election.beans.Candidate" %>
<%@page import="com.election.beans.Vote" %>
<%@page import="com.election.dao.ElectionDAO" %>
<%@page import="com.election.dao.CandidateDAO" %>
<%@page import="com.election.dao.VoteDAO" %>
<%@page import="com.election.dao.StudentDAO" %>
<%@page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat" %>
<%
    // Git student
    Student student = (Student) session.getAttribute("student");
    if (student == null) {
        response.sendRedirect("../login.jsp");
        return;
    }

    // Git DAO
    ElectionDAO electionDAO = new ElectionDAO();
    CandidateDAO candidateDAO = new CandidateDAO();
    VoteDAO voteDAO = new VoteDAO();
    StudentDAO studentDAO = new StudentDAO();

    // Git election db
    List<Election> allElections = new ArrayList<Election>();
    try {
        allElections = electionDAO.getAllElections();
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Find ongoing election
    Election ongoingElection = null;
    for (Election e : allElections) {
        if ("ONGOING".equals(e.getStatus())) {
            ongoingElection = e;
            break;
        }
    }

    // Get candidates for ongoing election
    List<Candidate> candidates = new ArrayList<Candidate>();
    int totalVotesElection = 0;
    int candidateCount = 0;
    
    if (ongoingElection != null) {
        try {
            candidates = candidateDAO.getCandidatesByElection(ongoingElection.getElectionId());
            candidateCount = candidates.size();
            
            // Calculate total votes for this election
            for (Candidate c : candidates) {
                totalVotesElection += c.getVoteCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    int totalStudents = 0;
    try {
        totalStudents = studentDAO.getTotalStudents();
    } catch (Exception e) {
        e.printStackTrace();
        totalStudents = 0;
    }

    
    boolean hasVotedInCurrent = false;
    List<Vote> votingHistory = new ArrayList<Vote>();
    if (ongoingElection != null) {
        try {
            hasVotedInCurrent = voteDAO.hasStudentVoted(student.getStudentId(), 
                                                        ongoingElection.getElectionId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
%>

<!DOCTYPE html>

<head>
    <title>Student Dashboard - UITM Election</title>
    <style>
        /* Reset and Base */
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

        .student-faculty {
            color: #DBE2EF;
            font-size: 13px;
            margin-top: 5px;
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

        /* Dashboard Content */
        .dashboard-content {
            padding: 30px;
            overflow-y: auto;
            flex: 1;
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

        /* Containers */
        .container {
            background-color: white;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }

        .container-title {
            color: #112D4E;
            margin-bottom: 15px;
            font-size: 18px;
            border-bottom: 2px solid #DBE2EF;
            padding-bottom: 10px;
        }

        /* Interactive Container */
        .interactive-container {
            background-color: #DBE2EF;
            text-align: center;
            padding: 30px;
        }

        .vote-now-btn {
            padding: 15px 40px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 18px;
            cursor: pointer;
            font-weight: bold;
        }

        .vote-now-btn:hover {
            background-color: #112D4E;
        }

        .already-voted-btn {
            padding: 15px 40px;
            background-color: #2e7d32;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 18px;
            font-weight: bold;
            cursor: default;
        }

        /* Election Status */
        .election-item {
            display: flex;
            justify-content: space-between;
            padding: 12px 0;
            border-bottom: 1px solid #F9F7F7;
        }

        .election-name {
            color: #112D4E;
            font-weight: bold;
        }

        .election-dates {
            color: #3F72AF;
            font-size: 13px;
            margin-top: 3px;
        }

        .election-status {
            padding: 4px 12px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
        }

        .status-ongoing {
            background-color: #e1f7e1;
            color: #2e7d32;
        }

        .status-upcoming {
            background-color: #fff3e0;
            color: #ef6c00;
        }

        .status-ended {
            background-color: #ffebee;
            color: #c62828;
        }

        /* Results Container */
        .candidate-result {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #F9F7F7;
        }

        .candidate-name {
            color: #112D4E;
        }

        .vote-count {
            background-color: #3F72AF;
            color: white;
            padding: 5px 15px;
            border-radius: 20px;
            font-weight: bold;
            min-width: 60px;
            text-align: center;
        }

        /* Stats Container */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
        }

        .stat-box {
            text-align: center;
            padding: 20px;
            background-color: #F9F7F7;
            border-radius: 8px;
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

        /* Voting History */
        .history-item {
            padding: 12px 0;
            border-bottom: 1px solid #F9F7F7;
            color: #112D4E;
        }

        .no-history {
            color: #3F72AF;
            text-align: center;
            padding: 20px;
        }

        .error-message {
            background-color: #ffebee;
            color: #c62828;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            text-align: center;
        }
    </style>
</head>
<body>
    <!-- Left Nav -->
    <div class="nav-panel">
        <div class="student-info">
            <div class="student-name"><%= student.getName() %></div>
            <div class="student-id"><%= student.getStudentId() %></div>
            <% if (student.getFaculty() != null && !student.getFaculty().isEmpty()) { %>
                <div class="student-faculty"><%= student.getFaculty() %></div>
            <% } %>
        </div>
        
        <div class="nav-links">
            <a href="studentdashboard.jsp" class="nav-link active">Dashboard</a>
            <a href="vote.jsp" class="nav-link">Vote</a>
            <a href="voterguideline.jsp" class="nav-link">Voter Guideline</a>
        </div>
    </div>

        
    <!-- Main -->
    <div class="main-content">
        <div class="top-header">
            <div class="page-title">Student Dashboard</div>
            <button class="logout-btn" onclick="window.location.href='<%= request.getContextPath() %>/LogoutServlet'">
                Logout
            </button>
        </div>


        <div class="dashboard-content">
            <div class="welcome-section">
                <h1 class="welcome-title">Hello <%= student.getName() %>!</h1>
                <p class="welcome-subtitle">Welcome to UITM Online Election System</p>
            </div>

                
            <div class="container interactive-container">
                <h2 class="container-title">Ongoing Election</h2>
                <% if (ongoingElection != null) { %>
                    <p style="margin-bottom: 20px; color: #112D4E; font-size: 18px;">
                        <%= ongoingElection.getTitle() %>
                    </p>
                    <p style="color: #3F72AF; margin-bottom: 20px;">
                        <%= ongoingElection.getDescription() %>
                    </p>
                    
                    <% if (hasVotedInCurrent) { %>
                        <button class="already-voted-btn" disabled>
                            ✓ YOU HAVE VOTED
                        </button>
                        <p style="margin-top: 15px; color: #2e7d32; font-size: 14px;">
                            Thank you for participating in this election!
                        </p>
                    <% } else { %>
                        <button class="vote-now-btn" onclick="window.location.href='vote.jsp'">
                            VOTE NOW
                        </button>
                        <p style="margin-top: 15px; color: #112D4E; font-size: 14px;">
                            Election ends: <%= dateFormat.format(ongoingElection.getEndDate()) %>
                        </p>
                    <% } %>
                <% } else { %>
                    <p style="margin-bottom: 20px; color: #112D4E;">No ongoing elections at the moment.</p>
                    <p style="color: #3F72AF;">Check back later for upcoming elections.</p>
                <% } %>
            </div>

            <!-- Current Election Activities -->
            <div class="container">
                <h2 class="container-title">Election Activities</h2>
                <% 
                if (allElections != null && !allElections.isEmpty()) { 
                    for (Election election : allElections) {
                        String statusClass = "";
                        String statusText = "";
                        String electionStatus = election.getStatus();
                        
                        
                        if ("ONGOING".equals(electionStatus)) {
                            statusClass = "status-ongoing";
                            statusText = "Ongoing";
                        } else if ("UPCOMING".equals(electionStatus)) {
                            statusClass = "status-upcoming";
                            statusText = "Upcoming";
                        } else if ("ENDED".equals(electionStatus)) {
                            statusClass = "status-ended";
                            statusText = "Ended";
                        } else {
                            statusClass = "";
                            statusText = electionStatus;
                        }
                %>
                    <div class="election-item">
                        <div>
                            <div class="election-name"><%= election.getTitle() %></div>
                            <% if (election.getStartDate() != null && election.getEndDate() != null) { %>
                            <div class="election-dates">
                                <%= dateFormat.format(election.getStartDate()) %> - <%= dateFormat.format(election.getEndDate()) %>
                            </div>
                            <% } else { %>
                            <div class="election-dates">Dates not set</div>
                            <% } %>
                        </div>
                        <span class="election-status <%= statusClass %>">
                            <%= statusText %>
                        </span>
                    </div>
                <%  
                    }
                } else { 
                %>
                    <div class="no-history">
                        No election data available
                    </div>
                <% } %>
            </div>

            <!-- Live Results -->
            <% if (ongoingElection != null && !candidates.isEmpty()) { 
                             List<Candidate> sortedCandidates = new ArrayList<Candidate>(candidates);
                Collections.sort(sortedCandidates, new Comparator<Candidate>() {
                    public int compare(Candidate c1, Candidate c2) {
                        return Integer.compare(c2.getVoteCount(), c1.getVoteCount());
                    }
                });
            %>
            <div class="container">
                <h2 class="container-title">Live Results - <%= ongoingElection.getTitle() %></h2>
                <% for (Candidate sortedCandidate : sortedCandidates) { %>
                    <div class="candidate-result">
                        <span class="candidate-name">
                            <% 
                                String candidateName = sortedCandidate.getCandidateName();
                                if (candidateName != null && !candidateName.isEmpty()) {
                                    out.print(candidateName);
                                } else {
                                    out.print("Candidate " + sortedCandidate.getCandidateId());
                                }
                            %>
                        </span>
                        <span class="vote-count"><%= sortedCandidate.getVoteCount() %> votes</span>
                    </div>
                <% } %>
                <div style="margin-top: 15px; color: #3F72AF; font-size: 14px; text-align: center;">
                    Total Votes Cast: <%= totalVotesElection %>
                </div>
            </div>
            <% } %>

            <!-- Voting Process Stats -->
            <div class="container">
                <h2 class="container-title">Voting Statistics</h2>
                <div class="stats-grid">
                    <div class="stat-box">
                        <div class="stat-number"><%= totalStudents %></div>
                        <div class="stat-label">Registered Students</div>
                    </div>
                    <div class="stat-box">
                        <div class="stat-number"><%= candidateCount %></div>
                        <div class="stat-label">Candidates</div>
                    </div>
                    <div class="stat-box">
                        <div class="stat-number"><%= totalVotesElection %></div>
                        <div class="stat-label">Total Votes</div>
                    </div>
                </div>
                <% if (ongoingElection != null) { %>
                    <div style="margin-top: 15px; color: #112D4E; font-size: 14px; text-align: center;">
                        Election: <strong><%= ongoingElection.getTitle() %></strong>
                    </div>
                <% } %>
            </div>

            <!-- Voting History -->
            <div class="container">
                <h2 class="container-title">Your Voting Status</h2>
                <div class="history-item">
                    <strong>Current Status:</strong>
                    <% if (hasVotedInCurrent && ongoingElection != null) { %>
                        <span style="color: #2e7d32; font-weight: bold;">✓ Voted in <%= ongoingElection.getTitle() %></span>
                    <% } else if (ongoingElection != null) { %>
                        <span style="color: #ef6c00; font-weight: bold;">⏳ Not voted yet in <%= ongoingElection.getTitle() %></span>
                    <% } else { %>
                        <span style="color: #3F72AF;">No active election</span>
                    <% } %>
                </div>
                <div class="history-item">
                    <strong>Account Created:</strong> Active student account
                </div>
                <div class="history-item">
                    <strong>Last Login:</strong> <%= new java.util.Date() %>
                </div>
            </div>
        </div>
    </div>

    <!-- Debug info (remove in production) -->
    <div style="display: none;">
        <!-- Debug info -->
        Student ID: <%= student.getStudentId() %><br>
        Faculty: <%= student.getFaculty() %><br>
        Has Voted (DB): <%= student.isHasVoted() %><br>
        Elections in DB: <%= allElections.size() %><br>
        Ongoing Election: <%= ongoingElection != null ? ongoingElection.getTitle() : "None" %><br>
        Candidates: <%= candidateCount %><br>
        Has Voted in Current: <%= hasVotedInCurrent %>
    </div>
</body>
