package com.pigmice.frc.robot.subsystems;

import com.pigmice.frc.robot.subsystems.SystemConfig.LEDConfiguration;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;

public class LEDs implements ISubsystem {
    private final AddressableLED leds;
    private final AddressableLEDBuffer buffer;

    private final int helixLengths = 38;
    private final int crossbarLength = 11;

    private final double tickTime = 0.25;

    private static LEDs instance = null;

    public static LEDs getInstance() {
        if(instance == null) {
            instance = new LEDs();
        }

        return instance;
    }

    private LEDs() {
        leds = new AddressableLED(LEDConfiguration.ledPort);
        buffer = new AddressableLEDBuffer(2*helixLengths + crossbarLength);

        leds.setLength(buffer.getLength());

        leds.start();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void updateDashboard() {
    }

    @Override
    public void updateInputs() {
    }

    @Override
    public void updateOutputs() {
        for (int i = 0; i < helixLengths/2; i++) {
            int offset = (int)((Timer.getFPGATimestamp() % (tickTime * helixLengths * 0.5))/tickTime);
            int value = i == offset ? 200 : 0;
            buffer.setHSV(i, 150, 255, value);
            buffer.setHSV(helixLengths - i, 150, 255, value);
            buffer.setHSV(i + helixLengths, 150, 255, value);
            buffer.setHSV(helixLengths - i + helixLengths, 150, 255, value);
        }

        for (int i = 0; i < crossbarLength; i++) {
            int offset = (int) ((Timer.getFPGATimestamp() % (tickTime * crossbarLength)) / tickTime);
            int value = i == offset ? 200 : 0;
            buffer.setHSV(i + 2*helixLengths, 150, 255, value);
        }

        leds.setData(buffer);
    }

    @Override
    public void test(double currentTestTime) {
    }
}
