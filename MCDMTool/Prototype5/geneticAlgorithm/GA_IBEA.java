//  IBEA.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package geneticAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jmetal.core.*;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.FitnessComparator;
import jmetal.qualityIndicator.Epsilon;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.util.*;

/**
 * This class implementing the IBEA algorithm
 */
public class GA_IBEA extends Algorithm{

	//test//for printing progression
	public static int extent;
	public static int scenario;
	//test//for printing progression
	
	
	public static boolean eliminateDuplicates=false; //if this is true, duplicate solutions will not be generated
	public static boolean eliminateDuplicatesWithinPop=true;//determine whether to eliminate duplicate solutions in one population
	public static HashMap<String, Object> allSolutions;//for all evaluated solutions
	public static HashMap<String, Object> currentPop;//for all evaluated solutions in one population
	
	
  /**
   * Defines the number of tournaments for creating the mating pool
   */
  public static final int TOURNAMENTS_ROUNDS = 1;

  /**
   * Stores the value of the indicator between each pair of solutions into
   * the solution set
   */
  private List<List<Double>> indicatorValues_;

  /**
   *
   */
  private double maxIndicatorValue_;
  /**
  * Constructor.
  * Create a new IBEA instance
  * @param problem Problem to solve
  */
  public GA_IBEA(Problem problem) {
    super (problem) ;
  } // Spea2

  /**
   * calculates the hypervolume of that portion of the objective space that
   * is dominated by individual a but not by individual b
   */
  double calcHypervolumeIndicator(Solution p_ind_a,
                                  Solution p_ind_b,
                                  int d,
                                  double maximumValues [],
                                  double minimumValues []) {
    double a, b, r, max;
    double volume = 0;
    double rho = 2.0;

    r = rho * (maximumValues[d-1] - minimumValues[d-1]);
    max = minimumValues[d-1] + r;


    a = p_ind_a.getObjective(d-1);
    if (p_ind_b == null)
      b = max;
    else
      b = p_ind_b.getObjective(d-1);

    if (d == 1)
    {
      if (a < b)
        volume = (b - a) / r;
      else
        volume = 0;
    }
    else
    {
      if (a < b)
      {
         volume = calcHypervolumeIndicator(p_ind_a, null, d - 1, maximumValues, minimumValues) *
         (b - a) / r;
         volume += calcHypervolumeIndicator(p_ind_a, p_ind_b, d - 1, maximumValues, minimumValues) *
         (max - b) / r;
      }
      else
      {
         volume = calcHypervolumeIndicator(p_ind_a, p_ind_b, d - 1, maximumValues, minimumValues) *
         (max - b) / r;
      }
    }

    return (volume);
  }



    /**
   * This structure store the indicator values of each pair of elements
   */
  public void computeIndicatorValuesHD(SolutionSet solutionSet,
                                     double [] maximumValues,
                                     double [] minimumValues) {
    SolutionSet A, B;
    // Initialize the structures
    indicatorValues_ = new ArrayList<List<Double>>();
    maxIndicatorValue_ = - Double.MAX_VALUE;

    for (int j = 0; j < solutionSet.size(); j++) {
      A = new SolutionSet(1);
      A.add(solutionSet.get(j));

      List<Double> aux = new ArrayList<Double>();
      for (int i = 0; i < solutionSet.size(); i++) {
        B = new SolutionSet(1);
        B.add(solutionSet.get(i));

        int flag = (new DominanceComparator()).compare(A.get(0), B.get(0));

        double value = 0.0;
        if (flag == -1) {
            value = - calcHypervolumeIndicator(A.get(0), B.get(0), problem_.getNumberOfObjectives(), maximumValues, minimumValues);
        } else {
            value = calcHypervolumeIndicator(B.get(0), A.get(0), problem_.getNumberOfObjectives(), maximumValues, minimumValues);
        }
        //double value = epsilon.epsilon(matrixA,matrixB,problem_.getNumberOfObjectives());

        
        //Update the max value of the indicator
        if (Math.abs(value) > maxIndicatorValue_)
          maxIndicatorValue_ = Math.abs(value);
        aux.add(value);
     }
     indicatorValues_.add(aux);
   }
  } // computeIndicatorValues



  /**
   * Calculate the fitness for the individual at position pos
   */
  public void fitness(SolutionSet solutionSet,int pos) {
      double fitness = 0.0;
      double kappa   = 0.05;
    
      for (int i = 0; i < solutionSet.size(); i++) {
        if (i!=pos) {
           fitness += Math.exp((-1 * indicatorValues_.get(i).get(pos)/maxIndicatorValue_) / kappa);
        }
      }
      solutionSet.get(pos).setFitness(fitness);
  }


