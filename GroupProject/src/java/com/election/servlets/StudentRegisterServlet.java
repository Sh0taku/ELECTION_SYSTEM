package com.election.servlets;

import com.election.beans.Student;
import com.election.dao.StudentDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Emir
 */
public class StudentRegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
     
        String studentId = request.getParameter("studentId");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String faculty = request.getParameter("faculty");
        
  
        Student student = new Student();
        student.setStudentId(studentId);
        student.setName(name);
        student.setEmail(email);
        student.setPassword(password);
        student.setFaculty(faculty);
        student.setHasVoted(false); // New students haven't voted yet
        
   
        StudentDAO studentDAO = new StudentDAO();
        boolean success = studentDAO.register(student);
        
     
        if (success) {
          
            request.setAttribute("successMessage", "Registration successful! Please login.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Registration failed. Student ID may already exist.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}

