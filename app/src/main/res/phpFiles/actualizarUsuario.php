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
	$parametros = json_decode( file_get_contents( 'php://input' ), true );
	
	$usuario = $parametros["usuario"];
	$password = password_hash($parametros["password"], PASSWORD_DEFAULT);
	$img = $parametros["img"];
	
	$resultado = mysqli_query($con, "UPDATE Usuarios set password='$password',img='$img' WHERE usuario='$usuario'");
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}	
	
	mysqli_close($link);	
?>