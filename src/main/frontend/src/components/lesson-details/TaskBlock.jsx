import React from "react";
import icon_document from "../../images/icon-document.svg";
import colors from "../../shared/dto/colors";
import check_icon from "../../images/check_icon.svg";
import success_icon from "../../images/success_icon.svg";
import icon_attention from "../../images/icon-attention.png";
import not_started_icon from "../../images/not_started_icon.svg";

export const TaskBlock = ({ task, tasksStatus, isPassedTask, i }) => {
    return (
        <div key={task.id} className="tasks-block card d-flex flex-row justify-content-between m-4 p-3">
            <div className="d-flex flex-column position-relative" style={{ width: "80%" }}>
                <div className="d-flex justify-content-start">
                    <img src={icon_document} width="40px" style={{ marginLeft: "-7px" }} alt="Document icon by Icons8 https://icons8.com/icon/jIuty9dtdDem/document" />
                    <h3 className="fw-bold mt-2">Task {i + 1}:</h3>
                </div>
                <h4 className="mb-5">{task.name}</h4>
                {
                    tasksStatus.length && tasksStatus.find(taskStatus => (taskStatus.taskId === task.id && taskStatus.status === "submitted") || (taskStatus.taskId === task.id && taskStatus.status === "passed")) ?
                        '' : <button className="btn btn-sm position-absolute task-block-btn"
                                     style={{ color: colors.GREEN_50, backgroundColor: colors.BLUE_900, borderColor: colors.BLUE_900 }}
                                     onClick={() => isPassedTask(task.id, task.name)}>
                            Open</button>
                }
            </div>
            <div className="text-center">
                <p className="p-status">Status:</p>
                <div>
                    {
                        tasksStatus.length && tasksStatus.find(taskStatus => taskStatus.taskId === task.id && taskStatus.status === "submitted") ?
                            (
                                <div><img src={check_icon} alt="check_icon" /></div>
                            ) : tasksStatus.length && tasksStatus.find(taskStatus => taskStatus.taskId === task.id && taskStatus.status === "passed") ?
                                (
                                    <div><img src={success_icon} alt="success_icon" /></div>
                                ) : tasksStatus.length && tasksStatus.find(taskStatus => taskStatus.taskId === task.id && taskStatus.status === "need correction") ?
                                    (
                                        <div><img src={icon_attention} alt="icon_attention" /></div>
                                    ) : <div><img src={not_started_icon} alt="not_started_icon" /></div>
                    }
                </div>
            </div>
        </div>
    )
}