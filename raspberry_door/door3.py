from flask import Flask, render_template, request
from multiprocessing import Process,Queue
import RPi.GPIO as GPIO
import socket
import serial
import time
import threading
import sys
import pexpect
import time  
import picamera
import MySQLdb
import os

gd = 0
class receiverThread(threading.Thread): #R
	def __init__(self):
		threading.Thread.__init__(self)
	def run(self):
                global gd
		global msg
		while 1:
			a = con.readline()
			print a
			c = a.split(' ')

			if len(c)>=13:
				if c[8]=="13": 
					con.sendline('char-write-cmd 0x0013 ' + frame("93","00"))
					con.sendline('char-write-cmd 0x0013 ' + frame("95","00"))
				elif c[8]=="14":
					con.sendline('char-write-cmd 0x0013 ' + frame("94","00"))
					con.sendline('char-write-cmd 0x0013 ' + frame("95","00"))
				elif c[8]=="15":
					if c[9]=="06":
						con.sendline('char-write-cmd 0x0013 ' + frame("95","06"))
						db()
						gd = 1
						
					elif c[9]=="04":
						con.sendline('char-write-cmd 0x0013 ' + frame("95","04"))
						db()
						gd = 2
						
				elif c[8]=="16":
					con.sendline('char-write-cmd 0x0013 ' + frame("96","00"))
					con.sendline('char-write-cmd 0x0013 ' + frame("95","00"))

def frame(CMD,PARAM):
	SOF = "72"
	EOF = "0f"
	SRC = "91"
	CHKSUM = "%02x" % (int(SRC,16)^int(CMD,16)^int(PARAM,16))
	FRA = SOF + SRC + CMD + PARAM + CHKSUM + EOF
	return FRA
def insertDB():
        global gd
        global MAC
        while 1:
                if gd ==1:
                        print"insertDB start"
                        op=pexpect.spawn("php /var/www/html/insert.php open "+ MAC)
                        gd=0
                elif gd ==2:
                        print"insertDB start"
                        cl=pexpect.spawn("php /var/www/html/insert.php close "+ MAC)
                        gd=0
                                
def GCM():
        global gd
        global MAC
        while 1:
                if gd ==1:
                        print"GCM start"
                        gcm=pexpect.spawn("java -cp gcm-server.jar:json-simple-1.1.1.jar:. sendGCM "+ MAC +" On")
                        gd=0
def inter():
        global MAC
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(5,GPIO.IN)
        GPIO.setup(21,GPIO.OUT)

        os.system("amixer cset numid=3 1")
        os.system("amixer cset numid=1 100%")

        call = pexpect.spawn('twinkle -c')
        time.sleep(2)
        call.sendline(' ')
	while 1:
		GPIO.output(21,False)
		if GPIO.input(5)==0:
			print GPIO.input(5)
			os.system('sudo aplay doorbell.wav')
			phone = pexpect.spawn("java getPhoneNumber " + MAC)
                        st = phone.readline()
			call.sendline('call ' + st)
			GPIO.output(21,True)
			time.sleep(1.5)
			
t1 = threading.Thread(target=GCM)
t2 = threading.Thread(target=insertDB)
t3 = threading.Thread(target=inter)
t1.start()
t2.start()

t3.start()

def db():
	#os.system("rm index.html?action=snapshot.jpg")
	os.system("wget http://127.0.0.1:8080/?action=snapshot.jpg -O snapshot.jpg")
app = Flask(__name__)

 
GPIO.setmode(GPIO.BCM)

flag=0
leds = {

24 : {'name' : 'DOORLOCK', 'state' : GPIO.LOW}

}

for led in leds :
	GPIO.setup(led, GPIO.OUT)
	GPIO.output(led, GPIO.LOW)
 
def getGpioState():
	for led in leds:
		leds[led]['state'] = GPIO.input(led)

	return leds
 
@app.route("/")
def main():
	gpioState = {
		'leds' : getGpioState()
}
 
	return render_template('main.html', **gpioState)

MAC = sys.argv[1] 
con = pexpect.spawn('gatttool -b ' + MAC + ' -I')
con.timeout=5000000

@app.route("/<led>/<act>")
def action(led, act):
	global flag
	global gd
	print flag
	led = int(led)
	leds = getGpioState()
	dev = leds[led]['name']
	if flag==0:
		con.sendline('connect')
		receiver = receiverThread()
		receiver.setDaemon(True)
		receiver.start()
		flag=1
	if act == "on":
		GPIO.output(led, GPIO.HIGH)
		msg = dev +" is OPENED"
		print("led ON")
		con.sendline('char-write-req 0x0020 041b')
		con.sendline('char-write-cmd 0x0013 ' + frame("11","00"))
		db()
		gd = 1
	elif act == "off":
		GPIO.output(led, GPIO.LOW)
		msg = dev +" is LOCKED"
		print("led OFF")
		con.sendline('char-write-req 0x0020 041b')
		con.sendline('char-write-cmd 0x0013 ' + frame("12","00"))
		db()
		gd = 2
		
	elif act == "toggle":
		GPIO.output(led, not GPIO.input(led))
		msg = "Toggled " + dev + "."
	else:
		msg = "Undefined action!"
 
	gpioState = {
		'msg' : msg,
		'leds' : getGpioState()
	}
 
	return render_template('main.html', **gpioState)
 
if __name__ == "__main__":
	app.run(host='0.0.0.0', port=8888, debug=True)




