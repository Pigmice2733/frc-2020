package com.pigmice.frc.robot;

import com.pigmice.frc.lib.controllers.PID;
import com.pigmice.frc.lib.controllers.PIDGains;
import com.pigmice.frc.lib.motion.setpoint.ISetpoint;
import com.pigmice.frc.lib.motion.setpoint.Setpoint;
import com.pigmice.frc.lib.utils.Range;
import com.pigmice.frc.lib.utils.Ring;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Vision {
    private static final NetworkTable input = NetworkTableInstance.getDefault().getTable("Vision");
    private static final NetworkTableEntry angleEntry = input.getEntry("targetAngle");
    private static final NetworkTableEntry widthEntry = input.getEntry("targetWidth");
    private static final NetworkTableEntry ledPercentEntry = input.getEntry("ledPercent");
    private static final NetworkTableEntry targetVisibleEntry = input.getEntry("targetVisible");
    private static final NetworkTableEntry distance = input.getEntry("distance");

    private static final Ring widthBuffer = new Ring(5);
    private static final Ring angleBuffer = new Ring(5);

    private static final ISetpoint alignmentSetpoint = new Setpoint(5.0, 0.0, 0.0, 0.0, 0.0);
    private static final PID alignmentPID;

    private static double pidOutput = 0.0;

    static {
        PIDGains gains = new PIDGains(-0.8e-2, -1.5e-2, -1e-6, 0.0, 0.0, 0.0);
        Range outputBounds = new Range(-0.15, 0.15);
        alignmentPID = new PID(gains, outputBounds, 0.02);
    }

    private static final double ledPower = 60;
    private static final double focalLength = 161;
    private static final double actualTargetWidth = 40;

    private static boolean currentlyAligning = false;

    public static void update() {
        if (!currentlyAligning) {
            currentlyAligning = true;
            alignmentPID.initialize(angleBuffer.average(), 0.0);
            ledPercentEntry.setDouble(ledPower);
        }

        if(!targetIsVisible()) {
            pidOutput = 0.0;
            return;
        }

        double angle = angleEntry.getDouble(0.0);
        double width = widthEntry.getDouble(0.0);

        angleBuffer.put(angle);
        widthBuffer.put(width);

        distance.setDouble(targetDistance());

        pidOutput = alignmentPID.calculateOutput(angleBuffer.average(), alignmentSetpoint);
    }

    public static void stop() {
        if(currentlyAligning) {
            currentlyAligning = false;
            ledPercentEntry.setDouble(0.0);
        }
    }

    public static double pidOutput() {
        return pidOutput;
    }

    public static boolean targetIsVisible() {
        return targetVisibleEntry.getBoolean(false);
    }

    public static double targetDistance() {
        return focalLength * actualTargetWidth / widthBuffer.average();
    }

    public static double targetAngle() {
        return angleBuffer.average();
    }

    public static double alignmentError() {
        return Math.abs(angleBuffer.average() - alignmentSetpoint.getPosition());
    }
}
