import {Teacher} from "../components/teacher/Teacher";
import {useSelector} from "react-redux";
import React from "react";
import {NotUser} from "../components/not-user/not-user";

export const TeacherPage = () => {
    const currentUser = useSelector((state) => state.userReducer.user);

    if (!currentUser.id) {
        return <NotUser />
    }

    return <Teacher />;
}