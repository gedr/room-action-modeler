package ru.intech.ussd.modeler.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.StringUtils;

import ru.intech.ussd.modeler.entities.Attribute;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.EdgeAction;
import ru.intech.ussd.modeler.graphobjects.EdgeFinish;
import ru.intech.ussd.modeler.graphobjects.EdgeStart;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
import ru.intech.ussd.modeler.graphobjects.VertexSpecial;
import ru.intech.ussd.modeler.graphobjects.VertexStart;

public class InfoPanel extends JPanel implements ActionListener {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final long serialVersionUID = 1L;

	private static final String EMPTY_FORM = "empty";
	private static final String VERTEX_FORM = "vf";
	private static final String EDGE_FORM = "ef";
	private static final String CHANGE_VERTEX_COLOR = "cvc";

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private JLabel lblId;
	private JLabel lblNameOrDescription;
	private JLabel lblFunctionOrKey;
	private JLabel lblIdValue;
	private JLabel lblActive;
	private JTextField txtNameOrDesription;
	private JTextField txtFunctionOrKey;
	private JButton btnColor;
	private JPanel vertexPanel;
	private JPanel edgePanel;
	private JCheckBox cbActive;
	private FixedSizeDocument fsd;
	private String currentForm;

	private Object editObject;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public InfoPanel() {
		setLayout(new CardLayout());
		createComponents();
		add(new JLabel("<html><h1><center>Не выбран элемент</center></h1></html>"), EMPTY_FORM);
		add(vertexPanel, VERTEX_FORM);
		add(edgePanel, EDGE_FORM);

		currentForm = EMPTY_FORM;
		((CardLayout) getLayout()).show(this, currentForm);
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	public void actionPerformed(ActionEvent e) {
		if (CHANGE_VERTEX_COLOR.equals(e.getActionCommand())) {
			Color clr = JColorChooser.showDialog(this, "Цвет вершины", ((JButton) e.getSource()).getBackground());
			if (clr != null) {
				btnColor.setBackground(clr);
			}
		}
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	private void createComponents() {
		vertexPanel = new JPanel(new MigLayout("", "[left][grow, fill][right]", "[][][]"));
		edgePanel = new JPanel(new MigLayout("", "[left][grow, fill, right]", "[][][][]"));
		lblActive = new JLabel("Активный путь");
		lblId = new JLabel("ID :");
		lblIdValue = new JLabel();
		lblNameOrDescription = new JLabel();
		lblFunctionOrKey = new JLabel();
		txtNameOrDesription = new JTextField();
		txtFunctionOrKey = new JTextField();
		fsd = new FixedSizeDocument();
		txtFunctionOrKey.setDocument(fsd);
		btnColor = new JButton();
		btnColor.setActionCommand(CHANGE_VERTEX_COLOR);
		btnColor.addActionListener((ActionListener) this);
		cbActive = new JCheckBox();
	}

	private JPanel collectVertexForm() {
		lblNameOrDescription.setText("Описание : ");
		lblFunctionOrKey.setText("Функция : ");
		vertexPanel.add(lblId, "cell 0 0");
		vertexPanel.add(lblIdValue, "cell 1 0");
		vertexPanel.add(lblNameOrDescription, "cell 0 1");
		vertexPanel.add(txtNameOrDesription, "cell 1 1");
		vertexPanel.add(lblFunctionOrKey, "cell 0 2 ");
		vertexPanel.add(txtFunctionOrKey, "cell 1 2");
		vertexPanel.add(btnColor, "grow, cell 2 0 1 3");
		return vertexPanel;
	}

	private JPanel collectEdgeForm(boolean edgeFromAction) {
		lblNameOrDescription.setText("Описание : ");
		lblFunctionOrKey.setText("Ключ : ");
		edgePanel.add(lblId, "cell 0 0");
		edgePanel.add(lblIdValue, "cell 1 0");
		edgePanel.add(lblNameOrDescription, "cell 0 1");
		edgePanel.add(txtNameOrDesription, "cell 1 1");
		edgePanel.add(lblFunctionOrKey, "cell 0 2 ");
		edgePanel.add(txtFunctionOrKey, "cell 1 2");
		edgePanel.add(lblActive, "cell 0 3");
		edgePanel.add(cbActive, "cell 1 3");
		return edgePanel;
	}

	public void showEmpty() {
		saveChanges();
		if (!EMPTY_FORM.equals(currentForm)) {
			editObject = null;
			currentForm = EMPTY_FORM;
			((CardLayout) getLayout()).show(this, currentForm);
		}
	}

	public void showEdge(Edge edge) {
		saveChanges();
		editObject = edge;

		if (edge instanceof EdgeFinish) {
			fsd.setMaxLength(1);
			lblIdValue.setText("none");
			txtNameOrDesription.setText("virtual edge");
			txtNameOrDesription.setEditable(false);
			txtFunctionOrKey.setText("-");
			txtFunctionOrKey.setEditable(false);
		}
		if (edge instanceof EdgeStart) {
			fsd.setUnlimitedMaxLength();
			EdgeStart edgeStart = (EdgeStart) edge;
			lblIdValue.setText(edgeStart.getEntryPoint() == null ? "null" : String.valueOf(edgeStart.getEntryPoint().getId()));
			txtNameOrDesription.setText(edgeStart.getDescription());
			txtNameOrDesription.setEditable(true);
			txtFunctionOrKey.setText(edgeStart.getKey());
			txtFunctionOrKey.setEditable(true);
			cbActive.setSelected(edgeStart.isActive());
		}
		if (edge instanceof EdgeAction) {
			fsd.setMaxLength(1);
			EdgeAction edgeAction = (EdgeAction) edge;
			lblIdValue.setText(edgeAction.getAction() == null ? "null" : String.valueOf(edgeAction.getAction().getId()));
			txtNameOrDesription.setText(edgeAction.getDescription());
			txtNameOrDesription.setEditable(true);
			txtFunctionOrKey.setText(String.valueOf(edgeAction.getKey()));
			txtFunctionOrKey.setEditable(true);
			cbActive.setSelected(edgeAction.isActive());
		}
		if (!EDGE_FORM.equals(currentForm)) {
			currentForm = EDGE_FORM;
			collectEdgeForm(edge instanceof EdgeStart);
			((CardLayout) getLayout()).show(this, currentForm);
		}
	}

	public void showVertex(Vertex vertex) {
		fsd.setUnlimitedMaxLength();
		saveChanges();
		editObject = vertex;
		if (vertex instanceof VertexSpecial) {
			lblIdValue.setText("none");
			txtNameOrDesription.setText(vertex instanceof VertexStart ? SpecialVertexName.start : SpecialVertexName.finish);
			txtNameOrDesription.setEditable(false);
			txtFunctionOrKey.setText("none");
			txtFunctionOrKey.setEditable(false);
		}
		if (vertex instanceof VertexRoom) {
			VertexRoom vr = (VertexRoom) vertex;
			lblIdValue.setText(vr.getRoom() == null ? "null" : String.valueOf(vr.getRoom().getId()));
			txtNameOrDesription.setText(vr.getDescription());
			txtNameOrDesription.setEditable(true);
			txtFunctionOrKey.setText(vr.getFunction());
			txtFunctionOrKey.setEditable(true);
			btnColor.setBackground(vr.getAttribute().getColor());
		}
		if (!VERTEX_FORM.equals(currentForm)) {
			currentForm = VERTEX_FORM;
			collectVertexForm();
			((CardLayout) getLayout()).show(this, currentForm);
		}
	}

	public void saveChanges() {
		saveVertex();
		saveEdgeStart();
		saveEdgeAction();
	}

	private void saveVertex() {
		if (editObject instanceof VertexRoom) {
			VertexRoom vr = (VertexRoom) editObject;
			vr.setDescription(txtNameOrDesription.getText());
			vr.setFunction(txtFunctionOrKey.getText());
			Attribute a = vr.getAttribute();
			if (a == null) {
				a = new Attribute();
			}
			a.setColorAsNum(btnColor.getBackground());
			vr.setAttribute(a);
		}
	}

	private void saveEdgeStart() {
		if (editObject instanceof EdgeStart) {
			EdgeStart es = (EdgeStart) editObject;
			if (!StringUtils.isBlank(txtFunctionOrKey.getText())) {
				es.setKey(txtFunctionOrKey.getText());
			}
			es.setDescription(txtNameOrDesription.getText());
			es.setActive(cbActive.isSelected());
		}
	}

	private void saveEdgeAction() {
		if (editObject instanceof EdgeAction) {
			EdgeAction ea = (EdgeAction) editObject;
			if (!StringUtils.isEmpty(txtFunctionOrKey.getText())) {
				ea.setKey(txtFunctionOrKey.getText().charAt(0));
			}
			ea.setDescription(txtNameOrDesription.getText());
			ea.setActive(cbActive.isSelected());
		}
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}