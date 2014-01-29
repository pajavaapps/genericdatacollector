package com.javaapps.gdc.dao.orm;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.javaapps.gdc.entities.DeviceCheckinData;

@Repository
public class DeviceCheckinDAO {
	private SessionFactory sessionFactory;

	private Logger logger = Logger.getLogger(DeviceCheckinDAO.class);

		public DeviceCheckinData getById(Long id, boolean lock) throws Exception {
		Session session = getSession();
		DeviceCheckinData deviceCheckinData = null;
		try {
			if (lock) {
				deviceCheckinData = (DeviceCheckinData) session.load(
						DeviceCheckinData.class, id, LockMode.UPGRADE);
			} else {
				deviceCheckinData = (DeviceCheckinData) session.load(
						DeviceCheckinData.class, id);
			}
		} catch (HibernateException ex) {
			throw new Exception(ex);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return deviceCheckinData;
	}

	public Collection<DeviceCheckinData> findAll() throws Exception {
		Session session = null;
		Collection<DeviceCheckinData> deviceCheckinDataList;
		try {
			session = getSession();
			deviceCheckinDataList = session.createCriteria(
					DeviceCheckinData.class).list();
		} catch (HibernateException ex) {
			throw new Exception(ex);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return deviceCheckinDataList;
	}

	public Collection<DeviceCheckinData> findByExample(DeviceCheckinData example)
			throws Exception {
		Collection<DeviceCheckinData> coll;
		Session session = null;
		try {
			session = getSession();
			Criteria crit = session.createCriteria(DeviceCheckinData.class);
			coll = crit.add(Example.create(example)).list();
		} catch (HibernateException ex) {
			throw new Exception(ex);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return coll;
	}

	public void save(DeviceCheckinData deviceCheckinData) throws Exception {
		Session session = null;
		try {
			session = getSession();
			session.saveOrUpdate(deviceCheckinData);
			session.flush();
		} catch (HibernateException ex) {
			throw new Exception(ex);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public void delete(DeviceCheckinData deviceCheckinData) throws Exception {
		Session session = getSession();
		try {
			session.delete(deviceCheckinData);
			session.flush();
		} catch (HibernateException ex) {
			throw new Exception(ex);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	private Session getSession() {
		Session session= sessionFactory.openSession();
		return session;
	}

	@Resource
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}



}
