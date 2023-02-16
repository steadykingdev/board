package com.study.board.controller;

import com.study.board.domain.Role;
import com.study.board.domain.dto.SignupRequest;
import com.study.board.service.MemberService;
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


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Captor
    private ArgumentCaptor<SignupRequest> signupRequestCaptor;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MemberController(memberService)).build();
    }

    @DisplayName("회원가입 성공 테스트")
    @Test
    public void signupTest() throws Exception {
        //given
        String content = "{\"loginId\": \"testMember\", \"password\" : \"1q2w3e4r!@\", \"passwordCheck\" :  \"1q2w3e4r!@\", \"nickname\" :  \"테스터\", \"role\" : \"ROLE_USER\"}";

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isCreated());

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
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

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
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verifyNoInteractions(memberService); // memberService.signup() 메서드가 호출되지 않는지 확인 (호출x 여야 성공)
    }
}