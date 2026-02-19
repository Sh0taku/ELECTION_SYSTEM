/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.election.dao;

/**
 *
 * @author Emir
 */
import com.election.beans.Student;
import java.sql.*;

public class StudentDAO {
    

public int getTotalStudents() {
    String sql = "SELECT COUNT(*) FROM students";
    
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        System.out.println("Error getting total students: " + e.getMessage());
    }
    return 0;
}
 
    public Student login(String studentId, String password) {
        Student student = null;
        String sql = "SELECT * FROM students WHERE student_id=? AND password=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentId);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
    student = new Student();
    student.setStudentId(rs.getString("STUDENTS_ID"));
    student.setName(rs.getString("name"));
    student.setPassword(rs.getString("password"));
    student.setEmail(rs.getString("email"));
    student.setFaculty(rs.getString("faculty"));
    student.setHasVoted(rs.getInt("has_voted") == 1);
    student.setCandidateStatus(rs.getString("candidate_status")); 
}
            
        } catch (SQLException e) {
            System.out.println("Error in login: " + e.getMessage());
        }
        return student;
    }
    

    public boolean register(Student student) {
    String sql = "INSERT INTO students (student_id, name, password, email, faculty, has_voted) VALUES (?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, student.getStudentId());
        pstmt.setString(2, student.getName());
        pstmt.setString(3, student.getPassword());
        pstmt.setString(4, student.getEmail());
        pstmt.setString(5, student.getFaculty());
        pstmt.setInt(6, student.isHasVoted() ? 1 : 0);
        
        int rows = pstmt.executeUpdate();
        return rows > 0;
        
    } catch (SQLException e) {
        System.out.println("Error registering student: " + e.getMessage());
        return false;
    }
}
    
    // student existant cehck
    public boolean studentExists(String studentId) {
        String sql = "SELECT student_id FROM students WHERE student_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            System.out.println("Error checking student: " + e.getMessage());
            return false;
        }
    }
    
    // Reset password
    public boolean resetPassword(String studentId, String newPassword) {
        String sql = "UPDATE students SET password=? WHERE student_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setString(2, studentId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error resetting password: " + e.getMessage());
            return false;
        }
    }
    
    // Change password
    public boolean changePassword(String studentId, String currentPassword, String newPassword) {
        String sql = "UPDATE students SET password=? WHERE student_id=? AND password=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setString(2, studentId);
            pstmt.setString(3, currentPassword);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error changing password: " + e.getMessage());
            return false;
        }
    }
    

public Student[] getAllStudents() {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    try {
        conn = DBConnection.getConnection();
        stmt = conn.createStatement();
        

        ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM students");
        countRs.next();
        int count = countRs.getInt(1);
        
        if (count == 0) {
            return new Student[0]; 
        }
        

        rs = stmt.executeQuery("SELECT * FROM students ORDER BY students_id");
        

        Student[] students = new Student[count];
        int index = 0;
        
        while (rs.next() && index < count) {
            Student student = new Student();
            student.setStudentId(rs.getString("students_id"));
            student.setName(rs.getString("name"));
            student.setPassword(rs.getString("password"));
            student.setEmail(rs.getString("email"));
            student.setFaculty(rs.getString("faculty"));
            student.setHasVoted(rs.getBoolean("has_voted"));
            student.setCandidateStatus(rs.getString("candidate_status"));
            
            students[index] = student;
            index++;
        }
        
        return students;
        
    } catch (SQLException e) {
        System.out.println("Error getting students: " + e.getMessage());
        return new Student[0];
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}
    
    // Get student by ID
    public Student getStudentById(String studentId) {
        Student student = null;
        String sql = "SELECT * FROM students WHERE student_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                student = new Student();
                student.setStudentId(rs.getString("STUDENTS_ID"));
                student.setName(rs.getString("name"));
                student.setPassword(rs.getString("password"));
                student.setEmail(rs.getString("email"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting student: " + e.getMessage());
        }
        return student;
    }
}
