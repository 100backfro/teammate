package com.api.backend.global.security;

import com.api.backend.global.redis.RedisService;
import com.api.backend.global.security.data.dto.TokenDto;
import com.api.backend.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    public String getPrincipal(String requestAccessToken) {
        return jwtTokenProvider.getAuthentication(requestAccessToken).getName();
    }

    public String resolveToken(String requestAccessTokenInHeader) {
        if (requestAccessTokenInHeader != null && requestAccessTokenInHeader.startsWith("Bearer ")) {
            return requestAccessTokenInHeader.substring(7);
        }
        return null;
    }

    public TokenDto generateToken(String memberId, String authorities) {
        if (redisService.getValues("RT:" + memberId) != null) {
            redisService.deleteValues("RT:" + memberId); // 삭제
        }

        TokenDto tokenDto = jwtTokenProvider.createToken(memberId, authorities);
        saveRefreshToken(memberId, tokenDto.getRefreshToken());
        return tokenDto;
    }

    public TokenDto reissue(String requestAccessTokenInHeader, String requestRefreshToken) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);

        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);
        String principal = getPrincipal(requestAccessToken);

        String refreshTokenInRedis = redisService.getValues("RT:" + principal);
        if (refreshTokenInRedis == null) {
            return null;
        }

        if (!jwtTokenProvider.validateRefreshToken(requestRefreshToken) || !refreshTokenInRedis.equals(requestRefreshToken)) {
            redisService.deleteValues("RT:" + principal);
            return null;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authorities = getAuthorities(authentication);

        redisService.deleteValues("RT:" + principal);
        TokenDto tokenDto = jwtTokenProvider.createToken(principal, authorities);
        saveRefreshToken(principal, tokenDto.getRefreshToken());
        return tokenDto;
    }

    public boolean validate(String requestAccessTokenInHeader) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);
        return jwtTokenProvider.validateAccessTokenOnlyExpired(requestAccessToken); // true = 재발급
    }

    public String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    public void saveRefreshToken(String principal, String refreshToken) {
        redisService.setValues("RT:" + principal,
                refreshToken,
                jwtTokenProvider.getTokenExpirationTime(refreshToken),
                TimeUnit.MILLISECONDS);
    }


}
