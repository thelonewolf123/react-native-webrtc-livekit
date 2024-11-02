package com.oney.WebRTCModule.videoEffects;

import livekit.org.webrtc.SurfaceTextureHelper;
import livekit.org.webrtc.VideoFrame;
import livekit.org.webrtc.VideoProcessor;
import livekit.org.webrtc.VideoSink;

/**
 * Lightweight abstraction for an object that can receive video frames, process and add effects in
 * them, and pass them on to another object.
 */
public class VideoEffectProcessor implements VideoProcessor {
    private VideoSink mSink;
    final private SurfaceTextureHelper textureHelper;
    final private VideoFrameProcessor videoFrameProcessor;

    public VideoEffectProcessor(VideoFrameProcessor processor, SurfaceTextureHelper textureHelper) {
        this.textureHelper = textureHelper;
        this.videoFrameProcessor = processor;
    }

    @Override
    public void onCapturerStarted(boolean success) {}

    @Override
    public void onCapturerStopped() {}

    @Override
    public void setSink(VideoSink sink) {
        mSink = sink;
    }

    /**
     * Called just after the frame is captured.
     * Will process the VideoFrame with the help of VideoFrameProcessor and send the processed
     * VideoFrame back to webrtc using onFrame method in VideoSink.
     * @param frame raw VideoFrame received from webrtc.
     */
    @Override
    public void onFrameCaptured(VideoFrame frame) {
        frame.retain();
        VideoFrame outputFrame = videoFrameProcessor.process(frame, textureHelper);

        if (outputFrame == null) {
            mSink.onFrame(frame);
            frame.release();
            return;
        }
        mSink.onFrame(outputFrame);
        outputFrame.release();
        frame.release();
    }
}
