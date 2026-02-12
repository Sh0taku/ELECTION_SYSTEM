<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.election.dao.CandidateDAO" %>
<%@page import="com.election.beans.Candidate" %>
<%
    String candidateIdStr = request.getParameter("candidateId");
    if (candidateIdStr != null) {
        try {
            int candidateId = Integer.parseInt(candidateIdStr);
            CandidateDAO candidateDAO = new CandidateDAO();
            Candidate candidate = candidateDAO.getCandidateById(candidateId);
            
            if (candidate != null) {
                String candidateName = candidate.getCandidateName();
                if (candidateName == null || candidateName.isEmpty()) {
                    candidateName = "Candidate " + candidate.getCandidateId();
                }
%>
                <p><strong>Name:</strong> <%= candidateName %></p>
                <p><strong>Student ID:</strong> <%= candidate.getStudentId() %></p>
                <p><strong>Position:</strong> <%= candidate.getPosition() %></p>
                <p><strong>Manifesto:</strong></p>
                <p><%= candidate.getManifesto() != null ? candidate.getManifesto() : "No manifesto provided." %></p>
                <p><strong>Current Votes:</strong> <%= candidate.getVoteCount() %></p>
<%
            } else {
                out.print("<p>Candidate not found.</p>");
            }
        } catch (NumberFormatException e) {
            out.print("<p>Invalid candidate ID.</p>");
        }
    } else {
        out.print("<p>No candidate specified.</p>");
    }
%>