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
package com.retroio.tools.impl.c64;

/**
 * Hex	     Decimal		Description
 * 0000		0		Chip directional register
 * 0001		1		Chip I/O; memory & tape control
 * 0003-0004	3-4		Float-Fixed vector
 * 0005-0006	5-6		Fixed-Float vector
 * 0007		7		Search character
 * 0008		8		Scan-quotes flag
 * 0009		9		TAB column save
 * 000A		10		0 = LOAD, 1 = VERIFY
 * 000B		11		Input buffer pointer/# subscrpt
 * 000C		12		Default DIM flag
 * 000D		13		Type : FF = string, 00 = numeric
 * 000E		14		Type : 80 = integer, 00 = floating point
 * 000F		15		DATA scan/LIST quote/memry flag
 * 0010		16		Subscript/FNx flag
 * 0011		17		0 = INPUT; $40 = GET; $98 = READ
 * 0012		18		ATN sign/Comparison eval flag
 * 0013		19		Current I/O prompt flag
 * 0014-0015	20-21		Integer value
 * 0016		22		Pointer : temporary strg stack
 * 0017-0018	23-24		Last temp string vector
 * 0019-0021	25-33		Stack for temporary strings
 * 0022-0025	34-37		Utility pointer area
 * 0026-002A	38-42		Product area for multiplication
 * 002B-002C	43-44		Pointer : Start-of-Basic
 * 002D-002E	45-46		Pointer : Start-of-Variables
 * 002F-0030	47-48		Pointer : Start-of-Arrays
 * 0031-0032	49-50		Pointer : End-of-Arrays
 * 0033-0034	51-52		Pointer : String-storage(moving down)
 * 0035-0036	53-54		Utility string pointer
 * 0037-0038	55-56		Pointer : Limit-of-memory
 * 0039-003A	57-58		Current Basic line number
 * 003B-003C	59-60		Previous Basic line number
 * 003D-003E	61-62		Pointer : Basic statement for CONT
 * 003F-0040	63-64		Current DATA line number
 * 0041-0042	65-66		Current DATA address
 * 0043-0044	67-68		Input vector
 * 0045-0046	69-70		Current variable name
 * 0047-0048	71-72		Current variable address
 * 0049-004A	73-74		Variable pointer for FOR/NEXT
 * 004B-004C	75-76		Y-save; op-save; Basic pointer save
 * 004D		77		Comparison symbol accumulator
 * 004E-0053	78-83		Misc work area, pointer, etc
 * 0054-0056	84-86		Jump vector for functions
 * 0057-0060	87-96		Misc numeric work area
 * 0061		97		Accum#l : Exponent
 * 0062-0065	98-101		Accum#l : Mantissa
 * 0066		102		Accum#l : Sign
 * 0067		103		Series evaluation constant pointer
 * 0068		104		Accum#l hi-order (over flow)
 * 0069-006E	105-110	Accum#2 : Exponent, etc.
 * 006F		111		Sign comparison, Acc#l vs #2
 * 0070		112		Accum#l lo-order (rounding)
 * 0071-0072	113-114	Cassette buff len/Series pointer
 * 0073-008A	115-138	CHRGET subroutine; get Basic char
 * 007A-007B	122-123	Basic pointer (within subrtn)
 * 008B-008F	139-143	RND seed value
 * 0090		144		Status word ST
 * 0091		145		Keyswitch PIA : STOP and RVS flags
 * 0092		146		Timing constant for tape
 * 0093		147		Load = 0, Verify = l
 * 0094		148		Serial output : deferred char flag
 * 0095		149		Serial deferred character
 * 0096		150		Tape EOT received
 * 0097		151		Register save
 * 0098		152		How many open files
 * 0099		153		Input device, normally 0
 * 009A		154		Output CMD device, normally 3
 * 009B		155		Tape character parity
 * 009C		156		Byte-received flag
 * 009D		157		Direct = $80/RUN = 0 output control
 * 009E		158		Tp Pass 1 error log/char buffer
 * 009F		159		Tp Pass 2 err log corrected
 * 00A0-00A2	160-162	Jiffy Clock HML
 * 00A3		163		Serial bit count/EOI flag
 * 00A4		164		Cycle count
 * 00A5		165		Countdown, tape write/bit count
 * 00A6		166		Tape buffer pointer
 * 00A7		167		Tp Wrt ldr count/Rd pass/inbit
 * 00A8		168		Tp Wrt new byte/Rd error/inbit cnt
 * 00A9		169		Wrt start bit/Rd bit err/stbit
 * 00AA		170		Tp Scan; Cnt; Ld; End/byte assy
 * 00AB		171		Wr lead length/Rd checksum/parity
 * 00AC-00AD	172-173	Pointer : tape bufr, scrolling
 * 00AE-00AF	174-175	Tape end adds/End of program
 * 00B0-00B1	176-177	Tape timing constants
 * 00B2-00B3	178-179	Pntr : start of tape buffer
 * 00B4		180		l = Tp timer enabled; bit count
 * 00B5		181		Tp EOT/RS232 next bit to send
 * 00B6		182		Read character error/outbyte buf
 * 00B7		183		# characters in file name
 * 00B8		184		Current logical file
 * 00B9		185		Current secndy address
 * 00BA		186		Current device
 * 00BB-00BC	187-188	Pointer to file name
 * 00BD		189		Wr shift word/Rd input char
 * 00BE		190		# blocks remaining to Wr/Rd
 * 00BF		191		Serial word buffer
 * 00C0		192		Tape motor interlock
 * 00C1-00C2	193-194	I/O start address
 * 00C3-00C4	195-196	Kernel setup pointer
 * 00C5		197		Last key pressed
 * 00C6		198		# chars in keybd buffer
 * 00C7		199		Screen reverse flag
 * 00C8		200		End-of-line for input pointer
 * 00C9-00CA	201-202	Input cursor log (row, column)
 * 00CB		203		Which key :  64 if no key
 * 00CC		204		0 = flash cursor
 * 00CD		205		Cursor timing countdown
 * 00CE		206		Character under cursor
 * 00CF		207		Cursor in blink phase
 * 00D0		208		Input from screen/from keyboard
 * 00D1-00D2	209-210	Pointer to screen line
 * 00D3		211		Position of cursor on above line
 * 00D4		212		0 = direct cursor, else programmed
 * 00D5		213		Current screen line length
 * 00D6		214		Row where curosr lives
 * 00D7		215		Last inkey/checksum/buffer
 * 00D8		216		# of INSERTs outstanding
 * 00D9-00F2	217-242	Screen line link table
 * 00F3-00F4	243-244	Screen color pointer
 * 00F5-00F6	245-246	Keyboard pointer
 * 00F7-00F8	247-248	RS-232 Rev pntr
 * 00F9-00FA	249-250	RS-232 Tx pntr
 * 00FF-010A	256-266	Floating to ASCII work area
 * 0100-103E	256-318	Tape error log
 * 0100-01FF	256-511	Processor stack area
 * 0200-0258	512-600	Basic input buffer
 * 0259-0262	601-610	Logical file table
 * 0263-026C	611-620	Device # table
 * 026D-0276	621-630	Sec Adds table
 * 0277-0280	631-640	Keybd buffer
 * 0281-0282	641-642	Start of Basic Memory
 * 0283-0284	643-644	Top of Basic Memory
 * 0285		645		Serial bus timeout flag
 * 0286		646		Current color code
 * 0287		647		Color under cursor
 * 0288		648		Screen memory page
 * 0289		649		Max size of keybd buffer
 * 028A		650		Repeat all keys
 * 028B		651		Repeat speed counter
 * 028C		652		Repeat delay counter
 * 028D		653		Keyboard Shift/Control flag
 * 028E		654		Last shift pattern
 * 028F-0290	655-656	Keyboard table setup pointer
 * 0291		657		Keyboard shift mode
 * 0292		658		0 = scroll enable
 * 0293		659		RS-232 control reg
 * 0294		660		RS-232 command reg
 * 0295-0296	661-662	Bit timing
 * 0297		663		RS-232 status
 * 0298		664		# bits to send
 * 0299-029A	665		RS-232 speed/code
 * 029B		667		RS232 receive pointer
 * 029C		668		RS232 input pointer
 * 029D		669		RS232 transmit pointer
 * 029E		670		RS232 output pointer
 * 029F-02A0	671-672	IRQ save during tape I/O
 * 02A1		673		CIA 2 (NMI) Interrupt Control
 * 02A2		674		CIA 1 Timer A control log
 * 02A3		675		CIA 1 Interrupt Log
 * 02A4		676		CIA 1 Timer A enabled flag
 * 02A5		677		Screen row marker
 * 02C0-02FE	704-766	 (Sprite 11)
 * 0300-0301	768-769	Error message link
 * 0302-0303	770-771	Basic warm start link
 * 0304-0305	772-773	Crunch Basic tokens link
 * 0306-0307	774-775	Print tokens link
 * 0308-0309	776-777	Start new Basic code link
 * 030A-030B	778-779	Get arithmetic element link
 * 030C		780		SYS A-reg save
 * 030D		781		SYS X-reg save
 * 030E		782		SYS Y-reg save
 * 030F		783		SYS status reg save
 * 0310-0312	784-785	USR function jump (B248)
 * 0314-0315	788-789	Hardware interrupt vector (EA31)
 * 0316-0317	790-791	Break interrupt vector (FE66)
 * 0318-0319	792-793	NMI interrupt vector (FE47)
 * 031A-031B	794-795	OPEN vector (F34A)
 * 031C-031D	796-797	CLOSE vector (F291)
 * 031E-031F	798-799	Set - input vector (F20E)
 * 0320-0321	800-801	Set - output vector (F250)
 * 0322-0323	802-803	Restore I/0 vector (F333)
 * 0324-0325	804-805	INPUT vector (F157)
 * 0326-0327	806-807	Output vector (F1CA)
 * 0328-0329	808-809	Test-STOP vector (F6ED)
 * 032A-032B	810-811	GET vector (F13E)
 * 032C-032D	812-813	Abort I/o vector (F32F)
 * 032E-032F	814-815	Warm start vector (FE66)
 * 0330-0331	816-817	LOAD link (F4A5)
 * 0332-0333	818-819	SAVE link (F5ED)
 * 033C-03FB	828-1019	Cassette buffer
 * 0340-037E	832-894	(Sprite 13)
 * 0380-03BE	896-958	(Sprite 14)
 * 03C0-03FE	960-1022	(Sprite 15)
 * 0400-07FF	1024-2047	Screen memory
 * 0800-9FFF	2048-40959	Basic ROM memory
 * 8000-9FFF	32758-40959	Alternate: Rom plug-in area
 * A000-BFFF	40960-49151	ROM : Basic
 * A000-BFFF	49060-59151	Alternate: RAM
 * C000-CFFF	49152-53247	RAM memory, including alternate
 * D000-D02E	53248-53294	Video Chip (6566)
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * D400-D41C	54272-54300	Sound Chip (6581 SID)
 * D800-DBFF	55296-56319	Color nybble memory
 * DC00-DC0F	56320-56335	Interface chip 1, IRQ (6526 CIA)
 * DD00-DD0F	56576-56591	Interface chip 2, NMI (6526 CIA)
 * D000-DFFF	53248-53294	Alternate: Character set
 * E000-FFFF	57344-65535	ROM: Operating System
 * E000-FFFF	57344-65535	Alternate : RAM
 * FF81-FFF5	65409-65525	Jump Table, Including :
 * FFC6 - Set Input channel
 * FFC9 - Set Output channel
 * FFCC - Restore default I/O channels
 * FFCF - INPUT
 * FFD2 - PRINT
 * FFE1 - Test Stop Key
 * FFE4 - GET
 */

