# retro-io
Java library and tool for reading and extracting floppy disk images of 8- and 16-bit computers

## Description

The goal of this Java library is to allow to read, extract the contents of, and eventually also write various 
legacy floppy disk image formats of old 8- and 16-bit computer systems such as Commodore 64, Atari XL, ST or Amiga.

Most of the actual image processing code will not be implemented in this project; it uses other, existing libraries
and code and bundles them in one library, with one common API. The "Credits" section below lists projects and people
whose code was used to implement this library.

As a simple proof-of-concept and utility, the code will come as an executable Jar and contain a simple GUI tool that
allows to load an image file and extract its contents.

## API / Usage

To use the library in your own code to read an image file, you need the following classes / interface:

* `ImageHandler` - a factory-provided implementation of a class that reads the image file
* `VirtualDisk` - a POJO representation of the disk's contents, as read by the `ImageHandler`
* `VirtualDirectory` - Allows to iterate through the disks' contents
* `VirtualFile` - Gives access to the details and contents of a file.

Example for reading an Atari ST "`.ST`" image file and accessing its root directory:

```File imageFile = new File("./SOMEGAME.ST");
ImageHandler handler = ImageHandlerFactory.get(ImageType.atarist_ST);
VirtualDisk stDisk = handler.loadImage(imageFile);
VirtualDirectory root = stDisk.getRootContents();
root.getContents().forEach(f -> System.out.println("Entry: " + f.getName() + ", is file: " + f.isFile()));
```

Then extract the contents with

```
handler.extractImage(stDisk, new File("/tmp/"));
```

## Credits

* https://github.com/waldheinz/fat32-lib - Great library for processing FAT images (DOS and Atari ST).
* https://github.com/steffest/ADF-reader - JavaScript ADF reader which I used as a template for my Java code.

## Status / Disk Image Format Support

Currently supported disk image formats are:

* MS DOS (.IMG)
* Atari ST (.ST, .MSA)
* Commodore Amiga (.ADF, partially; works with some images, not others)
* Commodore 64 (.D64)

Planned for the future / currently looking into:

* improved Commodore Amiga (.ADF)
* Atari XL (.ATR)
* Commodore 64 Tapes (.T64)

Future releases might include write support (at least for some formats). Of course any contributions to 
include support for any missing retro system floppy format is welcome.


## Build

This project requires Java 8 and Gradle to build it (I will probably add a Maven POM later too). 
To just compile it and run all the tests, run

```
$ gradle build
````

To build the fat, executable Jar with the extractor tool, run

```
$ gradle build shadowJar
````

Then run the extractor tool with this command:

```
$ java -jar build\libs\retro-io-1.0-SNAPSHOT-all.jar
````
