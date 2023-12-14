import { useState, useEffect } from "react";
import FullCalendar from '@fullcalendar/react'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import '../../styles/teamCalender.css'
import { Modal, Overlay, ModalContent, CloseModal, CalendarDiv } from '../../styles/TeamCalenderStyled.tsx'
import EditEvent from "./EditEvent.tsx";
import axios from "axios";

// import { schedules } from "../../recoil/atoms/schedules.tsx"
// import { useRecoilValue, useSetRecoilState } from 'recoil';

const TeamCalender = () => {
    // 모달팝업 유무 값
    const [eventDetailModal, setEventDetailModal] = useState(false);
    const [eventFormModal, seteventFormModal] = useState(false);
    
    // recoil 사용 선언부, 이벤트 목록
    // const eventList = useRecoilValue(schedules);
    // const setEventList = useSetRecoilState(schedules);
    const [eventList, setEventList] = useState([]);

    // 달력 일정 각각 state 핸들링용
    const [event, setEvent] = useState([]);

    // 모달팝업 관리
    const toggleModal = () => {
        setEventDetailModal(!eventDetailModal);
    };
    const toggleFormModal = () => {
        seteventFormModal(!eventFormModal);
    };

    // 일정클릭 핸들링
    const HandleEventClick = (e) => {
        console.log(e.event.extendedProps);
        setEvent({
            title: e.event.title,
            start: e.event.start.toString(),
            contents: e.event.extendedProps.contents,
            place: e.event.extendedProps.place,
            groupId: e.event.extendedProps.groupId,
        });
        toggleModal();
    }

    // 날짜클릭 핸들링
    const HandleDateClick = (e) => {
        // console.log(e.dayEl);
        toggleFormModal();
    }

    // 수정중인지 여부
    const [isEdit, setIsEdit] = useState(false);
    const toggleIsEdit = () => setIsEdit(!isEdit);

    // 일정목록 불러오기
    useEffect(() => {
        const getAllEvents = async () => {
            try {
                const res = await axios({
                    method: "get",
                    url: "/schedules",
                });
                if (res.status === 200) {
                    console.log(res.data);
                    setEventList(res.data);
                }
            } catch (error) {
                console.log(error);
            }
        };

        getAllEvents();
    }, []);

    return (
        <CalendarDiv>
            {/* <h2>캘린더입니다.</h2> */}
            <FullCalendar
                locale="kr"
                plugins={[dayGridPlugin, interactionPlugin, timeGridPlugin]}
                initialView="dayGridMonth"
                headerToolbar={{
                    start: "today prev,next",
                    center: "title",
                    end: "dayGridMonth timeGridWeek"
                }}
                events={eventList}
                eventClick={(e) => HandleEventClick(e)}
                dateClick={(e) => HandleDateClick(e)}
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
                                <EditEvent isEdit={isEdit} originEvent={event}/>
                                <button onClick={toggleIsEdit}>수정</button>
                            </>
                        ) : (
                            <>
                                <h2>일정상세</h2>
                                <p>
                                    이름: {event.title}<br />
                                    일시: {event.start}<br />
                                    내용: {event.contents}<br />
                                    장소: {event.place}<br />
                                    카테고리: {event.groupId}
                                </p>
                                <button onClick={toggleIsEdit}>수정</button>
                                <button>삭제</button>
                            </>
                        )}
                        {/* 수정중이 아닐때 close버튼 렌더링 */}
                        { !isEdit && 
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
                        <EditEvent isEdit={isEdit} originEvent={event} />
                        <CloseModal
                            onClick={toggleFormModal}
                        >
                            CLOSE
                        </CloseModal>
                    </ModalContent>
                </Modal>
            )}
        </CalendarDiv>
    );
};

export default TeamCalender;