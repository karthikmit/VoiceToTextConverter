package com.makemytrip.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.makemytrip.data.TextContent;
import com.makemytrip.data.TranslateRequest;
import org.springframework.stereotype.Component;

/**
 * Translator will convert the text input from one language to other.
 */
@Component
public class Translator {

    public TextContent translate(TranslateRequest translateRequest) {
        TextContent textContent = new TextContent();
        Translate translateService = TranslateOptions.getDefaultInstance().getService();
        String targetLanguageCode = translateRequest.getTargetLanguageCode();

        Translation translation =
                translateService.translate(
                        translateRequest.getContent(),
                        Translate.TranslateOption.sourceLanguage(translateRequest.getSourceLanguageCode()),
                        Translate.TranslateOption.targetLanguage(targetLanguageCode));

        textContent.setContent(translation.getTranslatedText()).setLanguageCode(targetLanguageCode);
        return textContent;
    }
}
