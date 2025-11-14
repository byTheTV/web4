
function selectXValue(x) {
    console.log('selectXValue called with:', x);
    
    var buttons = document.querySelectorAll('.x-btn');
    for (var i = 0; i < buttons.length; i++) {
        var btn = buttons[i];
        var btnX = parseInt(btn.getAttribute('data-x'));
        if (btnX === x) {
            btn.classList.add('selected');
            btn.style.backgroundColor = '#2196F3';
            btn.style.color = 'white';
            btn.style.borderColor = '#2196F3';
        } else {
            btn.classList.remove('selected');
            btn.style.backgroundColor = '';
            btn.style.color = '';
            btn.style.borderColor = '';
        }
    }
    
    var hiddenInput = document.getElementById('mainForm:xValue') || 
                     document.querySelector('[id$=":xValue"]') ||
                     document.querySelector('input[type="hidden"][id*="xValue"]');
    
    if (hiddenInput) {
        console.log('Found xValue input, setting value to:', x);
        var xStr = String(x);
        hiddenInput.value = xStr;
        
        if (hiddenInput.setAttribute) {
            hiddenInput.setAttribute('value', xStr);
        }
        
        try {
            var changeEvent = document.createEvent('HTMLEvents');
            changeEvent.initEvent('change', true, true);
            hiddenInput.dispatchEvent(changeEvent);
        } catch (e) {
            var evt = new Event('change', { bubbles: true, cancelable: true });
            hiddenInput.dispatchEvent(evt);
        }
        
        try {
            var inputEvent = document.createEvent('HTMLEvents');
            inputEvent.initEvent('input', true, true);
            hiddenInput.dispatchEvent(inputEvent);
        } catch (e) {
            var evt = new Event('input', { bubbles: true, cancelable: true });
            hiddenInput.dispatchEvent(evt);
        }
        
        hiddenInput.focus();
        setTimeout(function() {
            hiddenInput.blur();
        }, 10);
        
        console.log('X value set to:', hiddenInput.value);
    } else {
        console.error('Could not find xValue input');
    }
    
    if (typeof window.currentX !== 'undefined') {
        window.currentX = x;
    }
    
    if (typeof window.updateCanvas === 'function') {
        window.updateCanvas();
    }
}
window.selectXValue = selectXValue;


function setXValue(x) {
    console.log('setXValue called with:', x);
    if (typeof window.selectXValue === 'function') {
        window.selectXValue(x);
    } else {
        console.warn('selectXValue not available, using fallback');
        if (typeof currentX !== 'undefined') {
            currentX = x;
        }
        
        var hiddenInput = document.getElementById('mainForm:xValue') || 
                         document.querySelector('[id$=":xValue"]') ||
                         document.querySelector('input[type="hidden"][id*="xValue"]');
        
        if (hiddenInput) {
            var xStr = String(x);
            hiddenInput.value = xStr;
            if (hiddenInput.setAttribute) {
                hiddenInput.setAttribute('value', xStr);
            }
        }
    }
}
window.setXValue = setXValue;

function parseLocaleFloat(str) {
    if (!str) return NaN;
    var normalized = String(str).trim().replace(',', '.');
    var result = parseFloat(normalized);
    return isNaN(result) ? NaN : result;
}

window.updateCanvas = function() {
    var rInput = document.getElementById('mainForm:rValue_input');
    if (rInput) {
        var newR = parseLocaleFloat(rInput.value) || 1.0;
        var shouldLog = Math.abs(newR - currentR) > 0.001;
        currentR = newR;
        if (shouldLog) {
            console.log('updateCanvas: R value changed to:', currentR, 'input value:', rInput.value);
        }
    } else {
        console.warn('updateCanvas: rInput not found');
    }
    if (typeof drawAll === 'function') {
        drawAll();
    } else {
        console.warn('drawAll function not available');
    }
};

window.updateXButtonSelection = function(xValue) {
    var xButtonGroup = document.getElementById('xButtonGroup');
    if (!xButtonGroup) {
        console.warn('xButtonGroup not found for selection update');
        return;
    }
    var buttons = xButtonGroup.querySelectorAll('.btn[data-x]');
    for (var i = 0; i < buttons.length; i++) {
        var btn = buttons[i];
        var btnXValue = parseInt(btn.getAttribute('data-x'));
        if (!isNaN(btnXValue) && btnXValue === xValue) {
            btn.classList.add('selected');
            btn.style.setProperty('background-color', '#2196F3', 'important');
            btn.style.setProperty('color', 'white', 'important');
            btn.style.setProperty('border-color', '#2196F3', 'important');
        } else {
            btn.classList.remove('selected');
            btn.style.removeProperty('background-color');
            btn.style.removeProperty('color');
            btn.style.removeProperty('border-color');
        }
    }
};

