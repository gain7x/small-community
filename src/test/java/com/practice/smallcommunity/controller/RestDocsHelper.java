package com.practice.smallcommunity.controller;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.util.StringUtils;

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

    public static ConstrainedFields getConstrainedFields(Class<?> input) {
        return new ConstrainedFields(input);
    }

    public static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        public FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                .collectionToDelimitedString(this.constraintDescriptions
                    .descriptionsForProperty(path), ". ")));
        }
    }
}
