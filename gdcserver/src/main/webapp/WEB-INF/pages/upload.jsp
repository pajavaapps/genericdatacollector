<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Upload APK</title>
</head>
<body>

<form method="POST" action="backend/fileUpload"  enctype="multipart/form-data">
<label for="file">Filename:</label>
<input type="file" name="uploadFile" id="file"><br>
<input type="submit" name="submit" value="Upload APK File">
</form>
</body>
</html>