package org.apache.jsp.admin;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.election.beans.Admin;
import com.election.dao.StudentDAO;
import com.election.dao.ElectionDAO;
import com.election.dao.CandidateDAO;
import com.election.beans.Election;
import com.election.beans.Candidate;
import java.util.*;

public final class admindashboard_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("\n");

// Check admin session
Admin admin = (Admin) session.getAttribute("admin");
if (admin == null) {
    response.sendRedirect("../login.jsp");
    return;
}

StudentDAO studentDAO = new StudentDAO();
ElectionDAO electionDAO = new ElectionDAO();
CandidateDAO candidateDAO = new CandidateDAO();

// Get data
int totalStudents = studentDAO.getTotalStudents();
List allElections = electionDAO.getAllElections();

// Count elections
int ongoing = 0, upcoming = 0, ended = 0;
Election ongoingElection = null;

for (int i = 0; i < allElections.size(); i++) {
    Election e = (Election) allElections.get(i);
    String status = e.getStatus();
    if ("ONGOING".equals(status)) {
        ongoing++;
        ongoingElection = e;
    } else if ("UPCOMING".equals(status)) {
        upcoming++;
    } else if ("ENDED".equals(status)) {
        ended++;
    }
}

// Get candidates for ongoing election
List candidates = new ArrayList();
int totalVotes = 0;

