import React, { createContext, useState, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useSelector, useDispatch } from "react-redux";
import { useFormik } from "formik";
import * as Yup from "yup";
import { baseUrl } from "../api/http-client";
import endpoints from "../shared/router/endpoints";
import { removeUser, setUser } from "../redux/slices/userSlice";
import { removeCourse } from "../redux/slices/courseSlice";
import {
  addInvalidInviteCode,
  removeInvalidInviteCode,
} from "../redux/slices/counterInvalidInviteCodeSlice";
import { setLocalStorage } from "../utils/localStorage";
import { removeTask } from "../redux/slices/taskSlice";
import { removeLesson } from "../redux/slices/lessonSlice";
import {
  removeTelegramUrl,
  setTelegramUrl,
} from "../redux/slices/telegramUrlSlice";
import { removeTaskAnswers } from "../redux/slices/tasksForCorrectionSlice";
import { removeHeader } from "../redux/slices/headerSlice";
import {
  removeIsExpired,
  setIsExpired,
} from "../redux/slices/isInviteCodeExpiredSlice";
import { removeCourses } from "../redux/slices/coursesSlice";
import { AlertModal } from "../components/alert-modal/AlertModal";

const ReportRequest = createContext({});
const REGISTRATION_URL = "/api/register";
const LOGIN_URL = "/api/formLogin";
const LOGOUT_URL = "/api/logout";
const INVITATION_URL = "/api/invite";
const CERTIFICATE_ID_URL = "/api/certificate/";
const codeRegExr = /^[A-Za-z0-9]*$/;
const emailRegExr =
  /^(?!.*@.*@)(?![_-])(?!.*[_-]@)(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

const ReportRequestProvider = ({ children }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [isLogin, setIsLogin] = useState(false);
  const [isLogout, setIsLogout] = useState(false);
  const [certificateData, setCertificateData] = useState(null);
  const [isOnClickOnButtonChatBot, setIsOnClickButtonChatBot] = useState(() => {
    return (
      JSON.parse(sessionStorage.getItem("isOnClickOnButtonChatBot")) || false
    );
  });

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalProps, setModalProps] = useState({
    isOpen: false,
    title: "",
    alertText: "",
    buttonText: "",
    navigateTo: null,
  });

  const openModal = () => setIsModalOpen(true);

  const closeModal = () => {
    if (modalProps?.navigateTo) {
      navigate(modalProps.navigateTo); // Выполняем редирект, если navigateTo указан
    }
    setIsModalOpen(false);
    setModalProps({
      isOpen: false,
      title: "",
      alertText: "",
      buttonText: "",
      navigateTo: null,
    }); // Чистим состояние после закрытия модалки
  };

  const currentUser = useRef({});
  const currentUserEmail = useRef("");
  const telegramRef = useRef("");
  const isBannedRef = useRef(false);
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const arrInvalidInviteCode = useSelector(
    (state) => state.counterInvalidInviteCodeReducer.quantityInvalidInviteCode
  );
  const [isFollowTheLink, setIsFollowTheLink] = useState(false);

  const useInviteFormReport = useFormik({
    initialValues: {
      inviteCode: "",
    },

    validationSchema: Yup.object({
      inviteCode: Yup.string()
        .min(20, "Invite-code is short")
        .max(20, "No more than 20 characters")
        .matches(codeRegExr, "Invite-code is invalid")
        .required("Invite-code is required"),
    }),
    onSubmit: async (values) => {
      let invalidCode = 0;
      try {
        const response = await baseUrl({
          method: "post",
          url: INVITATION_URL,
          headers: {
            "Content-Type": "multipart/form-data",
          },
          data: {
            inviteCode: values.inviteCode,
          },
        });
        console.log(response.data);
        if (response.data.invite === endpoints.SUCCESS) {
          navigate(`/${endpoints.REGISTRATION}`, {
            state: { inviteCode: values.inviteCode },
          });
          localStorage.removeItem("invalid_code");
          localStorage.removeItem("quantity");
        } else if (response.data.invite === "expired") {
          dispatch(setIsExpired(false));
        } else if (response.data.invite === "used") {
          setModalProps({
            isOpen: { isModalOpen },
            title: "Oops...",
            alertText: "Invite code is fully used",
            buttonText: "Close",
          });
          openModal("Invite code is fully used");
        } else if (response.data.invite === "invalid code") {
          invalidCode++;
          const currentInvalidCode = localStorage.quantity
            ? invalidCode + parseInt(localStorage.quantity)
            : invalidCode;
          setLocalStorage("invalid_code", true);
          setLocalStorage("quantity", currentInvalidCode);
          dispatch(addInvalidInviteCode(parseInt(localStorage.quantity)));
          if (currentInvalidCode < 10 && !localStorage.date_lock === true) {
            setModalProps({
              isOpen: { isModalOpen },
              title: "Oops...",
              alertText: `Invite-code is invalid! ${
                10 - currentInvalidCode
              } attempts left`,
              buttonText: "Close",
            });

            openModal();
          }
          if (currentInvalidCode === 10 && !localStorage.date_lock === true) {
            setModalProps({
              isOpen: { isModalOpen },
              title: "Oops...",
              alertText: `Іnvite-code is invalid!\nYou are blocked for one hour`,
              buttonText: "Close",
            });
            openModal();

            setLocalStorage("date_lock", true);
            setLocalStorage("timer", true);
            localStorage.removeItem("invalid_code");
            localStorage.removeItem("quantity");
          }
        }
      } catch (error) {
        navigate(endpoints.INVITE);
      }
    },
  });

  const handleRegistrationFormReport = async (data) => {
    setIsLoading(true);
    try {
      const response = await baseUrl({
        method: "post",
        url: REGISTRATION_URL,
        headers: {
          "Content-Type": "multipart/form-data",
        },
        data: {
          name: data.name,
          surname: data.surname,
          email: data.email,
          phone: data.phone,
          client_invite: `${location.state.inviteCode}`,
          password: data.password,
          passwordConfirm: data.passwordConfirm,
        },
      });
      if (response.data.registration === endpoints.SUCCESS) {
        setIsLoading(false);
        telegramRef.current = response.data.telegramURL;
        currentUserEmail.current = response.data.email;
        navigate(`/${endpoints.SUCCESS}`, {
          state: { telegramUrl: response.data.telegramURL },
        });
      } else {
        if (response.data.description.length > 0) {
          setIsLoading(false);
          let errorMessages = response.data.description.map(function (error) {
            return error.error;
          });
          let errorMessage = errorMessages.join("\n");

          setModalProps({
            isOpen: { isModalOpen },
            title: "Oops...",
            alertText: errorMessage,
            buttonText: "Close",
          });

          openModal();
        } else {
          setIsLoading(false);
          setModalProps({
            isOpen: { isModalOpen },
            title: "Oops...",
            alertText: "Registration was not successful!",
            buttonText: "Close",
          });
          openModal();
        }
      }
    } catch (error) {}
  };

  const useLoginFormReport = useFormik({
    initialValues: {
      email: "",
      password: "",
    },

    validationSchema: Yup.object({
      email: Yup.string()
        .matches(emailRegExr, "Invalid email address.")
        .required("Email is required"),
      password: Yup.string()
        .min(6, "Password at least 6 characters")
        .max(20, "No more than 20 characters")
        .required("Password is required"),
    }),

    onSubmit: async (values) => {
      setIsLoading(true);
      try {
        const { data } = await baseUrl({
          method: "post",
          url: LOGIN_URL,
          headers: {
            "Content-Type": "application/json",
          },
          data: {
            email: values.email,
            password: values.password,
          },
        });

        if (data.principal.bannedStatus) {
          setIsLoading(false);
          isBannedRef.current = data.principal.bannedStatus;
        } else if (data.message === "Login is successful") {
          setIsLoading(false);
          dispatch(setUser(data.principal));
          currentUser.current = data.principal;
          telegramRef.current = data.telegramUrl;
          dispatch(setTelegramUrl(data.telegramUrl));
          localStorage.setItem("sessionId", data.jsessionId);
          setIsLogin(true);
          setIsLogout(true);
          navigate(endpoints.COURSES);
        } else {
          setIsLoading(false);
          setModalProps({
            isOpen: { isModalOpen },
            title: "Oops...",
            alertText: "Invalid email or password!",
            buttonText: "Close",
          });
          setModalProps({
            isOpen: { isModalOpen },
            title: "Oops...",
            alertText: "Invalid email or password!",
            buttonText: "Home",
            navigateTo: endpoints.HOME,
          });
          openModal();
        }
      } catch (error) {
        setIsLoading(false);
        setModalProps({
          isOpen: { isModalOpen },
          title: "Oops...",
          alertText: "Invalid email or password!",
          buttonText: "Home",
          navigateTo: endpoints.LOGIN,
        });
        openModal();
      }
      const resetLoginFields = () => {
        values.email = "";
        values.password = "";
      };
      resetLoginFields();
    },
  });

  const useClickLogout = async (event) => {
    event.preventDefault();
    try {
      const { data } = await baseUrl.post(LOGOUT_URL);
      document.cookie.split(";").forEach((c) => {
        document.cookie = c
          .replace(/^ +/, "")
          .replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
      });
      navigate(endpoints.HOME);
      dispatch(removeUser());
      dispatch(removeCourse());
      dispatch(removeCourses());
      dispatch(removeTask());
      dispatch(removeLesson());
      dispatch(removeTelegramUrl());
      dispatch(removeTaskAnswers());
      dispatch(removeHeader());
      dispatch(removeInvalidInviteCode());
      dispatch(removeIsExpired());
      localStorage.clear();
      window.location.reload();

      if (location.pathname === endpoints.HOME) {
        setIsLogout(false);
      }
    } catch (error) {
      console.error("Error during logout:", error);
    }
  };

  const useGetCertificateByUniqueId = useFormik({
    initialValues: {
      uniqueId: "",
    },

    validationSchema: Yup.object({
      uniqueId: Yup.string()
        .min(32, "Certificate code is short")
        .max(32, "No more than 32 characters")
        .matches(codeRegExr, "Wrong symbols!")
        .required("Wrong certificate ID"),
    }),

    onSubmit: async (values) => {
      try {
        const { data } = await baseUrl({
          method: "get",
          url: CERTIFICATE_ID_URL + values.uniqueId,
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
        setCertificateData(data);
      } catch {
        setModalProps({
          isOpen: { isModalOpen },
          title: "Oops...",
          alertText: "Certificate not found!",
          buttonText: "Home",
        });
        openModal();
      }
    },
  });

  return (
    <ReportRequest.Provider
      value={{
        useInviteFormReport: useInviteFormReport,
        arrInvalidInviteCode: arrInvalidInviteCode,
        handleRegistrationFormReport: handleRegistrationFormReport,
        useLoginFormReport: useLoginFormReport,
        useClickLogout: useClickLogout,
        useGetCertificateByUniqueId: useGetCertificateByUniqueId,
        currentUser: currentUser.current,
        isLoading: isLoading,
        isLogin: isLogin,
        isLogout: isLogout,
        certificateData: certificateData,
        telegramRef: telegramRef.current,
        currentUserEmail: currentUserEmail.current,
        isBannedRef: isBannedRef.current,
        isFollowTheLink: isFollowTheLink,
        setIsFollowTheLink: setIsFollowTheLink,
        isOnClickOnButtonChatBot: isOnClickOnButtonChatBot,
        setIsOnClickButtonChatBot: setIsOnClickButtonChatBot,
      }}
    >
      {children}
      <AlertModal
        isOpen={modalProps.isOpen}
        onClose={closeModal}
        title={modalProps.title}
        alertText={modalProps.alertText}
        buttonText={modalProps.buttonText}
        navigateTo={modalProps.navigateTo}
      />
    </ReportRequest.Provider>
  );
};
export { ReportRequestProvider, ReportRequest };
