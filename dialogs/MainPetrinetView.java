package org.processmining.processrepairing.dialogs;

import java.awt.Color;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.processrepairing.models.Const;
import org.processmining.processrepairing.models.State;

import info.clearthought.layout.TableLayout;

/**
 * View of the Petri net of the main process,
 * the nodes belonging to a subprocess or to the selected subprocess are marked with different colors
 */
public class MainPetrinetView extends JPanel {

	private static final long serialVersionUID = 556510046635109916L;

	private UIPluginContext context;
	private State state;
	private JComponent graph;

	public MainPetrinetView(UIPluginContext context, State state) {
		this.context = context;
		this.state = state;

		double size[][] = { { TableLayout.FILL }, { TableLayout.FILL } };
		setLayout(new TableLayout(size));
		updateGraph();
	}

	public void updateGraph() {
		try {
			remove(graph);
		} catch (NullPointerException e) { }
		try {
			Petrinet process = state.getMainProcess().generatePetrinet();
			try {
				markProcess(process, state.getSelectedSubprocess().getLabel());
			} catch (NullPointerException e) { };
			graph = ProMJGraphVisualizer.instance().visualizeGraph(context, process);
		} catch (NullPointerException e) {
	    	Petrinet emptyNet = PetrinetFactory.newPetrinet("Empty");
			graph = ProMJGraphVisualizer.instance().visualizeGraph(context, emptyNet);
			e.printStackTrace();
		}
		add(graph, "0, 0");

		revalidate();
		repaint();
	}
	
	private Color getPlaceColor(String label, String subprocessLabel) {
		if(label.endsWith(Const.SUBPROCESS_LABEL_DELIMITER + subprocessLabel))
			return Const.COLOR_SELECTED_SUBPROCESS;
		if(label.contains(Const.SUBPROCESS_LABEL_DELIMITER))
			return Const.COLOR_SUBPROCESS;
		return Const.COLOR_EMPTY;
	}
	
	private void markProcess(Petrinet process, String subprocessLabel) {
		Iterator<Place> places = process.getPlaces().iterator();
		Iterator<Transition> transitions = process.getTransitions().iterator();
		while(places.hasNext()){
			Place place = places.next();
			Color color = getPlaceColor(place.getLabel(), subprocessLabel);
			place.getAttributeMap().put(AttributeMap.FILLCOLOR, color);
		}
		while(transitions.hasNext()){
			Transition transition = transitions.next();
			Color color = getPlaceColor(transition.getLabel(), subprocessLabel);
			transition.getAttributeMap().put(AttributeMap.FILLCOLOR, color);
		}
	}
	
}
