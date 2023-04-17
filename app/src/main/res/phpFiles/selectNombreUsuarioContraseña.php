<?php
	$DB_SERVER="localhost"; #la dirección del servidor
	$DB_USER="Xagarcia794"; #el usuario para esa base de datos
	$DB_PASS="JSnuMAuKR"; #la clave para ese usuario
	$DB_DATABASE="Xagarcia794_brainmaster"; #la base de datos a la que hay que conectarse
	
	# Se establece la conexión:
	$link = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
	
	#Comprobamos conexión
	if (mysqli_connect_errno()) {
		echo 'Error de conexion: ' . mysqli_connect_error();
		exit();
	}
	

	// Attempt select query execution
	$usuario = $_GET["usuario"];
	$sql = "SELECT * FROM Usuarios WHERE usuario='$usuario'";
	if($result = mysqli_query($link, $sql)){
		if(mysqli_num_rows($result) > 0){
			$row = mysqli_fetch_array($result);
			if(password_verify($_GET["password"], $row['password'])){
				$arrayresultados = array(
					'nombre' => $row['nombre'],
					'apellidos' => $row['apellidos'],
					'usuario' => $row['usuario'],
					'password' => $row['password'],
					'email' => $row['email'],
					'fechaNac' => $row['fechaNac'],
					'img' => $row['img']
				);
				// Return result
				echo json_encode($arrayresultados);
				// Free result set
				mysqli_free_result($result);
			}else{
				echo "No records matching your query were found.";
			}
		} else{
			echo "No records matching your query were found.";
		}
	} else{
		echo "ERROR: Could not able to execute $sql. " . mysqli_error($link);
	}
	 
	// Close connection
	mysqli_close($link);
?>