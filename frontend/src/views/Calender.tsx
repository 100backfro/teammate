import CalendarView from "../components/Calendar/CalendarView";

const Calender = () => {
  return (
    <div className="flex min-h-screen flex-col items-center justify-between pt-10">
      <div className="grid grid-cols-10">
        <CalendarView />
      </div>
    </div>
  )
}

export default Calender;