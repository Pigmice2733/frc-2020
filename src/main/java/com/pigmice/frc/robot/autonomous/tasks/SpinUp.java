package com.pigmice.frc.robot.autonomous.tasks;

import com.pigmice.frc.robot.subsystems.Shooter;
import com.pigmice.frc.robot.subsystems.Shooter.Action;

import edu.wpi.first.wpilibj.Timer;

public class SpinUp implements ITask {
    private final Shooter shooter;

    private double startTime = 0.0;
    private final double time;
    private final Action action;

    public SpinUp(Shooter shooter, double time, Action action) {
        this.shooter = shooter;
        this.time = time;
        this.action = action;
    }

    @Override
    public void initialize() {
        startTime = Timer.getFPGATimestamp();
        shooter.initialize();
    }

    @Override
    public boolean update() {
        shooter.run(action);
        shooter.setHood(true);

        if (Timer.getFPGATimestamp() - startTime > time) {
            return true;
        }

        return false;
    }
}
