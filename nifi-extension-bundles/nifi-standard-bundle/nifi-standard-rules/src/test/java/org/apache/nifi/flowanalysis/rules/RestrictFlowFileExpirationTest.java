package org.apache.nifi.flowanalysis.rules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class RestrictFlowFileExpirationTest extends AbstractFlowAnalaysisRuleTest<RestrictFlowFileExpiration> {
    @Override
    protected RestrictFlowFileExpiration initializeRule() {
        return new RestrictFlowFileExpiration();
    }

    @BeforeEach
    @Override
    public void setup() {
        super.setup();
        setProperty(RestrictFlowFileExpiration.ALLOW_ZERO, "true");
        setProperty(RestrictFlowFileExpiration.MIN_FLOWFILE_EXPIRATION, RestrictFlowFileExpiration.MIN_FLOWFILE_EXPIRATION.getDefaultValue());
        setProperty(RestrictFlowFileExpiration.MAX_FLOWFILE_EXPIRATION, RestrictFlowFileExpiration.MAX_FLOWFILE_EXPIRATION.getDefaultValue());
    }

    @Test
    public void testBadConfiguration() {
        setProperty(RestrictFlowFileExpiration.MIN_FLOWFILE_EXPIRATION, "2 days");
        setProperty(RestrictFlowFileExpiration.MAX_FLOWFILE_EXPIRATION, "1 day");
        assertFalse(rule.customValidate(validationContext).isEmpty());
    }

    @Test
    public void testNoViolations() throws Exception {
        testAnalyzeProcessGroup(
                "src/test/resources/RestrictFlowFileExpiration/RestrictFlowFileExpiration_noViolation.json",
                List.of()
        );
    }

    @Test
    public void testViolationsAllowZeroTrue() throws Exception {
        testAnalyzeProcessGroup(
                "src/test/resources/RestrictFlowFileExpiration/RestrictFlowFileExpiration.json",
                List.of(
                        "e27073f8-0192-1000-cf43-9c41e69eadd2", // connection from output port to funnel, 45 days
                        "e2717989-0192-1000-3d06-d6ae392ca1bd" // connection from UpdateAttribute to funnel, 1 sec
                )
        );
    }

    @Test
    public void testViolationsAllowZeroFalse() throws Exception {
        setProperty(RestrictFlowFileExpiration.ALLOW_ZERO, "false");

        testAnalyzeProcessGroup(
                "src/test/resources/RestrictFlowFileExpiration/RestrictFlowFileExpiration.json",
                List.of(
                        "e26f4ded-0192-1000-b21b-23c498138f39", // connection from GenerateFlowFile to UpdateAttribute, 0 sec
                        "e26fd0d5-0192-1000-ee3d-f90141590475", // connection from funnel to funnel, 0 sec
                        "e27073f8-0192-1000-cf43-9c41e69eadd2", // connection from output port to funnel, 45 days
                        "e2716319-0192-1000-8e8e-1418566faa42", // connection from funnel to UpdateAttribute, 0 sec
                        "e2717989-0192-1000-3d06-d6ae392ca1bd" // connection from UpdateAttribute to funnel, 1 sec
                )
        );
    }
}
