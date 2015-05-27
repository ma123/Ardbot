#define HAVE_COMPASS 1
#define HAVE_ULTRASONIC 1
#define HAVE_MOTORS 1
#define HAVE_HC02 1

#include <Arduino.h>
#include <SoftwareSerial.h>

//#define PID_P  0.2
//#define PID_I  3.0
//#define PID_D  0.004
#define PID_P  1
#define PID_I  0.0
#define PID_D  0.02

#define LEDPIN 22                 // led pin 

#ifdef HAVE_MOTORS
#include "Motor.h"
#endif

#ifdef HAVE_HC02
#define leftEncoderPin 18 // 35 povodne
#define rightEncoderPin 19  // 36 povodne
#endif

#ifdef HAVE_COMPASS
#include <Wire.h>
#include <HMC5883L_Simple.h>     // compass
#endif

#ifdef HAVE_ULTRASONIC
#include <NewPing.h>              // sonic distance
#endif

/* bluetooth macro value start */
#define BTRXPIN  50
#define BTTXPIN  51
SoftwareSerial btSerial =  SoftwareSerial(BTRXPIN, BTTXPIN);

#define WORD_SIZE 1
#define DATA_SIZE WORD_SIZE+1
char inData[DATA_SIZE]; // string received on the serial port
/* bluetooth macro value end */

/* motor macro start*/
#ifdef HAVE_MOTORS
// obvod kolesa 21.24 cm
// pocet dielikov v encoderi 20 
Motor *motor;
#endif
/* motor macro end*/

/* compass macro and value start*/
#ifdef HAVE_COMPASS
#define COMPASS_VARIANT_SLOW 2  // odchylka kompasu berie hodnotu + 3 - 3
#define COMPASS_VARIANT_FAST 20
HMC5883L_Simple Compass;
#endif
/*comaps macro and value end*/

/* ultrasonic macro and value start */
#ifdef HAVE_ULTRASONIC
#define POCET_MERANI_ULTRASONIC 50
#define CHYBA_MERANIA 3
#define echoPinPred 24 // Echo Pin
#define trigPinPred 25 // Trigger Pin
NewPing sonarPredna(trigPinPred, echoPinPred, 500);  //Inicializacia senzora na max 500 cm vzdialenost
int valuePredna = 0;
int vysledokPredna = 0;

#define echoPinLava 28 // Echo Pin
#define trigPinLava 29 // Trigger Pin
NewPing sonarLava(trigPinLava, echoPinLava, 500);
int valueLava = 0;
int vysledokLava = 0;

#define echoPinPrava 30 // Echo Pin
#define trigPinPrava 31 // Trigger Pin
NewPing sonarPrava(trigPinPrava, echoPinPrava, 500);
int valuePrava = 0;
int vysledokPrava = 0;

#define echoPinZadna 32 // Echo Pin
#define trigPinZadna 33 // Trigger Pin
NewPing sonarZadna(trigPinZadna, echoPinZadna, 500);
int valueZadna = 0;
int vysledokZadna = 0;

int ultrasonicArray[3];
#endif
/* ultrasonic macro and value end */

void setup() {
  Serial.begin(9600);
  pinMode(BTRXPIN, INPUT);
  pinMode(BTTXPIN, OUTPUT);
  btSerial.begin(9600);
  pinMode(LEDPIN, OUTPUT);
#ifdef HAVE_HC02
  pinMode(leftEncoderPin, INPUT);
  pinMode(rightEncoderPin, INPUT);
#endif
#ifdef HAVE_COMPASS
  Compass.SetDeclination(4, 34, 'E');
  Compass.SetSamplingMode(COMPASS_SINGLE);
  Compass.SetScale(COMPASS_SCALE_130);
  Compass.SetOrientation(COMPASS_HORIZONTAL_X_NORTH);
#endif
  digitalWrite(LEDPIN, HIGH);
  delay(500);
  digitalWrite(LEDPIN, LOW);
}

