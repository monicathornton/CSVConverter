/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvconverter;

//changed from file
import java.io.*;
import java.util.ArrayList;
//for reading in files

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

    public void detectFileTypes() {

    }

    public void makeArffFiles(String a) {

    }

    public String[] detectAttributeTypes(String[] b) {

        return b;
    }

    public void makeAttributes(String[] attTypes, String[] attNames) {

    }

    public void addDataToArff(String file) {

    }

    public void converter() {
        for (int i = 0; i < fileList.size(); i++) {
            makeArffFiles(fileList.get(i));
            
            //TO DO: CHECK FOR BLANK LINE BETWEEN ATTRIBUTE NAME AND ATTRIBUTE TYPE
            //to DO: MOVE TO FUNCTIONS
            //TO DO: IGNORE ANY COMMAS IN ""
            
            //open and read the specified file    
            try {
                BufferedReader bReader = new BufferedReader(new FileReader(new File(fileList.get(i))));

                String attributeNames, attributeTypes;

                //read through the first two lines to get attribute names and types
                attributeNames = bReader.readLine();
                attributeTypes = bReader.readLine();

            //reads through the string storing attribute names until it reaches the end
                while ((attributeNames = bReader.readLine()) != null) {
                    //check for comma delimiting
                    String delims = "[,]";
                    String[] attributeNameArray = attributeNames.split(delims);
                }

                //reads through the string storing attribute types until it reaches the end
                while ((attributeTypes = bReader.readLine()) != null) {
                    String delims = "[,]";
                    String[] attributeNameArray = attributeNames.split(delims);
                }
                
                bReader.close();
            } catch (Exception e) {
                System.out.println("There was an issue parsing the file.");
            }

        }

    }
}
//final File folder = new File("/home/you/Desktop"); use File.separator
//listFilesForFolder(folder);
}
