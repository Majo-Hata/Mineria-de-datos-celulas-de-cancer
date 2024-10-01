
public class Individual{
    // TOPOLOGY HYPER-PARAMETERS
    private String numNeuronasString;
    private String numCapasOcultasString;
    private String numEpocasString;
    private String learningRateString;
    private String momentumString;

    // TOPOLOGY HYPER-PARAMETERS
    private int numNeuronasInt;
    private int numCapasOcultasInt;
    private int numEpocasInt;
    private double learningRateDouble;
    private double momentumDouble;

    // VALID RANGES OF THE TOPOLOGY OF THE MULTILAYER PERCEPTRON NEURAL NETWORK
    private int minNumNeuronas;
    private int maxNumNeuronas;
    private int minNumCapasOcultas;
    private int maxNumCapasOcultas;
    private int minNumEpocas;
    private int maxNumEpocas;
    private double minLearningRate;
    private double maxLearningRate;
    private double minMomentum;
    private double maxMomentum;

    // Número de bits que se usarán para cada parámetro
    // esto es útil para agregarle los bits mayores (0s) a los binaryStrings
    // ya que Integer.toBinraryString() no retorna 0s en su parte izquierda (no leading zeros)
    // Son strings que se proporcionarán al String.format()
    private String neuronaBits = "%5s";
    private String capasocultasBits = "%3s";
    private String epocasBits = "%9s";
    private String learningRateBits = "%12s";
    private String momentumBits = "%12s";

    private double accuracy;

    public Individual(){
        this.setDefaultRanges();
        this.numNeuronasString = new String();
        this.numCapasOcultasString = new String();
        this.numEpocasString = new String();
        this.learningRateString = new String();
        this.momentumString = new String();

    }

    public Individual(String numNeurons, String numHiddenLayers, String numEpochs, String learningRate, String momentum){
        this.setDefaultRanges();
        this.setTopologyBinaryString(numNeurons, numHiddenLayers, numEpochs, learningRate, momentum);
    }

    public Individual(int numNeurons, int numHiddenLayers, int numEpochs, double learningRate, double momentum){
        this.setDefaultRanges();
        this.setTopologyMLP(numNeurons, numHiddenLayers, numEpochs, learningRate, momentum);
    }

    public Individual(double accuracy){
        this.setDefaultRanges();
        this.accuracy = accuracy;
    }

    //Set de values as mlp request
    public void setTopologyMLP(int numNeurons, int numHiddenLayers, int numEpochs, double learningRate, double momentum){
        this.setnumNeuronasInt(numNeurons);
        this.setnumCapasOcultasInt(numHiddenLayers);
        this.setnumEpocasInt(numEpochs);
        this.setlearningRateDouble(learningRate);
        this.setMomentumDouble(momentum);
    }

    public void setTopologyBinaryString(String numNeurons, String numHiddenLayers, String numEpochs, 
            String learningRate, String momentum){
        this.setnumNeuronasString(numNeurons);
        this.setnumCapasOcultasString(numHiddenLayers);
        this.setnumEpocasString(numEpochs);
        this.setLearningRateString(learningRate);
        this.setMomentumString(momentum);
    }

    /***********START OF IMPORTANT SECTION**************/
    /**
     * validates whether the geneticSequence of this individual is within the
     * range of the previously specified range of the topology of the Neural Network.
     * The values used for this project are specified in setDefaultRanges()
     * @return boolean that indicates whether the geneticSequence is valid
     */
    public boolean validate(){
        if (!(numNeuronasInt>=minNumNeuronas && numNeuronasInt<=maxNumNeuronas)){
            return false;
        }
        if (!(numCapasOcultasInt>=minNumCapasOcultas && numCapasOcultasInt<=maxNumCapasOcultas)){
            return false;
        }

        if (!(numEpocasInt>=minNumEpocas && numEpocasInt<=maxNumEpocas)){
            return false;
        }

        if (!(learningRateDouble>=minLearningRate && learningRateDouble<=maxLearningRate)){
            return false;
        }

        if (!(momentumDouble>=minMomentum && momentumDouble<=maxMomentum)){
            return false;
        }

        return true;
    }

    public String getGeneticSequence(){
        return this.numNeuronasString + this.numCapasOcultasString + this.numEpocasString + this.learningRateString + 
            this.momentumString;
    }

    public String doubleToBinaryString(double number, String numBits){
        int numberInt = (int) number;
        return String.format(numBits, Integer.toBinaryString(numberInt)).replace(' ', '0');
    }

    /***********END OF IMPORTANT SECTION**************/

    private void setDefaultRanges(){
        this.minNumNeuronas = 8;
        this.maxNumNeuronas = 18;
        this.minNumCapasOcultas = 1;
        this.maxNumCapasOcultas = 3;
        this.minNumEpocas = 90;
        this.maxNumEpocas = 140;
        this.minLearningRate = 0.11;
        this.maxLearningRate = 0.17;
        this.minMomentum = 0.08;
        this.maxMomentum = 0.18;
        this.accuracy = 0.0;
    }

    public void setRanges(byte minNumNeuronas, byte maxNumNeuronas,
              byte minNumCapasOcultas, byte maxNumCapasOcultas,
              short minNumEpocas, short maxNumEpocas,
              double minLearningRate, double maxLearningRate,
              double minMomentum, double maxMomentum){
        this.minNumNeuronas = minNumNeuronas;
        this.maxNumNeuronas = maxNumNeuronas;
        this.minNumCapasOcultas = minNumCapasOcultas;
        this.maxNumCapasOcultas = maxNumCapasOcultas;
        this.minNumEpocas = minNumEpocas;
        this.maxNumEpocas = maxNumEpocas;
        this.minLearningRate = minLearningRate;
        this.maxLearningRate = maxLearningRate;
        this.minMomentum = minMomentum;
        this.maxMomentum = maxMomentum;
    }

