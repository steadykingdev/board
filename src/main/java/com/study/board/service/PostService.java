package com.study.board.service;

import com.study.board.domain.dto.PostRequest;
import com.study.board.domain.dto.PostResponse;
import com.study.board.domain.entity.Member;
import com.study.board.domain.entity.Post;
import com.study.board.exception.ForbiddenException;
import com.study.board.exception.PostNotFoundException;
import com.study.board.exception.UserNotFoundException;
import com.study.board.repository.MemberRepository;
import com.study.board.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    private final String imgHost;

    private final String COMMON_PROFILE = "/images/no_profile.png";

    public PostService(PostRepository postRepository, MemberRepository memberRepository,
                       @Value("${myapp.server.host}") String imgHost) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.imgHost = imgHost;
    }

    @Transactional
    public Post createPost(Long memberId, PostRequest postRequest) {

        Member member = findMemberById(memberId);

        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), member);

        return postRepository.save(post);
    }

    public List<PostResponse> getPostList() {
        List<Post> postList = postRepository.findAll();
        return postList.stream()
                .map(p -> {
                    String profilePath = p.getMember().getProfileImgPath();
                    String imgPath = getImagePath(profilePath);

                    PostResponse postResponse = PostResponse.fromEntity(p);
                    postResponse.setProfileImg(imgPath);

                    return postResponse;
                })
                .collect(Collectors.toList());
    }

    public PostResponse getPost(Long postId){
        Post post = findPostById(postId);
        String profilePath = post.getMember().getProfileImgPath();
        String imgPath = getImagePath(profilePath);

        PostResponse postResponse = PostResponse.fromEntity(post);
        postResponse.setProfileImg(imgPath);

        return postResponse;
    }

    @Transactional
    public void updatePost(Long postId, Long memberId, PostRequest postRequest) {
        Post post = findPostById(postId);
        if(post.getMember().getId() != memberId) {
            throw new ForbiddenException("나의 게시물이 아닙니다.");
        }
        post.update(postRequest.getTitle(), postRequest.getContent());
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = findPostById(postId);
        if(post.getMember().getId() != memberId) {
            throw new ForbiddenException("나의 게시물이 아닙니다.");
        }
        postRepository.delete(post);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException("존재하지 않는 회원입니다.");
                });
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    throw new PostNotFoundException("존재하지 않는 게시물입니다.");
                });
    }

    private String getImagePath(String profileImgPath){
        String resultPath;
        if (profileImgPath != null) {
            resultPath = profileImgPath;
        } else {
            resultPath = COMMON_PROFILE;
        }

        return resultPath;
    }

}
