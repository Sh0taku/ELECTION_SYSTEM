package com.election.servlets;

import com.election.beans.Admin;
import com.election.dao.AdminDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminLoginServlet2 extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        

        System.out.println("AdminLoginServlet2: Starting...");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        System.out.println("Username received: " + username);
        
        
        if (username == null || password == null) {
            System.out.println("ERROR: Username or password is null");
            request.setAttribute("errorMessage", "Please enter username and password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        
        username = username.trim();
        password = password.trim();
        
        
        AdminDAO adminDAO = new AdminDAO();
        System.out.println("Calling AdminDAO.login()...");
        
        Admin admin = adminDAO.login(username, password);
        
        
        if (admin != null) {
            System.out.println("SUCCESS: Admin found - " + admin.getUsername());
            
            HttpSession session = request.getSession();
            session.setAttribute("admin", admin);
            
            System.out.println("Redirecting to admin dashboard...");
            response.sendRedirect("admin/admindashboard.jsp");
        } else {
            System.out.println("FAILED: Invalid credentials");

            request.setAttribute("errorMessage", "Invalid admin username or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("AdminLoginServlet2: GET request received, redirecting...");
        response.sendRedirect("login.jsp");
    }
}