package pl.wipb.securityofcomputernetworks.algorithms.vigenere;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vigenere")
public class Vigenere {
    private final int NUM_OF_LETTERS_IN_ALPHABET = 26;

    @GetMapping("/encrypt")
    public String encrypt(@RequestParam String message, @RequestParam String key) {
        char[] input = message.toCharArray();
        char[] k = key.toCharArray();
        char[] encrypted = new char[input.length];

        for (int i = 0; i < input.length; i++) {
            encrypted[i] = encrypt(input[i], k[i % k.length]);
        }
        return String.valueOf(encrypted);
    }

    private char encrypt(char input, char key) {
        return (char) ((key - 'A' + (input - 'A')) % NUM_OF_LETTERS_IN_ALPHABET + 'A');
    }

    @GetMapping("/decrypt")
    public String decrypt(@RequestParam String message, @RequestParam String key) {
        char[] input = message.toCharArray();
        char[] k = key.toCharArray();
        char[] decrypted = new char[input.length];

        for (int i = 0; i < input.length; i++) {
            decrypted[i] = decrypt(input[i], k[i % k.length]);
        }
        return String.valueOf(decrypted);
    }

    private char decrypt(char input, char key) {
        return (char) ((26 - (key - 'A')  + (input - 'A')) % NUM_OF_LETTERS_IN_ALPHABET + 'A');
    }
}
