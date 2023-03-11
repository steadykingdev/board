package com.study.board.controller;

import com.study.board.domain.JwtPayload;
import com.study.board.domain.dto.*;
import com.study.board.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<CommonResponseFormat> signup(@Valid SignupRequest signupRequest) throws IOException {

        memberService.signup(signupRequest);
        return new ResponseEntity<>(CommonResponseFormat.createSuccessWithNoContent(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponseFormat<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        String token = memberService.login(loginRequest);

        LoginResponse loginResponse = new LoginResponse(token);

        return new ResponseEntity<>(CommonResponseFormat.createSuccess(loginResponse), HttpStatus.OK);
    }

    @GetMapping("/member")
    public ResponseEntity<CommonResponseFormat<MemberInfoResponse>> myInfo(@RequestAttribute("user") JwtPayload jwtPayload) {
        MemberInfoResponse memberInfoResponse = memberService.myInfo(jwtPayload.getId());
        return new ResponseEntity<>(CommonResponseFormat.createSuccess(memberInfoResponse), HttpStatus.OK);
    }

    @PutMapping("/member/profile-image")
    public ResponseEntity<CommonResponseFormat> updateProfileImg(@RequestAttribute("user") JwtPayload jwtPayload,
                                                                 @RequestPart(required = false) MultipartFile imgFile) throws IOException {
        memberService.updateProfileImg(jwtPayload.getId(), imgFile);
        return new ResponseEntity<>(CommonResponseFormat.createSuccessWithNoContent(), HttpStatus.OK);
    }

    @PostMapping("/member")
    public ResponseEntity<CommonResponseFormat> deleteMember(@RequestAttribute("user") JwtPayload jwtPayload,
                                                             @RequestBody @Valid MemberDeleteRequest memberDeleteRequest) {
        memberService.deleteMember(jwtPayload.getId(), memberDeleteRequest);

        return new ResponseEntity<>(CommonResponseFormat.createSuccessWithNoContent(), HttpStatus.OK);
    }
}
