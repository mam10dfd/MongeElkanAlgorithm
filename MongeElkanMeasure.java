package de.uni_leipzig.simba.measures.string;
import java.util.*;


/**
 * @author Peggy Lucke
 *
 */
public class MongeElkanMeasureFin {
	//Tokens are divide by space
	private String split=" ";
	//Token divide by another character as space
	public void setSplit(String split){
		this.split=split;
	}
	
	/**
	 * @param source Texts to compare with target
	 * @param target Texts to compare with source
	 * @param threshold is the minimum similarity of the results
	 * @return all results of source compare with target together with the similarity of them
	 */
	public Map<String, Map<String, Double>> mongeElkan(Set<String> source, Set<String> target, double threshold){
		 Iterator<String> sit = source.iterator();//As
		double resultDouble;
		//(A,B)+result
		Map<String, Map<String, Double>> result = new HashMap<String, Map<String, Double>>();
		while(sit.hasNext()){//ein A
			Iterator<String> tit = target.iterator();//Bs
			String sourceString=sit.next();
			HashMap<String, Double> resultB = new HashMap<String, Double>();
			while(tit.hasNext()){//ein B --> schneller machen, wenn B = vorh. B
				String targetString=tit.next();
				resultDouble=oneMongeElkan(sourceString.split(split), targetString.split(split), threshold);
				if(threshold<=resultDouble){
					resultB.put(targetString, resultDouble);
				}
			}
			result.put(sourceString, resultB);
		}
		 return result;
	 }
	
	/*
	 * compare one text with another
	 */
	private double oneMongeElkan(String[] sourceToken, String[] targetToken, double threshold){
		double simB=0;
		double result=0;
		float maxNumber=sourceToken.length;
		/*
		 * the minimum of the result to reach the threshold
		 */
		float treshMin=(float) (maxNumber*threshold);
		for(String sourceString : sourceToken){//ein a
			double maxSim=0;
			for(String targetString : targetToken){//ein b
				double sim=tokenSim(sourceString,targetString);
				if(maxSim<sim){maxSim=sim;}
				if(maxSim==1){break;}
			}
			maxNumber-=1-maxSim;
			/*
			 * add 0.0001 for rounding errors.
			 * if the similarity of all source tokens with the target tokens don't reach the minimum
			 * threshold, there are no result, so break the algorithm.
			 */
			if(treshMin>maxNumber+0.0001){
				result=0;
				break;
			}
			simB+=maxSim;
		}
		if(simB!=0){result=simB/sourceToken.length;}
		return result;
	}
	
	/*
	 * use the Trigramm Algorithm to compare the tokens
	 */
	private double tokenSim(String tokenA, String tokenB){
		TrigramMeasure trigram = new TrigramMeasure();
		double result = trigram.getSimilarity(tokenA, tokenB);
		return result;
	}

}