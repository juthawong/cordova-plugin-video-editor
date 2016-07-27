package org.apache.cordova.videoeditor;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.*;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * StreamGobbler
 * Catching process errors
 */
class StreamGobbler extends Thread {
    InputStream is;
    String type;

    StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
                Log.d("Stream", type + ">" + line);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

/**
 * VideoEditor
 * Editing of audio and video files
 */
public class VideoEditor extends CordovaPlugin {

    private static final String TAG = "VideoEditor";
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private String ffmpegPath;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        Log.d(TAG, "cordova: " + cordova);

        if (action.equals("example")) {
            try {
                Context appContext = cordova.getActivity().getApplicationContext();
                int resourceId = cordova.getActivity().getResources().getIdentifier("ffmpeg", "raw", appContext.getPackageName());
                File ffmpegFile = new File(appContext.getCacheDir(), "ffmpeg");
                ffmpegPath = ffmpegFile.getCanonicalPath();
                if (!ffmpegFile.exists()) {
                    ffmpegFile.createNewFile();
                    installBinaryFromRaw(appContext, resourceId, ffmpegFile);
                    Log.d(TAG, "VideoEditor newly installed at: " + ffmpegPath);
                } else {
                    Log.d(TAG, "VideoEditor already installed: " + ffmpegPath);
                }
                ffmpegFile.setExecutable(true);

                // original version
//                String[] cmdline = { "ls", "-l" };
//                //String[] cmdline = { ffmpegPath, "-i", "/storage/emulated/0/Movies/intro.mp4"};
//                Runtime rt = Runtime.getRuntime();
//                Process ps = rt.exec(cmdline);
//                StreamGobbler errorGobbler = new StreamGobbler(ps.getErrorStream(), "ERROR");
//                StreamGobbler outputGobbler = new StreamGobbler(ps.getInputStream(), "OUTPUT");
//                errorGobbler.start();
//                outputGobbler.start();
//                try {
//                    int exitVal = ps.waitFor();
//                    Log.d(TAG, "VideoEditor.success: " + exitVal);
//                } catch(InterruptedException e) {
//                    Log.e(TAG, "InterruptedException: " + e);
//                } finally {
//                    if (ps != null) {
//                        ps.getOutputStream().close();
//                        ps.getInputStream().close();
//                        ps.getErrorStream().close();
//                    }
//                }

                // ls directories has output
                //String[] cmdline = { "ls", "-l" };

                // ffmpeg doesn't, why?
                //String[] cmdline = { ffmpegPath, "-i", "/storage/emulated/0/Movies/intro.mp4", "/storage/emulated/0/Movies/intro2.mpg"};

                // another version
                String[] cmdline = { ffmpegPath, "-version" };

                Runtime rt = Runtime.getRuntime();
                ProcessBuilder pb = new ProcessBuilder(cmdline);
		        pb.directory(ffmpegFile.getParentFile());
                Process ps = pb.start();
                StreamGobbler errorGobbler = new StreamGobbler(ps.getErrorStream(), "ERROR");
                StreamGobbler outputGobbler = new StreamGobbler(ps.getInputStream(), "OUTPUT");
                errorGobbler.start();
                outputGobbler.start();
                try {
                    int exitVal = ps.waitFor();
                    Log.d(TAG, "VideoEditor.success: " + exitVal);
                    callbackContext.success(exitVal);
                } catch(InterruptedException e) {
                    Log.e(TAG, "InterruptedException: " + e);
                    callbackContext.error(e.getMessage());
                }
                return true;
            } catch(IOException e) {
                Log.d(TAG, "IOException: " + e);
                callbackContext.error(e.getMessage());
            }
        } else {
            Log.d(TAG, "Invalid action: " + action);
            callbackContext.error("Invalid action: " + action);
            return false;
        }
        return false;
    }

    public static void installBinaryFromRaw(Context context, int resId, File file) {
        final InputStream rawStream = context.getResources().openRawResource(resId);
        final OutputStream binStream = getFileOutputStream(file);

        if (rawStream != null && binStream != null) {
            pipeStreams(rawStream, binStream);

            try {
                rawStream.close();
                binStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close streams!", e);
            }

            doChmod(file, 777);
        }
    }

    public static OutputStream getFileOutputStream(File file) {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found attempting to stream file.", e);
        }
        return null;
    }

    public static void pipeStreams(InputStream is, OutputStream os) {
        byte[] buffer = new byte[IO_BUFFER_SIZE];
        int count;
        try {
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error writing stream.", e);
        }
    }

    public static void doChmod(File file, int chmodValue) {
        final StringBuilder sb = new StringBuilder();
        sb.append("chmod");
        sb.append(' ');
        sb.append(chmodValue);
        sb.append(' ');
        sb.append(file.getAbsolutePath());

        try {
            Runtime.getRuntime().exec(sb.toString());
        } catch (IOException e) {
            Log.e(TAG, "Error performing chmod", e);
        }
    }
}