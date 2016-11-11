package org.eclipse.bpmn2.modeler.suggestion.part;

import org.eclipse.bpmn2.impl.DataObjectImpl;
import org.eclipse.bpmn2.impl.EventImpl;
import org.eclipse.bpmn2.impl.ExclusiveGatewayImpl;
import org.eclipse.bpmn2.impl.LaneImpl;
import org.eclipse.bpmn2.impl.MessageFlowImpl;
import org.eclipse.bpmn2.impl.ParticipantImpl;
import org.eclipse.bpmn2.impl.SubProcessImpl;
import org.eclipse.bpmn2.impl.TaskImpl;
import org.eclipse.bpmn2.modeler.suggestion.algorithm.BPMNSuggestionEngine2;
import org.eclipse.bpmn2.modeler.suggestion.algorithm.BPMNSuggestionEngine3;
import org.eclipse.bpmn2.modeler.suggestion.views.OntologyView;
import org.eclipse.bpmn2.modeler.suggestion.views.SuggestionView;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.internal.parts.ConnectionEditPart;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.graphiti.ui.internal.parts.DiagramEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;

@SuppressWarnings("restriction")
public class SelectionChangeProcessor implements ISelectionListener {
	
	private ViewPart viewer;
	private Boolean suggestions;
	private EObject selection;
	private String id;
	private String name;
	private String type;
	private String uniqueId;
	public Boolean annotated;
	private IFigure fig;
	private ContainerShapeEditPart editPart;
	private ConnectionEditPart connectionPart;
	private String father;
	

	private String bpmnUrl = "http://www.mis.ugent.be/ontologies/bpmn.owl#";

