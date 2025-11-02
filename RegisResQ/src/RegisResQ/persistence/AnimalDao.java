package RegisResQ.persistence;
import RegisResQ.application.Animal;
import RegisResQ.application.Cat;
import RegisResQ.application.Dog;

import java.sql.*;
import java.util.*;

public class AnimalDao implements Dao<Animal> {
    private ArrayList<Animal> animals = new ArrayList<>();
    private final Connection connection;

    /*
     * Default Constructor
     * establishes a connection to MySQL server using a getConnection operation
     */
    public AnimalDao()
    {
        try 
        { 
            //load driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            //add common params so the connector is happy
            String url = "jdbc:mysql://localhost:3306/animals" + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            connection = DriverManager.getConnection(url, "cs444", "p@sswordCS444");
        }  catch(Exception e ) { 
            throw new RuntimeException("DB Connect Failed: " + e.getMessage()); 
        }
    }

    public boolean isConnected() { return connection != null; }

    /*
     * executes a select query on the database
     * selects all animals, regardless of species
     * @returns an ArrayList<Animal> animals
     */
    @Override
    public ArrayList<Animal> getAll()
    {
        ArrayList<Animal> list = new ArrayList<>();
        final String sql = "SELECT type, breed, name, sterilized, arrived FROM adoptable_pets ORDER BY name";
        try (PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) 
            {
                String type = resultSet.getString("type");
                String breed = resultSet.getString("breed");
                String name  = resultSet.getString("name");
                boolean sterilized = resultSet.getInt("sterilized") == 1;
                String arrived = resultSet.getString("arrived");

                if ("cat".equalsIgnoreCase(type))
                { 
                    list.add(new Cat(type, breed, name, sterilized, arrived));
                }
                else if ("dog".equalsIgnoreCase(type))
                {
                    list.add(new Dog(type, breed, name, sterilized, arrived));
                }
            }
        }
        catch (SQLException e ) { throw new RuntimeException("getAll() Failed: " + e); }
        this.animals = list;
        return list;
    }





    /*
     * adds a new animal into the MySQL database
     * @param Animal a
     * @return Boolean of success or failure
     */
    @Override
    public Boolean add(Animal a)
    {
        //create our Prepared Statement string with ? placeholders, the structure of the statement won't change, just the data
        //so this is easier and faster
        //O'Malley
        final String addSQL =
            "INSERT INTO adoptable_pets (type, breed, name, sterilized, arrived) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(addSQL))
        {
            //get the data values
            ps.setString(1, a.getSpecies());
            ps.setString(2, a.getBreed());
            ps.setString(3, a.getName());
            ps.setBoolean(4, a.getSterilized()); //should just resolve to the right value 1, 0
            ps.setString(5, a.getDateArrived());

            //1 row inserted, executeUpdate returns the number of affected rows, in this case it will be one, so
            //if this is false, then there was a failure of some sort
            return ps.executeUpdate() == 1; 
        }
        catch (SQLException e ) { System.err.println("add() Failed: " + e.getMessage()); return false; }
        
    }

    /*
     * deletes an animal from the MySQL database
     * similar to how the add method works, see comments there
     * @param Animal a
     * @return Boolean of success or failure
     */
    @Override
    public Boolean delete(Animal a)
    {

        final String deleteSQL =
        "DELETE FROM adoptable_pets WHERE name = ?";

        try (PreparedStatement ps = connection.prepareStatement(deleteSQL))
        {
            ps.setString(1, a.getName());
            return ps.executeUpdate() == 1;
        }
        catch (SQLException e ) { System.err.println("delete() Failed: " + e.getMessage()); return false; }
    }

    /*
     * updates an animal's information in the MySQL database
     * similar to how the add method works, see comments there
     * @param Animal a
     * @return Boolean of success or failure
     */
    @Override
    public Boolean update(Animal a)
    {
        final String updateSQL = 
        "update adoptable_pets set type = ?, breed = ?, sterilized = ?, arrived = ? where name = ?";

        try (PreparedStatement ps = connection.prepareStatement(updateSQL))
        {
            ps.setString(1, a.getSpecies());
            ps.setString(2, a.getBreed());
            ps.setBoolean(3, a.getSterilized());
            ps.setString(4, a.getDateArrived());
            ps.setString(5, a.getName());

            return ps.executeUpdate() == 1;
        }
        catch (SQLException e ) { System.err.println("update() Failed: " + e.getMessage()); return false; }
    }

}
