package pl.wipb.securityofcomputernetworks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.wipb.securityofcomputernetworks.algorithms.shiftmatrix.ShiftMatrix;

@SpringBootTest
class ShiftMatrixTest {

    @Autowired
    ShiftMatrix shiftMatrix;

    // dl.wiadomosci%dl.klucza=3
    @Test
    void test_encrypt_3() {
        String messageToEncrypt = shiftMatrix.encrypt("CRYPTOGRAPHYOSA","3-1-4-2");
        Assertions.assertEquals("YCPRGTROHAYPAO S", messageToEncrypt);
    }

    // dl.wiadomosci%dl.klucza=2
    @Test
    void test_encrypt_2() {
        String messageToEncrypt = shiftMatrix.encrypt("CRYPTOGRAPHYSA","3-1-4-2");
        Assertions.assertEquals("YCPRGTROHAYP S A", messageToEncrypt);
    }

    // dl.wiadomosci%dl.klucza=1
    @Test
    void test_encrypt_1() {
        String messageToEncrypt = shiftMatrix.encrypt("CRYPTOGRAPHYS","3-1-4-2");
        Assertions.assertEquals("YCPRGTROHAYP S  ", messageToEncrypt);
    }

    // dl.wiadomosci%dl.klucza=0
    @Test
    void test_encrypt_0() {
        String messageToEncrypt = shiftMatrix.encrypt("CRYPTOGRAPHYOSALK","3-1-4-2");
        Assertions.assertEquals("YCPRGTROHAYPAOLSK", messageToEncrypt);
    }

    // dl.wiadomosci%dl.klucza=3
    @Test
    void test_decrypt_3() {
        String messageToDecrypt = shiftMatrix.decrypt("YCPRGTROHAYPAO S","3-1-4-2");
        Assertions.assertEquals("CRYPTOGRAPHYOSA", messageToDecrypt);
    }

    // dl.wiadomosci%dl.klucza=2
    @Test
    void test_decrypt_2() {
        String messageToDecrypt = shiftMatrix.decrypt("YCPRGTROHAYP S A","3-1-4-2");
        Assertions.assertEquals("CRYPTOGRAPHYSA", messageToDecrypt);
    }

    // dl.wiadomosci%dl.klucza=1
    @Test
    void test_decrypt_1() {
        String messageToDecrypt = shiftMatrix.decrypt("YCPRGTROHAYP S ","3-1-4-2");
        Assertions.assertEquals("CRYPTOGRAPHYS", messageToDecrypt);
    }

    // dl.wiadomosci%dl.klucza=0
    @Test
    void test_decrypt_0() {
        String messageToDecrypt = shiftMatrix.decrypt("YCPRGTROHAYPAOLSK","3-1-4-2");
        Assertions.assertEquals("CRYPTOGRAPHYOSALK", messageToDecrypt);
    }

}
