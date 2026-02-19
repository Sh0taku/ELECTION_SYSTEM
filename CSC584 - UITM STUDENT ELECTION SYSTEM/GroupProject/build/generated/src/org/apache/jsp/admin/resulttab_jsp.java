package org.apache.jsp.admin;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.election.beans.Admin;
import com.election.beans.Election;
import com.election.beans.Candidate;
import java.util.*;
import java.sql.*;
import com.election.dao.DBConnection;
import com.election.dao.ElectionDAO;
import com.election.dao.CandidateDAO;

public final class resulttab_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write('\n');
      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");

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

      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("<head>\n");
      out.write("    <title>Election Results - UITM Election System</title>\n");
      out.write("    <style>\n");
      out.write("        /* Same CSS structure as other tabs */\n");
      out.write("        * {\n");
      out.write("            margin: 0;\n");
      out.write("            padding: 0;\n");
      out.write("            box-sizing: border-box;\n");
      out.write("            font-family: Arial, sans-serif;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        body {\n");
      out.write("            background-color: #F9F7F7;\n");
      out.write("            display: flex;\n");
      out.write("            height: 100vh;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Left Navigation Panel */\n");
      out.write("        .nav-panel {\n");
      out.write("            width: 250px;\n");
      out.write("            background-color: #112D4E;\n");
      out.write("            color: white;\n");
      out.write("            padding: 20px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .admin-info {\n");
      out.write("            padding-bottom: 20px;\n");
      out.write("            border-bottom: 1px solid #3F72AF;\n");
      out.write("            margin-bottom: 20px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .admin-name {\n");
      out.write("            font-size: 18px;\n");
      out.write("            font-weight: bold;\n");
      out.write("            margin-bottom: 5px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .admin-id {\n");
      out.write("            color: #DBE2EF;\n");
      out.write("            font-size: 14px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .nav-links {\n");
      out.write("            display: flex;\n");
      out.write("            flex-direction: column;\n");
      out.write("            gap: 10px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .nav-link {\n");
      out.write("            color: #DBE2EF;\n");
      out.write("            text-decoration: none;\n");
      out.write("            padding: 12px 15px;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            transition: all 0.3s;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .nav-link:hover, .nav-link.active {\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("            color: white;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Main Content Area */\n");
      out.write("        .main-content {\n");
      out.write("            flex: 1;\n");
      out.write("            display: flex;\n");
      out.write("            flex-direction: column;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Top Header */\n");
      out.write("        .top-header {\n");
      out.write("            background-color: white;\n");
      out.write("            padding: 15px 30px;\n");
      out.write("            display: flex;\n");
      out.write("            justify-content: space-between;\n");
      out.write("            align-items: center;\n");
      out.write("            box-shadow: 0 2px 5px rgba(0,0,0,0.1);\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .page-title {\n");
      out.write("            color: #112D4E;\n");
      out.write("            font-size: 20px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .logout-btn {\n");
      out.write("            padding: 8px 20px;\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            font-size: 14px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .logout-btn:hover {\n");
      out.write("            background-color: #112D4E;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Results Content */\n");
      out.write("        .results-content {\n");
      out.write("            padding: 30px;\n");
      out.write("            overflow-y: auto;\n");
      out.write("            flex: 1;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Message Alert */\n");
      out.write("        .message-alert {\n");
      out.write("            padding: 15px;\n");
      out.write("            margin-bottom: 20px;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            text-align: center;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .success {\n");
      out.write("            background-color: #e1f7e1;\n");
      out.write("            color: #2e7d32;\n");
      out.write("            border: 1px solid #2e7d32;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .error {\n");
      out.write("            background-color: #ffebee;\n");
      out.write("            color: #c62828;\n");
      out.write("            border: 1px solid #c62828;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .warning {\n");
      out.write("            background-color: #fff3e0;\n");
      out.write("            color: #ef6c00;\n");
      out.write("            border: 1px solid #ef6c00;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Election Selector */\n");
      out.write("        .election-selector {\n");
      out.write("            background-color: white;\n");
      out.write("            padding: 20px;\n");
      out.write("            border-radius: 10px;\n");
      out.write("            margin-bottom: 20px;\n");
      out.write("            box-shadow: 0 2px 5px rgba(0,0,0,0.1);\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .selector-title {\n");
      out.write("            color: #112D4E;\n");
      out.write("            margin-bottom: 15px;\n");
      out.write("            font-size: 18px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .selector-form {\n");
      out.write("            display: flex;\n");
      out.write("            gap: 10px;\n");
      out.write("            align-items: center;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .selector-select {\n");
      out.write("            flex: 1;\n");
      out.write("            padding: 10px;\n");
      out.write("            border: 1px solid #DBE2EF;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            font-size: 14px;\n");
      out.write("            max-width: 400px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .selector-btn {\n");
      out.write("            padding: 10px 20px;\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            font-weight: bold;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .selector-btn:hover {\n");
      out.write("            background-color: #112D4E;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Results Cards */\n");
      out.write("        .card {\n");
      out.write("            background-color: white;\n");
      out.write("            border-radius: 10px;\n");
      out.write("            padding: 20px;\n");
      out.write("            margin-bottom: 20px;\n");
      out.write("            box-shadow: 0 2px 5px rgba(0,0,0,0.1);\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .card-title {\n");
      out.write("            color: #112D4E;\n");
      out.write("            margin-bottom: 15px;\n");
      out.write("            font-size: 18px;\n");
      out.write("            border-bottom: 2px solid #DBE2EF;\n");
      out.write("            padding-bottom: 10px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Summary Stats */\n");
      out.write("        .summary-stats {\n");
      out.write("            display: grid;\n");
      out.write("            grid-template-columns: repeat(4, 1fr);\n");
      out.write("            gap: 15px;\n");
      out.write("            margin-bottom: 20px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .summary-box {\n");
      out.write("            background-color: #F9F7F7;\n");
      out.write("            padding: 15px;\n");
      out.write("            text-align: center;\n");
      out.write("            border-radius: 8px;\n");
      out.write("            border: 1px solid #DBE2EF;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .summary-number {\n");
      out.write("            font-size: 24px;\n");
      out.write("            color: #3F72AF;\n");
      out.write("            font-weight: bold;\n");
      out.write("            margin-bottom: 5px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .summary-label {\n");
      out.write("            color: #112D4E;\n");
      out.write("            font-size: 12px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Results Table */\n");
      out.write("        .results-table {\n");
      out.write("            width: 100%;\n");
      out.write("            border-collapse: collapse;\n");
      out.write("            margin-top: 10px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .results-table th {\n");
      out.write("            background-color: #DBE2EF;\n");
      out.write("            color: #112D4E;\n");
      out.write("            padding: 12px;\n");
      out.write("            text-align: left;\n");
      out.write("            font-weight: bold;\n");
      out.write("            border-bottom: 2px solid #3F72AF;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .results-table td {\n");
      out.write("            padding: 12px;\n");
      out.write("            border-bottom: 1px solid #F9F7F7;\n");
      out.write("            color: #112D4E;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .results-table tr:hover {\n");
      out.write("            background-color: #F9F7F7;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Winner badge */\n");
      out.write("        .winner-badge {\n");
      out.write("            background-color: #2e7d32;\n");
      out.write("            color: white;\n");
      out.write("            padding: 3px 8px;\n");
      out.write("            border-radius: 10px;\n");
      out.write("            font-size: 10px;\n");
      out.write("            font-weight: bold;\n");
      out.write("            margin-left: 5px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Position header */\n");
      out.write("        .position-header {\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("            color: white;\n");
      out.write("            padding: 10px 15px;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            margin: 20px 0 10px 0;\n");
      out.write("            font-weight: bold;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Vote bar */\n");
      out.write("        .vote-bar-container {\n");
      out.write("            width: 100%;\n");
      out.write("            background-color: #f0f0f0;\n");
      out.write("            border-radius: 10px;\n");
      out.write("            height: 20px;\n");
      out.write("            margin: 5px 0;\n");
      out.write("            overflow: hidden;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .vote-bar {\n");
      out.write("            height: 100%;\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("            border-radius: 10px;\n");
      out.write("            text-align: right;\n");
      out.write("            padding-right: 5px;\n");
      out.write("            font-size: 11px;\n");
      out.write("            line-height: 20px;\n");
      out.write("            color: white;\n");
      out.write("            min-width: 30px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Chart container */\n");
      out.write("        .chart-container {\n");
      out.write("            display: flex;\n");
      out.write("            align-items: flex-end;\n");
      out.write("            height: 200px;\n");
      out.write("            margin: 20px 0;\n");
      out.write("            padding: 10px;\n");
      out.write("            border: 1px solid #DBE2EF;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            background-color: #F9F7F7;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .chart-bar {\n");
      out.write("            flex: 1;\n");
      out.write("            margin: 0 5px;\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("            position: relative;\n");
      out.write("            border-radius: 5px 5px 0 0;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .chart-label {\n");
      out.write("            position: absolute;\n");
      out.write("            bottom: -25px;\n");
      out.write("            left: 0;\n");
      out.write("            right: 0;\n");
      out.write("            text-align: center;\n");
      out.write("            font-size: 10px;\n");
      out.write("            color: #112D4E;\n");
      out.write("            overflow: hidden;\n");
      out.write("            text-overflow: ellipsis;\n");
      out.write("            white-space: nowrap;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .chart-value {\n");
      out.write("            position: absolute;\n");
      out.write("            top: -20px;\n");
      out.write("            left: 0;\n");
      out.write("            right: 0;\n");
      out.write("            text-align: center;\n");
      out.write("            font-size: 11px;\n");
      out.write("            font-weight: bold;\n");
      out.write("            color: #112D4E;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Status badges */\n");
      out.write("        .status-badge {\n");
      out.write("            padding: 4px 8px;\n");
      out.write("            border-radius: 12px;\n");
      out.write("            font-size: 11px;\n");
      out.write("            font-weight: bold;\n");
      out.write("            display: inline-block;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        .status-ongoing {\n");
      out.write("            background-color: #e1f7e1;\n");
      out.write("            color: #2e7d32;\n");
      out.write("            border: 1px solid #2e7d32;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        .status-upcoming {\n");
      out.write("            background-color: #fff3e0;\n");
      out.write("            color: #ef6c00;\n");
      out.write("            border: 1px solid #ef6c00;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        .status-ended {\n");
      out.write("            background-color: #ffebee;\n");
      out.write("            color: #c62828;\n");
      out.write("            border: 1px solid #c62828;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* No data */\n");
      out.write("        .no-data {\n");
      out.write("            text-align: center;\n");
      out.write("            padding: 30px;\n");
      out.write("            color: #3F72AF;\n");
      out.write("            font-style: italic;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Export buttons */\n");
      out.write("        .export-buttons {\n");
      out.write("            display: flex;\n");
      out.write("            gap: 10px;\n");
      out.write("            margin-top: 20px;\n");
      out.write("            justify-content: flex-end;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .export-btn {\n");
      out.write("            padding: 8px 15px;\n");
      out.write("            background-color: #2e7d32;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            font-size: 12px;\n");
      out.write("            text-decoration: none;\n");
      out.write("            display: inline-flex;\n");
      out.write("            align-items: center;\n");
      out.write("            gap: 5px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .export-btn:hover {\n");
      out.write("            background-color: #1b5e20;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .export-btn.print {\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .export-btn.print:hover {\n");
      out.write("            background-color: #112D4E;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Winner announcement */\n");
      out.write("        .winner-announcement {\n");
      out.write("            background-color: #e1f7e1;\n");
      out.write("            border: 2px solid #2e7d32;\n");
      out.write("            padding: 20px;\n");
      out.write("            border-radius: 10px;\n");
      out.write("            margin: 20px 0;\n");
      out.write("            text-align: center;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .winner-title {\n");
      out.write("            color: #2e7d32;\n");
      out.write("            font-weight: bold;\n");
      out.write("            margin-bottom: 10px;\n");
      out.write("            font-size: 18px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .winner-name {\n");
      out.write("            color: #112D4E;\n");
      out.write("            font-size: 22px;\n");
      out.write("            margin: 10px 0;\n");
      out.write("        }\n");
      out.write("    </style>\n");
      out.write("</head>\n");
      out.write("<body>\n");
      out.write("    <!-- Left Navigation Panel -->\n");
      out.write("    <div class=\"nav-panel\">\n");
      out.write("        <div class=\"admin-info\">\n");
      out.write("            <div class=\"admin-name\">");
      out.print( admin.getUsername() );
      out.write("</div>\n");
      out.write("            <div class=\"admin-id\">Admin ID: ");
      out.print( admin.getAdminId() );
      out.write("</div>\n");
      out.write("        </div>\n");
      out.write("\n");
      out.write("        <div class=\"nav-links\">\n");
      out.write("            <a href=\"admindashboard.jsp\" class=\"nav-link\">Dashboard</a>\n");
      out.write("            <a href=\"studenttab.jsp\" class=\"nav-link\">Manage Students</a>\n");
      out.write("            <a href=\"candidatetab.jsp\" class=\"nav-link\">Manage Candidates</a>\n");
      out.write("            <a href=\"electiontab.jsp\" class=\"nav-link\">Manage Elections</a>\n");
      out.write("            <a href=\"votetab.jsp\" class=\"nav-link\">Manage Votes</a>\n");
      out.write("            <a href=\"admintab.jsp\" class=\"nav-link\">Manage Admins</a>\n");
      out.write("            <a href=\"resulttab.jsp\" class=\"nav-link active\">Current Results</a>\n");
      out.write("            <a href=\"../LogoutServlet\" class=\"nav-link\" onclick=\"return confirm('Logout?')\">Logout</a>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <!-- Main Content -->\n");
      out.write("    <div class=\"main-content\">\n");
      out.write("        <!-- Top Header -->\n");
      out.write("        <div class=\"top-header\">\n");
      out.write("            <div class=\"page-title\">Election Results</div>\n");
      out.write("            <button class=\"logout-btn\" onclick=\"if(confirm('Logout?')) window.location.href='../LogoutServlet'\">Logout</button>\n");
      out.write("        </div>\n");
      out.write("\n");
      out.write("        <!-- Results Content -->\n");
      out.write("        <div class=\"results-content\">\n");
      out.write("            ");
 if (!message.isEmpty()) { 
      out.write("\n");
      out.write("            <div class=\"message-alert ");
      out.print( messageType );
      out.write("\">\n");
      out.write("                ");
      out.print( message );
      out.write("\n");
      out.write("            </div>\n");
      out.write("            ");
 } 
      out.write("\n");
      out.write("\n");
      out.write("            <!-- Election Selector -->\n");
      out.write("            <div class=\"election-selector\">\n");
      out.write("                <div class=\"selector-title\">Select Election to View Results</div>\n");
      out.write("                <form class=\"selector-form\" method=\"GET\" action=\"resulttab.jsp\">\n");
      out.write("                    <select name=\"electionId\" class=\"selector-select\" onchange=\"this.form.submit()\">\n");
      out.write("                        <option value=\"\">-- Select Election --</option>\n");
      out.write("                        ");
 for (Election election : allElections) { 
                            boolean isSelected = election.getElectionId() == selectedElectionId;
                        
      out.write("\n");
      out.write("                        <option value=\"");
      out.print( election.getElectionId() );
      out.write('"');
      out.write(' ');
      out.print( isSelected ? "selected" : "" );
      out.write(">\n");
      out.write("                            ");
      out.print( election.getTitle() );
      out.write(" \n");
      out.write("                            (");
      out.print( election.getStatus() );
      out.write(" \n");
      out.write("                            ");
 if (election.getEndDate() != null) { 
      out.write("\n");
      out.write("                            - ");
      out.print( election.getEndDate() );
      out.write("\n");
      out.write("                            ");
 } 
      out.write(")\n");
      out.write("                        </option>\n");
      out.write("                        ");
 } 
      out.write("\n");
      out.write("                    </select>\n");
      out.write("                    <button type=\"submit\" class=\"selector-btn\">View Results</button>\n");
      out.write("                </form>\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            ");
 if (selectedElection != null) { 
      out.write("\n");
      out.write("            <!-- Election Info -->\n");
      out.write("            <div class=\"card\">\n");
      out.write("                <h2 class=\"card-title\">\n");
      out.write("                    ");
      out.print( selectedElection.getTitle() );
      out.write("\n");
      out.write("                    <span class=\"status-badge \n");
      out.write("                        ");
 if ("ONGOING".equals(selectedElection.getStatus())) { 
      out.write("status-ongoing");
 } 
                        else if ("UPCOMING".equals(selectedElection.getStatus())) { 
      out.write("status-upcoming");
 } 
                        else if ("ENDED".equals(selectedElection.getStatus())) { 
      out.write("status-ended");
 } 
      out.write("\">\n");
      out.write("                        ");
      out.print( selectedElection.getStatus() );
      out.write("\n");
      out.write("                    </span>\n");
      out.write("                </h2>\n");
      out.write("                \n");
      out.write("                <p style=\"color: #3F72AF; margin-bottom: 15px;\">");
      out.print( selectedElection.getDescription() );
      out.write("</p>\n");
      out.write("                \n");
      out.write("                ");
 if (selectedElection.getStartDate() != null && selectedElection.getEndDate() != null) { 
      out.write("\n");
      out.write("                <p style=\"color: #112D4E; font-size: 14px;\">\n");
      out.write("                    Election Period: <strong>");
      out.print( selectedElection.getStartDate() );
      out.write("</strong> to \n");
      out.write("                    <strong>");
      out.print( selectedElection.getEndDate() );
      out.write("</strong>\n");
      out.write("                </p>\n");
      out.write("                ");
 } 
      out.write("\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            <!-- Summary Statistics -->\n");
      out.write("            <div class=\"card\">\n");
      out.write("                <h2 class=\"card-title\">Election Summary</h2>\n");
      out.write("                <div class=\"summary-stats\">\n");
      out.write("                    <div class=\"summary-box\">\n");
      out.write("                        <div class=\"summary-number\">");
      out.print( candidates.size() );
      out.write("</div>\n");
      out.write("                        <div class=\"summary-label\">Candidates</div>\n");
      out.write("                    </div>\n");
      out.write("                    <div class=\"summary-box\">\n");
      out.write("                        <div class=\"summary-number\">");
      out.print( totalVotes );
      out.write("</div>\n");
      out.write("                        <div class=\"summary-label\">Total Votes</div>\n");
      out.write("                    </div>\n");
      out.write("                    <div class=\"summary-box\">\n");
      out.write("                        <div class=\"summary-number\">");
      out.print( totalVoters );
      out.write("</div>\n");
      out.write("                        <div class=\"summary-label\">Voters Participated</div>\n");
      out.write("                    </div>\n");
      out.write("                    <div class=\"summary-box\">\n");
      out.write("                        <div class=\"summary-number\">");
      out.print( request.getAttribute("votingPercentage") );
      out.write("%</div>\n");
      out.write("                        <div class=\"summary-label\">Voter Turnout</div>\n");
      out.write("                    </div>\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            ");
 if (!isElectionEnded) { 
      out.write("\n");
      out.write("            <!-- Warning for ongoing/upcoming elections -->\n");
      out.write("            <div class=\"message-alert warning\">\n");
      out.write("                <strong>Note:</strong> This election is ");
      out.print( selectedElection.getStatus() );
      out.write(". \n");
      out.write("                ");
 if (isElectionOngoing) { 
      out.write("\n");
      out.write("                Results shown are live but may change until the election ends.\n");
      out.write("                ");
 } else { 
      out.write("\n");
      out.write("                Voting has not started yet. No results available.\n");
      out.write("                ");
 } 
      out.write("\n");
      out.write("                Final results will be available after the election ends.\n");
      out.write("            </div>\n");
      out.write("            ");
 } 
      out.write("\n");
      out.write("\n");
      out.write("            ");
 if (!candidates.isEmpty()) { 
      out.write("\n");
      out.write("            <!-- Winners Announcement (for ended elections) -->\n");
      out.write("            ");
 if (isElectionEnded && !winnersByPosition.isEmpty()) { 
      out.write("\n");
      out.write("            <div class=\"winner-announcement\">\n");
      out.write("                <div class=\"winner-title\">🏆 ELECTION WINNERS 🏆</div>\n");
      out.write("                ");
 for (Map.Entry<String, Candidate> entry : winnersByPosition.entrySet()) { 
                    Candidate winner = entry.getValue();
                    String winnerName = winner.getCandidateName();
                    if (winnerName == null || winnerName.isEmpty()) {
                        winnerName = "Candidate " + winner.getCandidateId();
                    }
                
      out.write("\n");
      out.write("                <div style=\"margin: 10px 0;\">\n");
      out.write("                    <div style=\"color: #3F72AF; font-weight: bold;\">");
      out.print( entry.getKey() );
      out.write("</div>\n");
      out.write("                    <div class=\"winner-name\">");
      out.print( winnerName );
      out.write("</div>\n");
      out.write("                    <div style=\"color: #2e7d32;\">Won with ");
      out.print( winner.getVoteCount() );
      out.write(" votes</div>\n");
      out.write("                </div>\n");
      out.write("                ");
 } 
      out.write("\n");
      out.write("            </div>\n");
      out.write("            ");
 } 
      out.write("\n");
      out.write("\n");
      out.write("            <!-- Detailed Results by Position -->\n");
      out.write("            ");
 for (Map.Entry<String, List<Candidate>> entry : candidatesByPosition.entrySet()) { 
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
            
      out.write("\n");
      out.write("            <div class=\"card\">\n");
      out.write("                <div class=\"position-header\">");
      out.print( entry.getKey() );
      out.write("</div>\n");
      out.write("                \n");
      out.write("                <table class=\"results-table\">\n");
      out.write("                    <thead>\n");
      out.write("                        <tr>\n");
      out.write("                            <th width=\"5%\">Rank</th>\n");
      out.write("                            <th width=\"35%\">Candidate</th>\n");
      out.write("                            <th width=\"15%\">Student ID</th>\n");
      out.write("                            <th width=\"10%\">Votes</th>\n");
      out.write("                            <th width=\"35%\">Vote Percentage</th>\n");
      out.write("                        </tr>\n");
      out.write("                    </thead>\n");
      out.write("                    <tbody>\n");
      out.write("                        ");
 
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
                        
      out.write("\n");
      out.write("                        <tr>\n");
      out.write("                            <td>\n");
      out.write("                                ");
 if (isWinner) { 
      out.write("\n");
      out.write("                                <strong style=\"color: #2e7d32;\">#");
      out.print( rank );
      out.write("</strong>\n");
      out.write("                                <span class=\"winner-badge\">WINNER</span>\n");
      out.write("                                ");
 } else { 
      out.write("\n");
      out.write("                                #");
      out.print( rank );
      out.write("\n");
      out.write("                                ");
 } 
      out.write("\n");
      out.write("                            </td>\n");
      out.write("                            <td><strong>");
      out.print( candidateName );
      out.write("</strong></td>\n");
      out.write("                            <td>");
      out.print( candidate.getStudentId() );
      out.write("</td>\n");
      out.write("                            <td><strong style=\"color: #3F72AF;\">");
      out.print( candidate.getVoteCount() );
      out.write("</strong></td>\n");
      out.write("                            <td>\n");
      out.write("                                <div style=\"display: flex; align-items: center; gap: 10px;\">\n");
      out.write("                                    <div style=\"width: 100px;\">\n");
      out.write("                                        <div class=\"vote-bar-container\">\n");
      out.write("                                            <div class=\"vote-bar\" style=\"width: ");
      out.print( barWidth );
      out.write("%;\">\n");
      out.write("                                                ");
      out.print( candidate.getVoteCount() );
      out.write("\n");
      out.write("                                            </div>\n");
      out.write("                                        </div>\n");
      out.write("                                    </div>\n");
      out.write("                                    <div style=\"font-size: 12px; color: #112D4E;\">\n");
      out.write("                                        ");
      out.print( String.format("%.1f", percentage) );
      out.write("%\n");
      out.write("                                    </div>\n");
      out.write("                                </div>\n");
      out.write("                            </td>\n");
      out.write("                        </tr>\n");
      out.write("                        ");
 
                            rank++;
                        } 
                        
      out.write("\n");
      out.write("                    </tbody>\n");
      out.write("                </table>\n");
      out.write("            </div>\n");
      out.write("            ");
 } 
      out.write("\n");
      out.write("\n");
      out.write("            <!-- Simple Bar Chart -->\n");
      out.write("            ");
 if (candidates.size() <= 10) { 
      out.write("\n");
      out.write("            <div class=\"card\">\n");
      out.write("                <h2 class=\"card-title\">Vote Distribution</h2>\n");
      out.write("                <div class=\"chart-container\">\n");
      out.write("                    ");
 
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
                    
      out.write("\n");
      out.write("                    <div class=\"chart-bar\" style=\"height: ");
      out.print( barHeight );
      out.write("px;\">\n");
      out.write("                        <div class=\"chart-value\">");
      out.print( c.getVoteCount() );
      out.write("</div>\n");
      out.write("                        <div class=\"chart-label\">");
      out.print( name );
      out.write("</div>\n");
      out.write("                    </div>\n");
      out.write("                    ");
 } 
      out.write("\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("            ");
 } 
      out.write("\n");
      out.write("\n");
      out.write("            <!-- Export Options -->\n");
      out.write("            <div class=\"export-buttons\">\n");
      out.write("                <button class=\"export-btn print\" onclick=\"window.print()\">\n");
      out.write("                    📄 Print Results\n");
      out.write("                </button>\n");
      out.write("                <a href=\"export_results.jsp?electionId=");
      out.print( selectedElectionId );
      out.write("\" class=\"export-btn\">\n");
      out.write("                    📥 Export as PDF\n");
      out.write("                </a>\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            ");
 } else { 
      out.write("\n");
      out.write("            <!-- No Candidates -->\n");
      out.write("            <div class=\"card\">\n");
      out.write("                <div class=\"no-data\">\n");
      out.write("                    No candidates registered for this election yet.\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("            ");
 } 
      out.write("\n");
      out.write("\n");
      out.write("            ");
 } else if (allElections.isEmpty()) { 
      out.write("\n");
      out.write("            <!-- No Elections -->\n");
      out.write("            <div class=\"card\">\n");
      out.write("                <div class=\"no-data\">\n");
      out.write("                    No elections found in the system. Create elections first to view results.\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("            ");
 } else { 
      out.write("\n");
      out.write("            <!-- No Election Selected -->\n");
      out.write("            <div class=\"card\">\n");
      out.write("                <div class=\"no-data\">\n");
      out.write("                    Please select an election from the dropdown above to view results.\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("            ");
 } 
      out.write("\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <script>\n");
      out.write("        // Auto-refresh for ongoing elections\n");
      out.write("        ");
 if (selectedElection != null && "ONGOING".equals(selectedElection.getStatus())) { 
      out.write("\n");
      out.write("        setTimeout(function() {\n");
      out.write("            // Refresh every 30 seconds for ongoing elections\n");
      out.write("            window.location.reload();\n");
      out.write("        }, 30000);\n");
      out.write("        ");
 } 
      out.write("\n");
      out.write("\n");
      out.write("        // Print function\n");
      out.write("        function printResults() {\n");
      out.write("            window.print();\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        // Confirm before exporting\n");
      out.write("        document.querySelectorAll('.export-btn').forEach(btn => {\n");
      out.write("            if (!btn.classList.contains('print')) {\n");
      out.write("                btn.addEventListener('click', function(e) {\n");
      out.write("                    if (!confirm('Export election results?')) {\n");
      out.write("                        e.preventDefault();\n");
      out.write("                    }\n");
      out.write("                });\n");
      out.write("            }\n");
      out.write("        });\n");
      out.write("    </script>\n");
      out.write("</body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
