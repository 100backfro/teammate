import React from "react";
import styled from "styled-components";
import { useParams } from "react-router-dom";
import { useRecoilValue } from "recoil";
import { teamListState, userState } from "../../state/authState";

const TeamDetail = () => {
  const { teamId } = useParams();
  const teamList = useRecoilValue(teamListState);
  const user = useRecoilValue(userState);
  const team = teamList.find((team) => team.id === teamId);

  if (!team) {
    return <div>팀을 찾을 수 없습니다.</div>;
  }

  const isTeamLeader = user && team.leaderId === user.id;

  return (
    <TeamDetailContainer>
      {/* <TeamName>{team.name}</TeamName> */}

      <BoxContainer>
        <EmptyBox>공유문서리스트</EmptyBox>
        <EmptyBox>캘린더</EmptyBox>
      </BoxContainer>

      {/* {team.image && <Image src={team.image} alt={`${team.name} 이미지`} />} */}
    </TeamDetailContainer>
  );
};

export default TeamDetail;

const TeamDetailContainer = styled.div`
  text-align: center;
`;

const TeamName = styled.h2`
  top: 0;
  background-color: #fff; /* 팀 명과 페이지 내용이 겹치는 것을 방지하기 위해 배경을 흰색으로 설정 */
  padding: 10px 0;
  margin: 0;
  z-index: 1000; /* 다른 요소들 위에 표시하기 위한 z-index 설정 */
`;

const EmptyBox = styled.div`
  flex: 1;
  border: 2px solid #333;
  height: 700px;
  border-radius: 30px;
  margin: 20px;
`;

const BoxContainer = styled.div`
  display: flex;
  margin-top: 20px;
`;

const Image = styled.img`
  max-width: 200px;
  max-height: 200px;
  margin-top: 20px;
`;
