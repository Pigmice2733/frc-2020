package com.pigmice.frc.robot.subsystems;

import com.pigmice.frc.lib.controllers.TakeBackHalf;
import com.pigmice.frc.lib.motion.setpoint.ISetpoint;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;

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

    private final double reduction = 1.0/1.5;

    private double shooterRPM = 0.0;
    private double shooterVoltage = 0.0;

    //private ShooterSetpoint targetRPM = new ShooterSetpoint(4150, 0.8);
    private ShooterSetpoint targetRPM = new ShooterSetpoint(2700, 0.5);

    private boolean go = false;

    private final TakeBackHalf controller = new TakeBackHalf(0.5e-5, 0.8);

    public Shooter(CANSparkMax motor) {
        this.motor = motor;
        encoder = motor.getEncoder();

        encoder.setVelocityConversionFactor(-1.0/reduction);
    }

    @Override
    public void initialize() {
        shooterRPM = 0.0;
        shooterVoltage = 0.0;
        controller.updateTargetOutput(targetRPM.output);
        controller.initialize(0.0, 1.0);

        SmartDashboard.putNumber("Shooter target", 0.0);
        updateDashboard();
    }

    public void go() {
        if(!go) {
            go = true;
            controller.initialize(shooterRPM, 1.0);
        }
    }

    public void stop() {
        go = false;
    }

    @Override
    public void updateDashboard() {
        SmartDashboard.putNumber("Shooter RPM", shooterRPM);
        SmartDashboard.putNumber("Shooter Voltage", shooterVoltage);
    }

    @Override
    public void updateInputs() {
        shooterRPM = encoder.getVelocity();
    }

    @Override
    public void updateOutputs() {
        double output = 0.0;
        if(go) {
            output = controller.calculateOutput(shooterRPM, targetRPM);
        } else {
            output = 0.0;
        }

        shooterVoltage = output * 100.0;
        motor.set(output);
    }

    @Override
    public void test() {
    }
}
