<?php
	$DB_SERVER="localhost"; #la dirección del servidor
	$DB_USER="Xagarcia794"; #el usuario para esa base de datos
	$DB_PASS="JSnuMAuKR"; #la clave para ese usuario
	$DB_DATABASE="Xagarcia794_brainmaster"; #la base de datos a la que hay que conectarse
	
	$link = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
	
	// Check connection
	if($link === false){
		die("ERROR: Could not connect. " . mysqli_connect_error());
	}
	
	$sql2 = "CREATE TABLE Usuarios(
	codigo INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	nombre VARCHAR(255), 
	apellidos VARCHAR(255), 
	usuario VARCHAR(255), 
	password VARCHAR(255), 
	email VARCHAR(255), 
	fechaNac DATE, 
	img LONGBLOB)";
	
	if(mysqli_query($link, $sql2)){
		echo "Tabla usuarios created successfully.";
	} else{
		echo "ERROR: Could not able to execute $sql2. " . mysqli_error($link);
	} 
	
	mysqli_close($link);
	
	$link = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
	
	$sql = "CREATE TABLE Partidas(
    codigo_partida INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255),
	puntos INT,
    tipo VARCHAR(255),
    latitud VARCHAR(255),
	longitud VARCHAR(255))";
	
	if(mysqli_query($link, $sql)){
		echo "Tabla partidas created successfully.";
	} else{
		echo "ERROR: Could not able to execute $sql. " . mysqli_error($link);
	}

	mysqli_close($link);
?>