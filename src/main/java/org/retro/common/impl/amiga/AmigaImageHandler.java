package org.retro.common.impl.amiga;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.io.IOUtils;
import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;
import org.retro.common.impl.AbstractBaseImageHandler;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Handler for Commodore Amiga ADF disk images.
 *
 * NOT IMPLEMENTED YET
 *
 * @author Marcel Schoen
 */
public class AmigaImageHandler extends AbstractBaseImageHandler {

    Invocable invocable;

    @Override
    public VirtualDisk loadImage(File imageFile) throws VirtualDiskException {
        String diskName = imageFile.getName();

        ScriptEngine engine = null;
        try {
            NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
            engine = factory.getScriptEngine(new String[] { "--language=es6" });

//            System.setProperty("nashorn.args", "--language=es6");
//            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            loadJavaScript(engine, "/adf-reader/javascript/adf.js");
            loadJavaScript(engine, "/adf-reader/javascript/file.js");
            loadJavaScript(engine, "/adf-reader/javascript/util.js");
            /*
            loadJavaScript(engine, "/adf-reader/javascript/main.js");
*/
            this.invocable = (Invocable) engine;

        } catch(Exception e) {
            throw new VirtualDiskException("Failed to initialize JavaScript code: " + e, e);
        }

        try {
            byte[] diskData = new byte[(int)imageFile.length()];
            IOUtils.read(new FileInputStream(imageFile), diskData);
            System.out.println("Image data size: " + diskData.length);

            Object adf = engine.eval("adf");
            Object result;

            this.invocable.invokeFunction("load", diskData);
//            this.invocable.invokeMethod(adf, "loadDisk", diskData);

            result = this.invocable.invokeMethod(adf, "getInfo");

            System.out.println("Result: " + result == null ? "null" : result.getClass().getName());
            ScriptObjectMirror mirror = (ScriptObjectMirror)result;
            System.out.println("Result empty: " + mirror.isEmpty());
            mirror.keySet().stream().forEach(k -> System.out.println("Key: " + k + " = " + mirror.get(k)));
        } catch(Exception e) {
            throw new VirtualDiskException("Failed to execute JavaScript code: " + e, e);
        }

        AmigaVirtualDisk virtualDisk = new AmigaVirtualDisk(diskName);
        VirtualDirectory root = new VirtualDirectory("/");
        virtualDisk.setRootContent(root);

        return virtualDisk;
    }

    private void loadJavaScript(ScriptEngine engine, String scriptName) throws Exception {
        InputStream in = AmigaImageHandler.class.getResourceAsStream(scriptName);
        engine.eval(new InputStreamReader(in));
    }
}
