package pl.wipb.securityofcomputernetworks.algorithms.des;

import com.google.common.primitives.Booleans;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.*;
import pl.wipb.securityofcomputernetworks.algorithms.generator.Generator;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
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

        List<int[]> dividedBlocksInBinary = convertTextToIntArrayAndDivideToBlocks(textInBinary, sumOfBlocks);

        //permutacje inicjalizujące
        for (int i = 0; i < sumOfBlocks; i++) {
            dividedBlocksInBinary.set(i, permutation(dividedBlocksInBinary.get(i), ConstantTables.IP));
        }


        //Stworzenie tablicy kluczy składającej się z 16 elementów
        long keyToLong = new BigInteger(key,16).longValue();
        String[] keys = getKeys(keyToLong);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < sumOfBlocks; i++) {
            //Podział bloku na część lewą i prawą
            int[] l = divideArrayToLeftBlock(dividedBlocksInBinary.get(i));
            int[] r = divideArrayToRightBlock(dividedBlocksInBinary.get(i));

            //Wykonanie 16 iteracji algorytmu
            for (int j = 0; j < 16; j++) {
                int[] nKey = new int[48];
                for (int z = 0; z < keys.length; z++) {
                    nKey[z] = Integer.parseInt(keys[j].charAt(z) + "");
                }
                int[] prev = l;
                l = r;
                r = XORArrays(prev, function(nKey, r));
            }

            //Złączenie obu części w jedną tablicę
            int[] combinedArray = new int[l.length + r.length];
            System.arraycopy(r, 0, combinedArray, 0, r.length);
            System.arraycopy(l, 0, combinedArray, l.length, r.length);

            //Złączony blok 64-bitowy poddajemy odwróconej tablicy permutacji i dopisujemy do wyniku
            int[] arrayAfterPermutation = permutation(combinedArray, ConstantTables.INVERTED_IP);
            for (int k = 0; k < arrayAfterPermutation.length; k++) {
                result.append(arrayAfterPermutation[k]);
            }


            //Złączony blok 64-bitowy poddajemy odwróconej tablicy permutacji i dopisujemy do wyniku

        }

        return Strings.EMPTY;
    }

    private static List<int[]> convertTextToIntArrayAndDivideToBlocks(String textInBinary, int sumOfBlocks) {
        //Konwersja na tablicę
        int[] bitsInIntegerArray = new int[textInBinary.length()];
        for (int i = 0; i < textInBinary.length(); i++) {
            bitsInIntegerArray[i] = Integer.parseInt(textInBinary.charAt(i) + "");
        }
        System.out.println(Arrays.toString(bitsInIntegerArray));

        //Podział na bloki
        List<int[]> blocksInBinary = new LinkedList<>();
        for (int i = 0; i < sumOfBlocks; i++) {
            int[] singleBlock = Arrays.copyOfRange(bitsInIntegerArray, i * SIZE_OF_BLOCK, (i * SIZE_OF_BLOCK) + SIZE_OF_BLOCK);
            blocksInBinary.add(singleBlock);
        }
        return blocksInBinary;
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


    //    KROK 4
    private static boolean[] reduceKey(boolean[] key) {
        boolean[] reducedKey = new boolean[56];
        int[] flattedArray = null;
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
        int[] ints = null;
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
        return result;
    }

    // Permutacja na wiadomości 64 bitowej
    public static boolean[] encrypt(boolean[] input, boolean[][] keys) {
        // KROK 2
        //Poczatkowa permutacja na wiadomosci 64bitowej
        boolean[] rightTemporaryBlock;
        boolean[] leftTemporaryBlock;

        // KROK 3
        // Dzielenie na dwie 32 bitowe części
        boolean[] leftBlock = new boolean[32];
        boolean[] rightBlock = new boolean[32];


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
        boolean[] newKeysArray = null;

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
        return null;

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





    public static int[] permutation(int[] singleBlock, int[][] permArray) {
        int[] arrayAfterPermutation = new int[64];
        int k = 0;
        // przypisanie bitów zgodnie z permutacją permArray
        for (int i = 0; i < permArray.length; i++) {
            for (int j = 0; j < permArray[0].length; j++) {
                arrayAfterPermutation[k++] = singleBlock[permArray[i][j] - 1];
            }
        }

        return arrayAfterPermutation;
    }

    private static String[] getKeys(long key) {
        String subKeys[] = new String[16];

        //permutacja klucza według PC1, redukcja z 64 bitów na 56
        key = permuteKey(ConstantTables.PC_1, 64, key);

        //podział klucza na dwie 28-bitowe części
        int c = (int) (key >> 28);
        int d = (int) (key & 0xFFFFFFF);

        for (int i = 0; i < 16; i++) {

            //przesunięcia w lewo o x bitów zgodnie z tablicą SHIFT
            c = ((c << ConstantTables.LEFT_SHIFTS_ITERATIONS[i]) & 0xFFFFFFF) | (c >> (28 - ConstantTables.LEFT_SHIFTS_ITERATIONS[i]));
            d = ((d << ConstantTables.LEFT_SHIFTS_ITERATIONS[i]) & 0xFFFFFFF) | (d >> (28 - ConstantTables.LEFT_SHIFTS_ITERATIONS[i]));

            //połączenie obu bloków c i d
            long cd = (c & 0xFFFFFFFFL) << 28 | (d & 0xFFFFFFFFL);

            //permutacja klucza według PC2, redukcja z 56 bitów na 48
            long subKey = permuteKey(ConstantTables.PC_2, 56, cd);

            //zamiana klucza na binarnego stringa
            subKeys[i] = Long.toBinaryString(subKey);
            while (subKeys[i].length() % 48 != 0) {
                subKeys[i] = "0" + subKeys[i];
            }
        }

        return subKeys;
    }

    private static long permuteKey(int[] pc, int noBits, long key) {
        long result = 0;
        for (int i = 0; i < pc.length; i++) {
            int s = noBits - pc[i];
            result = (result << 1) | (key >> s & 1);
        }
        return result;
    }

    //Uzyskanie bloku lewego
    public static int[] divideArrayToLeftBlock(int[] arrayAfterPermutation) {
        return Arrays.copyOfRange(arrayAfterPermutation, 0, 32);
    }

    //Uzyskanie bloku prawego
    public static int[] divideArrayToRightBlock(int[] arrayAfterPermutation) {
        return Arrays.copyOfRange(arrayAfterPermutation, 32, 64);
    }

    public static int[] function(int[] key, int[] R) {
        // zmienna r przechowuje bity które są kombinacją bitów R według kolejności z tablicy E
        int[] r = new int[48];
        int k = 0;

        // przyporządkowanie bitów zgodnie z kolejnością z tablicy E do tablicy r
        for (int i = 0; i < ConstantTables.E_FUNC.length; i++) {
            for (int j = 0; j < ConstantTables.E_FUNC[0].length; j++) {
                r[k++] = R[ConstantTables.E_FUNC[i][j] - 1];
            }
        }
        // Wykonanie operacji xor pomiędzy bitami z tablicy r i tablicy key i przydzielenie ich do tymczasowej tablicy tmp
        int[] tmp = XORArrays(key, r);

        // dzielimy uzyskany ciąg bitów ( tmp ) na 8 ciągów 6 bitowych
        // wydzielamy pierwszy i ostatni bit jako indeks wiersza
        // środkowe bity oznaczają indeks kolumny
        // wyczytaną wartość zmieniamy na kod binarny i to są nasze 4 bity które dodajemy do wyniku
        k = 0;

        // Zmienna l wykorzystywana jest do ustalenia którą tablicę S mamy wykorzystać
        int l = 0;
        Collections.addAll(ConstantTables.listOfS, ConstantTables.S1_FUNC,
                ConstantTables.S2_FUNC, ConstantTables.S3_FUNC, ConstantTables.S4_FUNC, ConstantTables.S5_FUNC,
                ConstantTables.S6_FUNC, ConstantTables.S7_FUNC, ConstantTables.S8_FUNC);

        int[] beforeP = new int[32];
        for (int i = 0; i < 48; i += 6) {
            // tutaj wydzielamy bity które będą określać wiersz i kolumnę
            String rowString = tmp[i] + "" + tmp[i + 5];
            String columnString = tmp[i + 1] + "" + tmp[i + 2] + tmp[i + 3] + tmp[i + 4];
            int row = Integer.parseInt(rowString, 2);
            int column = Integer.parseInt(columnString, 2);

            // pobieramy wartość z tablic S która będzie naszymi bitami w końcowej tablicy
            int idx = ConstantTables.listOfS.get(l++)[row][column];

            // konwertujemy integer na string binarny
            String binaryIdx = Integer.toBinaryString(idx);

            // jeśli najwyższy bit nie stoi na 8, to musimy dodać 0 na początku tak aby końcowo nasza wartość miała 4 bity
            if (Integer.highestOneBit(idx) < 8) {
                int highestOneBit = Integer.highestOneBit(idx) / 2;
                for (int j = 3; j > highestOneBit; j--)
                    binaryIdx = "0" + binaryIdx;
            }

            for (int j = 0; j < 4; j++) {
                beforeP[k++] = Integer.parseInt(binaryIdx.substring(j, j + 1));
            }
        }
        // Permutacja tablicy beforeP z P
        int[] result = new int[32];

        k = 0;
        for (int i = 0; i < ConstantTables.P_FUNC.length; i++) {
            for (int j = 0; j < ConstantTables.P_FUNC[0].length; j++) {
                result[k++] = beforeP[ConstantTables.P_FUNC[i][j] - 1];
            }
        }

        return result;
    }

    public static int[] XORArrays(int[] arg1, int[] arg2) {
        int[] tmp = new int[arg1.length];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = XOR(arg1[i], arg2[i]);
        }
        return tmp;
    }

    public static int XOR(int arg1, int arg2) {
        return arg1 ^ arg2;
    }


    public static String encodeByteArrayToHex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static byte[] decodeHexToByteArray(String hexString)
            throws DecoderException {
        return Hex.decodeHex(hexString);
    }

}
