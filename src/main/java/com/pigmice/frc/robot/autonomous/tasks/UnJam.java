package com.pigmice.frc.robot.autonomous.tasks;

import com.pigmice.frc.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.Timer;

public class UnJam implements ITask {
    private final Intake intake;
    private final double timeLength;
    private double startTime;

    public UnJam(Intake intake, double timeLength) {
        this.intake = intake;
        this.timeLength = timeLength;
    }

    @Override
    public void initialize() {
        startTime = Timer.getFPGATimestamp();
    }

    @Override
    public boolean update() {
        if(Timer.getFPGATimestamp() - startTime > timeLength) {
            return true;
        }

        intake.setPosition(Intake.Position.DOWN);
        return false;
    }
}
