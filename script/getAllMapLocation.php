<?php
include 'config.php';

$con=mysqli_connect($sn,$un,$pw,$db);
if (mysqli_connect_errno($con)) {
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

mysqli_set_charset($con,"utf8");
$res = mysqli_query($con,
"select document_srl, value 
 from xe_document_extra_vars e1 natural join xe_document_extra_vars e2 
 where e1.module_srl = e2.module_srl and e1.document_srl = e2.document_srl and e1.var_idx = e2.var_idx and 
 (e1.var_idx = 17 or e1.var_idx = 18) and module_srl='".$_POST['module_srl']."' order by document_srl asc;");

$result = array();

while($row = mysqli_fetch_array($res)){
  array_push($result, array('srl'=>$row[0], 'value'=>$row[1]));
}

echo json_encode(array("result"=>$result));

mysqli_close($con);

?>
