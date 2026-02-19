/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.election.servlets;

import com.election.beans.Student;
import com.election.dao.StudentDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Emir
 */
public class StudentLoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String studentId = request.getParameter("studentId");
        String password = request.getParameter("password");

        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.login(studentId, password);

        if (student != null) {
           
            HttpSession session = request.getSession();
            session.setAttribute("student", student);
            
            
            response.sendRedirect("student/studentdashboard.jsp");
        } else {
          
            request.setAttribute("errorMessage", "Invalid student ID or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
