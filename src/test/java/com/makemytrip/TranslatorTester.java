package com.makemytrip;

import com.makemytrip.data.TextContent;
import com.makemytrip.data.TranslateRequest;
import com.makemytrip.service.Translator;
import org.junit.Test;

/**
 * Tests for translation service.
 */
public class TranslatorTester {

    @Test
    public void translationTests() {
        Translator translator = new Translator();
        TextContent translatedContent = translator.translate(new TranslateRequest().setContent("नमस्ते दुनिया")
                .setSourceLanguageCode("hi").setTargetLanguageCode("en"));

        System.out.println(translatedContent.getContent());
    }

    @Test
    public void translationTestsWithMixedContent() {
        Translator translator = new Translator();
        TextContent translatedContent = translator.translate(new TranslateRequest().setContent("hi and नमस्ते दुनिया")
                .setSourceLanguageCode("hi").setTargetLanguageCode("en"));

        System.out.println(translatedContent.getContent());
    }
}
