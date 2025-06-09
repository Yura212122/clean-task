import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import logo from "../../images/logo.svg";
import colors from "../../shared/dto/colors";
import endpoints from "../../shared/router/endpoints";
import icon_banned from "../../images/icon_banned.png";
import "./form-login.css";

export const FormLogin = ({ useLoginFormReport, isBannedRef }) => {
    const validEmail = useLoginFormReport?.touched?.email && !useLoginFormReport?.errors?.email;
    const invalidEmail = useLoginFormReport?.touched?.email && !!useLoginFormReport?.errors?.email;
    const validPassword = useLoginFormReport?.touched?.password && !useLoginFormReport?.errors?.password;
    const invalidPassword = useLoginFormReport?.touched?.password && !!useLoginFormReport?.errors?.password;
    const disabledButton =
        !useLoginFormReport?.values?.email ||
        !!useLoginFormReport?.errors?.email ||
        !useLoginFormReport?.values?.password ||
        !!useLoginFormReport?.errors?.password;

    const [isUserBanned, setIsUserBanned] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState(null);

    useEffect(() => {
        const isBanned = typeof isBannedRef === "object" && isBannedRef?.current ? isBannedRef.current : isBannedRef === true;
        setIsUserBanned(isBanned);
    }, [isBannedRef]);

    useEffect(() => {
    }, [useLoginFormReport]);

    const handleFormSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);
        setSubmitError(null);
        try {
            if (!useLoginFormReport?.handleSubmit) {
                throw new Error("handleSubmit is undefined");
            }
            const result = await useLoginFormReport.handleSubmit(e);
        } catch (error) {
            setSubmitError(`Login failed: ${error.message}`);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
<div
  className="d-flex flex-column col-12 col-sm-7 col-md-6 col-lg-5"
  style={{ background: colors.WHITE }}
>
  {isUserBanned && (
    <div className="overlay">
      <div className="modal-window text-center">
        <img src={icon_banned} width="45px" className="mb-3" alt="icon_banned" />
        <p className="mb-4 fw-bold">YOUR ACCOUNT IS BLOCKED!</p>
        <Link to={endpoints.HOME}>
          <button
            className="btn btn-sm btn-yes form-login-blocked"
            style={{
              color: colors.GREEN_50,
              backgroundColor: colors.BLUE_500,
              borderColor: colors.BLUE_500,
            }}
          >
            I see
          </button>
        </Link>
      </div>
    </div>
  )}

  <figure className="mx-auto m-0 my-4 d-flex">
    <Link to={endpoints.HOME}>
      <img src={logo} alt="Logo" className="img-fluid" width={96} height={96} />
    </Link>
  </figure>

  <form
    method="POST"
    className="mx-auto p-2 d-flex flex-column"
    noValidate
    onSubmit={async (e) => {
      e.preventDefault();
      await handleFormSubmit(e);
    }}
    style={{ width: "304px" }}
  >
    {/* Email */}
    <div
      className={`d-flex flex-column ${invalidEmail ? "mb-0" : ""}`}
      style={{ marginBottom: "21px" }}
    >
      <input
        id="input-email-login"
        type="email"
        name="email"
        placeholder="Email address"
        autoComplete="email"
        required
        value={useLoginFormReport?.values?.email || ""}
        onChange={useLoginFormReport?.handleChange || (() => {})}
        onBlur={(e) => useLoginFormReport?.handleBlur?.(e)}
        className="form-control form-control-sm"
        style={{
          color: colors.BLACK,
          backgroundColor: colors.GRAY_300,
          borderRadius: "8px",
          borderColor: validEmail ? colors.GREEN : invalidEmail ? colors.RED : "",
        }}
      />
      <label
        htmlFor="input-email-login"
        className="form-text mt-0"
        style={{ color: invalidEmail ? colors.RED : "" }}
      >
        {invalidEmail ? useLoginFormReport?.errors?.email : ""}
      </label>
    </div>

    {/* Password */}
    <div
      className={`d-flex flex-column ${invalidPassword ? "mb-0" : ""}`}
      style={{ marginBottom: "21px" }}
    >
      <input
        id="input-password-login"
        type="password"
        name="password"
        placeholder="Password"
        autoComplete="off"
        required
        value={useLoginFormReport?.values?.password || ""}
        onChange={useLoginFormReport?.handleChange || (() => {})}
        onBlur={(e) => useLoginFormReport?.handleBlur?.(e)}
        className="form-control form-control-sm"
        style={{
          color: colors.BLACK,
          backgroundColor: colors.GRAY_300,
          borderRadius: "8px",
          borderColor: validPassword ? colors.GREEN : invalidPassword ? colors.RED : "",
        }}
      />
      <label
        htmlFor="input-password-login"
        className="form-text mt-0"
        style={{ color: invalidPassword ? colors.RED : "" }}
      >
        {invalidPassword ? useLoginFormReport?.errors?.password : ""}
      </label>
    </div>

    {/* Error */}
    {submitError && (
      <div style={{ color: colors.RED, textAlign: "center", marginBottom: "10px" }}>
        {submitError}
      </div>
    )}

    {/* Submit */}
    <div className="d-flex justify-content-center">
      <button
        type="submit"
        className="btn btn-sm"
        style={{
          width: "194px",
          height: "32px",
          color: disabledButton || isSubmitting ? colors.GRAY : colors.GREEN_50,
          borderColor: colors.BLUE_900,
          borderRadius: "8px",
          backgroundColor: disabledButton || isSubmitting ? "" : colors.BLUE_900,
        }}
        disabled={disabledButton || isSubmitting}
        onClick={async (e) => {
          if (!disabledButton && !isSubmitting) {
            await handleFormSubmit(e);
          }
        }}
      >
        {isSubmitting ? "Logging in..." : "Login"}
      </button>
    </div>
  </form>
</div>
);
};