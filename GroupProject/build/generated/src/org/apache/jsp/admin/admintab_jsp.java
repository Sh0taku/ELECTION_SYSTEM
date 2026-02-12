package org.apache.jsp.admin;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.election.beans.Admin;
import java.util.*;
import java.sql.*;
import com.election.dao.DBConnection;

public final class admintab_jsp extends org.apache.jasper.runtime.HttpJspBase
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
String adminId = request.getParameter("adminId");

Connection conn = null;
PreparedStatement pstmt = null;
Statement stmt = null;
ResultSet rs = null;

if (action != null) {
    try {
        conn = DBConnection.getConnection();
        
        if ("delete".equals(action) && adminId != null) {
            // DELETE ADMIN (prevent deleting yourself)
            if (adminId.equals(admin.getAdminId())) {
                message = "You cannot delete your own account!";
                messageType = "error";
            } else {
                String sql = "DELETE FROM ADMIN WHERE ADMIN_ID = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, adminId);
                
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    message = "Admin deleted successfully!";
                    messageType = "success";
                }
            }
            
        } else if ("add".equals(action)) {
            // ADD NEW ADMIN
            String newAdminId = request.getParameter("adminId");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            
            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                message = "Passwords do not match!";
                messageType = "error";
            } else {
                // Check if admin already exists
                String checkSql = "SELECT ADMIN_ID FROM ADMIN WHERE ADMIN_ID = ? OR USERNAME = ?";
                pstmt = conn.prepareStatement(checkSql);
                pstmt.setString(1, newAdminId);
                pstmt.setString(2, username);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    message = "Admin ID or Username already exists!";
                    messageType = "error";
                } else {
                    String insertSql = "INSERT INTO ADMIN (ADMIN_ID, USERNAME, PASSWORD) VALUES (?, ?, ?)";
                    pstmt = conn.prepareStatement(insertSql);
                    pstmt.setString(1, newAdminId);
                    pstmt.setString(2, username);
                    pstmt.setString(3, password);
                    
                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        message = "Admin added successfully!";
                        messageType = "success";
                    }
                }
            }
            
        } else if ("update".equals(action)) {
            // UPDATE ADMIN
            String updateAdminId = request.getParameter("adminId");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            
            if (password != null && !password.trim().isEmpty()) {
                // Update with password
                String updateSql = "UPDATE ADMIN SET USERNAME = ?, PASSWORD = ? WHERE ADMIN_ID = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, updateAdminId);
            } else {
                // Update without password
                String updateSql = "UPDATE ADMIN SET USERNAME = ? WHERE ADMIN_ID = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, username);
                pstmt.setString(2, updateAdminId);
            }
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Admin updated successfully!";
                messageType = "success";
            }
        } else if ("resetPassword".equals(action) && adminId != null) {
            // RESET PASSWORD
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            
            if (!newPassword.equals(confirmPassword)) {
                message = "Passwords do not match!";
                messageType = "error";
            } else {
                String updateSql = "UPDATE ADMIN SET PASSWORD = ? WHERE ADMIN_ID = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, newPassword);
                pstmt.setString(2, adminId);
                
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    message = "Password reset successfully!";
                    messageType = "success";
                }
            }
        }
        
    } catch (SQLException e) {
        message = "Database error: " + e.getMessage();
        messageType = "error";
        e.printStackTrace();
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
}

// Fetch admins for display
List<Admin> admins = new ArrayList<Admin>();

