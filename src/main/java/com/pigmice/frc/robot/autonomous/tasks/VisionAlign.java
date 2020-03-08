package com.pigmice.frc.robot.autonomous.tasks;

import com.pigmice.frc.robot.Vision;
import com.pigmice.frc.robot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.Timer;

public class VisionAlign implements ITask {
    private final Drivetrain drivetrain;
    private double startTime;
    private final double timeLimit;

    public VisionAlign(Drivetrain drivetrain, double timeLimit) {
        this.drivetrain = drivetrain;
        this.timeLimit = timeLimit;
    }

    @Override
    public void initialize() {
        startTime = Timer.getFPGATimestamp();
        Vision.update();
    }

    @Override
    public boolean update() {
        Vision.update();

        if(Timer.getFPGATimestamp() - startTime > timeLimit) {
            drivetrain.arcadeDrive(0.0, 0.0);
            Vision.stop();
            return true;
        }

        if(Vision.alignmentError() < 1) {
            drivetrain.arcadeDrive(0.0, 0.0);
            Vision.stop();
            return true;
        }

        double output = Vision.pidOutput();
        drivetrain.arcadeDrive(0.0, output);
        return false;
    }
}
