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

        int rowCount = map.size();

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
        int lastRowSize = map.get(rowCount-1).size();
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
        HashMap<Integer, LinkedList<Character>> map = new HashMap<>();
        int[] keys = Arrays.stream(key.trim().split("-")).mapToInt(Integer::parseInt).toArray();
        int[] sortedKeys = Arrays.stream(keys).sorted().toArray();

        // inicjalizacja macierzy
        for (int i = 0; i < keys.length; i++) {
            map.putIfAbsent(keys[i], new LinkedList<>());
        }

        // uzupelnianie macierzy

        int rowCount = 0;
        int inRowCount = 0;
        for (int i = 0; i < message.length(); i++) {
            map.get(keys[inRowCount]).add(message.charAt(i));
            inRowCount++;
            if(inRowCount >= keys.length){
                inRowCount = 0;
                rowCount++;
            }
        }

        // odczytanie macierzy
        StringBuilder newMessage = new StringBuilder();
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < sortedKeys.length; j++) {
                if(!map.get(sortedKeys[j]).get(i).equals(' ')) {
                    newMessage.append(map.get(sortedKeys[j]).get(i));
                }
            }
        }
        return newMessage.toString();

    }
}