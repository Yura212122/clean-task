import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    timer: [],
};

export const timerSlice = createSlice({
    name: "TIMER",
    initialState,
    reducers: {
        addTimer: (state, action) => {
            state.timer = action.payload;
        }
    }
});
export const { addTimer } = timerSlice.actions;
export default timerSlice.reducer;