package com.pigmice.frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.pigmice.frc.lib.utils.Odometry;
import com.pigmice.frc.lib.utils.Odometry.Pose;
import com.pigmice.frc.lib.utils.Utils;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivetrain implements ISubsystem {
    private final CANSparkMax leftDrive, rightDrive;
    private final CANEncoder leftEncoder, rightEncoder;

    private double leftDemand, rightDemand;
    private double leftPosition, rightPosition, heading;

    private Odometry odometry;

    private AHRS navx;

    private static final double wheelBase = 0.603;
    private static final double drivetrainDistanceConversion = 16.13;

    public Drivetrain(CANSparkMax leftDrive, CANSparkMax rightDrive, AHRS navx) {
        this.leftDrive = leftDrive;
        this.rightDrive = rightDrive;
        this.navx = navx;

        leftEncoder = leftDrive.getEncoder();
        rightEncoder = rightDrive.getEncoder();

        leftEncoder.setPositionConversionFactor(1.0 / drivetrainDistanceConversion);
        rightEncoder.setPositionConversionFactor(1.0 / drivetrainDistanceConversion);

        odometry = new Odometry(new Pose(0.0, 0.0, 0.0));
    }

    public void initialize() {
        leftPosition = 0.0;
        rightPosition = 0.0;
        heading = 0.5 * Math.PI;

        leftEncoder.setPosition(0.0);
        rightEncoder.setPosition(0.0);

        odometry.set(new Pose(0.0, 0.0, heading), leftPosition, rightPosition);

        leftDemand = 0.0;
        rightDemand = 0.0;

        navx.setAngleAdjustment(navx.getAngleAdjustment() - navx.getAngle() - 90.0);
    }

    public void updateDashboard() {
        Pose currentPose = odometry.getPose();

        SmartDashboard.putNumber("Robot X", currentPose.getX());
        SmartDashboard.putNumber("Robot Y", currentPose.getY());
        SmartDashboard.putNumber("Robot Heading", currentPose.getHeading());
        SmartDashboard.putNumber("Left", leftPosition);
        SmartDashboard.putNumber("Right", rightPosition);
    }

    public void updateInputs() {
        leftPosition = leftEncoder.getPosition();
        rightPosition = rightEncoder.getPosition();
        heading = Math.toRadians(-navx.getAngle());

        odometry.update(leftPosition, rightPosition, heading);
    }

    public double getHeading() {
        return heading;
    }

    public Pose getPose() {
        return odometry.getPose();
    }

    public void tankDrive(double leftSpeed, double rightSpeed) {
        leftDemand = leftSpeed;
        rightDemand = rightSpeed;
    }

    public void arcadeDrive(double forwardSpeed, double turnSpeed) {
        leftDemand = forwardSpeed + turnSpeed;
        rightDemand = forwardSpeed - turnSpeed;
    }

    public void curvatureDrive(double forwardSpeed, double curvature) {
        double leftSpeed = forwardSpeed;
        double rightSpeed = forwardSpeed;

        if(!Utils.almostEquals(forwardSpeed, 0.0)) {
            leftSpeed = forwardSpeed * (1 + (curvature * 0.5 * wheelBase));
            rightSpeed = forwardSpeed * (1 - (curvature * 0.5 * wheelBase));
        }

        leftDemand = leftSpeed;
        rightDemand =  rightSpeed;
    }

    public void stop() {
        leftDemand = 0.0;
        rightDemand = 0.0;
    }

    public void updateOutputs() {
        leftDrive.set(leftDemand);
        rightDrive.set(rightDemand);

        leftDemand = 0.0;
        rightDemand = 0.0;
    }

    public void test() {
    }
}
