import { atom, useRecoilState, selector } from "recoil";
import { useEffect } from "react";

// Authentication State
export const isAuthenticatedState = atom({
  key: "isAuthenticatedState",
  default: Boolean(localStorage.getItem("accessToken")),
});

// Access Token Functions
export const saveAccessToken = (token: string | null) => {
  if (token) {
    localStorage.setItem("accessToken", token);
  } else {
    localStorage.removeItem("accessToken");
  }
};

export const logout = () => {
  localStorage.removeItem("accessToken");
};

// Home Screen Search State
export const searchState = atom({
  key: "searchState",
  default: (() => {
    const storedSearch = localStorage.getItem("search");
    return storedSearch || "";
  })() as string,
});

export const useSearchState = () => {
  const [search, setSearch] = useRecoilState(searchState);
  const [teamList, setTeamList] = useRecoilState(teamListState);

  const handleSearch = async (searchTerm: string) => {
    console.log("검색어:", searchTerm);
    const searchResults = await fetchTeamsBySearchTeam(searchTerm);
    setTeamList(searchResults);
  };

  const fetchTeamsBySearchTeam = async (searchTeam: string) => {
    const response = await fetch(`/api/teams?search=${searchTeam}`);
    const data = await response.json();
    return data;
  };

  return { search, setSearch, handleSearch, teamList, setTeamList };
};

// Team Creation State
export const teamNameState = atom({
  key: "teamNameState",
  default: (() => {
    const storedTeamName = localStorage.getItem("teamName");
    return storedTeamName || "";
  })(),
});

export const selectedTeamSizeState = atom({
  key: "selectedTeamSizeState",
  default: (() => {
    const storedTeamSize = localStorage.getItem("selectedTeamSize");
    return storedTeamSize || "1-9";
  })(),
});

// Team List State
export interface Team {
  id: string;
  name: string;
  size: string;
  image: string | null;
  leaderId: string | null;
}

export const teamListState = atom<Team[]>({
  key: "teamListState",
  default: [],

  effects_UNSTABLE: [
    ({ setSelf }) => {
      const storedTeamList = localStorage.getItem("teamList");
      if (storedTeamList) {
        setSelf(JSON.parse(storedTeamList));
      }
    },
  ],
});

// Selected Team State for MyPage
export const selectedTeamState = atom({
  key: "selectedTeam",
  default: "",
});

export const useSelectedTeamState = () => useRecoilState(selectedTeamState);

// User State and Functions
export interface User {
  id: string;
  name: string;
}

export const userState = atom<User | null>({
  key: "userState",
  default: { id: "", name: "" }, // 기본값 설정
  effects_UNSTABLE: [
    ({ onSet }) => {
      onSet((newValue) => {
        localStorage.setItem("user", JSON.stringify(newValue));
      });
    },
  ],
});

export const useUser = () => {
  const [user, setUser] = useRecoilState(userState);

  const saveUser = (loggedInUser: User) => {
    setUser(loggedInUser);
    localStorage.setItem("user", JSON.stringify(loggedInUser));
  };

  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, [setUser]);

  return { user, setUser, saveUser };
};

// MyPage State
