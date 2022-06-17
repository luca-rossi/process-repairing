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
 * - places of the (initial, not modified) main process
 * - start places of the main process (initial location)
 * - end places of the the main process (final location)
 * The user can add/remove start/end places of the main process for the selected subprocess
 */
public class SubprocessLocationView extends JPanel {

	private static final long serialVersionUID = -8736312689838457463L;

	private final int SELECTED_START = 0;
	private final int SELECTED_END = 1;
	private final int SELECTED_NONE = 2;

	private UIPluginContext context;
	private State state;
	private SetToListModel setToListModel;

	private JButton addToInitialLocationButton;
	private JButton addToFinalLocationButton;
	private JButton removeFromLocationButton;
	
	private ProMList<String> placesList;
	private ProMList<String> initialLocationList;
	private ProMList<String> finalLocationList;
	
	private DefaultListModel<String> placesListModel;
	private DefaultListModel<String> initialLocationListModel;
	private DefaultListModel<String> finalLocationListModel;
	
	private String selectedPlace = null;
	private int selectionMode = SELECTED_NONE;

	public SubprocessLocationView(UIPluginContext context, State state) {
		this.context = context;
		this.state = state;
		this.setToListModel = new SetToListModel();
		SlickerFactory factory = SlickerFactory.instance();
		
		placesListModel = new DefaultListModel<String>();
		initialLocationListModel = new DefaultListModel<String>();
		finalLocationListModel = new DefaultListModel<String>();
		
		placesList = new ProMList<String>("Main process places", placesListModel);
		placesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		placesList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = placesList.getSelectedValuesList();
				if (selected.size() == 1 && !e.getValueIsAdjusting())
					onSelectPlace(selected.get(0), SELECTED_NONE);
			}
		});

		initialLocationList = new ProMList<String>("Initial location", initialLocationListModel);
		initialLocationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		initialLocationList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = initialLocationList.getSelectedValuesList();
				if (selected.size() == 1 && !e.getValueIsAdjusting())
					onSelectPlace(selected.get(0), SELECTED_START);
			}
		});

		finalLocationList = new ProMList<String>("Final location", finalLocationListModel);
		finalLocationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		finalLocationList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = finalLocationList.getSelectedValuesList();
				if (selected.size() == 1 && !e.getValueIsAdjusting())
					onSelectPlace(selected.get(0), SELECTED_END);
			}
		});

		double size[][] = { { Const.LIST_WIDTH, Const.LIST_WIDTH, Const.LIST_WIDTH }, { Const.LIST_HEIGHT, TableLayout.FILL } };
		setLayout(new TableLayout(size));
		
		this.add(placesList, "0, 0");
		this.add(initialLocationList, "1, 0");
		this.add(finalLocationList, "2, 0");

		addToInitialLocationButton = factory.createButton("Add To Initial Location");
		addToInitialLocationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddToInitialLocation();
			}
		});
		this.add(addToInitialLocationButton, "0, 1");
		addToFinalLocationButton = factory.createButton("Add To Final Location");
		addToFinalLocationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddToFinalLocation();
			}
		});
		this.add(addToFinalLocationButton, "1, 1");
		removeFromLocationButton = factory.createButton("Remove From Location");
		removeFromLocationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemovePlace();
			}
		});
		this.add(removeFromLocationButton, "2, 1");
	}

	private void onSelectPlace(String label, int selectionMode) {
		this.selectionMode = selectionMode;
		if(selectionMode == SELECTED_START){
			finalLocationList.getList().clearSelection();
			placesList.getList().clearSelection();
		}
		else if(selectionMode == SELECTED_END){
			initialLocationList.getList().clearSelection();
			placesList.getList().clearSelection();
		}
		else if(selectionMode == SELECTED_NONE){
			initialLocationList.getList().clearSelection();
			finalLocationList.getList().clearSelection();
		}
		
		for(String place: state.getInitialMainProcess().getPlaces().keySet()) {
			if(place.equals(label)) {
				selectedPlace = place;
				return;
			}
		}
	}

	private void onAddToInitialLocation() {
		if(initialLocationListModel.contains(selectedPlace))
			return;
		Subprocess subprocess = state.getSelectedSubprocess();
		subprocess.addPlaceToInitialLocation(selectedPlace);
		setToListModel.getList(initialLocationListModel, subprocess.getInitialLocation());
	}

	private void onAddToFinalLocation() {
		if(finalLocationListModel.contains(selectedPlace))
			return;
		Subprocess subprocess = state.getSelectedSubprocess();
		subprocess.addPlaceToFinalLocation(selectedPlace);
		setToListModel.getList(finalLocationListModel, subprocess.getFinalLocation());
	}
	
	private void onRemovePlace() {
		Subprocess subprocess = state.getSelectedSubprocess();
		if(selectionMode == SELECTED_START){
			subprocess.removePlaceFromInitialLocation(selectedPlace);
			setToListModel.getList(initialLocationListModel, subprocess.getInitialLocation());
		}
		else if(selectionMode == SELECTED_END){
			subprocess.removePlaceFromFinalLocation(selectedPlace);
			setToListModel.getList(finalLocationListModel, subprocess.getFinalLocation());
		}
	}
	
	public void updateLists() {
		Subprocess subprocess = state.getSelectedSubprocess();
		selectedPlace = null;
		selectionMode = SELECTED_NONE;
		
		setToListModel.getList(placesListModel, state.getInitialMainProcess().getPlaces().keySet());
		setToListModel.getList(initialLocationListModel, subprocess.getInitialLocation());
		setToListModel.getList(finalLocationListModel, subprocess.getFinalLocation());
		
		updateButtons();
	}
	
	public void updateButtons() {
		boolean enabled = !state.getSelectedSubprocess().isIncluded();
		addToInitialLocationButton.setEnabled(enabled);
		addToFinalLocationButton.setEnabled(enabled);
		removeFromLocationButton.setEnabled(enabled);
	}
	
}
