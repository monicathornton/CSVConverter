/*
 * Soft Computing Project 1: Rollie Goodman, Janette Rounds, Monica Thornton
 * A class to convert a comma seperated value file (with either a .txt
 * .csv extension) to an .arff file for use in WEKA.  
 */

package csvconverter;

import java.io.FileNotFoundException;

public class CSVConverter {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here
        
        //String os = System.getProperty("os.name");
        String home = System.getProperty("user.home");
        String filePath = home;
        
//        if(os.equalsIgnoreCase("")){ //Windows
//            filePath += "\\ArffThisFolder";
//        }else if(os.equalsIgnoreCase("")){ //Mac
//            filePath += "";
//        }else{//everything else
//            filePath += "";
//        }
        
        filePath += "\\ArffThisFolder\\sonar.all-data.txt";
        
        Parser p = new Parser();
        p.makeArffFiles(filePath);
        

        
    }
    
}