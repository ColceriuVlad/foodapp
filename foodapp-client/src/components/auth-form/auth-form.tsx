import { Box, Button, Stack, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { RootState } from "../../store";
import { LoginData, sendLoginRequest } from "../../store/auth-actions";
import { useAppDispatch, useAppSelector } from "../hooks/hooks";

function AuthForm(props: any) {
    const [userName, setUserName] = useState("");
    const [password, setPassword] = useState("");
    const dispatch = useAppDispatch();
    const isLoggedIn = useAppSelector((state: RootState) => state.auth.isLoggedIn);
    const navigate = useNavigate();

    useEffect(() => {
        if (isLoggedIn) {
            navigate('/');
        }
    }, [isLoggedIn, navigate]);

    const loginHandler = () => {
        const loginData: LoginData = {
            username: userName,
            password: password
        }

        dispatch(sendLoginRequest(loginData));
    }

    const usernameChangeHandler = (event: React.ChangeEvent<HTMLInputElement>) => {
        setUserName(event.target.value);
    }

    const passwordChangeHandler = (event: React.ChangeEvent<HTMLInputElement>) => {
        setPassword(event.target.value);
    }

    return (
        <Box sx={{ height: '100vh' }} >
            <Stack spacing={2}>
                <Typography textAlign="center" variant="h4" >
                    Login
                </Typography>
                <TextField
                    required
                    id="username"
                    label="Username"
                    variant="filled"
                    value={userName}
                    onChange={usernameChangeHandler}
                />
                <TextField
                    required
                    id="password"
                    label="Password"
                    variant="filled"
                    type="password"
                    value={password}
                    onChange={passwordChangeHandler}
                />
                <Button onClick={loginHandler} variant="contained">Login</Button>
            </Stack>
        </Box >
    );
}

export default AuthForm;
