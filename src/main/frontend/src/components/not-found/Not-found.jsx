import React from "react";
import { Link } from "react-router-dom";
import logo from "../../images/logo.svg";
import notFound from "../../images/not-found.svg";
import oops from "../../images/oops.svg";

import endpoints from "../../shared/router/endpoints";
import colors from "../../shared/dto/colors";

import "./not-found.css";

export const NotFound = () => {
  return (
    <section className="container-fluid min-vh-100 d-flex flex-column align-items-center justify-content-center py-5">
      <figure className="m-0 py-4 text-center">
        <Link to={"https://prog.academy/"} target="_blank">
          <img
            src={logo}
            alt="Logo"
            className="img-fluid"
            width={96}
            height={96}
          />
        </Link>
      </figure>
      <div className="row w-100 gap-4 d-flex flex-column flex-md-row align-items-center justify-content-center">
        <div className="col-12 col-md-6 text-center">
          <img
            src={notFound}
            alt="Not Found"
            className="img-fluid w-75"
            width={350}
            height={350}
          />
        </div>
        <div className="col-12 col-md-6 text-center">
          <figure className="m-0 mb-3">
            <img
              src={oops}
              alt="Oops!"
              className="img-fluid"
              width={182}
              height={62}
            />
          </figure>
          <h1 className="mb-4 fs-2 fw-bold">
            We can't seem to find the <br /> page you're looking for
          </h1>
          <button
            type="button"
            className="btn btn-sm btn-lg not-found-btn"
            onClick={() => window.open(endpoints.HOME, "_self")}
            style={{
              color: colors.GREEN_50,
              borderColor: colors.BLUE_900,
              backgroundColor: colors.BLUE_900,
            }}
          >
            Go to Home
          </button>
        </div>
      </div>
    </section>
  );
};
