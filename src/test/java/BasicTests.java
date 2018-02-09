import com.makemytrip.service.VoiceProcessor;
import org.junit.Test;

/**
 * Created by karthik on 09/02/18.
 */
public class BasicTests {

    @Test
    public void basicTests() throws Exception {
        // The path to the audio file to transcribe
        String fileName = "/data/voice_sample.flac";

        VoiceProcessor quickStartSample = new VoiceProcessor();
        quickStartSample.process(fileName);
    }

}
