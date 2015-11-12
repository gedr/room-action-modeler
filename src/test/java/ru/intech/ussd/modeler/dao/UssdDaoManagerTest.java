package ru.intech.ussd.modeler.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.intech.ussd.modeler.entities.Action;
import ru.intech.ussd.modeler.entities.EntryPoint;
import ru.intech.ussd.modeler.entities.Room;

public class UssdDaoManagerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testLoadEntryPointByService_Success() {
		List<EntryPoint> lst = UssdDaoManager.loadEntryPointByService("333");
		assertNotNull(lst);
		assertTrue(!lst.isEmpty());
	}

	@Test
	public void testLoadEntryPointByService_Fail() {
		List<EntryPoint> lst = UssdDaoManager.loadEntryPointByService("fail");
		assertNotNull(lst);
		assertTrue(lst.isEmpty());
	}

	@Test
	public void testLoadActionByService_Success() {
		List<Action> lst = UssdDaoManager.loadActionByService("333");
		assertNotNull(lst);
		assertTrue(!lst.isEmpty());
	}

	@Test
	public void testLoadActionByService_Fail() {
		List<Action> lst = UssdDaoManager.loadActionByService("fail");
		assertNotNull(lst);
		assertTrue(lst.isEmpty());
	}

	@Test
	public void testDeleteActionsById() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteEntryPointsById() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteRoomsById() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveRoom() {
		Room room = new Room();
		room.setDescription("test");
		room.setFunction("test()");
		room.setFinish(false);
		UssdDaoManager.saveRoom(room);
		assertNotNull(room.getId());
	}

	@Test
	public void testUpdateRoom() {
		Room room = new Room();
		room.setDescription("test");
		room.setFunction("test()");
		room.setFinish(false);
		UssdDaoManager.saveRoom(room);
		assertNotNull(room.getId());
		room.setDescription("update test");
		room.setFunction("update()");
		UssdDaoManager.updateRoom(room);
	}

	@Test
	public void testSaveEntryPoint() {
		Room room = findRoom("333", 1);
		assertNotNull(room);
		EntryPoint ep = new EntryPoint();
		ep.setDescription("test EntryPoint");
		ep.setUserMessage("123");
		ep.setRoom(room);
		ep.setService("333");
		UssdDaoManager.saveEntryPoint(ep);
		assertNotNull(ep.getId());
	}

	@Test
	public void testUpdateEntryPoint() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveAction() {
		Room room2 = findRoom("333", 2);
		assertNotNull(room2);
		Room room1 = findRoom("333", 1);
		assertNotNull(room1);

		Action a = new Action();
		a.setKey('Q');
		a.setDescription("test");
		a.setCurrentRoom(room2);
		a.setNextRoom(room1);
		a.setService("333");
		UssdDaoManager.saveAction(a);
		assertNotNull(a.getId());

	}

	@Test
	public void testUpdateAction() {
		fail("Not yet implemented");
	}

	private Room findRoom(String service, int id) {
		List<Action> lst = UssdDaoManager.loadActionByService(service);
		for (Action a : lst) {
			if (a.getNextRoom().getId() == id) {
				return a.getNextRoom();
			}
			if (a.getCurrentRoom().getId() == id) {
				return a.getCurrentRoom();
			}
		}
		return null;
	}

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

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
