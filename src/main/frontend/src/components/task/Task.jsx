import React, { Fragment, useEffect, useState } from "react";
import TaskShowMoreText from "./TaskShowMoreText";
import { baseUrl } from "../../api/http-client";
import { useSelector, useDispatch } from "react-redux";
import side_shot from "../../images/side_shot.svg";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { setTask } from "../../redux/slices/taskSlice";
import endpoints from "../../shared/router/endpoints";
import { useForm } from "react-hook-form";
import chevron_left from "../../images/chevron_left.svg";
import { setLesson } from "../../redux/slices/lessonSlice";
import { setCourse } from "../../redux/slices/courseSlice";
import { updateTaskAnswer } from "../../redux/slices/tasksForCorrectionSlice";
import { setNumberOfLesson } from "../../redux/slices/headerSlice";
import { useGetLessons } from "../../hooks/course-lessons-request";
import { AlertModal } from "../alert-modal/AlertModal";

import "./task.css";

export const Task = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm();
  const userId = useSelector((state) => state.userReducer.user.id);
  const currentUserRole = useSelector((state) => state.userReducer.user.role);
  const taskNameStorage = useSelector((state) => state.taskReducer.task.name);
  const taskIdStorage = useSelector((state) => state.taskReducer.task.id);
  const taskStatusStorage = useSelector(
    (state) => state.taskReducer.task.status
  );
  const taskStatusMessage = useSelector(
    (state) => state.taskReducer.task.message
  );
  const taskStatusDescription = useSelector(
    (state) => state.taskReducer.task.description
  );
  const lessonId = useSelector((state) => state.lessonReducer.lesson.id);
  /*const lessonNum = useSelector(state => state.headerReducer.header.numberOfLesson);*/
  const lessonNum = useSelector(
    (state) => state.courseReducer.course.numberOfLesson
  );
  const answerId = useSelector((state) => state.taskReducer.task.answerId);
  const currentCourseNameStorage = useSelector(
    (state) => state.courseReducer.course.name
  );
  const currentCourseIdStorage = useSelector(
    (state) => state.courseReducer.course.courseId
  );
  const currentTaskAnswersArray = useSelector(
    (state) => state.taskAnswersReducer.taskAnswers
  );
  const [wrongUrl, setWrongUrl] = useState("");
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const { data } = useGetLessons(
    currentCourseIdStorage || location.state.currentCourseId
  );

  const [isModalOpen, setIsModalOpen] = useState(false);

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

  let taskName = taskNameStorage;
  let taskIdLocation = taskIdStorage;
  let taskDescription = taskStatusDescription;
  let lessonIdLocation = lessonId;
  let lessonNumLocation = lessonNum;
  let answerIdLocation = answerId;
  let currentCourseName = currentCourseNameStorage;
  let currentCourseId = currentCourseIdStorage;
  let taskMessage = taskStatusMessage;
  let taskStatus = taskStatusStorage;

  useEffect(() => {
    if (data) {
      dispatch(
        setCourse({
          name: currentCourseName,
          courseId: currentCourseId,
          numberOfLesson: lessonNumLocation,
          listOfLessons: data,
        })
      );
    }
  }, [data, lessonNumLocation]);

  useEffect(() => {
    if (location.state) {
      taskName = location.state.taskName || taskNameStorage;
      taskIdLocation = location.state.taskIdLocation || taskIdStorage;
      taskStatus = location.state.taskStatus || taskStatusStorage;
      taskDescription = location.state.taskDescription || taskStatusDescription;
      lessonIdLocation = location.state.lessonIdLocation || lessonId;
      lessonNumLocation = location.state.lessonNum || lessonNum;
      answerIdLocation = location.state.answerIdLocation || answerId;
      currentCourseName =
        location.state.currentCourseName || currentCourseNameStorage;
      currentCourseId =
        location.state.currentCourseId || currentCourseIdStorage;
      taskMessage = location.state.taskMessage || taskStatusMessage;

      dispatch(
        setTask({
          id: taskIdLocation,
          name: taskName,
          description: taskDescription,
          answerId: answerIdLocation,
          message: taskMessage,
          status: taskStatus,
        })
      );

      dispatch(
        setLesson({
          lessonId: lessonIdLocation,
        })
      );
      dispatch(
        setCourse({
          name: currentCourseName,
          courseId: currentCourseId,
          numberOfLesson: lessonNumLocation,
          listOfLessons: data,
        })
      );

      dispatch(setNumberOfLesson(lessonNumLocation));
    }
  }, [location.state, dispatch, lessonNumLocation]);

  const [isSubmittedStorage, setIsSubmittedStorage] = useState(
    taskStatus === ""
  );

  const onHandleSubmit = async (obj) => {
    let answerUrl = null;
    let temporarySubStr = obj.answerUrl.substring(obj.answerUrl.length - 4);
    if (temporarySubStr === ".git") {
      answerUrl = obj.answerUrl.substring(0, obj.answerUrl.length - 4);
    } else {
      answerUrl = obj.answerUrl;
    }
    if (currentUserRole === "TEACHER" || currentUserRole === "MENTOR") {
      openModal();
    } else {
      try {
        const { data } = await baseUrl.post("/api/tasks/submit", {
          answerUrl: answerUrl,
          userId: userId,
          courseId: currentCourseId,
          lessonId: lessonIdLocation,
          lessonNum: lessonNumLocation,
          taskId: taskIdLocation,
          course: currentCourseName,
        });
        dispatch(
          setTask({
            id: taskIdLocation,
            name: taskName,
            description: taskStatusDescription,
            answerId: answerIdLocation,
            message: "",
            status: "submitted",
          })
        );
        let taskId = location.pathname.split("/").pop();

        if (currentTaskAnswersArray && currentTaskAnswersArray.length) {
          currentTaskAnswersArray.map((task) => {
            if (
              task.taskId === parseInt(taskId) &&
              task.isCorrection &&
              !task.passed
            ) {
              dispatch(
                updateTaskAnswer({
                  answerId: task.answerId,
                  answerUrl: task.answerUrl,
                  course: task.course,
                  courseId: task.courseId,
                  description: task.description,
                  isCorrection: false,
                  isRead: task.isRead,
                  lessonId: task.lessonId,
                  lessonNum: task.lessonNum,
                  messageForCorrection: task.messageForCorrection,
                  passed: task.passed,
                  student: task.student,
                  submittedDate: task.submittedDate,
                  taskId: task.taskId,
                  taskName: task.taskName,
                })
              );
              setIsSubmittedStorage(false);
            }
            return task;
          });
        }
        setIsSubmittedStorage(false);
        reset();
      } catch (error) {
        setWrongUrl(error.response.data.message);
      }
    }
  };

  const readDescription = () => {
    window.open(`${taskDescription}`, "_blank");
  };

  useEffect(() => {
    if (!userId) {
      navigate(`/${endpoints.NOT_LOGGED_IN}`);
    }
    if (!lessonIdLocation) {
      navigate(`/${endpoints.COURSES}`);
    }
    let taskId = location.pathname.split("/").pop();

    if (taskStatus === "need correction") {
      setIsSubmittedStorage(true);
    } else if (taskIdLocation === parseInt(taskId) && taskStatus !== "") {
      setIsSubmittedStorage(false);
    }

    try {
      const onAutoSubmit = async () => {
        await baseUrl.post("/api/tasks/submit", {
          answerUrl: "isRead",
          userId: userId,
          courseId: currentCourseId,
          lessonId: lessonIdLocation,
          lessonNum: lessonNumLocation,
          taskId: taskIdLocation,
          course: currentCourseName,
        });
      };
      onAutoSubmit();
    } catch (error) {
      setWrongUrl(error.response.data.message);
    }
  }, [location.pathname]);

  useEffect(() => {
    let taskId = location.pathname.split("/").pop();
    if (currentTaskAnswersArray.length) {
      currentTaskAnswersArray.map((task) => {
        if (
          task.taskId === parseInt(taskId) &&
          !task.isCorrection &&
          !task.passed
        ) {
          setIsSubmittedStorage(false);
        }
        return task;
      });
      if (taskStatus === "submitted") {
        setIsSubmittedStorage(false);
      }
    }
  }, [isSubmittedStorage]);

  return (
    <Fragment>
      <div className="task-main mt-5">
        <div className="d-flex flex-column flex-lg-row justify-content-between new-column-block">
          <div className="text-center m-auto mt-0 px-3">
            <div className="d-flex align-items-center justify-content-center mb-3">
              <div className="arrow-back me-2">
                <Link
                  to={`/${endpoints.LESSON}/${
                    lessonId ?? location.state.lessonIdLocation
                  }`}
                >
                  <img
                    className="pt-1"
                    src={chevron_left}
                    width="30px"
                    alt="Back"
                  />
                </Link>
              </div>
              <h2 className="fw-bold name-block pe-2">{taskName}</h2>
            </div>
            <div onClick={readDescription} className="cursor-pointer mb-4">
              <h4 className="fw-bold text-primary">READ DESCRIPTION</h4>
            </div>
            {taskMessage && (
              <div className="mb-4">
                <p className="mb-2 fw-bold">Message from Teacher:</p>
                <div className="outer-container">
                  <div className="teacher-message">
                    <TaskShowMoreText text={taskMessage} maxLength={220} />
                  </div>
                </div>
              </div>
            )}
            {isSubmittedStorage ? (
              <div className="text-center">
                <p className="text-primary">Your answer is submitted</p>
              </div>
            ) : (
              <div className="text-center">
                <p className="mb-2">Enter your answer URL below:</p>
                <form
                  onSubmit={handleSubmit(onHandleSubmit)}
                  className="task-form"
                >
                  <input
                    {...register("answerUrl", {
                      required: true,
                      pattern: {
                        value:
                          /^(http(s)?:\/\/)[\w.-]+(?:\.[\w\.-]+)+[\w\-\._~:/?#[\]@!\$&'\(\)\*\+,;=.]+$/,
                      },
                    })}
                    className="form-control m-auto w-100 w-md-75 w-lg-50"
                  />
                  {wrongUrl ? (
                    <p className="text-danger mt-2">{wrongUrl}</p>
                  ) : errors["answerUrl"] ? (
                    <p className="text-danger mt-2">
                      Please enter a valid URL.
                    </p>
                  ) : null}

                  <button
                    type="submit"
                    className="btn btn-sm btn-outline-success mt-3"
                  >
                    Send Answer
                  </button>
                </form>
              </div>
            )}
          </div>
          <div className="d-flex justify-content-center align-items-center mt-4 mt-lg-0">
            <img
              className="img-fluid rounded shadow-lg"
              src={side_shot}
              alt="Illustration"
              width="600"
            />
          </div>
        </div>
      </div>
      <AlertModal
        isOpen={isModalOpen}
        onClose={closeModal}
        title="Oops..."
        alertText="Your role doesn't allow you to send an answer."
        buttonText="Close"
      />
    </Fragment>
  );
};
