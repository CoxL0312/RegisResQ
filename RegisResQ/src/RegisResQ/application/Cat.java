/**
 * Concrete domain type for a cat.
 * Extends Animal; species is always "cat".
 * Constructors:
 *  - Cat(): calls super(null, null, false, null); sets species = "cat".
 *  - Cat(breed, name, sterilized, dateArrived): calls super(...); sets species = "cat".
 * Not abstract; can be instantiated directly.
 */


package RegisResQ.application;

public class Cat extends Animal{


    /*
     * Default constructor
     */
    public Cat()
    {
        super(null, null, false, null);
        this.species = "cat";
    }

    /*
     * 4-param Constructor
     */
    public Cat(String species, String breed, String name, Boolean sterilized, String dateArrived)
    {
        super(breed, name, sterilized, dateArrived);
        this.species = "cat";
    }
}
