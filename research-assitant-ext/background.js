// background.js
chrome.runtime.onInstalled.addListener(() => {
    console.log("Research Assistant by Mr.Engineer installed.");
});

// This opens the side panel when the extension icon is clicked
chrome.sidePanel.setPanelBehavior({ openPanelOnActionClick: true });
