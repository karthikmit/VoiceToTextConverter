package com.makemytrip.data;

/**
 * Result POJO from VoiceBot API.
 */
public class VoiceToTextResult {
    private String text;
    private String score;

    public VoiceToTextResult() {
    }

    public String getText() {
        return text;
    }

    public VoiceToTextResult setText(String text) {
        this.text = text;
        return this;
    }

    public String getScore() {
        return score;
    }

    public VoiceToTextResult setScore(String score) {
        this.score = score;
        return this;
    }

    @Override
    public String toString() {
        return "VoiceToTextResult{" +
                "score='" + score + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
