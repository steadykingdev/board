package com.study.board.service;

import com.study.board.dto.SignupRequest;
import com.study.board.entity.Member;
import com.study.board.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock   // Mock 객체 생성
    private MemberRepository memberRepository;

    @InjectMocks    // Mock 객체를 주입해줌
    private MemberService memberService;

    @DisplayName("회원가입 성공 테스트")
    @Test
    public void signupTest() throws Exception {
        //given
        SignupRequest signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "loginId", "testMember");
        ReflectionTestUtils.setField(signupRequest, "password", "1q2w3e4r!@");
        ReflectionTestUtils.setField(signupRequest, "passwordCheck", "1q2w3e4r!@");
        ReflectionTestUtils.setField(signupRequest, "nickname", "테스터");

        Member member = new Member(signupRequest.getLoginId(), signupRequest.getPassword(), signupRequest.getNickname());

        //when
        when(memberRepository.existsByLoginId(anyString())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member savedMember = memberService.signup(signupRequest);

        //then
        assertThat(savedMember.getLoginId()).isEqualTo(signupRequest.getLoginId());
        assertThat(savedMember.getPassword()).isEqualTo(signupRequest.getPassword());
        assertThat(savedMember.getNickname()).isEqualTo(signupRequest.getNickname());

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
        ReflectionTestUtils.setField(signupRequest, "passwordCheck", "1q2w3e4r!");
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
}