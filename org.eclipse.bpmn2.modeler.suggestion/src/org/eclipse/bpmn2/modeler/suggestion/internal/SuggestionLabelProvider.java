package org.eclipse.bpmn2.modeler.suggestion.internal;

import java.net.URL;

import org.eclipse.bpmn2.modeler.suggestion.part.OntologyCategory;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import ugent.mis.cmoeplus.Recommendation;
import ugent.mis.cmoeplus.Recommendation.Type;

public class SuggestionLabelProvider extends ColumnLabelProvider implements IFontProvider {

	private static final Image CLASS = getImage("class.gif");
	private static final Image DATATYPE = getImage("datatype.gif");
	private static final Image DELETE = getImage("delete.gif");
	private final FontRegistry fr;
	public SuggestionLabelProvider(FontRegistry fr){
		this.fr = fr;
		}

	@Override
	  public String getText(Object element) {
	    if (element instanceof OntologyCategory) {
	    	OntologyCategory category = (OntologyCategory) element;
	    	if(category.getName().equals("Delete")) {
	    		return "Delete the assigned annotation";
	    	}
	    	return category.getName();
	    }
	    return ((Recommendation) element).getSuggestionString(); // + " " + ((Double)((Suggestion)element).getWeight()).toString();
	  }
	
	public Point getToolTipShift(Object object) {
		return new Point(5,5);
	}

	@Override
	public int getToolTipDisplayDelayTime(Object object) {
		return 1000;
	}
	
	@Override
	public int getToolTipTimeDisplayed(Object object) {
		return 5000;
	}
	
	@Override
	public String getToolTipText(Object element) {
		// TODO Auto-generated method stub
		if (element instanceof OntologyCategory) {
	    	OntologyCategory category = (OntologyCategory) element;
	    	if(category.getName().equals("Delete")) {
	    		return "Delete the assigned annotation";
	    	}
	    	return category.getName();
	    }
	    return ((Recommendation) element).getDescription(); // + " " + ((Double)((Suggestion)element).getWeight()).toString();
	}





	@Override
	public Image getImage(Object element) {
		if (element instanceof OntologyCategory) {	
			if(((OntologyCategory)element).getName().equals("Delete")) {
	    		return DELETE;
	    	}
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		else if (element instanceof Recommendation) {
			Recommendation sug = (Recommendation) element;
			Type type = sug.getType();
			if (sug.getScore() == Double.MAX_VALUE) {
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
	
	public Font getFont(Object element) {
		fr.put("text", new FontData[]{new FontData("Arial", 12, SWT.NORMAL)} );
        Font text = fr.get("text");
        return text;
    }

	private static Image getImage(String file) {
		Bundle bundle = FrameworkUtil.getBundle(SuggestionLabelProvider.class);
		URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();

	}



}
