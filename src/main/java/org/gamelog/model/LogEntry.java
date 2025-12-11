package org.gamelog.model;

import java.sql.Timestamp;

public class LogEntry {
    private final int logId;
    private final String operation;
    private final String tableName;
    private final Timestamp timestamp;
    private final String actingUser;
    private final Integer recordId;
    private final String details;

    public LogEntry(int logId, String operation, String tableName, Timestamp timestamp, String actingUser, Integer recordId, String details) {
        this.logId = logId;
        this.operation = operation;
        this.tableName = tableName;
        this.timestamp = timestamp;
        this.actingUser = actingUser;
        this.recordId = recordId;
        this.details = details;
    }

    // Getters for TableView property binding
    public int getLogId() { return logId; }
    public String getOperation() { return operation; }
    public String getTableName() { return tableName; }
    public Timestamp getTimestamp() { return timestamp; }
    public String getActingUser() { return actingUser; }
    public Integer getRecordId() { return recordId; }
    public String getDetails() { return details; }
}
