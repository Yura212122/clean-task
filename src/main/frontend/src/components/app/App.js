import React, {useEffect} from 'react';
import { Layout } from '../../layout/Layout';
import {Routes, Route} from 'react-router-dom';
import { ErrorBoundary } from '../../hooks/error-boundary';
import {ReportRequestProvider} from '../../hooks/report-request-provider';
import { HomePage } from '../../pages/home-page';
import { InvitePage } from '../../pages/invite-page';
import { NotFoundPage } from '../../pages/not-found-page';
import { RegistrationPage } from '../../pages/registration-page';
import { LoginPage } from '../../pages/login-page';
import { SuccessPage } from '../../pages/success-page';
import { CertificatePage } from '../../pages/certificate-page/certificate-page';
import {CoursesPage} from "../../pages/courses-page";
import {BannedPage} from "../../pages/banned-page";
import endpoints from '../../shared/router/endpoints';
import {CourseLessonsPage} from "../../pages/course-lessons-page";
import {LessonDetailsPage} from "../../pages/lesson-details-page";
import {TaskPage} from "../../pages/task-page";
import {TeacherPage} from "../../pages/teacher-page";
import {TestPage} from "../../pages/test-page";
import {NotLoggedInPage} from "../../pages/not-logged-in-page";
import {EmployeesPage} from "../../pages/employees-page";
import {StudentsPage} from "../../pages/students-page";
import {useDispatch} from "react-redux";
import {removeIsExpired} from "../../redux/slices/isInviteCodeExpiredSlice";

import "../teacher/teacher.css";
import AdminBroadcast from "../admin-page/Admin";

export const App = () => {
  const dispatch = useDispatch();

  useEffect(() => {
    const handleBeforeUnload = () => {
      dispatch(removeIsExpired());
    };

    window.addEventListener('beforeunload', handleBeforeUnload);

    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, [dispatch]);

  return (
      <ErrorBoundary>
        <ReportRequestProvider>
          <Routes>
            <Route path={endpoints.HOME} element={<Layout />}>
              <Route index element={<HomePage />} />
              <Route exact path={endpoints.INVITE} element={<InvitePage />} />
              <Route exact path={endpoints.REGISTRATION} element={<RegistrationPage />} />
              <Route exact path={endpoints.LOGIN} element={<LoginPage />} />
              <Route exact path={endpoints.SUCCESS} element={<SuccessPage />} />
              <Route exact path={endpoints.COURSES} element={<CoursesPage />} />
              <Route exact path={endpoints.COURSE}>
                <Route exact path=":courseId" element={<CourseLessonsPage />} />
              </Route>
              <Route exact path={endpoints.LESSON}>
                <Route exact path=":lessonId" element={<LessonDetailsPage />} />
              </Route>
              <Route exact path={endpoints.TASK}>
                <Route exact path=":taskId" element={<TaskPage />} />
              </Route>
              <Route exact path={endpoints.TEST}>
                <Route exact path=":testId" element={<TestPage />} />
              </Route>
              <Route exact path={endpoints.NOT_LOGGED_IN} element={<NotLoggedInPage />} />
              <Route exact path={endpoints.NOT_FOUND} element={<NotFoundPage />} />
              <Route exact path={endpoints.CERTIFICATE} element={<CertificatePage />} />
              <Route exact path={endpoints.BANNED} element={<BannedPage />} />
              <Route exact path={endpoints.TEACHER} element={<TeacherPage />} />
              <Route exact path={endpoints.EMPLOYEES} element={<EmployeesPage />} />
              <Route exact path={endpoints.STUDENTS} element={<StudentsPage />} />
              <Route exact path={endpoints.ADMIN_BROADCAST} element={<AdminBroadcast />} />
            </Route>
          </Routes>
        </ReportRequestProvider>
      </ErrorBoundary>
  );
};