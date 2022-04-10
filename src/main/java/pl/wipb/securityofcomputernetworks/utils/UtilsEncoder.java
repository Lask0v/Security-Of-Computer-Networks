package pl.wipb.securityofcomputernetworks.utils;

import java.util.Arrays;
import java.util.List;

public class UtilsEncoder {

    public static int[] toBinaryCode(String text) {
        return Arrays.stream(text.split(""))
                .mapToInt(Integer::valueOf)
                .toArray();
    }

    public static int processXOR(List<Integer> list, int[] polynomial) {
        int result;
        // pierwsza jedyka w liscie
        result = polynomial[list.get(0)];
        for (int i = 1; i < list.size(); i++) {
            result = XOR(result, polynomial[list.get(i)]);
        }
        return result;
    }

    public static int XOR(int a, int b) {
        return a ^ b;
    }
}
