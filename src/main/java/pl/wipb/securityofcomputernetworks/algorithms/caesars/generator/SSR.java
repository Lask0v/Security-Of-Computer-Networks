package pl.wipb.securityofcomputernetworks.algorithms.caesars.generator;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.wipb.securityofcomputernetworks.algorithms.generator.Generator;
import pl.wipb.securityofcomputernetworks.utils.UtilsEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/SSR")
public class SSR {
    private final Generator generator;
    private final Logger logger = Logger.getLogger(getClass().toString());

    SSR(Generator generator) {
        this.generator = generator;
    }

    private static String validate(String message, String seed) {
        String regex = "[0-1]+";
        if (!message.matches(regex) || !seed.matches(regex)) {
            return String.format("Provided incorrect data. Pattern of message: %s and seed: %s must match %s", message, seed, regex);
        }
        return Strings.EMPTY;
    }

    @RequestMapping(
            path = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public byte[] ssrFile(@RequestPart("file") MultipartFile file,
                          @RequestPart("polynomial") String polynomial,
                          @RequestParam("seed") String seed) throws Exception {

        StringBuilder message = new StringBuilder();

        byte[] encode = Base64.getEncoder().encode(file.getBytes());
        for (byte b : encode) {
            message.append(b);
        }
        String text = text123(message.toString(), polynomial, seed);
        return text.getBytes();
    }

    /*
     Endpoint to encrypt text message
     */
    @GetMapping(value = "/text")
    private String text(String message, String polynomial, String seed) throws Exception {
        String generatedByLfsr = this.generator.gen(polynomial, seed, message.length());
        logger.info(generatedByLfsr);
        byte[] x = message.getBytes(StandardCharsets.UTF_8);
        byte[] z = generatedByLfsr.getBytes(StandardCharsets.UTF_8);
        byte[] y = new byte[message.length()];
        for (int i = 0; i < message.length(); i++) {
            y[i] = (byte) UtilsEncoder.XOR(z[i], x[i]);
        }
        return new String(y);
    }


    @GetMapping("/text123")
    private String text123(String message, String polynomial, String seed) throws Exception {
        String generatedByLfsr = this.generator.gen(polynomial, seed, message.length());
        logger.info(generatedByLfsr);
        byte[] x = message.getBytes(StandardCharsets.UTF_8);
        byte[] z = generatedByLfsr.getBytes(StandardCharsets.UTF_8);
        byte[] y = new byte[message.length()];
        for (int i = 0; i < message.length(); i++) {
            y[i] = (byte) UtilsEncoder.XOR(z[i], x[i]);
        }
        org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();
        return new String(base64.encode(new String(y).getBytes()));
    }

}