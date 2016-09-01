<?php
include 'config.php';
$conn=mysqli_connect($sn,$un,$pw,$db);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sql = "DELETE FROM user_comment_log WHERE id = ".$_POST['id'].";";

if ($conn->query($sql) === TRUE) {
    echo "TRUE";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>
