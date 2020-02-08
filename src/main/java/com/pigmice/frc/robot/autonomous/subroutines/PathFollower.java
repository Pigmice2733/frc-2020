package com.pigmice.frc.robot.autonomous.subroutines;

import com.pigmice.frc.lib.purepursuit.Path;
import com.pigmice.frc.lib.purepursuit.PurePursuit;
import com.pigmice.frc.lib.purepursuit.PurePursuit.Output;
import com.pigmice.frc.lib.utils.Odometry.Pose;
import com.pigmice.frc.lib.utils.Point;
import com.pigmice.frc.robot.subsystems.Drivetrain;

public class PathFollower implements ISubroutine {
    private final PurePursuit controller;
    private final Drivetrain drivetrain;

    public PathFollower(Drivetrain drivetrain, Path path) {
        controller = new PurePursuit(path);
        this.drivetrain = drivetrain;
    }

    public void initialize() {
    }

    public boolean update() {
        Pose pose = drivetrain.getPose();

        double remainingDistance = new Point(pose).subtract(new Point(0.0, 3.0)).getMagnitude();
        if(remainingDistance < 0.05) {
            return true;
        }

        Output output = controller.process(pose, 1.0);
        double velocity = output.velocity * 0.15;

        drivetrain.curvatureDrive(velocity, output.curvature);

        return false;
    }
}
