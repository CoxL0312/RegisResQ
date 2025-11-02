/*
 * RegisResQ – MainUI
 * Author: Lindsey Cox
 * Date:   10/13/2025
 * -----------------------------------------------------------------------------
 * Purpose
 *   Swing JFrame for the app’s main window (presentation layer).
 *   Satisfies SRS-05: “The application shall allow users to view all animals…”
 *
 * Layout (BorderLayout)
 *   CENTER : JTable (columns: Type, Breed, Name, Sterilized, Arrived)
 *   SOUTH  : Form + Buttons
 *            - Form fields: Name, Breed, Arrival Date (MM/DD/YYYY as 3 boxes),
 *              Type (Cat/Dog), Spayed (Yes/No)
 *            - Buttons: Add, Modify, Delete, Exit
 *
 * UX / Accessibility
 *   - Mnemonics on labels (Alt+N/B/T/S) via setLabelFor(...) to move focus.
 *   - Global shortcuts on the root pane: see the HTML at the end of the class
 *
 * Data Flow
 *   - On startup: AnimalDao#getAll → ArrayList<Animal> → AnimalTableModel → JTable.
 *   - Clicking a table row populates the form for viewing/editing in later iterations.
 *   - “Add” uses the form to construct Cat/Dog, calls dao.add, re-queries, and refreshes model.
 *
 * Notes
 *   - Date inputs use a DocumentFilter to allow only digits or the hint text.
 *   - buildDateString() returns "yyyy-MM-dd" or "" if invalid (used to validate Add).
 *   - The model is read-only from the table’s perspective; editing is via the form/buttons.
 */



package RegisResQ.presentation;
import RegisResQ.application.*;
import RegisResQ.persistence.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;



/*
 * 
 */
public class MainUI extends JFrame{

    // ------------------------- Table & Data -------------------------//
    private JTable animalsTbl;
    private ArrayList<Animal> animals;
    private AnimalTableModel model;
    private AnimalDao database;
    private int selectedRow = -1;
    private javax.swing.table.TableRowSorter<AnimalTableModel> sorter;
    private final JComboBox<String> viewFilterCmb = new JComboBox<>(new String[]{"All", "Cat", "Dog"});

    // ------------------------- Form Controls -------------------------//
    private final JTextField nameTxtField = new JTextField();
    private final JTextField breedTxtField = new JTextField();
    private final JComboBox<String> typeCmb = new JComboBox<>(new String[]{"Cat", "Dog"});
    private final JComboBox<String> spayCmb = new JComboBox<>(new String[]{"Yes", "No"});

    //arrival date fields with hint texts
    private final JTextField tfMonth = new JTextField("MM");
    private final JTextField tfDay   = new JTextField("DD");
    private final JTextField tfYear  = new JTextField("YYYY");

