package ru.intech.ussd.modeler.ui;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.intech.ussd.modeler.dao.UssdDaoManager;
import ru.intech.ussd.modeler.entities.Room;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.EdgeAction;
import ru.intech.ussd.modeler.graphobjects.EdgeFinish;
import ru.intech.ussd.modeler.graphobjects.EdgeStart;
import ru.intech.ussd.modeler.graphobjects.Vertex;

public class InfoPanel extends JPanel {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final Logger LOG = LoggerFactory.getLogger(InfoPanel.class);
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
	private Object editObject;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public InfoPanel() {
		setLayout(new CardLayout());
		add(new JLabel("<html><h1><center>Не выбран элемент</center></h1></html>"), EMPTY_FORM);
		add(createInputForm(), INPUT_FORM);
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
		if (vertex.getObject() instanceof String) {
			txtId.setText("none");
			txtNameOrDesription.setText((String) vertex.getObject());
			txtNameOrDesription.setEditable(false);
			txtFunctionOrKey.setText("");
			txtFunctionOrKey.setEditable(false);
		}

		if (vertex.getObject() instanceof Room) {
			Room rh = (Room) vertex.getObject();
			RoomBody rb = findRoomBodyWithFunction(rh);
			txtId.setText(String.valueOf(rh.getId()));
			txtNameOrDesription.setText(rh.getDescription());
			txtNameOrDesription.setEditable(true);
			txtFunctionOrKey.setText(rb == null ? "none" : rb.getValue());
			txtFunctionOrKey.setEditable(true);
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
		if (editObject instanceof Vertex) {
			Vertex vertex = (Vertex) editObject;
			if (vertex.getObject() instanceof Room) {
				Room rh = (Room) vertex.getObject();
				rh.setDescription(txtNameOrDesription.getText());
				saveRoomBody(rh);
			}
		}
	}

	private void saveRoomBody(Room rh) {
		RoomBody rb = findRoomBodyWithFunction(rh);
		if (rb == null) {
			LOG.debug("new RoomBody for {}", rh);
			Param param = UssdDaoManager.loadParamByName("func");
			Validate.notNull(param);

			rb = new RoomBody();
			rb.setRoomHeader(rh);
			rb.setParam(param);

			Set<RoomBody> set = rh.getRoomBodies();
			if (set == null) {
				set = new HashSet<RoomBody>();
				rh.setRoomBodies(set);
			}
			set.add(rb);
		}
		rb.setValue(txtFunctionOrKey.getText());
	}

	private void saveEdge() {
		if (editObject instanceof Edge) {
			Edge edge = (Edge) editObject;
			edge.setKey(txtFunctionOrKey.getText());
			if (!edge.isVirtualActon()) {
				edge.getAction().setDescription(txtNameOrDesription.getText());
			}
		}
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
