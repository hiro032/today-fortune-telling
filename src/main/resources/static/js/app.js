// DOM Elements
const fortuneForm = document.getElementById('fortuneForm');
const formContainer = document.getElementById('formContainer');
const resultContainer = document.getElementById('resultContainer');
const loading = document.getElementById('loading');
const submitBtn = document.getElementById('submitBtn');
const resetBtn = document.getElementById('resetBtn');
const resultName = document.getElementById('resultName');
const resultContent = document.getElementById('resultContent');

// API endpoint
const API_URL = '/api/fortune';

// Form submit handler
fortuneForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Get form data
    const formData = new FormData(fortuneForm);
    const data = {
        name: formData.get('name'),
        gender: formData.get('gender'),
        birthDate: formData.get('birthDate')
    };

    // Validate data
    if (!data.name || !data.gender || !data.birthDate) {
        alert('모든 항목을 입력해주세요.');
        return;
    }

    try {
        // Show loading state
        formContainer.style.display = 'none';
        loading.style.display = 'block';
        submitBtn.disabled = true;

        // Call API
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error('운세를 가져오는데 실패했습니다.');
        }

        const result = await response.json();

        // Display result
        displayResult(result);

    } catch (error) {
        console.error('Error:', error);
        alert('운세를 가져오는데 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');

        // Show form again on error
        loading.style.display = 'none';
        formContainer.style.display = 'block';
        submitBtn.disabled = false;
    }
});

// Display result function
function displayResult(result) {
    resultName.textContent = result.name;
    resultContent.textContent = result.fortune;

    loading.style.display = 'none';
    resultContainer.style.display = 'block';
}

// Reset button handler
resetBtn.addEventListener('click', () => {
    // Reset form
    fortuneForm.reset();

    // Show form, hide result
    resultContainer.style.display = 'none';
    formContainer.style.display = 'block';
    submitBtn.disabled = false;
});

// Set max date to today for birth date input
const today = new Date().toISOString().split('T')[0];
document.getElementById('birthDate').setAttribute('max', today);
