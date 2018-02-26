package org.retro.common.impl.c64;

/**
 * Simulates a C64 floppy file.
 *
 * @author Thomas Frauenknecht
 */
public class VC1541File {

	private int startTrack;
	private int startSector;
	private int type;
	private String fileName;
	private int numberOfBlocks;
	
	public VC1541File(int startTrack, int startSector, int type, String fileName, int numberOfBlocks) {
		this.type = type;
		this.startTrack = startTrack;
		this.fileName = fileName;
		this.numberOfBlocks = numberOfBlocks;
		this.startSector = startSector;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getStartTrack() {
		return startTrack;
	}

	public void setStartTrack(int startTrack) {
		this.startTrack = startTrack;
	}

	public int getStartSector() {
		return startSector;
	}

	public void setStartSector(int startSector) {
		this.startSector = startSector;
	}

	/**
	 * returns the filesize in blocks
	 * 
	 * @return file size in blocks
	 */
	public int getNumberOfBlocks() {
		return numberOfBlocks;
	}
}
