/*
 * (C) Copyright ${year} retro-io (https://github.com/marcelschoen/retro-io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
