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

    // Wyraz wielomianu, czyli np "2x^4"
    @AllArgsConstructor
    private class PolynomialComponent {
        Integer coefficient;
        Integer degree;
    }

    @GetMapping()
    public String encrypt(@RequestParam String polynomial) {
        List<PolynomialComponent> polynomialComponentList = new ArrayList<>();

//       TODO: obsłużyć przypadki 1) np. "1" bez "x^0" oraz 2) np. "x4" bez współczynnika przed x
        List<String> list = List.of(polynomial.split(" + "));
        for (int i = 0; i < list.size(); i++) {
            PolynomialComponent polynomialComponent = new PolynomialComponent(Integer.parseInt(String.valueOf(list.get(i).charAt(0))), Integer.parseInt(String.valueOf(list.get(i).charAt(2))));
            polynomialComponentList.add(polynomialComponent);
        }

        return null;
    }
}
