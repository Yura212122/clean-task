import { createSlice } from "@reduxjs/toolkit";
import { getLocalStorage, setLocalStorage } from "../../utils/localStorage";

const initialState = {
    course: {
        name: getLocalStorage("course").name || "",
        courseId: getLocalStorage("course").courseId || null,
        numberOfLesson: getLocalStorage("course").numberOfLesson || null,
        listOfLessons: getLocalStorage("course").listOfLessons || [],
    }
};

export const courseSlice = createSlice({
    name: "course",
    initialState,
    reducers: {
        setCourse(state, action) {
            state.course = action.payload;
            setLocalStorage("course", action.payload);
        },
        removeCourse(state) {
            localStorage.removeItem("course");
            state.course = initialState;
        },
    },
});

export const { setCourse, removeCourse } = courseSlice.actions;
export default courseSlice.reducer;