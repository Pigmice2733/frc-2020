package com.pigmice.frc.robot.autonomous.tasks;

import com.pigmice.frc.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.Timer;

public class SpinUp implements ITask {
    private final Shooter shooter;

    private double startTime = 0.0;
    private final double time;
    private final double targetRange;

    public SpinUp(Shooter shooter, double time, double targetRange) {
        this.shooter = shooter;
        this.time = time;
        this.targetRange = targetRange;
    }

    @Override
    public void initialize() {
        startTime = Timer.getFPGATimestamp();
        shooter.initialize();
    }

    @Override
    public boolean update() {
        shooter.setRange(targetRange);

        if (Timer.getFPGATimestamp() - startTime > time) {
            return true;
        }

        return false;
    }
}
