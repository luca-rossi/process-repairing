package org.processmining.processrepairing.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.processrepairing.algorithms.EditDistanceHandler;
import org.processmining.processrepairing.algorithms.PetrinetExporter;
import org.processmining.processrepairing.models.Const;
import org.processmining.processrepairing.models.State;
import org.processmining.processrepairing.models.Subprocess;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import info.clearthought.layout.TableLayout;

/**
 * View containing two lists of added (to the main process) and not added subprocesses,
 * and the edit distance between the selected subprocess and the subnet of the main process that's been replaced.
 * The user can select a subprocess, add/remove it to/from the main process, export to file the modified main process
 */
public class SubprocessListView extends JPanel {

	private static final long serialVersionUID = 3068312074323957597L;

	private final int SELECTED_ADDED = 0;
	private final int SELECTED_TO_ADD = 1;
	
	private UIPluginContext context;
	private State state;
	private EditDistanceHandler editDistanceHandler;
	
	private JButton addButton;
	private JButton removeButton;
	private JButton exportButton;
	
	private JPanel editDistanceView;
	private JLabel editDistanceLabel;
	private ProMComboBox<String> editDistanceComboBox;
	private DefaultComboBoxModel<String> editDistanceComboBoxModel;
	private JPanel editDistanceComboBoxWrapper;

	private JLabel visualizationModeLabel;
	private ProMComboBox<String> visualizationModeComboBox;
	private DefaultComboBoxModel<String> visualizationModeComboBoxModel;
	private JPanel visualizationModeComboBoxWrapper;

	private JLabel replacementModeLabel;
	private ProMComboBox<String> replacementModeComboBox;
	private DefaultComboBoxModel<String> replacementModeComboBoxModel;
	private JPanel replacementModeComboBoxWrapper;
	
	private ProMList<String> toAddList;
	private ProMList<String> addedList;
	
	private DefaultListModel<String> toAddListModel;
	private DefaultListModel<String> addedListModel;

