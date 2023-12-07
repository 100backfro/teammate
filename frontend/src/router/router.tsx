import { Routes, Route } from "react-router-dom";

import Calender from "../views/Calender";
import SignInView from "../views/SignInView";
import SignUp from "../components/Join/SignUp";
import Index from "../views/Index";
import KakaoLogin from "../components/Login/KakaoLogin";
import Home from "../components/Home/HomeContent";
import Mypage from "../components/Profile/Mypage";
import HomeView from "../views/HomeView";
import TeamCreateView from "../views/TeamCreateView";

const Router = () => {
  return (
    <Routes>
      <Route path="/" element={<Index />} />
      <Route path="/schedules" element={<Calender />} />
      <Route path="/signup" element={<SignUp />} />
      <Route path="/signin" element={<SignInView />} />
      <Route path="/kakaoLogin" element={<KakaoLogin />} />
      <Route path="/home" element={<Home />} />
      <Route path="/homeview" element={<HomeView />} />
      <Route path="/mypage" element={<Mypage />} />
      <Route path="/teamcreateview" element={<TeamCreateView />} />
      <Route />
    </Routes>
  );
};

export default Router;
