package com.study.board.domain.entity;

import com.study.board.domain.dto.PostRequest;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.*;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;

    public Post() {
    }

    public Post(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.createdTime = LocalDateTime.now();
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.modifiedTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Member getMember() {
        return member;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }
}
