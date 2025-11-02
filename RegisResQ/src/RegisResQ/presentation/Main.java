package RegisResQ.presentation;

import RegisResQ.persistence.AnimalDao;
import RegisResQ.application.*;
import java.util.List;

public class Main {
    public static void main(String[] args)
    {

        /*
        * test the five functions in the AnimalDao class, 
        * and output the fields of both a Cat and Dog object,
        * and output an entire list of animals of different types
        */

        AnimalDao dao = new AnimalDao();
        int testsPassed = 0;
        //private List<Animal> animals = new ArrayList<>();

        long startTime = System.currentTimeMillis();



        //make a test cat and test dog
        long startTime2 = System.currentTimeMillis();
        Cat gCat = new Cat("Cat", "Tuxedo", "gCat", false, "2025-09-24");
        Dog gDog = new Dog("Dog", "Akita", "gDog", true, "2025-09-24");
        //did it pass the SRS-08 requirement
        long stopTime2 = System.currentTimeMillis();
        System.out.println("Elapsed time = " + (stopTime2 - startTime2) + "ms");
        String status = (stopTime2 - startTime2) <= 500 ? "Test Make 2 test animals: Passed, <= 500ms " : "Failed, > 500ms"; 
        if ((stopTime2 - startTime2) <= 500) {testsPassed++;}
        System.out.println(status + "\n");

        //output the fields 
        long startTime3 = System.currentTimeMillis();
        System.out.printf("Cat: %s %s %s %b %s%n", 
            gCat.getSpecies(), gCat.getBreed(), gCat.getName(), gCat.getSterilized(), gCat.getDateArrived());
        System.out.printf("Dog: %s %s %s %b %s%n", 
            gDog.getSpecies(), gDog.getBreed(), gDog.getName(), gDog.getSterilized(), gDog.getDateArrived());
        //did it pass the SRS-08 requirement
        long stopTime3 = System.currentTimeMillis();
        System.out.println("Elapsed time = " + (stopTime3 - startTime3) + "ms");
        status = (stopTime3 - startTime3) <= 500 ? "Test Output the Fields: Passed, <= 500ms " : "Failed, > 500ms"; 
        if ((stopTime3 - startTime3) <= 500) {testsPassed++;}
        System.out.println(status + "\n");

        //test add
        long startTime4 = System.currentTimeMillis();
        System.out.println("Cat added?: " + dao.add(gCat));
        System.out.println("Dog added?: " + dao.add(gDog));
        //did it pass the SRS-08 requirement
        long stopTime4 = System.currentTimeMillis();
        System.out.println("Elapsed time = " + (stopTime4 - startTime4) + "ms");
        status = (stopTime4 - startTime4) <= 500 ? "Test Add a new animal: Passed, <= 500ms " : "Failed, > 500ms"; 
        if ((stopTime4 - startTime4) <= 500) {testsPassed++;}
        System.out.println(status + "\n");

        //test get all
        long startTime5 = System.currentTimeMillis();
        System.out.println("Get all: \n");
        List<Animal> all = dao.getAll();
        for (Animal a : all)
        {
            System.out.printf("%s | %s | %s | %b | %s%n",
            a.getSpecies(), a.getBreed(), a.getName(), a.getSterilized(), a.getDateArrived());
        }
        //did it pass the SRS-08 requirement
        long stopTime5 = System.currentTimeMillis();
        System.out.println("Elapsed time = " + (stopTime5 - startTime5) + "ms");
        status = (stopTime5 - startTime5) <= 500 ? "Test Get all: Passed, <= 500ms " : "Failed, > 500ms"; 
        if ((stopTime5 - startTime5) <= 500) {testsPassed++;}
        System.out.println(status + "\n");

        //test update
        long startTime6 = System.currentTimeMillis();
        Dog updatedDog = new Dog("cat", "Shiba Inu", gDog.getName(),
                                 gDog.getSterilized(), gDog.getDateArrived());
        System.out.println("Dog updated? " + dao.update(updatedDog));
        //did it pass the SRS-08 requirement
        long stopTime6 = System.currentTimeMillis();
        System.out.println("Elapsed time = " + (stopTime6 - startTime6) + "ms");
        status = (stopTime6 - startTime6) <= 500 ? "Test update: Passed, <= 500ms " : "Failed, > 500ms"; 
        if ((stopTime5 - startTime5) <= 500) {testsPassed++;}
        System.out.println(status + "\n");


        //test delete
        long startTime7 = System.currentTimeMillis();
        System.out.println("Cat deleted?: " + dao.delete(gCat));
        System.out.println("Dog deleted?: " + dao.delete(updatedDog));
        //did it pass the SRS-08 requirement
        long stopTime7 = System.currentTimeMillis();
        System.out.println("Elapsed time = " + (stopTime7 - startTime7) + "ms");
        status = (stopTime7 - startTime7) <= 500 ? "Test delete : Passed, <= 500ms " : "Failed, > 500ms"; 
        if ((stopTime5 - startTime5) <= 500) {testsPassed++;}
        System.out.println(status + "\n");

        //total time
        long stopTime = System.currentTimeMillis();
        System.out.println("Total Elapsed time = " + (stopTime - startTime) + "ms");
        System.out.println("Tests Passed: " + testsPassed + "/6");


    }
}
