package main.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_votes")
@Getter
@Setter
@EqualsAndHashCode
public class PostVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time",
            nullable = false)
    private Date time;

    @Column(columnDefinition = "TINYINT",
            name = "value",
            nullable = false)
    private int value;

    // у каждого лайка и дизлайка только 1 автор

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // лайк или дизлайк может быть только к 1 посту

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
}
