import React, { useContext, useState, useEffect } from 'react';
import { Container, Box, TextField, Typography } from '@mui/material';
import axiosInstance from '../axios/axiosInstance';
import { AuthContext } from '../context/AuthContext';
import UserTable from './UserTable';

const Home = () => {
    const { auth } = useContext(AuthContext);
    const [users, setUsers] = useState([]);
    const [filters, setFilters] = useState({ startDate: '', endDate: '', profession: '' });
    const [pagination, setPagination] = useState({ offset: 0, limit: 10, total: 0 });
    const [searchId, setSearchId] = useState('');
    const  apiUrl = import.meta.env.VITE_APP_API_URL;
    useEffect(() => {
        if (searchId) {
            handleSearchById(searchId);
        } else {
            fetchUsers();
        }
    }, [filters, pagination.offset, pagination.limit, searchId, auth.token]);

    const fetchUsers = async () => {
        const params = {
            offset: pagination.offset,
            limit: pagination.limit,
        };

        if (filters.startDate) {
            params.startDate = filters.startDate;
        }
        if (filters.endDate) {
            params.endDate = filters.endDate;
        }
        if (filters.profession) {
            params.profession = filters.profession;
        }

        try {
            const response = await axiosInstance.get(`${apiUrl}/api/users`, {
                params,
                headers: {
                    Authorization: `Bearer ${auth.token}`,
                },
            });
            setUsers(response.data.content);
            setPagination({ ...pagination, total: response.data.totalElements });
        } catch (error) {
            console.error('Failed to fetch users:', error);
        }
    };

    const handleFilterChange = (e) => {
        setSearchId('');
        setFilters({ ...filters, [e.target.name]: e.target.value });
    };

    const handlePageChange = (newPage) => {
        setPagination({ ...pagination, offset: newPage * pagination.limit });
    };

    const handleRowsPerPageChange = (newRowsPerPage) => {
        setPagination({ ...pagination, limit: newRowsPerPage, offset: 0 });
    };

    const handleSearchIdChange = (e) => {
        const newSearchId = e.target.value;
        setSearchId(newSearchId);
        if (newSearchId.trim() === '') {
            setFilters({ startDate: '', endDate: '', profession: '' });
        } else {
            setFilters({ startDate: '', endDate: '', profession: '' });
        }
    };

    const handleSearchById = async (id) => {
        if (id.trim() === '') return;

        try {
            const response = await axiosInstance.get(`${apiUrl}/api/users/${id}`, {
                headers: {
                    Authorization: `Bearer ${auth.token}`,
                },
            });
            setUsers([response.data]);
            setPagination({ ...pagination, total: 1 });
        } catch (error) {
            console.error('Failed to fetch user by ID:', error);
            setUsers([]);
            setPagination({ ...pagination, total: 0 });
        }
    };

    return (
        <Container>
            <Box sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    MagMutual User Portal
                </Typography>
                <Box sx={{ display: 'flex', flexDirection: 'row', gap: 2, marginBottom: 4 }}>
                    <TextField
                        label="Search by ID"
                        type="text"
                        value={searchId}
                        onChange={handleSearchIdChange}
                    />
                    <TextField
                        label="Start Date"
                        type="date"
                        name="startDate"
                        value={filters.startDate}
                        onChange={handleFilterChange}
                        InputLabelProps={{ shrink: true }}
                    />
                    <TextField
                        label="End Date"
                        type="date"
                        name="endDate"
                        value={filters.endDate}
                        onChange={handleFilterChange}
                        InputLabelProps={{ shrink: true }}
                    />
                    <TextField
                        label="Profession"
                        type="text"
                        name="profession"
                        value={filters.profession}
                        onChange={handleFilterChange}
                    />
                </Box>
                <UserTable
                    users={users}
                    pagination={pagination}
                    onPageChange={handlePageChange}
                    onRowsPerPageChange={handleRowsPerPageChange}
                    fetchUsers={fetchUsers}
                />
            </Box>
        </Container>
    );
};

export default Home;
