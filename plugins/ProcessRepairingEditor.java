package org.processmining.processrepairing.plugins;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.processrepairing.models.State;

@Plugin(name = "Process Repairing Editor",
		parameterLabels = {"Main Process", "Subprocesses"},
		returnLabels = {"Editor"},
		returnTypes = {State.class},
		userAccessible = true,
		help = "This plug-in can be used to connect models (Petri nets) of subprocesses to the model of a main process " +
				"using an editor. The editor allows the user to choose the location and the marking of each subprocess. " +
				"For each connected subprocess, a similarity measure (edit distance) will be given " +
				"between the subprocess itself and the subnet of the main process between its initial and final location.")
public class ProcessRepairingEditor{
	
	/**
	 * This plug-in variant initializes the State object and returns it,
	 * so that the corresponding visualizer plug-in (ProcessRepairingVisualizer)
	 * will be called right after that and show the editor.
	 * 
	 * @param context The context to run in.
	 * @param process The model of the process where to add subprocesses.
	 * @param subprocesses Array of models that will be added to the main process.
	 * @return The State object which will be used by the editor.
	 */
	@UITopiaVariant(affiliation = "UnivPM",
					author = "Luca Rossi",
					email = "rossiluca711@gmail.com")
	@PluginVariant(variantLabel = "Process Repairing Editor", requiredParameterLabels = { 0, 1 })
	public State run(UIPluginContext context, Petrinet process, Petrinet[] subprocesses) {
		return new State(process, subprocesses);
	}
	
}
