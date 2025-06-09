import React from "react";
import { saveAs } from 'file-saver';
import { Table } from "react-bootstrap";

import pencil from "../../images/pencil.png";
import attention_yellow from "../../images/attention_yellow.png";
import attention_grey from "../../images/attention_grey.png";
import icon_no from "../../images/icon_no.png";
import icon_yes_yellow from "../../images/icons_yes_yellow.png";
import icon_yes from "../../images/icon_yes.png";

export const TeacherTaskBlock = ({ task, toggleDropdownTaskFromChild, readDescription, isOpenTask,
                                     setDoRequestCorrection, setDoRequestPassed, taskIncludesNotPassed }) => {

    const millisecondsToDate = (milliseconds) => {
        const date = new Date(milliseconds);
        return date.toLocaleString();
    }

    const handleDownloadZip = async (taskId, studentName, studentSurname, studentEmail, groupName) => {
        try {
            const sessionId = localStorage.getItem('sessionId');
            const response = await fetch(
                `http://localhost:8080/api/teacher/tasksSubmission/${taskId}/download-zp?studentName=
                ${studentName}&studentSurname=
                ${studentSurname}&studentEmail=
                ${studentEmail}&groupName=${groupName}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `${sessionId}`
                    }
                });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const arrayBuffer = await response.arrayBuffer();
            const blob = new Blob([arrayBuffer], { type: 'application/zip' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `${studentName}_${studentSurname}_${studentEmail}_${groupName}_Task${taskId}.zip`);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);

            const fileReader = new FileReader();
            fileReader.onload = function () {
                const arrayBuffer = this.result;
                const fileBlob = new Blob([arrayBuffer], { type: 'application/zip' });
                const file = new File([fileBlob], `${studentName}_${studentSurname}_${studentEmail}_${groupName}_Task${taskId}.zip`, { type: 'application/zip' });
                saveAs(file);
            };
            fileReader.readAsArrayBuffer(blob);
        } catch (error) {
            console.error('There has been a problem with our fetch operation:', error);
        }
    };

    return (
        <div key={task.taskId} className={!task.includesPassed && taskIncludesNotPassed ? "d-none" : "mb-3"}>
            <div onClick={() => toggleDropdownTaskFromChild(task.taskId)} className="d-flex cursor-pointer task-name-block">
                <img src={pencil} width="20px" height="20px" className="task-name-block-pencil" alt="pencil" />
                <h4 className="task-name-block-h4">Task: {task.taskAnswers[0].taskName}</h4>
                <img src={task.taskAnswers && task.taskAnswers.length && task.taskAnswers.filter(answer => answer.isCorrection).length ? attention_yellow : attention_grey} width="20px" height="20px" alt="attention"
                     className="task-name-block-attention" />
                <span className="task-name-block-correction">Need correction: </span>
                <span className="task-name-block-correction-num">{task.taskAnswers && task.taskAnswers.length ? task.taskAnswers.filter(answer => answer.isCorrection).length : 0}/{task.taskAnswers && task.taskAnswers.length ? task.taskAnswers.length : 0}</span>
                <span className="task-name-block-done" style={{ backgroundColor: `${task.taskAnswers && task.taskAnswers.length && task.taskAnswers.filter(answer => answer.isPassed).length ? "#66BB6A" : "#E8F5E9"}`,
                    color: `${task.taskAnswers && task.taskAnswers.length && task.taskAnswers.filter(answer => answer.isPassed).length ? "white" : "black"}`}}>Done:</span>
                <span className="task-name-block-done-num">{task.taskAnswers && task.taskAnswers.length ? task.taskAnswers.filter(answer => answer.isPassed).length : 0}/{task.taskAnswers && task.taskAnswers.length ? task.taskAnswers.length : 0}</span>
                <span className="task-name-read-desc" onClick={() => readDescription(task.taskAnswers[0].description)}>Read Description</span>
            </div>
            {
                isOpenTask && isOpenTask.length && isOpenTask.filter(obj => {
                    return (obj.taskId === task.taskId) && obj.isOpen;
                }).length ?
                    task.taskAnswers.length > 0 ? (
                        <div>
                            <Table bordered striped="columns" className="mt-2 text-center table-block">
                                <thead>
                                <tr>
                                    <th>Submitted Date</th>
                                    <th className="fixed-width-url">Answer URL</th>
                                    <th className="fixed-width-student">Student</th>
                                    <th className="fixed-width-course">Course</th>
                                    <th>Need Correction</th>
                                    <th>Is Passed</th>
                                    <th>Download ZIP repository</th>
                                </tr>
                                </thead>
                                <tbody>
                                {task.taskAnswers.sort((a, b) => a.submittedDate - b.submittedDate).map((answer, i) => (
                                    <tr key={i}>
                                        <td>{millisecondsToDate(answer.submittedDate)}</td>
                                        <td className="fixed-width-url"><a href={answer.answerUrl}
                                               onClick={() => window.open(answer.answerUrl, '_blank')}
                                               rel="noopener noreferrer">{answer.answerUrl}</a></td>
                                        <td className="fixed-width-student">{answer.student.name} {answer.student.surname}</td>
                                        <td className="fixed-width-course m-auto">{answer.course}</td>
                                        <td
                                            className="cursor-pointer"
                                            onClick={() => setDoRequestCorrection({
                                                correctionAnswer: answer.answerId,
                                                isOpen: answer.isCorrection
                                            })}>
                                            {!answer.isCorrection ? <img src={icon_no} width="35px" height="35px" alt="icon-no" /> : <img src={icon_yes_yellow} width="35px" height="35px" alt="icon-yes-yellow" />}
                                        </td>
                                        <td
                                            className="cursor-pointer"
                                            onClick={() => setDoRequestPassed({
                                                passedAnswer: answer.answerId,
                                                isOpen: answer.isPassed
                                            })}>
                                            {!answer.isPassed ? <img src={icon_no} width="35px" height="35px" alt="icon-no" /> : <img src={icon_yes} width="35px" alt="icon-yes" />}
                                        </td>
                                        <td>{answer.isPassed ? <button className="btn btn-primary btn-sm"
                                                                       onClick={() => handleDownloadZip(
                                                                           answer.answerId,
                                                                           answer.student.name,
                                                                           answer.student.surname,
                                                                           answer.student.email,
                                                                           answer.course
                                                                       )}>Download ZIP
                                        </button> : "Not available"}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </Table>
                        </div>
                    ) : null
                    : null
            }
        </div>
    )
}

