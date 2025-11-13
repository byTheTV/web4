// Define selectXValue function immediately so it's available when buttons are clicked
// Make it available both as window.selectXValue and global selectXValue
function selectXValue(x) {
    console.log('selectXValue called with:', x);
    
    // Update visual state first
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
    
    // Find and update hidden input
    var hiddenInput = document.getElementById('mainForm:xValue') || 
                     document.querySelector('[id$=":xValue"]') ||
                     document.querySelector('input[type="hidden"][id*="xValue"]');
    
    if (hiddenInput) {
        console.log('Found xValue input, setting value to:', x);
        var xStr = String(x);
        hiddenInput.value = xStr;
        
        // Set attribute
        if (hiddenInput.setAttribute) {
            hiddenInput.setAttribute('value', xStr);
        }
        
        // Trigger events for JSF
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
        
        // Focus and blur to trigger JSF processing
        hiddenInput.focus();
        setTimeout(function() {
            hiddenInput.blur();
        }, 10);
        
        console.log('X value set to:', hiddenInput.value);
    } else {
        console.error('Could not find xValue input');
    }
    
    // Update currentX if defined
    if (typeof window.currentX !== 'undefined') {
        window.currentX = x;
    }
    
    // Update canvas if function exists
    if (typeof window.updateCanvas === 'function') {
        window.updateCanvas();
    }
}
// Also make it available on window for global access
window.selectXValue = selectXValue;

// Make setXValue globally accessible (before area.js loads)
// This is used by canvas and other code, it should call selectXValue for consistency
function setXValue(x) {
    console.log('setXValue called with:', x);
    // Use selectXValue to ensure consistent behavior
    if (typeof window.selectXValue === 'function') {
        window.selectXValue(x);
    } else {
        // Fallback if selectXValue not available yet
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
// Make it available on window for global access
window.setXValue = setXValue;

// Parse number with locale support (handles both comma and dot as decimal separator)
function parseLocaleFloat(str) {
    if (!str) return NaN;
    // Convert to string, trim whitespace, replace comma with dot for parsing
    var normalized = String(str).trim().replace(',', '.');
    var result = parseFloat(normalized);
    return isNaN(result) ? NaN : result;
}

window.updateCanvas = function() {
    var rInput = document.getElementById('mainForm:rValue_input');
    if (rInput) {
        var newR = parseLocaleFloat(rInput.value) || 1.0;
        // Always update and redraw, even if value seems the same (in case of formatting issues)
        var shouldLog = Math.abs(newR - currentR) > 0.001;
        currentR = newR;
        if (shouldLog) {
            console.log('updateCanvas: R value changed to:', currentR, 'input value:', rInput.value);
        }
    } else {
        console.warn('updateCanvas: rInput not found');
    }
    // Always redraw, even if R didn't change (to ensure canvas is up to date)
    if (typeof drawAll === 'function') {
        drawAll();
    } else {
        console.warn('drawAll function not available');
    }
};

// Function to update X button visual state (make it global)
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
            // Select this button
            btn.classList.add('selected');
            btn.style.setProperty('background-color', '#2196F3', 'important');
            btn.style.setProperty('color', 'white', 'important');
            btn.style.setProperty('border-color', '#2196F3', 'important');
        } else {
            // Deselect this button
            btn.classList.remove('selected');
            btn.style.removeProperty('background-color');
            btn.style.removeProperty('color');
            btn.style.removeProperty('border-color');
        }
    }
};

// Setup X button click handlers using event delegation (works even after AJAX updates)
function setupXButtons() {
    // Remove old listener if exists
    if (window.xButtonClickHandler) {
        document.removeEventListener('click', window.xButtonClickHandler);
    }
    
    // Use event delegation on document level
    window.xButtonClickHandler = function(e) {
        var target = e.target;
        // Check if clicked element is an X button or inside one
        var btn = target.closest('#xButtonGroup .btn[data-x]');
        if (btn) {
            e.preventDefault();
            e.stopPropagation();
            var xValue = parseInt(btn.getAttribute('data-x'));
            console.log('X button clicked via delegation, value:', xValue);
            
            // Immediately update visual state
            if (typeof window.updateXButtonSelection === 'function') {
                window.updateXButtonSelection(xValue);
            }
            
            // Update the form value - try setXValue first, then fallback
            var valueSet = false;
            if (typeof window.setXValue === 'function') {
                try {
                    window.setXValue(xValue);
                    valueSet = true;
                } catch (err) {
                    console.warn('setXValue threw error:', err);
                }
            }
            
            // Fallback: set value directly if setXValue didn't work
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
                    
                    // Create proper events for JSF
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

// Setup buttons as soon as DOM is ready and also after page load
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', setupXButtons);
} else {
    setupXButtons();
}

// Initialize canvas after page load
window.addEventListener('load', function() {
    // Setup X button handlers (in case they weren't set up earlier)
    setTimeout(setupXButtons, 100);
    
    // Wait a bit for area.js to load
    setTimeout(function() {
        if (typeof drawAll === 'function') {
            drawAll();
        } else {
            console.error('drawAll function not found. Make sure area.js is loaded.');
        }
    }, 100);
    
    // Set initial selected button for X
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
            // Check if hidden input has a value
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
    
    // Setup R spinner change listener
    function setupRSpinner() {
        var rInput = document.getElementById('mainForm:rValue_input');
        if (rInput) {
            // Listen for all input changes
            rInput.addEventListener('change', function() {
                setTimeout(window.updateCanvas, 50);
            });
            rInput.addEventListener('input', function() {
                setTimeout(window.updateCanvas, 50);
            });
            
            // Watch for value property changes (polling for spinner button clicks)
            var lastValue = rInput.value;
            var valueWatcher = setInterval(function() {
                if (rInput && rInput.value !== lastValue) {
                    var newValue = rInput.value;
                    console.log('R value watcher detected change:', lastValue, '->', newValue);
                    lastValue = newValue;
                    window.updateCanvas();
                }
            }, 100);
            
            // Listen for PrimeFaces spinner button clicks using event delegation
            document.addEventListener('click', function(e) {
                var target = e.target;
                // Check if clicked element is a spinner button
                if (target && (target.classList.contains('ui-spinner-up') || 
                    target.classList.contains('ui-spinner-down') ||
                    target.closest('.ui-spinner-up') || 
                    target.closest('.ui-spinner-down'))) {
                    // Check if it's the R spinner
                    var spinnerContainer = document.getElementById('mainForm:rValue');
                    if (spinnerContainer && (target.closest('#' + spinnerContainer.id) || 
                        spinnerContainer.contains(target) ||
                        target.closest('[id*="rValue"]'))) {
                        console.log('Spinner button clicked, updating canvas...');
                        // Use multiple timeouts to catch value after update
                        setTimeout(window.updateCanvas, 100);
                        setTimeout(window.updateCanvas, 200);
                        setTimeout(window.updateCanvas, 400);
                    }
                }
            });
        } else {
            // Retry if element not found yet
            setTimeout(setupRSpinner, 100);
        }
    }
    
    setTimeout(setupRSpinner, 100);
});

