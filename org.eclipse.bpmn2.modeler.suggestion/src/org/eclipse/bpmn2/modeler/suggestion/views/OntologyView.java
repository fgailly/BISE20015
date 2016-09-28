package org.eclipse.bpmn2.modeler.suggestion.views;

import java.io.IOException;

import org.eclipse.bpmn2.modeler.suggestion.Activator;
import org.eclipse.bpmn2.modeler.suggestion.algorithm.BPMNSuggestionEngine;
import org.eclipse.bpmn2.modeler.suggestion.internal.OntologyContentProvider;
import org.eclipse.bpmn2.modeler.suggestion.internal.SuggestionLabelProvider;
import org.eclipse.bpmn2.modeler.suggestion.part.AnnotateProcessor;
import org.eclipse.bpmn2.modeler.suggestion.part.EditorProcessor;
import org.eclipse.bpmn2.modeler.suggestion.part.OntologyModel;
import org.eclipse.bpmn2.modeler.suggestion.part.SelectionChangeProcessor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class OntologyView extends ViewPart {

	final static public String ID = "org.eclipse.bpmn2.modeler.suggestion.views.OntologyView";
	//private TableViewer viewer;
	private TreeViewer viewer;
	private Action doubleClickAction;
	private BPMNSuggestionEngine engine;
	
	private SelectionChangeProcessor listener = new SelectionChangeProcessor();
	private EditorProcessor listener2 = new EditorProcessor();
	
	public void createPartControl(Composite parent) {
		FontRegistry fr = JFaceResources.getFontRegistry();
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new OntologyContentProvider());
		viewer.setLabelProvider(new SuggestionLabelProvider(fr));
		
		 // Expand the tree
	    viewer.setAutoExpandLevel(2);
	    
	    // Provide the input to the ContentProvider
	    //viewer.setInput(getInstructions());
	    
	    // Create a suggestion Engine with the lifeCycle of the view
		engine = new BPMNSuggestionEngine();
		
		Activator.getDefault().getPreferenceStore().
			addPropertyChangeListener(new IPropertyChangeListener(){

				@Override
				public void propertyChange(PropertyChangeEvent event) {
					engine = new BPMNSuggestionEngine();
					
				}
				
			});
		
		// Register Diagram Selection Listener
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
		
		// Register Selection Provider for Ontology Properties View
		getSite().setSelectionProvider(viewer);
		
		// Implement Annotation Action
		createDoubleClickAction();
		hookDoubleClickAction();
	    

	}

	private void createDoubleClickAction() {
		doubleClickAction = new AnnotateProcessor(listener);	
	}


	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	
	public void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Suggestion View",
			message);
	}
	
	public void showErrorMessage(String message) {
		MessageDialog.openError(
			viewer.getControl().getShell(),
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
		return viewer.getSelection();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}


	public BPMNSuggestionEngine getEngine() {
		return engine;
	}

	public void setInput(OntologyModel model) {
		viewer.setInput(model);
		
	}

	public SelectionChangeProcessor getListener() {
		return listener;
	}


}
