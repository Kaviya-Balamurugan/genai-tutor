function sendQuestion() {
    const questionInput = document.getElementById("question");
    const chatBox = document.getElementById("chat-box");

    const question = questionInput.value.trim();
    if (!question) return;

    // Show user message
    chatBox.innerHTML += `<div class="user">${question}</div>`;
    questionInput.value = "";

    fetch(`/chat?question=${encodeURIComponent(question)}`)
        .then(res => res.text())
        .then(answer => {
            chatBox.innerHTML += `<div class="bot">${answer}</div>`;
            chatBox.scrollTop = chatBox.scrollHeight;
        })
        .catch(() => {
            chatBox.innerHTML += `<div class="bot">Error connecting to server</div>`;
        });
}
