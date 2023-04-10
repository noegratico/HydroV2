package com.example.aqua_v2.model;

public class UserLog {
    private String logEmail;
    private String logDateTime;
    private String logActivity;

    public UserLog(String logEmail, String logDateTime, String logActivity) {
        this.logEmail = logEmail;
        this.logDateTime = logDateTime;
        this.logActivity = logActivity;
    }

    public String getLogEmail() {
        return logEmail;
    }

    public void setLogEmail(String logEmail) {
        this.logEmail = logEmail;
    }

    public String getLogDateTime() {
        return logDateTime;
    }

    public void setLogDateTime(String logDateTime) {
        this.logDateTime = logDateTime;
    }

    public String getLogActivity() {
        return logActivity;
    }

    public void setLogActivity(String logActivity) {
        this.logActivity = logActivity;
    }


}
