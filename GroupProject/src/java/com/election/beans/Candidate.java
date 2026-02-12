package com.election.beans;

import java.io.Serializable;

public class Candidate implements Serializable {
    private int candidateId;
    private String studentId;
    private int electionId;
    private String position;
    private String manifesto;      
    private int voteCount;
    private String candidateName;  
    
 
    public Candidate() {}
    
  
    public Candidate(int candidateId, String studentId, int electionId, 
                     String position, String manifesto, int voteCount, String candidateName) {
        this.candidateId = candidateId;
        this.studentId = studentId;
        this.electionId = electionId;
        this.position = position;
        this.manifesto = manifesto;
        this.voteCount = voteCount;
        this.candidateName = candidateName;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getManifesto() {
        return manifesto;
    }

    public void setManifesto(String manifesto) {
        this.manifesto = manifesto;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
}
    
    
