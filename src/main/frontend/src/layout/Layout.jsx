import React, { Fragment } from "react";
import { Outlet } from "react-router-dom";
import { useLocation } from 'react-router-dom';
import { Header } from '../components/header/Header';
import endpoints from "../shared/router/endpoints";

import "./layout.css";

export const Layout = () => {
    const location = useLocation();
    const isHeader = !(location.pathname === `/${endpoints.REGISTRATION}` || location.pathname === `/${endpoints.INVITE}` || location.pathname === `/${endpoints.LOGIN}` || location.pathname === `/${endpoints.NOT_FOUND}` || location.pathname === `/${endpoints.NOT_LOGGED_IN}`);
    const isBackgroundRotate = location.pathname === `/${endpoints.REGISTRATION}` || location.pathname === `/${endpoints.LOGIN}`;
    return (
        <Fragment >
            {isHeader ?
                <Header />
                : null}
            <div className={`w-100 d-flex flex-grow-1 background-to-sm ${isBackgroundRotate ? " background-from-lg-rotate background-from-sm-to-lg" : "background-from-lg background-from-sm-to-lg"}`}>
                <main className="mx-auto min-vh-100 d-flex container-fluid pt-5" style={{ width: "1324px" }} >
                    <Outlet />
                </main>
            </div>
        </Fragment >
    );
};
