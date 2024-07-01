import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { List, ListItem, ListItemIcon, ListItemText } from '@mui/material';
import HomeIcon from '@mui/icons-material/Home';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import { AuthContext } from '../context/AuthContext';

const NavigationDrawer = () => {
    const { auth } = useContext(AuthContext);
    console.log(auth.roles);

    return (
        <List>
            <ListItem button component={Link} to="/home">
                <ListItemIcon>
                    <HomeIcon />
                </ListItemIcon>
                <ListItemText primary="Home" />
            </ListItem>
            {auth.roles.includes('POST_USERS') && (
                <ListItem button component={Link} to="/admin">
                    <ListItemIcon>
                        <AdminPanelSettingsIcon />
                    </ListItemIcon>
                    <ListItemText primary="Admin" />
                </ListItem>
            )}
        </List>
    );
};

export default NavigationDrawer;
