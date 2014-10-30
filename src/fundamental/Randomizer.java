package fundamental;

import graph.Vertex;

import java.util.HashSet;
import java.util.Random;

public class Randomizer {
	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {
	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	//randomly select numbers from a range.
	public static Integer[] getRandomSetOfRange(int lowerBoundEnclusive, int upperBoundEnclusive, int numberOfElements){
		HashSet <Integer> resultSet=new HashSet<Integer>();
		while(resultSet.size()<numberOfElements){
			resultSet.add(randInt(lowerBoundEnclusive, upperBoundEnclusive));
		}
		Integer [] result=new Integer[resultSet.size()];
		int idx=0;
		for (Integer i: resultSet){
			result[idx++]=i;
		}
		return result;
	}
	//randomly select Entities from the Vertex.vertexMap
	public static String [] getRandomSetOfEntitiesString(int numberOfEntities){
		String [] results=new String[numberOfEntities];
		Integer[] indexSet=getRandomSetOfRange(1, Vertex.vertexMap.size(), numberOfEntities);
		for (int i=0; i<numberOfEntities; i++){
			results[i]=Vertex.vertexMap.getKey(indexSet[i]);
		}
		return results;
	}	
}
