import colors from "../../shared/dto/colors";
import schedule from "../../images/schedule.svg";
import {Link} from "react-router-dom";
import endpoints from "../../shared/router/endpoints";
import clock_transparent from "../../images/clock_transparent.svg";
import React from "react";

export const CourseBlock = ({ course, reData, tasksInCourse, getLessonsOfCourse, getNavigateToCertificatePage  }) =>{
    return <div key={course.id} className="card d-flex flex-row justify-content-between course-block"
                style={{ backgroundColor: colors.GREEN_50 }}>
        <div className='w-100 p-4 gap-3 position-relative'>
            <figure className='m-0 d-flex position-relative' style={{ height: '136px' }}>
                <figcaption
                    className='position-absolute start-50 translate-middle fs-2 course-block-figcaption'
                    style={{ color: course.text_color }}>
                    {course.name}
                </figcaption>
                <img src={course.image_color} alt={course.name} loading="lazy" className="stretched-image"/>
            </figure>

            <div className='mt-3 p-0 card-body'>
                {
                    course.name !== "ProgAcademy" ? <div className="d-flex justify-content-between align-items-start" style={{ minHeight: "80px" }}>
                        <h2 className='fs-4 fw-bold card-title flex-grow-1 course-name' style={{ flex: "1" }} >{course.name}</h2>
                        <div className="d-flex justify-content-end align-items-center" style={{ flex: "1" }}>
                            <img src={schedule} width="16px" className="img-hidden" alt="schedule" />
                            <p className='fs-6 card-text' style={{ marginLeft: "7px" }}>Start: 01.01.2024</p>
                        </div>
                    </div> : <div className="text-center">
                        <h2 className='fs-4 fw-bold card-title flex-grow-1 course-name' style={{ flex: "1" }} >{course.name}</h2>
                    </div>

                }

                {course.name === "ProgAcademy" ?
                    <div>
                        <div className={`text-center margin-text ${reData.length === 1 ? "margin-text-only-admin" : ""}`}>Group for employees</div>
                        <div>
                            <Link
                                to={`/${endpoints.EMPLOYEES}`}
                                className={`w-100 btn btn-sm course-block-link ${reData.length > 1 ? "margin-btn" : ""}`}
                                style={{
                                    color: colors.GREEN_50,
                                    backgroundColor: colors.BLUE_700,
                                    borderColor: colors.BLUE_700,
                                }}
                                state={{courseName: course.name}}>
                                Open
                            </Link>
                        </div>
                    </div>
                    :
                    <div>
                        <div className="mb-5 d-flex justify-content-between align-items-center">
                            <div className="d-flex justify-content-between align-items-center">
                                <img src={clock_transparent} className="img-hidden" alt="clock_transparent" />
                                <p className='fs-6 card-text' style={{ marginLeft: "7px" }}>Progress: {tasksInCourse.length && tasksInCourse.find(task => task.id === course.id) && tasksInCourse.find(task => task.id === course.id).progress ? tasksInCourse.find(task => task.id === course.id).progress : 0}%</p>

                            </div>
                            <progress style={{height: "16px" }}
                                      value={tasksInCourse.length && tasksInCourse.find(task => task.id === course.id) && tasksInCourse.find(task => task.id === course.id).progress ? tasksInCourse.find(task => task.id === course.id).progress : 0}
                                      max="100"></progress>
                        </div>
                        <div className="d-flex justify-content-between position-absolute" style={{ bottom: "20px" }}>
                            <div>
                                <button onClick={() => getLessonsOfCourse(course.id, course.name)}
                                        className="btn btn-sm course-block-btn"
                                        style={{
                                            color: colors.GREEN_50,
                                            backgroundColor: colors.BLUE_700,
                                            borderColor: colors.BLUE_700,
                                        }}
                                >Open</button>
                            </div>
                            <div>
                                {
                                    tasksInCourse.length && tasksInCourse.find(task => task.id === course.id) && tasksInCourse.find(task => task.id === course.id).progress === 100 ?
                                        <button onClick={getNavigateToCertificatePage}
                                                className="btn btn-sm course-block-btn-full"
                                                style={{
                                                    color: colors.GREEN_50,
                                                    backgroundColor: colors.BLUE_700,
                                                    borderColor: colors.BLUE_700,
                                                }}>Create certificate</button> :
                                        <button className="btn btn-sm course-block-btn-dashed"
                                                style={{
                                                    border: `1px dashed ${colors.BLUE_700}`,
                                                    borderColor: colors.BLUE_700
                                                }}>Create certificate</button>

                                }
                            </div>
                        </div>
                    </div>
                }
            </div>
        </div>
    </div>
}