import { AnyAction } from "@reduxjs/toolkit";
import { Dispatch } from "react";
import httpClient from "../shared/httpClient";
import { authActions } from "./auth-slice";

export interface LoginData {
    username: string,
    password: string
}

export const sendLoginRequest = (loginData: LoginData) => {
    return async (dispatch: Dispatch<AnyAction>) => {
        const sendRequest = async () => {
            const response = await httpClient.post('/authorization/login', {
                username: loginData.username,
                password: loginData.password,
            })

            return response.data;
        }

        try {
            const token = await sendRequest();
            dispatch(authActions.login({ token: token }));
        } catch (error: any) {
            // TO DO: show error pop-up
            console.log(error);
            debugger;
        }
    }
}