package com.study.board.controller;

import com.study.board.domain.JwtPayload;
import com.study.board.domain.dto.CommonResponseFormat;
import com.study.board.domain.dto.PostRequest;
import com.study.board.domain.dto.PostResponse;
import com.study.board.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public ResponseEntity<CommonResponseFormat> createPost(@RequestAttribute("user") JwtPayload jwtPayload,
                                                           @Valid @RequestBody PostRequest postRequest) {

        postService.createPost(jwtPayload.getId(), postRequest);

        return new ResponseEntity<>(CommonResponseFormat.createSuccessWithNoContent(), HttpStatus.CREATED);
    }

    @GetMapping("/posts/list")
    public ResponseEntity<CommonResponseFormat<List<PostResponse>>> getPostList() {
        List<PostResponse> postResponseList = postService.getPostList();

        return new ResponseEntity<>(CommonResponseFormat.createSuccess(postResponseList), HttpStatus.OK);
    }

    @GetMapping("/post/{post-id}")
    public ResponseEntity<CommonResponseFormat<PostResponse>> getPost(@PathVariable("post-id") Long postId) throws Exception {

        PostResponse postResponse = postService.getPost(postId);

        return new ResponseEntity<>(CommonResponseFormat.createSuccess(postResponse), HttpStatus.OK);
    }

    @PutMapping("/posts/{post-id}")
    public ResponseEntity<CommonResponseFormat> updatePost(@PathVariable("post-id") Long postId,
                                     @RequestAttribute("user") JwtPayload jwtPayload,
                                     @Valid @RequestBody PostRequest postRequest) {

        postService.updatePost(postId, jwtPayload.getId(), postRequest);

        return new ResponseEntity<>(CommonResponseFormat.createSuccessWithNoContent(), HttpStatus.OK);
    }

    @DeleteMapping("/posts/{post-id}")
    public ResponseEntity<CommonResponseFormat> deletePost(@PathVariable("post-id") Long postId,
                                                           @RequestAttribute("user") JwtPayload jwtPayload) {

        postService.deletePost(postId, jwtPayload.getId());

        return new ResponseEntity<>(CommonResponseFormat.createSuccessWithNoContent(), HttpStatus.OK);
    }
}
