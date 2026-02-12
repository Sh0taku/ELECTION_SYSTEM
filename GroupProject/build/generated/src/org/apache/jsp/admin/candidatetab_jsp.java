package org.apache.jsp.admin;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.election.beans.Admin;
import com.election.beans.Candidate;
import com.election.beans.Election;
import java.util.*;
import java.sql.*;
import com.election.dao.DBConnection;

public final class candidatetab_jsp extends org.apache.jasper.runtime.HttpJspBase
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

// Handle CRUD operations
String action = request.getParameter("action");
String candidateIdStr = request.getParameter("candidateId");

Connection conn = null;
PreparedStatement pstmt = null;
Statement stmt = null;
ResultSet rs = null;

if (action != null) {
    try {
        conn = DBConnection.getConnection();
        
        if ("delete".equals(action) && candidateIdStr != null) {
            // DELETE CANDIDATE
            int candidateId = Integer.parseInt(candidateIdStr);
            String sql = "DELETE FROM CANDIDATES WHERE CANDIDATE_ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, candidateId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Candidate deleted successfully!";
                messageType = "success";
            } else {
                message = "Failed to delete candidate!";
                messageType = "error";
            }
            
        } else if ("add".equals(action)) {
            // ADD NEW CANDIDATE
            String studentId = request.getParameter("studentId");
            String electionIdStr = request.getParameter("electionId");
            String candidateName = request.getParameter("candidateName");
            String position = request.getParameter("position");
            String manifesto = request.getParameter("manifesto");
            
            // Check if student exists - FIXED: Using uppercase column names
            String checkStudentSql = "SELECT STUDENT_ID FROM STUDENTS WHERE STUDENT_ID = ?";
            pstmt = conn.prepareStatement(checkStudentSql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                message = "Student ID does not exist!";
                messageType = "error";
            } else {
                // Insert candidate - FIXED: Using uppercase table/column names
                String insertSql = "INSERT INTO CANDIDATES (STUDENT_ID, ELECTION_ID, CANDIDATE_NAME, POSITION, MANIFESTO, VOTE_COUNT) VALUES (?, ?, ?, ?, ?, 0)";
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setString(1, studentId);
                pstmt.setInt(2, Integer.parseInt(electionIdStr));
                pstmt.setString(3, candidateName);
                pstmt.setString(4, position);
                pstmt.setString(5, manifesto);
                
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    message = "Candidate added successfully!";
                    messageType = "success";
                    
                    // Update student's candidate status - FIXED: Using uppercase
                    String updateStatusSql = "UPDATE STUDENTS SET CANDIDATE_STATUS = 'CANDIDATE' WHERE STUDENT_ID = ?";
                    pstmt = conn.prepareStatement(updateStatusSql);
                    pstmt.setString(1, studentId);
                    pstmt.executeUpdate();
                }
            }
            
        } else if ("update".equals(action)) {
            // UPDATE CANDIDATE
            String candidateId = request.getParameter("candidateId");
            String candidateName = request.getParameter("candidateName");
            String position = request.getParameter("position");
            String manifesto = request.getParameter("manifesto");
            
            String updateSql = "UPDATE CANDIDATES SET CANDIDATE_NAME = ?, POSITION = ?, MANIFESTO = ? WHERE CANDIDATE_ID = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, candidateName);
            pstmt.setString(2, position);
            pstmt.setString(3, manifesto);
            pstmt.setInt(4, Integer.parseInt(candidateId));
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Candidate updated successfully!";
                messageType = "success";
            }
        }
        
    } catch (SQLException e) {
        message = "Database error: " + e.getMessage();
        messageType = "error";
        e.printStackTrace();
    } catch (NumberFormatException e) {
        message = "Invalid number format!";
        messageType = "error";
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
}

// Fetch candidates and elections for display
List<Candidate> candidates = new ArrayList<Candidate>();
List<Election> elections = new ArrayList<Election>();

