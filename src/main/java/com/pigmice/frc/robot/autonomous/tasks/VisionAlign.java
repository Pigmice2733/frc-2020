package com.pigmice.frc.robot.autonomous.tasks;

import com.pigmice.frc.robot.Vision;
import com.pigmice.frc.robot.subsystems.Drivetrain;

public class VisionAlign implements ITask {
    private final Drivetrain drivetrain;

    public VisionAlign(Drivetrain drivetrain) {
        this.drivetrain = drivetrain;
    }

    @Override
    public void initialize() {
    }

    @Override
    public boolean update() {
        if(Vision.alignmentError() < 1) {
            drivetrain.arcadeDrive(0.0, 0.0);
            Vision.stop();
            return true;
        }

        double output = Vision.update();
        drivetrain.arcadeDrive(0.0, output);
        return false;
    }
}
