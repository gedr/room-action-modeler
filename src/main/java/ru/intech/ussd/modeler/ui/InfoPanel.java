package ru.intech.ussd.modeler.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.StringUtils;

import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.EdgeAction;
import ru.intech.ussd.modeler.graphobjects.EdgeFinish;
import ru.intech.ussd.modeler.graphobjects.EdgeStart;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.graphobjects.VertexRoom;
import ru.intech.ussd.modeler.graphobjects.VertexSpecial;
import ru.intech.ussd.modeler.graphobjects.VertexStart;

public class InfoPanel extends JPanel {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final long serialVersionUID = 1L;

	private static final String EMPTY_FORM = "empty";
	private static final String INPUT_FORM = "input";

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private JLabel lblId;
	private JLabel lblNameOrDescription;
	private JLabel lblFunctionOrKey;
	private JTextField txtId;
	private JTextField txtNameOrDesription;
	private JTextField txtFunctionOrKey;
	private JButton btnColor;
	private Object editObject;


	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public InfoPanel() {
		setLayout(new CardLayout());
		add(new JLabel("<html><h1><center>Не выбран элемент</center></h1></html>"), EMPTY_FORM);
//		add(createInputForm(), INPUT_FORM);
		add(createVertexForm(), INPUT_FORM);

		((CardLayout) getLayout()).show(this, EMPTY_FORM);
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	private void createComponents() {
		lblId = new JLabel("ID :");
		lblNameOrDescription = new JLabel();
		lblFunctionOrKey = new JLabel();
		txtId = new JTextField();
		txtId.setEditable(false);
		txtNameOrDesription = new JTextField();
		txtFunctionOrKey = new JTextField();
		btnColor = new JButton();
	}

	private JPanel createVertexForm() {
		createComponents();
		JPanel panel = new JPanel(new MigLayout("", "[left][grow, fill][right]", "[][][]"));
		panel.setBorder(BorderFactory.createLineBorder(Color.RED));
		panel.add(lblId, "cell 0 0");
		panel.add(txtId, "cell 1 0");
		panel.add(lblNameOrDescription, "cell 0 1");
		panel.add(txtNameOrDesription, "cell 1 1");
		panel.add(lblFunctionOrKey, "cell 0 2 ");
		panel.add(txtFunctionOrKey, "cell 1 2");
		panel.add(btnColor, "grow, cell 2 0 1 3");

		return panel;
	}

	private JPanel createInputForm() {
		createComponents();

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.1;
		panel.add(lblId, c);
		c.gridy = 1;
		panel.add(lblNameOrDescription, c);
		c.gridy = 2;
		panel.add(lblFunctionOrKey, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		panel.add(txtId, c);
		c.gridy = 1;
		panel.add(txtNameOrDesription, c);
		c.gridy = 2;
		panel.add(txtFunctionOrKey, c);

		return panel;
	}

	public void showEmpty() {
		saveChanges();
		editObject = null;
		((CardLayout) getLayout()).show(this, EMPTY_FORM);
	}

	public void showEdge(Edge edge) {
		saveChanges();
		editObject = edge;
		lblNameOrDescription.setText("Описание : ");
		lblFunctionOrKey.setText("Ключ : ");
		if (edge instanceof EdgeFinish) {
			txtId.setText("none");
			txtNameOrDesription.setText("virtual edge");
			txtNameOrDesription.setEditable(false);
			txtFunctionOrKey.setText("-");
			txtFunctionOrKey.setEditable(false);
		}
		if (edge instanceof EdgeStart) {
			EdgeStart edgeStart = (EdgeStart) edge;
			txtId.setText(edgeStart.getEntryPoint() == null ? "null" : String.valueOf(edgeStart.getEntryPoint().getId()));
			txtNameOrDesription.setText(edgeStart.getDescription());
			txtNameOrDesription.setEditable(true);
			txtFunctionOrKey.setText(edgeStart.getKey());
			txtFunctionOrKey.setEditable(true);
		}
		if (edge instanceof EdgeAction) {
			EdgeAction edgeAction = (EdgeAction) edge;
			txtId.setText(edgeAction.getAction() == null ? "null" : String.valueOf(edgeAction.getAction().getId()));
			txtNameOrDesription.setText(edgeAction.getDescription());
			txtNameOrDesription.setEditable(true);
			txtFunctionOrKey.setText(String.valueOf(edgeAction.getKey()));
			txtFunctionOrKey.setEditable(true);
		}

		((CardLayout) getLayout()).show(this, INPUT_FORM);
	}

	public void showVertex(Vertex vertex) {
		saveChanges();
		editObject = vertex;
		lblNameOrDescription.setText("Описание : ");
		lblFunctionOrKey.setText("Функция : ");
		if (vertex instanceof VertexSpecial) {
			txtId.setText("none");
			txtNameOrDesription.setText(vertex instanceof VertexStart ? SpecialVertexName.start : SpecialVertexName.finish);
			txtNameOrDesription.setEditable(false);
			txtFunctionOrKey.setText("none");
			txtFunctionOrKey.setEditable(false);
		}
		if (vertex instanceof VertexRoom) {
			VertexRoom vr = (VertexRoom) vertex;
			txtId.setText(vr.getRoom() == null ? "null" : String.valueOf(vr.getRoom().getId()));
			txtNameOrDesription.setText(vr.getDescription());
			txtNameOrDesription.setEditable(true);
			txtFunctionOrKey.setText(vr.getFunction());
			txtFunctionOrKey.setEditable(true);
			btnColor.setBackground(vr.getAttribute().getColor());
		}
		((CardLayout) getLayout()).show(this, INPUT_FORM);
	}

	public void saveChanges() {
		if (editObject != null) {
			saveVertex();
			saveEdge();
		}
	}

	private void saveVertex() {
		if (editObject instanceof VertexRoom) {
			VertexRoom vr = (VertexRoom) editObject;
			vr.setDescription(txtNameOrDesription.getText());
			vr.setFunction(txtFunctionOrKey.getText());
		}
	}

	private void saveEdge() {
		if (editObject instanceof EdgeStart) {
			EdgeStart es = (EdgeStart) editObject;
			if (!StringUtils.isBlank(txtFunctionOrKey.getText())) {
				es.setKey(txtFunctionOrKey.getText());
			}
			es.setDescription(txtNameOrDesription.getText());
		}
		if (editObject instanceof EdgeAction) {
			EdgeAction ea = (EdgeAction) editObject;
			if (!StringUtils.isEmpty(txtFunctionOrKey.getText())) {
				ea.setKey(txtFunctionOrKey.getText().charAt(0));
			}
			ea.setDescription(txtNameOrDesription.getText());
		}
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}