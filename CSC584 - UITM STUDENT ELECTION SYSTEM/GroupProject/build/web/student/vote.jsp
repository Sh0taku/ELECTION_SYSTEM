<%--
    Document : vote  
    Created on : Jan 10, 2026, 12:36:28 PM  
    Author : Emir  
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.beans.Student" %>
<%@page import="com.election.beans.Election" %>
<%@page import="com.election.beans.Candidate" %>
<%@page import="com.election.dao.ElectionDAO" %>
<%@page import="com.election.dao.CandidateDAO" %>
<%@page import="com.election.dao.VoteDAO" %>
<%@page import="java.util.*" %>
<%
    Student student = (Student) session.getAttribute("student");
    if (student == null) {
        response.sendRedirect("../login.jsp");
        return;
    }
    
    // Initialize DAOs
    ElectionDAO electionDAO = new ElectionDAO();
    CandidateDAO candidateDAO = new CandidateDAO();
    VoteDAO voteDAO = new VoteDAO();
    
    // Get all elections
    List<Election> allElections = electionDAO.getAllElections();
    
    // Separate elections by status
    List<Election> ongoingElections = new ArrayList<Election>();
    List<Election> upcomingElections = new ArrayList<Election>();
    
    for (Election election : allElections) {
        if ("ONGOING".equals(election.getStatus())) {
            ongoingElections.add(election);
        } else if ("UPCOMING".equals(election.getStatus())) {
            upcomingElections.add(election);
        }
    }
    
    // Check if student has voted in any ongoing election
    boolean hasVotedInAnyOngoing = false;
    for (Election election : ongoingElections) {
        if (voteDAO.hasStudentVoted(student.getStudentId(), election.getElectionId())) {
            hasVotedInAnyOngoing = true;
            break;
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Vote - UITM Election</title>
    <style>
        /* Same base styles */
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

        /* Vote Content */
        .vote-content {
            padding: 30px;
            overflow-y: auto;
            flex: 1;
        }

        .election-section {
            background-color: white;
            border-radius: 10px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }

        .election-title {
            color: #112D4E;
            font-size: 22px;
            margin-bottom: 10px;
            padding-bottom: 10px;
            border-bottom: 2px solid #3F72AF;
        }

        .election-info {
            color: #3F72AF;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .candidate-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }

        .candidate-card {
            background-color: #F9F7F7;
            border-radius: 8px;
            padding: 20px;
            border: 1px solid #DBE2EF;
        }

        .candidate-name {
            color: #112D4E;
            font-size: 18px;
            margin-bottom: 10px;
        }

        .candidate-position {
            color: #3F72AF;
            font-size: 14px;
            margin-bottom: 15px;
        }

        .candidate-manifesto {
            color: #112D4E;
            font-size: 14px;
            margin-bottom: 15px;
            line-height: 1.5;
        }

        .candidate-buttons {
            display: flex;
            gap: 10px;
            margin-top: 15px;
        }

        .vote-btn {
            flex: 2;
            padding: 10px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
        }

        .vote-btn:hover:not(:disabled) {
            background-color: #112D4E;
        }

        .vote-btn:disabled {
            background-color: #cccccc;
            cursor: not-allowed;
        }

        .details-btn {
            flex: 1;
            padding: 10px;
            background-color: #DBE2EF;
            color: #112D4E;
            border: 1px solid #3F72AF;
            border-radius: 5px;
            cursor: pointer;
        }

        .details-btn:hover {
            background-color: #3F72AF;
            color: white;
        }

        /* Already Voted Message */
        .voted-message {
            background-color: #e1f7e1;
            border: 2px solid #2e7d32;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
            margin-top: 20px;
        }

        .voted-title {
            color: #2e7d32;
            font-weight: bold;
            margin-bottom: 10px;
        }

        .back-btn {
            display: inline-block;
            margin-top: 15px;
            padding: 10px 20px;
            background-color: #3F72AF;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }

        /* Confirmation Dialog (hidden by default) */
        .confirmation-dialog {
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

        .dialog-content {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            width: 400px;
            max-width: 90%;
        }

        .dialog-title {
            color: #112D4E;
            margin-bottom: 20px;
            font-size: 20px;
        }

        .dialog-buttons {
            display: flex;
            gap: 10px;
            margin-top: 25px;
        }

        .confirm-btn {
            flex: 1;
            padding: 12px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
        }

        .confirm-btn:hover {
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
    <!-- Left Navigation Panel -->
    <div class="nav-panel">
        <div class="student-info">
            <div class="student-name"><%= student.getName() %></div>
            <div class="student-id"><%= student.getStudentId() %></div>
        </div>
        
        <div class="nav-links">
            <a href="studentdashboard.jsp" class="nav-link">Dashboard</a>
            <a href="vote.jsp" class="nav-link active">Vote</a>
            <a href="voterguideline.jsp" class="nav-link">Voter Guideline</a>
        </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Top Header -->
        <div class="top-header">
            <div class="page-title">Voting Page</div>
            <button class="logout-btn" onclick="window.location.href='<%= request.getContextPath() %>/LogoutServlet'">
                Logout
            </button>
        </div>

        <!-- Vote Content -->
        <div class="vote-content">
            <% if (hasVotedInAnyOngoing) { %>
                <!-- Already Voted Message -->
                <div class="voted-message">
                    <div class="voted-title">✓ You Have Already Voted!</div>
                    <p style="color: #112D4E; line-height: 1.6;">
                        Thank you for participating in the election.<br>
                        You have already cast your vote for the current election.
                    </p>
                    <a href="thankyou.jsp" class="back-btn">View Thank You Page</a>
                    <a href="studentdashboard.jsp" class="back-btn" style="background-color: #DBE2EF; color: #112D4E; margin-left: 10px;">
                        Back to Dashboard
                    </a>
                </div>
            <% } else { %>
                
                <!-- ONGOING ELECTIONS SECTION -->
                <% if (!ongoingElections.isEmpty()) { 
                    for (Election election : ongoingElections) { 
                        List<Candidate> candidates = candidateDAO.getCandidatesByElection(election.getElectionId());
                        boolean hasVotedInThis = voteDAO.hasStudentVoted(student.getStudentId(), election.getElectionId());
                %>
                    <div class="election-section">
                        <h2 class="election-title"><%= election.getTitle() %></h2>
                        <div class="election-info">
                            <%= election.getDescription() %> • 
                            Status: <strong>ONGOING</strong> • 
                            Ends: <%= election.getEndDate() %>
                        </div>
                        
                        <% if (hasVotedInThis) { %>
                            <div style="background-color: #e1f7e1; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                                <strong style="color: #2e7d32;">✓ You have already voted in this election.</strong>
                            </div>
                        <% } %>
                        
                        <div class="candidate-grid">
                            <% for (Candidate candidate : candidates) { 
                                String candidateName = candidate.getCandidateName();
                                if (candidateName == null || candidateName.isEmpty()) {
                                    candidateName = "Candidate " + candidate.getCandidateId();
                                }
                            %>
                                <div class="candidate-card">
                                    <div class="candidate-name"><%= candidateName %></div>
                                    <div class="candidate-position"><%= candidate.getPosition() %></div>
                                    <div class="candidate-manifesto">
                                        <% if (candidate.getManifesto() != null && !candidate.getManifesto().isEmpty()) { 
                                            if (candidate.getManifesto().length() > 100) {
                                                out.print(candidate.getManifesto().substring(0, 100) + "...");
                                            } else {
                                                out.print(candidate.getManifesto());
                                            }
                                        } else { %>
                                            No manifesto provided.
                                        <% } %>
                                    </div>
                                    <div class="candidate-buttons">
                                        <% if (!hasVotedInThis) { %>
                                            <button class="vote-btn" onclick="showConfirmation('<%= candidateName %>', <%= candidate.getCandidateId() %>, <%= election.getElectionId() %>)">
                                                VOTE
                                            </button>
                                        <% } else { %>
                                            <button class="vote-btn" disabled>VOTED</button>
                                        <% } %>
                                        <button class="details-btn" onclick="showCandidateDetails(<%= candidate.getCandidateId() %>)">
                                            Details
                                        </button>
                                    </div>
                                </div>
                            <% } %>
                        </div>
                    </div>
                <% } 
                } else { %>
                    <div class="election-section">
                        <h2 class="election-title">Ongoing Elections</h2>
                        <p style="color: #3F72AF;">No ongoing elections at the moment.</p>
                    </div>
                <% } %>

                <!-- UPCOMING ELECTIONS SECTION -->
                <% if (!upcomingElections.isEmpty()) { 
                    for (Election election : upcomingElections) { 
                        List<Candidate> candidates = candidateDAO.getCandidatesByElection(election.getElectionId());
                %>
                    <div class="election-section">
                        <h2 class="election-title"><%= election.getTitle() %></h2>
                        <div class="election-info">
                            <%= election.getDescription() %> • 
                            Status: <strong>UPCOMING</strong> • 
                            Starts: <%= election.getStartDate() %>
                        </div>
                        
                        <div style="background-color: #fff3e0; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                            <strong style="color: #ef6c00;">⚠ This election has not started yet. Voting will open on <%= election.getStartDate() %>.</strong>
                        </div>
                        
                        <div class="candidate-grid">
                            <% for (Candidate candidate : candidates) { 
                                String candidateName = candidate.getCandidateName();
                                if (candidateName == null || candidateName.isEmpty()) {
                                    candidateName = "Candidate " + candidate.getCandidateId();
                                }
                            %>
                                <div class="candidate-card">
                                    <div class="candidate-name"><%= candidateName %></div>
                                    <div class="candidate-position"><%= candidate.getPosition() %></div>
                                    <div class="candidate-manifesto">
                                        <% if (candidate.getManifesto() != null && !candidate.getManifesto().isEmpty()) { 
                                            if (candidate.getManifesto().length() > 100) {
                                                out.print(candidate.getManifesto().substring(0, 100) + "...");
                                            } else {
                                                out.print(candidate.getManifesto());
                                            }
                                        } else { %>
                                            No manifesto provided.
                                        <% } %>
                                    </div>
                                    <div class="candidate-buttons">
                                        <button class="vote-btn" disabled>VOTE</button>
                                        <button class="details-btn" onclick="showCandidateDetails(<%= candidate.getCandidateId() %>)">
                                            Details
                                        </button>
                                    </div>
                                </div>
                            <% } %>
                        </div>
                    </div>
                <% } 
                } else { %>
                    <div class="election-section">
                        <h2 class="election-title">Upcoming Elections</h2>
                        <p style="color: #3F72AF;">No upcoming elections scheduled.</p>
                    </div>
                <% } %>
                
            <% } %>
        </div>
    </div>

    <!-- Confirmation Dialog -->
    <div id="confirmationDialog" class="confirmation-dialog">
        <div class="dialog-content">
            <div class="dialog-title">Confirm Your Vote</div>
            <p style="color: #112D4E; line-height: 1.6; margin-bottom: 20px;">
                Are you sure you want to vote for <span id="candidateName" style="font-weight: bold;"></span>?
            </p>
            <p style="color: #3F72AF; font-size: 14px; margin-bottom: 20px;">
                <strong>Important:</strong> You can only vote once per election. This action cannot be undone.
            </p>
            <form id="voteForm" method="POST" action="<%= request.getContextPath() %>/VoteServlet">
                <input type="hidden" id="candidateIdInput" name="candidateId">
                <input type="hidden" id="electionIdInput" name="electionId">
                <div class="dialog-buttons">
                    <button type="button" class="confirm-btn" onclick="submitVote()">YES, CONFIRM VOTE</button>
                    <button type="button" class="cancel-btn" onclick="hideConfirmation()">CANCEL</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Candidate Details Popup -->
    <div id="candidatePopup" class="confirmation-dialog">
        <div class="dialog-content" style="width: 500px;">
            <div class="dialog-title" id="popupCandidateName">Candidate Details</div>
            <div id="candidateDetails" style="color: #112D4E; line-height: 1.6;">
                Loading candidate details...
            </div>
            <div class="dialog-buttons" style="justify-content: center; margin-top: 25px;">
                <button class="cancel-btn" onclick="hideCandidatePopup()">CLOSE</button>
            </div>
        </div>
    </div>

    <script>
        let selectedCandidateId = null;
        let selectedElectionId = null;
        
        // Show confirmation dialog
        function showConfirmation(candidateName, candidateId, electionId) {
            document.getElementById('candidateName').textContent = candidateName;
            document.getElementById('candidateIdInput').value = candidateId;
            document.getElementById('electionIdInput').value = electionId;
            selectedCandidateId = candidateId;
            selectedElectionId = electionId;
            document.getElementById('confirmationDialog').style.display = 'flex';
        }
        
        // Hide confirmation dialog
        function hideConfirmation() {
            document.getElementById('confirmationDialog').style.display = 'none';
            selectedCandidateId = null;
            selectedElectionId = null;
        }
        
        // Submit vote via form
        function submitVote() {
            document.getElementById('voteForm').submit();
        }
        
        // Show candidate details (AJAX version)
        function showCandidateDetails(candidateId) {
            // Create a simple AJAX request
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    document.getElementById('candidateDetails').innerHTML = xhr.responseText;
                    document.getElementById('candidatePopup').style.display = 'flex';
                }
            };
            xhr.open("GET", "getCandidateDetails.jsp?candidateId=" + candidateId, true);
            xhr.send();
        }
        
        // Hide candidate popup
        function hideCandidatePopup() {
            document.getElementById('candidatePopup').style.display = 'none';
        }
        
        // Close dialogs when clicking outside
        window.onclick = function(event) {
            const confirmationDialog = document.getElementById('confirmationDialog');
            const candidatePopup = document.getElementById('candidatePopup');
            
            if (event.target === confirmationDialog) {
                hideConfirmation();
            }
            if (event.target === candidatePopup) {
                hideCandidatePopup();
            }
        }
    </script>
</body>
</html>