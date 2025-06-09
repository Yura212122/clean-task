import React, { Fragment, useEffect, useState } from "react";
import { baseUrl } from "../../api/http-client";
import { useDispatch, useSelector } from "react-redux";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { setLesson } from "../../redux/slices/lessonSlice";

import endpoints from "../../shared/router/endpoints";
import colors from "../../shared/dto/colors";
import blue_brain from "../../images/blue_brain.png";
import red_brain from "../../images/red_brain.png";
import green_brain from "../../images/green_brain.png";
import chevron_left from "../../images/chevron_left.svg";

import "./Test.css";

export const Test = ({ testId }) => {
  const [tests, setTests] = useState([]);
  const [correctAnswers, setCorrectAnswers] = useState(0);
  const [isPassed, setIsPassed] = useState(false);
  const [isSubmit, setIsSubmit] = useState(false);
  const [getCorrectAnswers, setGetCorrectAnswers] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSecondModalOpen, setIsSecondModalOpen] = useState(false);
  const [testsName, setTestsName] = useState("");
  const currentUser = useSelector((state) => state.userReducer.user);
  const currentCourse = useSelector((state) => state.courseReducer.course.name);
  const currentLessonId = useSelector((state) => state.lessonReducer.lesson.id);
  const currentTests = useSelector(
    (state) => state.testsForLessonReducer.tests
  );
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  let lessonId;

  if (location.state) {
    lessonId = location.state.lessonId;
    dispatch(
      setLesson({
        lesson: {
          id: lessonId,
        },
      })
    );
  } else {
    lessonId = currentLessonId;
  }

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [isModalOpen]);

  useEffect(() => {
    if (!currentUser.id) {
      navigate(`/${endpoints.NOT_LOGGED_IN}`);
    }
    if (!lessonId) {
      navigate(`/${endpoints.COURSES}`);
    }

    let pathArr = location.pathname.split("/");
    let newDataTests = currentTests.filter(
      (test) => test.id === parseInt(pathArr[pathArr.length - 1])
    );
    if (!newDataTests.length) {
      navigate(`/${endpoints.COURSES}`);
    }
    setTests(newDataTests[0].questions);
    setTestsName(newDataTests[0].name);
  }, []);

  const showTheCorrectAnswers = () => {
    setGetCorrectAnswers(true);
    setIsModalOpen(false);
  };

  const handleOptionSelection = (testId, option) => {
    setTimeout(() => {
      setTests((prevTests) => {
        return prevTests.map((test) => {
          if (test.id === testId) {
            const isSelected = test.selectedAnswers.includes(option);
            const updatedSelectedAnswers = isSelected
              ? test.selectedAnswers.filter((answer) => answer !== option)
              : [...test.selectedAnswers, option];
            return { ...test, selectedAnswers: updatedSelectedAnswers };
          }
          return test;
        });
      });
    }, 0);
  };

  const sendTestAnswer = async (result) => {
    let passed;
    let resultInPercentage = (result * 100) / tests.length;
    if (resultInPercentage >= 80) {
      passed = true;
      setIsPassed(true);
    } else {
      passed = false;
    }
    const { data } = await baseUrl.post(
      `/api/tests/${currentUser.id}/${testId}/submit?passed=${passed}&totalScore=${resultInPercentage}&course=${currentCourse}`
    );
    setIsSubmit(true);
    setIsModalOpen(true);
  };

  const calculateResult = () => {
    setIsSecondModalOpen(false);
    let correctAnswers = 0;
    tests.forEach((test) => {
      const allCorrect = test.correctAnswers.every((answer) =>
        test.selectedAnswers.includes(answer)
      );
      const noExtraAnswers = test.selectedAnswers.every((answer) =>
        test.correctAnswers.includes(answer)
      );

      if (allCorrect && noExtraAnswers) {
        correctAnswers++;
      }
    });
    sendTestAnswer(correctAnswers);
    setCorrectAnswers(correctAnswers);
    return correctAnswers;
  };

  const acceptAction = () => {
    setIsSecondModalOpen(true);
  };

  const resetTest = () => {
    setTests((prevTests) =>
      prevTests.map((test) => ({
        ...test,
        selectedAnswers: [],
      }))
    );
    setCorrectAnswers(0);
    setIsSubmit(false);
    setIsModalOpen(false);
  };

  return (
    <Fragment>
      <div
        className="w-100 min-vh-100 d-flex flex-column"
        style={{ paddingTop: "56px" }}
      >
        <div className="content-container p-4 bg-white rounded shadow-sm">
          <div className="d-flex align-items-center mb-4 mt-3">
            <div className="arrow-back me-3">
              <Link to={`/${endpoints.LESSON}/${lessonId}`}>
                <img src={chevron_left} width="30" alt="Back" />
              </Link>
            </div>
            <h2 className="tests-name fw-bold text-primary">
              {testsName || "TESTS"}
            </h2>
          </div>
          <ul className="list-unstyled">
            {tests.map((test) => (
              <li key={test.id} className="mb-4">
                <h3 className="fw-semibold">{test.question}</h3>
                <ul className="options-list list-unstyled">
                  {test.options.map((option) => (
                    <li
                      key={option}
                      className={`p-2 rounded shadow-sm d-flex align-items-center ${
                        getCorrectAnswers &&
                        test.correctAnswers.includes(option)
                          ? "bg-success text-white fw-bold"
                          : getCorrectAnswers &&
                            test.selectedAnswers.includes(option) &&
                            !test.correctAnswers.includes(option)
                          ? "bg-danger text-white fw-bold"
                          : "bg-light"
                      }`}
                    >
                      <input
                        type="checkbox"
                        id={`${test.id}-${option}`}
                        className="me-2"
                        name={`test-${test.id}`}
                        value={option}
                        checked={test.selectedAnswers.includes(option)}
                        onChange={() => handleOptionSelection(test.id, option)}
                      />
                      <label
                        htmlFor={`${test.id}-${option}`}
                        className="w-100"
                        onClick={(e) => {
                          e.preventDefault();
                          handleOptionSelection(test.id, option);
                        }}
                      >
                        {option}
                      </label>
                    </li>
                  ))}
                </ul>
              </li>
            ))}
          </ul>
          {!isSubmit && (
            <button className="btn btn-primary btn-sm" onClick={acceptAction}>
              Submit
            </button>
          )}
        </div>
      </div>
      {isModalOpen && (
        <div className="overlay">
          <div className="modal-window text-center p-4 rounded shadow">
            <img
              src={isPassed ? green_brain : red_brain}
              className="mb-3"
              alt="Result"
            />
            <p>
              You got {correctAnswers} out of {tests.length} correct!
            </p>
            <p className="mb-4">
              You{" "}
              <span
                className={
                  isPassed ? "text-success fw-bold" : "text-danger fw-bold"
                }
              >
                {isPassed ? "passed" : "failed"}
              </span>{" "}
              the test
            </p>
            <Link to={`/${endpoints.LESSON}/${lessonId}`}>
              <button className="btn btn-success btn-sm me-2">
                Back to the lesson
              </button>
            </Link>
            {isPassed ? (
              <button
                className="btn btn-outline-primary btn-sm"
                onClick={showTheCorrectAnswers}
              >
                Show correct answers
              </button>
            ) : (
              <button
                className="btn btn-outline-danger btn-sm"
                onClick={resetTest}
              >
                Try again
              </button>
            )}
          </div>
        </div>
      )}
      {isSecondModalOpen && (
        <div className="overlay">
          <div className="modal-window text-center p-4 rounded shadow">
            <img src={blue_brain} className="mb-2" alt="Confirmation" />
            <p>Are you sure?</p>
            <button
              className="btn btn-success btn-sm me-2"
              onClick={calculateResult}
            >
              Yes
            </button>
            <button
              className="btn btn-outline-secondary btn-sm"
              onClick={() => setIsSecondModalOpen(false)}
            >
              No
            </button>
          </div>
        </div>
      )}
    </Fragment>
  );
};
