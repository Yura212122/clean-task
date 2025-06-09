import { configureStore } from "@reduxjs/toolkit";
import userReducer from "./slices/userSlice";
import courseReducer from "./slices/courseSlice";
import coursesReducer from "./slices/coursesSlice";
import counterInvalidInviteCodeReducer from "./slices/counterInvalidInviteCodeSlice";
import taskReducer from "./slices/taskSlice";
import timerReducer from "./slices/timerSlice";
import lessonReducer from "./slices/lessonSlice";
import headerReducer from "./slices/headerSlice";
import taskAnswersReducer from "./slices/tasksForCorrectionSlice";
import telegramUrlReducer from "./slices/telegramUrlSlice";
import isInviteCodeExpiredReducer from "./slices/isInviteCodeExpiredSlice";
import testsForLessonReducer from "./slices/testsForLessonSlice";

export const store = configureStore({
     reducer: {
          userReducer,
          courseReducer,
          coursesReducer,
          taskReducer,
          counterInvalidInviteCodeReducer,
          timerReducer,
          lessonReducer,
          headerReducer,
          taskAnswersReducer,
          telegramUrlReducer,
          isInviteCodeExpiredReducer,
          testsForLessonReducer,
     },
})