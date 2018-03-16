package com.makemytrip.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.*;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import com.makemytrip.data.VoiceToTextResult;
import com.makemytrip.exception.EncodeFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class VoiceProcessor {

    @Autowired private TranscoderUtils transcoder;
    @Autowired private PropertiesFetcher propertiesFetcher;
    @Autowired private TTSCoder ttsCoder;

    private static Logger logger = LoggerFactory.getLogger(VoiceProcessor.class);

    public InputStream respond(String fileName) throws Exception {
        Map<String, Object> resultsHolder = process(fileName);
        List<VoiceToTextResult> results = (List<VoiceToTextResult>) resultsHolder.get("results");
        if(results.size() > 0) {
            VoiceToTextResult textResult = results.get(0);
            String text = textResult.getText();

            return ttsCoder.convertTextToVoice(text);
        }
        return null;
    }

    public Map<String, Object> process(String fileName) throws Exception {

        String randomName = UUID.randomUUID().toString();
        String flacFormatted = "/data/" + randomName + ".flac";
        if(fileName.endsWith("flac")) {
            flacFormatted = fileName;
        } else {
            boolean encodeSuccessful = transcoder.encodeAudio(fileName, flacFormatted);

            if(!encodeSuccessful || !new File(flacFormatted).exists()) {
                String message = "Failed to transcode the file :: " + fileName;
                logger.error(message);
                throw new EncodeFailedException(message);
            }
        }

        // Instantiates a client
        SpeechClient speech = SpeechClient.create();

        // Reads the audio file into memory
        File targetFile = new File(flacFormatted);
        Path path = Paths.get(flacFormatted);
        byte[] data = Files.readAllBytes(path);
        ByteString audioBytes = ByteString.copyFrom(data);

        String sampleRateString = this.propertiesFetcher.fetchProperty("ffmpeg.sample.rate", TranscoderUtils.FFMPEG_SAMPLE_RATE);

        // Builds the sync recognize request
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(AudioEncoding.FLAC)
                .setSampleRateHertz(Integer.parseInt(sampleRateString))
                .setLanguageCode("hi-IN")
                .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build();

        // Performs speech recognition on the audio file
        RecognizeResponse response = speech.recognize(config, audio);
        List<SpeechRecognitionResult> results = response.getResultsList();

        logger.debug("Results Size :: "  +results.size());

        List<VoiceToTextResult> voiceToTextResults = new ArrayList<>();
        for (SpeechRecognitionResult result: results) {
            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
            String transcript = alternative.getTranscript();
            Float confidence = alternative.getConfidence();
            logger.info("Transcription: ", transcript + " Confidence: " + confidence);

            VoiceToTextResult voiceToTextResult = new VoiceToTextResult().setText(transcript).setScore(confidence.toString());
            voiceToTextResults.add(voiceToTextResult);
        }
        speech.close();
        boolean status = targetFile.delete();
        if(!status) {
            logger.error("Target file failed to be deleted :: " + targetFile.getAbsolutePath());
        }

        return new HashMap<String, Object>() {{
            put("results", voiceToTextResults);
        }};
    }
}