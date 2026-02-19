/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.election.beans;
import java.io.Serializable;
/**
 *
 * @author Emir
 */

public class Student implements Serializable {
    private String studentId;
    private String name;
    private String password;
    private String email;
    private String faculty;     
    private boolean hasVoted;   
    private String candidateStatus;  

    
    public Student() {}

    public Student(String studentId, String name, String password, String email) {
        this.studentId = studentId;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

     public String getFaculty() {
        return faculty;
    }
    
    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
    
    public boolean isHasVoted() {
        return hasVoted;
    }
    
    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }
    public String getCandidateStatus() {
    return candidateStatus;
}

public void setCandidateStatus(String candidateStatus) {
    this.candidateStatus = candidateStatus;
}
    
    
 
}