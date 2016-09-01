<?php
include 'config.php';
$con=mysqli_connect($sn,$un,$pw,$db);
if (mysqli_connect_errno($con)) {
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

mysqli_set_charset($con,"utf8");
$res = mysqli_query($con,"select var_idx, value from xe_document_extra_vars where document_srl=".
$_POST['document_srl']." order by var_idx;");

$result = array();

while($row = mysqli_fetch_array($res)){
  array_push($result, array('var'=>$row[0], 'value'=>$row[1]));
}

echo json_encode(array("result"=>$result));

mysqli_close($con);

?>
