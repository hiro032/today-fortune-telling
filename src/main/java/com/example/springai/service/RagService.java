package com.example.springai.service;

import com.example.springai.config.RagConfiguration;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final RagConfiguration ragConfiguration;
    private final TokenTextSplitter textSplitter;

    public RagService(VectorStore vectorStore,
                     ChatModel chatModel,
                     RagConfiguration ragConfiguration,
                     TokenTextSplitter textSplitter) {
        this.vectorStore = vectorStore;
        this.chatClient = ChatClient.builder(chatModel).build();
        this.ragConfiguration = ragConfiguration;
        this.textSplitter = textSplitter;
    }

    @PostConstruct
    public void init() {
        // Load documents into vector store on startup
        ragConfiguration.loadDocuments(vectorStore, textSplitter);
    }

    /**
     * Perform RAG-based query
     */
    public String queryWithRag(String question) {
        // Search for similar documents
        List<Document> similarDocuments = vectorStore.similaritySearch(
            SearchRequest.query(question).withTopK(3)
        );

        if (similarDocuments.isEmpty()) {
            return chatClient.prompt()
                .user(question)
                .call()
                .content();
        }

        // Build context from retrieved documents
        String context = similarDocuments.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n\n"));

        // Create prompt with context
        String prompt = String.format("""
            Use the following context to answer the question.
            If you cannot answer based on the context, say so.

            Context:
            %s

            Question: %s

            Answer:
            """, context, question);

        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
    }

    /**
     * Get similar documents for a query
     */
    public List<String> getSimilarDocuments(String query) {
        List<Document> documents = vectorStore.similaritySearch(
            SearchRequest.query(query).withTopK(3)
        );

        return documents.stream()
            .map(doc -> doc.getContent().substring(0, Math.min(200, doc.getContent().length())) + "...")
            .collect(Collectors.toList());
    }
}
