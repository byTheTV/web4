function $(sel) { return document.querySelector(sel); }
function $all(sel) { return document.querySelectorAll(sel); }
function toNum(val) { return parseFloat(String(val).replace(',', '.')); }
function showErr(id, msg) { const el = document.getElementById(id); if (el) el.textContent = msg || ''; }
function clearErrs() { showErr('x-error',''); showErr('y-error',''); showErr('r-error',''); }

function validate() {
    clearErrs();
    let ok = true;
    const x = $('input[name="x"]:checked');
    const y = $('#y');
    const r = $('#r');
    if (!x) { showErr('x-error','Выберите X'); ok = false; }
    const yVal = toNum(y.value);
    if (isNaN(yVal) || yVal < -5 || yVal > 5) { showErr('y-error','Y должен быть числом в диапазоне [-5, 5]'); ok = false; }
    const allowedR = [1,2,3,4,5];
    const rVal = toNum(r.value);
    if (!allowedR.includes(rVal)) { showErr('r-error','R должен быть одним из: ' + allowedR.join(', ')); ok = false; }
    return ok;
}

window.addEventListener('load', function() {
    const y = $('#y');
    if (y) {
        y.addEventListener('input', function() {
            const val = toNum(y.value);
            if (y.value.trim() === '') { showErr('y-error',''); return; }
            showErr('y-error', (isNaN(val) || val < -5 || val > 5) ? 'Y должен быть числом в диапазоне [-5, 5]' : '');
        });
    }
    $all('input[name="r"]').forEach(function(radio) { radio.addEventListener('change', function() { showErr('r-error',''); }); });
    $all('input[name="x"]').forEach(function(radio) { radio.addEventListener('change', function() { showErr('x-error',''); }); });
});


