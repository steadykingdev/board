package com.study.board.repository;

import com.study.board.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("로그인 아이디로 해당 멤버가 존재하는지 확인하는 테스트 (존재할 때)")
    @Test
    public void shouldReturnTrueWhenEntityExist() {
        //given
        String loginId = "testMember";
        Member member = new Member(loginId, "1q2w3e4r1!", "테스터");
        memberRepository.save(member);

        //when
        boolean isExist = memberRepository.existsByLoginId(loginId);

        //then
        assertThat(isExist).isEqualTo(true);
    }

    @DisplayName("로그인 아이디로 해당 멤버가 존재하는지 확인하는 테스트 (존재하지 않을 때)")
    @Test
    public void shouldReturnFalseWhenEntityDoesNotExist() {
        //given
        String loginId = "testMember";

        //when
        boolean isExist = memberRepository.existsByLoginId(loginId);

        //then
        assertThat(isExist).isEqualTo(false);
    }
}