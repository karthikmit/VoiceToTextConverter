package com.makemytrip.data;

/**
 * Request for Translation Data structure.
 */
public class TranslateRequest {

    private String content;
    private String sourceLanguageCode;
    private String targetLanguageCode;

    public TranslateRequest() {

    }

    public String getContent() {
        return content;
    }

    public TranslateRequest setContent(String content) {
        this.content = content;
        return this;
    }

    public String getSourceLanguageCode() {
        return sourceLanguageCode;
    }

    public TranslateRequest setSourceLanguageCode(String sourceLanguageCode) {
        this.sourceLanguageCode = sourceLanguageCode;
        return this;
    }

    public String getTargetLanguageCode() {
        return targetLanguageCode;
    }

    public TranslateRequest setTargetLanguageCode(String targetLanguageCode) {
        this.targetLanguageCode = targetLanguageCode;
        return this;
    }

    @Override
    public String toString() {
        return "TranslateRequest{" +
                "content='" + content + '\'' +
                ", sourceLanguageCode='" + sourceLanguageCode + '\'' +
                ", targetLanguageCode='" + targetLanguageCode + '\'' +
                '}';
    }
}
