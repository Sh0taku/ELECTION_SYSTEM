package org.apache.jsp.admin;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.election.beans.Admin;
import com.election.beans.Student;
import java.util.*;
import java.sql.*;
import com.election.dao.DBConnection;

public final class studenttab_jsp extends org.apache.jasper.runtime.HttpJspBase
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
String studentId = request.getParameter("studentId");

Connection conn = null;
PreparedStatement pstmt = null;
Statement stmt = null;
ResultSet rs = null;

if (action != null) {
    try {
        conn = DBConnection.getConnection();
        
        if ("delete".equals(action) && studentId != null) {
            // DELETE STUDENT (check if candidate first)
            String checkCandidate = "SELECT CANDIDATE_STATUS FROM STUDENTS WHERE STUDENT_ID = ?";
            pstmt = conn.prepareStatement(checkCandidate);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String candidateStatus = rs.getString("CANDIDATE_STATUS");
                if ("CANDIDATE".equals(candidateStatus)) {
                    message = "Cannot delete a candidate! Remove candidate status first.";
                    messageType = "error";
                } else {
                    String sql = "DELETE FROM STUDENTS WHERE STUDENT_ID = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, studentId);
                    
                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        message = "Student deleted successfully!";
                        messageType = "success";
                    }
                }
            }
            
        } else if ("add".equals(action)) {
            // ADD NEW STUDENT
            String newStudentId = request.getParameter("studentId");
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String faculty = request.getParameter("faculty");
            String candidateStatus = request.getParameter("candidateStatus");
            
            // Check if student already exists
            String checkSql = "SELECT STUDENT_ID FROM STUDENTS WHERE STUDENT_ID = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, newStudentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                message = "Student ID already exists!";
                messageType = "error";
            } else {
                String insertSql = "INSERT INTO STUDENTS (STUDENT_ID, NAME, EMAIL, PASSWORD, FACULTY, HAS_VOTED, CANDIDATE_STATUS) VALUES (?, ?, ?, ?, ?, 0, ?)";
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setString(1, newStudentId);
                pstmt.setString(2, name);
                pstmt.setString(3, email);
                pstmt.setString(4, password);
                pstmt.setString(5, faculty);
                pstmt.setString(6, candidateStatus);
                
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    message = "Student added successfully!";
                    messageType = "success";
                }
            }
            
        } else if ("update".equals(action)) {
            // UPDATE STUDENT
            String updateStudentId = request.getParameter("studentId");
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String faculty = request.getParameter("faculty");
            String candidateStatus = request.getParameter("candidateStatus");
            
            String updateSql = "UPDATE STUDENTS SET NAME = ?, EMAIL = ?, FACULTY = ?, CANDIDATE_STATUS = ? WHERE STUDENT_ID = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, faculty);
            pstmt.setString(4, candidateStatus);
            pstmt.setString(5, updateStudentId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Student updated successfully!";
                messageType = "success";
            }
        } else if ("resetPassword".equals(action) && studentId != null) {
            // RESET PASSWORD
            String newPassword = request.getParameter("newPassword");
            
            String updateSql = "UPDATE STUDENTS SET PASSWORD = ? WHERE STUDENT_ID = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, newPassword);
            pstmt.setString(2, studentId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                message = "Password reset successfully!";
                messageType = "success";
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

// Fetch students for display
List<Student> students = new ArrayList<Student>();

try {
    conn = DBConnection.getConnection();
    stmt = conn.createStatement();
    
    // Get all students
    String studentSql = "SELECT * FROM STUDENTS ORDER BY STUDENT_ID";
    rs = stmt.executeQuery(studentSql);
    
    while (rs.next()) {
        Student student = new Student();
        student.setStudentId(rs.getString("STUDENT_ID"));
        student.setName(rs.getString("NAME"));
        student.setEmail(rs.getString("EMAIL"));
        student.setFaculty(rs.getString("FACULTY"));
        student.setHasVoted(rs.getBoolean("HAS_VOTED"));
        student.setCandidateStatus(rs.getString("CANDIDATE_STATUS"));
        students.add(student);
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
      out.write("    <title>Manage Students - UITM Election System</title>\n");
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
      out.write("        /* Status badges */\n");
      out.write("        .badge {\n");
      out.write("            padding: 4px 8px;\n");
      out.write("            border-radius: 12px;\n");
      out.write("            font-size: 11px;\n");
      out.write("            font-weight: bold;\n");
      out.write("            display: inline-block;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        .badge-voted {\n");
      out.write("            background-color: #e1f7e1;\n");
      out.write("            color: #2e7d32;\n");
      out.write("            border: 1px solid #2e7d32;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        .badge-not-voted {\n");
      out.write("            background-color: #fff3e0;\n");
      out.write("            color: #ef6c00;\n");
      out.write("            border: 1px solid #ef6c00;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        .badge-candidate {\n");
      out.write("            background-color: #cce5ff;\n");
      out.write("            color: #004085;\n");
      out.write("            border: 1px solid #004085;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        .badge-not-candidate {\n");
      out.write("            background-color: #e2e3e5;\n");
      out.write("            color: #383d41;\n");
      out.write("            border: 1px solid #383d41;\n");
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
      out.write("        .form-input, .form-select {\n");
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
      out.write("            <a href=\"studenttab.jsp\" class=\"nav-link active\">Manage Students</a>\n");
      out.write("            <a href=\"candidatetab.jsp\" class=\"nav-link\">Manage Candidates</a>\n");
      out.write("            <a href=\"electiontab.jsp\" class=\"nav-link\">Manage Elections</a>\n");
      out.write("            <a href=\"votetab.jsp\" class=\"nav-link\">Manage Votes</a>\n");
      out.write("            <a href=\"admintab.jsp\" class=\"nav-link\">Manage Admins</a>\n");
      out.write("            <a href=\"resulttab.jsp\" class=\"nav-link\">Current Results</a>\n");
      out.write("            <a href=\"../LogoutServlet\" class=\"nav-link\" onclick=\"return confirm('Logout?')\">Logout</a>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <!-- Main Content -->\n");
      out.write("    <div class=\"main-content\">\n");
      out.write("        <!-- Top Header -->\n");
      out.write("        <div class=\"top-header\">\n");
      out.write("            <div class=\"page-title\">Manage Students</div>\n");
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
      out.write("                    <input type=\"text\" class=\"search-input\" placeholder=\"Search students...\" id=\"searchInput\">\n");
      out.write("                    <button class=\"search-btn\" onclick=\"searchStudents()\">Search</button>\n");
      out.write("                </div>\n");
      out.write("                <button class=\"add-btn\" onclick=\"openAddModal()\">+ Add New Student</button>\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            <!-- Data Table -->\n");
      out.write("            <div class=\"data-table\">\n");
      out.write("                <div class=\"table-header\">\n");
      out.write("                    <div class=\"table-title\">Students List (");
      out.print( students.size() );
      out.write(" students)</div>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div style=\"overflow-x: auto;\">\n");
      out.write("                    <table>\n");
      out.write("                        <thead>\n");
      out.write("                            <tr>\n");
      out.write("                                <th>Student ID</th>\n");
      out.write("                                <th>Name</th>\n");
      out.write("                                <th>Email</th>\n");
      out.write("                                <th>Faculty</th>\n");
      out.write("                                <th>Voted</th>\n");
      out.write("                                <th>Candidate Status</th>\n");
      out.write("                                <th>Actions</th>\n");
      out.write("                            </tr>\n");
      out.write("                        </thead>\n");
      out.write("                        <tbody id=\"studentTableBody\">\n");
      out.write("                            ");
 
                            for (Student student : students) { 
                                String votedClass = student.isHasVoted() ? "badge-voted" : "badge-not-voted";
                                String votedText = student.isHasVoted() ? "Voted" : "Not Voted";
                                
                                String candidateClass = "CANDIDATE".equals(student.getCandidateStatus()) ? "badge-candidate" : "badge-not-candidate";
                                String candidateText = student.getCandidateStatus() != null ? student.getCandidateStatus() : "NOT_CANDIDATE";
                            
      out.write("\n");
      out.write("                            <tr>\n");
      out.write("                                <td><strong>");
      out.print( student.getStudentId() );
      out.write("</strong></td>\n");
      out.write("                                <td>");
      out.print( student.getName() );
      out.write("</td>\n");
      out.write("                                <td>");
      out.print( student.getEmail() );
      out.write("</td>\n");
      out.write("                                <td>");
      out.print( student.getFaculty() );
      out.write("</td>\n");
      out.write("                                <td>\n");
      out.write("                                    <span class=\"badge ");
      out.print( votedClass );
      out.write("\">\n");
      out.write("                                        ");
      out.print( votedText );
      out.write("\n");
      out.write("                                    </span>\n");
      out.write("                                </td>\n");
      out.write("                                <td>\n");
      out.write("                                    <span class=\"badge ");
      out.print( candidateClass );
      out.write("\">\n");
      out.write("                                        ");
      out.print( candidateText );
      out.write("\n");
      out.write("                                    </span>\n");
      out.write("                                </td>\n");
      out.write("                                <td class=\"actions-cell\">\n");
      out.write("                                    <button class=\"edit-btn\" onclick=\"editStudent(\n");
      out.write("                                        '");
      out.print( student.getStudentId() );
      out.write("', \n");
      out.write("                                        '");
      out.print( student.getName().replace("'", "\\'") );
      out.write("', \n");
      out.write("                                        '");
      out.print( student.getEmail() != null ? student.getEmail().replace("'", "\\'") : "" );
      out.write("',\n");
      out.write("                                        '");
      out.print( student.getFaculty() != null ? student.getFaculty().replace("'", "\\'") : "" );
      out.write("',\n");
      out.write("                                        '");
      out.print( student.getCandidateStatus() != null ? student.getCandidateStatus().replace("'", "\\'") : "NOT_CANDIDATE" );
      out.write("'\n");
      out.write("                                    )\">Edit</button>\n");
      out.write("                                    <button class=\"password-btn\" onclick=\"resetPassword('");
      out.print( student.getStudentId() );
      out.write("', '");
      out.print( student.getName().replace("'", "\\'") );
      out.write("')\">Reset Password</button>\n");
      out.write("                                    <button class=\"delete-btn\" onclick=\"deleteStudent('");
      out.print( student.getStudentId() );
      out.write("', '");
      out.print( student.getName().replace("'", "\\'") );
      out.write("')\">Delete</button>\n");
      out.write("                                </td>\n");
      out.write("                            </tr>\n");
      out.write("                            ");
 } 
      out.write("\n");
      out.write("                            \n");
      out.write("                            ");
 if (students.isEmpty()) { 
      out.write("\n");
      out.write("                            <tr>\n");
      out.write("                                <td colspan=\"7\" class=\"no-data\">\n");
      out.write("                                    No students found. Add new students using the \"Add New Student\" button.\n");
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
      out.write("    <!-- Add/Edit Student Modal -->\n");
      out.write("    <div id=\"studentModal\" class=\"modal\">\n");
      out.write("        <div class=\"modal-content\">\n");
      out.write("            <h2 class=\"modal-title\" id=\"modalTitle\">Add New Student</h2>\n");
      out.write("            <form id=\"studentForm\" method=\"POST\" action=\"studenttab.jsp\">\n");
      out.write("                <input type=\"hidden\" id=\"actionType\" name=\"action\" value=\"add\">\n");
      out.write("                <input type=\"hidden\" id=\"editStudentId\" name=\"studentId\">\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Student ID:</label>\n");
      out.write("                    <input type=\"text\" id=\"studentId\" name=\"studentId\" class=\"form-input\" required \n");
      out.write("                           placeholder=\"e.g., 2023123456\">\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Full Name:</label>\n");
      out.write("                    <input type=\"text\" id=\"name\" name=\"name\" class=\"form-input\" required\n");
      out.write("                           placeholder=\"e.g., Ali bin Ahmad\">\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Email:</label>\n");
      out.write("                    <input type=\"email\" id=\"email\" name=\"email\" class=\"form-input\"\n");
      out.write("                           placeholder=\"e.g., 2023123456@student.uitm.edu.my\">\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-row\">\n");
      out.write("                    <div class=\"form-group\">\n");
      out.write("                        <label class=\"form-label\">Password:</label>\n");
      out.write("                        <input type=\"password\" id=\"password\" name=\"password\" class=\"form-input\" required\n");
      out.write("                               placeholder=\"Set password for student\">\n");
      out.write("                    </div>\n");
      out.write("                    <div class=\"form-group\">\n");
      out.write("                        <label class=\"form-label\">Faculty:</label>\n");
      out.write("                        <select id=\"faculty\" name=\"faculty\" class=\"form-select\" required>\n");
      out.write("                            <option value=\"\">Select Faculty</option>\n");
      out.write("                            <option value=\"FCSIT\">Faculty of Computer Science & IT</option>\n");
      out.write("                            <option value=\"FKE\">Faculty of Engineering</option>\n");
      out.write("                            <option value=\"FBM\">Faculty of Business Management</option>\n");
      out.write("                            <option value=\"FSSH\">Faculty of Social Sciences</option>\n");
      out.write("                            <option value=\"FSPU\">Faculty of Sports Science</option>\n");
      out.write("                            <option value=\"OTHER\">Other</option>\n");
      out.write("                        </select>\n");
      out.write("                    </div>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Candidate Status:</label>\n");
      out.write("                    <select id=\"candidateStatus\" name=\"candidateStatus\" class=\"form-select\">\n");
      out.write("                        <option value=\"NOT_CANDIDATE\">Not a Candidate</option>\n");
      out.write("                        <option value=\"CANDIDATE\">Candidate</option>\n");
      out.write("                        <option value=\"PENDING\">Pending</option>\n");
      out.write("                    </select>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"modal-buttons\">\n");
      out.write("                    <button type=\"submit\" class=\"save-btn\">Save Student</button>\n");
      out.write("                    <button type=\"button\" class=\"cancel-btn\" onclick=\"closeModal()\">Cancel</button>\n");
      out.write("                </div>\n");
      out.write("            </form>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <!-- Reset Password Modal -->\n");
      out.write("    <div id=\"passwordModal\" class=\"modal\">\n");
      out.write("        <div class=\"modal-content\">\n");
      out.write("            <h2 class=\"modal-title\">Reset Password</h2>\n");
      out.write("            <p style=\"color: #112D4E; margin-bottom: 15px;\" id=\"passwordMessage\">\n");
      out.write("                Enter new password for student:\n");
      out.write("            </p>\n");
      out.write("            \n");
      out.write("            <form id=\"passwordForm\" method=\"POST\" action=\"studenttab.jsp\">\n");
      out.write("                <input type=\"hidden\" name=\"action\" value=\"resetPassword\">\n");
      out.write("                <input type=\"hidden\" id=\"passwordStudentId\" name=\"studentId\">\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">New Password:</label>\n");
      out.write("                    <input type=\"password\" id=\"newPassword\" name=\"newPassword\" class=\"form-input\" required\n");
      out.write("                           placeholder=\"Enter new password\">\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div class=\"form-group\">\n");
      out.write("                    <label class=\"form-label\">Confirm Password:</label>\n");
      out.write("                    <input type=\"password\" id=\"confirmPassword\" class=\"form-input\" required\n");
      out.write("                           placeholder=\"Confirm new password\">\n");
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
      out.write("                Are you sure you want to delete this student?\n");
      out.write("            </p>\n");
      out.write("            <form id=\"deleteForm\" method=\"POST\" action=\"studenttab.jsp\">\n");
      out.write("                <input type=\"hidden\" name=\"action\" value=\"delete\">\n");
      out.write("                <input type=\"hidden\" id=\"deleteStudentId\" name=\"studentId\">\n");
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
      out.write("            document.getElementById('modalTitle').textContent = 'Add New Student';\n");
      out.write("            document.getElementById('actionType').value = 'add';\n");
      out.write("            document.getElementById('studentForm').reset();\n");
      out.write("            document.getElementById('studentModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function editStudent(studentId, name, email, faculty, candidateStatus) {\n");
      out.write("            document.getElementById('modalTitle').textContent = 'Edit Student';\n");
      out.write("            document.getElementById('actionType').value = 'update';\n");
      out.write("            document.getElementById('editStudentId').value = studentId;\n");
      out.write("            document.getElementById('studentId').value = studentId;\n");
      out.write("            document.getElementById('studentId').readOnly = true;\n");
      out.write("            document.getElementById('name').value = name;\n");
      out.write("            document.getElementById('email').value = email || '';\n");
      out.write("            document.getElementById('faculty').value = faculty || '';\n");
      out.write("            document.getElementById('candidateStatus').value = candidateStatus || 'NOT_CANDIDATE';\n");
      out.write("            document.getElementById('password').removeAttribute('required');\n");
      out.write("            document.getElementById('password').placeholder = 'Leave blank to keep current password';\n");
      out.write("            \n");
      out.write("            document.getElementById('studentModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function resetPassword(studentId, studentName) {\n");
      out.write("            document.getElementById('passwordMessage').innerHTML = \n");
      out.write("                'Enter new password for student:<br><strong>' + studentName + '</strong>';\n");
      out.write("            document.getElementById('passwordStudentId').value = studentId;\n");
      out.write("            document.getElementById('passwordForm').reset();\n");
      out.write("            document.getElementById('passwordModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function closeModal() {\n");
      out.write("            document.getElementById('studentModal').style.display = 'none';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function closePasswordModal() {\n");
      out.write("            document.getElementById('passwordModal').style.display = 'none';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function deleteStudent(studentId, studentName) {\n");
      out.write("            document.getElementById('deleteMessage').innerHTML = \n");
      out.write("                'Are you sure you want to delete student:<br><br>' +\n");
      out.write("                '<strong>' + studentName + '</strong><br>' +\n");
      out.write("                'ID: ' + studentId + '<br><br>' +\n");
      out.write("                'Warning: This action cannot be undone!';\n");
      out.write("            document.getElementById('deleteStudentId').value = studentId;\n");
      out.write("            document.getElementById('deleteModal').style.display = 'flex';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        function closeDeleteModal() {\n");
      out.write("            document.getElementById('deleteModal').style.display = 'none';\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        // Search function\n");
      out.write("        function searchStudents() {\n");
      out.write("            var searchTerm = document.getElementById('searchInput').value.toLowerCase();\n");
      out.write("            var rows = document.getElementById('studentTableBody').getElementsByTagName('tr');\n");
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
      out.write("        document.getElementById('studentForm').addEventListener('submit', function(e) {\n");
      out.write("            var studentId = document.getElementById('studentId').value;\n");
      out.write("            var name = document.getElementById('name').value;\n");
      out.write("            \n");
      out.write("            if (!studentId.trim() || !name.trim()) {\n");
      out.write("                e.preventDefault();\n");
      out.write("                alert('Please fill in all required fields!');\n");
      out.write("                return false;\n");
      out.write("            }\n");
      out.write("            \n");
      out.write("          \n");
      out.write("            \n");
      out.write("            return true;\n");
      out.write("        });\n");
      out.write("\n");
      out.write("        document.getElementById('passwordForm').addEventListener('submit', function(e) {\n");
      out.write("            var newPassword = document.getElementById('newPassword').value;\n");
      out.write("            var confirmPassword = document.getElementById('confirmPassword').value;\n");
      out.write("            \n");
      out.write("            if (newPassword.length < 6) {\n");
      out.write("                e.preventDefault();\n");
      out.write("                alert('Password must be at least 6 characters!');\n");
      out.write("                return false;\n");
      out.write("            }\n");
      out.write("            \n");
      out.write("            if (newPassword !== confirmPassword) {\n");
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
      out.write("            var studentModal = document.getElementById('studentModal');\n");
      out.write("            var passwordModal = document.getElementById('passwordModal');\n");
      out.write("            var deleteModal = document.getElementById('deleteModal');\n");
      out.write("            \n");
      out.write("            if (event.target === studentModal) {\n");
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
