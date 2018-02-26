package org.retro.common.impl.c64;

/**
 * C64 RAM
 *
 * @author Thomas Frauenknecht
 */
public class Ram extends Memory {

	public Ram(int adr, int size, String desc) {
		super(Memory.Type.RAM, adr, size, desc);
		memory = new byte[(int)size];
	}
	
	public byte[] getMemBuffer() {
		return memory;
	}
}
