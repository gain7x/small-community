package com.practice.smallcommunity.testutils.interfaces;

import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.util.StringUtils;

public abstract class RestDocsHelper {

    private RestDocsHelper() {

    }

    public static RestDocumentationResultHandler generateDocument(String domain, Snippet... snippets) {
        return generateDocument(domain, "{method-name}", snippets);
    }

    public static RestDocumentationResultHandler generateDocument(String domain, String name, Snippet... snippets) {
        return MockMvcRestDocumentation.document(domain + "/" + name,
            operationRequest -> Preprocessors.prettyPrint().preprocess(operationRequest),
            operationResponse -> Preprocessors.prettyPrint().preprocess(operationResponse),
            snippets
        );
    }

    public static PayloadSubsectionExtractor<?> baseData() {
        return beneathPath("data").withSubsectionId("data-base");
    }

    public static PayloadSubsectionExtractor<?> collectionData() {
        return beneathPath("data").withSubsectionId("data-collection");
    }

    public static PayloadSubsectionExtractor<?> pageData() {
        return beneathPath("data").withSubsectionId("data-page");
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
