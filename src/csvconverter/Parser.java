/*
 * Soft Computing Project 1: Rollie Goodman, Janette Rounds, Monica Thornton
 *
 * A class to take care of parsing a file (with either 
 * a .txt or a .csv extension) and building an .arff file for use in WEKA.
 */
package csvconverter;

//for reading in files
import java.io.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
//for constructing a regex for splitting attribute names
import java.util.regex.*;

/**
 *
 * @author Janette
 */
public class Parser {

    //for keeping an array of all files to be .arff'ed
    ArrayList<String> fileList = new ArrayList<String>();

    /*
     * Method for getting a list of all the files in the user specified folder.
     * Each valid file (i.e. not .arff files) is added to the array list of file
     * names.
     */
    public void listFilesForFolder(String path) {
        File[] files = new File(path).listFiles();
        String fileName;

        //adds each valid file to the array list
        for (File file : files) {
            if (file.isFile()) {
                fileName = file.toString();
                //does not add .arff files, because those do not need to be parsed
                if (!fileName.substring(fileName.length() - 4).equals("arff")) {
                    fileList.add(file.getAbsolutePath());
                }
            } //end ifs
        } //end for
    }

    /*
     * Method for checking all file types in the fileList to make sure files 
     * are of valid type (which for this instance is .csv or .txt).
     */
    public String detectFileTypes(String a) {
        //gets the file extension
        String sub1 = a.substring(a.length() - 4);
        String sub2 = "";
        //checks validity of file type
        if (sub1.equals(".csv") || sub1.equals(".txt")) {
            //gets name for saving .arff file
            sub2 = a.substring(0, a.length() - 4);
        } else {
            //panic!!!
            System.out.println("Please pick a .csv or .txt file");
            System.exit(1);
        }
        return sub2;
    }

    /*
     * Method for making the arff file, given the relation name.
     */
    public BufferedWriter makeArffFiles(String a) throws FileNotFoundException {
        //here rel is a file path
        String rel = detectFileTypes(a);

        //starts buffered reader and writer
        BufferedWriter writer = null;
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(new File(a)));

        //writes the arff file
        try {
            //names the created file
            FileWriter fileWriter = new FileWriter(rel + ".arff");

            //pares down to just necessary elements after file has been started
            String relationName = rel.substring(rel.lastIndexOf("\\") + 1).trim();
            
            if (relationName.contains(".data")) {
                relationName = relationName.substring(0, relationName.length() - 5);
            } else if (relationName.contains("-data")) {
                relationName = relationName.substring(0, relationName.length() - 5);                
            } else if (relationName.contains("data_")) {
                relationName = relationName.substring(5, relationName.length());
            }
              
            writer = new BufferedWriter(fileWriter);

            //writes relation info in file
            writer.write("@RELATION " + relationName);
            writer.newLine();

            //get the relation info
            makeAttributes(writer, reader, a);

            //adds data to file
            addDataToArff(writer, reader);

        } catch (IOException e) {
            //prints error in case of IO exception
            e.printStackTrace();
        }

