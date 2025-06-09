import { Employees } from "../components/employees/Employees";
import React from "react";
import {useSelector} from "react-redux";
import {NotUser} from "../components/not-user/not-user";

export const EmployeesPage = () => {
    const currentUser = useSelector((state) => state.userReducer.user);

    if (!currentUser.id) {
        return <NotUser />
    }

    return <Employees />
}