// DOM Elements
const serviceSelection = document.getElementById('serviceSelection');
const fortuneFormContainer = document.getElementById('fortuneFormContainer');
const sajuFormContainer = document.getElementById('sajuFormContainer');
const compatibilityFormContainer = document.getElementById('compatibilityFormContainer');
const fortuneForm = document.getElementById('fortuneForm');
const sajuForm = document.getElementById('sajuForm');
const compatibilityForm = document.getElementById('compatibilityForm');
const resultContainer = document.getElementById('resultContainer');
const loading = document.getElementById('loading');
const resultTitle = document.getElementById('resultTitle');
const resultContent = document.getElementById('resultContent');
const loadingText = document.getElementById('loadingText');
const loadingTip = document.getElementById('loadingTip');

// Current service type
let currentService = null;

// 로딩 팁 메시지
const loadingTips = [
    '사주팔자는 당신의 인생 지도입니다 📜',
    '운명은 변할 수 있습니다 ✨',
    '좋은 기운을 받고 계신가요? 🌟',
    '오늘도 좋은 하루 되세요! 💫',
    '행운이 함께하길 바랍니다 🍀',
    '긍정적인 마음이 좋은 운을 불러옵니다 😊',
    '당신의 미래는 밝습니다 🌈',
    '하늘의 뜻을 확인하고 있습니다 🔮'
];

let tipInterval = null;

// Service selection
function selectService(serviceType) {
    currentService = serviceType;
    serviceSelection.style.display = 'none';

    if (serviceType === 'fortune') {
        fortuneFormContainer.style.display = 'block';
    } else if (serviceType === 'saju') {
        sajuFormContainer.style.display = 'block';
    } else if (serviceType === 'compatibility') {
        compatibilityFormContainer.style.display = 'block';
    }
}

// 로딩 시작
function startLoading(message) {
    loading.style.display = 'block';
    loadingText.textContent = message;

    // 랜덤 팁 표시
    let tipIndex = 0;
    loadingTip.textContent = loadingTips[tipIndex];

    // 3초마다 팁 변경
    tipInterval = setInterval(() => {
        tipIndex = (tipIndex + 1) % loadingTips.length;
        loadingTip.textContent = loadingTips[tipIndex];
    }, 3000);
}

// 로딩 종료
function stopLoading() {
    loading.style.display = 'none';
    if (tipInterval) {
        clearInterval(tipInterval);
        tipInterval = null;
    }
}

// Back to service selection
function backToSelection() {
    // Hide all containers
    fortuneFormContainer.style.display = 'none';
    sajuFormContainer.style.display = 'none';
    compatibilityFormContainer.style.display = 'none';
    resultContainer.style.display = 'none';
    stopLoading();

    // Reset forms
    fortuneForm.reset();
    sajuForm.reset();
    compatibilityForm.reset();

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
        startLoading('운세를 확인하고 있습니다...');

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
        stopLoading();
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
        startLoading('사주팔자를 분석하고 있습니다...');

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
        stopLoading();
        sajuFormContainer.style.display = 'block';
    }
});

// Compatibility form submit handler
compatibilityForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Get form data
    const name1 = document.getElementById('name1').value;
    const gender1 = document.querySelector('input[name="gender1"]:checked').value;
    const birthDate1 = document.getElementById('birthDate1').value;

    const name2 = document.getElementById('name2').value;
    const gender2 = document.querySelector('input[name="gender2"]:checked').value;
    const birthDate2 = document.getElementById('birthDate2').value;

    const compatibilityType = document.getElementById('compatibilityType').value;

    const data = {
        name1, gender1, birthDate1,
        name2, gender2, birthDate2,
        compatibilityType
    };

    // Validate data
    if (!name1 || !gender1 || !birthDate1 || !name2 || !gender2 || !birthDate2) {
        alert('모든 필수 항목을 입력해주세요.');
        return;
    }

    try {
        // Show loading state
        compatibilityFormContainer.style.display = 'none';
        startLoading('두 사람의 궁합을 분석하고 있습니다...');

        // Call API
        const response = await fetch('/api/compatibility', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error('궁합을 가져오는데 실패했습니다.');
        }

        const result = await response.json();

        // Display result
        displayCompatibilityResult(result);

    } catch (error) {
        console.error('Error:', error);
        alert('궁합을 가져오는데 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');

        // Show form again on error
        stopLoading();
        compatibilityFormContainer.style.display = 'block';
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
    stopLoading();
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
    stopLoading();
    resultContainer.style.display = 'block';
}

// Display compatibility result
function displayCompatibilityResult(result) {
    resultTitle.textContent = `${result.name1} & ${result.name2}님의 궁합`;

    const html = `
        <div class="compatibility-result">
            <div class="compatibility-score">
                <div class="score-number">${result.compatibilityScore}</div>
                <div class="score-label">${result.compatibilityType} 점수</div>
            </div>

            <div class="saju-comparison">
                <div class="saju-item">
                    <h4>${result.name1}님의 사주</h4>
                    <p>${result.saju1}</p>
                    <small>${result.birthDate1}</small>
                </div>
                <div class="saju-item">
                    <h4>${result.name2}님의 사주</h4>
                    <p>${result.saju2}</p>
                    <small>${result.birthDate2}</small>
                </div>
            </div>

            <div class="compatibility-section">
                <h3>💑 궁합 분석</h3>
                <div class="interpretation">
                    ${result.interpretation.replace(/\n/g, '<br>')}
                </div>
            </div>

            <div class="compatibility-section">
                <h3>💡 추천 사항</h3>
                <div class="info-section">
                    <p>${result.recommendation}</p>
                </div>
            </div>
        </div>
    `;

    resultContent.innerHTML = html;
    stopLoading();
    resultContainer.style.display = 'block';
}

// Set max date to today for birth date inputs
const today = new Date().toISOString().split('T')[0];
document.getElementById('fortuneBirthDate').setAttribute('max', today);
document.getElementById('sajuBirthDate').setAttribute('max', today);
document.getElementById('birthDate1').setAttribute('max', today);
document.getElementById('birthDate2').setAttribute('max', today);
