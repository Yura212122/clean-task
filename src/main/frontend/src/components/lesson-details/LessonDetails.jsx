import React, { useState, Fragment, useEffect } from "react";
import { useGetLessonDetails } from "../../hooks/lesson-details-request";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { TaskBlock } from "./TaskBlock";
import { TestBlock } from "./TestBlock";
import { Spinner } from "../spinner/Spinner";
import ReactPlayer from "react-player/youtube";
import { useSelector, useDispatch } from "react-redux";
import { baseUrl } from "../../api/http-client";
import endpoints from "../../shared/router/endpoints";

import "./LessonDetails.css";

import clock from "../../images/clock.png";
import curved_arrow_down from "../../images/curved_arrow_down.png";
import curved_arrow_next from "../../images/curved_arrow_next.png";

import { setTask } from "../../redux/slices/taskSlice";
import { setLesson } from "../../redux/slices/lessonSlice";
import { setCourse } from "../../redux/slices/courseSlice";
import chevron_left from "../../images/chevron_left.svg";
import { setNumberOfLesson } from "../../redux/slices/headerSlice";
import { setTests } from "../../redux/slices/testsForLessonSlice";

import { AlertModal } from "../alert-modal/AlertModal";

const LessonDetails = ({ lessonId }) => {
  const [progress, setProgress] = useState(0);
  const [progressMessage, setProgressMessage] = useState("");
  const currentUser = useSelector((state) => state.userReducer.user);
  const currentCourse = useSelector((state) => state.courseReducer.course);
  const currentNameStorage = useSelector(
    (state) => state.courseReducer.course.name
  );
  const currentCourseIdStorage = useSelector(
    (state) => state.courseReducer.course.courseId
  );
  const currentNumberOfLesson = useSelector(
    (state) => state.courseReducer.course.numberOfLesson
  );
  const lessonNum = useSelector(
    (state) => state.headerReducer.header.numberOfLesson
  );
  const currentLessonsInCourseStorage = useSelector(
    (state) => state.courseReducer.course.listOfLessons
  );
  const [courseName, setCourseName] = useState(currentNameStorage);
  const [courseId, setCourseId] = useState(currentCourseIdStorage);
  const [numberOfLesson, setNumberOfLesson] = useState(
    currentNumberOfLesson || lessonNum
  );
  const [lessonsInCourse, setLessonsInCourse] = useState(
    currentLessonsInCourseStorage
  );
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [tasksStatus, setTasksStatus] = useState([]);
  const [testsStatus, setTestsStatus] = useState([]);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalText, setModalText] = useState("");
  const openModal = () => setIsModalOpen(true);
  const closeModal = () => {
    setIsModalOpen(false);
    setModalText("");
  };

  const { data, isLoading, error } = useGetLessonDetails(lessonId);

  const radius = 45;
  const strokeWidth = 8;
  const normalizedRadius = radius - strokeWidth * 2;
  const circumference = normalizedRadius * 2 * Math.PI;
  const strokeDashoffset = circumference - (progress / 100) * circumference;
  const offsetAngle = -90;
  const location = useLocation();

  useEffect(() => {
    if (location.state) {
      setCourseName(location.state.courseName || currentNameStorage);
      setCourseId(location.state.courseId || currentCourseIdStorage);
      setNumberOfLesson(location.state.numberOfLesson || lessonNum);
      setLessonsInCourse(
        location.state.lessonsInCourse || currentLessonsInCourseStorage
      );
      dispatch(
        setCourse({
          name: location.state.courseName,
          courseId: location.state.courseId,
          numberOfLesson: location.state.numberOfLesson,
          listOfLessons: location.state.lessonsInCourse,
        })
      );
    }
  }, [
    location.state,
    dispatch,
    currentNameStorage,
    currentCourseIdStorage,
    currentLessonsInCourseStorage,
    lessonNum,
  ]);

  const indexOfLesson =
    lessonsInCourse &&
    lessonsInCourse.length &&
    lessonsInCourse.indexOf(
      lessonsInCourse.filter((lesson) => {
        return lesson.id === parseInt(lessonId);
      })[0]
    );

  const videoUrl = data ? data.videoUrl : "";

  const dataTasks = data
    ? data.tasks.sort(function (a, b) {
        if (a.id > b.id) {
          return 1;
        }
        if (a.id < b.id) {
          return -1;
        }
        return 0;
      })
    : [];

  const dataTests = data
    ? [...data.tests].sort(function (a, b) {
        if (a.id > b.id) {
          return 1;
        }
        if (a.id < b.id) {
          return -1;
        }
        return 0;
      })
    : [];

  useEffect(() => {
    if (dataTests.length) {
      let newDataTests = dataTests.map((test) => {
        let questions = test.questions.map((obj) => {
          return {
            id: obj.id,
            question: obj.question,
            options: obj.options,
            correctAnswers: obj.correctAnswers,
            selectedAnswers: [],
          };
        });

        return {
          id: test.id,
          name: test.name,
          deadline: test.deadline,
          questions: questions,
          mandatory: test.mandatory,
        };
      });
      dispatch(setTests(newDataTests));
    }
  }, [JSON.stringify(dataTests)]);

  useEffect(() => {
    if (!courseId) {
      navigate(`/${endpoints.COURSES}`);
    }
    if (
      lessonsInCourse &&
      lessonsInCourse.length &&
      !lessonsInCourse.find((lesson) => lesson.id === parseInt(lessonId))
    ) {
      navigate(`/${endpoints.NOT_FOUND}`);
    }
    if (currentUser.id) {
      const getProgress = async () => {
        try {
          const { data: dataProgress } = await baseUrl.get(
            `/api/tasks/progress/${currentUser.id}/${lessonId}`
          );
          setProgress(dataProgress.percent);
          setProgressMessage(dataProgress.progress);
        } catch {
          setModalText("Failed to get a progress by lessonId");
          openModal();
        }
      };
      getProgress();
    }

    if (dataTasks.length && currentUser.id) {
      dataTasks.map((task) => {
        const getTaskStatus = async () => {
          try {
            const { data } = await baseUrl.get(
              `/api/tasks/${currentUser.id}/${task.id}`
            );
            if (data && !data.passed && !data.isCorrection) {
              if (
                tasksStatus.length &&
                !tasksStatus.filter(
                  (taskStatus) => taskStatus.taskId === task.id
                )
              ) {
                setTasksStatus((prev) => [
                  ...prev,
                  {
                    taskId: task.id,
                    taskName: task.name,
                    description: task.descriptionUrl,
                    answerId: data.answerId,
                    status: "submitted",
                  },
                ]);
              } else if (!tasksStatus.length) {
                setTasksStatus((prev) => [
                  ...prev,
                  {
                    taskId: task.id,
                    taskName: task.name,
                    description: task.descriptionUrl,
                    answerId: data.answerId,
                    status: "submitted",
                  },
                ]);
              }
            } else if (data && data.passed && !data.isCorrection) {
              if (
                tasksStatus.length &&
                !tasksStatus.filter(
                  (taskStatus) => taskStatus.taskId === task.id
                )
              ) {
                setTasksStatus((prev) => [
                  ...prev,
                  {
                    taskId: task.id,
                    taskName: task.name,
                    description: task.descriptionUrl,
                    answerId: data.answerId,
                    status: "passed",
                  },
                ]);
              } else if (!tasksStatus.length) {
                setTasksStatus((prev) => [
                  ...prev,
                  {
                    taskId: task.id,
                    taskName: task.name,
                    description: task.descriptionUrl,
                    answerId: data.answerId,
                    status: "passed",
                  },
                ]);
              }
            } else if (data && !data.passed && data.isCorrection) {
              if (
                tasksStatus.length &&
                !tasksStatus.filter(
                  (taskStatus) => taskStatus.taskId === task.id
                )
              ) {
                setTasksStatus((prev) => [
                  ...prev,
                  {
                    taskId: task.id,
                    taskName: task.name,
                    description: task.descriptionUrl,
                    answerId: data.answerId,
                    message: data.messageForCorrection,
                    status: "need correction",
                  },
                ]);
              } else if (!tasksStatus.length) {
                setTasksStatus((prev) => [
                  ...prev,
                  {
                    taskId: task.id,
                    taskName: task.name,
                    description: task.descriptionUrl,
                    answerId: data.answerId,
                    message: data.messageForCorrection,
                    status: "need correction",
                  },
                ]);
              }
            } else {
              if (
                tasksStatus.length &&
                !tasksStatus.filter(
                  (taskStatus) => taskStatus.taskId === task.id
                )
              ) {
                setTasksStatus((prev) => [
                  ...prev,
                  {
                    taskId: task.id,
                    taskName: task.name,
                    description: task.descriptionUrl,
                    answerId: null,
                    status: "not submitted",
                  },
                ]);
              } else if (!tasksStatus.length) {
                setTasksStatus((prev) => [
                  ...prev,
                  {
                    taskId: task.id,
                    taskName: task.name,
                    description: task.descriptionUrl,
                    answerId: null,
                    status: "not submitted",
                  },
                ]);
              }
            }
          } catch {
            setModalText("Failed to get task_answer by userId");
            openModal();
          }
        };
        getTaskStatus();
        return task;
      });
    }

    if (dataTests.length && currentUser.id) {
      dataTests.map((test) => {
        const getTestStatus = async () => {
          const { data } = await baseUrl.get(
            `/api/tests/${currentUser.id}/${test.id}`
          );

          if (data && !testsStatus.length) {
            setTestsStatus((prev) => [
              ...prev,
              {
                testId: test.id,
                answerId: data.answerId,
                attempt: data.attempt,
                passed: data.passed,
              },
            ]);
          } else if (data && testsStatus.length) {
            if (!testsStatus.find((test) => test.answerId === data.answerId)) {
              setTestsStatus((prev) => [
                ...prev,
                {
                  testId: test.id,
                  answerId: data.answerId,
                  attempt: data.attempt,
                  passed: data.passed,
                },
              ]);
            } else if (
              testsStatus.find((test) => test.answerId === data.answerId)
            ) {
              let newArrTestStatus = testsStatus.filter(
                (test) => test.answerId !== data.answerId
              );
              setTestsStatus(newArrTestStatus);
              setTestsStatus((prev) => [
                ...prev,
                {
                  testId: test.id,
                  answerId: data.answerId,
                  attempt: data.attempt,
                  passed: data.passed,
                },
              ]);
            }
          }
        };
        getTestStatus();
        return test;
      });
    }
  }, [
    data,
    JSON.stringify(dataTasks),
    JSON.stringify(dataTests),
    lessonId,
    lessonsInCourse,
  ]);

  const isPassedTask = (taskId, taskName) => {
    tasksStatus.map((taskStatus) => {
      if (taskStatus.taskId === taskId) {
        if (taskStatus.status === "not submitted") {
          dispatch(
            setTask({
              id: taskId,
              name: taskName,
              description: taskStatus.description,
              answerId: null,
              message: "",
              status: "",
            })
          );
          dispatch(
            setLesson({
              id: lessonId,
            })
          );
          navigate(`/${endpoints.TASK}/${taskId}`);
        } else if (taskStatus.status === "need correction") {
          dispatch(
            setTask({
              id: taskId,
              name: taskName,
              description: taskStatus.description,
              answerId: taskStatus.answerId,
              message: taskStatus.message,
              status: "",
            })
          );
          dispatch(
            setLesson({
              id: lessonId,
            })
          );
          navigate(`/${endpoints.TASK}/${taskId}`);
        }
      }
      return taskStatus;
    });
  };

  if (error && !data) {
    return (
      <div className="d-flex flex-column g-2" style={{ paddingTop: "56px" }}>
        <div className="mt-4 alert alert-danger" role="alert">
          {error.response.data[0].name}
        </div>
        <Link to={endpoints.HOME} className="mt-4 btn btn-primary btn-lg ">
          Повернутися на головну сторінку
        </Link>
      </div>
    );
  }

  return (
    <Fragment>
      {isLoading ? (
        <Spinner />
      ) : (
        <div
          className="w-100 min-vh-100 d-flex flex-column"
          style={{ paddingTop: "56px" }}
        >
          <div
            className="content-container d-flex p-4"
            style={{ backgroundColor: "white" }}
          >
            <div className="lesson-container d-block">
              <div className="lesson-block">
                <div className="d-flex align-items-center mb-3 text-center">
                  <div className="arrow-back">
                    <Link to={`/${endpoints.COURSE}/${courseId}`}>
                      <img src={chevron_left} width="30px" alt="chevron_left" />
                    </Link>
                  </div>
                  <h1 className="fw-bold">{courseName}</h1>
                </div>
                <div className="title-block d-flex justify-content-between">
                  <div>
                    <h4>Lesson {numberOfLesson}</h4>
                    <h3>{data.name}</h3>
                  </div>
                  {indexOfLesson > 0 ? (
                    <div>
                      <Link
                        to={`/${endpoints.LESSON}/${
                          lessonsInCourse[indexOfLesson - 1].id
                        }`}
                        state={{
                          courseName: courseName,
                          courseId: courseId,
                          numberOfLesson: numberOfLesson - 1,
                          lessonsInCourse: lessonsInCourse,
                        }}
                      >
                        <img src={curved_arrow_down} alt="curved_arrow_down" />
                      </Link>
                    </div>
                  ) : (
                    ""
                  )}
                </div>
                <div className="mt-5 video-player-wrapper text-center">
                  <ReactPlayer
                    className="react-player"
                    light
                    url={videoUrl}
                    width="100%"
                    height="100%"
                    playing
                    controls
                  />
                </div>
                <div className="row-block row mt-3">
                  {data.tasks.map((task, i) => (
                    <TaskBlock
                      key={task.id}
                      task={task}
                      i={i}
                      tasksStatus={tasksStatus}
                      isPassedTask={() => isPassedTask(task.id, task.name)}
                    />
                  ))}
                </div>
                <div className="row-block row mt-3">
                  {dataTests.map((test) => (
                    <TestBlock
                      key={test.id}
                      test={test}
                      testsStatus={testsStatus}
                      lessonId={lessonId}
                    />
                  ))}
                </div>
              </div>
              <div className="block-fixed">
                <div className="task-progress">
                  <div className="box1 text-center">Due time:</div>
                  <div className=" mt-1 text-center">
                    <img
                      src={clock}
                      style={{ width: "20px", marginRight: "5px" }}
                      alt="clock"
                    />
                    <span>2 days</span>
                  </div>
                  <div className="box4 text-center">
                    <span>Task progression:</span>
                  </div>
                  <div className="d-flex align-items-center">
                    <div className="box5">
                      <svg height={radius * 2} width={radius * 2}>
                        <circle
                          stroke="white"
                          fill="transparent"
                          strokeWidth={strokeWidth}
                          r={normalizedRadius}
                          cx={radius}
                          cy={radius}
                        />
                        <g
                          transform={`rotate(${offsetAngle} ${radius} ${radius})`}
                        >
                          <circle
                            stroke="#51b556"
                            fill="transparent"
                            strokeWidth={strokeWidth}
                            strokeDasharray={
                              circumference + " " + circumference
                            }
                            style={{ strokeDashoffset }}
                            r={normalizedRadius}
                            cx={radius}
                            cy={radius}
                          />
                        </g>
                        <text
                          x="50%"
                          y="50%"
                          textAnchor="middle"
                          dominantBaseline="middle"
                          fill="white"
                          fontSize="16px"
                        >
                          {progress}%
                        </text>
                      </svg>
                    </div>
                    <div className="box6">
                      <p>{progressMessage}</p>
                    </div>
                  </div>
                </div>
              </div>
              {lessonsInCourse.length > indexOfLesson + 1 ? (
                <div className="title-block d-flex justify-content-between mt-5 mb-5">
                  <div>
                    <h4>Lesson {indexOfLesson + 2}</h4>
                    <h3>{lessonsInCourse[indexOfLesson + 1].name}</h3>
                  </div>
                  <div>
                    <Link
                      to={`/${endpoints.LESSON}/${
                        lessonsInCourse[indexOfLesson + 1].id
                      }`}
                      state={{
                        courseName: courseName,
                        courseId: courseId,
                        numberOfLesson: numberOfLesson + 1,
                        lessonsInCourse: lessonsInCourse,
                      }}
                    >
                      <img src={curved_arrow_next} alt="curved_arrow_next" />
                    </Link>
                  </div>
                </div>
              ) : (
                ""
              )}
            </div>
          </div>
        </div>
      )}
      <AlertModal
        isOpen={isModalOpen}
        onClose={closeModal}
        title="Oops..."
        alertText={modalText}
        buttonText="Close"
      />
    </Fragment>
  );
};

export default LessonDetails;
