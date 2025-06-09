import React from "react";
import {Link} from "react-router-dom";
import endpoints from "../../shared/router/endpoints";
import colors from "../../shared/dto/colors";
import success_icon from "../../images/success_icon.svg";
import not_started_icon from "../../images/not_started_icon.svg";

export const TestBlock = ({ test, testsStatus, lessonId }) => {
    return (
        <div key={test.id} className="tasks-block card d-flex flex-row justify-content-between m-4 p-3">
            <div className="d-flex flex-column position-relative" style={{ width: "80%" }}>
                <h3 className="fw-bold">Test {test.id}:</h3>
                <h4 className="mt-1 mb-5">{test.name ? test.name : "Pass the test with at least 80% result"}</h4>
                {
                    testsStatus.length && testsStatus.find(testStatus => testStatus.testId === test.id && testStatus.passed) ?
                        <span className="fw-bold" style={{ color: "green" }}>Test passed</span> : <Link to={`/${endpoints.TEST}/${test.id}`}
                                                                                                        state={{ lessonId: lessonId }}>
                            <button className="btn btn-sm position-absolute task-block-btn"
                                    style={{ color: colors.GREEN_50, backgroundColor: colors.BLUE_900, borderColor: colors.BLUE_900 }}>
                                Open
                            </button></Link>
                }
            </div>
            <div className="d-flex flex-column">
                <div>
                    <div className="text-center">
                        <p className="p-status">Status:</p>
                        <div>
                            {
                                testsStatus.length && testsStatus.find(testStatus => testStatus.testId === test.id && testStatus.passed) ?
                                    (
                                        <div><img src={success_icon} alt="success_icon" /></div>
                                    )  : (
                                        <div><img src={not_started_icon} alt="not_started_icon" /></div>
                                    )
                            }
                        </div>
                    </div>
                </div>
                <div className="text-center">
                    {
                        testsStatus.length && testsStatus.find(testStatus => testStatus.testId === test.id && !testStatus.passed) ?
                            (
                                <Link to={`/${endpoints.TEST}/${test.id}`}
                                      className="text-decoration-none"
                                      state={{ lessonId: lessonId }}><div className="fw-bold div-try-again">
                                    <span className="span-try-again">Try again</span><br />
                                    attempt: {testsStatus.length && testsStatus.find(testStatus => testStatus.testId === test.id).attempt + 1}</div></Link>
                            )  : ''
                    }

                </div>
            </div>
        </div>
    )
}