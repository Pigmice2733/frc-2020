package com.pigmice.frc.robot.subsystems;

import com.pigmice.frc.lib.controllers.TakeBackHalf;
import com.pigmice.frc.lib.motion.setpoint.ISetpoint;
import com.pigmice.frc.robot.Dashboard;
import com.pigmice.frc.robot.MotorTester;
import com.pigmice.frc.robot.MotorTester.TestStatus;
import com.pigmice.frc.robot.subsystems.SystemConfig.ShooterConfiguration;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter implements ISubsystem {
    private static class ShooterSetpoint implements ISetpoint {
        private final double rpm;
        private final double output;

        public ShooterSetpoint(double rpm, double output) {
            this.rpm = rpm;
            this.output = output;
        }

        public double getAcceleration() {
            return 0;
        }

        public double getVelocity() {
            return rpm;
        }

        public double getPosition() {
            return 0;
        }

        public double getCurvature() {
            return 0;
        }

        public double getHeading() {
            return 0;
        }
    }

    public enum Action {
        LONG_SHOT(7000, 0.87, true),
        MEDIUM_SHOT(5200, 0.65, true),
        SHORT_SHOT(4000, 0.5, true),
        CLEAR(-100, -0.1, false),
        HOLD(0, 0, false);

        private final double rpm;
        private final double voltage;
        private final boolean closedLoop;

        private Action(double rpm, double voltage, boolean closedLoop) {
            this.rpm = rpm;
            this.voltage = voltage;
            this.closedLoop = closedLoop;
        }
    }

    private final CANSparkMax motor, follower;
    private final CANEncoder encoder;
    private DoubleSolenoid hoodSolenoid;

    private final MotorTester.Config motorTest = new MotorTester.Config(0.35, 0.1, 1.0);

    private double shooterRPM = 0.0;
    private double shooterVoltage = 0.0;
    private Value hoodState = Value.kOff;
    private Value previousHoodState = Value.kOff;

    private ShooterSetpoint targetRPM = new ShooterSetpoint(5200, 0.5);

    private Action action = Action.HOLD;

    private final TakeBackHalf controller = new TakeBackHalf(0.5e-5, 0.8);

    private static Shooter instance = null;

    private final NetworkTableEntry shooterLeaderReport;
    private final NetworkTableEntry shooterFollowerReport;

    public static Shooter getInstance() {
        if(instance == null) {
            instance = new Shooter();
        }

        return instance;
    }

    public Shooter() {
        motor = new CANSparkMax(ShooterConfiguration.leaderMotorPort, MotorType.kBrushless);
        motor.setInverted(ShooterConfiguration.inverted);

        follower = new CANSparkMax(ShooterConfiguration.followerMotorPort, MotorType.kBrushless);
        follower.follow(motor, true);

        encoder = motor.getEncoder();

        hoodSolenoid = new DoubleSolenoid(ShooterConfiguration.forwardSolenoidPort, ShooterConfiguration.reverseSolenoidPort);

        encoder.setVelocityConversionFactor(1.0 / ShooterConfiguration.reduction);

        ShuffleboardLayout testReportLayout = Shuffleboard.getTab(Dashboard.systemsTestTabName)
                .getLayout("Shooter", BuiltInLayouts.kList).withSize(2, 2).withPosition(4, 0);

        shooterLeaderReport = testReportLayout
                .add("Shooter Leader (" + ShooterConfiguration.leaderMotorPort + ")", false).getEntry();
        shooterFollowerReport = testReportLayout
                .add("Shooter Follower (" + ShooterConfiguration.followerMotorPort + ")", false).getEntry();
    }

    @Override
    public void initialize() {
        shooterRPM = 0.0;
        shooterVoltage = 0.0;
        hoodState = hoodSolenoid.get();
        previousHoodState = hoodState;

        action = Action.HOLD;

        controller.updateTargetOutput(targetRPM.output);
        controller.initialize(0.0, 1.0);

        updateDashboard();
    }

    public void run(Action action) {
        if(!this.action.closedLoop && action.closedLoop) {
            controller.initialize(shooterRPM, 1.0);
        }

        this.action = action;
    }

    public void setHood(boolean extend) {
        hoodState = extend ? Value.kForward : Value.kReverse;
    }

    public boolean isReady() {
        return Math.abs((shooterRPM - targetRPM.rpm) / targetRPM.rpm) < 0.01;
    }

    @Override
    public void updateDashboard() {
        SmartDashboard.putNumber("Shooter RPM", shooterRPM);
        SmartDashboard.putNumber("Shooter Voltage", shooterVoltage);
        SmartDashboard.putBoolean("Shooter Ready", isReady());
    }

    @Override
    public void updateInputs() {
        shooterRPM = encoder.getVelocity();
    }

    @Override
    public void updateOutputs() {
        double output = 0.0;

        if(action.closedLoop) {
            output = controller.calculateOutput(shooterRPM, targetRPM);
        } else {
            output = action.voltage;
        }

        shooterVoltage = output * 100.0;
        motor.set(output);

        if(hoodState != previousHoodState) {
            hoodSolenoid.set(hoodState);
            previousHoodState = hoodState;
        }
    }

    @Override
    public void test(double currentTestTime) {
        TestStatus status = MotorTester.Test(motor, motorTest, currentTestTime);
        shooterLeaderReport.setBoolean(status == TestStatus.PASS);

        status = MotorTester.Test(follower, motorTest, currentTestTime);
        shooterFollowerReport.setBoolean(status == TestStatus.PASS);
    }
}
