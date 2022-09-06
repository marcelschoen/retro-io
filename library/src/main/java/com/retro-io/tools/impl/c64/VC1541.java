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

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Simulates a C64 floppy drive VC1541.
 *
 * @author Thomas Frauenknecht
 */
public class VC1541 {

	public static final int BLOCK_SIZE = 256;
	
	private final int BAM_TRACK = 18;
	private final int BAM_SECTOR = 0;
	
	private final int DIR_TRACK = 18;
	private final int DIR_SECTOR = 1;
	
	private VC1541Directory directory;
	private boolean debug = false;
	
//	private BAM bam;
	
	/*
	Currently there are six different types of *.D64 formats:

	    * 683 Sectors (= 35 Tracks) without error info
	      (Total Size: 174848 Byte = 170.75 KByte)
	    * 683 Sectors (= 35 Tracks) with error info
	      (Total Size: 175531 Byte = 171.42 KByte)
	    * 768 Sectors (= 40 Tracks) without error info
	      (Total Size: 196608 Byte = 192 KByte)
	    * 768 Sectors (= 40 Tracks) with error info
	      (Total Size: 197376 Byte = 192.75 KByte)
	    * 802 Sectors (= 42 Tracks) without error info
	      (Total Size: 205312 Byte = 200.5 KByte)
	    * 802 Sectors (= 42 Tracks) with error info
	      (Total Size: 206114 Byte = 201.28 KByte) 

		Tracks 1..17 - 21 Sectors
		Tracks 18..24 - 19 Sectors
		Tracks 25..30 - 18 Sectors
		Tracks 31..35 - 17 Sectors
		Tracks 36..42 - 17 Sectors (non standard!) 

		The Block Allocation Map (BAM) is stored on track 18 - sector 0;
		the directory starts at track 18 - sector 1. 


http://www.softwolves.pp.se/idoc/alternative/vc1541_de/
----------------------------------------------------------------------
Tabelle 4 Format der VC-1540-BAM
Gespeichert auf : Spur   18; Sektor 0

BYTE   INHALT     BEDEUTUNG

0,1    18,01      Spur und Sektor des ersten Blocks der Directory
2      65         ASCII-Zeichen "A"; zeigt 1540-Fonnat an
3      0          0-Flag
4-143             Bitmuster der belegten bzw. nicht belegten Blocks
                  (0: belegt; 1: nicht belegt)
----------------------------------------------------------------------
Tabelle 5 : Vorspann (Header) der Directory
Gespeichert auf   : Spur 18; Sektor 0

BYTE     INHALT   BEDEUTUNG

144-161           Name der Diskette (ergänzt mit"geshifteten Spaces")
162,163           ID-Kennzeichnung der Diskette
164      160      "Geshifteter Space"

165,166  50,65    ASCII-Darstellung von 2A
                  (gibt DOS-Version und Format an)
166,167  160      "Geshiftete Spaces"
177-255  0        Nicht benutzt, aufgefüllt mit Nullen

Bem: Die Positionen 180 bis 191 können mit ASCII-Zeichen belegt
     sein.
----------------------------------------------------------------------
Tabelle 6 : Format der Directory
Gespeichert auf : Spur 18, Sektor 1

BYIE      INHALT

0,1       Spur und Sektor des nächsten Blocks der Directory
2-31      Eintrag  des  1. Files
34-63     Eintrag  des  2. Files
66-95     Eintrag  des  3. Files
98-128    Eintrag  des  4. Files
130-159   Eintrag  des  5. Files
162-191   Eintrag  des  6. Files
194-223   Eintrag  des  7. Files
226-255   Eintrag  des  8. Files
----------------------------------------------------------------------
Tabelle 7 : Format eines Directory-Eintrags
BYTE      INHALT

0         Filetyp (*) oder-verknüpft ("geort") mit $80
1,2       Spur und Sektor des ersten Datenblocks
3-18      Filename (ergänzt mit "geshifteten Spaces")
19,20     Nur bei relativen Files benutzt
21        Nur bei relativen Files benutzt          
22-25     Nicht benutzt
26,27     Spur und Sektor des neuen Files  beim  Oberschreiben mit
          dem (@) ("Klarmeraffe")
28,29     Anzahl der Blocks im File (Low Byte,  High Byte)

(*) Filetypen : 0 = DELeted
                1 = SEQuential
                2 = PROGram
                3 = USeR
                4 = RELative
----------------------------------------------------------------------

C 1541   ROM-Routinen

; LED einschalten

C100   78         SEI
C101   A9 F7      LDA #$F7
C103   2D 00 1C   AND $1C00
C106   48         PHA
C107   A5 7F      LDA $7F
C109   F0 05      BEQ $C110
C10B   68         PLA
C10C   09 00      ORA #$00
C10E   D0 03      BNE $C113
C110   68         PLA
C111   09 08      ORA #$08
C113   8D 00 1C   STA $1C00
C116   58         CLI
C117   60         RTS

; LED einschalten

C118   78         SEI
C119   A9 08      LDA #$08
C11B   0D 00 1C   ORA $1C00
C11E   8D 00 1C   STA $1C00
C121   58         CLI
C122   60         RTS

; Error-Flags löschen

C123   A9 00      LDA #$00
C125   8D 6C 02   STA $026C
C128   8D 6D 02   STA $026D
C12B   60         RTS

; LED-Flash vorbereiten – Errormeldung

C12C   78         SEI
C12D   8A         TXA
C12E   48         PHA
C12F   A9 50      LDA #$50
C131   8D 6C 02   STA $026C
C134   A2 00      LDX #$00
C136   BD CA FE   LDA $FECA,X
C139   8D 6D 02   STA $026D
C13C   0D 00 1C   ORA $1C00
C13F   8D 00 1C   STA $1C00
C142   68         PLA
C143   AA         TAX
C144   58         CLI
C145   60         RTS

; Befehl des Computers interpretieren

C146   A9 00      LDA #$00
C148   8D F9 02   STA $02F9
C14B   AD 8E 02   LDA $028E
C14E   85 7F      STA $7F
C150   20 BC E6   JSR $E6BC
C153   A5 84      LDA $84
C155   10 09      BPL $C160
C157   29 0F      AND #$0F
C159   C9 0F      CMP #$0F
C15B   F0 03      BEQ $C160
C15D   4C B4 D7   JMP $D7B4
C160   20 B3 C2   JSR $C2B3
C163   B1 A3      LDA ($A3),Y
C165   8D 75 02   STA $0275
C168   A2 0B      LDX #$0B
C16A   BD 89 FE   LDA $FE89,X
C16D   CD 75 02   CMP $0275
C170   F0 08      BEQ $C17A
C172   CA         DEX
C173   10 F5      BPL $C16A
C175   A9 31      LDA #$31
C177   4C C8 C1   JMP $C1C8
C17A   8E 2A 02   STX $022A
C17D   E0 09      CPX #$09
C17F   90 03      BCC $C184
C181   20 EE C1   JSR $C1EE
C184   AE 2A 02   LDX $022A
C187   BD 95 FE   LDA $FE95,X
C18A   85 6F      STA $6F
C18C   BD A1 FE   LDA $FEA1,X
C18F   85 70      STA $70
C191   6C 6F 00   JMP ($006F)

; Fehlermeldung nach Befehlsabarbeitung vorbereiten

C194   A9 00      LDA #$00
C196   8D F9 02   STA $02F9
C199   AD 6C 02   LDA $026C
C19C   D0 2A      BNE $C1C8
C19E   A0 00      LDY #$00
C1A0   98         TYA
C1A1   84 80      STY $80
C1A3   84 81      STY $81
C1A5   84 A3      STY $A3
C1A7   20 C7 E6   JSR $E6C7
C1AA   20 23 C1   JSR $C123
C1AD   A5 7F      LDA $7F
C1AF   8D 8E 02   STA $028E
C1B2   AA         TAX
C1B3   A9 00      LDA #$00
C1B5   95 FF      STA $FF,X
C1B7   20 BD C1   JSR $C1BD
C1BA   4C DA D4   JMP $D4DA

; Inputbuffer leeren

C1BD   A0 28      LDY #$28
C1BF   A9 00      LDA #$00
C1C1   99 00 02   STA $0200,Y
C1C4   88         DEY
C1C5   10 FA      BPL $C1C1
C1C7   60         RTS

; Fehlermeldung ausgeben (track and sector 0)

C1C8   A0 00      LDY #$00
C1CA   84 80      STY $80
C1CC   84 81      STY $81
C1CE   4C 45 E6   JMP $E645

; Eingabezeile überprüfen

C1D1   A2 00      LDX #$00
C1D3   8E 7A 02   STX $027A
C1D6   A9 3A      LDA #$3A
C1D8   20 68 C2   JSR $C268
C1DB   F0 05      BEQ $C1E2
C1DD   88         DEY
C1DE   88         DEY
C1DF   8C 7A 02   STY $027A
C1E2   4C 68 C3   JMP $C368

; Eingabezeile überprüfen

C1E5   A0 00      LDY #$00
C1E7   A2 00      LDX #$00
C1E9   A9 3A      LDA #$3A
C1EB   4C 68 C2   JMP $C268

; Eingabezeile überprüfen

C1EE   20 E5 C1   JSR $C1E5
C1F1   D0 05      BNE $C1F8
C1F3   A9 34      LDA #$34
C1F5   4C C8 C1   JMP $C1C8
C1F8   88         DEY
C1F9   88         DEY
C1FA   8C 7A 02   STY $027A
C1FD   8A         TXA
C1FE   D0 F3      BNE $C1F3
C200   A9 3D      LDA #$3D
C202   20 68 C2   JSR $C268
C205   8A         TXA
C206   F0 02      BEQ $C20A
C208   A9 40      LDA #$40
C20A   09 21      ORA #$21
C20C   8D 8B 02   STA $028B
C20F   E8         INX
C210   8E 77 02   STX $0277
C213   8E 78 02   STX $0278
C216   AD 8A 02   LDA $028A
C219   F0 0D      BEQ $C228
C21B   A9 80      LDA #$80
C21D   0D 8B 02   ORA $028B
C220   8D 8B 02   STA $028B
C223   A9 00      LDA #$00
C225   8D 8A 02   STA $028A
C228   98         TYA
C229   F0 29      BEQ $C254
C22B   9D 7A 02   STA $027A,X
C22E   AD 77 02   LDA $0277
C231   8D 79 02   STA $0279
C234   A9 8D      LDA #$8D
C236   20 68 C2   JSR $C268
C239   E8         INX
C23A   8E 78 02   STX $0278
C23D   CA         DEX
C23E   AD 8A 02   LDA $028A
C241   F0 02      BEQ $C245
C243   A9 08      LDA #$08
C245   EC 77 02   CPX $0277
C248   F0 02      BEQ $C24C
C24A   09 04      ORA #$04
C24C   09 03      ORA #$03
C24E   4D 8B 02   EOR $028B
C251   8D 8B 02   STA $028B
C254   AD 8B 02   LDA $028B
C257   AE 2A 02   LDX $022A
C25A   3D A5 FE   AND $FEA5,X
C25D   D0 01      BNE $C260
C25F   60         RTS
C260   8D 6C 02   STA $026C
C263   A9 30      LDA #$30
C265   4C C8 C1   JMP $C1C8

; Zeichen on Eingabepuffer suchen

C268   8D 75 02   STA $0275
C26B   CC 74 02   CPY $0274
C26E   B0 2E      BCS $C29E
C270   B1 A3      LDA ($A3),Y
C272   C8         INY
C273   CD 75 02   CMP $0275
C276   F0 28      BEQ $C2A0
C278   C9 2A      CMP #$2A
C27A   F0 04      BEQ $C280
C27C   C9 3F      CMP #$3F
C27E   D0 03      BNE $C283
C280   EE 8A 02   INC $028A
C283   C9 2C      CMP #$2C
C285   D0 E4      BNE $C26B
C287   98         TYA
C288   9D 7B 02   STA $027B,X
C28B   AD 8A 02   LDA $028A
C28E   29 7F      AND #$7F
C290   F0 07      BEQ $C299
C292   A9 80      LDA #$80
C294   95 E7      STA $E7,X
C296   8D 8A 02   STA $028A
C299   E8         INX
C29A   E0 04      CPX #$04
C29C   90 CD      BCC $C26B
C29E   A0 00      LDY #$00
C2A0   AD 74 02   LDA $0274
C2A3   9D 7B 02   STA $027B,X
C2A6   AD 8A 02   LDA $028A
C2A9   29 7F      AND #$7F
C2AB   F0 04      BEQ $C2B1
C2AD   A9 80      LDA #$80
C2AF   95 E7      STA $E7,X
C2B1   98         TYA
C2B2   60         RTS

; 'line length'-Zeiger in Befehl überprüfen

C2B3   A4 A3      LDY $A3
C2B5   F0 14      BEQ $C2CB
C2B7   88         DEY
C2B8   F0 10      BEQ $C2CA
C2BA   B9 00 02   LDA $0200,Y
C2BD   C9 0D      CMP #$0D
C2BF   F0 0A      BEQ $C2CB
C2C1   88         DEY
C2C2   B9 00 02   LDA $0200,Y
C2C5   C9 0D      CMP #$0D
C2C7   F0 02      BEQ $C2CB
C2C9   C8         INY
C2CA   C8         INY
C2CB   8C 74 02   STY $0274
C2CE   C0 2A      CPY #$2A
C2D0   A0 FF      LDY #$FF
C2D2   90 08      BCC $C2DC
C2D4   8C 2A 02   STY $022A
C2D7   A9 32      LDA #$32
C2D9   4C C8 C1   JMP $C1C8

; Flag für 'command input' löschen

C2DC   A0 00      LDY #$00
C2DE   98         TYA
C2DF   85 A3      STA $A3
C2E1   8D 58 02   STA $0258
C2E4   8D 4A 02   STA $024A
C2E7   8D 96 02   STA $0296
C2EA   85 D3      STA $D3
C2EC   8D 79 02   STA $0279
C2EF   8D 77 02   STA $0277
C2F2   8D 78 02   STA $0278
C2F5   8D 8A 02   STA $028A
C2F8   8D 6C 02   STA $026C
C2FB   A2 05      LDX #$05
C2FD   9D 79 02   STA $0279,X
C300   95 D7      STA $D7,X
C302   95 DC      STA $DC,X
C304   95 E1      STA $E1,X
C306   95 E6      STA $E6,X
C308   9D 7F 02   STA $027F,X
C30B   9D 84 02   STA $0284,X
C30E   CA         DEX
C30F   D0 EC      BNE $C2FD
C311   60         RTS

; Laufwerksnummer merken

C312   AD 78 02   LDA $0278
C315   8D 77 02   STA $0277
C318   A9 01      LDA #$01
C31A   8D 78 02   STA $0278
C31D   8D 79 02   STA $0279
C320   AC 8E 02   LDY $028E
C323   A2 00      LDX #$00
C325   86 D3      STX $D3
C327   BD 7A 02   LDA $027A,X
C32A   20 3C C3   JSR $C33C
C32D   A6 D3      LDX $D3
C32F   9D 7A 02   STA $027A,X
C332   98         TYA
C333   95 E2      STA $E2,X
C335   E8         INX
C336   EC 78 02   CPX $0278
C339   90 EA      BCC $C325
C33B   60         RTS

; Laufwerksnummer suchen

C33C   AA         TAX
C33D   A0 00      LDY #$00
C33F   A9 3A      LDA #$3A
C341   DD 01 02   CMP $0201,X
C344   F0 0C      BEQ $C352
C346   DD 00 02   CMP $0200,X
C349   D0 16      BNE $C361
C34B   E8         INX
C34C   98         TYA
C34D   29 01      AND #$01
C34F   A8         TAY
C350   8A         TXA
C351   60         RTS
C352   BD 00 02   LDA $0200,X
C355   E8         INX
C356   E8         INX
C357   C9 30      CMP #$30
C359   F0 F2      BEQ $C34D
C35B   C9 31      CMP #$31
C35D   F0 EE      BEQ $C34D
C35F   D0 EB      BNE $C34C
C361   98         TYA
C362   09 80      ORA #$80
C364   29 81      AND #$81
C366   D0 E7      BNE $C34F

; Laufwerksnummer holen

C368   A9 00      LDA #$00
C36A   8D 8B 02   STA $028B
C36D   AC 7A 02   LDY $027A
C370   B1 A3      LDA ($A3),Y
C372   20 BD C3   JSR $C3BD
C375   10 11      BPL $C388
C377   C8         INY
C378   CC 74 02   CPY $0274
C37B   B0 06      BCS $C383
C37D   AC 74 02   LDY $0274
C380   88         DEY
C381   D0 ED      BNE $C370
C383   CE 8B 02   DEC $028B
C386   A9 00      LDA #$00
C388   29 01      AND #$01
C38A   85 7F      STA $7F
C38C   4C 00 C1   JMP $C100

; Laufwerksnummer umkehren (8 <–> 9)

C38F   A5 7F      LDA $7F
C391   49 01      EOR #$01
C393   29 01      AND #$01
C395   85 7F      STA $7F
C397   60         RTS

;

C398   A0 00      LDY #$00
C39A   AD 77 02   LDA $0277
C39D   CD 78 02   CMP $0278
C3A0   F0 16      BEQ $C3B8
C3A2   CE 78 02   DEC $0278
C3A5   AC 78 02   LDY $0278
C3A8   B9 7A 02   LDA $027A,Y
C3AB   A8         TAY
C3AC   B1 A3      LDA ($A3),Y
C3AE   A0 04      LDY #$04
C3B0   D9 BB FE   CMP $FEBB,Y
C3B3   F0 03      BEQ $C3B8
C3B5   88         DEY
C3B6   D0 F8      BNE $C3B0
C3B8   98         TYA
C3B9   8D 96 02   STA $0296
C3BC   60         RTS

; check drive number

C3BD   C9 30      CMP #$30
C3BF   F0 06      BEQ $C3C7
C3C1   C9 31      CMP #$31
C3C3   F0 02      BEQ $C3C7
C3C5   09 80      ORA #$80
C3C7   29 81      AND #$81
C3C9   60         RTS

; verify drive number

C3CA   A9 00      LDA #$00
C3CC   85 6F      STA $6F
C3CE   8D 8D 02   STA $028D
C3D1   48         PHA
C3D2   AE 78 02   LDX $0278
C3D5   68         PLA
C3D6   05 6F      ORA $6F
C3D8   48         PHA
C3D9   A9 01      LDA #$01
C3DB   85 6F      STA $6F
C3DD   CA         DEX
C3DE   30 0F      BMI $C3EF
C3E0   B5 E2      LDA $E2,X
C3E2   10 04      BPL $C3E8
C3E4   06 6F      ASL $6F
C3E6   06 6F      ASL $6F
C3E8   4A         LSR
C3E9   90 EA      BCC $C3D5
C3EB   06 6F      ASL $6F
C3ED   D0 E6      BNE $C3D5
C3EF   68         PLA
C3F0   AA         TAX
C3F1   BD 3F C4   LDA $C43F,X
C3F4   48         PHA
C3F5   29 03      AND #$03
C3F7   8D 8C 02   STA $028C
C3FA   68         PLA
C3FB   0A         ASL
C3FC   10 3E      BPL $C43C
C3FE   A5 E2      LDA $E2
C400   29 01      AND #$01
C402   85 7F      STA $7F
C404   AD 8C 02   LDA $028C
C407   F0 2B      BEQ $C434
C409   20 3D C6   JSR $C63D
C40C   F0 12      BEQ $C420
C40E   20 8F C3   JSR $C38F
C411   A9 00      LDA #$00
C413   8D 8C 02   STA $028C
C416   20 3D C6   JSR $C63D
C419   F0 1E      BEQ $C439
C41B   A9 74      LDA #$74
C41D   20 C8 C1   JSR $C1C8
C420   20 8F C3   JSR $C38F
C423   20 3D C6   JSR $C63D
C426   08         PHP
C427   20 8F C3   JSR $C38F
C42A   28         PLP
C42B   F0 0C      BEQ $C439
C42D   A9 00      LDA #$00
C42F   8D 8C 02   STA $028C
C432   F0 05      BEQ $C439
C434   20 3D C6   JSR $C63D
C437   D0 E2      BNE $C41B
C439   4C 00 C1   JMP $C100
C43C   2A         ROL
C43D   4C 00 C4   JMP $C400

; flags for drive check

C440   .BY $00,$80,$41,$01
C444   .BY $01,$01,$01,$81
C448   .BY $81,$81,$81,$42
C44C   .BY $42,$42,$42

; search for file in directory

C44F   20 CA C3   JSR $C3CA
C452   A9 00      LDA #$00
C454   8D 92 02   STA $0292
C457   20 AC C5   JSR $C5AC
C45A   D0 19      BNE $C475
C45C   CE 8C 02   DEC $028C
C45F   10 01      BPL $C462
C461   60         RTS
C462   A9 01      LDA #$01
C464   8D 8D 02   STA $028D
C467   20 8F C3   JSR $C38F
C46A   20 00 C1   JSR $C100
C46D   4C 52 C4   JMP $C452
C470   20 17 C6   JSR $C617
C473   F0 10      BEQ $C485
C475   20 D8 C4   JSR $C4D8
C478   AD 8F 02   LDA $028F
C47B   F0 01      BEQ $C47E
C47D   60         RTS
C47E   AD 53 02   LDA $0253
C481   30 ED      BMI $C470
C483   10 F0      BPL $C475
C485   AD 8F 02   LDA $028F
C488   F0 D2      BEQ $C45C
C48A   60         RTS
C48B   20 04 C6   JSR $C604
C48E   F0 1A      BEQ $C4AA
C490   D0 28      BNE $C4BA
C492   A9 01      LDA #$01
C494   8D 8D 02   STA $028D
C497   20 8F C3   JSR $C38F
C49A   20 00 C1   JSR $C100
C49D   A9 00      LDA #$00
C49F   8D 92 02   STA $0292
C4A2   20 AC C5   JSR $C5AC
C4A5   D0 13      BNE $C4BA
C4A7   8D 8F 02   STA $028F
C4AA   AD 8F 02   LDA $028F
C4AD   D0 28      BNE $C4D7
C4AF   CE 8C 02   DEC $028C
C4B2   10 DE      BPL $C492
C4B4   60         RTS
C4B5   20 17 C6   JSR $C617
C4B8   F0 F0      BEQ $C4AA
C4BA   20 D8 C4   JSR $C4D8
C4BD   AE 53 02   LDX $0253
C4C0   10 07      BPL $C4C9
C4C2   AD 8F 02   LDA $028F
C4C5   F0 EE      BEQ $C4B5
C4C7   D0 0E      BNE $C4D7
C4C9   AD 96 02   LDA $0296
C4CC   F0 09      BEQ $C4D7
C4CE   B5 E7      LDA $E7,X
C4D0   29 07      AND #$07
C4D2   CD 96 02   CMP $0296
C4D5   D0 DE      BNE $C4B5
C4D7   60         RTS
C4D8   A2 FF      LDX #$FF
C4DA   8E 53 02   STX $0253
C4DD   E8         INX
C4DE   8E 8A 02   STX $028A
C4E1   20 89 C5   JSR $C589
C4E4   F0 06      BEQ $C4EC
C4E6   60         RTS
C4E7   20 94 C5   JSR $C594
C4EA   D0 FA      BNE $C4E6
C4EC   A5 7F      LDA $7F
C4EE   55 E2      EOR $E2,X
C4F0   4A         LSR
C4F1   90 0B      BCC $C4FE
C4F3   29 40      AND #$40
C4F5   F0 F0      BEQ $C4E7
C4F7   A9 02      LDA #$02
C4F9   CD 8C 02   CMP $028C
C4FC   F0 E9      BEQ $C4E7
C4FE   BD 7A 02   LDA $027A,X
C501   AA         TAX
C502   20 A6 C6   JSR $C6A6
C505   A0 03      LDY #$03
C507   4C 1D C5   JMP $C51D
C50A   BD 00 02   LDA $0200,X
C50D   D1 94      CMP ($94),Y
C50F   F0 0A      BEQ $C51B
C511   C9 3F      CMP #$3F
C513   D0 D2      BNE $C4E7
C515   B1 94      LDA ($94),Y
C517   C9 A0      CMP #$A0
C519   F0 CC      BEQ $C4E7
C51B   E8         INX
C51C   C8         INY
C51D   EC 76 02   CPX $0276
C520   B0 09      BCS $C52B
C522   BD 00 02   LDA $0200,X
C525   C9 2A      CMP #$2A
C527   F0 0C      BEQ $C535
C529   D0 DF      BNE $C50A
C52B   C0 13      CPY #$13
C52D   B0 06      BCS $C535
C52F   B1 94      LDA ($94),Y
C531   C9 A0      CMP #$A0
C533   D0 B2      BNE $C4E7
C535   AE 79 02   LDX $0279
C538   8E 53 02   STX $0253
C53B   B5 E7      LDA $E7,X
C53D   29 80      AND #$80
C53F   8D 8A 02   STA $028A
C542   AD 94 02   LDA $0294
C545   95 DD      STA $DD,X
C547   A5 81      LDA $81
C549   95 D8      STA $D8,X
C54B   A0 00      LDY #$00
C54D   B1 94      LDA ($94),Y
C54F   C8         INY
C550   48         PHA
C551   29 40      AND #$40
C553   85 6F      STA $6F
C555   68         PLA
C556   29 DF      AND #$DF
C558   30 02      BMI $C55C
C55A   09 20      ORA #$20
C55C   29 27      AND #$27
C55E   05 6F      ORA $6F
C560   85 6F      STA $6F
C562   A9 80      LDA #$80
C564   35 E7      AND $E7,X
C566   05 6F      ORA $6F
C568   95 E7      STA $E7,X
C56A   B5 E2      LDA $E2,X
C56C   29 80      AND #$80
C56E   05 7F      ORA $7F
C570   95 E2      STA $E2,X
C572   B1 94      LDA ($94),Y
C574   9D 80 02   STA $0280,X
C577   C8         INY
C578   B1 94      LDA ($94),Y
C57A   9D 85 02   STA $0285,X
C57D   AD 58 02   LDA $0258
C580   D0 07      BNE $C589
C582   A0 15      LDY #$15
C584   B1 94      LDA ($94),Y
C586   8D 58 02   STA $0258
C589   A9 FF      LDA #$FF
C58B   8D 8F 02   STA $028F
C58E   AD 78 02   LDA $0278
C591   8D 79 02   STA $0279
C594   CE 79 02   DEC $0279
C597   10 01      BPL $C59A
C599   60         RTS
C59A   AE 79 02   LDX $0279
C59D   B5 E7      LDA $E7,X
C59F   30 05      BMI $C5A6
C5A1   BD 80 02   LDA $0280,X
C5A4   D0 EE      BNE $C594
C5A6   A9 00      LDA #$00
C5A8   8D 8F 02   STA $028F
C5AB   60         RTS
C5AC   A0 00      LDY #$00
C5AE   8C 91 02   STY $0291
C5B1   88         DEY
C5B2   8C 53 02   STY $0253
C5B5   AD 85 FE   LDA $FE85
C5B8   85 80      STA $80
C5BA   A9 01      LDA #$01
C5BC   85 81      STA $81
C5BE   8D 93 02   STA $0293
C5C1   20 75 D4   JSR $D475
C5C4   AD 93 02   LDA $0293
C5C7   D0 01      BNE $C5CA
C5C9   60         RTS
C5CA   A9 07      LDA #$07
C5CC   8D 95 02   STA $0295
C5CF   A9 00      LDA #$00
C5D1   20 F6 D4   JSR $D4F6
C5D4   8D 93 02   STA $0293
C5D7   20 E8 D4   JSR $D4E8
C5DA   CE 95 02   DEC $0295
C5DD   A0 00      LDY #$00
C5DF   B1 94      LDA ($94),Y
C5E1   D0 18      BNE $C5FB
C5E3   AD 91 02   LDA $0291
C5E6   D0 2F      BNE $C617
C5E8   20 3B DE   JSR $DE3B
C5EB   A5 81      LDA $81
C5ED   8D 91 02   STA $0291
C5F0   A5 94      LDA $94
C5F2   AE 92 02   LDX $0292
C5F5   8D 92 02   STA $0292
C5F8   F0 1D      BEQ $C617
C5FA   60         RTS
C5FB   A2 01      LDX #$01
C5FD   EC 92 02   CPX $0292
C600   D0 2D      BNE $C62F
C602   F0 13      BEQ $C617
C604   AD 85 FE   LDA $FE85
C607   85 80      STA $80
C609   AD 90 02   LDA $0290
C60C   85 81      STA $81
C60E   20 75 D4   JSR $D475
C611   AD 94 02   LDA $0294
C614   20 C8 D4   JSR $D4C8
C617   A9 FF      LDA #$FF
C619   8D 53 02   STA $0253
C61C   AD 95 02   LDA $0295
C61F   30 08      BMI $C629
C621   A9 20      LDA #$20
C623   20 C6 D1   JSR $D1C6
C626   4C D7 C5   JMP $C5D7
C629   20 4D D4   JSR $D44D
C62C   4C C4 C5   JMP $C5C4
C62F   A5 94      LDA $94
C631   8D 94 02   STA $0294
C634   20 3B DE   JSR $DE3B
C637   A5 81      LDA $81
C639   8D 90 02   STA $0290
C63C   60         RTS

; test and initalise drive

C63D   A5 68      LDA $68
C63F   D0 28      BNE $C669
C641   A6 7F      LDX $7F
C643   56 1C      LSR $1C,X
C645   90 22      BCC $C669
C647   A9 FF      LDA #$FF
C649   8D 98 02   STA $0298
C64C   20 0E D0   JSR $D00E
C64F   A0 FF      LDY #$FF
C651   C9 02      CMP #$02
C653   F0 0A      BEQ $C65F
C655   C9 03      CMP #$03
C657   F0 06      BEQ $C65F
C659   C9 0F      CMP #$0F
C65B   F0 02      BEQ $C65F
C65D   A0 00      LDY #$00
C65F   A6 7F      LDX $7F
C661   98         TYA
C662   95 FF      STA $FF,X
C664   D0 03      BNE $C669
C666   20 42 D0   JSR $D042
C669   A6 7F      LDX $7F
C66B   B5 FF      LDA $FF,X
C66D   60         RTS

; name of file in directory buffer

C66E   48         PHA
C66F   20 A6 C6   JSR $C6A6
C672   20 88 C6   JSR $C688
C675   68         PLA
C676   38         SEC
C677   ED 4B 02   SBC $024B
C67A   AA         TAX
C67B   F0 0A      BEQ $C687
C67D   90 08      BCC $C687
C67F   A9 A0      LDA #$A0
C681   91 94      STA ($94),Y
C683   C8         INY
C684   CA         DEX
C685   D0 FA      BNE $C681
C687   60         RTS

;

C688   98         TYA
C689   0A         ASL
C68A   A8         TAY
C68B   B9 99 00   LDA $0099,Y
C68E   85 94      STA $94
C690   B9 9A 00   LDA $009A,Y
C693   85 95      STA $95
C695   A0 00      LDY #$00
C697   BD 00 02   LDA $0200,X
C69A   91 94      STA ($94),Y
C69C   C8         INY
C69D   F0 06      BEQ $C6A5
C69F   E8         INX
C6A0   EC 76 02   CPX $0276
C6A3   90 F2      BCC $C697
C6A5   60         RTS

; search for end of name in command

C6A6   A9 00      LDA #$00
C6A8   8D 4B 02   STA $024B
C6AB   8A         TXA
C6AC   48         PHA
C6AD   BD 00 02   LDA $0200,X
C6B0   C9 2C      CMP #$2C
C6B2   F0 14      BEQ $C6C8
C6B4   C9 3D      CMP #$3D
C6B6   F0 10      BEQ $C6C8
C6B8   EE 4B 02   INC $024B
C6BB   E8         INX
C6BC   A9 0F      LDA #$0F
C6BE   CD 4B 02   CMP $024B
C6C1   90 05      BCC $C6C8
C6C3   EC 74 02   CPX $0274
C6C6   90 E5      BCC $C6AD
C6C8   8E 76 02   STX $0276
C6CB   68         PLA
C6CC   AA         TAX
C6CD   60         RTS

;

C6CE   A5 83      LDA $83
C6D0   48         PHA
C6D1   A5 82      LDA $82
C6D3   48         PHA
C6D4   20 DE C6   JSR $C6DE
C6D7   68         PLA
C6D8   85 82      STA $82
C6DA   68         PLA
C6DB   85 83      STA $83
C6DD   60         RTS

;

C6DE   A9 11      LDA #$11
C6E0   85 83      STA $83
C6E2   20 EB D0   JSR $D0EB
C6E5   20 E8 D4   JSR $D4E8
C6E8   AD 53 02   LDA $0253
C6EB   10 0A      BPL $C6F7
C6ED   AD 8D 02   LDA $028D
C6F0   D0 0A      BNE $C6FC
C6F2   20 06 C8   JSR $C806
C6F5   18         CLC
C6F6   60         RTS
C6F7   AD 8D 02   LDA $028D
C6FA   F0 1F      BEQ $C71B
C6FC   CE 8D 02   DEC $028D
C6FF   D0 0D      BNE $C70E
C701   CE 8D 02   DEC $028D
C704   20 8F C3   JSR $C38F
C707   20 06 C8   JSR $C806
C70A   38         SEC
C70B   4C 8F C3   JMP $C38F
C70E   A9 00      LDA #$00
C710   8D 73 02   STA $0273
C713   8D 8D 02   STA $028D
C716   20 B7 C7   JSR $C7B7
C719   38         SEC
C71A   60         RTS
C71B   A2 18      LDX #$18
C71D   A0 1D      LDY #$1D
C71F   B1 94      LDA ($94),Y
C721   8D 73 02   STA $0273
C724   F0 02      BEQ $C728
C726   A2 16      LDX #$16
C728   88         DEY
C729   B1 94      LDA ($94),Y
C72B   8D 72 02   STA $0272
C72E   E0 16      CPX #$16
C730   F0 0A      BEQ $C73C
C732   C9 0A      CMP #$0A
C734   90 06      BCC $C73C
C736   CA         DEX
C737   C9 64      CMP #$64
C739   90 01      BCC $C73C
C73B   CA         DEX
C73C   20 AC C7   JSR $C7AC
C73F   B1 94      LDA ($94),Y
C741   48         PHA
C742   0A         ASL
C743   10 05      BPL $C74A
C745   A9 3C      LDA #$3C
C747   9D B2 02   STA $02B2,X
C74A   68         PLA
C74B   29 0F      AND #$0F
C74D   A8         TAY
C74E   B9 C5 FE   LDA $FEC5,Y
C751   9D B1 02   STA $02B1,X
C754   CA         DEX
C755   B9 C0 FE   LDA $FEC0,Y
C758   9D B1 02   STA $02B1,X
C75B   CA         DEX
C75C   B9 BB FE   LDA $FEBB,Y
C75F   9D B1 02   STA $02B1,X
C762   CA         DEX
C763   CA         DEX
C764   B0 05      BCS $C76B
C766   A9 2A      LDA #$2A
C768   9D B2 02   STA $02B2,X
C76B   A9 A0      LDA #$A0
C76D   9D B1 02   STA $02B1,X
C770   CA         DEX
C771   A0 12      LDY #$12
C773   B1 94      LDA ($94),Y
C775   9D B1 02   STA $02B1,X
C778   CA         DEX
C779   88         DEY
C77A   C0 03      CPY #$03
C77C   B0 F5      BCS $C773
C77E   A9 22      LDA #$22
C780   9D B1 02   STA $02B1,X
C783   E8         INX
C784   E0 20      CPX #$20
C786   B0 0B      BCS $C793
C788   BD B1 02   LDA $02B1,X
C78B   C9 22      CMP #$22
C78D   F0 04      BEQ $C793
C78F   C9 A0      CMP #$A0
C791   D0 F0      BNE $C783
C793   A9 22      LDA #$22
C795   9D B1 02   STA $02B1,X
C798   E8         INX
C799   E0 20      CPX #$20
C79B   B0 0A      BCS $C7A7
C79D   A9 7F      LDA #$7F
C79F   3D B1 02   AND $02B1,X
C7A2   9D B1 02   STA $02B1,X
C7A5   10 F1      BPL $C798
C7A7   20 B5 C4   JSR $C4B5
C7AA   38         SEC
C7AB   60         RTS

;

C7AC   A0 1B      LDY #$1B
C7AE   A9 20      LDA #$20
C7B0   99 B0 02   STA $02B0,Y
C7B3   88         DEY
C7B4   D0 FA      BNE $C7B0
C7B6   60         RTS

; create header with disk nmae

C7B7   20 19 F1   JSR $F119
C7BA   20 DF F0   JSR $F0DF
C7BD   20 AC C7   JSR $C7AC
C7C0   A9 FF      LDA #$FF
C7C2   85 6F      STA $6F
C7C4   A6 7F      LDX $7F
C7C6   8E 72 02   STX $0272
C7C9   A9 00      LDA #$00
C7CB   8D 73 02   STA $0273
C7CE   A6 F9      LDX $F9
C7D0   BD E0 FE   LDA $FEE0,X
C7D3   85 95      STA $95
C7D5   AD 88 FE   LDA $FE88
C7D8   85 94      STA $94
C7DA   A0 16      LDY #$16
C7DC   B1 94      LDA ($94),Y
C7DE   C9 A0      CMP #$A0
C7E0   D0 0B      BNE $C7ED
C7E2   A9 31      LDA #$31
C7E4   .BY $2C
C7E5   B1 94      LDA ($94),Y
C7E7   C9 A0      CMP #$A0
C7E9   D0 02      BNE $C7ED
C7EB   A9 20      LDA #$20
C7ED   99 B3 02   STA $02B3,Y
C7F0   88         DEY
C7F1   10 F2      BPL $C7E5
C7F3   A9 12      LDA #$12
C7F5   8D B1 02   STA $02B1
C7F8   A9 22      LDA #$22
C7FA   8D B2 02   STA $02B2
C7FD   8D C3 02   STA $02C3
C800   A9 20      LDA #$20
C802   8D C4 02   STA $02C4
C805   60         RTS

; create last line

C806   20 AC C7   JSR $C7AC
C809   A0 0B      LDY #$0B
C80B   B9 17 C8   LDA $C817,Y
C80E   99 B1 02   STA $02B1,Y
C811   88         DEY
C812   10 F7      BPL $C80B
C814   4C 4D EF   JMP $EF4D

; blocks free.

C817   .BY $42,$4C,$4F,$43,$4B,$53
C81D   .BY $20,$46,$52,$45,$45,$2E

; S - Scratch command

C823   20 98 C3   JSR $C398
C826   20 20 C3   JSR $C320
C829   20 CA C3   JSR $C3CA
C82C   A9 00      LDA #$00
C82E   85 86      STA $86
C830   20 9D C4   JSR $C49D
C833   30 3D      BMI $C872
C835   20 B7 DD   JSR $DDB7
C838   90 33      BCC $C86D
C83A   A0 00      LDY #$00
C83C   B1 94      LDA ($94),Y
C83E   29 40      AND #$40
C840   D0 2B      BNE $C86D
C842   20 B6 C8   JSR $C8B6
C845   A0 13      LDY #$13
C847   B1 94      LDA ($94),Y
C849   F0 0A      BEQ $C855
C84B   85 80      STA $80
C84D   C8         INY
C84E   B1 94      LDA ($94),Y
C850   85 81      STA $81
C852   20 7D C8   JSR $C87D
C855   AE 53 02   LDX $0253
C858   A9 20      LDA #$20
C85A   35 E7      AND $E7,X
C85C   D0 0D      BNE $C86B
C85E   BD 80 02   LDA $0280,X
C861   85 80      STA $80
C863   BD 85 02   LDA $0285,X
C866   85 81      STA $81
C868   20 7D C8   JSR $C87D
C86B   E6 86      INC $86
C86D   20 8B C4   JSR $C48B
C870   10 C3      BPL $C835
C872   A5 86      LDA $86
C874   85 80      STA $80
C876   A9 01      LDA #$01
C878   A0 00      LDY #$00
C87A   4C A3 C1   JMP $C1A3

; erase file

C87D   20 5F EF   JSR $EF5F
C880   20 75 D4   JSR $D475
C883   20 19 F1   JSR $F119
C886   B5 A7      LDA $A7,X
C888   C9 FF      CMP #$FF
C88A   F0 08      BEQ $C894
C88C   AD F9 02   LDA $02F9
C88F   09 40      ORA #$40
C891   8D F9 02   STA $02F9
C894   A9 00      LDA #$00
C896   20 C8 D4   JSR $D4C8
C899   20 56 D1   JSR $D156
C89C   85 80      STA $80
C89E   20 56 D1   JSR $D156
C8A1   85 81      STA $81
C8A3   A5 80      LDA $80
C8A5   D0 06      BNE $C8AD
C8A7   20 F4 EE   JSR $EEF4
C8AA   4C 27 D2   JMP $D227
C8AD   20 5F EF   JSR $EF5F
C8B0   20 4D D4   JSR $D44D
C8B3   4C 94 C8   JMP $C894

; erase dir entry

C8B6   A0 00      LDY #$00
C8B8   98         TYA
C8B9   91 94      STA ($94),Y
C8BB   20 5E DE   JSR $DE5E
C8BE   4C 99 D5   JMP $D599

; D - Backup command (UNUSED)

C8C1   A9 31      LDA #$31
C8C3   4C C8 C1   JMP $C1C8

; format disk

C8C6   A9 4C      LDA #$4C
C8C8   8D 00 06   STA $0600
C8CB   A9 C7      LDA #$C7
C8CD   8D 01 06   STA $0601
C8D0   A9 FA      LDA #$FA
C8D2   8D 02 06   STA $0602
C8D5   A9 03      LDA #$03
C8D7   20 D3 D6   JSR $D6D3
C8DA   A5 7F      LDA $7F
C8DC   09 E0      ORA #$E0
C8DE   85 03      STA $03
C8E0   A5 03      LDA $03
C8E2   30 FC      BMI $C8E0
C8E4   C9 02      CMP #$02
C8E6   90 07      BCC $C8EF
C8E8   A9 03      LDA #$03
C8EA   A2 00      LDX #$00
C8EC   4C 0A E6   JMP $E60A
C8EF   60         RTS

; C - Copy command

C8F0   A9 E0      LDA #$E0
C8F2   8D 4F 02   STA $024F
C8F5   20 D1 F0   JSR $F0D1
C8F8   20 19 F1   JSR $F119
C8FB   A9 FF      LDA #$FF
C8FD   95 A7      STA $A7,X
C8FF   A9 0F      LDA #$0F
C901   8D 56 02   STA $0256
C904   20 E5 C1   JSR $C1E5
C907   D0 03      BNE $C90C
C909   4C C1 C8   JMP $C8C1
C90C   20 F8 C1   JSR $C1F8
C90F   20 20 C3   JSR $C320
C912   AD 8B 02   LDA $028B
C915   29 55      AND #$55
C917   D0 0F      BNE $C928
C919   AE 7A 02   LDX $027A
C91C   BD 00 02   LDA $0200,X
C91F   C9 2A      CMP #$2A
C921   D0 05      BNE $C928
C923   A9 30      LDA #$30
C925   4C C8 C1   JMP $C1C8
C928   AD 8B 02   LDA $028B
C92B   29 D9      AND #$D9
C92D   D0 F4      BNE $C923
C92F   4C 52 C9   JMP $C952
C932   A9 00      LDA #$00
C934   8D 58 02   STA $0258
C937   8D 8C 02   STA $028C
C93A   8D 80 02   STA $0280
C93D   8D 81 02   STA $0281
C940   A5 E3      LDA $E3
C942   29 01      AND #$01
C944   85 7F      STA $7F
C946   09 01      ORA #$01
C948   8D 91 02   STA $0291
C94B   AD 7B 02   LDA $027B
C94E   8D 7A 02   STA $027A
C951   60         RTS
C952   20 4F C4   JSR $C44F
C955   AD 78 02   LDA $0278
C958   C9 03      CMP #$03
C95A   90 45      BCC $C9A1
C95C   A5 E2      LDA $E2
C95E   C5 E3      CMP $E3
C960   D0 3F      BNE $C9A1
C962   A5 DD      LDA $DD
C964   C5 DE      CMP $DE
C966   D0 39      BNE $C9A1
C968   A5 D8      LDA $D8
C96A   C5 D9      CMP $D9
C96C   D0 33      BNE $C9A1
C96E   20 CC CA   JSR $CACC
C971   A9 01      LDA #$01
C973   8D 79 02   STA $0279
C976   20 FA C9   JSR $C9FA
C979   20 25 D1   JSR $D125
C97C   F0 04      BEQ $C982
C97E   C9 02      CMP #$02
C980   D0 05      BNE $C987
C982   A9 64      LDA #$64
C984   20 C8 C1   JSR $C1C8
C987   A9 12      LDA #$12
C989   85 83      STA $83
C98B   AD 3C 02   LDA $023C
C98E   8D 3D 02   STA $023D
C991   A9 FF      LDA #$FF
C993   8D 3C 02   STA $023C
C996   20 2A DA   JSR $DA2A
C999   A2 02      LDX #$02
C99B   20 B9 C9   JSR $C9B9
C99E   4C 94 C1   JMP $C194
C9A1   20 A7 C9   JSR $C9A7
C9A4   4C 94 C1   JMP $C194
C9A7   20 E7 CA   JSR $CAE7
C9AA   A5 E2      LDA $E2
C9AC   29 01      AND #$01
C9AE   85 7F      STA $7F
C9B0   20 86 D4   JSR $D486
C9B3   20 E4 D6   JSR $D6E4
C9B6   AE 77 02   LDX $0277
C9B9   8E 79 02   STX $0279
C9BC   20 FA C9   JSR $C9FA
C9BF   A9 11      LDA #$11
C9C1   85 83      STA $83
C9C3   20 EB D0   JSR $D0EB
C9C6   20 25 D1   JSR $D125
C9C9   D0 03      BNE $C9CE
C9CB   20 53 CA   JSR $CA53
C9CE   A9 08      LDA #$08
C9D0   85 F8      STA $F8
C9D2   4C D8 C9   JMP $C9D8
C9D5   20 9B CF   JSR $CF9B
C9D8   20 35 CA   JSR $CA35
C9DB   A9 80      LDA #$80
C9DD   20 A6 DD   JSR $DDA6
C9E0   F0 F3      BEQ $C9D5
C9E2   20 25 D1   JSR $D125
C9E5   F0 03      BEQ $C9EA
C9E7   20 9B CF   JSR $CF9B
C9EA   AE 79 02   LDX $0279
C9ED   E8         INX
C9EE   EC 78 02   CPX $0278
C9F1   90 C6      BCC $C9B9
C9F3   A9 12      LDA #$12
C9F5   85 83      STA $83
C9F7   4C 02 DB   JMP $DB02
C9FA   AE 79 02   LDX $0279
C9FD   B5 E2      LDA $E2,X
C9FF   29 01      AND #$01
CA01   85 7F      STA $7F
CA03   AD 85 FE   LDA $FE85
CA06   85 80      STA $80
CA08   B5 D8      LDA $D8,X
CA0A   85 81      STA $81
CA0C   20 75 D4   JSR $D475
CA0F   AE 79 02   LDX $0279
CA12   B5 DD      LDA $DD,X
CA14   20 C8 D4   JSR $D4C8
CA17   AE 79 02   LDX $0279
CA1A   B5 E7      LDA $E7,X
CA1C   29 07      AND #$07
CA1E   8D 4A 02   STA $024A
CA21   A9 00      LDA #$00
CA23   8D 58 02   STA $0258
CA26   20 A0 D9   JSR $D9A0
CA29   A0 01      LDY #$01
CA2B   20 25 D1   JSR $D125
CA2E   F0 01      BEQ $CA31
CA30   C8         INY
CA31   98         TYA
CA32   4C C8 D4   JMP $D4C8
CA35   A9 11      LDA #$11
CA37   85 83      STA $83
CA39   20 9B D3   JSR $D39B
CA3C   85 85      STA $85
CA3E   A6 82      LDX $82
CA40   B5 F2      LDA $F2,X
CA42   29 08      AND #$08
CA44   85 F8      STA $F8
CA46   D0 0A      BNE $CA52
CA48   20 25 D1   JSR $D125
CA4B   F0 05      BEQ $CA52
CA4D   A9 80      LDA #$80
CA4F   20 97 DD   JSR $DD97
CA52   60         RTS
CA53   20 D3 D1   JSR $D1D3
CA56   20 CB E1   JSR $E1CB
CA59   A5 D6      LDA $D6
CA5B   48         PHA
CA5C   A5 D5      LDA $D5
CA5E   48         PHA
CA5F   A9 12      LDA #$12
CA61   85 83      STA $83
CA63   20 07 D1   JSR $D107
CA66   20 D3 D1   JSR $D1D3
CA69   20 CB E1   JSR $E1CB
CA6C   20 9C E2   JSR $E29C
CA6F   A5 D6      LDA $D6
CA71   85 87      STA $87
CA73   A5 D5      LDA $D5
CA75   85 86      STA $86
CA77   A9 00      LDA #$00
CA79   85 88      STA $88
CA7B   85 D4      STA $D4
CA7D   85 D7      STA $D7
CA7F   68         PLA
CA80   85 D5      STA $D5
CA82   68         PLA
CA83   85 D6      STA $D6
CA85   4C 3B E3   JMP $E33B

; R - Rename command

CA88   20 20 C3   JSR $C320
CA8B   A5 E3      LDA $E3
CA8D   29 01      AND #$01
CA8F   85 E3      STA $E3
CA91   C5 E2      CMP $E2
CA93   F0 02      BEQ $CA97
CA95   09 80      ORA #$80
CA97   85 E2      STA $E2
CA99   20 4F C4   JSR $C44F
CA9C   20 E7 CA   JSR $CAE7
CA9F   A5 E3      LDA $E3
CAA1   29 01      AND #$01
CAA3   85 7F      STA $7F
CAA5   A5 D9      LDA $D9
CAA7   85 81      STA $81
CAA9   20 57 DE   JSR $DE57
CAAC   20 99 D5   JSR $D599
CAAF   A5 DE      LDA $DE
CAB1   18         CLC
CAB2   69 03      ADC #$03
CAB4   20 C8 D4   JSR $D4C8
CAB7   20 93 DF   JSR $DF93
CABA   A8         TAY
CABB   AE 7A 02   LDX $027A
CABE   A9 10      LDA #$10
CAC0   20 6E C6   JSR $C66E
CAC3   20 5E DE   JSR $DE5E
CAC6   20 99 D5   JSR $D599
CAC9   4C 94 C1   JMP $C194

; check if file present

CACC   A5 E8      LDA $E8
CACE   29 07      AND #$07
CAD0   8D 4A 02   STA $024A
CAD3   AE 78 02   LDX $0278
CAD6   CA         DEX
CAD7   EC 77 02   CPX $0277
CADA   90 0A      BCC $CAE6
CADC   BD 80 02   LDA $0280,X
CADF   D0 F5      BNE $CAD6
CAE1   A9 62      LDA #$62
CAE3   4C C8 C1   JMP $C1C8
CAE6   60         RTS
CAE7   20 CC CA   JSR $CACC
CAEA   BD 80 02   LDA $0280,X
CAED   F0 05      BEQ $CAF4
CAEF   A9 63      LDA #$63
CAF1   4C C8 C1   JMP $C1C8
CAF4   CA         DEX
CAF5   10 F3      BPL $CAEA
CAF7   60         RTS

; M - Memory command

CAF8   AD 01 02   LDA $0201
CAFB   C9 2D      CMP #$2D
CAFD   D0 4C      BNE $CB4B
CAFF   AD 03 02   LDA $0203
CB02   85 6F      STA $6F
CB04   AD 04 02   LDA $0204
CB07   85 70      STA $70
CB09   A0 00      LDY #$00
CB0B   AD 02 02   LDA $0202
CB0E   C9 52      CMP #$52
CB10   F0 0E      BEQ $CB20
CB12   20 58 F2   JSR $F258
CB15   C9 57      CMP #$57
CB17   F0 37      BEQ $CB50
CB19   C9 45      CMP #$45
CB1B   D0 2E      BNE $CB4B
CB1D   6C 6F 00   JMP ($006F)

; M-R memory read

CB20   B1 6F      LDA ($6F),Y
CB22   85 85      STA $85
CB24   AD 74 02   LDA $0274
CB27   C9 06      CMP #$06
CB29   90 1A      BCC $CB45
CB2B   AE 05 02   LDX $0205
CB2E   CA         DEX
CB2F   F0 14      BEQ $CB45
CB31   8A         TXA
CB32   18         CLC
CB33   65 6F      ADC $6F
CB35   E6 6F      INC $6F
CB37   8D 49 02   STA $0249
CB3A   A5 6F      LDA $6F
CB3C   85 A5      STA $A5
CB3E   A5 70      LDA $70
CB40   85 A6      STA $A6
CB42   4C 43 D4   JMP $D443
CB45   20 EB D0   JSR $D0EB
CB48   4C 3A D4   JMP $D43A
CB4B   A9 31      LDA #$31
CB4D   4C C8 C1   JMP $C1C8

; M-W momory write

CB50   B9 06 02   LDA $0206,Y
CB53   91 6F      STA ($6F),Y
CB55   C8         INY
CB56   CC 05 02   CPY $0205
CB59   90 F5      BCC $CB50
CB5B   60         RTS

; U - User command

CB5C   AC 01 02   LDY $0201
CB5F   C0 30      CPY #$30
CB61   D0 09      BNE $CB6C
CB63   A9 EA      LDA #$EA
CB65   85 6B      STA $6B
CB67   A9 FF      LDA #$FF
CB69   85 6C      STA $6C
CB6B   60         RTS
CB6C   20 72 CB   JSR $CB72
CB6F   4C 94 C1   JMP $C194
CB72   88         DEY
CB73   98         TYA
CB74   29 0F      AND #$0F
CB76   0A         ASL
CB77   A8         TAY
CB78   B1 6B      LDA ($6B),Y
CB7A   85 75      STA $75
CB7C   C8         INY
CB7D   B1 6B      LDA ($6B),Y
CB7F   85 76      STA $76
CB81   6C 75 00   JMP ($0075)

; open direct access channel, number

CB84   AD 8E 02   LDA $028E
CB87   85 7F      STA $7F
CB89   A5 83      LDA $83
CB8B   48         PHA
CB8C   20 3D C6   JSR $C63D
CB8F   68         PLA
CB90   85 83      STA $83
CB92   AE 74 02   LDX $0274
CB95   CA         DEX
CB96   D0 0D      BNE $CBA5
CB98   A9 01      LDA #$01
CB9A   20 E2 D1   JSR $D1E2
CB9D   4C F1 CB   JMP $CBF1
CBA0   A9 70      LDA #$70
CBA2   4C C8 C1   JMP $C1C8
CBA5   A0 01      LDY #$01
CBA7   20 7C CC   JSR $CC7C
CBAA   AE 85 02   LDX $0285
CBAD   E0 05      CPX #$05
CBAF   B0 EF      BCS $CBA0
CBB1   A9 00      LDA #$00
CBB3   85 6F      STA $6F
CBB5   85 70      STA $70
CBB7   38         SEC
CBB8   26 6F      ROL $6F
CBBA   26 70      ROL $70
CBBC   CA         DEX
CBBD   10 F9      BPL $CBB8
CBBF   A5 6F      LDA $6F
CBC1   2D 4F 02   AND $024F
CBC4   D0 DA      BNE $CBA0
CBC6   A5 70      LDA $70
CBC8   2D 50 02   AND $0250
CBCB   D0 D3      BNE $CBA0
CBCD   A5 6F      LDA $6F
CBCF   0D 4F 02   ORA $024F
CBD2   8D 4F 02   STA $024F
CBD5   A5 70      LDA $70
CBD7   0D 50 02   ORA $0250
CBDA   8D 50 02   STA $0250
CBDD   A9 00      LDA #$00
CBDF   20 E2 D1   JSR $D1E2
CBE2   A6 82      LDX $82
CBE4   AD 85 02   LDA $0285
CBE7   95 A7      STA $A7,X
CBE9   AA         TAX
CBEA   A5 7F      LDA $7F
CBEC   95 00      STA $00,X
CBEE   9D 5B 02   STA $025B,X
CBF1   A6 83      LDX $83
CBF3   BD 2B 02   LDA $022B,X
CBF6   09 40      ORA #$40
CBF8   9D 2B 02   STA $022B,X
CBFB   A4 82      LDY $82
CBFD   A9 FF      LDA #$FF
CBFF   99 44 02   STA $0244,Y
CC02   A9 89      LDA #$89
CC04   99 F2 00   STA $00F2,Y
CC07   B9 A7 00   LDA $00A7,Y
CC0A   99 3E 02   STA $023E,Y
CC0D   0A         ASL
CC0E   AA         TAX
CC0F   A9 01      LDA #$01
CC11   95 99      STA $99,X
CC13   A9 0E      LDA #$0E
CC15   99 EC 00   STA $00EC,Y
CC18   4C 94 C1   JMP $C194

; B - Block command

CC1B   A0 00      LDY #$00
CC1D   A2 00      LDX #$00
CC1F   A9 2D      LDA #$2D
CC21   20 68 C2   JSR $C268
CC24   D0 0A      BNE $CC30
CC26   A9 31      LDA #$31
CC28   4C C8 C1   JMP $C1C8
CC2B   A9 30      LDA #$30
CC2D   4C C8 C1   JMP $C1C8
CC30   8A         TXA
CC31   D0 F8      BNE $CC2B
CC33   A2 05      LDX #$05
CC35   B9 00 02   LDA $0200,Y
CC38   DD 5D CC   CMP $CC5D,X
CC3B   F0 05      BEQ $CC42
CC3D   CA         DEX
CC3E   10 F8      BPL $CC38
CC40   30 E4      BMI $CC26
CC42   8A         TXA
CC43   09 80      ORA #$80
CC45   8D 2A 02   STA $022A
CC48   20 6F CC   JSR $CC6F
CC4B   AD 2A 02   LDA $022A
CC4E   0A         ASL
CC4F   AA         TAX
CC50   BD 64 CC   LDA $CC63,X
CC53   85 70      STA $70
CC55   BD 63 CC   LDA $CC63,X
CC58   85 6F      STA $6F
CC5A   6C 6F 00   JMP ($006F)

; block commands "AFRWEP"

CC5D   .BY $41,$46,$52,$57,$45,$50

; addresses of block commands

CC63   .WD $CD03
CC65   .WD $CCF5
CC67   .WD $CD56
CC69   .WD $CD73
CC6B   .WD $CDA3
CC6D   .WD $CDBD

; get parameters form block commands

CC6F   A0 00      LDY #$00
CC71   A2 00      LDX #$00
CC73   A9 3A      LDA #$3A
CC75   20 68 C2   JSR $C268
CC78   D0 02      BNE $CC7C
CC7A   A0 03      LDY #$03
CC7C   B9 00 02   LDA $0200,Y
CC7F   C9 20      CMP #$20
CC81   F0 08      BEQ $CC8B
CC83   C9 1D      CMP #$1D
CC85   F0 04      BEQ $CC8B
CC87   C9 2C      CMP #$2C
CC89   D0 07      BNE $CC92
CC8B   C8         INY
CC8C   CC 74 02   CPY $0274
CC8F   90 EB      BCC $CC7C
CC91   60         RTS
CC92   20 A1 CC   JSR $CCA1
CC95   EE 77 02   INC $0277
CC98   AC 79 02   LDY $0279
CC9B   E0 04      CPX #$04
CC9D   90 EC      BCC $CC8B
CC9F   B0 8A      BCS $CC2B
CCA1   A9 00      LDA #$00
CCA3   85 6F      STA $6F
CCA5   85 70      STA $70
CCA7   85 72      STA $72
CCA9   A2 FF      LDX #$FF
CCAB   B9 00 02   LDA $0200,Y
CCAE   C9 40      CMP #$40
CCB0   B0 18      BCS $CCCA
CCB2   C9 30      CMP #$30
CCB4   90 14      BCC $CCCA
CCB6   29 0F      AND #$0F
CCB8   48         PHA
CCB9   A5 70      LDA $70
CCBB   85 71      STA $71
CCBD   A5 6F      LDA $6F
CCBF   85 70      STA $70
CCC1   68         PLA
CCC2   85 6F      STA $6F
CCC4   C8         INY
CCC5   CC 74 02   CPY $0274
CCC8   90 E1      BCC $CCAB
CCCA   8C 79 02   STY $0279
CCCD   18         CLC
CCCE   A9 00      LDA #$00
CCD0   E8         INX
CCD1   E0 03      CPX #$03
CCD3   B0 0F      BCS $CCE4
CCD5   B4 6F      LDY $6F,X
CCD7   88         DEY
CCD8   30 F6      BMI $CCD0
CCDA   7D F2 CC   ADC $CCF2,X
CCDD   90 F8      BCC $CCD7
CCDF   18         CLC
CCE0   E6 72      INC $72
CCE2   D0 F3      BNE $CCD7
CCE4   48         PHA
CCE5   AE 77 02   LDX $0277
CCE8   A5 72      LDA $72
CCEA   9D 80 02   STA $0280,X
CCED   68         PLA
CCEE   9D 85 02   STA $0285,X
CCF1   60         RTS

; decimal values  1, 10, 100

CCF2   .BY $01,$0A,$64

; B-F block free

CCF5   20 F5 CD   JSR $CDF5
CCF8   20 5F EF   JSR $EF5F
CCFB   4C 94 C1   JMP $C194

;

CCFE   A9 01      LDA #$01
CD00   8D F9 02   STA $02F9

; B-A block allocate

CD03   20 F5 CD   JSR $CDF5
CD06   A5 81      LDA $81
CD08   48         PHA
CD09   20 FA F1   JSR $F1FA
CD0C   F0 0B      BEQ $CD19
CD0E   68         PLA
CD0F   C5 81      CMP $81
CD11   D0 19      BNE $CD2C
CD13   20 90 EF   JSR $EF90
CD16   4C 94 C1   JMP $C194
CD19   68         PLA
CD1A   A9 00      LDA #$00
CD1C   85 81      STA $81
CD1E   E6 80      INC $80
CD20   A5 80      LDA $80
CD22   CD D7 FE   CMP $FED7
CD25   B0 0A      BCS $CD31
CD27   20 FA F1   JSR $F1FA
CD2A   F0 EE      BEQ $CD1A
CD2C   A9 65      LDA #$65
CD2E   20 45 E6   JSR $E645
CD31   A9 65      LDA #$65
CD33   20 C8 C1   JSR $C1C8

;

CD36   20 F2 CD   JSR $CDF2
CD39   4C 60 D4   JMP $D460

; get byte from buffer

CD3C   20 2F D1   JSR $D12F
CD3F   A1 99      LDA ($99,X)
CD41   60         RTS

; read block from disk

CD42   20 36 CD   JSR $CD36
CD45   A9 00      LDA #$00
CD47   20 C8 D4   JSR $D4C8
CD4A   20 3C CD   JSR $CD3C
CD4D   99 44 02   STA $0244,Y
CD50   A9 89      LDA #$89
CD52   99 F2 00   STA $00F2,Y
CD55   60         RTS

; B-R block read

CD56   20 42 CD   JSR $CD42
CD59   20 EC D3   JSR $D3EC
CD5C   4C 94 C1   JMP $C194

; U1 substitute for block read

CD5F   20 6F CC   JSR $CC6F
CD62   20 42 CD   JSR $CD42
CD65   B9 44 02   LDA $0244,Y
CD68   99 3E 02   STA $023E,Y
CD6B   A9 FF      LDA #$FF
CD6D   99 44 02   STA $0244,Y
CD70   4C 94 C1   JMP $C194

; B-W block write

CD73   20 F2 CD   JSR $CDF2
CD76   20 E8 D4   JSR $D4E8
CD79   A8         TAY
CD7A   88         DEY
CD7B   C9 02      CMP #$02
CD7D   B0 02      BCS $CD81
CD7F   A0 01      LDY #$01
CD81   A9 00      LDA #$00
CD83   20 C8 D4   JSR $D4C8
CD86   98         TYA
CD87   20 F1 CF   JSR $CFF1
CD8A   8A         TXA
CD8B   48         PHA
CD8C   20 64 D4   JSR $D464
CD8F   68         PLA
CD90   AA         TAX
CD91   20 EE D3   JSR $D3EE
CD94   4C 94 C1   JMP $C194

; U2 substitute for block write

CD97   20 6F CC   JSR $CC6F
CD9A   20 F2 CD   JSR $CDF2
CD9D   20 64 D4   JSR $D464
CDA0   4C 94 C1   JMP $C194

; B-E block execute

CDA3   20 58 F2   JSR $F258
CDA6   20 36 CD   JSR $CD36
CDA9   A9 00      LDA #$00
CDAB   85 6F      STA $6F
CDAD   A6 F9      LDX $F9
CDAF   BD E0 FE   LDA $FEE0,X
CDB2   85 70      STA $70
CDB4   20 BA CD   JSR $CDBA
CDB7   4C 94 C1   JMP $C194
CDBA   6C 6F 00   JMP ($006F)

; B-P block pointer

CDBD   20 D2 CD   JSR $CDD2
CDC0   A5 F9      LDA $F9
CDC2   0A         ASL
CDC3   AA         TAX
CDC4   AD 86 02   LDA $0286
CDC7   95 99      STA $99,X
CDC9   20 2F D1   JSR $D12F
CDCC   20 EE D3   JSR $D3EE
CDCF   4C 94 C1   JMP $C194

; open channel

CDD2   A6 D3      LDX $D3
CDD4   E6 D3      INC $D3
CDD6   BD 85 02   LDA $0285,X
CDD9   A8         TAY
CDDA   88         DEY
CDDB   88         DEY
CDDC   C0 0C      CPY #$0C
CDDE   90 05      BCC $CDE5
CDE0   A9 70      LDA #$70
CDE2   4C C8 C1   JMP $C1C8
CDE5   85 83      STA $83
CDE7   20 EB D0   JSR $D0EB
CDEA   B0 F4      BCS $CDE0
CDEC   20 93 DF   JSR $DF93
CDEF   85 F9      STA $F9
CDF1   60         RTS

; check buffer number and open channel

CDF2   20 D2 CD   JSR $CDD2
CDF5   A6 D3      LDX $D3
CDF7   BD 85 02   LDA $0285,X
CDFA   29 01      AND #$01
CDFC   85 7F      STA $7F
CDFE   BD 87 02   LDA $0287,X
CE01   85 81      STA $81
CE03   BD 86 02   LDA $0286,X
CE06   85 80      STA $80
CE08   20 5F D5   JSR $D55F
CE0B   4C 00 C1   JMP $C100

; set pointer for REL file

CE0E   20 2C CE   JSR $CE2C
CE11   20 6E CE   JSR $CE6E
CE14   A5 90      LDA $90
CE16   85 D7      STA $D7
CE18   20 71 CE   JSR $CE71
CE1B   E6 D7      INC $D7
CE1D   E6 D7      INC $D7
CE1F   A5 8B      LDA $8B
CE21   85 D5      STA $D5
CE23   A5 90      LDA $90
CE25   0A         ASL
CE26   18         CLC
CE27   69 10      ADC #$10
CE29   85 D6      STA $D6
CE2B   60         RTS

;

CE2C   20 D9 CE   JSR $CED9
CE2F   85 92      STA $92
CE31   A6 82      LDX $82
CE33   B5 B5      LDA $B5,X
CE35   85 90      STA $90
CE37   B5 BB      LDA $BB,X
CE39   85 91      STA $91
CE3B   D0 04      BNE $CE41
CE3D   A5 90      LDA $90
CE3F   F0 0B      BEQ $CE4C
CE41   A5 90      LDA $90
CE43   38         SEC
CE44   E9 01      SBC #$01
CE46   85 90      STA $90
CE48   B0 02      BCS $CE4C
CE4A   C6 91      DEC $91
CE4C   B5 C7      LDA $C7,X
CE4E   85 6F      STA $6F
CE50   46 6F      LSR $6F
CE52   90 03      BCC $CE57
CE54   20 ED CE   JSR $CEED
CE57   20 E5 CE   JSR $CEE5
CE5A   A5 6F      LDA $6F
CE5C   D0 F2      BNE $CE50
CE5E   A5 D4      LDA $D4
CE60   18         CLC
CE61   65 8B      ADC $8B
CE63   85 8B      STA $8B
CE65   90 06      BCC $CE6D
CE67   E6 8C      INC $8C
CE69   D0 02      BNE $CE6D
CE6B   E6 8D      INC $8D
CE6D   60         RTS

; divide by 254

CE6E   A9 FE      LDA #$FE
CE70   .BY $2C

; divide by 120

CE71   A9 78      LDA #$78
CE73   85 6F      STA $6F
CE75   A2 03      LDX #$03
CE77   B5 8F      LDA $8F,X
CE79   48         PHA
CE7A   B5 8A      LDA $8A,X
CE7C   95 8F      STA $8F,X
CE7E   68         PLA
CE7F   95 8A      STA $8A,X
CE81   CA         DEX
CE82   D0 F3      BNE $CE77
CE84   20 D9 CE   JSR $CED9
CE87   A2 00      LDX #$00
CE89   B5 90      LDA $90,X
CE8B   95 8F      STA $8F,X
CE8D   E8         INX
CE8E   E0 04      CPX #$04
CE90   90 F7      BCC $CE89
CE92   A9 00      LDA #$00
CE94   85 92      STA $92
CE96   24 6F      BIT $6F
CE98   30 09      BMI $CEA3
CE9A   06 8F      ASL $8F
CE9C   08         PHP
CE9D   46 8F      LSR $8F
CE9F   28         PLP
CEA0   20 E6 CE   JSR $CEE6
CEA3   20 ED CE   JSR $CEED
CEA6   20 E5 CE   JSR $CEE5
CEA9   24 6F      BIT $6F
CEAB   30 03      BMI $CEB0
CEAD   20 E2 CE   JSR $CEE2
CEB0   A5 8F      LDA $8F
CEB2   18         CLC
CEB3   65 90      ADC $90
CEB5   85 90      STA $90
CEB7   90 06      BCC $CEBF
CEB9   E6 91      INC $91
CEBB   D0 02      BNE $CEBF
CEBD   E6 92      INC $92
CEBF   A5 92      LDA $92
CEC1   05 91      ORA $91
CEC3   D0 C2      BNE $CE87
CEC5   A5 90      LDA $90
CEC7   38         SEC
CEC8   E5 6F      SBC $6F
CECA   90 0C      BCC $CED8
CECC   E6 8B      INC $8B
CECE   D0 06      BNE $CED6
CED0   E6 8C      INC $8C
CED2   D0 02      BNE $CED6
CED4   E6 8D      INC $8D
CED6   85 90      STA $90
CED8   60         RTS

; erase work storage

CED9   A9 00      LDA #$00
CEDB   85 8B      STA $8B
CEDD   85 8C      STA $8C
CEDF   85 8D      STA $8D
CEE1   60         RTS

; left shift 3-byte register twice

CEE2   20 E5 CE   JSR $CEE5

; left shift 3-byte register once

CEE5   18         CLC
CEE6   26 90      ROL $90
CEE8   26 91      ROL $91
CEEA   26 92      ROL $92
CEEC   60         RTS

;

CEED   18         CLC
CEEE   A2 FD      LDX #$FD
CEF0   B5 8E      LDA $8E,X
CEF2   75 93      ADC $93,X
CEF4   95 8E      STA $8E,X
CEF6   E8         INX
CEF7   D0 F7      BNE $CEF0
CEF9   60         RTS
CEFA   A2 00      LDX #$00
CEFC   8A         TXA
CEFD   95 FA      STA $FA,X
CEFF   E8         INX
CF00   E0 04      CPX #$04
CF02   D0 F8      BNE $CEFC
CF04   A9 06      LDA #$06
CF06   95 FA      STA $FA,X
CF08   60         RTS
CF09   A0 04      LDY #$04
CF0B   A6 82      LDX $82
CF0D   B9 FA 00   LDA $00FA,Y
CF10   96 FA      STX $FA,Y
CF12   C5 82      CMP $82
CF14   F0 07      BEQ $CF1D
CF16   88         DEY
CF17   30 E1      BMI $CEFA
CF19   AA         TAX
CF1A   4C 0D CF   JMP $CF0D
CF1D   60         RTS
CF1E   20 09 CF   JSR $CF09
CF21   20 B7 DF   JSR $DFB7
CF24   D0 46      BNE $CF6C
CF26   20 D3 D1   JSR $D1D3
CF29   20 8E D2   JSR $D28E
CF2C   30 48      BMI $CF76
CF2E   20 C2 DF   JSR $DFC2
CF31   A5 80      LDA $80
CF33   48         PHA
CF34   A5 81      LDA $81
CF36   48         PHA
CF37   A9 01      LDA #$01
CF39   20 F6 D4   JSR $D4F6
CF3C   85 81      STA $81
CF3E   A9 00      LDA #$00
CF40   20 F6 D4   JSR $D4F6
CF43   85 80      STA $80
CF45   F0 1F      BEQ $CF66
CF47   20 25 D1   JSR $D125
CF4A   F0 0B      BEQ $CF57
CF4C   20 AB DD   JSR $DDAB
CF4F   D0 06      BNE $CF57
CF51   20 8C CF   JSR $CF8C
CF54   4C 5D CF   JMP $CF5D
CF57   20 8C CF   JSR $CF8C
CF5A   20 57 DE   JSR $DE57
CF5D   68         PLA
CF5E   85 81      STA $81
CF60   68         PLA
CF61   85 80      STA $80
CF63   4C 6F CF   JMP $CF6F
CF66   68         PLA
CF67   85 81      STA $81
CF69   68         PLA
CF6A   85 80      STA $80
CF6C   20 8C CF   JSR $CF8C
CF6F   20 93 DF   JSR $DF93
CF72   AA         TAX
CF73   4C 99 D5   JMP $D599
CF76   A9 70      LDA #$70
CF78   4C C8 C1   JMP $C1C8
CF7B   20 09 CF   JSR $CF09
CF7E   20 B7 DF   JSR $DFB7
CF81   D0 08      BNE $CF8B
CF83   20 8E D2   JSR $D28E
CF86   30 EE      BMI $CF76
CF88   20 C2 DF   JSR $DFC2
CF8B   60         RTS

; change buffer

CF8C   A6 82      LDX $82
CF8E   B5 A7      LDA $A7,X
CF90   49 80      EOR #$80
CF92   95 A7      STA $A7,X
CF94   B5 AE      LDA $AE,X
CF96   49 80      EOR #$80
CF98   95 AE      STA $AE,X
CF9A   60         RTS

; write data in buffer

CF9B   A2 12      LDX #$12
CF9D   86 83      STX $83
CF9F   20 07 D1   JSR $D107
CFA2   20 00 C1   JSR $C100
CFA5   20 25 D1   JSR $D125
CFA8   90 05      BCC $CFAF
CFAA   A9 20      LDA #$20
CFAC   20 9D DD   JSR $DD9D
CFAF   A5 83      LDA $83
CFB1   C9 0F      CMP #$0F
CFB3   F0 23      BEQ $CFD8
CFB5   D0 08      BNE $CFBF
CFB7   A5 84      LDA $84
CFB9   29 8F      AND #$8F
CFBB   C9 0F      CMP #$0F
CFBD   B0 19      BCS $CFD8
CFBF   20 25 D1   JSR $D125
CFC2   B0 05      BCS $CFC9
CFC4   A5 85      LDA $85
CFC6   4C 9D D1   JMP $D19D
CFC9   D0 03      BNE $CFCE
CFCB   4C AB E0   JMP $E0AB
CFCE   A5 85      LDA $85
CFD0   20 F1 CF   JSR $CFF1
CFD3   A4 82      LDY $82
CFD5   4C EE D3   JMP $D3EE
CFD8   A9 04      LDA #$04
CFDA   85 82      STA $82
CFDC   20 E8 D4   JSR $D4E8
CFDF   C9 2A      CMP #$2A
CFE1   F0 05      BEQ $CFE8
CFE3   A5 85      LDA $85
CFE5   20 F1 CF   JSR $CFF1
CFE8   A5 F8      LDA $F8
CFEA   F0 01      BEQ $CFED
CFEC   60         RTS
CFED   EE 55 02   INC $0255
CFF0   60         RTS

; write data byte in buffer

CFF1   48         PHA
CFF2   20 93 DF   JSR $DF93
CFF5   10 06      BPL $CFFD
CFF7   68         PLA
CFF8   A9 61      LDA #$61
CFFA   4C C8 C1   JMP $C1C8
CFFD   0A         ASL
CFFE   AA         TAX
CFFF   68         PLA
D000   81 99      STA ($99,X)
D002   F6 99      INC $99,X
D004   60         RTS

; I - Initalise command

D005   20 D1 C1   JSR $C1D1
D008   20 42 D0   JSR $D042
D00B   4C 94 C1   JMP $C194

;

D00E   20 0F F1   JSR $F10F
D011   A8         TAY
D012   B6 A7      LDX $A7,Y
D014   E0 FF      CPX #$FF
D016   D0 14      BNE $D02C
D018   48         PHA
D019   20 8E D2   JSR $D28E
D01C   AA         TAX
D01D   10 05      BPL $D024
D01F   A9 70      LDA #$70
D021   20 48 E6   JSR $E648
D024   68         PLA
D025   A8         TAY
D026   8A         TXA
D027   09 80      ORA #$80
D029   99 A7 00   STA $00A7,Y
D02C   8A         TXA
D02D   29 0F      AND #$0F
D02F   85 F9      STA $F9
D031   A2 00      LDX #$00
D033   86 81      STX $81
D035   AE 85 FE   LDX $FE85
D038   86 80      STX $80
D03A   20 D3 D6   JSR $D6D3
D03D   A9 B0      LDA #$B0
D03F   4C 8C D5   JMP $D58C

; load BAM

D042   20 D1 F0   JSR $F0D1
D045   20 13 D3   JSR $D313
D048   20 0E D0   JSR $D00E
D04B   A6 7F      LDX $7F
D04D   A9 00      LDA #$00
D04F   9D 51 02   STA $0251,X
D052   8A         TXA
D053   0A         ASL
D054   AA         TAX
D055   A5 16      LDA $16
D057   95 12      STA $12,X
D059   A5 17      LDA $17
D05B   95 13      STA $13,X
D05D   20 86 D5   JSR $D586
D060   A5 F9      LDA $F9
D062   0A         ASL
D063   AA         TAX
D064   A9 02      LDA #$02
D066   95 99      STA $99,X
D068   A1 99      LDA ($99,X)
D06A   A6 7F      LDX $7F
D06C   9D 01 01   STA $0101,X
D06F   A9 00      LDA #$00
D071   95 1C      STA $1C,X
D073   95 FF      STA $FF,X

; calculate blocks free

D075   20 3A EF   JSR $EF3A
D078   A0 04      LDY #$04
D07A   A9 00      LDA #$00
D07C   AA         TAX
D07D   18         CLC
D07E   71 6D      ADC ($6D),Y
D080   90 01      BCC $D083
D082   E8         INX
D083   C8         INY
D084   C8         INY
D085   C8         INY
D086   C8         INY
D087   C0 48      CPY #$48
D089   F0 F8      BEQ $D083
D08B   C0 90      CPY #$90
D08D   D0 EE      BNE $D07D
D08F   48         PHA
D090   8A         TXA
D091   A6 7F      LDX $7F
D093   9D FC 02   STA $02FC,X
D096   68         PLA
D097   9D FA 02   STA $02FA,X
D09A   60         RTS

;

D09B   20 D0 D6   JSR $D6D0
D09E   20 C3 D0   JSR $D0C3
D0A1   20 99 D5   JSR $D599
D0A4   20 37 D1   JSR $D137
D0A7   85 80      STA $80
D0A9   20 37 D1   JSR $D137
D0AC   85 81      STA $81
D0AE   60         RTS
D0AF   20 9B D0   JSR $D09B
D0B2   A5 80      LDA $80
D0B4   D0 01      BNE $D0B7
D0B6   60         RTS
D0B7   20 1E CF   JSR $CF1E
D0BA   20 D0 D6   JSR $D6D0
D0BD   20 C3 D0   JSR $D0C3
D0C0   4C 1E CF   JMP $CF1E

; read block

D0C3   A9 80      LDA #$80
D0C5   D0 02      BNE $D0C9

; write block

D0C7   A9 90      LDA #$90
D0C9   8D 4D 02   STA $024D
D0CC   20 93 DF   JSR $DF93
D0CF   AA         TAX
D0D0   20 06 D5   JSR $D506
D0D3   8A         TXA
D0D4   48         PHA
D0D5   0A         ASL
D0D6   AA         TAX
D0D7   A9 00      LDA #$00
D0D9   95 99      STA $99,X
D0DB   20 25 D1   JSR $D125
D0DE   C9 04      CMP #$04
D0E0   B0 06      BCS $D0E8
D0E2   F6 B5      INC $B5,X
D0E4   D0 02      BNE $D0E8
D0E6   F6 BB      INC $BB,X
D0E8   68         PLA
D0E9   AA         TAX
D0EA   60         RTS

; open channel for reading

D0EB   A5 83      LDA $83
D0ED   C9 13      CMP #$13
D0EF   90 02      BCC $D0F3
D0F1   29 0F      AND #$0F
D0F3   C9 0F      CMP #$0F
D0F5   D0 02      BNE $D0F9
D0F7   A9 10      LDA #$10
D0F9   AA         TAX
D0FA   38         SEC
D0FB   BD 2B 02   LDA $022B,X
D0FE   30 06      BMI $D106
D100   29 0F      AND #$0F
D102   85 82      STA $82
D104   AA         TAX
D105   18         CLC
D106   60         RTS

; open channel for writing

D107   A5 83      LDA $83
D109   C9 13      CMP #$13
D10B   90 02      BCC $D10F
D10D   29 0F      AND #$0F
D10F   AA         TAX
D110   BD 2B 02   LDA $022B,X
D113   A8         TAY
D114   0A         ASL
D115   90 0A      BCC $D121
D117   30 0A      BMI $D123
D119   98         TYA
D11A   29 0F      AND #$0F
D11C   85 82      STA $82
D11E   AA         TAX
D11F   18         CLC
D120   60         RTS
D121   30 F6      BMI $D119
D123   38         SEC
D124   60         RTS

; check for file type REL

D125   A6 82      LDX $82
D127   B5 EC      LDA $EC,X
D129   4A         LSR
D12A   29 07      AND #$07
D12C   C9 04      CMP #$04
D12E   60         RTS

; get buffer and channel numbers

D12F   20 93 DF   JSR $DF93
D132   0A         ASL
D133   AA         TAX
D134   A4 82      LDY $82
D136   60         RTS

; get a byte from buffer

D137   20 2F D1   JSR $D12F
D13A   B9 44 02   LDA $0244,Y
D13D   F0 12      BEQ $D151
D13F   A1 99      LDA ($99,X)
D141   48         PHA
D142   B5 99      LDA $99,X
D144   D9 44 02   CMP $0244,Y
D147   D0 04      BNE $D14D
D149   A9 FF      LDA #$FF
D14B   95 99      STA $99,X
D14D   68         PLA
D14E   F6 99      INC $99,X
D150   60         RTS
D151   A1 99      LDA ($99,X)
D153   F6 99      INC $99,X
D155   60         RTS

; get byte and read next block

D156   20 37 D1   JSR $D137
D159   D0 36      BNE $D191
D15B   85 85      STA $85
D15D   B9 44 02   LDA $0244,Y
D160   F0 08      BEQ $D16A
D162   A9 80      LDA #$80
D164   99 F2 00   STA $00F2,Y
D167   A5 85      LDA $85
D169   60         RTS
D16A   20 1E CF   JSR $CF1E
D16D   A9 00      LDA #$00
D16F   20 C8 D4   JSR $D4C8
D172   20 37 D1   JSR $D137
D175   C9 00      CMP #$00
D177   F0 19      BEQ $D192
D179   85 80      STA $80
D17B   20 37 D1   JSR $D137
D17E   85 81      STA $81
D180   20 1E CF   JSR $CF1E
D183   20 D3 D1   JSR $D1D3
D186   20 D0 D6   JSR $D6D0
D189   20 C3 D0   JSR $D0C3
D18C   20 1E CF   JSR $CF1E
D18F   A5 85      LDA $85
D191   60         RTS
D192   20 37 D1   JSR $D137
D195   A4 82      LDY $82
D197   99 44 02   STA $0244,Y
D19A   A5 85      LDA $85
D19C   60         RTS

; write byte in buffer and block

D19D   20 F1 CF   JSR $CFF1
D1A0   F0 01      BEQ $D1A3
D1A2   60         RTS
D1A3   20 D3 D1   JSR $D1D3
D1A6   20 1E F1   JSR $F11E
D1A9   A9 00      LDA #$00
D1AB   20 C8 D4   JSR $D4C8
D1AE   A5 80      LDA $80
D1B0   20 F1 CF   JSR $CFF1
D1B3   A5 81      LDA $81
D1B5   20 F1 CF   JSR $CFF1
D1B8   20 C7 D0   JSR $D0C7
D1BB   20 1E CF   JSR $CF1E
D1BE   20 D0 D6   JSR $D6D0
D1C1   A9 02      LDA #$02
D1C3   4C C8 D4   JMP $D4C8

; increment buffer pointer

D1C6   85 6F      STA $6F
D1C8   20 E8 D4   JSR $D4E8
D1CB   18         CLC
D1CC   65 6F      ADC $6F
D1CE   95 99      STA $99,X
D1D0   85 94      STA $94
D1D2   60         RTS

; get drive number

D1D3   20 93 DF   JSR $DF93
D1D6   AA         TAX
D1D7   BD 5B 02   LDA $025B,X
D1DA   29 01      AND #$01
D1DC   85 7F      STA $7F
D1DE   60         RTS

; find write channel and buffer

D1DF   38         SEC
D1E0   B0 01      BCS $D1E3

; find read channel and buffer

D1E2   18         CLC
D1E3   08         PHP
D1E4   85 6F      STA $6F
D1E6   20 27 D2   JSR $D227
D1E9   20 7F D3   JSR $D37F
D1EC   85 82      STA $82
D1EE   A6 83      LDX $83
D1F0   28         PLP
D1F1   90 02      BCC $D1F5
D1F3   09 80      ORA #$80
D1F5   9D 2B 02   STA $022B,X
D1F8   29 3F      AND #$3F
D1FA   A8         TAY
D1FB   A9 FF      LDA #$FF
D1FD   99 A7 00   STA $00A7,Y
D200   99 AE 00   STA $00AE,Y
D203   99 CD 00   STA $00CD,Y
D206   C6 6F      DEC $6F
D208   30 1C      BMI $D226
D20A   20 8E D2   JSR $D28E
D20D   10 08      BPL $D217
D20F   20 5A D2   JSR $D25A
D212   A9 70      LDA #$70
D214   4C C8 C1   JMP $C1C8
D217   99 A7 00   STA $00A7,Y
D21A   C6 6F      DEC $6F
D21C   30 08      BMI $D226
D21E   20 8E D2   JSR $D28E
D221   30 EC      BMI $D20F
D223   99 AE 00   STA $00AE,Y
D226   60         RTS

; close channel

D227   A5 83      LDA $83
D229   C9 0F      CMP #$0F
D22B   D0 01      BNE $D22E
D22D   60         RTS
D22E   A6 83      LDX $83
D230   BD 2B 02   LDA $022B,X
D233   C9 FF      CMP #$FF
D235   F0 22      BEQ $D259
D237   29 3F      AND #$3F
D239   85 82      STA $82
D23B   A9 FF      LDA #$FF
D23D   9D 2B 02   STA $022B,X
D240   A6 82      LDX $82
D242   A9 00      LDA #$00
D244   95 F2      STA $F2,X
D246   20 5A D2   JSR $D25A
D249   A6 82      LDX $82
D24B   A9 01      LDA #$01
D24D   CA         DEX
D24E   30 03      BMI $D253
D250   0A         ASL
D251   D0 FA      BNE $D24D
D253   0D 56 02   ORA $0256
D256   8D 56 02   STA $0256
D259   60         RTS

; free buffer

D25A   A6 82      LDX $82
D25C   B5 A7      LDA $A7,X
D25E   C9 FF      CMP #$FF
D260   F0 09      BEQ $D26B
D262   48         PHA
D263   A9 FF      LDA #$FF
D265   95 A7      STA $A7,X
D267   68         PLA
D268   20 F3 D2   JSR $D2F3
D26B   A6 82      LDX $82
D26D   B5 AE      LDA $AE,X
D26F   C9 FF      CMP #$FF
D271   F0 09      BEQ $D27C
D273   48         PHA
D274   A9 FF      LDA #$FF
D276   95 AE      STA $AE,X
D278   68         PLA
D279   20 F3 D2   JSR $D2F3
D27C   A6 82      LDX $82
D27E   B5 CD      LDA $CD,X
D280   C9 FF      CMP #$FF
D282   F0 09      BEQ $D28D
D284   48         PHA
D285   A9 FF      LDA #$FF
D287   95 CD      STA $CD,X
D289   68         PLA
D28A   20 F3 D2   JSR $D2F3
D28D   60         RTS

; find buffer

D28E   98         TYA
D28F   48         PHA
D290   A0 01      LDY #$01
D292   20 BA D2   JSR $D2BA
D295   10 0C      BPL $D2A3
D297   88         DEY
D298   20 BA D2   JSR $D2BA
D29B   10 06      BPL $D2A3
D29D   20 39 D3   JSR $D339
D2A0   AA         TAX
D2A1   30 13      BMI $D2B6
D2A3   B5 00      LDA $00,X
D2A5   30 FC      BMI $D2A3
D2A7   A5 7F      LDA $7F
D2A9   95 00      STA $00,X
D2AB   9D 5B 02   STA $025B,X
D2AE   8A         TXA
D2AF   0A         ASL
D2B0   A8         TAY
D2B1   A9 02      LDA #$02
D2B3   99 99 00   STA $0099,Y
D2B6   68         PLA
D2B7   A8         TAY
D2B8   8A         TXA
D2B9   60         RTS
D2BA   A2 07      LDX #$07
D2BC   B9 4F 02   LDA $024F,Y
D2BF   3D E9 EF   AND $EFE9,X
D2C2   F0 04      BEQ $D2C8
D2C4   CA         DEX
D2C5   10 F5      BPL $D2BC
D2C7   60         RTS
D2C8   B9 4F 02   LDA $024F,Y
D2CB   5D E9 EF   EOR $EFE9,X
D2CE   99 4F 02   STA $024F,Y
D2D1   8A         TXA
D2D2   88         DEY
D2D3   30 03      BMI $D2D8
D2D5   18         CLC
D2D6   69 08      ADC #$08
D2D8   AA         TAX
D2D9   60         RTS
D2DA   A6 82      LDX $82
D2DC   B5 A7      LDA $A7,X
D2DE   30 09      BMI $D2E9
D2E0   8A         TXA
D2E1   18         CLC
D2E2   69 07      ADC #$07
D2E4   AA         TAX
D2E5   B5 A7      LDA $A7,X
D2E7   10 F0      BPL $D2D9
D2E9   C9 FF      CMP #$FF
D2EB   F0 EC      BEQ $D2D9
D2ED   48         PHA
D2EE   A9 FF      LDA #$FF
D2F0   95 A7      STA $A7,X
D2F2   68         PLA
D2F3   29 0F      AND #$0F
D2F5   A8         TAY
D2F6   C8         INY
D2F7   A2 10      LDX #$10
D2F9   6E 50 02   ROR $0250
D2FC   6E 4F 02   ROR $024F
D2FF   88         DEY
D300   D0 01      BNE $D303
D302   18         CLC
D303   CA         DEX
D304   10 F3      BPL $D2F9
D306   60         RTS

; close all channels

D307   A9 0E      LDA #$0E
D309   85 83      STA $83
D30B   20 27 D2   JSR $D227
D30E   C6 83      DEC $83
D310   D0 F9      BNE $D30B
D312   60         RTS

; close all channels of other drives

D313   A9 0E      LDA #$0E
D315   85 83      STA $83
D317   A6 83      LDX $83
D319   BD 2B 02   LDA $022B,X
D31C   C9 FF      CMP #$FF
D31E   F0 14      BEQ $D334
D320   29 3F      AND #$3F
D322   85 82      STA $82
D324   20 93 DF   JSR $DF93
D327   AA         TAX
D328   BD 5B 02   LDA $025B,X
D32B   29 01      AND #$01
D32D   C5 7F      CMP $7F
D32F   D0 03      BNE $D334
D331   20 27 D2   JSR $D227
D334   C6 83      DEC $83
D336   10 DF      BPL $D317
D338   60         RTS

;

D339   A5 6F      LDA $6F
D33B   48         PHA
D33C   A0 00      LDY #$00
D33E   B6 FA      LDX $FA,Y
D340   B5 A7      LDA $A7,X
D342   10 04      BPL $D348
D344   C9 FF      CMP #$FF
D346   D0 16      BNE $D35E
D348   8A         TXA
D349   18         CLC
D34A   69 07      ADC #$07
D34C   AA         TAX
D34D   B5 A7      LDA $A7,X
D34F   10 04      BPL $D355
D351   C9 FF      CMP #$FF
D353   D0 09      BNE $D35E
D355   C8         INY
D356   C0 05      CPY #$05
D358   90 E4      BCC $D33E
D35A   A2 FF      LDX #$FF
D35C   D0 1C      BNE $D37A
D35E   86 6F      STX $6F
D360   29 3F      AND #$3F
D362   AA         TAX
D363   B5 00      LDA $00,X
D365   30 FC      BMI $D363
D367   C9 02      CMP #$02
D369   90 08      BCC $D373
D36B   A6 6F      LDX $6F
D36D   E0 07      CPX #$07
D36F   90 D7      BCC $D348
D371   B0 E2      BCS $D355
D373   A4 6F      LDY $6F
D375   A9 FF      LDA #$FF
D377   99 A7 00   STA $00A7,Y
D37A   68         PLA
D37B   85 6F      STA $6F
D37D   8A         TXA
D37E   60         RTS

; find channel and allocate

D37F   A0 00      LDY #$00
D381   A9 01      LDA #$01
D383   2C 56 02   BIT $0256
D386   D0 09      BNE $D391
D388   C8         INY
D389   0A         ASL
D38A   D0 F7      BNE $D383
D38C   A9 70      LDA #$70
D38E   4C C8 C1   JMP $C1C8
D391   49 FF      EOR #$FF
D393   2D 56 02   AND $0256
D396   8D 56 02   STA $0256
D399   98         TYA
D39A   60         RTS

; get byte for output

D39B   20 EB D0   JSR $D0EB
D39E   20 00 C1   JSR $C100
D3A1   20 AA D3   JSR $D3AA
D3A4   A6 82      LDX $82
D3A6   BD 3E 02   LDA $023E,X
D3A9   60         RTS
D3AA   A6 82      LDX $82
D3AC   20 25 D1   JSR $D125
D3AF   D0 03      BNE $D3B4
D3B1   4C 20 E1   JMP $E120
D3B4   A5 83      LDA $83
D3B6   C9 0F      CMP #$0F
D3B8   F0 5A      BEQ $D414
D3BA   B5 F2      LDA $F2,X
D3BC   29 08      AND #$08
D3BE   D0 13      BNE $D3D3
D3C0   20 25 D1   JSR $D125
D3C3   C9 07      CMP #$07
D3C5   D0 07      BNE $D3CE
D3C7   A9 89      LDA #$89
D3C9   95 F2      STA $F2,X
D3CB   4C DE D3   JMP $D3DE
D3CE   A9 00      LDA #$00
D3D0   95 F2      STA $F2,X
D3D2   60         RTS
D3D3   A5 83      LDA $83
D3D5   F0 32      BEQ $D409
D3D7   20 25 D1   JSR $D125
D3DA   C9 04      CMP #$04
D3DC   90 22      BCC $D400
D3DE   20 2F D1   JSR $D12F
D3E1   B5 99      LDA $99,X
D3E3   D9 44 02   CMP $0244,Y
D3E6   D0 04      BNE $D3EC
D3E8   A9 00      LDA #$00
D3EA   95 99      STA $99,X
D3EC   F6 99      INC $99,X
D3EE   A1 99      LDA ($99,X)
D3F0   99 3E 02   STA $023E,Y
D3F3   B5 99      LDA $99,X
D3F5   D9 44 02   CMP $0244,Y
D3F8   D0 05      BNE $D3FF
D3FA   A9 81      LDA #$81
D3FC   99 F2 00   STA $00F2,Y
D3FF   60         RTS
D400   20 56 D1   JSR $D156
D403   A6 82      LDX $82
D405   9D 3E 02   STA $023E,X
D408   60         RTS
D409   AD 54 02   LDA $0254
D40C   F0 F2      BEQ $D400
D40E   20 67 ED   JSR $ED67
D411   4C 03 D4   JMP $D403
D414   20 E8 D4   JSR $D4E8
D417   C9 D4      CMP #$D4
D419   D0 18      BNE $D433
D41B   A5 95      LDA $95
D41D   C9 02      CMP #$02
D41F   D0 12      BNE $D433
D421   A9 0D      LDA #$0D
D423   85 85      STA $85
D425   20 23 C1   JSR $C123
D428   A9 00      LDA #$00
D42A   20 C1 E6   JSR $E6C1
D42D   C6 A5      DEC $A5
D42F   A9 80      LDA #$80
D431   D0 12      BNE $D445
D433   20 37 D1   JSR $D137
D436   85 85      STA $85
D438   D0 09      BNE $D443
D43A   A9 D4      LDA #$D4
D43C   20 C8 D4   JSR $D4C8
D43F   A9 02      LDA #$02
D441   95 9A      STA $9A,X
D443   A9 88      LDA #$88
D445   85 F7      STA $F7
D447   A5 85      LDA $85
D449   8D 43 02   STA $0243
D44C   60         RTS

; read next block

D44D   20 93 DF   JSR $DF93
D450   0A         ASL
D451   AA         TAX
D452   A9 00      LDA #$00
D454   95 99      STA $99,X
D456   A1 99      LDA ($99,X)
D458   F0 05      BEQ $D45F
D45A   D6 99      DEC $99,X
D45C   4C 56 D1   JMP $D156
D45F   60         RTS

; read block

D460   A9 80      LDA #$80
D462   D0 02      BNE $D466

; write block

D464   A9 90      LDA #$90
D466   05 7F      ORA $7F
D468   8D 4D 02   STA $024D
D46B   A5 F9      LDA $F9
D46D   20 D3 D6   JSR $D6D3
D470   A6 F9      LDX $F9
D472   4C 93 D5   JMP $D593

; allocate buffer and read block

D475   A9 01      LDA #$01
D477   8D 4A 02   STA $024A
D47A   A9 11      LDA #$11
D47C   85 83      STA $83
D47E   20 46 DC   JSR $DC46
D481   A9 02      LDA #$02
D483   4C C8 D4   JMP $D4C8

; allocate new block

D486   A9 12      LDA #$12
D488   85 83      STA $83
D48A   4C DA DC   JMP $DCDA

; write dir block

D48D   20 3B DE   JSR $DE3B
D490   A9 01      LDA #$01
D492   85 6F      STA $6F
D494   A5 69      LDA $69
D496   48         PHA
D497   A9 03      LDA #$03
D499   85 69      STA $69
D49B   20 2D F1   JSR $F12D
D49E   68         PLA
D49F   85 69      STA $69
D4A1   A9 00      LDA #$00
D4A3   20 C8 D4   JSR $D4C8
D4A6   A5 80      LDA $80
D4A8   20 F1 CF   JSR $CFF1
D4AB   A5 81      LDA $81
D4AD   20 F1 CF   JSR $CFF1
D4B0   20 C7 D0   JSR $D0C7
D4B3   20 99 D5   JSR $D599
D4B6   A9 00      LDA #$00
D4B8   20 C8 D4   JSR $D4C8
D4BB   20 F1 CF   JSR $CFF1
D4BE   D0 FB      BNE $D4BB
D4C0   20 F1 CF   JSR $CFF1
D4C3   A9 FF      LDA #$FF
D4C5   4C F1 CF   JMP $CFF1

; set buffer pointer

D4C8   85 6F      STA $6F
D4CA   20 93 DF   JSR $DF93
D4CD   0A         ASL
D4CE   AA         TAX
D4CF   B5 9A      LDA $9A,X
D4D1   85 95      STA $95
D4D3   A5 6F      LDA $6F
D4D5   95 99      STA $99,X
D4D7   85 94      STA $94
D4D9   60         RTS

; close internal channel

D4DA   A9 11      LDA #$11
D4DC   85 83      STA $83
D4DE   20 27 D2   JSR $D227
D4E1   A9 12      LDA #$12
D4E3   85 83      STA $83
D4E5   4C 27 D2   JMP $D227

; set buffer pointer

D4E8   20 93 DF   JSR $DF93
D4EB   0A         ASL
D4EC   AA         TAX
D4ED   B5 9A      LDA $9A,X
D4EF   85 95      STA $95
D4F1   B5 99      LDA $99,X
D4F3   85 94      STA $94
D4F5   60         RTS

; get byte from buffer

D4F6   85 71      STA $71
D4F8   20 93 DF   JSR $DF93
D4FB   AA         TAX
D4FC   BD E0 FE   LDA $FEE0,X
D4FF   85 72      STA $72
D501   A0 00      LDY #$00
D503   B1 71      LDA ($71),Y
D505   60         RTS

; check track and sector numbers

D506   BD 5B 02   LDA $025B,X
D509   29 01      AND #$01
D50B   0D 4D 02   ORA $024D
D50E   48         PHA
D50F   86 F9      STX $F9
D511   8A         TXA
D512   0A         ASL
D513   AA         TAX
D514   B5 07      LDA $07,X
D516   8D 4D 02   STA $024D
D519   B5 06      LDA $06,X
D51B   F0 2D      BEQ $D54A
D51D   CD D7 FE   CMP $FED7
D520   B0 28      BCS $D54A
D522   AA         TAX
D523   68         PLA
D524   48         PHA
D525   29 F0      AND #$F0
D527   C9 90      CMP #$90
D529   D0 4F      BNE $D57A
D52B   68         PLA
D52C   48         PHA
D52D   4A         LSR
D52E   B0 05      BCS $D535
D530   AD 01 01   LDA $0101
D533   90 03      BCC $D538
D535   AD 02 01   LDA $0102
D538   F0 05      BEQ $D53F
D53A   CD D5 FE   CMP $FED5
D53D   D0 33      BNE $D572
D53F   8A         TXA
D540   20 4B F2   JSR $F24B
D543   CD 4D 02   CMP $024D
D546   F0 02      BEQ $D54A
D548   B0 30      BCS $D57A
D54A   20 52 D5   JSR $D552
D54D   A9 66      LDA #$66
D54F   4C 45 E6   JMP $E645

; get track and sector numbers

D552   A5 F9      LDA $F9
D554   0A         ASL
D555   AA         TAX
D556   B5 06      LDA $06,X
D558   85 80      STA $80
D55A   B5 07      LDA $07,X
D55C   85 81      STA $81
D55E   60         RTS

; check for vaild track and sector numbers

D55F   A5 80      LDA $80
D561   F0 EA      BEQ $D54D
D563   CD D7 FE   CMP $FED7
D566   B0 E5      BCS $D54D
D568   20 4B F2   JSR $F24B
D56B   C5 81      CMP $81
D56D   F0 DE      BEQ $D54D
D56F   90 DC      BCC $D54D
D571   60         RTS
D572   20 52 D5   JSR $D552
D575   A9 73      LDA #$73
D577   4C 45 E6   JMP $E645
D57A   A6 F9      LDX $F9
D57C   68         PLA
D57D   8D 4D 02   STA $024D
D580   95 00      STA $00,X
D582   9D 5B 02   STA $025B,X
D585   60         RTS

; read block

D586   A9 80      LDA #$80
D588   D0 02      BNE $D58C

; write block

D58A   A9 90      LDA #$90
D58C   05 7F      ORA $7F
D58E   A6 F9      LDX $F9
D590   8D 4D 02   STA $024D
D593   AD 4D 02   LDA $024D
D596   20 0E D5   JSR $D50E

; verify execution

D599   20 A6 D5   JSR $D5A6
D59C   B0 FB      BCS $D599
D59E   48         PHA
D59F   A9 00      LDA #$00
D5A1   8D 98 02   STA $0298
D5A4   68         PLA
D5A5   60         RTS
D5A6   B5 00      LDA $00,X
D5A8   30 1A      BMI $D5C4
D5AA   C9 02      CMP #$02
D5AC   90 14      BCC $D5C2
D5AE   C9 08      CMP #$08
D5B0   F0 08      BEQ $D5BA
D5B2   C9 0B      CMP #$0B
D5B4   F0 04      BEQ $D5BA
D5B6   C9 0F      CMP #$0F
D5B8   D0 0C      BNE $D5C6
D5BA   2C 98 02   BIT $0298
D5BD   30 03      BMI $D5C2
D5BF   4C 3F D6   JMP $D63F
D5C2   18         CLC
D5C3   60         RTS
D5C4   38         SEC
D5C5   60         RTS

; additional attempts for read errors

D5C6   98         TYA
D5C7   48         PHA
D5C8   A5 7F      LDA $7F
D5CA   48         PHA
D5CB   BD 5B 02   LDA $025B,X
D5CE   29 01      AND #$01
D5D0   85 7F      STA $7F
D5D2   A8         TAY
D5D3   B9 CA FE   LDA $FECA,Y
D5D6   8D 6D 02   STA $026D
D5D9   20 A6 D6   JSR $D6A6
D5DC   C9 02      CMP #$02
D5DE   B0 03      BCS $D5E3
D5E0   4C 6D D6   JMP $D66D
D5E3   BD 5B 02   LDA $025B,X
D5E6   29 F0      AND #$F0
D5E8   48         PHA
D5E9   C9 90      CMP #$90
D5EB   D0 07      BNE $D5F4
D5ED   A5 7F      LDA $7F
D5EF   09 B8      ORA #$B8
D5F1   9D 5B 02   STA $025B,X
D5F4   24 6A      BIT $6A
D5F6   70 39      BVS $D631
D5F8   A9 00      LDA #$00
D5FA   8D 99 02   STA $0299
D5FD   8D 9A 02   STA $029A
D600   AC 99 02   LDY $0299
D603   AD 9A 02   LDA $029A
D606   38         SEC
D607   F9 DB FE   SBC $FEDB,Y
D60A   8D 9A 02   STA $029A
D60D   B9 DB FE   LDA $FEDB,Y
D610   20 76 D6   JSR $D676
D613   EE 99 02   INC $0299
D616   20 A6 D6   JSR $D6A6
D619   C9 02      CMP #$02
D61B   90 08      BCC $D625
D61D   AC 99 02   LDY $0299
D620   B9 DB FE   LDA $FEDB,Y
D623   D0 DB      BNE $D600
D625   AD 9A 02   LDA $029A
D628   20 76 D6   JSR $D676
D62B   B5 00      LDA $00,X
D62D   C9 02      CMP #$02
D62F   90 2B      BCC $D65C
D631   24 6A      BIT $6A
D633   10 0F      BPL $D644
D635   68         PLA
D636   C9 90      CMP #$90
D638   D0 05      BNE $D63F
D63A   05 7F      ORA $7F
D63C   9D 5B 02   STA $025B,X
D63F   B5 00      LDA $00,X
D641   20 0A E6   JSR $E60A
D644   68         PLA
D645   2C 98 02   BIT $0298
D648   30 23      BMI $D66D
D64A   48         PHA
D64B   A9 C0      LDA #$C0
D64D   05 7F      ORA $7F
D64F   95 00      STA $00,X
D651   B5 00      LDA $00,X
D653   30 FC      BMI $D651
D655   20 A6 D6   JSR $D6A6
D658   C9 02      CMP #$02
D65A   B0 D9      BCS $D635
D65C   68         PLA
D65D   C9 90      CMP #$90
D65F   D0 0C      BNE $D66D
D661   05 7F      ORA $7F
D663   9D 5B 02   STA $025B,X
D666   20 A6 D6   JSR $D6A6
D669   C9 02      CMP #$02
D66B   B0 D2      BCS $D63F
D66D   68         PLA
D66E   85 7F      STA $7F
D670   68         PLA
D671   A8         TAY
D672   B5 00      LDA $00,X
D674   18         CLC
D675   60         RTS

; move head by half a track

D676   C9 00      CMP #$00
D678   F0 18      BEQ $D692
D67A   30 0C      BMI $D688
D67C   A0 01      LDY #$01
D67E   20 93 D6   JSR $D693
D681   38         SEC
D682   E9 01      SBC #$01
D684   D0 F6      BNE $D67C
D686   F0 0A      BEQ $D692
D688   A0 FF      LDY #$FF
D68A   20 93 D6   JSR $D693
D68D   18         CLC
D68E   69 01      ADC #$01
D690   D0 F6      BNE $D688
D692   60         RTS

; move head one track in or out

D693   48         PHA
D694   98         TYA
D695   A4 7F      LDY $7F
D697   99 FE 02   STA $02FE,Y
D69A   D9 FE 02   CMP $02FE,Y
D69D   F0 FB      BEQ $D69A
D69F   A9 00      LDA #$00
D6A1   99 FE 02   STA $02FE,Y
D6A4   68         PLA
D6A5   60         RTS

; attempt command execution multiple times

D6A6   A5 6A      LDA $6A
D6A8   29 3F      AND #$3F
D6AA   A8         TAY
D6AB   AD 6D 02   LDA $026D
D6AE   4D 00 1C   EOR $1C00
D6B1   8D 00 1C   STA $1C00
D6B4   BD 5B 02   LDA $025B,X
D6B7   95 00      STA $00,X
D6B9   B5 00      LDA $00,X
D6BB   30 FC      BMI $D6B9
D6BD   C9 02      CMP #$02
D6BF   90 03      BCC $D6C4
D6C1   88         DEY
D6C2   D0 E7      BNE $D6AB
D6C4   48         PHA
D6C5   AD 6D 02   LDA $026D
D6C8   0D 00 1C   ORA $1C00
D6CB   8D 00 1C   STA $1C00
D6CE   68         PLA
D6CF   60         RTS

; transmit param to disk controller

D6D0   20 93 DF   JSR $DF93
D6D3   0A         ASL
D6D4   A8         TAY
D6D5   A5 80      LDA $80
D6D7   99 06 00   STA $0006,Y
D6DA   A5 81      LDA $81
D6DC   99 07 00   STA $0007,Y
D6DF   A5 7F      LDA $7F
D6E1   0A         ASL
D6E2   AA         TAX
D6E3   60         RTS

; enter file in dir

D6E4   A5 83      LDA $83
D6E6   48         PHA
D6E7   A5 82      LDA $82
D6E9   48         PHA
D6EA   A5 81      LDA $81
D6EC   48         PHA
D6ED   A5 80      LDA $80
D6EF   48         PHA
D6F0   A9 11      LDA #$11
D6F2   85 83      STA $83
D6F4   20 3B DE   JSR $DE3B
D6F7   AD 4A 02   LDA $024A
D6FA   48         PHA
D6FB   A5 E2      LDA $E2
D6FD   29 01      AND #$01
D6FF   85 7F      STA $7F
D701   A6 F9      LDX $F9
D703   5D 5B 02   EOR $025B,X
D706   4A         LSR
D707   90 0C      BCC $D715
D709   A2 01      LDX #$01
D70B   8E 92 02   STX $0292
D70E   20 AC C5   JSR $C5AC
D711   F0 1D      BEQ $D730
D713   D0 28      BNE $D73D
D715   AD 91 02   LDA $0291
D718   F0 0C      BEQ $D726
D71A   C5 81      CMP $81
D71C   F0 1F      BEQ $D73D
D71E   85 81      STA $81
D720   20 60 D4   JSR $D460
D723   4C 3D D7   JMP $D73D
D726   A9 01      LDA #$01
D728   8D 92 02   STA $0292
D72B   20 17 C6   JSR $C617
D72E   D0 0D      BNE $D73D
D730   20 8D D4   JSR $D48D
D733   A5 81      LDA $81
D735   8D 91 02   STA $0291
D738   A9 02      LDA #$02
D73A   8D 92 02   STA $0292
D73D   AD 92 02   LDA $0292
D740   20 C8 D4   JSR $D4C8
D743   68         PLA
D744   8D 4A 02   STA $024A
D747   C9 04      CMP #$04
D749   D0 02      BNE $D74D
D74B   09 80      ORA #$80
D74D   20 F1 CF   JSR $CFF1
D750   68         PLA
D751   8D 80 02   STA $0280
D754   20 F1 CF   JSR $CFF1
D757   68         PLA
D758   8D 85 02   STA $0285
D75B   20 F1 CF   JSR $CFF1
D75E   20 93 DF   JSR $DF93
D761   A8         TAY
D762   AD 7A 02   LDA $027A
D765   AA         TAX
D766   A9 10      LDA #$10
D768   20 6E C6   JSR $C66E
D76B   A0 10      LDY #$10
D76D   A9 00      LDA #$00
D76F   91 94      STA ($94),Y
D771   C8         INY
D772   C0 1B      CPY #$1B
D774   90 F9      BCC $D76F
D776   AD 4A 02   LDA $024A
D779   C9 04      CMP #$04
D77B   D0 13      BNE $D790
D77D   A0 10      LDY #$10
D77F   AD 59 02   LDA $0259
D782   91 94      STA ($94),Y
D784   C8         INY
D785   AD 5A 02   LDA $025A
D788   91 94      STA ($94),Y
D78A   C8         INY
D78B   AD 58 02   LDA $0258
D78E   91 94      STA ($94),Y
D790   20 64 D4   JSR $D464
D793   68         PLA
D794   85 82      STA $82
D796   AA         TAX
D797   68         PLA
D798   85 83      STA $83
D79A   AD 91 02   LDA $0291
D79D   85 D8      STA $D8
D79F   9D 60 02   STA $0260,X
D7A2   AD 92 02   LDA $0292
D7A5   85 DD      STA $DD
D7A7   9D 66 02   STA $0266,X
D7AA   AD 4A 02   LDA $024A
D7AD   85 E7      STA $E7
D7AF   A5 7F      LDA $7F
D7B1   85 E2      STA $E2
D7B3   60         RTS

; OPEN command, secondary addr not 15

D7B4   A5 83      LDA $83
D7B6   8D 4C 02   STA $024C
D7B9   20 B3 C2   JSR $C2B3
D7BC   8E 2A 02   STX $022A
D7BF   AE 00 02   LDX $0200
D7C2   AD 4C 02   LDA $024C
D7C5   D0 2C      BNE $D7F3
D7C7   E0 2A      CPX #$2A
D7C9   D0 28      BNE $D7F3
D7CB   A5 7E      LDA $7E
D7CD   F0 4D      BEQ $D81C
D7CF   85 80      STA $80
D7D1   AD 6E 02   LDA $026E
D7D4   85 7F      STA $7F
D7D6   85 E2      STA $E2
D7D8   A9 02      LDA #$02
D7DA   85 E7      STA $E7
D7DC   AD 6F 02   LDA $026F
D7DF   85 81      STA $81
D7E1   20 00 C1   JSR $C100
D7E4   20 46 DC   JSR $DC46
D7E7   A9 04      LDA #$04
D7E9   05 7F      ORA $7F
D7EB   A6 82      LDX $82
D7ED   99 EC 00   STA $00EC,Y
D7F0   4C 94 C1   JMP $C194
D7F3   E0 24      CPX #$24
D7F5   D0 1E      BNE $D815
D7F7   AD 4C 02   LDA $024C
D7FA   D0 03      BNE $D7FF
D7FC   4C 55 DA   JMP $DA55
D7FF   20 D1 C1   JSR $C1D1
D802   AD 85 FE   LDA $FE85
D805   85 80      STA $80
D807   A9 00      LDA #$00
D809   85 81      STA $81
D80B   20 46 DC   JSR $DC46
D80E   A5 7F      LDA $7F
D810   09 02      ORA #$02
D812   4C EB D7   JMP $D7EB
D815   E0 23      CPX #$23
D817   D0 12      BNE $D82B
D819   4C 84 CB   JMP $CB84
D81C   A9 02      LDA #$02
D81E   8D 96 02   STA $0296
D821   A9 00      LDA #$00
D823   85 7F      STA $7F
D825   8D 8E 02   STA $028E
D828   20 42 D0   JSR $D042
D82B   20 E5 C1   JSR $C1E5
D82E   D0 04      BNE $D834
D830   A2 00      LDX #$00
D832   F0 0C      BEQ $D840
D834   8A         TXA
D835   F0 05      BEQ $D83C
D837   A9 30      LDA #$30
D839   4C C8 C1   JMP $C1C8
D83C   88         DEY
D83D   F0 01      BEQ $D840
D83F   88         DEY
D840   8C 7A 02   STY $027A
D843   A9 8D      LDA #$8D
D845   20 68 C2   JSR $C268
D848   E8         INX
D849   8E 78 02   STX $0278
D84C   20 12 C3   JSR $C312
D84F   20 CA C3   JSR $C3CA
D852   20 9D C4   JSR $C49D
D855   A2 00      LDX #$00
D857   8E 58 02   STX $0258
D85A   8E 97 02   STX $0297
D85D   8E 4A 02   STX $024A
D860   E8         INX
D861   EC 77 02   CPX $0277
D864   B0 10      BCS $D876
D866   20 09 DA   JSR $DA09
D869   E8         INX
D86A   EC 77 02   CPX $0277
D86D   B0 07      BCS $D876
D86F   C0 04      CPY #$04
D871   F0 3E      BEQ $D8B1
D873   20 09 DA   JSR $DA09
D876   AE 4C 02   LDX $024C
D879   86 83      STX $83
D87B   E0 02      CPX #$02
D87D   B0 12      BCS $D891
D87F   8E 97 02   STX $0297
D882   A9 40      LDA #$40
D884   8D F9 02   STA $02F9
D887   AD 4A 02   LDA $024A
D88A   D0 1B      BNE $D8A7
D88C   A9 02      LDA #$02
D88E   8D 4A 02   STA $024A
D891   AD 4A 02   LDA $024A
D894   D0 11      BNE $D8A7
D896   A5 E7      LDA $E7
D898   29 07      AND #$07
D89A   8D 4A 02   STA $024A
D89D   AD 80 02   LDA $0280
D8A0   D0 05      BNE $D8A7
D8A2   A9 01      LDA #$01
D8A4   8D 4A 02   STA $024A
D8A7   AD 97 02   LDA $0297
D8AA   C9 01      CMP #$01
D8AC   F0 18      BEQ $D8C6
D8AE   4C 40 D9   JMP $D940
D8B1   BC 7A 02   LDY $027A,X
D8B4   B9 00 02   LDA $0200,Y
D8B7   8D 58 02   STA $0258
D8BA   AD 80 02   LDA $0280
D8BD   D0 B7      BNE $D876
D8BF   A9 01      LDA #$01
D8C1   8D 97 02   STA $0297
D8C4   D0 B0      BNE $D876
D8C6   A5 E7      LDA $E7
D8C8   29 80      AND #$80
D8CA   AA         TAX
D8CB   D0 14      BNE $D8E1
D8CD   A9 20      LDA #$20
D8CF   24 E7      BIT $E7
D8D1   F0 06      BEQ $D8D9
D8D3   20 B6 C8   JSR $C8B6
D8D6   4C E3 D9   JMP $D9E3
D8D9   AD 80 02   LDA $0280
D8DC   D0 03      BNE $D8E1
D8DE   4C E3 D9   JMP $D9E3
D8E1   AD 00 02   LDA $0200
D8E4   C9 40      CMP #$40
D8E6   F0 0D      BEQ $D8F5
D8E8   8A         TXA
D8E9   D0 05      BNE $D8F0
D8EB   A9 63      LDA #$63
D8ED   4C C8 C1   JMP $C1C8
D8F0   A9 33      LDA #$33
D8F2   4C C8 C1   JMP $C1C8

; open a file with overwriting

D8F5   A5 E7      LDA $E7
D8F7   29 07      AND #$07
D8F9   CD 4A 02   CMP $024A
D8FC   D0 67      BNE $D965
D8FE   C9 04      CMP #$04
D900   F0 63      BEQ $D965
D902   20 DA DC   JSR $DCDA
D905   A5 82      LDA $82
D907   8D 70 02   STA $0270
D90A   A9 11      LDA #$11
D90C   85 83      STA $83
D90E   20 EB D0   JSR $D0EB
D911   AD 94 02   LDA $0294
D914   20 C8 D4   JSR $D4C8
D917   A0 00      LDY #$00
D919   B1 94      LDA ($94),Y
D91B   09 20      ORA #$20
D91D   91 94      STA ($94),Y
D91F   A0 1A      LDY #$1A
D921   A5 80      LDA $80
D923   91 94      STA ($94),Y
D925   C8         INY
D926   A5 81      LDA $81
D928   91 94      STA ($94),Y
D92A   AE 70 02   LDX $0270
D92D   A5 D8      LDA $D8
D92F   9D 60 02   STA $0260,X
D932   A5 DD      LDA $DD
D934   9D 66 02   STA $0266,X
D937   20 3B DE   JSR $DE3B
D93A   20 64 D4   JSR $D464
D93D   4C EF D9   JMP $D9EF
D940   AD 80 02   LDA $0280
D943   D0 05      BNE $D94A
D945   A9 62      LDA #$62
D947   4C C8 C1   JMP $C1C8
D94A   AD 97 02   LDA $0297
D94D   C9 03      CMP #$03
D94F   F0 0B      BEQ $D95C
D951   A9 20      LDA #$20
D953   24 E7      BIT $E7
D955   F0 05      BEQ $D95C
D957   A9 60      LDA #$60
D959   4C C8 C1   JMP $C1C8
D95C   A5 E7      LDA $E7
D95E   29 07      AND #$07
D960   CD 4A 02   CMP $024A
D963   F0 05      BEQ $D96A
D965   A9 64      LDA #$64
D967   4C C8 C1   JMP $C1C8
D96A   A0 00      LDY #$00
D96C   8C 79 02   STY $0279
D96F   AE 97 02   LDX $0297
D972   E0 02      CPX #$02
D974   D0 1A      BNE $D990
D976   C9 04      CMP #$04
D978   F0 EB      BEQ $D965
D97A   B1 94      LDA ($94),Y
D97C   29 4F      AND #$4F
D97E   91 94      STA ($94),Y
D980   A5 83      LDA $83
D982   48         PHA
D983   A9 11      LDA #$11
D985   85 83      STA $83
D987   20 3B DE   JSR $DE3B
D98A   20 64 D4   JSR $D464
D98D   68         PLA
D98E   85 83      STA $83
D990   20 A0 D9   JSR $D9A0
D993   AD 97 02   LDA $0297
D996   C9 02      CMP #$02
D998   D0 55      BNE $D9EF
D99A   20 2A DA   JSR $DA2A
D99D   4C 94 C1   JMP $C194

; open file for reading

D9A0   A0 13      LDY #$13
D9A2   B1 94      LDA ($94),Y
D9A4   8D 59 02   STA $0259
D9A7   C8         INY
D9A8   B1 94      LDA ($94),Y
D9AA   8D 5A 02   STA $025A
D9AD   C8         INY
D9AE   B1 94      LDA ($94),Y
D9B0   AE 58 02   LDX $0258
D9B3   8D 58 02   STA $0258
D9B6   8A         TXA
D9B7   F0 0A      BEQ $D9C3
D9B9   CD 58 02   CMP $0258
D9BC   F0 05      BEQ $D9C3
D9BE   A9 50      LDA #$50
D9C0   20 C8 C1   JSR $C1C8
D9C3   AE 79 02   LDX $0279
D9C6   BD 80 02   LDA $0280,X
D9C9   85 80      STA $80
D9CB   BD 85 02   LDA $0285,X
D9CE   85 81      STA $81
D9D0   20 46 DC   JSR $DC46
D9D3   A4 82      LDY $82
D9D5   AE 79 02   LDX $0279
D9D8   B5 D8      LDA $D8,X
D9DA   99 60 02   STA $0260,Y
D9DD   B5 DD      LDA $DD,X
D9DF   99 66 02   STA $0266,Y
D9E2   60         RTS

; open file for writing

D9E3   A5 E2      LDA $E2
D9E5   29 01      AND #$01
D9E7   85 7F      STA $7F
D9E9   20 DA DC   JSR $DCDA
D9EC   20 E4 D6   JSR $D6E4
D9EF   A5 83      LDA $83
D9F1   C9 02      CMP #$02
D9F3   B0 11      BCS $DA06
D9F5   20 3E DE   JSR $DE3E
D9F8   A5 80      LDA $80
D9FA   85 7E      STA $7E
D9FC   A5 7F      LDA $7F
D9FE   8D 6E 02   STA $026E
DA01   A5 81      LDA $81
DA03   8D 6F 02   STA $026F
DA06   4C 99 C1   JMP $C199

; check file type and control mode

DA09   BC 7A 02   LDY $027A,X
DA0C   B9 00 02   LDA $0200,Y
DA0F   A0 04      LDY #$04
DA11   88         DEY
DA12   30 08      BMI $DA1C
DA14   D9 B2 FE   CMP $FEB2,Y
DA17   D0 F8      BNE $DA11
DA19   8C 97 02   STY $0297
DA1C   A0 05      LDY #$05
DA1E   88         DEY
DA1F   30 08      BMI $DA29
DA21   D9 B6 FE   CMP $FEB6,Y
DA24   D0 F8      BNE $DA1E
DA26   8C 4A 02   STY $024A
DA29   60         RTS

; preparation for append

DA2A   20 39 CA   JSR $CA39
DA2D   A9 80      LDA #$80
DA2F   20 A6 DD   JSR $DDA6
DA32   F0 F6      BEQ $DA2A
DA34   20 95 DE   JSR $DE95
DA37   A6 81      LDX $81
DA39   E8         INX
DA3A   8A         TXA
DA3B   D0 05      BNE $DA42
DA3D   20 A3 D1   JSR $D1A3
DA40   A9 02      LDA #$02
DA42   20 C8 D4   JSR $D4C8
DA45   A6 82      LDX $82
DA47   A9 01      LDA #$01
DA49   95 F2      STA $F2,X
DA4B   A9 80      LDA #$80
DA4D   05 82      ORA $82
DA4F   A6 83      LDX $83
DA51   9D 2B 02   STA $022B,X
DA54   60         RTS

; open directory

DA55   A9 0C      LDA #$0C
DA57   8D 2A 02   STA $022A
DA5A   A9 00      LDA #$00
DA5C   AE 74 02   LDX $0274
DA5F   CA         DEX
DA60   F0 0B      BEQ $DA6D
DA62   CA         DEX
DA63   D0 21      BNE $DA86
DA65   AD 01 02   LDA $0201
DA68   20 BD C3   JSR $C3BD
DA6B   30 19      BMI $DA86
DA6D   85 E2      STA $E2
DA6F   EE 77 02   INC $0277
DA72   EE 78 02   INC $0278
DA75   EE 7A 02   INC $027A
DA78   A9 80      LDA #$80
DA7A   85 E7      STA $E7
DA7C   A9 2A      LDA #$2A
DA7E   8D 00 02   STA $0200
DA81   8D 01 02   STA $0201
DA84   D0 18      BNE $DA9E
DA86   20 E5 C1   JSR $C1E5
DA89   D0 05      BNE $DA90
DA8B   20 DC C2   JSR $C2DC
DA8E   A0 03      LDY #$03
DA90   88         DEY
DA91   88         DEY
DA92   8C 7A 02   STY $027A
DA95   20 00 C2   JSR $C200
DA98   20 98 C3   JSR $C398
DA9B   20 20 C3   JSR $C320
DA9E   20 CA C3   JSR $C3CA
DAA1   20 B7 C7   JSR $C7B7
DAA4   20 9D C4   JSR $C49D
DAA7   20 9E EC   JSR $EC9E
DAAA   20 37 D1   JSR $D137
DAAD   A6 82      LDX $82
DAAF   9D 3E 02   STA $023E,X
DAB2   A5 7F      LDA $7F
DAB4   8D 8E 02   STA $028E
DAB7   09 04      ORA #$04
DAB9   95 EC      STA $EC,X
DABB   A9 00      LDA #$00
DABD   85 A3      STA $A3
DABF   60         RTS

; close routine

DAC0   A9 00      LDA #$00
DAC2   8D F9 02   STA $02F9
DAC5   A5 83      LDA $83
DAC7   D0 0B      BNE $DAD4
DAC9   A9 00      LDA #$00
DACB   8D 54 02   STA $0254
DACE   20 27 D2   JSR $D227
DAD1   4C DA D4   JMP $D4DA
DAD4   C9 0F      CMP #$0F
DAD6   F0 14      BEQ $DAEC
DAD8   20 02 DB   JSR $DB02
DADB   A5 83      LDA $83
DADD   C9 02      CMP #$02
DADF   90 F0      BCC $DAD1
DAE1   AD 6C 02   LDA $026C
DAE4   D0 03      BNE $DAE9
DAE6   4C 94 C1   JMP $C194
DAE9   4C AD C1   JMP $C1AD
DAEC   A9 0E      LDA #$0E
DAEE   85 83      STA $83
DAF0   20 02 DB   JSR $DB02
DAF3   C6 83      DEC $83
DAF5   10 F9      BPL $DAF0
DAF7   AD 6C 02   LDA $026C
DAFA   D0 03      BNE $DAFF
DAFC   4C 94 C1   JMP $C194
DAFF   4C AD C1   JMP $C1AD

; close file

DB02   A6 83      LDX $83
DB04   BD 2B 02   LDA $022B,X
DB07   C9 FF      CMP #$FF
DB09   D0 01      BNE $DB0C
DB0B   60         RTS
DB0C   29 0F      AND #$0F
DB0E   85 82      STA $82
DB10   20 25 D1   JSR $D125
DB13   C9 07      CMP #$07
DB15   F0 0F      BEQ $DB26
DB17   C9 04      CMP #$04
DB19   F0 11      BEQ $DB2C
DB1B   20 07 D1   JSR $D107
DB1E   B0 09      BCS $DB29
DB20   20 62 DB   JSR $DB62
DB23   20 A5 DB   JSR $DBA5
DB26   20 F4 EE   JSR $EEF4
DB29   4C 27 D2   JMP $D227
DB2C   20 F1 DD   JSR $DDF1
DB2F   20 1E CF   JSR $CF1E
DB32   20 CB E1   JSR $E1CB
DB35   A6 D5      LDX $D5
DB37   86 73      STX $73
DB39   E6 73      INC $73
DB3B   A9 00      LDA #$00
DB3D   85 70      STA $70
DB3F   85 71      STA $71
DB41   A5 D6      LDA $D6
DB43   38         SEC
DB44   E9 0E      SBC #$0E
DB46   85 72      STA $72
DB48   20 51 DF   JSR $DF51
DB4B   A6 82      LDX $82
DB4D   A5 70      LDA $70
DB4F   95 B5      STA $B5,X
DB51   A5 71      LDA $71
DB53   95 BB      STA $BB,X
DB55   A9 40      LDA #$40
DB57   20 A6 DD   JSR $DDA6
DB5A   F0 03      BEQ $DB5F
DB5C   20 A5 DB   JSR $DBA5
DB5F   4C 27 D2   JMP $D227

; write last block

DB62   A6 82      LDX $82
DB64   B5 B5      LDA $B5,X
DB66   15 BB      ORA $BB,X
DB68   D0 0C      BNE $DB76
DB6A   20 E8 D4   JSR $D4E8
DB6D   C9 02      CMP #$02
DB6F   D0 05      BNE $DB76
DB71   A9 0D      LDA #$0D
DB73   20 F1 CF   JSR $CFF1
DB76   20 E8 D4   JSR $D4E8
DB79   C9 02      CMP #$02
DB7B   D0 0F      BNE $DB8C
DB7D   20 1E CF   JSR $CF1E
DB80   A6 82      LDX $82
DB82   B5 B5      LDA $B5,X
DB84   D0 02      BNE $DB88
DB86   D6 BB      DEC $BB,X
DB88   D6 B5      DEC $B5,X
DB8A   A9 00      LDA #$00
DB8C   38         SEC
DB8D   E9 01      SBC #$01
DB8F   48         PHA
DB90   A9 00      LDA #$00
DB92   20 C8 D4   JSR $D4C8
DB95   20 F1 CF   JSR $CFF1
DB98   68         PLA
DB99   20 F1 CF   JSR $CFF1
DB9C   20 C7 D0   JSR $D0C7
DB9F   20 99 D5   JSR $D599
DBA2   4C 1E CF   JMP $CF1E

; directory entry

DBA5   A6 82      LDX $82
DBA7   8E 70 02   STX $0270
DBAA   A5 83      LDA $83
DBAC   48         PHA
DBAD   BD 60 02   LDA $0260,X
DBB0   85 81      STA $81
DBB2   BD 66 02   LDA $0266,X
DBB5   8D 94 02   STA $0294
DBB8   B5 EC      LDA $EC,X
DBBA   29 01      AND #$01
DBBC   85 7F      STA $7F
DBBE   AD 85 FE   LDA $FE85
DBC1   85 80      STA $80
DBC3   20 93 DF   JSR $DF93
DBC6   48         PHA
DBC7   85 F9      STA $F9
DBC9   20 60 D4   JSR $D460
DBCC   A0 00      LDY #$00
DBCE   BD E0 FE   LDA $FEE0,X
DBD1   85 87      STA $87
DBD3   AD 94 02   LDA $0294
DBD6   85 86      STA $86
DBD8   B1 86      LDA ($86),Y
DBDA   29 20      AND #$20
DBDC   F0 43      BEQ $DC21
DBDE   20 25 D1   JSR $D125
DBE1   C9 04      CMP #$04
DBE3   F0 44      BEQ $DC29
DBE5   B1 86      LDA ($86),Y
DBE7   29 8F      AND #$8F
DBE9   91 86      STA ($86),Y
DBEB   C8         INY
DBEC   B1 86      LDA ($86),Y
DBEE   85 80      STA $80
DBF0   84 71      STY $71
DBF2   A0 1B      LDY #$1B
DBF4   B1 86      LDA ($86),Y
DBF6   48         PHA
DBF7   88         DEY
DBF8   B1 86      LDA ($86),Y
DBFA   D0 0A      BNE $DC06
DBFC   85 80      STA $80
DBFE   68         PLA
DBFF   85 81      STA $81
DC01   A9 67      LDA #$67
DC03   20 45 E6   JSR $E645
DC06   48         PHA
DC07   A9 00      LDA #$00
DC09   91 86      STA ($86),Y
DC0B   C8         INY
DC0C   91 86      STA ($86),Y
DC0E   68         PLA
DC0F   A4 71      LDY $71
DC11   91 86      STA ($86),Y
DC13   C8         INY
DC14   B1 86      LDA ($86),Y
DC16   85 81      STA $81
DC18   68         PLA
DC19   91 86      STA ($86),Y
DC1B   20 7D C8   JSR $C87D
DC1E   4C 29 DC   JMP $DC29
DC21   B1 86      LDA ($86),Y
DC23   29 0F      AND #$0F
DC25   09 80      ORA #$80
DC27   91 86      STA ($86),Y
DC29   AE 70 02   LDX $0270
DC2C   A0 1C      LDY #$1C
DC2E   B5 B5      LDA $B5,X
DC30   91 86      STA ($86),Y
DC32   C8         INY
DC33   B5 BB      LDA $BB,X
DC35   91 86      STA ($86),Y
DC37   68         PLA
DC38   AA         TAX
DC39   A9 90      LDA #$90
DC3B   05 7F      ORA $7F
DC3D   20 90 D5   JSR $D590
DC40   68         PLA
DC41   85 83      STA $83
DC43   4C 07 D1   JMP $D107

; read block, allocate buffer

DC46   A9 01      LDA #$01
DC48   20 E2 D1   JSR $D1E2
DC4B   20 B6 DC   JSR $DCB6
DC4E   AD 4A 02   LDA $024A
DC51   48         PHA
DC52   0A         ASL
DC53   05 7F      ORA $7F
DC55   95 EC      STA $EC,X
DC57   20 9B D0   JSR $D09B
DC5A   A6 82      LDX $82
DC5C   A5 80      LDA $80
DC5E   D0 05      BNE $DC65
DC60   A5 81      LDA $81
DC62   9D 44 02   STA $0244,X
DC65   68         PLA
DC66   C9 04      CMP #$04
DC68   D0 3F      BNE $DCA9
DC6A   A4 83      LDY $83
DC6C   B9 2B 02   LDA $022B,Y
DC6F   09 40      ORA #$40
DC71   99 2B 02   STA $022B,Y
DC74   AD 58 02   LDA $0258
DC77   95 C7      STA $C7,X
DC79   20 8E D2   JSR $D28E
DC7C   10 03      BPL $DC81
DC7E   4C 0F D2   JMP $D20F
DC81   A6 82      LDX $82
DC83   95 CD      STA $CD,X
DC85   AC 59 02   LDY $0259
DC88   84 80      STY $80
DC8A   AC 5A 02   LDY $025A
DC8D   84 81      STY $81
DC8F   20 D3 D6   JSR $D6D3
DC92   20 73 DE   JSR $DE73
DC95   20 99 D5   JSR $D599
DC98   A6 82      LDX $82
DC9A   A9 02      LDA #$02
DC9C   95 C1      STA $C1,X
DC9E   A9 00      LDA #$00
DCA0   20 C8 D4   JSR $D4C8
DCA3   20 53 E1   JSR $E153
DCA6   4C 3E DE   JMP $DE3E
DCA9   20 56 D1   JSR $D156
DCAC   A6 82      LDX $82
DCAE   9D 3E 02   STA $023E,X
DCB1   A9 88      LDA #$88
DCB3   95 F2      STA $F2,X
DCB5   60         RTS

; reset pointer

DCB6   A6 82      LDX $82
DCB8   B5 A7      LDA $A7,X
DCBA   0A         ASL
DCBB   A8         TAY
DCBC   A9 02      LDA #$02
DCBE   99 99 00   STA $0099,Y
DCC1   B5 AE      LDA $AE,X
DCC3   09 80      ORA #$80
DCC5   95 AE      STA $AE,X
DCC7   0A         ASL
DCC8   A8         TAY
DCC9   A9 02      LDA #$02
DCCB   99 99 00   STA $0099,Y
DCCE   A9 00      LDA #$00
DCD0   95 B5      STA $B5,X
DCD2   95 BB      STA $BB,X
DCD4   A9 00      LDA #$00
DCD6   9D 44 02   STA $0244,X
DCD9   60         RTS

; construct a new block

DCDA   20 A9 F1   JSR $F1A9
DCDD   A9 01      LDA #$01
DCDF   20 DF D1   JSR $D1DF
DCE2   20 D0 D6   JSR $D6D0
DCE5   20 B6 DC   JSR $DCB6
DCE8   A6 82      LDX $82
DCEA   AD 4A 02   LDA $024A
DCED   48         PHA
DCEE   0A         ASL
DCEF   05 7F      ORA $7F
DCF1   95 EC      STA $EC,X
DCF3   68         PLA
DCF4   C9 04      CMP #$04
DCF6   F0 05      BEQ $DCFD
DCF8   A9 01      LDA #$01
DCFA   95 F2      STA $F2,X
DCFC   60         RTS
DCFD   A4 83      LDY $83
DCFF   B9 2B 02   LDA $022B,Y
DD02   29 3F      AND #$3F
DD04   09 40      ORA #$40
DD06   99 2B 02   STA $022B,Y
DD09   AD 58 02   LDA $0258
DD0C   95 C7      STA $C7,X
DD0E   20 8E D2   JSR $D28E
DD11   10 03      BPL $DD16
DD13   4C 0F D2   JMP $D20F
DD16   A6 82      LDX $82
DD18   95 CD      STA $CD,X
DD1A   20 C1 DE   JSR $DEC1
DD1D   20 1E F1   JSR $F11E
DD20   A5 80      LDA $80
DD22   8D 59 02   STA $0259
DD25   A5 81      LDA $81
DD27   8D 5A 02   STA $025A
DD2A   A6 82      LDX $82
DD2C   B5 CD      LDA $CD,X
DD2E   20 D3 D6   JSR $D6D3
DD31   A9 00      LDA #$00
DD33   20 E9 DE   JSR $DEE9
DD36   A9 00      LDA #$00
DD38   20 8D DD   JSR $DD8D
DD3B   A9 11      LDA #$11
DD3D   20 8D DD   JSR $DD8D
DD40   A9 00      LDA #$00
DD42   20 8D DD   JSR $DD8D
DD45   AD 58 02   LDA $0258
DD48   20 8D DD   JSR $DD8D
DD4B   A5 80      LDA $80
DD4D   20 8D DD   JSR $DD8D
DD50   A5 81      LDA $81
DD52   20 8D DD   JSR $DD8D
DD55   A9 10      LDA #$10
DD57   20 E9 DE   JSR $DEE9
DD5A   20 3E DE   JSR $DE3E
DD5D   A5 80      LDA $80
DD5F   20 8D DD   JSR $DD8D
DD62   A5 81      LDA $81
DD64   20 8D DD   JSR $DD8D
DD67   20 6C DE   JSR $DE6C
DD6A   20 99 D5   JSR $D599
DD6D   A9 02      LDA #$02
DD6F   20 C8 D4   JSR $D4C8
DD72   A6 82      LDX $82
DD74   38         SEC
DD75   A9 00      LDA #$00
DD77   F5 C7      SBC $C7,X
DD79   95 C1      STA $C1,X
DD7B   20 E2 E2   JSR $E2E2
DD7E   20 19 DE   JSR $DE19
DD81   20 5E DE   JSR $DE5E
DD84   20 99 D5   JSR $D599
DD87   20 F4 EE   JSR $EEF4
DD8A   4C 98 DC   JMP $DC98

; write byte in side-sector block

DD8D   48         PHA
DD8E   A6 82      LDX $82
DD90   B5 CD      LDA $CD,X
DD92   4C FD CF   JMP $CFFD

; manipulate flags

DD95   90 06      BCC $DD9D
DD97   A6 82      LDX $82
DD99   15 EC      ORA $EC,X
DD9B   D0 06      BNE $DDA3
DD9D   A6 82      LDX $82
DD9F   49 FF      EOR #$FF
DDA1   35 EC      AND $EC,X
DDA3   95 EC      STA $EC,X
DDA5   60         RTS
DDA6   A6 82      LDX $82
DDA8   35 EC      AND $EC,X
DDAA   60         RTS

; verify command code for writing

DDAB   20 93 DF   JSR $DF93
DDAE   AA         TAX
DDAF   BD 5B 02   LDA $025B,X
DDB2   29 F0      AND #$F0
DDB4   C9 90      CMP #$90
DDB6   60         RTS

;

DDB7   A2 00      LDX #$00
DDB9   86 71      STX $71
DDBB   BD 2B 02   LDA $022B,X
DDBE   C9 FF      CMP #$FF
DDC0   D0 08      BNE $DDCA
DDC2   A6 71      LDX $71
DDC4   E8         INX
DDC5   E0 10      CPX #$10
DDC7   90 F0      BCC $DDB9
DDC9   60         RTS
DDCA   86 71      STX $71
DDCC   29 3F      AND #$3F
DDCE   A8         TAY
DDCF   B9 EC 00   LDA $00EC,Y
DDD2   29 01      AND #$01
DDD4   85 70      STA $70
DDD6   AE 53 02   LDX $0253
DDD9   B5 E2      LDA $E2,X
DDDB   29 01      AND #$01
DDDD   C5 70      CMP $70
DDDF   D0 E1      BNE $DDC2
DDE1   B9 60 02   LDA $0260,Y
DDE4   D5 D8      CMP $D8,X
DDE6   D0 DA      BNE $DDC2
DDE8   B9 66 02   LDA $0266,Y
DDEB   D5 DD      CMP $DD,X
DDED   D0 D3      BNE $DDC2
DDEF   18         CLC
DDF0   60         RTS

; write a block of a REL file

DDF1   20 9E DF   JSR $DF9E
DDF4   50 06      BVC $DDFC
DDF6   20 5E DE   JSR $DE5E
DDF9   20 99 D5   JSR $D599
DDFC   60         RTS

; write bytes for following track

DDFD   20 2B DE   JSR $DE2B
DE00   A5 80      LDA $80
DE02   91 94      STA ($94),Y
DE04   C8         INY
DE05   A5 81      LDA $81
DE07   91 94      STA ($94),Y
DE09   4C 05 E1   JMP $E105

; get following track and sector numbers

DE0C   20 2B DE   JSR $DE2B
DE0F   B1 94      LDA ($94),Y
DE11   85 80      STA $80
DE13   C8         INY
DE14   B1 94      LDA ($94),Y
DE16   85 81      STA $81
DE18   60         RTS

; following track for last block

DE19   20 2B DE   JSR $DE2B
DE1C   A9 00      LDA #$00
DE1E   91 94      STA ($94),Y
DE20   C8         INY
DE21   A6 82      LDX $82
DE23   B5 C1      LDA $C1,X
DE25   AA         TAX
DE26   CA         DEX
DE27   8A         TXA
DE28   91 94      STA ($94),Y
DE2A   60         RTS

; buffer pointer to zero

DE2B   20 93 DF   JSR $DF93
DE2E   0A         ASL
DE2F   AA         TAX
DE30   B5 9A      LDA $9A,X
DE32   85 95      STA $95
DE34   A9 00      LDA #$00
DE36   85 94      STA $94
DE38   A0 00      LDY #$00
DE3A   60         RTS

; get track and sector

DE3B   20 EB D0   JSR $D0EB
DE3E   20 93 DF   JSR $DF93
DE41   85 F9      STA $F9
DE43   0A         ASL
DE44   A8         TAY
DE45   B9 06 00   LDA $0006,Y
DE48   85 80      STA $80
DE4A   B9 07 00   LDA $0007,Y
DE4D   85 81      STA $81
DE4F   60         RTS

;

DE50   A9 90      LDA #$90
DE52   8D 4D 02   STA $024D
DE55   D0 28      BNE $DE7F
DE57   A9 80      LDA #$80
DE59   8D 4D 02   STA $024D
DE5C   D0 21      BNE $DE7F
DE5E   A9 90      LDA #$90
DE60   8D 4D 02   STA $024D
DE63   D0 26      BNE $DE8B
DE65   A9 80      LDA #$80
DE67   8D 4D 02   STA $024D
DE6A   D0 1F      BNE $DE8B
DE6C   A9 90      LDA #$90
DE6E   8D 4D 02   STA $024D
DE71   D0 02      BNE $DE75
DE73   A9 80      LDA #$80
DE75   8D 4D 02   STA $024D
DE78   A6 82      LDX $82
DE7A   B5 CD      LDA $CD,X
DE7C   AA         TAX
DE7D   10 13      BPL $DE92
DE7F   20 D0 D6   JSR $D6D0
DE82   20 93 DF   JSR $DF93
DE85   AA         TAX
DE86   A5 7F      LDA $7F
DE88   9D 5B 02   STA $025B,X
DE8B   20 15 E1   JSR $E115
DE8E   20 93 DF   JSR $DF93
DE91   AA         TAX
DE92   4C 06 D5   JMP $D506

; get following track and sector from buffer

DE95   A9 00      LDA #$00
DE97   20 C8 D4   JSR $D4C8
DE9A   20 37 D1   JSR $D137
DE9D   85 80      STA $80
DE9F   20 37 D1   JSR $D137
DEA2   85 81      STA $81
DEA4   60         RTS

; coppy buffer contents

DEA5   48         PHA
DEA6   A9 00      LDA #$00
DEA8   85 6F      STA $6F
DEAA   85 71      STA $71
DEAC   B9 E0 FE   LDA $FEE0,Y
DEAF   85 70      STA $70
DEB1   BD E0 FE   LDA $FEE0,X
DEB4   85 72      STA $72
DEB6   68         PLA
DEB7   A8         TAY
DEB8   88         DEY
DEB9   B1 6F      LDA ($6F),Y
DEBB   91 71      STA ($71),Y
DEBD   88         DEY
DEBE   10 F9      BPL $DEB9
DEC0   60         RTS

; erase buffer Y

DEC1   A8         TAY
DEC2   B9 E0 FE   LDA $FEE0,Y
DEC5   85 70      STA $70
DEC7   A9 00      LDA #$00
DEC9   85 6F      STA $6F
DECB   A8         TAY
DECC   91 6F      STA ($6F),Y
DECE   C8         INY
DECF   D0 FB      BNE $DECC
DED1   60         RTS

; get side-sector number

DED2   A9 00      LDA #$00
DED4   20 DC DE   JSR $DEDC
DED7   A0 02      LDY #$02
DED9   B1 94      LDA ($94),Y
DEDB   60         RTS

; set buffer pointer to side-sector

DEDC   85 94      STA $94
DEDE   A6 82      LDX $82
DEE0   B5 CD      LDA $CD,X
DEE2   AA         TAX
DEE3   BD E0 FE   LDA $FEE0,X
DEE6   85 95      STA $95
DEE8   60         RTS

; buffer pointer for side-sector

DEE9   48         PHA
DEEA   20 DC DE   JSR $DEDC
DEED   48         PHA
DEEE   8A         TXA
DEEF   0A         ASL
DEF0   AA         TAX
DEF1   68         PLA
DEF2   95 9A      STA $9A,X
DEF4   68         PLA
DEF5   95 99      STA $99,X
DEF7   60         RTS

; get side sector and buffer pointer

DEF8   20 66 DF   JSR $DF66
DEFB   30 0E      BMI $DF0B
DEFD   50 13      BVC $DF12
DEFF   A6 82      LDX $82
DF01   B5 CD      LDA $CD,X
DF03   20 1B DF   JSR $DF1B
DF06   20 66 DF   JSR $DF66
DF09   10 07      BPL $DF12
DF0B   20 CB E1   JSR $E1CB
DF0E   2C CE FE   BIT $FECE
DF11   60         RTS
DF12   A5 D6      LDA $D6
DF14   20 E9 DE   JSR $DEE9
DF17   2C CD FE   BIT $FECD
DF1A   60         RTS

; read side-sector

DF1B   85 F9      STA $F9
DF1D   A9 80      LDA #$80
DF1F   D0 04      BNE $DF25

; write side-sector

DF21   85 F9      STA $F9
DF23   A9 90      LDA #$90
DF25   48         PHA
DF26   B5 EC      LDA $EC,X
DF28   29 01      AND #$01
DF2A   85 7F      STA $7F
DF2C   68         PLA
DF2D   05 7F      ORA $7F
DF2F   8D 4D 02   STA $024D
DF32   B1 94      LDA ($94),Y
DF34   85 80      STA $80
DF36   C8         INY
DF37   B1 94      LDA ($94),Y
DF39   85 81      STA $81
DF3B   A5 F9      LDA $F9
DF3D   20 D3 D6   JSR $D6D3
DF40   A6 F9      LDX $F9
DF42   4C 93 D5   JMP $D593

; set buffer pointer in side-sector

DF45   A6 82      LDX $82
DF47   B5 CD      LDA $CD,X
DF49   4C EB D4   JMP $D4EB

; calculate number of blocks in a REL file

DF4C   A9 78      LDA #$78
DF4E   20 5C DF   JSR $DF5C
DF51   CA         DEX
DF52   10 F8      BPL $DF4C
DF54   A5 72      LDA $72
DF56   4A         LSR
DF57   20 5C DF   JSR $DF5C
DF5A   A5 73      LDA $73
DF5C   18         CLC
DF5D   65 70      ADC $70
DF5F   85 70      STA $70
DF61   90 02      BCC $DF65
DF63   E6 71      INC $71
DF65   60         RTS

; verify side-sector in buffer

DF66   20 D2 DE   JSR $DED2
DF69   C5 D5      CMP $D5
DF6B   D0 0E      BNE $DF7B
DF6D   A4 D6      LDY $D6
DF6F   B1 94      LDA ($94),Y
DF71   F0 04      BEQ $DF77
DF73   2C CD FE   BIT $FECD
DF76   60         RTS
DF77   2C CF FE   BIT $FECF
DF7A   60         RTS
DF7B   A5 D5      LDA $D5
DF7D   C9 06      CMP #$06
DF7F   B0 0A      BCS $DF8B
DF81   0A         ASL
DF82   A8         TAY
DF83   A9 04      LDA #$04
DF85   85 94      STA $94
DF87   B1 94      LDA ($94),Y
DF89   D0 04      BNE $DF8F
DF8B   2C D0 FE   BIT $FED0
DF8E   60         RTS
DF8F   2C CE FE   BIT $FECE
DF92   60         RTS

; get buffer number

DF93   A6 82      LDX $82
DF95   B5 A7      LDA $A7,X
DF97   10 02      BPL $DF9B
DF99   B5 AE      LDA $AE,X
DF9B   29 BF      AND #$BF
DF9D   60         RTS
DF9E   A6 82      LDX $82
DFA0   8E 57 02   STX $0257
DFA3   B5 A7      LDA $A7,X
DFA5   10 09      BPL $DFB0
DFA7   8A         TXA
DFA8   18         CLC
DFA9   69 07      ADC #$07
DFAB   8D 57 02   STA $0257
DFAE   B5 AE      LDA $AE,X
DFB0   85 70      STA $70
DFB2   29 1F      AND #$1F
DFB4   24 70      BIT $70
DFB6   60         RTS
DFB7   A6 82      LDX $82
DFB9   B5 A7      LDA $A7,X
DFBB   30 02      BMI $DFBF
DFBD   B5 AE      LDA $AE,X
DFBF   C9 FF      CMP #$FF
DFC1   60         RTS
DFC2   A6 82      LDX $82
DFC4   09 80      ORA #$80
DFC6   B4 A7      LDY $A7,X
DFC8   10 03      BPL $DFCD
DFCA   95 A7      STA $A7,X
DFCC   60         RTS
DFCD   95 AE      STA $AE,X
DFCF   60         RTS

; get next record iin REL file

DFD0   A9 20      LDA #$20
DFD2   20 9D DD   JSR $DD9D
DFD5   A9 80      LDA #$80
DFD7   20 A6 DD   JSR $DDA6
DFDA   D0 41      BNE $E01D
DFDC   A6 82      LDX $82
DFDE   F6 B5      INC $B5,X
DFE0   D0 02      BNE $DFE4
DFE2   F6 BB      INC $BB,X
DFE4   A6 82      LDX $82
DFE6   B5 C1      LDA $C1,X
DFE8   F0 2E      BEQ $E018
DFEA   20 E8 D4   JSR $D4E8
DFED   A6 82      LDX $82
DFEF   D5 C1      CMP $C1,X
DFF1   90 03      BCC $DFF6
DFF3   20 3C E0   JSR $E03C
DFF6   A6 82      LDX $82
DFF8   B5 C1      LDA $C1,X
DFFA   20 C8 D4   JSR $D4C8
DFFD   A1 99      LDA ($99,X)
DFFF   85 85      STA $85
E001   A9 20      LDA #$20
E003   20 9D DD   JSR $DD9D
E006   20 04 E3   JSR $E304
E009   48         PHA
E00A   90 28      BCC $E034
E00C   A9 00      LDA #$00
E00E   20 F6 D4   JSR $D4F6
E011   D0 21      BNE $E034
E013   68         PLA
E014   C9 02      CMP #$02
E016   F0 12      BEQ $E02A
E018   A9 80      LDA #$80
E01A   20 97 DD   JSR $DD97
E01D   20 2F D1   JSR $D12F
E020   B5 99      LDA $99,X
E022   99 44 02   STA $0244,Y
E025   A9 0D      LDA #$0D
E027   85 85      STA $85
E029   60         RTS
E02A   20 35 E0   JSR $E035
E02D   A6 82      LDX $82
E02F   A9 00      LDA #$00
E031   95 C1      STA $C1,X
E033   60         RTS
E034   68         PLA
E035   A6 82      LDX $82
E037   95 C1      STA $C1,X
E039   4C 6E E1   JMP $E16E

; write block and read next block

E03C   20 D3 D1   JSR $D1D3
E03F   20 95 DE   JSR $DE95
E042   20 9E DF   JSR $DF9E
E045   50 16      BVC $E05D
E047   20 5E DE   JSR $DE5E
E04A   20 1E CF   JSR $CF1E
E04D   A9 02      LDA #$02
E04F   20 C8 D4   JSR $D4C8
E052   20 AB DD   JSR $DDAB
E055   D0 24      BNE $E07B
E057   20 57 DE   JSR $DE57
E05A   4C 99 D5   JMP $D599
E05D   20 1E CF   JSR $CF1E
E060   20 AB DD   JSR $DDAB
E063   D0 06      BNE $E06B
E065   20 57 DE   JSR $DE57
E068   20 99 D5   JSR $D599
E06B   20 95 DE   JSR $DE95
E06E   A5 80      LDA $80
E070   F0 09      BEQ $E07B
E072   20 1E CF   JSR $CF1E
E075   20 57 DE   JSR $DE57
E078   20 1E CF   JSR $CF1E
E07B   60         RTS

; write a byte in a record

E07C   20 05 E1   JSR $E105
E07F   20 93 DF   JSR $DF93
E082   0A         ASL
E083   AA         TAX
E084   A5 85      LDA $85
E086   81 99      STA ($99,X)
E088   B4 99      LDY $99,X
E08A   C8         INY
E08B   D0 09      BNE $E096
E08D   A4 82      LDY $82
E08F   B9 C1 00   LDA $00C1,Y
E092   F0 0A      BEQ $E09E
E094   A0 02      LDY #$02
E096   98         TYA
E097   A4 82      LDY $82
E099   D9 C1 00   CMP $00C1,Y
E09C   D0 05      BNE $E0A3
E09E   A9 20      LDA #$20
E0A0   4C 97 DD   JMP $DD97
E0A3   F6 99      INC $99,X
E0A5   D0 03      BNE $E0AA
E0A7   20 3C E0   JSR $E03C
E0AA   60         RTS

; write byte in REL file

E0AB   A9 A0      LDA #$A0
E0AD   20 A6 DD   JSR $DDA6
E0B0   D0 27      BNE $E0D9
E0B2   A5 85      LDA $85
E0B4   20 7C E0   JSR $E07C
E0B7   A5 F8      LDA $F8
E0B9   F0 0D      BEQ $E0C8
E0BB   60         RTS
E0BC   A9 20      LDA #$20
E0BE   20 A6 DD   JSR $DDA6
E0C1   F0 05      BEQ $E0C8
E0C3   A9 51      LDA #$51
E0C5   8D 6C 02   STA $026C
E0C8   20 F3 E0   JSR $E0F3
E0CB   20 53 E1   JSR $E153
E0CE   AD 6C 02   LDA $026C
E0D1   F0 03      BEQ $E0D6
E0D3   4C C8 C1   JMP $C1C8
E0D6   4C BC E6   JMP $E6BC
E0D9   29 80      AND #$80
E0DB   D0 05      BNE $E0E2
E0DD   A5 F8      LDA $F8
E0DF   F0 DB      BEQ $E0BC
E0E1   60         RTS
E0E2   A5 85      LDA $85
E0E4   48         PHA
E0E5   20 1C E3   JSR $E31C
E0E8   68         PLA
E0E9   85 85      STA $85
E0EB   A9 80      LDA #$80
E0ED   20 9D DD   JSR $DD9D
E0F0   4C B2 E0   JMP $E0B2

; fill record with 0s

E0F3   A9 20      LDA #$20
E0F5   20 A6 DD   JSR $DDA6
E0F8   D0 0A      BNE $E104
E0FA   A9 00      LDA #$00
E0FC   85 85      STA $85
E0FE   20 7C E0   JSR $E07C
E101   4C F3 E0   JMP $E0F3
E104   60         RTS

; write buffer number in table

E105   A9 40      LDA #$40
E107   20 97 DD   JSR $DD97
E10A   20 9E DF   JSR $DF9E
E10D   09 40      ORA #$40
E10F   AE 57 02   LDX $0257
E112   95 A7      STA $A7,X
E114   60         RTS
E115   20 9E DF   JSR $DF9E
E118   29 BF      AND #$BF
E11A   AE 57 02   LDX $0257
E11D   95 A7      STA $A7,X
E11F   60         RTS

; get byte from REL file

E120   A9 80      LDA #$80
E122   20 A6 DD   JSR $DDA6
E125   D0 37      BNE $E15E
E127   20 2F D1   JSR $D12F
E12A   B5 99      LDA $99,X
E12C   D9 44 02   CMP $0244,Y
E12F   F0 22      BEQ $E153
E131   F6 99      INC $99,X
E133   D0 06      BNE $E13B
E135   20 3C E0   JSR $E03C
E138   20 2F D1   JSR $D12F
E13B   A1 99      LDA ($99,X)
E13D   99 3E 02   STA $023E,Y
E140   A9 89      LDA #$89
E142   99 F2 00   STA $00F2,Y
E145   B5 99      LDA $99,X
E147   D9 44 02   CMP $0244,Y
E14A   F0 01      BEQ $E14D
E14C   60         RTS
E14D   A9 81      LDA #$81
E14F   99 F2 00   STA $00F2,Y
E152   60         RTS
E153   20 D0 DF   JSR $DFD0
E156   20 2F D1   JSR $D12F
E159   A5 85      LDA $85
E15B   4C 3D E1   JMP $E13D
E15E   A6 82      LDX $82
E160   A9 0D      LDA #$0D
E162   9D 3E 02   STA $023E,X
E165   A9 81      LDA #$81
E167   95 F2      STA $F2,X
E169   A9 50      LDA #$50
E16B   20 C8 C1   JSR $C1C8
E16E   A6 82      LDX $82
E170   B5 C1      LDA $C1,X
E172   85 87      STA $87
E174   C6 87      DEC $87
E176   C9 02      CMP #$02
E178   D0 04      BNE $E17E
E17A   A9 FF      LDA #$FF
E17C   85 87      STA $87
E17E   B5 C7      LDA $C7,X
E180   85 88      STA $88
E182   20 E8 D4   JSR $D4E8
E185   A6 82      LDX $82
E187   C5 87      CMP $87
E189   90 19      BCC $E1A4
E18B   F0 17      BEQ $E1A4
E18D   20 1E CF   JSR $CF1E
E190   20 B2 E1   JSR $E1B2
E193   90 08      BCC $E19D
E195   A6 82      LDX $82
E197   9D 44 02   STA $0244,X
E19A   4C 1E CF   JMP $CF1E
E19D   20 1E CF   JSR $CF1E
E1A0   A9 FF      LDA #$FF
E1A2   85 87      STA $87
E1A4   20 B2 E1   JSR $E1B2
E1A7   B0 03      BCS $E1AC
E1A9   20 E8 D4   JSR $D4E8
E1AC   A6 82      LDX $82
E1AE   9D 44 02   STA $0244,X
E1B1   60         RTS
E1B2   20 2B DE   JSR $DE2B
E1B5   A4 87      LDY $87
E1B7   B1 94      LDA ($94),Y
E1B9   D0 0D      BNE $E1C8
E1BB   88         DEY
E1BC   C0 02      CPY #$02
E1BE   90 04      BCC $E1C4
E1C0   C6 88      DEC $88
E1C2   D0 F3      BNE $E1B7
E1C4   C6 88      DEC $88
E1C6   18         CLC
E1C7   60         RTS
E1C8   98         TYA
E1C9   38         SEC
E1CA   60         RTS

; get last side-sector

E1CB   20 D2 DE   JSR $DED2
E1CE   85 D5      STA $D5
E1D0   A9 04      LDA #$04
E1D2   85 94      STA $94
E1D4   A0 0A      LDY #$0A
E1D6   D0 04      BNE $E1DC
E1D8   88         DEY
E1D9   88         DEY
E1DA   30 26      BMI $E202
E1DC   B1 94      LDA ($94),Y
E1DE   F0 F8      BEQ $E1D8
E1E0   98         TYA
E1E1   4A         LSR
E1E2   C5 D5      CMP $D5
E1E4   F0 09      BEQ $E1EF
E1E6   85 D5      STA $D5
E1E8   A6 82      LDX $82
E1EA   B5 CD      LDA $CD,X
E1EC   20 1B DF   JSR $DF1B
E1EF   A0 00      LDY #$00
E1F1   84 94      STY $94
E1F3   B1 94      LDA ($94),Y
E1F5   D0 0B      BNE $E202
E1F7   C8         INY
E1F8   B1 94      LDA ($94),Y
E1FA   A8         TAY
E1FB   88         DEY
E1FC   84 D6      STY $D6
E1FE   98         TYA
E1FF   4C E9 DE   JMP $DEE9
E202   A9 67      LDA #$67
E204   20 45 E6   JSR $E645

; P - Position command

E207   20 B3 C2   JSR $C2B3
E20A   AD 01 02   LDA $0201
E20D   85 83      STA $83
E20F   20 EB D0   JSR $D0EB
E212   90 05      BCC $E219
E214   A9 70      LDA #$70
E216   20 C8 C1   JSR $C1C8
E219   A9 A0      LDA #$A0
E21B   20 9D DD   JSR $DD9D
E21E   20 25 D1   JSR $D125
E221   F0 05      BEQ $E228
E223   A9 64      LDA #$64
E225   20 C8 C1   JSR $C1C8
E228   B5 EC      LDA $EC,X
E22A   29 01      AND #$01
E22C   85 7F      STA $7F
E22E   AD 02 02   LDA $0202
E231   95 B5      STA $B5,X
E233   AD 03 02   LDA $0203
E236   95 BB      STA $BB,X
E238   A6 82      LDX $82
E23A   A9 89      LDA #$89
E23C   95 F2      STA $F2,X
E23E   AD 04 02   LDA $0204
E241   F0 10      BEQ $E253
E243   38         SEC
E244   E9 01      SBC #$01
E246   F0 0B      BEQ $E253
E248   D5 C7      CMP $C7,X
E24A   90 07      BCC $E253
E24C   A9 51      LDA #$51
E24E   8D 6C 02   STA $026C
E251   A9 00      LDA #$00
E253   85 D4      STA $D4
E255   20 0E CE   JSR $CE0E
E258   20 F8 DE   JSR $DEF8
E25B   50 08      BVC $E265
E25D   A9 80      LDA #$80
E25F   20 97 DD   JSR $DD97
E262   4C 5E E1   JMP $E15E
E265   20 75 E2   JSR $E275
E268   A9 80      LDA #$80
E26A   20 A6 DD   JSR $DDA6
E26D   F0 03      BEQ $E272
E26F   4C 5E E1   JMP $E15E
E272   4C 94 C1   JMP $C194
E275   20 9C E2   JSR $E29C
E278   A5 D7      LDA $D7
E27A   20 C8 D4   JSR $D4C8
E27D   A6 82      LDX $82
E27F   B5 C7      LDA $C7,X
E281   38         SEC
E282   E5 D4      SBC $D4
E284   B0 03      BCS $E289
E286   4C 02 E2   JMP $E202
E289   18         CLC
E28A   65 D7      ADC $D7
E28C   90 03      BCC $E291
E28E   69 01      ADC #$01
E290   38         SEC
E291   20 09 E0   JSR $E009
E294   4C 38 E1   JMP $E138
E297   A9 51      LDA #$51
E299   20 C8 C1   JSR $C1C8
E29C   A5 94      LDA $94
E29E   85 89      STA $89
E2A0   A5 95      LDA $95
E2A2   85 8A      STA $8A
E2A4   20 D0 E2   JSR $E2D0
E2A7   D0 01      BNE $E2AA
E2A9   60         RTS
E2AA   20 F1 DD   JSR $DDF1
E2AD   20 0C DE   JSR $DE0C
E2B0   A5 80      LDA $80
E2B2   F0 0E      BEQ $E2C2
E2B4   20 D3 E2   JSR $E2D3
E2B7   D0 06      BNE $E2BF
E2B9   20 1E CF   JSR $CF1E
E2BC   4C DA D2   JMP $D2DA
E2BF   20 DA D2   JSR $D2DA
E2C2   A0 00      LDY #$00
E2C4   B1 89      LDA ($89),Y
E2C6   85 80      STA $80
E2C8   C8         INY
E2C9   B1 89      LDA ($89),Y
E2CB   85 81      STA $81
E2CD   4C AF D0   JMP $D0AF
E2D0   20 3E DE   JSR $DE3E
E2D3   A0 00      LDY #$00
E2D5   B1 89      LDA ($89),Y
E2D7   C5 80      CMP $80
E2D9   F0 01      BEQ $E2DC
E2DB   60         RTS
E2DC   C8         INY
E2DD   B1 89      LDA ($89),Y
E2DF   C5 81      CMP $81
E2E1   60         RTS

; divide data blocks into records

E2E2   20 2B DE   JSR $DE2B
E2E5   A0 02      LDY #$02
E2E7   A9 00      LDA #$00
E2E9   91 94      STA ($94),Y
E2EB   C8         INY
E2EC   D0 FB      BNE $E2E9
E2EE   20 04 E3   JSR $E304
E2F1   95 C1      STA $C1,X
E2F3   A8         TAY
E2F4   A9 FF      LDA #$FF
E2F6   91 94      STA ($94),Y
E2F8   20 04 E3   JSR $E304
E2FB   90 F4      BCC $E2F1
E2FD   D0 04      BNE $E303
E2FF   A9 00      LDA #$00
E301   95 C1      STA $C1,X
E303   60         RTS

; set pointer to next record

E304   A6 82      LDX $82
E306   B5 C1      LDA $C1,X
E308   38         SEC
E309   F0 0D      BEQ $E318
E30B   18         CLC
E30C   75 C7      ADC $C7,X
E30E   90 0B      BCC $E31B
E310   D0 06      BNE $E318
E312   A9 02      LDA #$02
E314   2C CC FE   BIT $FECC
E317   60         RTS
E318   69 01      ADC #$01
E31A   38         SEC
E31B   60         RTS

; expand side-sector

E31C   20 D3 D1   JSR $D1D3
E31F   20 CB E1   JSR $E1CB
E322   20 9C E2   JSR $E29C
E325   20 7B CF   JSR $CF7B
E328   A5 D6      LDA $D6
E32A   85 87      STA $87
E32C   A5 D5      LDA $D5
E32E   85 86      STA $86
E330   A9 00      LDA #$00
E332   85 88      STA $88
E334   A9 00      LDA #$00
E336   85 D4      STA $D4
E338   20 0E CE   JSR $CE0E
E33B   20 4D EF   JSR $EF4D
E33E   A4 82      LDY $82
E340   B6 C7      LDX $C7,Y
E342   CA         DEX
E343   8A         TXA
E344   18         CLC
E345   65 D7      ADC $D7
E347   90 0C      BCC $E355
E349   E6 D6      INC $D6
E34B   E6 D6      INC $D6
E34D   D0 06      BNE $E355
E34F   E6 D5      INC $D5
E351   A9 10      LDA #$10
E353   85 D6      STA $D6
E355   A5 87      LDA $87
E357   18         CLC
E358   69 02      ADC #$02
E35A   20 E9 DE   JSR $DEE9
E35D   A5 D5      LDA $D5
E35F   C9 06      CMP #$06
E361   90 05      BCC $E368
E363   A9 52      LDA #$52
E365   20 C8 C1   JSR $C1C8
E368   A5 D6      LDA $D6
E36A   38         SEC
E36B   E5 87      SBC $87
E36D   B0 03      BCS $E372
E36F   E9 0F      SBC #$0F
E371   18         CLC
E372   85 72      STA $72
E374   A5 D5      LDA $D5
E376   E5 86      SBC $86
E378   85 73      STA $73
E37A   A2 00      LDX #$00
E37C   86 70      STX $70
E37E   86 71      STX $71
E380   AA         TAX
E381   20 51 DF   JSR $DF51
E384   A5 71      LDA $71
E386   D0 07      BNE $E38F
E388   A6 70      LDX $70
E38A   CA         DEX
E38B   D0 02      BNE $E38F
E38D   E6 88      INC $88
E38F   CD 73 02   CMP $0273
E392   90 09      BCC $E39D
E394   D0 CD      BNE $E363
E396   AD 72 02   LDA $0272
E399   C5 70      CMP $70
E39B   90 C6      BCC $E363
E39D   A9 01      LDA #$01
E39F   20 F6 D4   JSR $D4F6
E3A2   18         CLC
E3A3   69 01      ADC #$01
E3A5   A6 82      LDX $82
E3A7   95 C1      STA $C1,X
E3A9   20 1E F1   JSR $F11E
E3AC   20 FD DD   JSR $DDFD
E3AF   A5 88      LDA $88
E3B1   D0 15      BNE $E3C8
E3B3   20 5E DE   JSR $DE5E
E3B6   20 1E CF   JSR $CF1E
E3B9   20 D0 D6   JSR $D6D0
E3BC   20 1E F1   JSR $F11E
E3BF   20 FD DD   JSR $DDFD
E3C2   20 E2 E2   JSR $E2E2
E3C5   4C D4 E3   JMP $E3D4
E3C8   20 1E CF   JSR $CF1E
E3CB   20 D0 D6   JSR $D6D0
E3CE   20 E2 E2   JSR $E2E2
E3D1   20 19 DE   JSR $DE19
E3D4   20 5E DE   JSR $DE5E
E3D7   20 0C DE   JSR $DE0C
E3DA   A5 80      LDA $80
E3DC   48         PHA
E3DD   A5 81      LDA $81
E3DF   48         PHA
E3E0   20 3E DE   JSR $DE3E
E3E3   A5 81      LDA $81
E3E5   48         PHA
E3E6   A5 80      LDA $80
E3E8   48         PHA
E3E9   20 45 DF   JSR $DF45
E3EC   AA         TAX
E3ED   D0 0A      BNE $E3F9
E3EF   20 4E E4   JSR $E44E
E3F2   A9 10      LDA #$10
E3F4   20 E9 DE   JSR $DEE9
E3F7   E6 86      INC $86
E3F9   68         PLA
E3FA   20 8D DD   JSR $DD8D
E3FD   68         PLA
E3FE   20 8D DD   JSR $DD8D
E401   68         PLA
E402   85 81      STA $81
E404   68         PLA
E405   85 80      STA $80
E407   F0 0F      BEQ $E418
E409   A5 86      LDA $86
E40B   C5 D5      CMP $D5
E40D   D0 A7      BNE $E3B6
E40F   20 45 DF   JSR $DF45
E412   C5 D6      CMP $D6
E414   90 A0      BCC $E3B6
E416   F0 B0      BEQ $E3C8
E418   20 45 DF   JSR $DF45
E41B   48         PHA
E41C   A9 00      LDA #$00
E41E   20 DC DE   JSR $DEDC
E421   A9 00      LDA #$00
E423   A8         TAY
E424   91 94      STA ($94),Y
E426   C8         INY
E427   68         PLA
E428   38         SEC
E429   E9 01      SBC #$01
E42B   91 94      STA ($94),Y
E42D   20 6C DE   JSR $DE6C
E430   20 99 D5   JSR $D599
E433   20 F4 EE   JSR $EEF4
E436   20 0E CE   JSR $CE0E
E439   20 1E CF   JSR $CF1E
E43C   20 F8 DE   JSR $DEF8
E43F   70 03      BVS $E444
E441   4C 75 E2   JMP $E275
E444   A9 80      LDA #$80
E446   20 97 DD   JSR $DD97
E449   A9 50      LDA #$50
E44B   20 C8 C1   JSR $C1C8

; write side-sector and allocate new

E44E   20 1E F1   JSR $F11E
E451   20 1E CF   JSR $CF1E
E454   20 F1 DD   JSR $DDF1
E457   20 93 DF   JSR $DF93
E45A   48         PHA
E45B   20 C1 DE   JSR $DEC1
E45E   A6 82      LDX $82
E460   B5 CD      LDA $CD,X
E462   A8         TAY
E463   68         PLA
E464   AA         TAX
E465   A9 10      LDA #$10
E467   20 A5 DE   JSR $DEA5
E46A   A9 00      LDA #$00
E46C   20 DC DE   JSR $DEDC
E46F   A0 02      LDY #$02
E471   B1 94      LDA ($94),Y
E473   48         PHA
E474   A9 00      LDA #$00
E476   20 C8 D4   JSR $D4C8
E479   68         PLA
E47A   18         CLC
E47B   69 01      ADC #$01
E47D   91 94      STA ($94),Y
E47F   0A         ASL
E480   69 04      ADC #$04
E482   85 89      STA $89
E484   A8         TAY
E485   38         SEC
E486   E9 02      SBC #$02
E488   85 8A      STA $8A
E48A   A5 80      LDA $80
E48C   85 87      STA $87
E48E   91 94      STA ($94),Y
E490   C8         INY
E491   A5 81      LDA $81
E493   85 88      STA $88
E495   91 94      STA ($94),Y
E497   A0 00      LDY #$00
E499   98         TYA
E49A   91 94      STA ($94),Y
E49C   C8         INY
E49D   A9 11      LDA #$11
E49F   91 94      STA ($94),Y
E4A1   A9 10      LDA #$10
E4A3   20 C8 D4   JSR $D4C8
E4A6   20 50 DE   JSR $DE50
E4A9   20 99 D5   JSR $D599
E4AC   A6 82      LDX $82
E4AE   B5 CD      LDA $CD,X
E4B0   48         PHA
E4B1   20 9E DF   JSR $DF9E
E4B4   A6 82      LDX $82
E4B6   95 CD      STA $CD,X
E4B8   68         PLA
E4B9   AE 57 02   LDX $0257
E4BC   95 A7      STA $A7,X
E4BE   A9 00      LDA #$00
E4C0   20 C8 D4   JSR $D4C8
E4C3   A0 00      LDY #$00
E4C5   A5 80      LDA $80
E4C7   91 94      STA ($94),Y
E4C9   C8         INY
E4CA   A5 81      LDA $81
E4CC   91 94      STA ($94),Y
E4CE   4C DE E4   JMP $E4DE
E4D1   20 93 DF   JSR $DF93
E4D4   A6 82      LDX $82
E4D6   20 1B DF   JSR $DF1B
E4D9   A9 00      LDA #$00
E4DB   20 C8 D4   JSR $D4C8
E4DE   C6 8A      DEC $8A
E4E0   C6 8A      DEC $8A
E4E2   A4 89      LDY $89
E4E4   A5 87      LDA $87
E4E6   91 94      STA ($94),Y
E4E8   C8         INY
E4E9   A5 88      LDA $88
E4EB   91 94      STA ($94),Y
E4ED   20 5E DE   JSR $DE5E
E4F0   20 99 D5   JSR $D599
E4F3   A4 8A      LDY $8A
E4F5   C0 03      CPY #$03
E4F7   B0 D8      BCS $E4D1
E4F9   4C 1E CF   JMP $CF1E

; table of error messages, format: error numbers, description
; (with start and end letters +$80)
; If value AND $7F less than $10 then look up text in pointer table.
; ok

E4FC   .BY $00
E4FD   .BY $A0,$4F,$CB

; read eroor

E500   .BY $20,$21,$22,$23,$24,$27
E506   .BY $D2,$45,$41,$44
E50A   .BY $89

; file too large

E50B   .BY $52
E50C   .BY $83
E50D   .BY $20,$54,$4F,$4F
E511   .BY $20,$4C,$41,$52,$47,$C5

; record not present

E517   .BY $50
E518   .BY $8B
E519   .BY $06
E51A   .BY $20,$50,$52,$45,$53,$45,$4E,$D4

; overflow in record

E522   .BY $51
E523   .BY $CF,$56,$45,$52,$46,$4C,$4F,$57
E52B   .BY $20,$49,$4E
E52E   .BY $8B

; write error

E52F   .BY $25,$28
E531   .BY $8A
E532   .BY $89

; write protect on

E533   .BY $26
E534   .BY $8A
E535   .BY $20,$50,$52,$4F,$54,$45,$43,$54
E53D   .BY $20,$4F,$CE

; disk id mismatch

E540   .BY $29
E541   .BY $88
E542   .BY $20,$49,$44
E545   .BY $85

; syntax error

E546   .BY $30,$31,$32,$33,$34
E54B   .BY $D3,$59,$4E,$54,$41,$58
E551   .BY $89

; write file open

E552   .BY $60
E553   .BY $8A
E554   .BY $03
E555   .BY $84

; file exists

E556   .BY $63
E557   .BY $83
E558   .BY $20,$45,$58,$49,$53,$54,$D3

; file type mismatch

E55F   .BY $64
E560   .BY $83
E561   .BY $20,$54,$59,$50,$45
E566   .BY $85

; no block

E567   .BY $65
E568   .BY $CE,$4F
E56A   .BY $20,$42,$4C,$4F,$43,$CB

; illegal track or sector

E570   .BY $66,$67
E572   .BY $C9,$4C,$4C,$45,$47,$41,$4C
E579   .BY $20,$54,$52,$41,$43,$4B
E57F   .BY $20,$4F,$52
E582   .BY $20,$53,$45,$43,$54,$4F,$D2

; file not open

E589   .BY $61
E58A   .BY $83
E58B   .BY $06
E58C   .BY $84

; file not found

E58D   .BY $39,$62
E58F   .BY $83
E590   .BY $06
E591   .BY $87

; files scratched

E592   .BY $01
E593   .BY $83
E594   .BY $53,$20
E596   .BY $53,$43,$52,$41,$54,$43,$48,$45,$C4

; no channel

E59F   .BY $70
E5A0   .BY $CE,$4F
E5A2   .BY $20,$43,$48,$41,$4E,$4E,$45,$CC

; dir error

E5AA   .BY $71
E5AB   .BY $C4,$49,$52
E5AE   .BY $89

; disk full

E5AF   .BY $72
E5B0   .BY $88
E5B1   .BY $20,$46,$55,$4C,$CC

; cbm dos v2.6 1541

E5B6   .BY $73
E5B7   .BY $C3,$42,$4D
E5BA   .BY $20,$44,$4F,$53
E5BE   .BY $20,$56,$32,$2E,$36
E5C3   .BY $20,$31,$35,$34,$B1

; drive not ready

E5C8   .BY $74
E5C9   .BY $C4,$52,$49,$56,$45
E5CE   .BY $06
E5CF   .BY $20,$52,$45,$41,$44,$D9

; indexed words - format: index number, description

E5D5   .BY $09
E5D6   .BY $C5,$52,$52,$4F,$D2
E5DB   .BY $0A
E5DC   .BY $D7,$52,$49,$54,$C5
E5E1   .BY $03
E5E2   .BY $C6,$49,$4C,$C5
E5E6   .BY $04
E5E7   .BY $CF,$50,$45,$CE
E5EB   .BY $05
E5EC   .BY $CD,$49,$53,$4D,$41,$54,$43,$C8
E5F4   .BY $06
E5F5   .BY $CE,$4F,$D4
E5F8   .BY $07
E5F9   .BY $C6,$4F,$55,$4E,$C4
E5FE   .BY $08
E5FF   .BY $C4,$49,$53,$CB
E603   .BY $0B
E604   .BY $D2,$45,$43,$4F,$52,$C4

; prepare error number and message

E60A   48         PHA
E60B   86 F9      STX $F9
E60D   8A         TXA
E60E   0A         ASL
E60F   AA         TAX
E610   B5 06      LDA $06,X
E612   85 80      STA $80
E614   B5 07      LDA $07,X
E616   85 81      STA $81
E618   68         PLA
E619   29 0F      AND #$0F
E61B   F0 08      BEQ $E625
E61D   C9 0F      CMP #$0F
E61F   D0 06      BNE $E627
E621   A9 74      LDA #$74
E623   D0 08      BNE $E62D
E625   A9 06      LDA #$06
E627   09 20      ORA #$20
E629   AA         TAX
E62A   CA         DEX
E62B   CA         DEX
E62C   8A         TXA
E62D   48         PHA
E62E   AD 2A 02   LDA $022A
E631   C9 00      CMP #$00
E633   D0 0F      BNE $E644
E635   A9 FF      LDA #$FF
E637   8D 2A 02   STA $022A
E63A   68         PLA
E63B   20 C7 E6   JSR $E6C7
E63E   20 42 D0   JSR $D042
E641   4C 48 E6   JMP $E648
E644   68         PLA
E645   20 C7 E6   JSR $E6C7
E648   20 BD C1   JSR $C1BD
E64B   A9 00      LDA #$00
E64D   8D F9 02   STA $02F9
E650   20 2C C1   JSR $C12C
E653   20 DA D4   JSR $D4DA
E656   A9 00      LDA #$00
E658   85 A3      STA $A3
E65A   A2 45      LDX #$45
E65C   9A         TXS
E65D   A5 84      LDA $84
E65F   29 0F      AND #$0F
E661   85 83      STA $83
E663   C9 0F      CMP #$0F
E665   F0 31      BEQ $E698
E667   78         SEI
E668   A5 79      LDA $79
E66A   D0 1C      BNE $E688
E66C   A5 7A      LDA $7A
E66E   D0 10      BNE $E680
E670   A6 83      LDX $83
E672   BD 2B 02   LDA $022B,X
E675   C9 FF      CMP #$FF
E677   F0 1F      BEQ $E698
E679   29 0F      AND #$0F
E67B   85 82      STA $82
E67D   4C 8E E6   JMP $E68E

; Talk

E680   20 EB D0   JSR $D0EB
E683   EA         NOP
E684   EA         NOP
E685   EA         NOP
E686   D0 06      BNE $E68E

; Listen

E688   20 07 D1   JSR $D107
E68B   EA         NOP
E68C   EA         NOP
E68D   EA         NOP
E68E   20 25 D1   JSR $D125
E691   C9 04      CMP #$04
E693   B0 03      BCS $E698
E695   20 27 D2   JSR $D227
E698   4C E7 EB   JMP $EBE7

; convert hex to decimal (2 bytes)

E69B   AA         TAX
E69C   A9 00      LDA #$00
E69E   F8         SED
E69F   E0 00      CPX #$00
E6A1   F0 07      BEQ $E6AA
E6A3   18         CLC
E6A4   69 01      ADC #$01
E6A6   CA         DEX
E6A7   4C 9F E6   JMP $E69F
E6AA   D8         CLD

; divide BCD number into two bytes

E6AB   AA         TAX
E6AC   4A         LSR
E6AD   4A         LSR
E6AE   4A         LSR
E6AF   4A         LSR
E6B0   20 B4 E6   JSR $E6B4
E6B3   8A         TXA
E6B4   29 0F      AND #$0F
E6B6   09 30      ORA #$30
E6B8   91 A5      STA ($A5),Y
E6BA   C8         INY
E6BB   60         RTS

; write OK in buffer

E6BC   20 23 C1   JSR $C123
E6BF   A9 00      LDA #$00
E6C1   A0 00      LDY #$00
E6C3   84 80      STY $80
E6C5   84 81      STY $81

; error message in buffer

E6C7   A0 00      LDY #$00
E6C9   A2 D5      LDX #$D5
E6CB   86 A5      STX $A5
E6CD   A2 02      LDX #$02
E6CF   86 A6      STX $A6
E6D1   20 AB E6   JSR $E6AB
E6D4   A9 2C      LDA #$2C
E6D6   91 A5      STA ($A5),Y
E6D8   C8         INY
E6D9   AD D5 02   LDA $02D5
E6DC   8D 43 02   STA $0243
E6DF   8A         TXA
E6E0   20 06 E7   JSR $E706
E6E3   A9 2C      LDA #$2C
E6E5   91 A5      STA ($A5),Y
E6E7   C8         INY
E6E8   A5 80      LDA $80
E6EA   20 9B E6   JSR $E69B
E6ED   A9 2C      LDA #$2C
E6EF   91 A5      STA ($A5),Y
E6F1   C8         INY
E6F2   A5 81      LDA $81
E6F4   20 9B E6   JSR $E69B
E6F7   88         DEY
E6F8   98         TYA
E6F9   18         CLC
E6FA   69 D5      ADC #$D5
E6FC   8D 49 02   STA $0249
E6FF   E6 A5      INC $A5
E701   A9 88      LDA #$88
E703   85 F7      STA $F7
E705   60         RTS

; write error message to buffer

E706   AA         TAX
E707   A5 86      LDA $86
E709   48         PHA
E70A   A5 87      LDA $87
E70C   48         PHA
E70D   A9 FC      LDA #$FC
E70F   85 86      STA $86
E711   A9 E4      LDA #$E4
E713   85 87      STA $87
E715   8A         TXA
E716   A2 00      LDX #$00
E718   C1 86      CMP ($86,X)
E71A   F0 21      BEQ $E73D
E71C   48         PHA
E71D   20 75 E7   JSR $E775
E720   90 05      BCC $E727
E722   20 75 E7   JSR $E775
E725   90 FB      BCC $E722
E727   A5 87      LDA $87
E729   C9 E6      CMP #$E6
E72B   90 08      BCC $E735
E72D   D0 0A      BNE $E739
E72F   A9 0A      LDA #$0A
E731   C5 86      CMP $86
E733   90 04      BCC $E739
E735   68         PLA
E736   4C 18 E7   JMP $E718
E739   68         PLA
E73A   4C 4D E7   JMP $E74D
E73D   20 67 E7   JSR $E767
E740   90 FB      BCC $E73D
E742   20 54 E7   JSR $E754
E745   20 67 E7   JSR $E767
E748   90 F8      BCC $E742
E74A   20 54 E7   JSR $E754
E74D   68         PLA
E74E   85 87      STA $87
E750   68         PLA
E751   85 86      STA $86
E753   60         RTS

; get character and in buffer

E754   C9 20      CMP #$20
E756   B0 0B      BCS $E763
E758   AA         TAX
E759   A9 20      LDA #$20
E75B   91 A5      STA ($A5),Y
E75D   C8         INY
E75E   8A         TXA
E75F   20 06 E7   JSR $E706
E762   60         RTS
E763   91 A5      STA ($A5),Y
E765   C8         INY
E766   60         RTS

; get a char of the error message

E767   E6 86      INC $86
E769   D0 02      BNE $E76D
E76B   E6 87      INC $87
E76D   A1 86      LDA ($86,X)
E76F   0A         ASL
E770   A1 86      LDA ($86,X)
E772   29 7F      AND #$7F
E774   60         RTS

; increment pointer

E775   20 6D E7   JSR $E76D
E778   E6 86      INC $86
E77A   D0 02      BNE $E77E
E77C   E6 87      INC $87
E77E   60         RTS

;

E77F   60         RTS

; check for auto start removed

E780   60         RTS
E781   .BY $EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA
E789   .BY $EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA
E791   .BY $EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA
E799   .BY $EA,$EA,$EA,$EA,$EA,$EA,$EA,$EA
E7A1   .BY $EA
E7A2   60         RTS

; ∧ - USR file execute command

E7A3   A9 8D      LDA #$8D
E7A5   20 68 C2   JSR $C268
E7A8   20 58 F2   JSR $F258
E7AB   AD 78 02   LDA $0278
E7AE   48         PHA
E7AF   A9 01      LDA #$01
E7B1   8D 78 02   STA $0278
E7B4   A9 FF      LDA #$FF
E7B6   85 86      STA $86
E7B8   20 4F C4   JSR $C44F
E7BB   AD 80 02   LDA $0280
E7BE   D0 05      BNE $E7C5
E7C0   A9 39      LDA #$39
E7C2   20 C8 C1   JSR $C1C8
E7C5   68         PLA
E7C6   8D 78 02   STA $0278
E7C9   AD 80 02   LDA $0280
E7CC   85 80      STA $80
E7CE   AD 85 02   LDA $0285
E7D1   85 81      STA $81
E7D3   A9 03      LDA #$03
E7D5   20 77 D4   JSR $D477
E7D8   A9 00      LDA #$00
E7DA   85 87      STA $87
E7DC   20 39 E8   JSR $E839
E7DF   85 88      STA $88
E7E1   20 4B E8   JSR $E84B
E7E4   20 39 E8   JSR $E839
E7E7   85 89      STA $89
E7E9   20 4B E8   JSR $E84B
E7EC   A5 86      LDA $86
E7EE   F0 0A      BEQ $E7FA
E7F0   A5 88      LDA $88
E7F2   48         PHA
E7F3   A5 89      LDA $89
E7F5   48         PHA
E7F6   A9 00      LDA #$00
E7F8   85 86      STA $86
E7FA   20 39 E8   JSR $E839
E7FD   85 8A      STA $8A
E7FF   20 4B E8   JSR $E84B
E802   20 39 E8   JSR $E839
E805   A0 00      LDY #$00
E807   91 88      STA ($88),Y
E809   20 4B E8   JSR $E84B
E80C   A5 88      LDA $88
E80E   18         CLC
E80F   69 01      ADC #$01
E811   85 88      STA $88
E813   90 02      BCC $E817
E815   E6 89      INC $89
E817   C6 8A      DEC $8A
E819   D0 E7      BNE $E802
E81B   20 35 CA   JSR $CA35
E81E   A5 85      LDA $85
E820   C5 87      CMP $87
E822   F0 08      BEQ $E82C
E824   20 3E DE   JSR $DE3E
E827   A9 50      LDA #$50
E829   20 45 E6   JSR $E645
E82C   A5 F8      LDA $F8
E82E   D0 A8      BNE $E7D8
E830   68         PLA
E831   85 89      STA $89
E833   68         PLA
E834   85 88      STA $88
E836   6C 88 00   JMP ($0088)
E839   20 35 CA   JSR $CA35
E83C   A5 F8      LDA $F8
E83E   D0 08      BNE $E848
E840   20 3E DE   JSR $DE3E
E843   A9 51      LDA #$51
E845   20 45 E6   JSR $E645
E848   A5 85      LDA $85
E84A   60         RTS

; generate checksum

E84B   18         CLC
E84C   65 87      ADC $87
E84E   69 00      ADC #$00
E850   85 87      STA $87
E852   60         RTS

; IRQ routine for serial bus

E853   AD 01 18   LDA $1801
E856   A9 01      LDA #$01
E858   85 7C      STA $7C
E85A   60         RTS

; service the serial bus

E85B   78         SEI
E85C   A9 00      LDA #$00
E85E   85 7C      STA $7C
E860   85 79      STA $79
E862   85 7A      STA $7A
E864   A2 45      LDX #$45
E866   9A         TXS
E867   A9 80      LDA #$80
E869   85 F8      STA $F8
E86B   85 7D      STA $7D
E86D   20 B7 E9   JSR $E9B7
E870   20 A5 E9   JSR $E9A5
E873   AD 00 18   LDA $1800
E876   09 10      ORA #$10
E878   8D 00 18   STA $1800
E87B   AD 00 18   LDA $1800
E87E   10 57      BPL $E8D7
E880   29 04      AND #$04
E882   D0 F7      BNE $E87B
E884   20 C9 E9   JSR $E9C9
E887   C9 3F      CMP #$3F
E889   D0 06      BNE $E891
E88B   A9 00      LDA #$00
E88D   85 79      STA $79
E88F   F0 71      BEQ $E902
E891   C9 5F      CMP #$5F
E893   D0 06      BNE $E89B
E895   A9 00      LDA #$00
E897   85 7A      STA $7A
E899   F0 67      BEQ $E902
E89B   C5 78      CMP $78
E89D   D0 0A      BNE $E8A9
E89F   A9 01      LDA #$01
E8A1   85 7A      STA $7A
E8A3   A9 00      LDA #$00
E8A5   85 79      STA $79
E8A7   F0 29      BEQ $E8D2
E8A9   C5 77      CMP $77
E8AB   D0 0A      BNE $E8B7
E8AD   A9 01      LDA #$01
E8AF   85 79      STA $79
E8B1   A9 00      LDA #$00
E8B3   85 7A      STA $7A
E8B5   F0 1B      BEQ $E8D2
E8B7   AA         TAX
E8B8   29 60      AND #$60
E8BA   C9 60      CMP #$60
E8BC   D0 3F      BNE $E8FD
E8BE   8A         TXA
E8BF   85 84      STA $84
E8C1   29 0F      AND #$0F
E8C3   85 83      STA $83
E8C5   A5 84      LDA $84
E8C7   29 F0      AND #$F0
E8C9   C9 E0      CMP #$E0
E8CB   D0 35      BNE $E902
E8CD   58         CLI
E8CE   20 C0 DA   JSR $DAC0
E8D1   78         SEI
E8D2   2C 00 18   BIT $1800
E8D5   30 AD      BMI $E884
E8D7   A9 00      LDA #$00
E8D9   85 7D      STA $7D
E8DB   AD 00 18   LDA $1800
E8DE   29 EF      AND #$EF
E8E0   8D 00 18   STA $1800
E8E3   A5 79      LDA $79
E8E5   F0 06      BEQ $E8ED
E8E7   20 2E EA   JSR $EA2E
E8EA   4C E7 EB   JMP $EBE7
E8ED   A5 7A      LDA $7A
E8EF   F0 09      BEQ $E8FA
E8F1   20 9C E9   JSR $E99C
E8F4   20 AE E9   JSR $E9AE
E8F7   20 09 E9   JSR $E909
E8FA   4C 4E EA   JMP $EA4E
E8FD   A9 10      LDA #$10
E8FF   8D 00 18   STA $1800
E902   2C 00 18   BIT $1800
E905   10 D0      BPL $E8D7
E907   30 F9      BMI $E902

; send data

E909   78         SEI
E90A   20 EB D0   JSR $D0EB
E90D   B0 06      BCS $E915
E90F   A6 82      LDX $82
E911   B5 F2      LDA $F2,X
E913   30 01      BMI $E916
E915   60         RTS
E916   20 59 EA   JSR $EA59
E919   20 C0 E9   JSR $E9C0
E91C   29 01      AND #$01
E91E   08         PHP
E91F   20 B7 E9   JSR $E9B7
E922   28         PLP
E923   F0 12      BEQ $E937
E925   20 59 EA   JSR $EA59
E928   20 C0 E9   JSR $E9C0
E92B   29 01      AND #$01
E92D   D0 F6      BNE $E925
E92F   A6 82      LDX $82
E931   B5 F2      LDA $F2,X
E933   29 08      AND #$08
E935   D0 14      BNE $E94B
E937   20 59 EA   JSR $EA59
E93A   20 C0 E9   JSR $E9C0
E93D   29 01      AND #$01
E93F   D0 F6      BNE $E937
E941   20 59 EA   JSR $EA59
E944   20 C0 E9   JSR $E9C0
E947   29 01      AND #$01
E949   F0 F6      BEQ $E941
E94B   20 AE E9   JSR $E9AE
E94E   20 59 EA   JSR $EA59
E951   20 C0 E9   JSR $E9C0
E954   29 01      AND #$01
E956   D0 F3      BNE $E94B
E958   A9 08      LDA #$08
E95A   85 98      STA $98
E95C   20 C0 E9   JSR $E9C0
E95F   29 01      AND #$01
E961   D0 36      BNE $E999
E963   A6 82      LDX $82
E965   BD 3E 02   LDA $023E,X
E968   6A         ROR
E969   9D 3E 02   STA $023E,X
E96C   B0 05      BCS $E973
E96E   20 A5 E9   JSR $E9A5
E971   D0 03      BNE $E976
E973   20 9C E9   JSR $E99C
E976   20 B7 E9   JSR $E9B7
E979   A5 23      LDA $23
E97B   D0 03      BNE $E980
E97D   20 F3 FE   JSR $FEF3
E980   20 FB FE   JSR $FEFB
E983   C6 98      DEC $98
E985   D0 D5      BNE $E95C
E987   20 59 EA   JSR $EA59
E98A   20 C0 E9   JSR $E9C0
E98D   29 01      AND #$01
E98F   F0 F6      BEQ $E987
E991   58         CLI
E992   20 AA D3   JSR $D3AA
E995   78         SEI
E996   4C 0F E9   JMP $E90F
E999   4C 4E EA   JMP $EA4E

; DATA OUT low

E99C   AD 00 18   LDA $1800
E99F   29 FD      AND #$FD
E9A1   8D 00 18   STA $1800
E9A4   60         RTS

; DATA OUT high

E9A5   AD 00 18   LDA $1800
E9A8   09 02      ORA #$02
E9AA   8D 00 18   STA $1800
E9AD   60         RTS

; CLOCK OUT high

E9AE   AD 00 18   LDA $1800
E9B1   09 08      ORA #$08
E9B3   8D 00 18   STA $1800
E9B6   60         RTS

; CLOCK OUT low

E9B7   AD 00 18   LDA $1800
E9BA   29 F7      AND #$F7
E9BC   8D 00 18   STA $1800
E9BF   60         RTS

; read IEEE port

E9C0   AD 00 18   LDA $1800
E9C3   CD 00 18   CMP $1800
E9C6   D0 F8      BNE $E9C0
E9C8   60         RTS

; get data byte from bus

E9C9   A9 08      LDA #$08
E9CB   85 98      STA $98
E9CD   20 59 EA   JSR $EA59
E9D0   20 C0 E9   JSR $E9C0
E9D3   29 04      AND #$04
E9D5   D0 F6      BNE $E9CD
E9D7   20 9C E9   JSR $E99C
E9DA   A9 01      LDA #$01
E9DC   4C 20 FF   JMP $FF20
E9DF   20 59 EA   JSR $EA59
E9E2   AD 0D 18   LDA $180D
E9E5   29 40      AND #$40
E9E7   D0 09      BNE $E9F2
E9E9   20 C0 E9   JSR $E9C0
E9EC   29 04      AND #$04
E9EE   F0 EF      BEQ $E9DF
E9F0   D0 19      BNE $EA0B

; accept byte with EOI

E9F2   20 A5 E9   JSR $E9A5
E9F5   A2 0A      LDX #$0A
E9F7   CA         DEX
E9F8   D0 FD      BNE $E9F7
E9FA   20 9C E9   JSR $E99C
E9FD   20 59 EA   JSR $EA59
EA00   20 C0 E9   JSR $E9C0
EA03   29 04      AND #$04
EA05   F0 F6      BEQ $E9FD
EA07   A9 00      LDA #$00
EA09   85 F8      STA $F8
EA0B   AD 00 18   LDA $1800
EA0E   49 01      EOR #$01
EA10   4A         LSR
EA11   29 02      AND #$02
EA13   D0 F6      BNE $EA0B
EA15   EA         NOP
EA16   EA         NOP
EA17   EA         NOP
EA18   66 85      ROR $85
EA1A   20 59 EA   JSR $EA59
EA1D   20 C0 E9   JSR $E9C0
EA20   29 04      AND #$04
EA22   F0 F6      BEQ $EA1A
EA24   C6 98      DEC $98
EA26   D0 E3      BNE $EA0B
EA28   20 A5 E9   JSR $E9A5
EA2B   A5 85      LDA $85
EA2D   60         RTS

; accept data from serial bus

EA2E   78         SEI
EA2F   20 07 D1   JSR $D107
EA32   B0 05      BCS $EA39
EA34   B5 F2      LDA $F2,X
EA36   6A         ROR
EA37   B0 0B      BCS $EA44
EA39   A5 84      LDA $84
EA3B   29 F0      AND #$F0
EA3D   C9 F0      CMP #$F0
EA3F   F0 03      BEQ $EA44
EA41   4C 4E EA   JMP $EA4E
EA44   20 C9 E9   JSR $E9C9
EA47   58         CLI
EA48   20 B7 CF   JSR $CFB7
EA4B   4C 2E EA   JMP $EA2E
EA4E   A9 00      LDA #$00
EA50   8D 00 18   STA $1800
EA53   4C E7 EB   JMP $EBE7
EA56   4C 5B E8   JMP $E85B

; test for ATN

EA59   A5 7D      LDA $7D
EA5B   F0 06      BEQ $EA63
EA5D   AD 00 18   LDA $1800
EA60   10 09      BPL $EA6B
EA62   60         RTS
EA63   AD 00 18   LDA $1800
EA66   10 FA      BPL $EA62
EA68   4C 5B E8   JMP $E85B
EA6B   4C D7 E8   JMP $E8D7

; flash LED for hardware defects, self-test

EA6E   A2 00      LDX #$00
EA70   .BY $2C
EA71   A6 6F      LDX $6F
EA73   9A         TXS
EA74   BA         TSX
EA75   A9 08      LDA #$08
EA77   0D 00 1C   ORA $1C00
EA7A   4C EA FE   JMP $FEEA
EA7D   98         TYA
EA7E   18         CLC
EA7F   69 01      ADC #$01
EA81   D0 FC      BNE $EA7F
EA83   88         DEY
EA84   D0 F8      BNE $EA7E
EA86   AD 00 1C   LDA $1C00
EA89   29 F7      AND #$F7
EA8B   8D 00 1C   STA $1C00
EA8E   98         TYA
EA8F   18         CLC
EA90   69 01      ADC #$01
EA92   D0 FC      BNE $EA90
EA94   88         DEY
EA95   D0 F8      BNE $EA8F
EA97   CA         DEX
EA98   10 DB      BPL $EA75
EA9A   E0 FC      CPX #$FC
EA9C   D0 F0      BNE $EA8E
EA9E   F0 D4      BEQ $EA74

; Reset routine

EAA0   78         SEI
EAA1   D8         CLD
EAA2   A2 FF      LDX #$FF
EAA4   4C 10 FF   JMP $FF10
EAA7   E8         INX
EAA8   A0 00      LDY #$00
EAAA   A2 00      LDX #$00
EAAC   8A         TXA
EAAD   95 00      STA $00,X
EAAF   E8         INX
EAB0   D0 FA      BNE $EAAC
EAB2   8A         TXA
EAB3   D5 00      CMP $00,X
EAB5   D0 B7      BNE $EA6E
EAB7   F6 00      INC $00,X
EAB9   C8         INY
EABA   D0 FB      BNE $EAB7
EABC   D5 00      CMP $00,X
EABE   D0 AE      BNE $EA6E
EAC0   94 00      STY $00,X
EAC2   B5 00      LDA $00,X
EAC4   D0 A8      BNE $EA6E
EAC6   E8         INX
EAC7   D0 E9      BNE $EAB2
EAC9   E6 6F      INC $6F
EACB   86 76      STX $76
EACD   A9 00      LDA #$00
EACF   85 75      STA $75
EAD1   A8         TAY
EAD2   A2 20      LDX #$20
EAD4   18         CLC
EAD5   C6 76      DEC $76
EAD7   71 75      ADC ($75),Y
EAD9   C8         INY
EADA   D0 FB      BNE $EAD7
EADC   CA         DEX
EADD   D0 F6      BNE $EAD5
EADF   69 00      ADC #$00
EAE1   AA         TAX
EAE2   C5 76      CMP $76
EAE4   D0 39      BNE $EB1F
EAE6   E0 C0      CPX #$C0
EAE8   D0 DF      BNE $EAC9
EAEA   A9 01      LDA #$01
EAEC   85 76      STA $76
EAEE   E6 6F      INC $6F
EAF0   A2 07      LDX #$07
EAF2   98         TYA
EAF3   18         CLC
EAF4   65 76      ADC $76
EAF6   91 75      STA ($75),Y
EAF8   C8         INY
EAF9   D0 F7      BNE $EAF2
EAFB   E6 76      INC $76
EAFD   CA         DEX
EAFE   D0 F2      BNE $EAF2
EB00   A2 07      LDX #$07
EB02   C6 76      DEC $76
EB04   88         DEY
EB05   98         TYA
EB06   18         CLC
EB07   65 76      ADC $76
EB09   D1 75      CMP ($75),Y
EB0B   D0 12      BNE $EB1F
EB0D   49 FF      EOR #$FF
EB0F   91 75      STA ($75),Y
EB11   51 75      EOR ($75),Y
EB13   91 75      STA ($75),Y
EB15   D0 08      BNE $EB1F
EB17   98         TYA
EB18   D0 EA      BNE $EB04
EB1A   CA         DEX
EB1B   D0 E5      BNE $EB02
EB1D   F0 03      BEQ $EB22
EB1F   4C 71 EA   JMP $EA71
EB22   A2 45      LDX #$45
EB24   9A         TXS
EB25   AD 00 1C   LDA $1C00
EB28   29 F7      AND #$F7
EB2A   8D 00 1C   STA $1C00
EB2D   A9 01      LDA #$01
EB2F   8D 0C 18   STA $180C
EB32   A9 82      LDA #$82
EB34   8D 0D 18   STA $180D
EB37   8D 0E 18   STA $180E
EB3A   AD 00 18   LDA $1800
EB3D   29 60      AND #$60
EB3F   0A         ASL
EB40   2A         ROL
EB41   2A         ROL
EB42   2A         ROL
EB43   09 48      ORA #$48
EB45   85 78      STA $78
EB47   49 60      EOR #$60
EB49   85 77      STA $77
EB4B   A2 00      LDX #$00
EB4D   A0 00      LDY #$00
EB4F   A9 00      LDA #$00
EB51   95 99      STA $99,X
EB53   E8         INX
EB54   B9 E0 FE   LDA $FEE0,Y
EB57   95 99      STA $99,X
EB59   E8         INX
EB5A   C8         INY
EB5B   C0 05      CPY #$05
EB5D   D0 F0      BNE $EB4F
EB5F   A9 00      LDA #$00
EB61   95 99      STA $99,X
EB63   E8         INX
EB64   A9 02      LDA #$02
EB66   95 99      STA $99,X
EB68   E8         INX
EB69   A9 D5      LDA #$D5
EB6B   95 99      STA $99,X
EB6D   E8         INX
EB6E   A9 02      LDA #$02
EB70   95 99      STA $99,X
EB72   A9 FF      LDA #$FF
EB74   A2 12      LDX #$12
EB76   9D 2B 02   STA $022B,X
EB79   CA         DEX
EB7A   10 FA      BPL $EB76
EB7C   A2 05      LDX #$05
EB7E   95 A7      STA $A7,X
EB80   95 AE      STA $AE,X
EB82   95 CD      STA $CD,X
EB84   CA         DEX
EB85   10 F7      BPL $EB7E
EB87   A9 05      LDA #$05
EB89   85 AB      STA $AB
EB8B   A9 06      LDA #$06
EB8D   85 AC      STA $AC
EB8F   A9 FF      LDA #$FF
EB91   85 AD      STA $AD
EB93   85 B4      STA $B4
EB95   A9 05      LDA #$05
EB97   8D 3B 02   STA $023B
EB9A   A9 84      LDA #$84
EB9C   8D 3A 02   STA $023A
EB9F   A9 0F      LDA #$0F
EBA1   8D 56 02   STA $0256
EBA4   A9 01      LDA #$01
EBA6   85 F6      STA $F6
EBA8   A9 88      LDA #$88
EBAA   85 F7      STA $F7
EBAC   A9 E0      LDA #$E0
EBAE   8D 4F 02   STA $024F
EBB1   A9 FF      LDA #$FF
EBB3   8D 50 02   STA $0250
EBB6   A9 01      LDA #$01
EBB8   85 1C      STA $1C
EBBA   85 1D      STA $1D
EBBC   20 63 CB   JSR $CB63
EBBF   20 FA CE   JSR $CEFA
EBC2   20 59 F2   JSR $F259
EBC5   A9 22      LDA #$22
EBC7   85 65      STA $65
EBC9   A9 EB      LDA #$EB
EBCB   85 66      STA $66
EBCD   A9 0A      LDA #$0A
EBCF   85 69      STA $69
EBD1   A9 05      LDA #$05
EBD3   85 6A      STA $6A
EBD5   A9 73      LDA #$73
EBD7   20 C1 E6   JSR $E6C1
EBDA   A9 00      LDA #$00
EBDC   8D 00 18   STA $1800
EBDF   A9 1A      LDA #$1A
EBE1   8D 02 18   STA $1802
EBE4   20 80 E7   JSR $E780
EBE7   58         CLI
EBE8   AD 00 18   LDA $1800
EBEB   29 E5      AND #$E5
EBED   8D 00 18   STA $1800
EBF0   AD 55 02   LDA $0255
EBF3   F0 0A      BEQ $EBFF
EBF5   A9 00      LDA #$00
EBF7   8D 55 02   STA $0255
EBFA   85 67      STA $67
EBFC   20 46 C1   JSR $C146

; wait loop

EBFF   58         CLI
EC00   A5 7C      LDA $7C
EC02   F0 03      BEQ $EC07
EC04   4C 5B E8   JMP $E85B
EC07   58         CLI
EC08   A9 0E      LDA #$0E
EC0A   85 72      STA $72
EC0C   A9 00      LDA #$00
EC0E   85 6F      STA $6F
EC10   85 70      STA $70
EC12   A6 72      LDX $72
EC14   BD 2B 02   LDA $022B,X
EC17   C9 FF      CMP #$FF
EC19   F0 10      BEQ $EC2B
EC1B   29 3F      AND #$3F
EC1D   85 82      STA $82
EC1F   20 93 DF   JSR $DF93
EC22   AA         TAX
EC23   BD 5B 02   LDA $025B,X
EC26   29 01      AND #$01
EC28   AA         TAX
EC29   F6 6F      INC $6F,X
EC2B   C6 72      DEC $72
EC2D   10 E3      BPL $EC12
EC2F   A0 04      LDY #$04
EC31   B9 00 00   LDA $0000,Y
EC34   10 05      BPL $EC3B
EC36   29 01      AND #$01
EC38   AA         TAX
EC39   F6 6F      INC $6F,X
EC3B   88         DEY
EC3C   10 F3      BPL $EC31
EC3E   78         SEI
EC3F   AD 00 1C   LDA $1C00
EC42   29 F7      AND #$F7
EC44   48         PHA
EC45   A5 7F      LDA $7F
EC47   85 86      STA $86
EC49   A9 00      LDA #$00
EC4B   85 7F      STA $7F
EC4D   A5 6F      LDA $6F
EC4F   F0 0B      BEQ $EC5C
EC51   A5 1C      LDA $1C
EC53   F0 03      BEQ $EC58
EC55   20 13 D3   JSR $D313
EC58   68         PLA
EC59   09 08      ORA #$08
EC5B   48         PHA
EC5C   E6 7F      INC $7F
EC5E   A5 70      LDA $70
EC60   F0 0B      BEQ $EC6D
EC62   A5 1D      LDA $1D
EC64   F0 03      BEQ $EC69
EC66   20 13 D3   JSR $D313
EC69   68         PLA
EC6A   09 00      ORA #$00
EC6C   48         PHA
EC6D   A5 86      LDA $86
EC6F   85 7F      STA $7F
EC71   68         PLA
EC72   AE 6C 02   LDX $026C
EC75   F0 21      BEQ $EC98
EC77   AD 00 1C   LDA $1C00
EC7A   E0 80      CPX #$80
EC7C   D0 03      BNE $EC81
EC7E   4C 8B EC   JMP $EC8B
EC81   AE 05 18   LDX $1805
EC84   30 12      BMI $EC98
EC86   A2 A0      LDX #$A0
EC88   8E 05 18   STX $1805
EC8B   CE 6C 02   DEC $026C
EC8E   D0 08      BNE $EC98
EC90   4D 6D 02   EOR $026D
EC93   A2 10      LDX #$10
EC95   8E 6C 02   STX $026C
EC98   8D 00 1C   STA $1C00
EC9B   4C FF EB   JMP $EBFF

; load dir

EC9E   A9 00      LDA #$00
ECA0   85 83      STA $83
ECA2   A9 01      LDA #$01
ECA4   20 E2 D1   JSR $D1E2
ECA7   A9 00      LDA #$00
ECA9   20 C8 D4   JSR $D4C8
ECAC   A6 82      LDX $82
ECAE   A9 00      LDA #$00
ECB0   9D 44 02   STA $0244,X
ECB3   20 93 DF   JSR $DF93
ECB6   AA         TAX
ECB7   A5 7F      LDA $7F
ECB9   9D 5B 02   STA $025B,X
ECBC   A9 01      LDA #$01
ECBE   20 F1 CF   JSR $CFF1
ECC1   A9 04      LDA #$04
ECC3   20 F1 CF   JSR $CFF1
ECC6   A9 01      LDA #$01
ECC8   20 F1 CF   JSR $CFF1
ECCB   20 F1 CF   JSR $CFF1
ECCE   AD 72 02   LDA $0272
ECD1   20 F1 CF   JSR $CFF1
ECD4   A9 00      LDA #$00
ECD6   20 F1 CF   JSR $CFF1
ECD9   20 59 ED   JSR $ED59
ECDC   20 93 DF   JSR $DF93
ECDF   0A         ASL
ECE0   AA         TAX
ECE1   D6 99      DEC $99,X
ECE3   D6 99      DEC $99,X
ECE5   A9 00      LDA #$00
ECE7   20 F1 CF   JSR $CFF1
ECEA   A9 01      LDA #$01
ECEC   20 F1 CF   JSR $CFF1
ECEF   20 F1 CF   JSR $CFF1
ECF2   20 CE C6   JSR $C6CE
ECF5   90 2C      BCC $ED23
ECF7   AD 72 02   LDA $0272
ECFA   20 F1 CF   JSR $CFF1
ECFD   AD 73 02   LDA $0273
ED00   20 F1 CF   JSR $CFF1
ED03   20 59 ED   JSR $ED59
ED06   A9 00      LDA #$00
ED08   20 F1 CF   JSR $CFF1
ED0B   D0 DD      BNE $ECEA
ED0D   20 93 DF   JSR $DF93
ED10   0A         ASL
ED11   AA         TAX
ED12   A9 00      LDA #$00
ED14   95 99      STA $99,X
ED16   A9 88      LDA #$88
ED18   A4 82      LDY $82
ED1A   8D 54 02   STA $0254
ED1D   99 F2 00   STA $00F2,Y
ED20   A5 85      LDA $85
ED22   60         RTS

;

ED23   AD 72 02   LDA $0272
ED26   20 F1 CF   JSR $CFF1
ED29   AD 73 02   LDA $0273
ED2C   20 F1 CF   JSR $CFF1
ED2F   20 59 ED   JSR $ED59
ED32   20 93 DF   JSR $DF93
ED35   0A         ASL
ED36   AA         TAX
ED37   D6 99      DEC $99,X
ED39   D6 99      DEC $99,X
ED3B   A9 00      LDA #$00
ED3D   20 F1 CF   JSR $CFF1
ED40   20 F1 CF   JSR $CFF1
ED43   20 F1 CF   JSR $CFF1
ED46   20 93 DF   JSR $DF93
ED49   0A         ASL
ED4A   A8         TAY
ED4B   B9 99 00   LDA $0099,Y
ED4E   A6 82      LDX $82
ED50   9D 44 02   STA $0244,X
ED53   DE 44 02   DEC $0244,X
ED56   4C 0D ED   JMP $ED0D

; transmit dir line

ED59   A0 00      LDY #$00
ED5B   B9 B1 02   LDA $02B1,Y
ED5E   20 F1 CF   JSR $CFF1
ED61   C8         INY
ED62   C0 1B      CPY #$1B
ED64   D0 F5      BNE $ED5B
ED66   60         RTS

; get byte from buffer

ED67   20 37 D1   JSR $D137
ED6A   F0 01      BEQ $ED6D
ED6C   60         RTS
ED6D   85 85      STA $85
ED6F   A4 82      LDY $82
ED71   B9 44 02   LDA $0244,Y
ED74   F0 08      BEQ $ED7E
ED76   A9 80      LDA #$80
ED78   99 F2 00   STA $00F2,Y
ED7B   A5 85      LDA $85
ED7D   60         RTS
ED7E   48         PHA
ED7F   20 EA EC   JSR $ECEA
ED82   68         PLA
ED83   60         RTS

; V - Validate command

ED84   20 D1 C1   JSR $C1D1
ED87   20 42 D0   JSR $D042
ED8A   A9 40      LDA #$40
ED8C   8D F9 02   STA $02F9
ED8F   20 B7 EE   JSR $EEB7
ED92   A9 00      LDA #$00
ED94   8D 92 02   STA $0292
ED97   20 AC C5   JSR $C5AC
ED9A   D0 3D      BNE $EDD9
ED9C   A9 00      LDA #$00
ED9E   85 81      STA $81
EDA0   AD 85 FE   LDA $FE85
EDA3   85 80      STA $80
EDA5   20 E5 ED   JSR $EDE5
EDA8   A9 00      LDA #$00
EDAA   8D F9 02   STA $02F9
EDAD   20 FF EE   JSR $EEFF
EDB0   4C 94 C1   JMP $C194

;

EDB3   C8         INY
EDB4   B1 94      LDA ($94),Y
EDB6   48         PHA
EDB7   C8         INY
EDB8   B1 94      LDA ($94),Y
EDBA   48         PHA
EDBB   A0 13      LDY #$13
EDBD   B1 94      LDA ($94),Y
EDBF   F0 0A      BEQ $EDCB
EDC1   85 80      STA $80
EDC3   C8         INY
EDC4   B1 94      LDA ($94),Y
EDC6   85 81      STA $81
EDC8   20 E5 ED   JSR $EDE5
EDCB   68         PLA
EDCC   85 81      STA $81
EDCE   68         PLA
EDCF   85 80      STA $80
EDD1   20 E5 ED   JSR $EDE5
EDD4   20 04 C6   JSR $C604
EDD7   F0 C3      BEQ $ED9C
EDD9   A0 00      LDY #$00
EDDB   B1 94      LDA ($94),Y
EDDD   30 D4      BMI $EDB3
EDDF   20 B6 C8   JSR $C8B6
EDE2   4C D4 ED   JMP $EDD4

; allocate file blocks in BAM

EDE5   20 5F D5   JSR $D55F
EDE8   20 90 EF   JSR $EF90
EDEB   20 75 D4   JSR $D475
EDEE   A9 00      LDA #$00
EDF0   20 C8 D4   JSR $D4C8
EDF3   20 37 D1   JSR $D137
EDF6   85 80      STA $80
EDF8   20 37 D1   JSR $D137
EDFB   85 81      STA $81
EDFD   A5 80      LDA $80
EDFF   D0 03      BNE $EE04
EE01   4C 27 D2   JMP $D227
EE04   20 90 EF   JSR $EF90
EE07   20 4D D4   JSR $D44D
EE0A   4C EE ED   JMP $EDEE

; N - New (Format) command

EE0D   20 12 C3   JSR $C312
EE10   A5 E2      LDA $E2
EE12   10 05      BPL $EE19
EE14   A9 33      LDA #$33
EE16   4C C8 C1   JMP $C1C8
EE19   29 01      AND #$01
EE1B   85 7F      STA $7F
EE1D   20 00 C1   JSR $C100
EE20   A5 7F      LDA $7F
EE22   0A         ASL
EE23   AA         TAX
EE24   AC 7B 02   LDY $027B
EE27   CC 74 02   CPY $0274
EE2A   F0 1A      BEQ $EE46
EE2C   B9 00 02   LDA $0200,Y
EE2F   95 12      STA $12,X
EE31   B9 01 02   LDA $0201,Y
EE34   95 13      STA $13,X
EE36   20 07 D3   JSR $D307
EE39   A9 01      LDA #$01
EE3B   85 80      STA $80
EE3D   20 C6 C8   JSR $C8C6
EE40   20 05 F0   JSR $F005
EE43   4C 56 EE   JMP $EE56
EE46   20 42 D0   JSR $D042
EE49   A6 7F      LDX $7F
EE4B   BD 01 01   LDA $0101,X
EE4E   CD D5 FE   CMP $FED5
EE51   F0 03      BEQ $EE56
EE53   4C 72 D5   JMP $D572
EE56   20 B7 EE   JSR $EEB7
EE59   A5 F9      LDA $F9
EE5B   A8         TAY
EE5C   0A         ASL
EE5D   AA         TAX
EE5E   AD 88 FE   LDA $FE88
EE61   95 99      STA $99,X
EE63   AE 7A 02   LDX $027A
EE66   A9 1B      LDA #$1B
EE68   20 6E C6   JSR $C66E
EE6B   A0 12      LDY #$12
EE6D   A6 7F      LDX $7F
EE6F   AD D5 FE   LDA $FED5
EE72   9D 01 01   STA $0101,X
EE75   8A         TXA
EE76   0A         ASL
EE77   AA         TAX
EE78   B5 12      LDA $12,X
EE7A   91 94      STA ($94),Y
EE7C   C8         INY
EE7D   B5 13      LDA $13,X
EE7F   91 94      STA ($94),Y
EE81   C8         INY
EE82   C8         INY
EE83   A9 32      LDA #$32
EE85   91 94      STA ($94),Y
EE87   C8         INY
EE88   AD D5 FE   LDA $FED5
EE8B   91 94      STA ($94),Y
EE8D   A0 02      LDY #$02
EE8F   91 6D      STA ($6D),Y
EE91   AD 85 FE   LDA $FE85
EE94   85 80      STA $80
EE96   20 93 EF   JSR $EF93
EE99   A9 01      LDA #$01
EE9B   85 81      STA $81
EE9D   20 93 EF   JSR $EF93
EEA0   20 FF EE   JSR $EEFF
EEA3   20 05 F0   JSR $F005
EEA6   A0 01      LDY #$01
EEA8   A9 FF      LDA #$FF
EEAA   91 6D      STA ($6D),Y
EEAC   20 64 D4   JSR $D464
EEAF   C6 81      DEC $81
EEB1   20 60 D4   JSR $D460
EEB4   4C 94 C1   JMP $C194

; create BAM

EEB7   20 D1 F0   JSR $F0D1
EEBA   A0 00      LDY #$00
EEBC   A9 12      LDA #$12
EEBE   91 6D      STA ($6D),Y
EEC0   C8         INY
EEC1   98         TYA
EEC2   91 6D      STA ($6D),Y
EEC4   C8         INY
EEC5   C8         INY
EEC6   C8         INY
EEC7   A9 00      LDA #$00
EEC9   85 6F      STA $6F
EECB   85 70      STA $70
EECD   85 71      STA $71
EECF   98         TYA
EED0   4A         LSR
EED1   4A         LSR
EED2   20 4B F2   JSR $F24B
EED5   91 6D      STA ($6D),Y
EED7   C8         INY
EED8   AA         TAX
EED9   38         SEC
EEDA   26 6F      ROL $6F
EEDC   26 70      ROL $70
EEDE   26 71      ROL $71
EEE0   CA         DEX
EEE1   D0 F6      BNE $EED9
EEE3   B5 6F      LDA $6F,X
EEE5   91 6D      STA ($6D),Y
EEE7   C8         INY
EEE8   E8         INX
EEE9   E0 03      CPX #$03
EEEB   90 F6      BCC $EEE3
EEED   C0 90      CPY #$90
EEEF   90 D6      BCC $EEC7
EEF1   4C 75 D0   JMP $D075

; write BAM if needed

EEF4   20 93 DF   JSR $DF93
EEF7   AA         TAX
EEF8   BD 5B 02   LDA $025B,X
EEFB   29 01      AND #$01
EEFD   85 7F      STA $7F
EEFF   A4 7F      LDY $7F
EF01   B9 51 02   LDA $0251,Y
EF04   D0 01      BNE $EF07
EF06   60         RTS
EF07   A9 00      LDA #$00
EF09   99 51 02   STA $0251,Y
EF0C   20 3A EF   JSR $EF3A
EF0F   A5 7F      LDA $7F
EF11   0A         ASL
EF12   48         PHA
EF13   20 A5 F0   JSR $F0A5
EF16   68         PLA
EF17   18         CLC
EF18   69 01      ADC #$01
EF1A   20 A5 F0   JSR $F0A5
EF1D   A5 80      LDA $80
EF1F   48         PHA
EF20   A9 01      LDA #$01
EF22   85 80      STA $80
EF24   0A         ASL
EF25   0A         ASL
EF26   85 6D      STA $6D
EF28   20 20 F2   JSR $F220
EF2B   E6 80      INC $80
EF2D   A5 80      LDA $80
EF2F   CD D7 FE   CMP $FED7
EF32   90 F0      BCC $EF24
EF34   68         PLA
EF35   85 80      STA $80
EF37   4C 8A D5   JMP $D58A

; set buffer pointer for BAM

EF3A   20 0F F1   JSR $F10F
EF3D   AA         TAX
EF3E   20 DF F0   JSR $F0DF
EF41   A6 F9      LDX $F9
EF43   BD E0 FE   LDA $FEE0,X
EF46   85 6E      STA $6E
EF48   A9 00      LDA #$00
EF4A   85 6D      STA $6D
EF4C   60         RTS

; get number of free blocks for dir

EF4D   A6 7F      LDX $7F
EF4F   BD FA 02   LDA $02FA,X
EF52   8D 72 02   STA $0272
EF55   BD FC 02   LDA $02FC,X
EF58   8D 73 02   STA $0273
EF5B   60         RTS

; mark block as free

EF5C   20 F1 EF   JSR $EFF1
EF5F   20 CF EF   JSR $EFCF
EF62   38         SEC
EF63   D0 22      BNE $EF87
EF65   B1 6D      LDA ($6D),Y
EF67   1D E9 EF   ORA $EFE9,X
EF6A   91 6D      STA ($6D),Y
EF6C   20 88 EF   JSR $EF88
EF6F   A4 6F      LDY $6F
EF71   18         CLC
EF72   B1 6D      LDA ($6D),Y
EF74   69 01      ADC #$01
EF76   91 6D      STA ($6D),Y
EF78   A5 80      LDA $80
EF7A   CD 85 FE   CMP $FE85
EF7D   F0 3B      BEQ $EFBA
EF7F   FE FA 02   INC $02FA,X
EF82   D0 03      BNE $EF87
EF84   FE FC 02   INC $02FC,X
EF87   60         RTS

; set flag for BAM changed

EF88   A6 7F      LDX $7F
EF8A   A9 01      LDA #$01
EF8C   9D 51 02   STA $0251,X
EF8F   60         RTS

; mark block as allocated

EF90   20 F1 EF   JSR $EFF1
EF93   20 CF EF   JSR $EFCF
EF96   F0 36      BEQ $EFCE
EF98   B1 6D      LDA ($6D),Y
EF9A   5D E9 EF   EOR $EFE9,X
EF9D   91 6D      STA ($6D),Y
EF9F   20 88 EF   JSR $EF88
EFA2   A4 6F      LDY $6F
EFA4   B1 6D      LDA ($6D),Y
EFA6   38         SEC
EFA7   E9 01      SBC #$01
EFA9   91 6D      STA ($6D),Y
EFAB   A5 80      LDA $80
EFAD   CD 85 FE   CMP $FE85
EFB0   F0 0B      BEQ $EFBD
EFB2   BD FA 02   LDA $02FA,X
EFB5   D0 03      BNE $EFBA
EFB7   DE FC 02   DEC $02FC,X
EFBA   DE FA 02   DEC $02FA,X
EFBD   BD FC 02   LDA $02FC,X
EFC0   D0 0C      BNE $EFCE
EFC2   BD FA 02   LDA $02FA,X
EFC5   C9 03      CMP #$03
EFC7   B0 05      BCS $EFCE
EFC9   A9 72      LDA #$72
EFCB   20 C7 E6   JSR $E6C7
EFCE   60         RTS

; erase bit for sector in BAM entry

EFCF   20 11 F0   JSR $F011
EFD2   98         TYA
EFD3   85 6F      STA $6F
EFD5   A5 81      LDA $81
EFD7   4A         LSR
EFD8   4A         LSR
EFD9   4A         LSR
EFDA   38         SEC
EFDB   65 6F      ADC $6F
EFDD   A8         TAY
EFDE   A5 81      LDA $81
EFE0   29 07      AND #$07
EFE2   AA         TAX
EFE3   B1 6D      LDA ($6D),Y
EFE5   3D E9 EF   AND $EFE9,X
EFE8   60         RTS

; powers of 2

EFE9   .BY $01,$02,$04,$08,$10,$20,$40,$80

; write BAM after change

EFF1   A9 FF      LDA #$FF
EFF3   2C F9 02   BIT $02F9
EFF6   F0 0C      BEQ $F004
EFF8   10 0A      BPL $F004
EFFA   70 08      BVS $F004
EFFC   A9 00      LDA #$00
EFFE   8D F9 02   STA $02F9
F001   4C 8A D5   JMP $D58A
F004   60         RTS

; erase BAM buffer

F005   20 3A EF   JSR $EF3A
F008   A0 00      LDY #$00
F00A   98         TYA
F00B   91 6D      STA ($6D),Y
F00D   C8         INY
F00E   D0 FB      BNE $F00B
F010   60         RTS

;

F011   A5 6F      LDA $6F
F013   48         PHA
F014   A5 70      LDA $70
F016   48         PHA
F017   A6 7F      LDX $7F
F019   B5 FF      LDA $FF,X
F01B   F0 05      BEQ $F022
F01D   A9 74      LDA #$74
F01F   20 48 E6   JSR $E648
F022   20 0F F1   JSR $F10F
F025   85 6F      STA $6F
F027   8A         TXA
F028   0A         ASL
F029   85 70      STA $70
F02B   AA         TAX
F02C   A5 80      LDA $80
F02E   DD 9D 02   CMP $029D,X
F031   F0 0B      BEQ $F03E
F033   E8         INX
F034   86 70      STX $70
F036   DD 9D 02   CMP $029D,X
F039   F0 03      BEQ $F03E
F03B   20 5B F0   JSR $F05B
F03E   A5 70      LDA $70
F040   A6 7F      LDX $7F
F042   9D 9B 02   STA $029B,X
F045   0A         ASL
F046   0A         ASL
F047   18         CLC
F048   69 A1      ADC #$A1
F04A   85 6D      STA $6D
F04C   A9 02      LDA #$02
F04E   69 00      ADC #$00
F050   85 6E      STA $6E
F052   A0 00      LDY #$00
F054   68         PLA
F055   85 70      STA $70
F057   68         PLA
F058   85 6F      STA $6F
F05A   60         RTS

;

F05B   A6 6F      LDX $6F
F05D   20 DF F0   JSR $F0DF
F060   A5 7F      LDA $7F
F062   AA         TAX
F063   0A         ASL
F064   1D 9B 02   ORA $029B,X
F067   49 01      EOR #$01
F069   29 03      AND #$03
F06B   85 70      STA $70
F06D   20 A5 F0   JSR $F0A5
F070   A5 F9      LDA $F9
F072   0A         ASL
F073   AA         TAX
F074   A5 80      LDA $80
F076   0A         ASL
F077   0A         ASL
F078   95 99      STA $99,X
F07A   A5 70      LDA $70
F07C   0A         ASL
F07D   0A         ASL
F07E   A8         TAY
F07F   A1 99      LDA ($99,X)
F081   99 A1 02   STA $02A1,Y
F084   A9 00      LDA #$00
F086   81 99      STA ($99,X)
F088   F6 99      INC $99,X
F08A   C8         INY
F08B   98         TYA
F08C   29 03      AND #$03
F08E   D0 EF      BNE $F07F
F090   A6 70      LDX $70
F092   A5 80      LDA $80
F094   9D 9D 02   STA $029D,X
F097   AD F9 02   LDA $02F9
F09A   D0 03      BNE $F09F
F09C   4C 8A D5   JMP $D58A
F09F   09 80      ORA #$80
F0A1   8D F9 02   STA $02F9
F0A4   60         RTS
F0A5   A8         TAY
F0A6   B9 9D 02   LDA $029D,Y
F0A9   F0 25      BEQ $F0D0
F0AB   48         PHA
F0AC   A9 00      LDA #$00
F0AE   99 9D 02   STA $029D,Y
F0B1   A5 F9      LDA $F9
F0B3   0A         ASL
F0B4   AA         TAX
F0B5   68         PLA
F0B6   0A         ASL
F0B7   0A         ASL
F0B8   95 99      STA $99,X
F0BA   98         TYA
F0BB   0A         ASL
F0BC   0A         ASL
F0BD   A8         TAY
F0BE   B9 A1 02   LDA $02A1,Y
F0C1   81 99      STA ($99,X)
F0C3   A9 00      LDA #$00
F0C5   99 A1 02   STA $02A1,Y
F0C8   F6 99      INC $99,X
F0CA   C8         INY
F0CB   98         TYA
F0CC   29 03      AND #$03
F0CE   D0 EE      BNE $F0BE
F0D0   60         RTS
F0D1   A5 7F      LDA $7F
F0D3   0A         ASL
F0D4   AA         TAX
F0D5   A9 00      LDA #$00
F0D7   9D 9D 02   STA $029D,X
F0DA   E8         INX
F0DB   9D 9D 02   STA $029D,X
F0DE   60         RTS

;

F0DF   B5 A7      LDA $A7,X
F0E1   C9 FF      CMP #$FF
F0E3   D0 25      BNE $F10A
F0E5   8A         TXA
F0E6   48         PHA
F0E7   20 8E D2   JSR $D28E
F0EA   AA         TAX
F0EB   10 05      BPL $F0F2
F0ED   A9 70      LDA #$70
F0EF   20 C8 C1   JSR $C1C8
F0F2   86 F9      STX $F9
F0F4   68         PLA
F0F5   A8         TAY
F0F6   8A         TXA
F0F7   09 80      ORA #$80
F0F9   99 A7 00   STA $00A7,Y
F0FC   0A         ASL
F0FD   AA         TAX
F0FE   AD 85 FE   LDA $FE85
F101   95 06      STA $06,X
F103   A9 00      LDA #$00
F105   95 07      STA $07,X
F107   4C 86 D5   JMP $D586
F10A   29 0F      AND #$0F
F10C   85 F9      STA $F9
F10E   60         RTS

; get buffer number for BAM

F10F   A9 06      LDA #$06
F111   A6 7F      LDX $7F
F113   D0 03      BNE $F118
F115   18         CLC
F116   69 07      ADC #$07
F118   60         RTS

; buffer number for BAM

F119   20 0F F1   JSR $F10F
F11C   AA         TAX
F11D   60         RTS

; find and allocate free block

F11E   20 3E DE   JSR $DE3E
F121   A9 03      LDA #$03
F123   85 6F      STA $6F
F125   A9 01      LDA #$01
F127   0D F9 02   ORA $02F9
F12A   8D F9 02   STA $02F9
F12D   A5 6F      LDA $6F
F12F   48         PHA
F130   20 11 F0   JSR $F011
F133   68         PLA
F134   85 6F      STA $6F
F136   B1 6D      LDA ($6D),Y
F138   D0 39      BNE $F173
F13A   A5 80      LDA $80
F13C   CD 85 FE   CMP $FE85
F13F   F0 19      BEQ $F15A
F141   90 1C      BCC $F15F
F143   E6 80      INC $80
F145   A5 80      LDA $80
F147   CD D7 FE   CMP $FED7
F14A   D0 E1      BNE $F12D
F14C   AE 85 FE   LDX $FE85
F14F   CA         DEX
F150   86 80      STX $80
F152   A9 00      LDA #$00
F154   85 81      STA $81
F156   C6 6F      DEC $6F
F158   D0 D3      BNE $F12D
F15A   A9 72      LDA #$72
F15C   20 C8 C1   JSR $C1C8
F15F   C6 80      DEC $80
F161   D0 CA      BNE $F12D
F163   AE 85 FE   LDX $FE85
F166   E8         INX
F167   86 80      STX $80
F169   A9 00      LDA #$00
F16B   85 81      STA $81
F16D   C6 6F      DEC $6F
F16F   D0 BC      BNE $F12D
F171   F0 E7      BEQ $F15A
F173   A5 81      LDA $81
F175   18         CLC
F176   65 69      ADC $69
F178   85 81      STA $81
F17A   A5 80      LDA $80
F17C   20 4B F2   JSR $F24B
F17F   8D 4E 02   STA $024E
F182   8D 4D 02   STA $024D
F185   C5 81      CMP $81
F187   B0 0C      BCS $F195
F189   38         SEC
F18A   A5 81      LDA $81
F18C   ED 4E 02   SBC $024E
F18F   85 81      STA $81
F191   F0 02      BEQ $F195
F193   C6 81      DEC $81
F195   20 FA F1   JSR $F1FA
F198   F0 03      BEQ $F19D
F19A   4C 90 EF   JMP $EF90
F19D   A9 00      LDA #$00
F19F   85 81      STA $81
F1A1   20 FA F1   JSR $F1FA
F1A4   D0 F4      BNE $F19A
F1A6   4C F5 F1   JMP $F1F5

; find free sector and allocate

F1A9   A9 01      LDA #$01
F1AB   0D F9 02   ORA $02F9
F1AE   8D F9 02   STA $02F9
F1B1   A5 86      LDA $86
F1B3   48         PHA
F1B4   A9 01      LDA #$01
F1B6   85 86      STA $86
F1B8   AD 85 FE   LDA $FE85
F1BB   38         SEC
F1BC   E5 86      SBC $86
F1BE   85 80      STA $80
F1C0   90 09      BCC $F1CB
F1C2   F0 07      BEQ $F1CB
F1C4   20 11 F0   JSR $F011
F1C7   B1 6D      LDA ($6D),Y
F1C9   D0 1B      BNE $F1E6
F1CB   AD 85 FE   LDA $FE85
F1CE   18         CLC
F1CF   65 86      ADC $86
F1D1   85 80      STA $80
F1D3   E6 86      INC $86
F1D5   CD D7 FE   CMP $FED7
F1D8   90 05      BCC $F1DF
F1DA   A9 67      LDA #$67
F1DC   20 45 E6   JSR $E645
F1DF   20 11 F0   JSR $F011
F1E2   B1 6D      LDA ($6D),Y
F1E4   F0 D2      BEQ $F1B8
F1E6   68         PLA
F1E7   85 86      STA $86
F1E9   A9 00      LDA #$00
F1EB   85 81      STA $81
F1ED   20 FA F1   JSR $F1FA
F1F0   F0 03      BEQ $F1F5
F1F2   4C 90 EF   JMP $EF90
F1F5   A9 71      LDA #$71
F1F7   20 45 E6   JSR $E645

; find free sectors in current track

F1FA   20 11 F0   JSR $F011
F1FD   98         TYA
F1FE   48         PHA
F1FF   20 20 F2   JSR $F220
F202   A5 80      LDA $80
F204   20 4B F2   JSR $F24B
F207   8D 4E 02   STA $024E
F20A   68         PLA
F20B   85 6F      STA $6F
F20D   A5 81      LDA $81
F20F   CD 4E 02   CMP $024E
F212   B0 09      BCS $F21D
F214   20 D5 EF   JSR $EFD5
F217   D0 06      BNE $F21F
F219   E6 81      INC $81
F21B   D0 F0      BNE $F20D
F21D   A9 00      LDA #$00
F21F   60         RTS

; verify number of free blocks in BAM

F220   A5 6F      LDA $6F
F222   48         PHA
F223   A9 00      LDA #$00
F225   85 6F      STA $6F
F227   AC 86 FE   LDY $FE86
F22A   88         DEY
F22B   A2 07      LDX #$07
F22D   B1 6D      LDA ($6D),Y
F22F   3D E9 EF   AND $EFE9,X
F232   F0 02      BEQ $F236
F234   E6 6F      INC $6F
F236   CA         DEX
F237   10 F4      BPL $F22D
F239   88         DEY
F23A   D0 EF      BNE $F22B
F23C   B1 6D      LDA ($6D),Y
F23E   C5 6F      CMP $6F
F240   D0 04      BNE $F246
F242   68         PLA
F243   85 6F      STA $6F
F245   60         RTS
F246   A9 71      LDA #$71
F248   20 45 E6   JSR $E645

; establish number of sectors per track

F24B   AE D6 FE   LDX $FED6
F24E   DD D6 FE   CMP $FED6,X
F251   CA         DEX
F252   B0 FA      BCS $F24E
F254   BD D1 FE   LDA $FED1,X
F257   60         RTS
F258   60         RTS

; initialise disk controller

F259   A9 6F      LDA #$6F
F25B   8D 02 1C   STA $1C02
F25E   29 F0      AND #$F0
F260   8D 00 1C   STA $1C00
F263   AD 0C 1C   LDA $1C0C
F266   29 FE      AND #$FE
F268   09 0E      ORA #$0E
F26A   09 E0      ORA #$E0
F26C   8D 0C 1C   STA $1C0C
F26F   A9 41      LDA #$41
F271   8D 0B 1C   STA $1C0B
F274   A9 00      LDA #$00
F276   8D 06 1C   STA $1C06
F279   A9 3A      LDA #$3A
F27B   8D 07 1C   STA $1C07
F27E   8D 05 1C   STA $1C05
F281   A9 7F      LDA #$7F
F283   8D 0E 1C   STA $1C0E
F286   A9 C0      LDA #$C0
F288   8D 0D 1C   STA $1C0D
F28B   8D 0E 1C   STA $1C0E
F28E   A9 FF      LDA #$FF
F290   85 3E      STA $3E
F292   85 51      STA $51
F294   A9 08      LDA #$08
F296   85 39      STA $39
F298   A9 07      LDA #$07
F29A   85 47      STA $47
F29C   A9 05      LDA #$05
F29E   85 62      STA $62
F2A0   A9 FA      LDA #$FA
F2A2   85 63      STA $63
F2A4   A9 C8      LDA #$C8
F2A6   85 64      STA $64
F2A8   A9 04      LDA #$04
F2AA   85 5E      STA $5E
F2AC   A9 04      LDA #$04
F2AE   85 5F      STA $5F

; IRQ routine for disk controller

F2B0   BA         TSX
F2B1   86 49      STX $49
F2B3   AD 04 1C   LDA $1C04
F2B6   AD 0C 1C   LDA $1C0C
F2B9   09 0E      ORA #$0E
F2BB   8D 0C 1C   STA $1C0C
F2BE   A0 05      LDY #$05
F2C0   B9 00 00   LDA $0000,Y
F2C3   10 2E      BPL $F2F3
F2C5   C9 D0      CMP #$D0
F2C7   D0 04      BNE $F2CD
F2C9   98         TYA
F2CA   4C 70 F3   JMP $F370
F2CD   29 01      AND #$01
F2CF   F0 07      BEQ $F2D8
F2D1   84 3F      STY $3F
F2D3   A9 0F      LDA #$0F
F2D5   4C 69 F9   JMP $F969
F2D8   AA         TAX
F2D9   85 3D      STA $3D
F2DB   C5 3E      CMP $3E
F2DD   F0 0A      BEQ $F2E9
F2DF   20 7E F9   JSR $F97E
F2E2   A5 3D      LDA $3D
F2E4   85 3E      STA $3E
F2E6   4C 9C F9   JMP $F99C
F2E9   A5 20      LDA $20
F2EB   30 03      BMI $F2F0
F2ED   0A         ASL
F2EE   10 09      BPL $F2F9
F2F0   4C 9C F9   JMP $F99C
F2F3   88         DEY
F2F4   10 CA      BPL $F2C0
F2F6   4C 9C F9   JMP $F99C

; head transport

F2F9   A9 20      LDA #$20
F2FB   85 20      STA $20
F2FD   A0 05      LDY #$05
F2FF   84 3F      STY $3F
F301   20 93 F3   JSR $F393
F304   30 1A      BMI $F320
F306   C6 3F      DEC $3F
F308   10 F7      BPL $F301
F30A   A4 41      LDY $41
F30C   20 95 F3   JSR $F395
F30F   A5 42      LDA $42
F311   85 4A      STA $4A
F313   06 4A      ASL $4A
F315   A9 60      LDA #$60
F317   85 20      STA $20
F319   B1 32      LDA ($32),Y
F31B   85 22      STA $22
F31D   4C 9C F9   JMP $F99C
F320   29 01      AND #$01
F322   C5 3D      CMP $3D
F324   D0 E0      BNE $F306
F326   A5 22      LDA $22
F328   F0 12      BEQ $F33C
F32A   38         SEC
F32B   F1 32      SBC ($32),Y
F32D   F0 0D      BEQ $F33C
F32F   49 FF      EOR #$FF
F331   85 42      STA $42
F333   E6 42      INC $42
F335   A5 3F      LDA $3F
F337   85 41      STA $41
F339   4C 06 F3   JMP $F306
F33C   A2 04      LDX #$04
F33E   B1 32      LDA ($32),Y
F340   85 40      STA $40
F342   DD D6 FE   CMP $FED6,X
F345   CA         DEX
F346   B0 FA      BCS $F342
F348   BD D1 FE   LDA $FED1,X
F34B   85 43      STA $43
F34D   8A         TXA
F34E   0A         ASL
F34F   0A         ASL
F350   0A         ASL
F351   0A         ASL
F352   0A         ASL
F353   85 44      STA $44
F355   AD 00 1C   LDA $1C00
F358   29 9F      AND #$9F
F35A   05 44      ORA $44
F35C   8D 00 1C   STA $1C00
F35F   A6 3D      LDX $3D
F361   A5 45      LDA $45
F363   C9 40      CMP #$40
F365   F0 15      BEQ $F37C
F367   C9 60      CMP #$60
F369   F0 03      BEQ $F36E
F36B   4C B1 F3   JMP $F3B1

; execute program in buffer

F36E   A5 3F      LDA $3F
F370   18         CLC
F371   69 03      ADC #$03
F373   85 31      STA $31
F375   A9 00      LDA #$00
F377   85 30      STA $30
F379   6C 30 00   JMP ($0030)

; bump, find track 1 (head at stop)

F37C   A9 60      LDA #$60
F37E   85 20      STA $20
F380   AD 00 1C   LDA $1C00
F383   29 FC      AND #$FC
F385   8D 00 1C   STA $1C00
F388   A9 A4      LDA #$A4
F38A   85 4A      STA $4A
F38C   A9 01      LDA #$01
F38E   85 22      STA $22
F390   4C 69 F9   JMP $F969

; initialise pointer in buffer

F393   A4 3F      LDY $3F
F395   B9 00 00   LDA $0000,Y
F398   48         PHA
F399   10 10      BPL $F3AB
F39B   29 78      AND #$78
F39D   85 45      STA $45
F39F   98         TYA
F3A0   0A         ASL
F3A1   69 06      ADC #$06
F3A3   85 32      STA $32
F3A5   98         TYA
F3A6   18         CLC
F3A7   69 03      ADC #$03
F3A9   85 31      STA $31
F3AB   A0 00      LDY #$00
F3AD   84 30      STY $30
F3AF   68         PLA
F3B0   60         RTS

; read block header, verify ID

F3B1   A2 5A      LDX #$5A
F3B3   86 4B      STX $4B
F3B5   A2 00      LDX #$00
F3B7   A9 52      LDA #$52
F3B9   85 24      STA $24
F3BB   20 56 F5   JSR $F556
F3BE   50 FE      BVC $F3BE
F3C0   B8         CLV
F3C1   AD 01 1C   LDA $1C01
F3C4   C5 24      CMP $24
F3C6   D0 3F      BNE $F407
F3C8   50 FE      BVC $F3C8
F3CA   B8         CLV
F3CB   AD 01 1C   LDA $1C01
F3CE   95 25      STA $25,X
F3D0   E8         INX
F3D1   E0 07      CPX #$07
F3D3   D0 F3      BNE $F3C8
F3D5   20 97 F4   JSR $F497
F3D8   A0 04      LDY #$04
F3DA   A9 00      LDA #$00
F3DC   59 16 00   EOR $0016,Y
F3DF   88         DEY
F3E0   10 FA      BPL $F3DC
F3E2   C9 00      CMP #$00
F3E4   D0 38      BNE $F41E
F3E6   A6 3E      LDX $3E
F3E8   A5 18      LDA $18
F3EA   95 22      STA $22,X
F3EC   A5 45      LDA $45
F3EE   C9 30      CMP #$30
F3F0   F0 1E      BEQ $F410
F3F2   A5 3E      LDA $3E
F3F4   0A         ASL
F3F5   A8         TAY
F3F6   B9 12 00   LDA $0012,Y
F3F9   C5 16      CMP $16
F3FB   D0 1E      BNE $F41B
F3FD   B9 13 00   LDA $0013,Y
F400   C5 17      CMP $17
F402   D0 17      BNE $F41B
F404   4C 23 F4   JMP $F423
F407   C6 4B      DEC $4B
F409   D0 B0      BNE $F3BB
F40B   A9 02      LDA #$02
F40D   20 69 F9   JSR $F969

; preserve block header

F410   A5 16      LDA $16
F412   85 12      STA $12
F414   A5 17      LDA $17
F416   85 13      STA $13
F418   A9 01      LDA #$01
F41A   .BY $2C
F41B   A9 0B      LDA #$0B
F41D   .BY $2C
F41E   A9 09      LDA #$09
F420   4C 69 F9   JMP $F969

; job optimisation

F423   A9 7F      LDA #$7F
F425   85 4C      STA $4C
F427   A5 19      LDA $19
F429   18         CLC
F42A   69 02      ADC #$02
F42C   C5 43      CMP $43
F42E   90 02      BCC $F432
F430   E5 43      SBC $43
F432   85 4D      STA $4D
F434   A2 05      LDX #$05
F436   86 3F      STX $3F
F438   A2 FF      LDX #$FF
F43A   20 93 F3   JSR $F393
F43D   10 44      BPL $F483
F43F   85 44      STA $44
F441   29 01      AND #$01
F443   C5 3E      CMP $3E
F445   D0 3C      BNE $F483
F447   A0 00      LDY #$00
F449   B1 32      LDA ($32),Y
F44B   C5 40      CMP $40
F44D   D0 34      BNE $F483
F44F   A5 45      LDA $45
F451   C9 60      CMP #$60
F453   F0 0C      BEQ $F461
F455   A0 01      LDY #$01
F457   38         SEC
F458   B1 32      LDA ($32),Y
F45A   E5 4D      SBC $4D
F45C   10 03      BPL $F461
F45E   18         CLC
F45F   65 43      ADC $43
F461   C5 4C      CMP $4C
F463   B0 1E      BCS $F483
F465   48         PHA
F466   A5 45      LDA $45
F468   F0 14      BEQ $F47E
F46A   68         PLA
F46B   C9 09      CMP #$09
F46D   90 14      BCC $F483
F46F   C9 0C      CMP #$0C
F471   B0 10      BCS $F483
F473   85 4C      STA $4C
F475   A5 3F      LDA $3F
F477   AA         TAX
F478   69 03      ADC #$03
F47A   85 31      STA $31
F47C   D0 05      BNE $F483
F47E   68         PLA
F47F   C9 06      CMP #$06
F481   90 F0      BCC $F473
F483   C6 3F      DEC $3F
F485   10 B3      BPL $F43A
F487   8A         TXA
F488   10 03      BPL $F48D
F48A   4C 9C F9   JMP $F99C
F48D   86 3F      STX $3F
F48F   20 93 F3   JSR $F393
F492   A5 45      LDA $45
F494   4C CA F4   JMP $F4CA
F497   A5 30      LDA $30
F499   48         PHA
F49A   A5 31      LDA $31
F49C   48         PHA
F49D   A9 24      LDA #$24
F49F   85 30      STA $30
F4A1   A9 00      LDA #$00
F4A3   85 31      STA $31
F4A5   A9 00      LDA #$00
F4A7   85 34      STA $34
F4A9   20 E6 F7   JSR $F7E6
F4AC   A5 55      LDA $55
F4AE   85 18      STA $18
F4B0   A5 54      LDA $54
F4B2   85 19      STA $19
F4B4   A5 53      LDA $53
F4B6   85 1A      STA $1A
F4B8   20 E6 F7   JSR $F7E6
F4BB   A5 52      LDA $52
F4BD   85 17      STA $17
F4BF   A5 53      LDA $53
F4C1   85 16      STA $16
F4C3   68         PLA
F4C4   85 31      STA $31
F4C6   68         PLA
F4C7   85 30      STA $30
F4C9   60         RTS

; test command code further

F4CA   C9 00      CMP #$00
F4CC   F0 03      BEQ $F4D1
F4CE   4C 6E F5   JMP $F56E

; read sector

F4D1   20 0A F5   JSR $F50A
F4D4   50 FE      BVC $F4D4
F4D6   B8         CLV
F4D7   AD 01 1C   LDA $1C01
F4DA   91 30      STA ($30),Y
F4DC   C8         INY
F4DD   D0 F5      BNE $F4D4
F4DF   A0 BA      LDY #$BA
F4E1   50 FE      BVC $F4E1
F4E3   B8         CLV
F4E4   AD 01 1C   LDA $1C01
F4E7   99 00 01   STA $0100,Y
F4EA   C8         INY
F4EB   D0 F4      BNE $F4E1
F4ED   20 E0 F8   JSR $F8E0
F4F0   A5 38      LDA $38
F4F2   C5 47      CMP $47
F4F4   F0 05      BEQ $F4FB
F4F6   A9 04      LDA #$04
F4F8   4C 69 F9   JMP $F969
F4FB   20 E9 F5   JSR $F5E9
F4FE   C5 3A      CMP $3A
F500   F0 03      BEQ $F505
F502   A9 05      LDA #$05
F504   .BY $2C
F505   A9 01      LDA #$01
F507   4C 69 F9   JMP $F969

; find start of data block

F50A   20 10 F5   JSR $F510
F50D   4C 56 F5   JMP $F556

; read block header

F510   A5 3D      LDA $3D
F512   0A         ASL
F513   AA         TAX
F514   B5 12      LDA $12,X
F516   85 16      STA $16
F518   B5 13      LDA $13,X
F51A   85 17      STA $17
F51C   A0 00      LDY #$00
F51E   B1 32      LDA ($32),Y
F520   85 18      STA $18
F522   C8         INY
F523   B1 32      LDA ($32),Y
F525   85 19      STA $19
F527   A9 00      LDA #$00
F529   45 16      EOR $16
F52B   45 17      EOR $17
F52D   45 18      EOR $18
F52F   45 19      EOR $19
F531   85 1A      STA $1A
F533   20 34 F9   JSR $F934
F536   A2 5A      LDX #$5A
F538   20 56 F5   JSR $F556
F53B   A0 00      LDY #$00
F53D   50 FE      BVC $F53D
F53F   B8         CLV
F540   AD 01 1C   LDA $1C01
F543   D9 24 00   CMP $0024,Y
F546   D0 06      BNE $F54E
F548   C8         INY
F549   C0 08      CPY #$08
F54B   D0 F0      BNE $F53D
F54D   60         RTS
F54E   CA         DEX
F54F   D0 E7      BNE $F538
F551   A9 02      LDA #$02
F553   4C 69 F9   JMP $F969

; wait for SYNC

F556   A9 D0      LDA #$D0
F558   8D 05 18   STA $1805
F55B   A9 03      LDA #$03
F55D   2C 05 18   BIT $1805
F560   10 F1      BPL $F553
F562   2C 00 1C   BIT $1C00
F565   30 F6      BMI $F55D
F567   AD 01 1C   LDA $1C01
F56A   B8         CLV
F56B   A0 00      LDY #$00
F56D   60         RTS

; test command code further

F56E   C9 10      CMP #$10
F570   F0 03      BEQ $F575
F572   4C 91 F6   JMP $F691

; write data block to disk

F575   20 E9 F5   JSR $F5E9
F578   85 3A      STA $3A
F57A   AD 00 1C   LDA $1C00
F57D   29 10      AND #$10
F57F   D0 05      BNE $F586
F581   A9 08      LDA #$08
F583   4C 69 F9   JMP $F969
F586   20 8F F7   JSR $F78F
F589   20 10 F5   JSR $F510
F58C   A2 09      LDX #$09
F58E   50 FE      BVC $F58E
F590   B8         CLV
F591   CA         DEX
F592   D0 FA      BNE $F58E
F594   A9 FF      LDA #$FF
F596   8D 03 1C   STA $1C03
F599   AD 0C 1C   LDA $1C0C
F59C   29 1F      AND #$1F
F59E   09 C0      ORA #$C0
F5A0   8D 0C 1C   STA $1C0C
F5A3   A9 FF      LDA #$FF
F5A5   A2 05      LDX #$05
F5A7   8D 01 1C   STA $1C01
F5AA   B8         CLV
F5AB   50 FE      BVC $F5AB
F5AD   B8         CLV
F5AE   CA         DEX
F5AF   D0 FA      BNE $F5AB
F5B1   A0 BB      LDY #$BB
F5B3   B9 00 01   LDA $0100,Y
F5B6   50 FE      BVC $F5B6
F5B8   B8         CLV
F5B9   8D 01 1C   STA $1C01
F5BC   C8         INY
F5BD   D0 F4      BNE $F5B3
F5BF   B1 30      LDA ($30),Y
F5C1   50 FE      BVC $F5C1
F5C3   B8         CLV
F5C4   8D 01 1C   STA $1C01
F5C7   C8         INY
F5C8   D0 F5      BNE $F5BF
F5CA   50 FE      BVC $F5CA
F5CC   AD 0C 1C   LDA $1C0C
F5CF   09 E0      ORA #$E0
F5D1   8D 0C 1C   STA $1C0C
F5D4   A9 00      LDA #$00
F5D6   8D 03 1C   STA $1C03
F5D9   20 F2 F5   JSR $F5F2
F5DC   A4 3F      LDY $3F
F5DE   B9 00 00   LDA $0000,Y
F5E1   49 30      EOR #$30
F5E3   99 00 00   STA $0000,Y
F5E6   4C B1 F3   JMP $F3B1

; calculate parity for data buffer

F5E9   A9 00      LDA #$00
F5EB   A8         TAY
F5EC   51 30      EOR ($30),Y
F5EE   C8         INY
F5EF   D0 FB      BNE $F5EC
F5F1   60         RTS

; convert GCR data to normal data

F5F2   A9 00      LDA #$00
F5F4   85 2E      STA $2E
F5F6   85 30      STA $30
F5F8   85 4F      STA $4F
F5FA   A5 31      LDA $31
F5FC   85 4E      STA $4E
F5FE   A9 01      LDA #$01
F600   85 31      STA $31
F602   85 2F      STA $2F
F604   A9 BB      LDA #$BB
F606   85 34      STA $34
F608   85 36      STA $36
F60A   20 E6 F7   JSR $F7E6
F60D   A5 52      LDA $52
F60F   85 38      STA $38
F611   A4 36      LDY $36
F613   A5 53      LDA $53
F615   91 2E      STA ($2E),Y
F617   C8         INY
F618   A5 54      LDA $54
F61A   91 2E      STA ($2E),Y
F61C   C8         INY
F61D   A5 55      LDA $55
F61F   91 2E      STA ($2E),Y
F621   C8         INY
F622   84 36      STY $36
F624   20 E6 F7   JSR $F7E6
F627   A4 36      LDY $36
F629   A5 52      LDA $52
F62B   91 2E      STA ($2E),Y
F62D   C8         INY
F62E   A5 53      LDA $53
F630   91 2E      STA ($2E),Y
F632   C8         INY
F633   F0 0E      BEQ $F643
F635   A5 54      LDA $54
F637   91 2E      STA ($2E),Y
F639   C8         INY
F63A   A5 55      LDA $55
F63C   91 2E      STA ($2E),Y
F63E   C8         INY
F63F   84 36      STY $36
F641   D0 E1      BNE $F624
F643   A5 54      LDA $54
F645   91 30      STA ($30),Y
F647   C8         INY
F648   A5 55      LDA $55
F64A   91 30      STA ($30),Y
F64C   C8         INY
F64D   84 36      STY $36
F64F   20 E6 F7   JSR $F7E6
F652   A4 36      LDY $36
F654   A5 52      LDA $52
F656   91 30      STA ($30),Y
F658   C8         INY
F659   A5 53      LDA $53
F65B   91 30      STA ($30),Y
F65D   C8         INY
F65E   A5 54      LDA $54
F660   91 30      STA ($30),Y
F662   C8         INY
F663   A5 55      LDA $55
F665   91 30      STA ($30),Y
F667   C8         INY
F668   84 36      STY $36
F66A   C0 BB      CPY #$BB
F66C   90 E1      BCC $F64F
F66E   A9 45      LDA #$45
F670   85 2E      STA $2E
F672   A5 31      LDA $31
F674   85 2F      STA $2F
F676   A0 BA      LDY #$BA
F678   B1 30      LDA ($30),Y
F67A   91 2E      STA ($2E),Y
F67C   88         DEY
F67D   D0 F9      BNE $F678
F67F   B1 30      LDA ($30),Y
F681   91 2E      STA ($2E),Y
F683   A2 BB      LDX #$BB
F685   BD 00 01   LDA $0100,X
F688   91 30      STA ($30),Y
F68A   C8         INY
F68B   E8         INX
F68C   D0 F7      BNE $F685
F68E   86 50      STX $50
F690   60         RTS

; test command code further

F691   C9 20      CMP #$20
F693   F0 03      BEQ $F698
F695   4C CA F6   JMP $F6CA

; compare written data with data on disk

F698   20 E9 F5   JSR $F5E9
F69B   85 3A      STA $3A
F69D   20 8F F7   JSR $F78F
F6A0   20 0A F5   JSR $F50A
F6A3   A0 BB      LDY #$BB
F6A5   B9 00 01   LDA $0100,Y
F6A8   50 FE      BVC $F6A8
F6AA   B8         CLV
F6AB   4D 01 1C   EOR $1C01
F6AE   D0 15      BNE $F6C5
F6B0   C8         INY
F6B1   D0 F2      BNE $F6A5
F6B3   B1 30      LDA ($30),Y
F6B5   50 FE      BVC $F6B5
F6B7   B8         CLV
F6B8   4D 01 1C   EOR $1C01
F6BB   D0 08      BNE $F6C5
F6BD   C8         INY
F6BE   C0 FD      CPY #$FD
F6C0   D0 F1      BNE $F6B3
F6C2   4C 18 F4   JMP $F418
F6C5   A9 07      LDA #$07
F6C7   4C 69 F9   JMP $F969

; command code for find sector

F6CA   20 10 F5   JSR $F510
F6CD   4C 18 F4   JMP $F418

; convert 4 bytes ($52-$55) to 5 GCR bytes ($56-$5a)

F6D0   A9 00      LDA #$00
F6D2   85 57      STA $57
F6D4   85 5A      STA $5A
F6D6   A4 34      LDY $34
F6D8   A5 52      LDA $52
F6DA   29 F0      AND #$F0
F6DC   4A         LSR
F6DD   4A         LSR
F6DE   4A         LSR
F6DF   4A         LSR
F6E0   AA         TAX
F6E1   BD 7F F7   LDA $F77F,X
F6E4   0A         ASL
F6E5   0A         ASL
F6E6   0A         ASL
F6E7   85 56      STA $56
F6E9   A5 52      LDA $52
F6EB   29 0F      AND #$0F
F6ED   AA         TAX
F6EE   BD 7F F7   LDA $F77F,X
F6F1   6A         ROR
F6F2   66 57      ROR $57
F6F4   6A         ROR
F6F5   66 57      ROR $57
F6F7   29 07      AND #$07
F6F9   05 56      ORA $56
F6FB   91 30      STA ($30),Y
F6FD   C8         INY
F6FE   A5 53      LDA $53
F700   29 F0      AND #$F0
F702   4A         LSR
F703   4A         LSR
F704   4A         LSR
F705   4A         LSR
F706   AA         TAX
F707   BD 7F F7   LDA $F77F,X
F70A   0A         ASL
F70B   05 57      ORA $57
F70D   85 57      STA $57
F70F   A5 53      LDA $53
F711   29 0F      AND #$0F
F713   AA         TAX
F714   BD 7F F7   LDA $F77F,X
F717   2A         ROL
F718   2A         ROL
F719   2A         ROL
F71A   2A         ROL
F71B   85 58      STA $58
F71D   2A         ROL
F71E   29 01      AND #$01
F720   05 57      ORA $57
F722   91 30      STA ($30),Y
F724   C8         INY
F725   A5 54      LDA $54
F727   29 F0      AND #$F0
F729   4A         LSR
F72A   4A         LSR
F72B   4A         LSR
F72C   4A         LSR
F72D   AA         TAX
F72E   BD 7F F7   LDA $F77F,X
F731   18         CLC
F732   6A         ROR
F733   05 58      ORA $58
F735   91 30      STA ($30),Y
F737   C8         INY
F738   6A         ROR
F739   29 80      AND #$80
F73B   85 59      STA $59
F73D   A5 54      LDA $54
F73F   29 0F      AND #$0F
F741   AA         TAX
F742   BD 7F F7   LDA $F77F,X
F745   0A         ASL
F746   0A         ASL
F747   29 7C      AND #$7C
F749   05 59      ORA $59
F74B   85 59      STA $59
F74D   A5 55      LDA $55
F74F   29 F0      AND #$F0
F751   4A         LSR
F752   4A         LSR
F753   4A         LSR
F754   4A         LSR
F755   AA         TAX
F756   BD 7F F7   LDA $F77F,X
F759   6A         ROR
F75A   66 5A      ROR $5A
F75C   6A         ROR
F75D   66 5A      ROR $5A
F75F   6A         ROR
F760   66 5A      ROR $5A
F762   29 03      AND #$03
F764   05 59      ORA $59
F766   91 30      STA ($30),Y
F768   C8         INY
F769   D0 04      BNE $F76F
F76B   A5 2F      LDA $2F
F76D   85 31      STA $31
F76F   A5 55      LDA $55
F771   29 0F      AND #$0F
F773   AA         TAX
F774   BD 7F F7   LDA $F77F,X
F777   05 5A      ORA $5A
F779   91 30      STA ($30),Y
F77B   C8         INY
F77C   84 34      STY $34
F77E   60         RTS

; GCR code table

F77F   .BY $0A,$0B,$12,$13,$0E,$0F,$16,$17
F787   .BY $09,$19,$1A,$1B,$0D,$1D,$1E,$15

; convert 260 bytes to 325 bytes group code

F78F   A9 00      LDA #$00
F791   85 30      STA $30
F793   85 2E      STA $2E
F795   85 36      STA $36
F797   A9 BB      LDA #$BB
F799   85 34      STA $34
F79B   85 50      STA $50
F79D   A5 31      LDA $31
F79F   85 2F      STA $2F
F7A1   A9 01      LDA #$01
F7A3   85 31      STA $31
F7A5   A5 47      LDA $47
F7A7   85 52      STA $52
F7A9   A4 36      LDY $36
F7AB   B1 2E      LDA ($2E),Y
F7AD   85 53      STA $53
F7AF   C8         INY
F7B0   B1 2E      LDA ($2E),Y
F7B2   85 54      STA $54
F7B4   C8         INY
F7B5   B1 2E      LDA ($2E),Y
F7B7   85 55      STA $55
F7B9   C8         INY
F7BA   84 36      STY $36
F7BC   20 D0 F6   JSR $F6D0
F7BF   A4 36      LDY $36
F7C1   B1 2E      LDA ($2E),Y
F7C3   85 52      STA $52
F7C5   C8         INY
F7C6   F0 11      BEQ $F7D9
F7C8   B1 2E      LDA ($2E),Y
F7CA   85 53      STA $53
F7CC   C8         INY
F7CD   B1 2E      LDA ($2E),Y
F7CF   85 54      STA $54
F7D1   C8         INY
F7D2   B1 2E      LDA ($2E),Y
F7D4   85 55      STA $55
F7D6   C8         INY
F7D7   D0 E1      BNE $F7BA
F7D9   A5 3A      LDA $3A
F7DB   85 53      STA $53
F7DD   A9 00      LDA #$00
F7DF   85 54      STA $54
F7E1   85 55      STA $55
F7E3   4C D0 F6   JMP $F6D0

; convert 5 GCR bytes to 4 normal bytes

F7E6   A4 34      LDY $34
F7E8   B1 30      LDA ($30),Y
F7EA   29 F8      AND #$F8
F7EC   4A         LSR
F7ED   4A         LSR
F7EE   4A         LSR
F7EF   85 56      STA $56
F7F1   B1 30      LDA ($30),Y
F7F3   29 07      AND #$07
F7F5   0A         ASL
F7F6   0A         ASL
F7F7   85 57      STA $57
F7F9   C8         INY
F7FA   D0 06      BNE $F802
F7FC   A5 4E      LDA $4E
F7FE   85 31      STA $31
F800   A4 4F      LDY $4F
F802   B1 30      LDA ($30),Y
F804   29 C0      AND #$C0
F806   2A         ROL
F807   2A         ROL
F808   2A         ROL
F809   05 57      ORA $57
F80B   85 57      STA $57
F80D   B1 30      LDA ($30),Y
F80F   29 3E      AND #$3E
F811   4A         LSR
F812   85 58      STA $58
F814   B1 30      LDA ($30),Y
F816   29 01      AND #$01
F818   0A         ASL
F819   0A         ASL
F81A   0A         ASL
F81B   0A         ASL
F81C   85 59      STA $59
F81E   C8         INY
F81F   B1 30      LDA ($30),Y
F821   29 F0      AND #$F0
F823   4A         LSR
F824   4A         LSR
F825   4A         LSR
F826   4A         LSR
F827   05 59      ORA $59
F829   85 59      STA $59
F82B   B1 30      LDA ($30),Y
F82D   29 0F      AND #$0F
F82F   0A         ASL
F830   85 5A      STA $5A
F832   C8         INY
F833   B1 30      LDA ($30),Y
F835   29 80      AND #$80
F837   18         CLC
F838   2A         ROL
F839   2A         ROL
F83A   29 01      AND #$01
F83C   05 5A      ORA $5A
F83E   85 5A      STA $5A
F840   B1 30      LDA ($30),Y
F842   29 7C      AND #$7C
F844   4A         LSR
F845   4A         LSR
F846   85 5B      STA $5B
F848   B1 30      LDA ($30),Y
F84A   29 03      AND #$03
F84C   0A         ASL
F84D   0A         ASL
F84E   0A         ASL
F84F   85 5C      STA $5C
F851   C8         INY
F852   D0 06      BNE $F85A
F854   A5 4E      LDA $4E
F856   85 31      STA $31
F858   A4 4F      LDY $4F
F85A   B1 30      LDA ($30),Y
F85C   29 E0      AND #$E0
F85E   2A         ROL
F85F   2A         ROL
F860   2A         ROL
F861   2A         ROL
F862   05 5C      ORA $5C
F864   85 5C      STA $5C
F866   B1 30      LDA ($30),Y
F868   29 1F      AND #$1F
F86A   85 5D      STA $5D
F86C   C8         INY
F86D   84 34      STY $34
F86F   A6 56      LDX $56
F871   BD A0 F8   LDA $F8A0,X
F874   A6 57      LDX $57
F876   1D C0 F8   ORA $F8C0,X
F879   85 52      STA $52
F87B   A6 58      LDX $58
F87D   BD A0 F8   LDA $F8A0,X
F880   A6 59      LDX $59
F882   1D C0 F8   ORA $F8C0,X
F885   85 53      STA $53
F887   A6 5A      LDX $5A
F889   BD A0 F8   LDA $F8A0,X
F88C   A6 5B      LDX $5B
F88E   1D C0 F8   ORA $F8C0,X
F891   85 54      STA $54
F893   A6 5C      LDX $5C
F895   BD A0 F8   LDA $F8A0,X
F898   A6 5D      LDX $5D
F89A   1D C0 F8   ORA $F8C0,X
F89D   85 55      STA $55
F89F   60         RTS

; conversion table   GCR to binary - high byte, $FF means invalid

F8A0   .BY $FF,$FF,$FF,$FF,$FF,$FF,$FF,$FF
F8A8   .BY $FF,$80,$00,$10,$FF,$C0,$40,$50
F8B0   .BY $FF,$FF,$20,$30,$FF,$F0,$60,$70
F8B8   .BY $FF,$90,$A0,$B0,$FF,$D0,$E0,$FF

; conversion table   GCR to binary - low  byte, $FF means invalid

F8C0   .BY $FF,$FF,$FF,$FF,$FF,$FF,$FF,$FF
F8C8   .BY $FF,$08,$00,$01,$FF,$0C,$04,$05
F8D0   .BY $FF,$FF,$02,$03,$FF,$0F,$06,$07
F8D8   .BY $FF,$09,$0A,$0B,$FF,$0D,$0E,$FF

; decode 69 GCR bytes

F8E0   A9 00      LDA #$00
F8E2   85 34      STA $34
F8E4   85 2E      STA $2E
F8E6   85 36      STA $36
F8E8   A9 01      LDA #$01
F8EA   85 4E      STA $4E
F8EC   A9 BA      LDA #$BA
F8EE   85 4F      STA $4F
F8F0   A5 31      LDA $31
F8F2   85 2F      STA $2F
F8F4   20 E6 F7   JSR $F7E6
F8F7   A5 52      LDA $52
F8F9   85 38      STA $38
F8FB   A4 36      LDY $36
F8FD   A5 53      LDA $53
F8FF   91 2E      STA ($2E),Y
F901   C8         INY
F902   A5 54      LDA $54
F904   91 2E      STA ($2E),Y
F906   C8         INY
F907   A5 55      LDA $55
F909   91 2E      STA ($2E),Y
F90B   C8         INY
F90C   84 36      STY $36
F90E   20 E6 F7   JSR $F7E6
F911   A4 36      LDY $36
F913   A5 52      LDA $52
F915   91 2E      STA ($2E),Y
F917   C8         INY
F918   F0 11      BEQ $F92B
F91A   A5 53      LDA $53
F91C   91 2E      STA ($2E),Y
F91E   C8         INY
F91F   A5 54      LDA $54
F921   91 2E      STA ($2E),Y
F923   C8         INY
F924   A5 55      LDA $55
F926   91 2E      STA ($2E),Y
F928   C8         INY
F929   D0 E1      BNE $F90C
F92B   A5 53      LDA $53
F92D   85 3A      STA $3A
F92F   A5 2F      LDA $2F
F931   85 31      STA $31
F933   60         RTS

; convert block header to GCR code

F934   A5 31      LDA $31
F936   85 2F      STA $2F
F938   A9 00      LDA #$00
F93A   85 31      STA $31
F93C   A9 24      LDA #$24
F93E   85 34      STA $34
F940   A5 39      LDA $39
F942   85 52      STA $52
F944   A5 1A      LDA $1A
F946   85 53      STA $53
F948   A5 19      LDA $19
F94A   85 54      STA $54
F94C   A5 18      LDA $18
F94E   85 55      STA $55
F950   20 D0 F6   JSR $F6D0
F953   A5 17      LDA $17
F955   85 52      STA $52
F957   A5 16      LDA $16
F959   85 53      STA $53
F95B   A9 00      LDA #$00
F95D   85 54      STA $54
F95F   85 55      STA $55
F961   20 D0 F6   JSR $F6D0
F964   A5 2F      LDA $2F
F966   85 31      STA $31
F968   60         RTS

; error entry disk controller

F969   A4 3F      LDY $3F
F96B   99 00 00   STA $0000,Y
F96E   A5 50      LDA $50
F970   F0 03      BEQ $F975
F972   20 F2 F5   JSR $F5F2
F975   20 8F F9   JSR $F98F
F978   A6 49      LDX $49
F97A   9A         TXS
F97B   4C BE F2   JMP $F2BE

; turn drive motor on

F97E   A9 A0      LDA #$A0
F980   85 20      STA $20
F982   AD 00 1C   LDA $1C00
F985   09 04      ORA #$04
F987   8D 00 1C   STA $1C00
F98A   A9 3C      LDA #$3C
F98C   85 48      STA $48
F98E   60         RTS

; turn drive motor off

F98F   A6 3E      LDX $3E
F991   A5 20      LDA $20
F993   09 10      ORA #$10
F995   85 20      STA $20
F997   A9 FF      LDA #$FF
F999   85 48      STA $48
F99B   60         RTS

; job loop disk controller

F99C   AD 07 1C   LDA $1C07
F99F   8D 05 1C   STA $1C05
F9A2   AD 00 1C   LDA $1C00
F9A5   29 10      AND #$10
F9A7   C5 1E      CMP $1E
F9A9   85 1E      STA $1E
F9AB   F0 04      BEQ $F9B1
F9AD   A9 01      LDA #$01
F9AF   85 1C      STA $1C
F9B1   AD FE 02   LDA $02FE
F9B4   F0 15      BEQ $F9CB
F9B6   C9 02      CMP #$02
F9B8   D0 07      BNE $F9C1
F9BA   A9 00      LDA #$00
F9BC   8D FE 02   STA $02FE
F9BF   F0 0A      BEQ $F9CB
F9C1   85 4A      STA $4A
F9C3   A9 02      LDA #$02
F9C5   8D FE 02   STA $02FE
F9C8   4C 2E FA   JMP $FA2E

;

F9CB   A6 3E      LDX $3E
F9CD   30 07      BMI $F9D6
F9CF   A5 20      LDA $20
F9D1   A8         TAY
F9D2   C9 20      CMP #$20
F9D4   D0 03      BNE $F9D9
F9D6   4C BE FA   JMP $FABE

;

F9D9   C6 48      DEC $48
F9DB   D0 1D      BNE $F9FA
F9DD   98         TYA
F9DE   10 04      BPL $F9E4
F9E0   29 7F      AND #$7F
F9E2   85 20      STA $20
F9E4   29 10      AND #$10
F9E6   F0 12      BEQ $F9FA
F9E8   AD 00 1C   LDA $1C00
F9EB   29 FB      AND #$FB
F9ED   8D 00 1C   STA $1C00
F9F0   A9 FF      LDA #$FF
F9F2   85 3E      STA $3E
F9F4   A9 00      LDA #$00
F9F6   85 20      STA $20
F9F8   F0 DC      BEQ $F9D6
F9FA   98         TYA
F9FB   29 40      AND #$40
F9FD   D0 03      BNE $FA02
F9FF   4C BE FA   JMP $FABE
FA02   6C 62 00   JMP ($0062)

;

FA05   A5 4A      LDA $4A
FA07   10 05      BPL $FA0E
FA09   49 FF      EOR #$FF
FA0B   18         CLC
FA0C   69 01      ADC #$01
FA0E   C5 64      CMP $64
FA10   B0 0A      BCS $FA1C
FA12   A9 3B      LDA #$3B
FA14   85 62      STA $62
FA16   A9 FA      LDA #$FA
FA18   85 63      STA $63
FA1A   D0 12      BNE $FA2E

; calculate number of head steps

FA1C   E5 5E      SBC $5E
FA1E   E5 5E      SBC $5E
FA20   85 61      STA $61
FA22   A5 5E      LDA $5E
FA24   85 60      STA $60
FA26   A9 7B      LDA #$7B
FA28   85 62      STA $62
FA2A   A9 FA      LDA #$FA
FA2C   85 63      STA $63
FA2E   A5 4A      LDA $4A
FA30   10 31      BPL $FA63
FA32   E6 4A      INC $4A
FA34   AE 00 1C   LDX $1C00
FA37   CA         DEX
FA38   4C 69 FA   JMP $FA69

; move stepper motor short distance

FA3B   A5 4A      LDA $4A
FA3D   D0 EF      BNE $FA2E
FA3F   A9 4E      LDA #$4E
FA41   85 62      STA $62
FA43   A9 FA      LDA #$FA
FA45   85 63      STA $63
FA47   A9 05      LDA #$05
FA49   85 60      STA $60
FA4B   4C BE FA   JMP $FABE

; load head

FA4E   C6 60      DEC $60
FA50   D0 6C      BNE $FABE
FA52   A5 20      LDA $20
FA54   29 BF      AND #$BF
FA56   85 20      STA $20
FA58   A9 05      LDA #$05
FA5A   85 62      STA $62
FA5C   A9 FA      LDA #$FA
FA5E   85 63      STA $63
FA60   4C BE FA   JMP $FABE

;

FA63   C6 4A      DEC $4A
FA65   AE 00 1C   LDX $1C00
FA68   E8         INX
FA69   8A         TXA
FA6A   29 03      AND #$03
FA6C   85 4B      STA $4B
FA6E   AD 00 1C   LDA $1C00
FA71   29 FC      AND #$FC
FA73   05 4B      ORA $4B
FA75   8D 00 1C   STA $1C00
FA78   4C BE FA   JMP $FABE

; prepare fast head movement

FA7B   38         SEC
FA7C   AD 07 1C   LDA $1C07
FA7F   E5 5F      SBC $5F
FA81   8D 05 1C   STA $1C05
FA84   C6 60      DEC $60
FA86   D0 0C      BNE $FA94
FA88   A5 5E      LDA $5E
FA8A   85 60      STA $60
FA8C   A9 97      LDA #$97
FA8E   85 62      STA $62
FA90   A9 FA      LDA #$FA
FA92   85 63      STA $63
FA94   4C 2E FA   JMP $FA2E

; fast head movement

FA97   C6 61      DEC $61
FA99   D0 F9      BNE $FA94
FA9B   A9 A5      LDA #$A5
FA9D   85 62      STA $62
FA9F   A9 FA      LDA #$FA
FAA1   85 63      STA $63
FAA3   D0 EF      BNE $FA94

; prepare slow head movement

FAA5   AD 07 1C   LDA $1C07
FAA8   18         CLC
FAA9   65 5F      ADC $5F
FAAB   8D 05 1C   STA $1C05
FAAE   C6 60      DEC $60
FAB0   D0 E2      BNE $FA94
FAB2   A9 4E      LDA #$4E
FAB4   85 62      STA $62
FAB6   A9 FA      LDA #$FA
FAB8   85 63      STA $63
FABA   A9 05      LDA #$05
FABC   85 60      STA $60
FABE   AD 0C 1C   LDA $1C0C
FAC1   29 FD      AND #$FD
FAC3   8D 0C 1C   STA $1C0C
FAC6   60         RTS

; formatting

FAC7   A5 51      LDA $51
FAC9   10 2A      BPL $FAF5
FACB   A6 3D      LDX $3D
FACD   A9 60      LDA #$60
FACF   95 20      STA $20,X
FAD1   A9 01      LDA #$01
FAD3   95 22      STA $22,X
FAD5   85 51      STA $51
FAD7   A9 A4      LDA #$A4
FAD9   85 4A      STA $4A
FADB   AD 00 1C   LDA $1C00
FADE   29 FC      AND #$FC
FAE0   8D 00 1C   STA $1C00
FAE3   A9 0A      LDA #$0A
FAE5   8D 20 06   STA $0620
FAE8   A9 A0      LDA #$A0
FAEA   8D 21 06   STA $0621
FAED   A9 0F      LDA #$0F
FAEF   8D 22 06   STA $0622
FAF2   4C 9C F9   JMP $F99C
FAF5   A0 00      LDY #$00
FAF7   D1 32      CMP ($32),Y
FAF9   F0 05      BEQ $FB00
FAFB   91 32      STA ($32),Y
FAFD   4C 9C F9   JMP $F99C
FB00   AD 00 1C   LDA $1C00
FB03   29 10      AND #$10
FB05   D0 05      BNE $FB0C
FB07   A9 08      LDA #$08
FB09   4C D3 FD   JMP $FDD3
FB0C   20 A3 FD   JSR $FDA3
FB0F   20 C3 FD   JSR $FDC3
FB12   A9 55      LDA #$55
FB14   8D 01 1C   STA $1C01
FB17   20 C3 FD   JSR $FDC3
FB1A   20 00 FE   JSR $FE00
FB1D   20 56 F5   JSR $F556
FB20   A9 40      LDA #$40
FB22   0D 0B 18   ORA $180B
FB25   8D 0B 18   STA $180B
FB28   A9 62      LDA #$62
FB2A   8D 06 18   STA $1806
FB2D   A9 00      LDA #$00
FB2F   8D 07 18   STA $1807
FB32   8D 05 18   STA $1805
FB35   A0 00      LDY #$00
FB37   A2 00      LDX #$00
FB39   2C 00 1C   BIT $1C00
FB3C   30 FB      BMI $FB39
FB3E   2C 00 1C   BIT $1C00
FB41   10 FB      BPL $FB3E
FB43   AD 04 18   LDA $1804
FB46   2C 00 1C   BIT $1C00
FB49   10 11      BPL $FB5C
FB4B   AD 0D 18   LDA $180D
FB4E   0A         ASL
FB4F   10 F5      BPL $FB46
FB51   E8         INX
FB52   D0 EF      BNE $FB43
FB54   C8         INY
FB55   D0 EC      BNE $FB43
FB57   A9 02      LDA #$02
FB59   4C D3 FD   JMP $FDD3
FB5C   86 71      STX $71
FB5E   84 72      STY $72
FB60   A2 00      LDX #$00
FB62   A0 00      LDY #$00
FB64   AD 04 18   LDA $1804
FB67   2C 00 1C   BIT $1C00
FB6A   30 11      BMI $FB7D
FB6C   AD 0D 18   LDA $180D
FB6F   0A         ASL
FB70   10 F5      BPL $FB67
FB72   E8         INX
FB73   D0 EF      BNE $FB64
FB75   C8         INY
FB76   D0 EC      BNE $FB64
FB78   A9 02      LDA #$02
FB7A   4C D3 FD   JMP $FDD3
FB7D   38         SEC
FB7E   8A         TXA
FB7F   E5 71      SBC $71
FB81   AA         TAX
FB82   85 70      STA $70
FB84   98         TYA
FB85   E5 72      SBC $72
FB87   A8         TAY
FB88   85 71      STA $71
FB8A   10 0B      BPL $FB97
FB8C   49 FF      EOR #$FF
FB8E   A8         TAY
FB8F   8A         TXA
FB90   49 FF      EOR #$FF
FB92   AA         TAX
FB93   E8         INX
FB94   D0 01      BNE $FB97
FB96   C8         INY
FB97   98         TYA
FB98   D0 04      BNE $FB9E
FB9A   E0 04      CPX #$04
FB9C   90 18      BCC $FBB6
FB9E   06 70      ASL $70
FBA0   26 71      ROL $71
FBA2   18         CLC
FBA3   A5 70      LDA $70
FBA5   6D 21 06   ADC $0621
FBA8   8D 21 06   STA $0621
FBAB   A5 71      LDA $71
FBAD   6D 22 06   ADC $0622
FBB0   8D 22 06   STA $0622
FBB3   4C 0C FB   JMP $FB0C
FBB6   A2 00      LDX #$00
FBB8   A0 00      LDY #$00
FBBA   B8         CLV
FBBB   AD 00 1C   LDA $1C00
FBBE   10 0E      BPL $FBCE
FBC0   50 F9      BVC $FBBB
FBC2   B8         CLV
FBC3   E8         INX
FBC4   D0 F5      BNE $FBBB
FBC6   C8         INY
FBC7   D0 F2      BNE $FBBB
FBC9   A9 03      LDA #$03
FBCB   4C D3 FD   JMP $FDD3
FBCE   8A         TXA
FBCF   0A         ASL
FBD0   8D 25 06   STA $0625
FBD3   98         TYA
FBD4   2A         ROL
FBD5   8D 24 06   STA $0624
FBD8   A9 BF      LDA #$BF
FBDA   2D 0B 18   AND $180B
FBDD   8D 0B 18   STA $180B
FBE0   A9 66      LDA #$66
FBE2   8D 26 06   STA $0626
FBE5   A6 43      LDX $43
FBE7   A0 00      LDY #$00
FBE9   98         TYA
FBEA   18         CLC
FBEB   6D 26 06   ADC $0626
FBEE   90 01      BCC $FBF1
FBF0   C8         INY
FBF1   C8         INY
FBF2   CA         DEX
FBF3   D0 F5      BNE $FBEA
FBF5   49 FF      EOR #$FF
FBF7   38         SEC
FBF8   69 00      ADC #$00
FBFA   18         CLC
FBFB   6D 25 06   ADC $0625
FBFE   B0 03      BCS $FC03
FC00   CE 24 06   DEC $0624
FC03   AA         TAX
FC04   98         TYA
FC05   49 FF      EOR #$FF
FC07   38         SEC
FC08   69 00      ADC #$00
FC0A   18         CLC
FC0B   6D 24 06   ADC $0624
FC0E   10 05      BPL $FC15
FC10   A9 04      LDA #$04
FC12   4C D3 FD   JMP $FDD3
FC15   A8         TAY
FC16   8A         TXA
FC17   A2 00      LDX #$00
FC19   38         SEC
FC1A   E5 43      SBC $43
FC1C   B0 03      BCS $FC21
FC1E   88         DEY
FC1F   30 03      BMI $FC24
FC21   E8         INX
FC22   D0 F5      BNE $FC19
FC24   8E 26 06   STX $0626
FC27   E0 04      CPX #$04
FC29   B0 05      BCS $FC30
FC2B   A9 05      LDA #$05
FC2D   4C D3 FD   JMP $FDD3
FC30   18         CLC
FC31   65 43      ADC $43
FC33   8D 27 06   STA $0627
FC36   A9 00      LDA #$00
FC38   8D 28 06   STA $0628
FC3B   A0 00      LDY #$00
FC3D   A6 3D      LDX $3D
FC3F   A5 39      LDA $39
FC41   99 00 03   STA $0300,Y
FC44   C8         INY
FC45   C8         INY
FC46   AD 28 06   LDA $0628
FC49   99 00 03   STA $0300,Y
FC4C   C8         INY
FC4D   A5 51      LDA $51
FC4F   99 00 03   STA $0300,Y
FC52   C8         INY
FC53   B5 13      LDA $13,X
FC55   99 00 03   STA $0300,Y
FC58   C8         INY
FC59   B5 12      LDA $12,X
FC5B   99 00 03   STA $0300,Y
FC5E   C8         INY
FC5F   A9 0F      LDA #$0F
FC61   99 00 03   STA $0300,Y
FC64   C8         INY
FC65   99 00 03   STA $0300,Y
FC68   C8         INY
FC69   A9 00      LDA #$00
FC6B   59 FA 02   EOR $02FA,Y
FC6E   59 FB 02   EOR $02FB,Y
FC71   59 FC 02   EOR $02FC,Y
FC74   59 FD 02   EOR $02FD,Y
FC77   99 F9 02   STA $02F9,Y
FC7A   EE 28 06   INC $0628
FC7D   AD 28 06   LDA $0628
FC80   C5 43      CMP $43
FC82   90 BB      BCC $FC3F
FC84   98         TYA
FC85   48         PHA
FC86   E8         INX
FC87   8A         TXA
FC88   9D 00 05   STA $0500,X
FC8B   E8         INX
FC8C   D0 FA      BNE $FC88
FC8E   A9 03      LDA #$03
FC90   85 31      STA $31
FC92   20 30 FE   JSR $FE30
FC95   68         PLA
FC96   A8         TAY
FC97   88         DEY
FC98   20 E5 FD   JSR $FDE5
FC9B   20 F5 FD   JSR $FDF5
FC9E   A9 05      LDA #$05
FCA0   85 31      STA $31
FCA2   20 E9 F5   JSR $F5E9
FCA5   85 3A      STA $3A
FCA7   20 8F F7   JSR $F78F
FCAA   A9 00      LDA #$00
FCAC   85 32      STA $32
FCAE   20 0E FE   JSR $FE0E
FCB1   A9 FF      LDA #$FF
FCB3   8D 01 1C   STA $1C01
FCB6   A2 05      LDX #$05
FCB8   50 FE      BVC $FCB8
FCBA   B8         CLV
FCBB   CA         DEX
FCBC   D0 FA      BNE $FCB8
FCBE   A2 0A      LDX #$0A
FCC0   A4 32      LDY $32
FCC2   50 FE      BVC $FCC2
FCC4   B8         CLV
FCC5   B9 00 03   LDA $0300,Y
FCC8   8D 01 1C   STA $1C01
FCCB   C8         INY
FCCC   CA         DEX
FCCD   D0 F3      BNE $FCC2
FCCF   A2 09      LDX #$09
FCD1   50 FE      BVC $FCD1
FCD3   B8         CLV
FCD4   A9 55      LDA #$55
FCD6   8D 01 1C   STA $1C01
FCD9   CA         DEX
FCDA   D0 F5      BNE $FCD1
FCDC   A9 FF      LDA #$FF
FCDE   A2 05      LDX #$05
FCE0   50 FE      BVC $FCE0
FCE2   B8         CLV
FCE3   8D 01 1C   STA $1C01
FCE6   CA         DEX
FCE7   D0 F7      BNE $FCE0
FCE9   A2 BB      LDX #$BB
FCEB   50 FE      BVC $FCEB
FCED   B8         CLV
FCEE   BD 00 01   LDA $0100,X
FCF1   8D 01 1C   STA $1C01
FCF4   E8         INX
FCF5   D0 F4      BNE $FCEB
FCF7   A0 00      LDY #$00
FCF9   50 FE      BVC $FCF9
FCFB   B8         CLV
FCFC   B1 30      LDA ($30),Y
FCFE   8D 01 1C   STA $1C01
FD01   C8         INY
FD02   D0 F5      BNE $FCF9
FD04   A9 55      LDA #$55
FD06   AE 26 06   LDX $0626
FD09   50 FE      BVC $FD09
FD0B   B8         CLV
FD0C   8D 01 1C   STA $1C01
FD0F   CA         DEX
FD10   D0 F7      BNE $FD09
FD12   A5 32      LDA $32
FD14   18         CLC
FD15   69 0A      ADC #$0A
FD17   85 32      STA $32
FD19   CE 28 06   DEC $0628
FD1C   D0 93      BNE $FCB1
FD1E   50 FE      BVC $FD1E
FD20   B8         CLV
FD21   50 FE      BVC $FD21
FD23   B8         CLV
FD24   20 00 FE   JSR $FE00
FD27   A9 C8      LDA #$C8
FD29   8D 23 06   STA $0623
FD2C   A9 00      LDA #$00
FD2E   85 30      STA $30
FD30   A9 03      LDA #$03
FD32   85 31      STA $31
FD34   A5 43      LDA $43
FD36   8D 28 06   STA $0628
FD39   20 56 F5   JSR $F556
FD3C   A2 0A      LDX #$0A
FD3E   A0 00      LDY #$00
FD40   50 FE      BVC $FD40
FD42   B8         CLV
FD43   AD 01 1C   LDA $1C01
FD46   D1 30      CMP ($30),Y
FD48   D0 0E      BNE $FD58
FD4A   C8         INY
FD4B   CA         DEX
FD4C   D0 F2      BNE $FD40
FD4E   18         CLC
FD4F   A5 30      LDA $30
FD51   69 0A      ADC #$0A
FD53   85 30      STA $30
FD55   4C 62 FD   JMP $FD62
FD58   CE 23 06   DEC $0623
FD5B   D0 CF      BNE $FD2C
FD5D   A9 06      LDA #$06
FD5F   4C D3 FD   JMP $FDD3
FD62   20 56 F5   JSR $F556
FD65   A0 BB      LDY #$BB
FD67   50 FE      BVC $FD67
FD69   B8         CLV
FD6A   AD 01 1C   LDA $1C01
FD6D   D9 00 01   CMP $0100,Y
FD70   D0 E6      BNE $FD58
FD72   C8         INY
FD73   D0 F2      BNE $FD67
FD75   A2 FC      LDX #$FC
FD77   50 FE      BVC $FD77
FD79   B8         CLV
FD7A   AD 01 1C   LDA $1C01
FD7D   D9 00 05   CMP $0500,Y
FD80   D0 D6      BNE $FD58
FD82   C8         INY
FD83   CA         DEX
FD84   D0 F1      BNE $FD77
FD86   CE 28 06   DEC $0628
FD89   D0 AE      BNE $FD39
FD8B   E6 51      INC $51
FD8D   A5 51      LDA $51
FD8F   C9 24      CMP #$24
FD91   B0 03      BCS $FD96
FD93   4C 9C F9   JMP $F99C
FD96   A9 FF      LDA #$FF
FD98   85 51      STA $51
FD9A   A9 00      LDA #$00
FD9C   85 50      STA $50
FD9E   A9 01      LDA #$01
FDA0   4C 69 F9   JMP $F969

; write SYNC 10240 times, erase track

FDA3   AD 0C 1C   LDA $1C0C
FDA6   29 1F      AND #$1F
FDA8   09 C0      ORA #$C0
FDAA   8D 0C 1C   STA $1C0C
FDAD   A9 FF      LDA #$FF
FDAF   8D 03 1C   STA $1C03
FDB2   8D 01 1C   STA $1C01
FDB5   A2 28      LDX #$28
FDB7   A0 00      LDY #$00
FDB9   50 FE      BVC $FDB9
FDBB   B8         CLV
FDBC   88         DEY
FDBD   D0 FA      BNE $FDB9
FDBF   CA         DEX
FDC0   D0 F7      BNE $FDB9
FDC2   60         RTS

; read/write ($621/$622) times

FDC3   AE 21 06   LDX $0621
FDC6   AC 22 06   LDY $0622
FDC9   50 FE      BVC $FDC9
FDCB   B8         CLV
FDCC   CA         DEX
FDCD   D0 FA      BNE $FDC9
FDCF   88         DEY
FDD0   10 F7      BPL $FDC9
FDD2   60         RTS

; attempt counter for formatting

FDD3   CE 20 06   DEC $0620
FDD6   F0 03      BEQ $FDDB
FDD8   4C 9C F9   JMP $F99C
FDDB   A0 FF      LDY #$FF
FDDD   84 51      STY $51
FDDF   C8         INY
FDE0   84 50      STY $50
FDE2   4C 69 F9   JMP $F969

;

FDE5   B9 00 03   LDA $0300,Y
FDE8   99 45 03   STA $0345,Y
FDEB   88         DEY
FDEC   D0 F7      BNE $FDE5
FDEE   AD 00 03   LDA $0300
FDF1   8D 45 03   STA $0345
FDF4   60         RTS

; copy data from overflow buffer

FDF5   A0 44      LDY #$44
FDF7   B9 BB 01   LDA $01BB,Y
FDFA   91 30      STA ($30),Y
FDFC   88         DEY
FDFD   10 F8      BPL $FDF7
FDFF   60         RTS

; switch to reading

FE00   AD 0C 1C   LDA $1C0C
FE03   09 E0      ORA #$E0
FE05   8D 0C 1C   STA $1C0C
FE08   A9 00      LDA #$00
FE0A   8D 03 1C   STA $1C03
FE0D   60         RTS

; write $55 10240 times

FE0E   AD 0C 1C   LDA $1C0C
FE11   29 1F      AND #$1F
FE13   09 C0      ORA #$C0
FE15   8D 0C 1C   STA $1C0C
FE18   A9 FF      LDA #$FF
FE1A   8D 03 1C   STA $1C03
FE1D   A9 55      LDA #$55
FE1F   8D 01 1C   STA $1C01
FE22   A2 28      LDX #$28
FE24   A0 00      LDY #$00
FE26   50 FE      BVC $FE26
FE28   B8         CLV
FE29   88         DEY
FE2A   D0 FA      BNE $FE26
FE2C   CA         DEX
FE2D   D0 F7      BNE $FE26
FE2F   60         RTS

; convert header in buffer 0 to GCR code

FE30   A9 00      LDA #$00
FE32   85 30      STA $30
FE34   85 2E      STA $2E
FE36   85 36      STA $36
FE38   A9 BB      LDA #$BB
FE3A   85 34      STA $34
FE3C   A5 31      LDA $31
FE3E   85 2F      STA $2F
FE40   A9 01      LDA #$01
FE42   85 31      STA $31
FE44   A4 36      LDY $36
FE46   B1 2E      LDA ($2E),Y
FE48   85 52      STA $52
FE4A   C8         INY
FE4B   B1 2E      LDA ($2E),Y
FE4D   85 53      STA $53
FE4F   C8         INY
FE50   B1 2E      LDA ($2E),Y
FE52   85 54      STA $54
FE54   C8         INY
FE55   B1 2E      LDA ($2E),Y
FE57   85 55      STA $55
FE59   C8         INY
FE5A   F0 08      BEQ $FE64
FE5C   84 36      STY $36
FE5E   20 D0 F6   JSR $F6D0
FE61   4C 44 FE   JMP $FE44
FE64   4C D0 F6   JMP $F6D0

; interrupt routine

FE67   48         PHA
FE68   8A         TXA
FE69   48         PHA
FE6A   98         TYA
FE6B   48         PHA
FE6C   AD 0D 18   LDA $180D
FE6F   29 02      AND #$02
FE71   F0 03      BEQ $FE76
FE73   20 53 E8   JSR $E853
FE76   AD 0D 1C   LDA $1C0D
FE79   0A         ASL
FE7A   10 03      BPL $FE7F
FE7C   20 B0 F2   JSR $F2B0
FE7F   68         PLA
FE80   A8         TAY
FE81   68         PLA
FE82   AA         TAX
FE83   68         PLA
FE84   40         RTI


FE85   .BY $12
FE86   .BY $04
FE87   .BY $04
FE88   .BY $90
FE89   .BY $56,$49,$44,$4D,$42,$55
FE8F   .BY $50,$26,$43,$52,$53,$4E
FE95   .BY $84,$05,$C1,$F8,$1B,$5C
FE9B   .BY $07,$A3,$F0,$88,$23,$0D
FEA1   .BY $ED,$D0,$C8,$CA,$CC,$CB
FEA7   .BY $E2,$E7,$C8,$CA,$C8,$EE
FEAD   .BY $51,$DD,$1C,$9E,$1C
FEB2   .BY $52,$57,$41,$4D
FEB6   .BY $44,$53,$50,$55,$4C
FEBB   .BY $44,$53,$50,$55,$52
FEC0   .BY $45,$45,$52,$53,$45
FEC5   .BY $4C,$51,$47,$52,$4C
FECA   .BY $08,$00,$00
FECD   .BY $3F,$7F,$BF,$FF
FED1   .BY $11,$12,$13,$15
FED5   .BY $41
FED6   .BY $04
FED7   .BY $24
FED8   .BY $1F,$19,$12
FEDB   .BY $01,$FF,$FF,$01,$00
FEE0   .BY $03,$04,$05,$06,$07,$07
FEE6   .BY $3E


; from UI command $EB22, to reset without RAM/ROM test

FEE7   6C 65 00   JMP ($0065)

; patch for diagnostic routine from $EA7A

FEEA   8D 00 1C   STA $1C00
FEED   8D 02 1C   STA $1C02
FEF0   4C 7D EA   JMP $EA7D

; delay loop for serial bus in 1541 mode, from $E97D

FEF3   8A         TXA
FEF4   A2 05      LDX #$05
FEF6   CA         DEX
FEF7   D0 FD      BNE $FEF6
FEF9   AA         TAX
FEFA   60         RTS

; patch for data output to serial bus, from $E980

FEFB   20 AE E9   JSR $E9AE
FEFE   4C 9C E9   JMP $E99C

; U9 vector, switch 1540/1541

FF01   AD 02 02   LDA $0202
FF04   C9 2D      CMP #$2D
FF06   F0 05      BEQ $FF0D
FF08   38         SEC
FF09   E9 2B      SBC #$2B
FF0B   D0 DA      BNE $FEE7
FF0D   85 23      STA $23
FF0F   60         RTS

; patch for reset routine, from $EAA4

FF10   8E 03 18   STX $1803
FF13   A9 02      LDA #$02
FF15   8D 00 18   STA $1800
FF18   A9 1A      LDA #$1A
FF1A   8D 02 18   STA $1802
FF1D   4C A7 EA   JMP $EAA7

; patch for listen to serial bus, from $E9DC

FF20   AD 00 18   LDA $1800
FF23   29 01      AND #$01
FF25   D0 F9      BNE $FF20
FF27   A9 01      LDA #$01
FF29   8D 05 18   STA $1805
FF2C   4C DF E9   JMP $E9DF

; unused

FF2F   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF37   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF3F   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF47   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF4F   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF57   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF5F   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF67   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF6F   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF77   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF7F   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF87   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF8F   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF97   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FF9F   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FFA7   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FFAF   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FFB7   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FFBF   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FFC7   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FFCF   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FFD7   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA,$AA
FFDF   .BY $AA,$AA,$AA,$AA,$AA,$AA,$AA

FFE6   .WD $C8C6   ; format
FFE8   .WD $F98F   ; turn motor off

; USER vectors

FFEA   .WD $CD5F
FFEC   .WD $CD97
FFEE   .WD $0500
FFF0   .WD $0503
FFF2   .WD $0506
FFF4   .WD $0509
FFF6   .WD $050C
FFF8   .WD $050F
FFFA   .WD $FF01

; hardware vectors

FFFC   .WD $EAA0   ; RESET
FFFE   .WD $FE67   ; IRQ

*/

