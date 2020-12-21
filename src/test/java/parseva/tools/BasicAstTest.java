package parseva.tools;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class BasicAstTest extends AbstractTestSupport{

    @Test
    public void testAntlrTest() throws IOException {
        verifyAst(getPath("Test.txt"), getPath("Test.java"));
    }

    @Test
    public void testPSVM() throws IOException {
        verifyAst(getPath("InputPSVM.txt"), getPath("InputPSVM.java"));
    }

/* TODO: add support to grammar for records
    @Test
    public void testBasicRecord() throws IOException {
        final String expectedAstPrintFilename =
            "src/test/resources/org/antlr4javaparser/antlr-test/Test.txt";
        final String actualJavaFilename =
            "src/test/resources/org/antlr4javaparser/antlr-test/Test.java";
        verifyAst(getPath("InputBasicRecord.txt"), getPath("InputBasicRecord.java"));
    }
    */
    /*TODO: add support to grammar for text blocks */
    /*TODO: add support to grammar for sealed classes */
    /*TODO: add support to grammar for pattern matching for instanceof */
    /*TODO: add support to grammar for switch expressions */

    @Override
    protected String getPackageLocation() {
        return "parseva.tools.grammar/";
    }
}