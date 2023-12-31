package com.api.backend.member.controller;

import com.api.backend.member.data.dto.LogoutResponse;
import com.api.backend.member.data.dto.MemberInfoResponse;
import com.api.backend.member.data.dto.SignInRequest;
import com.api.backend.member.data.dto.SignInResponse;
import com.api.backend.member.data.dto.SignUpRequest;
import com.api.backend.member.data.dto.TeamParticipantUpdateRequest;
import com.api.backend.member.data.dto.UpdateMemberPasswordRequest;
import com.api.backend.member.service.MemberService;
import com.api.backend.team.data.dto.TeamParticipantsDto;
import com.api.backend.team.service.TeamParticipantsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "회원")
@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final TeamParticipantsService teamParticipantsService;

  private final long COOKIE_EXPIRATION = 7776000;

  @ApiOperation(value = "회원가입 API", notes = "회원가입")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "이메일, 이메일인증하라는 메시지 반환"),
      @ApiResponse(code = 400, message = "회원가입폼에 유효성 체크")
  })
  @PostMapping("/sign-up")
  public ResponseEntity<?> signUp(
      @Valid @RequestBody SignUpRequest request,
      BindingResult bindingResult
  ) {

    if (bindingResult.hasErrors()) {
      Map<String, String> validatorResult = memberService.validateHandling(bindingResult);

      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(validatorResult);
    }

    return ResponseEntity.ok(this.memberService.register(request));
  }

  @ApiOperation(value = "이메일 중복여부확인 API", notes = "회원가입시 이메일이 중복되었는지 판단")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "해당이메일로 가입된 회원이 없으면 정상처리"),
      @ApiResponse(code = 400, message = "해당이메일로 가입된 회원이 있을시 예외처리")
  })
  @PostMapping("/sign-up/email-check")
  public ResponseEntity<Boolean> checkEmailDuplicate(
      @RequestBody Map<String, String> request) {
    String email = request.get("email");
    memberService.checkEamilDuplicate(email);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "가입링크 확인 API", notes = "가입링크에 저장된 정보로 회원 검증")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "회원아이디와 key값이 redis에 저장된값이 정확히 일치할경우 정상처리"),
      @ApiResponse(code = 400, message = "회원아이디와 key값이 redis에서 찾을수 없을때 예외처리"),
      @ApiResponse(code = 400, message = "해당이메일로 가입된 회원이 없을시 예외처리")
  })
  @GetMapping("/email-verify/{key}/{email}")
  public ResponseEntity<String> getVerify(@PathVariable("key") String key,
      @PathVariable("email") String email) {

    boolean result = memberService.verifyEmail(key, email);

    if (result) {
      return ResponseEntity.ok("이메일 인증에 성공했습니다.");
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("이메일 인증에 실패했습니다. 이메일을 다시 확인해주세요");
    }
  }

  @ApiOperation(value = "회원 로그인 API", notes = "이메일과 비밀번호로 로그인")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "access토큰과 refresh토큰 반환"),
      @ApiResponse(code = 400, message = "입력폼에 누락이 발생할때 예외처리"),
      @ApiResponse(code = 400, message = "해당이메일로 가입한 회원이 없을시 예외처리"),
      @ApiResponse(code = 400, message = "메일인증이 되지 않았을시 예외처리")
  })
  @PostMapping("/sign-in")
  public ResponseEntity<?> signIn(
      @RequestBody @Valid SignInRequest signInRequest,
      BindingResult bindingResult
  ) {
    if (bindingResult.hasErrors()) {
      Map<String, String> validatorResult = memberService.validateHandling(bindingResult);

      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(validatorResult);
    }

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

  @GetMapping("/social-success/")
  public ResponseEntity<?> socialLoginSuccuss(
          @RequestParam(name = "access_token") String accessToken,
          @RequestParam(name = "refresh_token") String refreshToken
  ) throws URISyntaxException {
    SignInResponse signInResponse = memberService.socialLogin(accessToken, refreshToken);
    URI redirectUri = new URI("http://loclhost:5173");
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(redirectUri);

    return new ResponseEntity<>(signInResponse, httpHeaders, HttpStatus.MOVED_PERMANENTLY);
  }
  @ApiOperation(value = "회원 로그아웃 API", notes = "헤더의 토큰정보를 바탕으로 로그아웃")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "정상처리시 토큰 삭제후 헤더값 삭제")
  })
  @PostMapping("/logout")
  public ResponseEntity<LogoutResponse> logOut(
      @RequestHeader("Authorization") String requestAccessToken) {

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


  @GetMapping("/my-page")
  ResponseEntity<?> getMemberInfo(
      @RequestHeader("Authorization") String requestAccessTokenInHeader
  ) {
    MemberInfoResponse memberInfo = memberService.getMemberInfo(requestAccessTokenInHeader);

    return ResponseEntity.ok(memberInfo);
  }


  @PutMapping("/member/password")
  public ResponseEntity<?> updateMemberPassword(
      @RequestHeader("Authorization") String accessToken,
      @RequestBody @Valid UpdateMemberPasswordRequest updateMemberPasswordRequest
  ) {

    memberService.updateMemberPassword(accessToken, updateMemberPasswordRequest);

    return ResponseEntity.ok().build();
  }


  @ApiOperation(value = "내가 속한 팀 참가자 조회 API", notes = "내가 속한 팀 참가자의 정보들을 반환")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "팀 참가자 정보들을 반환")
  })
  @GetMapping("/member/participants")
  public ResponseEntity<List<TeamParticipantsDto>> getTeamParticipantRequest(
      @ApiIgnore Principal principal
  ) {
    return ResponseEntity.ok(
        teamParticipantsService
            .getTeamParticipantsByUserId(
                Long.valueOf(principal.getName())
            )
            .stream()
            .map(TeamParticipantsDto::from)
            .collect(Collectors.toList())
    );
  }

  @ApiOperation(value = "팀 참가자 수정 API", notes = "참가자 이미지, 닉네임을 할 수 있다.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "수정된 팀 참가자 정보를 반환"),
      @ApiResponse(code = 200, message = "팀원이 아닌 경우, 허용되지 않은 회원,팀이 해체된 경우"),
  })
  @PostMapping(value = "/member/participant", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<TeamParticipantsDto> updateTeamParticipantContentRequest(
      @Valid TeamParticipantUpdateRequest teamParticipantUpdateRequest,
      @ApiIgnore Principal principal
  ) {
    return ResponseEntity.ok(
        TeamParticipantsDto.from(
            teamParticipantsService.updateParticipantContent(teamParticipantUpdateRequest,
                Long.valueOf(principal.getName()))
        )
    );
  }
}
