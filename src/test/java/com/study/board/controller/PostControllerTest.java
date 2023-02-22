package com.study.board.controller;

import com.study.board.domain.JwtPayload;
import com.study.board.domain.Role;
import com.study.board.domain.dto.PostRequest;
import com.study.board.domain.dto.PostResponse;
import com.study.board.exception.PostNotFoundException;
import com.study.board.service.PostService;
import com.study.board.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PostController.class)
class PostControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Captor
    private ArgumentCaptor<PostRequest> postRequestCaptor;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PostController(postService))
                .setControllerAdvice(CommonExceptionController.class)
                .build();
    }

    @DisplayName("게시물 작성 성공 테스트")
    @Test
    public void creatPostSuccessTest() throws Exception {
        //given
        String content = "{\"title\": \"testTitle\", \"content\" : \"testContent\"}";
        JwtPayload jwtPayload = new JwtPayload(1L, "testMember", Role.ROLE_USER);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .requestAttr("user", jwtPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated());

        //then
        verify(postService).createPost(eq(jwtPayload.getId()), postRequestCaptor.capture());
        PostRequest capturedPostRequest = postRequestCaptor.getValue();
        assertThat(capturedPostRequest.getTitle()).isEqualTo("testTitle");
        assertThat(capturedPostRequest.getContent()).isEqualTo("testContent");
    }

    @DisplayName("게시물 작성 실패 테스트(글 제목 검증 실패)")
    @Test
    public void creatPostTitleValidFailTest() throws Exception {
        //given
        String content = "{\"title\": \"\", \"content\" : \"testContent\"}";
        JwtPayload jwtPayload = new JwtPayload(1L, "testMember", Role.ROLE_USER);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .requestAttr("user", jwtPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());

        //then
        verifyNoInteractions(postService);
    }

    @DisplayName("게시물 작성 실패 테스트(글 본문 검증 실패)")
    @Test
    public void creatPostContentValidFailTest() throws Exception {
        //given
        String content = "{\"title\": \"testTitle\", \"content\" : \"\"}";
        JwtPayload jwtPayload = new JwtPayload(1L, "testMember", Role.ROLE_USER);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .requestAttr("user", jwtPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());

        //then
        verifyNoInteractions(postService);
    }

    @DisplayName("게시물 목록 조회 성공 테스트")
    @Test
    public void getPostListSuccessTest() throws Exception {
        //given
        LocalDateTime localDateTime = LocalDateTime.now();
        PostResponse postResponse = new PostResponse(1L, "testTitle", "testContent", "test", localDateTime);

        //when then
        when(postService.getPostList()).thenReturn(List.of(postResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/list"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("게시물 조회 성공 테스트")
    @Test
    public void getPostSuccessTest() throws Exception {
        //given
        LocalDateTime localDateTime = LocalDateTime.now();
        PostResponse postResponse = new PostResponse(1L, "testTitle", "testContent", "test", localDateTime);

        //when, then
        when(postService.getPost(anyLong())).thenReturn(postResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("게시물 조회 실패 테스트(게시물 id 조회)")
    @Test
    public void getPostNotExistFailTest() throws Exception {
        //when, then
        when(postService.getPost(anyLong())).thenThrow(new PostNotFoundException("존재하지 않는 게시물입니다."));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/1"))
                .andExpect(jsonPath("message").value("존재하지 않는 게시물입니다."))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("게시물 수정 성공 테스트")
    @Test
    public void updatePostSuccessTest() throws Exception {
        //given
        String content = "{\"title\": \"updateTitle\", \"content\" : \"updateContent\"}";
        JwtPayload jwtPayload = new JwtPayload(1L, "testMember", Role.ROLE_USER);

        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1")
                        .requestAttr("user", jwtPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        //then
        verify(postService).updatePost(eq(1L), eq(jwtPayload.getId()), postRequestCaptor.capture());
        PostRequest capturedPostRequest = postRequestCaptor.getValue();
        assertThat(capturedPostRequest.getTitle()).isEqualTo("updateTitle");
        assertThat(capturedPostRequest.getContent()).isEqualTo("updateContent");
    }

    @DisplayName("게시물 수정 실패 테스트(글 제목이 비어있을 때)")
    @Test
    public void updatePostValidFailTest() throws Exception {
        //given
        String content = "{\"title\": \"\", \"content\" : \"updateContent\"}";
        JwtPayload jwtPayload = new JwtPayload(1L, "testMember", Role.ROLE_USER);

        //when then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/1")
                        .requestAttr("user", jwtPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("게시물 삭제 성공 테스트")
    @Test
    public void deletePostSuccessTest() throws Exception {
        //given
        JwtPayload jwtPayload = new JwtPayload(1L, "testMember", Role.ROLE_USER);

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/1")
                        .requestAttr("user", jwtPayload))
                .andExpect(status().isOk());
    }
}