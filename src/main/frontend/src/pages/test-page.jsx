import {Test} from "../components/test/Test";
import {Link, useParams} from "react-router-dom";
import React from "react";
import {useSelector} from "react-redux";
import {NotUser} from "../components/not-user/not-user";

export const TestPage = () => {
    const currentUser = useSelector((state) => state.userReducer.user);
    const { testId } = useParams();

    if (!currentUser.id) {
        return <NotUser />
    }

    return <Test testId={testId} />;
}