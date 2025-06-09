import React, { useEffect } from 'react';
import { useDispatch, useSelector } from "react-redux";
import { setLocalStorage } from '../../utils/localStorage';
import { addTimer } from '../../redux/slices/timerSlice';

import endpoints from "../../shared/router/endpoints";

export const Timer = () => {
    const dispatch = useDispatch();
    const arrTimer = useSelector((state) => state.timerReducer.timer);
    let hours = arrTimer[0];
    let minutes = arrTimer[1];
    let seconds = arrTimer[2];

    function addHoursToDate(date, hours) {
        const newDate = new Date(date);
        newDate.setTime(newDate.getTime() + (hours * 60 * 60 * 1000));
        return newDate;
    }

    if (localStorage.date_lock === "true") {
        const startCountdown = addHoursToDate(new Date(), 1);
        localStorage.setItem("countdown", startCountdown.toString());
        setLocalStorage("date_lock", false);
    }

    const second = localStorage.countdown?.split("").splice(22, 2).join("").toString();
    const minute = localStorage.countdown?.split("").splice(19, 2).join("").toString();
    const hour = localStorage.countdown?.split("").splice(16, 2).join("").toString();
    const date = localStorage.countdown?.split("").splice(8, 2).join("").toString();
    const month = localStorage.countdown?.split("").splice(4, 3).join("").toString();
    const year = localStorage.countdown?.split("").splice(11, 4).join("").toString();
    const fixDate = `${month} ${date}, ${year}, ${hour}:${minute}:${second}`

    useEffect(() => {
        const targetDate = new Date(`${fixDate}`).getTime();
        let hours, minutes, seconds, intervalHandler;

        const startTimer = () => {
            dispatch(addTimer([hours, minutes, seconds]));
            const currentDate = new Date().getTime();
            let secondsLeft = (targetDate - currentDate) / 1000;
            hours = parseInt(secondsLeft / 3600);
            secondsLeft = secondsLeft % 3600;
            minutes = parseInt(secondsLeft / 60);
            seconds = parseInt(secondsLeft % 60);

            if (hours <= 0 && minutes <= 0 && seconds < 0) {
                clearInterval(intervalHandler);
                localStorage.removeItem("date_lock");
                localStorage.removeItem("timer");
                localStorage.removeItem("countdown");
                window.location.replace(`/${endpoints.INVITE}`);
            };
        }
        clearInterval(intervalHandler);
        intervalHandler = setInterval(startTimer, 1000);
    }, [fixDate, dispatch])
    
    return (
        <div className='d-flex justify-content-center'>
            <div id='time-hours' className="d-flex fs-4 justify-content-center align-items-center" style={{ height: "31px", width: "48px" }}>{hours < 10 ? "0" + hours : hours || "hh"}</div>
            <p className="m-0 px-2 d-flex justify-content-center align-items-center fs-3" style={{ height: "31px" }}>:</p>
            <div id='time-minutes' className="d-flex fs-4 justify-content-center align-items-center" style={{ height: "31px", width: "48px" }}>{minutes < 10 ? "0" + minutes : minutes || "mm"}</div>
            <p className="m-0 px-2 d-flex justify-content-center align-items-center fs-3" style={{ height: "31px" }}>:</p>
            <div id='time-seconds' className="d-flex fs-4 justify-content-center align-items-center" style={{ height: "31px", width: "48px" }}>{seconds < 10 ? "0" + seconds : seconds || "ss"}</div>
        </div>
    )
}