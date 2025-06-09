import axios from "axios";

export const baseUrl = axios.create({
    baseURL: process.env.REACT_APP_API_URL
});

