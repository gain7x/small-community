package com.practice.smallcommunity.infrastructure;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class CustomMySQLDialect extends MySQL8Dialect {

    public CustomMySQLDialect() {
        super();
        registerFunction("match", new SQLFunctionTemplate(
            StandardBasicTypes.DOUBLE, "match(?1) against (?2 in boolean mode)"));
    }
}