    // ------------------------- Main UI Constructor -------------------------//
    public MainUI() 
    {
        setTitle("RegisResQ");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700, 520));

        //build all Swing widgets and register key bindings
        buildUIScaffold();

        //connect to DB and load table data
        database = new AnimalDao();
        try 
        {
            animals = database.getAll();
            //JOptionPane.showMessageDialog(null, "Loaded Animals: " + animals.size());
        }
        catch (Exception e)
        {
            animals = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Failed to load animals: " + e.getMessage(),
                 "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        //bind the model to the table and apply selection behavior
        model = new AnimalTableModel();
        model.setAnimals(animals);
        animalsTbl.setModel(model);
        model.fireTableDataChanged();
        animalsTbl.setColumnSelectionAllowed(false);
        animalsTbl.setRowSelectionAllowed(true);
        //enable sorting/filtering
        sorter = new javax.swing.table.TableRowSorter<>(model);
        animalsTbl.setRowSorter(sorter);

        animalsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //populate the form whenever selection changes (mouse ORRRR arrow keys!!)
        //list selection listener fires for both keyboard and mouse
        animalsTbl.getSelectionModel().addListSelectionListener(e ->
        {
            if (e.getValueIsAdjusting()) return;
            int row = animalsTbl.getSelectedRow();
            if (row < 0) return;

            breedTxtField.setText(String.valueOf(model.getValueAt(row, 1)));
            nameTxtField.setText(String.valueOf(model.getValueAt(row, 2)));
            String date = String.valueOf(model.getValueAt(row, 4));
            String[] parts = date.split("-");
            if (parts.length == 3) 
            {
                tfYear.setText(parts[0]);
                tfMonth.setText(parts[1]);
                tfDay.setText(parts[2]);
            }

            String type = String.valueOf(model.getValueAt(row, 0));
            typeCmb.setSelectedItem(type.equalsIgnoreCase("cat") ? "Cat" : "Dog");

            Object ster = model.getValueAt(row, 3);
            boolean isSter = (ster instanceof Boolean) ? (Boolean) ster
                                                    : "true".equalsIgnoreCase(String.valueOf(ster));
            spayCmb.setSelectedItem(isSter ? "Yes" : "No");
        });


        //enter on table means we jump to the field input form
        //doing it with action and input maps keeps the behavior scoped to "when the table has focus"
        InputMap pikachu = animalsTbl.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap eevee = animalsTbl.getActionMap();
        pikachu.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "focusFirstField");
        eevee.put("focusFirstField", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                nameTxtField.requestFocusInWindow();
                nameTxtField.selectAll();
            }
        });

 
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //************************ UI Construction **********************************//
    /**
     * build the menubar, table area, form, buttons, and global keystrokes
     */
    private void buildUIScaffold()
    {
        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBorder(new javax.swing.border.EmptyBorder(12, 12, 12, 12));
        setContentPane(main);

        //menu
        //File holds exit
        //Help holds keyboard shortcuts
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> dispose());
        file.add(exit);
        menuBar.add(file);
        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        JMenuItem kb = new JMenuItem("Keyboard Shortcuts");
        kb.setMnemonic(KeyEvent.VK_K);
        kb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        kb.addActionListener(e -> showKeyboardShortcuts());
        help.add(kb);
        menuBar.add(help);


        setJMenuBar(menuBar);

        //filter bar(north)
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JLabel viewLbl = new JLabel("View: ");
        viewLbl.setDisplayedMnemonic(KeyEvent.VK_V);
        viewLbl.setLabelFor(viewFilterCmb);
        viewFilterCmb.setSelectedIndex(0); //default ALL
        viewFilterCmb.addActionListener(e -> applyTypeFilter());
        filterBar.add(viewLbl);
        filterBar.add(viewFilterCmb);
        main.add(filterBar, BorderLayout.NORTH);



        //table (center)
        animalsTbl = new JTable();
        animalsTbl.setRowHeight(22);
        animalsTbl.setFillsViewportHeight(true);
        animalsTbl.setPreferredScrollableViewportSize(new Dimension(700, 180));
        main.add(new JScrollPane(animalsTbl), BorderLayout.CENTER);

        //form (south side, but above buttons))
        JLabel lbName = new JLabel("Name");
        lbName.setDisplayedMnemonic(KeyEvent.VK_N);
        lbName.setLabelFor(nameTxtField);

        JLabel lbBreed = new JLabel("Breed");
        lbBreed.setDisplayedMnemonic(KeyEvent.VK_B);
        lbBreed.setLabelFor(breedTxtField);

        JLabel lbSpecies = new JLabel("Species");
        lbSpecies.setDisplayedMnemonic(KeyEvent.VK_T);
        lbSpecies.setLabelFor(typeCmb);

        JLabel lbSpay = new JLabel("Spayed/Neutered");
        lbSpay.setDisplayedMnemonic(KeyEvent.VK_S);
        lbSpay.setLabelFor(spayCmb);

        //arrival date row (with the hints + digit filters)
        JLabel lbDate = new JLabel("Arrival Date");
        lbDate.setLabelFor(tfMonth);
        installHint(tfMonth, "MM");
        installHint(tfDay, "DD");
        installHint(tfYear, "YYYY");
        digitsOnly(tfMonth, 2, "MM");
        digitsOnly(tfDay, 2, "DD");
        digitsOnly(tfYear, 4, "YYYY");
        tfMonth.setColumns(2);
        tfDay.setColumns(2);
        tfYear.setColumns(4);

        JButton btnClearDate = new JButton("Clear");
        btnClearDate.setMnemonic(KeyEvent.VK_C);
        btnClearDate.addActionListener(e -> 
        {
            tfMonth.setText("MM");
            tfDay.setText("DD");
            tfYear.setText("YYYY");
        });

        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        dateRow.add(tfMonth);
        dateRow.add(tfDay);
        dateRow.add(tfYear);
        dateRow.add(btnClearDate);

        //key stroke events for date fields
        //we are doing global keyboard shortcuts that work anywhere while the window is focused
        //choice of using Swing's InputMap/ActionMap system on teh RootPane , instead of just
        //Key Listeners
        //the root pane is like the internal chassis for the JFrame window
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        //open the mneunominmimicsnscin cheat sheet from anywhere
        // F1 and Alt+H open the cheat-sheet from anywhere
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "showShortcuts");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_DOWN_MASK), "showShortcuts");
        am.put("showShortcuts", new AbstractAction() 
        {
            @Override public void actionPerformed(ActionEvent e) { showKeyboardShortcuts(); }
        });


        //focus on the table so you can use keyboard inputs to navigate
        //Alt-0 focus table (works from anywhere)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.ALT_DOWN_MASK), "focusTable");
        am.put("focusTable", new AbstractAction() 
        {
            @Override public void actionPerformed(ActionEvent e) { focusTable(true); }
        });

        //esc-jump back to table, keep current selection if any
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "focusTableEsc");
        am.put("focusTableEsc", new AbstractAction() 
        {
            @Override public void actionPerformed(ActionEvent e) { focusTable(false); }
        });

        //Crtl + up, Ctrl + Down -> move selection up and down even if a text field has focus
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK), "tablePrev");
        am.put("tablePrev", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e)
            {
                int row = Math.max(0, animalsTbl.getSelectedRow() -1);
                if (animalsTbl.getRowCount() == 0) return;
                animalsTbl.setRowSelectionInterval(row, row);
                focusTable(false);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK), "tableNext");
        am.put("tableNext", new AbstractAction() 
        {
            @Override public void actionPerformed(ActionEvent e) {
            if (animalsTbl.getRowCount() == 0) return;
            int row = animalsTbl.getSelectedRow();
            row = (row < 0) ? 0 : Math.min(animalsTbl.getRowCount() - 1, row + 1);
            animalsTbl.setRowSelectionInterval(row, row);
            focusTable(false);
            }
        });



        //date fields
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK), "focusMonth");
        am.put("focusMonth", new AbstractAction() 
        { @Override public void actionPerformed(ActionEvent e)
            { tfMonth.requestFocusInWindow(); 
              tfMonth.selectAll(); 
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_DOWN_MASK), "focusDay");
        am.put("focusDay", new AbstractAction() 
        { @Override public void actionPerformed(ActionEvent e)
            { tfDay.requestFocusInWindow(); 
              tfDay.selectAll(); 
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_DOWN_MASK), "focusYear");
        am.put("focusYear", new AbstractAction() 
        { @Override public void actionPerformed(ActionEvent e)
            { tfYear.requestFocusInWindow(); 
              tfYear.selectAll(); 
            }
        });

        //key stroke events for options in the combo boxes
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK), "typeCat");
        am.put("typeCat", new AbstractAction()
        { 
            @Override public void actionPerformed(ActionEvent e)
            { 
                typeCmb.setSelectedItem("Cat"); 
                typeCmb.requestFocusInWindow(); 
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.ALT_DOWN_MASK), "typeDog");
        am.put("typeDog", new AbstractAction()
        { 
            @Override public void actionPerformed(ActionEvent e)
            { 
                typeCmb.setSelectedItem("Dog"); 
                typeCmb.requestFocusInWindow(); 
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.ALT_DOWN_MASK), "Yes");
        am.put("Yes", new AbstractAction()
        { 
            @Override public void actionPerformed(ActionEvent e)
            { 
                spayCmb.setSelectedItem("Yes"); 
                spayCmb.requestFocusInWindow(); 
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.ALT_DOWN_MASK), "No");
        am.put("No", new AbstractAction()
        { 
            @Override public void actionPerformed(ActionEvent e)
            { 
                spayCmb.setSelectedItem("No"); 
                spayCmb.requestFocusInWindow(); 
            }
        });

        //grid for the labels & input fields
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(lbName);
        form.add(nameTxtField);
        form.add(lbBreed);
        form.add(breedTxtField);
        form.add(lbDate);
        form.add(dateRow);
        form.add(lbSpecies);
        form.add(typeCmb);
        form.add(lbSpay);
        form.add(spayCmb);

        //buttons - the stubs for CRUD hooks
        JButton btnAdd = new JButton("Add");
        btnAdd.setMnemonic(KeyEvent.VK_A);
        btnAdd.addActionListener(e -> 
        {
            String name = nameTxtField.getText().trim();
            String breed = breedTxtField.getText().trim();
            String type = (String) typeCmb.getSelectedItem();
            boolean sterilized = "Yes".equals(spayCmb.getSelectedItem());
            String date = buildDateString();
            if (name.isEmpty() || breed.isEmpty() || date.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Enter Name, Breed, and a valid date (MM/DD/YYYY)");
                return;
            }

            Animal a = "Cat".equals(type) ? new Cat("Cat", breed, name, sterilized, date) : new Dog("Dog", breed, name, sterilized, date);
            if (database.add(a))
            {
                refreshTableAndClear();
            }
            else { JOptionPane.showMessageDialog(this, "Insert failed"); }
        });




        JButton btnMod = new JButton("Modify");
        btnMod.setMnemonic(KeyEvent.VK_M);

        /**
         * the AnimalDao class uses the name field for looking up which animal to modify, so this function listener
         * needs robust logic for handling that scenario-it does a Delete and then Add, with ROLLBACK if the add fails
         * grab the edited values from the form, normalizing the choices and trimming them, as well as update validation
         * construct a new Animal instance for these values
         * read the currently stored values, importantly grabbing the original name
         * build an animal instance to match the original (this will be our key for DAO ops that match by name)
         * if the name change, we notify the user that the record will be deleted and a new one added (this can be changed, but good to know)
         * delete the record using the original name, here we have roll back
         * 
         */
        btnMod.addActionListener(e -> {
            int row = animalsTbl.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a row to modify");
                return;
            }

            // New values from the form
            String name  = nameTxtField.getText().trim();
            String breed = breedTxtField.getText().trim();
            String type  = (String) typeCmb.getSelectedItem();
            boolean sterilized = "Yes".equals(spayCmb.getSelectedItem());
            String date = buildDateString();

            if (name.isEmpty() || breed.isEmpty() || date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Name, Breed, and a valid date (YYYY-MM-DD)");
                return;
            }

            // Build the new Animal (what we want to persist)
            Animal updated = "Cat".equals(type)
                    ? new Cat("Cat", breed, name, sterilized, date)
                    : new Dog("Dog", breed, name, sterilized, date);

            // Original values from the table row (what's currently in DB)
            String origType  = String.valueOf(model.getValueAt(row, 0));
            String origBreed = String.valueOf(model.getValueAt(row, 1));
            String origName  = String.valueOf(model.getValueAt(row, 2));
            Object sterObj   = model.getValueAt(row, 3);
            boolean origSter = (sterObj instanceof Boolean) ? (Boolean) sterObj
                            : "true".equalsIgnoreCase(String.valueOf(sterObj));
            String origDate  = String.valueOf(model.getValueAt(row, 4));

            Animal original = origType.equalsIgnoreCase("cat")
                    ? new Cat("Cat", origBreed, origName, origSter, origDate)
                    : new Dog("Dog", origBreed, origName, origSter, origDate);

            // If name hasn't changed, a simple UPDATE works
            if (name.equals(origName)) 
            {
                if (database.update(updated)) 
                {
                    refreshTableAndClear();
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed");
                }
                return;
            }

            // Name DID change -> emulate rename via DELETE + ADD
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "You changed the Name from \"" + origName + "\" to \"" + name + "\".\n"
                + "This will delete the old record and insert a new one.\nContinue?",
                    "Confirm Rename",
                    JOptionPane.YES_NO_OPTION);

            if (choice != JOptionPane.YES_OPTION) return;

            boolean deleted = database.delete(original);   // delete by original name
            if (!deleted) 
            {
                JOptionPane.showMessageDialog(this, "Rename failed: original record not found/deleted.");
                return;
            }

            boolean added = database.add(updated);
            if (added) 
            {
                refreshTableAndClear();
            } else {
                // rollback to be safe
                database.add(original);
                JOptionPane.showMessageDialog(this, "Rename failed while inserting new record. Original restored.");
            }
        });




        JButton btnDel = new JButton("Delete");
        btnDel.setMnemonic(KeyEvent.VK_D);
        btnDel.addActionListener(e-> 
        {
            int row = animalsTbl.getSelectedRow();
            if (row < 0)
            {
                JOptionPane.showMessageDialog(this, "Select a row to delete");
                return;
            }

            Animal a = animals.get(row);
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete the selected row?\n" + a.getName() + " (" + a.getBreed() + ")",
                "Yes/No",
                JOptionPane.YES_NO_OPTION);
            
            if (choice != JOptionPane.YES_OPTION) return;

            if (database.delete(a))
            {
                refreshTableAndClear();
            } else {
                JOptionPane.showMessageDialog(this, "The animal did not want to be deleted");
            }
        });

        JButton btnExit = new JButton("Exit");
        btnExit.setMnemonic(KeyEvent.VK_X);
        btnExit.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new GridLayout(1, 4, 8, 8));
        buttons.add(btnAdd);
        buttons.add(btnMod);
        buttons.add(btnDel);
        buttons.add(btnExit);

        JPanel south = new JPanel(new BorderLayout(8, 8));
        south.setBorder(new javax.swing.border.EmptyBorder(12, 0, 0, 0));
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);

        main.add(south, BorderLayout.SOUTH);
    }

    //----------------------- Helpers ----------------------------//
    /*
     * Helper function that adds a focuslistener so when the field gains "focus", the placeholder is cleared
     * When the focus is lost, the hint is restored
     * Used for the arrived date field, so that the user does not have to manually delete the placeholder text
     * MM - DD - YYYY
     * 
     * @param JTextField tf
     * @param String hint
     */
    private void installHint(JTextField tf, String hint)
    {
        tf.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                if (tf.getText().equals(hint))
                {
                    tf.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (tf.getText().isBlank())
                {
                    tf.setText(hint);
                }
            }
        });
    }

    /**
     * requery DB, refresh table, clear form, reset selection
     * called after CRUD ops
     */
    private void refreshTableAndClear()
    {
        animals = database.getAll();
        model.setAnimals(animals);
        model.fireTableDataChanged();
        clearForm();
        selectedRow = -1;
    }

    /*
     * attaches a Document Filter to the field's document, 
     * this document holds the intercepted text for inserts/replacements
     * but only allows the change if it's digits only and the length is <= max or
     * it outright equals the hint text
     * Document Filter specifically because it prevents invalid characters *as you type* -> really cool stuff
     * Other options would work too: KeyListener (tho this would miss stuff like pastes), InputVerifier (this doesn't do real time checks tho),
     * and JFormattedTextField (larger and a bit more cumbersome imo to work with?)
     * 
     * @param JTextField tf --such as tfMonth
     * @param int max
     * @param String hint
     * 
     */
    private void digitsOnly(JTextField tf, int max, String hint)
    {
        //attach the filter by casting the tf's document to abstract document
        ((AbstractDocument) tf.getDocument()).setDocumentFilter(new DocumentFilter()
        {
            //call this whenever a change would replace some text in the document for the tf
            //a filter bypasss lets us forward the edit to the actual document if we approve it, hence the name
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException
            {
                //these lines compute what the text would become
                //next is the preview of the post-edit text
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String next = current.substring(0, offset)
                        + (text == null ? "" : text)
                        + current.substring(offset + length);

                //validate the preview
                boolean okDigits = next.length() <= max && next.chars().allMatch(Character::isDigit);
                boolean isHint   = next.equals(hint);

                //apply or block
                if (okDigits || isHint)
                {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            //extra check for if an edit comes in as an "insert" instead of a replace
            //just make it a replace
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs)
                    throws BadLocationException
            {
                replace(fb, offset, 0, text, attrs);
            }
        });
    }

        /*
    * This is used for when we are uploading data to the model, currently not used, may just get moved to another class
    * in a later iteration
    * basically just reformats the string using the private fields above
    * 
    * @return String of either yyyy-mm-dd format (what goes into the DB) or "" so the calling code shows an error
    */
    private String buildDateString()
    {
        String mm = tfMonth.getText().trim();
        String dd = tfDay.getText().trim();
        String yy = tfYear.getText().trim();

        if (mm.length() != 2 || dd.length() !=2 || yy.length() != 4) return "";
        if (!mm.chars().allMatch(Character::isDigit)) return "";
        if (!dd.chars().allMatch(Character::isDigit)) return "";
        if (!yy.chars().allMatch(Character::isDigit)) return "";

        int m = Integer.parseInt(mm);
        int d = Integer.parseInt(dd);
        int y = Integer.parseInt(yy);

        if (m < 1 || m > 12) return "";

        int[] max = {31, (isLeap(y) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (d < 1 || d > max[m - 1]) return "";

        //OKAY, return DB/model format
        return String.format("%04d-%02d-%02d", y, m, d);
    }

    private boolean isLeap(int y)
    {
        return (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0);
    }

    /**
     * function to clear form
     */
    private void clearForm() 
    {
        nameTxtField.setText("");
        breedTxtField.setText("");
        typeCmb.setSelectedIndex(0);
        spayCmb.setSelectedIndex(0);
        tfMonth.setText("MM"); tfDay.setText("DD"); tfYear.setText("YYYY");
    }


    /**
     * logic for the modify button
     */
    private void doModify()
    {
       int row = animalsTbl.getSelectedRow();
            if (row < 0)
            {
                JOptionPane.showMessageDialog(this, "Select a row to modify");
                return;
            }

            String name = nameTxtField.getText().trim();
            String breed = breedTxtField.getText().trim();
            String type = (String) typeCmb.getSelectedItem();
            boolean sterilized = "Yes".equals(spayCmb.getSelectedItem());
            String date = buildDateString();

            if (name.isEmpty() || breed.isEmpty() || type.isEmpty() || date.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Enter Nme, Breed, and a valid date");
            }

            Animal a = "Cat".equals(type)
                ? new Cat("Cat", breed, name, sterilized, date)
                : new Dog("Dog", breed, name, sterilized, date);
            
            if (database.update(a))
            {
                refreshTableAndClear();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed");
            }

    }

    
        private void focusTable(boolean selectFirstIfNone)
        {
            animalsTbl.requestFocusInWindow();
            if (selectFirstIfNone && animalsTbl.getSelectedRow() < 0 && animalsTbl.getRowCount() > 0)
            {
                animalsTbl.setRowSelectionInterval(0, 0); //pick first row if nothing seletced
            }
        }

            /** Show a modal cheat-sheet of all mnemonics and global shortcuts. */
    private void showKeyboardShortcuts() {
        String html =
            "<html><body style='font-family:sans-serif;font-size:11pt;'>"
        + "<h2 style='margin-top:0'>Keyboard Shortcuts</h2>"
        + "<h3 style='margin-bottom:2px'>Mnemonics (Alt + Key)</h3>"
        + "<table border='0' cellspacing='0' cellpadding='2'>"
        + "  <tr><td><b>Alt+N</b></td><td>Focus Name</td></tr>"
        + "  <tr><td><b>Alt+B</b></td><td>Focus Breed</td></tr>"
        + "  <tr><td><b>Alt+T</b></td><td>Focus Species (Cat/Dog)</td></tr>"
        + "  <tr><td><b>Alt+S</b></td><td>Focus Spayed/Neutered</td></tr>"
        + "  <tr><td><b>Alt+A</b></td><td>Activate Add</td></tr>"
        + "  <tr><td><b>Alt+M</b></td><td>Activate Modify</td></tr>"
        + "  <tr><td><b>Alt+D</b></td><td>Activate Delete</td></tr>"
        + "  <tr><td><b>Alt+X</b></td><td>Exit</td></tr>"
        + "  <tr><td><b>Alt+C</b></td><td>Clear Date</td></tr>"
        + "</table>"

        + "<h3 style='margin-bottom:2px;margin-top:12px'>Global Shortcuts</h3>"
        + "<table border='0' cellspacing='0' cellpadding='2'>"
        + "  <tr><td><b>Alt+1</b></td><td>Focus Month (MM)</td></tr>"
        + "  <tr><td><b>Alt+2</b></td><td>Focus Day (DD)</td></tr>"
        + "  <tr><td><b>Alt+3</b></td><td>Focus Year (YYYY)</td></tr>"
        + "  <tr><td><b>Alt+Q</b></td><td>Type = Cat</td></tr>"
        + "  <tr><td><b>Alt+G</b></td><td>Type = Dog</td></tr>"
        + "  <tr><td><b>Alt+Y</b></td><td>Spayed = Yes</td></tr>"
        + "  <tr><td><b>Alt+Z</b></td><td>Spayed = No</td></tr>"
        + "  <tr><td><b>Alt+0</b></td><td>Focus the table</td></tr>"
        + "  <tr><td><b>Esc</b></td><td>Return focus to table</td></tr>"
        + "  <tr><td><b>Ctrl+↑ / Ctrl+↓</b></td><td>Move table selection (from anywhere)</td></tr>"
        + "  <tr><td><b>Enter</b></td><td>Load row into form or Modify (your binding)</td></tr>"
        + "</table>"

        + "<p style='margin-top:12px'><i>Tip:</i> Use arrows in the table to navigate rows, "
        + "then <b>Enter</b> to load or modify depending on your current binding.</p>"
        + "</body></html>";

        // wrap in a scroll pane in case fonts/OS scaling clip the content
        JLabel content = new JLabel(html);
        JScrollPane scroller = new JScrollPane(content);
        scroller.setPreferredSize(new Dimension(520, 420));

        JOptionPane.showMessageDialog(
            this,
            scroller,
            "Keyboard Shortcuts",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /** Apply the table filter based on the "View" combo (All/Cat/Dog). */
    private void applyTypeFilter() {
        if (sorter == null) return; // not yet initialized

        String choice = String.valueOf(viewFilterCmb.getSelectedItem());
        if ("All".equalsIgnoreCase(choice)) {
            sorter.setRowFilter(null); // show everything
            return;
        }

        // Column 0 in your table is "Type" (Cat/Dog). Match exactly, case-insensitive.
        RowFilter<AnimalTableModel, Integer> rf =
            RowFilter.regexFilter("(?i)^" + java.util.regex.Pattern.quote(choice) + "$", 0);
        sorter.setRowFilter(rf);
    }




    //main
    public static void main(String[] args) 
    {
    //error code in here to test output as I was having issues with the mysql connector
    //temp fix by running via command lines in the terminal
    /**
     *     // 0) show the runtime classpath VS Code is actually using
    System.out.println("CLASSPATH = " + System.getProperty("java.class.path"));

    // 1) prove whether the driver is visible at runtime
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("MySQL driver FOUND.");
    } catch (ClassNotFoundException e) {
        System.err.println("MySQL driver MISSING at runtime.");
        e.printStackTrace();
        return; // bail so we don't crash inside AnimalDao
    }
     */

    // 2) launch UI
        SwingUtilities.invokeLater(() -> new MainUI());
    }

}
