package geneticAlgorithm;

import java.util.HashMap;

import mcdmProblem.BatchManufacturingModel;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

public class GA_main {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws JMException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, JMException {
		
	    Problem   problem   ;         // The problem to solve
	    Algorithm algorithm ;         // The algorithm to use
	    Operator  crossover ;         // Crossover operator
	    Operator  mutation  ;         // Mutation operator
	    Operator  selection ;         // Selection operator
	    
	    HashMap  parameters ; // Operator parameters
	    
	    problem		=new BatchManufacturingModel("Integer", 23);
	    
	    algorithm	=new NSGAII(problem);
	    
	    algorithm.setInputParameter("populationSize", 200);
	    algorithm.setInputParameter("maxEvaluations", 200);
	    
	    parameters = new HashMap() ;
	    parameters.put("probability", 0.9) ;
	    parameters.put("distributionIndex", 20.0) ;
	    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);                   

	    parameters = new HashMap() ;
	    parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
	    parameters.put("distributionIndex", 20.0) ;
	    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);                    
	    
	    // Selection Operator 
	    parameters = null ;
	    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;                           

	    // Add the operators to the algorithm
	    algorithm.addOperator("crossover",crossover);
	    algorithm.addOperator("mutation",mutation);
	    algorithm.addOperator("selection",selection);
	    
	    // Execute the Algorithm
	    long initTime = System.currentTimeMillis();
	    SolutionSet population = algorithm.execute();
	    long estimatedTime = System.currentTimeMillis() - initTime;
	    
	    System.out.println("The estimatedTime is "+estimatedTime);
	    
	    //population.printToFile("P20_MC1000_MAX100");
	    population.printRunningAverage("RA");
	}

}
