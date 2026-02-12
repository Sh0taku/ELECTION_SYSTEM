package org.apache.jsp.admin;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.election.beans.Admin;
import com.election.beans.Election;
import java.util.*;
import java.sql.*;
import com.election.dao.DBConnection;
import java.text.SimpleDateFormat;

public final class electiontab_jsp extends org.apache.jasper.runtime.HttpJspBase
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
String electionIdStr = request.getParameter("electionId");

Connection conn = null;
PreparedStatement pstmt = null;
Statement stmt = null;
ResultSet rs = null;

if (action != null) {
    try {
        conn = DBConnection.getConnection();
        
        if ("delete".equals(action) && electionIdStr != null) {
            // DELETE ELECTION (only if no candidates/votes)
            int electionId = Integer.parseInt(electionIdStr);
            
            // Check if election has candidates
            String checkCandidates = "SELECT COUNT(*) FROM CANDIDATES WHERE ELECTION_ID = ?";
            pstmt = conn.prepareStatement(checkCandidates);
            pstmt.setInt(1, electionId);
            rs = pstmt.executeQuery();
            rs.next();
            int candidateCount = rs.getInt(1);
            
            if (candidateCount > 0) {
                message = "Cannot delete election with candidates! Remove candidates first.";
                messageType = "error";
            } else {
                String sql = "DELETE FROM ELECTIONS WHERE ELECTION_ID = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, electionId);
                
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    message = "Election deleted successfully!";
                    messageType = "success";
                }
            }
            
        } else if ("add".equals(action)) {
            // ADD NEW ELECTION
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String status = request.getParameter("status");
            
            String insertSql = "INSERT INTO ELECTIONS (TITLE, DESCRIPTION, START_DATE, END_DATE, STATUS) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setDate(3, java.sql.Date.valueOf(startDate));
            pstmt.setDate(4, java.sql.Date.valueOf(endDate));
            pstmt.setString(5, status);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Election added successfully!";
                messageType = "success";
            }
            
        } else if ("update".equals(action)) {
            // UPDATE ELECTION
            String electionId = request.getParameter("electionId");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String status = request.getParameter("status");
            
            String updateSql = "UPDATE ELECTIONS SET TITLE = ?, DESCRIPTION = ?, START_DATE = ?, END_DATE = ?, STATUS = ? WHERE ELECTION_ID = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setDate(3, java.sql.Date.valueOf(startDate));
            pstmt.setDate(4, java.sql.Date.valueOf(endDate));
            pstmt.setString(5, status);
            pstmt.setInt(6, Integer.parseInt(electionId));
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Election updated successfully!";
                messageType = "success";
            }
        } else if ("changeStatus".equals(action) && electionIdStr != null) {
            // CHANGE ELECTION STATUS
            int electionId = Integer.parseInt(electionIdStr);
            String newStatus = request.getParameter("status");
            
            String updateSql = "UPDATE ELECTIONS SET STATUS = ? WHERE ELECTION_ID = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, electionId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Election status updated to " + newStatus + "!";
                messageType = "success";
            }
        }
        
    } catch (SQLException e) {
        message = "Database error: " + e.getMessage();
        messageType = "error";
        e.printStackTrace();
    } catch (Exception e) {
        message = "Error: " + e.getMessage();
        messageType = "error";
        e.printStackTrace();
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
}

// Fetch elections for display
List<Election> elections = new ArrayList<Election>();
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

