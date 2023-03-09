package com.study.board.domain.dto;

import com.study.board.domain.entity.Post;

import java.time.LocalDateTime;

public class PostResponse {

    private Long id;

    private String title;

    private String content;

    private String nickname;

    private byte[] profileImg;

    private LocalDateTime createdTime;

    public PostResponse(Long id, String title, String content, String nickname, LocalDateTime createdTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.createdTime = createdTime;
    }

    public static PostResponse fromEntity(Post post) {

        return new PostResponse(post.getId(), post.getTitle(), post.getContent(), post.getMember().getNickname(), post.getCreatedTime());
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

    public String getNickname() {
        return nickname;
    }

    public byte[] getProfileImg() {
        return profileImg;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setProfileImg(byte[] profileImg) {
        this.profileImg = profileImg;
    }
}
