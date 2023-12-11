import react, { useState, useEffect } from "react";
import logo from "../../assets/logo.png";
import { Link, useLocation } from "react-router-dom";
import {
  HeaderImg,
  HeaderUl,
  HeaderTag,
  HeaderLink,
  LogoContainer,
} from "../../styles/HeaderStyled";
import { useRecoilState, useRecoilValue } from "recoil";
import { isAuthenticatedState, teamListState } from "../../state/authState";
import { useNavigate } from "react-router-dom";

const Header = () => {
  const [isAuthenticated, setIsAuthenticated] =
    useRecoilState(isAuthenticatedState);
  const navigate = useNavigate();
  const location = useLocation();
  const teamList = useRecoilValue(teamListState);
  const [teamName, setTeamName] = useState("");

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    navigate("/signin"); // 로그인 페이지로 리디렉션
    setIsAuthenticated(false);
  };

  const isTeamPage = location.pathname.startsWith("/team/");

  useEffect(() => {
    // 현재 페이지가 팀 페이지이고 팀 목록이 존재할 경우에만 팀 이름 설정
    if (isTeamPage && teamList.length > 0) {
      const teamId = location.pathname.split("/")[2];
      const team = teamList.find((team) => team.id === teamId);
      setTeamName(team ? team.name : "팀을 찾을 수 없음");
    }
  }, [location.pathname, isTeamPage, teamList]);

  return (
    <HeaderTag>
      {isAuthenticated ? (
        <>
          <LogoContainer>
            <div>
              <Link to="/homeview">
                <HeaderImg src={logo} />
              </Link>
            </div>
            <div style={{ marginLeft: "10px" }}>
              {isTeamPage && <h3>{teamName}</h3>}
            </div>
          </LogoContainer>
        </>
      ) : (
        <>
          <Link to="/">
            <HeaderImg src={logo} />
          </Link>
        </>
      )}
      <nav>
        <HeaderUl>
          {isAuthenticated ? (
            <>
              <li>
                <span
                  onClick={handleLogout}
                  style={{ cursor: "pointer", color: "#333333" }}
                >
                  로그아웃
                </span>
              </li>
              <li>
                {isTeamPage ? (
                  <Link to="/teammembers">팀프로필</Link>
                ) : (
                  <Link to="/mypage">마이페이지</Link>
                )}
              </li>
            </>
          ) : (
            <>
              <li>
                <Link to="/signin">로그인</Link>
              </li>
              <li>
                <Link to="/signup">회원가입</Link>
              </li>
            </>
          )}
        </HeaderUl>
      </nav>
    </HeaderTag>
  );
};

export default Header;
