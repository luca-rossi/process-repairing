package org.processmining.processrepairing.models;

import java.awt.Color;

import org.processmining.processrepairing.algorithms.editdistance.EditDistanceCalculatorExhaustive;
import org.processmining.processrepairing.algorithms.editdistance.EditDistanceCalculatorGU;
import org.processmining.processrepairing.algorithms.editdistance.EditDistanceCalculatorGreedy;
import org.processmining.processrepairing.algorithms.editdistance.EditDistanceCalculatorMCS;

public class Const {

	public static final String PETRINET_FILE_FORMAT = "pnml";
	
	public static final String SUBPROCESS_LABEL_DELIMITER = "_";
	public static final String SUBPROCESS_LABEL_DELIMITER_ESCAPE = "-";

	public static final Color COLOR_SUBPROCESS = new Color(150, 255, 150);
	public static final Color COLOR_SELECTED_SUBPROCESS = new Color(0, 255, 0);
	public static final Color COLOR_EMPTY = new Color(255, 255, 255);

	public static final int SUBVIEW_WIDTH = 450;

	public static final int LIST_WIDTH = 140;
	public static final int LIST_HEIGHT = 180;
	
	public static final int COMBOBOX_WIDTH = 140;
	public static final int COMBOBOX_HEIGHT = 30;

	public static final String[] PACKAGES_TO_SEARCH = new String[] {
			//"org.processmining.processrepairing.algorithms.editdistance"
	};
	
	public static final Class[] EDIT_DISTANCE_CLASSES = new Class[] {
			EditDistanceCalculatorGreedy.class,
			EditDistanceCalculatorExhaustive.class,
			EditDistanceCalculatorGU.class,
			EditDistanceCalculatorMCS.class
	};

}
