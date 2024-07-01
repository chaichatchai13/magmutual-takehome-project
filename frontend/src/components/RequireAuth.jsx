// src/components/RequireAuth.jsx
import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const RequireAuth = ({ children }) => {
    const { auth } = useContext(AuthContext);

    if (!auth.token) {
        return <Navigate to="/login" />;
    }

    return children;
};

export default RequireAuth;