	private byte[] discImageBuffer;

	public VC1541(boolean debug) {
		this.debug = debug;
	}

	public void loadImage(File file) {
	    String fileName = file.getName();
		long d64Size = file.length();

		try {
			// read in the data
			System.out.println("[VC1541] loadImage(); Reading Image " + fileName + ", size=" + d64Size);
            discImageBuffer = new byte[(int)file.length()];
            IOUtils.read(new FileInputStream(file), discImageBuffer);
			directory = new VC1541Directory(getSector(DIR_TRACK, DIR_SECTOR));
			
		} catch(IOException ex) {
			System.out.println("[VC1541] loadImage(); failure " + ex.getMessage());
		}
	}
	
	public void loadPrg(String fileName, int adress) {
		Memory memory = MemoryMap.findSegment(adress);
		if(!(memory instanceof Ram)) {
			System.err.println("[VC1541] loadPrg(); memory at adress " + adress + " is Read-Only");
			return;
		}		
		Ram ram = (Ram)memory;
		
		File file = new File(fileName);
		long d64Size = file.length();

		try {
			// read in the data
			System.out.println("[VC1541] loadPrg(); Reading PRG " + fileName + ", size=" + d64Size);
            discImageBuffer = new byte[(int)d64Size];
            IOUtils.read(new FileInputStream(file), discImageBuffer);

			System.arraycopy(discImageBuffer, 0, ram.getMemBuffer(), 0, (int)d64Size);
		} catch(IOException ex) {
			System.out.println("[VC1541] loadPrg(); failure " + ex.getMessage());
		}
	}

