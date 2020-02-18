package com.pigmice.frc.robot.autonomous.subroutines;

import com.pigmice.frc.lib.purepursuit.Path;
import com.pigmice.frc.lib.purepursuit.PurePursuit;
import com.pigmice.frc.lib.purepursuit.PurePursuit.Output;
import com.pigmice.frc.lib.utils.Odometry.Pose;
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
        Output output = controller.process(pose, 0.5);

        if(output.done) {
            return true;
        }

        drivetrain.curvatureDrive(output.velocity * 0.2, output.curvature);

        return false;
    }
}
