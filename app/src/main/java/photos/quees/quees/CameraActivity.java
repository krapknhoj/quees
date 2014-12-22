package photos.quees.quees;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    public void takePicture(View v) {
        camera.takePicture(shutterCallback, null, pictureCallback);
    }

    protected Camera        camera;
    protected SurfaceView   surfaceView;
    protected SurfaceHolder surfaceHolder;
    protected Button        cameraButton;
    private File            path;
    private int             i = 0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        /**
         * Get the external storage directory path Generally /mnt/sdcard/
         */
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        path = cw.getDir("imgDir", Context.MODE_PRIVATE);

        /**
         * Get the SurfaceView which is defined in the resources file using the
         * resource ID
         */
        surfaceView = (SurfaceView) findViewById(R.id.surface_camera);

        /**
         * Surface holder allows us to control the changes to the surface. A
         * Callback interface is registered in order to control changes to the
         * surface.
         */
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        /**
         * A Button is provided, on click which the image is captured An image
         * button is used here which allows to specify an image for the button
         * instead of plain text. The button is defined in the resource file is
         * referenced using the resource ID.
         */
        cameraButton = (Button) findViewById(R.id.camera_button);
    }

    /**
     * This callback can be used to play shutter sound and such since this
     * callback method is called when the image is captured by the sensor
     */
    ShutterCallback shutterCallback = new ShutterCallback() {
        @Override
        public void onShutter() {
            Log.v("Camera", "pictureCallback");
        }
    };

    /**
     * The image data that is available after image capture is supplied using
     * this callback interface. The image data is available in form of bytes[],
     * the format of which depends on the context of the callback. This can be
     * converted to any formats using Bitmap or BitmapFactory classes.
     */
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        String fileName = new SimpleDateFormat("yyyyMMddhhmmss'.jpg'").format(new Date());
        private String saveToInternalStorage(Bitmap bitmapImage) {
            File finalPath = new File(path, fileName);
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(finalPath);
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return finalPath.getAbsolutePath();
        }

        @Override
        public void onPictureTaken(final byte[] data, Camera c) {

            /**
             * This callback method is
             * called when the data is
             * available after capture of
             * the image.
             */
            Log.v("Camera", "pictureCallback");

            Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
            String finalPath = saveToInternalStorage(picture);

            Intent intent = new Intent(CameraActivity.this, AddActivity.class);
            intent.putExtra("selectedItemId", 0);
            intent.putExtra("imgPath", finalPath);
            intent.putExtra("imgFileName", fileName);

            startActivity(intent);
        }
    };

    /**
     * This callback is called immediately after the surface is first created.
     * Obtain an instance of the camera when the surface is created.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
    }

    /**
     * This callback is called when there are any structural changes to the
     * surface.
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        View root = findViewById(R.id.root);
        camera.stopPreview();

        /**
         * Camera parameters can be modified by modifying the object that is
         * returned by getParameters()
         */
        Camera.Parameters p = camera.getParameters();

        /**
         * Determine supported preview sizes and and set the preview size which
         * best matches the device's screen resolution.
         */
        List<Size> supportedSizes = p.getSupportedPreviewSizes();
        Size previewSize = determinePreviewSize(supportedSizes, root.getWidth(), root.getHeight());

        /**
         * Switch camera to portrait mode. TODO: Handle upside down orientations
         * properly
         */
        if (root.getWidth() < root.getHeight()) {
            camera.setDisplayOrientation(90);
        }
        p.setPreviewSize(previewSize.width, previewSize.height);

        p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.setParameters(p);

        try {
            /**
             * setPreviewDisplay() must be called before calling startPreview()
             * This method sets the surface required for the camera preview. A
             * fully initialized SurfaceHolder must be passed to this method.
             */
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * Layout the surfaceView correctly. It might not cover the whole
         * screen, depending in the screen's and camera's aspect ratio.
         */
        float previewAspectRatio = (float) (previewSize.width) / previewSize.height;
        if (root.getWidth() > root.getHeight()) {
            surfaceView.layout(0, 0, (int) (root.getHeight() * previewAspectRatio),
                    root.getHeight());
        } else {
            surfaceView.layout(0, 0, root.getWidth(), (int) (root.getWidth() * previewAspectRatio));
        }

        /**
         * Starts the live preview required for camera
         */
        camera.startPreview();
    }

    /**
     * This callback is called just before the surface is being destroyed. Since
     * camera is a shared resource it is good practice to release the resource
     * when not using it.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("Camera", "surfaceDestroyed");
        camera.stopPreview();
        camera.release();
    }

    /**
     * Determine preview size based on the supported preview sizes. The preview
     * size is the maximum preview size supported which is less or equal to the
     * device's screen resolution. To test the best matching size both portrait
     * and landscape screen orientations are tested.
     *
     * @param sizes
     *            The preview sizes supported by the device's camera.
     *
     * @param width
     *            The device's screen width.
     *
     * @param height
     *            The device's screen height.
     *
     * @return The determined preview size.
     */
    private Size determinePreviewSize(List<Size> sizes, int width, int height) {
        if (height > width) {
            int temp = width;
            width = height;
            height = temp;
        }

        for (Size s : sizes) {
            if (s.width <= width && s.height <= height) {
                return s;
            }

            if (s.width <= height && s.height <= width) {
                return s;
            }
        }
        return null;
    }
}
