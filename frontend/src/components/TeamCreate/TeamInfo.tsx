import React, { useState, useEffect } from "react";
import { useRecoilState, useRecoilValue } from "recoil";
import axios from "axios";
import {
  teamNameState,
  selectedTeamSizeState,
  teamListState,
  userState,
} from "../../state/authState";
import { useNavigate } from "react-router-dom";
import {
  StyledContainer,
  StyledFormItem,
  StyledImagePreview,
  StyledErrorMessage,
  StyledHeading,
  StyledLabel,
  StyledInput,
  ImageUploadContainer,
  StyledButton,
} from "./TeamInfoStyled";
import profileImg from "../../assets/profileImg.png";

export default function TeamInfo() {
  const [teamName, setTeamName] = useRecoilState(teamNameState);
  const [selectedTeamSize, setSelectedTeamSize] = useRecoilState(
    selectedTeamSizeState,
  );
  const [selectedFileName, setSelectedFileName] = useState<string | null>(null);
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const [teamList, setTeamList] = useRecoilState(teamListState);
  const [error, setError] = useState<string | null>(null);
  const user = useRecoilValue(userState);
  const navigate = useNavigate();

  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];

    if (file) {
      // 이미지 파일을 선택한 경우
      const reader = new FileReader();
      reader.readAsDataURL(file);

      reader.onload = () => {
        const result = reader.result as string;
        setSelectedImage(result);
      };
    }
  };

  const generateTeamId = () => {
    return `team_${Date.now()}`;
  };

  const handleCreateTeam = () => {
    let errorMessage = "";

    if (!teamName) {
      errorMessage = "팀 이름을 입력해주세요.";
    } else if (teamList.some((team) => team.name === teamName)) {
      errorMessage = "이미 있는 팀 이름입니다.";
    } else if (!selectedTeamSize) {
      errorMessage = "인원 수를 선택해주세요.";
    } else if (!selectedImage) {
      errorMessage = "이미지를 업로드해주세요.";
    }

    if (errorMessage) {
      setError(errorMessage);
      return;
    }

    setError(null);

    const newTeamId = generateTeamId();
    const newTeam = {
      id: newTeamId,
      name: teamName,
      size: selectedTeamSize,
      image: selectedImage,
      leaderId: user?.id || null,
    };
    setTeamList([...teamList, newTeam]);

    // 이동
    navigate("/homeview");
  };

  const handleFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const fileInput = e.target;
    const file = fileInput.files && fileInput.files[0];

    if (file) {
      setSelectedFileName(file.name);
      handleImageUpload(e);
    }
  };

  // 페이지 로드 시 초기값 설정
  useEffect(() => {
    setTeamName("");
    setSelectedTeamSize("");
    setSelectedImage(null);
    setError(null);
  }, []);

  return (
    <StyledContainer>
      <StyledHeading>팀 생성</StyledHeading>
      <StyledFormItem>
        <StyledLabel htmlFor="teamName">팀 이름</StyledLabel>
        <StyledInput
          type="text"
          id="teamName"
          value={teamName}
          placeholder="팀 이름"
          onChange={(e) => {
            setTeamName(e.target.value);
            setError(null); // 팀 이름이 변경될 때 에러 상태 초기화
          }}
        />
        {/* 이미 있는 팀 이름인 경우 에러 메시지 표시 */}
        {teamList.some((team) => team.name === teamName) && (
          <div style={{ color: "red" }}>이미 있는 팀 이름입니다.</div>
        )}
      </StyledFormItem>
      <StyledFormItem>
        <StyledLabel htmlFor="teamSize">인원 수</StyledLabel>
        <select
          title="teamSize"
          id="teamSize"
          value={selectedTeamSize}
          onChange={(e) => {
            setSelectedTeamSize(e.target.value);
            setError(null); // 인원 수 선택 시 에러 상태 초기화
          }}
        >
          <option value="" disabled>
            인원 수
          </option>
          <option value="1-9">1~9명</option>
          <option value="10-99">10~99명</option>
          <option value="100+">100명 이상</option>
        </select>
        <ImageUploadContainer>
          <img
            src={selectedImage || profileImg}
            alt="Selected"
            onClick={() => document.getElementById("imageUpload")?.click()}
          />
          <input
            title="imgupload"
            type="file"
            id="imageUpload"
            accept="image/*"
            onChange={handleFileInputChange}
          />
        </ImageUploadContainer>
      </StyledFormItem>
      <StyledFormItem>
        <StyledButton onClick={handleCreateTeam}>생성하기</StyledButton>
        {error && <StyledErrorMessage>{error}</StyledErrorMessage>}
      </StyledFormItem>
    </StyledContainer>
  );
}
