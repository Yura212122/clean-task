import React, {useContext, useEffect, useState} from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Link } from "react-router-dom";
import { ReportRequest } from '../../hooks/report-request-provider';
import { useSelector, useDispatch } from "react-redux";
import { Avatar } from '../avatar/Avatar';
import progacademy from "../../images/prog-academy.svg";
import {setCourseId, setLessonId} from "../../redux/slices/headerSlice";
import {baseUrl} from "../../api/http-client";

import endpoints from '../../shared/router/endpoints';
import colors from "../../shared/dto/colors";

import "./header.css";

export const Header = () => {
    const [isMainListMenuOpen, setMainListMenuOpen] = useState(false);
    const [isAvatarListMenuOpen, setAvatarListMenuOpen] = useState(false);
    const [isNotificationListMenuOpen, setNotificationListMenuOpen] = useState(false);
    const currentUser = useSelector((state) => state.userReducer.user);
    const currentCourseId = useSelector(state => state.headerReducer.header.courseId);
    const currentLessonId = useSelector(state => state.headerReducer.header.lessonId);
    const currentNumberOfLesson = useSelector(state => state.headerReducer.header.numberOfLesson);
    const currentNumberOfLessonCourse = useSelector(state => state.courseReducer.course.numberOfLesson);
    const currentUserRole = useSelector((state) => state.userReducer.user.role);
    const currentTaskAnswers = useSelector((state) => state.taskAnswersReducer.taskAnswers);

    const [isLoginButtonShow, setIsLoginButtonShow] = useState(() => {
        return JSON.parse(sessionStorage.getItem('isLoginButtonShow')) || false;
    });
    const [isConnected, setIsConnected] = useState(() => {
        return JSON.parse(sessionStorage.getItem('isConnected')) || false;
    });
    const { useClickLogout, telegramRef, isOnClickOnButtonChatBot } = useContext(ReportRequest);
    const location = useLocation();
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const mainListMenu = () => document.querySelector("#main-list-menu");

    const onHandleClick = (event) => {
        if (!isMainListMenuOpen) {
            setMainListMenuOpen(true);
            setAvatarListMenuOpen(false);
            setNotificationListMenuOpen(false);
        } else {
            setMainListMenuOpen(false);
        }
        event.stopPropagation();
        const target = event.target;

        if (mainListMenu() && (target.id === "main-menu-button" || target.id === "main-menu-span")) {

            if (mainListMenu().classList.contains("d-block")) {
                mainListMenu().classList.remove("d-block");
                mainListMenu().classList.add("visually-hidden");
            } else {
                mainListMenu().classList.remove("visually-hidden");
                mainListMenu().classList.add("d-block");
            }
        }
    }

    useEffect(() => {
        const handleGlobalClick = (event) => {
            setMainListMenuOpen(false);
            event.stopPropagation();
            const target = event.target;
            if (mainListMenu() && (target.id !== "main-menu-button" && target.id !== "main-menu-span")) {
                mainListMenu().classList.remove("d-block");
                mainListMenu().classList.add("visually-hidden");
            }
        };

        document.addEventListener("click", handleGlobalClick);

        return () => {
            document.removeEventListener("click", handleGlobalClick);
        };

    }, []);

    useEffect(() => {
        const handleLiClick = (event) => {
            const target = event.target;
            if (mainListMenu() && target.tagName === "LI"  || target.tagName === "A") {
                mainListMenu().classList.remove("d-block");
                mainListMenu().classList.add("visually-hidden");
            }
        };

        const mainListMenuElement = mainListMenu();
        if (mainListMenuElement) {
            mainListMenuElement.addEventListener("click", handleLiClick);
        }

        return () => {
            if (mainListMenuElement) {
                mainListMenuElement.removeEventListener("click", handleLiClick);
            }
        };

    }, [isMainListMenuOpen]);

    useEffect(() => {
        if (isOnClickOnButtonChatBot && !isConnected) {
            const eventSource = new EventSource('http://localhost:8080/sse');
            eventSource.onmessage = function(event) {
                const message = event.data;
                if (message === "The user clicks Start") {
                    setIsLoginButtonShow(true);
                    sessionStorage.setItem('isLoginButtonShow', JSON.stringify(true));
                    sessionStorage.setItem('isOnClickOnButtonChatBot', JSON.stringify(true));
                    sessionStorage.setItem('isConnected', JSON.stringify(true));
                }
            };

            eventSource.onerror = function() {
                eventSource.close();
                setIsConnected(false);
            };

            setIsConnected(true);
            sessionStorage.setItem('isOnClickOnButtonChatBot', JSON.stringify(true));
        }
    }, [isOnClickOnButtonChatBot, isConnected, isLoginButtonShow]);

    useEffect(() => {

        if (location.pathname.includes("lesson")) {
            dispatch(setLessonId(location.pathname.substring(8)));
        } else if (location.pathname.includes("course")) {
            dispatch(setCourseId(location.pathname.substring(8)));
        }
        if (currentTaskAnswers.length && location.pathname.includes("task")) {
            const taskId = location.pathname.split("/").pop();
            currentTaskAnswers.map(task => {
                if (task.taskId === parseInt(taskId)) {
                    const getCourseIdAndLessonId = async() => {
                        const { data } = await baseUrl.get(`/api/tasks/${currentUser.id}/${taskId}`);
                        if (data) {
                            dispatch(setLessonId(data.lessonId));
                            dispatch(setCourseId(data.courseId));
                        }
                    }
                    getCourseIdAndLessonId();
                }
            })

        }
    }, [location.pathname, currentTaskAnswers, currentUser.id, dispatch])

    useEffect(() => {
        if (mainListMenu()) {
            mainListMenu().classList.remove("d-block");
            mainListMenu().classList.add("visually-hidden");
        }
        const header = () => document.querySelector("#header");
        let prevScrollPosition = window.pageXOffset;

        window.onscroll = function () {
            scroll();
        };

        function scroll() {
            let currentScrollPosition = window.pageYOffset;
            if (prevScrollPosition > currentScrollPosition) {
                header() && header().classList.remove("top-negative");
                header() && header().classList.add("header-shadow");
                header() && header().classList.add("top-0");
            } else if (currentScrollPosition > 0) {
                header() && header().classList.remove("top-0");
                header() && header().classList.remove("header-shadow");
                header() && header().classList.add("top-negative");
            }
            prevScrollPosition = currentScrollPosition;
        };

    }, []);

    const handleClickLogin = async () => {
        navigate(`/${endpoints.LOGIN}`);
    }

    return (
       <header
  id="header"
  className="w-100 position-fixed top-0 z-3 navbar navbar-expand-lg header-shadow transition"
  style={{ background: colors.WHITE, padding: "1px 0px" }}
>
  <section
    className="container-fluid d-flex justify-content-between align-items-center"
    style={{ width: "1324px" }}
  >
    <Link to={`/${endpoints.COURSES}`} className="header-link">
      <img
        src={progacademy}
        alt="Prog Academy"
        className="img-fluid"
        width={144}
        height={12}
      />
    </Link>

    {location.pathname !== `/${endpoints.SUCCESS}` && (
      <div className="d-flex main-block">
        <div id="main-menu-block" onClick={onHandleClick} className="position-relative">
          {currentUser.id && (
            <button
              id="main-menu-button"
              className="navbar-toggler"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#navbarNav"
              aria-controls="navbarNav"
              aria-expanded="false"
              aria-label="Toggle navigation"
            >
              <span id="main-menu-span" className="navbar-toggler-icon"></span>
            </button>
          )}

          <ul
            id="main-list-menu"
            className={`${
              isMainListMenuOpen ? "d-block" : "visually-hidden"
            } p-0 position-absolute mt-2 top-100 border border-0 shadow-sm header-ul`}
            style={{ background: colors.GRAY_100 }}
          >
            {currentUser.id && location.pathname !== `/${endpoints.COURSES}` && (
              <li className="dropdown-item li-item-menu header-li">
                <Link to={`/${endpoints.COURSES}`} className="nav-link">Courses</Link>
              </li>
            )}

            {location.pathname !== endpoints.HOME &&
              !location.pathname.includes(endpoints.COURSE) &&
              !location.pathname.includes(endpoints.LESSON) &&
              ![
                `/${endpoints.NOT_LOGGED_IN}`,
                `/${endpoints.TEACHER}`,
                `/${endpoints.CERTIFICATE}`,
                `/${endpoints.EMPLOYEES}`,
                `/${endpoints.STUDENTS}`,
                "/your_account_is_blocked"
              ].includes(location.pathname) && (
                <>
                  <li className="dropdown-item li-item-menu header-li">
                    <Link
                      to={`/${endpoints.COURSE}/${currentCourseId}`}
                      className="nav-link"
                    >
                      Lessons
                    </Link>
                  </li>
                  <li className="dropdown-item li-item-menu header-li">
                    <Link
                      to={`/${endpoints.LESSON}/${currentLessonId}`}
                      className="nav-link"
                    >
                      Lesson {currentNumberOfLessonCourse}
                    </Link>
                  </li>
                </>
            )}

            {(currentUserRole === "TEACHER" || currentUserRole === "MENTOR") && (
              <li className="dropdown-item li-item-menu header-li">
                <Link to={`/${endpoints.TEACHER}`} className="nav-link">Homeworks</Link>
              </li>
            )}

            {(currentUserRole === "ADMIN" || currentUserRole === "MANAGER") && (
              <>
                {location.pathname !== `/${endpoints.STUDENTS}` && (
                  <li className="dropdown-item li-item-menu header-li">
                    <Link to={`/${endpoints.STUDENTS}`} className="nav-link">Students</Link>
                  </li>
                )}
                {location.pathname !== `/${endpoints.EMPLOYEES}` && (
                  <li className="dropdown-item li-item-menu header-li">
                    <Link to={`/${endpoints.EMPLOYEES}`} className="nav-link">Employees</Link>
                  </li>
                )}
                {location.pathname !== `/${endpoints.ADMIN_BROADCAST}` && (
                  <li className="dropdown-item li-item-menu header-li">
                    <Link to={`/${endpoints.ADMIN_BROADCAST}`} className="nav-link">Admin</Link>
                  </li>
                )}
              </>
            )}
          </ul>
        </div>

        <nav className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav">
            {currentUser.id && location.pathname !== `/${endpoints.COURSES}` && (
              <li className="nav-item">
                <Link to={`/${endpoints.COURSES}`} className="nav-link">Courses</Link>
              </li>
            )}

            {location.pathname !== endpoints.HOME &&
              !location.pathname.includes(endpoints.COURSE) &&
              !location.pathname.includes(endpoints.LESSON) &&
              ![
                `/${endpoints.NOT_LOGGED_IN}`,
                `/${endpoints.TEACHER}`,
                `/${endpoints.CERTIFICATE}`,
                `/${endpoints.EMPLOYEES}`,
                `/${endpoints.STUDENTS}`,
                `/${endpoints.ADMIN_BROADCAST}`,
                "/your_account_is_blocked"
              ].includes(location.pathname) && (
                <>
                  <li className="nav-item">
                    <Link
                      to={`/${endpoints.COURSE}/${currentCourseId}`}
                      className="nav-link"
                    >
                      Lessons
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link
                      to={`/${endpoints.LESSON}/${currentLessonId}`}
                      className="nav-link"
                    >
                      Lesson {currentNumberOfLessonCourse}
                    </Link>
                  </li>
                </>
            )}

            {(currentUserRole === "TEACHER" || currentUserRole === "MENTOR") &&
              location.pathname !== `/${endpoints.TEACHER}` && (
                <li className="nav-item">
                  <Link to={`/${endpoints.TEACHER}`} className="nav-link">Homeworks</Link>
                </li>
            )}

            {(currentUserRole === "ADMIN" || currentUserRole === "MANAGER") && (
              <>
                {location.pathname !== `/${endpoints.STUDENTS}` && (
                  <li className="nav-item">
                    <Link to={`/${endpoints.STUDENTS}`} className="nav-link">Students</Link>
                  </li>
                )}
                {location.pathname !== `/${endpoints.EMPLOYEES}` && (
                  <li className="nav-item">
                    <Link to={`/${endpoints.EMPLOYEES}`} className="nav-link">Employees</Link>
                  </li>
                )}
                {location.pathname !== `/${endpoints.ADMIN_BROADCAST}` && (
                  <li className="nav-item">
                    <Link to={`/${endpoints.ADMIN_BROADCAST}`} className="nav-link">Admin</Link>
                  </li>
                )}
              </>
            )}
          </ul>
        </nav>
      </div>
    )}

    <div
      className="d-flex justify-content-end align-items-center header-login-block"
      style={{ width: "184px" }}
    >
      {!currentUser.id && isLoginButtonShow && location.pathname !== endpoints.HOME ? (
        <div className="d-flex justify-content-center">
          <button
            type="button"
            className="btn btn-sm header-login-btn"
            onClick={handleClickLogin}
            style={{
              color: colors.WHITE,
              backgroundColor: colors.BLUE_900,
              borderColor: colors.BLUE_900
            }}
          >
            Login
          </button>
        </div>
      ) : null}

      {currentUser.id && (
        <Avatar
          useClickLogout={useClickLogout}
          currentUser={currentUser}
          telegramRef={telegramRef}
          setMainListMenuOpen={setMainListMenuOpen}
          isAvatarListMenuOpen={isAvatarListMenuOpen}
          setAvatarListMenuOpen={setAvatarListMenuOpen}
          isNotificationListMenuOpen={isNotificationListMenuOpen}
          setNotificationListMenuOpen={setNotificationListMenuOpen}
        />
      )}
    </div>
  </section>
</header>

    )
};