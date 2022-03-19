package kea.exercises;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{

    static Statement stmt;
    static String sqlString;
    static Connection con;

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
        updateDB(); //Updates db table with csv data
    }

    private static void updateDB()
    {
        connectDB();
        try
        {
            stmt = con.createStatement();
            stmt.executeUpdate("DROP TABLE `new_table_from_csv`");
        }
        catch (Exception e)
        {
            System.out.println(e);
            System.out.println("No existing table to delete");
        }

        try
        {
            //Create a table in the database
            sqlString = "CREATE TABLE `new_table_from_csv` ( " +
                    "`Year` INT NULL," +
                    "`Length` INT NULL," +
                    "`Title` VARCHAR(128) NULL," +
                    "`Subject` VARCHAR(20) NULL," +
                    "`Popularity` INT NULL," +
                    "`Awards` VARCHAR(4) NULL)";

            stmt.executeUpdate(sqlString);

            Scanner scanner = new Scanner(new File("src/imdb-data.csv"));
            scanner.useDelimiter(";|\n");
            String title, subject, awards;
            int year, length, popularity;
            scanner.nextLine();
            while(scanner.hasNextLine())
            {
                year = scanner.nextInt();
                length = scanner.nextInt();
                title = scanner.next();
                subject = scanner.next();
                popularity = scanner.nextInt();
                awards = scanner.next();
                sqlString = "INSERT INTO new_table_from_csv (`Year`,`Length`,`Title`,`Subject`,`Popularity`,`Awards`) VALUES(?,?,?,?,?,?)";
                PreparedStatement statement = con.prepareStatement(sqlString);
                statement.setInt(1,year);
                statement.setInt(2,length);
                statement.setString(3,title);
                statement.setString(4,subject);
                statement.setInt(5,popularity);
                statement.setString(6,awards);
                statement.executeUpdate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }public static void connectDB()
{
    try
    {
        String url = "jdbc:mysql://localhost:3306/movies_db";
        con = DriverManager.getConnection(url,"root","ekch22Lmysql");
        System.out.println("Ok, we have a connection.");
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
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
