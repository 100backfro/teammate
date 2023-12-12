// StyledModal.tsx
import React from "react";
import styled from "styled-components";

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children?: React.ReactNode;
}

// 모달이 열릴 때만 표시되도록 스타일링
const ModalOverlay = styled.div<{ isOpen: boolean }>`
  display: ${({ isOpen }) => (isOpen ? "flex" : "none")};
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  justify-content: center;
  align-items: center;
`;

const ModalContent = styled.div`
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
`;

const CloseButton = styled.button`
  margin-top: 10px;
`;

const StyledModal: React.FC<ModalProps> = ({ isOpen, onClose, children }) => {
  return (
    <ModalOverlay isOpen={isOpen}>
      <ModalContent>
        {children}
        <CloseButton onClick={onClose}>닫기</CloseButton>
      </ModalContent>
    </ModalOverlay>
  );
};

export default StyledModal;
