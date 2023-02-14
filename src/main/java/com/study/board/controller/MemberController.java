package com.study.board.controller;

import com.study.board.dto.CommonResponseFormat;
import com.study.board.dto.SignupRequest;
import com.study.board.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    public MemberController(@RequestBody MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<CommonResponseFormat> signup(@Valid @RequestBody SignupRequest signupRequest) {
        memberService.signup(signupRequest);
        return new ResponseEntity<>(CommonResponseFormat.createSuccessWithNoContent(), HttpStatus.CREATED);
    }
}
