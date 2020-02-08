package com.pigmice.frc.robot.autonomous.subroutines;

import com.pigmice.frc.lib.controllers.PID;
import com.pigmice.frc.lib.controllers.PIDGains;
import com.pigmice.frc.lib.motion.execution.ProfileExecutor;
import com.pigmice.frc.lib.motion.profile.StaticProfile;
import com.pigmice.frc.lib.utils.Point;
import com.pigmice.frc.lib.utils.Range;
import com.pigmice.frc.robot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.Timer;

public class Drive implements ISubroutine {
    private ProfileExecutor executor;
    private PID drivingPID;

    private final Drivetrain drivetrain;

    private final double targetDistance;
    private Point initialPosition = Point.origin();

    public Drive(Drivetrain drivetrain, double meters) {
        this.drivetrain = drivetrain;
        this.targetDistance = meters;

        PIDGains gains = new PIDGains(0.5, 0.0, 0.0, 0.0, 0.15, 0.005);
        Range outputBounds = new Range(-0.8, 0.8);
        drivingPID = new PID(gains, outputBounds, 0.02);
    }

    public void initialize() {
        initialPosition = new Point(drivetrain.getPose());

        StaticProfile profile = new StaticProfile(0.0, 0.0, targetDistance, 4, 6, 4);
        executor = new ProfileExecutor(profile, drivingPID, this::output, this::getDistance,
                0.0075 * 2 * Math.PI, 0.05, Timer::getFPGATimestamp);

        drivingPID.initialize(0.0, 0.0);
        executor.initialize();
    }

    public boolean update() {
        return executor.update();
    }

    private double getDistance() {
        var currentPosition = new Point(drivetrain.getPose());
        return currentPosition.subtract(initialPosition).getMagnitude();
    }

    private void output(double output) {
        drivetrain.tankDrive(output, output);
    }
}
