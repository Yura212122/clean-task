import  LessonDetails from "../components/lesson-details/LessonDetails";
import {useParams} from "react-router-dom";
import React from "react";
import {useSelector} from "react-redux";
import {NotUser} from "../components/not-user/not-user";

export const LessonDetailsPage = () => {
    const { lessonId } = useParams();
    const currentUser = useSelector((state) => state.userReducer.user);

    if (!currentUser.id) {
        return <NotUser />
    }

    return <LessonDetails  lessonId={lessonId} key={lessonId} />
}