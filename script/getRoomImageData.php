<?php
include 'config.php';
$con=mysqli_connect($sn,$un,$pw,$db);

if (mysqli_connect_errno($con)) {
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

mysqli_set_charset($con,"utf8");
$res = mysqli_query($con,"select uploaded_filename from xe_files where upload_target_srl = 
".$_POST['document_srl']." and (uploaded_filename like '%.jpg' or uploaded_filename like '%.png');");

$result = array();

while($row = mysqli_fetch_array($res)){
  array_push($result, array('file'=>$row[0]));
}

echo json_encode(array("result"=>$result));

mysqli_close($con);

?>
