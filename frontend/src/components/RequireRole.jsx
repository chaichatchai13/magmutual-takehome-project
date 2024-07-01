import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const RequireRole = ({ children, role }) => {
    const { auth } = useContext(AuthContext);
    console.log(role);
    console.log(auth.roles);
    if (!auth.roles.includes(role)) {
        return <Navigate to="/home" />;
    }

    return children;
};

export default RequireRole;
