import React, { useContext } from 'react';
import {BrowserRouter as Router, Route, Routes, Navigate, useLocation} from 'react-router-dom';
import {CssBaseline, Box } from '@mui/material';
import SignIn from './components/SignIn';
import Home from './components/Home';
import AdminPanel from './components/AdminPanel';
import { AuthProvider, AuthContext } from './context/AuthContext';
import RequireAuth from './components/RequireAuth';
import RequireRole from './components/RequireRole';
import NavigationDrawer from './components/NavigationDrawer';

const AppContent = () => {
    const { isAuthenticated } = useContext(AuthContext);
    const location = useLocation();
    const isLogInRoute = location.pathname === '/login';
    return (
        <Box sx={{ display: 'flex' , position: 'relative', height: '100vh' }}>
            {!isLogInRoute && isAuthenticated && <NavigationDrawer />}
            <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
                <Routes>
                    <Route path="/login" element={<SignIn />} />
                    <Route
                        path="/home"
                        element={
                            <RequireAuth>
                                <Home />
                            </RequireAuth>
                        }
                    />
                    <Route
                        path="/admin"
                        element={
                            <RequireAuth>
                                <RequireRole role="POST_USERS">
                                    <AdminPanel />
                                </RequireRole>
                            </RequireAuth>
                        }
                    />
                    <Route
                        path="/"
                        element={isAuthenticated ? <Navigate to="/home" /> : <Navigate to="/login" />}
                    />
                </Routes>
            </Box>
        </Box>
    );
};

function App() {
    return (
            <AuthProvider>
                <CssBaseline />
                <Router>
                    <AppContent />
                </Router>
            </AuthProvider>
    );
}

export default App;
