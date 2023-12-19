// PersonalAlarm.tsx
import React from "react";
import styled from "styled-components";

const AlarmContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #ccc;
  padding: 10px;
  margin-bottom: 10px;
`;

const AlarmContent = styled.p`
  flex-grow: 1;
  margin-right: 10px; /* 오른쪽 여백 추가 */
  color: black;
`;

const DateInfo = styled.p`
  color: #888;
  font-size: 12px;
  margin-right: 10px; /* 오른쪽 여백 추가 */
`;

const DeleteButton = styled.button`
  color: red;
  font-weight: bold;
  cursor: pointer;
`;

interface PersonalAlarmProps {
  content: string;
  date: string;
  onDelete: () => void;
}

const PersonalAlarm: React.FC<PersonalAlarmProps> = ({
  content,
  date,
  onDelete,
}) => {
  const today = new Date();
  const formattedDate = `${today.getFullYear()}-${
    today.getMonth() + 1
  }-${today.getDate()}`;
  return (
    <AlarmContainer>
      <AlarmContent>{content} (개인 알람 내용)</AlarmContent>
      <DateInfo>{formattedDate}</DateInfo>
      <DeleteButton onClick={onDelete}>삭제</DeleteButton>
    </AlarmContainer>
  );
};

export default PersonalAlarm;