// DOM Elements
const serviceSelection = document.getElementById('serviceSelection');
const fortuneFormContainer = document.getElementById('fortuneFormContainer');
const sajuFormContainer = document.getElementById('sajuFormContainer');
const fortuneForm = document.getElementById('fortuneForm');
const sajuForm = document.getElementById('sajuForm');
const resultContainer = document.getElementById('resultContainer');
const loading = document.getElementById('loading');
const resultTitle = document.getElementById('resultTitle');
const resultContent = document.getElementById('resultContent');
const loadingText = document.getElementById('loadingText');

// Current service type
let currentService = null;

// Service selection
function selectService(serviceType) {
    currentService = serviceType;
    serviceSelection.style.display = 'none';

    if (serviceType === 'fortune') {
        fortuneFormContainer.style.display = 'block';
    } else if (serviceType === 'saju') {
        sajuFormContainer.style.display = 'block';
    }
}

// Back to service selection
function backToSelection() {
    // Hide all containers
    fortuneFormContainer.style.display = 'none';
    sajuFormContainer.style.display = 'none';
    resultContainer.style.display = 'none';

    // Reset forms
    fortuneForm.reset();
    sajuForm.reset();

    // Show service selection
    serviceSelection.style.display = 'block';
    currentService = null;
}

// Fortune form submit handler
fortuneForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Get form data
    const name = document.getElementById('fortuneName').value;
    const gender = document.querySelector('input[name="fortuneGender"]:checked').value;
    const birthDate = document.getElementById('fortuneBirthDate').value;

    const data = {
        name: name,
        gender: gender,
        birthDate: birthDate
    };

    // Validate data
    if (!data.name || !data.gender || !data.birthDate) {
        alert('모든 항목을 입력해주세요.');
        return;
    }

    try {
        // Show loading state
        fortuneFormContainer.style.display = 'none';
        loading.style.display = 'block';
        loadingText.textContent = '운세를 확인하고 있습니다...';

        // Call API
        const response = await fetch('/api/fortune', {
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
        displayFortuneResult(result);

    } catch (error) {
        console.error('Error:', error);
        alert('운세를 가져오는데 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');

        // Show form again on error
        loading.style.display = 'none';
        fortuneFormContainer.style.display = 'block';
    }
});

// Saju form submit handler
sajuForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Get form data
    const name = document.getElementById('sajuName').value;
    const gender = document.querySelector('input[name="sajuGender"]:checked').value;
    const birthDate = document.getElementById('sajuBirthDate').value;
    const birthTime = document.getElementById('sajuBirthTime').value;

    const data = {
        name: name,
        gender: gender,
        birthDate: birthDate,
        birthTime: birthTime || null
    };

    // Validate data
    if (!data.name || !data.gender || !data.birthDate) {
        alert('이름, 성별, 생년월일은 필수 항목입니다.');
        return;
    }

    try {
        // Show loading state
        sajuFormContainer.style.display = 'none';
        loading.style.display = 'block';
        loadingText.textContent = '사주팔자를 분석하고 있습니다...';

        // Call API
        const response = await fetch('/api/saju', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error('사주를 가져오는데 실패했습니다.');
        }

        const result = await response.json();

        // Display result
        displaySajuResult(result);

    } catch (error) {
        console.error('Error:', error);
        alert('사주를 가져오는데 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');

        // Show form again on error
        loading.style.display = 'none';
        sajuFormContainer.style.display = 'block';
    }
});

// Display fortune result
function displayFortuneResult(result) {
    resultTitle.textContent = `${result.name}님의 오늘의 운세`;

    const html = `
        <div class="fortune-result">
            <div class="info-section">
                <p><strong>생년월일:</strong> ${result.birthDate}</p>
                <p><strong>성별:</strong> ${result.gender === 'male' ? '남성' : '여성'}</p>
            </div>
            <div class="fortune-content">
                <h3>🌟 운세 해석</h3>
                <div class="interpretation">
                    ${result.fortune.replace(/\n/g, '<br>')}
                </div>
            </div>
        </div>
    `;

    resultContent.innerHTML = html;
    loading.style.display = 'none';
    resultContainer.style.display = 'block';
}

// Display saju result
function displaySajuResult(result) {
    resultTitle.textContent = `${result.name}님의 사주팔자`;

    const html = `
        <div class="saju-result">
            <div class="info-section">
                <p><strong>생년월일:</strong> ${result.birthDate}</p>
                ${result.birthTime ? `<p><strong>태어난 시간:</strong> ${result.birthTime}</p>` : ''}
            </div>

            <div class="saju-pillars">
                <h3>📜 사주팔자</h3>
                <div class="pillars-grid">
                    <div class="pillar">
                        <div class="pillar-label">년주 (年柱)</div>
                        <div class="pillar-value">${result.yearPillar}</div>
                    </div>
                    <div class="pillar">
                        <div class="pillar-label">월주 (月柱)</div>
                        <div class="pillar-value">${result.monthPillar}</div>
                    </div>
                    <div class="pillar">
                        <div class="pillar-label">일주 (日柱)</div>
                        <div class="pillar-value">${result.dayPillar}</div>
                    </div>
                    <div class="pillar">
                        <div class="pillar-label">시주 (時柱)</div>
                        <div class="pillar-value">${result.hourPillar}</div>
                    </div>
                </div>
            </div>

            <div class="saju-interpretation">
                <h3>🔮 사주 해석</h3>
                <div class="interpretation">
                    ${result.interpretation.replace(/\n/g, '<br>')}
                </div>
            </div>
        </div>
    `;

    resultContent.innerHTML = html;
    loading.style.display = 'none';
    resultContainer.style.display = 'block';
}

// Set max date to today for birth date inputs
const today = new Date().toISOString().split('T')[0];
document.getElementById('fortuneBirthDate').setAttribute('max', today);
document.getElementById('sajuBirthDate').setAttribute('max', today);
