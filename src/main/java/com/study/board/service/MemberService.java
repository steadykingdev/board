package com.study.board.service;

import com.study.board.domain.JwtPayload;
import com.study.board.domain.Role;
import com.study.board.domain.dto.LoginRequest;
import com.study.board.domain.dto.MemberInfoResponse;
import com.study.board.domain.dto.SignupRequest;
import com.study.board.domain.entity.Member;
import com.study.board.exception.IncorrectPasswordException;
import com.study.board.exception.UserNotFoundException;
import com.study.board.repository.MemberRepository;
import com.study.board.util.FileStorage;
import com.study.board.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final JwtTokenUtil jwtTokenUtil;

    private final FileStorage fileStorage;

    private final String fileLocation;

    private final String COMMON_PROFILE = "src/main/resources/static/images/no_profile.png";

    public MemberService(MemberRepository memberRepository, JwtTokenUtil jwtTokenUtil, FileStorage fileStorage, @Value("${file.upload.location}") String fileLocation) {
        this.memberRepository = memberRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.fileStorage = fileStorage;
        this.fileLocation = fileLocation;
    }

    @Transactional
    public Member signup(SignupRequest signupRequest) throws IOException {

        if (!signupRequest.checkPassword()) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        } else if (existsMember(signupRequest.getLoginId())) {
            throw new DataIntegrityViolationException("이미 존재하는 아이디입니다.");
        }

        Member member = new Member(signupRequest.getLoginId(), signupRequest.getPassword(), signupRequest.getNickname(), Role.valueOf(signupRequest.getRole()));

        MultipartFile imgFile = signupRequest.getImgFile();

        if (imgFile != null) {
            String fileName = fileStorage.store(imgFile);
            member.setProfileImg(fileLocation, fileName);
        }

        return memberRepository.save(member);
    }

    public String login(LoginRequest loginRequest) {
        Member member = findByLoginId(loginRequest.getLoginId());
        if (!member.getPassword().equals(loginRequest.getPassword())) {
            throw new IncorrectPasswordException("비밀번호가 틀렸습니다.");
        }
        return jwtTokenUtil.createToken(new JwtPayload(member.getId(), member.getLoginId(), member.getRole()));
    }

    public MemberInfoResponse findById(Long memberId) throws Exception {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException("존재하지 않는 아이디입니다.");
                });

        String profilePath = member.getProfileImgPath();
        if (profilePath == null) {
            profilePath = COMMON_PROFILE;
        }
        byte[] img = getImage(profilePath);

        MemberInfoResponse memberResponse = new MemberInfoResponse(member.getLoginId(), member.getNickname(), member.getRole());
        memberResponse.setProfileImg(img);

        return memberResponse;
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

    private byte[] getImage(String profileImgPath) throws Exception {
        byte[] img = null;
        System.out.println("profileImgPath : " + profileImgPath);
        try {
            img = fileStorage.getImage(profileImgPath);
        } catch (Exception e) {
            throw new Exception("파일을 변환하는데 문제가 발생했습니다.");
        }

        return img;
    }
}
