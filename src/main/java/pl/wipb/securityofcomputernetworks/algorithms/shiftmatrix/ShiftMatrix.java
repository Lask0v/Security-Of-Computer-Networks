package pl.wipb.securityofcomputernetworks.algorithms.shiftmatrix;

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

    @GetMapping("/encrypt")
    public String encrypt(@RequestParam String message, @RequestParam String key) {
        int[] sequence = Arrays.stream(key.trim().split("-")).mapToInt(Integer::parseInt).toArray();
        HashMap<Integer, LinkedList<Character>> map = new HashMap<>();

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

        int rowSize = sequence.length;
        int lastRowSize = map.get(sequence.length-1).size();

        if(lastRowSize < rowSize){
            // wypełnienie spacjami pustych miejsc w ostatnim wierszu
            for(int j=lastRowSize; j<rowSize; j++){
                map.get(counter).add(' ');
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

    @GetMapping("/decrypt")
    public String decrypt(@RequestParam String message, @RequestParam String key) {
        int[] sequence = Arrays.stream(key.trim().split("-")).mapToInt(Integer::parseInt).toArray();
        int[] revertedSequence = new int[sequence.length];
        HashMap<Integer, LinkedList<Character>> map = new HashMap<>();

        // odwracanie klucza
        for (int i=0; i<sequence.length; i++){
            revertedSequence[i] = sequence[sequence.length-i-1];
        }

        int temp_counter = 0;
        int counter = 0;

        for (int i = 0; i < message.length(); i++) {
            map.putIfAbsent(counter, new LinkedList<>());
            if (temp_counter++ >= revertedSequence.length - 1) {
                counter++;
                temp_counter = 0;
            }
        }

        counter = 0;
        temp_counter = 0;
        for (int i = 0; i < message.length(); i++) {
            map.get(counter).add(message.charAt(i));
            if (temp_counter++ >= revertedSequence.length - 1) {
                ++counter;
                temp_counter = 0;
            }
        }

        // odszyfrowywanie tekstu
        StringBuilder cipheredText = new StringBuilder();
        map.keySet().forEach(keyMap -> {
            for (int sequenceKey : revertedSequence) {
                if (map.get(keyMap).size() >= sequenceKey)
                    if(!map.get(keyMap).get(sequenceKey-1).equals(' ')){    // pominięcie znaków spacji, które służyły przy szyfrowaniu jako wypełnienie niepełnego wiersza
                        cipheredText.append(map.get(keyMap).get(sequenceKey - 1));
                    }
            }
        });

        return cipheredText.toString();
    }
}