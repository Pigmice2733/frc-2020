package com.pigmice.frc.robot.autonomous;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pigmice.frc.lib.purepursuit.Path;
import com.pigmice.frc.lib.utils.Point;
import com.pigmice.frc.robot.autonomous.subroutines.Turn;
import com.pigmice.frc.robot.subsystems.Drivetrain;

public class Test extends Autonomous {
    private final Drivetrain drivetrain;

    public Test(Drivetrain drivetrain) {
        this.drivetrain = drivetrain;

        List<Point> positions = new ArrayList<>();
        positions.add(new Point(0.0, 0.0));
        positions.add(new Point(0.0, 0.5));
        positions.add(new Point(0.0, 2.5));
        positions.add(new Point(0.0, 3.0));
        positions.add(new Point(2.0, 5.0));
        positions.add(new Point(4.0, 3.0));
        positions.add(new Point(4.0, 2.5));
        positions.add(new Point(4.0, 0.5));
        positions.add(new Point(4.0, 0.0));

        List<Double> velocities = new ArrayList<>();
        velocities.add(1.0);
        velocities.add(2.0);
        velocities.add(2.0);
        velocities.add(1.0);
        velocities.add(1.0);
        velocities.add(1.0);
        velocities.add(2.0);
        velocities.add(2.0);
        velocities.add(0.25);

        Path path = new Path(positions, velocities);

        this.subroutines = Arrays.asList(
            // new PathFollower(drivetrain, path),
            new Turn(drivetrain, 2 * Math.PI)
        );
    }

    public void initialize() {
        super.initialize();

        drivetrain.initialize();
    }
}
