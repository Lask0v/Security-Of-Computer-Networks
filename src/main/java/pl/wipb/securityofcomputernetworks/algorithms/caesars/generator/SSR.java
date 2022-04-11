package pl.wipb.securityofcomputernetworks.algorithms.caesars.generator;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.wipb.securityofcomputernetworks.algorithms.generator.Generator;
import pl.wipb.securityofcomputernetworks.utils.UtilsEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

    @GetMapping("/encrypt")
    public String encrypt(@RequestParam String message, @RequestParam String seed, @RequestParam String polynomial) throws Exception {
        String password = "";
        message = convertStringToBinary(message);
        String lfsr = generator.gen(polynomial, seed, message.length());
        for (int i = 0; i < message.length(); i++) {
            int generatedBit = lfsr.charAt(i);
            int messageBit = message.charAt(i);
            password += generatedBit ^ messageBit;
        }
        return binaryToText(password);
    }

    public static String convertStringToBinary(String input) {

        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(
                    String.format("%8s", Integer.toBinaryString(aChar))
                            .replaceAll(" ", "0")
            );
        }
        return result.toString();

    }

    public static String binaryToText(String binary) {
        return Arrays.stream(binary.split("(?<=\\G.{8})"))
                .parallel()
                .map(eightBits -> (char)Integer.parseInt(eightBits, 2))
                .collect(
                        StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append
                ).toString();
    }

}