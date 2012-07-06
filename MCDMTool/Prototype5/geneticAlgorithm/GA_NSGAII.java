package geneticAlgorithm;

import java.util.Arrays;
import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparators.CrowdingComparator;

public class GA_NSGAII extends Algorithm {
	//test//for printing progression
	public static int extent;
	public static int scenario;
	//test//for printing progression
	
	public static boolean eliminateDuplicates=false; //if this is true, duplicate solutions will not be generated
	public static HashMap<String, Object> allSolutions;
	
	public GA_NSGAII(Problem problem) {
		super(problem);
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
	    //initialise the hashMap of solutions
		allSolutions=new HashMap<String, Object>();
		
		
		int populationSize;
	    int maxEvaluations;
	    int evaluations;

	    QualityIndicator indicators; // QualityIndicator object
	    int requiredEvaluations; // Use in the example of use of the
	                                // indicators object (see below)

	    SolutionSet population;
	    SolutionSet offspringPopulation;
	    SolutionSet union;

	    Operator mutationOperator;
	    Operator crossoverOperator;
	    Operator selectionOperator;

	    Distance distance = new Distance();

	    //Read the parameters
	    populationSize = ((Integer) getInputParameter("populationSize")).intValue();
	    maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
	    indicators = (QualityIndicator) getInputParameter("indicators");

	    //Initialize the variables
	    population = new SolutionSet(populationSize);
	    evaluations = 0;

	    requiredEvaluations = 0;

	    //Read the operators
	    mutationOperator = operators_.get("mutation");
	    crossoverOperator = operators_.get("crossover");
	    selectionOperator = operators_.get("selection");

	    // Create the initial solutionSet
	    Solution newSolution;
	    for (int i = 0; i < populationSize; i++) {
	    	do{
	    		newSolution = new Solution(problem_);
	    		problem_.evaluateConstraints(newSolution);
	    		problem_.evaluate(newSolution);
	    		//by Wenhao
	    	}while(allSolutions.containsKey(SolutionToString(newSolution)));
	    	allSolutions.put(SolutionToString(newSolution), newSolution);
	      
	    	evaluations++;
	    	population.add(newSolution);
	    } //for       
	    
    	//test//this code print out current population
    	int generation=0;
    	population.printSuccessToFile("CMOvsInHouseE_"+extent+"_S_"+scenario+"_G_"+generation);
    	//test//end
	    
	    generation++;
	    // Generations 
	    while (evaluations < maxEvaluations) {

	      // Create the offSpring solutionSet      
	      offspringPopulation = new SolutionSet(populationSize);
	      Solution[] parents = new Solution[2];
	      
	      for (int i = 0; i < (populationSize / 2); i++) {
	        if (evaluations < maxEvaluations) {
	          //obtain parents
	        	
	          parents[0] = (Solution) selectionOperator.execute(population);
	          parents[1] = (Solution) selectionOperator.execute(population);
	          Solution[] offSpring;
	          if(eliminateDuplicates){//if the duplicates are eliminated
	        	  do{ 
	        		  offSpring = (Solution[]) crossoverOperator.execute(parents);
	        		  mutationOperator.execute(offSpring[0]);
	        		  mutationOperator.execute(offSpring[1]);

	         
	        		  problem_.evaluateConstraints(offSpring[0]);
	        		  problem_.evaluate(offSpring[0]);
	        		  problem_.evaluateConstraints(offSpring[1]);
	        		  problem_.evaluate(offSpring[1]);
	        	  
	        	  }while(allSolutions.containsKey(SolutionToString(offSpring[0])) || allSolutions.containsKey(SolutionToString(offSpring[1])));
	        	  allSolutions.put(SolutionToString(offSpring[0]), offSpring[0]);
	        	  allSolutions.put(SolutionToString(offSpring[1]), offSpring[1]);
	          
	        	  offspringPopulation.add(offSpring[0]);
	        	  offspringPopulation.add(offSpring[1]);
	        	  evaluations += 2;
	          } // if
	          else{//if duplicate solutions are not to be eliminated
        		  offSpring = (Solution[]) crossoverOperator.execute(parents);
        		  mutationOperator.execute(offSpring[0]);
        		  mutationOperator.execute(offSpring[1]);
        		  
         
        		  problem_.evaluateConstraints(offSpring[0]);
        		  problem_.evaluate(offSpring[0]);
        		  problem_.evaluateConstraints(offSpring[1]);
        		  problem_.evaluate(offSpring[1]);
        		  
        		  if(allSolutions.containsKey(SolutionToString(offSpring[0])))
        			  MergeDuplicates(offSpring[0]);
        		  else
        			  allSolutions.put(SolutionToString(offSpring[0]), offSpring[0]);
        		  
         		  if(allSolutions.containsKey(SolutionToString(offSpring[1])))
        			  MergeDuplicates(offSpring[1]);
        		  else
        			  allSolutions.put(SolutionToString(offSpring[1]), offSpring[1]);
        		  
	        	  offspringPopulation.add(offSpring[0]);
	        	  offspringPopulation.add(offSpring[1]);
	        	  evaluations += 2;
	          }
	        }
	      } // for

	      // Create the solutionSet union of solutionSet and offSpring
	      union = ((SolutionSet) population).union(offspringPopulation);

	      // Ranking the union
	      Ranking ranking = new Ranking(union);

	      int remain = populationSize;
	      int index = 0;
	      SolutionSet front = null;
	      population.clear();

	      // Obtain the next front
	      front = ranking.getSubfront(index);
	      
	      while ((remain > 0) && (remain >= front.size())) {
	        //Assign crowding distance to individuals
	        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
	        //Add the individuals of this front
	        for (int k = 0; k < front.size(); k++) {
	          population.add(front.get(k));
	        } // for

	        //Decrement remain
	        remain = remain - front.size();

	        //Obtain the next front
	        index++;
	        if (remain > 0) {
	          front = ranking.getSubfront(index);
	        } // if        
	      } // while

	      // Remain is less than front(index).size, insert only the best one
	      if (remain > 0) {  // front contains individuals to insert                        
	        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
	        front.sort(new CrowdingComparator());
	        for (int k = 0; k < remain; k++) {
	          population.add(front.get(k));
	        } // for

	        remain = 0;
	      } // if                               

	      // This piece of code shows how to use the indicator object into the code
	      // of NSGA-II. In particular, it finds the number of evaluations required
	      // by the algorithm to obtain a Pareto front with a hypervolume higher
	      // than the hypervolume of the true Pareto front.
	      if ((indicators != null) &&
	        (requiredEvaluations == 0)) {
	        double HV = indicators.getHypervolume(population);
	        if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
	          requiredEvaluations = evaluations;
	        } // if
	      } // if
	      
	      //test//this code print out current population
	      population.printSuccessToFile("CMOvsInHouseE_"+extent+"_S_"+scenario+"_G_"+generation);
	      generation=generation+1;
	      //test//end
	      
	      
	      
	    } // while

	    // Return as output parameter the required evaluations
	    setOutputParameter("evaluations", requiredEvaluations);

	    // Return the first non-dominated front
	    Ranking ranking = new Ranking(population);
	    
	   
	    
	    return population; //by Wenhao, changed from return ranking.getSubfront(0)
	}

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
	
}
