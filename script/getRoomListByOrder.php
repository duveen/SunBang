<?php
include 'config.php';
$con=mysqli_connect($sn,$un,$pw,$db);

if (mysqli_connect_errno($con)) {
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

mysqli_set_charset($con,"utf8");
$res = mysqli_query($con,"select document_srl from xe_documents where module_srl = ".
$_POST['module_srl']." order by priority ASC, last_update DESC;");

$result = array();

while($row = mysqli_fetch_array($res)){
  array_push($result, array('srl'=>$row[0]));
}

echo json_encode(array("result"=>$result));

mysqli_close($con);

?>
