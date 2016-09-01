<?php
include 'config.php';
$conn=mysqli_connect($sn,$un,$pw,$db);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sql = "UPDATE user_room_rating SET rating=".$_POST['rate']." WHERE roomSrl = ".$_POST['document_srl']." and user_id = '".$_POST['phone']."';";

if ($conn->query($sql) === TRUE) {
    echo "TRUE";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>
