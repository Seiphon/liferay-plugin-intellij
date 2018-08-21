package de.dm.intellij.liferay.language;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.util.List;

public class AlloyUIScriptLanguageInjectorTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected String getTestDataPath() {
        return "testdata/de/dm/intellij/liferay/language/AlloyUIScriptLanguageInjectorTest";
    }

    public void testJavascriptAttribute() {
        myFixture.configureByFiles("view.jsp", "liferay-aui.tld");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertTrue(strings.contains("alert"));
    }

    public void testJavascriptBody() {
        myFixture.configureByFiles("custom.jsp", "liferay-aui.tld");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertTrue(strings.contains("alert"));
    }

}