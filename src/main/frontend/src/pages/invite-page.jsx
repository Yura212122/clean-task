import React from "react";
import { Link } from "react-router-dom";
import { FormInvite } from "../components/form-invite/Form-invite";
import logo from "../images/logo.svg";
import rocket from "../images/rocket.svg";
import endpoints from "../shared/router/endpoints";

export const InvitePage = () => {
  return (
    <section className="container-fluid d-flex flex-column align-items-center position-relative py-4">
      <figure className="m-0 py-3 text-center">
        <Link to={`${endpoints.HOME}`}>
          <img
            src={logo}
            alt="Logo"
            className="img-fluid"
            width={96}
            height={96}
          />
        </Link>
      </figure>
      <div className="d-flex justify-content-center my-3">
        <figure className="m-0">
          <img
            src={rocket}
            alt="Rocket"
            className="img-fluid img-to-sm w-75 w-md-50"
          />
        </figure>
      </div>
      <h1 className="w-100 text-center fs-4 fw-bold mb-3">Input invite code</h1>
      <div className="w-100 w-md-75 w-lg-50">
        <FormInvite />
      </div>
    </section>
  );
};
