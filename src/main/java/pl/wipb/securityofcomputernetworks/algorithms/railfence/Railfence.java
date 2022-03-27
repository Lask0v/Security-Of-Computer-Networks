package pl.wipb.securityofcomputernetworks.algorithms.railfence;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rail-Fence")
class Railfence {

    @GetMapping("/encrypt")
    public String encrypt(@RequestParam String text, @RequestParam int key) {
        char[][] rail = new char[key][(text.length())];
        boolean goDown = false;
        int row = 0;
        int col = 0;

        for (int i = 0; i < text.length(); i++) {
            if (row == 0 || row == key - 1) {
                goDown = !goDown;

            }
            rail[row][col++] = text.charAt(i);
            if (goDown) {
                row++;
            } else {
                row--;
            }
        }
        LinkedList<Character> characters = new LinkedList<>();
        for (int i = 0; i < key; i++)
            for (int j = 0; j < text.length(); j++)
                if (rail[i][j] != '\u0000') {
                    characters.addLast(rail[i][j]);
                }

        return convertListToString(characters);
    }

    private String convertListToString(LinkedList<Character> characters) {
        return characters.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    @GetMapping("/decrypt")
    public String decrypt(@RequestParam String cipher, @RequestParam int key) {
        char[][] rail = new char[key][(cipher.length())];
        for (int i = 0; i < key; i++)
            for (int j = 0; j < cipher.length(); j++)
                rail[i][j] = '\n';

        boolean goDown = true;
        int row = 0;
        int col = 0;

        for (int i = 0; i < cipher.length(); i++) {
            if (row == 0) {
                goDown = true;
            }
            if (row == key - 1) {
                goDown = false;
            }
            rail[row][col++] = '*';
            if (goDown) {
                row++;
            } else {
                row--;
            }
        }

        int index = 0;
        for (int i = 0; i < key; i++)
            for (int j = 0; j < cipher.length(); j++)
                if (rail[i][j] == '*' && index < cipher.length())
                    rail[i][j] = cipher.charAt(index++);
        row = 0;
        col = 0;
        LinkedList<Character> characters = new LinkedList<>();

        for (int i = 0; i < cipher.length(); i++) {
            if (row == 0) {
                goDown = true;
            }
            if (row == key - 1) {
                goDown = false;
            }

            if (rail[row][col] != '*') {
                characters.addLast(rail[row][col++]);
            }

            if (goDown) {
                row++;
            } else {
                row--;
            }
        }
        return convertListToString(characters);
    }

}
