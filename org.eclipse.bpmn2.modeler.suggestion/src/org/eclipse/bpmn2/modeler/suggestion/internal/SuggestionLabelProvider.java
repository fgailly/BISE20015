package org.eclipse.bpmn2.modeler.suggestion.internal;

import java.net.URL;

import org.eclipse.bpmn2.modeler.suggestion.algorithm.Suggestion;
import org.eclipse.bpmn2.modeler.suggestion.algorithm.Suggestion.Type;
import org.eclipse.bpmn2.modeler.suggestion.part.OntologyCategory;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class SuggestionLabelProvider extends LabelProvider {

	private static final Image CLASS = getImage("class.gif");
	private static final Image DATATYPE = getImage("datatype.gif");
	private static final Image DELETE = getImage("delete.gif");

	@Override
	  public String getText(Object element) {
	    if (element instanceof OntologyCategory) {
	    	OntologyCategory category = (OntologyCategory) element;
	    	if(category.getName().equals("Delete")) {
	    		return "Delete the assigned annotation";
	    	}
	    	return category.getName();
	    }
	    return ((Suggestion) element).getSuggestionString(); // + " " + ((Double)((Suggestion)element).getWeight()).toString();
	  }
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof OntologyCategory) {	
			if(((OntologyCategory)element).getName().equals("Delete")) {
	    		return DELETE;
	    	}
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		else if (element instanceof Suggestion) {
			Suggestion sug = (Suggestion) element;
			Type type = sug.getType();
			if (sug.getWeight() == Double.MAX_VALUE) {
				return DELETE;
			}
			switch (type) {

			case Class:
				return CLASS;
			case Datatype:
				return DATATYPE;
			default:
				return null;
			}
		}
		return null;
	}

	private static Image getImage(String file) {
		Bundle bundle = FrameworkUtil.getBundle(SuggestionLabelProvider.class);
		URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();

	} 


}
