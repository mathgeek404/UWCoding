import java.util.*;

/**
 * You should implement your Perceptron in this class. 
 * Any methods, variables, or secondary classes could be added, but will 
 * only interact with the methods or variables in this framework.
 * 
 * You must add code for at least the 3 methods specified below. Because we
 * don't provide the weights of the Perceptron, you should create your own 
 * data structure to store the weights.
 * 
 */
public class Perceptron {

   /**
    * The initial value for ALL weights in the Perceptron.
    * We fix it to 0, and you CANNOT change it.
    */
   public final double INIT_WEIGHT = 0.0;

   /**
    * Learning rate value. You should use it in your implementation.
    * You can set the value via command line parameter.
    */
   public final double ALPHA;

   /**
    * Training iterations. You should use it in your implementation.
    * You can set the value via command line parameter.
    */
   public final int EPOCH;

   public final int numInUnits;	
   public final int numOutUnits;

   private double[] inputUnits;
   private double[] outputUnits;
   private Weights weights;
   // TODO: create weights variables, input units, and output units.

   /**
    * Constructor. You should initialize the Perceptron weights in this
    * method. Also, if necessary, you could do some operations on
    * your own variables or objects.
    * 
    * @param alpha
    * 		The value for initializing learning rate.
    * 
    * @param epoch
    * 		The value for initializing training iterations.
    * 
    * @param featureNum
    * 		This is the length of input feature vector. You might
    * 		use this value to create the input units.
    * 
    * @param labelNum
    * 		This is the size of label set. You might use this
    * 		value to create the output units.
    */
   public Perceptron(double alpha, int epoch, int featureNum, int labelNum) {
      this.ALPHA = alpha;
      this.EPOCH = epoch;
      this.numInUnits = featureNum+1;
      this.numOutUnits = labelNum;
      weights = new Weights(numInUnits,numOutUnits,INIT_WEIGHT,alpha);
      inputUnits = new double[numInUnits];
      outputUnits = new double[numOutUnits];
      inputUnits[0]=1; //The bias unit
   }

   /**
    * Train your Perceptron in this method.
    * 
    * @param trainingData
    */
   public void train(Dataset trainingData) {
      for (int i=0; i<EPOCH; i++) {		
         for (Instance train: trainingData.instanceList) {
            List<Double> features = train.getFeatureValue();
            for (int j=0; j<features.size(); j++) {
               inputUnits[j+1]=features.get(j);
            }
            //calculate the output unit value
            for (int k=0; k<outputUnits.length; k++) {
               double sum = 0;
               for (int l=0; l<inputUnits.length; l++)
                  sum+=weights.getWeight(l,k)*inputUnits[l];
               outputUnits[k]=sigmoid(sum);
            }
            weights.updateWeights(inputUnits, outputUnits, train);
         }
      }

   }

   /**
    * Test your Perceptron in this method. Refer to the homework documentation
    * for implementation details and requirement of this method.
    * 
    * @param testData
    */
   public void classify(Dataset testData) {
      int accuracy = 0;
      for (Instance test: testData.instanceList) {
         List<Double> features = test.getFeatureValue();
         for (int j=0; j<features.size(); j++) {
            inputUnits[j+1]=features.get(j);
         }
         double maxVal = 0.0;
         int maxIndex = 0;
         for (int k=0; k<outputUnits.length; k++) {
            double sum = 0.0;
            for (int l=0; l<inputUnits.length; l++) {
               sum+=weights.getWeight(l,k)*inputUnits[l];
            }
            outputUnits[k]=sigmoid(sum);
            if (outputUnits[k]>maxVal) {
               maxVal=outputUnits[k];
               maxIndex=k;
            }
         }
         System.out.println(maxIndex);
         if (maxIndex==Integer.parseInt(test.getLabel()))
            accuracy++;
      }
      
      System.out.printf("%.4f", ((double)accuracy)/((double)testData.instanceList.size()));
   }

   private double sigmoid(double x) {
      return 1/(1+Math.exp(-1*x));
   }

}


class Weights {
   double[][] weightMatrix;
   double alpha;

   public Weights(int inNum, int outNum, double init_weight, double a) {
      alpha = a;
      weightMatrix=new double[inNum][outNum];
      for (int i = 0;i<inNum;i++) {
         for (int j = 0;j<outNum;j++) {
            weightMatrix[i][j] = init_weight;
         }
      }
   }

   double getWeight(int inUnit, int outUnit) {
      return weightMatrix[inUnit][outUnit];
   }

   void updateWeights(double[] inUnits,double[] outUnits,Instance ins) {
      for (int j=0; j<outUnits.length; j++) {
         double expected = 0.0;
         if (Integer.parseInt(ins.getLabel())==j)   //same label as output unit
            expected=1.0;
         for (int i=0; i<inUnits.length; i++) {
            weightMatrix[i][j]+=alpha*outUnits[j]*inUnits[i]*(1 - outUnits[j])*(expected - outUnits[j]);
         }  
      }
   }

}
