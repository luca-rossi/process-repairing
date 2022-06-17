package org.processmining.processrepairing.algorithms.editdistance;

import java.util.HashSet;
import java.util.Hashtable;

import org.apache.commons.lang3.tuple.Pair;
import org.processmining.processrepairing.models.PetrinetModel;

public class EdgesCounter {
	
	private int numEdges1;
	private int numEdges2;
	private int numCommonEdges;
	
	/**
	 * Count and store in the attributes of this class the number of edges in a first Petri net,
	 * the number of edges in a second Petri net, and the number of common edges between the two nets
	 * (comparing the labels of both the starting place/transition and the ending transition/place for each edge)
	 * @param net1 first Petri net
	 * @param net2 second Petri net
	 */
	public void compute(PetrinetModel net1, PetrinetModel net2) {
		Hashtable<String, Pair<HashSet<String>, HashSet<String>>> places1 = net1.getPlaces();
		Hashtable<String, Pair<HashSet<String>, HashSet<String>>> places2 = net2.getPlaces();
		Hashtable<String, Pair<HashSet<String>, HashSet<String>>> transitions1 = net1.getTransitions();
		Hashtable<String, Pair<HashSet<String>, HashSet<String>>> transitions2 = net2.getTransitions();
		
		this.numEdges1 = getNumEdges(places1) + getNumEdges(transitions1);
		this.numEdges2 = getNumEdges(places2) + getNumEdges(transitions2);
		this.numCommonEdges = getNumCommonEdges(places1, places2) + getNumCommonEdges(transitions1, transitions2);
	}

	public int getNumEdges1() {
		return numEdges1;
	}
	public int getNumEdges2() {
		return numEdges2;
	}
	public int getNumCommonEdges() {
		return numCommonEdges;
	}

	/**
	 * Count the number of edges in a collection of nodes
	 * @param nodes hash table which associates to each node a set containing its exit nodes
	 * @return number of edges
	 */
	private int getNumEdges(Hashtable<String, Pair<HashSet<String>, HashSet<String>>> nodes) {
		int numEdges = 0;
		for(String node: nodes.keySet())
			numEdges += nodes.get(node).getRight().size();
		return numEdges;
	}
	
	/**
	 * Count the number of common edges between two collection of nodes, comparing their labels
	 * @param nodes1 hash table which associates to each node a set containing its exit nodes
	 * @param nodes2 hash table which associates to each node a set containing its exit nodes
	 * @return number of common edges
	 */
	private int getNumCommonEdges(Hashtable<String, Pair<HashSet<String>, HashSet<String>>> nodes1, Hashtable<String, Pair<HashSet<String>, HashSet<String>>> nodes2) {
		int numCommonEdges = 0;
		for(String node: nodes1.keySet()) {
			if(nodes2.containsKey(node)) {
				HashSet<String> edges1 = nodes1.get(node).getRight();
				HashSet<String> edges2 = nodes2.get(node).getRight();
				for(String edge: edges1)
					if(edges2.contains(edge))
						numCommonEdges++;
			}
		}
		return numCommonEdges;
	}

}