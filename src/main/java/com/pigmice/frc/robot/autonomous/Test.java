package com.pigmice.frc.robot.autonomous;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pigmice.frc.lib.purepursuit.Path;
import com.pigmice.frc.lib.utils.Point;
import com.pigmice.frc.robot.autonomous.actions.Acquire;
import com.pigmice.frc.robot.autonomous.actions.IAction;
import com.pigmice.frc.robot.autonomous.actions.SpinUp;
import com.pigmice.frc.robot.autonomous.tasks.PathFollower;
import com.pigmice.frc.robot.autonomous.tasks.Shoot;
import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Intake;
import com.pigmice.frc.robot.subsystems.Shooter;

public class Test extends Autonomous {
    public Test(Drivetrain drivetrain, Shooter shooter, Feeder feeder, Intake intake) {
        PathFollower acquisition = constructAcquisition(drivetrain, intake);
        PathFollower returnAndSpinUp = constructReturnPath(drivetrain, shooter);

        this.subroutines = Arrays.asList(
            new Shoot(shooter, feeder),
            acquisition,
            returnAndSpinUp,
            new Shoot(shooter, feeder)
        );
    }

    public void initialize() {
        super.initialize();
    }

    public static PathFollower constructAcquisition(Drivetrain drivetrain, Intake intake) {
        List<Point> positions = new ArrayList<>();
        positions.add(new Point(0.0, 0.0));
        positions.add(new Point(0.4, -0.5));
        positions.add(new Point(1.2, -0.75));
        positions.add(new Point(1.5, -1.25));
        positions.add(new Point(1.5, -2.0));
        positions.add(new Point(1.5, -3.0));
        positions.add(new Point(1.5, -3.25));

        List<Double> velocities = new ArrayList<>();
        velocities.add(1.0);
        velocities.add(1.0);
        velocities.add(1.0);
        velocities.add(1.0);
        velocities.add(1.0);
        velocities.add(1.0);
        velocities.add(0.15);

        Path path = new Path(positions, velocities);

        IAction powerCellAcquisition = new Acquire(intake);

        PathFollower follower = new PathFollower(drivetrain, path, true);
        follower.addAction(4, 6, powerCellAcquisition);
        return follower;
    }

    public static PathFollower constructReturnPath(Drivetrain drivetrain, Shooter shooter) {
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

        Path path = new Path(positions, velocities);

        IAction spinUp = new SpinUp(shooter);

        PathFollower follower = new PathFollower(drivetrain, path, false);
        follower.addAction(2, 3, spinUp);
        return follower;
    }
}
