package com.pigmice.frc.robot;

import com.pigmice.frc.lib.inputs.Debouncer;
import com.pigmice.frc.lib.inputs.IBooleanSource;
import com.pigmice.frc.lib.inputs.Toggle;

import edu.wpi.first.wpilibj.Joystick;

public class Controls {
    private static class ButtonSource implements IBooleanSource {
        public static interface Button {
            public boolean get();
        }

        private final Button button;

        public ButtonSource(Button button) {
            this.button = button;
        }

        @Override
        public boolean get() {
            return button.get();
        }

        @Override
        public void update() {
        }

    }

    private interface ControllerProfile {
        boolean demoMode();
        double driveSpeed();
        double turnSpeed();
        boolean intake();
        boolean shoot();
        boolean feed();
        boolean toggleIntake();
        boolean toggleHood();
    }

    private class EasySMX implements ControllerProfile {
        private final Joystick joystick;

        public EasySMX(Joystick joystick) {
            this.joystick = joystick;
        }

        @Override
        public boolean demoMode() {
            return joystick.getRawButton(5);
        }

        @Override
        public double driveSpeed() {
            return joystick.getRawAxis(1);
        }

        @Override
        public double turnSpeed() {
            return joystick.getRawAxis(2);
        }

        @Override
        public boolean intake() {
            return joystick.getRawButton(3);
        }

        @Override
        public boolean shoot() {
            return joystick.getRawButton(8);
        }

        @Override
        public boolean feed() {
            return joystick.getRawButton(6);
        }

        @Override
        public boolean toggleIntake() {
            return joystick.getRawButton(3);
        }

        @Override
        public boolean toggleHood() {
            return joystick.getRawButton(3);
        }
    }

    private class XBox implements ControllerProfile {
        private final Joystick joystick;

        public XBox(Joystick joystick) {
            this.joystick = joystick;
        }

        @Override
        public boolean demoMode() {
            return joystick.getRawButton(5);
        }

        @Override
        public double driveSpeed() {
            return joystick.getRawAxis(1);
        }

        @Override
        public double turnSpeed() {
            return joystick.getRawAxis(4);
        }

        @Override
        public boolean intake() {
            return joystick.getRawButton(3);
        }

        @Override
        public boolean shoot() {
            return joystick.getRawAxis(3) > 0.75;
        }

        @Override
        public boolean feed() {
            return joystick.getRawButton(6);
        }

        @Override
        public boolean toggleIntake() {
            return joystick.getRawButton(1);
        }

        @Override
        public boolean toggleHood() {
            return joystick.getRawButton(1);
        }
    }

    ControllerProfile controller;
    Toggle intakeToggle;
    Toggle hoodToggle;

    Debouncer intakeDebouncer, hoodDebouncer;

    public Controls() {
        Joystick joystick = new Joystick(0);

        if(joystick.getName().equals("EasySMX CONTROLLER")) {
            controller = new EasySMX(joystick);
        } else if(joystick.getName().equals("Controller (XBOX 360 For Windows)")){
            controller = new XBox(joystick);
        } else {
            controller = new XBox(joystick);
        }

        intakeDebouncer = new Debouncer(new ButtonSource(controller::toggleIntake));
        hoodDebouncer = new Debouncer(new ButtonSource(controller::toggleHood));

        intakeToggle = new Toggle(intakeDebouncer);
        hoodToggle = new Toggle(hoodDebouncer);
    }

    public void initialize() {
        intakeToggle.set(false);
        hoodToggle.set(false);
    }

    public void update() {
        intakeDebouncer.update();
        hoodDebouncer.update();

        intakeToggle.update();
        hoodToggle.update();
    }

    public double turnSpeed() {
        final double steering = controller.turnSpeed();
        return Math.pow(steering, 2) * Math.signum(steering);
    }

    public double driveSpeed() {
        return -controller.driveSpeed();
    }

    public boolean demoMode() {
        return false;
    }

    public boolean intake() {
        return controller.intake();
    }

    public boolean shoot() {
        return controller.shoot();
    }

    public boolean feed() {
        return controller.feed();
    }

    public boolean extendHood() {
        return hoodToggle.get();
    }

    public boolean dropIntake() {
        return intakeToggle.get();
    }
}