  /**
   * Calculate the fitness for the entire population.
  **/
  public void calculateFitness(SolutionSet solutionSet) {
    // Obtains the lower and upper bounds of the population
    double [] maximumValues = new double[problem_.getNumberOfObjectives()];
    double [] minimumValues = new double[problem_.getNumberOfObjectives()];

    for (int i = 0; i < problem_.getNumberOfObjectives();i++) {
        maximumValues[i] = - Double.MAX_VALUE; // i.e., the minus maxium value
        minimumValues[i] =   Double.MAX_VALUE; // i.e., the maximum value
    }

    for (int pos = 0; pos < solutionSet.size(); pos++) {
        for (int obj = 0; obj < problem_.getNumberOfObjectives(); obj++) {
          double value = solutionSet.get(pos).getObjective(obj);
          if (value > maximumValues[obj])
              maximumValues[obj] = value;
          if (value < minimumValues[obj])
              minimumValues[obj] = value;
        }
    }

    computeIndicatorValuesHD(solutionSet,maximumValues,minimumValues);
    for (int pos =0; pos < solutionSet.size(); pos++) {
        fitness(solutionSet,pos);
    }
  }



  /** 
   * Update the fitness before removing an individual
 * @throws JMException 
   */
  public void removeWorst(SolutionSet solutionSet) throws JMException {
   
    // Find the worst;
    double worst      = solutionSet.get(0).getFitness();
    int    worstIndex = 0;
    double kappa = 0.05;
     
    for (int i = 1; i < solutionSet.size(); i++) {
      if (solutionSet.get(i).getFitness() > worst) {
        worst = solutionSet.get(i).getFitness();
        worstIndex = i;
      }
    }

    //if (worstIndex == -1) {
    //    System.out.println("Yes " + worst);
    //}
    //System.out.println("Solution Size "+solutionSet.size());
    //System.out.println(worstIndex);

    // Update the population
    for (int i = 0; i < solutionSet.size(); i++) {
      if (i!=worstIndex) {
          double fitness = solutionSet.get(i).getFitness();
          fitness -= Math.exp((- indicatorValues_.get(worstIndex).get(i)/maxIndicatorValue_) / kappa);
          solutionSet.get(i).setFitness(fitness);
      }
    }

    // remove worst from the indicatorValues list
    indicatorValues_.remove(worstIndex); // Remove its own list
    Iterator<List<Double>> it = indicatorValues_.iterator();
    while (it.hasNext())
        it.next().remove(worstIndex);

    //remove the worst individual from the currentPop
    currentPop.remove(SolutionToString(solutionSet.get(worstIndex)));
    
    // remove the worst individual from the population
    solutionSet.remove(worstIndex);
    

    
    
  } // removeWorst


  /**
  * Runs of the IBEA algorithm.
  * @return a <code>SolutionSet</code> that is a set of non dominated solutions
  * as a result of the algorithm execution
  * @throws JMException
  */
  public SolutionSet execute() throws JMException, ClassNotFoundException{
    int populationSize, archiveSize, maxEvaluations, evaluations;
    Operator crossoverOperator, mutationOperator, selectionOperator;
    SolutionSet solutionSet, archive, offSpringSolutionSet;

  //initialise the hashMap of solutions
  allSolutions=new HashMap<String, Object>();
   	currentPop=new HashMap<String, Object>();
    
    //Read the params
    populationSize = ((Integer)getInputParameter("populationSize")).intValue();
    archiveSize    = ((Integer)getInputParameter("archiveSize")).intValue();
    maxEvaluations = ((Integer)getInputParameter("maxEvaluations")).intValue();

    //Read the operators
    crossoverOperator = operators_.get("crossover");
    mutationOperator  = operators_.get("mutation");
    selectionOperator = operators_.get("selection");

    //Initialize the variables
    solutionSet  = new SolutionSet(populationSize);
    archive     = new SolutionSet(archiveSize);
    evaluations = 0;

    //-> Create the initial solutionSet
    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
    	do{
    		newSolution = new Solution(problem_);
    		problem_.evaluateConstraints(newSolution);
    		problem_.evaluate(newSolution);
    		//by Wenhao
    	}while(allSolutions.containsKey(SolutionToString(newSolution)));
    	allSolutions.put(SolutionToString(newSolution), newSolution);
    	currentPop.put(SolutionToString(newSolution), newSolution);
    	
    	evaluations++;
    	solutionSet.add(newSolution);
    }
    
