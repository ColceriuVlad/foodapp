import { createSlice } from "@reduxjs/toolkit";

const authSlice = createSlice({
    name: 'auth',
    initialState: {
        isLoggedIn: localStorage.getItem('token') !== null && localStorage.getItem('token') !== undefined,
        token: localStorage.getItem('token')
    },
    reducers: {
        login(state, action: any) {
            state.isLoggedIn = true;
            state.token = action.payload.token;
            localStorage.setItem('token', action.payload.token);
        },
        logout(state) {
            state.isLoggedIn = false;
            state.token = null;
            localStorage.removeItem('token');
        }
    }
});

export const authActions = authSlice.actions;

export default authSlice;