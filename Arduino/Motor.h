#ifndef _MOTOR_H_ 
#define _MOTOR_H_

#include <stdint.h>
#include <avr/io.h>
// #include <avr/interrupt.h>
#include <avr/pgmspace.h>
#include "Arduino.h"

#define MOTORLATCH 12
#define MOTORCLK 4
#define MOTORENABLE 7
#define MOTORDATA 8

#define MOTOR3_A 5
#define MOTOR3_B 7
#define MOTOR4_A 0
#define MOTOR4_B 6

#define MOTOR3_PWM 6
#define MOTOR4_PWM 5

#define FORWARD 1
#define BACKWARD 2
#define RELEASE 3

class Motor {
  
public:
  Motor();
  void motor(int nMotor, int command, int speed);
  void motor_output (int output, int high_low, int speed);
  void shiftWrite(int output, int high_low);
};

#endif // _MOTOR_H_
