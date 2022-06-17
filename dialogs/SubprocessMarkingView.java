package org.processmining.processrepairing.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.processrepairing.algorithms.SetToListModel;
import org.processmining.processrepairing.models.Const;
import org.processmining.processrepairing.models.State;
import org.processmining.processrepairing.models.Subprocess;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import info.clearthought.layout.TableLayout;

/**
 * View containing three lists:
 * - places of the selected subprocess
 * - start places of the selected subprocess (initial marking)
 * - end places of the the selected subprocess (final marking)
 * The user can add/remove start/end places of the selected subprocess
 */
public class SubprocessMarkingView extends JPanel {

	private static final long serialVersionUID = 137822681827039472L;

	private final int SELECTED_START = 0;
	private final int SELECTED_END = 1;
	private final int SELECTED_NONE = 2;
	
	private UIPluginContext context;
	private State state;
	private SetToListModel setToListModel;

	private JButton addToInitialMarkingButton;
	private JButton addToFinalMarkingButton;
	private JButton removeFromMarkingButton;
	
	private ProMList<String> placesList;
	private ProMList<String> initialMarkingList;
	private ProMList<String> finalMarkingList;
	
	private DefaultListModel<String> placesListModel;
	private DefaultListModel<String> initialMarkingListModel;
	private DefaultListModel<String> finalMarkingListModel;
	
	private String selectedPlace = null;
	private int selectionMode = SELECTED_NONE;

	public SubprocessMarkingView(UIPluginContext context, State state) {
		this.context = context;
		this.state = state;
		this.setToListModel = new SetToListModel();
		SlickerFactory factory = SlickerFactory.instance();
		
		placesListModel = new DefaultListModel<String>();
		initialMarkingListModel = new DefaultListModel<String>();
		finalMarkingListModel = new DefaultListModel<String>();
		
		placesList = new ProMList<String>("Subprocess places", placesListModel);
		placesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		placesList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = placesList.getSelectedValuesList();
				if (selected.size() == 1 && !e.getValueIsAdjusting())
					onSelectPlace(selected.get(0), SELECTED_NONE);
			}
		});

		initialMarkingList = new ProMList<String>("Initial marking", initialMarkingListModel);
		initialMarkingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		initialMarkingList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = initialMarkingList.getSelectedValuesList();
				if (selected.size() == 1 && !e.getValueIsAdjusting())
					onSelectPlace(selected.get(0), SELECTED_START);
			}
		});

		finalMarkingList = new ProMList<String>("Final marking", finalMarkingListModel);
		finalMarkingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		finalMarkingList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = finalMarkingList.getSelectedValuesList();
				if (selected.size() == 1 && !e.getValueIsAdjusting())
					onSelectPlace(selected.get(0), SELECTED_END);
			}
		});

		double size[][] = { { Const.LIST_WIDTH, Const.LIST_WIDTH, Const.LIST_WIDTH }, { Const.LIST_HEIGHT, TableLayout.FILL } };
		setLayout(new TableLayout(size));
		
		this.add(placesList, "0, 0");
		this.add(initialMarkingList, "1, 0");
		this.add(finalMarkingList, "2, 0");
		
		addToInitialMarkingButton = factory.createButton("Add To Initial Marking");
		addToInitialMarkingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddToInitialMarking();
			}
		});
		this.add(addToInitialMarkingButton, "0, 1");
		addToFinalMarkingButton = factory.createButton("Add To Final Marking");
		addToFinalMarkingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddToFinalMarking();
			}
		});
		this.add(addToFinalMarkingButton, "1, 1");
		removeFromMarkingButton = factory.createButton("Remove From Marking");
		removeFromMarkingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemoveFromMarking();
			}
		});
		this.add(removeFromMarkingButton, "2, 1");
	}

	private void onSelectPlace(String label, int selectionMode) {
		this.selectionMode = selectionMode;
		if(selectionMode == SELECTED_START){
			finalMarkingList.getList().clearSelection();
			placesList.getList().clearSelection();
		}
		else if(selectionMode == SELECTED_END){
			initialMarkingList.getList().clearSelection();
			placesList.getList().clearSelection();
		}
		else if(selectionMode == SELECTED_NONE){
			initialMarkingList.getList().clearSelection();
			finalMarkingList.getList().clearSelection();
		}
		
		for(String place: state.getSelectedSubprocess().getModel().getPlaces().keySet()) {
			if(place.equals(label)) {
				selectedPlace = place;
				return;
			}
		}
	}

	private void onAddToInitialMarking() {
		if(initialMarkingListModel.contains(selectedPlace))
			return;
		Subprocess subprocess = state.getSelectedSubprocess();
		subprocess.addPlaceToInitialMarking(selectedPlace);
		setToListModel.getList(initialMarkingListModel, subprocess.getInitialMarking());
	}

	private void onAddToFinalMarking() {
		if(finalMarkingListModel.contains(selectedPlace))
			return;
		Subprocess subprocess = state.getSelectedSubprocess();
		subprocess.addPlaceToFinalMarking(selectedPlace);
		setToListModel.getList(finalMarkingListModel, subprocess.getFinalMarking());
	}
	
	private void onRemoveFromMarking() {
		Subprocess subprocess = state.getSelectedSubprocess();
		if(selectionMode == SELECTED_START){
			subprocess.removePlaceFromInitialMarking(selectedPlace);
			setToListModel.getList(initialMarkingListModel, subprocess.getInitialMarking());
		}
		else if(selectionMode == SELECTED_END){
			subprocess.removePlaceFromFinalMarking(selectedPlace);
			setToListModel.getList(finalMarkingListModel, subprocess.getFinalMarking());
		}
	}
	
	public void updateLists() {
		Subprocess subprocess = state.getSelectedSubprocess();
		selectedPlace = null;
		selectionMode = SELECTED_NONE;
		
		setToListModel.getList(placesListModel, subprocess.getModel().getPlaces().keySet());
		setToListModel.getList(initialMarkingListModel, subprocess.getInitialMarking());
		setToListModel.getList(finalMarkingListModel, subprocess.getFinalMarking());
		
		updateButtons();
	}
	
	public void updateButtons() {
		boolean enabled = !state.getSelectedSubprocess().isIncluded();
		addToInitialMarkingButton.setEnabled(enabled);
		addToFinalMarkingButton.setEnabled(enabled);
		removeFromMarkingButton.setEnabled(enabled);
	}
	
}
