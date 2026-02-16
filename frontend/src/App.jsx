import { useState, useRef, useEffect } from "react";
import ReactMarkdown from "react-markdown";

export default function App() {
  const [question, setQuestion] = useState("");
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);

  const bottomRef = useRef(null);
  const inputRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, loading]);

  const autoResize = () => {
    const el = inputRef.current;
    if (!el) return;
    el.style.height = "auto";
    el.style.height = el.scrollHeight + "px";
  };

  const sendMessage = async () => {
    if (!question.trim() || loading) return;

    const text = question;
    setMessages((prev) => [...prev, { role: "user", text }]);
    setQuestion("");
    setLoading(true);

    if (inputRef.current) inputRef.current.style.height = "56px";

    try {
      const res = await fetch(
        text.toLowerCase().includes("quiz")
          ? `http://localhost:8080/quiz?topic=${encodeURIComponent(text)}`
          : `http://localhost:8080/chat?question=${encodeURIComponent(text)}`
      );

      const answer = await res.text();
      setMessages((prev) => [...prev, { role: "ai", text: answer }]);
    } catch {
      setMessages((prev) => [
        ...prev,
        { role: "ai", text: "❌ Unable to connect to server." },
      ]);
    }

    setLoading(false);
  };

  return (
    <div style={styles.page}>
      <div style={styles.header}>🤖 GenAI Tutor</div>

      <div style={styles.chatWrapper}>
        <div style={styles.chat}>
          {messages.map((m, i) => {
            const isQuiz =
              m.role === "ai" && m.text.trim().includes("Q1.");

            return (
              <div
                key={i}
                style={{
                  display: "flex",
                  justifyContent:
                    m.role === "user" ? "flex-end" : "flex-start",
                }}
              >
                <div
                  style={m.role === "user" ? styles.userBubble : styles.aiBubble}
                >
                  {isQuiz ? (
                    <QuizBlock quizText={m.text} />
                  ) : (
                    <ReactMarkdown>{m.text}</ReactMarkdown>
                  )}
                </div>
              </div>
            );
          })}

          {loading && <p style={styles.typing}>Typing…</p>}
          <div ref={bottomRef} />
        </div>
      </div>

      <div style={styles.footer}>
        <div style={styles.inputWrapper}>
          <textarea
            ref={inputRef}
            style={styles.input}
            rows={1}
            placeholder="Ask anything…"
            value={question}
            onChange={(e) => {
              setQuestion(e.target.value);
              autoResize();
            }}
            onKeyDown={(e) => {
              if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
              }
            }}
          />
          <button style={styles.sendBtn} onClick={sendMessage}>
            Send
          </button>
        </div>
      </div>
    </div>
  );
}

/* ================= QUIZ COMPONENT ================= */

function QuizBlock({ quizText }) {
  const questions = quizText
    .split(/\n(?=Q\d+\.)/)
    .filter(Boolean);

  const parsed = questions.map((q, i) => {
    const lines = q.split("\n").filter(Boolean);
    return {
      id: i,
      question: lines[0],
      options: lines.filter((l) => /^[A-D]\./.test(l)),
      correct: lines
        .find((l) => l.includes("[CORRECT"))
        ?.match(/\[CORRECT:(.)\]/)?.[1],
    };
  });

  const [answers, setAnswers] = useState({});
  const [submitted, setSubmitted] = useState(false);

  const score = parsed.reduce(
    (s, q, i) => s + (answers[i] === q.correct ? 1 : 0),
    0
  );

  return (
    <div>
      {parsed.map((q, i) => (
        <div key={i} style={styles.quizBox}>
          <p style={styles.quizQuestion}>{q.question}</p>
          {q.options.map((opt) => {
            const key = opt[0];
            return (
              <label key={key} style={styles.option}>
                <input
                  type="radio"
                  name={`q-${i}`}
                  disabled={submitted}
                  onChange={() =>
                    setAnswers((prev) => ({ ...prev, [i]: key }))
                  }
                />{" "}
                {opt}
              </label>
            );
          })}
        </div>
      ))}

      {!submitted && (
        <button style={styles.submitBtn} onClick={() => setSubmitted(true)}>
          Submit Quiz
        </button>
      )}

      {submitted && (
        <p style={{ marginTop: 12, fontWeight: 600 }}>
          Score: {score} / {parsed.length}
        </p>
      )}
    </div>
  );
}

/* ================= STYLES ================= */

const styles = {
  page: {
    height: "100vh",
    display: "flex",
    flexDirection: "column",
    fontFamily: "Inter, system-ui, sans-serif",
  },

  header: {
    height: 60,
    display: "flex",
    alignItems: "center",
    paddingLeft: 28,
    fontSize: 20,
    fontWeight: 600,
    borderBottom: "1px solid #e5e7eb",
  },

  chatWrapper: {
    flex: 1,
    overflowY: "auto",
    padding: "24px 0 160px",
  },

  chat: {
    maxWidth: 820,
    margin: "0 auto",
    display: "flex",
    flexDirection: "column",
    gap: 24,
    padding: "0 18px",
  },

  userBubble: {
    background: "#10a37f",
    color: "white",
    padding: "12px 18px",
    borderRadius: 18,
    maxWidth: "60%",
  },

  aiBubble: {
    background: "#f3f4f6",
    padding: "16px 20px",
    borderRadius: 18,
    maxWidth: "75%",
  },

  quizBox: {
    marginBottom: 20,
  },

  quizQuestion: {
    fontWeight: 600,
    marginBottom: 8,
  },

  option: {
    display: "block",
    marginLeft: 10,
    cursor: "pointer",
  },

  submitBtn: {
    marginTop: 12,
    padding: "8px 16px",
    borderRadius: 8,
    border: "none",
    background: "#10a37f",
    color: "white",
    fontWeight: 600,
    cursor: "pointer",
  },

  typing: {
    color: "#6b7280",
  },

  footer: {
    position: "fixed",
    bottom: 0,
    left: 0,
    right: 0,
    borderTop: "1px solid #e5e7eb",
    background: "#fff",
    padding: "16px 0",
  },

  inputWrapper: {
    maxWidth: 760,
    margin: "0 auto",
    display: "flex",
    gap: 12,
    padding: "0 20px",
  },

  input: {
    flex: 1,
    resize: "none",
    borderRadius: 14,
    border: "1px solid #d1d5db",
    padding: "14px 16px",
    fontSize: 16,
  },

  sendBtn: {
    background: "#10a37f",
    color: "white",
    border: "none",
    borderRadius: 14,
    padding: "0 24px",
    fontWeight: 600,
    cursor: "pointer",
  },
};
