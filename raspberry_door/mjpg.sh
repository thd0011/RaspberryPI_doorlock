export STREAMER_PATH=$HOME/mjpg-streamer/mjpg-streamer-experimental
export LD_LIBRARY_PATH=$STREAMER_PATH
sudo modprobe bcm2835-v4l2
cd work/mjpg-streamer/mjpg-streamer
./mjpg_streamer -i "./input_uvc.so -d /dev/video0 -r 1280x960 -f 30 -y" -o "./output_http.so -w ./www"
cd
