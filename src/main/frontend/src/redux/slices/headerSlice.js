import { createSlice } from "@reduxjs/toolkit";
import { getLocalStorage, setLocalStorage } from "../../utils/localStorage";

const initialState = {
    header: getLocalStorage("header"),
};

if (!initialState.header.courseId ||
    !initialState.header.lessonId ||
    !initialState.header.numberOfLesson) {
    initialState.header = {
        courseId: "",
        lessonId: "",
        numberOfLesson: "",
    };
}

export const headerSlice = createSlice({
    name: "header",
    initialState,
    reducers: {
        setCourseId(state, action) {
            state.header.courseId = action.payload;
            setLocalStorage("header", state.header);
        },
        setLessonId(state, action) {
            state.header.lessonId = action.payload;
            setLocalStorage("header", state.header);
        },
        setNumberOfLesson(state, action) {
            state.header.numberOfLesson = action.payload;
            setLocalStorage("header", state.header);
        },
        removeHeader(state) {
            localStorage.removeItem("header");
            state.header = { courseId: "", lessonId: "" };
        },
    },
});

export const { setCourseId, setLessonId, setNumberOfLesson, removeHeader } = headerSlice.actions;
export default headerSlice.reducer;