package pl.wipb.securityofcomputernetworks.algorithms.shiftmatrix;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/shift-matrix2")
public class SecondShiftMatrix {

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

        // Tworzenie listy obiektów klasy Key
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
            countInRow++;

            if(countInRow>=keys.size()){
                countInRow = 0;
                rowCount++;
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

    @GetMapping("/decrypt")
    public String decrypt(@RequestParam String message, @RequestParam String key){
        HashMap<Key, LinkedList<Character>> map = new HashMap<>();

        // Stworzenie tablicy pojedyńczych słów z wiadomości
        String[] words = message.split(" ");

        // Zliczanie długości wiadomości bez spacji
        int actualMessageLength = message.replaceAll(" ","").length();

        char[] keyChars = key.toCharArray();
        Arrays.sort(keyChars);

        // Tworzymy listę obiektów klasy Key
        List<Key> keys = new ArrayList<>();

        // Zapisywanie samych liter w obiektach klasy Key i dodawanie ich do listy
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

        // Stworzenie kopii tabeli "keys" i posortowanie jej kluczy według kolejności alfabetycznej
        List<Key> sortedKeys = new ArrayList<>(keys);
        sortedKeys.sort(Comparator.comparing(Key::getNumber));

        // Uzupełnianie macierzy
        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < words[i].length(); j++) {
                map.get(sortedKeys.get(i)).add(words[i].charAt(j));
            }
        }

        // Zwracanie odszyfrowanego hasła
        StringBuilder stringBuilder = new StringBuilder();
        int inRowCounter = 0;
        int rowCounter=0;

        for (int i = 0; i < actualMessageLength; i++) {
            stringBuilder.append(map.get(keys.get(inRowCounter)).get(rowCounter));
            if(++inRowCounter >= keys.size()){
                inRowCounter = 0;
                rowCounter++;
            }
        }

        return stringBuilder.toString();
    }
}
