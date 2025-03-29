package com.unknown.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.unknown.util.ResourceLoader;
import com.unknown.xml.dom.Element;
import com.unknown.xml.dom.XMLReader;

public class StatementCache {
	public static final String TABLE_PREFIX = "__TABLE_PREFIX__";
	public static final String XML_SQLNODE = "sql";
	public static final String XML_IDATTR = "id";

	private static Map<String, String> cache = new HashMap<>();

	private static Object lock = new Object();

	private static String getKey(Class<?> clazz, String key) {
		return clazz.getName() + "#" + key;
	}

	private static String getCachedStatement(Class<?> statementClass, String key) {
		String statement = getKey(statementClass, key);
		String result;
		synchronized(lock) {
			result = cache.get(statement);
			if(result == null) {
				loadSQLResource(statementClass);
				result = cache.get(statement);
			}
		}
		return result;
	}

	public static String getStatement(Class<?> statementClass, String key, String schema, String schemaSeparator) {
		String result = getCachedStatement(statementClass, key);
		if(result == null) {
			throw new RuntimeException("Unable to find a statement for key \"" + key + "\" for class " +
					statementClass);
		}

		if(schema != null) {
			if(schemaSeparator == null) {
				throw new RuntimeException("schemaSeparator must not be null");
			}
			String prefix = schema + schemaSeparator;
			result = result.replace(TABLE_PREFIX, prefix);
		}
		return result;
	}

	public static String getStatement(Class<?> statementClass, String key) {
		return getStatement(statementClass, key, null, null);
	}

	private static void loadSQLResource(Class<?> statementClass) {
		InputStream in = getResourceInputStream(statementClass);
		if(in != null) {
			Element root;
			try {
				root = XMLReader.read(in);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			Element[] l = root.getElementsByTagName(XML_SQLNODE);
			for(Element node : l) {
				String id = node.getAttribute(XML_IDATTR);
				String sql = node.getNodeValue();
				sql = sql.trim();
				cache.put(getKey(statementClass, id), sql);
			}
		} else {
			throw new RuntimeException("XML resource for class " + statementClass.getName() + " not found");
		}
	}

	private static InputStream getResourceInputStream(Class<?> statementClass) {
		String resourceName = resourceFromClass(statementClass);
		if(resourceName == null) {
			return null;
		}
		InputStream in = ResourceLoader.loadResource(statementClass, resourceName);
		return in;
	}

	private static String resourceFromClass(Class<?> statementClass) {
		String daoClassName = statementClass.getSimpleName();
		if(daoClassName != null) {
			return daoClassName + ".xml";
		} else {
			return null;
		}
	}
}
