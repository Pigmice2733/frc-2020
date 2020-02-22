package com.pigmice.frc.robot.autonomous.tasks;

import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Feeder.LiftAction;
import com.pigmice.frc.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.Timer;

public class Shoot implements ITask {
    private final Shooter shooter;
    private final Feeder feeder;

    private final double shootLength = 2.0;

    private double startTime = 0.0;
    private boolean shootingStarted = false;

    public Shoot(Shooter shooter, Feeder feeder) {
        this.shooter = shooter;
        this.feeder = feeder;
    }

    @Override
    public void initialize() {
        startTime = 0.0;
        shootingStarted = false;
    }

    @Override
    public boolean update() {
        shooter.run(true);

        if (shooter.isReady() && !shootingStarted) {
            shootingStarted = true;
            startTime = Timer.getFPGATimestamp();
        }

        if(shootingStarted) {
            feeder.runLift(LiftAction.FEED);
            feeder.runHopper(true);
        }

        if (shootingStarted && Timer.getFPGATimestamp() - startTime > shootLength) {
            shooter.run(false);
            feeder.runLift(LiftAction.HOLD);
            feeder.runHopper(false);

            return true;
        }

        return false;
    }
}