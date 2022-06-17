package org.processmining.processrepairing.dialogs;

import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
import org.processmining.processrepairing.models.Const;
import org.processmining.processrepairing.models.State;

import info.clearthought.layout.TableLayout;

/**
 * View of the editor, it observes the State and updates its 5 subviews
 */
public class MainView extends JPanel implements Observer {

	private static final long serialVersionUID = -60087716353524468L;
	
	private State state;
	
	private MainPetrinetView mainPetrinetView;
	private SubprocessPetrinetView subprocessPetrinetView;
	private SubprocessListView subprocessListView;
	private SubprocessLocationView subprocessLocationView;
	private SubprocessMarkingView subprocessMarkingView;
	
	public MainView(UIPluginContext context, State state) {
		this.state = state;
		this.state.addObserver(this);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		ProMSplitPane splitPane = new ProMSplitPane(ProMSplitPane.VERTICAL_SPLIT);
		splitPane.setResizeWeight(1.0);
		splitPane.setOneTouchExpandable(true);
		ProMSplitPane petriViews = new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
		JPanel editViews = new JPanel();
		splitPane.setTopComponent(petriViews);
		splitPane.setBottomComponent(editViews);
		add(splitPane);
		
		petriViews.setLayout(new BoxLayout(petriViews, BoxLayout.X_AXIS));
		petriViews.setResizeWeight(0.5);
		petriViews.setOneTouchExpandable(true);
		mainPetrinetView = new MainPetrinetView(context, state);
		subprocessPetrinetView = new SubprocessPetrinetView(context, state);
		petriViews.setLeftComponent(mainPetrinetView);
		petriViews.setRightComponent(subprocessPetrinetView);

		double size[][] = { { Const.SUBVIEW_WIDTH, Const.SUBVIEW_WIDTH, TableLayout.FILL }, { TableLayout.FILL } };
		editViews.setLayout(new TableLayout(size));
		subprocessListView = new SubprocessListView(context, state);
		editViews.add(subprocessListView, "0, 0");
		subprocessLocationView = new SubprocessLocationView(context, state);
		editViews.add(subprocessLocationView, "1, 0");
		subprocessMarkingView = new SubprocessMarkingView(context, state);
		editViews.add(subprocessMarkingView, "2, 0");

		validate();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		mainPetrinetView.updateGraph();
		subprocessPetrinetView.updateGraph();
		subprocessListView.updateEditDistance();
		try {
			subprocessLocationView.updateButtons();
			subprocessMarkingView.updateButtons();
		} catch (NullPointerException e) { }
		try {
			subprocessLocationView.updateLists();
			subprocessMarkingView.updateLists();
		} catch (NullPointerException e) { }
	}

}
