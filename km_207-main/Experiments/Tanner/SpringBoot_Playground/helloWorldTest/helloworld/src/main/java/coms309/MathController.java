package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/math")
class MathController {
    @GetMapping("/add/{num1}+{num2}")
    public String addition(@PathVariable int num1, @PathVariable int num2) {
        int total = num1 + num2;
        return "Total = " + total;
    }

    @GetMapping("/subtract/{num1}-{num2}")
    public String subtraction(@PathVariable int num1, @PathVariable int num2) {
        int total = num1 - num2;
        return "Total = " + total;
    }

    @GetMapping("/multAndDivide/{num1}x{num2}")
    public String multiplication(@PathVariable int num1, @PathVariable int num2) {
        int total = num1 * num2;
        return "Total = " + total;
    }

    @GetMapping("/multAndDivide/{num1}d{num2}")
    public String division(@PathVariable int num1, @PathVariable int num2) {
        int total = num1 / num2;
        return "Total = " + total;
    }
}
