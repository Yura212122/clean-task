import not_logged_in from "../../images/not_logged_in.svg";
import { Link } from "react-router-dom";
import endpoints from "../../shared/router/endpoints";
import colors from "../../shared/dto/colors";
import React from "react";

export const NotUser = () => {
  return (
    <div className="overlay d-flex justify-content-center align-items-center">
      <div className="modal-window-not-logged-in text-center p-4 p-md-5 rounded shadow">
        <img
          src={not_logged_in}
          className="mb-3 img-fluid"
          style={{ maxWidth: "150px" }}
          alt="not_logged_in"
        />
        <p className="mb-4 fw-bold fs-5 fs-md-4">YOU'RE NOT LOGGED IN!</p>
        <Link to={endpoints.HOME}>
          <button
            className="btn btn-sm btn-yes modal-window-not-logged-in-btn px-3 py-2"
            style={{
              color: colors.GREEN_50,
              backgroundColor: colors.BLUE_500,
              borderColor: colors.BLUE_500,
            }}
          >
            I'm ready to log in
          </button>
        </Link>
      </div>
    </div>
  );
};
