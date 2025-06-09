import {Task} from "../components/task/Task";
import {useParams} from "react-router-dom";
import React from "react";
import {useSelector} from "react-redux";
import {NotUser} from "../components/not-user/not-user";

export const TaskPage = () => {
    const { taskId } = useParams();
    const currentUser = useSelector((state) => state.userReducer.user);

    if (!currentUser.id) {
        return <NotUser />
    }

    return <Task taskId={taskId} />;
}