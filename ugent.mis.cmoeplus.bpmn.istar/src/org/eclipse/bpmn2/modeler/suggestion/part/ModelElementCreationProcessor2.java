package org.eclipse.bpmn2.modeler.suggestion.part;



import java.io.IOException;
import java.util.Collection;

import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.InteractionNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.impl.BPMNEdgeImpl;
import org.eclipse.bpmn2.di.impl.BPMNShapeImpl;
import org.eclipse.bpmn2.impl.CollaborationImpl;
import org.eclipse.bpmn2.impl.DataObjectImpl;
import org.eclipse.bpmn2.impl.EventImpl;
import org.eclipse.bpmn2.impl.ExclusiveGatewayImpl;
import org.eclipse.bpmn2.impl.FlowNodeImpl;
import org.eclipse.bpmn2.impl.LaneImpl;
import org.eclipse.bpmn2.impl.MessageFlowImpl;
import org.eclipse.bpmn2.impl.ParticipantImpl;
import org.eclipse.bpmn2.impl.SequenceFlowImpl;
import org.eclipse.bpmn2.impl.StartEventImpl;
import org.eclipse.bpmn2.impl.TaskImpl;
import org.eclipse.bpmn2.impl.ProcessImpl;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.notify.impl.NotifierImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.internal.parts.ConnectionEditPart;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.graphiti.ui.internal.parts.DiagramEditPart;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import ugent.mis.cmoeplus.bpmn.istar.views.OntologyView;
import ugent.mis.cmoeplus.bpmn.istar.views.SuggestionView;


public class ModelElementCreationProcessor2 extends AdapterImpl{
	
