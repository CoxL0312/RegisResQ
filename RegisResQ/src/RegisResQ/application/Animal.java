/**
 * Base domain model for an adoptable animal.
 * Abstract; fields: species, breed, name, sterilized, dateArrived (YYYY-MM-DD).
 * validate() checks non-empty + valid date (incl. leap years). toString() summarizes.
 * No setter for species; subclasses set it (e.g., Dog/Cat).
 */






package RegisResQ.application;

import java.util.regex.Pattern;

public abstract class Animal {
    protected String species;
    protected String breed;
    protected String name;
    protected Boolean sterilized;
    protected String dateArrived;

    //setters
    public void setBreed(String breed)
    {
        this.breed = breed;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setSterilized(Boolean sterilized)
    {
        this.sterilized = sterilized;
    }
    public void setDateArrived(String dateArrived)
    {
        this.dateArrived = dateArrived;
    }

    //getters
    public String getSpecies()
    {
        return this.species;
    }
    public String getBreed()
    {
        return this.breed;
    }
    public String getName()
    {
        return this.name;
    }
    public Boolean getSterilized()
    {
        return this.sterilized;
    }
    public String getDateArrived()
    {
        return this.dateArrived;
    }

    //default constructor
    Animal()
    {
    }



    //parameterized constructor
    Animal(String breed, String name, Boolean sterilized, String dateArrived)
    {
        //this.species = species;
        setBreed(breed);
        setName(name);
        setSterilized(sterilized);
        setDateArrived(dateArrived);
    }

    /*
     * toString()
     * returns a string of Animal: Breed: Name: Sterilized: DateArrived: with the corresponding fields
     */
    @Override
    public String toString()
    {
        return "Animal: " + getSpecies() + " Breed: " + getBreed() + " Name: " + getName() + " Sterilized: " + getSterilized() + " Date Arrived: " + getDateArrived();
    }

    /*  date pattern for matching
     *
     * counts for leap years and correct dates
     * 
     * (19|2[0-9])[0-9]{2} covers a restricted range of years by matching a number which starts with 19 or 2X followed by a couple of any digits.
     * 0[1-9]|1[012] matches a month number in a range of 01-12
     * 0[1-9]|[12][0-9]|3[01] matches a day number in a range of 01-31
     * 
     * 2000|2400|2800 matches a set of leap years with a divider of 400 in a restricted range of 1900-2999
     * 19|2[0-9](0[48]|[2468][048]|[13579][26])) matches all white-list combinations of years which have a divider of 4 and don’t have a divider of 100
     * -02-29 matches February 2nd
     * ^(((19|2[0-9])[0-9]{2})-02-(0[1-9]|1[0-9]|2[0-8]))$ matches all otehr days of Feb in all years
     * * ^(((19|2[0-9])[0-9]{2})-(0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01]))$ to match Jan, Mar, May, July, Aug, Oct, and Dec for 1- 31 days
     * ^(((19|2[0-9])[0-9]{2})-(0[469]|11)-(0[1-9]|[12][0-9]|30))$ to match Apr, June, Sep, Nov for 1 to 30 days
     */
    private static Pattern DATE_PATTERN = Pattern.compile(
      "^((2000|2400|2800|(19|2[0-9])(0[48]|[2468][048]|[13579][26]))-02-29)$" 
      + "|^(((19|2[0-9])[0-9]{2})-02-(0[1-9]|1[0-9]|2[0-8]))$"
      + "|^(((19|2[0-9])[0-9]{2})-(0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01]))$" 
      + "|^(((19|2[0-9])[0-9]{2})-(0[469]|11)-(0[1-9]|[12][0-9]|30))$");


    /*
     * matches method for validating the date
     * returns true if the date is a valid date, false otherwise
     * tries to guard against null input
     */
    public boolean matches(String date)
    {
        return date != null && DATE_PATTERN.matcher(date).matches();
    }


    /* helper function because we check this a lot
     * the if statement was out of control
     * null checks first, then the empties to prevent null pointer exceptions
     */
    private static boolean isNullOrEmpty(String s) 
    {
        return s == null || s.isEmpty();
    }




    public Boolean validate()
    {
        //null checks first, then the empties to prevent null pointer exceptions

        if (isNullOrEmpty(species) ||
            isNullOrEmpty(breed) ||
            isNullOrEmpty(name) ||
            isNullOrEmpty(dateArrived) ||
            sterilized == null)
            {
                return false;
            }
        //check date format and if it is a real calender day
        else if (!matches(this.dateArrived))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

}
