package pl.wipb.securityofcomputernetworks.algorithms.generator;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/generator")
public class Generator {

    @GetMapping()
    // Wielomian przyjmujemy w postaci stringa np. "1 + 2x2 + 3x4 + x6"
    public String encrypt(@RequestParam String polynomial) {
        List<PolynomialComponent> polynomialComponentList = new ArrayList<>();

//       TODO: obsłużyć przypadki 1) np. "1" bez "x0" oraz 2) np. "x4" bez współczynnika przed x
        List<String> list = List.of(polynomial.split(" + "));
        int counter = 0;
        for (String s : list) {
            if (counter++ == 0) {
                polynomialComponentList.add(new PolynomialComponent(Integer.parseInt(String.valueOf(list.get(0))), null));
            }
            String[] xes = s.split("x");

            if (counter++ == 1) {
                polynomialComponentList.add(new PolynomialComponent(Integer.parseInt(String.valueOf(list.get(0))), 1));
            } else if (counter > 1) {
                polynomialComponentList.add(new PolynomialComponent(Integer.parseInt(xes[0]), Integer.parseInt(xes[1])));

            }
        }

        return null;
    }

    @GetMapping("/generator")
    public void gen(){
        //Wynik -pozniej to mozna przerobic zeby nie bylo na liscie ale nwm
        List<Integer> result = new ArrayList<>();

        //Na sztywno podany wielomian x^1+2x^2+3x^4+x^6
        List<PolynomialComponent> polynomialComponentList = new ArrayList<>();
        polynomialComponentList.add(new PolynomialComponent(1,1));
        polynomialComponentList.add(new PolynomialComponent(2,2));
        polynomialComponentList.add(new PolynomialComponent(3,4));
        polynomialComponentList.add(new PolynomialComponent(1,6));

        //Ziarno. Podane birnarnie (na razie na sztywno) ->
        //TODO trzeba zrobic na to walidacje
        //TODO walidacje na to, czy jego dlugosc == najwyzsza potega wielomianu
        List<Integer> seed = new ArrayList<>();
        seed.add(0);
        seed.add(1);
        seed.add(0);
        seed.add(1);
        seed.add(0);
        seed.add(1);

        //TODO: to ma byc w petli
        //Pobranie wartości do xora
        List<Integer> xorList = new ArrayList<>();
        for (PolynomialComponent component:polynomialComponentList) {
            if(component.degree!=0){
                xorList.add(seed.get(component.degree-1));
            }
        }

        //Przesuniecie
        //Dodanie na poczatek seeda wyniku xora
        seed.add(0,executeXor(xorList));
        //Dodanie do wyniku ostatniej wartości xora
        result.add(seed.get(seed.size()-1));
        //Usuniecie z seeda ostatniej wartosci
        seed.remove(seed.size()-1);

        //TODO: tu koniec petli





    }

    // Funkcja xorująca (jeżeli cokolwiek jest 1 -> wypisz 1; w przeciwnym wypadku wypisz 0)
    public int executeXor(List<Integer> xorList){
       return xorList.stream()
                .filter(number -> number==1)
                .findAny()
                .orElse(0);
    }

    // Wyraz wielomianu, czyli np "2x^4"
    @AllArgsConstructor
    private class PolynomialComponent {
        Integer coefficient;
        Integer degree;
    }
}
