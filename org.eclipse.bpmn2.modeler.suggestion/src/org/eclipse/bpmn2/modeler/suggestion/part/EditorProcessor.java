package org.eclipse.bpmn2.modeler.suggestion.part;

import org.eclipse.core.commands.CommandManagerEvent;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ICommandManagerListener;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;


public class EditorProcessor implements ICommandManagerListener {


	@Override
	public void commandManagerChanged(CommandManagerEvent commandManagerEvent) {
		System.out.println("commandstack changed");
		
	}


	

}
