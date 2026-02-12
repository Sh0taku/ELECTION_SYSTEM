/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.election.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;     
import java.sql.ResultSet; 
/**
 *
 * @author Luqman
 */
public class DBConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            String url = "jdbc:derby://localhost:1527/ElectionDB";
            String user;
            user = "admin1";
            String pass;
            pass = "admin";
            conn = DriverManager.getConnection(url, user, pass);
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("DB Error: " + e.getMessage());
        }
        return conn;
    }
    
    // Test
    public static void main(String[] args) {
        System.out.println("Testing DB Connection...");
        Connection conn = getConnection();
        
        if (conn != null) {
            System.out.println("✅ SUCCESS: Database connected!");
            try {
                // Test query
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students");
                if (rs.next()) {
                    System.out.println("✅ Students in DB: " + rs.getInt(1));
                }
                conn.close();
            } catch (SQLException e) {
                System.out.println("❌ Query error: " + e.getMessage());
            }
        } else {
            System.out.println("❌ FAILED: Connection is null");
        }
    }
}

