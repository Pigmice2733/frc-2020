package com.pigmice.frc.robot;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;

public class Vision {
    private static class SerialConfiguration {
        private static final int BAUD_RATE = 115200;
        private static final char TERMINATOR = '\n';
        private static final SerialPort.Port PORT = SerialPort.Port.kUSB1;

        private static final String START_DELIMITER = "[";
        private static final String END_DELIMITER = "]";
        private static final String DATA_SEPARATOR = ",";
        private static final String NULL_RESPONSE = "NONE";

        private static final int BUFFER_SIZE = 24;
    }

    private static boolean enabled = false;
    private static double lastInitAttempt = 0.0;
    private static double initAttemptPeriod = 1.0;

    private static SerialPort port = null;
    private static Notifier thread = new Notifier(Vision::update);

    private static volatile double targetDistance = 0.0;
    private static volatile double targetAngle = 0.0;
    private static volatile boolean targetVisible = false;

    private static StringBuilder readBuffer = new StringBuilder(SerialConfiguration.BUFFER_SIZE);

    public static void startProcessing() {
        if (!enabled) {
            enabled = true;

            thread.startPeriodic(1.0 / 30.0);
        }
    }

    public static double getAngle() {
        return targetAngle;
    }

    public static double getDistance() {
        return targetDistance;
    }

    public static boolean targetVisible() {
        return targetVisible;
    }

    private static void update() {
        if (port == null) {
            initPort();
            return;
        }

        readBuffer.append(port.readString());

        try {
            parseInput();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void parseInput() {
        int startIndex = readBuffer.lastIndexOf(SerialConfiguration.START_DELIMITER);
        int separatorIndex = readBuffer.lastIndexOf(SerialConfiguration.DATA_SEPARATOR);
        int endIndex = readBuffer.lastIndexOf(SerialConfiguration.END_DELIMITER);

        if((endIndex - startIndex) == (SerialConfiguration.NULL_RESPONSE.length() + 1)) {
            if(readBuffer.substring(startIndex + 1, endIndex).equals(SerialConfiguration.NULL_RESPONSE)) {
                Vision.targetVisible = false;
                return;
            }
        }

        if (endIndex > startIndex && startIndex > -1) {
            String distanceString = readBuffer.substring(startIndex + 1, separatorIndex);
            String angleString = readBuffer.substring(separatorIndex + 1, endIndex);

            targetDistance = Double.valueOf(distanceString);
            targetAngle = Double.valueOf(angleString);

            targetVisible = true;

            readBuffer.delete(0, endIndex + 1);
            return;
        }
    }

    private static void initPort() {
        double now = Timer.getFPGATimestamp();
        if((now - lastInitAttempt) < initAttemptPeriod) {
            return;
        }

        lastInitAttempt = now;
        try {
            port = new SerialPort(SerialConfiguration.BAUD_RATE, SerialConfiguration.PORT);

            port.enableTermination(SerialConfiguration.TERMINATOR);
            readBuffer.delete(0, SerialConfiguration.BUFFER_SIZE);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
