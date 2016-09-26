import RPi.GPIO as GPIO
import time
import pexpect
import os

GPIO.setmode(GPIO.BCM)
GPIO.setup(5,GPIO.IN)
GPIO.setup(21,GPIO.OUT)

os.system("amixer cset numid=3 1")
os.system("amixer cset numid=1 100%")

call = pexpect.spawn('twinkle -c')
time.sleep(2)
call.sendline(' ')
try:
	while 1:
		GPIO.output(21,False)
		if GPIO.input(5)==0:
			print GPIO.input(5)
			os.system('sudo aplay doorbell.wav')
			call.sendline('call 01045382166')
			GPIO.output(21,True)
			time.sleep(1.5)
			
except KeyboardInterrupt:
	GPIO.cleanup()

