<?php
include 'config.php';
$conn=mysqli_connect($sn,$un,$pw,$db);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sql = "INSERT INTO user_room_rating(roomSrl, user_id, rating) VALUES (".$_POST['document_srl'].", '".$_POST['phone']."', ".$_POST['rate'].");";

if ($conn->query($sql) === TRUE) {
    echo "TRUE";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>
