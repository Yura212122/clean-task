import { CourseLessons } from "../components/course-lessons/CourseLessons"
import {Link, useParams} from "react-router-dom";
import {useSelector} from "react-redux";
import React from "react";
import {NotUser} from "../components/not-user/not-user";

export const CourseLessonsPage = () => {
    const { courseId } = useParams();
    const currentUser = useSelector((state) => state.userReducer.user);

    if (!currentUser.id) {
        return <NotUser />
    }

    return <CourseLessons courseId={courseId} />
}