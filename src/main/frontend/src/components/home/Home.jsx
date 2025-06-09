import React, { Fragment, useEffect } from 'react';
import {Link, useNavigate, useLocation} from "react-router-dom";
import welcome_image from "../../images/welcome_image.svg";
import home_reg_img from "../../images/home_reg_img.svg";
import { useSelector } from "react-redux";

import colors from "../../shared/dto/colors";
import endpoints from "../../shared/router/endpoints";

import "./home.css";

export const Home = () => {
    const currentUserId = useSelector((state) => state.userReducer.user.id);
    const token = localStorage.getItem("authToken");
    const navigate = useNavigate();
    const location = useLocation();


    useEffect(() => {
        if (currentUserId && token && location.pathname === "/") {
            console.log("Navigating to /courses");
            navigate(`/${endpoints.COURSES}`, { replace: true });
        }
    }, [currentUserId, token, location.pathname, navigate]);

  return (
   <Fragment>
  <div className="container text-center my-5">
    <h2 className="text-border fs-4 fw-bold mb-5">
      ARE YOU ALREADY REGISTERED?
    </h2>

    <div className="row justify-content-center g-4">
      {/* Login Card */}
      <div className="col-12 col-md-6 col-lg-5">
        <div className="card position-relative border-0 shadow-sm">
          <img
            src={welcome_image}
            className="img-fluid rounded card-welcome-img"
            alt="Welcome"
          />
          <Link to={`/${endpoints.LOGIN}`} className="text-decoration-none">
            <button
              className="btn btn-sm position-absolute top-50 start-50 translate-middle card-welcome-btn"
              style={{
                color: colors.GREEN_50,
                backgroundColor: colors.BLUE_700,
                borderColor: colors.BLUE_700,
              }}
            >
              Login
            </button>
          </Link>
        </div>
      </div>

      {/* Registration Card */}
      <div className="col-12 col-md-6 col-lg-5">
        <div className="card position-relative border-0 shadow-sm">
          <img
            src={home_reg_img}
            className="img-fluid rounded card-welcome-img-reg"
            alt="Register"
          />
          <Link to={`/${endpoints.INVITE}`} className="text-decoration-none">
            <button
              className="btn btn-sm position-absolute top-50 start-50 translate-middle card-welcome-btn"
              style={{
                color: colors.GREEN_50,
                backgroundColor: colors.BLUE_700,
                borderColor: colors.BLUE_700,
              }}
            >
              Registration
            </button>
          </Link>
        </div>
      </div>
    </div>
  </div>
</Fragment>

  );
};