void loop() {
  int bytes_read = 0;
  while (bytes_read < WORD_SIZE) { // cyklus primajuci 1 byte
    if (btSerial.available() > 0) { // ak je btSerial dostupny ide dalej
      inData[bytes_read] = btSerial.read();
      bytes_read++;
    }
  }
  inData[bytes_read] = 0; // na koniec prikazu ide 0
  
  processCommand();
  //rotateAlfa(45);
  //rotateStop(2000);
}

/*Vyberie zhodny prikaz*/
void processCommand() {
  if (strcmp(inData, "L") == 0) {
    processLed();
  } 
  else if (strcmp(inData, "M") == 0) {
    processMove();
  } 
  else if (strcmp(inData, "R") == 0) {
    processRotate();
  }
  else if (strcmp(inData, "K") == 0) {
    btSerial.println((int) compassValue());
  } 
  else if (strcmp(inData, "U") == 0) {
    processUltrasonic();
  }
}

#define POMS_SIZE 32
char poms[POMS_SIZE];

void readUntil(char c) {
  char x = 0, i = 0;
  Serial.println("------------------------");
/*  while (x!=c && i < POMS_SIZE) {
    x = btSerial.read();
    poms[i++] = x;
  };
  poms[i] = '\0';*/
  do {
    if (btSerial.available()>0) {
      delay(15);
      int serAva = btSerial.available();
      while(i < serAva && i < POMS_SIZE) {
        poms[i] = btSerial.read();
        i++;
      }
//      delay(15);
    }
  } while (poms[i-1]!=c);
  poms[i] = '\0';
  Serial.println(poms);
}

/********************************** LED ****************************************/

// ak pride cez bt T svieti ak F nesvieti led
void processLed() {
  char state = btSerial.read();
  if (state == 'T') {
    digitalWrite(LEDPIN, HIGH);
  } 
  else if (state == 'F') {
    digitalWrite(LEDPIN, LOW);
  }
}

/********************************** MOTOR ****************************************/
#define TMP_SIZE 5000
char tmps[TMP_SIZE];
int tmpidx = 0;
void tmp_append(char *s) {
  if (tmpidx + strlen(s) >= TMP_SIZE)
    return;
//  Serial.print(s);
  strcat(tmps, s);
}
void tmp_append(float f) {
  char s[16];
  sprintf(s, "%i", (int)(f*10000));
  tmp_append(s);
}
void tmp_append(int i) {
  char s[16];
  sprintf(s, "%i", i);
  tmp_append(s);
}

