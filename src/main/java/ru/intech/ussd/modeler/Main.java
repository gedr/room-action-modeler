package ru.intech.ussd.modeler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;

import org.apache.log4j.PropertyConfigurator;

import ru.intech.ussd.modeler.config.GraphConfig;
import ru.intech.ussd.modeler.graphobjects.Edge;
import ru.intech.ussd.modeler.graphobjects.Vertex;
import ru.intech.ussd.modeler.services.GraphService;
import ru.intech.ussd.modeler.ui.MainFrame;
import ru.intech.ussd.modeler.util.Unit;
import edu.uci.ics.jung.graph.Graph;

public class Main {
	// =================================================================================================================
	// Constants
	// =================================================================================================================

	// =================================================================================================================
	// Fields
	// =================================================================================================================

	// =================================================================================================================
	// Constructors
	// =================================================================================================================

	// =================================================================================================================
	// Methods for/from SuperClass/Interface
	// =================================================================================================================

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public static void main(String[] args) {
//		new A().run();
//		System.exit(0);
		PropertyConfigurator.configure(Main.class.getResource("/log4j.properties").getPath());

		final Graph<Vertex, Unit<Edge>> g = GraphService.loadGraph("177");

		JFrame frame = new MainFrame(g, new GraphConfig());
		frame.pack();
		frame.setVisible(true);
	}


	private static void runMethod(Object obj, String methodName, Object...objects) {

		Class<?>[] clss = new Class<?>[objects == null ? 0 : objects.length];
		int i = 0;
		if (objects != null) {
			for (Object o : objects) {
				clss[i++] = o.getClass();
			}
		}

		try {
			Method m = obj.getClass().getMethod(methodName, clss);
			m.invoke(obj, objects);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================


	private static class A implements Runnable {
		public void run() {
			Main.runMethod(this, "testA", new Object[0]);
			Main.runMethod(this, "testB", 10);
			Main.runMethod(this, "testC", new GraphConfig());

		}

		public void testA() {
			System.out.println("TEST A");
		}

		public void testB(int val) {
			System.out.println("TEST B val = " + val);
		}

		public void testC(GraphConfig config) {
			System.out.println("TEST C config is null" + (config == null));
		}

	}

}
