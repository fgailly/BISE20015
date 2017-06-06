package org.eclipse.bpmn2.modeler.suggestion.internal;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.TableColumn;

public abstract class OntologyColumn extends ColumnLabelProvider implements IFontProvider {
	private final FontRegistry fr;

	public OntologyColumn(FontRegistry fr){
		this.fr = fr;
		}
	
	public abstract String getText(Object element);
	
	public Font getFont(Object element) {
		fr.put("text", new FontData[]{new FontData("Arial", 14, SWT.NORMAL)} );
        Font text = fr.get("text");
        return text;
    }

	public abstract String getTitle();
	
	public int getWidth() {
		return 250; 
	}
	
	public TableViewerColumn addColumnTo(TableViewer viewer) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer,SWT.NONE);
		TableColumn column = tableViewerColumn.getColumn();	
		column.setMoveable(true);
        column.setResizable(true);
        column.setText(getTitle());
        column.setWidth(getWidth());
        tableViewerColumn.setLabelProvider(this);
        return tableViewerColumn;
	}
}
