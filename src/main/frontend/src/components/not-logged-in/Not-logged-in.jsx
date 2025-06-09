import { Link } from "react-router-dom";
import endpoints from "../../shared/router/endpoints";
import React from "react";
import not_logged_in from "../../images/not_logged_in.svg";
import arrow_left_home from "../../images/arrow_left_home.svg";

import "./not-logged-in.css";

export const NotLoggedIn = () => {
  return (
    <div className="d-flex flex-column align-items-center text-center py-5 w-100">
      <p className="text-border p-not-logged-in fs-4">YOU'RE NOT LOGGED IN</p>
      <img
        src={not_logged_in}
        alt="Not Logged In"
        className="img-fluid w-100 w-md-75"
        width={350}
      />
      <Link to={`${endpoints.HOME}`}>
        <button className="btn btn-sm btn-lg btn-home mt-4 btn-not-logged-in d-flex align-items-center">
          <img src={arrow_left_home} className="img-btn-not-logged-in me-2" />
          GO TO HOME
        </button>
      </Link>
    </div>
  );
};
