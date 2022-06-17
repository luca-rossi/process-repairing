package org.processmining.processrepairing.algorithms;

import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.pnml.exporting.PnmlExportNetToPNML;
import org.processmining.processrepairing.models.Const;

public class PetrinetExporter {
	
	private static PnmlExportNetToPNML p;
	private static JFileChooser fileChooser;
	
    static {
		p = new PnmlExportNetToPNML();
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("*." + Const.PETRINET_FILE_FORMAT, Const.PETRINET_FILE_FORMAT));
    }
	
    /**
     * Export a Petri net as a pnml file
     * @param context of the plug-in
     * @param net to export
     * @param component where to show the save dialog
     */
	public static void export(UIPluginContext context, Petrinet net, JComponent component) {
		if(fileChooser.showSaveDialog(component) != JFileChooser.APPROVE_OPTION)
			return;
		File file = fileChooser.getSelectedFile();
		if(!file.getName().endsWith("." + Const.PETRINET_FILE_FORMAT))
			file = new File(file.getAbsolutePath() + "." + Const.PETRINET_FILE_FORMAT);
		try {
			p.exportPetriNetToPNMLFile(context, net, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
