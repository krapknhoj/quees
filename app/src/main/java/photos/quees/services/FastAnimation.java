package photos.quees.services;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

public class FastAnimation {
    private class AnimationFrame {
        private int mResourceId;
        private int mDuration;

        AnimationFrame(int resourceId, int duration) {
            mResourceId = resourceId;
            mDuration = duration;
        }

        public int getResourceId() {
            return mResourceId;
        }
        public int getDuration() {
            return mDuration;
        }
    }
    private ArrayList<AnimationFrame> mAnimationFrames;
    private int mIndex;

    private boolean mShouldRun;
    private boolean mIsRunning;
    private SoftReference<ImageView> mSoftReferenceImageView;
    private Handler mHandler;
    private OnAnimationStoppedListener mOnAnimationStoppedListener;
    private OnAnimationFrameChangedListener mOnAnimationFrameChangedListener;

    private FastAnimation(ImageView imageView) {
        init(imageView);
    };

    private static FastAnimation sInstance;

    public static FastAnimation getInstance(ImageView imageView) {
        if (sInstance == null)
            sInstance = new FastAnimation(imageView);
        return sInstance;
    }

    public void init(ImageView imageView) {
        mAnimationFrames = new ArrayList<AnimationFrame>();
        mSoftReferenceImageView = new SoftReference<ImageView>(imageView);

        mHandler = new Handler();
        if(mIsRunning == true) {
            stop();
        }

        mShouldRun = false;
        mIsRunning = false;

        mIndex = -1;
    }

    public void addFrame(int index, int resId, int interval) {
        mAnimationFrames.add(index, new AnimationFrame(resId, interval));
    }

    public void addFrame(int resId, int interval) {
        mAnimationFrames.add(new AnimationFrame(resId, interval));
    }

    public void addAllFrames(int[] resIds, int interval) {
        for(int resId : resIds){
            mAnimationFrames.add(new AnimationFrame(resId, interval));
        }
    }

    public void removeFrame(int index){
        mAnimationFrames.remove(index);
    }

    public void removeAllFrames(){
        mAnimationFrames.clear();
    }

    public void replaceFrame(int index, int resId, int interval) {
        mAnimationFrames.set(index, new AnimationFrame(resId, interval));
    }

    private AnimationFrame getNext() {
        mIndex++;
        if (mIndex >= mAnimationFrames.size())
            mIndex = 0;
        return mAnimationFrames.get(mIndex);
    }

    public interface OnAnimationStoppedListener {
        public void onAnimationStopped();
    }

    public interface OnAnimationFrameChangedListener {
        public void onAnimationFrameChanged(int index);
    }

    public void setOnAnimationStoppedListener(OnAnimationStoppedListener listener) {
        mOnAnimationStoppedListener = listener;
    }

    public void setOnAnimationFrameChangedListener(OnAnimationFrameChangedListener listener){
        mOnAnimationFrameChangedListener = listener;
    }

    public synchronized void start() {
        mShouldRun = true;
        if (mIsRunning)
            return;
        mHandler.post(new FramesSequenceAnimation());
    }

    public synchronized void stop() {
        mShouldRun = false;
    }

    private class FramesSequenceAnimation implements Runnable {

        @Override
        public void run() {
            ImageView imageView = mSoftReferenceImageView.get();
            if (!mShouldRun || imageView == null) {
                mIsRunning = false;
                if (mOnAnimationStoppedListener != null) {
                    mOnAnimationStoppedListener.onAnimationStopped();
                }
                return;
            }
            mIsRunning = true;

            if (imageView.isShown()) {
                AnimationFrame frame = getNext();
                GetImageDrawableTask task = new GetImageDrawableTask(imageView);
                task.execute(frame.getResourceId());
                // TODO postDelayed after onPostExecute
                mHandler.postDelayed(this, frame.getDuration());
            }
        }
    }

    private class GetImageDrawableTask extends AsyncTask<Integer, Void, Drawable> {

        private ImageView mImageView;

        public GetImageDrawableTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Drawable doInBackground(Integer... params) {
            return mImageView.getContext().getResources().getDrawable(params[0]);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            if(result!=null) mImageView.setImageDrawable(result);
            if (mOnAnimationFrameChangedListener != null)
                mOnAnimationFrameChangedListener.onAnimationFrameChanged(mIndex);
        }

    }
}