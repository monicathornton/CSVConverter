/*
 * Soft Computing Project 1: Rollie Goodman, Janette Rounds, Monica Thornton
 *
 * A class to drive the conversion of a comma seperated value file (with either 
 * a .txt or a .csv extension) to an .arff file for use in WEKA. File locations
 * are specified here, so user can control which datasets to use.
 */
package csvconverter;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class CSVConverter {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    
    public static void main(String[] args) throws FileNotFoundException {
        //gets the os for the computer this program is run on
        String os = System.getProperty("os.name").toLowerCase();
        //gets the home location
        String home = System.getProperty("user.home");
        //starts building the file path
        String filePath = home;

        //uses file separator so is operating system agnostic
        if (os.startsWith("windows")) { //Windows
            filePath += File.separator;
        } else if (os.startsWith("mac")) { //Mac
            filePath += File.separator;
        } else {
            //everything else
            filePath += File.separator;
        }
        
        //calls the file chooser, returns the updated file path
        filePath = callFileChooser(filePath);
        
        //updates filepath with trailing separator
        filePath += File.separator;
        
        //calls the parser with the appropriate file path
        Parser parser = new Parser();
        parser.converter(filePath);
    }

    public static String callFileChooser(String filePath) {
        //builds a JFrame
        JFrame frame = new JFrame("Folder Selection Pane");
        //string to score the path
        String thisPath = "";
        
        //JFrame look and feel
        frame.setPreferredSize(new Dimension(400, 200));
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JButton button = new JButton("Select Folder");
        
        //sets up the file chooser
        JFileChooser fileChooser = new JFileChooser();
        //uses file path as a starting point for file browsing
        fileChooser.setCurrentDirectory(new File(filePath));
        //choose only from directories
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int fileChosen = fileChooser.showOpenDialog(null);

        //returns either the file path, or nothing (based on user choice)
        if (fileChosen == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            thisPath = selectedFile.getAbsolutePath();
            return thisPath;
        } else {
            return null;
        }
    }

}
