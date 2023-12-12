import React, { useState } from "react";
import axios, { AxiosError } from "axios";
import { useNavigate } from "react-router-dom";
import { StyledContainer, StyledFormItem } from "./SignInStyled";
import { useRecoilState } from "recoil";
import {
  isAuthenticatedState,
  accessTokenState,
  refreshTokenState,
  saveAccessToken,
  saveRefreshToken,
  useUser,
} from "../../state/authState";

const SignIn = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isAuthenticated, setIsAuthenticated] =
    useRecoilState(isAuthenticatedState);
  const [accessToken, setAccessToken] = useRecoilState(accessTokenState);
  const [refreshToken, setRefreshToken] = useRecoilState(refreshTokenState);
  const { setUser } = useUser();
  const navigate = useNavigate();

  const handleLogout = () => {
    saveAccessToken("");
    saveRefreshToken("");
    navigate("/signin");
    setIsAuthenticated(false);
  };

  const handleSignIn = async () => {
    if (!email.trim() || !password.trim()) {
      setError("입력되지 않은 항목이 있습니다.");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email.trim())) {
      setError("올바른 이메일 형식이 아닙니다.");
      return;
    }

    try {
      if (password.length < 8) {
        setError("비밀번호는 최소 8자 이상이어야 합니다.");
        return;
      }

      const response = await axios.post(
        "http://localhost:8080/sign-in",
        {
          email: email,
          password: password,
        },
        {
          withCredentials: true,
        },
      );

      const newAccessToken = response.data.accessToken;
      const newRefreshToken = response.data.refreshToken;

      if (newAccessToken && newRefreshToken) {
        // 토큰 저장
        saveAccessToken(newAccessToken);
        saveRefreshToken(newRefreshToken);

        // Recoil 상태 업데이트
        setAccessToken(newAccessToken);
        setRefreshToken(newRefreshToken);

        setIsAuthenticated(true);
        setUser({ id: email, name: response.data.name });
        navigate("/homeview");
      } else {
        setError("토큰이 올바르게 전달되지 않았습니다.");
      }
    } catch (error) {
      console.error("Sign In Error:", error);

      if (axios.isAxiosError(error)) {
        console.error("Axios Error Response:", error.response);
      }

      const axiosError = error as AxiosError;
      if (axiosError.response?.status === 401) {
        setError("올바른 이메일 또는 비밀번호를 입력하세요.");
      } else {
        setError("로그인 중 오류가 발생했습니다.");
      }
    }
  };

  const handleSignUp = () => {
    navigate("/signup");
  };

  return (
    <>
      <StyledContainer>
        <h2>로그인</h2>
        <br />
        <StyledFormItem>
          <input
            type="email"
            title="email"
            value={email}
            placeholder="이메일"
            onChange={(e) => setEmail(e.target.value)}
          />
        </StyledFormItem>
        <br />
        <StyledFormItem>
          <input
            type="password"
            title="password"
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </StyledFormItem>
        <br />
        <StyledFormItem>
          {isAuthenticated ? (
            <button onClick={handleLogout}>로그아웃</button>
          ) : (
            <button onClick={handleSignIn}>로그인</button>
          )}
        </StyledFormItem>
        <p>
          계정이 없으신가요?{" "}
          <span
            style={{ cursor: "pointer", color: "#333333" }}
            onClick={handleSignUp}
          >
            회원가입
          </span>
        </p>
        {error && <p style={{ color: "red" }}>{error}</p>}
      </StyledContainer>
    </>
  );
};

export default SignIn;
