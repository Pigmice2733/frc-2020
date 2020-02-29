package com.pigmice.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.pigmice.frc.lib.utils.Range;
import com.pigmice.frc.robot.subsystems.SystemConfig.ClimberConfiguration;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Climber implements ISubsystem {
    private final TalonSRX leftWinch, rightWinch;

    private final DoubleSolenoid pistons;
    private Value targetPistonState = Value.kReverse;
    private Value previousPistonState = Value.kOff;

    private double targetPosition = 0.0;
    private final Range sensorRange = new Range(0, 40000);
    private final double climbRate = sensorRange.size() / (50 * 4);

    private static Climber instance = null;

    public static Climber getInstance() {
        if (instance == null) {
            instance = new Climber();
        }

        return instance;
    }

    public Climber() {
        leftWinch = new TalonSRX(ClimberConfiguration.leftWinchPort);
        leftWinch.setInverted(ClimberConfiguration.leftWinchInverted);

        leftWinch.config_kP(0, 0.2, 0);
        leftWinch.config_kI(0, 0.0, 0);

        leftWinch.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        leftWinch.setSensorPhase(ClimberConfiguration.leftSensorPhase);
        leftWinch.setSelectedSensorPosition(0);

        rightWinch = new TalonSRX(ClimberConfiguration.rightWinchPort);
        rightWinch.setInverted(ClimberConfiguration.rightWinchInverted);

        rightWinch.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        rightWinch.setSensorPhase(ClimberConfiguration.rightSensorPhase);
        rightWinch.setSelectedSensorPosition(0);

        rightWinch.config_kP(0, 0.2, 0);
        rightWinch.config_kI(0, 0.0, 0);

        pistons = new DoubleSolenoid(ClimberConfiguration.forwardSolenoidPort,
                ClimberConfiguration.reverseSolenoidPort);
        pistons.set(Value.kReverse);
    }

    @Override
    public void initialize() {
    }

    public void driveUp() {
        targetPosition += climbRate;
    }

    public void driveDown() {
        targetPosition -= climbRate;
    }

    public void stop() {
    }

    @Override
    public void updateDashboard() {
    }

    @Override
    public void updateInputs() {
        double position = 0.5 * (leftWinch.getSelectedSensorPosition() + rightWinch.getSelectedSensorPosition());

        if (position < 4096 && targetPosition < 4096) {
            targetPistonState = Value.kReverse;
        } else {
            targetPistonState = Value.kForward;
        }
    }

    @Override
    public void updateOutputs() {
        targetPosition = sensorRange.clamp(targetPosition);
        leftWinch.set(ControlMode.Position, targetPosition);
        rightWinch.set(ControlMode.Position, targetPosition);

        if (targetPistonState != previousPistonState) {
            pistons.set(targetPistonState);
            previousPistonState = targetPistonState;
        }
    }

    @Override
    public void test(double currentTestTime) {
    }
}
