package pl.wipb.securityofcomputernetworks.algorithms.des;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wipb.securityofcomputernetworks.algorithms.generator.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/DES")
public class Des {
    private final Generator generator;
    private final Logger logger = Logger.getLogger(getClass().toString());

    Des(Generator generator) {
        this.generator = generator;
    }

    // Permutacja na wiadomości 64 bitowej
    public static boolean[] encrypt(boolean[] input, boolean[][] keys) {
        // KROK 2
        //Poczatkowa permutacja na wiadomosci 64bitowej
        boolean[] output = initialPermutation(input);
        boolean[] rightTemporaryBlock;
        boolean[] leftTemporaryBlock;

        // KROK 3
        // Dzielenie na dwie 32 bitowe części
        boolean[] leftBlock = new boolean[32];
        boolean[] rightBlock = new boolean[32];

        // Dzielenie bloku na pół
        for (int i = 0; i < 32; i++) {
            leftBlock[i] = output[i];
            rightBlock[i] = output[i + 32];
        }


        // KROK 4
        // Klucz w tabeli jednowymiarowej
        boolean[] leftKeyBlock = new boolean[26];
        boolean[] rightKeyBlock = new boolean[26];

        boolean[] keysArray = new boolean[64];
        int counter = 0;
        for (int i = 0; i < keys.length; i++) {
            for (int j = 0; j < keys[i].length; j++) {
                keysArray[counter] = keys[i][j];
            }
        }

        // Zastosowanie na kluczu permutowanego wyboru
        // W newKeysArray jest teraz 56 elementów
        boolean[] newKeysArray = permutedChoice(keysArray);

        // Dzielenie klucza na pół
        for (int i = 0; i < 26; i++) {
            leftKeyBlock[i] = newKeysArray[i];
            rightKeyBlock[i] = newKeysArray[i + 26];
        }

        //TODO:

        // 16 Iteracji podczas kodowania wiadomosci - funkcje Feistela
        for (int i = 0; i < 15; i++) {
            leftTemporaryBlock = Arrays.copyOf(leftBlock, leftBlock.length);
            // Lewą częścią staje się przepisana bez zmian prawa strona (Li = Ri-1)
            leftBlock = Arrays.copyOf(rightBlock, rightBlock.length);

            // Prawa część jest poddawana przekształceniu
            rightTemporaryBlock = feistelFunction(rightBlock, keys[i]);
            for (int j = 0; j < 32; j++) {
                // Operacja modulo na lewej i prawej
                rightBlock[j] = leftTemporaryBlock[j] ^ rightTemporaryBlock[j];
            }
        }
        // Ostatnia iteracja
        leftTemporaryBlock = leftBlock;
        rightTemporaryBlock = feistelFunction(rightBlock, keys[15]);

        for (int j = 0; j < 32; j++) {
            // Przeprowadzenie operacji XOR lewej czesci i wyniku po permutacjach i tabeli
            leftBlock[j] = leftTemporaryBlock[j] ^ rightTemporaryBlock[j];
        }
        for (int i = 0; i < 32; i++) {
            // Spajanie bloków
            output[i] = leftBlock[i];
            output[i + 32] = rightBlock[i];
        }
        output = finalPermutation(output);
        return output;
    }

    // Początkowa permutacja bloku
    public static boolean[] initialPermutation(boolean[] input) {
        boolean[] output = new boolean[64];
        for (int i = 0; i < 64; i++) {
            output[i] = input[ConstantTables.IP[i] - 1];
        }
        return output;
    }

    // Permutacja końcowa
    public static boolean[] finalPermutation(boolean[] input) {
        boolean[] output = new boolean[64];
        for (int i = 0; i < 64; i++) {
            output[i] = input[ConstantTables.INVERTED_IP[i] - 1];
        }
        return output;
    }

    // Permutacja PC
    public static boolean[] permutedChoice(boolean[] input){
        boolean[] newTable = new boolean[56];
        int counter = 0;
        for (int i = 0; i < ConstantTables.PC_1.length; i++) {
            for (int j = 0; j < ConstantTables.PC_1[i].length; j++) {
                newTable[counter] = input[ConstantTables.PC_1[i][j]];
                counter++;
            }
        }
        return newTable;
    }

    // Funkcja Feistela
    public static boolean[] feistelFunction(boolean[] rightBlock, boolean[] key) {
        boolean[] extendedTable = new boolean[48];
        for (int i = 0; i < 48; i++) {

            // Zastosowanie permutacji rozszerzającej
            extendedTable[i] = rightBlock[ConstantTables.E_FUNC[i] - 1];
        }
        int result;
        int[] afterXor = new int[48];
        for (int i = 0; i < 48; i++) {
            // Przeprowadzenie operacji XOR na tabeli po permitacji rozszerzonej z aktualnym kluczem
            afterXor[i] = (extendedTable[i] ^ key[i]) ? 1 : 0;
        }
        int j = 0;
        String binaryResult;

        // Bloki wynikowe
        boolean[] resultBlock = new boolean[32];
        Block block = new Block();
        // 8 bloków po 6 bitów
        for (int i = 0; i < 8; i++) {
            // Obliczanie rzędu
            int outer = afterXor[i * 6] * 2 + afterXor[i * 6 + 5];
            // Obliczanie kolumny
            int inner = afterXor[i * 6 + 1] * 8 + afterXor[i * 6 + 2] * 4 + afterXor[i * 6 + 3] * 2 + afterXor[i * 6 + 4];

            // Pobieranie wartości z odpowiedniej komórki tabeli
            result = Block.block[i][outer][inner];
            // Wynik z tabeli po przekształceniu binarnym
            binaryResult = Integer.toBinaryString(result);
            String tempBinaryResult = "";
            // Zmiana na binarną
            if (binaryResult.length() == 4) {
                tempBinaryResult = binaryResult;
            } else if (binaryResult.length() == 3) {
                tempBinaryResult = "0" + binaryResult.charAt(0) + binaryResult.charAt(1) + binaryResult.charAt(2);
            } else if (binaryResult.length() == 2) {
                tempBinaryResult = "00" + binaryResult.charAt(0) + binaryResult.charAt(1);
            } else if (binaryResult.length() == 1) {
                tempBinaryResult = "000" + binaryResult.charAt(0);
            } else if (binaryResult.length() == 0) {
                tempBinaryResult = "0000";
            }
            // Zapis wyniku bloku do resultBlock
            for (int l = 0; l < 4; l++)
                if (tempBinaryResult.charAt(l) == '1') {
                    resultBlock[j++] = true;
                } else if (tempBinaryResult.charAt(l) == '0') {
                    resultBlock[j++] = false;
                }
        }

        boolean[] output = new boolean[32];
        for (int i = 0; i < 32; i++) {
            output[i] = resultBlock[ConstantTables.P_FUNC[i] - 1];
        }
        return output;
    }
}
