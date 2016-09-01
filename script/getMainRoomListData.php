<?php  
include 'config.php';
$con=mysqli_connect($sn,$un,$pw,$db);

if (mysqli_connect_errno($con)) {  
   echo "Failed to connect to MySQL: " . mysqli_connect_error();  
}

mysqli_set_charset($con,"utf8");  
$res = mysqli_query($con,
"select document_srl, title 
from xe_documents 
WHERE module_srl = ".$_POST['module_srl']." ORDER BY document_srl DESC LIMIT 5;");  
   
$result = array();  
   
while($row = mysqli_fetch_array($res)){  
  array_push($result,  
    array('document_id'=>$row[0], 'title'=>$row[1])
	);  
}  
   
echo json_encode(array("result"=>$result));  
   
mysqli_close($con);  
   
?>  
