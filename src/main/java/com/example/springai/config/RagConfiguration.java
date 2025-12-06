package com.example.springai.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RagConfiguration {

    @Value("classpath:/documents/*.txt")
    private Resource[] documentResources;

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
