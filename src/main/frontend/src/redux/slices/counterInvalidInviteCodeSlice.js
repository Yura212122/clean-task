import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    quantityInvalidInviteCode: localStorage.invalid_code === "true" ? localStorage.quantity : 0,
};

export const counterInvalidInviteCodeSlice = createSlice({
    name: "COUNTER_INVALID_INVITE_CODE",
    initialState,
    reducers: {
        addInvalidInviteCode: (state, action) => {
            state.quantityInvalidInviteCode = [state.quantityInvalidInviteCode, action.payload];
            state.quantityInvalidInviteCode.splice(0, 1);
            if (state.quantityInvalidInviteCode[0] > 10) {
                state.quantityInvalidInviteCode = 0;
            }
        },
        removeInvalidInviteCode: (state) => {
            state.quantityInvalidInviteCode = 0;
        }
    }
})
export const { addInvalidInviteCode, removeInvalidInviteCode } = counterInvalidInviteCodeSlice.actions;
export default counterInvalidInviteCodeSlice.reducer;