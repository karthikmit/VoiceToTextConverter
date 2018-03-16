package com.makemytrip.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * TTSCoder converts text to speech.
 */
@Service
public class TTSCoder {
    private AmazonPolly pollyClient;
    private final Voice voice;
    private AmazonS3 amazonS3;

    public TTSCoder() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials("AKIAIZHNPH77ZY7VPCWA", "cQ9Ye8/m8DwHdiEmQ+uc2OSCe9x9dZbekOxXaacr");
        pollyClient = AmazonPollyClient.builder().withClientConfiguration(new ClientConfiguration())
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.AP_SOUTH_1).build();
        amazonS3 = AmazonS3ClientBuilder.standard().withClientConfiguration(new ClientConfiguration())
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.AP_SOUTH_1).build();
        createIfNotExists("tts-lambda");
        // Create describe voices request.
        DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

        // Synchronously ask Amazon Polly to describe available TTS voices.
        DescribeVoicesResult describeVoicesResult = pollyClient.describeVoices(describeVoicesRequest);
        Optional<Voice> femaleIndianVoice = describeVoicesResult.getVoices().stream()
                .filter(voice1 -> voice1.getLanguageCode().equals("en-IN") && voice1.getGender().equalsIgnoreCase("female"))
                .findAny();
        voice = femaleIndianVoice.orElse(describeVoicesResult.getVoices().get(0));
    }

    private void createIfNotExists(String name) {
        Bucket b = null;
        if (!amazonS3.doesBucketExistV2(name)) {
            try {
                b = amazonS3.createBucket(name);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getErrorMessage());
            }
        }
    }

    public InputStream convertTextToVoice(String text) {

        SynthesizeSpeechRequest synthReq =
                new SynthesizeSpeechRequest().withText(text).withVoiceId(voice.getId())
                        .withOutputFormat(OutputFormat.Mp3);
        SynthesizeSpeechResult synthRes = pollyClient.synthesizeSpeech(synthReq);

        return synthRes.getAudioStream();
    }

    public String convertTextToVoiceAndStoreInS3(String text) throws IOException {
        InputStream inputStream = convertTextToVoice(text);
        String fileName = "temp_tts_store" + ".mp3";
        File tempDestination = new File("/tmp/" + fileName);
        FileUtils.copyInputStreamToFile(inputStream, tempDestination);
        String sha1Hex = DigestUtils.sha1Hex(FileUtils.openInputStream(tempDestination));

        String encodedFileName = sha1Hex + ".mp3";
        PutObjectResult putObjectResult = amazonS3.putObject("tts-lambda", encodedFileName, tempDestination);
        FileUtils.deleteQuietly(tempDestination);
        return "https://s3.ap-south-1.amazonaws.com/tts-lambda/" + sha1Hex + ".mp3";
    }
}
