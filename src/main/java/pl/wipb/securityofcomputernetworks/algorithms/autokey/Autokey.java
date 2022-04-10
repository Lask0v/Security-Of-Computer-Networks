package pl.wipb.securityofcomputernetworks.algorithms.autokey;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.wipb.securityofcomputernetworks.algorithms.generator.Generator;

import java.util.Arrays;

@RestController
@RequestMapping("/autokey")
public class Autokey {
    private final Generator generator;

    public Autokey(Generator generator) {
        this.generator = generator;
    }

    @GetMapping("/encrypt")
    public String encrypt(@RequestParam String message, @RequestParam String seed, @RequestParam String polynomial) throws Exception {
        StringBuilder password = new StringBuilder();
        message = convertStringToBinary(message);
        int sum = 0;
        for (int i = 0; i < message.length(); i++) {
            int generated = Integer.parseInt(generator.gen(polynomial, seed, 1));
            sum += message.charAt(i);
            sum += generated;
            sum %= 2;
            password.append(sum);
            seed = sum + seed.substring(0, seed.length() - 1);
            sum = 0;
        }
        return password.toString();
    }

    @GetMapping("/decrypt")
    public String decrypt(@RequestParam String message, @RequestParam String seed, @RequestParam String polynomial) throws Exception {
        String password = "";
        int sum = 0;
        for (int i = 0; i < message.length(); i++) {
            int generated = Integer.parseInt(generator.gen(polynomial, seed, 1));
            sum += message.charAt(i);
            sum += generated;
            sum %= 2;
            password += sum;
            seed = message.charAt(i) + seed.substring(0, seed.length() - 1);
            sum = 0;
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
