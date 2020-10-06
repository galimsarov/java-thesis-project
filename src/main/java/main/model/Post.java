package main.model;

import lombok.*;
import main.model.helper.PostStatus;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(columnDefinition = "TINYINT",
            name = "is_active",
            nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('NEW','ACCEPTED','DECLINED') default 'NEW'",
            name = "moderation_status",
            nullable = false)
    private PostStatus moderationStatus;

    @Column(name = "moderator_id")
    private int moderatorId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time",
            nullable = false)
    private Date time;

    @Column(name = "title",
            nullable = false)
    private String title;

    @Column(columnDefinition="TEXT",
            name = "text",
            nullable = false)
    private String text;

    @Column(name = "view_count",
            nullable = false)
    private int viewCount;

    // у каждого поста только один автор

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // у поста может быть много лайков и дизлайков

    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PostVote> postVoteList = new ArrayList<>();

    public void addPostVote(PostVote postVote) {
        postVoteList.add(postVote);
        postVote.setPost(this);
    }

    public void removePostVote(PostVote postVote) {
        postVoteList.remove(postVote);
        postVote.setPost(null);
    }

    // у поста может быть много комментариев

    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PostComment> postCommentList = new ArrayList<>();

    public void addPostComment(PostComment postComment) {
        postCommentList.add(postComment);
        postComment.setPost(this);
    }

    public void removePostComment(PostComment postComment) {
        postCommentList.remove(postComment);
        postComment.setPost(null);
    }

    // у поста может быть много тэгов

//    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH,
//            CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
//    @JoinTable(name = "tag2post",
//            joinColumns = @JoinColumn(name = "post_id"),
//            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "tag2post",
        joinColumns = {@JoinColumn(name = "post_id")},
        inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private Set<Tag> tagSet = new HashSet<>();

    public void addTag(Tag tag) {
        tagSet.add(tag);
        tag.getPostSet().add(this);
    }

    public void removeTag(Tag tag) {
        tagSet.remove(tag);
        tag.getPostSet().remove(this);
    }
}
