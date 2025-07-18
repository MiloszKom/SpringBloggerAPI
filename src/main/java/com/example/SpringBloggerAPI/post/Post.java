package com.example.SpringBloggerAPI.post;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "POSTS_TBL")
public class Post {

    @Id
    @GeneratedValue
    private int postId;
    private String title;
    private String content;

    public Post() {}

    public Post(int postId, String title, String content) {
        this.postId = postId;
        this.title = title;
        this.content = content;
    }

    public int getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
