package com.pigmice.frc.robot.subsystems;

import com.pigmice.frc.robot.subsystems.SystemConfig.LEDConfiguration;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;

public class LEDs implements ISubsystem {
    private final AddressableLED leds;
    private final AddressableLEDBuffer buffer;

    private static LEDs instance = null;

    public static LEDs getInstance() {
        if(instance == null) {
            instance = new LEDs();
        }

        return instance;
    }

    private LEDs() {
        leds = new AddressableLED(LEDConfiguration.ledPort);
        buffer = new AddressableLEDBuffer(LEDConfiguration.ledCount);

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
        final int length  = buffer.getLength();
        final int hue = 150;
        final double period = 10.0;

        int offset = (int)(((Timer.getFPGATimestamp() % period) / period) * length);

        for (int i = 0; i < length; i++) {
            buffer.setHSV(i, hue, 255, Math.abs(i - offset) < 2 ? 255 : 0);
        }

        leds.setData(buffer);
    }

    @Override
    public void test(double currentTestTime) {
    }
}
