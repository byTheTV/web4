import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Card } from 'primereact/card';
import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import { Calendar } from 'primereact/calendar';
import { Message } from 'primereact/message';
import { logoutFromKeycloak } from '../store/slices/authSlice';
import { fetchAdminStats } from '../store/slices/adminSlice';
import keycloak from '../keycloak';
import './MainPage.css';

const AdminPage = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { username, isAuthenticated } = useSelector(state => state.auth);
  const { stats, loading, error } = useSelector(state => state.admin);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [dateString, setDateString] = useState('');

  useEffect(() => {
    // Загрузка статистики за сегодня
    if (isAuthenticated) {
      loadStats();
    }
  }, [dispatch, isAuthenticated]);

  const loadStats = () => {
    const date = dateString || selectedDate.toISOString().split('T')[0];
    dispatch(fetchAdminStats(date));
  };

  const handleDateChange = (e) => {
    setSelectedDate(e.value);
    if (e.value) {
      const dateStr = e.value.toISOString().split('T')[0];
      setDateString(dateStr);
      dispatch(fetchAdminStats(dateStr));
    } else {
      setDateString('');
      loadStats();
    }
  };

  const handleLogout = () => {
    dispatch(logoutFromKeycloak());
    navigate('/');
  };

  return (
    <div className="main-page">
      <header className="main-header">
        <h1>Панель администратора</h1>
        <div className="header-actions">
          <span className="username">Администратор: {username}</span>
          <Button 
            label="Выход" 
            onClick={handleLogout}
            severity="secondary"
            size="small"
          />
        </div>
      </header>
      
      <main className="main-content">
        <Card className="form-card">
          <h2>Статистика проверок по пользователям</h2>
          
          <div style={{ marginBottom: '1rem' }}>
            <label htmlFor="date" style={{ marginRight: '0.5rem' }}>Дата:</label>
            <Calendar
              id="date"
              value={selectedDate}
              onChange={handleDateChange}
              dateFormat="yy-mm-dd"
              showIcon
            />
            <Button
              label="Обновить"
              onClick={loadStats}
              style={{ marginLeft: '0.5rem' }}
              loading={loading}
            />
          </div>

          {error && (
            <Message 
              severity="error" 
              text={typeof error === 'string' ? error : (error?.message || JSON.stringify(error))} 
              className="error-message" 
            />
          )}
          
          <DataTable
            value={stats}
            loading={loading}
            emptyMessage="Нет данных за выбранную дату"
            className="results-table"
            responsiveLayout="scroll"
          >
            <Column 
              field="username" 
              header="Имя пользователя"
              body={(rowData) => rowData.username || rowData.keycloakId || 'N/A'}
            />
            <Column 
              field="keycloakId" 
              header="Keycloak ID"
            />
            <Column 
              field="checkCount" 
              header="Количество проверок"
              body={(rowData) => String(rowData.checkCount || 0)}
            />
          </DataTable>
        </Card>
      </main>
    </div>
  );
};

export default AdminPage;
