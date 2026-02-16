OVERVIEW:

GenAI Tutor is a full-stack Generative AI powered tutoring system built using Spring Boot (Java) and React.
It implements a Retrieval-Augmented Generation (RAG) pipeline that allows users to:
Upload documents (PDF)
Automatically chunk and embed content
Perform vector similarity search
Generate context-aware AI answers
Take AI-generated quizzes
This project demonstrates backend AI system design, embedding pipelines, vector search, and LLM integration.

ARCHITECTURE (RAG Pipeline)

 User uploads document (PDF)
 Text is chunked using TextChunker
 Embeddings are generated
 Embeddings stored via DocumentChunkRepository
 Vector similarity search retrieves relevant chunks
 LLM generates final context-aware answer

 Features

 AI-powered chat system
 PDF document ingestion
 Text chunking & embedding generation
 Vector similarity search
 Retrieval-Augmented Generation (RAG)
 AI-generated quizzes
 React frontend interface
 Mock LLM support for testing

TECH STACK

Backend:
Java
Spring Boot
Maven
REST APIs
Embedding Service
Vector Search Logic

Frontend:
React
Vite
Tailwind CSS

PROJECT STRUCTURE

backend/
 ├── controller/
 ├── service/
 ├── repository/
 ├── model/
 ├── dto/
 └── config/

frontend/
 ├── src/
 └── public/

How to Run
Backend:
mvn spring-boot:run

Frontend:
cd frontend
npm install
npm run dev