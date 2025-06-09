import { createSlice } from "@reduxjs/toolkit";
import { getLocalStorage, setLocalStorage } from "../../utils/localStorage";

const initialState = {
    task: {
        id: getLocalStorage("task").id || null,
        name: getLocalStorage("task").name || "",
        description: getLocalStorage("task").description || "",
        answerId: getLocalStorage("task").answerId || null,
        message: getLocalStorage("task").message || "",
        status: getLocalStorage("task").status || "",
    }
};

export const taskSlice = createSlice({
    name: "task",
    initialState,
    reducers: {
        setTask(state, action) {
            state.task = action.payload;
            setLocalStorage("task", action.payload);
        },
        removeTask(state) {
            localStorage.removeItem("task");
            state.task = initialState;
        },
    },
});

export const { setTask, removeTask } = taskSlice.actions;
export default taskSlice.reducer;
