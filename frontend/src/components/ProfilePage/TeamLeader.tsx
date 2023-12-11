import React, { useState, ChangeEvent } from "react";
import { useNavigate } from "react-router-dom";
import { CenteredContainer, TeamLeaderModal } from "./TeamLeaderStyled";
import profileImg from "../../assets/profileImg.png";

export default function TeamLeader() {
  const navigate = useNavigate();
  const [teamName, setTeamName] = useState("Your Team");
  const [teamLeader, setTeamLeader] = useState("Team Leader");
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const [teamMembers, setTeamMembers] = useState<string[]>([
    "Team Member 1",
    "Team Member 2",
    "Team Member 3",
    "Team Member 4",
  ]);
  const [searchTeam, setSearchTeam] = useState<string>("");
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [memberIndexToRemove, setMemberIndexToRemove] = useState<number | null>(
    null,
  );
  const [editingTeamName, setEditingTeamName] = useState(false);
  const [newTeamName, setNewTeamName] = useState("");
  const [editingTeamLeader, setEditingTeamLeader] = useState(false);
  const [newLeaderSelect, setNewLeaderSelect] = useState<string | null>(null);
  const [kickReason, setKickReason] = useState("");
  const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
  const [password, setPassword] = useState("");
  const [passwordInput, setPasswordInput] = useState("");

  const handleImageChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        setSelectedImage(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleTeamMemberChange = (
    index: number,
    e: ChangeEvent<HTMLInputElement>,
  ) => {
    const updatedTeamMembers = [...teamMembers];
    updatedTeamMembers[index] = e.target.value;
    setTeamMembers(updatedTeamMembers);
  };

  const filteredTeamMembers = teamMembers.filter((member) =>
    member.toLowerCase().includes(searchTeam.toLowerCase()),
  );

  const handleRemoveMember = (index: number) => {
    setMemberIndexToRemove(index);
    setShowConfirmation(true);
  };

  const confirmRemoveMember = () => {
    if (memberIndexToRemove !== null && kickReason.trim() !== "") {
      const updatedTeamMembers = [...teamMembers];
      updatedTeamMembers.splice(memberIndexToRemove, 1);
      setTeamMembers(updatedTeamMembers);
      setMemberIndexToRemove(null);
      setKickReason("");
      setShowConfirmation(false);
    } else {
      // 강퇴 사유를 입력하지 않은 경우 예외 처리 또는 메시지 표시
      console.log("강퇴 사유를 입력하세요.");
    }
  };

  const cancelRemoveMember = () => {
    setMemberIndexToRemove(null);
    setKickReason("");
    setShowConfirmation(false);
  };

  const handleEditTeamName = () => {
    setEditingTeamName(true);
    setNewTeamName(teamName);
  };

  const confirmEditTeamName = () => {
    setEditingTeamName(false);
    setTeamName(newTeamName);
  };

  const cancelEditTeamName = () => {
    setEditingTeamName(false);
  };

  const handleEditTeamLeader = () => {
    setEditingTeamLeader(true);
    setNewLeaderSelect("");
  };

  const confirmEditTeamLeader = () => {
    if (newLeaderSelect !== null) {
      const updatedTeamMembers = teamMembers.filter(
        (member) => member !== newLeaderSelect,
      );
      setTeamMembers(updatedTeamMembers);

      setTeamMembers((prevTeamMembers) => [...prevTeamMembers, teamLeader]);
    }
    setTeamLeader(newLeaderSelect || "");
    setEditingTeamLeader(false);
    setNewLeaderSelect(null);
  };

  const cancelEditTeamLeader = () => {
    setEditingTeamLeader(false);
    setNewLeaderSelect(null);
  };

  const openPasswordModal = () => {
    setIsPasswordModalOpen(true);
  };

  const closePasswordModal = () => {
    setIsPasswordModalOpen(false);
  };

  const handlePasswordChange = (e: ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  const handleConfirmDelete = () => {
    // 여기에 비밀번호 확인 및 삭제 로직 추가
    console.log("비밀번호 확인 및 삭제 로직을 구현하세요.");
    if (!passwordInput) {
      console.log("비밀번호를 입력하세요.");
      return;
    }
    // 삭제가 성공했다고 가정하고 homeview로 이동
    navigate("/homeview");
  };

  return (
    <>
      <CenteredContainer>
        <div>
          <h1 style={{ textAlign: "center" }}>팀 프로필</h1>

          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <div>
              <input
                type="file"
                accept="image/*"
                onChange={handleImageChange}
                style={{ display: "none" }}
                id="imageInput"
              />
              <label htmlFor="imageInput">
                <img
                  src={selectedImage || profileImg}
                  alt="Team Logo"
                  style={{ width: "100px", height: "100px", cursor: "pointer" }}
                />
              </label>
            </div>

            <div>
              <span title="teamNameChange">
                {editingTeamName ? (
                  <input
                    type="text"
                    value={newTeamName}
                    onChange={(e) => setNewTeamName(e.target.value)}
                  />
                ) : (
                  teamName
                )}
              </span>
              {editingTeamName ? (
                <>
                  <button onClick={confirmEditTeamName}>확인</button>
                  <button onClick={cancelEditTeamName}>취소</button>
                </>
              ) : (
                <button onClick={handleEditTeamName}>변경하기</button>
              )}
              <br />
              {editingTeamLeader ? (
                <div>
                  <select
                    title="select"
                    value={newLeaderSelect || ""}
                    onChange={(e) => setNewLeaderSelect(e.target.value)}
                  >
                    <option value="" disabled>
                      선택하세요
                    </option>
                    {teamMembers.map((member, index) => (
                      <option key={index} value={member}>
                        {member}
                      </option>
                    ))}
                  </select>
                  <button onClick={confirmEditTeamLeader}>확인</button>
                  <button onClick={cancelEditTeamLeader}>취소</button>
                </div>
              ) : (
                <div>
                  <span>{teamLeader}</span>
                  <button onClick={handleEditTeamLeader}>변경하기</button>
                </div>
              )}
            </div>
          </div>

          <div>
            <p style={{ fontWeight: "bold", marginBottom: "5px" }}>팀원</p>
            <input
              type="text"
              placeholder="검색"
              value={searchTeam}
              onChange={(e) => setSearchTeam(e.target.value)}
            />
            <div
              style={{
                maxHeight: "200px", // Set your desired max height
                overflowY: "auto",
              }}
            >
              {filteredTeamMembers.map((member, index) => (
                <div
                  key={index}
                  style={{ display: "flex", marginBottom: "10px" }}
                >
                  <input
                    title="disband"
                    type="text"
                    value={member}
                    onChange={(e) => handleTeamMemberChange(index, e)}
                  />
                  <button onClick={() => handleRemoveMember(index)}>
                    강퇴
                  </button>
                </div>
              ))}
            </div>
          </div>
          {showConfirmation && (
            <div
              style={{
                position: "fixed",
                top: "50%",
                left: "50%",
                transform: "translate(-50%, -50%)",
                padding: "20px",
                background: "white",
                zIndex: 999,
              }}
            >
              <p>강퇴하시겠습니까?</p>
              {memberIndexToRemove !== null && (
                <>
                  <p>강퇴 사유를 입력하세요:</p>
                  <input
                    title="reason"
                    type="text"
                    value={kickReason}
                    onChange={(e) => setKickReason(e.target.value)}
                  />
                </>
              )}
              <button onClick={confirmRemoveMember}>확인</button>
              <button onClick={cancelRemoveMember}>취소</button>
            </div>
          )}

          <div style={{ textAlign: "center", marginTop: "20px" }}>
            <button onClick={openPasswordModal}>팀 계정 삭제하기</button>
          </div>
          <TeamLeaderModal
            isOpen={isPasswordModalOpen}
            onRequestClose={closePasswordModal}
            contentLabel="Password Modal"
          >
            <div className="modal-content">
              <div>팀 계정을 삭제하시겠습니까?</div>
              <p>비밀번호를 입력하세요:</p>
              <input
                type="password"
                value={password}
                onChange={handlePasswordChange}
              />
              <button onClick={handleConfirmDelete}>확인</button>
              <button onClick={closePasswordModal}>취소</button>
            </div>
          </TeamLeaderModal>
        </div>
      </CenteredContainer>
    </>
  );
}
