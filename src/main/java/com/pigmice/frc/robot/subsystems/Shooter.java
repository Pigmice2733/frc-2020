package com.pigmice.frc.robot.subsystems;

import com.pigmice.frc.lib.controllers.TakeBackHalf;
import com.pigmice.frc.lib.motion.setpoint.ISetpoint;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
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

    private final CANSparkMax motor;
    private final CANEncoder encoder;
    private DoubleSolenoid hoodSolenoid;

    private final double reduction = 1.0/1.5;

    private double shooterRPM = 0.0;
    private double shooterVoltage = 0.0;
    private Value hoodState = Value.kOff;
    private Value previousHoodState = Value.kOff;

    //private ShooterSetpoint targetRPM = new ShooterSetpoint(4000, 0.5);
    private ShooterSetpoint targetRPM = new ShooterSetpoint(6200, 0.85);

    private boolean runWheels = false;

    private final TakeBackHalf controller = new TakeBackHalf(0.5e-5, 0.8);

    public Shooter(CANSparkMax motor, DoubleSolenoid hoodSolenoid) {
        this.motor = motor;
        this.hoodSolenoid = hoodSolenoid;
        encoder = motor.getEncoder();

        encoder.setVelocityConversionFactor(1.0/reduction);
    }

    @Override
    public void initialize() {
        shooterRPM = 0.0;
        shooterVoltage = 0.0;
        hoodState = hoodSolenoid.get();
        previousHoodState = hoodState;

        controller.updateTargetOutput(targetRPM.output);
        controller.initialize(0.0, 1.0);

        SmartDashboard.putNumber("Shooter target", 0.0);
        updateDashboard();
    }

    public void run(boolean run) {
        if(run && !runWheels) {
            controller.initialize(shooterRPM, 1.0);
        }

        runWheels = run;
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
        double output = runWheels ? controller.calculateOutput(shooterRPM, targetRPM) : 0.0;

        shooterVoltage = output * 100.0;
        motor.set(output);

        if(hoodState != previousHoodState) {
            hoodSolenoid.set(hoodState);
            previousHoodState = hoodState;
        }
    }

    @Override
    public void test() {
    }
}
