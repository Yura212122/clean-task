import { createSlice } from "@reduxjs/toolkit";
import { getLocalStorage, setLocalStorage } from "../../utils/localStorage";

const initialState = {
    taskAnswers: getLocalStorage("taskAnswers") || [],
};

export const tasksForCorrectionSlice = createSlice({
    name: "taskAnswers",
    initialState,
    reducers: {
        setTaskAnswers(state, action) {
            state.taskAnswers = action.payload;
            setLocalStorage("taskAnswers", action.payload);
        },
        updateTaskAnswer(state, action) {
            const index = state.taskAnswers.findIndex(task => task.taskId === action.payload.taskId);
            if (index !== -1) {
                state.taskAnswers[index] = action.payload;
                setLocalStorage("taskAnswers", state.taskAnswers);
            }
        },
        removeTaskAnswers(state) {
            localStorage.removeItem("taskAnswers");
            state.taskAnswers = initialState.taskAnswers;
        },
    },
});

export const { setTaskAnswers, updateTaskAnswer, removeTaskAnswers } = tasksForCorrectionSlice.actions;
export default tasksForCorrectionSlice.reducer;