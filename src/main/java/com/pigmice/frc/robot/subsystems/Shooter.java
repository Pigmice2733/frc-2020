package com.pigmice.frc.robot.subsystems;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import com.pigmice.frc.lib.motion.setpoint.ISetpoint;
import com.pigmice.frc.lib.utils.Utils;
import com.pigmice.frc.robot.Dashboard;
import com.pigmice.frc.robot.MotorTester;
import com.pigmice.frc.robot.MotorTester.TestStatus;
import com.pigmice.frc.robot.subsystems.SystemConfig.ShooterConfiguration;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public class Shooter implements ISubsystem {
    private static class ShooterSetpoint implements ISetpoint {
        private final double rpm;

        public ShooterSetpoint(double rpm) {
            this.rpm = rpm;
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

    private static class PowerCurve {
        private static final ArrayList<ShooterSetpoint> data = new ArrayList<>();

        private static final double maxRange = 40 * 12;
        private static final ShooterSetpoint maxPower = new ShooterSetpoint(8000.0);

        private static final SortedMap<Double, Double> seedData = new TreeMap<>();

        static {
            seedData.put(0.0, 2400.0);
            seedData.put(65.0, 2800.0);
            seedData.put(150.0, 3000.0);
            seedData.put(300.0, 3800.0);
            seedData.put(360.0, 4200.0);
            seedData.put(maxRange, maxPower.getVelocity());
        }

        static {
            int seedIndex = 0;
            Double[] ranges = seedData.keySet().toArray(new Double[seedData.size()]);
            for (int i = 0; i <= maxRange; i += 10) {
                if (ranges[seedIndex + 1] < i) {
                    seedIndex++;
                }

                double min = ranges[seedIndex];
                double max = ranges[seedIndex + 1];
                double rpm = Utils.lerp(i, min, max, seedData.getOrDefault(min, 0.0), seedData.getOrDefault(max, 0.0));
                data.add(new ShooterSetpoint(rpm));
            }
        }

        public static ShooterSetpoint getPowerAtRange(double range) {
            int index = (int) (range / 10.0);

            if (index >= data.size()) {
                return maxPower;
            }

            return data.get(index);
        }
    }

    private final CANSparkMax motor, follower;
    private final CANPIDController canPID;
    private final CANEncoder encoder;

    private final MotorTester.Config motorTest = new MotorTester.Config(0.35, 0.1, 1.0);

    private double shooterRPM = 0.0;
    private double shooterVoltage = 0.0;

    private static final ShooterSetpoint stoppedSetpoint = new ShooterSetpoint(0);
    private static final ShooterSetpoint clearSetpoint = new ShooterSetpoint(-100);

    private ShooterSetpoint setpoint = stoppedSetpoint;

    private static Shooter instance = null;

    private final NetworkTableEntry shooterLeaderReport;
    private final NetworkTableEntry shooterFollowerReport;
    private double targetShooterRPM;

    private final NetworkTableEntry shooterRPMDisplay, shooterVoltageDisplay, shooterReadyDisplay;

    public static Shooter getInstance() {
        if (instance == null) {
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
        canPID = motor.getPIDController();

        canPID.setP(1.5e-4);
        canPID.setI(4e-7);
        canPID.setD(4e-7);
        canPID.setIZone(0);
        canPID.setFF(0.00002);
        canPID.setOutputRange(-1, 1);

        encoder.setVelocityConversionFactor(1.0);

        ShuffleboardLayout testReportLayout = Shuffleboard.getTab(Dashboard.systemsTestTabName)
                .getLayout("Shooter", BuiltInLayouts.kList).withSize(2, 2)
                .withPosition(Dashboard.shooterTestPosition, 0);

        shooterLeaderReport = testReportLayout
                .add("Shooter Leader (" + ShooterConfiguration.leaderMotorPort + ")", false).getEntry();
        shooterFollowerReport = testReportLayout
                .add("Shooter Follower (" + ShooterConfiguration.followerMotorPort + ")", false).getEntry();

        ShuffleboardLayout displayLayout = Shuffleboard.getTab(Dashboard.developmentTabName)
                .getLayout("Shooter", BuiltInLayouts.kList).withSize(2, 7)
                .withPosition(Dashboard.shooterDisplayPosition, 0);

        shooterRPMDisplay = displayLayout.add("Shooter RPM", 0.0).withWidget(BuiltInWidgets.kGraph).getEntry();
        shooterVoltageDisplay = displayLayout.add("Shooter Voltage", 0.0).withWidget(BuiltInWidgets.kGraph).getEntry();
        shooterReadyDisplay = displayLayout.add("Shooter Ready", false).getEntry();
        targetShooterRPM = 0.0;
    }

    @Override
    public void initialize() {
        setpoint = stoppedSetpoint;

        updateDashboard();
    }

    public void setRange(double range) {
        setpoint = PowerCurve.getPowerAtRange(range);
    }

    public void upPower() {
        targetShooterRPM += 100;
    }

    public void downPower() {
        targetShooterRPM -= 100;
    }

    public void stop() {
        setpoint = stoppedSetpoint;
    }

    public void clear() {
        setpoint = clearSetpoint;
    }

    public boolean isReady() {
        return Math.abs((shooterRPM - setpoint.rpm) / setpoint.rpm) < 0.02;
    }

    @Override
    public void updateDashboard() {
        shooterRPMDisplay.setNumber(shooterRPM);
        shooterVoltageDisplay.setNumber(shooterVoltage);
        shooterReadyDisplay.setBoolean(isReady());
    }

    @Override
    public void updateInputs() {
        shooterRPM = encoder.getVelocity();
    }

    @Override
    public void updateOutputs() {
        if (setpoint.getVelocity() != 0.0) {
            double target = setpoint.getVelocity();
            canPID.setReference(target, ControlType.kVelocity);
        } else {
            canPID.setReference(0.0, ControlType.kDutyCycle);
        }

        shooterVoltage = motor.getAppliedOutput() * 100.0;
    }

    @Override
    public void test(double currentTestTime) {
        TestStatus status = MotorTester.Test(motor, motorTest, currentTestTime);
        shooterLeaderReport.setBoolean(status == TestStatus.PASS);

        status = MotorTester.Test(follower, motorTest, currentTestTime);
        shooterFollowerReport.setBoolean(status == TestStatus.PASS);
    }
}
