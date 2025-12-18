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
  const [allowedR, setAllowedR] = useState([]);
  
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { username, isAuthenticated } = useSelector(state => state.auth);
  const { results, loading, error } = useSelector(state => state.result);

  const allowedX = ['-2', '-1.5', '-1', '-0.5', '0', '0.5', '1', '1.5', '2'];

  // Генерация допустимых значений R на основе maxRadius
  const generateAllowedR = (maxR) => {
    if (!maxR || maxR <= 0) return ['0.5', '1', '1.5', '2'];
    
    const values = [];
    const step = 0.5;
    
    // Генерируем значения от 0.5 до maxRadius с шагом 0.5
    for (let i = step; i <= maxR; i += step) {
      values.push(i.toFixed(1));
    }
    
    // Добавляем само значение maxRadius если его нет в списке
    const maxRStr = maxR.toFixed(1);
    if (!values.includes(maxRStr) && maxR > 0) {
      values.push(maxRStr);
      values.sort((a, b) => parseFloat(a) - parseFloat(b));
    }
    
    return values;
  };

  useEffect(() => {
    if (isAuthenticated) {
      dispatch(fetchResults());

      // Получаем maxRadius из токена
      const maxRadiusClaim = keycloak.tokenParsed?.maxRadius;
      let maxR = 2.0; // значение по умолчанию
      
      if (maxRadiusClaim) {
        if (typeof maxRadiusClaim === 'number') {
          maxR = maxRadiusClaim;
        } else if (typeof maxRadiusClaim === 'string') {
          try {
            maxR = parseFloat(maxRadiusClaim);
          } catch (e) {
            console.warn('Invalid maxRadius in token:', maxRadiusClaim);
          }
        }
      }
      
      console.log('Setting maxRadius:', maxR);
      setMaxRadius(maxR);
      
      // Генерируем допустимые значения R
      const allowed = generateAllowedR(maxR);
      console.log('Generated allowedR:', allowed);
      setAllowedR(allowed);
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
    
    console.log('=== SUBMIT DEBUG ===');
    console.log('Submit - rValue:', rValue, 'type:', typeof rValue);
    console.log('Submit - allowedR:', allowedR);
    console.log('Submit - maxRadius:', maxRadius);
    console.log('Submit - keycloak.tokenParsed?.maxRadius:', keycloak.tokenParsed?.maxRadius);
    
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

    const rNum = parseFloat(rValue.replace(',', '.'));
    if (isNaN(rNum) || !isFinite(rNum) || rNum <= 0) {
      alert('R должен быть положительным числом');
      return;
    }

    // Проверка maxRadius
    if (maxRadius !== null && rNum > maxRadius) {
      alert(`R (${rNum}) превышает ваш максимально допустимый радиус (${maxRadius})`);
      return;
    }
    
    // Нормализуем rValue для сравнения - приводим к формату с одним знаком после запятой
    const normalizedRValue = rNum.toFixed(1);
    console.log('Normalized rValue for comparison:', normalizedRValue);
    
    // Проверка что значение в списке разрешенных
    if (allowedR.length > 0 && !allowedR.includes(normalizedRValue)) {
      console.log('Value not found in allowedR!');
      alert(`R должно быть одним из допустимых значений: ${allowedR.join(', ')}`);
      return;
    }

    const xNum = parseFloat(xValue.replace(',', '.'));
    const yNum = parseFloat(y.replace(',', '.'));
    
    console.log('=== SENDING TO SERVER ===');
    console.log('Final values: x=', xNum, 'y=', yNum, 'r=', rNum);
    console.log('maxRadius from token:', maxRadius);
    
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
          <div style={{display: 'flex', flexDirection: 'column', alignItems: 'flex-end', marginRight: '15px'}}>
          <span className="username">Пользователь: {username}</span>
            {maxRadius !== null && (
              <span style={{fontSize: '0.85em', color: '#666', marginTop: '2px'}}>
                Макс. радиус: {maxRadius}
              </span>
            )}
          </div>
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
                <label htmlFor="r">R (радиус):</label>
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
                {maxRadius !== null && (
                  <small className="hint-text" style={{color: '#2196f3', fontWeight: 'bold'}}>
                    Ваш максимальный радиус: {maxRadius}
                  </small>
                )}
                <small>Допустимые значения: {allowedR.length > 0 ? allowedR.join(', ') : 'загрузка...'}</small>
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
                  (() => {
                    const rValue = normalizeValue(r);
                    if (!rValue || allowedR.length === 0) return false;
                    const rNum = parseFloat(rValue.replace(',', '.'));
                    if (isNaN(rNum)) return true;
                    const normalizedR = rNum.toFixed(1);
                    return !allowedR.includes(normalizedR);
                  })()
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

