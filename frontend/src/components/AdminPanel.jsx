// src/components/AdminPanel.jsx
import React, { useContext, useState } from 'react';
import axiosInstance from '../axios/axiosInstance';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { Container, Box, Typography, Button, TextField, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material';
import { getCsrfToken } from '../utils/csrf';

const AdminPanel = () => {
    const { auth } = useContext(AuthContext);
    const [file, setFile] = useState(null);
    const [open, setOpen] = useState(false);
    const navigate = useNavigate();

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleUpload = async () => {
        const formData = new FormData();
        formData.append('file', file);

        try {
            const csrfToken = getCsrfToken();
            console.log(csrfToken);
            const response = await axiosInstance.post('http://localhost:8080/api/users/upload', formData, {
                headers: {
                    Authorization: `Bearer ${auth.token}`,
                    'Content-Type': 'multipart/form-data',
                    'X-XSRF-TOKEN': csrfToken,
                },
            });
            if (response.status === 200) {
                alert('File uploaded successfully.');
                navigate('/home');
            }
        } catch (error) {
            console.error('File upload failed:', error);
        }
        setOpen(false);
    };

    return (
        <Container>
            <Box sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Admin Panel
                </Typography>
                <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2 }}>
                    <Typography variant="h6">Upload CSV:</Typography>
                    <TextField type="file" onChange={handleFileChange} />
                    <Button variant="contained" onClick={handleClickOpen}>Upload</Button>
                </Box>
            </Box>

            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>Confirm Upload</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to upload this file?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose}>Cancel</Button>
                    <Button onClick={handleUpload} color="primary">Confirm</Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default AdminPanel;
