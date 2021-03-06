package tfm.programs.pdg;

import tfm.utils.Logger;

import java.util.Arrays;

public class Example2 {

    public static void main(String[] args) {
        int x = 1;
        int y = 2;

        if (x > y)
            x = 1;

        int z = 10;

        y = z + x;

        x += y;

        Iterable<Integer> integers = Arrays.asList(1, 2, 3);

        for(int i : integers) {
            System.out.println(x + i);
        }

        int a = 8;

        int b = 2;

        for (int i = 0; a < 100; a++, i++) {
            b++;
        }

        System.out.println(b);
    }
}
