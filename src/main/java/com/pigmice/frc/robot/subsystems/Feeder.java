package com.pigmice.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.pigmice.frc.robot.subsystems.System.FeederConfiguration;

public class Feeder implements ISubsystem {
    public enum LiftAction {
        FEED(0.65),
        GRAB(0.25),
        BACKFEED(-0.2),
        HOLD(0.0);

        public final double speed;

        private LiftAction(double speed) {
            this.speed = speed;
        }
    }

    private final TalonSRX hopperMotor;
    private final TalonSRX liftMotor;

    private final double hopperSpeed = 0.5;

    private LiftAction liftAction = LiftAction.HOLD;
    private boolean runHopper = false;

    private static Feeder instance = null;

    public static Feeder getInstance() {
        if(instance == null) {
            instance = new Feeder();
        }

        return instance;
    }

    public Feeder() {
        liftMotor = new TalonSRX(FeederConfiguration.liftLeaderMotorPort);
        liftMotor.setInverted(FeederConfiguration.liftInverted);

        TalonSRX liftFollower = new TalonSRX(FeederConfiguration.liftFollowerMotorPort);
        liftFollower.follow(liftMotor);
        liftFollower.setInverted(!FeederConfiguration.liftInverted);

        hopperMotor = new TalonSRX(FeederConfiguration.hopperLeaderMotorPort);
        hopperMotor.setInverted(FeederConfiguration.hopperInverted);

        TalonSRX hopperFollower = new TalonSRX(FeederConfiguration.hopperLeaderMotorPort);
        hopperFollower.follow(hopperMotor);
        hopperFollower.setInverted(!FeederConfiguration.hopperInverted);
    }

    @Override
    public void initialize() {
        runHopper = false;
        liftAction = LiftAction.HOLD;
    }

    public void runLift(LiftAction action) {
        this.liftAction = action;
    }

    public void runHopper(boolean run) {
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
    public void test() {
    }
}
