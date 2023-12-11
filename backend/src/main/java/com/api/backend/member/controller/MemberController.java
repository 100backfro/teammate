package com.api.backend.member.controller;

import com.api.backend.global.security.AuthService;
import com.api.backend.global.security.data.dto.TokenDto;
import com.api.backend.member.data.dto.*;
import com.api.backend.member.data.dto.TeamParticipantUpdateRequest;
import com.api.backend.member.service.MemberService;
import com.api.backend.team.data.dto.TeamParticipantsDto;
import com.api.backend.team.service.TeamParticipantsService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TeamParticipantsService teamParticipantsService;
    private final AuthService authService;

    private final long COOKIE_EXPIRATION = 7776000;

    @PostMapping("/sign-up")
    public SignUpResponse signUp(
            @RequestBody SignUpRequest request){
        return this.memberService.register(request);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest signInRequest) {

        SignInResponse signInResponse = memberService.login(signInRequest);

        HttpCookie httpCookie = ResponseCookie.from("refresh-token", signInResponse.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION)
                .httpOnly(true)
                .secure(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, httpCookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + signInResponse.getAccessToken())
            .body(signInResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logOut(@RequestHeader("Authorization") String requestAccessToken) {

        LogoutResponse logoutResponse = memberService.logout(requestAccessToken);
        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                .maxAge(0)
                .path("/")
                .build();


        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(logoutResponse);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String requestAccessToken) {
        if (!authService.validate(requestAccessToken)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refresh-token") String requestRefreshToken,
                                     @RequestHeader("Authorization") String requestAccessToken) {
        TokenDto reissuedTokenDto = authService.reissue(requestAccessToken, requestRefreshToken);

        if (reissuedTokenDto != null) {
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", reissuedTokenDto.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .httpOnly(true)
                    .secure(true)
                    .build();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    // AT 저장
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokenDto.getAccessToken())
                    .build();

        } else {
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .build();
        }
    }


    @GetMapping("/member/participants")
    public ResponseEntity<Page<TeamParticipantsDto>> getTeamParticipantRequest(
        Principal principal,
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            TeamParticipantsDto.fromDtos(
                teamParticipantsService
                    .getTeamParticipantsByUserId(principal, pageable)
            )
        );
    }

    @PostMapping(value = "/member/participant",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TeamParticipantsDto> updateTeamParticipantContentRequest(
        @Valid TeamParticipantUpdateRequest teamParticipantUpdateRequest,
        Principal principal
    ) {
        return ResponseEntity.ok(
            TeamParticipantsDto.from(
                teamParticipantsService.updateParticipantContent(teamParticipantUpdateRequest, principal.getName())
            )
        );
    }
}
