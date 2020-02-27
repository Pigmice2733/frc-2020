package com.pigmice.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.pigmice.frc.robot.Dashboard;
import com.pigmice.frc.robot.MotorTester;
import com.pigmice.frc.robot.MotorTester.TestStatus;
import com.pigmice.frc.robot.subsystems.SystemConfig.IntakeConfiguration;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public class Intake implements ISubsystem {
    public enum Position {
        DOWN,
        UP
    }

    private final TalonSRX motor;
    private final DoubleSolenoid solenoid;

    private final MotorTester.Config test = new MotorTester.Config(0.25, 0.1, 1.0);
    private final NetworkTableEntry motorReport;

    private static final double wheelSpeed = 0.6;

    private double speed = 0.0;
    private Value targetPistonState = Value.kOff;
    private Value previousPistonState = Value.kOff;

    private static Intake instance = null;

    public static Intake getInstance() {
        if(instance == null) {
            instance = new Intake();
        }

        return instance;
    }

    public Intake() {
        motor = new TalonSRX(IntakeConfiguration.motorPort);
        motor.setInverted(IntakeConfiguration.motorInverted);

        solenoid = new DoubleSolenoid(IntakeConfiguration.forwardSolenoidPort, IntakeConfiguration.reverseSolenoidPort);

        ShuffleboardLayout testReportLayout = Shuffleboard.getTab(Dashboard.systemsTestTabName)
                .getLayout("Intake", BuiltInLayouts.kList).withSize(2, 1).withPosition(2, 0);

        motorReport = testReportLayout.add("Motor (" + IntakeConfiguration.motorPort + ")", false).getEntry();
    }

    @Override
    public void initialize() {
        speed = 0.0;
        targetPistonState = solenoid.get();
        previousPistonState = targetPistonState;
    }

    public void run(boolean run) {
        this.speed = run ? wheelSpeed : 0.0;
    }

    public void setPosition(Position position) {
        targetPistonState = (position == Position.DOWN) ? Value.kForward : Value.kReverse;
    }

    @Override
    public void updateDashboard() {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateInputs() {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateOutputs() {
        motor.set(ControlMode.PercentOutput, -speed);

        if (targetPistonState != previousPistonState) {
            solenoid.set(targetPistonState);
            previousPistonState = targetPistonState;
        }
    }

    @Override
    public void test(double currentTestTime) {
        TestStatus status = MotorTester.Test(motor, test, currentTestTime);
        motorReport.setBoolean(status == TestStatus.PASS);
    }
}
