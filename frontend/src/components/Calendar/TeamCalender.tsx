import { useState, useEffect } from "react";
import FullCalendar from '@fullcalendar/react'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import '../../styles/teamCalender.css'
import { Modal, Overlay, ModalContent, CloseModal } from '../../styles/TeamCalenderStyled.tsx'
import EditEvent from "./EditEvent.tsx";
// import axios from "axios";
import axiosInstance from "../../axios";
import { useNavigate, useParams } from "react-router-dom";

// import { Team } from "../../interface/interface";

const TeamCalender = () => {
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
            contents: e.event.extendedProps.contents,
            place: e.event.extendedProps.place,
            groupId: e.event.extendedProps.groupId,
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

    // 일정목록 불러오기
    const getAllEvents = async () => {
        try {
            const res = await axiosInstance({
                method: "get",
                url: `/team/${teamId}/schedules/calendar`,
            });
            if (res.status === 200) {
                console.log(res.data);
                setEventList(res.data);
                return;
            }
        } catch (error) {
            console.log(error);
        }
    };
    useEffect(() => {
        getAllEvents();
    }, [teamId]);

    // 일정 삭제
    const handleEventDelete = async (e: any) => {
        e.preventDefault();
        if (!window.confirm(`번째 일정을 삭제하시겠습니까?`)) return;
        const eventId = event.id;
        try {
            const res = await axiosInstance.delete(`/schedules`, {
                headers: {
                    "Content-Type": "application/json"
                },
                data: {
                    eventId
                }
            });
            if (res.status === 201) {
                // setNewEvent()
                console.log(res.data);
            }
        } catch (error) {
            console.log(error);
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
                <Modal>
                    <Overlay
                    // onClick={toggleModal}
                    ></Overlay>
                    <ModalContent>
                        {isEdit ? (
                            <>
                                {/* 에디터컴포넌트 */}
                                <EditEvent isEdit={isEdit} originEvent={event} setEventList={setEventList} toggleIsEdit={toggleIsEdit} />
                            </>
                        ) : (
                            <>
                                <h2>일정상세</h2>
                                <p>
                                    {/* 일정 번호: {event.id} */}
                                    이름: {event.title}<br />
                                    일시: {event.start.toJSON()}<br />
                                    내용: {event.contents}<br />
                                    장소: {event.place}<br />
                                    카테고리: {event.groupId}
                                </p>
                                <button onClick={toggleIsEdit}>수정</button>
                                <button onClick={handleEventDelete}>삭제</button>
                            </>
                        )}
                        {/* 수정중이 아닐때 close버튼 렌더링 */}
                        {!isEdit &&
                            <CloseModal
                                onClick={toggleModal}
                            >
                                CLOSE
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
                        <EditEvent isEdit={isEdit} originEvent={event} setEventList={setEventList} toggleIsEdit={toggleIsEdit} />
                        <CloseModal
                            onClick={toggleFormModal}
                        >
                            CLOSE
                        </CloseModal>
                    </ModalContent>
                </Modal>
            )}
        </>
    );
};

export default TeamCalender;