    //test//this code print out current population
    int generation=0;
    //test//end
    
    generation++;
    while (evaluations < maxEvaluations){
      SolutionSet union = ((SolutionSet)solutionSet).union(archive);
      calculateFitness(union);
      archive = union;
      
      while (archive.size() > populationSize) {
        removeWorst(archive);
      }
      
      
      //test//this code print out current population
      Ranking currentRank=new Ranking(archive);//rank the solutionSet
      archive.printFitnessToFile("CMOvsInHouseE_"+extent+"_S_"+scenario+"_G_"+generation);
      generation++;
      //test//end
      
      // Create a new offspringPopulation
      offSpringSolutionSet= new SolutionSet(populationSize);
      Solution  [] parents = new Solution[2];
      while (offSpringSolutionSet.size() < populationSize){
        int j = 0;
        do{
          j++;
          parents[0] = (Solution)selectionOperator.execute(archive);
        } while (j < GA_IBEA.TOURNAMENTS_ROUNDS); // do-while
        int k = 0;
        do{
          k++;
          parents[1] = (Solution)selectionOperator.execute(archive);
        } while (k < GA_IBEA.TOURNAMENTS_ROUNDS); // do-while

        Solution [] offSpring;
        if(eliminateDuplicates){
        	do{
		        //make the crossover
		        offSpring = (Solution [])crossoverOperator.execute(parents);
		        mutationOperator.execute(offSpring[0]);
		        problem_.evaluateConstraints(offSpring[0]);
		        problem_.evaluate(offSpring[0]);
        	}while(allSolutions.containsKey(SolutionToString(offSpring[0])));
        	allSolutions.put(SolutionToString(offSpring[0]), offSpring[0]);
		    offSpringSolutionSet.add(offSpring[0]);
		    evaluations++;
        }//if
        else{
        	if(eliminateDuplicatesWithinPop){
        		do{
        	        offSpring = (Solution [])crossoverOperator.execute(parents);
        	        mutationOperator.execute(offSpring[0]);
        	        problem_.evaluateConstraints(offSpring[0]);
        	        problem_.evaluate(offSpring[0]);
        	        
        	        if(allSolutions.containsKey(SolutionToString(offSpring[0])))
          			  MergeDuplicates(offSpring[0]);
          		  	else
          			  allSolutions.put(SolutionToString(offSpring[0]), offSpring[0]);
        		}while(currentPop.containsKey(SolutionToString(offSpring[0])));
        		currentPop.put(SolutionToString(offSpring[0]), offSpring[0]);
        	}
        	else{
		        offSpring = (Solution [])crossoverOperator.execute(parents);
		        mutationOperator.execute(offSpring[0]);
		        problem_.evaluateConstraints(offSpring[0]);
		        problem_.evaluate(offSpring[0]);
		        
		        if(allSolutions.containsKey(SolutionToString(offSpring[0])))
	  			  MergeDuplicates(offSpring[0]);
	  		  	else
	  			  allSolutions.put(SolutionToString(offSpring[0]), offSpring[0]);
        	}
	        offSpringSolutionSet.add(offSpring[0]);
		    evaluations++;
        }//else
      } // while
      // End Create a offSpring solutionSet
      solutionSet = offSpringSolutionSet;
    } // while

    Ranking ranking = new Ranking(archive);
    return ranking.getSubfront(0);
  } // execute
  
	public static String SolutionToString(Solution solution) throws JMException{
		Variable[] variables=solution.getDecisionVariables();
		int[] dv=new int[variables.length];
		for(int i=0;i<variables.length;i++){
			dv[i]=(int)variables[i].getValue();
		}
		return Arrays.toString(dv);
	}
	
	public static void MergeDuplicates(Solution solution) throws JMException{//combine already existed solution with newly evaluated solution
		//requires to check if allSolutions contains the existing one
		//if containsKey, then:
		Solution existingSolution=(Solution)allSolutions.get(SolutionToString(solution));
		double[] combinedObjective=new double[solution.numberOfObjectives()];
		for(int i=0;i<solution.numberOfObjectives();i++){
			combinedObjective[i]=(existingSolution.getObjective(i)+solution.getObjective(i))/2;//calculate the average of the 2
			existingSolution.setObjective(i, combinedObjective[i]);//give the combined objective existing solution
			solution.setObjective(i, combinedObjective[i]);//give the combined objective to new solution
		}
	}
  
} // Spea2
