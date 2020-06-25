package main.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @NotNull
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('NEW','ACCEPTED','DECLINED') default 'NEW'")
    @NotNull
    private Status moderationStatus;

    @Column(nullable = true)
    private int moderatorId;

    private int userId;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date time;

    @NotNull
    private String title;

    @Column(columnDefinition="TEXT")
    @NotNull
    private String text;

    private int viewCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Status getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(Status moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public int getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(int moderatorId) {
        this.moderatorId = moderatorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
