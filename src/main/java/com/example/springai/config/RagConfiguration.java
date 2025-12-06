package com.example.springai.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RagConfiguration {

    @Value("classpath:/documents/*.txt")
    private Resource[] documentResources;

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.embedding.options.model:nomic-embed-text}")
    private String embeddingModelName;

    /**
     * Create EmbeddingModel bean for Ollama embeddings
     * Only create if not already provided by auto-configuration
     */
    @Bean
    @ConditionalOnMissingBean
    public EmbeddingModel embeddingModel() {
        var ollamaApi = new OllamaApi(ollamaBaseUrl);
        return OllamaEmbeddingModel.builder()
            .withOllamaApi(ollamaApi)
            .withDefaultOptions(OllamaOptions.create().withModel(embeddingModelName))
            .build();
    }

    /**
     * VectorStore using Ollama embeddings
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return new SimpleVectorStore(embeddingModel);
    }

    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }

    /**
     * Load and process documents into the vector store
     */
    public void loadDocuments(VectorStore vectorStore, TokenTextSplitter textSplitter) {
        List<Document> documents = new ArrayList<>();

        try {
            for (Resource resource : documentResources) {
                if (resource.exists() && resource.isReadable()) {
                    TextReader textReader = new TextReader(resource);
                    List<Document> docs = textReader.get();
                    documents.addAll(docs);
                }
            }

            if (!documents.isEmpty()) {
                List<Document> splitDocuments = textSplitter.apply(documents);
                vectorStore.add(splitDocuments);
            }
        } catch (Exception e) {
            // Handle case where no documents exist yet
            System.out.println("No documents found to load. Vector store will be empty.");
        }
    }
}
