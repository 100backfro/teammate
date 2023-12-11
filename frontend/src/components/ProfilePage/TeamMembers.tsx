import React, { useState } from "react";
import styled from "styled-components";
import profileImg from "../../assets/profileImg.png";

interface MemberInputProps {
  isTeamLeader?: boolean;
}

const TeamProfileContainer = styled.div`
  text-align: center;
  position: absolute;
  top: 45%;
  left: 50%;
  transform: translate(-50%, -50%);
`;
const TeamProfileTitle = styled.h2`
  text-align: center;
  margin-bottom: 30px;
`;

const TeamInfoContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
`;

const TeamImage = styled.img`
  max-width: 100px;
  max-height: 100px;
  border-radius: 50%;
  margin-right: 20px;
`;

const TeamInfo = styled.div`
  display: flex;
  flex-direction: column;
`;

const TeamName = styled.span`
  font-weight: bold;
`;

const SelectContainer = styled.div`
  text-align: center;
  margin-top: 10px;
`;

const SearchInput = styled.input`
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
  margin: 20px 0 10px 0;
`;

const MemberContainer = styled.div`
  max-height: 150px;
  overflow-y: auto;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 5px;
  display: flex;
  flex-direction: column;
`;

const MemberInput = styled.input<MemberInputProps>`
  width: 100%;
  box-sizing: border-box;
  margin-bottom: 5px;
  font-weight: ${({ isTeamLeader }) => (isTeamLeader ? "bold" : "normal")};
  border: none;
  padding: 5px;
  background-color: transparent;
  cursor: pointer;

  &:hover {
    background-color: #f0f0f0;
  }
`;

const TeamMembers = () => {
  const teamInfo = {
    name: "팀 명", // 실제 데이터로 교체
    image: "팀 이미지 경로", // 실제 데이터로 교체
  };

  // 팀원 목록
  const teamMembers = [
    { id: 1, name: "팀원1" },
    { id: 2, name: "팀원2" },
    { id: 3, name: "팀원3" },
    { id: 4, name: "팀원4" },
    { id: 5, name: "김팀장" },
    { id: 6, name: "팀원6" },
    { id: 7, name: "팀원7" },
    { id: 8, name: "팀원8" },
    // ... 다른 팀원 데이터
  ];

  // 검색어 상태
  const [searchTeam, setSearchTeam] = useState("");

  // 검색된 팀원 목록
  const filteredTeamMembers = teamMembers
    .filter((member) =>
      member.name.toLowerCase().includes(searchTeam.toLowerCase()),
    )
    .sort((a, b) => (a.name === "김팀장" ? -1 : b.name === "김팀장" ? 1 : 0));

  return (
    <div>
      <TeamProfileContainer>
        <TeamProfileTitle>팀 프로필</TeamProfileTitle>

        <TeamInfoContainer>
          <TeamImage src={profileImg} alt={teamInfo.name} />
          <TeamInfo>
            <TeamName>{teamInfo.name}</TeamName>
          </TeamInfo>
        </TeamInfoContainer>

        <SelectContainer>
          <SearchInput
            type="text"
            id="teamMemberSearch"
            placeholder="팀원 검색"
            value={searchTeam}
            onChange={(e) => setSearchTeam(e.target.value)}
          />
        </SelectContainer>

        {/* 팀원 목록 */}
        <SelectContainer>
          {filteredTeamMembers.length > 0 && (
            <MemberContainer>
              {filteredTeamMembers.map((member) => (
                <MemberInput
                  key={member.id}
                  type="text"
                  value={`${member.name} ${
                    member.name === "김팀장" ? "(팀장)" : ""
                  }`}
                  readOnly
                  isTeamLeader={member.name === "김팀장"}
                />
              ))}
            </MemberContainer>
          )}
        </SelectContainer>
      </TeamProfileContainer>
    </div>
  );
};

export default TeamMembers;
