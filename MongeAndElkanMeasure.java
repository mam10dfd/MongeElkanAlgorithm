package de.uni_leipzig.simba.measures.string;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import de.uni_leipzig.simba.*;
public class MongeAndElkanMeasure {
	//Tokens are divide by space
	private String split=" ";
	//Token divide by another character as space
	public void setSplit(String split){
		this.split=split;
	}
//-------------------------------------	0815---------------------------------------------
	/*
	 * Naiv approach of monge-elkan algorithm. Source mean a few ore more texts, which are compare with all texts in the target
	 */
	public Map<String, Map<String, Double>> algorithmNorm(Set<String> source, Set<String> target, double threshold){
		 Iterator<String> sit = source.iterator();//As (all texts from source to compare)
		double resultDouble;
		//(A,B)+result
		Map<String, Map<String, Double>> result = new HashMap<String, Map<String, Double>>();
		while(sit.hasNext()){//one A (one text too compare with all texts in the target)
			Iterator<String> tit = target.iterator();//Bs (all texts from target to compare)
			String sourceString=sit.next();
			HashMap<String, Double> resultB = new HashMap<String, Double>();
			while(tit.hasNext()){//one B
				String targetString=tit.next();
				//the Monge Elkan algorithm for one source A and one target B
				resultDouble=oneMongeElkanNorm(sourceString.split(split), targetString.split(split));
				if(threshold<=resultDouble){
					resultB.put(targetString, resultDouble);
				}
			}
			
			result.put(sourceString, resultB);
		}
		 return result;
	 }
	/*
	 * naiv use of the Monge-Elkan algorithm for texts to compare
	 */
	private double oneMongeElkanNorm(String[] sourceToken, String[] targetToken){
		double simB=0;
		double result=0;
		/*
		 * All tokens of source A are compare with all tokens of target B 
		 */
		for(String sourceString : sourceToken){//ein a
			double maxSim=0;
			/*
			 * find the max(a,all b)
			 */
			for(String targetString : targetToken){//ein b
				double sim=tokenSim(sourceString,targetString);
				if(maxSim<sim){maxSim=sim;}
			}
			/*
			 * sum of max(a, all b)
			 */
			simB+=maxSim;
		}
		/*
		 * sum/(number of all tokens in a)
		 */
		if(simB!=0){
			result=simB/sourceToken.length;
			}
		return result;
	}
//-----------------------------END 0815---------------------------------------------------------
//------------------------------faster algorithm maxSim=1 -> break------------------------------
	public Map<String, Map<String, Double>> algorithmMaxSim(Set<String> source, Set<String> target, double threshold){
		 Iterator<String> sit = source.iterator();//As
		double resultDouble;
		//(A,B)+result
		Map<String, Map<String, Double>> result = new HashMap<String, Map<String, Double>>();
		while(sit.hasNext()){//one A
			Iterator<String> tit = target.iterator();//Bs
			String sourceString=sit.next();
			HashMap<String, Double> resultB = new HashMap<String, Double>();
			while(tit.hasNext()){//one B
				String targetString=tit.next();
				resultDouble=oneMongeElkan(sourceString.split(split), targetString.split(split));
				if(threshold<=resultDouble){
					resultB.put(targetString, resultDouble);
				}
			}
			result.put(sourceString, resultB);
		}
		 return result;
	 }
	
