<?php
include 'config.php';
$conn=mysqli_connect($sn,$un,$pw,$db);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sql = "INSERT INTO user_contact_log (srl, phone)
VALUES ('".$_POST['document_srl']."', '".$_POST['phone']."')";

if ($conn->query($sql) === TRUE) {
    echo "TRUE";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>
