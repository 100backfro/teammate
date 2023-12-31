import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../../axios";
import {
  TeamProfileContainer,
  TeamProfileTitle,
  ImageUploadContainer,
  NicknameContainer,
  ContainerWrapper,
  ButtonContainer,
  UserProfileInfo,
  UserProfileTitle,
  TeamProfileBox,
  Button,
  LinkContainer,
  TeamDisband,
} from "./MyTeamProfileStyled";
import { StyledLink } from "../../styles/CommonStyled.tsx";
import profileImg from "../../assets/profileImg.png";
import { Team } from "../../interface/interface.ts";
import { accessTokenState } from "../../state/authState";
import { useRecoilValue } from "recoil";

const MyTeamProfile: React.FC = () => {
  const [teamList, setTeamList] = useState<Team[]>([]);
  const [selectedTeam, setSelectedTeam] = useState<Team | null>(null);
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const [previewImage, setPreviewImage] = useState<string | null>(null);
  const [newSelectedImage, setNewSelectedImage] = useState<File | null>(null);
  const [nickName, setNickName] = useState("");
  const [userTeamData, setUserTeamData] = useState<{
    teamParticipantsId?: number;
    participantsProfileUrl?: string;
    teamNickName?: string;
  } | null>(null);
  const accessToken = useRecoilValue(accessTokenState);
  const navigator = useNavigate();

  // 팀 리스트 API 호출
  useEffect(() => {
    const fetchTeamList = async () => {
      try {
        const response = await axiosInstance.get("/member/participants");
        setTeamList(
          response.data.map((team: Team) => ({
            ...team,
            teamName: team.teamName,
          })),
        );
      } catch (error) {
        console.error("Error fetching team list:", error);
      }
    };

    fetchTeamList();
  }, [accessToken]);

  //프로필 수정
  const handleCreateTeam = async () => {
    if (!selectedTeam) {
      console.error("선택된 팀이 없습니다.");
      return;
    }
    const formData = new FormData();
    formData.append("teamNickName", nickName);
    if (userTeamData?.teamParticipantsId !== undefined) {
      formData.append(
        "teamParticipantsId",
        userTeamData.teamParticipantsId.toString(),
      );
    }

    if (newSelectedImage instanceof File) {
      formData.append("participantImg", newSelectedImage);
    }
    await axiosInstance.post(`/member/participant`, formData, {
      withCredentials: true,
      headers: {
        Authorization: `Bearer ${accessToken}`,
        "Content-Type": "multipart/form-data",
      },
    });
    window.alert("프로필이 수정되었습니다.");
    navigator("/homeview");
  };

  //이미지업로드
  const handleFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const fileInput = e.target;
    const file = fileInput.files && fileInput.files[0];

    if (file) {
      const reader = new FileReader();
      reader.readAsDataURL(file);

      reader.onload = () => {
        const result = reader.result as string;
        setNewSelectedImage(file);
        setPreviewImage(result);
        setSelectedImage(result);
      };

      reader.onerror = (error) => {
        console.error("Error reading the file:", error);
      };
    }
  };

  //팀 클릭시
  const handleTeamClick = async (team: Team) => {
    setSelectedTeam(team);

    try {
      await axiosInstance.get(`/member/participants?teamId=${team.teamId}`);
      const teamParticipant = team;
      setUserTeamData({
        teamParticipantsId: teamParticipant.teamParticipantsId,
        participantsProfileUrl: teamParticipant.participantsProfileUrl,
        teamNickName: teamParticipant.teamNickName,
      });
      setNickName(teamParticipant.teamNickName || "");
      setSelectedImage(null);
      setPreviewImage(null);
    } catch (error) {
      console.error("팀 참가자 데이터가 없습니다.", error);
    }
  };

  //탈퇴
  const handleLeaveTeam = async () => {
    try {
      if (!selectedTeam || userTeamData?.teamParticipantsId === undefined) {
        console.error("선택된 팀이 없거나 팀 참가자 ID가 없습니다.");
        return;
      }

      if (selectedTeam.teamRole === "LEADER") {
        window.alert("팀장 권한을 부여한 후 다시 시도해주세요.");
        return;
      }
      await axiosInstance.delete(`/team/${selectedTeam.teamId}/participant`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      window.alert("팀에서 탈퇴되었습니다.");
      navigator("/homeView");
      setSelectedTeam(null);
      setUserTeamData(null);
      setNickName("");
      setSelectedImage(null);
      setPreviewImage(null);
    } catch (error) {
      console.error("팀 탈퇴 실패:", error);
    }
  };
  return (
    <TeamProfileContainer>
      <UserProfileInfo>
        <UserProfileTitle>내 팀 프로필</UserProfileTitle>
        <LinkContainer>
          <StyledLink to="/myUserProfile">내 프로필로 이동</StyledLink>
        </LinkContainer>
        <select
          title="profile"
          onChange={(e) =>
            handleTeamClick(
              teamList.find((team) => team.teamName === e.target.value)!,
            )
          }
        >
          <option value="">조회할 팀을 선택하세요.</option>
          {teamList.map((team) => (
            <option key={team.teamId} value={team.teamName}>
              {team.teamName}
            </option>
          ))}
        </select>
      </UserProfileInfo>
      {selectedTeam && (
        <TeamProfileBox>
          <TeamProfileTitle>{selectedTeam.teamName} 프로필</TeamProfileTitle>
          <ContainerWrapper>
            <ImageUploadContainer>
              <img
                alt="profileImg"
                src={
                  typeof selectedImage === "string"
                    ? selectedImage
                    : previewImage ||
                      userTeamData?.participantsProfileUrl ||
                      profileImg
                }
                onClick={() => document.getElementById("imageUpload")?.click()}
              />
              <input
                title="imgUpload"
                type="file"
                id="imageUpload"
                accept="image/*"
                onChange={handleFileInputChange}
              />
            </ImageUploadContainer>
            <NicknameContainer>
              <input
                title="nickName"
                type="text"
                placeholder="닉네임 입력"
                id="nickname"
                value={nickName || userTeamData?.teamNickName || ""}
                onChange={(e) => setNickName(e.target.value)}
                onBlur={(e) => {
                  const trimmedValue = e.target.value.trim();
                  setNickName(trimmedValue); // 이 부분 수정
                }}
              />
            </NicknameContainer>
          </ContainerWrapper>
          <ButtonContainer>
            <Button onClick={handleCreateTeam}>변경하기</Button>
          </ButtonContainer>
          <TeamDisband onClick={handleLeaveTeam}>팀 탈퇴하기</TeamDisband>
        </TeamProfileBox>
      )}
    </TeamProfileContainer>
  );
};

export default MyTeamProfile;
