import { createSlice } from "@reduxjs/toolkit";
import { getLocalStorage, setLocalStorage } from "../../utils/localStorage";

const initialState = {
    courses: getLocalStorage("courses") || [],
};

export const coursesSlice = createSlice({
    name: "courses",
    initialState,
    reducers: {
        setCourses(state, action) {
            state.courses = action.payload;
            setLocalStorage("courses", action.payload);
        },
        removeCourses(state) {
            localStorage.removeItem("courses");
            state.courses = initialState;
        },
    },
});

export const { setCourses, removeCourses } = coursesSlice.actions;
export default coursesSlice.reducer;