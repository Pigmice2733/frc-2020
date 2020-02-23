package com.pigmice.frc.robot.autonomous.tasks;

import java.util.ArrayList;
import java.util.List;

import com.pigmice.frc.lib.purepursuit.Path;
import com.pigmice.frc.lib.purepursuit.PurePursuit;
import com.pigmice.frc.lib.purepursuit.PurePursuit.Output;
import com.pigmice.frc.lib.utils.Odometry.Pose;
import com.pigmice.frc.robot.autonomous.actions.IAction;
import com.pigmice.frc.robot.subsystems.Drivetrain;

public class PathFollower implements ITask {
    private class ActionSegment {
        private final int startSegment;
        private final int endSegment;
        private final IAction action;

        private boolean active = false;

        public ActionSegment(int startSegment, int endSegment, IAction action) {
            this.startSegment = startSegment;
            this.endSegment = endSegment;
            this.action = action;
        }

        public boolean isActive() {
            return active;
        }

        public void enable() {
            active = true;
            action.initialize();
        }

        public void disable() {
            action.end();
            active = false;
        }
    }

    private final PurePursuit controller;
    private final Drivetrain drivetrain;
    private final List<ActionSegment> actions;

    // Indicates whether the robot will be driving facing backwards along the path
    private final boolean backwards;

    private final double kV = 0.2;
    private final double kA = 0.05;

    private final double lookahead;

    public PathFollower(Drivetrain drivetrain, Path path, boolean backwards, double lookahead) {
        controller = new PurePursuit(path, 7e-2);
        actions = new ArrayList<>();
        this.backwards = backwards;
        this.drivetrain = drivetrain;
        this.lookahead = lookahead;
    }

    public void addAction(int startSegment, int endSegment, IAction action) {
        actions.add(new ActionSegment(startSegment, endSegment, action));
    }

    public void initialize() {
        for (ActionSegment segment : actions) {
            segment.active = false;
        }
    }

    public boolean update() {
        Pose pose = drivetrain.getPose();
        double heading = backwards ? (pose.getHeading() + Math.PI) % (2*Math.PI) : pose.getHeading();
        pose = new Pose(pose.getX(), pose.getY(), heading);
        Output output = controller.process(pose, lookahead);

        if(output.atEnd) {
            for (ActionSegment segment : actions) {
                if (segment.isActive()) {
                    segment.disable();
                }
            }

            return true;
        }

        for (ActionSegment segment : actions) {
            if(segment.startSegment <= output.pathSegment && segment.endSegment >= output.pathSegment) {
                if(!segment.isActive()) {
                    segment.enable();
                }

                segment.action.update();
            } else if(segment.isActive()) {
                segment.disable();
            }
        }

        double outputPower = kV * output.velocity + kA * output.acceleration;
        outputPower = backwards ? -outputPower : outputPower;
        double curvature = backwards ? -output.curvature  : output.curvature;

        drivetrain.curvatureDrive(outputPower, curvature);

        return false;
    }
}
