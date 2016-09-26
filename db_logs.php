<!-- 디바이스 화면 크기에 맞춰주는 메타 데이터 -->
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta charset="utf-8">

<html>
<head>
  <title>이력 및 알림</title>
</head>
<script>
function delete_log(number) {
  location.href = 'db_delete_log.php?NUM='+number;
}
function check_all(check) {
  var chk = document.getElementsByName('chk_delete[]');
  for(i=0;i<chk.length; i++) chk[i].checked = check.checked;
}
</script>
<center>
<body bgcolor="F0EEEE">
  <!-- 타이틀과 검색 도구가 위치하는 테이블 -->
  <table>
    <tr>
      <td align="center">
        <form name=search method=get action="<?=$PHP_SELF?>">
          <select name=field>
            <option value='date'>날짜</option>
            <option value='time'>시간</option>
            <option value='state'>상태</option>
          </select>
          <input type="text" size=10 name='search'/><input type="submit" value='검색'/>
        </form>
      </td>
      <td>
        <form action='db_delete_log.php?MAC=<?=$_GET[MAC]?>' method="post">
          <input type="submit" value="Reset"/>
        </form>
      </td>
    </tr>
  </table>
  <!-- 실제 로그들이 위치하는 테이블 -->
  <form name='form_list' action='db_delete_log_number.php?MAC=<?=$_GET[MAC]?>' method='post'>
  <table>
    <tr>
      <td align='center'><input type='checkbox' name='chk_all' onclick='check_all(this);'></td><td colspan=2><input type='submit' value='체크 항목 삭제'></td>
    </tr>
     	<?php
      		$connect = mysql_connect('localhost', 'root', 'dusrntlf8544');
  	    	mysql_select_db('doorlock', $connect);

          // 검색할 때 쿼리문 뒤에 붙여줌
          $query = "SELECT * FROM door_log";
          if($_GET[MAC]!="" || $_GET[search]!="") {
            $query .= " where";
            if($_GET[MAC]!="") $query .= " MAC='$_GET[MAC]'";
            if($_GET[search]!="") $query .= " $_GET[field] like '%".$_GET[search]."%' ";
          }
          $query .= " ORDER BY NUM Desc";

          // 쿼리 실행
          $result = mysql_query($query);

          // 결과로 나오는 레코드 값들을 처리해줄거임
        		while($row = mysql_fetch_array($result)) {
        		  echo "<tr bgcolor='F0DDCC'>";
              echo "<td align='center' width=30><input type='checkbox' name='chk_delete[]' value=$row[NUM]></td>";
              // 도어락에서 촬영한 이미지 출력
              echo "<td width=130><a href='image.php?num=$row[NUM]'><img src='image.php?num=$row[NUM]' width='80' height='80' align='middle'></a>";

              // 열거나 닫은 상태에 따라서 다른 이미지 출력
        		  if($row[STATE]=="open") $image2 = "open.png";
        		  else $image2 = "close.png";
        		  echo "<img src=$image2 width='30' height='30' align='middle' vspace='5' hspace='5'></td>";

              // 발생한 날짜와 시간을 출력
        		  echo "<td width=150 align='center'>$row[DATE]<br>$row[TIME]</td>";
            	echo "</tr>";
            }
  	?>
  </table>
  </form>
</center>
</body>
</html>
