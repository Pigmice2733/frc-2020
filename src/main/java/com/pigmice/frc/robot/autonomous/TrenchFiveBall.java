package com.pigmice.frc.robot.autonomous;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pigmice.frc.lib.purepursuit.Path;
import com.pigmice.frc.lib.utils.Point;
import com.pigmice.frc.robot.autonomous.actions.Acquire;
import com.pigmice.frc.robot.autonomous.actions.BackFeed;
import com.pigmice.frc.robot.autonomous.actions.IAction;
import com.pigmice.frc.robot.autonomous.actions.SpinUp;
import com.pigmice.frc.robot.autonomous.tasks.PathFollower;
import com.pigmice.frc.robot.autonomous.tasks.Shoot;
import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Intake;
import com.pigmice.frc.robot.subsystems.Shooter;

public class TrenchFiveBall extends Autonomous {
    public TrenchFiveBall(Drivetrain drivetrain, Shooter shooter, Feeder feeder, Intake intake) {
        PathFollower acquisition = constructAcquisition(drivetrain, intake, feeder);
        PathFollower returnAndSpinUp = constructReturnPath(drivetrain, shooter, intake, feeder);

        this.subroutines = Arrays.asList(acquisition, returnAndSpinUp, new Shoot(shooter, feeder, 2.0, 120));
    }

    public void initialize() {
        super.initialize();
    }

    public String name() {
        return "Trench Five Ball";
    }

    public static PathFollower constructAcquisition(Drivetrain drivetrain, Intake intake, Feeder feeder) {
        List<Point> positions = new ArrayList<>();
        positions.add(new Point(0.0, 0.0));
        positions.add(new Point(-0.3, -0.45));
        positions.add(new Point(-0.5, -0.6));
        positions.add(new Point(-0.5, -0.8));
        positions.add(new Point(-1.25, -1.85));
        positions.add(new Point(-1.4, -2.1));

        List<Double> velocities = new ArrayList<>();
        velocities.add(1.0);
        velocities.add(2.0);
        velocities.add(1.5);
        velocities.add(1.3);
        velocities.add(0.9);
        velocities.add(0.75);

        Path path = new Path(positions, velocities);

        IAction powerCellAcquisition = new Acquire(intake, feeder, true);

        PathFollower follower = new PathFollower(drivetrain, path, true, 2.0);
        follower.addAction(0, 2, powerCellAcquisition);
        return follower;
    }

    public static PathFollower constructReturnPath(Drivetrain drivetrain, Shooter shooter, Intake intake,
            Feeder feeder) {
        List<Point> positions = new ArrayList<>();
        positions.add(new Point(-1.365, -2.05));
        positions.add(new Point(-1.325, -1.25));
        positions.add(new Point(1.0, -0.5));
        positions.add(new Point(1.5, -0.5));
        positions.add(new Point(3.0, -0.5));
        positions.add(new Point(3.25, -0.0));
        positions.add(new Point(3.4, 0.5));
        positions.add(new Point(3.5, 1.0));

        List<Double> velocities = new ArrayList<>();
        velocities.add(2.0);
        velocities.add(2.0);
        velocities.add(2.0);
        velocities.add(2.0);
        velocities.add(1.75);
        velocities.add(1.0);
        velocities.add(1.0);
        velocities.add(0.75);

        Path path = new Path(positions, velocities);

        IAction spinUp = new SpinUp(shooter, feeder, 120);
        IAction backFeed = new BackFeed(feeder);
        IAction powerCellAcquisition = new Acquire(intake, feeder, false);

        PathFollower follower = new PathFollower(drivetrain, path, false, 1.0);
        follower.addAction(0, 1, powerCellAcquisition);
        follower.addAction(1, 2, backFeed);
        follower.addAction(2, 5, spinUp);

        return follower;
    }
}
