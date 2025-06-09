import React, { Fragment, useEffect, useState } from "react";
import { useGetLessons } from "../../hooks/course-lessons-request";
import { CourseLessonBlock } from "./CourseLessonBlock";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { Spinner } from "../spinner/Spinner";
import { useSelector, useDispatch } from "react-redux";
import { setCourse } from "../../redux/slices/courseSlice";
import Pagination from "../pagination/Pagination";
import { AlertModal } from "../alert-modal/AlertModal";

import endpoints from "../../shared/router/endpoints";
import { baseUrl } from "../../api/http-client";
import chevron_left from "../../images/chevron_left.svg";
import next_page from "../../images/next_page.svg";

import "./courseLessons.css";
export const CourseLessons = ({ courseId }) => {
  const currentUser = useSelector((state) => state.userReducer.user);
  const currentNameStorage = useSelector(
    (state) => state.courseReducer.course.name
  );
  const [courseName, setCourseName] = useState(currentNameStorage);
  const { data, isLoading, error } = useGetLessons(courseId);
  const [lessonsInCourse, setLessonsInCourse] = useState([]);
  const [isLoadingProgress, setIsLoadingProgress] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const navigate = useNavigate();
  const itemsPerPage = 4;
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentItems = data.slice(startIndex, endIndex);
  const totalPages = Math.ceil(data.length / itemsPerPage);

  const location = useLocation();
  const dispatch = useDispatch();
  useEffect(() => {
    if (location.state) {
      setCourseName(location.state.courseName || currentNameStorage);
      dispatch(
        setCourse({
          name: courseName,
          courseId: courseId,
          listOfLessons: data,
        })
      );
    }
  }, [location.state, currentNameStorage]);

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handlePageChangeForward = () => {
    setCurrentPage(currentPage + 1);
  };

  const handlePageChangeBack = () => {
    setCurrentPage(currentPage - 1);
  };

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

  useEffect(() => {
    if (currentUser.id) {
      if (data.length) {
        data.map((lesson) => {
          const getProgress = async () => {
            try {
              const { data: dataProgress } = await baseUrl.get(
                `/api/tasks/progress/${currentUser.id}/${lesson.id}`
              );
              if (
                !lessonsInCourse.length ||
                !lessonsInCourse.filter((lessonL) => lessonL.id === lesson.id)
                  .length
              ) {
                setLessonsInCourse((prev) => [...prev, dataProgress]);
              }

              if (data[data.length - 1].id === lesson.id) {
                setIsLoadingProgress(false);
              }
            } catch {
              openModal();
            }
          };
          getProgress();

          return lesson;
        });
      } else {
        setIsLoadingProgress(false);
      }
    } else {
      navigate(`/${endpoints.NOT_LOGGED_IN}`);
    }
  }, [data]);

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
    <>
  {isLoading || isLoadingProgress ? (
    <Spinner />
  ) : (
    <div className="w-100 min-vh-100 d-flex flex-column course-lessons-block">
      <div className="mt-4 gap-4 d-flex flex-row flex-wrap justify-content-start container-fluid">
        <div className="pt-3 ps-3">
          <Link to={`/${endpoints.COURSES}`}>
            <img
              src={chevron_left}
              className="course-lessons-block-main-img"
              alt="chevron_left"
            />
          </Link>
        </div>
        <h1 className="mt-2 fw-bold course-name-block">{courseName}</h1>
        <div className="p-3 row w-100">
          {!Array.isArray(data) || data.length === 0 ? (
            <div style={{ fontSize: "2rem" }}>There are no lessons yet</div>
          ) : (
            <div className="d-flex align-items-center">
              <div
                onClick={handlePageChangeBack}
                className={`cursor-pointer ${startIndex === 0 ? "d-none" : ""}`}
                style={{ marginRight: "10px" }}
              >
                <img
                  src={chevron_left}
                  style={{ width: "40px" }}
                  alt="chevron_left"
                />
              </div>
              <div className="row">
                {currentItems.map((lesson, index) => (
                  <CourseLessonBlock
                    key={lesson.id || index}
                    lesson={lesson}
                    index={index}
                    startIndex={startIndex}
                    lessonsInCourse={lessonsInCourse}
                    courseName={courseName}
                    courseId={courseId}
                    data={data}
                  />
                ))}
              </div>
              <div
                onClick={handlePageChangeForward}
                className={`cursor-pointer ${
                  endIndex === totalPages * itemsPerPage ? "d-none" : ""
                }`}
                style={{ marginLeft: "10px" }}
              >
                <img src={next_page} style={{ width: "40px" }} alt="next_page" />
              </div>
            </div>
          )}
        </div>
      </div>
      <div className="text-center">
        <Pagination
          totalPages={totalPages}
          currentPage={currentPage}
          onPageChange={handlePageChange}
        />
      </div>
    </div>
  )}

  {isModalOpen && (
    <AlertModal
      isOpen={isModalOpen}
      onClose={closeModal}
      title="Oops..."
      alertText="Failed to get a progress by lessonId"
      buttonText="Close"
    />
  )}
</>

  );
};
