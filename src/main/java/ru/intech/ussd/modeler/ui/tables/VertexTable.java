package ru.intech.ussd.modeler.ui.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.graph.Graph;

public class VertexTable extends AbstractCellEditor implements TableModel, TableCellRenderer, TableCellEditor, ActionListener {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final long serialVersionUID = 1L;
	private static final String EDIT = "edit";
	private static final String []COLUMN_NAMES = {"Status", "Id", "Description", "Function", "Color"};
	private static final Class<?> []COLUMN_CLASSES = {String.class, Integer.class, String.class, String.class, Color.class};

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private Graph<Vertex, Unit<Edge>> graph;
	private List<VertexRoom> vertexes;
	private JLabel lbl = createColorLabel();
	private Border selBorder;
	private Border unselBorder;

	private Color editColor;
	private JButton button;
	private JColorChooser colorChooser;
	private JDialog dialog;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public VertexTable() {
	}

	public VertexTable(Graph<Vertex, Unit<Edge>> graph) {
		setGraph(graph);
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	@Override
	public int getRowCount() {
		return vertexes == null ? 0 : vertexes.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUMN_NAMES[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COLUMN_CLASSES[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
//		return false;
		return columnIndex > 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		VertexRoom v = vertexes.get(rowIndex);
		switch (columnIndex) {
			case 0 :
				return "stat";
			case 1 :
				return v.getRoom().getId();
			case 2 :
				return v.getDescription();
			case 3 :
				return v.getFunction();
			case 4 :
				return v.getAttribute().getColor();
			default :
				return "unknown";
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		VertexRoom v = vertexes.get(rowIndex);
		switch (columnIndex) {
			case 2 :
				v.setDescription((String) aValue);
				break;
			case 3 :
				v.setFunction((String) aValue);
				break;
			case 4 :
				v.getAttribute().setColor((Color) aValue);
				break;
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (!(value instanceof Color)) {
			return null;
		}

		Border b = null;
		if (!hasFocus) {
			if (isSelected) {
				if (selBorder == null) {
					selBorder = BorderFactory.createLineBorder(table.getSelectionBackground(), 3);
				}
				b = selBorder;
			} else {
				if (unselBorder == null) {
					unselBorder = BorderFactory.createLineBorder(Color.WHITE, 3);
				}
				b = unselBorder;
			}
		}
		lbl.setBackground((Color) value);
		lbl.setBorder(b);
		return lbl;
	}

    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so bring up the dialog.
            button.setBackground(editColor);
            colorChooser.setColor(editColor);
            dialog.setVisible(true);

            fireEditingStopped(); //Make the renderer reappear.

        } else { //User pressed dialog's "OK" button.
        	editColor = colorChooser.getColor();
        }
    }

	@Override
	public Object getCellEditorValue() {
		return editColor;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		editColor = (Color)value;
		return button;
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public Graph<Vertex, Unit<Edge>> getGraph() {
		return graph;
	}

	public void setGraph(Graph<Vertex, Unit<Edge>> graph) {
		this.graph = graph;
		extractVertexesFromGraph(graph);
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public static JTable createSwingTable(Graph<Vertex, Unit<Edge>> graph) {
		VertexTable vt = new VertexTable(graph);
		JTable tbl = new JTable(vt);
		vt.initColorDialog();
		tbl.getColumnModel().getColumn(0).setMaxWidth(50);
		tbl.getColumnModel().getColumn(1).setMaxWidth(75);
		tbl.getColumnModel().getColumn(4).setMaxWidth(50);
		tbl.getColumnModel().getColumn(4).setCellRenderer(vt);
		tbl.getColumnModel().getColumn(4).setCellEditor(vt);
		return tbl;
	}

	private void extractVertexesFromGraph(Graph<Vertex, Unit<Edge>> graph) {
		List<VertexRoom> lvr = new ArrayList<VertexRoom>();
		if ((graph != null) && (graph.getVertexCount() > 0)) {
			for (Vertex v : graph.getVertices()) {
				if (v instanceof VertexRoom) {
					lvr.add((VertexRoom) v);
				}
			}
		}
		vertexes = lvr;
	}

	private JLabel createColorLabel() {
		JLabel lbl = new JLabel();
		lbl.setOpaque(true);
		return lbl;
	}

	public void initColorDialog() {
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);

        //Set up the dialog that the button brings up.
        colorChooser = new JColorChooser();
        dialog = JColorChooser.createDialog(button,
                                        "Pick a Color",
                                        true,  //modal
                                        colorChooser,
                                        this,  //OK button handler
                                        null); //no CANCEL button handler
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
