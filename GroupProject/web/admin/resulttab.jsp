<%-- 
    Document   : resulttab
    Created on : Jan 13, 2026, 10:31:08 AM
    Author     : Emir
--%>

<%--
Document: resulttab
Created on: Jan 13, 2026
Author: Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.beans.Admin" %>
<%@page import="com.election.beans.Election" %>
<%@page import="com.election.beans.Candidate" %>
<%@page import="java.util.*" %>
<%@page import="java.sql.*" %>
<%@page import="com.election.dao.DBConnection" %>
<%@page import="com.election.dao.ElectionDAO" %>
<%@page import="com.election.dao.CandidateDAO" %>
<%
// Check admin session
Admin admin = (Admin) session.getAttribute("admin");
if (admin == null) {
    response.sendRedirect("../login.jsp");
    return;
}

String message = "";
String messageType = "";

// Get election ID from parameter
int selectedElectionId = 0;
try {
    selectedElectionId = Integer.parseInt(request.getParameter("electionId"));
} catch (NumberFormatException e) {
    // Use default election or first available
}

// Fetch elections for dropdown
List<Election> allElections = new ArrayList<Election>();
Election selectedElection = null;
Connection conn = null;
PreparedStatement pstmt = null;
Statement stmt = null;
ResultSet rs = null;

try {
    conn = DBConnection.getConnection();
    
    // Get all elections
    String electionSql = "SELECT * FROM ELECTIONS ORDER BY END_DATE DESC, START_DATE DESC";
    pstmt = conn.prepareStatement(electionSql);
    rs = pstmt.executeQuery();
    
    while (rs.next()) {
        Election election = new Election(
            rs.getInt("ELECTION_ID"),
            rs.getString("TITLE"),
            rs.getString("DESCRIPTION"),
            rs.getDate("START_DATE"),
            rs.getDate("END_DATE"),
            rs.getString("STATUS")
        );
        allElections.add(election);
        
        // If no election selected, pick first one
        if (selectedElection == null && selectedElectionId == 0) {
            selectedElection = election;
            selectedElectionId = election.getElectionId();
        }
        
        // Find selected election
        if (selectedElectionId == election.getElectionId()) {
            selectedElection = election;
        }
    }
    
} catch (SQLException e) {
    message = "Error fetching elections: " + e.getMessage();
    messageType = "error";
    e.printStackTrace();
} finally {
    try { if (rs != null) rs.close(); } catch (Exception e) {}
    try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
}

// Fetch results for selected election
List<Candidate> candidates = new ArrayList<Candidate>();
int totalVotes = 0;
int totalVoters = 0;
List<String> topCandidates = new ArrayList<String>();

if (selectedElection != null) {
    try {
        // Get candidates for this election
        String candidateSql = "SELECT * FROM CANDIDATES WHERE ELECTION_ID = ? ORDER BY VOTE_COUNT DESC, POSITION";
        pstmt = conn.prepareStatement(candidateSql);
        pstmt.setInt(1, selectedElectionId);
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Candidate candidate = new Candidate(
                rs.getInt("CANDIDATE_ID"),
                rs.getString("STUDENT_ID"),
                rs.getInt("ELECTION_ID"),
                rs.getString("POSITION"),
                rs.getString("MANIFESTO"),
                rs.getInt("VOTE_COUNT"),
                rs.getString("CANDIDATE_NAME")
            );
            candidates.add(candidate);
            totalVotes += candidate.getVoteCount();
            
            // Track top 3 candidates
            if (topCandidates.size() < 3) {
                String name = candidate.getCandidateName();
                if (name == null || name.isEmpty()) {
                    name = "Candidate " + candidate.getCandidateId();
                }
                topCandidates.add(name + " (" + candidate.getVoteCount() + " votes)");
            }
        }
        
        // Get total voters who voted in this election
        String voterSql = "SELECT COUNT(DISTINCT STUDENT_ID) FROM VOTES WHERE ELECTION_ID = ?";
        pstmt = conn.prepareStatement(voterSql);
        pstmt.setInt(1, selectedElectionId);
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            totalVoters = rs.getInt(1);
        }
        
        // Get total eligible students
        String eligibleSql = "SELECT COUNT(*) FROM STUDENTS";
        pstmt = conn.prepareStatement(eligibleSql);
        rs = pstmt.executeQuery();
        int totalEligible = 0;
        if (rs.next()) {
            totalEligible = rs.getInt(1);
        }
        
        // Calculate voting percentage
        double votingPercentage = 0;
        if (totalEligible > 0) {
            votingPercentage = (totalVoters * 100.0) / totalEligible;
        }
        
        // Store in request for use in JSP
        request.setAttribute("totalVoters", totalVoters);
        request.setAttribute("totalEligible", totalEligible);
        request.setAttribute("votingPercentage", String.format("%.1f", votingPercentage));
        
    } catch (SQLException e) {
        message = "Error fetching results: " + e.getMessage();
        messageType = "error";
        e.printStackTrace();
    }
}

