
function load(byteArray) {

    var buffer = new ArrayBuffer(byteArray.length);
    for (var i = 0; i < byteArray.length; i++) {
        buffer[i] = byteArray[i];
    }

    var filename = "/c/develop/idea-workspace/retro-io/src/test/resources/images/amiga/wbench1.3.adf";

    var reader = new FileReader();
    reader.readAsArrayBuffer(filename);
//    var contents = readFully(filename);

    adf.loadDisk(buffer);
    var info = adf.getInfo();
    print(">> info: " + info.diskType);

    var folder = adf.readRootFolder();

    print(">> folder name: " + folder.name);
    print(">> folder parent: " + folder.parent);


    print(">> Folders: " + folder.folders.length);

    return folder;
}
