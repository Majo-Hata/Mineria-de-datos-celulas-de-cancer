import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.classifiers.evaluation.Evaluation;
import java.util.Scanner;
import java.io.*;

public class GeneticAlgorithm{

    private Individual[] generation;
    private int n;
    private FileInputStream entrada;
    private int numGeneracion;
    private MultilayerPerceptron mlp;

    public GeneticAlgorithm(String file) throws FileNotFoundException{
        this.entrada = new FileInputStream(file);
        this.generation = new Individual[45];   //30 por generación + 15 hijos
        this.n = 15;                            //Numero de hiperparametros (Inicialmente 15)
        this.mlp = new MultilayerPerceptron();
        this.numGeneracion = 1;
        for(int i=0; i<generation.length; i++){
            generation[i] = new Individual(0, 0, 0, 0.0, 0.0);
        }
    }

    public GeneticAlgorithm() throws FileNotFoundException{
        this.entrada = new FileInputStream("population0.txt");
        this.generation = new Individual[45];   //30 por generación + 15 hijos
        this.n = 15;                            //Numero de hiperparametros (Inicialmente 15)
        this.mlp = new MultilayerPerceptron();
        this.numGeneracion = 1;
        for (int i=0; i<generation.length; i++){
            generation[i] = new Individual(0, 0, 0, 0.0, 0.0);
        }
    }

    public void generarPoblacion(){
        try{
            //Lee la primera población
            if (numGeneracion==1){
                System.out.println("Leyendo primer archivo");
                lectura();
            }
            Scanner input = new Scanner(System.in);
            boolean answ=false;
            //Proceso general de las generaciones
            do{
                selectPairs();
                evaluate();
                controlPopulation();
                System.out.println("Ordenando poblacion");
                quicksortGeneration(generation,0,this.n-1);
                numGeneracion++;
                creacionArchivo(numGeneracion);
                System.out.println("Continuar? \nSi [Ingresa 'True'] \nNo [Ingresa 'False']");
                answ = input.nextBoolean();
            }while (answ);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     *  Crea los archivos para almacenar las generaciones
     *  Solo almacena los 30 mejores individuos
     *  Guarda valores estadísticos de la población
     */
    public void creacionArchivo(int generacion) throws IOException{
        String nombreFile = "population" + String.valueOf(generacion) + ".txt";
        FileOutputStream salida = new FileOutputStream(nombreFile);
        BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(salida));
        buf.write("Neuronas  |  Capas  |  Epocas | Learning Rate | Momentum | Accuracy\n");
        for (int i = 0; i < this.n; i++){
            buf.write("    " + Double.toString(generation[i].getnumNeuronasInt()) + "    |    " 
                + Double.toString(generation[i].getnumCapasOcultasInt()) + "   |    " + 
                Integer.toString(generation[i].getnumEpocasInt()) + "  |      " + 
                Double.toString(generation[i].getlearningRateDouble()) + "      |     " + 
                Double.toString(generation[i].getMomentumDouble()) + "   | " + 
                Double.toString(generation[i].getAccuracy()) + "\n");
        }

        double a=0.0;
        double standardDeviation = 0.0;
        double med=0.0;
        for (int i=0; i<30; i++){
            a+=generation[i].getAccuracy();
        }
        med=a/30;

        for (int i=0; i<30; i++){
            standardDeviation  += Math.pow((generation[i].getAccuracy() - med), 2);
        }
        double raiz = standardDeviation / 30;
        double des = Math.sqrt(raiz);

        buf.write("\n");
        buf.write("\n");
        buf.write("\n");
        buf.write("\n");
        buf.write("Estadisticas de la generacion: \n");
        buf.write("Max= "+ generation[0].getAccuracy()+"\n");
        buf.write("Min= "+ generation[29].getAccuracy()+"\n");
        buf.write("Avg= "+ med +"\n");
        buf.write("Dev Std= " + des + "\n");
        buf.close();
        salida.close();
    }


    /**
     * Lee el archivo de la generación 0 (La generación inicial)
     * Almacena los hiperparámetros en el arreglo "generation"
     * generation[] es un arreglo de tipo "Individual"
     */
    public void lectura() throws IOException{
        BufferedReader buf = new BufferedReader(new InputStreamReader(entrada));
        String linea;
        String aa = buf.readLine();
        for (int i=0; i<n; i++){
            linea = buf.readLine();
            String linea1 = linea.replaceAll("\\|", " ");
            //El doble \\ es para que no tome en cuenta | como un meta character (Java ocupa eso para regex)
            String lineaCorrect = linea1.trim().replaceAll(" +", " ");
            //El " +" es para cuando hay mas de un espacio convertirlo a uno solo y el trim elimina espacios
            String[] temp = lineaCorrect.split(" ");
            generation[i] = new Individual(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), 
                Integer.parseInt(temp[2]), Double.parseDouble(temp[3])/100.0, Double.parseDouble(temp[4])/100.0);
        }
        entrada.close();
    }

