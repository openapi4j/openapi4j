package org.openapi4j.parser.validation.v31;

import org.openapi4j.core.validation.ValidationResults;

public class OAI31Keywords {
    private OAI31Keywords() {
    }

    public static final String IDENTIFIER = "identifier";
    public static final String SUMMARY = "summary";

    public static final ValidationResults.CrumbInfo CRUMB_IDENTIFIER = new ValidationResults.CrumbInfo(IDENTIFIER, false);
    public static final ValidationResults.CrumbInfo CRUMB_SUMMARY = new ValidationResults.CrumbInfo(SUMMARY, false);
}
