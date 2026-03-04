package io.shaama.textrag.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorStoreService {

    // TODO: Exercise 3 - Inject VectorStore (see README)
    // private final VectorStore vectorStore;

    public String addToVectorStore(List<String> contents){
        // TODO: Exercise 3 - Implement vector store logic (see README)
        
        // List<Document> documents = contents
        //         .parallelStream()
        //         .map(Document::new)
        //         .toList();

        // vectorStore.add(documents);

        // return "Data Added";
        
        return "TODO: Implement";
    }
}