    /**
     *  Crea nuevos "Individual" a partir de dos padres
     *  Toma los atributos de los padres y realiza una combinación entre ellos para
     *      generar nuevos atributos
     *  Verifica si los nuevos atributos se encuentran dentro del rango definido
     * */
    public void mate(Individual[][] couples,int i){
        Individual p = new Individual(); //auxiliar individual for assign it to the matrix
        int n = 0; //getting the middle of the hiperparam
        String temp; //temporal string for "bit exchange"

        //getting just one child per couple
        //each hiperParam gets an one-point crossover
        temp = "";
        //numNeurons
        n = couples[i][0].getnumNeuronasString().length();
        boolean shortStrin;
        temp = couples[i][0].getnumNeuronasString().substring(0, (n/2)+1);

        n = couples[i][1].getnumNeuronasString().length();
        if (n==1){
            temp.concat(Character.toString(couples[i][1].getnumNeuronasString().charAt(0)));
        }
        else{
            temp.concat(couples[i][1].getnumNeuronasString().substring((n/2)+1, n));
        }
        p.setnumNeuronasString(temp);

        //HiddenLayers
        n = couples[i][0].getnumCapasOcultasString().length();
        if (n==1){
            temp = (Character.toString(couples[i][0].getnumCapasOcultasString().charAt(0)));
        }
        else{
            temp = couples[i][0].getnumCapasOcultasString().substring(0, n/2);
        }
        n = couples[i][1].getnumCapasOcultasString().length();
        temp.concat(couples[i][1].getnumCapasOcultasString().substring((n/2), n));

        p.setnumCapasOcultasString(temp);

        //Epochs
        n = couples[i][0].getnumEpocasString().length();
        if (n<=2){
            temp = (Character.toString(couples[i][0].getnumEpocasString().charAt(0)));
        }
        else{
            temp = couples[i][0].getnumEpocasString().substring(0, (n/2)-1);
        }

        n = couples[i][1].getnumEpocasString().length();
        if (n==1){
            temp.concat(Character.toString(couples[i][1].getnumEpocasString().charAt(0)));
        }
        else{
            temp.concat(couples[i][1].getnumEpocasString().substring((n/2)-1, n));
        }
        p.setnumEpocasString(temp);
        //L.R.
        n = couples[i][0].getLearningRateString().length();
        if (n==1){
            temp = (Character.toString(couples[i][0].getLearningRateString().charAt(0)));
        }
        else{
            temp = couples[i][0].getLearningRateString().substring(0, n/2);
        }
        n = couples[i][1].getLearningRateString().length();
        temp += (couples[i][1].getLearningRateString().substring((n/2), n));
        if ((Double.valueOf(Integer.parseInt(temp,2))/100.0+couples[i][0].getMinLearningRate())
            >=couples[i][0].getMaxLearningRate()){
            temp = "110";
        }
        else{
            if ((Double.valueOf(Integer.parseInt(temp, 2))/100.0+couples[i][0].getMinLearningRate())
                <=couples[i][0].getMinLearningRate()){
                temp = "0";
            }
        }
        p.setLearningRateString(temp);

        //Momentum
        n = couples[i][0].getMomentumString().length();
        if (n==1){
            temp = (Character.toString(couples[i][0].getnumNeuronasString().charAt(0)));
        }
        else{
            temp = couples[i][0].getMomentumString().substring(0, (n / 2));
        }
        n = couples[i][1].getMomentumString().length();
        temp += (couples[i][1].getMomentumString().substring((n/2),n));
        if ((Double.valueOf(Integer.parseInt(temp,2))/100.0 + couples[i][0].getMinMomentum())>=couples[i][0].getMaxMomentum()){
            temp = "1010";
        }
        else{
            if ((Double.valueOf(Integer.parseInt(temp, 2))/100.0+couples[i][0].getMinMomentum())<=couples[i][0].getMinMomentum()){
                temp = "0";
            }
        }
        p.setMomentumString(temp);
        couples[i][2] = p; //Almacena al hijo
        System.out.println("\tHijo\n\t" + couples[i][2] + " Creado correctamente? " + couples[i][2].validate() + "\n");
        this.n++;
    }

