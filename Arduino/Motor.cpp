#include "Motor.h"

Motor::Motor() {
}

void Motor::motor(int nMotor, int command, int speed) {
  int motorA, motorB;
  switch (nMotor) {
  case 3:
    motorA   = MOTOR3_A;
    motorB   = MOTOR3_B;
    break;
  case 4:
    motorA   = MOTOR4_A;
    motorB   = MOTOR4_B;
    break;
  default:
    break;
  }

  switch (command) {
  case FORWARD:
    motor_output (motorA, HIGH, speed);
    motor_output (motorB, LOW, -1);     // -1: no PWM set
    break;
  case BACKWARD:
    motor_output (motorA, LOW, speed);
    motor_output (motorB, HIGH, -1);    // -1: no PWM set
    break;
  case RELEASE:
    motor_output (motorA, LOW, 0);  // 0: output floating.
    motor_output (motorB, LOW, -1); // -1: no PWM set
    break;
  default:
    break;
  }  
}

void Motor::motor_output (int output, int high_low, int speed) {
  int motorPWM;

  switch (output) {
  case MOTOR3_A:
  case MOTOR3_B:
    motorPWM = MOTOR3_PWM;
    break;
  case MOTOR4_A:
  case MOTOR4_B:
    motorPWM = MOTOR4_PWM;
    break;
  default:
    speed = -3333;
    break;
  }

  if (speed != -3333) {
    shiftWrite(output, high_low);
    if (speed >= 0 && speed <= 255) {
      analogWrite(motorPWM, speed);
    }
  }
}

void Motor::shiftWrite(int output, int high_low) {
  static int latch_copy;
  static int shift_register_initialized = false;

  if (!shift_register_initialized) {
    pinMode(MOTORLATCH, OUTPUT);
    pinMode(MOTORENABLE, OUTPUT);
    pinMode(MOTORDATA, OUTPUT);
    pinMode(MOTORCLK, OUTPUT);

    digitalWrite(MOTORDATA, LOW);
    digitalWrite(MOTORLATCH, LOW);
    digitalWrite(MOTORCLK, LOW);
    digitalWrite(MOTORENABLE, LOW);
    latch_copy = 0;
    shift_register_initialized = true;
  }
  bitWrite(latch_copy, output, high_low);
  shiftOut(MOTORDATA, MOTORCLK, MSBFIRST, latch_copy);
  delayMicroseconds(5);    // For safety, not really needed.
  digitalWrite(MOTORLATCH, HIGH);
  delayMicroseconds(5);    // For safety, not really needed.
  digitalWrite(MOTORLATCH, LOW);
}

