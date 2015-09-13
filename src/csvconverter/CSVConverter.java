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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        filePath = callFileChooser(filePath);
        System.out.println(filePath);

        //Parser parser = new Parser();
        //parser.converter(filePath);
    }

    public static String callFileChooser(String filePath) {
        JFrame frame = new JFrame("Folder Selection Pane");
        String thisPath = "";

        frame.setPreferredSize(new Dimension(400, 200));
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton button = new JButton("Select Folder");
        button.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(filePath));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String thisPath = selectedFile.getAbsolutePath();
                    
                } 
             }
        });

        frame.add(button);
        frame.pack();
        frame.setVisible(true);
        

        System.out.print("Your path" + thisPath);

        return thisPath;
    }
}
