package org.processmining.processrepairing.models;

import java.util.ArrayList;
import java.util.Observable;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.processrepairing.algorithms.PetrinetModelConverter;

/**
 * Used to handle the state of the main process, the subprocesses and the selected subprocess,
 * this class also provides the methods to add and remove subprocesses
 * and the methods to select the "Visualization Mode" and the "Replacement Mode",
 * the state of this class is observed by the MainView, which updates its subviews when something changes
 */
public class State extends Observable {
	
	public static final String VISUALIZATION_MODE_SUBPROCESS = "Selected subprocess";
	public static final String VISUALIZATION_MODE_REPLACED = "Replaced subnet";
	public static final String REPLACEMENT_MODE_INITIAL = "Initial main model";
	public static final String REPLACEMENT_MODE_CURRENT = "Updated main model";

	private PetrinetModel initialMainProcess = null;
	private PetrinetModel mainProcess = null;
	private ArrayList<Subprocess> subprocesses = null;
	private Subprocess selectedSubprocess = null;
	
	private String visualizationMode;
	private String replacementMode;

	public State(Petrinet process) {
		initialMainProcess = PetrinetModelConverter.getModelFromPetrinet(process);
		mainProcess = initialMainProcess.clone();
		visualizationMode = VISUALIZATION_MODE_SUBPROCESS;
		replacementMode = REPLACEMENT_MODE_INITIAL;
	}

	public State(Petrinet process, Petrinet[] subprocesses) {
		this(process);
		setSubprocesses(subprocesses);
	}
	
	public PetrinetModel getInitialMainProcess() {
		return initialMainProcess;
	}
	public PetrinetModel getMainProcess() {
		return this.mainProcess;
	}
	
	public ArrayList<Subprocess> getSubprocesses() {
		return this.subprocesses;
	}
	public void setSubprocesses(Petrinet[] subprocesses) {
		if(subprocesses == null || subprocesses.length == 0)
			return;
		this.subprocesses = new ArrayList<Subprocess>();
		int i = 0;
		for(Petrinet subprocess: subprocesses) {
			this.subprocesses.add(new Subprocess(PetrinetModelConverter.getModelFromPetrinet(subprocess), "" + i, initialMainProcess, mainProcess));
			i++;
		}
	}
	
	public Subprocess getSubprocessFromLabel(String label) {
		for(Subprocess subprocess: this.subprocesses)
			if(subprocess.getLabel().equals(label))
				return subprocess;
		return null;
	}
	
	public Subprocess getSelectedSubprocess() {
		return this.selectedSubprocess;
	}
	public void setSelectedSubprocess(Subprocess subprocess) {
		synchronized (this) {
			this.selectedSubprocess = subprocess;
		}
		setChanged();
		notifyObservers();
	}
	
	public void addSubprocessToMainProcess(Subprocess subprocess) {
		synchronized (this) {
			mainProcess.addSubnet(subprocess);
			subprocess.setIncluded(true);
			for(Subprocess s: subprocesses) {
				s.setUpdatedInitial(false);
				s.setUpdatedCurrent(false);
			}
		}
		setChanged();
		notifyObservers();
	}

	public void removeSubprocessFromMainProcess(Subprocess subprocess) {
		synchronized (this) {
			mainProcess.removeSubnet(subprocess);
			subprocess.setIncluded(false);
			for(Subprocess s: subprocesses) {
				s.setUpdatedInitial(false);
				s.setUpdatedCurrent(false);
			}
		}
		setChanged();
		notifyObservers();
	}

	public String getVisualizationMode() {
		return visualizationMode;
	}
	public void setVisualizationMode(String visualizationMode) {
		synchronized (this) {
			this.visualizationMode = visualizationMode;
		}
		setChanged();
		notifyObservers();
	}

	public String getReplacementMode() {
		return replacementMode;
	}
	public void setReplacementMode(String replacementMode) {
		synchronized (this) {
			this.replacementMode = replacementMode;
		}
		setChanged();
		notifyObservers();
	}

}