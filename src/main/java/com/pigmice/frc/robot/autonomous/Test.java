package com.pigmice.frc.robot.autonomous;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pigmice.frc.lib.purepursuit.Path;
import com.pigmice.frc.lib.utils.Point;
import com.pigmice.frc.robot.autonomous.subroutines.Acquire;
import com.pigmice.frc.robot.autonomous.subroutines.PathFollower;
import com.pigmice.frc.robot.autonomous.subroutines.Shoot;
import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Intake;
import com.pigmice.frc.robot.subsystems.Shooter;

public class Test extends Autonomous {
    private static final Path outPath = constructOutPath();
    private static final Path inPath = constructInPath();

    public Test(Drivetrain drivetrain, Shooter shooter, Feeder feeder, Intake intake) {
        PathFollower acquirePathFollower = new PathFollower(drivetrain, constructAcquirePath());

        this.subroutines = Arrays.asList(
            new Shoot(shooter, feeder),
            new PathFollower(drivetrain, outPath),
            new Acquire(intake, acquirePathFollower),
            new PathFollower(drivetrain, inPath),
            new Shoot(shooter, feeder)
        );
    }

    public void initialize() {
        super.initialize();
    }

    public static Path constructAcquirePath() {
        List<Point> positions = new ArrayList<>();
        positions.add(new Point(1.5, -2.0));
        positions.add(new Point(1.5, -3.0));
        positions.add(new Point(1.5, -3.25));

        List<Double> velocities = new ArrayList<>();
        velocities.add(-1.0);
        velocities.add(-1.0);
        velocities.add(-0.15);

        return new Path(positions, velocities);
    }

    public static Path constructOutPath() {
        List<Point> positions = new ArrayList<>();
        positions.add(new Point(0.0, 0.0));
        positions.add(new Point(0.4, -0.5));
        positions.add(new Point(1.2, -0.75));
        positions.add(new Point(1.5, -1.25));
        positions.add(new Point(1.5, -2.0));

        List<Double> velocities = new ArrayList<>();
        velocities.add(-1.0);
        velocities.add(-1.0);
        velocities.add(-1.0);
        velocities.add(-1.0);
        velocities.add(-1.0);

        return new Path(positions, velocities);
    }

    public static Path constructInPath() {
        List<Point> positions = new ArrayList<>();
        positions.add(new Point(1.5, -3.25));
        positions.add(new Point(0.9, -2.5));
        positions.add(new Point(0.3, -1.75));
        positions.add(new Point(0.0, -1));
        positions.add(new Point(0.0, 0.0));

        List<Double> velocities = new ArrayList<>();
        velocities.add(1.0);
        velocities.add(1.0);
        velocities.add(1.0);
        velocities.add(1.0);
        velocities.add(0.15);

        return new Path(positions, velocities);
    }
}
