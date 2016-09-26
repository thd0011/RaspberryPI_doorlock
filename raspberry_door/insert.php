#!/usr/bin/php -q
<?php
	$name = "/home/pi/snapshot.jpg";
	$handle = fopen($name,"r");
	$contents = addslashes(fread($handle,filesize($name)));
	//	fclose($handle);
	
	$connect = mysql_connect('117.16.244.105', 'root', 'dusrntlf8544');
	
	mysql_select_db('doorlock', $connect);

	$query = "insert into door_log values('',now(),now(),'$argv[1]','$contents','$argv[2]')";
	$reslut = mysql_query($query);
?>
