import java.io.*;

public class FileHandler {
    private String fileName;
    private FileReader fileR;
    private FileWriter fileW;

    public FileHandler(String fileName){
        this.fileName = fileName;
        try{
        }catch(Exception ex){
            System.out.println(ex);
        }
    }

    public FileReader getFileR(){
        return fileR;
    }
    
    public FileWriter getFileW(){
        return fileW;
    }

    public void closeFileR(){
        try{fileR.close();}catch(Exception ex){System.out.println(ex);}
    }

    public String getLine(int index){
        return "cadena";
    }

}
