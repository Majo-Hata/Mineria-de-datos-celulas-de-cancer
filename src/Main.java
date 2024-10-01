
public class Main {
    public static void main(String[] args) {
        try {
            GeneticAlgorithm genAlg = new GeneticAlgorithm();
            genAlg.generarPoblacion();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
