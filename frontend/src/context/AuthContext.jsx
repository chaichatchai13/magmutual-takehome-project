import React, { createContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext();

const AuthProvider = ({ children }) => {
    const [auth, setAuth] = useState({ token: null, roles: [] });

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            const decodedToken = jwtDecode(token);
            const roles = decodedToken.authorities || [];
            setAuth({ token, roles });
        }
    }, []);

    const login = (jwt) => {
        const decodedToken = jwtDecode(jwt);
        const roles = decodedToken.authorities || [];
        localStorage.setItem('token', jwt);
        setAuth({ token: jwt, roles });
    };

    const logout = () => {
        localStorage.removeItem('token');
        setAuth({ token: null, roles: [] });
    };

    const isAuthenticated = !!auth.token;

    return (
        <AuthContext.Provider value={{ auth, login, logout, isAuthenticated }}>
            {children}
        </AuthContext.Provider>
    );
};

export { AuthContext, AuthProvider };
