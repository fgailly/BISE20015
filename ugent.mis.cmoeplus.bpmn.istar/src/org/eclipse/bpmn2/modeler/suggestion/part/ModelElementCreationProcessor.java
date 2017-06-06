package org.eclipse.bpmn2.modeler.suggestion.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;

import org.eclipse.bpmn2.modeler.ui.editor.BPMN2MultiPageEditor;
import org.eclipse.core.commands.ICommandManagerListener;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.ui.IPropertyListener;

public class ModelElementCreationProcessor implements  IPropertyListener{

	

	@Override
	public void propertyChanged(Object source, int propId) {
		System.out.println("reaction " + source.toString() + propId);
		BPMN2MultiPageEditor editor = (BPMN2MultiPageEditor) source;
		System.out.println(editor.getBpmnDiagram(0).getName());
		
	}

}