    // Los BinaryStrings siempré estáran en el rango de (0, maxNumParametro - minNumParametro)
    // lo cual significa que a versiones numéricas decimales de los parámetros se le debe sumar
    // su respectivo delta, es decir minNumParametro.
    // Ej. de conversión: minNumNeuronas = 8, maxNumNeuronas = 18.
    // numNeuronasString solo puede estar dentro del rango (0, 10)
    // al hacer un setNeuronsString se le debe de sumar minNumNeuronas a la versión decimal del hiperparámetro
    // al hacer un setnumNeuronasInt se le debe de restar minNumNeuronas al binario. Esto es por lo siguiente:
    // SE ESPERA QUE LOS STRINGS DE INDIVIDUAL SIEMPRE SE PROPORCIONEN DENTRO DEL RANGO [0, maxNumParametro - minNumParametro]
    // SE ESPERA QUE LOS DECIMALES DE INDIVIDUAL SIEMPRE SE PROPORCIONEN DE TAL MANERA QUE WEKA LOS PUEDA INTERPRETAR 
    //      [minNumParametro, maxNumParametro)]

    public void setnumNeuronasString(String numNeurons){
        this.numNeuronasString = numNeurons;
        this.numNeuronasInt = Integer.parseInt(numNeurons, 2)+minNumNeuronas;
    }

    public void setnumCapasOcultasString(String numHiddenLayers){
        this.numCapasOcultasString = numHiddenLayers;
        this.numCapasOcultasInt = Integer.parseInt(numHiddenLayers, 2)+minNumCapasOcultas;
    }

    public void setnumEpocasString(String numEpochs){
        this.numEpocasString = numEpochs;
        this.numEpocasInt = Integer.parseInt(numEpochs, 2) + minNumEpocas;
    }

    public void setLearningRateString(String learningRate){
        this.learningRateString = learningRate;
        this.learningRateDouble = (Double.valueOf(Integer.parseInt(learningRate, 2))+(minLearningRate*100d))/100d;
    }

    public void setMomentumString(String momentum){
        this.momentumString = momentum;
        this.momentumDouble = (Double.valueOf(Integer.parseInt(momentum, 2))+(minMomentum*100d))/100d;
    }

    public void setnumNeuronasInt(int numNeurons){
        this.numNeuronasInt = numNeurons;
        this.numNeuronasString = this.doubleToBinaryString(numNeurons - minNumNeuronas, neuronaBits);
    }

    public void setnumCapasOcultasInt(int numHiddenLayers){
        this.numCapasOcultasInt = numHiddenLayers;
        this.numCapasOcultasString = this.doubleToBinaryString(numHiddenLayers - minNumCapasOcultas, capasocultasBits);
    }

    public void setnumEpocasInt(int numEpochs){
        this.numEpocasInt = numEpochs;
        this.numEpocasString = this.doubleToBinaryString(numEpochs - minNumEpocas, epocasBits);
    }

    public void setlearningRateDouble(double learningRate){
        this.learningRateDouble = learningRate;
        this.learningRateString = this.doubleToBinaryString(((learningRate*100)-(minLearningRate*100)), learningRateBits);
    }

    public void setMomentumDouble(double momentum){
        this.momentumDouble = momentum;
        this.momentumString = this.doubleToBinaryString(((momentum*100)-(minMomentum*100)), momentumBits);
    }

    public void setAccuracy(double accuracy){
        this.accuracy = accuracy;
    }

    public String getnumNeuronasString(){
        return numNeuronasString;
    }

    public String getnumCapasOcultasString(){
        return numCapasOcultasString;
    }

    public String getnumEpocasString(){
        return numEpocasString;
    }

    public String getLearningRateString(){
        return learningRateString;
    }

    public String getMomentumString(){
        return momentumString;
    }

    public double getnumNeuronasInt(){
        return numNeuronasInt;
    }

    public double getnumCapasOcultasInt(){
        return numCapasOcultasInt;
    }

    public int getnumEpocasInt(){
        return numEpocasInt;
    }

    public double getlearningRateDouble(){
        return learningRateDouble;
    }

    public double getMomentumDouble(){
        return momentumDouble;
    }

    public double getAccuracy(){
        return accuracy;
    }

    public String getnumCapasOcultasStringMLP(){
        String numHiddenLayersMLP = "";
        for (int i=0; i<this.numCapasOcultasInt; i++){
            numHiddenLayersMLP += Integer.toString(this.numNeuronasInt);
            if (i!=numCapasOcultasInt-1){
                numHiddenLayersMLP += ",";
            }
        }
        return numHiddenLayersMLP;
    }

    public double getMaxLearningRate(){
        return maxLearningRate;
    }

    public double getMaxMomentum(){
        return maxMomentum;
    }

    public double getMinLearningRate(){
        return minLearningRate;
    }

    public double getMinMomentum(){
        return minMomentum;
    }

    @Override
    public String toString(){
        return "Binary Strings: " + numNeuronasString + ", " + numCapasOcultasString + ", " + numEpocasString + ", " + 
            learningRateString + ", " + momentumString + ", \n" + "MLP values: " + numNeuronasInt + ", " + 
            numCapasOcultasInt + " ( " + getnumCapasOcultasStringMLP() + " ), " + numEpocasInt + ", " + 
            learningRateDouble + ", " + momentumDouble + ", ";
    }

}