	public SubprocessListView(UIPluginContext context, State state) {
		this.context = context;
		this.state = state;
		this.editDistanceHandler = EditDistanceHandler.getInstance();
		SlickerFactory factory = SlickerFactory.instance();
		double comboBoxSize[][] = { { Const.COMBOBOX_WIDTH }, { Const.COMBOBOX_HEIGHT } };

		toAddListModel = new DefaultListModel<String>();
		addedListModel = new DefaultListModel<String>();
		ArrayList<Subprocess> subprocesses = state.getSubprocesses();
		for(Subprocess subprocess : subprocesses) {
			String label = subprocess.getLabel();
			if(subprocess.isIncluded())
				addedListModel.addElement(label);
			else
				toAddListModel.addElement(label);
		}

		toAddList = new ProMList<String>("Not included subprocesses", toAddListModel);
		toAddList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		toAddList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = toAddList.getSelectedValuesList();
				if (selected.size() == 1 && !e.getValueIsAdjusting())
					onSelectSubprocess(selected.get(0), SELECTED_TO_ADD);
			}
		});

		addedList = new ProMList<String>("Included subprocesses", addedListModel);
		addedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addedList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = addedList.getSelectedValuesList();
				if (selected.size() == 1 && !e.getValueIsAdjusting())
					onSelectSubprocess(selected.get(0), SELECTED_ADDED);
			}
		});
		
		editDistanceLabel = new JLabel("<html><p align=\"center\"><b>Edit distance</b></p></html>");
		editDistanceComboBoxModel = new DefaultComboBoxModel<String>();
		Set<String> editDistanceAlgorithms = editDistanceHandler.getAlgorithmNames();
		for(String algorithm: editDistanceAlgorithms)
			editDistanceComboBoxModel.addElement(algorithm);
		editDistanceComboBox = new ProMComboBox<String>(editDistanceComboBoxModel);
		try {
			editDistanceComboBox.setSelectedIndex(0);
			editDistanceHandler.setCurrentAlgorithm((String) editDistanceComboBox.getSelectedItem());
		} catch(Exception e) { }
		editDistanceComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSelectEditDistanceAlgorithm((String) editDistanceComboBox.getSelectedItem());
			}
		});
		editDistanceComboBoxWrapper = new JPanel(new TableLayout(comboBoxSize));
		editDistanceComboBoxWrapper.add(editDistanceComboBox, "0, 0");
		updateEditDistance();

		visualizationModeLabel = new JLabel("<html><p align=\"center\"><b>Visualization mode</b></p></html>");
		visualizationModeComboBoxModel = new DefaultComboBoxModel<String>();
		visualizationModeComboBoxModel.addElement(State.VISUALIZATION_MODE_SUBPROCESS);
		visualizationModeComboBoxModel.addElement(State.VISUALIZATION_MODE_REPLACED);
		visualizationModeComboBox = new ProMComboBox<String>(visualizationModeComboBoxModel);
		visualizationModeComboBox.setSelectedIndex(0);
		visualizationModeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onChangeVisualizationMode((String) visualizationModeComboBox.getSelectedItem());
			}
		});
		visualizationModeComboBoxWrapper = new JPanel(new TableLayout(comboBoxSize));
		visualizationModeComboBoxWrapper.add(visualizationModeComboBox, "0, 0");

		replacementModeLabel = new JLabel("<html><p align=\"center\"><b>Replacement mode</b></p></html>");
		replacementModeComboBoxModel = new DefaultComboBoxModel<String>();
		replacementModeComboBoxModel.addElement(State.REPLACEMENT_MODE_INITIAL);
		replacementModeComboBoxModel.addElement(State.REPLACEMENT_MODE_CURRENT);
		replacementModeComboBox = new ProMComboBox<String>(replacementModeComboBoxModel);
		replacementModeComboBox.setSelectedIndex(0);
		replacementModeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onChangeReplacementMode((String) replacementModeComboBox.getSelectedItem());
			}
		});
		replacementModeComboBoxWrapper = new JPanel(new TableLayout(comboBoxSize));
		replacementModeComboBoxWrapper.add(replacementModeComboBox, "0, 0");

		editDistanceView = new JPanel();
		editDistanceView.add(editDistanceLabel);
		editDistanceView.add(editDistanceComboBoxWrapper);
		editDistanceView.add(visualizationModeLabel);
		editDistanceView.add(visualizationModeComboBoxWrapper);
		editDistanceView.add(replacementModeLabel);
		editDistanceView.add(replacementModeComboBoxWrapper);
		
		addButton = factory.createButton("Add");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddSubprocess();
			}
		});

		removeButton = factory.createButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemoveSubprocess();
			}
		});
		
		exportButton = factory.createButton("Export");
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExport();
			}
		});
		
		double size[][] = { { Const.LIST_WIDTH, Const.LIST_WIDTH, TableLayout.FILL }, { Const.LIST_HEIGHT, TableLayout.FILL } };
		setLayout(new TableLayout(size));
		this.add(toAddList, "0, 0");
		this.add(addedList, "1, 0");
		this.add(editDistanceView, "2, 0");
		this.add(addButton, "0, 1");
		this.add(removeButton, "1, 1");
		this.add(exportButton, "2, 1");
	}

	protected void onChangeVisualizationMode(String mode) {
		state.setVisualizationMode(mode);
	}

	protected void onChangeReplacementMode(String mode) {
		state.setReplacementMode(mode);
	}

	protected void onSelectEditDistanceAlgorithm(String selectedItem) {
		editDistanceHandler.setCurrentAlgorithm(selectedItem);
		updateEditDistance();
	}

	private void onSelectSubprocess(String label, int selectionMode) {
		Subprocess subprocess = state.getSubprocessFromLabel(label);
		state.setSelectedSubprocess(subprocess);
		if(selectionMode == SELECTED_ADDED) {
			toAddList.getList().clearSelection();
			updateEditDistance();
		}
		else if(selectionMode == SELECTED_TO_ADD)
			addedList.getList().clearSelection();
	}
	
	private void onAddSubprocess() {
		Subprocess subprocess = state.getSelectedSubprocess();
		if(subprocess.isIncluded())
			return;
		if(state.getSelectedSubprocess().getInitialLocation().isEmpty()){
			JOptionPane.showMessageDialog(new JFrame(), "Start place missing", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(state.getSelectedSubprocess().getFinalLocation().isEmpty()){
			JOptionPane.showMessageDialog(new JFrame(), "End place missing", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(state.getSelectedSubprocess().getInitialMarking().isEmpty()){
			JOptionPane.showMessageDialog(new JFrame(), "Initial marking missing", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(state.getSelectedSubprocess().getFinalMarking().isEmpty()){
			JOptionPane.showMessageDialog(new JFrame(), "Final marking missing", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String label = subprocess.getLabel();
		toAddListModel.removeElement(label);
		addedListModel.addElement(label);
		state.addSubprocessToMainProcess(subprocess);
	}
	
	private void onRemoveSubprocess() {
		Subprocess subprocess = state.getSelectedSubprocess();
		if(!subprocess.isIncluded())
			return;
		String label = subprocess.getLabel();
		addedListModel.removeElement(label);
		toAddListModel.addElement(label);
		state.removeSubprocessFromMainProcess(subprocess);
	}

	private void onExport() {
		//the PetrinetModel is cloned so the Petrinet is generated again without the subprocess marking
		PetrinetExporter.export(context, state.getMainProcess().clone().generatePetrinet(), this.getRootPane());
	}

	public void updateEditDistance() {
		Subprocess subprocess = state.getSelectedSubprocess();
		double editDistance = 0;
		String label = "-";
		try {
			label = subprocess.getLabel();
			if(subprocess.isIncluded()) {
				editDistance = editDistanceHandler.compute(subprocess, state.getReplacementMode());
				editDistance = Math.round(editDistance * 100.0) / 100.0;
			}
		} catch (NullPointerException e) { }
		editDistanceLabel.setText("<html><p align=\"center\"><b>Edit distance " + label + ": " + editDistance + "</b></p></html>");
	}

}
