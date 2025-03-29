package com.unknown.db;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.logging.Logger;

import com.unknown.db.exception.DAOFactoryException;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;

public class DAOFactory {
	private static final Logger log = Trace.create(DAOFactory.class);

	public static <T extends DAO> T getDAO(Connection connection, Class<T> clazz) {
		if(clazz == null) {
			throw new DAOFactoryException(Messages.NO_DAO_CLASS);
		}
		try {
			try {
				Constructor<T> ctor = clazz.getConstructor(Connection.class);
				return ctor.newInstance(connection);
			} catch(NoSuchMethodException e) {
				throw new DAOFactoryException(Messages.NO_DAO_CTOR);
			}
		} catch(Exception e) {
			log.log(Levels.ERROR, Messages.DAO_INST_ERROR.format(e.getMessage()));
			throw new DAOFactoryException(Messages.DAO_INST_ERROR, e);
		}
	}
}