	private Boolean findView() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = activePage.findView(SuggestionView.ID);
		if (view == null) {
			view = activePage.findView(OntologyView.ID);
			viewer = (OntologyView) view;
			this.suggestions = false;
			return false;
		} else {
			this.suggestions = true;
			viewer = (SuggestionView) view;
			return true;
		}		
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		findView();
		if (suggestions) {
			SuggestionView sugView = (SuggestionView)viewer;
			if(sugView.getEngine()==null){
				sugView.setEngine(new BPMNSuggestionEngine3());
			}
		}
		System.gc();
			
		
		if (part instanceof EditorPart) {
			if (sel instanceof IStructuredSelection) {
				IStructuredSelection ss = (IStructuredSelection) sel;
				Object o = ss.getFirstElement();

				if ((o instanceof ContainerShapeEditPart || o instanceof ConnectionEditPart) && !(o instanceof DiagramEditPart)) {
					if (o instanceof ContainerShapeEditPart) {
						editPart = (ContainerShapeEditPart) o;
						IFeatureProvider fp = editPart.getFeatureProvider();
						selection = (EObject) fp.getBusinessObjectForPictogramElement(editPart.getPictogramElement());
						fig = editPart.getFigure();
					} else if (o instanceof ConnectionEditPart) {
						connectionPart = (ConnectionEditPart) o;
						IFeatureProvider fp = connectionPart.getFeatureProvider();
						selection = (EObject) fp.getBusinessObjectForPictogramElement(connectionPart.getPictogramElement());
						fig = connectionPart.getFigure();
					}
					if(castElement()){
						// SUPPORTED TYPE
						addPropertyListener();
						if (suggestions) {
							((SuggestionView)viewer).setInput(new SuggestionModel());
							((SuggestionView) viewer).getViewer().getTree().setSelection(((SuggestionView) viewer).getViewer().getTree().getItem(0));
						}
						else {
							((OntologyView)viewer).setInput(new OntologyModel());
						}
					}
					else {
						// NOT SUPPORTED TYPE (SEQUENCE FLOW, MESSAGE FLOW)
						if (suggestions) {
							
							((SuggestionView)viewer).setInput(null);
						}
						else {
							((OntologyView)viewer).setInput(null);
						}
					}
				}
				else {
					// WEIRD SELECTION
					setUnknownProperties();
					if (suggestions) {
						((SuggestionView)viewer).setInput(null);
					}
					else {
						((OntologyView)viewer).setInput(null);
					}
				}       
			}
			else {
				// WEIRD SELECTION
				setUnknownProperties();
			}
		}
	}

	private void setUnknownProperties() {
		setType(null);
		setName(null);
		setId("Error");

	}

	private void addPropertyListener() {
		if (suggestions) {
			if ((selection != null) && !(getConstructId().equals("unknown"))) {
				if (!containsPropListener()) {
					selection.eAdapters().add(new PropertyChangeProcessor(this));
				}
			}
		}

	}

	private boolean containsPropListener() {
		for (Adapter ad : selection.eAdapters()) {
			if (ad instanceof PropertyChangeProcessor) {
				return true;
			}
		}
		return false;
	}

	private Boolean castElement() {
		if(selection != null) {
			
			//uniqueId = System.identityHashCode(selection);
			if (selection instanceof EventImpl) {
				 EventImpl event = (EventImpl) selection;
				 setType(event.getId().split("_")[0]);
				 setName(event.getName());
				 setUniqueId(event.getId());
				 setId("Event");
				 deriveFather();
				 return true;
			}
			else if (selection instanceof TaskImpl) {
				TaskImpl task = (TaskImpl) selection;
				setType(task.getId().split("_")[0]);
				setName(task.getName());
				setUniqueId(task.getId());
				setId("Task");
				deriveFather();
				return true;
				
				
			}
			else if (selection instanceof ParticipantImpl) {
				ParticipantImpl pool = (ParticipantImpl) selection;
				setType(pool.getId().split("_")[0]);
				setName(pool.getName());
				setUniqueId(pool.getId());
				setId("Pool");
				setFather("");
				return true;
			}
				
			else if (selection instanceof LaneImpl) {
				LaneImpl lane = (LaneImpl) selection;
				setType(lane.getId().split("_")[0]);
				setName(lane.getName());
				setUniqueId(lane.getId());
				setId("Lane");
				deriveFather();
				return true;
			}
			else if (selection instanceof ExclusiveGatewayImpl) {
				ExclusiveGatewayImpl gateway = (ExclusiveGatewayImpl) selection;
				setType(gateway.getId().split("_")[0]);
				setName(gateway.getName());
				setUniqueId(gateway.getId());
				setId("Gateway");
				deriveFather();
				return true;
			} 
			else if (selection instanceof DataObjectImpl) {
				DataObjectImpl flow = (DataObjectImpl) selection;
				setType(flow.getId().split("_")[0]);
				setName(flow.getName());
				setUniqueId(flow.getId());
				setId("Data_Object");
				setFather("");
				return true;
			}
			else if (selection instanceof MessageFlowImpl) {
				MessageFlowImpl flow = (MessageFlowImpl) selection;
				setType(flow.getId().split("_")[0]);
				setName(flow.getName());
				setUniqueId(flow.getId());
				setId("MessageFlow");
				setFather("");
				return true;
			}
			else {
				setType("unknown");
				setName(null);
				setId(null);
				setFather("");
				return false;
			}		
		}
		uniqueId = "";
		return false;
		
	}
	

	private void setUniqueId(String id) {
		this.uniqueId = id;
	}
	
	private void deriveFather() {
		if (editPart.getParent() instanceof ContainerShapeEditPart) {
			ContainerShapeEditPart parent = (ContainerShapeEditPart) editPart.getParent();
			IFeatureProvider fp = editPart.getFeatureProvider();
			EObject father = (EObject) fp.getBusinessObjectForPictogramElement(parent.getPictogramElement());
			
			if (father instanceof ParticipantImpl) {
				setFather(((ParticipantImpl) father).getId());
			}
			else if (father instanceof LaneImpl) {
				setFather(((LaneImpl) father).getId());
			}
			else if (father instanceof SubProcessImpl) {
				setFather(((SubProcessImpl) father).getId());
			}
			
		}

		
	}

	public String createBpmnUrl() {
		return this.bpmnUrl + getConstructId();
	}

	public EObject getSelection() {
		return selection;
	}

	public void setSelection(EObject selection) {
		this.selection = selection;
	}

	public String getConstructId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null && !name.equals("")) {
			if (name.trim().substring(0,1).matches("@")) {
				this.annotated = true;
				this.name = name.substring(1);
			} else {
				this.annotated = false;
				this.name = name;
			}
		} else {
			this.name = name;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getAnnotated() {
		return annotated;
	}

	public void setAnnotated(boolean b) {
		this.annotated = b;
		
	}

	public String getUniqueId() {
		return uniqueId;
	}
	
	public String getFather() {
		return father;
	}

	public void setFather(String father) {
		this.father = father;
	}
	

}
