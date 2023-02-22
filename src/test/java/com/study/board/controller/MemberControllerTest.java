package com.study.board.controller;

import com.study.board.domain.Role;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MemberController(memberService)).build();
    }

    @DisplayName("회원가입 성공 테스트")
    @Test
    public void signupSuccessTest() throws Exception {
        //given
        String content = "{\"loginId\": \"testMember\", \"password\" : \"1q2w3e4r!@\", \"passwordCheck\" :  \"1q2w3e4r!@\", \"nickname\" :  \"테스터\", \"role\" : \"ROLE_USER\"}";

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated());

        //then
        verify(memberService).signup(signupRequestCaptor.capture());
        SignupRequest capturedSignupRequest = signupRequestCaptor.getValue();
        assertThat(capturedSignupRequest.getLoginId()).isEqualTo("testMember");
        assertThat(capturedSignupRequest.getPassword()).isEqualTo("1q2w3e4r!@");
        assertThat(capturedSignupRequest.getPasswordCheck()).isEqualTo("1q2w3e4r!@");
        assertThat(capturedSignupRequest.getNickname()).isEqualTo("테스터");
        assertThat(capturedSignupRequest.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @DisplayName("회원가입 loginId 정합성 검사 실패 테스트")
    @Test
    public void signupLoginIdValidFailTest() throws Exception {
        //given
        String content = "{\"loginId\": \"test\", \"password\" : \"1q2w3e4r!@\", \"passwordCheck\" :  \"1q2w3e4r!@\", \"nickname\" :  \"테스터\", \"role\" : \"ROLE_USER\"}";

        //when then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(memberService);
    }

    @DisplayName("회원가입 password 정합성 검사 실패 테스트")
    @Test
    public void signupPasswordValidFailTest() throws Exception {
        //given
        String content = "{\"loginId\": \"testMember\", \"password\" : \"test\", \"passwordCheck\" :  \"test\", \"nickname\" :  \"테스터\"}";

        //when then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(memberService); // memberService.signup() 메서드가 호출되지 않는지 확인 (호출x 여야 성공)
    }

    @DisplayName("로그인 성공 테스트")
    @Test
    public void loginTest() throws Exception {
        //given
        String content = "{\"loginId\": \"testMember\", \"password\": \"1q2w3e4r!@\"}";

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
        //then
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());
        //then
        verifyNoInteractions(memberService);
    }

    @DisplayName("로그인 password 정합성 검사 실패 테스트")
    @Test
    public void loginPasswordValidFailTest() throws Exception {
        //given
        String content = "{\"loginId\": \"testMember\", \"password\": \"\"}";

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());

        //then
        verifyNoInteractions(memberService);
    }
}