package com.election.beans;
import java.io.Serializable;

public class Admin implements Serializable {
private String adminId;
private String username;
private String password;
public Admin() {}
public Admin(String adminId, String username, String password) {
this.adminId = adminId;
this.username = username;
this.password = password;
}
public String getAdminId() {
return adminId;
}
public void setAdminId(String adminId) {
this.adminId = adminId;
}
public String getUsername() {
return username;
}
public void setUsername(String username) {
this.username = username;
}
public String getPassword() {
return password;
}
public void setPassword(String password) {
this.password = password;
}
}