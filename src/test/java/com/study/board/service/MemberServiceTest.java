package com.study.board.service;

import com.study.board.domain.JwtPayload;
import com.study.board.domain.Role;
import com.study.board.domain.dto.LoginRequest;
import com.study.board.domain.dto.MemberDeleteRequest;
import com.study.board.domain.dto.MemberInfoResponse;
import com.study.board.domain.dto.SignupRequest;
import com.study.board.domain.entity.Member;
import com.study.board.exception.IncorrectPasswordException;
import com.study.board.exception.UserNotFoundException;
import com.study.board.repository.MemberRepository;
import com.study.board.util.FileStorage;
import com.study.board.util.JwtTokenUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberServiceTest {

    @MockBean   // Mock 객체 생성
    private MemberRepository memberRepository;

    @MockBean
    private FileStorage fileStorage;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Value("${file.upload.location}")
    private String fileLocation;

    @Value("${myapp.server.host}")
    private String imgHost;

    private String COMMON_PROFILE = "/images/no_profile.png";

    MockMultipartFile imgFile;

    @Autowired
    private MemberService memberService;

    private Member member;

    @BeforeEach
    public void setUp() {
        member = new Member();
        ReflectionTestUtils.setField(member, "id", 1253L);
        ReflectionTestUtils.setField(member, "loginId", "testMember");
        ReflectionTestUtils.setField(member, "password", "1q2w3e4r!@");
        ReflectionTestUtils.setField(member, "nickname", "테스터");
        ReflectionTestUtils.setField(member, "role", Role.ROLE_USER);

        imgFile = new MockMultipartFile(
                "imgFile",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "imgFile".getBytes()
        );
    }

    @DisplayName("회원가입 성공 테스트(프로필 이미지 x)")
    @Order(1)
    @Test
    public void signupSuccessWithoutProfileTest() throws Exception {
        //given
        SignupRequest signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "loginId", "testMember");
        ReflectionTestUtils.setField(signupRequest, "password", "1q2w3e4r!@");
        ReflectionTestUtils.setField(signupRequest, "passwordCheck", "1q2w3e4r!@");
        ReflectionTestUtils.setField(signupRequest, "nickname", "테스터");
        ReflectionTestUtils.setField(signupRequest, "role", "ROLE_USER");

        Member member = new Member(signupRequest.getLoginId(), signupRequest.getPassword(), signupRequest.getNickname(), Role.valueOf(signupRequest.getRole()));

        //when
        when(memberRepository.existsByLoginId(anyString())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member savedMember = memberService.signup(signupRequest);

        //then
        assertThat(savedMember.getLoginId()).isEqualTo(signupRequest.getLoginId());
        assertThat(savedMember.getPassword()).isEqualTo(signupRequest.getPassword());
        assertThat(savedMember.getNickname()).isEqualTo(signupRequest.getNickname());
        assertThat(savedMember.getRole()).isEqualTo(Role.valueOf(signupRequest.getRole()));
        assertThat(savedMember.getProfileImgPath()).isNull();

        verify(memberRepository, times(1)).save(any(Member.class));     // 한번만 수행했는지 검증
        verify(memberRepository, times(1)).existsByLoginId(anyString());// 한번만 수행했는지 검증
    }

    @DisplayName("회원가입 성공 테스트(프로필 이미지 o)")
    @Order(2)
    @Test
    public void signupSuccessWithProfileTest() throws Exception {
        //given

        SignupRequest signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "loginId", "testMember");
        ReflectionTestUtils.setField(signupRequest, "password", "1q2w3e4r!@");
        ReflectionTestUtils.setField(signupRequest, "passwordCheck", "1q2w3e4r!@");
        ReflectionTestUtils.setField(signupRequest, "nickname", "테스터");
        ReflectionTestUtils.setField(signupRequest, "role", Role.ROLE_USER.toString());
        ReflectionTestUtils.setField(signupRequest, "imgFile", imgFile);

        Member member = new Member(signupRequest.getLoginId(), signupRequest.getPassword(), signupRequest.getNickname(), Role.valueOf(signupRequest.getRole()));
        member.setProfileImg(fileLocation, imgFile.getOriginalFilename());

        //when
        when(memberRepository.existsByLoginId(anyString())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(fileStorage.store(imgFile)).thenReturn(imgFile.getOriginalFilename());

        Member savedMember = memberService.signup(signupRequest);

        //then
        assertThat(savedMember.getLoginId()).isEqualTo(signupRequest.getLoginId());
        assertThat(savedMember.getPassword()).isEqualTo(signupRequest.getPassword());
        assertThat(savedMember.getNickname()).isEqualTo(signupRequest.getNickname());
        assertThat(savedMember.getRole()).isEqualTo(Role.valueOf(signupRequest.getRole()));
        assertThat(savedMember.getProfileImgPath()).isEqualTo(fileLocation + "/" + imgFile.getOriginalFilename());

        verify(memberRepository, times(1)).save(any(Member.class));     // 한번만 수행했는지 검증
        verify(memberRepository, times(1)).existsByLoginId(anyString());// 한번만 수행했는지 검증
    }

    @DisplayName("회원가입 실패 테스트 (password, passwordCheck 불일치)")
    @Order(3)
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
    @Order(4)
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
    @Order(5)
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
    @Order(6)
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
    @Order(7)
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

    @DisplayName("내 정보 조회 테스트(프로필 이미지 O)")
    @Order(8)
    @Test
    public void getMyInfoSuccessWithProfileTest() throws Exception {
        //given
        Long memberId = member.getId();
        ReflectionTestUtils.setField(member, "profileImgPath", fileLocation + "/profileImg.png");

        //when
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        MemberInfoResponse memberResponse = memberService.myInfo(memberId);

        //then
        assertThat(memberResponse.getLoginId()).isEqualTo(member.getLoginId());
        assertThat(memberResponse.getNickname()).isEqualTo(member.getNickname());
        // 현재 host와 path를 가져오지 못해 하드코딩해놓음. (다시 봐야함)
        assertThat(memberResponse.getProfileImg()).isEqualTo(imgHost + member.getProfileImgPath());
        assertThat(memberResponse.getRole()).isEqualTo(member.getRole());

        verify(memberRepository, times(1)).findById(anyLong());
    }

    @DisplayName("내 정보 조회 테스트(프로필 이미지 X)")
    @Order(9)
    @Test
    public void getMyInfoSuccessWithNoProfileTest() throws Exception {
        //given
        Long memberId = member.getId();

        //when
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        MemberInfoResponse memberResponse = memberService.myInfo(memberId);

        //then
        assertThat(memberResponse.getLoginId()).isEqualTo(member.getLoginId());
        assertThat(memberResponse.getNickname()).isEqualTo(member.getNickname());
        assertThat(memberResponse.getProfileImg()).isEqualTo(COMMON_PROFILE);
        assertThat(memberResponse.getRole()).isEqualTo(member.getRole());

        verify(memberRepository, times(1)).findById(anyLong());
    }

    @DisplayName("회원 삭제 성공")
    @Order(10)
    @Test
    public void deleteMemberSuccessTest() throws Exception {
        //given
        Long memberId = member.getId();
        MemberDeleteRequest memberDeleteRequest = new MemberDeleteRequest();
        ReflectionTestUtils.setField(memberDeleteRequest, "password", "1q2w3e4r!@");

        //when
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        memberService.deleteMember(memberId, memberDeleteRequest);

        //then
        verify(memberRepository, times(1)).findById(anyLong());
        verify(memberRepository, times(1)).delete(member);
    }

    @DisplayName("회원 삭제 실패(회원 X)")
    @Order(11)
    @Test
    public void deleteMemberNoSearchMemberFailTest() throws Exception {
        //given
        Long memberId = member.getId();
        MemberDeleteRequest memberDeleteRequest = new MemberDeleteRequest();
        ReflectionTestUtils.setField(memberDeleteRequest, "password", "1q2w3e4r!@");

        //when
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> memberService.deleteMember(memberId, memberDeleteRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("존재하지 않는 아이디입니다.");
        verify(memberRepository, times(1)).findById(anyLong());
    }

    @DisplayName("회원 삭제 실패(비밀번호 매치 X)")
    @Order(12)
    @Test
    public void deleteMemberIncorrectPasswordFailTest() throws Exception {
        //given
        Long memberId = member.getId();
        MemberDeleteRequest memberDeleteRequest = new MemberDeleteRequest();
        ReflectionTestUtils.setField(memberDeleteRequest, "password", "1q2w3e4r!");

        //when
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        //then
        assertThatThrownBy(() -> memberService.deleteMember(memberId, memberDeleteRequest))
                .isInstanceOf(IncorrectPasswordException.class)
                .hasMessage("비밀번호가 틀렸습니다.");
        verify(memberRepository, times(1)).findById(anyLong());
    }

    @DisplayName("회원 프로필 이미지 수정 성공")
    @Order(13)
    @Test
    public void updateMemberSuccessTest() throws Exception {
        //given
        Long memberId = member.getId();
        member.setProfileImg(fileLocation, imgFile.getOriginalFilename());

        //when
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(fileStorage.store(imgFile)).thenReturn(imgFile.getOriginalFilename());
        memberService.updateProfileImg(memberId, imgFile);

        //then
        verify(memberRepository, times(1)).findById(anyLong());
        assertThat(member.getProfileImgPath()).isEqualTo(fileLocation + "/" + imgFile.getOriginalFilename());
    }

    @DisplayName("회원 프로필 이미지 수정 성공(이미지 x)")
    @Order(14)
    @Test
    public void updateMemberSuccessWithNoImageFileTest() throws Exception {
        //given
        Long memberId = member.getId();
        member.setProfileImg(fileLocation, imgFile.getOriginalFilename());
        //when
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        memberService.updateProfileImg(memberId, null);

        //then
        verify(memberRepository, times(1)).findById(anyLong());
        assertThat(member.getProfileImgPath()).isNull();
    }
}