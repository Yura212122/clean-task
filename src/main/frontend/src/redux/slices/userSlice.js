import { createSlice } from "@reduxjs/toolkit";
import { getLocalStorage, setLocalStorage } from "../../utils/localStorage";

const initialState = {
    user: getLocalStorage("user")||{},
};

export const userSlice = createSlice({
    name: "user",
    initialState,
    reducers: {
        setUser(state, action) {
            state.user = action.payload;
            setLocalStorage("user", action.payload);
        },
        removeUser(state) {
            localStorage.removeItem("user");
            state.user = initialState;
        },
    },
});

export const { setUser, removeUser } = userSlice.actions;
export default userSlice.reducer;