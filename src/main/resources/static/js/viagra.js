// const style = document.createElement('style');
// style.innerHTML = `
//     .overlay {
//         position: fixed;
//         top: 0;
//         left: 0;
//         width: 100vw;
//         height: 100vh;
//         background-color: rgba(0, 0, 0, 0.8);
//         z-index: 10000; /* Высокий z-index, чтобы перекрыть все */
//         display: flex;
//         justify-content: center;
//         align-items: center;
//         overflow: hidden;
//     }
//
//     .popup {
//         position: relative;
//         background-color: white;
//         border-radius: 8px;
//         padding: 20px;
//         max-width: 70%;
//         width: 80%;
//         text-align: center;
//         z-index: 10001;
//     }
//
//     .ad-label {
//         position: absolute;
//         top: -30px;
//         left: 50%;
//         transform: translateX(-50%);
//         background-color: #ffcc00;
//         padding: 5px 10px;
//         border-radius: 5px;
//         font-size: 16px;
//         font-weight: bold;
//         color: #333;
//     }
//
//     .close-btn {
//         position: absolute;
//         top: 10px;
//         right: 10px;
//         font-size: 24px;
//         font-weight: bold;
//         cursor: pointer;
//     }
//
//     .ad-image {
//         width: 100%;
//         height: auto;
//         border-radius: 8px;
//     }
// `;
// document.head.appendChild(style);
//
// const overlay = document.createElement('div');
// overlay.className = 'overlay';
// overlay.id = 'overlay';
// document.body.appendChild(overlay);
//
// const popup = document.createElement('div');
// popup.className = 'popup';
// popup.id = 'popup';
// popup.innerHTML = `
//     <div class="ad-label">Реклама Виагры</div>
//     <span id="close-btn" class="close-btn">&times;</span>
//     <a href="https://apteka-lz.ru/catalog/lekarstvennye-sredstva/muzhskoe-zdorove/140668/">
//         <img src="/files/ads_vig.png" alt="Рекламное изображение" class="ad-image" />
//     </a>
// `;
// overlay.appendChild(popup);
//
// function showPopup() {
//     overlay.style.display = 'flex';
//     document.body.style.overflow = 'hidden';
// }
//
// function hidePopup() {
//     overlay.style.display = 'none';
//     document.body.style.overflow = '';
// }
//
// setInterval(showPopup, 30000);
//
// document.getElementById('close-btn').addEventListener('click', hidePopup);
//
// showPopup();