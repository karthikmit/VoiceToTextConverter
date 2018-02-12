package com.makemytrip.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TranscoderUtils {

    public static final String FFMPEG_EXE_PATH = "/opt/chatbot/lib/ffmpeg/3.4.1/bin/ffmpeg";
    public static final String FFMPEG_SAMPLE_RATE = "44100";

    private static Logger logger = LoggerFactory.getLogger(TranscoderUtils.class);

    @Autowired
    private PropertiesFetcher propertiesFetcher;

    public TranscoderUtils(){

    }

    public boolean encodeAudio(String source, String target) {

        try {
            String command = this.propertiesFetcher.fetchProperty("ffmpeg.path", FFMPEG_EXE_PATH) +
                    " -i " +
                    source +
                    " -ar " +
                    this.propertiesFetcher.fetchProperty("ffmpeg.sample.rate", FFMPEG_SAMPLE_RATE) +
                    " -c:a flac -ac 1 " +
                    target;
            Process process = Runtime.getRuntime ().exec (command);
            int responseCode = process.waitFor();
            logger.debug("Process return code for " + source + " :: " + responseCode);
            return responseCode == 0;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Transcode failed :: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("Transcode process interrupted :: " + e.getMessage());
        }

        return false;
    }
}