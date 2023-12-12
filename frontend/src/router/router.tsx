import { Routes, Route } from "react-router-dom";

import Calender from "../views/Calender";

import TextEditorView from "../views/TextEditorView";
import SignInView from "../views/SignInView";
import SignUp from "../components/Join/SignUp";
import Index from "../views/Index";
import KakaoLogin from "../components/Login/KakaoLogin";
import Home from "../components/Home/HomeContent";
import Mypage from "../components/Mypage/Mypage";
import HomeView from "../views/HomeView";
import TeamCreateView from "../views/TeamCreateView";
import TeamDetail from "../components/TeamPage/TeamDetail";
import TeamLeader from "../components/ProfilePage/TeamLeader";
import TeamMembers from "../components/ProfilePage/TeamMembers";

const Router = () => {
  return (
    <Routes>
      {/* <Route path='*' element={<Error />} /> */}
      <Route path="/캘린더" element={<Calender />} />
      <Route path="text-editor/:id" element={<TextEditorView />} />
      <Route path="/" element={<Index />} />
      <Route path="/schedules" element={<Calender />} />
      <Route path="/signup" element={<SignUp />} />
      <Route path="/signin" element={<SignInView />} />
      <Route path="/kakaoLogin" element={<KakaoLogin />} />
      <Route path="/home" element={<Home />} />
      <Route path="/homeview" element={<HomeView />} />
      <Route path="/mypage" element={<Mypage />} />
      <Route path="/teamcreateview" element={<TeamCreateView />} />
      <Route path="/team/:teamId" element={<TeamDetail />} />
      <Route path="/teamleader" element={<TeamLeader />} />
      <Route path="/teammembers" element={<TeamMembers />} />
      <Route />
    </Routes>
  );
};

export default Router;