// caka sa F/B cislo F/B cislo
void processMove() {
  // parsuj vstup
  readUntil('.');
  int j = 0, k = 0;
  int s1 = (poms[k]=='F'?FORWARD:(poms[k]=='B'?BACKWARD:RELEASE));
  j = ++k; while (poms[k]>='0' && poms[k]<='9' && k<POMS_SIZE) k++;
  int s2 = (poms[k]=='F'?FORWARD:(poms[k]=='B'?BACKWARD:RELEASE));
  poms[k] = 0;
  int r1 = atoi(poms+j);     // rychlost 1
  j = ++k; while (poms[k]>='0' && poms[k]<='9' && k<POMS_SIZE) k++;
  char t = poms[k];          // typ cakania
  poms[k] = 0;
  int r2 = atoi(poms+j);     // rychlost 2
  j = ++k; while (poms[k]>='0' && poms[k]<='9' && k<POMS_SIZE) k++;
  poms[k] = 0;
  int w = atoi(poms+j);      // mnozstvo cakania
  if (t=='R') {
    w = w * 2;
  }
  Serial.println(s1);
  Serial.println(r1);
  Serial.println(s2);
  Serial.println(r2);
  Serial.println(t);
  Serial.println(w);
  // pusti motory
  Serial.println("pusti");
  motor->motor(3, s1, r1);
  motor->motor(4, s2, r2);
  // cakaj a pocitaj otocenia motorov
  if (t=='R' && r1<=0 && r2<=0) {
    // koniec
    Serial.println("zastav");
    btSerial.println(0);
    Serial.println(0);
    btSerial.println(0);
    Serial.println(0);
    return;
  }
  Serial.println("cakaj");
#ifdef HAVE_HC02
  unsigned long tstart = millis(), tnow;
  int lpoc = 0, rpoc = 0, lastl, lastr, x, c = 0;
  float lp_prev, rp_prev, li_sum = 0, ri_sum = 0;
  int counter = 0; tmps[0] = 0;
  while (true) {
    tnow = millis();
    if (t=='T' && tnow-tstart > w) {
      break;
    }
    if (t=='R' && lpoc >= w && rpoc >= w) {
      break;
    }
    // pocitaj lave
    x = digitalRead(leftEncoderPin);
    if (x!=lastl) {
      lastl = x;
      lpoc++;
      if (t=='R' && lpoc >= w) {
        motor->motor(3, FORWARD, 0);
      }
    }
    // pocitaj prave
    x = digitalRead(rightEncoderPin);
    if (x!=lastr) {
      lastr = x;
      rpoc++;
      if (t=='R' && rpoc >= w) {
        motor->motor(4, FORWARD, 0);
      }
    }
    // synchronizacia rychlosti krutenia kolies (PID control)
    if (r1==r2) {
      float lfreq = (float)lpoc/(tnow-tstart);
      float rfreq = (float)rpoc/(tnow-tstart);
      float afreq = (lfreq+rfreq)/2.0f;
      if (!(counter%100)) { tmp_append("freq:"); tmp_append(lfreq); tmp_append(","); tmp_append(rfreq); tmp_append(","); tmp_append(afreq); tmp_append("\n"); }
      c++;
      if (c>5) {
        // lave
        float lp = - PID_P * (lfreq - afreq);
        float li = - PID_I * li_sum; li_sum += (lfreq - afreq);
        float ld = - PID_D * ((lfreq - afreq) - lp_prev);
        float lpid = lp + li + ld;
        if (!(counter%100)) { tmp_append("lpid:"); tmp_append(lp); tmp_append(","); tmp_append(li); tmp_append(","); tmp_append(ld); tmp_append("=>"); tmp_append(lpid); tmp_append("\n"); }
        // prave
        float rp = - PID_P * (rfreq - afreq);
        float ri = - PID_I * ri_sum; ri_sum += (rfreq - afreq);
        float rd = - PID_D * ((rfreq - afreq) - rp_prev);
        float rpid = rp + ri + rd;
        if (!(counter%100)) { tmp_append("rpid:"); tmp_append(rp); tmp_append(","); tmp_append(ri); tmp_append(","); tmp_append(rd); tmp_append("=>"); tmp_append(rpid); tmp_append("\n"); }
        // uprav rychlost motora
        if (lpid>0) r1++; else r1--;
        if (rpid>0) r2++; else r2--;
        motor->motor(3, s1, r1);
        motor->motor(4, s2, r2);
      }
      counter++;
      lp_prev = lfreq - afreq;
      rp_prev = rfreq - afreq;
      // TODO
    }
  }
  Serial.println(tmps);
#endif
  // zastav motory
  Serial.println("zastav");
  motor->motor(3, FORWARD, 0);
  motor->motor(4, FORWARD, 0);
  // posli naspat velkost otocenia
  lpoc = lpoc/2;
  rpoc = rpoc/2;
  btSerial.println(lpoc);
  Serial.println(lpoc);
  btSerial.println(rpoc);
  Serial.println(rpoc);
}

void processRotate() {
  readUntil('.');
  int j = 0, k = 0;
  if (poms[k]=='-') k++;
  while (poms[k]>='0' && poms[k]<='9' && k<POMS_SIZE) k++;
  poms[k] = 0;
  int angleAlfa = atoi(poms+j);     // rychlost 1
  while (angleAlfa<0) {
    angleAlfa = 360 + angleAlfa;
  }

/*  int i = 0, serAva = 0;
  char inputBytes[3];

  if (btSerial.available()>0) {
    delay(5);                            
    serAva = btSerial.available();  
    while(i < serAva) {
      inputBytes[i] = btSerial.read();
      Serial.println(inputBytes[i]);
      i++;
    }
    inputBytes[i] = '\0';
  }

  int angleAlfa = atoi(inputBytes);*/
  rotateAlfa(angleAlfa);
}

