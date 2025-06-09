import React from "react";

export const BanMessage = () => {
  return (
    <div
      className="container-fluid min-vh-100 d-flex flex-column justify-content-center align-items-center text-center"
      style={{ paddingTop: "56px" }}
    >
      <div className="row">
        <div className="col">
          <h1 className="fw-bold">Your Account is Blocked</h1>
          <p className="fs-5">
            If you believe this is an error, please contact support.
          </p>
        </div>
      </div>
    </div>
  );
};
