package com.makemytrip;

import com.makemytrip.service.TTSCoder;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by karthik on 09/03/18.
 */
public class TTSCoderTests {

    @Test
    public void testBasics() throws IOException {
        TTSCoder ttsCoder = new TTSCoder();

        InputStream voiceStream = ttsCoder.convertTextToVoice("Hello Karthik, How are you ?");
        FileUtils.copyInputStreamToFile(voiceStream, new File("/tmp/hello.mp3"));
    }

    @Test
    public void testStoreInS3() throws IOException {
        TTSCoder ttsCoder = new TTSCoder();

        String storedFile = ttsCoder.convertTextToVoiceAndStoreInS3("Diyon is a very good boy. Don't disturb appa. Go and watch TV.");
        System.out.println(storedFile);
    }
}
