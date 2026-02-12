/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.election.dao;

import com.election.beans.Election;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luqman
 */
public class ElectionDAO {


    public List<Election> getAllElections() {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT * FROM elections ORDER BY election_id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Election election = new Election(
                    rs.getInt("election_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                );
                elections.add(election);
            }
        } catch (SQLException e) {
            System.out.println("Error getting elections: " + e.getMessage());
        }
        return elections;
    }
    

    public List<Election> getActiveElections() {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT * FROM elections WHERE status='ONGOING'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Election election = new Election(
                    rs.getInt("election_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                );
                elections.add(election);
            }
        } catch (SQLException e) {
            System.out.println("Error getting active elections: " + e.getMessage());
        }
        return elections;
    }
    

    public List<Election> getElectionsWithVoteStatus(String studentId) {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT e.*, " +
                     "CASE WHEN v.vote_id IS NOT NULL THEN 1 ELSE 0 END as has_voted " +
                     "FROM elections e " +
                     "LEFT JOIN votes v ON e.election_id = v.election_id AND v.student_id = ? " +
                     "ORDER BY e.start_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Election election = new Election(
                    rs.getInt("election_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                );
                elections.add(election);
            }
        } catch (SQLException e) {
            System.out.println("Error getting elections with vote status: " + e.getMessage());
        }
        return elections;
    }
    

    public Election getElectionById(int electionId) {
        Election election = null;
        String sql = "SELECT * FROM elections WHERE election_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                election = new Election(
                    rs.getInt("election_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error getting election by ID: " + e.getMessage());
        }
        return election;
    }
    
    // Create new election for admin
    public boolean createElection(Election election) {
        String sql = "INSERT INTO elections (title, description, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, election.getTitle());
            pstmt.setString(2, election.getDescription());
            pstmt.setDate(3, election.getStartDate());
            pstmt.setDate(4, election.getEndDate());
            pstmt.setString(5, election.getStatus());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error creating election: " + e.getMessage());
            return false;
        }
    }
    
    // Update election status
    public boolean updateElectionStatus(int electionId, String status) {
        String sql = "UPDATE elections SET status=? WHERE election_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, electionId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating election status: " + e.getMessage());
            return false;
        }
    }
    
    // Get upcoming elections
    public List<Election> getUpcomingElections() {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT * FROM elections WHERE status='UPCOMING' ORDER BY start_date";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Election election = new Election(
                    rs.getInt("election_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                );
                elections.add(election);
            }
        } catch (SQLException e) {
            System.out.println("Error getting upcoming elections: " + e.getMessage());
        }
        return elections;
    }
    
    // Get ended elections
    public List<Election> getEndedElections() {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT * FROM elections WHERE status='ENDED' ORDER BY end_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Election election = new Election(
                    rs.getInt("election_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                );
                elections.add(election);
            }
        } catch (SQLException e) {
            System.out.println("Error getting ended elections: " + e.getMessage());
        }
        return elections;
    }
}
