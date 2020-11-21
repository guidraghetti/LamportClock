import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadConfigFile {
    private int nOfProcces;
    private ArrayList<Process> listOfProcess = new ArrayList<Process>();

    public ReadConfigFile() {
    }

    public void readConfigAddToList() {
        try {
            File myObj = new File("config.txt");
            Scanner myReader = new Scanner(myObj);  
            while (myReader.hasNextLine()) {
                String [] pArray = myReader.nextLine().split((" "));
                Process process = new Process(Integer.parseInt(pArray[0]),pArray[1],Integer.parseInt(pArray[2]), Double.parseDouble(pArray[3]));
                listOfProcess.add(process);
            }
            this.nOfProcces = listOfProcess.size();
            myReader.close();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          } 

    }

    public int getNOfProcess() {
        return this.nOfProcces;
    }


    public ArrayList<Process> getListOfProcess() {
        return this.listOfProcess;
    }

    public Process getIndexListOfProcess (int position) {
        return this.listOfProcess.get(position);
    }
}