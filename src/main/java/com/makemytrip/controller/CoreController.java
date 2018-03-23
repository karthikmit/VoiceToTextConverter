package com.makemytrip.controller;

import com.makemytrip.data.TextContent;
import com.makemytrip.data.TranslateRequest;
import com.makemytrip.service.Translator;
import com.makemytrip.service.VoiceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class CoreController {

    private Logger logger = LoggerFactory.getLogger(CoreController.class);
    private VoiceProcessor voiceProcessor;
    private Translator translator;

    @Autowired
    public CoreController(VoiceProcessor voiceProcessor, Translator translator) {
        this.voiceProcessor = voiceProcessor;
        this.translator = translator;
    }

    @RequestMapping(value = "/api/healthCheck")
    @ResponseBody
    public String healthCheck() {
        return "OK";
    }

    @RequestMapping(value = "/api/translate")
    @ResponseBody
    public TextContent translateText(@RequestBody TranslateRequest request) {
        return translator.translate(request);
    }

    @RequestMapping(value = "/api/upload/voice", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> voiceUpload(MultipartHttpServletRequest request, HttpServletResponse response,
                                           HttpSession session) {
        Iterator<String> itr = request.getFileNames();

        MultipartFile mpf;

        while (itr.hasNext()) {
            mpf = request.getFile(itr.next());
            File file = new File("/data");
            if(!file.exists()){
                file.mkdir();
            }
            String originalFileName = mpf.getOriginalFilename();

            String storageDirectory = file.getPath();
            String pathname = storageDirectory + "/" + originalFileName;
            File newFile = new File(pathname);
            try {
                mpf.transferTo(newFile);
                Map<String, Object> resultMap = this.voiceProcessor.process(pathname);
                // Delete the file.
                boolean status = newFile.delete();
                if(!status) {
                    logger.error("Uploaded file is not deleted :: " + pathname);
                }
                return resultMap;
            } catch(Exception e) {
                String errorMessage = "uploadImage::failed::Could not upload file::" + e.getMessage();
                logger.error(errorMessage + "::" + mpf.getOriginalFilename(), e);
            }
        }

        return new HashMap<>();
    }
}
