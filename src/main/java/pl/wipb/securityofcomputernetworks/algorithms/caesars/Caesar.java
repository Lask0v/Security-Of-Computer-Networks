package pl.wipb.securityofcomputernetworks.algorithms.caesars;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/caesar")
public class Caesar {
    private final int NUM_OF_LETTERS_IN_ALPHABET = 26;

    @GetMapping("/encrypt")
    public String encrypt(@RequestParam String message, @RequestParam int key) {
        char[] input = message.toCharArray();
        char[] encrypted = new char [input.length];
        for (int i = 0; i < input.length; i++) {
            encrypted[i] = encrypt(input[i], key);
        }
        return String.valueOf(encrypted);
    }

    private char encrypt(char a, int key) {
        return (char) ((a - 'A' + key) % NUM_OF_LETTERS_IN_ALPHABET + 'A');
    }

    @GetMapping("/decrypt")
    public String decrypt(@RequestParam String message, @RequestParam int key) {
        char[] input = message.toCharArray();
        char[] decrypted = new char [input.length];
        for (int i = 0; i < input.length; i++) {
            decrypted[i] = decrypt(input[i], key);
        }
        return String.valueOf(decrypted);
    }

    private char decrypt(char a, int key) {
        return (char) ((a - 'A' + (NUM_OF_LETTERS_IN_ALPHABET - key)) % NUM_OF_LETTERS_IN_ALPHABET + 'A');
    }
}
