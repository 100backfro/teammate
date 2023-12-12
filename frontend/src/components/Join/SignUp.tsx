import React, { useState, ChangeEvent } from "react";
import axios from "axios";
import { StyledContainer, StyledFormItem } from "./SignUpStyled.tsx";
import * as Regex from "../../common/Regex.ts";

interface SignUpProps {}

const SignUp: React.FC<SignUpProps> = () => {
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [repassword, setRepassword] = useState<string>("");
  const [name, setName] = useState<string>("");
  const [nickName, setNickname] = useState<string>("");
  const [sexType, setSexType] = useState<string>("");
  const [isIdAvailable, setIsIdAvailable] = useState<boolean | null>(null);
  const [isEmailFormatValid, setIsEmailFormatValid] = useState<boolean | null>(
    null,
  );
  const [isRepasswordValid, setIsRepasswordValid] = useState<boolean>(true);
  const [isNicknameAvailable, setIsNicknameAvailable] = useState<
    boolean | null
  >(null);
  const [isNicknameFormatValid, setIsNicknameFormatValid] = useState<
    boolean | null
  >(null);
  const [signupMessage, setSignupMessage] = useState<string | null>(null);

  const isEmailValid = (email: string): boolean =>
    Regex.emailRegex.test(email.trim());
  const isNicknameValid = (nickName: string): boolean =>
    Regex.nicknameRegex.test(nickName.trim());

  const handleSignUp = () => {
    if (
      [email, password, repassword, name, nickName].some(
        (value) => value.trim() === "",
      )
    ) {
      setSignupMessage("입력되지 않은 항목이 있습니다.");
      return;
    }
    if (password !== repassword) {
      setSignupMessage("비밀번호가 일치하지 않습니다.");
      setIsRepasswordValid(false);
      return;
    }
    if (password.length < 4) {
      setSignupMessage("비밀번호는 4자리 이상이어야 합니다.");
      return;
    }
    if (!sexType) {
      setSignupMessage("성별을 선택하세요.");
      return;
    }
    if (isIdAvailable === null) {
      setSignupMessage("아이디 중복 확인이 필요합니다.");
    } else if (
      isIdAvailable &&
      isRepasswordValid &&
      isEmailFormatValid &&
      isNicknameAvailable
    ) {
      axios
        .post(
          "http://localhost:8080/sign-up",
          {
            email: email,
            password: password,
            nickName: nickName,
            name: name,
            repassword: repassword,
            sexType: sexType,
          },
          {
            withCredentials: true,
          },
        )
        .then((res) => {
          console.log(res.data);
        });
    } else if (!isIdAvailable) {
      setSignupMessage("아이디가 이미 사용 중입니다.");
    }
    if (isNicknameAvailable === null) {
      setSignupMessage("닉네임 중복 확인이 필요합니다.");
    } else if (!isNicknameAvailable) {
      setSignupMessage("닉네임이 이미 사용 중입니다.");
    }
  };

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    switch (name) {
      case "email":
        setEmail(value);
        setIsIdAvailable(null);
        setIsEmailFormatValid(isEmailValid(value));
        break;
      case "password":
        setPassword(value);
        setIsRepasswordValid(true);
        break;
      case "repassword":
        setRepassword(value);
        setIsRepasswordValid(true);
        break;
      case "name":
        setName(value);
        break;
      case "nickname":
        setNickname(value);
        setIsNicknameAvailable(null);
        setIsNicknameFormatValid(value ? isNicknameValid(value) : null);
        break;
      default:
        break;
    }
  };

  const handleCheckIdAvailability = () => {
    if (isEmailFormatValid === null || !isEmailFormatValid) {
      return;
    }
    const dummyApiCall = () =>
      Promise.resolve({ isAvailable: email !== "test@naver.com" });
    dummyApiCall().then((response) => {
      setIsIdAvailable(response.isAvailable);
    });
  };

  const handleCheckNicknameAvailability = () => {
    if (!nickName || !isNicknameFormatValid) {
      setIsNicknameAvailable(null);
      return;
    }
    const dummyApiCall = () =>
      Promise.resolve({ isAvailable: nickName !== "닉네임" });
    dummyApiCall().then((response) => {
      setIsNicknameAvailable(response.isAvailable);
    });
  };

  return (
    <StyledContainer>
      <h2>회원가입</h2>
      <br />
      <StyledFormItem>
        <input
          type="email"
          id="email"
          name="email"
          value={email}
          onChange={handleInputChange}
          placeholder="이메일"
        />
        <button onClick={handleCheckIdAvailability}>중복 확인</button>
      </StyledFormItem>
      {isIdAvailable !== null && (
        <span style={{ color: isIdAvailable ? "green" : "red" }}>
          {isIdAvailable
            ? "사용 가능한 아이디입니다."
            : "이미 사용 중인 아이디입니다."}
        </span>
      )}
      {isEmailFormatValid !== null && !isEmailFormatValid && (
        <span style={{ color: "red" }}>{"올바른 이메일 형식이 아닙니다."}</span>
      )}
      <br />

      <StyledFormItem>
        <input
          type="password"
          id="password"
          name="password"
          value={password}
          onChange={handleInputChange}
          placeholder="비밀번호 (4자 이상)"
        />
      </StyledFormItem>
      {password.length < 4 && password.length > 0 && (
        <span style={{ color: "red" }}>
          비밀번호는 4자리 이상이어야 합니다.
        </span>
      )}
      <br />

      <StyledFormItem>
        <input
          type="password"
          id="repassword"
          name="repassword"
          placeholder="비밀번호 확인"
          value={repassword}
          onChange={handleInputChange}
          onBlur={() => {
            if (repassword && repassword !== password) {
              setIsRepasswordValid(false);
            } else {
              setIsRepasswordValid(true);
            }
          }}
        />
      </StyledFormItem>
      {isRepasswordValid !== null && (
        <span style={{ color: isRepasswordValid ? "green" : "red" }}>
          {!isRepasswordValid && "비밀번호가 일치하지 않습니다."}
        </span>
      )}
      <br />

      <StyledFormItem>
        <input
          type="text"
          id="name"
          name="name"
          value={name}
          onChange={handleInputChange}
          placeholder="이름"
        />
      </StyledFormItem>
      <br />

      <StyledFormItem>
        <input
          type="text"
          id="nickname"
          name="nickname"
          value={nickName}
          onChange={handleInputChange}
          placeholder="닉네임 (한글 2~10자)"
        />
        <button onClick={handleCheckNicknameAvailability}>중복 확인</button>
      </StyledFormItem>
      {isNicknameAvailable !== null && (
        <span style={{ color: isNicknameAvailable ? "green" : "red" }}>
          {isNicknameAvailable
            ? "사용 가능한 닉네임입니다."
            : "이미 사용 중인 닉네임입니다."}
        </span>
      )}
      {isNicknameFormatValid !== null && !isNicknameFormatValid && (
        <span style={{ color: "red" }}>
          {"한글로 2~10자 이내로 입력해주세요."}
        </span>
      )}
      <br />

      <StyledFormItem>
        <input
          type="radio"
          id="male"
          name="sexType"
          value="MALE"
          checked={sexType === "MALE"}
          onChange={() => setSexType("MALE")}
        />
        <label htmlFor="male">남성</label>

        <input
          type="radio"
          id="female"
          name="sexType"
          value="FEMALE"
          checked={sexType === "FEMALE"}
          onChange={() => setSexType("FEMALE")}
        />
        <label htmlFor="female">여성</label>
      </StyledFormItem>
      <br />

      <StyledFormItem>
        <button onClick={handleSignUp}>회원 가입</button>
      </StyledFormItem>

      {signupMessage && <p style={{ color: "red" }}>{signupMessage}</p>}
    </StyledContainer>
  );
};

export default SignUp;
