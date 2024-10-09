package org.example;

import java.util.function.Consumer;
import java.util.function.Function;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

public class Main {

    public static void main(String[] args) {
        Main1.main(args);
    }

    public static class Main1 {

        // Static declarations in inner classes are not supported at language level '11'
        // unless inner classes are also declared static; or upgrade JDK to 16+

        public static void main(String[] args) {
            // Press Opt+Enter with your caret at the highlighted text to see how
            // IntelliJ IDEA suggests fixing it.
            System.out.println("Hello and welcome!");

            /*
            * Cannot infer type: lambda expressions require an explicit target type
            var f = (String x) -> { // Best to use Functional Interfaces
                System.out.println(x);
            }; f();
            */

            Consumer<String> f1 = (String x) -> { // Consumer FunctionalInterface - 'accepts' 1 arg, returns void
                System.out.println(x);
            }; f1.accept("123");

            Function<String, String> f2 = (String x) -> { // Function FunctionalInterface - 'applies' 1 arg, returns 1 type
                System.out.println(x);
                return x;
            }; String r = f2.apply("123"); System.out.println(r);

            // Press Ctrl+R or click the green arrow button in the gutter to run the code.
            for (int i = 1; i <= 5; i++) {

                // Press Ctrl+D to start debugging your code. We have set one breakpoint
                // for you, but you can always add more by pressing Cmd+F8.
                System.out.println("i = " + i);
            }
        }

    }

}