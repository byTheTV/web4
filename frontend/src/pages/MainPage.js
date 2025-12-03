import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { AutoComplete } from 'primereact/autocomplete';
import { InputText } from 'primereact/inputtext';
import { Button } from 'primereact/button';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Card } from 'primereact/card';
import { Message } from 'primereact/message';
import { logout } from '../store/slices/authSlice';
import { checkPoint, fetchResults } from '../store/slices/resultSlice';
import AreaCanvas from '../components/AreaCanvas';
import './MainPage.css';

const MainPage = () => {
  const [x, setX] = useState(null);
  const [y, setY] = useState('');
  const [r, setR] = useState(null);
  const [xSuggestions, setXSuggestions] = useState([]);
  const [rSuggestions, setRSuggestions] = useState([]);
  const [yError, setYError] = useState('');
  
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { username } = useSelector(state => state.auth);
  const { results, loading, error } = useSelector(state => state.result);

  const allowedX = ['-2', '-1.5', '-1', '-0.5', '0', '0.5', '1', '1.5', '2'];
  const allowedR = ['-2', '-1.5', '-1', '-0.5', '0', '0.5', '1', '1.5', '2'];

  useEffect(() => {
    dispatch(fetchResults());
  }, [dispatch]);

  const searchX = (event) => {
    const query = event.query.toLowerCase();
    const filtered = allowedX.filter(item => 
      item.toLowerCase().includes(query)
    );
    setXSuggestions(filtered);
  };

  const searchR = (event) => {
    const query = event.query.toLowerCase();
    const filtered = allowedR.filter(item => 
      item.toLowerCase().includes(query)
    );
    setRSuggestions(filtered);
  };

  const normalizeValue = (value) => {
    if (value === null || value === undefined) return null;
    if (typeof value === 'string') return value;
    if (typeof value === 'number') return String(value);
    if (typeof value === 'object' && value !== null) {
      // Если это объект, попробуем извлечь значение
      return value.value || value.label || String(value);
    }
    return String(value);
  };

  const validateY = (value) => {
    if (!value || value.trim() === '') {
      setYError('Y обязателен для ввода');
      return false;
    }
    
    const numValue = parseFloat(value);
    if (isNaN(numValue)) {
      setYError('Y должен быть числом');
      return false;
    }
    
    if (numValue < -3 || numValue > 3) {
      setYError('Y должен быть в диапазоне от -3 до 3');
      return false;
    }
    
    setYError('');
    return true;
  };

  const handleYChange = (e) => {
    const value = e.target.value;
    setY(value);
    if (value) {
      validateY(value);
    } else {
      setYError('');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const xValue = normalizeValue(x);
    const rValue = normalizeValue(r);
    
    if (!xValue || xValue === null || xValue === undefined) {
      return;
    }
    
    if (!validateY(y)) {
      return;
    }
    
    if (!rValue || rValue === null || rValue === undefined) {
      return;
    }
    
    // Проверяем, что значения в списке допустимых
    if (!allowedX.includes(xValue)) {
      alert(`X должно быть одним из: ${allowedX.join(', ')}`);
      return;
    }
    
    if (!allowedR.includes(rValue)) {
      alert(`R должно быть одним из: ${allowedR.join(', ')}`);
      return;
    }
    
    const xNum = parseFloat(xValue);
    const yNum = parseFloat(y);
    const rNum = parseFloat(rValue);
    
    await dispatch(checkPoint({ x: xNum, y: yNum, r: rNum }));
    await dispatch(fetchResults());
  };

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  const handleCanvasClick = (canvasX, canvasY) => {
    // Find closest X value
    const xNum = parseFloat(canvasX);
    let closestX = allowedX[0];
    let minDist = Math.abs(xNum - parseFloat(closestX));
    for (const val of allowedX) {
      const dist = Math.abs(xNum - parseFloat(val));
      if (dist < minDist) {
        minDist = dist;
        closestX = val;
      }
    }
    setX(closestX);
    const yStr = canvasY.toString();
    setY(yStr);
    validateY(yStr);
  };

  const formatTimestamp = (timestamp) => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    return date.toLocaleString('ru-RU');
  };

  const studentInfo = {
    name: 'Иванов Иван Иванович',
    group: 'P3215',
    variant: 'Вариант 12345'
  };

  return (
    <div className="main-page">
      <header className="main-header">
        <h1>
          {studentInfo.name} — группа {studentInfo.group} — {studentInfo.variant}
        </h1>
        <div className="header-actions">
          <span className="username">Пользователь: {username}</span>
          <Button 
            label="Выход" 
            onClick={handleLogout}
            severity="secondary"
            size="small"
          />
        </div>
      </header>
      
      <main className="main-content">
        <div className="content-grid">
          <Card className="form-card">
            <h2>Проверка попадания точки</h2>
            
            {error && (
              <Message 
                severity="error" 
                text={typeof error === 'string' ? error : (error?.message || JSON.stringify(error))} 
                className="error-message" 
              />
            )}
            
            <form onSubmit={handleSubmit} className="check-form">
              <div className="form-field">
                <label htmlFor="x">X:</label>
                <AutoComplete
                  id="x"
                  value={normalizeValue(x) || ''}
                  suggestions={xSuggestions}
                  completeMethod={searchX}
                  onChange={(e) => {
                    const normalized = normalizeValue(e.value);
                    // Проверяем, что значение в списке допустимых
                    if (normalized && !allowedX.includes(normalized)) {
                      // Если значение не в списке, сбрасываем
                      setX(null);
                    } else {
                      setX(normalized);
                    }
                  }}
                  placeholder="Выберите значение"
                  required
                  className="form-input"
                />
                <small>Допустимые значения: {allowedX.join(', ')}</small>
              </div>
              
              <div className="form-field">
                <label htmlFor="y">Y:</label>
                <InputText
                  id="y"
                  value={y}
                  onChange={handleYChange}
                  onBlur={() => validateY(y)}
                  placeholder="Введите значение"
                  required
                  className={yError ? 'form-input p-invalid' : 'form-input'}
                />
                {yError && <small className="p-error">{yError}</small>}
                <small className="hint-text">Диапазон: от -3 до 3</small>
              </div>
              
              <div className="form-field">
                <label htmlFor="r">R:</label>
                <AutoComplete
                  id="r"
                  value={normalizeValue(r) || ''}
                  suggestions={rSuggestions}
                  completeMethod={searchR}
                  onChange={(e) => {
                    const normalized = normalizeValue(e.value);
                    // Проверяем, что значение в списке допустимых
                    if (normalized && !allowedR.includes(normalized)) {
                      // Если значение не в списке, сбрасываем
                      setR(null);
                    } else {
                      setR(normalized);
                    }
                  }}
                  placeholder="Выберите значение"
                  required
                  className="form-input"
                />
                <small>Допустимые значения: {allowedR.join(', ')}</small>
              </div>
              
              <Button
                type="submit"
                label="Проверить"
                loading={loading}
                disabled={loading || x === null || !y || r === null || !!yError}
                className="submit-button"
              />
            </form>
          </Card>
          
          <Card className="canvas-card">
            <h2>Область</h2>
            <AreaCanvas
              r={r ? Math.abs(parseFloat(normalizeValue(r) || '1')) : 1}
              results={results}
              onCanvasClick={handleCanvasClick}
            />
            <p className="canvas-hint">
              Кликните по графику для выбора координат точки
            </p>
          </Card>
        </div>
        
        <Card className="results-card">
          <h2>История проверок</h2>
          <DataTable
            value={results}
            loading={loading}
            emptyMessage="Нет результатов"
            className="results-table"
            responsiveLayout="scroll"
          >
            <Column 
              field="executionTime" 
              header="Время выполнения"
              body={(rowData) => rowData.executionTime || ''}
            />
            <Column 
              field="x" 
              header="X"
              body={(rowData) => String(rowData.x || '')}
            />
            <Column 
              field="y" 
              header="Y"
              body={(rowData) => String(rowData.y || '')}
            />
            <Column 
              field="r" 
              header="R"
              body={(rowData) => String(rowData.r || '')}
            />
            <Column 
              field="hit" 
              header="Попадание"
              body={(rowData) => rowData.hit ? 'Да' : 'Нет'}
            />
            <Column 
              field="timestamp" 
              header="Время"
              body={(rowData) => formatTimestamp(rowData.timestamp)}
            />
          </DataTable>
        </Card>
      </main>
    </div>
  );
};

export default MainPage;