// Check if election is ended
boolean isElectionEnded = selectedElection != null && "ENDED".equals(selectedElection.getStatus());
boolean isElectionOngoing = selectedElection != null && "ONGOING".equals(selectedElection.getStatus());
boolean canShowFullResults = isElectionEnded || (admin != null); // Admins can see all results

// Get positions for grouping
Map<String, List<Candidate>> candidatesByPosition = new HashMap<String, List<Candidate>>();
for (Candidate candidate : candidates) {
    String position = candidate.getPosition();
    if (position == null || position.isEmpty()) {
        position = "General";
    }
    
    if (!candidatesByPosition.containsKey(position)) {
        candidatesByPosition.put(position, new ArrayList<Candidate>());
    }
    candidatesByPosition.get(position).add(candidate);
}

// Get winner for each position
Map<String, Candidate> winnersByPosition = new HashMap<String, Candidate>();
for (Map.Entry<String, List<Candidate>> entry : candidatesByPosition.entrySet()) {
    List<Candidate> posCandidates = entry.getValue();
    if (!posCandidates.isEmpty()) {
        // Sort by vote count descending
        Collections.sort(posCandidates, new Comparator<Candidate>() {
            public int compare(Candidate c1, Candidate c2) {
                return Integer.compare(c2.getVoteCount(), c1.getVoteCount());
            }
        });
        winnersByPosition.put(entry.getKey(), posCandidates.get(0));
    }
}