try {
    conn = DBConnection.getConnection();
    stmt = conn.createStatement();
    
    // Get all admins
    String adminSql = "SELECT * FROM ADMIN ORDER BY ADMIN_ID";
    rs = stmt.executeQuery(adminSql);
    
    while (rs.next()) {
        Admin adminObj = new Admin(
            rs.getString("ADMIN_ID"),
            rs.getString("USERNAME"),
            rs.getString("PASSWORD")
        );
        admins.add(adminObj);
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
      out.write("    <title>Manage Admins - UITM Election System</title>\n");
      out.write("    <style>\n");
      out.write("        /* Same CSS structure */\n");
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
      out.write("            gap: 5px;\n");
      out.write("            flex-wrap: wrap;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .edit-btn {\n");
      out.write("            padding: 5px 10px;\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            border-radius: 3px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            font-size: 11px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .edit-btn:hover {\n");
      out.write("            background-color: #112D4E;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .delete-btn {\n");
      out.write("            padding: 5px 10px;\n");
      out.write("            background-color: #c62828;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            border-radius: 3px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            font-size: 11px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .delete-btn:hover {\n");
      out.write("            background-color: #b71c1c;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .password-btn {\n");
      out.write("            padding: 5px 10px;\n");
      out.write("            background-color: #ff9800;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            border-radius: 3px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            font-size: 11px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .password-btn:hover {\n");
      out.write("            background-color: #f57c00;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Current user indicator */\n");
      out.write("        .current-user {\n");
      out.write("            background-color: #f0f7ff;\n");
      out.write("            border-left: 4px solid #3F72AF;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .current-user:hover {\n");
      out.write("            background-color: #e3f2fd;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .current-user-badge {\n");
      out.write("            background-color: #3F72AF;\n");
      out.write("            color: white;\n");
      out.write("            padding: 2px 6px;\n");
      out.write("            border-radius: 10px;\n");
      out.write("            font-size: 10px;\n");
      out.write("            margin-left: 5px;\n");
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
      out.write("        .form-input {\n");
      out.write("            width: 100%;\n");
      out.write("            padding: 10px;\n");
      out.write("            border: 1px solid #DBE2EF;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            font-size: 14px;\n");
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
      out.write("            <a href=\"admintab.jsp\" class=\"nav-link active\">Manage Admins</a>\n");
      out.write("            <a href=\"resulttab.jsp\" class=\"nav-link\">Current Results</a>\n");
      out.write("            <a href=\"../LogoutServlet\" class=\"nav-link\" onclick=\"return confirm('Logout?')\">Logout</a>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <!-- Main Content -->\n");
      out.write("    <div class=\"main-content\">\n");
      out.write("        <!-- Top Header -->\n");
      out.write("        <div class=\"top-header\">\n");
      out.write("            <div class=\"page-title\">Manage Admin Accounts</div>\n");
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
      out.write("                    <input type=\"text\" class=\"search-input\" placeholder=\"Search admins...\" id=\"searchInput\">\n");
      out.write("                    <button class=\"search-btn\" onclick=\"searchAdmins()\">Search</button>\n");
      out.write("                </div>\n");
      out.write("                <button class=\"add-btn\" onclick=\"openAddModal()\">+ Add New Admin</button>\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            <!-- Data Table -->\n");
      out.write("            <div class=\"data-table\">\n");
      out.write("                <div class=\"table-header\">\n");
      out.write("                    <div class=\"table-title\">Admin Accounts (");
      out.print( admins.size() );
      out.write(" admins)</div>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div style=\"overflow-x: auto;\">\n");
      out.write("                    <table>\n");
      out.write("                        <thead>\n");
      out.write("                            <tr>\n");
      out.write("                                <th>Admin ID</th>\n");
      out.write("                                <th>Username</th>\n");
      out.write("                                <th>Actions</th>\n");
      out.write("                            </tr>\n");
      out.write("                        </thead>\n");
      out.write("                        <tbody id=\"adminTableBody\">\n");
      out.write("                            ");
 
                            for (Admin adminObj : admins) { 
                                boolean isCurrentUser = adminObj.getAdminId().equals(admin.getAdminId());
                            
      out.write("\n");
      out.write("                            <tr class=\"");
      out.print( isCurrentUser ? "current-user" : "" );
      out.write("\">\n");
      out.write("                                <td>\n");
      out.write("                                    <strong>");
      out.print( adminObj.getAdminId() );
      out.write("</strong>\n");
      out.write("                                    ");
 if (isCurrentUser) { 
      out.write("\n");
      out.write("                                    <span class=\"current-user-badge\">You</span>\n");
      out.write("                                    ");
 } 
      out.write("\n");
      out.write("                                </td>\n");
      out.write("                                <td>");
      out.print( adminObj.getUsername() );
      out.write("</td>\n");
      out.write("                                <td class=\"actions-cell\">\n");
      out.write("                                    <button class=\"edit-btn\" onclick=\"editAdmin(\n");
      out.write("                                        '");
      out.print( adminObj.getAdminId() );
      out.write("', \n");
      out.write("                                        '");
      out.print( adminObj.getUsername().replace("'", "\\'") );
      out.write("'\n");
      out.write("                                    )\">Edit</button>\n");
      out.write("                                    <button class=\"password-btn\" onclick=\"resetAdminPassword(\n");
      out.write("                                        '");
      out.print( adminObj.getAdminId() );
      out.write("', \n");
      out.write("                                        '");
      out.print( adminObj.getUsername().replace("'", "\\'") );
      out.write("'\n");
      out.write("                                    )\">Reset Password</button>\n");
      out.write("                                    ");
 if (!isCurrentUser) { 
      out.write("\n");
      out.write("                                    <button class=\"delete-btn\" onclick=\"deleteAdmin(\n");
      out.write("                                        '");
      out.print( adminObj.getAdminId() );
      out.write("', \n");
      out.write("                                        '");
      out.print( adminObj.getUsername().replace("'", "\\'") );
      out.write("'\n");
      out.write("                                    )\">Delete</button>\n");
      out.write("                                    ");
 } else { 
      out.write("\n");
      out.write("                                    <button class=\"delete-btn\" style=\"background-color: #cccccc; cursor: not-allowed;\" \n");
      out.write("                                            title=\"Cannot delete your own account\" disabled>Delete</button>\n");
      out.write("                                    ");
 } 
      out.write("\n");
      out.write("                                </td>\n");
      out.write("                            </tr>\n");
      out.write("                            ");
 } 
      out.write("\n");
      out.write("                            \n");
      out.write("                            ");
 if (admins.isEmpty()) { 
      out.write("\n");
      out.write("                            <tr>\n");
      out.write("                                <td colspan=\"3\" class=\"no-data\">\n");
      out.write("                                    No admin accounts found. Add new admins using the \"Add New Admin\" button.\n");
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
      out.write("    <!-- Add/Edit Admin Modal -->\n");
      out.write("    <div id=\"adminModal\" class=\"modal\">\n");
      out.write("        <div class=\"modal-content\">\n");
      out.write("            <h2 class=\"modal-title\" id=\"modalTitle\">Add New Admin</h2>\n");
      out.write("            <form id=\"adminForm\" method=\"POST\" action=\"admintab.jsp\">\n");
      out.write("                <input type=\"hidden\" id=\"actionType\" name=\"action\" value=\"add\">\n");
      out.write("                <input type=\"hidden\" id=\"editAdminId\" name=\"adminId\">\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Admin ID:</label>\n");
      out.write("                    <input type=\"text\" id=\"adminIdInput\" name=\"adminId\" class=\"form-input\" required \n");
      out.write("                           placeholder=\"e.g., ADM001\">\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Username:</label>\n");
      out.write("                    <input type=\"text\" id=\"username\" name=\"username\" class=\"form-input\" required\n");
      out.write("                           placeholder=\"e.g., admin_user\">\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-row\">\n");
      out.write("                    <div class=\"form-group\">\n");
      out.write("                        <label class=\"form-label\">Password:</label>\n");
      out.write("                        <input type=\"password\" id=\"password\" name=\"password\" class=\"form-input\" required\n");
      out.write("                               placeholder=\"Set password\">\n");
      out.write("                    </div>\n");
      out.write("                    <div class=\"form-group\">\n");
      out.write("                        <label class=\"form-label\">Confirm Password:</label>\n");
      out.write("                        <input type=\"password\" id=\"confirmPassword\" name=\"confirmPassword\" class=\"form-input\" required\n");
      out.write("                               placeholder=\"Confirm password\">\n");
      out.write("                    </div>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"modal-buttons\">\n");
      out.write("                    <button type=\"submit\" class=\"save-btn\">Save Admin</button>\n");
      out.write("                    <button type=\"button\" class=\"cancel-btn\" onclick=\"closeModal()\">Cancel</button>\n");
      out.write("                </div>\n");
      out.write("            </form>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <!-- Reset Password Modal -->\n");
      out.write("    <div id=\"passwordModal\" class=\"modal\">\n");
      out.write("        <div class=\"modal-content\">\n");
      out.write("            <h2 class=\"modal-title\">Reset Admin Password</h2>\n");
      out.write("            <p style=\"color: #112D4E; margin-bottom: 15px;\" id=\"passwordMessage\">\n");
      out.write("                Enter new password for admin:\n");
      out.write("            </p>\n");
      out.write("            \n");
      out.write("            <form id=\"passwordForm\" method=\"POST\" action=\"admintab.jsp\">\n");
      out.write("                <input type=\"hidden\" name=\"action\" value=\"resetPassword\">\n");
      out.write("                <input type=\"hidden\" id=\"passwordAdminId\" name=\"adminId\">\n");
      out.write("                \n");
      out.write("                <div class=\"form-row\">\n");
      out.write("                    <div class=\"form-group\">\n");
      out.write("                        <label class=\"form-label\">New Password:</label>\n");
      out.write("                        <input type=\"password\" id=\"newPassword\" name=\"newPassword\" class=\"form-input\" required\n");
      out.write("                               placeholder=\"Enter new password\">\n");
      out.write("                    </div>\n");
      out.write("                    <div class=\"form-group\">\n");
      out.write("                        <label class=\"form-label\">Confirm Password:</label>\n");
      out.write("                        <input type=\"password\" id=\"confirmNewPassword\" name=\"confirmPassword\" class=\"form-input\" required\n");
      out.write("                               placeholder=\"Confirm new password\">\n");
      out.write("                    </div>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"modal-buttons\">\n");
      out.write("                    <button type=\"submit\" class=\"save-btn\">Reset Password</button>\n");
      out.write("                    <button type=\"button\" class=\"cancel-btn\" onclick=\"closePasswordModal()\">Cancel</button>\n");
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
      out.write("                Are you sure you want to delete this admin account?\n");
      out.write("            </p>\n");
      out.write("            <form id=\"deleteForm\" method=\"POST\" action=\"admintab.jsp\">\n");
      out.write("                <input type=\"hidden\" name=\"action\" value=\"delete\">\n");
      out.write("                <input type=\"hidden\" id=\"deleteAdminId\" name=\"adminId\">\n");
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
      out.write("            document.getElementById('modalTitle').textContent = 'Add New Admin';\n");
      out.write("            document.getElementById('actionType').value = 'add';\n");
      out.write("            document.getElementById('adminForm').reset();\n");
      out.write("            document.getElementById('adminModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function editAdmin(adminId, username) {\n");
      out.write("            document.getElementById('modalTitle').textContent = 'Edit Admin';\n");
      out.write("            document.getElementById('actionType').value = 'update';\n");
      out.write("            document.getElementById('editAdminId').value = adminId;\n");
      out.write("            document.getElementById('adminIdInput').value = adminId;\n");
      out.write("            document.getElementById('adminIdInput').readOnly = true;\n");
      out.write("            document.getElementById('username').value = username;\n");
      out.write("            document.getElementById('password').removeAttribute('required');\n");
      out.write("            document.getElementById('confirmPassword').removeAttribute('required');\n");
      out.write("            document.getElementById('password').placeholder = 'Leave blank to keep current password';\n");
      out.write("            document.getElementById('confirmPassword').placeholder = 'Leave blank to keep current password';\n");
      out.write("            \n");
      out.write("            document.getElementById('adminModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function resetAdminPassword(adminId, username) {\n");
      out.write("            document.getElementById('passwordMessage').innerHTML = \n");
      out.write("                'Enter new password for admin:<br><strong>' + username + '</strong>';\n");
      out.write("            document.getElementById('passwordAdminId').value = adminId;\n");
      out.write("            document.getElementById('passwordForm').reset();\n");
      out.write("            document.getElementById('passwordModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function closeModal() {\n");
      out.write("            document.getElementById('adminModal').style.display = 'none';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function closePasswordModal() {\n");
      out.write("            document.getElementById('passwordModal').style.display = 'none';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function deleteAdmin(adminId, username) {\n");
      out.write("            document.getElementById('deleteMessage').innerHTML = \n");
      out.write("                'Are you sure you want to delete admin account:<br><br>' +\n");
      out.write("                '<strong>' + username + '</strong><br>' +\n");
      out.write("                'ID: ' + adminId + '<br><br>' +\n");
      out.write("                'Warning: This action cannot be undone!';\n");
      out.write("            document.getElementById('deleteAdminId').value = adminId;\n");
      out.write("            document.getElementById('deleteModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function closeDeleteModal() {\n");
      out.write("            document.getElementById('deleteModal').style.display = 'none';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        // Search function\n");
      out.write("        function searchAdmins() {\n");
      out.write("            var searchTerm = document.getElementById('searchInput').value.toLowerCase();\n");
      out.write("            var rows = document.getElementById('adminTableBody').getElementsByTagName('tr');\n");
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
      out.write("        document.getElementById('adminForm').addEventListener('submit', function(e) {\n");
      out.write("            var adminId = document.getElementById('adminIdInput').value;\n");
      out.write("            var username = document.getElementById('username').value;\n");
      out.write("            var password = document.getElementById('password').value;\n");
      out.write("            var confirmPassword = document.getElementById('confirmPassword').value;\n");
      out.write("            \n");
      out.write("            if (!adminId.trim() || !username.trim()) {\n");
      out.write("                e.preventDefault();\n");
      out.write("                alert('Please fill in all required fields!');\n");
      out.write("                return false;\n");
      out.write("            }\n");
      out.write("            \n");
      out.write("            // For new admin, password is required\n");
      out.write("            if (document.getElementById('actionType').value === 'add') {\n");
      out.write("                if (!password.trim() || password.length < 6) {\n");
      out.write("                    e.preventDefault();\n");
      out.write("                    alert('Password must be at least 6 characters!');\n");
      out.write("                    return false;\n");
      out.write("                }\n");
      out.write("                \n");
      out.write("                if (password !== confirmPassword) {\n");
      out.write("                    e.preventDefault();\n");
      out.write("                    alert('Passwords do not match!');\n");
      out.write("                    return false;\n");
      out.write("                }\n");
      out.write("            }\n");
      out.write("            \n");
      out.write("            return true;\n");
      out.write("        });\n");
      out.write("\n");
      out.write("        document.getElementById('passwordForm').addEventListener('submit', function(e) {\n");
      out.write("            var newPassword = document.getElementById('newPassword').value;\n");
      out.write("            var confirmNewPassword = document.getElementById('confirmNewPassword').value;\n");
      out.write("            \n");
      out.write("            if (newPassword.length < 6) {\n");
      out.write("                e.preventDefault();\n");
      out.write("                alert('Password must be at least 6 characters!');\n");
      out.write("                return false;\n");
      out.write("            }\n");
      out.write("            \n");
      out.write("            if (newPassword !== confirmNewPassword) {\n");
      out.write("                e.preventDefault();\n");
      out.write("                alert('Passwords do not match!');\n");
      out.write("                return false;\n");
      out.write("            }\n");
      out.write("            \n");
      out.write("            return true;\n");
      out.write("        });\n");
      out.write("\n");
      out.write("        // Close modals when clicking outside\n");
      out.write("        window.onclick = function(event) {\n");
      out.write("            var adminModal = document.getElementById('adminModal');\n");
      out.write("            var passwordModal = document.getElementById('passwordModal');\n");
      out.write("            var deleteModal = document.getElementById('deleteModal');\n");
      out.write("            \n");
      out.write("            if (event.target === adminModal) {\n");
      out.write("                closeModal();\n");
      out.write("            }\n");
      out.write("            if (event.target === passwordModal) {\n");
      out.write("                closePasswordModal();\n");
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