function setupXButtons() {
    if (window.xButtonClickHandler) {
        document.removeEventListener('click', window.xButtonClickHandler);
    }
    
    window.xButtonClickHandler = function(e) {
        var target = e.target;
        var btn = target.closest('#xButtonGroup .btn[data-x]');
        if (btn) {
            e.preventDefault();
            e.stopPropagation();
            var xValue = parseInt(btn.getAttribute('data-x'));
            console.log('X button clicked via delegation, value:', xValue);
            
            if (typeof window.updateXButtonSelection === 'function') {
                window.updateXButtonSelection(xValue);
            }
            
            var valueSet = false;
            if (typeof window.setXValue === 'function') {
                try {
                    window.setXValue(xValue);
                    valueSet = true;
                } catch (err) {
                    console.warn('setXValue threw error:', err);
                }
            }
            
            if (!valueSet) {
                console.log('Using fallback to set X value directly');
                var hiddenInput = document.getElementById('mainForm:xValue') || 
                                 document.querySelector('[id$=":xValue"]') ||
                                 document.querySelector('input[type="hidden"][id*="xValue"]');
                if (hiddenInput) {
                    console.log('Found xValue input, setting value to:', xValue);
                    var xStr = String(xValue);
                    hiddenInput.value = xStr;
                    if (hiddenInput.setAttribute) {
                        hiddenInput.setAttribute('value', xStr);
                    }
                    
                    var changeEvent = document.createEvent('HTMLEvents');
                    changeEvent.initEvent('change', true, true);
                    hiddenInput.dispatchEvent(changeEvent);
                    
                    var inputEvent = document.createEvent('HTMLEvents');
                    inputEvent.initEvent('input', true, true);
                    hiddenInput.dispatchEvent(inputEvent);
                    
                    hiddenInput.focus();
                    setTimeout(function() {
                        var blurEvent = document.createEvent('HTMLEvents');
                        blurEvent.initEvent('blur', true, true);
                        hiddenInput.dispatchEvent(blurEvent);
                    }, 10);
                    
                    console.log('X value set via fallback, current value:', hiddenInput.value);
                } else {
                    console.error('Could not find xValue input element');
                }
            }
        }
    };
    
    document.addEventListener('click', window.xButtonClickHandler, true);
    console.log('X button handlers set up using event delegation');
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', setupXButtons);
} else {
    setupXButtons();
}

window.addEventListener('load', function() {
    setTimeout(setupXButtons, 100);
    
    setTimeout(function() {
        if (typeof drawAll === 'function') {
            drawAll();
        } else {
            console.error('drawAll function not found. Make sure area.js is loaded.');
        }
    }, 100);
    
    setTimeout(function() {
        if (currentX !== null && currentX !== 'null' && currentX !== undefined) {
            var xVal = typeof currentX === 'string' && currentX === 'null' ? null : parseInt(currentX);
            if (xVal !== null && !isNaN(xVal)) {
                console.log('Initializing X value from currentX:', xVal);
                if (typeof window.selectXValue === 'function') {
                    window.selectXValue(xVal);
                } else if (typeof window.setXValue === 'function') {
                    window.setXValue(xVal);
                }
            }
        } else {
            var hiddenInput = document.getElementById('mainForm:xValue') || 
                             document.querySelector('[id$=":xValue"]') ||
                             document.querySelector('input[type="hidden"][id*="xValue"]');
            if (hiddenInput && hiddenInput.value) {
                var xVal = parseInt(hiddenInput.value);
                if (!isNaN(xVal)) {
                    console.log('Initializing X value from hidden input:', xVal);
                    if (typeof window.selectXValue === 'function') {
                        window.selectXValue(xVal);
                    }
                }
            }
        }
    }, 200);
    
    function setupRSpinner() {
        var rInput = document.getElementById('mainForm:rValue_input');
        if (rInput) {
            rInput.addEventListener('change', function() {
                setTimeout(window.updateCanvas, 50);
            });
            rInput.addEventListener('input', function() {
                setTimeout(window.updateCanvas, 50);
            });
            
            var lastValue = rInput.value;
            var valueWatcher = setInterval(function() {
                if (rInput && rInput.value !== lastValue) {
                    var newValue = rInput.value;
                    console.log('R value watcher detected change:', lastValue, '->', newValue);
                    lastValue = newValue;
                    window.updateCanvas();
                }
            }, 100);
            
            document.addEventListener('click', function(e) {
                var target = e.target;
                if (target && (target.classList.contains('ui-spinner-up') || 
                    target.classList.contains('ui-spinner-down') ||
                    target.closest('.ui-spinner-up') || 
                    target.closest('.ui-spinner-down'))) {
                    var spinnerContainer = document.getElementById('mainForm:rValue');
                    if (spinnerContainer && (target.closest('#' + spinnerContainer.id) || 
                        spinnerContainer.contains(target) ||
                        target.closest('[id*="rValue"]'))) {
                        console.log('Spinner button clicked, updating canvas...');
                        setTimeout(window.updateCanvas, 100);
                        setTimeout(window.updateCanvas, 200);
                        setTimeout(window.updateCanvas, 400);
                    }
                }
            });
        } else {
            setTimeout(setupRSpinner, 100);
        }
    }
    
    setTimeout(setupRSpinner, 100);
});

