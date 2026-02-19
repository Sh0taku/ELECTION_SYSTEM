package com.election.dao;

/**
 *
 * @author Luqman
 */
import com.election.beans.Vote;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoteDAO {
    
    
    

    public boolean hasStudentVoted(String studentId, int electionId) {
    String sql = "SELECT vote_id FROM votes WHERE student_id = ? AND election_id = ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, studentId);
        pstmt.setInt(2, electionId);
        ResultSet rs = pstmt.executeQuery();
        return rs.next();
        
    } catch (SQLException e) {
        System.out.println("Error checking vote: " + e.getMessage());
        return false;
    }
}
    
public List<Vote> getVotingHistory(String studentId) {
    List<Vote> votes = new ArrayList<>();
    String sql = "SELECT * FROM votes WHERE student_id = ? ORDER BY vote_time DESC";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, studentId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Vote vote = new Vote(
                rs.getInt("vote_id"),
                rs.getString("student_id"),
                rs.getInt("election_id"),
                rs.getInt("candidate_id"),
                rs.getTimestamp("vote_time")
            );
            votes.add(vote);
        }
    } catch (SQLException e) {
        System.out.println("Error getting voting history: " + e.getMessage());
    }
    return votes;
}


    /**
     *
     * @param vote
     * @return
     */
public boolean recordVote(String studentId, int electionId, int candidateId) {
    Connection conn = null;
    try {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false); 
        

        String sql1 = "INSERT INTO votes (student_id, election_id, candidate_id, vote_time) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        PreparedStatement pstmt1 = conn.prepareStatement(sql1);
        pstmt1.setString(1, studentId);
        pstmt1.setInt(2, electionId);
        pstmt1.setInt(3, candidateId);
        int rows1 = pstmt1.executeUpdate();

        String sql2 = "UPDATE candidates SET vote_count = vote_count + 1 WHERE candidate_id = ?";
        PreparedStatement pstmt2 = conn.prepareStatement(sql2);
        pstmt2.setInt(1, candidateId);
        int rows2 = pstmt2.executeUpdate();
        
 
        String sql3 = "UPDATE students SET has_voted = 1 WHERE student_id = ?";
        PreparedStatement pstmt3 = conn.prepareStatement(sql3);
        pstmt3.setString(1, studentId);
        int rows3 = pstmt3.executeUpdate();
        
        conn.commit(); 
        return (rows1 > 0 && rows2 > 0);
        
    } catch (SQLException e) {
        System.out.println("Error recording vote: " + e.getMessage());
        e.printStackTrace();
        if (conn != null) {
            try {
                conn.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
    
    // Get vote count for an election

    /**
     *
     * @param electionId
     * @return
     */
    public int getVoteCount(int electionId) {
        String sql = "SELECT COUNT(*) FROM votes WHERE election_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting vote count: " + e.getMessage());
        }
        return 0;
    }

    
public List<Vote> getAllVotesWithDetails() {
    List<Vote> votes = new ArrayList<>();
    String sql = "SELECT v.*, s.NAME as STUDENT_NAME, c.CANDIDATE_NAME, e.TITLE as ELECTION_TITLE " +
                 "FROM VOTES v " +
                 "LEFT JOIN STUDENTS s ON v.STUDENT_ID = s.STUDENTS_ID " +
                 "LEFT JOIN CANDIDATES c ON v.CANDIDATE_ID = c.CANDIDATE_ID " +
                 "LEFT JOIN ELECTIONS e ON v.ELECTION_ID = e.ELECTION_ID " +
                 "ORDER BY v.VOTE_TIME DESC";
    
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            Vote vote = new Vote(
                rs.getInt("VOTE_ID"),
                rs.getString("STUDENT_ID"),
                rs.getInt("ELECTION_ID"),
                rs.getInt("CANDIDATE_ID"),
                rs.getTimestamp("VOTE_TIME")
            );
            votes.add(vote);
        }
    } catch (SQLException e) {
        System.out.println("Error getting all votes: " + e.getMessage());
        e.printStackTrace();
    }
    return votes;
}
}
