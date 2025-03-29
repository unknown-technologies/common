package com.unknown.db.test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.unknown.db.LoggablePreparedStatement;

public class LoggablePreparedStatementTest {
	private LoggablePreparedStatement stmt;

	private void prepare(String sql) {
		stmt = new LoggablePreparedStatement(new DummyPreparedStatement(), sql);
	}

	@Before
	public void setup() {
		stmt = null;
	}

	@After
	public void teardown() throws SQLException {
		if(stmt != null) {
			stmt.close();
		}
	}

	@Test
	public void testNormalizeSpace1() {
		String sql = "SELECT * FROM SYSIBM.SYSDUMMY1";
		prepare(sql);
		assertEquals(sql, stmt.getStatement());
	}

	@Test
	public void testNormalizeSpace2() {
		String ref = "SELECT * FROM SYSIBM.SYSDUMMY1";
		String sql = "  SELECT  \n   *   FROM  SYSIBM.SYSDUMMY1  ";
		prepare(sql);
		assertEquals(ref, stmt.getStatement());
	}

	@Test
	public void testNormalizeSpace3() {
		String ref = "SELECT * FROM SYSIBM.SYSDUMMY1";
		String sql = "\tSELECT\n*\nFROM\tSYSIBM.SYSDUMMY1\n";
		prepare(sql);
		assertEquals(ref, stmt.getStatement());
	}

	@Test
	public void testNormalizeArgs1() throws SQLException {
		String sql = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = ?";
		prepare(sql);
		stmt.setTrace(false);
		stmt.setString(1, "Y");
		assertEquals(sql, stmt.getStatement());
	}

	@Test
	public void testNormalizeArgs2() throws SQLException {
		String sql = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = ?";
		String ref = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = 'Y'";
		prepare(sql);
		stmt.setTrace(true);
		stmt.setString(1, "Y");
		assertEquals(ref, stmt.getStatement());
	}

	@Test
	public void testNormalizeArgs3() throws SQLException {
		String sql = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = ? AND 'Z' = ?";
		String ref = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = 'Y' AND 'Z' = 'Z'";
		prepare(sql);
		stmt.setTrace(true);
		stmt.setString(1, "Y");
		stmt.setString(2, "Z");
		assertEquals(ref, stmt.getStatement());
	}

	@Test
	public void testNormalizeArgs4() throws SQLException {
		String sql = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = ? AND 'Z' = ?";
		String ref = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = 'N' AND 'Z' = 'Z'";
		prepare(sql);
		stmt.setTrace(true);
		stmt.setString(1, "Y");
		stmt.setString(2, "Z");
		stmt.setString(1, "N");
		assertEquals(ref, stmt.getStatement());
	}

	@Test
	public void testNormalizeArgs5() throws SQLException {
		String sql = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = ?";
		String ref = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = 42";
		prepare(sql);
		stmt.setTrace(true);
		stmt.setInt(1, 42);
		assertEquals(ref, stmt.getStatement());
	}

	@Test
	public void testNormalizeArgs6() throws SQLException {
		String sql = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = ?";
		String ref = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = 3.14";
		prepare(sql);
		stmt.setTrace(true);
		stmt.setFloat(1, 3.14f);
		assertEquals(ref, stmt.getStatement());
	}

	@Test
	public void testNormalizeArgs7() throws SQLException {
		String sql = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = ?";
		String ref = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = 3.14";
		prepare(sql);
		stmt.setTrace(true);
		stmt.setDouble(1, 3.14);
		assertEquals(ref, stmt.getStatement());
	}

	@Test
	public void testNormalizeArgs8() throws SQLException {
		String sql = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = ?";
		String ref = "SELECT * FROM SYSIBM.SYSDUMMY1 WHERE IBMREQD = 3141592765358979323";
		prepare(sql);
		stmt.setTrace(true);
		stmt.setLong(1, 3141592765358979323L);
		assertEquals(ref, stmt.getStatement());
	}

	@Test
	public void testFunction() throws SQLException {
		String sql = "DELETE FROM FAVORITE WHERE PAPER = VARCHAR_BIT_FORMAT(?, 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx') AND USER = VARCHAR_BIT_FORMAT(?, 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx')";
		String ref = "DELETE FROM FAVORITE WHERE PAPER = VARCHAR_BIT_FORMAT('356f03eb-9e9a-4ced-9724-1b6d8f9f1339', 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx') AND USER = VARCHAR_BIT_FORMAT('4b510a0a-7222-4349-894f-a3e8946afaa0', 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx')";
		prepare(sql);
		stmt.setTrace(true);
		stmt.setString(1, "356f03eb-9e9a-4ced-9724-1b6d8f9f1339");
		stmt.setString(2, "4b510a0a-7222-4349-894f-a3e8946afaa0");
		assertEquals(ref, stmt.getStatement());
	}
}
