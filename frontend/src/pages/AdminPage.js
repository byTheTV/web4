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
  
  // Инициализируем сегодняшней датой (локальное время)
  const getTodayDate = () => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return today;
  };
  
  const [selectedDate, setSelectedDate] = useState(getTodayDate());

  const loadStats = () => {
    // Используем локальную дату без преобразований в UTC
    const year = selectedDate.getFullYear();
    const month = String(selectedDate.getMonth() + 1).padStart(2, '0');
    const day = String(selectedDate.getDate()).padStart(2, '0');
    const date = `${year}-${month}-${day}`;
    
    console.log('Loading stats for date:', date);
    dispatch(fetchAdminStats(date));
  };

  useEffect(() => {
    // Загрузка статистики за сегодня
    if (isAuthenticated) {
      console.log('Admin page mounted, loading stats...');
      loadStats();
    }
  }, [isAuthenticated]);

  const handleDateChange = (e) => {
    if (e.value) {
    setSelectedDate(e.value);
      // Используем локальную дату
      const year = e.value.getFullYear();
      const month = String(e.value.getMonth() + 1).padStart(2, '0');
      const day = String(e.value.getDate()).padStart(2, '0');
      const dateStr = `${year}-${month}-${day}`;
      
      console.log('Date changed to:', dateStr);
      dispatch(fetchAdminStats(dateStr));
    } else {
      setSelectedDate(getTodayDate());
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
          
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.5rem' }}>
            <label htmlFor="date">Дата:</label>
            <Calendar
              id="date"
              value={selectedDate}
              onChange={handleDateChange}
              dateFormat="yy-mm-dd"
              showIcon
            />
            <Button
              label="Обновить"
              icon="pi pi-refresh"
              onClick={loadStats}
              loading={loading}
              size="small"
            />
          </div>

          {error && (
            <Message 
              severity="error" 
              text={typeof error === 'string' ? error : (error?.message || JSON.stringify(error))} 
              style={{ marginBottom: '1rem' }}
            />
          )}
          
          <DataTable
            value={stats}
            loading={loading}
            emptyMessage="Нет данных за выбранную дату"
            className="results-table"
            responsiveLayout="scroll"
            sortField="checkCount"
            sortOrder={-1}
            showGridlines
          >
            <Column 
              field="username" 
              header="Пользователь"
              body={(rowData) => rowData.username || 'N/A'}
              sortable
            />
            <Column 
              field="keycloakId" 
              header="Keycloak ID"
              sortable
              style={{ fontSize: '0.85em', color: '#666' }}
            />
            <Column 
              field="checkCount" 
              header="Проверок"
              body={(rowData) => <span style={{ fontWeight: 'bold' }}>{rowData.checkCount || 0}</span>}
              sortable
              style={{ textAlign: 'center' }}
            />
          </DataTable>
        </Card>
      </main>
    </div>
  );
};

export default AdminPage;
