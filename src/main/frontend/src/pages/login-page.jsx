import React, { useContext, Fragment } from "react";
import { ReportRequest } from "../hooks/report-request-provider";
import { Spinner } from "../components/spinner/Spinner";
import { FormLogin } from "../components/form-login/Form-login";
import meeting from "../images/meeting.svg";

export const LoginPage = () => {
  const { useLoginFormReport, isLoading, isBannedRef } =
    useContext(ReportRequest);
  return (
    <Fragment>
      {isLoading ? (
        <Spinner />
      ) : (
        <div className="container-fluid min-vh-100 d-flex align-items-center justify-content-center py-5">
          <div className="row w-100 justify-content-center align-items-center">
            <div className="col-12 col-lg-6 mb-4">
              <FormLogin
                useLoginFormReport={useLoginFormReport}
                isBannedRef={isBannedRef}
              />
            </div>
            <div className="col-12 col-lg-6 d-none d-lg-block">
              <figure className="m-0">
                <img
                  src={meeting}
                  alt="Meeting"
                  className="img-fluid w-100 object-fit-cover"
                  width={770}
                  height={550}
                />
              </figure>
            </div>
          </div>
        </div>
      )}
    </Fragment>
  );
};
