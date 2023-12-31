import FullCalendar from '@fullcalendar/react'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import '../../styles/teamCalender.css'
import styled from "styled-components";
import EditEvent from "./EditEvent.tsx";
import axiosInstance from "../../axios";
import { ConvertedEvent } from "../../interface/interface.ts"

const TeamCalender = ({ categoryList, myTeamMemberId }: any) => {
  const navigate = useNavigate();

  // 팀 아이디
  const { teamId } = useParams();

  // 모달팝업 유무 값
  const [eventDetailModal, setEventDetailModal] = useState<any>(false);
  const [eventFormModal, setEventFormModal] = useState<any>(false);

  // 일정 전체 목록
  const [eventList, setEventList] = useState<any>([]);

  // 달력 일정 각각 state 핸들링용
  const [event, setEvent] = useState<any>([]);

  // 모달팝업 관리
  const toggleModal = () => {
    setEventDetailModal(!eventDetailModal);
  };
  const toggleFormModal = () => {
    setEventFormModal(!eventFormModal);
  };

  // 일정클릭 핸들링
  const HandleEventClick = (e: any) => {
    // console.log(e.event.extendedProps);
    // console.log(e.event.start);
    setEvent({
      id: e.event.id,
      title: e.event.title,
      start: e.event.start,
      content: e.event.extendedProps.content,
      place: e.event.extendedProps.place,
      groupId: e.event.extendedProps.categoryName,
      categoryId: e.event.extendedProps.categoryId,
    });
    toggleModal();
  }

  // 날짜클릭 핸들링
  const HandleDateClick = () => {
    // console.log(e.dayEl);
    toggleFormModal();
  }

  // 수정중인지 여부
  const [isEdit, setIsEdit] = useState(false);
  const toggleIsEdit = () => setIsEdit(!isEdit);

  // 일정목록 렌더링을 위한 변환
  const convertEvents = (events: any[]): ConvertedEvent[] => {
    return events.map(event => ({
      id: event.scheduleId,
      start: event.startDt,
      end: event.endDt,
      title: event.title,
      borderColor: event.color,
      backgroundColor: event.color,
      extendedProps: {
        content: event.content,
        place: event.place,
        scheduleType: event.scheduleType,
        category: event.category,
        categoryName: event.categoryName,
        categoryId: event.categoryId,
      }
    }));
  };
  
  // 일정목록 불러오기
  useEffect(() => {
    const getAllEvents = async () => {
      try {
        const res = await axiosInstance({
          method: "get",
          url: `/team/${teamId}/schedules/calendar`,
        });
        if (res.status === 200) {
          console.log(res.data);
          // 데이터 변환
          const convertedEvents = convertEvents(res.data);
          // console.log(convertedEvents);
          setEventList(convertedEvents);
          return;
        }
      } catch (error) {
        console.log(error);
      }
    };

    getAllEvents();
  }, [teamId]);

  // 일정 삭제
  const handleEventDelete = async () => {
    // e.preventDefault();
    if (!window.confirm(`일정을 삭제하시겠습니까?`)) return;
    const eventId = event.id;
    try {
      // team/{teamId}/schedules/simple/{schedule_id}
      const res = await axiosInstance.delete(`/team/${teamId}/schedules/simple/${eventId}`, {
        headers: {
          "Content-Type": "application/json"
        },
        data: {
          scheduleId: eventId,
          teamId: teamId,
          teamParticipantId: myTeamMemberId,
        }
      });
      if (res.status === 200) {
        // setNewEvent()
        console.log(res.data);
        alert("삭제 되었습니다");
        location.reload();
      }
    } catch (error) {
      console.log(error);
      alert("일정 생성자가 팀 내에 존재하므로, 팀장권한으로 삭제가 불가능합니다.");
    }
  }

  return (
    <>
      <FullCalendar
        locale="kr"
        plugins={[dayGridPlugin, interactionPlugin, timeGridPlugin]}
        timeZone="UTC"
        initialView="dayGridMonth"
        headerToolbar={{
          start: "goTeamHomeButton prev title next",
          center: "",
          end: "today dayGridMonth,timeGridWeek"
        }}
        buttonText={{
          // prev: "이전", // 부트스트랩 아이콘으로 변경 가능
          // next: "다음",
          // prevYear: "이전 년도",
          // nextYear: "다음 년도",
          today: "오늘",
          month: "월간",
          week: "주간",
          day: "일간",
          list: "목록"
        }}
        customButtons={{
          goTeamHomeButton: {
            text: "팀 홈",
            click: () => {
              navigate(`/team/${teamId}`)
            }
          }
        }}
        events={eventList}
        dayMaxEvents={true}
        height="90vh"
        expandRows={true}
        eventClick={(e) => HandleEventClick(e)}
        dateClick={() => HandleDateClick()}
      />
      {/* 일정클릭 모달 */}
      {eventDetailModal && (
        <Modal >
          <Overlay
          // onClick={toggleModal}
          ></Overlay>
          <ModalContent className="rounded-lg shadow">
            {isEdit ? (
              <>
                {/* 에디터컴포넌트 */}
                <EditEvent isEdit={isEdit} originEvent={event} setEventList={setEventList} toggleIsEdit={toggleIsEdit} categoryList={categoryList} myTeamMemberId={myTeamMemberId || 0} />
              </>
            ) : (
              <div className="p-4 md:p-5">
                <h2 className="text-xl mt-4 mb-4 font-semibold text-gray-900">{event.title}</h2>
                <div>
                  {/* 일정 번호: {event.id} */}
                  {/* 이름: {event.title}<br /> */}
                  <div className="mb-3">
                    <span className="mr-10 text-gray-500">일시</span><span className="">{event.start.toISOString().split(':00')[0].replace("T", " ")}</span>
                  </div>
                  <div className="mb-3">
                    <span className="mr-10 text-gray-500">내용</span>{event.content}
                  </div>
                  <div className="mb-3">
                    <span className="mr-10 text-gray-500">장소</span>{event.place}
                  </div>
                  <div className="mb-3">
                    <span className="text-gray-500 mr-3">카테고리</span>{event.groupId}
                  </div>
                  {/* <div className="mb-5">
                    <span className="text-gray-500 mr-3">참가자</span>
                    {event.groupId}
                  </div> */}
                </div>
                <button onClick={toggleIsEdit} className="bg-white border-1 border-gray-300 mr-2">수정</button>
                <button onClick={handleEventDelete} className="bg-white border-1 border-gray-300">삭제</button>
              </div>
            )}
            {/* 수정중이 아닐때 close버튼 렌더링 */}
            {!isEdit &&
              <CloseModal
                onClick={toggleModal}
              >
                닫기
              </CloseModal>
            }
          </ModalContent>
        </Modal>
      )}
      {/* 날짜클릭 모달 */}
      {eventFormModal && (
        <Modal>
          <Overlay
            onClick={toggleFormModal}
          ></Overlay>
          <ModalContent>
            <EditEvent isEdit={isEdit} toggleIsEdit={toggleIsEdit} originEvent={event} setEventList={setEventList} categoryList={categoryList} myTeamMemberId={myTeamMemberId || 0} />
            <CloseModal
              onClick={toggleFormModal}
            >
              닫기
            </CloseModal>
          </ModalContent>
        </Modal>
      )}
    </>
  );
};

export default TeamCalender;

// 스타일드 컴포넌트
export const Modal = styled.div`
  width: 100vw;
  height: 100vh;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  position: fixed;
  z-index: 99999999;
`

export const Overlay = styled.div`
  background: rgba(49,49,49,0.5);
  width: 100vw;
  height: 100vh;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  position: fixed;
`

export const ModalContent = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  line-height: 1.4;
  background: white;
  padding: 14px 28px;
  border-radius: 0.5rem;
  max-width: 600px;
  min-width: 300px;
  z-index: 6;
`

export const CloseModal = styled.button`
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 5px 7px;
  background-color: rgb(17 24 39 / var(--tw-text-opacity)); 
`