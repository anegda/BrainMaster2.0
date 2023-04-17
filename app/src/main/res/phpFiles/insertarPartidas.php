<?php
	$DB_SERVER="localhost"; #la dirección del servidor
	$DB_USER="Xagarcia794"; #el usuario para esa base de datos
	$DB_PASS="JSnuMAuKR"; #la clave para ese usuario
	$DB_DATABASE="Xagarcia794_brainmaster"; #la base de datos a la que hay que conectarse
	
	# Se establece la conexión:
	$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
	
	#Comprobamos conexión
	if (mysqli_connect_errno()) {
		echo 'Error de conexion: ' . mysqli_connect_error();
		exit();
	}
	
	$usuario = $_GET["usuario"];
	$puntos = $_GET["puntos"];
	$tipo = $_GET["tipo"];
	$latitud = $_GET["latitud"];
	$longitud = $_GET["longitud"];
	
	$resultado = mysqli_query($con, "INSERT INTO Partidas(usuario, puntos, tipo, latitud, longitud) VALUES ('$usuario', $puntos, '$tipo','$latitud','$longitud')");
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}	
	
	mysqli_close($con);	
?>