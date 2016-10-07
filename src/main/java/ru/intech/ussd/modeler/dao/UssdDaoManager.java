package ru.intech.ussd.modeler.dao;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.intech.ussd.modeler.entities.Action;
import ru.intech.ussd.modeler.entities.Attribute;
import ru.intech.ussd.modeler.entities.EntryPoint;
import ru.intech.ussd.modeler.entities.Projection;
import ru.intech.ussd.modeler.entities.Room;
import ru.intech.ussd.modeler.util.OrmHelper;
import ru.intech.ussd.modeler.util.OrmHelper.QUERY_LANG;

public class UssdDaoManager {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final Logger LOG = LoggerFactory.getLogger(UssdDaoManager.class);

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private static OrmHelper ussd;

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	static {
		URL url = UssdDaoManager.class.getResource("/hiberate.cfg.xml");
		ussd = new OrmHelper(new File(url.getPath()));
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
	public static List<EntryPoint> loadEntryPointByService(String service) {
		String hsql = "SELECT ep FROM EntryPoint AS ep INNER JOIN FETCH ep.room WHERE ep.service = :service";
		try {
			return ussd.executeAndGetResultList(QUERY_LANG.HSQL, hsql,
					OrmHelper.QueryAttrs.build().setParam("service", service));
		} catch (Throwable e) {
			LOG.error("loadEntryPointForService failed : ", e);
		}
		return Collections.emptyList();
	}

	public static List<String> loadServices() {
		String hsql = "SELECT distinct ep.service FROM EntryPoint AS ep";
		try {
			return ussd.executeAndGetResultList(QUERY_LANG.HSQL, hsql, null);
		} catch (Throwable e) {
			LOG.error("loadServices failed : ", e);
		}
		return Collections.emptyList();
	}


	public static List<Action> loadActionByService(String service) {
		String hsql = "SELECT a FROM Action AS a "
				+ " INNER JOIN FETCH a.currentRoom "
				+ " LEFT JOIN FETCH a.currentRoom.projections "
				+ " INNER JOIN FETCH a.nextRoom "
				+ " LEFT JOIN FETCH a.nextRoom.projections "
				+ " WHERE a.service = :service";
		try {
			return ussd.executeAndGetResultList(QUERY_LANG.HSQL, hsql,
					OrmHelper.QueryAttrs.build().setParam("service", service));
		} catch (Throwable e) {
			LOG.error("loadEntryPointForService failed : ", e);
		}
		return Collections.emptyList();
	}

	public static List<Projection> loadProjections(String service) {
		String hsql = "SELECT p FROM Projection AS p WHERE p.service = :service";
		try {
			return ussd.executeAndGetResultList(QUERY_LANG.HSQL, hsql,
					OrmHelper.QueryAttrs.build().setParam("service", service));
		} catch (Throwable e) {
			LOG.error("loadProjections failed : ", e);
		}
		return Collections.emptyList();
	}

	public static int deleteActionsById(Collection<Integer> ids) {
		Validate.notNull(ids);
		Validate.notEmpty(ids);
		String hsql = "DELETE FROM Action a WHERE a.id IN (:ids)";
		int count = 0;
		try {
			count = ussd.executeUpdate(QUERY_LANG.HSQL, hsql, OrmHelper.QueryAttrs.build().setParam("ids", ids));
			LOG.debug("delete {} row from table ACTIONS", count);
		} catch (Throwable e) {
			LOG.error("deleteActionsById failed : ", e);
		}
		return count;
	}

	public static int deleteEntryPointsById(Collection<Integer> ids) {
		Validate.notNull(ids);
		Validate.notEmpty(ids);
		String hsql = "DELETE FROM EntryPoint ep WHERE ep.id IN (:ids)";
		int count = 0;
		try {
			int delCount = ussd.executeUpdate(QUERY_LANG.HSQL, hsql, OrmHelper.QueryAttrs.build().setParam("ids", ids));
			LOG.debug("delete {} row from table ENTRY_POINTS", delCount);
		} catch (Throwable e) {
			LOG.error("deleteEntryPointsById failed : ", e);
		}
		return count;
	}

	public static int deleteRoomsById(Collection<Integer> ids) {
		Validate.notNull(ids);
		Validate.notEmpty(ids);
		String hsql = "DELETE FROM Room r WHERE r.id IN (:ids)";
		int count = 0;
		try {
			int delCount = ussd.executeUpdate(QUERY_LANG.HSQL, hsql, OrmHelper.QueryAttrs.build().setParam("ids", ids));
			LOG.debug("delete {} row from table ROOMS", delCount);
		} catch (Throwable e) {
			LOG.error("deleteRoomsById failed : ", e);
		}
		return count;
	}

	public static void saveRoom(Room room) {
		LOG.info("saveRoom({})", room);
		Validate.notNull(room);
		try {
			ussd.save(room);
		} catch (Throwable e) {
			LOG.error("saveRoom failed : ", e);
		}
	}

	public static void updateRoom(Room room) {
		LOG.info("updateRoom({})", room);
		Validate.notNull(room);
		Validate.notNull(room.getId());
		try {
			ussd.update(room);
		} catch (Throwable e) {
			LOG.error("updateRoom failed : ", e);
		}
	}

	public static void saveEntryPoint(EntryPoint ep) {
		LOG.info("saveEntryPoint({})", ep);
		Validate.notNull(ep);
		try {
			ussd.save(ep);
		} catch (Throwable e) {
			LOG.error("saveRoom failed : ", e);
		}
	}

	public static void updateEntryPoint(EntryPoint ep) {
		LOG.info("updateEntryPoint({})", ep);
		Validate.notNull(ep);
		Validate.notNull(ep.getId());
		try {
			ussd.update(ep);
		} catch (Throwable e) {
			LOG.error("updateRoom failed : ", e);
		}
	}

	public static void saveAction(Action a) {
		LOG.info("saveAction({})", a);
		Validate.notNull(a);
		try {
			ussd.save(a);
		} catch (Throwable e) {
			LOG.error("saveRoom failed : ", e);
		}
	}

	public static void updateAction(Action a) {
		LOG.info("updateAction({})", a);
		Validate.notNull(a);
		Validate.notNull(a.getId());
		try {
			ussd.update(a);
		} catch (Throwable e) {
			LOG.error("updateRoom failed : ", e);
		}
	}

	public static void saveAttribute(Attribute a) {
		LOG.info("saveAttribute({})", a);
		Validate.notNull(a);
		try {
			ussd.save(a);
		} catch (Throwable e) {
			LOG.error("save Attribute failed : ", e);
		}
	}

	public static void updateAttribute(Attribute a) {
		LOG.info("updateAttribute({})", a);
		Validate.notNull(a);
		Validate.notNull(a.getId());
		try {
			ussd.update(a);
		} catch (Throwable e) {
			LOG.error("update Attribute failed : ", e);
		}
	}


	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================



	public static Pair<Set<Integer>, Set<Integer>> loadEntryPointAndActionIds(String prefix) {
		String sql = "; WITH RECURSIVE r AS ( SELECT room_id AS rid, true AS tp, id FROM test.entry_points WHERE :prefix = srv "
				+ "UNION "
				+ "SELECT a.next_room_id AS rid, false AS tp, a.id FROM test.actions a JOIN r ON r.id = a.current_room_id )  "
				+ "SELECT tp, id FROM r;";
		try {
			List<Object[]> lst = ussd.executeAndGetResultList(QUERY_LANG.SQL, sql, OrmHelper.QueryAttrs.build()
					.addScalar("tp", Hibernate.BOOLEAN).addScalar("id", Hibernate.INTEGER)
					.setParam("prefix", prefix));
			if (!lst.isEmpty()) {
				Set<Integer> epids = new TreeSet<Integer>();
				Set<Integer> aids = new TreeSet<Integer>();

				for (Object[] objs : lst) {
					if ((Boolean) objs[0]) {
						epids.add((Integer) objs[1]);
					} else {
						aids.add((Integer) objs[1]);
					}
				}
				return ImmutablePair.of(epids, aids);
			}
		} catch (Throwable e) {
			LOG.error("loadEntryPointForService failed : ", e);
		}
		return null;
	}

	public static List<EntryPoint> loadEntryPointByIds(Collection<Integer> ids) {
		String hsql = "SELECT ep FROM EntryPoint AS ep INNER JOIN FETCH ep.room WHERE ep.id IN (:ids)";
		try {
			return ussd.executeAndGetResultList(QUERY_LANG.HSQL, hsql, OrmHelper.QueryAttrs.build().setParam("ids", ids));
		} catch (Throwable e) {
			LOG.error("loadEntryPointByIds failed : ", e);
		}
		return Collections.emptyList();
	}

	public static List<Action> loadActionByIds(Collection<Integer> ids) {
		String hsql = "SELECT a FROM Action AS a INNER JOIN FETCH a.currentRoom INNER JOIN FETCH a.nextRoom WHERE a.id IN (:ids)";
		try {
			return ussd.executeAndGetResultList(QUERY_LANG.HSQL, hsql, OrmHelper.QueryAttrs.build().setParam("ids", ids));
		} catch (Throwable e) {
			LOG.error("loadActionByIds failed : ", e);
		}
		return Collections.emptyList();
	}





	public static List<EntryPoint> loadEntryPointByPrefix(String prefix) {
		String jpql = "SELECT ep FROM EntryPoint ep INNER JOIN FETCH ep.room WHERE ep.prefix = :prefix";
		return em.createQuery(jpql, EntryPoint.class)
				.setParameter("prefix", prefix)
				.getResultList();
	}


	public static List<Action> loadActionByPrefix(String prefix) {
		String ipql = "SELECT a FROM Action a INNER JOIN FETCH a.currentRoom INNER JOIN FETCH a.nextRoom WHERE a.active = true";
		return em.createQuery(ipql, Action.class).getResultList();
	}

}