try {
    conn = DBConnection.getConnection();
    stmt = conn.createStatement();
    
    // Get all candidates - FIXED: Using uppercase
    String candidateSql = "SELECT * FROM CANDIDATES ORDER BY CANDIDATE_ID";
    rs = stmt.executeQuery(candidateSql);
    
    while (rs.next()) {
        Candidate candidate = new Candidate();
        candidate.setCandidateId(rs.getInt("CANDIDATE_ID"));
        candidate.setStudentId(rs.getString("STUDENT_ID"));
        candidate.setElectionId(rs.getInt("ELECTION_ID"));
        candidate.setPosition(rs.getString("POSITION"));
        candidate.setManifesto(rs.getString("MANIFESTO"));
        candidate.setVoteCount(rs.getInt("VOTE_COUNT"));
        candidate.setCandidateName(rs.getString("CANDIDATE_NAME"));
        candidates.add(candidate);
    }
    
    // Get all elections - FIXED: Using uppercase
    String electionSql = "SELECT * FROM ELECTIONS ORDER BY ELECTION_ID";
    rs = stmt.executeQuery(electionSql);
    
    while (rs.next()) {
        Election election = new Election();
        election.setElectionId(rs.getInt("ELECTION_ID"));
        election.setTitle(rs.getString("TITLE"));
        election.setDescription(rs.getString("DESCRIPTION"));
        election.setStartDate(rs.getDate("START_DATE"));
        election.setEndDate(rs.getDate("END_DATE"));
        election.setStatus(rs.getString("STATUS"));
        elections.add(election);
    }
    
} catch (SQLException e) {
    message = "Error fetching data: " + e.getMessage();
    messageType = "error";
    e.printStackTrace();
} finally {
    try { if (rs != null) rs.close(); } catch (Exception e) {}
    try { if (stmt != null) stmt.close(); } catch (Exception e) {}
    try { if (conn != null) conn.close(); } catch (Exception e) {}
}

      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("<head>\n");
      out.write("    <title>Manage Candidates - UITM Election System</title>\n");
      out.write("    <style>\n");
      out.write("        /* Keep all your existing CSS styles - they're good */\n");
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
      out.write("        /* Management Content */\n");
      out.write("        .management-content {\n");
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
      out.write("        /* Action Bar */\n");
      out.write("        .action-bar {\n");
      out.write("            background-color: white;\n");
      out.write("            padding: 20px;\n");
      out.write("            border-radius: 10px;\n");
      out.write("            margin-bottom: 20px;\n");
      out.write("            display: flex;\n");
      out.write("            justify-content: space-between;\n");
      out.write("            align-items: center;\n");
      out.write("            box-shadow: 0 2px 5px rgba(0,0,0,0.1);\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .search-box {\n");
      out.write("            display: flex;\n");
      out.write("            gap: 10px;\n");
      out.write("            flex: 1;\n");
      out.write("            max-width: 400px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .search-input {\n");
      out.write("            flex: 1;\n");
      out.write("            padding: 10px;\n");
      out.write("            border: 1px solid #DBE2EF;\n");
      out.write("            border-radius: 5px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .search-btn {\n");
      out.write("            padding: 10px 20px;\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            cursor: pointer;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .search-btn:hover {\n");
      out.write("            background-color: #112D4E;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .add-btn {\n");
      out.write("            padding: 10px 20px;\n");
      out.write("            background-color: #2e7d32;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            font-weight: bold;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .add-btn:hover {\n");
      out.write("            background-color: #1b5e20;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Data Table */\n");
      out.write("        .data-table {\n");
      out.write("            width: 100%;\n");
      out.write("            background-color: white;\n");
      out.write("            border-radius: 10px;\n");
      out.write("            overflow: hidden;\n");
      out.write("            box-shadow: 0 2px 5px rgba(0,0,0,0.1);\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .table-header {\n");
      out.write("            background-color: #112D4E;\n");
      out.write("            color: white;\n");
      out.write("            padding: 15px;\n");
      out.write("            display: flex;\n");
      out.write("            justify-content: space-between;\n");
      out.write("            align-items: center;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .table-title {\n");
      out.write("            font-size: 18px;\n");
      out.write("            font-weight: bold;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        table {\n");
      out.write("            width: 100%;\n");
      out.write("            border-collapse: collapse;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        th {\n");
      out.write("            background-color: #DBE2EF;\n");
      out.write("            color: #112D4E;\n");
      out.write("            padding: 15px;\n");
      out.write("            text-align: left;\n");
      out.write("            font-weight: bold;\n");
      out.write("            border-bottom: 2px solid #3F72AF;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        td {\n");
      out.write("            padding: 15px;\n");
      out.write("            border-bottom: 1px solid #F9F7F7;\n");
      out.write("            color: #112D4E;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        tr:hover {\n");
      out.write("            background-color: #F9F7F7;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .actions-cell {\n");
      out.write("            display: flex;\n");
      out.write("            gap: 10px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .edit-btn {\n");
      out.write("            padding: 5px 15px;\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            border-radius: 3px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            font-size: 12px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .edit-btn:hover {\n");
      out.write("            background-color: #112D4E;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .delete-btn {\n");
      out.write("            padding: 5px 15px;\n");
      out.write("            background-color: #c62828;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            border-radius: 3px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            font-size: 12px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .delete-btn:hover {\n");
      out.write("            background-color: #b71c1c;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .manifesto-cell {\n");
      out.write("            max-width: 200px;\n");
      out.write("            overflow: hidden;\n");
      out.write("            text-overflow: ellipsis;\n");
      out.write("            white-space: nowrap;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .vote-count {\n");
      out.write("            font-weight: bold;\n");
      out.write("            color: #3F72AF;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .no-data {\n");
      out.write("            text-align: center;\n");
      out.write("            padding: 30px;\n");
      out.write("            color: #3F72AF;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Modal Styles */\n");
      out.write("        .modal {\n");
      out.write("            display: none;\n");
      out.write("            position: fixed;\n");
      out.write("            top: 0;\n");
      out.write("            left: 0;\n");
      out.write("            width: 100%;\n");
      out.write("            height: 100%;\n");
      out.write("            background-color: rgba(0,0,0,0.5);\n");
      out.write("            z-index: 1000;\n");
      out.write("            justify-content: center;\n");
      out.write("            align-items: center;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .modal-content {\n");
      out.write("            background-color: white;\n");
      out.write("            padding: 30px;\n");
      out.write("            border-radius: 10px;\n");
      out.write("            width: 500px;\n");
      out.write("            max-width: 90%;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .modal-title {\n");
      out.write("            color: #112D4E;\n");
      out.write("            margin-bottom: 20px;\n");
      out.write("            font-size: 20px;\n");
      out.write("            border-bottom: 2px solid #3F72AF;\n");
      out.write("            padding-bottom: 10px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .form-group {\n");
      out.write("            margin-bottom: 15px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .form-label {\n");
      out.write("            display: block;\n");
      out.write("            margin-bottom: 5px;\n");
      out.write("            color: #112D4E;\n");
      out.write("            font-weight: bold;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .form-input, .form-select, .form-textarea {\n");
      out.write("            width: 100%;\n");
      out.write("            padding: 10px;\n");
      out.write("            border: 1px solid #DBE2EF;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            font-size: 14px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .form-textarea {\n");
      out.write("            resize: vertical;\n");
      out.write("            min-height: 100px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .modal-buttons {\n");
      out.write("            display: flex;\n");
      out.write("            gap: 10px;\n");
      out.write("            margin-top: 25px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .save-btn {\n");
      out.write("            flex: 1;\n");
      out.write("            padding: 12px;\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            font-weight: bold;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .save-btn:hover {\n");
      out.write("            background-color: #112D4E;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .cancel-btn {\n");
      out.write("            flex: 1;\n");
      out.write("            padding: 12px;\n");
      out.write("            background-color: #DBE2EF;\n");
      out.write("            color: #112D4E;\n");
      out.write("            border: 1px solid #3F72AF;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            cursor: pointer;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .cancel-btn:hover {\n");
      out.write("            background-color: #cccccc;\n");
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
      out.write("            <a href=\"candidatetab.jsp\" class=\"nav-link active\">Manage Candidates</a>\n");
      out.write("            <a href=\"electiontab.jsp\" class=\"nav-link\">Manage Elections</a>\n");
      out.write("            <a href=\"votetab.jsp\" class=\"nav-link\">Manage Votes</a>\n");
      out.write("            <a href=\"resulttab.jsp\" class=\"nav-link\">Current Results</a>\n");
      out.write("            <a href=\"../LogoutServlet\" class=\"nav-link\" onclick=\"return confirm('Logout?')\">Logout</a>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <!-- Main Content -->\n");
      out.write("    <div class=\"main-content\">\n");
      out.write("        <!-- Top Header -->\n");
      out.write("        <div class=\"top-header\">\n");
      out.write("            <div class=\"page-title\">Manage Candidates</div>\n");
      out.write("            <button class=\"logout-btn\" onclick=\"if(confirm('Logout?')) window.location.href='../LogoutServlet'\">Logout</button>\n");
      out.write("        </div>\n");
      out.write("\n");
      out.write("        <!-- Management Content -->\n");
      out.write("        <div class=\"management-content\">\n");
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
      out.write("            <!-- Action Bar -->\n");
      out.write("            <div class=\"action-bar\">\n");
      out.write("                <div class=\"search-box\">\n");
      out.write("                    <input type=\"text\" class=\"search-input\" placeholder=\"Search candidates...\" id=\"searchInput\">\n");
      out.write("                    <button class=\"search-btn\" onclick=\"searchCandidates()\">Search</button>\n");
      out.write("                </div>\n");
      out.write("                <button class=\"add-btn\" onclick=\"openAddModal()\">+ Add New Candidate</button>\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            <!-- Data Table -->\n");
      out.write("            <div class=\"data-table\">\n");
      out.write("                <div class=\"table-header\">\n");
      out.write("                    <div class=\"table-title\">Candidates List (");
      out.print( candidates.size() );
      out.write(" candidates)</div>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div style=\"overflow-x: auto;\">\n");
      out.write("                    <table>\n");
      out.write("                        <thead>\n");
      out.write("                            <tr>\n");
      out.write("                                <th>Candidate ID</th>\n");
      out.write("                                <th>Candidate Name</th>\n");
      out.write("                                <th>Student ID</th>\n");
      out.write("                                <th>Position</th>\n");
      out.write("                                <th>Election</th>\n");
      out.write("                                <th>Manifesto</th>\n");
      out.write("                                <th>Votes</th>\n");
      out.write("                                <th>Actions</th>\n");
      out.write("                            </tr>\n");
      out.write("                        </thead>\n");
      out.write("                        <tbody id=\"candidateTableBody\">\n");
      out.write("                            ");
 
                            for (Candidate candidate : candidates) { 
                                String candidateName = candidate.getCandidateName();
                                if (candidateName == null || candidateName.isEmpty()) {
                                    candidateName = "Candidate " + candidate.getCandidateId();
                                }
                                
                                String manifesto = candidate.getManifesto();
                                if (manifesto == null || manifesto.isEmpty()) {
                                    manifesto = "No manifesto";
                                } else if (manifesto.length() > 50) {
                                    manifesto = manifesto.substring(0, 50) + "...";
                                }
                                
                                // Get election title
                                String electionTitle = "Election " + candidate.getElectionId();
                                for (Election election : elections) {
                                    if (election.getElectionId() == candidate.getElectionId()) {
                                        electionTitle = election.getTitle();
                                        break;
                                    }
                                }
                            
      out.write("\n");
      out.write("                            <tr>\n");
      out.write("                                <td>");
      out.print( candidate.getCandidateId() );
      out.write("</td>\n");
      out.write("                                <td>");
      out.print( candidateName );
      out.write("</td>\n");
      out.write("                                <td>");
      out.print( candidate.getStudentId() );
      out.write("</td>\n");
      out.write("                                <td>");
      out.print( candidate.getPosition() );
      out.write("</td>\n");
      out.write("                                <td>");
      out.print( electionTitle );
      out.write("</td>\n");
      out.write("                                <td class=\"manifesto-cell\" title=\"");
      out.print( candidate.getManifesto() != null ? candidate.getManifesto() : "" );
      out.write('"');
      out.write('>');
      out.print( manifesto );
      out.write("</td>\n");
      out.write("                                <td class=\"vote-count\">");
      out.print( candidate.getVoteCount() );
      out.write("</td>\n");
      out.write("                                <td class=\"actions-cell\">\n");
      out.write("                                    <button class=\"edit-btn\" onclick=\"editCandidate(");
      out.print( candidate.getCandidateId() );
      out.write(", '");
      out.print( candidateName );
      out.write("', '");
      out.print( candidate.getPosition() );
      out.write("', '");
      out.print( candidate.getManifesto() != null ? candidate.getManifesto().replace("'", "\\'") : "" );
      out.write("')\">Edit</button>\n");
      out.write("                                    <button class=\"delete-btn\" onclick=\"deleteCandidate(");
      out.print( candidate.getCandidateId() );
      out.write(", '");
      out.print( candidateName.replace("'", "\\'") );
      out.write("')\">Delete</button>\n");
      out.write("                                </td>\n");
      out.write("                            </tr>\n");
      out.write("                            ");
 } 
      out.write("\n");
      out.write("                            \n");
      out.write("                            ");
 if (candidates.isEmpty()) { 
      out.write("\n");
      out.write("                            <tr>\n");
      out.write("                                <td colspan=\"8\" class=\"no-data\">\n");
      out.write("                                    No candidates found. Add new candidates using the \"Add New Candidate\" button.\n");
      out.write("                                </td>\n");
      out.write("                            </tr>\n");
      out.write("                            ");
 } 
      out.write("\n");
      out.write("                        </tbody>\n");
      out.write("                    </table>\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <!-- Add/Edit Modal -->\n");
      out.write("    <div id=\"candidateModal\" class=\"modal\">\n");
      out.write("        <div class=\"modal-content\">\n");
      out.write("            <h2 class=\"modal-title\" id=\"modalTitle\">Add New Candidate</h2>\n");
      out.write("            <form id=\"candidateForm\" method=\"POST\" action=\"candidatetab.jsp\">\n");
      out.write("                <input type=\"hidden\" id=\"actionType\" name=\"action\" value=\"add\">\n");
      out.write("                <input type=\"hidden\" id=\"editCandidateId\" name=\"candidateId\">\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Student ID:</label>\n");
      out.write("                    <input type=\"text\" id=\"studentId\" name=\"studentId\" class=\"form-input\" required \n");
      out.write("                           placeholder=\"Enter student ID\">\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Election:</label>\n");
      out.write("                    <select id=\"electionId\" name=\"electionId\" class=\"form-select\" required>\n");
      out.write("                        <option value=\"\">Select Election</option>\n");
      out.write("                        ");
 for (Election election : elections) { 
      out.write("\n");
      out.write("                        <option value=\"");
      out.print( election.getElectionId() );
      out.write('"');
      out.write('>');
      out.print( election.getTitle() );
      out.write("</option>\n");
      out.write("                        ");
 } 
      out.write("\n");
      out.write("                    </select>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Candidate Name:</label>\n");
      out.write("                    <input type=\"text\" id=\"candidateName\" name=\"candidateName\" class=\"form-input\" required\n");
      out.write("                           placeholder=\"Enter candidate's full name\">\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Position:</label>\n");
      out.write("                    <input type=\"text\" id=\"position\" name=\"position\" class=\"form-input\" required \n");
      out.write("                           placeholder=\"e.g., President, Vice President\">\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Manifesto:</label>\n");
      out.write("                    <textarea id=\"manifesto\" name=\"manifesto\" class=\"form-textarea\" \n");
      out.write("                              placeholder=\"Describe the candidate's platform, goals, and vision...\"></textarea>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"modal-buttons\">\n");
      out.write("                    <button type=\"submit\" class=\"save-btn\">Save</button>\n");
      out.write("                    <button type=\"button\" class=\"cancel-btn\" onclick=\"closeModal()\">Cancel</button>\n");
      out.write("                </div>\n");
      out.write("            </form>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <!-- Delete Confirmation Modal -->\n");
      out.write("    <div id=\"deleteModal\" class=\"modal\">\n");
      out.write("        <div class=\"modal-content\">\n");
      out.write("            <h2 class=\"modal-title\">Confirm Delete</h2>\n");
      out.write("            <p style=\"color: #112D4E; margin-bottom: 20px;\" id=\"deleteMessage\">\n");
      out.write("                Are you sure you want to delete this candidate?\n");
      out.write("            </p>\n");
      out.write("            <form id=\"deleteForm\" method=\"POST\" action=\"candidatetab.jsp\">\n");
      out.write("                <input type=\"hidden\" name=\"action\" value=\"delete\">\n");
      out.write("                <input type=\"hidden\" id=\"deleteCandidateId\" name=\"candidateId\">\n");
      out.write("                \n");
      out.write("                <div class=\"modal-buttons\">\n");
      out.write("                    <button type=\"submit\" class=\"save-btn\" style=\"background-color: #c62828;\">Delete</button>\n");
      out.write("                    <button type=\"button\" class=\"cancel-btn\" onclick=\"closeDeleteModal()\">Cancel</button>\n");
      out.write("                </div>\n");
      out.write("            </form>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <script>\n");
      out.write("        // Modal functions\n");
      out.write("        function openAddModal() {\n");
      out.write("            document.getElementById('modalTitle').textContent = 'Add New Candidate';\n");
      out.write("            document.getElementById('actionType').value = 'add';\n");
      out.write("            document.getElementById('candidateForm').reset();\n");
      out.write("            document.getElementById('candidateModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function editCandidate(candidateId, candidateName, position, manifesto) {\n");
      out.write("            document.getElementById('modalTitle').textContent = 'Edit Candidate';\n");
      out.write("            document.getElementById('actionType').value = 'update';\n");
      out.write("            document.getElementById('editCandidateId').value = candidateId;\n");
      out.write("            document.getElementById('candidateName').value = candidateName;\n");
      out.write("            document.getElementById('position').value = position;\n");
      out.write("            document.getElementById('manifesto').value = manifesto;\n");
      out.write("            \n");
      out.write("            document.getElementById('candidateModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function closeModal() {\n");
      out.write("            document.getElementById('candidateModal').style.display = 'none';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function deleteCandidate(candidateId, candidateName) {\n");
      out.write("            document.getElementById('deleteMessage').textContent = \n");
      out.write("                'Are you sure you want to delete candidate: ' + candidateName + ' (ID: ' + candidateId + ')?';\n");
      out.write("            document.getElementById('deleteCandidateId').value = candidateId;\n");
      out.write("            document.getElementById('deleteModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function closeDeleteModal() {\n");
      out.write("            document.getElementById('deleteModal').style.display = 'none';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        // Search function\n");
      out.write("        function searchCandidates() {\n");
      out.write("            var searchTerm = document.getElementById('searchInput').value.toLowerCase();\n");
      out.write("            var rows = document.getElementById('candidateTableBody').getElementsByTagName('tr');\n");
      out.write("            \n");
      out.write("            for (var i = 0; i < rows.length; i++) {\n");
      out.write("                var cells = rows[i].getElementsByTagName('td');\n");
      out.write("                var found = false;\n");
      out.write("                \n");
      out.write("                for (var j = 0; j < cells.length; j++) {\n");
      out.write("                    var cellText = cells[j].textContent || cells[j].innerText;\n");
      out.write("                    if (cellText.toLowerCase().indexOf(searchTerm) > -1) {\n");
      out.write("                        found = true;\n");
      out.write("                        break;\n");
      out.write("                    }\n");
      out.write("                }\n");
      out.write("                \n");
      out.write("                rows[i].style.display = found ? '' : 'none';\n");
      out.write("            }\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        // Close modals when clicking outside\n");
      out.write("        window.onclick = function(event) {\n");
      out.write("            var candidateModal = document.getElementById('candidateModal');\n");
      out.write("            var deleteModal = document.getElementById('deleteModal');\n");
      out.write("            \n");
      out.write("            if (event.target === candidateModal) {\n");
      out.write("                closeModal();\n");
      out.write("            }\n");
      out.write("            if (event.target === deleteModal) {\n");
      out.write("                closeDeleteModal();\n");
      out.write("            }\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        // Form validation\n");
      out.write("        document.getElementById('candidateForm').addEventListener('submit', function(e) {\n");
      out.write("            var studentId = document.getElementById('studentId').value;\n");
      out.write("            var electionId = document.getElementById('electionId').value;\n");
      out.write("            var candidateName = document.getElementById('candidateName').value;\n");
      out.write("            var position = document.getElementById('position').value;\n");
      out.write("            \n");
      out.write("            if (!studentId.trim() || !electionId || !candidateName.trim() || !position.trim()) {\n");
      out.write("                e.preventDefault();\n");
      out.write("                alert('Please fill in all required fields!');\n");
      out.write("                return false;\n");
      out.write("            }\n");
      out.write("            \n");
      out.write("            return true;\n");
      out.write("        });\n");
      out.write("    </script>\n");
      out.write("</body>\n");
      out.write("</html>");
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
