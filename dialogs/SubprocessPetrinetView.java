package org.processmining.processrepairing.dialogs;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.processrepairing.models.State;
import org.processmining.processrepairing.models.Subprocess;

import info.clearthought.layout.TableLayout;

/**
 * View of the Petri net of the selected subprocess or of the subnet of the main process between its initial and final location,
 * depending on the Visualization Mode and the Replacement Mode
 */
public class SubprocessPetrinetView extends JPanel {

	private static final long serialVersionUID = 6090934514176046552L;

	private UIPluginContext context;
	private State state;

	private JComponent graph;

	public SubprocessPetrinetView(UIPluginContext context, State state) {
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
			Subprocess subprocess = state.getSelectedSubprocess();
			Petrinet net;
			if(state.getVisualizationMode().equals(State.VISUALIZATION_MODE_SUBPROCESS))
				net = subprocess.getModel().generatePetrinet();
			else {
				if(!subprocess.isIncluded())
					net = PetrinetFactory.newPetrinet("Empty");
				else
					net = state.getReplacementMode().equals(State.REPLACEMENT_MODE_INITIAL)
							? state.getSelectedSubprocess().getReplacedInitialModel().generatePetrinet()
							: state.getSelectedSubprocess().getReplacedCurrentModel().generatePetrinet();
			}
			graph = ProMJGraphVisualizer.instance().visualizeGraph(context, net);
		} catch (NullPointerException e) {
	    	Petrinet emptyNet = PetrinetFactory.newPetrinet("Empty");
			graph = ProMJGraphVisualizer.instance().visualizeGraph(context, emptyNet);
		}
		add(graph, "0, 0");
		
		revalidate();
		repaint();
	}

}
