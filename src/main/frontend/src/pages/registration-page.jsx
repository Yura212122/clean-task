import React, { useContext, Fragment } from "react";
import { ReportRequest } from "../hooks/report-request-provider";
import { Spinner } from "../components/spinner/Spinner";
import { FormRegistration } from "../components/form-registration/Form-registration";
import meeting from "../images/meeting.svg";

export const RegistrationPage = () => {
  const { handleRegistrationFormReport, isLoading } = useContext(ReportRequest);
  return (
    <Fragment>
      {isLoading ? (
        <Spinner />
      ) : (
        <div className="w-100 min-vh-100 d-flex flex-column flex-lg-row align-items-center justify-content-center">
          <div className="w-100 w-md-75 w-lg-50 px-3">
            <FormRegistration
              handleRegistrationFormReport={handleRegistrationFormReport}
            />
          </div>
          <div className="w-100 d-none d-lg-flex">
            <figure className="w-100 m-0 d-flex">
              <img
                src={meeting}
                alt="Meeting"
                className="w-100 object-fit-cover img-fluid"
                width={770}
                height={550}
              />
            </figure>
          </div>
        </div>
      )}
    </Fragment>
  );
};
