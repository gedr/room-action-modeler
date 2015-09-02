package ru.intech.ussd.modeler.dao;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.intech.ussd.modeler.entities.Action;
import ru.intech.ussd.modeler.entities.EntryPoint;
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
		String hsql = "select ep from EntryPoint as ep inner join fetch ep.room where ep.service = :service";
		try {
			return ussd.executeAndGetResultList(QUERY_LANG.HSQL, hsql,
					OrmHelper.QueryAttrs.build().setParam("service", service));
		} catch (Throwable e) {
			LOG.error("loadEntryPointForService failed : ", e);
		}
		return Collections.emptyList();
	}

	public static List<Action> loadActionByService(String service) {
		String hsql = "select a from Action as a inner join fetch a.currentRoom inner join fetch a.nextRoom where a.service = :service";
		try {
			return ussd.executeAndGetResultList(QUERY_LANG.HSQL, hsql,
					OrmHelper.QueryAttrs.build().setParam("service", service));
		} catch (Throwable e) {
			LOG.error("loadEntryPointForService failed : ", e);
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
		Validate.notNull(room);
		try {
			ussd.save(room);
		} catch (Throwable e) {
			LOG.error("saveRoom failed : ", e);
		}
	}

	public static void updateRoom(Room room) {
		Validate.notNull(room);
		Validate.notNull(room.getId());
		try {
			ussd.update(room);
		} catch (Throwable e) {
			LOG.error("updateRoom failed : ", e);
		}
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
}
