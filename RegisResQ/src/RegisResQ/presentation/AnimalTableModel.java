/*
 * RegisResQ – AnimalTableModel
 * Author: Lindsey
 * Date:   10/13/2025
 * ---------------------------------------------------------------------------
 * A read-only Swing TableModel that backs the animals JTable in the UI.
 * 
 * Responsibilities:
 *   • Expose animal data as rows/columns to a JTable.
 *   • Keep column metadata (names/types).
 *   • Notify the JTable when the underlying list changes.
 *
 * Columns (index → meaning → Java type returned by getValueAt):
 *   0 → Type        → String   ("Cat" or "Dog")
 *   1 → Breed       → String
 *   2 → Name        → String
 *   3 → Sterilized  → Boolean
 *   4 → Arrived     → String   (yyyy-MM-dd)
 *
 * Notes:
 *   • Model is read-only; editing is not supported here (future iterations will add it).
 */

package RegisResQ.presentation;
import javax.swing.table.AbstractTableModel;
import RegisResQ.application.*;
import java.util.ArrayList;

public class AnimalTableModel extends AbstractTableModel{

    private String[] columnNames = {"Type", "Breed", "Name", "Sterilized", "Arrived"};
    private ArrayList<Animal> animals;

    /**
     * default constructor that initializes animals to null
     */
    public AnimalTableModel()
    {
        animals = new ArrayList<Animal>();
    }

    /**
     * 
     * @param newAnimals
     */
    public void setAnimals(ArrayList<Animal> newAnimals)
    {
        if (newAnimals != null)
        {
            this.animals = new ArrayList<>(newAnimals);
            //tell the JTable to redo itself with new rows
            fireTableDataChanged();
        }
    }
    
    /**
     * size of animals list
     * @return size of animals list
     */
    @Override
    public int getRowCount() 
    {
        return animals.size();
    }

    /**
     * how many columns there are
     * @return length of columnNames array
     */
    @Override
    public int getColumnCount() 
    {
        return columnNames.length;
    }

    /**
     * given a row and column index, return the value to display
     * @param rowIndex
     * @param columnIndex
     * @return value of selected Animal Obj
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) 
    {
        if (columnIndex < 0 || columnIndex > 4)
        {
            //System.out.println("val 0 - 4 for species, breed, name, sterilized, and date arrived");
            return null;
        }
        if (rowIndex > getRowCount())
        {
            //System.out.println("too big num");
            return null;
        }

        Animal a = animals.get(rowIndex);
        switch (columnIndex)
        {
            case 0:
                return a.getSpecies();
            case 1:
                return a.getBreed();
            case 2:
                return a.getName();
            case 3:
                return a.getSterilized();
            case 4:
                return a.getDateArrived();
            default:
                return null;
        }
    }

    /**
     * @param column
     * @return String columnName
     */
    @Override
    public String getColumnName(int column)
    {
        return columnNames[column];
    }
}
