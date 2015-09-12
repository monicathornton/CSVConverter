/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvconverter;

//for reading in files
import java.io.*;
import java.util.ArrayList;
//for constructing a regex for splitting attribute names
import java.util.regex.*;

/**
 *
 * @author Janette
 */
public class Parser {

    ArrayList<String> fileList = new ArrayList<String>();
  

    public Parser() {

    }

    public void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                fileList.add(fileEntry.getName());
            }
        }
    }

    public String detectFileTypes(String a) {
        String sub1 = a.substring(a.length() - 4);
        String sub2 = "";
        if (sub1.equals(".csv") || sub1.equals(".txt")) {
            sub2 = a.substring(0, a.length() - 4);
        } else {
            //panic!!!
        }
        return sub2;
    }

    public BufferedWriter makeArffFiles(String a) throws FileNotFoundException {
        String rel = detectFileTypes(a);
        //maybe should make a string to store filepath?
        BufferedWriter writer = null;
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(new File(rel)));
        
        try {
            FileWriter fileWriter = new FileWriter(rel + ".arff");
            writer = new BufferedWriter(fileWriter);
            writer.write("@RELATION " + rel);
            writer.newLine();
            //get the relation info
            makeAttributes(reader, writer);
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer;
    }

    public String[] detectAttributeTypes(String[] attTypes) {
        //TODO: HOW TO HANDLE DOLLAR SIGNS? CURRENTLY FLOATS, do I axe the $?
        //iterates over all of the attribute data to determine types
        String thisItem;
        //matches pos and neg integers with optional negative sign
        String integerPattern = "-?\\s?\\d*";
        //matches pos and neg real numbers with optional negative sign
        String realNumberPattern = "-?|$?\\s?\\d*\\.\\d+";
        //matches dates, in formats used in our datasets
        String datePattern1 = "([0-9]{2})/([0-9]{2})/([0-9]{4})";
        String datePattern2 = "([0-9]{2})-(\\w\\w\\w)-([0-9]{4})";
        //matches strings
        String stringPattern = ".";
        //matches nominals
        //String nominal = "";

        for (int i = 0; i < attTypes.length; i++) {
            thisItem = attTypes[i];
            //checks if is positive or negative integer
            if (thisItem.matches(integerPattern) || thisItem.matches(realNumberPattern)) {
                attTypes[i] = "numeric";
            } else if (thisItem.matches(datePattern1) || thisItem.matches(datePattern2)) {
                //checks if value is a date
                attTypes[i] = "date";
            } else if (thisItem.matches(stringPattern)) {
                attTypes[i] = "string";
            } else {
                //nominal (list)
            }
        }
        //returns the list of data, which now reflects the type of data
        return attTypes;
    }

    //gets all the attribute names from the csv file
    public void makeAttributes(BufferedReader r, BufferedWriter w) {
        String attributeNames, attributeTypes;

        try {
            //get first line, which gives you attribute names
            attributeNames = r.readLine();
            //TODO: might need to "unread" the second line? or just use as data
            //get data from second line, which will give you the information for types
            attributeTypes = r.readLine();

            //constructs a pattern to split on commas (except when within "")
            Pattern attPattern = Pattern.compile(
                    "(?x)          # enables free spacing (whitespace between tokens ignored)   \n"
                    + "(\"[^\"]*\")  # quoted data, where commas are treated differently (group1) \n"
                    + "|             # OR                                                         \n"
                    + "([^,]+)       # one or more chars with no quotes and no commas (group 2)   \n"
                    + "|             # OR                                                         \n"
                    + "\\s*,\\s*     # a , (with spaces around or not) for splitting              \n"
            );

            Matcher matchedAttNames = attPattern.matcher(attributeNames);
            String[] attNames = new String[matchedAttNames.groupCount()];
            int index = 0;

            while (matchedAttNames.find()) {
                // get the match
                String matched = matchedAttNames.group().trim();

                // only put the match in the array if it in groups #1 or #2
                if (matchedAttNames.group(1) != null || matchedAttNames.group(2) != null) {
                    attNames[index] = matched;
                    index++;
                }
            }

            //have list of attribute names, need to figure out type
            Matcher matchedAttTypes = attPattern.matcher(attributeTypes);
            String[] attTypes = new String[matchedAttTypes.groupCount()];

            index = 0;

            while (matchedAttTypes.find()) {
                // get the match
                String matched = matchedAttTypes.group().trim();

                // only put the match in the array if it in groups #1 or #2
                if (matchedAttTypes.group(1) != null || matchedAttTypes.group(2) != null) {
                    attTypes[index] = matched;
                    index++;
                }
            }

            //take the data from the second row, use it to determine types  
            attTypes = detectAttributeTypes(attTypes);
            //now have attribute names and types, write them out
            //form @ATTRIBUTE name datatype
            addRelationToArff(w, attNames, attTypes);
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
            line = r.readLine();
            while (line != null) {
                w.write(line);
                line = r.readLine();
            }
            r.close();
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addRelationToArff(BufferedWriter w, String[] attNames, String[] attTypes) {
        String line = null;
        try {
            for (int i = 0; i < attNames.length; i++) {
                w.newLine();
                w.write("@RELATION ");
                w.write(" " + attNames[i] + " " + attTypes[i]);
            }
            //w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void converter() throws FileNotFoundException {
        //for (int i = 0; i < fileList.size(); i++) {
            //makeArffFiles(fileList.get(i));
            makeArffFiles("C:\\Users\\Monica\\Documents\\GitHub\\CSVConverter\\src\\csvconverter\\transfusion.data.txt");
            //TO DO: CHECK FOR BLANK LINE BETWEEN ATTRIBUTE NAME AND ATTRIBUTE TYPE
            //open and read the specified file    
           // try {
                //move this up?
           //     BufferedReader bReader = new BufferedReader(new FileReader(new File(fileList.get(i))));
                //gets list of attribute names
           //     makeAttributes(bReader);
                //put attribute before, then name, then type, then \n

           // } catch (Exception e) {
           //     System.out.println("There was an issue parsing the file.");
           // }

       // }

    }

//final File folder = new File("/home/you/Desktop"); use File.separator
//listFilesForFolder(folder);
}
