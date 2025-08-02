package com.example.SpringBloggerAPI.post;

import com.example.SpringBloggerAPI.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "POSTS_TBL")
public class Post {

    @Id
    @GeneratedValue
    private int postId;
    private String title;
    private String content;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false) // FK column
//    private User user;

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
