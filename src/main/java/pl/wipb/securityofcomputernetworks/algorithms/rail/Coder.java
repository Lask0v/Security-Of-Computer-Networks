package pl.wipb.securityofcomputernetworks.algorithms.rail;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/rail-fence")
public class Coder {

    @GetMapping
    public String algorithm(@RequestParam String providedString, @RequestParam int n) {
        HashMap<Integer, List<Character>> integerListHashMap = new HashMap<>();

        char[] sequence = providedString.toCharArray();
        initHashMap(n, integerListHashMap, sequence);
        fillHashMap(n, integerListHashMap, sequence);

        StringBuilder result = new StringBuilder();
        for (List<Character> value : integerListHashMap.values()) {
            for (Character character : value) {
                result.append(character);
            }
        }
        return result.toString();
    }

    private void fillHashMap(int n, HashMap<Integer, List<Character>> integerListHashMap, char[] sequence) {
        for (int i = 0; i < sequence.length; i++) {
            integerListHashMap.get(i % n).add(sequence[i]);
        }
    }

    private void initHashMap(int n, HashMap<Integer, List<Character>> hashMap, char[] sequence) {
        for (int i = 0; i < sequence.length; i++) {
            hashMap.put(i % n, new ArrayList<>());
        }
    }
}
