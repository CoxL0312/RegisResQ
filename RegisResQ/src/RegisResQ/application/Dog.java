/**
 * Concrete domain type for a dog.
 * Extends Animal; species is always "dog".
 * Constructors:
 *  - Dog(): calls super(null, null, false, null); sets species = "dog".
 *  - Dog(breed, name, sterilized, dateArrived): calls super(...); sets species = "dog".
 * Not abstract; can be instantiated directly.
 */


package RegisResQ.application;

public class Dog extends Animal{


    /*
     * Default constructor
     */
    public Dog()
    {
        super(null, null, false, null);
        this.species = "dog";
    }

    /*
     * 4-param Constructor
     */
    public Dog(String species, String breed, String name, Boolean sterilized, String dateArrived)
    {
        super(breed, name, sterilized, dateArrived);
        this.species = "dog";
    }
}
