function load(byteArray) {

    var buffer = new ArrayBuffer(byteArray.length);
    for(var i = 0; i < buffer.length; i++) {
        buffer[i] = byteArray[i];
    }
    print("*** HELLO ***");
    return adf.loadDisk(buffer);
}