    /**
     *  Realiza mutaciones a 2 hijos creados anteriormente
     *  La selección de hijos se realiza de forma aleatoria
     *  Debido a que las mutaciones son aleatorias, el método comprueba
     *      que los atributos estén dentro del rango permitido
     * */
    private void mutate(Individual[][] couples){
        Individual p = new Individual();
        int n = 0;
        String temp,aux, newString;
        int random_int, random_int2, descriptor;
        int max = 14; int min = 0, rep=-1;
        temp = "";
        aux = "";
        newString = "";

        //modificar un bit de 2 hijos aleatorios
        for (int i=0; i<2; i++){
            do{    //Controla si ya se aplicó mutación a ese hijo
                random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);//para la fila a mutar
            }while (rep==random_int);
            descriptor = (int)(Math.random()*5+1); //descriptor
            switch (descriptor){
                case 1://numNeurons
                    n = couples[random_int][2].getnumNeuronasString().length();
                    temp = couples[random_int][2].getnumNeuronasString();
                    random_int2 = (int) (Math.random() * n + 0);
                    if (temp.charAt(random_int2)=='1'){
                        newString = temp.substring(0, random_int2) + '0' + temp.substring(random_int2 + 1);
                    }
                    else{
                        newString = temp.substring(0, random_int2) + '1' + temp.substring(random_int2 + 1);
                    }
                    aux = couples[random_int][2].getnumNeuronasString();
                    couples[random_int][2].setnumNeuronasString(newString);
                    break;
                case 2://HiddenLayers
                    n = couples[random_int][2].getnumCapasOcultasString().length();
                    temp = couples[random_int][2].getnumCapasOcultasString();
                    random_int2 = (int) (Math.random() * n + 0);
                    if (temp.charAt(random_int2)=='1'){
                        newString = temp.substring(0, random_int2) + '0' + temp.substring(random_int2 + 1);
                    }
                    else{
                        newString = temp.substring(0, random_int2) + '1' + temp.substring(random_int2 + 1);
                    }
                    aux = couples[random_int][2].getnumCapasOcultasString();
                    couples[random_int][2].setnumCapasOcultasString(newString);
                    break;
                case 3://Epochs
                    n = couples[random_int][2].getnumEpocasString().length();
                    temp = couples[random_int][2].getnumEpocasString();
                    random_int2 = (int) (Math.random() * n + 0);
                    if (temp.charAt(random_int2)=='1'){
                        newString = temp.substring(0, random_int2) + '0' + temp.substring(random_int2 + 1);
                    }
                    else{
                        newString = temp.substring(0, random_int2) + '1' + temp.substring(random_int2 + 1);
                    }
                    aux = couples[random_int][2].getnumEpocasString();
                    couples[random_int][2].setnumEpocasString(newString);
                    break;
                case 4://L.R.
                    n = couples[random_int][2].getLearningRateString().length();
                    temp = couples[random_int][2].getLearningRateString();
                    random_int2 = (int)(Math.random()*n+0);
                    if (temp.charAt(random_int2)=='1'){
                        newString = temp.substring(0, random_int2) + '0' + temp.substring(random_int2 + 1);
                    }
                    else{
                        newString = temp.substring(0, random_int2) + '1' + temp.substring(random_int2 + 1);
                    }
                    aux = couples[random_int][2].getLearningRateString();
                    couples[random_int][2].setLearningRateString(newString);
                    break;
                case 5://Momentum
                    n = couples[random_int][2].getMomentumString().length();
                    temp = couples[random_int][2].getMomentumString();
                    random_int2 = (int)(Math.random()*n+0);
                    if (temp.charAt(random_int2)=='1'){
                        newString = temp.substring(0, random_int2) + '0' + temp.substring(random_int2 + 1);
                    }
                    else{
                        newString = temp.substring(0, random_int2) + '1' + temp.substring(random_int2 + 1);
                    }
                    aux = couples[random_int][2].getMomentumString();
                    couples[random_int][2].setMomentumString(newString);
                    break;
                default:
                    System.out.println("ERROR");
                    break;
            }
            //Reasigna el atributo original si la mutación no ocurrió correctamente
            if (!couples[random_int][2].validate()){
                switch (descriptor){
                    case 1:
                        couples[random_int][2].setnumNeuronasString(aux);
                        break;
                    case 2:
                        couples[random_int][2].setnumCapasOcultasString(aux);
                        break;
                    case 3:
                        couples[random_int][2].setnumEpocasString(aux);
                        break;
                    case 4:
                        couples[random_int][2].setLearningRateString(aux);
                        break;
                    case 5:
                        couples[random_int][2].setMomentumString(aux);
                        break;
                }
                i--;
            }
            else{
                rep = random_int;
            }
            temp = "";
            aux = "";
            newString = "";
        }
    }

    /**
     *  Selecciona 15 parejas de individuos al azar para posteriormente
     *      generar hijos mediante el método "mate(couples,i)"
     *  Solo los mejores 15 individuos pueden tener pareja
     *  Un individuo no puede formar pareja consigo mismo
     *  Una vez generados los hijos, se llama al método "mutate(couples)"
     *  Agrega los hijos al arreglo "generation" de la clase
     *  Por último llama al método "quicksortGeneration(generation,0,k+couples.length)"
     *      la cual ordena el arreglo
     * */
    public void selectPairs(){//select the couples
        //[parent1][parent2][child]
        Individual[][] couples = new Individual[15][3]; //15 rows and  3 cols
        for (int i =0; i<15; i++){
            for (int j=0; j<3; j++){
                couples[i][j] = new Individual();
            }
        }
        int max = 14; int min = 0; int k=0; //15 parejas
        if (n<30){
            max=14;
        }
        else{
            max=29;
        }
        int random_int;
        //Asigna parejas
        System.out.println(couples.length);
        for (int i=0; i<couples.length; i++){//15 couples created
            for (int j=0; j<2; j++){
                random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
                if (j==1 && generation[random_int]==couples[i][0]){   //Comprueba si no se trata del mismo individuo
                    j--;
                }
                else{
                    if (generation[random_int].getnumNeuronasInt()!=0){
                        couples[i][j] = generation[random_int];
                    }
                    else{
                        j--;
                    }
                }
            }
        }
        for (int i=0; i<couples.length; i++){//creating here the for loop, for an expected output :p
            System.out.println("Pareja\n" + "\t"+couples[i][0] + "\n\t"+couples[i][1]);
            mate(couples,i);
        }

        //Obtiene la ubicación donde están almacenados los hijos
        Individual p;//aux
        k = 0;
        while(k<generation.length){
            p = generation[k];
            if(p.getlearningRateDouble()==0.0 && p.getMomentumDouble()==0.0){
                break;
            }
            k++;
        }
        k--;

        //Asigna los hijos al arreglo principal
        int j=0;
        for(int i=k; i<k+couples.length; i++){
            generation[i] = couples[j][2];
            j++;
        }

        //Crea mutaciones a 2 hijos
        mutate(couples);
    }


    public void evaluate(){
        System.out.println("Evaluando poblacion");
        try{
            Instances train;
            Evaluation eval;
            FileReader file1,file2;
            for (int i=0; i<n; i++){
                if(generation[i].getAccuracy()==0.0){
                    file1 = new FileReader("fold2.arff");
                    train = new Instances(file1);
                    train.setClassIndex(train.numAttributes() - 1);
                    mlp.setLearningRate(generation[i].getlearningRateDouble());
                    mlp.setMomentum(generation[i].getMomentumDouble());
                    mlp.setTrainingTime(generation[i].getnumEpocasInt());
                    mlp.setHiddenLayers(generation[i].getnumCapasOcultasString());
                    mlp.setValidationSetSize(1);
                    mlp.buildClassifier(train);
                    file1.close();
                    file2 = new FileReader("fold1.arff");
                    train = new Instances(file2);
                    train.setClassIndex(train.numAttributes() - 1);
                    eval = new Evaluation(train);
                    eval.evaluateModel(mlp, train);
                    System.out.println("-" + (i + 1) + " " + eval.pctCorrect());
                    generation[i].setAccuracy(eval.pctCorrect());
                    file2.close();
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     *  Ordena los individuos de acuerdo con su accuracy utilizando
     *      el ordenamiento quicksort
     * */
    public static void quicksortGeneration(Individual[] A, int izq, int der){
        Individual aux,piv = A[izq];
        int i=izq,j=der;
        while (i<j){
            while (A[i].getAccuracy() >= piv.getAccuracy() && i < j) {
                i++;
            }
            while (A[j].getAccuracy() < piv.getAccuracy()) {
                j--;
            }
            if (i < j){
                aux = A[i];
                A[i] = A[j];
                A[j] = aux;
            }
        }
        A[izq] = A[j];
        A[j] = piv;
        if (izq<(j-1)){
            quicksortGeneration(A,izq,j-1);
        }
        if ((j+1)<der){
            quicksortGeneration(A,j+1,der);
        }
    }

    /**
     *  Elimina los 15 peores individuos asignando un objeto vacío
     **/
    public void controlPopulation(){
        System.out.println("Controlando poblacion");
        for (int i=this.n-1; i>=30; i--){
            this.generation[i] = new Individual();
            this.n--;
        }
    }
}
