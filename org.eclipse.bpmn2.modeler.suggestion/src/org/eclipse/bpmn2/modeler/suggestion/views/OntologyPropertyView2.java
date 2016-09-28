package org.eclipse.bpmn2.modeler.suggestion.views;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.bpmn2.modeler.suggestion.algorithm.Suggestion;
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
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


public class OntologyPropertyView2 extends ViewPart {

	
	private Suggestion suggestion;
	private Label label;
	private Group txtBox;
	private StyledText styledText;
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
						//String[] input = createPropertyArray(sug);
						//tableViewer.setInput(input);
						suggestion = sug;
						
						//label.setText(suggestion.getDescription());
						//label.setBounds(120, 10, 100, 100);
						//label.pack();
						styledText.setText(suggestion.getDescription());
						FontRegistry fr = JFaceResources.getFontRegistry();
						fr.put("text", new FontData[]{new FontData("Arial", 14, SWT.NORMAL)} );
				        Font text = fr.get("text");
						styledText.setFont(text);
						//txtBox.pack();
						styledText.pack();
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
		
		txtBox = new Group(parent, 0);
		//txtBox.setSize(300, 300);
		txtBox.setText("Description");
	
		
		//label = new Label(txtBox, SWT.WRAP | SWT.BORDER | SWT.LEFT);
		styledText = new StyledText(txtBox, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		//styledText.setBounds(10, 10, 400, 400);
		//label.setSize(500, 500);
		//final GridData data = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
		//label.setLayoutData(data); 
		//styledText.setWordWrap(true);
		styledText.setText("This panel contains a description\n of the ontology element selected\n in the suggestion panel.");
		styledText.pack();
		
        
		
		
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
		
	}

	
	
	public void dispose() {
		if (selectionListener != null) {
       	 getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
       	 selectionListener = null;
        }
	}

	@Override
	public void setFocus() {
		label.setFocus();		
	}

}
