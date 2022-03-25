package pl.wipb.securityofcomputernetworks.algorithms.shiftmatrix;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/shift-matrix3")
public class ThirdShiftMatrix {

    @Getter
    public class Key {
        // Kolejność w alfabecie względem pozostałych liter w kluczu
        Integer number;
        Character letter;

        public Key(Character letter) {
            this.letter = letter;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }
    }

    @GetMapping("/encrypt")
    public String encrypt(@RequestParam String message, @RequestParam String key){
        HashMap<Key, LinkedList<Character>> map = new HashMap<>();

        // Usunięcie spacji z wiadomości
        message = message.replaceAll(" ","");

        char[] keyChars = key.toCharArray();
        Arrays.sort(keyChars);

        // Tworzymy listę obiektów klasy Key
        List<Key> keys = new ArrayList<>();

        // Zapisywanie samych liter w obiektach klasy Key i dodawnie ich do listy
        for (int i = 0; i < key.length(); i++) {
            Key k = new Key(key.charAt(i));
            keys.add(k);
        }

        // Ustalanie kolejności alfabetycznej dla wszystkich obiektów Key
        for (int i = 0; i < keyChars.length; i++) {
            int finalI = i;
            Key foundKey = keys.stream()
                    .filter(k -> k.letter.equals(keyChars[finalI]))
                    .filter(k -> k.number == null)
                    .findFirst()
                    .get();
            foundKey.setNumber(finalI+1);
        }

        // Tworzenie LinkedList dla każdego obiektu klasy Key (inicjalizacja macierzy)
        for (Key k:keys) {
            map.putIfAbsent(k, new LinkedList<>());
        }

        int countInRow = 0;
        int rowCount = 0;

        // Uzupełnianie macierzy
        for (int i = 0; i < message.length(); i++) {
            map.get(keys.get(countInRow)).add(message.charAt(i));
            int finalRowCount = rowCount+1;

            Key wantedKey = keys
                    .stream()
                    .filter(k -> k.number == finalRowCount)
                    .findFirst()
                    .get();

            if(countInRow == keys.indexOf(wantedKey)){
                countInRow = 0;
                rowCount++;
            } else {
                countInRow++;
            }
        }

        // Posortowanie kluczy w tabeli "keys" według kolejności w alfabecie
        keys.sort(Comparator.comparing(Key::getNumber));

        // Zwracanie zaszyfrowanego hasła
        StringBuilder stringBuilder = new StringBuilder();
        for (Key k : keys) {
            map.get(k).forEach(stringBuilder::append);
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

}
