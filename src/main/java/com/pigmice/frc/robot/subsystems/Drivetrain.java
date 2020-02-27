package com.pigmice.frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.pigmice.frc.lib.utils.Odometry;
import com.pigmice.frc.lib.utils.Odometry.Pose;
import com.pigmice.frc.lib.utils.Utils;
import com.pigmice.frc.robot.Dashboard;
import com.pigmice.frc.robot.subsystems.SystemConfig.DrivetrainConfiguration;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivetrain implements ISubsystem {
    private final CANSparkMax leftDrive, rightDrive;
    private final CANEncoder leftEncoder, rightEncoder;

    private double leftDemand, rightDemand;
    private double leftPosition, rightPosition, heading;

    private Odometry odometry;

    private AHRS navx;
    private final NetworkTableEntry navxReport;

    private static Drivetrain instance = null;

    public static Drivetrain getInstance() {
        if (instance == null) {
            instance = new Drivetrain();
        }

        return instance;
    }

    private Drivetrain() {
        rightDrive = new CANSparkMax(DrivetrainConfiguration.frontRightMotorPort, MotorType.kBrushless);
        CANSparkMax rightFollower = new CANSparkMax(DrivetrainConfiguration.backRightMotorPort, MotorType.kBrushless);
        leftDrive = new CANSparkMax(DrivetrainConfiguration.frontLeftMotorPort, MotorType.kBrushless);
        CANSparkMax leftFollower = new CANSparkMax(DrivetrainConfiguration.backRightMotorPort, MotorType.kBrushless);

        rightDrive.setInverted(true);
        leftFollower.follow(leftDrive);
        rightFollower.follow(rightDrive);

        navx = new AHRS(DrivetrainConfiguration.navxPort);

        ShuffleboardLayout testReportLayout = Shuffleboard.getTab(Dashboard.systemsTestTabName)
                .getLayout("Drivetrain", BuiltInLayouts.kList).withSize(2, 1).withPosition(6, 0);
        navxReport = testReportLayout.add("NavX", false).getEntry();

        leftEncoder = leftDrive.getEncoder();
        rightEncoder = rightDrive.getEncoder();

        leftEncoder.setPositionConversionFactor(1.0 / DrivetrainConfiguration.rotationToDistanceConversion);
        rightEncoder.setPositionConversionFactor(1.0 / DrivetrainConfiguration.rotationToDistanceConversion);

        odometry = new Odometry(new Pose(0.0, 0.0, 0.0));
    }

    @Override
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

    @Override
    public void updateDashboard() {
        Pose currentPose = odometry.getPose();

        SmartDashboard.putNumber("Robot X", currentPose.getX());
        SmartDashboard.putNumber("Robot Y", currentPose.getY());
        SmartDashboard.putNumber("Robot Heading", currentPose.getHeading());
        SmartDashboard.putNumber("Left", leftPosition);
        SmartDashboard.putNumber("Right", rightPosition);
    }

    @Override
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

        if (!Utils.almostEquals(forwardSpeed, 0.0)) {
            leftSpeed = forwardSpeed * (1 + (curvature * 0.5 * DrivetrainConfiguration.wheelBase));
            rightSpeed = forwardSpeed * (1 - (curvature * 0.5 * DrivetrainConfiguration.wheelBase));
        }

        leftDemand = leftSpeed;
        rightDemand = rightSpeed;
    }

    public void stop() {
        leftDemand = 0.0;
        rightDemand = 0.0;
    }

    @Override
    public void updateOutputs() {
        leftDrive.set(leftDemand);
        rightDrive.set(rightDemand);

        leftDemand = 0.0;
        rightDemand = 0.0;
    }

    @Override
    public void test(double time) {
        navxReport.setBoolean(navx.getAngle() != 0.0);
    }
}
