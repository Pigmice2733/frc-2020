package com.pigmice.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.pigmice.frc.robot.Dashboard;
import com.pigmice.frc.robot.MotorTester;
import com.pigmice.frc.robot.MotorTester.TestStatus;
import com.pigmice.frc.robot.subsystems.SystemConfig.FeederConfiguration;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public class Feeder implements ISubsystem {
    public enum LiftAction {
        FEED(0.65), GRAB(0.25), BACKFEED(-0.3), HOLD(0.0);

        public final double speed;

        private LiftAction(final double speed) {
            this.speed = speed;
        }
    }

    private final TalonSRX hopperMotor, hopperFollower;
    private final TalonSRX liftMotor, liftFollower;

    private final MotorTester.Config hopperTest = new MotorTester.Config(0.25, 0.1, 1.0);
    private final MotorTester.Config liftTest = new MotorTester.Config(0.75, 0.1, 1.0);
    private final NetworkTableEntry liftReport, liftFollowerReport, hopperReport, hopperFollowerReport;

    private final double hopperSpeed = 0.5;

    private LiftAction liftAction = LiftAction.HOLD;
    private boolean runHopper = false;

    private static Feeder instance = null;

    public static Feeder getInstance() {
        if (instance == null) {
            instance = new Feeder();
        }

        return instance;
    }

    public Feeder() {
        liftMotor = new TalonSRX(FeederConfiguration.liftLeaderMotorPort);
        liftMotor.setInverted(FeederConfiguration.liftInverted);

        liftFollower = new TalonSRX(FeederConfiguration.liftFollowerMotorPort);
        liftFollower.follow(liftMotor);
        liftFollower.setInverted(!FeederConfiguration.liftInverted);

        hopperMotor = new TalonSRX(FeederConfiguration.hopperLeaderMotorPort);
        hopperMotor.setInverted(FeederConfiguration.hopperInverted);

        hopperFollower = new TalonSRX(FeederConfiguration.hopperFollowerMotorPort);
        hopperFollower.follow(hopperMotor);
        hopperFollower.setInverted(!FeederConfiguration.hopperInverted);

        ShuffleboardLayout testReportLayout = Shuffleboard.getTab(Dashboard.systemsTestTabName)
                .getLayout("Feeder", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(Dashboard.feederTestPosition, 0);

        liftReport = testReportLayout.add("Lift Leader (" + liftMotor.getDeviceID() + ")", false).getEntry();
        liftFollowerReport = testReportLayout.add("Lift Follower (" + liftFollower.getDeviceID() + ")", false).getEntry();
        hopperReport = testReportLayout.add("Hopper Leader (" + hopperMotor.getDeviceID() + ")", false).getEntry();
        hopperFollowerReport = testReportLayout.add("Hopper Follower (" + hopperFollower.getDeviceID() + ")", false).getEntry();
    }

    @Override
    public void initialize() {
        runHopper = false;
        liftAction = LiftAction.HOLD;
    }

    public void runLift(final LiftAction action) {
        this.liftAction = action;
    }

    public void runHopper(final boolean run) {
        runHopper = run;
    }

    @Override
    public void updateDashboard() {
    }

    @Override
    public void updateInputs() {
    }

    @Override
    public void updateOutputs() {
        hopperMotor.set(ControlMode.PercentOutput, runHopper ? hopperSpeed : 0.0);

        liftMotor.set(ControlMode.PercentOutput, liftAction.speed);
    }

    @Override
    public void test(final double currentTestTime) {
        TestStatus status = MotorTester.Test(hopperMotor, hopperTest, currentTestTime);
        hopperReport.setBoolean(status == TestStatus.PASS);

        status = MotorTester.Test(hopperFollower, hopperTest, currentTestTime);
        hopperFollowerReport.setBoolean(status == TestStatus.PASS);

        status = MotorTester.Test(liftMotor, liftTest, currentTestTime);
        liftReport.setBoolean(status == TestStatus.PASS);

        status = MotorTester.Test(liftFollower, liftTest, currentTestTime);
        liftFollowerReport.setBoolean(status == TestStatus.PASS);
    }
}
