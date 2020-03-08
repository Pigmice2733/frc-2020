package com.pigmice.frc.robot.subsystems;

import com.pigmice.frc.robot.subsystems.SystemConfig.ClimberConfiguration;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Climber implements ISubsystem {
    private final DoubleSolenoid pistons;
    private Value targetPistonState = Value.kReverse;
    private Value previousPistonState = Value.kOff;

    private static Climber instance = null;

    public static Climber getInstance() {
        if (instance == null) {
            instance = new Climber();
        }

        return instance;
    }

    public Climber() {
        pistons = new DoubleSolenoid(ClimberConfiguration.forwardSolenoidPort,
                ClimberConfiguration.reverseSolenoidPort);
        pistons.set(Value.kReverse);
    }

    @Override
    public void initialize() {
    }

    public void driveUp() {
        targetPistonState = Value.kForward;
    }

    public void driveDown() {
        targetPistonState = Value.kReverse;
    }

    public void stop() {
    }

    @Override
    public void updateDashboard() {
    }

    @Override
    public void updateInputs() {
    }

    @Override
    public void updateOutputs() {
        if (targetPistonState != previousPistonState) {
            pistons.set(targetPistonState);
            previousPistonState = targetPistonState;
        }
    }

    @Override
    public void test(double currentTestTime) {
    }
}
