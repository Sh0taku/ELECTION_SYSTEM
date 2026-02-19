/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.election.beans;

import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author Emir
 */
public class Election implements Serializable {
    private int electionId;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private String status; 

    public Election() {}

    public Election(int electionId, String title, String description, 
                    Date startDate, Date endDate, String status) {
        this.electionId = electionId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }


    public Election(int electionId, String title, String description, String status) {
        this.electionId = electionId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startDate = null;
        this.endDate = null;
    }

    public Election(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getElectionId() {
        return electionId;
    }

    public void setElectionId(int electionId) {
        this.electionId = electionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Election{" + 
               "electionId=" + electionId + 
               ", title='" + title + '\'' + 
               ", description='" + description + '\'' + 
               ", startDate=" + startDate + 
               ", endDate=" + endDate + 
               ", status='" + status + '\'' + 
               '}';
    }
}