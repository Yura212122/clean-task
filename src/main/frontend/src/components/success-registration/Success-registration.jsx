import React, { useContext, useEffect, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { ReportRequest } from "../../hooks/report-request-provider";

import endpoints from "../../shared/router/endpoints";
import colors from "../../shared/dto/colors";
import success from "../../images/success.svg";

import "./success-registration.css";

export const SuccessRegistration = () => {
  const location = useLocation();
  const [isModalWindowShown, setIsModalWindowShown] = useState(true);
  const { telegramRef, setIsOnClickButtonChatBot, setIsFollowTheLink } =
    useContext(ReportRequest);

  useEffect(() => {
    const storedTelegramRef = sessionStorage.getItem("telegramRef");
    if (!storedTelegramRef) {
      sessionStorage.setItem("telegramRef", telegramRef);
    }
  }, [telegramRef]);

  if (!location.state) {
    return <Navigate to={endpoints.HOME} />;
  }

  const getChatBot = () => {
    setIsFollowTheLink(true);
    setIsOnClickButtonChatBot(true);
    window.open(sessionStorage.getItem("telegramRef"), "_blank");
  };

  return (
    <section
      className="w-100 d-flex flex-column align-items-center px-3"
      style={{ paddingTop: "56px" }}
    >
      {isModalWindowShown && (
        <div className="overlay">
          <div className="modal-window-not-logged-in text-center p-4">
            <p
              className="fw-bold text-center mb-2"
              style={{ fontSize: "22px", color: colors.BLUE_900 }}
            >
              🎉 Congratulations, you have successfully registered!
            </p>
            <p
              className="fw-bold text-center mb-3"
              style={{ fontSize: "20px", color: colors.BLUE_900 }}
            >
              A confirmation email has been sent to your inbox.
            </p>
            <button
              className="btn btn-sm btn-yes btn-success-reg"
              onClick={() => setIsModalWindowShown(false)}
              style={{
                color: colors.GREEN_50,
                backgroundColor: colors.BLUE_500,
                borderColor: colors.BLUE_500,
              }}
            >
              OK
            </button>
          </div>
        </div>
      )}
      <div className="mt-5 mb-4 d-flex flex-column align-items-center text-center">
        <p
          className="fw-bold"
          style={{ fontSize: "22px", color: colors.BLUE_900 }}
        >
          Please go to the Telegram chatbot,
          <br />
          click <strong>"START"</strong>, and then return here to log in.
        </p>
        <figure className="m-0 d-flex justify-content-center">
          <img
            src={success}
            alt="Megaphone"
            className="img-fluid"
            width={300}
            height={300}
          />
        </figure>
      </div>
      <div className="my-3">
        <button
          type="button"
          className="btn btn-sm btn-telegram px-4 py-2"
          onClick={getChatBot}
          style={{
            color: colors.GREEN_50,
            borderColor: colors.BLUE_900,
            backgroundColor: colors.BLUE_900,
          }}
        >
          🚀 Join Telegram
        </button>
      </div>
    </section>
  );
};
