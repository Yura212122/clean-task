import { createSlice } from "@reduxjs/toolkit";
import {getLocalStorage, setLocalStorage} from "../../utils/localStorage";

const initialState = {
    isExpired: getLocalStorage("isExpired") || true,
}

export const isInviteCodeExpiredSlice = createSlice({
    name: "isExpired",
    initialState,
    reducers: {
        setIsExpired(state, action) {
            state.isExpired = action.payload;
            setLocalStorage("isExpired", action.payload);

        },
        removeIsExpired(state) {
            localStorage.removeItem("isExpired");
            state.isExpired = initialState;
        }
    }
})

export const { setIsExpired, removeIsExpired } = isInviteCodeExpiredSlice.actions;

export default isInviteCodeExpiredSlice.reducer;