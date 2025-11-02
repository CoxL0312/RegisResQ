package test;

import org.testng.annotations.Test;
import static org.testng.Assert.*;
import RegisResQ.application.Cat;

public class catNGTest {
    @Test
    public void testValidate()
    {
        Boolean result;
        int passed = 0;

        Cat c = new Cat();
        result = c.validate();
        if (result) passed++;
        assertFalse(result);

        c.setName("Rusty");
        result = c.validate();
        if (result) passed++;
        assertFalse(result);

        c.setBreed("Orange Tabby");
        result = c.validate();
        if (result) passed++;
        assertFalse(result);

        c.setSterilized(true);
        result = c.validate();
        if (result) passed++;
        assertFalse(result);

        c.setDateArrived("2025-07-32");
        result = c.validate();
        if (result) passed++;
        assertFalse(result);

        c.setDateArrived("2025-07-15");
        result = c.validate();
        if (result) passed++;
        assertTrue(result);


        double pct = ((double)passed * 100.0) / 6;
        System.out.println("Tests passed: " + (int)Math.round(pct) + "%");
    }
}
