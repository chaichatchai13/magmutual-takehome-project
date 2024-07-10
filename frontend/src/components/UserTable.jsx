import React, { useContext, useState } from 'react';
import {
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, TablePagination,
    Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, DialogContentText, Alert
} from '@mui/material';
import { AuthContext } from '../context/AuthContext';
import axiosInstance from '../axios/axiosInstance';
import './UserTable.css';

const UserTable = ({ users, pagination, onPageChange, onRowsPerPageChange, fetchUsers }) => {
    const { auth } = useContext(AuthContext);
    const [openAddEditDialog, setOpenAddEditDialog] = useState(false);
    const [editUser, setEditUser] = useState(null);
    const [deleteUserId, setDeleteUserId] = useState(null);
    const [openDeleteDialog, setDeleteEditDialog] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const apiUrl = import.meta.env.VITE_APP_API_URL;
    const [error, setError] = useState('');

    const handleAddClick = () => {
        setEditUser(null);
        setIsEditMode(false);
        setOpenAddEditDialog(true);
        setError('');
    };

    const handleEditClick = (user) => {
        setEditUser({...user,
        dateCreated: (user.dateCreated)?new Date(user.dateCreated).toISOString().split('T')[0] : ''});
        setIsEditMode(true);
        setOpenAddEditDialog(true);
    };

    const handleDeleteClick = async (id) => {
        setDeleteEditDialog(true);
        setDeleteUserId(id);
    };

    const handleDeleteConfirm = async () => {
        try {
            if(deleteUserId !== null){
                await axiosInstance.delete(`${apiUrl}/api/users/${deleteUserId}`, {
                    headers: {
                        Authorization: `Bearer ${auth.token}`,
                    },
                });
                fetchUsers();
                handleDeleteClose();
            }
            else{
                console.error('Delete user is null');
            }
        } catch (error) {
            console.error('Delete failed:', error);
        }
    };

    const handleAddEditDialogClose = () => {
        setOpenAddEditDialog(false);
    };

    const handleDeleteClose = () => {
        setDeleteEditDialog(false);
    };

    const handleSave = async () => {
        const user = {
            id: editUser?.id,
            firstname: editUser.firstname,
            lastname: editUser.lastname,
            email: editUser.email,
            profession: editUser.profession,
            dateCreated: editUser.dateCreated,
            country: editUser.country,
            city: editUser.city,
        };

        try {
            if (isEditMode) {
                await axiosInstance.put(`${apiUrl}/api/users/${editUser.id}`, user, {
                    headers: {
                        Authorization: `Bearer ${auth.token}`,
                    },
                });
            } else {
                await axiosInstance.post(`${apiUrl}/api/users`, user, {
                    headers: {
                        Authorization: `Bearer ${auth.token}`,
                    },
                });
            }
            fetchUsers();
            handleAddEditDialogClose();
        } catch (error) {
            setError(`Save failed: ${error}`);
            console.error('Save failed:', error);
        }
    };

    const handleChangePage = (event, newPage) => {
        onPageChange(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        onRowsPerPageChange(parseInt(event.target.value, 10));
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setEditUser({ ...editUser, [name]: value });
        setError('');
    };

    const todayDate = new Date().toISOString().split('T')[0];

    return (
        <Paper>
            {auth.roles.includes('POST_USERS') && (
                <Button variant="contained" color="primary" onClick={handleAddClick} style={{ margin: '16px' }}>
                    Add User
                </Button>
            )}
            <TableContainer>
                <Table className="user-table">
                    <TableHead>
                        <TableRow>
                            <TableCell sx={{ fontWeight: 'bold', backgroundColor: '#f5f5f5' }}>ID</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', backgroundColor: '#f5f5f5' }}>First Name</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', backgroundColor: '#f5f5f5' }}>Last Name</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', backgroundColor: '#f5f5f5' }}>Email</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', backgroundColor: '#f5f5f5' }}>Profession</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', backgroundColor: '#f5f5f5' }}>Date Created</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', backgroundColor: '#f5f5f5' }}>Country</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', backgroundColor: '#f5f5f5' }}>City</TableCell>
                            {(auth.roles.includes('PUT_USERS') || auth.roles.includes('DELETE_USERS')) && (
                                <TableCell sx={{ fontWeight: 'bold', backgroundColor: '#f5f5f5' }}>Actions</TableCell>
                            )}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {users.map((user) => (
                            <TableRow key={user.id}>
                                <TableCell>{user.id}</TableCell>
                                <TableCell>{user.firstname}</TableCell>
                                <TableCell>{user.lastname}</TableCell>
                                <TableCell>{user.email}</TableCell>
                                <TableCell>{user.profession}</TableCell>
                                <TableCell>{new Date(user.dateCreated).toLocaleDateString()}</TableCell>
                                <TableCell>{user.country}</TableCell>
                                <TableCell>{user.city}</TableCell>
                                {(auth.roles.includes('PUT_USERS') || auth.roles.includes('DELETE_USERS')) && (
                                    <TableCell>
                                        {auth.roles.includes('PUT_USERS') && (
                                            <Button
                                                variant="contained"
                                                color="warning"
                                                onClick={() => handleEditClick(user)}
                                                style={{ marginRight: '8px' }}
                                            >
                                                Edit
                                            </Button>
                                        )}
                                        {auth.roles.includes('DELETE_USERS') && (
                                            <Button
                                                variant="contained"
                                                color="secondary"
                                                onClick={() => handleDeleteClick(user.id)}
                                            >
                                                Delete
                                            </Button>
                                        )}
                                    </TableCell>
                                )}
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <TablePagination
                rowsPerPageOptions={[10, 25, 50]}
                component="div"
                count={pagination.total}
                rowsPerPage={pagination.limit}
                page={Math.floor(pagination.offset / pagination.limit)}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
            />

            <Dialog open={openAddEditDialog} onClose={handleAddEditDialogClose}>
                <DialogTitle>{isEditMode ? 'Edit User' : 'Add User'}</DialogTitle>
                <DialogContent>
                    <TextField
                        margin="dense"
                        id="id"
                        name="id"
                        label="ID"
                        type="text"
                        fullWidth
                        value={editUser?.id || ''}
                        onChange={handleInputChange}
                        disabled={isEditMode}
                    />
                    <TextField
                        margin="dense"
                        id="firstname"
                        name="firstname"
                        label="First Name"
                        type="text"
                        fullWidth
                        value={editUser?.firstname || ''}
                        onChange={handleInputChange}
                    />
                    <TextField
                        margin="dense"
                        id="lastname"
                        name="lastname"
                        label="Last Name"
                        type="text"
                        fullWidth
                        value={editUser?.lastname || ''}
                        onChange={handleInputChange}
                    />
                    <TextField
                        margin="dense"
                        id="email"
                        name="email"
                        label="Email"
                        type="email"
                        fullWidth
                        value={editUser?.email || ''}
                        onChange={handleInputChange}
                    />
                    <TextField
                        margin="dense"
                        id="profession"
                        name="profession"
                        label="Profession"
                        type="text"
                        fullWidth
                        value={editUser?.profession || ''}
                        onChange={handleInputChange}
                    />
                    <TextField
                        margin="dense"
                        id="dateCreated"
                        name="dateCreated"
                        label="Date Created"
                        type="date"
                        fullWidth
                        value={editUser?.dateCreated || ''}
                        onChange={handleInputChange}
                        inputProps={{max : todayDate}}
                        InputLabelProps={{
                            shrink: true,
                        }}
                    />
                    <TextField
                        margin="dense"
                        id="country"
                        name="country"
                        label="Country"
                        type="text"
                        fullWidth
                        value={editUser?.country || ''}
                        onChange={handleInputChange}
                    />
                    <TextField
                        margin="dense"
                        id="city"
                        name="city"
                        label="City"
                        type="text"
                        fullWidth
                        value={editUser?.city || ''}
                        onChange={handleInputChange}
                    />
                </DialogContent>
                {error && <Alert severity="error">{error}</Alert>}
                <DialogActions>
                    <Button onClick={handleAddEditDialogClose} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={handleSave} color="primary">
                        Save
                    </Button>
                </DialogActions>
            </Dialog>
            <Dialog open={openDeleteDialog} onClose={handleDeleteClose}>
                <DialogTitle>Confirm Delete</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete user #{deleteUserId}?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleDeleteClose}>Cancel</Button>
                    <Button onClick={ handleDeleteConfirm} color="primary">Confirm</Button>
                </DialogActions>
            </Dialog>
        </Paper>
    );
};

export default UserTable;