// otoci robota o uhol alfa
void rotateAlfa(int angleAlfa) {
#ifdef HAVE_COMPASS
  /*int headOld = (int) compassValue();
  Serial.println("HeadOLD ");
  Serial.println(headOld);

  Serial.println("Tocim sa ");
  motor->motor(3, FORWARD, 130);
  motor->motor(4, BACKWARD, 130);
  
  headOld = (headOld + angleAlfa) % 360;
  while(true) {
    int headNew = (int) compassValue();
    Serial.println("HeadNew ");
    Serial.println(headNew);
  
    if((headNew >= (hedOld - COMPASS_VARIANT)) && (headNew <= (headOld + COMPASS_VARIANT))) {
       break;
    }
  }
  Serial.println("Som otoceny");
  motor->motor(3, RELEASE, 0);
  motor->motor(4, RELEASE, 0);*/

  int headOld = (int) compassValue();
  headOld = (headOld + angleAlfa) % 360;
  Serial.println("Compass head2");
  Serial.println(headOld);
  
  int headToogle = (headOld + 180) % 360;
  Serial.println("Toogle: ");
  Serial.println(headToogle);
  
  while(true) {
     int headNew = (int) compassValue();
     if((headNew >= (headOld - COMPASS_VARIANT_SLOW)) && (headNew <= (headOld + COMPASS_VARIANT_SLOW))) {
         rotateStop(200);
         break;
     } else { 
        if((headNew >= (headOld - COMPASS_VARIANT_FAST)) && (headNew <= (headOld + COMPASS_VARIANT_FAST))) {
             rotateRight(20);
        } else {
           rotateRight(80);
        }
    }
    rotateStop(20);
  }
#endif
}

void rotateRight(int wait) {
  Serial.println("Otacam sa o alfa vpravo idem pomalsie");
  motor->motor(3, FORWARD, 255);
  motor->motor(4, BACKWARD, 255);
  delay(wait);
} 

void rotateLeft(int wait) {
  Serial.println("Otacam sa o alfa vlavo idem pomalsie");
  motor->motor(3, BACKWARD, 255);
  motor->motor(4, FORWARD, 255);
  delay(wait);
} 

void rotateStop(int wait) {
  Serial.println("Robot stop neotacam sa");
  motor->motor(3, RELEASE, 0);
  motor->motor(4, RELEASE, 0);
  delay(wait);
}

/********************************** KOMPAS ****************************************/

float compassValue() {
#ifdef HAVE_COMPASS
  float compassDegrees = Compass.GetHeadingDegrees();
  return compassDegrees;
#endif
}

/********************************** ULTRASONIC ****************************************/

// vrati hodnoty 4 ultrasonic senzorov
void processUltrasonic() {   
  ultrasonicSensorValue();
  for (int i = 0; i < 4; i++) { // posle pole 4 int hodnoty ultrasonic senzoru
    btSerial.println(ultrasonicArray[i]);
    Serial.println(ultrasonicArray[i]);
  }
}

void ultrasonicSensorValue() {
#ifdef HAVE_ULTRASONIC
  int i = 0;
  vysledokPredna = 0;
  vysledokLava = 0;
  vysledokPrava = 0;
  vysledokZadna = 0;

  while (i < POCET_MERANI_ULTRASONIC) {
    valuePredna = sonarPredna.ping_cm();
    valueLava = sonarLava.ping_cm();
    valuePrava = sonarPrava.ping_cm();
    valueZadna = sonarZadna.ping_cm();

    vysledokPredna += valuePredna;
    vysledokLava += valueLava;
    vysledokPrava += valuePrava;
    vysledokZadna += valueZadna;

    i++;
  }

  ultrasonicArray[0] = vysledokLava / POCET_MERANI_ULTRASONIC;
  ultrasonicArray[1] = vysledokPredna / POCET_MERANI_ULTRASONIC;
  ultrasonicArray[2] = vysledokPrava / POCET_MERANI_ULTRASONIC;
  ultrasonicArray[3] = vysledokZadna / POCET_MERANI_ULTRASONIC;
#endif
}

