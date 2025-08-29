/* ===== ADMIN INTERFACE JAVASCRIPT =====
 * File JavaScript tổng hợp cho toàn bộ admin interface
 * Bao gồm: Language switcher, Navigation, DOM initialization
 */

document.addEventListener("DOMContentLoaded", function () {
    initializeLanguageSwitcher();
    initializeSidebarNavigation();
    initializeLanguageSwitcherEvents();
    initializeClickOutsideHandler();
    initializeBookingExpandCollapse();
});

// ===== Custom Dropdown Functions =====

function toggleDropdown() {
    const dropdown = document.querySelector('.custom-dropdown');
    const isShow = dropdown.classList.contains('show');
    
    if (isShow) {
        closeDropdown();
    } else {
        openDropdown();
    }
}

function openDropdown() {
    const dropdown = document.querySelector('.custom-dropdown');
    dropdown.classList.add('show');
}

function closeDropdown() {
    const dropdown = document.querySelector('.custom-dropdown');
    dropdown.classList.remove('show');
}

function initializeClickOutsideHandler() {
    document.addEventListener('click', function(event) {
        const dropdown = document.querySelector('.custom-dropdown');
        if (dropdown && !dropdown.contains(event.target)) {
            closeDropdown();
        }
    });
}

// ===== Language Switcher Functions =====

/**
 * Khởi tạo event listeners cho language switcher
 */
function initializeLanguageSwitcherEvents() {
    const languageItems = document.querySelectorAll('.language-item');
    
    languageItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const lang = this.getAttribute('data-lang');
            changeLanguage(lang);
        });
    });
}

/**
 * Thay đổi ngôn ngữ của trang
 * @param {string} lang - Mã ngôn ngữ ('vi' hoặc 'en')
 */
function changeLanguage(lang) {
    const url = new URL(window.location.href);
    url.searchParams.set('lang', lang);
    window.location.href = url.toString();
}

/**
 * Khởi tạo highlighting cho language switcher
 */
function initializeLanguageSwitcher() {
    const currentLang = getCurrentLanguage();
    highlightCurrentLanguage(currentLang);
}

/**
 * Lấy ngôn ngữ hiện tại từ URL parameter hoặc default
 * @returns {string} Mã ngôn ngữ hiện tại
 */
function getCurrentLanguage() {
    const urlParams = new URLSearchParams(window.location.search);
    const langFromUrl = urlParams.get('lang');
    
    if (langFromUrl) {
        return langFromUrl;
    }
    
    // Check từ document.documentElement.lang
    const htmlLang = document.documentElement.lang;
    if (htmlLang) {
        return htmlLang.toLowerCase().split('-')[0]; // 'vi-VN' -> 'vi'
    }
    
    return 'vi'; // Default fallback
}

/**
 * Highlight nút ngôn ngữ hiện tại
 * @param {string} currentLang - Ngôn ngữ hiện tại
 */
function highlightCurrentLanguage(currentLang) {
    const languageItems = document.querySelectorAll('.language-item');
    
    // Remove existing active classes
    languageItems.forEach(item => {
        item.classList.remove('active', 'text-white', 'bg-primary');
    });
    
    // Add active class to current language
    const currentLanguageItem = document.querySelector(`[data-lang="${currentLang}"]`);
    if (currentLanguageItem) {
        currentLanguageItem.classList.add('active', 'text-white', 'bg-primary');
    }
}

// ===== Sidebar Navigation Functions =====

/**
 * Khởi tạo highlighting cho sidebar navigation
 */
function initializeSidebarNavigation() {
    const currentPath = window.location.pathname;
    const sidebarItems = document.querySelectorAll('.sidebar-nav-item');
    
    // Remove all active classes first
    sidebarItems.forEach(item => {
        item.classList.remove('active');
    });
    
    // Add active class to current page
    sidebarItems.forEach(item => {
        const navType = item.getAttribute('data-nav');
        if (isCurrentPage(currentPath, navType)) {
            item.classList.add('active');
        }
    });
}

/**
 * Kiểm tra xem có phải trang hiện tại không
 * @param {string} currentPath - Đường dẫn hiện tại
 * @param {string} navType - Loại navigation (dashboard, users, venues, notifications)
 * @returns {boolean} True nếu là trang hiện tại
 */
