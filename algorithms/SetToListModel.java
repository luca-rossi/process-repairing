package org.processmining.processrepairing.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import javax.swing.DefaultListModel;

public class SetToListModel {

	/**
	 * Convert a Set of string in a sorted List,
	 * used to show the sorted elements in a list view
	 * @param listModel where to copy and sort the elements in the set
	 * @param set containing the elements to put in a list
	 */
	public static void getList(DefaultListModel<String> listModel, Set<String> set) {
		ArrayList<String> list = new ArrayList<String>(set);
		Collections.sort(list, new Comparator<String>() {
		    public int compare(String o1, String o2) {
		        return extractInt(o1) - extractInt(o2);
		    }
		    int extractInt(String s) {
		        String num = s.replaceAll("\\D", "");
		        return num.isEmpty() ? 0 : Integer.parseInt(num);
		    }
		});
		listModel.clear();
		for(String elem: list)
			listModel.addElement(elem);
	}

}
