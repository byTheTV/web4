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
import { logoutFromKeycloak } from '../store/slices/authSlice';
import { checkPoint, fetchResults } from '../store/slices/resultSlice';
import AreaCanvas from '../components/AreaCanvas';
import keycloak from '../keycloak';
import './MainPage.css';

const MainPage = () => {
  const [x, setX] = useState(null);
  const [y, setY] = useState('');
  const [r, setR] = useState(null);
  const [xSuggestions, setXSuggestions] = useState([]);
  const [rSuggestions, setRSuggestions] = useState([]);
  const [yError, setYError] = useState('');
  const [maxRadius, setMaxRadius] = useState(null);
  
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { username, isAuthenticated } = useSelector(state => state.auth);
  const { results, loading, error } = useSelector(state => state.result);


  const allowedX = ['-2', '-1.5', '-1', '-0.5', '0', '0.5', '1', '1.5', '2'];
  const allowedR = ['-2', '-1.5', '-1', '-0.5', '0', '0.5', '1', '1.5', '2'];

  useEffect(() => {
    if (isAuthenticated) {
      dispatch(fetchResults());

      // Получаем maxRadius из токена
      const maxRadiusClaim = keycloak.tokenParsed?.maxRadius;
      if (maxRadiusClaim) {
        if (typeof maxRadiusClaim === 'number') {
          setMaxRadius(maxRadiusClaim);
        } else if (typeof maxRadiusClaim === 'string') {
          try {
            setMaxRadius(parseFloat(maxRadiusClaim));
          } catch (e) {
            console.warn('Invalid maxRadius in token:', maxRadiusClaim);
          }
        }
      }
    }
  }, [dispatch, isAuthenticated]);

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
      return value.value || value.label || String(value);
    }
    return String(value);
  };

  const validateY = (value) => {
    if (!value || value.trim() === '') {
      setYError('Y обязателен для ввода');
      return false;
    }

    const normalized = value.replace(',', '.').trim();

    const decimalRegex = /^[+-]?\d+(\.\d+)?$/;
    if (!decimalRegex.test(normalized)) {
      setYError('Y должен быть числом');
      return false;
    }

    const withoutSign =
      normalized[0] === '-' || normalized[0] === '+'
        ? normalized.slice(1)
        : normalized;

    let [intPartRaw, fracPartRaw = ''] = withoutSign.split('.');

    let intPart = intPartRaw.replace(/^0+(?=\d)/, '');
    if (intPart === '') intPart = '0';

    const fracPart = fracPartRaw.replace(/0+$/, '');

    if (intPart.length > 1) {
      setYError('Y должен быть в диапазоне от -3 до 3');
      return false;
    }

    if (intPart < '3') {
      setYError('');
      return true;
    }

    if (intPart > '3') {
      setYError('Y должен быть в диапазоне от -3 до 3');
      return false;
    }

    if (fracPart && fracPart.length > 0) {
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
    
    if (!allowedX.includes(xValue)) {
      alert(`X должно быть одним из: ${allowedX.join(', ')}`);
      return;
    }

    if (!allowedR.includes(rValue)) {
      alert(`R должно быть одним из: ${allowedR.join(', ')}`);
      return;
    }

    const rNum = parseFloat(rValue.replace(',', '.'));
    if (isNaN(rNum) || !isFinite(rNum) || rNum <= 0) {
      alert('R должен быть положительным числом из указанного набора. Хоть в задании написано, что R может быть отрицательным, но отрицательные радиусы не существуют в нашем случае');
      return;
    }

    // Проверка maxRadius
    if (maxRadius !== null && rNum > maxRadius) {
      alert(`R (${rNum}) превышает максимально допустимое значение (${maxRadius})`);
      return;
    }

    const xNum = parseFloat(xValue.replace(',', '.'));
    const yNum = parseFloat(y.replace(',', '.'));
    
    await dispatch(checkPoint({ x: xNum, y: yNum, r: rNum }));
    await dispatch(fetchResults());
  };

  const handleLogout = () => {
    dispatch(logoutFromKeycloak());
    navigate('/');
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
    const yStr = canvasY.toFixed(3).replace(/\.?0+$/,''); 
    setY(yStr);
    validateY(yStr);
  };

  const formatTimestamp = (timestamp) => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    return date.toLocaleString('ru-RU');
  };

  const studentInfo = {
    name: 'Тарасов Владислав Павлович',
    group: 'P3219',
    variant: 'Вариант 8765'
  };

  return (
    <div className="main-page">
      <header className="main-header">
        <h1>
          {studentInfo.name}, группа {studentInfo.group}, {studentInfo.variant}
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
                    // Разрешаем промежуточные значения (например, только "-")
                    const normalized = normalizeValue(e.value);
                    setX(normalized);
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
                    // Разрешаем промежуточные значения, валидация — при сабмите
                    const normalized = normalizeValue(e.value);
                    setR(normalized);
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
                disabled={
                  loading ||
                  !x ||
                  !y ||
                  !r ||
                  !!yError ||
                  !allowedX.includes(normalizeValue(x) || '') ||
                  !allowedR.includes(normalizeValue(r) || '')
                }
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

