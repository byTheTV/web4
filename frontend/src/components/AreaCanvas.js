import React, { useEffect, useRef } from 'react';
import './AreaCanvas.css';

const AreaCanvas = ({ r, results, onCanvasClick }) => {
  const canvasRef = useRef(null);
  const previewPointRef = useRef(null);

  useEffect(() => {
    drawCanvas();
  }, [r, results]);

  const drawCanvas = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    const size = canvas.clientWidth;
    const dpr = window.devicePixelRatio || 1;
    
    canvas.width = size * dpr;
    canvas.height = size * dpr;
    ctx.scale(dpr, dpr);
    
    ctx.clearRect(0, 0, size, size);
    
    const currentR = Math.abs(r || 1);
    const domainMax = Math.max(5, currentR + 1);
    const unit = size / (domainMax * 2);
    const cx = size / 2;
    const cy = size / 2;
    
    // Draw grid
    ctx.strokeStyle = '#e5e7eb';
    ctx.lineWidth = 1;
    for (let k = -domainMax; k <= domainMax; k++) {
      const gx = cx + k * unit;
      const gy = cy - k * unit;
      ctx.beginPath();
      ctx.moveTo(gx, 0);
      ctx.lineTo(gx, size);
      ctx.stroke();
      ctx.beginPath();
      ctx.moveTo(0, gy);
      ctx.lineTo(size, gy);
      ctx.stroke();
    }
    
    // Draw axes
    ctx.strokeStyle = '#333';
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.moveTo(0, cy);
    ctx.lineTo(size, cy);
    ctx.moveTo(cx, 0);
    ctx.lineTo(cx, size);
    ctx.stroke();
    
    // Draw arrows
    ctx.fillStyle = '#333';
    ctx.beginPath();
    ctx.moveTo(size - 10, cy - 4);
    ctx.lineTo(size, cy);
    ctx.lineTo(size - 10, cy + 4);
    ctx.moveTo(cx - 4, 10);
    ctx.lineTo(cx, 0);
    ctx.lineTo(cx + 4, 10);
    ctx.fill();
    
    // Draw region
    ctx.fillStyle = 'rgba(30, 144, 255, 0.4)';
    ctx.strokeStyle = 'rgba(30, 144, 255, 0.8)';
    ctx.lineWidth = 2;
    
    // First quadrant: triangle
    ctx.beginPath();
    ctx.moveTo(cx, cy);
    ctx.lineTo(cx + currentR * unit, cy);
    ctx.lineTo(cx, cy - currentR * unit);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();
    
    // Second quadrant: rectangle
    ctx.fillRect(cx - (currentR / 2) * unit, cy - currentR * unit, (currentR / 2) * unit, currentR * unit);
    ctx.strokeRect(cx - (currentR / 2) * unit, cy - currentR * unit, (currentR / 2) * unit, currentR * unit);
    
    // Fourth quadrant: quarter circle
    ctx.beginPath();
    ctx.moveTo(cx, cy);
    ctx.arc(cx, cy, (currentR / 2) * unit, 0, Math.PI / 2, false);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();
    
    // Draw axis labels
    ctx.fillStyle = '#333';
    ctx.font = '12px sans-serif';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'top';
    
    const labels = [-currentR, -currentR / 2, currentR / 2, currentR];
    for (const v of labels) {
      const x = cx + v * unit;
      if (x >= 0 && x <= size) {
        ctx.beginPath();
        ctx.moveTo(x, cy - 5);
        ctx.lineTo(x, cy + 5);
        ctx.stroke();
        if (v !== 0) {
          ctx.fillText(String(v), x, cy + 6);
        }
      }
    }
    
    ctx.textAlign = 'right';
    ctx.textBaseline = 'middle';
    for (const v of labels) {
      const y = cy - v * unit;
      if (y >= 0 && y <= size) {
        ctx.beginPath();
        ctx.moveTo(cx - 5, y);
        ctx.lineTo(cx + 5, y);
        ctx.stroke();
        if (v !== 0) {
          ctx.fillText(String(v), cx - 6, y);
        }
      }
    }
    
    // Draw points from results
    if (results && results.length > 0) {
      results.forEach(result => {
        // Compare absolute values of R for matching
        if (Math.abs(Math.abs(result.r) - Math.abs(currentR)) < 0.01) {
          const px = cx + result.x * unit;
          const py = cy - result.y * unit;
          ctx.beginPath();
          ctx.arc(px, py, 4, 0, Math.PI * 2);
          ctx.fillStyle = result.hit ? '#2e7d32' : '#b00020';
          ctx.fill();
        }
      });
    }
    
    // Draw preview point
    if (previewPointRef.current) {
      const { x, y } = previewPointRef.current;
      const px = cx + x * unit;
      const py = cy - y * unit;
      ctx.beginPath();
      ctx.arc(px, py, 6, 0, Math.PI * 2);
      ctx.fillStyle = '#2563eb';
      ctx.fill();
      ctx.strokeStyle = '#ffffff';
      ctx.lineWidth = 2;
      ctx.stroke();
    }
  };

  const handleMouseMove = (e) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    
    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    
    const size = canvas.clientWidth;
    const currentR = Math.abs(r || 1);
    const domainMax = Math.max(5, currentR + 1);
    const unit = size / (domainMax * 2);
    const cx = size / 2;
    const cy = size / 2;
    
    // Calculate coordinates
    const canvasX = (x - cx) / unit;
    const canvasY = -(y - cy) / unit;
    
    // Clamp Y
    const clampedY = Math.max(-3, Math.min(3, canvasY));
    
    // Find closest X value
    const allowedX = [-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2];
    let closestX = allowedX[0];
    let minDist = Math.abs(canvasX - closestX);
    for (const val of allowedX) {
      const dist = Math.abs(canvasX - val);
      if (dist < minDist) {
        minDist = dist;
        closestX = val;
      }
    }
    
    previewPointRef.current = { x: closestX, y: clampedY };
    drawCanvas();
  };

  const handleClick = (e) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    
    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    
    const size = canvas.clientWidth;
    const currentR = Math.abs(r || 1);
    const domainMax = Math.max(5, currentR + 1);
    const unit = size / (domainMax * 2);
    const cx = size / 2;
    const cy = size / 2;
    
    // Calculate coordinates
    const canvasX = (x - cx) / unit;
    const canvasY = -(y - cy) / unit;
    
    // Clamp Y
    const clampedY = Math.max(-3, Math.min(3, canvasY));
    
    // Find closest X value
    const allowedX = [-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2];
    let closestX = allowedX[0];
    let minDist = Math.abs(canvasX - closestX);
    for (const val of allowedX) {
      const dist = Math.abs(canvasX - val);
      if (dist < minDist) {
        minDist = dist;
        closestX = val;
      }
    }
    
    if (onCanvasClick) {
      onCanvasClick(closestX, clampedY);
    }
    
    previewPointRef.current = null;
    drawCanvas();
  };

  const handleMouseLeave = () => {
    previewPointRef.current = null;
    drawCanvas();
  };

  useEffect(() => {
    const handleResize = () => {
      drawCanvas();
    };
    
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <div className="canvas-container">
      <canvas
        ref={canvasRef}
        className="area-canvas"
        onMouseMove={handleMouseMove}
        onClick={handleClick}
        onMouseLeave={handleMouseLeave}
      />
    </div>
  );
};

export default AreaCanvas;

