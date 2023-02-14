package com.study.board.service;

import com.study.board.dto.SignupRequest;
import com.study.board.entity.Member;
import com.study.board.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member signup(SignupRequest signupRequest) {

        if (!signupRequest.checkPassword()) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        } else if (existsMember(signupRequest.getLoginId())) {
            throw new DataIntegrityViolationException("이미 존재하는 아이디입니다.");
        }

        Member member = new Member(signupRequest.getLoginId(), signupRequest.getPassword(), signupRequest.getNickname());
        return memberRepository.save(member);
    }
    private boolean existsMember(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }


}
