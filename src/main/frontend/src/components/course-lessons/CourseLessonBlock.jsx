import clock_transparent from "../../images/clock_transparent.svg";
import colors from "../../shared/dto/colors";
import {Link} from "react-router-dom";
import endpoints from "../../shared/router/endpoints";
import React from "react";

import "./courseLessons.css";

export const CourseLessonBlock = ({ lesson, index, startIndex, lessonsInCourse, courseName, courseId, data }) => {

    let lessonNameArr = lesson.name.split(' ');
    let newLessonNameArr = lessonNameArr.map(name => {
        if (name.length > 16) {
            let firstPart = name.slice(0, 16);
            let secondPart = name.slice(16, name.length);
            return firstPart + " " + secondPart;
        }
        return name;
    })

    let newName = newLessonNameArr.join(' ');

    return (
        <div key={lesson.id}
             className='card d-flex flex-row justify-content-between m-3 course-lesson-block-main'>
            <div className='p-4 card-body position-relative'>
                <h2 className='fs-3 fw-bold card-title'>Lesson {startIndex + index + 1}</h2>
                <h4 className='fs-4 card-title course-lesson-block-h4'>{newName}</h4>
                <div className="d-flex justify-content-between position-absolute course-lesson-block-part">
                    <div className="d-flex align-items-start course-lesson-block-clock">
                        <img src={clock_transparent} width="12px" className="course-lesson-block-img" alt="transparent clock" />
                        <p className='m-0 fs-6 card-text'>Due time:<br />
                            01.07.2024</p>
                    </div>


                    <div>
                        <p className='fs-6 card-text course-lesson-block-card'>Status:<br /><span
                            className="fw-bold"
                            style={lessonsInCourse.length && lessonsInCourse.find(lessonL => lessonL.id === lesson.id) && lessonsInCourse.find(lessonL => lessonL.id === lesson.id).progress === "All passed" ?
                                {color: colors.BLUE_500} :
                                lessonsInCourse.length && lessonsInCourse.find(lessonL => lessonL.id === lesson.id) && lessonsInCourse.find(lessonL => lessonL.id === lesson.id).progress === "Need correction" ?
                                    {color: "#F44336"} :
                                    lessonsInCourse.length && lessonsInCourse.find(lessonL => lessonL.id === lesson.id) && lessonsInCourse.find(lessonL => lessonL.id === lesson.id).progress === "In progress" ?
                                        {color: "#008000"} : {color: "black"}}>
                                                    {lessonsInCourse.length && lessonsInCourse.find(lessonL => lessonL.id === lesson.id) && lessonsInCourse.find(lessonL => lessonL.id === lesson.id).progress ? lessonsInCourse.find(lessonL => lessonL.id === lesson.id).progress : ''}
                                                </span></p>

                    </div>
                </div>
                <Link className="btn btn-sm position-absolute course-lesson-block-button"

                      style={{
                          color: colors.GREEN_50,
                          backgroundColor: colors.BLUE_500,
                          borderColor: colors.BLUE_500,
                      }}
                      to={`/${endpoints.LESSON}/${lesson.id}`}
                      state={{
                          courseName: courseName,
                          courseId: courseId,
                          numberOfLesson: startIndex + index + 1,
                          lessonsInCourse: data,
                          lessonsProgress: lessonsInCourse
                      }}>
                    <div className="course-lesson-block-button-text">Open</div>

                </Link>
            </div>
        </div>
    )
}