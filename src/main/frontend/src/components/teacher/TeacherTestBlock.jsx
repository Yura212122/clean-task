import React from "react";
import icon_test from "../../images/icon_test.png";
import icon_no_grey from "../../images/icon_no_grey.png";
import icon_no_red from "../../images/icon_no_red.png";
import {Table} from "react-bootstrap";
import icon_yes from "../../images/icon_yes.png";

export const TeacherTestBlock = ({ test, toggleDropdownTestFromChild, isOpenTest, testIncludesNotPassed }) => {

    const millisecondsToDate = (milliseconds) => {
        const date = new Date(milliseconds);
        const formattedDate = date.toLocaleString();
        return formattedDate;
    }

    const hasTestAnswers = test.testAnswers && test.testAnswers.length;

    return (
        <div key={test.testId} className={!test.includesPassed && testIncludesNotPassed ? "d-none" : "mb-3"}>
            <div onClick={() => toggleDropdownTestFromChild(test.testId)} className="d-flex cursor-pointer task-name-block">
                <img className="test-name-block-icon" src={icon_test} width="25px" height="25px" alt="icon-test" />
                <h4 className="test-name-block-h4">Test {test.testId}</h4>
                <span className="task-name-block-done" style={{ backgroundColor: `${hasTestAnswers && test.testAnswers.filter(answer => answer.isPassed).length ? "#66BB6A" : "#E8F5E9"}`,
                    color: `${hasTestAnswers && test.testAnswers.filter(answer => answer.isPassed).length ? "white" : "black"}`}}>Done:</span>
                <span className="task-name-block-done-num">{hasTestAnswers ? test.testAnswers.filter(answer => answer.isPassed).length : 0}/{hasTestAnswers ? test.testAnswers.length : 0}</span>
                <img src={(hasTestAnswers && test.testAnswers.filter(answer => answer.isPassed).length) || !hasTestAnswers ? icon_no_grey : icon_no_red} width="20px" height="20px" alt="attention"
                     className="task-name-block-attention"/>
                <span className="task-name-block-correction">Failed: </span>
                <span className="task-name-block-correction-num">{hasTestAnswers ? test.testAnswers.filter(answer => !answer.isPassed).length : 0}/{hasTestAnswers ? test.testAnswers.length : 0}</span>
            </div>
            {
                isOpenTest && isOpenTest.length && isOpenTest.some(obj => obj.testId === test.testId && obj.isOpen) ? (
                    hasTestAnswers ? (
                        <Table bordered striped="columns" className="text-center">
                            <thead>
                            <tr>
                                <th>Submitted Date</th>
                                <th className="fixed-width-student">Student</th>
                                <th className="fixed-width-course">Course</th>
                                <th>Is Passed</th>
                                <th>Attempt</th>
                                <th>Total Score</th>
                            </tr>
                            </thead>
                            <tbody>
                            {test.testAnswers.sort((a, b) => a.submittedDate - b.submittedDate).map((answer, i) => (
                                <tr key={i}>
                                    <td>{millisecondsToDate(answer.submittedDate)}</td>
                                    <td className="fixed-width-student">{answer.student.name} {answer.student.surname}</td>
                                    <td className="fixed-width-course">{answer.course}</td>
                                    <td>
                                        {!answer.isPassed ? <img src={icon_no_red} width="35px" alt="icon-yes" /> : <img src={icon_yes} width="35px" alt="icon-yes" />}
                                    </td>
                                    <td>{answer.attempt}</td>
                                    <td>{parseInt(answer.totalScore)}%</td>
                                </tr>
                            ))}
                            </tbody>
                        </Table>
                    ) : ''
                ) : ''
            }
        </div>
    )
}