try {
    conn = DBConnection.getConnection();
    stmt = conn.createStatement();
    
    // Get all elections
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
      out.write("    <title>Manage Elections - UITM Election System</title>\n");
      out.write("    <style>\n");
      out.write("        /* Same CSS as candidatetab */\n");
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
      out.write("        .status-btn {\n");
      out.write("            padding: 5px 15px;\n");
      out.write("            background-color: #ff9800;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            border-radius: 3px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            font-size: 12px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .status-btn:hover {\n");
      out.write("            background-color: #f57c00;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Status badges */\n");
      out.write("        .badge {\n");
      out.write("            padding: 4px 8px;\n");
      out.write("            border-radius: 12px;\n");
      out.write("            font-size: 11px;\n");
      out.write("            font-weight: bold;\n");
      out.write("            display: inline-block;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        .badge-ongoing {\n");
      out.write("            background-color: #e1f7e1;\n");
      out.write("            color: #2e7d32;\n");
      out.write("            border: 1px solid #2e7d32;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        .badge-upcoming {\n");
      out.write("            background-color: #fff3e0;\n");
      out.write("            color: #ef6c00;\n");
      out.write("            border: 1px solid #ef6c00;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        .badge-ended {\n");
      out.write("            background-color: #ffebee;\n");
      out.write("            color: #c62828;\n");
      out.write("            border: 1px solid #c62828;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .description-cell {\n");
      out.write("            max-width: 300px;\n");
      out.write("            overflow: hidden;\n");
      out.write("            text-overflow: ellipsis;\n");
      out.write("            white-space: nowrap;\n");
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
      out.write("            min-height: 80px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .form-row {\n");
      out.write("            display: flex;\n");
      out.write("            gap: 15px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .form-row .form-group {\n");
      out.write("            flex: 1;\n");
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
      out.write("\n");
      out.write("        /* Status Modal */\n");
      out.write("        .status-options {\n");
      out.write("            display: flex;\n");
      out.write("            gap: 10px;\n");
      out.write("            margin: 15px 0;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .status-option {\n");
      out.write("            flex: 1;\n");
      out.write("            padding: 10px;\n");
      out.write("            text-align: center;\n");
      out.write("            border: 2px solid #DBE2EF;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            transition: all 0.3s;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .status-option:hover {\n");
      out.write("            border-color: #3F72AF;\n");
      out.write("            background-color: #F9F7F7;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .status-option.selected {\n");
      out.write("            border-color: #3F72AF;\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("            color: white;\n");
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
      out.write("            <a href=\"electiontab.jsp\" class=\"nav-link active\">Manage Elections</a>\n");
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
      out.write("            <div class=\"page-title\">Manage Elections</div>\n");
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
      out.write("                    <input type=\"text\" class=\"search-input\" placeholder=\"Search elections...\" id=\"searchInput\">\n");
      out.write("                    <button class=\"search-btn\" onclick=\"searchElections()\">Search</button>\n");
      out.write("                </div>\n");
      out.write("                <button class=\"add-btn\" onclick=\"openAddModal()\">+ Create New Election</button>\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            <!-- Data Table -->\n");
      out.write("            <div class=\"data-table\">\n");
      out.write("                <div class=\"table-header\">\n");
      out.write("                    <div class=\"table-title\">Elections List (");
      out.print( elections.size() );
      out.write(" elections)</div>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div style=\"overflow-x: auto;\">\n");
      out.write("                    <table>\n");
      out.write("                        <thead>\n");
      out.write("                            <tr>\n");
      out.write("                                <th>ID</th>\n");
      out.write("                                <th>Title</th>\n");
      out.write("                                <th>Description</th>\n");
      out.write("                                <th>Start Date</th>\n");
      out.write("                                <th>End Date</th>\n");
      out.write("                                <th>Status</th>\n");
      out.write("                                <th>Actions</th>\n");
      out.write("                            </tr>\n");
      out.write("                        </thead>\n");
      out.write("                        <tbody id=\"electionTableBody\">\n");
      out.write("                            ");
 
                            for (Election election : elections) { 
                                String statusClass = "";
                                String status = election.getStatus();
                                if ("ONGOING".equals(status)) {
                                    statusClass = "badge-ongoing";
                                } else if ("UPCOMING".equals(status)) {
                                    statusClass = "badge-upcoming";
                                } else if ("ENDED".equals(status)) {
                                    statusClass = "badge-ended";
                                }
                                
                                String startDate = election.getStartDate() != null ? 
                                    new SimpleDateFormat("dd MMM yyyy").format(election.getStartDate()) : "Not set";
                                String endDate = election.getEndDate() != null ? 
                                    new SimpleDateFormat("dd MMM yyyy").format(election.getEndDate()) : "Not set";
                            
      out.write("\n");
      out.write("                            <tr>\n");
      out.write("                                <td><strong>#");
      out.print( election.getElectionId() );
      out.write("</strong></td>\n");
      out.write("                                <td><strong>");
      out.print( election.getTitle() );
      out.write("</strong></td>\n");
      out.write("                                <td class=\"description-cell\" title=\"");
      out.print( election.getDescription() != null ? election.getDescription() : "" );
      out.write("\">\n");
      out.write("                                    ");
      out.print( election.getDescription() != null && election.getDescription().length() > 50 ? 
                                        election.getDescription().substring(0, 50) + "..." : 
                                        (election.getDescription() != null ? election.getDescription() : "No description") );
      out.write("\n");
      out.write("                                </td>\n");
      out.write("                                <td>");
      out.print( startDate );
      out.write("</td>\n");
      out.write("                                <td>");
      out.print( endDate );
      out.write("</td>\n");
      out.write("                                <td>\n");
      out.write("                                    <span class=\"badge ");
      out.print( statusClass );
      out.write("\">\n");
      out.write("                                        ");
      out.print( status );
      out.write("\n");
      out.write("                                    </span>\n");
      out.write("                                </td>\n");
      out.write("                                <td class=\"actions-cell\">\n");
      out.write("                                    <button class=\"edit-btn\" onclick=\"editElection(\n");
      out.write("                                        ");
      out.print( election.getElectionId() );
      out.write(", \n");
      out.write("                                        '");
      out.print( election.getTitle().replace("'", "\\'") );
      out.write("', \n");
      out.write("                                        '");
      out.print( election.getDescription() != null ? election.getDescription().replace("'", "\\'") : "" );
      out.write("',\n");
      out.write("                                        '");
      out.print( election.getStartDate() != null ? dateFormat.format(election.getStartDate()) : "" );
      out.write("',\n");
      out.write("                                        '");
      out.print( election.getEndDate() != null ? dateFormat.format(election.getEndDate()) : "" );
      out.write("',\n");
      out.write("                                        '");
      out.print( election.getStatus() );
      out.write("'\n");
      out.write("                                    )\">Edit</button>\n");
      out.write("                                    <button class=\"status-btn\" onclick=\"changeStatus(");
      out.print( election.getElectionId() );
      out.write(", '");
      out.print( election.getStatus() );
      out.write("', '");
      out.print( election.getTitle().replace("'", "\\'") );
      out.write("')\">Change Status</button>\n");
      out.write("                                    <button class=\"delete-btn\" onclick=\"deleteElection(");
      out.print( election.getElectionId() );
      out.write(", '");
      out.print( election.getTitle().replace("'", "\\'") );
      out.write("')\">Delete</button>\n");
      out.write("                                </td>\n");
      out.write("                            </tr>\n");
      out.write("                            ");
 } 
      out.write("\n");
      out.write("                            \n");
      out.write("                            ");
 if (elections.isEmpty()) { 
      out.write("\n");
      out.write("                            <tr>\n");
      out.write("                                <td colspan=\"7\" class=\"no-data\">\n");
      out.write("                                    No elections found. Create new elections using the \"Create New Election\" button.\n");
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
      out.write("    <!-- Add/Edit Election Modal -->\n");
      out.write("    <div id=\"electionModal\" class=\"modal\">\n");
      out.write("        <div class=\"modal-content\">\n");
      out.write("            <h2 class=\"modal-title\" id=\"modalTitle\">Create New Election</h2>\n");
      out.write("            <form id=\"electionForm\" method=\"POST\" action=\"electiontab.jsp\">\n");
      out.write("                <input type=\"hidden\" id=\"actionType\" name=\"action\" value=\"add\">\n");
      out.write("                <input type=\"hidden\" id=\"editElectionId\" name=\"electionId\">\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Election Title:</label>\n");
      out.write("                    <input type=\"text\" id=\"title\" name=\"title\" class=\"form-input\" required \n");
      out.write("                           placeholder=\"e.g., Student Council Election 2024\">\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Description:</label>\n");
      out.write("                    <textarea id=\"description\" name=\"description\" class=\"form-textarea\" \n");
      out.write("                              placeholder=\"Describe the election purpose, positions available, etc.\"></textarea>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-row\">\n");
      out.write("                    <div class=\"form-group\">\n");
      out.write("                        <label class=\"form-label\">Start Date:</label>\n");
      out.write("                        <input type=\"date\" id=\"startDate\" name=\"startDate\" class=\"form-input\" required>\n");
      out.write("                    </div>\n");
      out.write("                    <div class=\"form-group\">\n");
      out.write("                        <label class=\"form-label\">End Date:</label>\n");
      out.write("                        <input type=\"date\" id=\"endDate\" name=\"endDate\" class=\"form-input\" required>\n");
      out.write("                    </div>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Initial Status:</label>\n");
      out.write("                    <select id=\"status\" name=\"status\" class=\"form-select\" required>\n");
      out.write("                        <option value=\"UPCOMING\">Upcoming</option>\n");
      out.write("                        <option value=\"ONGOING\">Ongoing</option>\n");
      out.write("                        <option value=\"ENDED\">Ended</option>\n");
      out.write("                    </select>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"modal-buttons\">\n");
      out.write("                    <button type=\"submit\" class=\"save-btn\">Save Election</button>\n");
      out.write("                    <button type=\"button\" class=\"cancel-btn\" onclick=\"closeModal()\">Cancel</button>\n");
      out.write("                </div>\n");
      out.write("            </form>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <!-- Change Status Modal -->\n");
      out.write("    <div id=\"statusModal\" class=\"modal\">\n");
      out.write("        <div class=\"modal-content\">\n");
      out.write("            <h2 class=\"modal-title\">Change Election Status</h2>\n");
      out.write("            <p style=\"color: #112D4E; margin-bottom: 15px;\" id=\"statusMessage\">\n");
      out.write("                Select new status for election:\n");
      out.write("            </p>\n");
      out.write("            \n");
      out.write("            <div class=\"status-options\">\n");
      out.write("                <div class=\"status-option\" data-status=\"UPCOMING\" onclick=\"selectStatus('UPCOMING')\">\n");
      out.write("                    <div style=\"font-weight: bold; color: #ef6c00;\">UPCOMING</div>\n");
      out.write("                    <div style=\"font-size: 11px; color: #666;\">Election hasn't started</div>\n");
      out.write("                </div>\n");
      out.write("                <div class=\"status-option\" data-status=\"ONGOING\" onclick=\"selectStatus('ONGOING')\">\n");
      out.write("                    <div style=\"font-weight: bold; color: #2e7d32;\">ONGOING</div>\n");
      out.write("                    <div style=\"font-size: 11px; color: #666;\">Election is active</div>\n");
      out.write("                </div>\n");
      out.write("                <div class=\"status-option\" data-status=\"ENDED\" onclick=\"selectStatus('ENDED')\">\n");
      out.write("                    <div style=\"font-weight: bold; color: #c62828;\">ENDED</div>\n");
      out.write("                    <div style=\"font-size: 11px; color: #666;\">Election has ended</div>\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("            \n");
      out.write("            <form id=\"statusForm\" method=\"POST\" action=\"electiontab.jsp\">\n");
      out.write("                <input type=\"hidden\" name=\"action\" value=\"changeStatus\">\n");
      out.write("                <input type=\"hidden\" id=\"statusElectionId\" name=\"electionId\">\n");
      out.write("                <input type=\"hidden\" id=\"selectedStatus\" name=\"status\">\n");
      out.write("                \n");
      out.write("                <div class=\"modal-buttons\">\n");
      out.write("                    <button type=\"submit\" class=\"save-btn\">Update Status</button>\n");
      out.write("                    <button type=\"button\" class=\"cancel-btn\" onclick=\"closeStatusModal()\">Cancel</button>\n");
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
      out.write("                Are you sure you want to delete this election?\n");
      out.write("            </p>\n");
      out.write("            <form id=\"deleteForm\" method=\"POST\" action=\"electiontab.jsp\">\n");
      out.write("                <input type=\"hidden\" name=\"action\" value=\"delete\">\n");
      out.write("                <input type=\"hidden\" id=\"deleteElectionId\" name=\"electionId\">\n");
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
      out.write("            document.getElementById('modalTitle').textContent = 'Create New Election';\n");
      out.write("            document.getElementById('actionType').value = 'add';\n");
      out.write("            document.getElementById('electionForm').reset();\n");
      out.write("            \n");
      out.write("            // Set default dates (today and tomorrow)\n");
      out.write("            var today = new Date().toISOString().split('T')[0];\n");
      out.write("            var tomorrow = new Date();\n");
      out.write("            tomorrow.setDate(tomorrow.getDate() + 1);\n");
      out.write("            var tomorrowStr = tomorrow.toISOString().split('T')[0];\n");
      out.write("            \n");
      out.write("            document.getElementById('startDate').value = today;\n");
      out.write("            document.getElementById('endDate').value = tomorrowStr;\n");
      out.write("            \n");
      out.write("            document.getElementById('electionModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function editElection(electionId, title, description, startDate, endDate, status) {\n");
      out.write("            document.getElementById('modalTitle').textContent = 'Edit Election';\n");
      out.write("            document.getElementById('actionType').value = 'update';\n");
      out.write("            document.getElementById('editElectionId').value = electionId;\n");
      out.write("            document.getElementById('title').value = title;\n");
      out.write("            document.getElementById('description').value = description || '';\n");
      out.write("            document.getElementById('startDate').value = startDate;\n");
      out.write("            document.getElementById('endDate').value = endDate;\n");
      out.write("            document.getElementById('status').value = status;\n");
      out.write("            \n");
      out.write("            document.getElementById('electionModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function changeStatus(electionId, currentStatus, electionTitle) {\n");
      out.write("            document.getElementById('statusMessage').innerHTML = \n");
      out.write("                'Select new status for election:<br><strong>' + electionTitle + '</strong>';\n");
      out.write("            document.getElementById('statusElectionId').value = electionId;\n");
      out.write("            \n");
      out.write("            // Reset and select current status\n");
      out.write("            var options = document.querySelectorAll('.status-option');\n");
      out.write("            options.forEach(option => {\n");
      out.write("                option.classList.remove('selected');\n");
      out.write("                if (option.dataset.status === currentStatus) {\n");
      out.write("                    option.classList.add('selected');\n");
      out.write("                    document.getElementById('selectedStatus').value = currentStatus;\n");
      out.write("                }\n");
      out.write("            });\n");
      out.write("            \n");
      out.write("            document.getElementById('statusModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function selectStatus(status) {\n");
      out.write("            document.getElementById('selectedStatus').value = status;\n");
      out.write("            \n");
      out.write("            var options = document.querySelectorAll('.status-option');\n");
      out.write("            options.forEach(option => {\n");
      out.write("                option.classList.remove('selected');\n");
      out.write("                if (option.dataset.status === status) {\n");
      out.write("                    option.classList.add('selected');\n");
      out.write("                }\n");
      out.write("            });\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function closeModal() {\n");
      out.write("            document.getElementById('electionModal').style.display = 'none';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function closeStatusModal() {\n");
      out.write("            document.getElementById('statusModal').style.display = 'none';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function deleteElection(electionId, electionTitle) {\n");
      out.write("            document.getElementById('deleteMessage').innerHTML = \n");
      out.write("                'Are you sure you want to delete election:<br><br>' +\n");
      out.write("                '<strong>' + electionTitle + '</strong><br>' +\n");
      out.write("                'ID: ' + electionId + '<br><br>' +\n");
      out.write("                'Warning: This action cannot be undone!';\n");
      out.write("            document.getElementById('deleteElectionId').value = electionId;\n");
      out.write("            document.getElementById('deleteModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function closeDeleteModal() {\n");
      out.write("            document.getElementById('deleteModal').style.display = 'none';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        // Search function\n");
      out.write("        function searchElections() {\n");
      out.write("            var searchTerm = document.getElementById('searchInput').value.toLowerCase();\n");
      out.write("            var rows = document.getElementById('electionTableBody').getElementsByTagName('tr');\n");
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
      out.write("        // Form validation\n");
      out.write("        document.getElementById('electionForm').addEventListener('submit', function(e) {\n");
      out.write("            var title = document.getElementById('title').value;\n");
      out.write("            var startDate = document.getElementById('startDate').value;\n");
      out.write("            var endDate = document.getElementById('endDate').value;\n");
      out.write("            \n");
      out.write("            if (!title.trim()) {\n");
      out.write("                e.preventDefault();\n");
      out.write("                alert('Please enter election title!');\n");
      out.write("                return false;\n");
      out.write("            }\n");
      out.write("            \n");
      out.write("            if (!startDate || !endDate) {\n");
      out.write("                e.preventDefault();\n");
      out.write("                alert('Please select both start and end dates!');\n");
      out.write("                return false;\n");
      out.write("            }\n");
      out.write("            \n");
      out.write("            if (new Date(startDate) > new Date(endDate)) {\n");
      out.write("                e.preventDefault();\n");
      out.write("                alert('End date must be after start date!');\n");
      out.write("                return false;\n");
      out.write("            }\n");
      out.write("            \n");
      out.write("            return true;\n");
      out.write("        });\n");
      out.write("\n");
      out.write("        // Close modals when clicking outside\n");
      out.write("        window.onclick = function(event) {\n");
      out.write("            var electionModal = document.getElementById('electionModal');\n");
      out.write("            var statusModal = document.getElementById('statusModal');\n");
      out.write("            var deleteModal = document.getElementById('deleteModal');\n");
      out.write("            \n");
      out.write("            if (event.target === electionModal) {\n");
      out.write("                closeModal();\n");
      out.write("            }\n");
      out.write("            if (event.target === statusModal) {\n");
      out.write("                closeStatusModal();\n");
      out.write("            }\n");
      out.write("            if (event.target === deleteModal) {\n");
      out.write("                closeDeleteModal();\n");
      out.write("            }\n");
      out.write("        }\n");
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
