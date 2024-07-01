import axios from 'axios';
import {getCsrfToken} from "../utils/csrf.js";



// Create an Axios instance
const instance = axios.create({
    baseURL: 'http://localhost:8080', // Set your API base URL
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
