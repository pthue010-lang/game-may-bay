import javax.sound.sampled.*;
import java.io.*;

public class SoundEngine {

    private static Clip shootClip;
    private static Clip explosionClip;
    private static Clip bgClip;
    private static final float SAMPLE_RATE = 44100f;
    
    private static void playBuffer(byte[] buffer) {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            shootClip = createTone(700, 80);
            explosionClip = createNoise(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Clip createTone(int hz, int durationMs) throws Exception {
    float sampleRate = 44100f;
    byte[] buffer = new byte[(int)(durationMs * sampleRate / 1000)];

    for (int i = 0; i < buffer.length; i++) {
        double t = i / sampleRate;

        // sweep nhẹ từ cao xuống thấp (êm tai hơn)
        double freq = hz - t * 400;

        // sóng sin mềm
        double wave = Math.sin(2 * Math.PI * freq * t);

        // fade in + fade out để không gắt
        double fadeIn = Math.min(1.0, i / 200.0);
        double fadeOut = 1.0 - (double)i / buffer.length;
        double volume = fadeIn * fadeOut * 0.4; // giảm âm lượng tổng

        buffer[i] = (byte)(wave * 127 * volume);
    }

    return createClip(buffer, sampleRate);
}

    private static Clip createNoise(int durationMs) throws Exception {
        float sampleRate = 44100f;
        byte[] buffer = new byte[(int)(durationMs * sampleRate / 1000)];

        for (int i = 0; i < buffer.length; i++) {
            double volume = 1 - (double)i / buffer.length;
            buffer[i] = (byte)((Math.random() * 2 - 1) * 127 * volume);
        }

        return createClip(buffer, sampleRate);
    }

    private static Clip createClip(byte[] buffer, float sampleRate) throws Exception {
        AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
        Clip clip = AudioSystem.getClip();
        clip.open(format, buffer, 0, buffer.length);
        return clip;
    }

    public static void shoot() {
        if (shootClip.isRunning()) shootClip.stop();
        shootClip.setFramePosition(0);
        shootClip.start();
    }

    public static void explosion() {
        if (explosionClip.isRunning()) explosionClip.stop();
        explosionClip.setFramePosition(0);
        explosionClip.start();
    }

public static void playBackgroundMusic() {
    try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(
                SoundEngine.class.getResource("/sounds/background.wav"));

        bgClip = AudioSystem.getClip();
        bgClip.open(ais);
        bgClip.loop(Clip.LOOP_CONTINUOUSLY); // lặp vô hạn
        bgClip.start();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public static void stopBackgroundMusic() {
    if (bgClip != null && bgClip.isRunning()) {
        bgClip.stop();
    }
}

    private static void playNote(int hz, int durationMs) {
        byte[] buffer = new byte[(int)(durationMs * SAMPLE_RATE / 1000)];
        for (int i = 0; i < buffer.length; i++) {
            double t = i / SAMPLE_RATE;
            double wave1 = Math.sin(2 * Math.PI * hz * t);
            double wave2 = Math.sin(2 * Math.PI * hz * 0.5 * t);
            double tremolo = Math.sin(2 * Math.PI * 8 * t) * 0.3 + 0.7;
            buffer[i] = (byte)((wave1 * 0.7 + wave2 * 0.3) * 127 * tremolo * 0.4);
        }
        playBuffer(buffer);
    }
}