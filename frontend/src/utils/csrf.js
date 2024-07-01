// src/utils/csrf.js
export const getCsrfToken = () => {
    const match = document.cookie.match(new RegExp('(^| )XSRF-TOKEN=([^;]+)'));
    if (match) {
        return match[2];
    }
    return null;
};
