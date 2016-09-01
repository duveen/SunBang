<?php  
include 'config.php';
$con=mysqli_connect($sn,$un,$pw,$db);

if (mysqli_connect_errno($con)) {  
   echo "Failed to connect to MySQL: " . mysqli_connect_error();  
}

mysqli_set_charset($con,"utf8");  
$res = mysqli_query($con,"select * from popularImage");  
   
$result = array();  
   
while($row = mysqli_fetch_array($res)){  
  array_push($result, array('link'=>$row[0], 'srl'=>$row[1])
	);  
}  
   
echo json_encode(array("result"=>$result));  
   
mysqli_close($con);  
   
?>  
