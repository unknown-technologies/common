package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.unknown.vm.memory.VirtualMemory;
import com.unknown.vm.posix.PosixEnvironment;
import com.unknown.vm.power.PowerCode;
import com.unknown.vm.power.PowerState;

public class PowerStateTest {
	private PowerState state;

	@Before
	public void setup() {
		VirtualMemory mem = new VirtualMemory();
		PosixEnvironment posix = new PosixEnvironment(mem, PowerCode::new, "ppc");
		state = new PowerState(mem, posix);
	}

	@Test
	public void testSetCr001() {
		state.setCR(0, false, false, false, false);
		assertEquals(0, state.cr);
	}

	@Test
	public void testSetCr002() {
		state.setCR(0, false, false, true, true);
		assertEquals(0x30000000, state.cr);
	}
}
