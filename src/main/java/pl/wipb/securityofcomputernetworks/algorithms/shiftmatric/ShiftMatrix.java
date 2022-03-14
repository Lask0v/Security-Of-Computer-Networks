package pl.wipb.securityofcomputernetworks.algorithms.shiftmatric;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

@RestController
@RequestMapping("/shift-matrix")
public class ShiftMatrix {
    private static final HashMap<Integer, LinkedList<Character>> map = new HashMap<>();

    @GetMapping("/encrypt")
    public String main(@RequestParam String message, @RequestParam String key) {
        int[] sequence = Arrays.stream(key.trim().split("-")).mapToInt(Integer::parseInt).toArray();

        int temp_counter = 0;
        int counter = 0;

        for (int i = 0; i < message.length(); i++) {
            map.putIfAbsent(counter, new LinkedList<>());
            if (temp_counter++ >= sequence.length - 1) {
                counter++;
                temp_counter = 0;
            }
        }

        counter = 0;
        temp_counter = 0;
        for (int i = 0; i < message.length(); i++) {
            map.get(counter).add(message.charAt(i));
            if (temp_counter++ >= sequence.length - 1) {
                ++counter;
                temp_counter = 0;
            }
        }

        StringBuilder cipheredText = new StringBuilder();


        map.keySet().forEach(keyMap -> {
            for (int sequenceKey : sequence) {
                if (map.get(keyMap).size() >= sequenceKey)
                    cipheredText.append(map.get(keyMap).get(sequenceKey - 1));
            }
        });

        return cipheredText.toString();
    }
}