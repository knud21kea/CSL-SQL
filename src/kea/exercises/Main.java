package kea.exercises;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{

    //Reads contents of a csv file
    //Outputs SQL to create a DB table to ddl.sql
    //Outputs SQL to fill the table to dml.sql

    private static final File cslSourceFile = new File("src/imdb-data.csv");
    private static final String definitionData = "src/ddl.sql";
    private static final String manipulationData = "src/dml.sql";
    private static final ArrayList<String> listOfDataLines = new ArrayList<>();
    private static String listOfColumnTitles;

    public static void main(String[] args)
    {
        loadFile(); // Load all data from given CSV to array list listOfDataLines
        listOfColumnTitles = listOfDataLines.get(0); // Get first line (column names)
        listOfDataLines.remove(0); // Remove column names from data
        writeDdl(); // Write DDL SQL to create db to ddl.sql
        writeDml(); // Write DML SQL to update db to dml.sql
    }

    public static void loadFile()
    {
        try
        {
            Scanner input = new Scanner(cslSourceFile);
            String line;
            while (input.hasNext())
            {
                line = input.nextLine();
                listOfDataLines.add(line);
            }
            System.out.println("Movie data loaded successfully.");
            System.out.println("For " + listOfDataLines.size() + " rows.");
        } catch (FileNotFoundException e)
        {
            System.out.println(cslSourceFile + " not found.");
        }
    }

    public static void writeDdl()
    {
        String[] columnNames = listOfColumnTitles.split(";");
        StringBuilder ddl = new StringBuilder("CREATE TABLE new_table_from_csv (\n");
        for (String columnName : columnNames)
        {
            ddl.append("\t").append(columnName).append(" varchar(255),\n");
        }
        ddl.append(");");
        writeFile(definitionData, ddl.toString());
        System.out.println("DDL saved");
    }

    public static void writeDml()
    {

        StringBuilder dml = new StringBuilder();
        for (String s : listOfDataLines)
        {
            String[] dataLines = s.split(";");
            dml.append("\nINSERT INTO new_table_from_csv");
            dml.append("\nVALUES (");
            for (String v : dataLines)
            {
                dml.append(v).append(",");
            }
            dml.setLength(dml.length() - 1); // Remove last semicolon
            dml.append(");");
        }
        writeFile(manipulationData, dml.toString());
        System.out.println("DML saved");
    }


    public static void writeFile(String file, String output)
    {
        PrintStream ps;
        {
            try
            {
                ps = new PrintStream(new FileOutputStream(file, false));
                if (listOfDataLines == null) // Avoids existing but empty csv
                {
                    System.out.println("No data to save");
                } else
                {
                    ps.println(output);
                }
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }
}
