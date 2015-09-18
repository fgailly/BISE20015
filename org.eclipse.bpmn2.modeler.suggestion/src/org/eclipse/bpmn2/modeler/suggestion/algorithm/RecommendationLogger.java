package org.eclipse.bpmn2.modeler.suggestion.algorithm;

import org.cheetahplatform.common.logging.AbstractPromLogger;
import org.cheetahplatform.common.logging.AuditTrailEntry;
import org.cheetahplatform.common.logging.Process;
import org.cheetahplatform.common.logging.ProcessInstance;
import org.eclipse.core.runtime.IStatus;


public class RecommendationLogger extends AbstractPromLogger {
	
	String processId;
	
	public RecommendationLogger(){
		
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getProcessInstanceId() {
		// TODO Auto-generated method stub
		return this.processId;
	}

	@Override
	protected IStatus doAppend(AuditTrailEntry entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IStatus doAppend(Process process, ProcessInstance instance) {
		// TODO Auto-generated method stub
		return null;
	}

}
