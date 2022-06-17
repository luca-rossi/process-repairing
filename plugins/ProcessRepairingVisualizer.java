package org.processmining.processrepairing.plugins;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.processrepairing.dialogs.MainView;
import org.processmining.processrepairing.models.State;

@Plugin(name = "Process Repairing Visualizer",
		parameterLabels = {"Process Repairing"},
		returnLabels = {"Net"},
		returnTypes = {JComponent.class},
		userAccessible = true)
@Visualizer
public class ProcessRepairingVisualizer {
	
	/**
	 * This plug-in variant (associated to a State object) shows the editor (MainView)
	 * which allows the user to connect models of subprocesses to a main process,
	 * these models have been previously saved in the State object when initialized.
	 * 
	 * @param context The context to run in.
	 * @param state The State object containing information about the main process and the subprocesses.
	 * @return The model (Petri net) of the enhanced main process.
	 */
	@UITopiaVariant(affiliation = "UnivPM",
					author = "Luca Rossi",
					email = "rossiluca711@gmail.com")
	@PluginVariant(variantLabel = "Process Repairing Visualizer", requiredParameterLabels = { 0 })
	public JComponent run(UIPluginContext context, State state) {
		return new MainView(context, state);
	}
	
}
