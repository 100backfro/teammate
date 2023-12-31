import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
import { RecoilRoot } from "recoil";

import { worker } from "./mocks/browser";

if (process.env.NODE_ENV === "development") {
  worker.stop();
}
ReactDOM.createRoot(document.getElementById("root")!).render(
  <RecoilRoot>
    <App />
  </RecoilRoot>,
);