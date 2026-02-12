package com.election.servlets;

import com.election.beans.Student;
import com.election.beans.Election;
import com.election.dao.VoteDAO;
import com.election.dao.ElectionDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class VoteServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Student student = (Student) session.getAttribute("student");
        
        if (student == null) {
            response.sendRedirect("../login.jsp");
            return;
        }
        
        try {

            int candidateId = Integer.parseInt(request.getParameter("candidateId"));
            int electionId = Integer.parseInt(request.getParameter("electionId"));
            
 
            VoteDAO voteDAO = new VoteDAO();
            ElectionDAO electionDAO = new ElectionDAO();
            
            if (voteDAO.hasStudentVoted(student.getStudentId(), electionId)) {
                request.setAttribute("errorMessage", "You have already voted in this election!");
                request.getRequestDispatcher("student/vote.jsp").forward(request, response);
                return;
            }

            boolean success = voteDAO.recordVote(student.getStudentId(), electionId, candidateId);
            
            if (success) {
                student.setHasVoted(true);
                session.setAttribute("student", student);

                response.sendRedirect("student/thankyou.jsp");
            } else {
                request.setAttribute("errorMessage", "Failed to record vote. Please try again.");
                request.getRequestDispatcher("student/vote.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid vote data.");
            request.getRequestDispatcher("student/vote.jsp").forward(request, response);
        } catch (IOException | ServletException e) {
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("student/vote.jsp").forward(request, response);
        }
    }
}