        return writer;
    }

    /*
     * Method for getting the attribute types from data
     */
    public String[] detectAttributeTypes(String[] attTypes, String path) throws FileNotFoundException {

        //iterates over all of the attribute data to determine types
        String thisItem;

        //matches pos and neg integers with optional negative sign
        String integerPattern = "-?\\s?\\d*";

        //matches pos and neg real numbers with optional negative sign
        String realNumberPattern = "-?\\s?\\d*\\.\\d+";

        //matches dates, in formats used in our datasets
        String datePattern1 = "([0-9]{2})/([0-9]{2})/([0-9]{4})";
        String datePattern2 = "([0-9]{2})-(\\w\\w\\w)-([0-9]{4})";

        //matches strings
        String stringPattern = ".";

        for (int i = 0; i < attTypes.length - 1; i++) {
            thisItem = attTypes[i];
            //checks if is positive or negative integer
            if (thisItem.matches(integerPattern) || thisItem.matches(realNumberPattern)) {
                attTypes[i] = "NUMERIC";
            } else if (thisItem.matches(datePattern1) || thisItem.matches(datePattern2)) {
                //checks if value is a date
                attTypes[i] = "DATE";
            } else if (thisItem.matches(stringPattern)) {
                attTypes[i] = "STRING";
            }
        }

        //the last column in all of our datasets corresponds to the classifier
        attTypes[attTypes.length - 1] = " {";

        Scanner classTypeScanner = new Scanner(new File(path));

        //throw away the attribute names
        String attributeNames = classTypeScanner.nextLine();
        String line = "";
        ArrayList<String> classCats = new ArrayList<String>();;

        while (classTypeScanner.hasNextLine()) {
            line = classTypeScanner.nextLine();
            line = line.substring(line.lastIndexOf(",") + 1).trim();

            if (classCats.contains(line)) {
                //do nothing
            } else {
                classCats.add(line);
            }
        }

        //moves through every entry in the list of categories
        Iterator<String> iterator = classCats.iterator();
        String allCats = "";

        while (iterator.hasNext()) {
            //add comma separated vaues to the string
            allCats += iterator.next() + ",";
        }

        //trim last comma
        while ((allCats.charAt(allCats.length() - 1)) == ',') {
            allCats = allCats.substring(0, allCats.length() - 1);
        }

        //get classifier in correct form
        attTypes[attTypes.length - 1] += allCats + "}";

        //returns the list of data, which now reflects the type of data
        return attTypes;
    }

    /*
     * Method for getting the attribute names from the data. In the event that 
     * attribute names are not given, attribute names of the form A# are provided.
     */
    public void makeAttributes(BufferedWriter w, BufferedReader r, String path) {
        String attributeNames, attributeTypes;
        String[] attNames;

        //for discerning where to split the above strings
        Pattern attPattern;

        Boolean noAttNames = false;

        try {
            //get first line, which gives you attribute names
            r.mark(10000);
            attributeNames = r.readLine();

            //constructs a pattern to split on commas (except when within "")
            attPattern = Pattern.compile(
                    "(?x)          # enables free spacing (whitespace between tokens ignored)   \n"
                    + "(\"[^\"]*\")  # quoted data, where commas are treated differently (group1) \n"
                    + "|             # OR                                                         \n"
                    + "([^,]+)       # one or more chars with no quotes and no commas (group 2)   \n"
                    + "|             # OR                                                         \n"
                    + "\\s*,\\s*     # a , (with spaces around or not) for splitting              \n"
            );

            if (attributeNames.substring(0, 1).matches("-?\\s?\\d*")) {
                noAttNames = true;

                //get the number of attributes
                attNames = attributeNames.split(",", -1);

                //name attributes                
                for (int i = 0; i < attNames.length; i++) {
                    attNames[i] = "'A" + i + "'";
                }

                //reset reader
                r.reset();
            } else {

                Matcher matchedAttNames = attPattern.matcher(attributeNames);
                String allMatched = "";
                
                //generates a string of all matches, to store in attNames array
                while (matchedAttNames.find()) {
                    String matched = matchedAttNames.group().trim();
                    if (matchedAttNames.group(1) != null || matchedAttNames.group(2) != null) {
                       //checks if begins in double quotes, if so replaces
                       //per WEKA documentation, anything with spaces should go  
                       //between single quotes
                        if (matched.charAt(0) != '"') {
                            allMatched += "'" + matched + "' \n";
                        } else {
                            //removes double quotes, replaces with single quotes
                            matched = matched.substring(1, matched.length());
                            matched = matched.substring(0, matched.length() - 1);
                            allMatched += "'" + matched + "' \n";
                        }
                    }
                }
                //splits string, stores in array of attribute names
                attNames = allMatched.split("\\r?\\n");
            }

            /*
             * Uses mark and reset to read in the second line in order to get the 
             * attribute type data, but then resets it so those values can be re-read
             * in as data.
             */
            r.mark(10000);
            attributeTypes = r.readLine();
            r.reset();

            //have list of attribute names, need to figure out type
            Matcher matchedAttTypes = attPattern.matcher(attributeTypes);
            String matchedTypes = "";

            while (matchedAttTypes.find()) {
                String matched = matchedAttTypes.group().trim();

                if (matchedAttTypes.group(1) != null || matchedAttTypes.group(2) != null) {
                    matchedTypes += matched + "\n";
                }
            }

            String attTypes[] = matchedTypes.split("\\r?\\n");

            //take the data from the second row, use it to determine types  
            attTypes = detectAttributeTypes(attTypes, path);
            //now have attribute names and types, write them out
            //form @ATTRIBUTE name datatype
            addAttributeToArff(w, attNames, attTypes);
        } //end try
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addDataToArff(BufferedWriter w, BufferedReader r) {
        String line = null;
        try {
            w.newLine();
            w.write("@DATA ");
            w.newLine();
            line = r.readLine();
            while (line != null) {
                w.write(line);
                w.newLine();
                line = r.readLine();
            }
            r.close();
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addAttributeToArff(BufferedWriter w, String[] attNames, String[] attTypes) {
        String line = null;
        try {
            for (int i = 0; i < attNames.length; i++) {
                w.newLine();
                w.write("@ATTRIBUTE ");
                w.write(" " + attNames[i] + " " + attTypes[i]);
            }
            w.newLine();
            //w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void converter(String path) throws FileNotFoundException {
        listFilesForFolder(path);

        for (int i = 0; i < fileList.size(); i++) {
            makeArffFiles(fileList.get(i));

        }

    }
}
