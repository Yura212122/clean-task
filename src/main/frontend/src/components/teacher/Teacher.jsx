import React, { Fragment, useState, useEffect, useRef } from "react";
import { baseUrl } from "../../api/http-client";

import { useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { TeacherTaskBlock } from "./TeacherTaskBlock";
import { TeacherTestBlock } from "./TeacherTestBlock";
import { useForm } from "react-hook-form";
import endpoints from "../../shared/router/endpoints";
import colors from "../../shared/dto/colors";

import chevron_left from "../../images/chevron_left.svg";
import black_triangle_down from "../../images/black_triangle_down.png";
import icon_message from "../../images/icon_message.png";
import icon_question from "../../images/icons_question.png";

import { AlertModal } from "../alert-modal/AlertModal";

import "./teacher.css";

export const Teacher = () => {
  const currentUserName = useSelector((state) => state.userReducer.user.name);
  const currentUserId = useSelector((state) => state.userReducer.user.id);
  const currentUserSurname = useSelector(
    (state) => state.userReducer.user.surname
  );
  const currentUserRole = useSelector((state) => state.userReducer.user.role);
  const currentCourseNames = useSelector(
    (state) => state.coursesReducer.courses.courses
  );
  const dropdownGroupTaskRef = useRef([]);
  const dropdownPassedTaskRef = useRef([]);
  const dropdownGroupTestRef = useRef([]);
  const dropdownPassedTestRef = useRef([]);
  const [contentTasksMain, setContentTasksMain] = useState([]);
  const [contentTasks, setContentTasks] = useState([]);
  const [contentTestsMain, setContentTestsMain] = useState([]);
  const [contentTests, setContentTests] = useState([]);
  const [changeCorrection, setChangeCorrection] = useState(false);
  const [changeGrade, setChangeGrade] = useState(false);
  const [doRequest, setDoRequest] = useState(null);
  const [sendMessageForCorrection, setSendMessageForCorrection] =
    useState(false);
  const [isOpenTask, setIsOpenTask] = useState([]);
  const [isOpenTasksOrTests, setIsOpenTasksOrTests] = useState(false);
  const [isOpenGroupTasks, setIsOpenGroupTasks] = useState([]);
  const [isOpenPassedTask, setIsOpenPassedTask] = useState([]);
  const [taskIncludesNotPassed, setTaskIncludesNotPassed] = useState(false);
  const [sortGroupAndPassedByTaskId, setSortGroupAndPassedByTaskId] = useState(
    []
  );
  const [isOpenTest, setIsOpenTest] = useState([]);
  const [isOpenGroupTests, setIsOpenGroupTests] = useState([]);
  const [isOpenPassedTest, setIsOpenPassedTest] = useState([]);
  const [isOpenModalEmpty, setIsOpenModalEmpty] = useState(false);
  const [testIncludesNotPassed, setTestIncludesNotPassed] = useState(false);
  const [sortGroupAndPassedByTestId, setSortGroupAndPassedByTestId] = useState(
    []
  );
  const [courseNames, setCourseNames] = useState([]);
  const [courseName, setCourseName] = useState("");
  const [passedOption, setPassedOption] = useState("");
  const [courseNamesTest, setCourseNamesTest] = useState([]);
  const [courseNameTest, setCourseNameTest] = useState("");
  const [passedOptionTest, setPassedOptionTest] = useState("");
  const [isHoveredCourses, setIsHoveredCourses] = useState(false);
  const [isHoveredIsDone, setIsHoveredIsDone] = useState(false);
  const [isHoveredAll, setIsHoveredAll] = useState(false);
  const [isHoveredCoursesTest, setIsHoveredCoursesTest] = useState(false);
  const [isHoveredIsDoneTest, setIsHoveredIsDoneTest] = useState(false);
  const [isHoveredAllTest, setIsHoveredAllTest] = useState(false);
  const [currentPageTasks, setCurrentPageTasks] = useState(1);
  const [currentPageTests, setCurrentPageTests] = useState(1);
  const itemsPerPage = 10;
  const startIndexTasks = (currentPageTasks - 1) * itemsPerPage;
  const endIndexTasks = startIndexTasks + itemsPerPage;
  const startIndexTests = (currentPageTests - 1) * itemsPerPage;
  const endIndexTests = startIndexTests + itemsPerPage;
  const currentTaskItems = contentTasks
    .filter((task) => task.taskAnswers.length)
    .slice(startIndexTasks, endIndexTasks);
  const currentTestItems = contentTests
    .filter((test) => test.testAnswers.length)
    .slice(startIndexTests, endIndexTests);
  const totalTasksPages = Math.ceil(
    contentTasks.filter((task) => task.taskAnswers.length).length / itemsPerPage
  );
  const totalTestsPages = Math.ceil(
    contentTests.filter((test) => test.testAnswers.length).length / itemsPerPage
  );

  const navigate = useNavigate();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalText, setModalText] = useState("");

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => {
    setIsModalOpen(false);
    setModalText("");
  };

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm();

  const handlePageChangeForwardTasks = () => {
    if (currentPageTasks !== totalTasksPages) {
      setCurrentPageTasks(currentPageTasks + 1);
    }
  };

  const handlePageChangeBackTasks = () => {
    if (currentPageTasks !== 1) {
      setCurrentPageTasks(currentPageTasks - 1);
    }
  };

  const handlePageChangeForwardTests = () => {
    if (currentPageTests !== totalTestsPages) {
      setCurrentPageTests(currentPageTests + 1);
    }
  };

  const handlePageChangeBackTests = () => {
    if (currentPageTests !== 1) {
      setCurrentPageTests(currentPageTests - 1);
    }
  };
  const handleSetDoRequestCorrection = (data) => {
    setDoRequest({
      correctionAnswer: data.correctionAnswer,
      isOpen: data.isOpen,
    });
  };

  const handleSetDoRequestPassed = (data) => {
    setDoRequest({ passedAnswer: data.passedAnswer, isOpen: data.isOpen });
  };
  const sendAnswerGrade = async (obj) => {
    if (obj.correctionAnswer) {
      try {
        const { data } = await baseUrl.post(
          "/api/teacher/tasksSubmission/grade",
          {
            answerId: obj.correctionAnswer,
            isPassed: false,
            isCorrection: true,
            messageForCorrection: "",
            isRead: true,
          }
        );
        setChangeCorrection(!changeCorrection);
      } catch (error) {
        const errorMessage =
          error.response?.data?.message ||
          "Failed to send a grade for correction";
        setModalText(errorMessage);
        openModal();
      }
    } else if (obj.passedAnswer) {
      try {
        const { data } = await baseUrl.post(
          "/api/teacher/tasksSubmission/grade",
          {
            answerId: obj.passedAnswer,
            isPassed: true,
            isCorrection: false,
            messageForCorrection: "",
            isRead: false,
          }
        );
        setChangeGrade(!changeGrade);
      } catch (error) {
        const errorMessage =
          error.response?.data?.message ||
          "Failed to send a grade for correction";
        setModalText(errorMessage);
        openModal();
      }
    } else if (obj.message) {
      if (!obj.message.trim()) {
        setModalText("Empty message!");
        openModal();
      } else {
        try {
          const { data } = await baseUrl.post(
            "/api/teacher/tasksSubmission/grade",
            {
              answerId: doRequest.correctionAnswer,
              isPassed: false,
              isCorrection: true,
              messageForCorrection: obj.message,
              isRead: true,
            }
          );
          setChangeCorrection(!changeCorrection);
          setSendMessageForCorrection(false);
          reset();
        } catch (error) {
          const errorMessage =
            error.response?.data?.message ||
            "Failed to send a grade for correction";
          setModalText(errorMessage);
          openModal();
        }
      }
    }
    setDoRequest(null);
  };

  const checkTheFormObj = (obj) => {
    if (obj.message && !obj.message.trim()) {
      setIsOpenModalEmpty(true);
    } else {
      sendAnswerGrade(obj);
    }
  };

  const resetGeneralSort = () => {
    setCourseName("");
    setPassedOption("");
  };

  const changeHoveredCourses = (value) => {
    setIsHoveredAll(value);
    setIsHoveredCourses(value);
  };

  const changeHoveredIsDone = (value) => {
    setIsHoveredAll(value);
    setIsHoveredIsDone(value);
  };

  const resetGeneralSortTest = () => {
    setCourseNameTest("");
    setPassedOptionTest("");
  };

  const changeHoveredCoursesTest = (value) => {
    setIsHoveredAllTest(value);
    setIsHoveredCoursesTest(value);
  };

  const changeHoveredIsDoneTest = (value) => {
    setIsHoveredAllTest(value);
    setIsHoveredIsDoneTest(value);
  };

  const readDescription = (taskDescription) => {
    window.open(`${taskDescription}`, "_blank");
  };

  useEffect(() => {
    if (currentUserRole === "TEACHER" || currentUserRole === "MENTOR") {
      const getTasksData = async () => {
        try {
          const { data } = await baseUrl.get(
            "/api/teacher/tasksSubmissionByTeacherId/" + currentUserId,
            {
              params: { page: 0, size: 100 },
            }
          );
          let contentData = [];
          if (data.content && currentCourseNames) {
            contentData = data.content.filter((item) =>
              item.groupNames.some((groupName) =>
                currentCourseNames.includes(groupName)
              )
            );
          }
          const uniqueCoursesArr = contentData.flatMap((task) =>
            task.taskAnswers.map((answer) => answer.course)
          );
          const allCoursesArr = [
            ...new Set(contentData.flatMap((task) => task.groupNames)),
          ];
          const allUniqueCoursesArr = [
            ...new Set([...uniqueCoursesArr, ...allCoursesArr]),
          ];
          setCourseNames(allUniqueCoursesArr);
          setContentTasks(contentData);
          setContentTasksMain(contentData);
        } catch {
          setModalText("Failed to get all task answers");
          openModal();
        }
      };
      getTasksData();

      const getTestsData = async () => {
        try {
          const { data } = await baseUrl.get(
            "/api/teacher/testsSubmissionByTeacherId/" + currentUserId,
            {
              params: { page: 0, size: 100 },
            }
          );
          const contentDataTests = data.content.filter((item) =>
            item.groupNames.some((groupName) =>
              currentCourseNames.includes(groupName)
            )
          );
          const uniqueCoursesArrTests = (contentDataTests || [])
            .filter((test) => test !== null)
            .flatMap((test) =>
              (test.testAnswers || [])
                .filter((answer) => answer !== null)
                .map((answer) => answer.course)
            );
          const testsWithoutNulls = (contentDataTests || []).map((test) => {
            if (test.testAnswers.length) {
              test.testAnswers = test.testAnswers.filter(
                (testAnswer) => testAnswer !== null
              );
            }
            return test;
          });
          const allCoursesArrTests = [
            ...new Set(contentDataTests.flatMap((test) => test.groupNames)),
          ];
          const allUniqueCoursesArrTests = [
            ...new Set([...uniqueCoursesArrTests, ...allCoursesArrTests]),
          ];
          setCourseNamesTest(allUniqueCoursesArrTests);
          setContentTests(contentDataTests);
          setContentTestsMain(contentDataTests);
        } catch {
          setModalText("Failed to get all test answers");
          openModal();
        }
      };
      getTestsData();
    } else if (!currentUserRole) {
      navigate(`/${endpoints.NOT_LOGGED_IN}`);
    } else {
      navigate(`/${endpoints.NOT_FOUND}`);
    }
  }, [
    changeCorrection,
    changeGrade,
    sortGroupAndPassedByTaskId,
    sortGroupAndPassedByTestId,
  ]);

  useEffect(() => {
    if (contentTasksMain.length) {
      let newContentData = [];
      if (!courseName && !passedOption) {
        newContentData = contentTasksMain;
        setContentTasks(newContentData);
      } else if (courseName && !passedOption) {
        newContentData = contentTasksMain
          .map((task) => ({
            ...task,
            taskAnswers: task.taskAnswers.filter(
              (answer) => answer.course === courseName
            ),
          }))
          .filter((task) => task.taskAnswers.length > 0);
        setContentTasks(newContentData);
      } else if (!courseName && passedOption) {
        if (passedOption === "Not checked") {
          newContentData = contentTasksMain
            .map((task) => ({
              ...task,
              taskAnswers: task.taskAnswers.filter(
                (answer) => !answer.isPassed && !answer.isCorrection
              ),
            }))
            .filter((task) => task.taskAnswers.length > 0);
          setContentTasks(newContentData);
        } else if (passedOption === "Completed") {
          newContentData = contentTasksMain
            .map((task) => ({
              ...task,
              taskAnswers: task.taskAnswers.filter((answer) => answer.isPassed),
            }))
            .filter((task) => task.taskAnswers.length > 0);
          setContentTasks(newContentData);
        } else if (passedOption === "Need correction") {
          newContentData = contentTasksMain
            .map((task) => ({
              ...task,
              taskAnswers: task.taskAnswers.filter(
                (answer) => answer.isCorrection
              ),
            }))
            .filter((task) => task.taskAnswers.length > 0);
          setContentTasks(newContentData);
        }
      } else if (courseName && passedOption) {
        if (passedOption === "Not checked") {
          newContentData = contentTasksMain
            .map((task) => ({
              ...task,
              taskAnswers:
                task.taskAnswers.filter(
                  (answer) => answer.course === courseName
                ) &&
                task.taskAnswers.filter(
                  (answer) => !answer.isPassed && !answer.isCorrection
                ),
            }))
            .filter((task) => task.taskAnswers.length > 0);
          setContentTasks(newContentData);
        } else if (passedOption === "Completed") {
          newContentData = contentTasksMain
            .map((task) => ({
              ...task,
              taskAnswers:
                task.taskAnswers.filter(
                  (answer) => answer.course === courseName
                ) &&
                task.taskAnswers.filter((answer) => answer.isPassed === true),
            }))
            .filter((task) => task.taskAnswers.length > 0);
          setContentTasks(newContentData);
        } else if (passedOption === "Need correction") {
          newContentData = contentTasksMain
            .map((task) => ({
              ...task,
              taskAnswers:
                task.taskAnswers.filter(
                  (answer) => answer.course === courseName
                ) &&
                task.taskAnswers.filter(
                  (answer) => answer.isCorrection === true
                ),
            }))
            .filter((task) => task.taskAnswers.length > 0);
          setContentTasks(newContentData);
        }
      }
    }

    if (contentTestsMain.length) {
      let newContentDataTests = [];
      if (!courseNameTest && !passedOptionTest) {
        newContentDataTests = contentTestsMain;
        setContentTests(newContentDataTests);
      } else if (courseNameTest && !passedOptionTest) {
        newContentDataTests = contentTestsMain
          .map((test) => ({
            ...test,
            testAnswers: test.testAnswers.filter(
              (answer) => answer.course === courseNameTest
            ),
          }))
          .filter((test) => test.testAnswers.length > 0);
        setContentTests(newContentDataTests);
      } else if (!courseNameTest && passedOptionTest) {
        if (passedOptionTest === "Not passed") {
          newContentDataTests = contentTestsMain
            .map((test) => ({
              ...test,
              testAnswers: test.testAnswers.filter(
                (answer) => !answer.isPassed
              ),
            }))
            .filter((test) => test.testAnswers.length > 0);
          setContentTests(newContentDataTests);
        } else if (passedOptionTest === "Passed") {
          newContentDataTests = contentTestsMain
            .map((test) => ({
              ...test,
              testAnswers: test.testAnswers.filter((answer) => answer.isPassed),
            }))
            .filter((test) => test.testAnswers.length > 0);
          setContentTests(newContentDataTests);
        } else if (passedOptionTest === "Not started") {
          newContentDataTests = contentTestsMain.filter(
            (test) => !test.testAnswers.length
          );
          setContentTests(newContentDataTests);
        }
      } else if (courseNameTest && passedOptionTest) {
        if (passedOptionTest === "Not passed") {
          newContentDataTests = contentTestsMain
            .map((test) => ({
              ...test,
              testAnswers:
                test.testAnswers.filter(
                  (answer) => answer.course === courseNameTest
                ) && test.testAnswers.filter((answer) => !answer.isPassed),
            }))
            .filter((test) => test.testAnswers.length > 0);
          setContentTests(newContentDataTests);
        } else if (passedOptionTest === "Passed") {
          newContentDataTests = contentTestsMain
            .map((test) => ({
              ...test,
              testAnswers:
                test.testAnswers.filter(
                  (answer) => answer.course === courseNameTest
                ) && test.testAnswers.filter((answer) => answer.isPassed),
            }))
            .filter((test) => test.testAnswers.length > 0);
          setContentTests(newContentDataTests);
        } else if (passedOptionTest === "Not started") {
          newContentDataTests = contentTestsMain
            .map((test) => ({
              ...test,
              testAnswers:
                test.testAnswers.filter(
                  (answer) => answer.course === courseNameTest
                ) && !test.testAnswers.length,
            }))
            .filter((test) => test.testAnswers.length > 0);
          setContentTests(newContentDataTests);
        }
      }
    }
  }, [
    courseName,
    passedOption,
    contentTasksMain,
    courseNameTest,
    passedOptionTest,
    contentTestsMain,
  ]);

  const handleClickOutside = (event) => {
    const isOutsideDropdown =
      dropdownGroupTaskRef.current.every(
        (ref) => ref && !ref.contains(event.target)
      ) &&
      dropdownPassedTaskRef.current.every(
        (ref) => ref && !ref.contains(event.target)
      ) &&
      dropdownGroupTestRef.current.every(
        (ref) => ref && !ref.contains(event.target)
      ) &&
      dropdownPassedTestRef.current.every(
        (ref) => ref && !ref.contains(event.target)
      );
    if (isOutsideDropdown) {
      setIsOpenGroupTasks((prevState) => {
        return prevState.map((obj) => {
          return { ...obj, isOpen: false };
        });
      });
      setIsOpenPassedTask((prevState) => {
        return prevState.map((obj) => {
          return { ...obj, isOpen: false };
        });
      });
      setIsOpenGroupTests((prevState) => {
        return prevState.map((obj) => {
          return { ...obj, isOpen: false };
        });
      });
      setIsOpenPassedTest((prevState) => {
        return prevState.map((obj) => {
          return { ...obj, isOpen: false };
        });
      });
    }
  };

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const toggleDropdownTask = (taskId) => {
    if (isOpenTask.length) {
      if (
        isOpenTask.filter((obj) => {
          return obj.taskId === taskId;
        }).length
      ) {
        setIsOpenTask((prevState) => {
          return prevState.map((obj) => {
            if (obj.taskId === taskId && !obj.isOpen) {
              return { ...obj, isOpen: true };
            } else if (obj.taskId === taskId && obj.isOpen) {
              return { ...obj, isOpen: false };
            }
            return obj;
          });
        });
      } else {
        setIsOpenTask((prev) => [...prev, { taskId: taskId, isOpen: true }]);
      }
    } else {
      setIsOpenTask((prev) => [...prev, { taskId: taskId, isOpen: true }]);
    }
  };

  const toggleDropdownTaskFromChild = (id) => {
    toggleDropdownTask(id);
  };

  const toggleDropdownTest = (testId) => {
    if (isOpenTest.length) {
      if (
        isOpenTest.filter((obj) => {
          return obj.testId === testId;
        }).length
      ) {
        setIsOpenTest((prevState) => {
          return prevState.map((obj) => {
            if (obj.testId === testId && !obj.isOpen) {
              return { ...obj, isOpen: true };
            } else if (obj.testId === testId && obj.isOpen) {
              return { ...obj, isOpen: false };
            }
            return obj;
          });
        });
      } else {
        setIsOpenTest((prev) => [...prev, { testId: testId, isOpen: true }]);
      }
    } else {
      setIsOpenTest((prev) => [...prev, { testId: testId, isOpen: true }]);
    }
  };

  const toggleDropdownTestFromChild = (id) => {
    toggleDropdownTest(id);
  };

  const exitFormMessage = () => {
    setSendMessageForCorrection(!sendMessageForCorrection);
    reset();
  };

  return (
    <Fragment>
      <div
        className="container-fluid min-vh-100 d-flex flex-column scrollable-element"
        style={{ backgroundColor: colors.WHITE, padding: "56px" }}
      >
        <div className="row align-items-center">
          <div className="col-auto mt-2 pt-3">
            <Link to={`/${endpoints.COURSES}`}>
              <img
                src={chevron_left}
                width="30px"
                className="mt-1"
                alt="chevron_left"
              />
            </Link>
          </div>
          <div className="col">
            <h1 className="mt-3 mb-5 text-center text-md-start">
              <span className="fw-bold text-black">TEACHER:</span>{" "}
              {currentUserName} {currentUserSurname}
            </h1>
          </div>
        </div>
        <div className="d-flex flex-wrap gap-2 justify-content-center">
          <button className="btn btn-sm button-style-active fw-bold">
            Task Homeworks
          </button>
          <button className="btn btn-sm button-style-no-active fw-bold">
            Test Homeworks
          </button>
        </div>
        <div className="mt-3 mb-5">
          <div className="row mb-4 position-relative">
            <div className="col-auto">Sort tasks by:</div>
            <div className="col-auto">
              <button className="btn btn-sm sort-button">
                <span className="sort-span">Select</span>
                <img
                  src={black_triangle_down}
                  className="black-triangle-down"
                  alt="black-triangle-down"
                />
              </button>
            </div>
          </div>
          <div className="text-center">Page 1 of X</div>
          <div className="d-flex justify-content-center text-center mt-3">
            <img
              src={chevron_left}
              width="30px"
              style={{ opacity: "0.5", cursor: "default" }}
              alt="chevron-left"
            />
            <div className="mx-3">Page 1 of X</div>
            <img
              src={chevron_left}
              width="30px"
              style={{ rotate: "180deg", opacity: "1", cursor: "pointer" }}
              alt="chevron-left"
            />
          </div>
        </div>
      </div>
    </Fragment>
  );
};
