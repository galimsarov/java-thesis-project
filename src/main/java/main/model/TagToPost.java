package main.model;

import javax.persistence.*;

@Entity
@Table(name = "tag2post")
public class TagToPost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int postId;

    private int tagId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
