package edu.wpi.teamA.modules;

import java.io.*;
import javafx.concurrent.Task;
import javax.sound.sampled.*;

/**
 * A sample program is to demonstrate how to record sound in Java adapted from:
 * http://www.codejava.net/coding/capture-and-record-sound-into-wav-file-with-java-sound-api
 */
public class JavaSoundRecorder extends Task<Void> {

  // record duration, in milliseconds
  public static final long RECORD_TIME = 6000; // 6 seconds

  // path of the wav file
  File wavFile =
      new File(
          this.getClass().getResource("/edu/wpi/teamA/assets/sounds/recordedVoice.wav").getPath());

  // format of audio file
  AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

  // the line from which audio data is captured
  TargetDataLine line;

  @Override
  protected Void call() throws Exception {
    try {
      wavFile.createNewFile();
      AudioFormat format = getAudioFormat();
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

      // checks if system supports the data line
      if (!AudioSystem.isLineSupported(info)) {
        System.out.println("Line not supported");
        System.exit(0);
      }
      line = (TargetDataLine) AudioSystem.getLine(info);
      line.open(format);
      line.start(); // start capturing

      System.out.println("Start capturing...");

      AudioInputStream ais = new AudioInputStream(line);

      System.out.println("Start recording...");

      // start recording
      AudioSystem.write(ais, fileType, wavFile);

    } catch (LineUnavailableException | IOException ex) {
      ex.printStackTrace();
    }

    return null;
  }

  /** Defines an audio format */
  AudioFormat getAudioFormat() {
    float sampleRate = 16000;
    int sampleSizeInBits = 8;
    int channels = 2;
    boolean signed = true;
    boolean bigEndian = true;
    AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    return format;
  }

  /** Closes the target data line to finish capturing and recording */
  public void finish() {
    line.stop();
    line.close();
    System.out.println("Finished");
  }
}
