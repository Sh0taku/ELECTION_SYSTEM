package com.election.dao;

import com.election.beans.Candidate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luqman
 */
public class CandidateDAO {
    

    public List<Candidate> getCandidatesByElection(int electionId) {
        List<Candidate> candidates = new ArrayList<>();
        String sql = "SELECT * FROM candidates WHERE election_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Candidate candidate = new Candidate(
                    rs.getInt("candidate_id"),
                    rs.getString("student_id"),
                    rs.getInt("election_id"),
                    rs.getString("position"),
                    rs.getString("manifesto"),
                    rs.getInt("vote_count"),
                    rs.getString("candidate_name")
                );
                candidates.add(candidate);
            }
        } catch (SQLException e) {
            System.out.println("Error getting candidates: " + e.getMessage());
            e.printStackTrace();
        }
        return candidates;
    }
    
    public int getCandidateCount(int electionId) {
        String sql = "SELECT COUNT(*) FROM candidates WHERE election_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting candidate count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    

    public boolean voteForCandidate(int candidateId) {
        String sql = "UPDATE candidates SET vote_count = vote_count + 1 WHERE candidate_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, candidateId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error voting for candidate: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public boolean addCandidate(Candidate candidate) {
        String sql = "INSERT INTO candidates (student_id, election_id, position, manifesto, vote_count, candidate_name) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, candidate.getStudentId());
            pstmt.setInt(2, candidate.getElectionId());
            pstmt.setString(3, candidate.getPosition());
            pstmt.setString(4, candidate.getManifesto());
            pstmt.setInt(5, candidate.getVoteCount());
            pstmt.setString(6, candidate.getCandidateName());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding candidate: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public Candidate getCandidateById(int candidateId) {
        Candidate candidate = null;
        String sql = "SELECT * FROM candidates WHERE candidate_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, candidateId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                candidate = new Candidate(
                    rs.getInt("candidate_id"),
                    rs.getString("student_id"),
                    rs.getInt("election_id"),
                    rs.getString("position"),
                    rs.getString("manifesto"),
                    rs.getInt("vote_count"),
                    rs.getString("candidate_name")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error getting candidate: " + e.getMessage());
            e.printStackTrace();
        }
        return candidate;
    }
    

    public Candidate getCandidateByStudentAndElection(String studentId, int electionId) {
        Candidate candidate = null;
        String sql = "SELECT * FROM candidates WHERE student_id=? AND election_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentId);
            pstmt.setInt(2, electionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                candidate = new Candidate(
                    rs.getInt("candidate_id"),
                    rs.getString("student_id"),
                    rs.getInt("election_id"),
                    rs.getString("position"),
                    rs.getString("manifesto"),
                    rs.getInt("vote_count"),
                    rs.getString("candidate_name")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error getting candidate by student and election: " + e.getMessage());
            e.printStackTrace();
        }
        return candidate;
    }
    

    public boolean updateCandidateVoteCount(int candidateId, int newVoteCount) {
        String sql = "UPDATE candidates SET vote_count=? WHERE candidate_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newVoteCount);
            pstmt.setInt(2, candidateId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating candidate vote count: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public List<Candidate> getCandidatesByPosition(int electionId, String position) {
        List<Candidate> candidates = new ArrayList<>();
        String sql = "SELECT * FROM candidates WHERE election_id=? AND position=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, electionId);
            pstmt.setString(2, position);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Candidate candidate = new Candidate(
                    rs.getInt("candidate_id"),
                    rs.getString("student_id"),
                    rs.getInt("election_id"),
                    rs.getString("position"),
                    rs.getString("manifesto"),
                    rs.getInt("vote_count"),
                    rs.getString("candidate_name")
                );
                candidates.add(candidate);
            }
        } catch (SQLException e) {
            System.out.println("Error getting candidates by position: " + e.getMessage());
            e.printStackTrace();
        }
        return candidates;
    }
    

    public List<Candidate> getAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String sql = "SELECT * FROM candidates ORDER BY election_id, position";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Candidate candidate = new Candidate(
                    rs.getInt("candidate_id"),
                    rs.getString("student_id"),
                    rs.getInt("election_id"),
                    rs.getString("position"),
                    rs.getString("manifesto"),
                    rs.getInt("vote_count"),
                    rs.getString("candidate_name")
                );
                candidates.add(candidate);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all candidates: " + e.getMessage());
            e.printStackTrace();
        }
        return candidates;
    }
    

    public boolean deleteCandidate(int candidateId) {
        String sql = "DELETE FROM candidates WHERE candidate_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, candidateId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting candidate: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

public List<Candidate> getAllCandidatesWithDetails() {
    List<Candidate> candidates = new ArrayList<>();
    String sql = "SELECT c.*, e.TITLE as ELECTION_TITLE FROM CANDIDATES c " +
                 "LEFT JOIN ELECTIONS e ON c.ELECTION_ID = e.ELECTION_ID " +
                 "ORDER BY c.ELECTION_ID, c.CANDIDATE_ID";
    
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            Candidate candidate = new Candidate(
                rs.getInt("CANDIDATE_ID"),
                rs.getString("STUDENT_ID"),
                rs.getInt("ELECTION_ID"),
                rs.getString("POSITION"),
                rs.getString("MANIFESTO"),
                rs.getInt("VOTE_COUNT"),
                rs.getString("CANDIDATE_NAME")
            );
            candidates.add(candidate);
        }
    } catch (SQLException e) {
        System.out.println("Error getting all candidates: " + e.getMessage());
        e.printStackTrace();
    }
    return candidates;
}
}
