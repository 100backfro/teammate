package com.api.backend.member.service;

import com.api.backend.member.data.dto.*;


public interface MemberService {

    SignUpResponse register(SignUpRequest request);

    SignInResponse login(SignInRequest signInRequest);

    LogoutResponse logout(String requestAccessToken);
}
