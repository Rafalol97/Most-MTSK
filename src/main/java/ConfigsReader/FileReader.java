package ConfigsReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class FileReader {

    public static String getFileContent(String fileName) {
        ArrayList<String> fileContent = FileReader.getFileContentByLine(fileName);
        String text = "";
        for(String line : fileContent) text += line + "\n";
        return text;
    }

    private static ArrayList<String> getFileContentByLine(String fileName) {
        ArrayList<String> fileContent = new ArrayList<>();
        File file = new File(fileName);
        try{
            BufferedReader br = new BufferedReader(new java.io.FileReader(file));
            String line;
            while ((line = br.readLine()) != null){
                fileContent.add(line);
            }
            br.close();
            return fileContent;
        } catch (FileNotFoundException fnfException) {
            System.err.println("File "+fileName+" not found");
        } catch (IOException ioException) {
            System.err.println("Unable to read file"+ fileName);
        }
        return fileContent;
    }


}