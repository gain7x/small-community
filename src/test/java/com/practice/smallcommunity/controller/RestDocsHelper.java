package com.practice.smallcommunity.controller;

import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.snippet.Snippet;

public abstract class RestDocsHelper {

    private RestDocsHelper() {

    }

    public static RestDocumentationResultHandler generateDocument(String domain, Snippet... snippets) {
        return MockMvcRestDocumentation.document(domain + "/{method-name}",
            operationRequest -> Preprocessors.prettyPrint().preprocess(operationRequest),
            operationResponse -> Preprocessors.prettyPrint().preprocess(operationResponse),
            snippets
        );
    }
}
