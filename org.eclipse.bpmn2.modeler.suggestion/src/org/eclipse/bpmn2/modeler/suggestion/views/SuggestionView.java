package org.eclipse.bpmn2.modeler.suggestion.views;


import java.io.IOException;

import org.eclipse.bpmn2.modeler.suggestion.algorithm.BPMNSuggestionEngine;
import org.eclipse.bpmn2.modeler.suggestion.algorithm.BPMNSuggestionEngine2;
import org.eclipse.bpmn2.modeler.suggestion.internal.SuggestionContentProvider;
import org.eclipse.bpmn2.modeler.suggestion.internal.SuggestionLabelProvider;
import org.eclipse.bpmn2.modeler.suggestion.part.AnnotateProcessor;
import org.eclipse.bpmn2.modeler.suggestion.part.EditorProcessor;
import org.eclipse.bpmn2.modeler.suggestion.part.SelectionChangeProcessor;
import org.eclipse.bpmn2.modeler.suggestion.part.SuggestionModel;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.core.commands.CommandManager;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.internal.services.impl.CommandService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandManagerListener;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;


/**
 * This class demonstrates how to plug-in a new
 * workbench suggestion view. The view shows annotation
 * suggestions based on the selection in the editor
 * computed with the OntologySuggestionAlgorithm.
 * The view is connected to the model using a content provider.
 */

public class SuggestionView extends ViewPart {
	
	final static public String ID = "org.eclipse.bpmn2.modeler.suggestion.views.SuggestionView";
	
	//private TableViewer viewer;
	private TreeViewer viewer;
	private Action doubleClickAction;
	private BPMNSuggestionEngine2 engine;

	private SelectionChangeProcessor listener = new SelectionChangeProcessor();
	private EditorProcessor listener2 = new EditorProcessor();

	public void createPartControl(Composite parent) {
		setViewer(new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL));
		getViewer().setContentProvider(new SuggestionContentProvider());
		getViewer().setLabelProvider(new SuggestionLabelProvider());
		
		 // Expand the tree
	    getViewer().setAutoExpandLevel(2);
	    
	    // Provide the input to the ContentProvider
	    //viewer.setInput(getInstructions());
	    
	    // Create a suggestion Engine with the lifeCycle of the view
		//engine = new BPMNSuggestionEngine2();
		
		// Register Diagram Selection Listener
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
		
		DiagramEditor test = (DiagramEditor) (getSite().getWorkbenchWindow().getActivePage().getActiveEditor());
		
		
		CommandManager service = (CommandManager) getSite().getWorkbenchWindow().getService(CommandManager.class);
		service.addCommandManagerListener(listener2);
		
		
		// Register Selection Provider for Ontology Properties View
		getSite().setSelectionProvider(getViewer());
		
		// Implement Annotation Action
		createDoubleClickAction();
		hookDoubleClickAction();
	    

	}

//	protected void generateErrorInstruction(IWorkbenchPart part, ISelection sel) {
//		System.out.println(part.getTitle());
//		String line1;
//		if (part.toString().contains("BPMN2MultiPageEditor")) {
//			line1 = "We can't generate suggestions for this selection.";
//		}
//		else {
//			line1 = "Select a construct in the editor to generate Suggestions.";
//		}
//		String[] instructions = {line1}; 
//		viewer.setInput(instructions);
//	}
//
//	private String[] getInstructions() {
//		String line1 = "Welcome to the Suggestion View.";
//		String line2 = "Select a construct in the editor to generate Suggestions.";
//		String[] instructions = {line1, line2}; 
//		return instructions;
//	}

	private void createDoubleClickAction() {
		doubleClickAction = new AnnotateProcessor(listener); 	
	}



	private void hookDoubleClickAction() {
		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	
	public void showMessage(String message) {
		MessageDialog.openInformation(
			getViewer().getControl().getShell(),
			"Annotated!",
			message);
	}
	
	public void showErrorMessage(String message) {
		MessageDialog.openError(
			getViewer().getControl().getShell(),
			"Annotation Error",
			message);
	}
	
	public void dispose() {
		getSite().getPage().removeSelectionListener(listener);
		
		try {
			engine.saveModelOntology();
			System.out.println("save model");
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
	public ISelection getSelection() {
		return getViewer().getSelection();
	}
	
	public void setInput(SuggestionModel model) {
		
			getViewer().setInput(model);
		
			
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		getViewer().getControl().setFocus();
	}


	public BPMNSuggestionEngine2 getEngine() {
		return engine;
	}
	
	public void setEngine(BPMNSuggestionEngine2 engine){
		this.engine = engine;
	}
	
	public SelectionChangeProcessor getListener() {
		return listener;
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
	}

	


}