    public static int toUnsignedInt(byte value) {
        return (value & 0x7F) + (value < 0 ? 128 : 0);
    }

    public byte[] readBAMBlock() {
		return getSector(BAM_TRACK, BAM_SECTOR);
	}

	public VC1541Directory getDirectory() {
		return directory;
	}

	public int loadFile(VC1541File file, Ram ram) {
		int track = file.getStartTrack();
		int sector = file.getStartSector();
		for(int b=0; b < file.getNumberOfBlocks(); b++) {
			byte[] block = getSector(track, sector);
			System.arraycopy(block, 2, ram.getMemBuffer(), b*254, 254);
			// prepare for next block 
			track = toUnsignedInt(block[0]);
			sector = toUnsignedInt(block[1]);
			if(track == 0 && sector == 0)
				break;
		}
		return file.getNumberOfBlocks()*(BLOCK_SIZE-2);
	}
	
	public int loadFile(String fileName, int adress) {
		
		Memory memory = MemoryMap.findSegment(adress);
		if(!(memory instanceof Ram)) {
			System.err.println("[VC1541] loadFile(); memory at adress " + adress + " is Read-Only");
			return 0;
		}		
		Ram ram = (Ram)memory;
		System.out.println("[VC1541] loadFile(); writable memory segment defined for adress " + adress + " (Type=" + ram.getDesc() + ")");
		for(int i=0; i < getDirectory().getSize(); i++) {
			System.out.println("[VC1541] loadFile(); fileName=" + fileName + ", adress=" + adress);
			VC1541File file = getDirectory().getEntry(i);
			System.out.println("[VC1541] loadFile(); Directory-Entry[" + i + "]=" + file.getFileName());
			if(fileName.endsWith("*")) {
				System.out.println("[VC1541] loadFile(); Loading file using wildcard(*)");
				if(file.getFileName().substring(0,fileName.length()-1).equalsIgnoreCase(fileName.substring(0,fileName.length()-1))) {
					System.out.println("[VC1541] loadFile(); Loading file " +  file.getFileName());
					return loadFile(file, ram);
				}
			}
			if(file.getFileName().equalsIgnoreCase(fileName)) {
				System.out.println("[VC1541] loadFile(); Loading file \"" + (i+1) + "\": " + file.getFileName() + ", " + file.getNumberOfBlocks());
				return loadFile(file, ram);
			}
		}
		// file not found
		System.out.println("[VC1541] loadFile(); File \"" + fileName + "\" not found");
		return 0;
	}

