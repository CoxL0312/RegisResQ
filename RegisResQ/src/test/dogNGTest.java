package test;

import org.testng.annotations.Test;
import static org.testng.Assert.*;
import RegisResQ.application.Dog;

public class dogNGTest {
    @Test
    public void testValidate()
    {
        Boolean result;

        Dog d = new Dog();
        result = d.validate();
        assertFalse(result);

        d.setName("Toto");
        result = d.validate();
        assertFalse(result);

        d.setBreed("Cairn Terrier");
        result = d.validate();
        assertFalse(result);

        d.setSterilized(true);
        result = d.validate();
        assertFalse(result);

        d.setDateArrived("2025-05-32");
        result = d.validate();
        assertFalse(result);

        d.setDateArrived("2025-05-31");
        result = d.validate();
        assertTrue(result);

    }
}


