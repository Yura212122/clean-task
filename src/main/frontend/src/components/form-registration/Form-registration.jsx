import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { Link } from "react-router-dom";
import ReCAPTCHA from "react-google-recaptcha";
import logo from "../../images/logo.svg";
import endpoints from "../../shared/router/endpoints";
import colors from "../../shared/dto/colors";
import { AlertModal } from "../alert-modal/AlertModal";

import "./form-registration.css";

export const FormRegistration = ({ handleRegistrationFormReport }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    setValue,
    trigger,
    clearErrors,
    reset,
    setError,
  } = useForm({
    mode: "onBlur",
  });
  const location = useLocation();
  const navigate = useNavigate();
  const [isRecaptchaVerified, setRecaptchaVerified] = useState(false);
  const [isPasswordsMatch, setIsPasswordsMatch] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

  useEffect(() => {
    handleAutoFill();
  }, []);

  useEffect(() => {
    const subscription = watch((value, { name }) => {
      if (name === "password" || name === "passwordConfirm") {
        const password = value.password;
        const passwordConfirm = value.passwordConfirm;
        if (password && passwordConfirm) {
          if (password !== passwordConfirm) {
            setIsPasswordsMatch(false);
            setError("passwordConfirm", {
              type: "manual",
              message: "Passwords should match",
            });
          } else {
            setIsPasswordsMatch(true);
            clearErrors("passwordConfirm");
          }
        }
      }
    });
    return () => subscription.unsubscribe();
  }, [watch, setError, clearErrors]);
  const handleRecaptchaChange = (captchaValue) => {
    setRecaptchaVerified(!!captchaValue);
  };

  const handleInviteCode = () => {
    if (!location.state) {
      openModal();
    }
  };

  const handleAutoFill = () => {
    // Check if fields are autofilled
    const fields = [
      "name",
      "surname",
      "email",
      "phone",
      "password",
      "passwordConfirm",
    ];
    fields.forEach((field) => {
      if (watch(field)) {
        setValue(field, watch(field)); // Set value to trigger validation
        trigger(field); // Trigger validation
      }
    });
  };

  const onHandleSubmit = (data) => {
    handleRegistrationFormReport(data);
    reset();
  };

  return (
    <div
      className="d-flex flex-column col-12 col-sm-7 col-md-6 col-lg-5"
      style={{ background: colors.WHITE }}
    >
      <figure className="mx-auto m-0 my-4 d-flex">
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
      <form
        className="mx-auto px-2 d-flex flex-column"
        onSubmit={handleSubmit(onHandleSubmit)}
        onChange={handleInviteCode}
        style={{ width: "304px" }}
      >
        <div
          className={`d-flex flex-column ${errors.name ? "mb-0" : ""}`}
          style={{ marginBottom: "21px" }}
        >
          <input
            id="input-first-name"
            type="text"
            name="name"
            placeholder="First name"
            autoComplete="given-name"
            {...register("name", {
              required: "First name is required",
              minLength: {
                value: 2,
                message: "First name should be at least 2 characters",
              },
              maxLength: {
                value: 30,
                message: "First name shouldn't be more than 30 characters",
              },
              pattern: {
                value: /^(?!.{31})[A-Z][a-z]{1,}(-[A-Z][a-z]{1,})*$/,
                message:
                  "Only the Latin alphabet is allowed and the first letter is capitalized",
              },
            })}
            className={`w-100 form-control form-control-sm ${
              errors.name ? "is-invalid" : ""
            }`}
            style={{
              color: colors.BLACK,
              backgroundColor: colors.GRAY_300,
              borderRadius: "8px",
              borderColor: `${
                watch("name")
                  ? errors.name
                    ? colors.RED
                    : colors.GREEN
                  : colors.GRAY_300
              }`,
            }}
          />
          <label
            htmlFor="input-first-name"
            className="form-text mt-0"
            style={{ color: `${errors.name ? colors.RED : "transparent"}` }}
          >
            {errors.name && errors.name.message}
          </label>
        </div>
        <div
          className={`d-flex flex-column ${errors.surname ? "mb-0" : ""}`}
          style={{ marginBottom: "21px" }}
        >
          <input
            id="input-last-name"
            type="text"
            name="surname"
            placeholder="Last name"
            autoComplete="family-name"
            {...register("surname", {
              required: "Last name is required",
              minLength: {
                value: 2,
                message: "Last name should be at least 2 characters",
              },
              maxLength: {
                value: 30,
                message: "First name shouldn't be more than 30 characters",
              },
              pattern: {
                value: /^(?!.{31})[A-Z][a-z]{1,}(-[A-Z][a-z]{1,})*$/,
                message:
                  "Only the Latin alphabet is allowed and the first letter is capitalized",
              },
            })}
            className={`form-control form-control-sm ${
              errors.surname ? "is-invalid" : ""
            }`}
            style={{
              color: colors.BLACK,
              backgroundColor: colors.GRAY_300,
              borderRadius: "8px",
              borderColor: `${
                watch("surname")
                  ? errors.surname
                    ? colors.RED
                    : colors.GREEN
                  : colors.GRAY_300
              }`,
            }}
          />
          <label
            htmlFor="input-last-name"
            className="form-text mt-0"
            style={{ color: `${errors.surname ? colors.RED : "transparent"}` }}
          >
            {errors.surname && errors.surname.message}
          </label>
        </div>
        <div
          className={`d-flex flex-column ${errors.email ? "mb-0" : ""}`}
          style={{ marginBottom: "21px" }}
        >
          <input
            id="input-email"
            type="email"
            name="email"
            placeholder="Email address"
            autoComplete="email"
            {...register("email", {
              required: "Email is required",
              maxLength: {
                value: 256,
                message: "Email shouldn't be more than 256 characters",
              },
              pattern: {
                value:
                  /^(?!.*@.*@)(?![_-])(?!.*[_-]@)(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,

                message: "Invalid email address",
              },
            })}
            className={`form-control form-control-sm ${
              errors.email ? "is-invalid" : ""
            }`}
            style={{
              color: colors.BLACK,
              backgroundColor: colors.GRAY_300,
              borderRadius: "8px",
              borderColor: `${
                watch("email")
                  ? errors.email
                    ? colors.RED
                    : colors.GREEN
                  : colors.GRAY_300
              }`,
            }}
          />
          <label
            htmlFor="input-email"
            className="form-text mt-0"
            style={{ color: `${errors.email ? colors.RED : "transparent"}` }}
          >
            {errors.email && errors.email.message}
          </label>
        </div>
        <div
          className={`d-flex flex-column ${errors.phone ? "mb-0" : ""}`}
          style={{ marginBottom: "21px" }}
        >
          <input
            id="input-phone"
            type="tel"
            name="phone"
            placeholder="Phone number"
            autoComplete="tel"
            {...register("phone", {
              required: "Phone number is required",
              minLength: {
                value: 9,
                message: "Phone number must contain 9-15 digits",
              },
              maxLength: {
                value: 15,
                message: "Phone number must contain 9-15 digits",
              },
              pattern: {
                value: /^(00\d{7,13}|0\d{9}|\+\d{9,15}|\d{9,15})$/,
                message: "Invalid phone number",
              },
            })}
            className={`form-control form-control-sm ${
              errors.phone ? "is-invalid" : ""
            }`}
            style={{
              color: colors.BLACK,
              backgroundColor: colors.GRAY_300,
              borderRadius: "8px",
              borderColor: `${
                watch("phone")
                  ? errors.phone
                    ? colors.RED
                    : colors.GREEN
                  : colors.GRAY_300
              }`,
            }}
          />
          <label
            htmlFor="input-phone"
            className="form-text mt-0"
            style={{ color: `${errors.phone ? colors.RED : "transparent"}` }}
          >
            {errors.phone && errors.phone.message}
          </label>
        </div>
        <div
          className={`d-flex flex-column ${errors.password ? "mb-0" : ""}`}
          style={{ marginBottom: "21px" }}
        >
          <input
            id="input-password"
            type="password"
            name="password"
            placeholder="Password"
            autoComplete="new-password"
            {...register("password", {
              required: "Password is required",
              minLength: {
                value: 6,
                message: "Password should be at least 6 characters",
              },
              maxLength: { value: 20, message: "No more than 20 characters" },
            })}
            className={`form-control form-control-sm ${
              errors.password ? "is-invalid" : ""
            }`}
            style={{
              color: colors.BLACK,
              backgroundColor: colors.GRAY_300,
              borderRadius: "8px",
              borderColor: `${
                watch("password")
                  ? errors.password
                    ? colors.RED
                    : colors.GREEN
                  : colors.GRAY_300
              }`,
            }}
          />
          <label
            htmlFor="input-password"
            className="form-text mt-0"
            style={{ color: `${errors.password ? colors.RED : "transparent"}` }}
          >
            {errors.password && errors.password.message}
          </label>
        </div>
        <div
          className={`d-flex flex-column ${
            errors.passwordConfirm ? "mb-0" : ""
          }`}
          style={{ marginBottom: "21px" }}
        >
          <input
            id="input-password-confirm"
            type="password"
            name="passwordConfirm"
            placeholder="Confirm Password"
            autoComplete="new-password"
            {...register("passwordConfirm", {
              required: "Please confirm your password",
              validate: {
                matchesPreviousPassword: (value) => {
                  const { password } = watch();
                  return password === value || "Passwords should match";
                },
              },
            })}
            className={`form-control form-control-sm ${
              errors.passwordConfirm ? "is-invalid" : ""
            }`}
            style={{
              color: colors.BLACK,
              backgroundColor: colors.GRAY_300,
              borderRadius: "8px",
              borderColor: `${
                watch("passwordConfirm")
                  ? errors.passwordConfirm
                    ? colors.RED
                    : colors.GREEN
                  : colors.GRAY_300
              }`,
            }}
          />
          <label
            htmlFor="input-password-confirm"
            className="form-text mt-0"
            style={{
              color: `${errors.passwordConfirm ? colors.RED : "transparent"}`,
            }}
          >
            {errors.passwordConfirm && errors.passwordConfirm.message}
          </label>
        </div>
        <div className="recaptcha-container mt-2">
          <div className="recaptcha">
            <ReCAPTCHA
              sitekey={process.env.REACT_APP_SITE_KEY}
              onChange={handleRecaptchaChange}
              theme="light"
            />
          </div>
        </div>
        <div className="mb-4 d-flex justify-content-center">
          <button
            type="submit"
            onClick={handleSubmit(onHandleSubmit)}
            className="btn btn-sm mt-2"
            style={{
              width: "194px",
              height: "32px",
              color: `${
                !isRecaptchaVerified || !isPasswordsMatch
                  ? colors.GRAY
                  : colors.GREEN_50
              }`,
              borderColor: colors.BLUE_900,
              borderRadius: "8px",
              backgroundColor: `${
                !isRecaptchaVerified || !isPasswordsMatch ? "" : colors.BLUE_900
              }`,
            }}
            disabled={!isRecaptchaVerified || !isPasswordsMatch}
          >
            Registration
          </button>
        </div>
      </form>
      <AlertModal
        isOpen={isModalOpen}
        onClose={closeModal}
        title="Oops..."
        alertText="You need to enter an invite-code or your invite-code is not valid. Please contact Prog Academy."
        buttonText="Home"
        navigateTo={endpoints.HOME}
      />
    </div>
  );
};
