package pl.wipb.securityofcomputernetworks.algorithms.caesars.generator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wipb.securityofcomputernetworks.algorithms.generator.Generator;
import pl.wipb.securityofcomputernetworks.utils.UtilsEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class SSR {
    private final Generator generator;

    SSR(Generator generator) {
        this.generator = generator;
    }

    @GetMapping("/SSR")
    public String SSR(String message, String polynomial, String seed) throws Exception {
        validate(message, seed);
        String generatedByLfsr = this.generator.gen(polynomial, seed, message.length());

        // rozkodowanie String'a do postaci tablicy intów
        int[] messageArray = UtilsEncoder.toBinaryCode(message);
        int[] seedArray = UtilsEncoder.toBinaryCode(seed);
        // usuniecie zbednych znaków typu [,], ","
        int[] generatedByLfsrCode = Arrays.stream(generatedByLfsr
                        .replaceAll("\\W", "").split(""))
                .mapToInt(Integer::valueOf).toArray();
        // lista przechowujaca wartosci 1 (potegi) -> 1010 da nam 0 i 2 bo 1 w ziarnie znajduje sie na 1-szym i trzecim bicie
        List<Integer> integers = setPriorityXor(seedArray);
        StringBuilder resultAppender = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            resultAppender.append(UtilsEncoder.XOR(generatedByLfsrCode[i], messageArray[i]));
            System.arraycopy(generatedByLfsrCode, 0, generatedByLfsrCode, 1, generatedByLfsrCode.length - 1);
            generatedByLfsrCode[0] = UtilsEncoder.processXOR(integers, generatedByLfsrCode);
        }
        return resultAppender.toString();
    }


    private static void validate(String message, String seed) {
        String regex = "[0-9]+";
        if (!message.matches(regex) || !seed.matches(regex)) {
            System.err.println("Provided incorrect data");
            System.exit(0);
        }
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



}


