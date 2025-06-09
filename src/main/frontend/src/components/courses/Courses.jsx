import React, { Fragment, useEffect, useState } from "react";
import { NotUser } from "../not-user/not-user";
import { CourseBlock } from "./CourseBlock";
import { useGetCourses } from "../../hooks/courses-request";
import { Link, useNavigate } from "react-router-dom";
import { Spinner } from "../spinner/Spinner";
import { useSelector } from "react-redux";
import { AlertModal } from "../alert-modal/AlertModal";

import endpoints from "../../shared/router/endpoints";
import colors from "../../shared/dto/colors";
import course_purple from "../../images/course_purple.svg";
import course_red from "../../images/course_red.svg";
import course_green from "../../images/course_green.svg";
import "./courses.css";
import { baseUrl } from "../../api/http-client";
import { setCourses } from "../../redux/slices/coursesSlice";
import { useDispatch } from "react-redux";
import green_brain from "../../images/green_brain.png";
import red_brain from "../../images/red_brain.png";

export const Courses = () => {
  const currentUser = useSelector((state) => state.userReducer.user);
  const currentUserId = useSelector((state) => state.userReducer.user.id);
  const { data, error } = useGetCourses(currentUser.id);
  const [tasksInCourse, setTaskInCourse] = useState([]);
  const [isLoadingProgress, setIsLoadingProgress] = useState(true);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  // ...
  const [isModalOpen, setIsModalOpen] = useState(false);

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);
  // ...

  const randomSvg = [course_purple, course_green, course_red];
  const randomColor = ["#E868BD", "#79BD8E", "#EA3737"];

  let reData = [];
  if (data.length) {
    reData = data.map((course) => {
      course.image_color = randomSvg[course.id % 3];
      course.text_color = randomColor[course.id % 3];
      return course;
    });
  }

  const getLessonsOfCourse = (id, courseName) => {
    navigate(`/${endpoints.COURSE}/${id}`, {
      state: { courseName: courseName },
    });
  };

  const getNavigateToCertificatePage = () => {
    navigate(`/${endpoints.CERTIFICATE}`);
  };

  useEffect(() => {
    if (!currentUser) {
      navigate(`/${endpoints.NOT_LOGGED_IN}`);
    }

    let courseNames = [];
    courseNames = reData.map((course) => {
      return course.name;
    });

    dispatch(
      setCourses({
        courses: courseNames,
      })
    );

    if (data.length) {
      data.map((course) => {
        const getProgress = async () => {
          const { data: dataProgress } = await baseUrl.get(
            `/api/tasks/${currentUserId}/${course.id}/progress`
          );

          if (
            !tasksInCourse.length ||
            !tasksInCourse.filter((task) => task.id === course.id).length
          ) {
            setTaskInCourse((prev) => [...prev, dataProgress]);
          }

          if (data[data.length - 1].id === course.id) {
            setIsLoadingProgress(false);
          }
        };
        getProgress();

        return course;
      });
    }
  }, [data]);

  if (!currentUser.id) {
    return <NotUser />;
  }

  if (error && !data) {
    return (
      <div className="d-flex flex-column align-items-center text-center p-3">
        <div className="mt-4 alert alert-danger" role="alert">
          {error.response.data[0].name}
        </div>
        <Link to={endpoints.HOME} className="mt-4 btn btn-primary btn-lg">
          Back to the Home page
        </Link>
      </div>
    );
  }

  return (
    <>
  {isLoadingProgress ? (
    <Spinner />
  ) : (
    <div
      className="w-100 min-vh-100 d-flex flex-column"
      style={{ backgroundColor: colors.WHITE, paddingTop: "56px" }}
    >
      <h1 className="mt-4 m-0 fs-2 fs-md-3 fs-sm-4 fw-bold text-center">
        Available courses:
      </h1>
      <div className="container-fluid mt-4">
        <div className="row g-4 justify-content-center">
          {Array.isArray(reData) && reData.length > 0 ? (
            reData.map((course) => (
              <div key={course.id} className="col-12 col-md-6 col-lg-4">
                <CourseBlock
                  course={course}
                  reData={reData}
                  tasksInCourse={tasksInCourse}
                  getLessonsOfCourse={() =>
                    getLessonsOfCourse(course.id, course.name)
                  }
                  getNavigateToCertificatePage={getNavigateToCertificatePage}
                />
              </div>
            ))
          ) : (
            <div className="text-center fs-4 mt-4">
              No courses available at the moment.
            </div>
          )}
        </div>
      </div>
    </div>
  )}

  {isModalOpen && (
    <AlertModal
      isOpen={isModalOpen}
      onClose={closeModal}
      title="Success!"
      alertText="You have successfully registered."
      buttonText="Close"
    />
  )}
</>

  );
};
