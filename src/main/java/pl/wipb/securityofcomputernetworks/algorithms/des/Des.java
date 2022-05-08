package pl.wipb.securityofcomputernetworks.algorithms.des;

import com.google.common.primitives.Booleans;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.wipb.securityofcomputernetworks.algorithms.generator.Generator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/DES")
public class Des {
    private static final int SIZE_OF_BLOCK = 64;
    private static final int NUMBER_OF_ITERATIONS = 16;
    private static final int SINGLE_SHIFT_IN_LEFT = -1;
    private static final int DOUBLE_SHIFT_IN_LEFT = -2;
    private static final Logger logger = Logger.getLogger(Des.class.toString());
    private final Generator generator;

    Des(Generator generator) {
        this.generator = generator;
    }

    @RequestMapping(
            path = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public static String encode(@RequestPart(required = true) MultipartFile file, @RequestParam String key) throws IOException {
        InputStream inputStream = new BufferedInputStream(file.getInputStream());
        byte[] buffer = new byte[8];
        while (inputStream.read(buffer) != -1) {
            //Krok pierwszy
            String correctedHexMessage = fillBlockIfMessageIsNotEqualDivided(encodeByteArrayToHex(buffer));
            processEncryption(correctedHexMessage, key);
        }

    public static String encode(String str, String key) throws IOException {
//        byte[] buffer = new byte[8];
//        InputStream inputStream = file.getInputStream();
//        while (inputStream.read(buffer) != -1) {
//            //Krok pierwszy
//            String correctedHexMessage = fillBlockIfMessageIsNotEqualDivided(encodeByteArrayToHex(buffer));
//            processEncryption(correctedHexMessage, key);
//
//
//        }
        String textInBinary = fillBlockIfMessageIsNotEqualDivided(str);
        int sumOfBlocks = textInBinary.length() / SIZE_OF_BLOCK;

        return Strings.EMPTY;
    }

    /**
     * Znak ma rozmiar 2 bajtów (czyli 16 bitów).
     * Dodaj 0 na końcu, jeśli wiadomość heksadecymalna nie jest równa blokom 64-bitowym.
     *
     * @return Poprawioną wiadomość, z równo podzielonymi blokami gotową do kodowania
     */
    private static String fillBlockIfMessageIsNotEqualDivided(String text) {
        // konwertowanie hasła do bitów
        String textInBinary = new BigInteger(text.getBytes()).toString(2);
        //gdy w pierwszym znaku na początku są 0 to są pomijane i trzeba je dopisać
        while (textInBinary.length() % 8 != 0) {
            textInBinary = "0" + textInBinary;
        }
        return textInBinary;
    }

    public static String processEncryption(String hexMessage, String hexKey) {

        boolean[] input = hexToBooleanArray(hexMessage);
        boolean[] keys = hexToBooleanArray(hexKey);
        return null;
    }

    //    KROK 2
    private static boolean [] initialPermutationStepTwo(boolean[] inputData) {
        boolean[] result = new boolean[inputData.length];
        for (int i = 0; i < inputData.length; i++) {
            for (int i1 : ConstantTables.IP) {
                result[i] = inputData[i1];
            }
        }
        return result;
    }

    //    KROK 4
    private static boolean[] reduceKey(boolean[] key) {
        boolean[] reducedKey = new boolean[56];
        int[] flattedArray = flatArrayTwoDimensionalIntoOneDimensionalArray(ConstantTables.PC_1);
        for (int i = 0; i < reducedKey.length; i++) {
            for (int i1 : flattedArray) {
                reducedKey[i] = key[i1];
            }
        }
        return reducedKey;
    }

    //    KROK 5
    private static boolean[][] separateKey(boolean[] reducedKey) {
        boolean[] leftPartKey = new boolean[reducedKey.length / 2];
        boolean[] rightPartKey = new boolean[reducedKey.length / 2];
        boolean inputToLeftPartKey = true;
        for (int i = 0; i < reducedKey.length; i++) {
            if (inputToLeftPartKey) {
                leftPartKey[i] = reducedKey[i];
            } else {
                rightPartKey[i] = reducedKey[i];
            }
            if (i == reducedKey.length / 2) {
                inputToLeftPartKey = false;
            }
            shiftInLeft(leftPartKey, rightPartKey);
        }
// FIXME: 07.05.2022 to czeba bedzie naprawic
        return null;
    }

    //    KROK 6
// TODO: 07.05.2022 6. W każdej z 28-bitowych części klucza robimy przesunięcie w lewo o X bitów,
//  gdzie ilość bitów jest określana w tabeli obok numeru iteracji który robimy dla danego 64-bitowego bloku.
//  Tych iteracji jest 16.
    private static boolean[] shiftInLeft(boolean[] leftPartKey, boolean[] rightPartKey) {
        int lengthPartKey = -1;
        lengthPartKey = getValidatedKey(leftPartKey, rightPartKey, lengthPartKey);
        List<Boolean> leftPartKeyList = convertArrayToList(leftPartKey);
        List<Boolean> rightPartKeyList = convertArrayToList(rightPartKey);
        boolean[] keyAfterPermutation = new boolean[lengthPartKey * 2];
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            for (int j = 0; j < lengthPartKey; j++) {
                if (i <= 1 || i == 8 || i == 15) {
                    Collections.rotate(leftPartKeyList, SINGLE_SHIFT_IN_LEFT);
                    Collections.rotate(rightPartKeyList, SINGLE_SHIFT_IN_LEFT);
                } else {
                    Collections.rotate(leftPartKeyList, DOUBLE_SHIFT_IN_LEFT);
                    Collections.rotate(rightPartKeyList, DOUBLE_SHIFT_IN_LEFT);
                }
            }
            keyAfterPermutation = permutedChoiceSecond
                    (
                            Booleans.toArray(Stream.of(leftPartKeyList, rightPartKeyList)
                                    .flatMap(Collection::stream)
                                    .collect(Collectors.toList()))
                    );
        }
        return keyAfterPermutation;
    }

    //    KROK 7
    private static boolean[] permutedChoiceSecond(boolean[] key) {
        int[] ints = flatArrayTwoDimensionalIntoOneDimensionalArray(ConstantTables.PC_2);
        for (int i = 0; i < ints.length; i++) {
            key[i] = key[ints[i]];
        }
        return key;
    }

    private static List<Boolean> convertArrayToList(boolean[] array) {
        ArrayList<Boolean> booleans = new ArrayList<>();
        for (boolean b : array) {
            booleans.add(b);
        }
        return booleans;
    }

    private static int getValidatedKey(boolean[] leftPartKey, boolean[] rightPartKey, int lengthPartKey) {
        if (leftPartKey.length == rightPartKey.length) {
            lengthPartKey = leftPartKey.length;
        }
        if (lengthPartKey < 0) {
            logger.warning("Keys are not equal");
            System.exit(0);
        }
        return lengthPartKey;
    }
    // TODO: 07.05.2022 7. Dla każdej iteracji powstaje klucz Kn, który tworzymy łącząc bloki Cn i Dn, a następnie ich
    //połączenie permutując ciągiem permutowanego wyboru 2 (Permuted Choice 2 – PC2):

    private static int[] flatArrayTwoDimensionalIntoOneDimensionalArray(int[][] array) {
        int[] flattedArray = new int[array.length * array[0].length];
        int counter = 0;
        for (int[] row : array) {
            for (int cell : row) {
                flattedArray[counter++] = cell;
            }
        }
        return flattedArray;
    }

    private static boolean[] hexToBooleanArray(String hex) {
        boolean[] result = new boolean[hex.length()];
        for (int i = 0; i < hex.length(); i++) {
            result[i] = BooleanUtils.toBoolean(Integer.parseInt(String.valueOf(hex.charAt(i)), 2));
        }
        return result;
    }

    //    KROK 8
    private static boolean[] transform32BitInto48(boolean[] data) {
        boolean[] result = new boolean[48];
        int counter = 0;
        for (int i : ConstantTables.E_FUNC) {
            result[counter++] = data[i];
        }
        return result;
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
        boolean[] leftKeyBlock = new boolean[26];
        boolean[] rightKeyBlock = new boolean[26];

        // Klucz w tabeli jednowymiarowej
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

        // KROK 5
        // Dzielenie klucza na pół
        for (int i = 0; i < 28; i++) {
            leftKeyBlock[i] = newKeysArray[i];
            rightKeyBlock[i] = newKeysArray[i + 28];
        }

        // KROK 6
        // TODO:


        // KROK 7
        // TODO:


        // KROK 8
        // TODO:


        // KROK 9
        // TODO:


        // KROK 10
        // TODO:


        // KROK 11
        // TODO:


        // KROK 12
        // TODO:


        // KROK 13
        // TODO:


        // KROK 14
        // TODO:


        // KROK 15
        // TODO:


        // KROK 16
        // TODO:


        // KROK 17
        // TODO:


        // KROK 18
        // TODO:


        // TODO:
        return output;

        /*
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
        return output;*/

    }

    private static int convertBooleanArrayToBinaryNumber(boolean[] booleanArray) {
        StringBuilder number = new StringBuilder();
        for (boolean element : booleanArray) {
            if (element) {
                number.append("1");
            } else {
                number.append("0");
            }
        }
        return Integer.parseInt(number.toString());
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
    public static boolean[] permutedChoice(boolean[] input) {
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

    public static String encodeByteArrayToHex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static byte[] decodeHexToByteArray(String hexString)
            throws DecoderException {
        return Hex.decodeHex(hexString);
    }

}
