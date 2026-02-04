/**
 * 런타임 설정 로더
 * 페이지 로드 시 /api/public/config에서 설정을 가져옵니다
 * 모든 페이지는 API 호출 전에 설정 로드가 완료될 때까지 기다려야 합니다
 */

window.APP_RUNTIME = {
    loaded: false,
    loading: false,
    config: null,
    error: null
};

/**
 * 서버에서 설정 로드
 * @returns {Promise<Object>} 설정 객체
 */
async function loadConfig() {
    if (window.APP_RUNTIME.loaded) {
        return window.APP_RUNTIME.config;
    }

    if (window.APP_RUNTIME.loading) {
        // 기존 로드가 완료될 때까지 대기
        return new Promise((resolve, reject) => {
            const checkInterval = setInterval(() => {
                if (window.APP_RUNTIME.loaded) {
                    clearInterval(checkInterval);
                    resolve(window.APP_RUNTIME.config);
                } else if (window.APP_RUNTIME.error) {
                    clearInterval(checkInterval);
                    reject(window.APP_RUNTIME.error);
                }
            }, 100);
        });
    }

    window.APP_RUNTIME.loading = true;

    try {
        const response = await fetch('/api/public/config');
        if (!response.ok) {
            throw new Error(`설정 로드 실패: ${response.status}`);
        }

        const config = await response.json();
        window.APP_RUNTIME.config = config;
        window.APP_RUNTIME.loaded = true;
        window.APP_RUNTIME.loading = false;

        console.log('설정 로드 완료:', config);

        // 설정으로 UI 업데이트
        updateConfigDisplay(config);

        return config;
    } catch (error) {
        console.error('설정 로드 실패:', error);
        window.APP_RUNTIME.error = error;
        window.APP_RUNTIME.loading = false;
        throw error;
    }
}

/**
 * 로드된 설정으로 네비게이션 바 업데이트
 */
function updateConfigDisplay(config) {
    // 설정 배지가 있으면 업데이트
    const configBadges = document.querySelectorAll('.config-badge-container');
    if (configBadges.length > 0 && config) {
        // 첫 번째 채널 키 가져오기 (toss 또는 kg-inicis)
        const channelKeys = config.portone.channelKeys || {};
        const firstChannelKey = Object.values(channelKeys)[0] || 'N/A';

        configBadges.forEach(container => {
            container.innerHTML = `
                <span class="config-badge">Store ID: ${config.portone.storeId.substring(0, 20)}...</span>
                <span class="config-badge">Channel: ${firstChannelKey.substring(0, 20)}...</span>
            `;
        });
    }
}

/**
 * 설정 가져오기 (로드되지 않았으면 대기)
 * @returns {Promise<Object>} 설정 객체
 */
async function getConfig() {
    if (!window.APP_RUNTIME.loaded) {
        await loadConfig();
    }
    return window.APP_RUNTIME.config;
}

/**
 * 엔드포인트 키로 전체 API URL 생성
 * @param {string} endpointKey - config.api.endpoints의 키
 * @param {Object} params - 경로 파라미터 (예: {orderId: '123'})
 * @returns {Promise<string>} 전체 URL
 */
async function buildApiUrl(endpointKey, params = {}) {
    const config = await getConfig();
    const endpointContract = config.api.endpoints[endpointKey];

    if (!endpointContract) {
        throw new Error(`엔드포인트 '${endpointKey}'를 설정에서 찾을 수 없습니다`);
    }

    // URL 가져오기
    let url = endpointContract.url;

    // 경로 파라미터 치환
    Object.keys(params).forEach(key => {
        url = url.replace(`{${key}}`, params[key]);
    });

    return config.api.baseUrl + url;
}

// 페이지 로드 시 자동으로 설정 로드
document.addEventListener('DOMContentLoaded', () => {
    loadConfig().catch(error => {
        console.error('초기 설정 로드 실패:', error);
        // 오류 배너 표시
        const banner = document.createElement('div');
        banner.className = 'alert alert-danger';
        banner.style.margin = '1rem';
        banner.innerHTML = `
            <strong>⚠️ 설정 오류</strong><br>
            애플리케이션 설정을 로드하지 못했습니다. 서버가 실행 중인지 확인해주세요.
        `;
        document.body.insertBefore(banner, document.body.firstChild);
    });
});
