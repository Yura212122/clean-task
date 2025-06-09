import { createSlice } from "@reduxjs/toolkit";
import { getLocalStorage, setLocalStorage } from "../../utils/localStorage";

const initialState = {
    telegramUrl: getLocalStorage("telegramUrl") || "",
};

export const telegramUrlSlice = createSlice({
    name: "telegramUrl",
    initialState,
    reducers: {
        setTelegramUrl(state, action) {
            state.telegramUrl = action.payload;
            setLocalStorage("telegramUrl", action.payload);
        },
        removeTelegramUrl(state) {
            localStorage.removeItem("telegramUrl");
            state.telegramUrl = initialState;
        },
    }
})

export const { setTelegramUrl, removeTelegramUrl } = telegramUrlSlice.actions;
export default telegramUrlSlice.reducer;