	public int loadFileToBuffer(VC1541File file, byte[] buffer) {
		int track = file.getStartTrack();
		int sector = file.getStartSector();
		for(int b=0; b < file.getNumberOfBlocks(); b++) {
			byte[] block = getSector(track, sector);
			System.arraycopy(block, 2, buffer, b*254, 254);
			// prepare for next block
			track = toUnsignedInt(block[0]);
			sector = toUnsignedInt(block[1]);
			if(track == 0 && sector == 0)
				break;
		}
		return file.getNumberOfBlocks()*(BLOCK_SIZE-2);
	}

	public byte[] loadFile(VC1541File file) {
		byte[] fileBuffer = new byte[file.getNumberOfBlocks() * BLOCK_SIZE];
		System.out.println("[VC1541] loadFile(); Loading file " + file.getFileName() + ", " + file.getNumberOfBlocks());
		int numberOfBlocksRead = loadFileToBuffer(file, fileBuffer);
		return fileBuffer;
	}

	public byte[] getSector(int track, int sector) {

		int blockOffset = 0;
		if(debug)
			System.out.println("[VC1541] getSector(); reading Track " + track + ", Sector " + sector);
		if(discImageBuffer.length == 0) {
			System.err.println("[VC1541] getSector(); no image loaded");
			return null;
		}
		while(true) {
			if(track > 17) {
				blockOffset += 17*21*BLOCK_SIZE;
			} else {
				blockOffset += ((track-1)*21*BLOCK_SIZE)+(sector*BLOCK_SIZE);
				break;
			}
			if(track > 24) {
				blockOffset += 7*19*BLOCK_SIZE;
			} else {
				blockOffset += ((track-17-1)*19*BLOCK_SIZE)+(sector*BLOCK_SIZE);
				break;
			}
			if(track > 30) {
				blockOffset += 6*18*BLOCK_SIZE;
			} else {
				blockOffset += ((track-24-1)*18*BLOCK_SIZE)+(sector*BLOCK_SIZE);
				break;
			}
			if(track > 35) {
				blockOffset += 5*17*BLOCK_SIZE;
			} else {
				blockOffset += ((track-35-1)*17*BLOCK_SIZE)+(sector*BLOCK_SIZE);
				break;
			}
		}
		
		byte[] result = new byte[BLOCK_SIZE];
		System.arraycopy(discImageBuffer, blockOffset, result, 0, BLOCK_SIZE);
		return result;
	}
}
