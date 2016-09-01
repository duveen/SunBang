<?php  
include 'config.php';
$con=mysqli_connect($sn,$un,$pw,$db);

if (mysqli_connect_errno($con)) {  
   echo "Failed to connect to MySQL: " . mysqli_connect_error();  
}

mysqli_set_charset($con,"utf8");  
$res = mysqli_query($con,"select count(*) as co from xe_documents WHERE module_srl = ".$_POST["module_srl"].";");  
   
$row = mysqli_fetch_array($res);

echo $row['co'];
   
mysqli_close($con);  
   
?>  
