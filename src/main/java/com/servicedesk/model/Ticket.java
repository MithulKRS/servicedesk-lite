package com.servicedesk.model;

import java.time.LocalDateTime;

public class Ticket {

    public enum Priority { LOW, MEDIUM, HIGH }
    public enum Status   { OPEN, IN_PROGRESS, RESOLVED }

    private long          id;
    private String        title;
    private String        description;
    private String        requester;
    private Priority      priority;
    private Status        status;
    private LocalDateTime createdAt;

    public Ticket() {}

    public Ticket(String title, String description, String requester, Priority priority) {
        this.title       = title;
        this.description = description;
        this.requester   = requester;
        this.priority    = priority;
        this.status      = Status.OPEN;
    }

    // Getters & setters
    public long          getId()          { return id; }
    public void          setId(long id)   { this.id = id; }

    public String        getTitle()                    { return title; }
    public void          setTitle(String title)        { this.title = title; }

    public String        getDescription()                       { return description; }
    public void          setDescription(String description)     { this.description = description; }

    public String        getRequester()                    { return requester; }
    public void          setRequester(String requester)    { this.requester = requester; }

    public Priority      getPriority()                    { return priority; }
    public void          setPriority(Priority priority)   { this.priority = priority; }

    public Status        getStatus()                  { return status; }
    public void          setStatus(Status status)     { this.status = status; }

    public LocalDateTime getCreatedAt()                       { return createdAt; }
    public void          setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format(
            "[#%d] %-40s | %-8s | %-11s | %s | %s",
            id, title, priority, status, requester,
            createdAt != null ? createdAt.toString().replace("T", " ").substring(0, 19) : ""
        );
    }
}
