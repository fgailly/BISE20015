package ugent.mis.cmoeplus.bpmn.istar.views;


import java.io.IOException;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.impl.BPMNEdgeImpl;
import org.eclipse.bpmn2.di.impl.BPMNPlaneImpl;
import org.eclipse.bpmn2.di.impl.BPMNShapeImpl;
import org.eclipse.bpmn2.impl.ParticipantImpl;
import org.eclipse.bpmn2.impl.TaskImpl;
import org.eclipse.bpmn2.impl.ProcessImpl;
import org.eclipse.bpmn2.modeler.suggestion.internal.SuggestionContentProvider;
import org.eclipse.bpmn2.modeler.suggestion.internal.SuggestionLabelProvider;
import org.eclipse.bpmn2.modeler.suggestion.part.AnnotateProcessor;
import org.eclipse.bpmn2.modeler.suggestion.part.EditorProcessor;
import org.eclipse.bpmn2.modeler.suggestion.part.ModelElementCreationProcessor2;
import org.eclipse.bpmn2.modeler.suggestion.part.SelectionChangeProcessor;
import org.eclipse.bpmn2.modeler.suggestion.part.SuggestionModel;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2MultiPageEditor;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import ugent.mis.cmoeplus.bpmn.istar.BPMNSuggestionEngine;


/**
 * This class demonstrates how to plug-in a new
 * workbench suggestion view. The view shows annotation
 * suggestions based on the selection in the editor
 * computed with the OntologySuggestionAlgorithm.
 * The view is connected to the model using a content provider.
 */

public class SuggestionView extends ViewPart {
	
	final static public String ID = "ugent.mis.cmoeplus.bpmn.SuggestionView";
	
	//private TableViewer viewer;
	private TreeViewer viewer;
	private Action doubleClickAction;
	private BPMNSuggestionEngine engine;

	private SelectionChangeProcessor listener = new SelectionChangeProcessor();
	//private EditorProcessor listener2 = new EditorProcessor();

	public void createPartControl(Composite parent) {
		setViewer(new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL));
		
		Tree tree = viewer.getTree();
		
		tree.addListener(SWT.MeasureItem, new Listener() {
			   public void handleEvent(Event event) {
			      // height cannot be per row so simply set
			      event.height = 22;
			   }
			});
		
		FontRegistry fr = JFaceResources.getFontRegistry();
		
		ColumnViewerToolTipSupport.enableFor(viewer);
		getViewer().setContentProvider(new SuggestionContentProvider());
		getViewer().setLabelProvider(new SuggestionLabelProvider(fr));
		
		 // Expand the tree
	    getViewer().setAutoExpandLevel(2);
	    
	    
	    // Provide the input to the ContentProvider
	    //viewer.setInput(getInstructions());
	    
	    // Create a suggestion Engine with the lifeCycle of the view
		//engine = new BPMNSuggestionEngine2();
		
		// Register Diagram Selection Listener
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
	
		IEditorPart e = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		BPMN2MultiPageEditor editor = (BPMN2MultiPageEditor) e;
		//DiagramEditPart part =(DiagramEditPart) editor.getEditorInput();
		//editor.addPropertyListener(new ModelElementCreationProcessor());
		//editor.getBpmnDiagram(editor.getActivePage()).eAdapters().add(new ModelElementCreationProcessor2() );
		
		
		/*TreeIterator<EObject> eAllContents1 = editor.getBpmnDiagram(0).eAllContents();
		boolean listenerAdded = false;
		while (eAllContents1.hasNext() && !listenerAdded) {
			   EObject next = eAllContents1.next(); 
			   if(next instanceof BPMNShapeImpl){
				   BPMNShapeImpl shape = (BPMNShapeImpl) next;
				   if(shape.getBpmnElement() instanceof TaskImpl){
					   shape.getBpmnElement().eContainer().eAdapters().add(new ModelElementCreationProcessor2());
					   System.out.println("deletioncreationlistener added to: " + shape.getBpmnElement().eContainer().toString());
					   listenerAdded = true;
				   }
			   }
		   }*/
		
		TreeIterator<EObject> eAllContents = editor.getBpmnDiagram(0).eContainer().eAllContents();
		   while (eAllContents.hasNext()) {
			   EObject next = eAllContents.next();
			   //System.out.println(next);
			   if( next instanceof ProcessImpl){
				   ProcessImpl aProcess = (ProcessImpl) next;
				   addCreateDeletionProcessor(aProcess);
				   
			   } 
			   
			   if( next instanceof ParticipantImpl){
				   ParticipantImpl aProcess = (ParticipantImpl) next;
				   addCreateDeletionProcessor(aProcess);
				   
			   } 
			   
			   if( next instanceof BPMNPlaneImpl){
				   BPMNPlaneImpl shape = (BPMNPlaneImpl) next;
				   addCreateDeletionProcessor(shape.getBpmnElement());
				   
			   } 
			   if( next instanceof BPMNShapeImpl){
				   BPMNShapeImpl shape = (BPMNShapeImpl) next;
				   addCreateDeletionProcessor(shape.getBpmnElement());
				   
			   }
			   else if(next instanceof BPMNEdgeImpl){
				   BPMNEdgeImpl edge = (BPMNEdgeImpl) next;
				   addCreateDeletionProcessor(edge.getBpmnElement());
			   }
		   }
		   
		   /*EList<EObject> objects = editor.getBpmnDiagram(0).eContainer().eContents();
		   for(EObject eObject : objects){
			   System.out.println("EOBJECT: " + eObject.toString());
		   }*/
			
			
		


		
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
		//getSite().getPage().removeSelectionListener(listener);
		
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


	public BPMNSuggestionEngine getEngine() {
		return engine;
	}
	
	public void setEngine(BPMNSuggestionEngine engine){
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
	
	private void addCreateDeletionProcessor(BaseElement base) {
		if (!containsModelElementCreationListener(base)) {
			base.eAdapters().add(new ModelElementCreationProcessor2());
			System.out.println("deletioncreationlistener added to: " + base.getId());
		}	
	}
	
	private boolean containsModelElementCreationListener(BaseElement base) {
		for (Adapter ad : base.eAdapters()) {
			if (ad instanceof ModelElementCreationProcessor2) {
				return true;
			}
		}
		return false;
	}
}