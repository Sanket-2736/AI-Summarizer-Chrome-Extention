document.addEventListener('DOMContentLoaded', () => {
    chrome.storage.local.get(['researchNotes'], function (res){
        if (res.researchNotes) {
            document.getElementById('notes').value = res.researchNotes;
        }
    });

    document.getElementById('summarizeBtn').addEventListener('click', summarizeText);
    document.getElementById('saveNotesBtn').addEventListener('click', saveNotesToStorage);
});

async function summarizeText() {
    try {
        const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
        const [{result}] = await chrome.scripting.executeScript({
            target: { tabId: tab.id },
            function: () => window.getSelection().toString()
        });

        if (!result) {
            showResult("Please select some text first!");
            return;
        }

        const response = await fetch('http://localhost:8080/api/research/process', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ content: result, operation: "summarize" })
        });

        if (!response.ok) {
            alert("API Error: " + response.status);
            return;
        }

        const text = await response.text();
        showResult(text.replace(/\n/g, '<br>'));
    } catch (error) {
        alert("Internal error!");
        console.error(error);
    }
}

async function saveNotesToStorage() {
    const notes = document.getElementById('notes').value;
    chrome.storage.local.set({ 'researchNotes': notes }, () => {
        alert("Notes saved successfully!");
    });
}

function showResult(content) {
    document.getElementById('results').innerHTML = `
        <div class="result-item">
            <div class="result-content">${content}</div>
        </div>
    `;
}
