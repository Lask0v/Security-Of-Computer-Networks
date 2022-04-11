package pl.wipb.securityofcomputernetworks.algorithms.caesars.generator;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.wipb.securityofcomputernetworks.algorithms.generator.Generator;
import pl.wipb.securityofcomputernetworks.utils.UtilsEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static List<Integer> setPriorityXor(int[] seed) {
        List<Integer> bitQueueXor = new ArrayList<>();
        for (int i = 0; i < seed.length; i++) {
            if (seed[i] == 1) {
                bitQueueXor.add(i);
            }
        }
        return bitQueueXor;
    }

    @GetMapping("/input")
    public String SSR(String message, String polynomial, String seed) throws Exception {
        // walidowanie tylko ziarna i wiadomosci
        String validation = validate(message, seed);
//        if (Strings.isNotEmpty(validation)) {
//            return validation;
//        }

        String generatedByLfsr = this.generator.gen(polynomial, seed, message.length());

        // rozkodowanie String'a do postaci tablicy intów
        int[] messageArray = UtilsEncoder.toBinaryCode(message);
        int[] seedArray = UtilsEncoder.toBinaryCode(seed);
        // usuniecie zbednych znaków typu [,], ","
        int[] generatedByLfsrCode = Arrays.stream(generatedByLfsr
                        .replaceAll("\\W", "").split(""))
                .mapToInt(Integer::valueOf).toArray();
        // lista przechowujaca wartosci 1 (potegi) -> 1010 da nam 0 i 2 bo 1 w ziarnie znajduje sie na 1-szym i trzecim bicie
        List<Integer> positionsOfOne = setPriorityXor(seedArray);
        StringBuilder resultAppender = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            resultAppender.append(UtilsEncoder.XOR(generatedByLfsrCode[i], messageArray[i]));
            System.arraycopy(generatedByLfsrCode, 0, generatedByLfsrCode, 1, generatedByLfsrCode.length - 1);
            generatedByLfsrCode[0] = UtilsEncoder.processXOR(positionsOfOne, generatedByLfsrCode);
        }
        return resultAppender.toString();
    }

    @RequestMapping(
            path = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String ssrFile(@RequestPart("file") MultipartFile file,
                          @RequestPart("polynomial") String polynomial,
                          @RequestParam("seed") String seed) throws Exception {

        StringBuilder message = new StringBuilder();

        for (byte b : file.getBytes()) {
            message.append(b);
        }
        byte[] messageBytes = message.toString().getBytes();
        String generatedByLfsr = this.generator.gen(polynomial, seed, message.length());
        byte[] bytes = generatedByLfsr.getBytes();
        StringBuilder resultAppender = new StringBuilder();

        for (int i = 0; i < messageBytes.length; i++) {
            resultAppender.append(UtilsEncoder.XOR(bytes[i], messageBytes[i]));

        }
        return resultAppender.toString();

        //        String validation = validate(message, seed);
//        if (Strings.isNotEmpty(validation)) {
//            return validation;
//        }
////
//
//        String generatedByLfsr = this.generator.gen(polynomial, seed, message.length());
//        if (generatedByLfsr.equals("Niepoprawny format! Wprowadź wielomiat według wzoru: 1x0+2x1+5x3")) {
//            return "Niepoprawny format! Wprowadź wielomiat według wzoru: 1x0+2x1+5x3";
//        }
//        logger.info(generatedByLfsr);
////
//        // rozkodowanie String'a do postaci tablicy intów
//        int[] generatedByLfsrCode = Arrays.stream(generatedByLfsr
//                        .replaceAll("\\W", "").split(""))
//                .mapToInt(Integer::valueOf).toArray();
//
//
//        int[] messageArray = message.chars().toArray();
//
//        int[] seedArray = UtilsEncoder.toBinaryCode(seed);
//        // usuniecie zbednych znaków typu [,], ","
//        byte[] messageBytes = message.toString().getBytes(StandardCharsets.UTF_8);
//        byte[] generatedByLfsrCodeBytes = Arrays.toString(Arrays.stream(generatedByLfsrCode).toArray()).getBytes(StandardCharsets.UTF_8);
//
//        // lista przechowujaca wartosci 1 (potegi) -> 1010 da nam 0 i 2 bo 1 w ziarnie znajduje sie na 1-szym i trzecim bicie
//        List<Integer> positionsOfOne = setPriorityXor(seedArray);
//        StringBuilder resultAppender = new StringBuilder();
//        byte[] output = new byte[messageBytes.length];
//        for (int i = 0; i < message.length(); i++) {
//            resultAppender.append(UtilsEncoder.XOR(generatedByLfsrCodeBytes[i], messageBytes[i]));
//            System.arraycopy(generatedByLfsrCode, 0, generatedByLfsrCode, 1, generatedByLfsrCode.length - 1);
//            generatedByLfsrCode[0] = UtilsEncoder.processXOR(positionsOfOne, generatedByLfsrCode);
////            int xor = UtilsEncoder.XOR(bytes[i], bytes1[i]);
////            resultAppender.append(xor);
//        }/
//        return resultAppender.toString();
    }
}


