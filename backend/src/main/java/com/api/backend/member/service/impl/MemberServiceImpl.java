package com.api.backend.member.service.impl;

import com.api.backend.global.exception.CustomException;
import com.api.backend.global.redis.RedisService;
import com.api.backend.global.security.AuthService;
import com.api.backend.global.security.data.dto.TokenDto;
import com.api.backend.global.security.jwt.JwtTokenProvider;
import com.api.backend.member.data.dto.*;
import com.api.backend.member.data.entity.Member;
import com.api.backend.member.data.repository.MemberRepository;
import com.api.backend.member.data.type.Authority;
import com.api.backend.member.data.type.LoginType;
import com.api.backend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.api.backend.global.exception.type.ErrorCode.EMAIL_ALREADY_EXIST_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.PASSWORD_NOT_MATCH_EXCEPTION;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final AuthService authService;

    @Override
    public SignUpResponse register(SignUpRequest request) {

        boolean exists = this.memberRepository.existsByEmail(request.getEmail());
        if (exists) {
            throw new CustomException(EMAIL_ALREADY_EXIST_EXCEPTION);
        }

        if (!request.getPassword().equals(request.getRepassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH_EXCEPTION);
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        Member member = Member.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .nickName(request.getNickName())
                .sexType(request.getSexType())
                .loginType(LoginType.TEAMMATE)
                .authority(Authority.USER)
                .build();

        memberRepository.save(member);


        return SignUpResponse.builder()
                .email(member.getEmail())
                .message("이메일 인증후 로그인 가능합니다.")
                .build();
    }

    @Override
    public SignInResponse login(SignInRequest signInRequest) {

        Member member = memberRepository.findByEmail(signInRequest.getEmail()).orElseThrow();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member.getMemberId().toString(), signInRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 비밀번호 틀렸을때 BadCredentialsException 던짐 이부분 처리하는 로직 구현해야함

        TokenDto tokenDto = authService.generateToken(authentication.getName(), authService.getAuthorities(authentication));

        return SignInResponse.builder()
                .grantType("Bearer")
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }

    public LogoutResponse logout(String requestAccessTokenInHeader) {
        String requestAccessToken = authService.resolveToken(requestAccessTokenInHeader);
        String principal = authService.getPrincipal(requestAccessToken);

        String refreshTokenInRedis = redisService.getValues("RT:" + principal);
        if (refreshTokenInRedis != null) {
            redisService.deleteValues("RT:" + principal);
        }

        long expiration = jwtTokenProvider.getTokenExpirationTime(requestAccessToken) - new Date().getTime();
        redisService.setValues(requestAccessToken,
                "logout",
                expiration,
                TimeUnit.MILLISECONDS);

        return LogoutResponse.builder()
                .message("로그아웃되었습니다.")
                .build();
    }


}
