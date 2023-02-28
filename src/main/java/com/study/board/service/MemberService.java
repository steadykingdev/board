package com.study.board.service;

import com.study.board.domain.JwtPayload;
import com.study.board.domain.dto.LoginRequest;
import com.study.board.domain.dto.MemberInfoResponse;
import com.study.board.domain.dto.SignupRequest;
import com.study.board.domain.entity.Member;
import com.study.board.exception.IncorrectPasswordException;
import com.study.board.exception.UserNotFoundException;
import com.study.board.repository.MemberRepository;
import com.study.board.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${file.upload.location}")
    private String fileLocation;


    public MemberService(MemberRepository memberRepository, JwtTokenUtil jwtTokenUtil) {
        this.memberRepository = memberRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Transactional
    public Member signup(SignupRequest signupRequest, @Nullable MultipartFile imgFile) throws IOException {

        if (!signupRequest.checkPassword()) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        } else if (existsMember(signupRequest.getLoginId())) {
            throw new DataIntegrityViolationException("이미 존재하는 아이디입니다.");
        }
        Member member = new Member(signupRequest.getLoginId(), signupRequest.getPassword(), signupRequest.getNickname(), signupRequest.getRole());

        if (imgFile != null) {
            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString() + "_" + imgFile.getOriginalFilename();
            File profileImg = new File(fileLocation, fileName);
            imgFile.transferTo(profileImg);
            member.setProfileImg(fileName, fileLocation);
        }
        return memberRepository.save(member);
    }
//
//    @Transactional
//    public Member signupWithProfile(SignupRequest signupRequest, MultipartFile imgFile) throws IOException {
//
//        if (!signupRequest.checkPassword()) {
//            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
//        } else if (existsMember(signupRequest.getLoginId())) {
//            throw new DataIntegrityViolationException("이미 존재하는 아이디입니다.");
//        }
//
//        UUID uuid = UUID.randomUUID();
//        String fileName = uuid.toString() + "_" + imgFile.getOriginalFilename();
//        File profileImg = new File(fileLocation, fileName);
//        imgFile.transferTo(profileImg);
//
//        Member member = new Member(signupRequest.getLoginId(), signupRequest.getPassword(), signupRequest.getNickname(), signupRequest.getRole());
//        member.setProfileImg(fileName, fileLocation);
//
//        return memberRepository.save(member);
//    }

    public String login(LoginRequest loginRequest) {
        Member member = findByLoginId(loginRequest.getLoginId());
        if (!member.getPassword().equals(loginRequest.getPassword())) {
            throw new IncorrectPasswordException("비밀번호가 틀렸습니다.");
        }
        return jwtTokenUtil.createToken(new JwtPayload(member.getId(), member.getLoginId(), member.getRole()));
    }

    public MemberInfoResponse findById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException("존재하지 않는 아이디입니다.");
                });
        return new MemberInfoResponse(member.getLoginId(), member.getNickname(), member.getRole());
    }

    private boolean existsMember(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    private Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException("존재하지 않는 아이디입니다.");
                });
    }
}