	private double oneMongeElkan(String[] sourceToken, String[] targetToken){
		double simB=0;
		double result=0;
		for(String sourceString : sourceToken){//one a
			double maxSim=0;
			for(String targetString : targetToken){//one b
				double sim=tokenSim(sourceString,targetString);
				if(maxSim<sim){maxSim=sim;}
				//new if the algorithm find similarity of one by the compare one a with all b, break and write the result to the sum
				if(maxSim==1){break;}
			}
			simB+=maxSim;
		}
		if(simB!=0){result=simB/sourceToken.length;}
		return result;
	}
//-------------------------------END faster algorithm maxSim=1 -> break-------------------------------
//---------------------- faster algorithm threshold>break &maxSim------------------------------------------
	public Map<String, Map<String, Double>> algorithmThreshold(Set<String> source, Set<String> target, double threshold){
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
				resultDouble=oneMongeElkanThresh(sourceString.split(split), targetString.split(split), threshold);
				if(threshold<=resultDouble){
					resultB.put(targetString, resultDouble);
				}
			}
			result.put(sourceString, resultB);
		}
		 return result;
	 }
	
	private double oneMongeElkanThresh(String[] sourceToken, String[] targetToken, double threshold){
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
//---------------------- END faster algorithm threshold>break &maxSim------------------------------------------
//---------------------- faster algorithm all before + length-------------------------------------------------------
/*
 * an idea for compare the length of tokens, it doesn't work yet. It is an idea for later 
 */
	public Map<String, Map<String, Double>> algorithmLength(Set<String> source, Set<String> target, double threshold){
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
				resultDouble=oneMongeElkanLength(sourceString.split(split), targetString.split(split), threshold);
				if(threshold<=resultDouble){
					resultB.put(targetString, resultDouble);
				}
			}
			result.put(sourceString, resultB);
		}
		 return result;
	 }
	
	private Double oneMongeElkanLength(String[] sourceToken, String[] targetToken, double threshold){
		double simB=0;
		double result=0;
		float maxNumber=sourceToken.length;
		float treshMin=(float) (maxNumber*threshold);
		for(String sourceString : sourceToken){//ein a
			double sourceLength=sourceString.length();
			double maxSim=0;
			for(String targetString : targetToken){//ein b
				//length too different:
				double targetLength=targetString.length();
				if(targetLength>sourceLength){
					if(maxSim>sourceLength/targetLength){
						continue;
					}
				}else{
					if(maxSim>targetLength/sourceLength){
						continue;
					}
				}
				
				double sim=tokenSim(sourceString,targetString);
				if(maxSim<sim){maxSim=sim;}
				if(maxSim==1){break;}
			}
			maxNumber=(float) (maxNumber-1+maxSim);
			if(treshMin>maxNumber+0.0001){
				result=0;
				break;
			}
			simB+=maxSim;
		}
		if(simB!=0){result=simB/sourceToken.length;}
		return result;
	}
	//-------------------END faster algorithm all before + length-------------------------------------------------------
	public Map<String, Map<String, Double>> mongeElkan(Set<String> source, Set<String> target, double threshold){
		 return algorithmNorm(source,target,threshold);
	 }
	public Map<String, Map<String, Double>> mongeElkanMaxSim(Set<String> source, Set<String> target, double threshold){
		 return algorithmMaxSim(source,target,threshold);
	 }
	public Map<String, Map<String, Double>> mongeElkanThresh(Set<String> source, Set<String> target, double threshold){
		 return algorithmThreshold(source,target,threshold);
	 }
	
	/*
	 * Trigramm-Algorithmus kann nicht mit Länge der Wörter genutzt werden gegenbeispiel:
	 * mongemonge mit monge sim=0,93; monga mit monge sim=0,7...
	 * Qgram ebenfalls nicht.
	 */
	public Map<String, Map<String, Double>> mongeElkanLength(Set<String> source, Set<String> target, double threshold){
		 return algorithmLength(source,target,threshold);
	 }
	
	/*
	 * use the Trigramm Algorithm to compare the tokens
	 */
	private double tokenSim(String tokenA, String tokenB){
		TrigramMeasure trigram = new TrigramMeasure();
		double result = trigram.getSimilarity(tokenA, tokenB);
		return result;
	}

	
	/*
	 * a little test
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String [] argh) throws IOException{
		HashSet source = new HashSet();
		BufferedReader br = new BufferedReader(new FileReader(new File("./data/02002.cleaned.txt")));
		BufferedReader br2 = new BufferedReader(new FileReader(new File("./data/02003.cleaned.txt")));
		String line="";
		String all="";
		String all2="";

		while((line = br.readLine()) != null){
			all+=line;
		}
		while((line = br2.readLine()) != null){
			all2+=line;
		}
		br.close();
		br2.close();

		source.add(all);
		HashSet target = new HashSet();
		target.add(all2);
		
		MongeAndElkanMeasure m = new MongeAndElkanMeasure();
		long normTime = System.currentTimeMillis();
		Map<String, Map<String, Double>> monge1 = m.mongeElkan(source, target, 0.5);
		long normTimeDone = System.currentTimeMillis();
		long normTime2 = System.currentTimeMillis();
		Map<String, Map<String, Double>> monge2 = m.mongeElkanMaxSim(source, target, 0.5);
		long normTimeDone2 = System.currentTimeMillis();
		long normTime3 = System.currentTimeMillis();
		Map<String, Map<String, Double>> monge3 = m.mongeElkanThresh(source, target, 0.5);
		long normTimeDone3 = System.currentTimeMillis();
		long normTime4 = System.currentTimeMillis();
		Map<String, Map<String, Double>> monge4 = m.mongeElkanLength(source, target, 0.5);
		long normTimeDone4 = System.currentTimeMillis();
		
		long resultTime=normTimeDone-normTime;
		long resultTime2=normTimeDone2-normTime2;
		long resultTime3=normTimeDone3-normTime3;
		long resultTime4=normTimeDone4-normTime4;
		
	//	TrigramMeasure trigram = new TrigramMeasure();
	//	double result = trigram.getSimilarity("monga", "monge");
	//	System.out.println(result);
		System.out.println(resultTime+" : "+resultTime2+" : "+resultTime3+" : "+resultTime4+" equal: "+(monge1.equals(monge2))+" "+(monge3.equals(monge2))+" "+(monge4.equals(monge3))+"\n Ergebnis: "+monge1);
	}
}