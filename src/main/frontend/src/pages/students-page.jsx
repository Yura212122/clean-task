import { Students } from "../components/students/Students";
import React from "react";
import {useSelector} from "react-redux";
import {NotUser} from "../components/not-user/not-user";

export const StudentsPage = () => {
    const currentUser = useSelector((state) => state.userReducer.user);

    if (!currentUser.id) {
        return <NotUser />
    }

    return <Students />
}