if (ongoingElection != null) {
    candidates = candidateDAO.getCandidatesByElection(ongoingElection.getElectionId());
    for (int i = 0; i < candidates.size(); i++) {
        Candidate c = (Candidate) candidates.get(i);
        totalVotes += c.getVoteCount();
    }
}

      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("<head>\n");
      out.write("    <title>Admin Dashboard - UITM Election System</title>\n");
      out.write("    <style>\n");
      out.write("        /* Updated CSS to match studenttab.jsp layout */\n");
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
      out.write("        /* Left Navigation Panel - Matching studenttab.jsp */\n");
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
      out.write("        /* Main Content Area - Matching studenttab.jsp */\n");
      out.write("        .main-content {\n");
      out.write("            flex: 1;\n");
      out.write("            display: flex;\n");
      out.write("            flex-direction: column;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Top Header - Matching studenttab.jsp */\n");
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
      out.write("        /* Dashboard Content */\n");
      out.write("        .dashboard-content {\n");
      out.write("            padding: 30px;\n");
      out.write("            overflow-y: auto;\n");
      out.write("            flex: 1;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Cards */\n");
      out.write("        .card {\n");
      out.write("            background: white;\n");
      out.write("            border-radius: 10px;\n");
      out.write("            padding: 20px;\n");
      out.write("            margin-bottom: 20px;\n");
      out.write("            box-shadow: 0 2px 5px rgba(0,0,0,0.1);\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .card h2 {\n");
      out.write("            color: #112D4E;\n");
      out.write("            margin-bottom: 15px;\n");
      out.write("            font-size: 18px;\n");
      out.write("            border-bottom: 2px solid #DBE2EF;\n");
      out.write("            padding-bottom: 10px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Stats */\n");
      out.write("        .stats {\n");
      out.write("            display: grid;\n");
      out.write("            grid-template-columns: repeat(3, 1fr);\n");
      out.write("            gap: 20px;\n");
      out.write("            margin-bottom: 20px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .stat-box {\n");
      out.write("            background: white;\n");
      out.write("            padding: 20px;\n");
      out.write("            text-align: center;\n");
      out.write("            border-radius: 8px;\n");
      out.write("            box-shadow: 0 2px 5px rgba(0,0,0,0.1);\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .stat-number {\n");
      out.write("            font-size: 24px;\n");
      out.write("            color: #3F72AF;\n");
      out.write("            font-weight: bold;\n");
      out.write("            margin-bottom: 5px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .stat-label {\n");
      out.write("            color: #112D4E;\n");
      out.write("            font-size: 14px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Tables */\n");
      out.write("        .data-table {\n");
      out.write("            width: 100%;\n");
      out.write("            border-collapse: collapse;\n");
      out.write("            margin-top: 10px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .data-table th {\n");
      out.write("            background: #DBE2EF;\n");
      out.write("            color: #112D4E;\n");
      out.write("            text-align: left;\n");
      out.write("            padding: 10px;\n");
      out.write("            font-weight: bold;\n");
      out.write("            border-bottom: 2px solid #3F72AF;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .data-table td {\n");
      out.write("            padding: 10px;\n");
      out.write("            border-bottom: 1px solid #F9F7F7;\n");
      out.write("            color: #112D4E;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .data-table tr:hover {\n");
      out.write("            background: #F9F7F7;\n");
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
      out.write("        /* Buttons */\n");
      out.write("        .btn {\n");
      out.write("            display: inline-block;\n");
      out.write("            background: #3F72AF;\n");
      out.write("            color: white;\n");
      out.write("            border: none;\n");
      out.write("            padding: 8px 15px;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            cursor: pointer;\n");
      out.write("            text-decoration: none;\n");
      out.write("            font-size: 14px;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        .btn:hover {\n");
      out.write("            background: #112D4E;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        .btn-small {\n");
      out.write("            padding: 5px 10px;\n");
      out.write("            font-size: 12px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* Welcome Section */\n");
      out.write("        .welcome-section {\n");
      out.write("            margin-bottom: 30px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .welcome-title {\n");
      out.write("            color: #112D4E;\n");
      out.write("            font-size: 32px;\n");
      out.write("            margin-bottom: 5px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        .welcome-subtitle {\n");
      out.write("            color: #3F72AF;\n");
      out.write("            font-size: 16px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        /* System Info */\n");
      out.write("        .info-box {\n");
      out.write("            background-color: #F9F7F7;\n");
      out.write("            padding: 15px;\n");
      out.write("            border-radius: 5px;\n");
      out.write("            margin-top: 20px;\n");
      out.write("            font-size: 14px;\n");
      out.write("            color: #666;\n");
      out.write("            border: 1px solid #DBE2EF;\n");
      out.write("        }\n");
      out.write("    </style>\n");
      out.write("</head>\n");
      out.write("<body>\n");
      out.write("    <!-- Left Navigation Panel - Matching studenttab.jsp -->\n");
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
      out.write("            <a href=\"admindashboard.jsp\" class=\"nav-link active\">Dashboard</a>\n");
      out.write("            <a href=\"studenttab.jsp\" class=\"nav-link\">Manage Students</a>\n");
      out.write("            <a href=\"candidatetab.jsp\" class=\"nav-link\">Manage Candidates</a>\n");
      out.write("            <a href=\"electiontab.jsp\" class=\"nav-link\">Manage Elections</a>\n");
      out.write("            <a href=\"votetab.jsp\" class=\"nav-link\">Manage Votes</a>\n");
      out.write("            <a href=\"admintab.jsp\" class=\"nav-link\">Manage Admins</a>\n");
      out.write("            <a href=\"resulttab.jsp\" class=\"nav-link\">Current Results</a>\n");
      out.write("            <a href=\"../LogoutServlet\" class=\"nav-link\" onclick=\"return confirm('Logout?')\">Logout</a>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("    <!-- Main Content - Matching studenttab.jsp -->\n");
      out.write("    <div class=\"main-content\">\n");
      out.write("        <!-- Top Header - Matching studenttab.jsp -->\n");
      out.write("        <div class=\"top-header\">\n");
      out.write("            <div class=\"page-title\">Admin Dashboard</div>\n");
      out.write("            <button class=\"logout-btn\" onclick=\"if(confirm('Logout?')) window.location.href='../LogoutServlet'\">Logout</button>\n");
      out.write("        </div>\n");
      out.write("\n");
      out.write("        <!-- Dashboard Content -->\n");
      out.write("        <div class=\"dashboard-content\">\n");
      out.write("            <!-- Welcome Section -->\n");
      out.write("            <div class=\"welcome-section\">\n");
      out.write("                <h1 class=\"welcome-title\">Welcome, ");
      out.print( admin.getUsername() );
      out.write("!</h1>\n");
      out.write("                <p class=\"welcome-subtitle\">UITM Election System Administration Panel</p>\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            <!-- Quick Stats -->\n");
      out.write("            <div class=\"stats\">\n");
      out.write("                <div class=\"stat-box\">\n");
      out.write("                    <div class=\"stat-number\">");
      out.print( totalStudents );
      out.write("</div>\n");
      out.write("                    <div class=\"stat-label\">Total Students</div>\n");
      out.write("                </div>\n");
      out.write("                <div class=\"stat-box\">\n");
      out.write("                    <div class=\"stat-number\">");
      out.print( allElections.size() );
      out.write("</div>\n");
      out.write("                    <div class=\"stat-label\">Total Elections</div>\n");
      out.write("                </div>\n");
      out.write("                <div class=\"stat-box\">\n");
      out.write("                    <div class=\"stat-number\">");
      out.print( ongoing );
      out.write("</div>\n");
      out.write("                    <div class=\"stat-label\">Ongoing Elections</div>\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            <!-- Ongoing Election -->\n");
      out.write("            ");
 if (ongoingElection != null) { 
      out.write("\n");
      out.write("            <div class=\"card\">\n");
      out.write("                <h2>Current Election: ");
      out.print( ongoingElection.getTitle() );
      out.write("</h2>\n");
      out.write("                <p style=\"color: #3F72AF; margin-bottom: 20px;\">");
      out.print( ongoingElection.getDescription() );
      out.write("</p>\n");
      out.write("                \n");
      out.write("                ");
 if (!candidates.isEmpty()) { 
      out.write("\n");
      out.write("                <table class=\"data-table\">\n");
      out.write("                    <thead>\n");
      out.write("                        <tr>\n");
      out.write("                            <th>Candidate</th>\n");
      out.write("                            <th>Position</th>\n");
      out.write("                            <th>Votes</th>\n");
      out.write("                        </tr>\n");
      out.write("                    </thead>\n");
      out.write("                    <tbody>\n");
      out.write("                        ");
 for (int i = 0; i < candidates.size(); i++) { 
                            Candidate c = (Candidate) candidates.get(i);
                            String name = c.getCandidateName();
                            if (name == null || name.isEmpty()) {
                                name = "Candidate " + c.getCandidateId();
                            }
                        
      out.write("\n");
      out.write("                        <tr>\n");
      out.write("                            <td><strong>");
      out.print( name );
      out.write("</strong></td>\n");
      out.write("                            <td>");
      out.print( c.getPosition() );
      out.write("</td>\n");
      out.write("                            <td><strong style=\"color: #3F72AF;\">");
      out.print( c.getVoteCount() );
      out.write("</strong></td>\n");
      out.write("                        </tr>\n");
      out.write("                        ");
 } 
      out.write("\n");
      out.write("                    </tbody>\n");
      out.write("                </table>\n");
      out.write("                <p style=\"margin-top: 15px; color: #3F72AF; text-align: center;\">\n");
      out.write("                    Total Votes: <strong>");
      out.print( totalVotes );
      out.write("</strong>\n");
      out.write("                </p>\n");
      out.write("                ");
 } else { 
      out.write("\n");
      out.write("                <p style=\"color: #3F72AF; text-align: center; padding: 20px;\">\n");
      out.write("                    No candidates yet for this election.\n");
      out.write("                </p>\n");
      out.write("                ");
 } 
      out.write("\n");
      out.write("            </div>\n");
      out.write("            ");
 } 
      out.write("\n");
      out.write("\n");
      out.write("            <!-- All Elections -->\n");
      out.write("            <div class=\"card\">\n");
      out.write("                <h2>All Elections</h2>\n");
      out.write("                ");
 if (!allElections.isEmpty()) { 
      out.write("\n");
      out.write("                <table class=\"data-table\">\n");
      out.write("                    <thead>\n");
      out.write("                        <tr>\n");
      out.write("                            <th>Title</th>\n");
      out.write("                            <th>Dates</th>\n");
      out.write("                            <th>Status</th>\n");
      out.write("                        </tr>\n");
      out.write("                    </thead>\n");
      out.write("                    <tbody>\n");
      out.write("                        ");
 for (int i = 0; i < allElections.size(); i++) { 
                            Election e = (Election) allElections.get(i);
                            String badgeClass = "";
                            if ("ONGOING".equals(e.getStatus())) badgeClass = "badge-ongoing";
                            else if ("UPCOMING".equals(e.getStatus())) badgeClass = "badge-upcoming";
                            else if ("ENDED".equals(e.getStatus())) badgeClass = "badge-ended";
                        
      out.write("\n");
      out.write("                        <tr>\n");
      out.write("                            <td><strong>");
      out.print( e.getTitle() );
      out.write("</strong></td>\n");
      out.write("                            <td>\n");
      out.write("                                ");
 if (e.getStartDate() != null && e.getEndDate() != null) { 
      out.write("\n");
      out.write("                                ");
      out.print( e.getStartDate() );
      out.write(" to ");
      out.print( e.getEndDate() );
      out.write("\n");
      out.write("                                ");
 } else { 
      out.write("\n");
      out.write("                                <span style=\"color: #666;\">Dates not set</span>\n");
      out.write("                                ");
 } 
      out.write("\n");
      out.write("                            </td>\n");
      out.write("                            <td>\n");
      out.write("                                <span class=\"badge ");
      out.print( badgeClass );
      out.write("\">\n");
      out.write("                                    ");
      out.print( e.getStatus() );
      out.write("\n");
      out.write("                                </span>\n");
      out.write("                            </td>\n");
      out.write("                        </tr>\n");
      out.write("                        ");
 } 
      out.write("\n");
      out.write("                    </tbody>\n");
      out.write("                </table>\n");
      out.write("                ");
 } else { 
      out.write("\n");
      out.write("                <p style=\"color: #3F72AF; text-align: center; padding: 20px;\">\n");
      out.write("                    No elections created yet.\n");
      out.write("                </p>\n");
      out.write("                ");
 } 
      out.write("\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            <!-- Quick Actions -->\n");
      out.write("            <div class=\"card\">\n");
      out.write("                <h2>Quick Actions</h2>\n");
      out.write("                <div style=\"display: flex; gap: 10px; flex-wrap: wrap; margin-top: 10px;\">\n");
      out.write("                    <a href=\"studenttab.jsp\" class=\"btn\">Manage Students</a>\n");
      out.write("                    <a href=\"candidatetab.jsp\" class=\"btn\">Manage Candidates</a>\n");
      out.write("                    <a href=\"electiontab.jsp\" class=\"btn\">Create Election</a>\n");
      out.write("                    <a href=\"resulttab.jsp\" class=\"btn\">View Results</a>\n");
      out.write("                </div>\n");
      out.write("            </div>\n");
      out.write("\n");
      out.write("            <!-- System Info -->\n");
      out.write("            <div class=\"info-box\">\n");
      out.write("                <strong>System Information:</strong><br>\n");
      out.write("                Server Time: ");
      out.print( new java.util.Date() );
      out.write(" | \n");
      out.write("                Database: Apache Derby (ElectionDB) | \n");
      out.write("                Server: GlassFish\n");
      out.write("            </div>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
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
