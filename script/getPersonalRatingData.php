<?php
include 'config.php';
$con=mysqli_connect($sn,$un,$pw,$db);

if (mysqli_connect_errno($con)) {
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

mysqli_set_charset($con,"utf8");
$res = mysqli_query($con,"SELECT rating FROM user_room_rating WHERE roomSrl = ".$_POST['document_srl']." and user_id = '".$_POST['phone']."';");

$result = array();

while($row = mysqli_fetch_array($res)){
  array_push($result, array('rate'=>$row[0]));
}

echo json_encode(array("result"=>$result));

mysqli_close($con);

?>
