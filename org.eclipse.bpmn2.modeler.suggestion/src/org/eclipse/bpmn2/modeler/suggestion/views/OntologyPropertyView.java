package org.eclipse.bpmn2.modeler.suggestion.views;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.bpmn2.modeler.suggestion.algorithm.Suggestion;
import org.eclipse.bpmn2.modeler.suggestion.internal.OntologyPropertyColumn;
import org.eclipse.bpmn2.modeler.suggestion.internal.OntologyValueColumn;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


public class OntologyPropertyView extends ViewPart {

	private TableViewer tableViewer;
	private ISelectionListener selectionListener = new ISelectionListener() {
		
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection sel) {

			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IViewPart sugView = activePage.findView(SuggestionView.ID);
			IViewPart ontview = activePage.findView(OntologyView.ID);
			if (part == sugView || part == ontview) {
				Object selected = ((IStructuredSelection) sel).getFirstElement();
				if (selected instanceof Suggestion && selected != null) {
					Suggestion sug = (Suggestion) selected;
					if(sug.getType() != null){
						String[] input = createPropertyArray(sug);
						tableViewer.setInput(input);
					}
				}

			}

		}

	};	

	protected String[] createPropertyArray(Suggestion sug) {
		
		ArrayList<String> properties = new ArrayList<String>();
		if (sug.getType().toString() == "Datatype") {
			properties.add("Class" + "#" + sug.getClasses());
		}
		properties.add("Description" + "#" + sug.getDescription());
		properties.add("Weight" + "#" + ((Double)sug.getWeight()).toString());
		properties.add("Weight ConstructMatching" + "#" + ((Double)sug.getWeightConstructMatching()).toString());
		properties.add("Weight LocationMatching" + "#" + ((Double)sug.getWeightLocationMechanism()).toString());
		properties.add("Weight WordnetSynonyms" + "#" + ((Double)sug.getWeightWordnetSynonyms()).toString());
		properties.add("Weight TextMatching" + "#" + ((Double)sug.getWeightTextMatching()).toString());
		String[] input = Arrays.copyOf(properties.toArray(), properties.toArray().length, String[].class);
		return input;
	}

	@Override
	public void createPartControl(Composite parent) {
		tableViewer = new TableViewer(parent,SWT.H_SCROLL|SWT.V_SCROLL);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		new OntologyPropertyColumn().addColumnTo(tableViewer);
		new OntologyValueColumn().addColumnTo(tableViewer);
		
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
		
	}

	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}
	
	public void dispose() {
		if (selectionListener != null) {
       	 getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
       	 selectionListener = null;
        }
	}

}
