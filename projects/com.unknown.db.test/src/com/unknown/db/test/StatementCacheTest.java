package com.unknown.db.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.db.StatementCache;

public class StatementCacheTest {
	@Test
	public void test1() {
		String ref = "SELECT CURRENT TIMESTAMP AS DBTIME FROM SYSIBM.SYSDUMMY1";
		String act = StatementCache.getStatement(getClass(), "get-time");
		assertEquals(ref, act);
	}

	@Test
	public void test2() {
		String ref = "SELECT *\n\t\tFROM SYSIBM.SYSDUMMY1";
		String act = StatementCache.getStatement(getClass(), "ping");
		assertEquals(ref, act);
	}
}
