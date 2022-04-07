package pl.wipb.securityofcomputernetworks.algorithms.generator;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RestController
@RequestMapping("/generator")
public class Generator {

    @GetMapping()
    public String encrypt(@RequestParam String polynomial) {
        List<PolynomialComponent> polynomialComponentList = new ArrayList<>();

//       TODO: obsłużyć przypadki 1) np. "1" bez "x0" oraz 2) np. "x4" bez współczynnika przed x
        List<String> list = List.of(polynomial.split(" + "));
        int counter = 0;
        for (String s : list) {
            if (counter++ == 0) {
                polynomialComponentList.add(new PolynomialComponent(Integer.parseInt(s), 0));
                continue;
            }
            String[] xes = s.split("x");

            if (counter++ == 1) {
                polynomialComponentList.add(new PolynomialComponent(Integer.parseInt(xes[0]), 1));
                continue;
            } else if (counter > 1) {
                polynomialComponentList.add(new PolynomialComponent(Integer.parseInt(xes[0]), Integer.parseInt(xes[1])));
                continue;
            }
        }

        return null;
    }

    @GetMapping("/generator")
    public void gen(){
        //Wynik -pozniej to mozna przerobic zeby nie bylo na liscie ale nwm
        List<Integer> result = new ArrayList<>();

        //Na sztywno podany wielomian z przykładu
        List<PolynomialComponent> polynomialComponentList = new ArrayList<>();
        polynomialComponentList.add(new PolynomialComponent(1,0));
        polynomialComponentList.add(new PolynomialComponent(1,1));
        polynomialComponentList.add(new PolynomialComponent(1,4));

        //Ziarno. Podane birnarnie (na razie na sztywno z przykładu) ->
        //TODO trzeba zrobic na to walidacje
        //TODO walidacje na to, czy jego dlugosc == najwyzsza potega wielomianu
        List<Integer> seed = new ArrayList<>();

        seed.add(0);
        seed.add(1);
        seed.add(1);
        seed.add(0);

        Scanner scanner = new Scanner(System.in);
        List<Integer> xorList;

        final int wantedResultElements = 7;
        for(int i = 0 ; i<wantedResultElements; i++){
            xorList = new ArrayList<>();
            for (PolynomialComponent component : polynomialComponentList) {
                if (component.degree != 0) {
                    xorList.add(seed.get(component.degree - 1));
                }
            }

            //Przesuniecie
            //Dodanie na poczatek seeda wyniku xora
            seed.add(0, executeXor(xorList));
            //Dodanie do wyniku ostatniej wartości xora
            result.add(seed.get(seed.size() - 1));
            //Usuniecie z seeda ostatniej wartosci
            seed.remove(seed.size() - 1);
        }

        System.out.println(result);
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

        return min == max ? 0 : 1;
    }

    // Wyraz wielomianu, czyli np "2x^4" -> w tym przypadku 2 to coefficient, a 4 to degree
    @AllArgsConstructor
    private class PolynomialComponent {
        Integer coefficient;
        Integer degree;
    }
}
