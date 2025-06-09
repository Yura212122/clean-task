import React, { useState, useEffect } from "react";
import { baseUrl } from "../../api/http-client";
import colors from "../../shared/dto/colors";
import { Link, useLocation, useNavigate } from "react-router-dom";
import endpoints from "../../shared/router/endpoints";
import { useDispatch, useSelector } from "react-redux";
import { setTaskAnswers } from "../../redux/slices/tasksForCorrectionSlice";

import notif_bell from "../../images/notif_bell.png";

import "./notification-icon.css";
import { setNumberOfLesson } from "../../redux/slices/headerSlice";

import { AlertModal } from "../alert-modal/AlertModal";

export const NotificationIcon = ({
  currentUser,
  setMainListMenuOpen,
  setAvatarListMenuOpen,
  isNotificationListMenuOpen,
  setNotificationListMenuOpen,
}) => {
  const [count, setCount] = useState(0);
  const [taskAnswersData, setTaskAnswersData] = useState([]);
  const [animate, setAnimate] = useState(false);
  const task = useSelector((state) => state.taskReducer.task);
  const location = useLocation();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [isModalOpen, setIsModalOpen] = useState(false);

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

  const noticeListMenu = () => document.querySelector("#notice-list-menu");
  const onHandleClick = (event) => {
    if (!isNotificationListMenuOpen) {
      setNotificationListMenuOpen(true);
      setMainListMenuOpen(false);
      setAvatarListMenuOpen(false);
    } else {
      setNotificationListMenuOpen(false);
    }
    event.stopPropagation();
    const target = event.target;

    if (
      noticeListMenu() &&
      (target.id === "horn" || target.id === "notification-badge")
    ) {
      if (noticeListMenu().classList.contains("d-block")) {
        noticeListMenu().classList.remove("d-block");
        noticeListMenu().classList.add("visually-hidden");
      } else {
        noticeListMenu().classList.remove("visually-hidden");
        noticeListMenu().classList.add("d-block");
      }
    }
  };

  document.addEventListener("click", (event) => {
    const target = event.target;
    if (
      noticeListMenu() &&
      (target.id !== "horn" || target.id !== "notification-badge")
    ) {
      setNotificationListMenuOpen(false);
    }
    event.stopPropagation();

    if (
      noticeListMenu() &&
      (target.id !== "horn" || target.id !== "notification-badge")
    ) {
      noticeListMenu().classList.remove("d-block");
      noticeListMenu().classList.add("visually-hidden");
    }
  });

  useEffect(() => {
    const triggerAnimation = () => {
      setAnimate(true);
      setTimeout(() => setAnimate(false), 2000);
    };
    triggerAnimation();
    const interval = setInterval(triggerAnimation, 20000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    if (noticeListMenu()) {
      noticeListMenu().classList.remove("d-block");
      noticeListMenu().classList.add("visually-hidden");
    }

    if (currentUser.id) {
      const fetchData = async () => {
        try {
          const { data } = await baseUrl.get(
            `api/tasks/answers/${currentUser.id}`
          );
          if (data) {
            dispatch(setTaskAnswers(data));
            let taskAnswerIsRead = [];
            data.map((task) => {
              if (task.isRead) {
                taskAnswerIsRead.push(task);
              }
              return task;
            });
            setTaskAnswersData(taskAnswerIsRead);
            setCount(taskAnswerIsRead.length);
          }
        } catch {
          openModal();
        }
      };
      fetchData();
    }
  }, [count, location.pathname]);

  const handleLinkClick = (taskAnswer) => {
    dispatch(setNumberOfLesson(taskAnswer.lessonNum));
    navigate(`/${endpoints.TASK}/${taskAnswer.taskId}`, {
      state: {
        taskName: taskAnswer.taskName,
        taskIdLocation: taskAnswer.taskId,
        taskStatus: "need correction",
        taskDescription: taskAnswer.description,
        lessonIdLocation: taskAnswer.lessonId,
        lessonNum: taskAnswer.lessonNum,
        answerIdLocation: taskAnswer.answerId,
        currentCourseName: taskAnswer.course,
        currentCourseId: taskAnswer.courseId,
        taskMessage: taskAnswer.messageForCorrection,
      },
    });
  };

  return (
    <div onClick={onHandleClick} className="notification-li">
      <figure className="dropdown m-0 p-0 cursor-pointer">
        <img
          id="horn"
          src={notif_bell}
          className="img-fluid"
          width={25}
          alt="Bell"
          loading="lazy"
        />
        {count ? (
          <button
            id="notification-badge"
            className={`notification-badge ${animate ? "pulse" : ""}`}
          >
            {count !== 0 ? count : ""}
          </button>
        ) : (
          ""
        )}
      </figure>
      {taskAnswersData.length ? (
        <ul
          id="notice-list-menu"
          className={`${
            isNotificationListMenuOpen ? "d-block" : "visually-hidden"
          } dropdown-menu p-0 position-absolute mt-2 top-100 end-0 border border-0 shadow-sm`}
          style={{ background: colors.GRAY_100, borderRadius: "8px" }}
        >
          {taskAnswersData.map((taskAnswer) => {
            return (
              <li key={taskAnswer.answerId}>
                <span
                  className="dropdown-item li-item"
                  onClick={() => handleLinkClick(taskAnswer)}
                >
                  Incorrect answer to the Тask{" "}
                  {taskAnswer.taskName.length > 10
                    ? `${taskAnswer.taskName.slice(0, 10)}...`
                    : taskAnswer.taskName}
                </span>
              </li>
            );
          })}
        </ul>
      ) : (
        ""
      )}
      <AlertModal
        isOpen={isModalOpen}
        onClose={closeModal}
        title="Oops..."
        alertText="failed get Task Answers by userId!"
        buttonText="Close"
      />
    </div>
  );
};