/**
 * C64 memory.
 *
 * @author Thomas Frauenknecht
 */
public abstract class Memory {

    protected Type type;

    ;
    protected int base;
    protected int size;
    protected String desc;
    protected byte[] memory;

    public Memory() {
    }

    public Memory(Type type, int base, int size, String desc) {
        this.type = type;
        this.base = base;
        this.size = size - 1;    // always include the first adress
        this.desc = desc;
        MemoryMap.addSegment(base, this);
    }

    /**
     * return the "adress" (double byte/word) contained at the designated adress
     * @param adr
     * @return
     */
    public static int peekWord(int adr) {
        Memory mem = MemoryMap.findSegment(adr);
//		System.out.print("[MEMORY] peekWord(); adr=" + DataUtils.toHex4(adr));
        int b0 = mem.get(adr);
        int b1 = mem.get(adr + 1);
        int ret = (b1 << 8) + b0;
//		System.out.println("[MACHINE] B0=" + DataUtils.toHex2(b0));
//		System.out.println("[MACHINE] B1=" + DataUtils.toHex2(b1));
//		System.out.println("[MACHINE] ret=" + DataUtils.toHex4(ret));
//		System.out.println(", value=" + DataUtils.toHex4(ret));
        return ret;
    }

    /**
     * return the "byte" contained at the designated adress
     * @param adr
     * @return
     */
    public static int peek(int adr) {
        Memory mem = MemoryMap.findSegment(adr);
        return mem.get(adr);
    }

    public static void poke(int adr, byte value) {
        Memory mem = MemoryMap.findSegment(adr);
        mem.set(adr, value);
    }

    private int get(int adr) {
        return (memory[adr - base] & 0xFF);
    }

    private void set(int adr, byte value) {
        memory[adr - base] = value;
    }

    public int getBase() {
        return base;
    }

    public String getDesc() {
        return desc;
    }

    public enum Type {ROM, RAM}
	/*
	public String toString() {
		//TODO: should not need (int) casting
		return "[" + type + "]: Begin: $" + DataUtils.toHex4((int)base) + ", Size: $" + DataUtils.toHex4((int)size);
	}
	*/
}
