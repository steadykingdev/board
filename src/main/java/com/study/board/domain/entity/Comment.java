package com.study.board.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;

    public Comment() {
    }

    public Comment(Post post, Member member, String content) {
        this.post = post;
        this.member = member;
        this.content = content;
        this.createdTime = LocalDateTime.now();
    }

    public void update(String content) {
        this.content = content;
        this.modifiedTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public Member getMember() {
        return member;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
}
