package com.makemytrip.data;

/**
 * Text Content Abstraction
 */
public class TextContent {

    private String content;
    private String languageCode;

    public TextContent() {

    }

    public String getContent() {
        return content;
    }

    public TextContent setContent(String content) {
        this.content = content;
        return this;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public TextContent setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
        return this;
    }

    @Override
    public String toString() {
        return "TextContent{" +
                "content='" + content + '\'' +
                ", languageCode='" + languageCode + '\'' +
                '}';
    }
}
