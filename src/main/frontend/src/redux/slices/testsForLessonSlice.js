import { createSlice } from "@reduxjs/toolkit";
import { getLocalStorage, setLocalStorage } from "../../utils/localStorage";

const initialState = {
    tests: getLocalStorage("tests") || [],
};

export const testsForLessonSlice = createSlice({
    name: "tests",
    initialState,
    reducers: {
        setTests(state, action) {
            state.tests = action.payload;
            setLocalStorage("tests", action.payload);
        },
        removeTests(state) {
            localStorage.removeItem("tests");
            state.tests = initialState;
        },
    },
});

export const { setTests, removeTests } = testsForLessonSlice.actions;
export default testsForLessonSlice.reducer;