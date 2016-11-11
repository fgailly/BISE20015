package org.eclipse.bpmn2.modeler.suggestion.views;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.bpmn2.modeler.suggestion.internal.OntologyPropertyColumn;
import org.eclipse.bpmn2.modeler.suggestion.internal.OntologyValueColumn;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ugent.mis.cmoeplus.Recommendation;


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
				if (selected instanceof Recommendation && selected != null) {
					Recommendation sug = (Recommendation) selected;
					if(sug.getType() != null){
						String[] input = createPropertyArray(sug);
						tableViewer.setInput(input);
					}
				}

			}

		}

	};	

	protected String[] createPropertyArray(Recommendation sug) {
		
		ArrayList<String> properties = new ArrayList<String>();
		if (sug.getType().toString() == "Datatype") {
			properties.add("Class" + "#" + sug.getClasses());
		}
		properties.add("Description" + "#" + sug.getDescription());
		properties.add("Weight" + "#" + ((Double)sug.getScore()).toString());
		properties.add("Weight Model Language RS" + "#" + ((Double)sug.getScoreModelLanguageRecommendationService()).toString());
		properties.add("Weight Rule-based RS" + "#" + ((Double)sug.getScoreRuleBasedRecommendationService()).toString());
		//properties.add("Weight WordnetSynonyms" + "#" + ((Double)sug.getWeightWordnetSynonyms()).toString());
		properties.add("Weight Label-based RS" + "#" + ((Double)sug.getScoreLabelBasedRecommendationService()).toString());
		String[] input = Arrays.copyOf(properties.toArray(), properties.toArray().length, String[].class);
		return input;
	}

	@Override
	public void createPartControl(Composite parent) {
		tableViewer = new TableViewer(parent,SWT.H_SCROLL|SWT.V_SCROLL);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		FontRegistry fr = JFaceResources.getFontRegistry();
		new OntologyPropertyColumn(fr).addColumnTo(tableViewer);
		new OntologyValueColumn(fr).addColumnTo(tableViewer);
		
		Table table = tableViewer.getTable();
		
		table.addListener(SWT.MeasureItem, new Listener() {
			   public void handleEvent(Event event) {
			      // height cannot be per row so simply set
			      event.height = 28;
			   }
			});
		
		
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
