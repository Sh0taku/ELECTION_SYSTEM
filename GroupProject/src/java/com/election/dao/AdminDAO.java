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

import com.election.beans.Admin;
import java.sql.*;

public class AdminDAO {
    
  
    public Admin login(String username, String password) {
        Admin admin = null;
        String sql = "SELECT * FROM admin WHERE username=? AND password=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                admin = new Admin(
                    rs.getString("admin_id"),
                    rs.getString("username"),
                    rs.getString("password")
                );
            }
            
        } catch (SQLException e) {
            System.out.println("Error in admin login: " + e.getMessage());
        }
        return admin;
    }
}