function isCurrentPage(currentPath, navType) {
    // Normalize path - remove trailing slash
    const path = currentPath.endsWith('/') && currentPath.length > 1 
        ? currentPath.slice(0, -1) 
        : currentPath;
    
    switch (navType) {
        case 'dashboard':
            return path === '/admin' || path === '/admin/dashboard';
        case 'users':
            return path.startsWith('/admin/users');
        case 'notifications':
            return path.startsWith('/admin/notifications');
        case 'venues':
            return path.startsWith('/admin/venues');
        case 'bookings':
            return path.startsWith('/admin/bookings');
        default:
            return false;
    }
}

/* ===== Dashboard Charts JavaScript =====
 * - Khởi tạo các biểu đồ cho dashboard admin
 * - Xử lý dữ liệu và render charts
 */

// ===== Global Variables =====
let dashboardCharts = {};
let dashboardData = {};

// ===== Color Palette =====
const COLOR_PALETTE = {
    primary: ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7', '#DDA0DD', '#98D8C8', '#F7DC6F'],
    gradients: [
        'linear-gradient(45deg, #FF6B6B, #FF8E8E)',
        'linear-gradient(45deg, #4ECDC4, #44A08D)',
        'linear-gradient(45deg, #45B7D1, #2196F3)',
        'linear-gradient(45deg, #96CEB4, #6C7CE0)',
        'linear-gradient(45deg, #FFEAA7, #FFA726)',
        'linear-gradient(45deg, #DDA0DD, #BA68C8)',
        'linear-gradient(45deg, #98D8C8, #4DB6AC)',
        'linear-gradient(45deg, #F7DC6F, #FFD54F)'
    ]
};

// ===== Main Initialization =====
document.addEventListener('DOMContentLoaded', function() {
    initializeDashboardData();
    initializeAllCharts();
});

/**
 * Khởi tạo dữ liệu từ Thymeleaf variables
 */
function initializeDashboardData() {
    // Lấy dữ liệu từ các thẻ data attributes hoặc inline script
    dashboardData = {
        totalUsers: parseInt(document.querySelector('[data-total-users]')?.dataset.totalUsers) || 0,
        totalPendingOwners: parseInt(document.querySelector('[data-pending-owners]')?.dataset.pendingOwners) || 0,
        totalVenues: parseInt(document.querySelector('[data-total-venues]')?.dataset.totalVenues) || 0,
        totalVerifiedVenues: parseInt(document.querySelector('[data-verified-venues]')?.dataset.verifiedVenues) || 0,
        totalDeletedVenues: parseInt(document.querySelector('[data-deleted-venues]')?.dataset.deletedVenues) || 0,
        totalBookings: parseInt(document.querySelector('[data-total-bookings]')?.dataset.totalBookings) || 0,
        totalNotifications: parseInt(document.querySelector('[data-total-notifications]')?.dataset.totalNotifications) || 0
    };
}

/**
 * Khởi tạo tất cả biểu đồ
 */
function initializeAllCharts() {
    initVenueStatusChart();
    initUserStatsChart();
    initBookingTrendChart();
}

/**
 * Biểu đồ tròn trạng thái venue
 */
