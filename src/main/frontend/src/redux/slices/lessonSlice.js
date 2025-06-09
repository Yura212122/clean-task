import { createSlice } from "@reduxjs/toolkit";
import { getLocalStorage, setLocalStorage } from "../../utils/localStorage";

const initialState = {
    lesson: {
        id: getLocalStorage("lesson").id || null,
    }
};

export const lessonSlice = createSlice({
    name: "lesson",
    initialState,
    reducers: {
        setLesson(state, action) {
            state.lesson = action.payload;
            setLocalStorage("lesson", action.payload);
        },
        removeLesson(state) {
            localStorage.removeItem("lesson");
            state.lesson = initialState;
        },
    },
});

export const { setLesson, removeLesson } = lessonSlice.actions;
export default lessonSlice.reducer;