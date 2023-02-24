package com.study.board.domain.dto;

import com.study.board.domain.entity.Comment;

import java.time.LocalDateTime;

public class CommentResponse {

    private Long id;

    private String nickname;

    private String comment;

    private LocalDateTime createdTime;

    public CommentResponse(Long id, String nickname, String comment, LocalDateTime createdTime) {
        this.id = id;
        this.nickname = nickname;
        this.comment = comment;
        this.createdTime = createdTime;
    }

    public static CommentResponse fromEntity(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getMember().getNickname(), comment.getContent(), comment.getCreatedTime());
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
}