	private ViewPart viewer;
	public boolean suggestions;
	private EObject selection;
	private String id;
	private String name;
	private String type;
	private String uniqueId;
	private String bpmnUrl = "http://www.mis.ugent.be/ontologies/bpmn.owl#";
	private String owlUrl = "http://www.mis.ugent.be/ontologies/MedicalOntology#";

	
	private boolean findView() {
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
	
	@SuppressWarnings("restriction")
	private void findEditpart(Object object){
		if (object instanceof BPMNEdgeImpl) {
						//diagramElement = (BPMNEdge) object;
			BPMNEdgeImpl edge = (BPMNEdgeImpl) object;
			System.out.println("Model element: " + edge.getBpmnElement());
			selection = edge.getBpmnElement();
			System.out.println(castElement());
			
		} else if (object instanceof BPMNShapeImpl) {
			BPMNShapeImpl shape = (BPMNShapeImpl) object;
			System.out.println("Model element: " + shape.getBpmnElement());
			selection = shape.getBpmnElement();
			//System.out.println(selection);
			selection = shape.getBpmnElement();
			System.out.println(castElement());
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
			
	
	
	@Override
	public void notifyChanged(Notification msg) {
		findView();
		
		System.out.println("MESSAGE:" + msg.toString());
		
		
		if(msg.getEventType() == Notification.REMOVE && (msg.getOldValue() instanceof TaskImpl)){
			
			
			TaskImpl task = (TaskImpl) msg.getOldValue();
			System.out.println("Task deleted " + task.getId());
			selection = task;
			IRI modelIRI = ((SuggestionView)viewer).getEngine().getManager().removeModelInstance(task.getId());
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			
			
		}
		else if(msg.getEventType() == Notification.REMOVE && (msg.getOldValue() instanceof SequenceFlowImpl)){
			
			
			SequenceFlowImpl sequenceFlow = (SequenceFlowImpl) msg.getOldValue();
			System.out.println("Sequence flow deleted " + sequenceFlow.getId());
			selection = sequenceFlow;
			IRI modelIRI = ((SuggestionView)viewer).getEngine().getManager().removeModelInstance(sequenceFlow.getId());
			
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			
			//System.out.println(sf.getName());
			//System.out.println(castElement());
			
		}
		
		
		else if(msg.getEventType() == Notification.SET && (msg.getOldValue() instanceof TaskImpl)){
			
			
			TaskImpl task = (TaskImpl) msg.getOldValue();
			System.out.println("Change properties Task " + task.getId());
			selection = task;
			
			
			//System.out.println(sf.getName());
			//System.out.println(castElement());
			
		}
		
		
		else if(msg.getEventType() == Notification.ADD && (msg.getNewValue() instanceof TaskImpl)){
			
			
			TaskImpl task = (TaskImpl) msg.getNewValue();
			System.out.println("Task added " + task.getId());
			selection = task;
			
			
			
			//List<Participant> participants = col.getParticipants();
			castElement();
			String constructIRI = createBpmnUrl();
			IRI modelIRI = ((SuggestionView)viewer).getEngine().getManager().addModelInstance(constructIRI, task.getId(), task.getName());
			if(msg.getNotifier() instanceof ProcessImpl){
				ProcessImpl process = (ProcessImpl) msg.getNotifier();
				((SuggestionView)viewer).getEngine().getManager().addModelRelationship("http://www.mis.ugent.be/ontologies/bpmn.owl#isLocatedWithin", task.getId(), process.getId());
			}
			if(msg.getNotifier() instanceof LaneImpl){
				LaneImpl lane = (LaneImpl) msg.getNotifier();
				((SuggestionView)viewer).getEngine().getManager().addModelRelationship("http://www.mis.ugent.be/ontologies/bpmn.owl#isLocatedWithin", task.getId(), lane.getId());
			}
			
			System.out.println(modelIRI.toString());
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			//System.out.println(castElement());
			
		}
		
		else if(msg.getEventType() == Notification.ADD && (msg.getNewValue() instanceof ExclusiveGatewayImpl)){
			
			
			ExclusiveGatewayImpl gateway = (ExclusiveGatewayImpl) msg.getNewValue();
			System.out.println("Gateway added " + gateway.getId());
			selection = gateway;
			//castElement();
			String constructIRI = "http://www.mis.ugent.be/ontologies/bpmn.owl#ExclusiveGateway";
			IRI modelIRI = ((SuggestionView)viewer).getEngine().getManager().addModelInstance(constructIRI, gateway.getId(), gateway.getId());
			System.out.println(modelIRI.toString());
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			//System.out.println(castElement());
			
		}
		
		else if(msg.getEventType() == Notification.REMOVE && (msg.getOldValue() instanceof ExclusiveGatewayImpl)){
			
			
			ExclusiveGatewayImpl gateway = (ExclusiveGatewayImpl) msg.getOldValue();
			System.out.println("Gateway removed " + gateway.getId());
			selection = gateway;
			//castElement();
			IRI modelIRI = ((SuggestionView)viewer).getEngine().getManager().removeModelInstance(gateway.getId());
			
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			//System.out.println(castElement());
			
		}

		else if(msg.getEventType() == Notification.ADD && (msg.getNewValue() instanceof EventImpl)){
			
			
			EventImpl event = (EventImpl) msg.getNewValue();
			System.out.println("Event added " + event.getId());
			selection = event;
			//castElement();
			String constructIRI = "http://www.mis.ugent.be/ontologies/bpmn.owl#Event";
			IRI modelIRI = ((SuggestionView)viewer).getEngine().getManager().addModelInstance(constructIRI, event.getId(), event.getId());
			System.out.println(modelIRI.toString());
			if(msg.getNotifier() instanceof ProcessImpl){
				ProcessImpl process = (ProcessImpl) msg.getNotifier();
				((SuggestionView)viewer).getEngine().getManager().addModelRelationship("http://www.mis.ugent.be/ontologies/bpmn.owl#isLocatedWithin", event.getId(), process.getId());
			}
			if(msg.getNotifier() instanceof LaneImpl){
				LaneImpl lane = (LaneImpl) msg.getNotifier();
				((SuggestionView)viewer).getEngine().getManager().addModelRelationship("http://www.mis.ugent.be/ontologies/bpmn.owl#isLocatedWithin", event.getId(), lane.getId());
			}
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			//System.out.println(castElement());
			
		}
		
		else if(msg.getEventType() == Notification.REMOVE && (msg.getOldValue() instanceof EventImpl)){
			
			
			EventImpl event = (EventImpl) msg.getOldValue();
			System.out.println("Event removed " + event.getId());
			selection = event;
			//castElement();
			IRI modelIRI = ((SuggestionView)viewer).getEngine().getManager().removeModelInstance(event.getId());
			
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			//System.out.println(castElement());
			
		}
		else if(msg.getEventType() == Notification.ADD && (msg.getNewValue() instanceof SequenceFlowImpl)){
			
			
			SequenceFlowImpl seq = (SequenceFlowImpl) msg.getNewValue();
			System.out.println("Sequence flow added " + seq.getId());
			if(!this.containsModelElementCreationListener(seq))
				seq.eAdapters().add(new ModelElementCreationProcessor2());
			
			
			selection = seq;
			castElement();
			String constructIRI = createBpmnUrl();
			//System.out.println("TARGET" +target.getId());
			IRI sequenceIRI = ((SuggestionView)viewer).getEngine().getManager().addModelInstance(constructIRI, seq.getId(), seq.getId());
			//((SuggestionView)viewer).getEngine().getManager().addModelRelationship("http://www.mis.ugent.be/ontologies/bpmn.owl#hasSource", seq.getId(), source.getId());
			//((SuggestionView)viewer).getEngine().getManager().addModelRelationship("http://www.mis.ugent.be/ontologies/bpmn.owl#hasTarget", seq.getId(), target.getId());
			
			System.out.println(sequenceIRI.toString());
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
				System.out.println("Save sequence flow");
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			
			
		}
		else if(msg.getNotifier() instanceof SequenceFlowImpl && msg.getEventType() == Notification.SET && (msg.getNewValue() instanceof FlowNodeImpl)){
			
			
			SequenceFlowImpl seq = (SequenceFlowImpl) msg.getNotifier();
			EReferenceImpl feature = (EReferenceImpl) msg.getFeature();
			if(feature.getName().equals("sourceRef")){
				System.out.println("Source seqflow added " + seq.getId());
				FlowNode source = (FlowNodeImpl)msg.getNewValue();
				((SuggestionView)viewer).getEngine().getManager().addModelRelationship("http://www.mis.ugent.be/ontologies/bpmn.owl#hasSource", seq.getId(), source.getId());
			}
			else{
				System.out.println("Target seqflow added " + seq.getId());
				FlowNode target = (FlowNodeImpl)msg.getNewValue();
				((SuggestionView)viewer).getEngine().getManager().addModelRelationship("http://www.mis.ugent.be/ontologies/bpmn.owl#hasTarget", seq.getId(), target.getId());
			}
			
			
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
				System.out.println("Save sequence flow");
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			
			
		}
		else if(msg.getEventType() == Notification.ADD && (msg.getNewValue() instanceof MessageFlowImpl)){
			
			
			MessageFlowImpl mes = (MessageFlowImpl) msg.getNewValue();
			System.out.println("Message flow added " + mes.getId());
			InteractionNode source = mes.getSourceRef();
			
			//FlowNode target = seq.getTargetRef();
			
			selection = mes;
			castElement();
			String constructIRI = createBpmnUrl();
			//System.out.println(target.getId());
			IRI sequenceIRI = ((SuggestionView)viewer).getEngine().getManager().addModelInstance(constructIRI, mes.getId(), mes.getId());
			((SuggestionView)viewer).getEngine().getManager().addModelRelationship("http://www.mis.ugent.be/ontologies/bpmn.owl#hasSource", mes.getId(), mes.getId());
			//((SuggestionView)viewer).getEngine().getManager().addModelRelationship("http://www.mis.ugent.be/ontologies/bpmn.owl#hasTarget", seq.getId(), target.getId());
			
			System.out.println(sequenceIRI.toString());
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			
		}
		else if(msg.getEventType() == Notification.REMOVE && (msg.getOldValue() instanceof MessageFlowImpl)){
			
			
			MessageFlowImpl mes = (MessageFlowImpl) msg.getOldValue();
			System.out.println("Gateway added " + mes.getId());
			selection = mes;
			//castElement();
			IRI modelIRI = ((SuggestionView)viewer).getEngine().getManager().removeModelInstance(mes.getId());
			
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			//System.out.println(castElement());
			
		}
		else if(msg.getEventType() == Notification.ADD && (msg.getNewValue() instanceof ParticipantImpl)){
			
			
			ParticipantImpl pool = (ParticipantImpl) msg.getNewValue();
			System.out.println("Participant added " + pool.getId());
			
			
			
			
		}
		
		else if(msg.getEventType() == Notification.SET && (msg.getNewValue() instanceof ProcessImpl)){
			
			
			ProcessImpl pool = (ProcessImpl) msg.getNewValue();
			System.out.println("Process added " + pool.getId());
			
			
			//FlowNode target = seq.getTargetRef();
			
			String constructIRI = "http://www.mis.ugent.be/ontologies/bpmn.owl#Pool";
			//System.out.println(target.getId());
			IRI sequenceIRI = ((SuggestionView)viewer).getEngine().getManager().addModelInstance(constructIRI, pool.getId(), pool.getId());
			
			System.out.println(sequenceIRI.toString());
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			
		}
		else if(msg.getEventType() == Notification.REMOVE && (msg.getOldValue() instanceof ProcessImpl)){
			
			
			ProcessImpl mes = (ProcessImpl) msg.getOldValue();
			System.out.println("Pool removed " + mes.getId());
			selection = mes;
			//castElement();
			IRI modelIRI = ((SuggestionView)viewer).getEngine().getManager().removeModelInstance(mes.getId());
			
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			//System.out.println(castElement());
			
		}
		
		else if(msg.getEventType() == Notification.ADD && (msg.getNewValue() instanceof LaneImpl)){
			
			
			LaneImpl lane = (LaneImpl) msg.getNewValue();
			System.out.println("Lane added " + lane.getId());
			LaneImpl pool = (LaneImpl) msg.getNotifier();
			
			
			//FlowNode target = seq.getTargetRef();
			
			String constructIRI = "http://www.mis.ugent.be/ontologies/bpmn.owl#Lane";
			System.out.println(lane.getId());
			IRI sequenceIRI = ((SuggestionView)viewer).getEngine().getManager().addModelInstance(constructIRI, lane.getId(), lane.getId());
			((SuggestionView)viewer).getEngine().getManager().addModelRelationship("http://www.mis.ugent.be/ontologies/bpmn.owl#hasLane", lane.getId(), pool.getId());
			
			
			System.out.println(sequenceIRI.toString());
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			
		}
		else if(msg.getEventType() == Notification.REMOVE && (msg.getOldValue() instanceof LaneImpl)){
			
			
			LaneImpl mes = (LaneImpl) msg.getOldValue();
			System.out.println("Lane removed " + mes.getId());
			selection = mes;
			//castElement();
			IRI modelIRI = ((SuggestionView)viewer).getEngine().getManager().removeModelInstance(mes.getId());
			
			try {
				((SuggestionView)viewer).getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
			//System.out.println(castElement());
			
		}
		
			
			
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
				 return true;
			}
			else if (selection instanceof TaskImpl) {
				TaskImpl task = (TaskImpl) selection;
				setType(task.getId().split("_")[0]);
				setName(task.getName());
				setUniqueId(task.getId());
				setId("Task");
				return true;
			}
			else if (selection instanceof ParticipantImpl) {
				ParticipantImpl pool = (ParticipantImpl) selection;
				setType(pool.getId().split("_")[0]);
				setName(pool.getName());
				setUniqueId(pool.getId());
				setId("Pool");
				return true;
			}
				
			else if (selection instanceof LaneImpl) {
				LaneImpl lane = (LaneImpl) selection;
				setType(lane.getId().split("_")[0]);
				setName(lane.getName());
				setUniqueId(lane.getId());
				setId("Lane");
				return true;
			}
			else if (selection instanceof ExclusiveGatewayImpl) {
				ExclusiveGatewayImpl gateway = (ExclusiveGatewayImpl) selection;
				setType(gateway.getId().split("_")[0]);
				setName(gateway.getName());
				setUniqueId(gateway.getId());
				setId("Gateway");
				return true;
			} 
			else if (selection instanceof DataObjectImpl) {
				DataObjectImpl flow = (DataObjectImpl) selection;
				setType(flow.getId().split("_")[0]);
				setName(flow.getName());
				setUniqueId(flow.getId());
				setId("Data_Object");
				return true;
			}
			else if (selection instanceof MessageFlowImpl) {
				MessageFlowImpl flow = (MessageFlowImpl) selection;
				setType(flow.getId().split("_")[0]);
				setName(flow.getName());
				setUniqueId(flow.getId());
				setId("MessageFlow");
				return true;
			}
			else if (selection instanceof SequenceFlowImpl) {
				SequenceFlowImpl flow = (SequenceFlowImpl) selection;
				setType(flow.getId().split("_")[0]);
				setName(flow.getName());
				setUniqueId(flow.getId());
				setId("SequenceFlow");
				return true;
			}
			else {
				setType("unknown");
				setName(null);
				setId(null);
				return false;
			}		
		}
		uniqueId = "";
		return false;
		
	}

	public void setSuggestions(boolean suggestions) {
		this.suggestions = suggestions;
	}

	public void setSelection(EObject selection) {
		this.selection = selection;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	public String createBpmnUrl() {
		return this.bpmnUrl + getConstructId();
	}

	public EObject getSelection() {
		return selection;
	}


	public String getConstructId() {
		return id;
	}
	
	private boolean containsModelElementCreationListener(EObject eobject) {
		for (Adapter ad : eobject.eAdapters()) {
			if (ad instanceof ModelElementCreationProcessor2) {
				return true;
			}
		}
		return false;
	}
	
	

}
