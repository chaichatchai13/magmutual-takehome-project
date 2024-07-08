import axios from 'axios';
import {getCsrfToken} from "../utils/csrf.js";
const  apiUrl = import.meta.env.VITE_APP_API_URL;

// Create an Axios instance
const instance = axios.create({
    baseURL: apiUrl,
    withCredentials: true,
});

// Add a request interceptor to include the CSRF token in headers
instance.interceptors.request.use((config) => {
    const csrfToken = getCsrfToken();
    if (csrfToken) {
        config.headers['X-XSRF-TOKEN'] = csrfToken;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// Add a response interceptor to update the CSRF token in cookies after each response
instance.interceptors.response.use((response) => {
    const csrfToken = response.headers['x-xsrf-token'];
    if (csrfToken) {
        document.cookie = `XSRF-TOKEN=${csrfToken}; path=/;`;
    }
    return response;
}, (error) => {
    return Promise.reject(error);
});

export default instance;
