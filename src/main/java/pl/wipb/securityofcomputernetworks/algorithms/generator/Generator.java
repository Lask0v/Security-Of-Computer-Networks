package pl.wipb.securityofcomputernetworks.algorithms.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/generator")
public class Generator {

    @GetMapping("/LSFR")
    public String gen(@RequestParam String polynomial,
                    @RequestParam String seed,
                    @RequestParam Integer wantedResultLength) throws Exception {

        List<Integer> result = new ArrayList<>();

        // Przekonwertowanie wielomianu ze stringa na listę obiektów typu PolynomialComponent
        List<PolynomialComponent> polynomialComponentList = extractPolynomial(polynomial);
        int maxDegree = polynomialComponentList.stream().max(Comparator.comparing(PolynomialComponent::getDegree)).get().getDegree();

        // Przekonwertowanie ziarna

        if(!isSeedValid(seed, maxDegree)){
            return "Długość ziarna nie może być krótsza niż stopień wielomianu, które wynosi "+maxDegree;
        }

        List<Integer> seedElementList = new ArrayList<>();
        for(int i=0; i<seed.length(); i++){
            seedElementList.add(Integer.parseInt(String.valueOf(seed.charAt(i))));
        }

        // Poddaniu operacji XOR te elementy, które występują na tym miejscu w ziarnie, których potęgi występują w wielomianie
        // Na przykład dla wielomianu 2x2+3x6 xorujemy elementy które znajdują się na 2 i 6 elemencie w aktualnym ziarnie
        List<Integer> xorList;
        for(int i = 0 ; i<wantedResultLength; i++){
            xorList = new ArrayList<>();
            // Przejście po wszystkich wyrazach wielomianu
            for (PolynomialComponent component : polynomialComponentList) {
                if (component.degree != 0) {
                    // Wywołanie operacji xor dla elementu, który znajduje się na pozycji takiej, jak stopień wielomianu
                    xorList.add(seedElementList.get(component.degree - 1));
                }
            }

            // SLAJD 10 Z TEORII:
            // Dodanie na poczatek seeda wyniku xora
            seedElementList.add(0, executeXor(xorList));
            // Dodanie do wyniku ostatniej wartości xora
            result.add(seedElementList.get(seedElementList.size() - 1));
            // Usuniecie z seeda ostatniej wartosci
            seedElementList.remove(seedElementList.size() - 1);
        }
        return result.toString();
    }

    // Sprawdza, czy długość ziarna nie jest krótsza niż stopień wielomianu
    private boolean isSeedValid(String seed, int maxDegree) {
        return seed.length() >= maxDegree;
    }

    // Funkcja xorująca (jeżeli xor ma wszystkie wartości=1 lub wszystkie wartości=0, zwróć 0 jako wynik
    //                  jeżeli xor ma wartości różnej wielkości, zwróć 1 jako wynik)
    public int executeXor(List<Integer> xorList){
       int min = xorList.stream()
               .min(Integer::compare)
               .get();
       int max = xorList.stream()
               .max(Integer::compare)
               .get();

       // Jeżeli min == max, to znaczy że w xorList są albo same jedynki albo same 0
        return min == max ? 0 : 1;
    }


    // Rozszyfrowywanie wielomianu -> w tym momencie wczytuje poprawnie tylko w formacie np. "1x0+1x1+1x4"
    // TODO: obsłużyć sytuację, kiedy przed "x" nie ma "1" lub zwalidować tak żeby nie dało się tak wpisać
    // TODO: obsłużyć sytuację, kiedy po "x" nie ma potęgi "0" lub zwalidować tak żeby nie dało się tak wpisać
    private List<PolynomialComponent> extractPolynomial(String polynomial){
        List<PolynomialComponent> polynomialComponentList = new ArrayList<>();
        // Wyrazy wielomianu rodzielane są znakiem "+"
        String[] components = polynomial.split("\\+");
        for (String component:components) {
            // Współczynnik i stopień wielomianu rozdzielane są znakiem "x"
            String[] polynomialContent = component.split("x");
            // Tworzenie obiektów klasy PolynomialComponent i dodanie ich do zwracanej listy
            PolynomialComponent polynomialComponent = new PolynomialComponent(Integer.parseInt(polynomialContent[0]), Integer.parseInt(polynomialContent[1]));
            polynomialComponentList.add(polynomialComponent);
        }
        return polynomialComponentList;
    }

    // Wyraz wielomianu, czyli np "2x^4" -> w tym przypadku 2 to coefficient, a 4 to degree
    @AllArgsConstructor
    @Getter
    private class PolynomialComponent {
        Integer coefficient;
        Integer degree;
    }
}
