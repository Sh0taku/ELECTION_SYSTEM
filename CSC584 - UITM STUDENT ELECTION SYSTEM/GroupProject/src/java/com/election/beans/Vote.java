/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.election.beans;
import java.io.Serializable;
import java.sql.Timestamp;
/**
 *
 * @author Emir
 */

public class Vote implements Serializable {
    private int voteId;
    private String studentId;
    private int electionId;
    private int candidateId;
    
    
    public Vote() {}
    
    public Vote(int voteId, String studentId, int electionId, int candidateId) {
        this.voteId = voteId;
        this.studentId = studentId;
        this.electionId = electionId;
        this.candidateId = candidateId;
    }

    public Vote(int aInt, String string, int aInt0, int aInt1, Timestamp timestamp) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public int getVoteId() {
        return voteId;
    }

    public void setVoteId(int voteId) {
        this.voteId = voteId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getElectionId() {
        return electionId;
    }

    public void setElectionId(int electionId) {
        this.electionId = electionId;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }
}