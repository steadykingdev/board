package com.study.board.service;

import com.study.board.domain.JwtPayload;
import com.study.board.domain.Role;
import com.study.board.domain.dto.LoginRequest;
import com.study.board.domain.dto.SignupRequest;
import com.study.board.domain.entity.Member;
import com.study.board.exception.IncorrectPasswordException;
import com.study.board.exception.UserNotFoundException;
import com.study.board.repository.MemberRepository;
import com.study.board.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock   // Mock 객체 생성
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks    // Mock 객체를 주입해줌
    private MemberService memberService;

    private Member member;

    @BeforeEach
    public void setUp() {
        member = new Member();
        ReflectionTestUtils.setField(member, "loginId", "testMember");
        ReflectionTestUtils.setField(member, "password", "1q2w3e4r!@");
        ReflectionTestUtils.setField(member, "nickname", "테스터");
        ReflectionTestUtils.setField(member, "role", Role.ROLE_USER);
    }

    @DisplayName("회원가입 성공 테스트")
    @Test
    public void signupSuccessTest() throws Exception {
        //given
        SignupRequest signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "loginId", "testMember");
        ReflectionTestUtils.setField(signupRequest, "password", "1q2w3e4r!@");
        ReflectionTestUtils.setField(signupRequest, "passwordCheck", "1q2w3e4r!@");
        ReflectionTestUtils.setField(signupRequest, "nickname", "테스터");
        ReflectionTestUtils.setField(signupRequest, "role", Role.ROLE_USER);

        Member member = new Member(signupRequest.getLoginId(), signupRequest.getPassword(), signupRequest.getNickname(), signupRequest.getRole());

        //when
        when(memberRepository.existsByLoginId(anyString())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member savedMember = memberService.signup(signupRequest);

        //then
        assertThat(savedMember.getLoginId()).isEqualTo(signupRequest.getLoginId());
        assertThat(savedMember.getPassword()).isEqualTo(signupRequest.getPassword());
        assertThat(savedMember.getNickname()).isEqualTo(signupRequest.getNickname());
        assertThat(savedMember.getRole()).isEqualTo(signupRequest.getRole());

        verify(memberRepository, times(1)).save(any(Member.class));     // 한번만 수행했는지 검증
        verify(memberRepository, times(1)).existsByLoginId(anyString());// 한번만 수행했는지 검증
    }

    @DisplayName("회원가입 실패 테스트 (password, passwordCheck 불일치)")
    @Test
    public void signupPasswordCheckFailTest() throws Exception {
        //given
        SignupRequest signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "loginId", "testMember");
        ReflectionTestUtils.setField(signupRequest, "password", "1q2w3e4r!@");
        ReflectionTestUtils.setField(signupRequest, "passwordCheck", "1q2w3e4r!"); // 비밀번호 확인 틀림
        ReflectionTestUtils.setField(signupRequest, "nickname", "테스터");

        // when then
        assertThatThrownBy(() -> memberService.signup(signupRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("회원가입 실패 테스트 (이미 loginId가 존재 할 때)")
    @Test
    public void signupAlreadyExistLoginIdFailTest() throws Exception {
        //given
        SignupRequest signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "loginId", "testMember");
        ReflectionTestUtils.setField(signupRequest, "password", "1q2w3e4r!@");
        ReflectionTestUtils.setField(signupRequest, "passwordCheck", "1q2w3e4r!@");
        ReflectionTestUtils.setField(signupRequest, "nickname", "테스터");

        //when
        when(memberRepository.existsByLoginId(anyString())).thenReturn(true);   // 이미 loginId가 존재 : true

        //then
        assertThatThrownBy(() -> memberService.signup(signupRequest))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("이미 존재하는 아이디입니다.");
    }

    @DisplayName("로그인 성공 테스트")
    @Test
    public void loginSuccessTest() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "loginId", "testMember");
        ReflectionTestUtils.setField(loginRequest, "password", "1q2w3e4r!@");

        //when
        when(memberRepository.findByLoginId(anyString())).thenReturn(Optional.of(member));
        when(jwtTokenUtil.createToken(any(JwtPayload.class))).thenReturn("testToken");

        String token = memberService.login(loginRequest);

        //then
        assertThat(token).isNotNull();
        verify(memberRepository, times(1)).findByLoginId(anyString());     // 한번만 수행했는지 검증
    }

    @DisplayName("로그인 실패 테스트 (loginId 틀렸을 때)")
    @Test
    public void loginNoLoginIdFailTest() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "loginId", "testMember");
        ReflectionTestUtils.setField(loginRequest, "password", "1q2w3e4r!@");

        //when
        when(memberRepository.findByLoginId(anyString())).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> memberService.login(loginRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("존재하지 않는 아이디입니다.");
        verify(memberRepository, times(1)).findByLoginId(anyString());
    }

    @DisplayName("로그인 실패 테스트 (password 틀렸을 때)")
    @Test
    public void loginWrongPasswordFailTest() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "loginId", "testMember");
        ReflectionTestUtils.setField(loginRequest, "password", "1q2w3e4r!");

        //when
        when(memberRepository.findByLoginId(anyString())).thenReturn(Optional.of(member));

        //then
        assertThatThrownBy(() -> memberService.login(loginRequest))
                .isInstanceOf(IncorrectPasswordException.class)
                .hasMessage("비밀번호가 틀렸습니다.");
        verify(memberRepository, times(1)).findByLoginId(anyString());
    }

}