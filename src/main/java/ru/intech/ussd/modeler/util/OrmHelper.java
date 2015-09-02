package ru.intech.ussd.modeler.util;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrmHelper {
	// =================================================================================================================
	// Constants
	// =================================================================================================================
	private static final Logger LOG = LoggerFactory.getLogger(OrmHelper.class);

	// =================================================================================================================
	// Fields
	// =================================================================================================================
	private SessionFactory factory;
	private ThreadLocalMulti<Session> localSession = new ThreadLocalMulti<Session>();
	private ThreadLocalMulti<Transaction> localTransaction = new ThreadLocalMulti<Transaction>();
	private ThreadLocalMulti<Boolean> localMultiplex = new ThreadLocalMulti<Boolean>();

	// =================================================================================================================
	// Constructors
	// =================================================================================================================
	public OrmHelper(File cfgFile) {
		Validate.notNull(cfgFile);
		try {
			factory = new AnnotationConfiguration().configure(cfgFile).buildSessionFactory();
			LOG.info("Creating new SessionFactory successfully from file=" + cfgFile.getAbsolutePath());
		} catch (Throwable ex) {
			LOG.error("Initial SessionFactory creation failed from file=" + cfgFile.getAbsolutePath(), ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public OrmHelper(Properties prop) {
		Validate.notNull(prop);
		try {
			AnnotationConfiguration cfg = new AnnotationConfiguration();
			cfg.setProperties(prop);
			factory = cfg.buildSessionFactory();
			LOG.info("Creating new SessionFactory successfully from propeprties");
		} catch (Throwable ex) {
			LOG.error("Initial SessionFactory creation failed from propeprties", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	// =================================================================================================================
	// Getter & Setter
	// =================================================================================================================
	public SessionFactory getFactory() {
		return factory;
	}

	// =================================================================================================================
	// Methods
	// =================================================================================================================
	public void openSession() {
		Session s = localSession.get();
		if (s == null) {
			s = getFactory().openSession();
			localSession.set(s);
		}
	}

	public Session getCurrentSession() {
		return localSession.get();
	}

	public void closeSession() {
		Session s = localSession.get();
		localSession.remove();
		if ((s != null) && s.isOpen()) {
			s.close();
		}
	}

	public int getBatchSize() {
		return ((SessionFactoryImplementor) factory).getSettings().getJdbcBatchSize();
	}

	public void beginTransaction() {
		Transaction tx = localTransaction.get();
		if (tx == null) {
			tx = getCurrentSession().beginTransaction();
			localTransaction.set(tx);
		}
	}

	public void commitTransaction() {
		Transaction tx = localTransaction.get();
		localTransaction.remove();
		if ((tx != null) && tx.isActive()) {
			tx.commit();
		}
	}

	public void rollbackTransaction() {
		Transaction tx = localTransaction.get();
		localTransaction.remove();
		if ((tx != null) && tx.isActive()) {
			tx.rollback();
		}
	}

	public void beginMuliplex() {
		Boolean mx = localMultiplex.get();
		if (mx == null) {
			openSession();
			beginTransaction();
			localMultiplex.set(Boolean.TRUE);
		}
	}

	public boolean isMultiplex() {
		Boolean mx = localMultiplex.get();
		return (mx == null ? false : mx);
	}

	public void closeMuliplex() throws Throwable {
		Boolean mx = localMultiplex.get();
		localMultiplex.remove();
		if (mx != null) {
			try {
				commitTransaction();
			} catch (Throwable ex) {
				rollbackTransaction();
				throw ex;
			} finally {
				closeSession();
			}
		}
	}

	public <T> List<T> executeAndGetResultList(QUERY_LANG ql, String req, QueryAttrs attrs) throws Throwable {
		try {
			openSession();
			beginTransaction();

			Query q = ql.getQuery(getCurrentSession(), req);

			q = applyAttrs(attrs, q);

			@SuppressWarnings("unchecked")
			List<T> result = (List<T>) q.list();
			if (result == null) {
				result = Collections.emptyList();
			}

			if (!isMultiplex()) {
				commitTransaction();
			}

			return result;
		} catch (Throwable ex) {
			rollbackTransaction();
			if (isMultiplex()) {
				closeMuliplex();
			}
			throw ex;
		} finally {
			if (!isMultiplex()) {
				closeSession();
			}
		}
	}

	public <T> T executeAndGetUniqueResult(QUERY_LANG ql, String req, QueryAttrs attrs) throws Throwable {
		try {
			openSession();
			beginTransaction();

			Query q = ql.getQuery(getCurrentSession(), req);

			q = applyAttrs(attrs, q);

			@SuppressWarnings("unchecked")
			T result = (T) q.uniqueResult();

			if (!isMultiplex()) {
				commitTransaction();
			}

			return result;
		} catch (Throwable ex) {
			rollbackTransaction();
			if (isMultiplex()) {
				closeMuliplex();
			}
			throw ex;
		} finally {
			if (!isMultiplex()) {
				closeSession();
			}
		}
	}

	public int executeUpdate(QUERY_LANG ql, String req, QueryAttrs attrs) throws Throwable {
		try {
			openSession();
			beginTransaction();

			Query q = ql.getQuery(getCurrentSession(), req);

			q = applyAttrs(attrs, q);

			int result = q.executeUpdate();

			if (!isMultiplex()) {
				commitTransaction();
			}

			return result;
		} catch (Throwable ex) {
			rollbackTransaction();
			if (isMultiplex()) {
				closeMuliplex();
			}
			throw ex;
		} finally {
			if (!isMultiplex()) {
				closeSession();
			}
		}
	}

	public <T> T load(Class<T> clz, Serializable id) throws Throwable {
		try {
			openSession();

			@SuppressWarnings("unchecked")
			T res = (T) getCurrentSession().load(clz, id);
			return res;
		} catch (Throwable ex) {
			if (isMultiplex()) {
				closeMuliplex();
			}
			throw ex;
		} finally {
			if (!isMultiplex()) {
				closeSession();
			}
		}
	}

	public void save(List<?> entities) throws Throwable {
		if ((entities == null) || entities.isEmpty()) {
			LOG.warn("list of entities is empty");
			return;
		}

		try {
			openSession();
			Session session = getCurrentSession();
			int batchSize = getBatchSize();

			beginTransaction();

			int i = 1;
			for (Object o : entities) {
				session.save(o);
				if (i++ % batchSize == 0) {
					session.flush();
					session.clear();
				}
			}

			if (!isMultiplex()) {
				commitTransaction();
			}
		} catch (Throwable ex) {
			rollbackTransaction();
			if (isMultiplex()) {
				closeMuliplex();
			}
			throw ex;
		} finally {
			if (!isMultiplex()) {
				closeSession();
			}
		}
	}

	public void save(Object obj) throws Throwable {
		if (obj == null) {
			LOG.warn("entity for save is null");
			return;
		}
		try {
			openSession();
			beginTransaction();

			getCurrentSession().save(obj);

			if (!isMultiplex()) {
				commitTransaction();
			}
		} catch (Throwable ex) {
			rollbackTransaction();
			if (isMultiplex()) {
				closeMuliplex();
			}
			throw ex;
		} finally {
			if (!isMultiplex()) {
				closeSession();
			}
		}
	}

	public void update(List<?> entities) throws Throwable {
		if ((entities == null) || entities.isEmpty()) {
			LOG.warn("list of entities is empty");
			return;
		}
		try {
			openSession();
			Session session = getCurrentSession();
			int batchSize = getBatchSize();

			beginTransaction();

			int i = 1;
			for (Object o : entities) {
				session.update(o);
				if (i++ % batchSize == 0) {
					session.flush();
					session.clear();
				}
			}

			if (!isMultiplex()) {
				commitTransaction();
			}
		} catch (Throwable ex) {
			rollbackTransaction();
			if (isMultiplex()) {
				closeMuliplex();
			}
			throw ex;
		} finally {
			if (!isMultiplex()) {
				closeSession();
			}
		}
	}

	public void update(Object obj) throws Throwable {
		if (obj == null) {
			LOG.warn("entity for update is null");
			return;
		}
		try {
			openSession();
			beginTransaction();

			getCurrentSession().update(obj);

			if (!isMultiplex()) {
				commitTransaction();
			}
		} catch (Exception ex) {
			rollbackTransaction();
			if (isMultiplex()) {
				closeMuliplex();
			}
			throw ex;
		} finally {
			if (!isMultiplex()) {
				closeSession();
			}
		}
	}

	public void delete(List<?> entities) throws Throwable {
		if ((entities == null) || entities.isEmpty()) {
			LOG.warn("list of entities is empty");
			return;
		}
		try {
			openSession();
			Session session = getCurrentSession();
			int batchSize = getBatchSize();

			beginTransaction();

			int i = 1;
			for (Object o : entities) {
				session.delete(o);
				if (i++ % batchSize == 0) {
					session.flush();
					session.clear();
				}
			}

			if (!isMultiplex()) {
				commitTransaction();
			}
		} catch (Throwable ex) {
			rollbackTransaction();
			if (isMultiplex()) {
				closeMuliplex();
			}
			throw ex;
		} finally {
			if (!isMultiplex()) {
				closeSession();
			}
		}
	}

	public void delete(Object obj) throws Throwable {
		if (obj == null) {
			LOG.warn("entity for delete is null");
			return;
		}
		try {
			openSession();
			beginTransaction();

			getCurrentSession().delete(obj);

			if (!isMultiplex()) {
				commitTransaction();
			}
		} catch (Exception ex) {
			rollbackTransaction();
			if (isMultiplex()) {
				closeMuliplex();
			}
			throw ex;
		} finally {
			if (!isMultiplex()) {
				closeSession();
			}
		}
	}

	private Query applyAttrs(QueryAttrs attrs, Query q) {
		if (attrs == null) {
			return q;
		}
		if ((attrs.getScalars() != null) && (q instanceof SQLQuery)) {
			for (Scalar s : attrs.getScalars()) {
				if (s != null) {
					((SQLQuery) q).addScalar(s.getName(), s.getType());
				}
			}
		}
		if (attrs.getParams() != null) {
			for (QueryParam p : attrs.getParams()) {
				if (p != null) {
					q = p.setParam(q);
				}
			}
		}
		if (attrs.isCacheable()) {
			q.setCacheable(true);
			if (StringUtils.isNotBlank(attrs.getCacheRegion()))
				q.setCacheRegion(attrs.getCacheRegion());
		}
		if (attrs.getMaxResult() > 0) {
			q.setMaxResults(attrs.getMaxResult());
		}
		return q;
	}

	// =================================================================================================================
	// Inner and Anonymous Classes
	// =================================================================================================================
	public enum QUERY_LANG {
		SQL {
			Query getQuery(Session session, String req) {
				return session.createSQLQuery(req);
			}
		},
		HSQL {
			Query getQuery(Session session, String req) {
				return session.createQuery(req);
			}
		};

		abstract Query getQuery(Session session, String req);
	};

	private static class Scalar {
		private String name;
		private Type type;

		public Scalar(String name, Type type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public Type getType() {
			return type;
		}
	}

	private static class QueryParam {
		public static enum PARAM_TYPE {STRING, CHAR, BOOLEAN, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BINARY, SERIAL, LOCALE
			, TEXT, BIG_DECIMAL, BIG_INT, CALENDAR, CALENDAR_DATE, DATE, TIME, TIMESTAMP, COLLECTION};

		private String pname;
		private Object pvalue;
		private PARAM_TYPE ptype;

		public QueryParam(PARAM_TYPE type, String name, Object value) {
			this.ptype = type;
			this.pname = name;
			this.pvalue = value;
		}

		public Query setParam(Query q) {
			switch (ptype) {
				case TEXT :
					return q.setText(pname, (String) pvalue);
				case STRING :
					return q.setString(pname, (String) pvalue);
				case CHAR :
					return q.setCharacter(pname, (Character) pvalue);
				case BOOLEAN :
					return q.setBoolean(pname, (Boolean) pvalue);
				case BYTE :
					return q.setByte(pname, (Byte) pvalue);
				case SHORT :
					return q.setShort(pname, (Short) pvalue);
				case INT :
					return q.setInteger(pname, (Integer) pvalue);
				case LONG :
					return q.setLong(pname, (Long) pvalue);
				case FLOAT :
					return q.setFloat(pname, (Float) pvalue);
				case DOUBLE :
					return q.setDouble(pname, (Double) pvalue);
				case BINARY :
					return q.setBinary(pname, (byte[]) pvalue);
				case SERIAL :
					return q.setSerializable(pname, (Serializable) pvalue);
				case LOCALE :
					return q.setLocale(pname, (Locale) pvalue);
				case BIG_DECIMAL :
					return q.setBigDecimal(pname, (BigDecimal) pvalue);
				case BIG_INT :
					return q.setBigInteger(pname, (BigInteger) pvalue);
				case CALENDAR :
					return q.setCalendar(pname, (Calendar) pvalue);
				case CALENDAR_DATE :
					return q.setCalendarDate(pname, (Calendar) pvalue);
				case DATE :
					return q.setDate(pname, (Date) pvalue);
				case TIME :
					return q.setTime(pname, (Date) pvalue);
				case TIMESTAMP :
					return q.setTimestamp(pname, (Date) pvalue);
				case COLLECTION :
					return q.setParameterList(pname, (Collection<?>) pvalue);
			}
			return q;
		}
	}

	public static class QueryAttrs {
		private int maxResult = -1;
		private List<QueryParam> params = null;
		private List<Scalar> scalars = null;
		private boolean cacheable = false;
		private String cacheRegion = null;

		public QueryAttrs() {
		}

		public int getMaxResult() {
			return maxResult;
		}

		public QueryAttrs setMaxResult(int maxResult) {
			this.maxResult = maxResult;
			return this;
		}

		public List<QueryParam> getParams() {
			return (params == null ? Collections.<QueryParam> emptyList() : params);
		}

		public QueryAttrs setParams(List<QueryParam> params) {
			this.params = params;
			return this;
		}

		public List<Scalar> getScalars() {
			return (scalars == null ? Collections.<Scalar> emptyList() : scalars);
		}

		public QueryAttrs setScalars(List<Scalar> scalars) {
			this.scalars = scalars;
			return this;
		}

		public String getCacheRegion() {
			return cacheRegion;
		}

		public QueryAttrs setCacheRegion(String cacheRegion) {
			this.cacheRegion = cacheRegion;
			return this;
		}

		public boolean isCacheable() {
			return cacheable;
		}

		public QueryAttrs setCacheable(boolean cacheable) {
			this.cacheable = cacheable;
			return this;
		}

		private void addParam(QueryParam qp) {
			if (params == null) {
				params = new ArrayList<OrmHelper.QueryParam>();
			}
			params.add(qp);
		}

		public static QueryAttrs build() {
			return new QueryAttrs();
		}

		public QueryAttrs addScalar(String name, Type type) {
			if (scalars == null) {
				scalars = new ArrayList<OrmHelper.Scalar>();
			}
			scalars.add(new Scalar(name, type));
			return this;
		}

		public QueryAttrs setParam(String name, String val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.STRING, name, val));
			return this;
		}

		public QueryAttrs setParam(String name, char val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.CHAR, name, Character.valueOf(val)));
			return this;
		}

		public QueryAttrs setParam(String name, boolean val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.BOOLEAN, name, Boolean.valueOf(val)));
			return this;
		}

		public QueryAttrs setParam(String name, byte val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.BYTE, name, Byte.valueOf(val)));
			return this;
		}

		public QueryAttrs setParam(String name, short val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.SHORT, name, Short.valueOf(val)));
			return this;
		}

		public QueryAttrs setParam(String name, int val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.INT, name, Integer.valueOf(val)));
			return this;
		}

		public QueryAttrs setParam(String name, long val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.LONG, name, Long.valueOf(val)));
			return this;
		}

		public QueryAttrs setParam(String name, float val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.FLOAT, name, Float.valueOf(val)));
			return this;
		}

		public QueryAttrs setParam(String name, double val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.DOUBLE, name, Double.valueOf(val)));
			return this;
		}

		public QueryAttrs setParam(String name, byte[] val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.BINARY, name, val));
			return this;
		}

		public QueryAttrs setParam(String name, Serializable val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.SERIAL, name, val));
			return this;
		}

		public QueryAttrs setParam(String name, Locale val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.LOCALE, name, val));
			return this;
		}

		public QueryAttrs setParam(String name, BigDecimal val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.BIG_DECIMAL, name, val));
			return this;
		}

		public QueryAttrs setParam(String name, BigInteger val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.BIG_INT, name, val));
			return this;
		}

		public QueryAttrs setParam(String name, Date val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.DATE, name, val));
			return this;
		}

		public QueryAttrs setParam(String name, Calendar val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.CALENDAR, name, val));
			return this;
		}

		public QueryAttrs setParam(String name, Collection<?> val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.COLLECTION, name, val));
			return this;
		}

		public QueryAttrs setParamCalendarDate(String name, Calendar val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.CALENDAR_DATE, name, val));
			return this;
		}

		public QueryAttrs setParamTime(final String name, final Date val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.TIME, name, val));
			return this;
		}

		public QueryAttrs setParamTimestamp(final String name, final Date val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.TIMESTAMP, name, val));
			return this;
		}

		public QueryAttrs setParamText(final String name, final String val) {
			addParam(new QueryParam(QueryParam.PARAM_TYPE.TEXT, name, val));
			return this;
		}
	}
}