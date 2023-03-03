package com.study.board.controller;

import com.study.board.domain.dto.LoginRequest;
import com.study.board.domain.dto.SignupRequest;
import com.study.board.service.MemberService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Captor
    private ArgumentCaptor<SignupRequest> signupRequestCaptor;

    @Captor
    private ArgumentCaptor<LoginRequest> loginRequestCaptor;

    private final String LOGIN_ID = "loginId";
    private final String PASSWORD = "password";
    private final String PASSWORD_CHECK = "passwordCheck";
    private final String NICKNAME = "nickname";
    private final String ROLE = "role";

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MemberController(memberService)).build();
    }

    @DisplayName("회원가입 성공 테스트(프로필 이미지 x)")
    @Test
    public void signupSuccessWithoutProfileTest() throws Exception {
        //given
        String loginId = "testMember";
        String password = "1q2w3e4r!@";
        String nickname = "테스터";
        String role = "ROLE_USER";

        //when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/api/signup")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(LOGIN_ID, loginId)
                .param(PASSWORD, password)
                .param(PASSWORD_CHECK, password)
                .param(NICKNAME, nickname)
                .param(ROLE, role);

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DisplayName("회원가입 성공 테스트(프로필 이미지 o)")
    @Test
    public void signupSuccessWithProfileTest() throws Exception {
        //given
        String loginId = "testMember";
        String password = "1q2w3e4r!@";
        String nickname = "테스터";
        String role = "ROLE_USER";

        MockMultipartFile profileImg = new MockMultipartFile(
                "imgFile",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "imgFile".getBytes()
        );

        //when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/api/signup")
                .file(profileImg)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(LOGIN_ID, loginId)
                .param(PASSWORD, password)
                .param(PASSWORD_CHECK, password)
                .param(NICKNAME, nickname)
                .param(ROLE, role);

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());

        verify(memberService).signup(signupRequestCaptor.capture());
        SignupRequest capturedSignupRequest = signupRequestCaptor.getValue();

        assertThat(capturedSignupRequest.getImgFile()).isEqualTo(profileImg);
    }

    @DisplayName("회원가입 loginId 정합성 검사 실패 테스트")
    @Test
    public void signupLoginIdValidFailTest() throws Exception {
        //given
        String loginId = "1";
        String password = "1q2w3e4r!@";
        String nickname = "테스터";
        String role = "ROLE_USER";

        //when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .param(LOGIN_ID, loginId)
                .param(PASSWORD, password)
                .param(PASSWORD_CHECK, password)
                .param(NICKNAME, nickname)
                .param(ROLE, role);

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
        verifyNoInteractions(memberService);
    }

    @DisplayName("회원가입 password 정합성 검사 실패 테스트")
    @Test
    public void signupPasswordValidFailTest() throws Exception {
        //given
        String loginId = "testMember";
        String password = "test";
        String nickname = "테스터";
        String role = "ROLE_USER";

        //when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .param(LOGIN_ID, loginId)
                .param(PASSWORD, password)
                .param(PASSWORD_CHECK, password)
                .param(NICKNAME, nickname)
                .param(ROLE, role);

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
        verifyNoInteractions(memberService); // memberService.signup() 메서드가 호출되지 않는지 확인 (호출x 여야 성공)
    }

    @DisplayName("로그인 성공 테스트")
    @Test
    public void loginTest() throws Exception {
        //given
        String content = "{\"loginId\": \"testMember\", \"password\": \"1q2w3e4r!@\"}";

        //when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        //then
        mockMvc.perform(requestBuilder).andExpect(status().isOk());

        verify(memberService).login(loginRequestCaptor.capture());
        LoginRequest capturedLoginRequest = loginRequestCaptor.getValue();

        assertThat(capturedLoginRequest.getLoginId()).isEqualTo("testMember");
        assertThat(capturedLoginRequest.getPassword()).isEqualTo("1q2w3e4r!@");
    }

    @DisplayName("로그인 loginId 정합성 검사 실패 테스트")
    @Test
    public void loginLoginIdValidFailTest() throws Exception {
        //given
        String content = "{\"loginId\": \"\", \"password\": \"1q2w3e4r!@\"}";

        //when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        //then
        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
        verifyNoInteractions(memberService);
    }

    @DisplayName("로그인 password 정합성 검사 실패 테스트")
    @Test
    public void loginPasswordValidFailTest() throws Exception {
        //given
        String content = "{\"loginId\": \"testMember\", \"password\": \"\"}";

        //when
        MockHttpServletRequestBuilder requestBuilder =MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        //then
        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
        verifyNoInteractions(memberService);
    }
}