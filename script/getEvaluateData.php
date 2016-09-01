<?php
include 'config.php';

$con=mysqli_connect($sn,$un,$pw,$db);

if (mysqli_connect_errno($con)) {
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

mysqli_set_charset($con,"utf8");
$res = mysqli_query($con,"select * from user_comment_log where document_srl = ".$_POST['document_srl']." order by date desc;");

$result = array();

while($row = mysqli_fetch_array($res)){
  array_push($result, array('id'=>$row[0], 'phone'=>$row[2], 'data'=>$row[3]));
}

echo json_encode(array("result"=>$result));

mysqli_close($con);

?>
