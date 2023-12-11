import styled from "styled-components";
import Modal from "react-modal";

export const CenteredContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh; // 높이를 화면 높이 전체로 설정

  .modal-content {
    padding: 20px;
    background: #cccccc;
  }
`;

export const TeamLeaderModal = styled(Modal)`
  display: flex;
  align-items: center;
  justify-content: center;
`;