function initVenueStatusChart() {
    const canvas = document.getElementById('venueStatusChart');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    const pendingVenues = Math.max(0,
        dashboardData.totalVenues - dashboardData.totalVerifiedVenues - dashboardData.totalDeletedVenues
    );

    dashboardCharts.venueStatus = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Đã xác minh', 'Đã xóa', 'Chờ xác minh'],
            datasets: [{
                label: 'Trạng thái địa điểm',
                data: [
                    dashboardData.totalVerifiedVenues,
                    dashboardData.totalDeletedVenues,
                    pendingVenues
                ],
                backgroundColor: ['#4ECDC4', '#FF6B6B', '#FFEAA7'],
                borderWidth: 0,
                hoverOffset: 15
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 20,
                        usePointStyle: true,
                        font: { size: 12 }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((context.parsed / total) * 100).toFixed(1);
                            return `${context.label}: ${context.parsed} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

/**
 * Biểu đồ cột thống kê người dùng
 */
function initUserStatsChart() {
    const canvas = document.getElementById('userStatsChart');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    const approvedOwners = Math.max(0, dashboardData.totalUsers - dashboardData.totalPendingOwners);

    dashboardCharts.userStats = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Tổng người dùng', 'Chờ duyệt', 'Đã duyệt'],
            datasets: [{
                label: 'Số lượng',
                data: [
                    dashboardData.totalUsers,
                    dashboardData.totalPendingOwners,
                    approvedOwners
                ],
                backgroundColor: ['#45B7D1', '#FF6B6B', '#4ECDC4'],
                borderRadius: 8,
                borderSkipped: false,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        title: function(context) {
                            return `${context[0].label}`;
                        },
                        label: function(context) {
                            return `Số lượng: ${context.parsed.y}`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: { color: '#f0f0f0' },
                    ticks: {
                        precision: 0
                    }
                },
                x: {
                    grid: { display: false }
                }
            }
        }
    });
}

/**
 * Biểu đồ đường xu hướng đặt chỗ
 */
function initBookingTrendChart() {
    const canvas = document.getElementById('bookingTrendChart');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');

    // Dữ liệu mẫu cho xu hướng - có thể thay thế bằng dữ liệu thực từ server
    const currentYearData = generateTrendData(dashboardData.totalBookings);
    const lastYearData = generateTrendData(Math.floor(dashboardData.totalBookings * 0.7));

    dashboardCharts.bookingTrend = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12'],
            datasets: [{
                label: 'Đặt chỗ 2024',
                data: currentYearData,
                borderColor: '#FF6B6B',
                backgroundColor: 'rgba(255, 107, 107, 0.1)',
                tension: 0.4,
                fill: true,
                pointBackgroundColor: '#FF6B6B',
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointRadius: 5
            }, {
                label: 'Đặt chỗ 2023',
                data: lastYearData,
                borderColor: '#4ECDC4',
                backgroundColor: 'rgba(78, 205, 196, 0.1)',
                tension: 0.4,
                fill: true,
                pointBackgroundColor: '#4ECDC4',
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointRadius: 5
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        padding: 20
                    }
                },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: { color: '#f0f0f0' }
                },
                x: {
                    grid: { display: false }
                }
            },
            interaction: {
                mode: 'nearest',
                axis: 'x',
                intersect: false
            }
        }
    });
}

// ===== Utility Functions =====

/**
 * Tạo dữ liệu xu hướng dựa trên tổng số
 * @param {number} total - Tổng số để tạo xu hướng
 * @returns {Array} Mảng 12 tháng với dữ liệu xu hướng
 */
function generateTrendData(total) {
    const baseValue = Math.floor(total / 12);
    const variation = Math.floor(baseValue * 0.3);

    return Array.from({ length: 12 }, (_, index) => {
        const seasonalFactor = Math.sin((index + 1) * Math.PI / 6) * 0.2 + 1;
        const randomFactor = (Math.random() - 0.5) * 0.4 + 1;
        return Math.max(0, Math.floor(baseValue * seasonalFactor * randomFactor));
    });
}

/**
 * Tính điểm hiệu suất dựa trên giá trị hiện tại và mục tiêu
 * @param {number} current - Giá trị hiện tại
 * @param {number} target - Giá trị mục tiêu
 * @returns {number} Điểm từ 0-100
 */
function calculatePerformanceScore(current, target) {
    return Math.min(100, Math.floor((current / target) * 100));
}

/**
 * Cập nhật dữ liệu cho tất cả biểu đồ
 * @param {Object} newData - Dữ liệu mới
 */
function updateAllCharts(newData) {
    dashboardData = { ...dashboardData, ...newData };

    // Destroy existing charts
    Object.values(dashboardCharts).forEach(chart => {
        if (chart && typeof chart.destroy === 'function') {
            chart.destroy();
        }
    });

    // Reinitialize with new data
    initializeAllCharts();
}

/**
 * Xuất dữ liệu biểu đồ dưới dạng hình ảnh
 * @param {string} chartId - ID của chart canvas
 * @returns {string} Base64 image data
 */
function exportChartAsImage(chartId) {
    const canvas = document.getElementById(chartId);
    if (!canvas) return null;

    return canvas.toDataURL('image/png');
}

// ===== Export for global access =====
window.DashboardCharts = {
    updateAllCharts,
    exportChartAsImage,
    charts: dashboardCharts,
    data: dashboardData
};
// ===== Booking Management Functions =====

/**
 * Khởi tạo chức năng expand/collapse cho booking details
 */
function initializeBookingExpandCollapse() {
    const expandButtons = document.querySelectorAll('.btn-expand');
    
    expandButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target');
            const targetRow = document.getElementById(targetId);
            const expandText = this.querySelector('.expand-text');
            const collapseText = this.querySelector('.collapse-text');
            
            if (targetRow && expandText && collapseText) {
                if (targetRow.style.display === 'none' || targetRow.style.display === '') {
                    targetRow.style.display = 'table-row';
                    expandText.style.display = 'none';
                    collapseText.style.display = 'inline';
                } else {
                    targetRow.style.display = 'none';
                    expandText.style.display = 'inline';
                    collapseText.style.display = 'none';
                }
            }
        });
    });
}
