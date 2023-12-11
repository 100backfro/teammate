//이 핸들러를 활용하면 됨.
import { rest } from "msw";
import people from "./dummy.json";

export const handlers = [
  rest.get("/people", async (req, res, ctx) => {
    await sleep(200);
    return res(ctx.status(200), ctx.json(people));
  }),
  //   rest.get("/*", async (req, res, ctx) => {
  //     return res(
  //       ctx.status(200),
  //       ctx.json({ message: "Mocked response for all GET requests" }),
  //     );
  //   }),
  rest.post("/sign-up", (req, res, ctx) => {
    return res(ctx.status(200), ctx.json({ message: "가입 성공" }));
  }),
  rest.get("/schedules", (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json([
        { id: "1", title: "Meeting1", start: new Date("2023-11-29") },
        {
          id: "2",
          title: "Meeting2",
          start: new Date("2023-11-30"),
          contents: "백프로팀 회의 하는 날",
        },
        {
          id: "3",
          title: "Meeting3",
          start: new Date("2023-11-30"),
          contents: "화분 물 주는 날",
        },
        {
          id: "4",
          title: "msw일정1",
          start: new Date("2023-12-06 10:20:20"),
          contents: "대청소 하는 날",
          place: "자택",
        },
        {
          id: "5",
          title: "msw일정2",
          start: new Date("2023-12-06 10:00:20"),
          extendedProps: {
            contents: "친구 만나는 날",
            place: "서울특별시",
            groupId: "주간회의",
          },
        },
      ]),
    );
  }),
  rest.get("/vite.svg", (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.text("This is a mock response for /vite.svg"),
    );
  }),
];

async function sleep(timeout: number) {
  return new Promise((resolve) => {
    setTimeout(resolve, timeout);
  });
}
