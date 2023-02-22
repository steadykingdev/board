package com.study.board.controller;

import com.study.board.domain.JwtPayload;
import com.study.board.domain.dto.*;
import com.study.board.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    public MemberController(@RequestBody MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<CommonResponseFormat> signup(@RequestBody @Valid SignupRequest signupRequest) {
        memberService.signup(signupRequest);
        return new ResponseEntity<>(CommonResponseFormat.createSuccessWithNoContent(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponseFormat<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        String token = memberService.login(loginRequest);

        LoginResponse loginResponse = new LoginResponse(token);

        return new ResponseEntity<>(CommonResponseFormat.createSuccess(loginResponse), HttpStatus.OK);
    }

    @GetMapping("/myinfo")
    public ResponseEntity<CommonResponseFormat<MemberInfoResponse>> myInfo(@RequestAttribute("user") JwtPayload jwtPayload) {
        MemberInfoResponse memberInfoResponse = memberService.findById(jwtPayload.getId());
        return new ResponseEntity<>(CommonResponseFormat.createSuccess(memberInfoResponse), HttpStatus.OK);
    }
}
