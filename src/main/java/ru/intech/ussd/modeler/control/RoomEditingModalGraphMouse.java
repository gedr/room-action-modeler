package ru.intech.ussd.modeler.control;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.ItemSelectable;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.plaf.basic.BasicIconFactory;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.visualization.MultiLayerTransformer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ShearingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;

public class RoomEditingModalGraphMouse<V, E> extends AbstractModalGraphMouse implements ModalGraphMouse, ItemSelectable {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	protected Factory<V> vertexFactory;
	protected Factory<E> edgeFactory;
	protected EditingGraphMousePlugin<V, E> editingPlugin;
	protected MultiLayerTransformer basicTransformer;
	protected RenderContext<V, E> rc;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public RoomEditingModalGraphMouse(RenderContext<V, E> rc, Factory<V> vertexFactory, Factory<E> edgeFactory,
			float scaleFactor) {
		super(scaleFactor, 1 / scaleFactor);
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
		this.rc = rc;
		this.basicTransformer = rc.getMultiLayerTransformer();
		loadPlugins();
		setModeKeyListener(new ModeKeyAdapter(this));
	}

	public RoomEditingModalGraphMouse(RenderContext<V, E> rc, Factory<V> vertexFactory, Factory<E> edgeFactory,
			float scaleFactor, CreateEdgeControl<V> vne) {
		super(scaleFactor, 1 / scaleFactor);
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
		this.rc = rc;
		this.basicTransformer = rc.getMultiLayerTransformer();
		loadPlugins();
		((RoomEditingGraphMousePlugin<V, E>) editingPlugin).setVetoableNewEdge(vne);
		setModeKeyListener(new ModeKeyAdapter(this));
	}

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================
	@Override
	protected void loadPlugins() {
		pickingPlugin = new PickingGraphMousePlugin<V, E>();
		animatedPickingPlugin = new AnimatedPickingGraphMousePlugin<V, E>();
		translatingPlugin = new TranslatingGraphMousePlugin(InputEvent.BUTTON1_MASK);
		scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, in, out);
		rotatingPlugin = new RotatingGraphMousePlugin();
		shearingPlugin = new ShearingGraphMousePlugin();
		editingPlugin = new RoomEditingGraphMousePlugin<V, E>(vertexFactory, edgeFactory);
		add(scalingPlugin);
		setMode(Mode.TRANSFORMING);
	}

	@Override
	public void setMode(Mode mode) {
		if (this.mode != mode) {
			fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this.mode, ItemEvent.DESELECTED));
			this.mode = mode;
			if (mode == Mode.TRANSFORMING) {
				setModeTransforming();
			} else if (mode == Mode.PICKING) {
				setModePicking();
			}
			if (modeBox != null) {
				modeBox.setSelectedItem(mode);
			}
			fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, mode, ItemEvent.SELECTED));
		}
	}

	@Override
	public JMenu getModeMenu() {
		if (modeMenu == null) {
			modeMenu = new JMenu();
			Icon icon = BasicIconFactory.getMenuArrowIcon();
			modeMenu.setIcon(BasicIconFactory.getMenuArrowIcon());
			modeMenu.setPreferredSize(new Dimension(icon.getIconWidth() + 10, icon.getIconHeight() + 10));

			final JRadioButtonMenuItem transformingButton = new JRadioButtonMenuItem(Mode.TRANSFORMING.toString());
			transformingButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.TRANSFORMING);
					}
				}
			});

			final JRadioButtonMenuItem pickingButton = new JRadioButtonMenuItem(Mode.PICKING.toString());
			pickingButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.PICKING);
					}
				}
			});

			ButtonGroup radio = new ButtonGroup();
			radio.add(transformingButton);
			radio.add(pickingButton);
			transformingButton.setSelected(true);
			modeMenu.add(transformingButton);
			modeMenu.add(pickingButton);
			modeMenu.setToolTipText("Menu for setting Mouse Mode");
			addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						if (e.getItem() == Mode.TRANSFORMING) {
							transformingButton.setSelected(true);
						} else if (e.getItem() == Mode.PICKING) {
							pickingButton.setSelected(true);
						}
					}
				}
			});
		}
		return modeMenu;
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public CreateEdgeControl<V> getVetoableNewEdge() {
		return ((RoomEditingGraphMousePlugin<V, E>) editingPlugin).getVetoableNewEdge();
	}

	public void setVetoableNewEdge(CreateEdgeControl<V> vetoableNewEdge) {
		((RoomEditingGraphMousePlugin<V, E>) editingPlugin).setVetoableNewEdge(vetoableNewEdge);
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	protected void setModeTransforming() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(editingPlugin);
		add(translatingPlugin);
		add(rotatingPlugin);
		add(shearingPlugin);
	}

	protected void setModePicking() {
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(editingPlugin);
		add(pickingPlugin);
		add(animatedPickingPlugin);
		add(editingPlugin);
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
	public static class ModeKeyAdapter extends KeyAdapter {
		private char t = 't';
		private char p = 'p';
		protected ModalGraphMouse graphMouse;

		public ModeKeyAdapter(ModalGraphMouse graphMouse) {
			this.graphMouse = graphMouse;
		}

		public ModeKeyAdapter(char t, char p, ModalGraphMouse graphMouse) {
			this.t = t;
			this.p = p;
			this.graphMouse = graphMouse;
		}

		@Override
		public void keyTyped(KeyEvent event) {
			char keyChar = event.getKeyChar();
			if (keyChar == t) {
				((Component) event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				graphMouse.setMode(Mode.TRANSFORMING);
			} else if (keyChar == p) {
				((Component) event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				graphMouse.setMode(Mode.PICKING);
			}
		}
	}
}