try { if (conn != null) conn.close(); } catch (Exception e) {}
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

        /* Results Content */
        .results-content {
            padding: 30px;
            overflow-y: auto;
            flex: 1;
        }

        /* Message Alert */
        .message-alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            text-align: center;
        }

        .success {
            background-color: #e1f7e1;
            color: #2e7d32;
            border: 1px solid #2e7d32;
        }

        .error {
            background-color: #ffebee;
            color: #c62828;
            border: 1px solid #c62828;
        }

        .warning {
            background-color: #fff3e0;
            color: #ef6c00;
            border: 1px solid #ef6c00;
        }

        /* Election Selector */
        .election-selector {
            background-color: white;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }

        .selector-title {
            color: #112D4E;
            margin-bottom: 15px;
            font-size: 18px;
        }

        .selector-form {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .selector-select {
            flex: 1;
            padding: 10px;
            border: 1px solid #DBE2EF;
            border-radius: 5px;
            font-size: 14px;
            max-width: 400px;
        }

        .selector-btn {
            padding: 10px 20px;
            background-color: #3F72AF;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
        }

        .selector-btn:hover {
            background-color: #112D4E;
        }

        /* Results Cards */
        .card {
            background-color: white;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }

        .card-title {
            color: #112D4E;
            margin-bottom: 15px;
            font-size: 18px;
            border-bottom: 2px solid #DBE2EF;
            padding-bottom: 10px;
        }

        /* Summary Stats */
        .summary-stats {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 15px;
            margin-bottom: 20px;
        }

        .summary-box {
            background-color: #F9F7F7;
            padding: 15px;
            text-align: center;
            border-radius: 8px;
            border: 1px solid #DBE2EF;
        }

        .summary-number {
            font-size: 24px;
            color: #3F72AF;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .summary-label {
            color: #112D4E;
            font-size: 12px;
        }

        /* Results Table */
        .results-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .results-table th {
            background-color: #DBE2EF;
            color: #112D4E;
            padding: 12px;
            text-align: left;
            font-weight: bold;
            border-bottom: 2px solid #3F72AF;
        }

        .results-table td {
            padding: 12px;
            border-bottom: 1px solid #F9F7F7;
            color: #112D4E;
        }

        .results-table tr:hover {
            background-color: #F9F7F7;
        }

        /* Winner badge */
        .winner-badge {
            background-color: #2e7d32;
            color: white;
            padding: 3px 8px;
            border-radius: 10px;
            font-size: 10px;
            font-weight: bold;
            margin-left: 5px;
        }

        /* Position header */
        .position-header {
            background-color: #3F72AF;
            color: white;
            padding: 10px 15px;
            border-radius: 5px;
            margin: 20px 0 10px 0;
            font-weight: bold;
        }

        /* Vote bar */
        .vote-bar-container {
            width: 100%;
            background-color: #f0f0f0;
            border-radius: 10px;
            height: 20px;
            margin: 5px 0;
            overflow: hidden;
        }

        .vote-bar {
            height: 100%;
            background-color: #3F72AF;
            border-radius: 10px;
            text-align: right;
            padding-right: 5px;
            font-size: 11px;
            line-height: 20px;
            color: white;
            min-width: 30px;
        }

        /* Chart container */
        .chart-container {
            display: flex;
            align-items: flex-end;
            height: 200px;
            margin: 20px 0;
            padding: 10px;
            border: 1px solid #DBE2EF;
            border-radius: 5px;
            background-color: #F9F7F7;
        }

        .chart-bar {
            flex: 1;
            margin: 0 5px;
            background-color: #3F72AF;
            position: relative;
            border-radius: 5px 5px 0 0;
        }

        .chart-label {
            position: absolute;
            bottom: -25px;
            left: 0;
            right: 0;
            text-align: center;
            font-size: 10px;
            color: #112D4E;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .chart-value {
            position: absolute;
            top: -20px;
            left: 0;
            right: 0;
            text-align: center;
            font-size: 11px;
            font-weight: bold;
            color: #112D4E;
        }

        /* Status badges */
        .status-badge {
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: bold;
            display: inline-block;
        }
        
        .status-ongoing {
            background-color: #e1f7e1;
            color: #2e7d32;
            border: 1px solid #2e7d32;
        }
        
        .status-upcoming {
            background-color: #fff3e0;
            color: #ef6c00;
            border: 1px solid #ef6c00;
        }
        
        .status-ended {
            background-color: #ffebee;
            color: #c62828;
            border: 1px solid #c62828;
        }

        /* No data */
        .no-data {
            text-align: center;
            padding: 30px;
            color: #3F72AF;
            font-style: italic;
        }

        /* Export buttons */
        .export-buttons {
            display: flex;
            gap: 10px;
            margin-top: 20px;
            justify-content: flex-end;
        }

        .export-btn {
            padding: 8px 15px;
            background-color: #2e7d32;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 12px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 5px;
        }

        .export-btn:hover {
            background-color: #1b5e20;
        }

        .export-btn.print {
            background-color: #3F72AF;
        }

        .export-btn.print:hover {
            background-color: #112D4E;
        }

        /* Winner announcement */
        .winner-announcement {
            background-color: #e1f7e1;
            border: 2px solid #2e7d32;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
            text-align: center;
        }

        .winner-title {
            color: #2e7d32;
            font-weight: bold;
            margin-bottom: 10px;
            font-size: 18px;
        }

        .winner-name {
            color: #112D4E;
            font-size: 22px;
            margin: 10px 0;
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

        <!-- Results Content -->
        <div class="results-content">
            <% if (!message.isEmpty()) { %>
            <div class="message-alert <%= messageType %>">
                <%= message %>
            </div>
            <% } %>

            <!-- Election Selector -->
            <div class="election-selector">
                <div class="selector-title">Select Election to View Results</div>
                <form class="selector-form" method="GET" action="resulttab.jsp">
                    <select name="electionId" class="selector-select" onchange="this.form.submit()">
                        <option value="">-- Select Election --</option>
                        <% for (Election election : allElections) { 
                            boolean isSelected = election.getElectionId() == selectedElectionId;
                        %>
                        <option value="<%= election.getElectionId() %>" <%= isSelected ? "selected" : "" %>>
                            <%= election.getTitle() %> 
                            (<%= election.getStatus() %> 
                            <% if (election.getEndDate() != null) { %>
                            - <%= election.getEndDate() %>
                            <% } %>)
                        </option>
                        <% } %>
                    </select>
                    <button type="submit" class="selector-btn">View Results</button>
                </form>
            </div>

            <% if (selectedElection != null) { %>
            <!-- Election Info -->
            <div class="card">
                <h2 class="card-title">
                    <%= selectedElection.getTitle() %>
                    <span class="status-badge 
                        <% if ("ONGOING".equals(selectedElection.getStatus())) { %>status-ongoing<% } 
                        else if ("UPCOMING".equals(selectedElection.getStatus())) { %>status-upcoming<% } 
                        else if ("ENDED".equals(selectedElection.getStatus())) { %>status-ended<% } %>">
                        <%= selectedElection.getStatus() %>
                    </span>
                </h2>
                
                <p style="color: #3F72AF; margin-bottom: 15px;"><%= selectedElection.getDescription() %></p>
                
                <% if (selectedElection.getStartDate() != null && selectedElection.getEndDate() != null) { %>
                <p style="color: #112D4E; font-size: 14px;">
                    Election Period: <strong><%= selectedElection.getStartDate() %></strong> to 
                    <strong><%= selectedElection.getEndDate() %></strong>
                </p>
                <% } %>
            </div>

            <!-- Summary Statistics -->
            <div class="card">
                <h2 class="card-title">Election Summary</h2>
                <div class="summary-stats">
                    <div class="summary-box">
                        <div class="summary-number"><%= candidates.size() %></div>
                        <div class="summary-label">Candidates</div>
                    </div>
                    <div class="summary-box">
                        <div class="summary-number"><%= totalVotes %></div>
                        <div class="summary-label">Total Votes</div>
                    </div>
                    <div class="summary-box">
                        <div class="summary-number"><%= totalVoters %></div>
                        <div class="summary-label">Voters Participated</div>
                    </div>
                    <div class="summary-box">
                        <div class="summary-number"><%= request.getAttribute("votingPercentage") %>%</div>
                        <div class="summary-label">Voter Turnout</div>
                    </div>
                </div>
            </div>

            <% if (!isElectionEnded) { %>
            <!-- Warning for ongoing/upcoming elections -->
            <div class="message-alert warning">
                <strong>Note:</strong> This election is <%= selectedElection.getStatus() %>. 
                <% if (isElectionOngoing) { %>
                Results shown are live but may change until the election ends.
                <% } else { %>
                Voting has not started yet. No results available.
                <% } %>
                Final results will be available after the election ends.
            </div>
            <% } %>

            <% if (!candidates.isEmpty()) { %>
            <!-- Winners Announcement (for ended elections) -->
            <% if (isElectionEnded && !winnersByPosition.isEmpty()) { %>
            <div class="winner-announcement">
                <div class="winner-title">🏆 ELECTION WINNERS 🏆</div>
                <% for (Map.Entry<String, Candidate> entry : winnersByPosition.entrySet()) { 
                    Candidate winner = entry.getValue();
                    String winnerName = winner.getCandidateName();
                    if (winnerName == null || winnerName.isEmpty()) {
                        winnerName = "Candidate " + winner.getCandidateId();
                    }
                %>
                <div style="margin: 10px 0;">
                    <div style="color: #3F72AF; font-weight: bold;"><%= entry.getKey() %></div>
                    <div class="winner-name"><%= winnerName %></div>
                    <div style="color: #2e7d32;">Won with <%= winner.getVoteCount() %> votes</div>
                </div>
                <% } %>
            </div>
            <% } %>

            <!-- Detailed Results by Position -->
            <% for (Map.Entry<String, List<Candidate>> entry : candidatesByPosition.entrySet()) { 
                List<Candidate> posCandidates = entry.getValue();
                // Sort by vote count descending
                Collections.sort(posCandidates, new Comparator<Candidate>() {
                    public int compare(Candidate c1, Candidate c2) {
                        return Integer.compare(c2.getVoteCount(), c1.getVoteCount());
                    }
                });
                
                // Find max votes for percentage calculation
                int maxVotes = 0;
                for (Candidate c : posCandidates) {
                    if (c.getVoteCount() > maxVotes) {
                        maxVotes = c.getVoteCount();
                    }
                }
            %>
            <div class="card">
                <div class="position-header"><%= entry.getKey() %></div>
                
                <table class="results-table">
                    <thead>
                        <tr>
                            <th width="5%">Rank</th>
                            <th width="35%">Candidate</th>
                            <th width="15%">Student ID</th>
                            <th width="10%">Votes</th>
                            <th width="35%">Vote Percentage</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        int rank = 1;
                        for (Candidate candidate : posCandidates) { 
                            String candidateName = candidate.getCandidateName();
                            if (candidateName == null || candidateName.isEmpty()) {
                                candidateName = "Candidate " + candidate.getCandidateId();
                            }
                            
                            // Calculate percentage
                            double percentage = 0;
                            if (totalVotes > 0) {
                                percentage = (candidate.getVoteCount() * 100.0) / totalVotes;
                            }
                            
                            // Calculate bar width
                            int barWidth = 0;
                            if (maxVotes > 0) {
                                barWidth = (int) ((candidate.getVoteCount() * 100.0) / maxVotes);
                            }
                            
                            boolean isWinner = isElectionEnded && rank == 1;
                        %>
                        <tr>
                            <td>
                                <% if (isWinner) { %>
                                <strong style="color: #2e7d32;">#<%= rank %></strong>
                                <span class="winner-badge">WINNER</span>
                                <% } else { %>
                                #<%= rank %>
                                <% } %>
                            </td>
                            <td><strong><%= candidateName %></strong></td>
                            <td><%= candidate.getStudentId() %></td>
                            <td><strong style="color: #3F72AF;"><%= candidate.getVoteCount() %></strong></td>
                            <td>
                                <div style="display: flex; align-items: center; gap: 10px;">
                                    <div style="width: 100px;">
                                        <div class="vote-bar-container">
                                            <div class="vote-bar" style="width: <%= barWidth %>%;">
                                                <%= candidate.getVoteCount() %>
                                            </div>
                                        </div>
                                    </div>
                                    <div style="font-size: 12px; color: #112D4E;">
                                        <%= String.format("%.1f", percentage) %>%
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <% 
                            rank++;
                        } 
                        %>
                    </tbody>
                </table>
            </div>
            <% } %>

            <!-- Simple Bar Chart -->
            <% if (candidates.size() <= 10) { %>
            <div class="card">
                <h2 class="card-title">Vote Distribution</h2>
                <div class="chart-container">
                    <% 
                    // Sort candidates by vote count for chart
                    List<Candidate> chartCandidates = new ArrayList<Candidate>(candidates);
                    Collections.sort(chartCandidates, new Comparator<Candidate>() {
                        public int compare(Candidate c1, Candidate c2) {
                            return Integer.compare(c2.getVoteCount(), c1.getVoteCount());
                        }
                    });
                    
                    // Limit to top 10 for chart
                    int chartLimit = Math.min(10, chartCandidates.size());
                    int maxChartVotes = 0;
                    for (int i = 0; i < chartLimit; i++) {
                        if (chartCandidates.get(i).getVoteCount() > maxChartVotes) {
                            maxChartVotes = chartCandidates.get(i).getVoteCount();
                        }
                    }
                    
                    for (int i = 0; i < chartLimit; i++) {
                        Candidate c = chartCandidates.get(i);
                        String name = c.getCandidateName();
                        if (name == null || name.isEmpty()) {
                            name = "C" + c.getCandidateId();
                        } else if (name.length() > 15) {
                            name = name.substring(0, 12) + "...";
                        }
                        
                        int barHeight = 0;
                        if (maxChartVotes > 0) {
                            barHeight = (int) ((c.getVoteCount() * 180.0) / maxChartVotes);
                        }
                    %>
                    <div class="chart-bar" style="height: <%= barHeight %>px;">
                        <div class="chart-value"><%= c.getVoteCount() %></div>
                        <div class="chart-label"><%= name %></div>
                    </div>
                    <% } %>
                </div>
            </div>
            <% } %>

            <!-- Export Options -->
            <div class="export-buttons">
                <button class="export-btn print" onclick="window.print()">
                    📄 Print Results
                </button>
                <a href="export_results.jsp?electionId=<%= selectedElectionId %>" class="export-btn">
                    📥 Export as PDF
                </a>
            </div>

            <% } else { %>
            <!-- No Candidates -->
            <div class="card">
                <div class="no-data">
                    No candidates registered for this election yet.
                </div>
            </div>
            <% } %>

            <% } else if (allElections.isEmpty()) { %>
            <!-- No Elections -->
            <div class="card">
                <div class="no-data">
                    No elections found in the system. Create elections first to view results.
                </div>
            </div>
            <% } else { %>
            <!-- No Election Selected -->
            <div class="card">
                <div class="no-data">
                    Please select an election from the dropdown above to view results.
                </div>
            </div>
            <% } %>
        </div>
    </div>

    <script>
        // Auto-refresh for ongoing elections
        <% if (selectedElection != null && "ONGOING".equals(selectedElection.getStatus())) { %>
        setTimeout(function() {
            // Refresh every 30 seconds for ongoing elections
            window.location.reload();
        }, 30000);
        <% } %>

        // Print function
        function printResults() {
            window.print();
        }

        // Confirm before exporting
        document.querySelectorAll('.export-btn').forEach(btn => {
            if (!btn.classList.contains('print')) {
                btn.addEventListener('click', function(e) {
                    if (!confirm('Export election results?')) {
                        e.preventDefault();
                    }
                });
            }
        });
    </script>
</body>
</html>
