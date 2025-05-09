document.addEventListener('DOMContentLoaded', async () => {
    const select = document.getElementById('testSelect');
    let tests = {};
    try {
        const res = await fetch('/assets/TestValues.json');
        tests = await res.json();
        Object.entries(tests).forEach(([key]) => {
            const opt = document.createElement('option');
            opt.value = key;
            opt.textContent = key.toUpperCase();
            select.append(opt);
        });
    } catch (e) {
        console.error('Error loading test cases', e);
    }

    select.addEventListener('change', () => {
        const cfg = tests[select.value] || {};
        const form = document.getElementById('simForm');
        Object.entries(cfg).forEach(([k, v]) => {
            const el = form.elements.namedItem(k);
            if (el) el.value = v;
        });
    });
});
