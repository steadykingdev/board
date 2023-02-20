package com.study.board.repository;

import com.study.board.domain.Role;
import com.study.board.domain.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;

    @BeforeEach
    public void setup() {
        String loginId = "testMember";
        String password = "1q2w3e4r!";
        String nickname = "테스터";
        Member member = new Member(loginId, password, nickname, Role.ROLE_USER);
        savedMember = memberRepository.save(member);
    }

    @DisplayName("로그인 아이디로 해당 멤버가 존재하는지 확인하는 테스트 (존재할 때)")
    @Test
    public void shouldReturnTrueWhenEntityExist() {
        //given
        String loginId = "testMember";

        //when
        boolean isExist = memberRepository.existsByLoginId(loginId);

        //then
        assertThat(isExist).isEqualTo(true);
    }

    @DisplayName("로그인 아이디로 해당 멤버가 존재하는지 확인하는 테스트 (존재하지 않을 때)")
    @Test
    public void shouldReturnFalseWhenEntityDoesNotExist() {
        //given
        String loginId = "testMember2";

        //when
        boolean isExist = memberRepository.existsByLoginId(loginId);

        //then
        assertThat(isExist).isEqualTo(false);
    }

    @DisplayName("로그인 아이디로 멤버를 가져오는 쿼리 테스트(존재할 때")
    @Test
    public void shouldReturnMemberWhenEntityDoesExist() throws Exception {
        //given
        String loginId = "testMember";

        //when
        Member findMember = memberRepository.findByLoginId(loginId).get();

        //then
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getLoginId()).isEqualTo(savedMember.getLoginId());
        assertThat(findMember.getPassword()).isEqualTo(savedMember.getPassword());
        assertThat(findMember.getNickname()).isEqualTo(savedMember.getNickname());
        assertThat(findMember.getRole()).isEqualTo(savedMember.getRole());
    }

    @DisplayName("로그인 아이디로 멤버를 가져오는 쿼리 테스트(존재하지 않을 때")
    @Test
    public void shouldReturnMemberWhenEntityDoesNotExist() throws Exception {
        //given
        String loginId = "testMember1";

        //when then
        Optional<Member> result = memberRepository.findByLoginId(loginId);

        //then
        assertThat(result).isEmpty();
    }

}